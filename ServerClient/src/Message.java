
public class Message {

	private String id;// 클라이언트의 대화명
	private String password;// 현재의 채팅 프로그램에서는 따로 사용하지않음
	private String msg;// 클라이언트가 입력한 메세지 내용
	private String type;// 로그인 ,로그아웃 동의 상태를 알려주기 위한 데이터

	private String encodedMsg;
	private String privateKey;

	public Message() {
	}

	public Message(String id, String password, String msg, String type) {
		this.id = id;
		this.password = password;
		this.msg = msg;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}

	public String getPassword() {
		return password;
	}

	public String getEncodedMsg() {
		return encodedMsg;
	}

	public void setEncodedMsg(String encodedMsg) {
		this.encodedMsg = encodedMsg;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
