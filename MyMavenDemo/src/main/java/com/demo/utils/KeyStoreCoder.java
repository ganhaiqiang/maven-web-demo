package com.demo.utils;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class KeyStoreCoder {
	/**
	 * Java密钥库(Java Key Store，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";

	public static final String X509 = "X.509";

	public static void main(String[] args) throws Exception {
		String path = "D:/ghq.keystore";
		String priKey = getStrPrivateKey(path, "ghq", "123456", "gan420325");
		String pubKey = getStrPublicKey(path, "ghq", "123456");
		System.out.println(priKey);
		System.out.println(pubKey);
		String enString = encryptByPublicKey(pubKey, "广东省深圳市");
		System.out.println(enString);
		String deString = descryptByPrivateKey(priKey, enString);
		System.out.println(deString);
	}

	/**
	 * 获得KeyStore
	 * 
	 * @author 奔跑的蜗牛
	 * @version 2012-3-16
	 * @param keyStorePath
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static KeyStore getKeyStore(String keyStorePath, String password) throws Exception {
		FileInputStream is = new FileInputStream(keyStorePath);
		KeyStore ks = KeyStore.getInstance(KEY_STORE);
		ks.load(is, password.toCharArray());
		is.close();
		return ks;
	}

	/**
	 * 由KeyStore获得私钥
	 * 
	 * @author 奔跑的蜗牛
	 * @param keyStorePath
	 * @param alias
	 * @param storePass
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey(String keyStorePath, String alias, String storePass, String keyPass) throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, storePass);
		return (PrivateKey) ks.getKey(alias, keyPass.toCharArray());
	}

	/**
	 * 由Certificate获得公钥
	 * 
	 * @author 奔跑的蜗牛
	 * @param keyStorePath
	 *            KeyStore路径
	 * @param alias
	 *            别名
	 * @param storePass
	 *            KeyStore访问密码
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getPublicKey(String keyStorePath, String alias, String storePass) throws Exception {
		KeyStore ks = getKeyStore(keyStorePath, storePass);
		return ks.getCertificate(alias).getPublicKey();
	}

	/**
	 * 从KeyStore中获取公钥，并经BASE64编码
	 * 
	 * @author 奔跑的蜗牛
	 * @param keyStorePath
	 * @param alias
	 * @param storePass
	 * @return
	 * @throws Exception
	 */
	public static String getStrPublicKey(String keyStorePath, String alias, String storePass) throws Exception {
		PublicKey key = getPublicKey(keyStorePath, alias, storePass);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 获取经BASE64编码后的私钥
	 * 
	 * @author 奔跑的蜗牛
	 * @param keyStorePath
	 * @param alias
	 * @param storePass
	 * @param keyPass
	 * @return
	 * @throws Exception
	 */
	public static String getStrPrivateKey(String keyStorePath, String alias, String storePass, String keyPass) throws Exception {
		PrivateKey key = getPrivateKey(keyStorePath, alias, storePass, keyPass);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 使用公钥加密数据
	 * 
	 * @author 奔跑的蜗牛
	 * @param publicKey
	 * @param srcData
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String publicKey, String srcData) throws Exception {
		// 解密
		byte[] pk = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(pk);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		// 获取公钥
		PublicKey pubKey = kf.generatePublic(spec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);

		byte[] doFinal = cipher.doFinal(srcData.getBytes());
		return Base64.encodeBase64String(doFinal);
	}

	/**
	 * 使用私钥解密数据
	 * 
	 * @author 奔跑的蜗牛
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String descryptByPrivateKey(String privateKey, String data) throws Exception {
		// BASE64转码解密私钥
		byte[] pk = Base64.decodeBase64(privateKey);
		// BASE64转码解密密文
		byte[] text = Base64.decodeBase64(data);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pk);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		// 获取私钥
		PrivateKey prvKey = kf.generatePrivate(spec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, prvKey);

		byte[] doFinal = cipher.doFinal(text);
		return new String(doFinal);
	}

}
