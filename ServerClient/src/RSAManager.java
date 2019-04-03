import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSAManager {
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public Cipher cipher;

	public SecureRandom secureRandom;
	public KeyFactory keyFactory;
	public KeyPairGenerator keyPairGenerator;
	public KeyPair keyPair;

	public byte[] bPublicKey;// public key는 암호화에만 필요하므로 따로 보내지 않음
	public byte[] bPrivateKey;

	private String sPrivateKey;

	public RSAManager() {
		publicKey = null;
		privateKey = null;
		secureRandom = new SecureRandom();

		try {
			keyFactory = KeyFactory.getInstance("RSA");
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void newKey() {
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512, secureRandom);

			keyPair = keyPairGenerator.genKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} // key 생성

		bPublicKey = publicKey.getEncoded();

		bPrivateKey = privateKey.getEncoded();
		sPrivateKey = Base64.encodeBase64String(bPrivateKey);
	}

	public String getPrivateKey() {
		return sPrivateKey;
	}

	public String getEncodedString(String msg) {
		String endcodedMsg = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] bCipher1 = cipher.doFinal(msg.getBytes());
			endcodedMsg = Base64.encodeBase64String(bCipher1);
			// 이부분은 인코딩입니당...
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		} finally {
			return endcodedMsg;
		}
	}

	public String getDcodedString(String msg, String key) {
		String decodedMsg = null;

		try {
			byte[] bKey = Base64.decodeBase64(key.getBytes());
			PrivateKey pKey = null;

			PKCS8EncodedKeySpec pKeySpec = new PKCS8EncodedKeySpec(bKey);
			pKey = keyFactory.generatePrivate(pKeySpec);

			byte[] bCipher = Base64.decodeBase64(msg.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, pKey);
			byte[] bPlain2 = cipher.doFinal(bCipher);
			decodedMsg = new String(bPlain2);
			// 이부분은 인코딩입니당...
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} finally {
			return decodedMsg;
		}
	}

}
