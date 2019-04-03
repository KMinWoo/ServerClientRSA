import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class MultiChatServer {

	private ServerSocket serverSocket = null;
	private Socket socket = null;

	private ArrayList<ChatTread> chatTreads = new ArrayList<ChatTread>();

	private Logger logger;

	private Vector<String> users;// 현재 접속중인 클라이언트 정보를 저장하기 위한 벡터
	private boolean flag = false;// 클라이언트가 로그인 or 로그아웃 or 사용종료인 경우에는 현재 접속중인 정보를 모든 클라이언트에게 넘겨주기 위한 플래그

	public void start() {
		logger = Logger.getLogger(this.getClass().getName());
		users = new Vector<String>();
		try {
			serverSocket = new ServerSocket(1593);
			logger.info("MultiChatServer start");

			while (true) {
				socket = serverSocket.accept();// 클라이언트의 접속이 있을 때까지 대기

				ChatTread chat = new ChatTread();
				chatTreads.add(chat);// 클라이언트가 서버에 접속하면 스레드를 생성하여 arrayList에 저장

				chat.start();
			}
		} catch (Exception e) {
			logger.info("[MultiChatServer]start() Exception 발샹!");
			e.printStackTrace();
		}

	}

	public class ChatTread extends Thread {

		private String msg;
		private Message m;
		private Gson gson;
		private boolean status;
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		public void msgSendAll(String msg) {
			for (ChatTread ct : chatTreads) {
				ct.outMsg.println(msg);// 현재 접속하고 있는 모든 클라이언트들에게 메세지를 전송
				if (flag) {
					ct.outMsg.println("user");// 벡터 정보를 넘겨주기 전에 유저 정보가 넘어 간다고 클라이언트에게 알려줌
					ct.outMsg.println(gson.toJson(users));// 현재 접속 중인 클라이언트 정보를 넘겨줌
				}
			}
		}

		public void run() {
			try {
				m = new Message();
				gson = new Gson();
				inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				outMsg = new PrintWriter(socket.getOutputStream(), true);
				status = true;

				while (status) {
					msg = inMsg.readLine();// 클라이언트가 보낸 메세지를 저장

					m = gson.fromJson(msg, Message.class);// 넘오온 메세지를 Message Class의 형태에 맞게 매핑

					if (m.getType().equals("logout")) {// 메세지 타입이 로그아웃인 경우에는 클라이언트들에게 로그아웃한 클라이언트의 대화명이 로그아웃 했다고 알려줌
						flag = true;
						chatTreads.remove(this);// 로그아웃한 클라이언트를 삭제
						users.remove(m.getId());// 로그아웃인 경우 저장되어있던 대화명을 삭제
						msgSendAll(gson.toJson(new Message(m.getId(), "", "님이 종료했습니다", "server")));
						status = false;

					} else if (m.getType().equals("login")) {// 메세지 타입이 로그인인 경우에는 현재 접속 중인 클라리언트에게 로그인 했다고 알려줌
						flag = true;
						users.add(m.getId());// 클라이언트가 호그인을 하면 해당 클라이언트의 대화명을 저장
						msgSendAll(gson.toJson(new Message(m.getId(), "", "님이 로그인 했습니다", "server")));
					} else if (m.getType().equals("SystemExit")) {// 클라이언트가 로그인 중에 사용종료를 누른 경우
						flag = true;
						chatTreads.remove(this);// 사용종료한 클라이언트를 삭제
						users.remove(m.getId());// 사용종료인 경우 저장되어있던 대화명을 삭제
						msgSendAll(gson.toJson(new Message(m.getId(), "", "님이 종료했습니다", "server")));
						status = false;
					} else {
						flag = false;// 일반 메세지인 경우에는 사용자 정보를 다시 넘겨줄 필요가 없어서 false로 변경
						msgSendAll(msg);// 그냥 일반 메세지인 경우에 접속된 클라이언트에게 메세지를 넘겨줌
					}
				}
				this.interrupt();// 대기 상태로
				logger.info(this.getName() + " 종료됨!!");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MultiChatServer a = new MultiChatServer();
		a.start();
	}

}
