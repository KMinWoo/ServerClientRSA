import java.awt.Component;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JTextArea;

public class MultiChatData {

	protected JTextArea msgOut;
	protected JList userStatus;

	public void addObj(Component comp, Component comp2) {
		msgOut = (JTextArea) comp;// ui의 JTextArea와 연결
		userStatus = (JList) comp2;// ui의 JList와 연결
	}

	public void refreshData(String msg) {
		msgOut.append(msg);// 넘어온 메세지를 JTextArea에 입력
	}

	public void refreshUserStatus(Vector<String> users) {
		userStatus.setListData(users);// 넘어온 유저 정보를 JList 에 삽입
	}

	public void refreshUserClear() {
		userStatus.setListData(new Vector());// JList를 초기화 하기 위해서
		// userStatus.removeAll(); 메소드를 사용하려고 하였으나 선언하여도 JList가 초기화 되지 않아서 새로운 벡터를 만들어
		// 넣어 주었습니다
	}
}
