import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class MultiChatController implements Runnable {

	private final MultiChatUI v;
	private final MultiChatData chatData;
	private Socket socket;
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;
	private Message m;
	private boolean status;
	private Logger logger;
	private Thread thread;
	private Gson gson;
	private String ip;

	private RSAManager rsa;

	public MultiChatController(MultiChatData data, MultiChatUI ui) {

		v = ui;
		chatData = data;
		logger = Logger.getLogger(this.getClass().getName());
		ip = "127.0.0.1"; // 자기 자신의 서버에 접속하기 위해서 해당 ip를 변경하면 다른 서버에 접속가능
		gson = new Gson();
		rsa = new RSAManager();
	}// MultiChatController()

	public void appMain() {
		chatData.addObj(v.msgOut, v.userStatus);// 현재 ui에 선언되어 있는 JTextArea를 넘겨주어 클라이언트 들이 입력한 메세지를 적을 수 있게 만들어줌
		v.addButtonActionListeners(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Object obj = e.getSource();

				if (obj == v.exitButton) {// 사용종료 버튼을 누르게 되면 종료
					if (outMsg != null)
						outMsg.println(gson.toJson(new Message(v.id, "", "", "SystemExit")));// 서버로 로그아웃을 하지 않고 바로 종료를
																								// 눌렀을 때에 대한 처리
					System.exit(0);

				} else if (obj == v.loginButton) {// 로그인 버튼을 글릭하게 되면
					v.id = v.idInput.getText();// 사용자가 입력한 대화명을 id에 저장해둠
					v.outLabel.setText("대화명 : " + v.id);// 로그인 후에는 사용자가 입력한 대화명을 보여줌
					v.cardLayout.show(v.tab, "logout");// 로그인 후에는 로그아웃 패넝을 사용자에게 보여줌
					ConnectServer();// 서버와 연결
				} else if (obj == v.logoutButton) {
					outMsg.println(gson.toJson(new Message(v.id, "", "", "logout")));// 클라이언트가 로그아웃 버튼을 누르게 되면 대화명과 로그아웃
																						// 했다는 메세지를 서버로 넘겨줌
					v.msgOut.setText("");// 여태까지 나누었던 대화 내용을 삭제1
					v.cardLayout.show(v.tab, "login");// 로그아웃시에는 다시 로그인 할 수 있게 로그인 패널을 보여줌
					chatData.refreshUserClear();// ui의 JList를 최기화
					outMsg.close();
					try {
						inMsg.close();
						socket.close();// 서버와의 연결을 해제
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					status = false;
				} else if (obj == v.msgInput) {// 사용자가 메세지를 입력한 경우
					rsa.newKey();
					Message m = new Message(v.id, "", v.msgInput.getText(), "msg");
					m.setEncodedMsg(rsa.getEncodedString(v.msgInput.getText()));
					m.setPrivateKey(rsa.getPrivateKey());
					outMsg.println(gson.toJson(m));// 서버로 사용자가 입력한 메세지를
													// 넘겨줌
					v.msgInput.setText("");
				}
			}
		});

	}

	public void ConnectServer() {
		try {
			socket = new Socket(ip, 1593);// 1593 포트에 소켓을 연결
			logger.info("[Client]Server 연결 성공");// 연결이 되었으면 콘솔 창에 정보를 출력

			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));// 서버에서 보내오는 내용을 읽기 위한 데이터
			outMsg = new PrintWriter(socket.getOutputStream(), true);// 서버에게 내용을 보내기위한 데이터

			m = new Message(v.id, "", "", "login");// 클라이언트가 입력한 대화명과 로그인 했다는 정보를 메세지에 저장

			outMsg.println(gson.toJson(m));// 저장된 메세지를 서버로 보내줌

			thread = new Thread(this);
			thread.start();
			// 스레드를 생성하고 동작을 수행

		} catch (Exception e) {
			logger.warning("[MultiChatUI]connectServer() Exception 발생!");
			e.printStackTrace();
		}
	}

	public void run() {
		String msg;
		status = true;
		Vector<String> users = new Vector<String>();

		while (status) {
			try {
				msg = inMsg.readLine();// 서버에서 보내오는 메세지를 저장

				if (msg.equals("user")) {// 서버로 부터 현재 로그인 중인 정보가 넘어오는 지에 대한 체크를 하기 위해서
					users = gson.fromJson(inMsg.readLine(), Vector.class);// 넘어온 데이터를 벡터로 변환
					chatData.refreshUserStatus(users);// 넘어온 벡터를 넘겨주어서 JList에 삽입
				} else {// 일반 메세지인 경우
					m = gson.fromJson(msg, Message.class);// 보내온 메세지를 Message class에 맞게 매핑

					chatData.refreshData(m.getId() + ">" + m.getMsg() + "\n");// 변환한 메세지를 chatData에 넘겨주어 클라이언트에게
					// JTextArea에 보여줌

					// 이 부분이 디코딩한 메세지를 출력하는 부분

					chatData.refreshData("Decoded" + m.getId() + ">"
							+ rsa.getDcodedString(m.getEncodedMsg(), m.getPrivateKey()) + "\n");// 변환한 메세지를 chatData에
																								// 넘겨주어
					// 클라이언트에게

					v.msgOut.setCaretPosition(v.msgOut.getDocument().getLength());
					// 대화 내용이 많아져도 스크롤을 내리지 않고 현재의 대화 내용을 바로 볼 수 있게 해줌
				}

			} catch (Exception e) {
			}

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MultiChatController app = new MultiChatController(new MultiChatData(), new MultiChatUI());
		app.appMain();
	}
}
