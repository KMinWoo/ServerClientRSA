import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MultiChatUI extends JFrame {

	private JPanel loginPanel; // 초기의 사용자가 원하는 대화명을 입력 받기 위한 패널
	protected JButton loginButton; // 사용자가 아이디를 입력 후 해당 버튼을 클릭하면 입력한 아이디로 채팅을 할 수 있음
	private JLabel inLabel; // "대화명"이라는 것을 사용자에게 보여주기 위한 라벨
	protected JTextField idInput;// 사용자가 입력한 대화명을 적기 위한 공간

	private JPanel logoutPanel; // 사용자가 로그인 후에는 해당 패널로 변경이 되어 로그아웃을 할 수 있게 만들어 주기 위한 패널
	protected JLabel outLabel;
	protected JButton logoutButton;

	private JPanel msgPanel;// 하단부의 사용죵료 버튼과 사용자가 채팅 할 내용을 적고 엔터를 입력하면 현재 접속이 된 클라이언트 끼리 통신을 할 수 있게 해줌
	protected JButton exitButton;
	protected JTextField msgInput;

	protected Container tab;// 로그인 패널과 로그아웃 패널을 카드레이아웃을 적용 하기 위한 데이터
	protected CardLayout cardLayout;

	protected JTextArea msgOut;

	private JPanel userStatusPanel;// 오른쪽의 현재 접속중인 클라이언트 정보를 보여주기 위한 패널
	private JLabel userStatusLabel;// 하단의 JList의 정보가 어떤 것인지 클라이언트에게 알려주기 위한 라벨
	protected JList userStatus;// 현재 로그인 한 유저의 정보를 보여주기 위한 데이터

	protected String id;

	public MultiChatUI() {
		super("::멀티쳇::");// JFrame의 상단 이름을 수정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 정상적인 종료를 위하여
		setLayout(new BorderLayout());

		loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());

		idInput = new JTextField(15);
		loginButton = new JButton("로그인");

		inLabel = new JLabel("대화명 ");

		loginPanel.add(inLabel, BorderLayout.WEST);
		loginPanel.add(idInput, BorderLayout.CENTER);
		loginPanel.add(loginButton, BorderLayout.EAST);

		logoutPanel = new JPanel();
		logoutPanel.setLayout(new BorderLayout());

		outLabel = new JLabel();
		logoutButton = new JButton("로그아웃");

		logoutPanel.add(outLabel, BorderLayout.CENTER);
		logoutPanel.add(logoutButton, BorderLayout.EAST);

		msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());

		exitButton = new JButton("종료");
		msgInput = new JTextField();

		msgPanel.add(msgInput, BorderLayout.CENTER);
		msgPanel.add(exitButton, BorderLayout.EAST);

		tab = new JPanel();
		cardLayout = new CardLayout();
		tab.setLayout(cardLayout);
		tab.add(loginPanel, "login");
		tab.add(logoutPanel, "logout");

		msgOut = new JTextArea("", 10, 30);
		msgOut.setEditable(false);

		JScrollPane jsp = new JScrollPane(msgOut, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		userStatus = new JList();

		JScrollPane jsp2 = new JScrollPane(userStatus, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		userStatusPanel = new JPanel();
		userStatusPanel.setLayout(new BorderLayout());
		userStatusPanel.setBackground(Color.WHITE);

		userStatusLabel = new JLabel("현재 접속중인 유저");
		userStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		userStatusPanel.add(userStatusLabel, BorderLayout.PAGE_START);
		userStatusPanel.add(jsp2, BorderLayout.PAGE_END);

		getContentPane().add(tab, BorderLayout.PAGE_START);
		getContentPane().add(jsp, BorderLayout.CENTER);
		getContentPane().add(userStatusPanel, BorderLayout.EAST);
		getContentPane().add(msgPanel, BorderLayout.PAGE_END);

		pack();
		setVisible(true);
		// 멀티 채팅 프로그램의 기본 UI 설정 및 레이아웃 설정
	}

	public void addButtonActionListeners(ActionListener listener) {

		loginButton.addActionListener(listener);
		logoutButton.addActionListener(listener);
		exitButton.addActionListener(listener);
		msgInput.addActionListener(listener);// Controller 부분에서 이벤트 핸들링에 대한 내용을 다루기 위해서
	}

}
