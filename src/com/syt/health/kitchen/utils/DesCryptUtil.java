package com.syt.health.kitchen.utils;

import java.security.Key;
import java.util.Calendar;

import javax.crypto.Cipher;

/**
 * DES 加密，解密工具。
 * 根据密钥对字符串进行加解密。
 * 
 * @author tom
 *
 */
public class DesCryptUtil {

	private static class DesCryptUtilHolder {
		static DesCryptUtil instance = new DesCryptUtil();
	}

	private DesCryptUtil() { 
		try {
			this.setKey(strDefaultKey);
		} catch (Exception ignored) {
		}
	}

	public static DesCryptUtil getInstance() {
		return DesCryptUtilHolder.instance;
	}

	/** 密钥 */
	private static Key key = null;

	/** 字符串默认键值 */
	private static String strDefaultKey = "key999";

	/** 加密工具 */
	private Cipher encryptCipher = null;

	/** 解密工具 */
	private Cipher decryptCipher = null;

	private String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	private byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 加密字节数组
	 * 
	 * @param arrB
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn
	 *            需加密的字符串
	 * @return 加密后的字符串
	 */
	public String encrypt(String strIn) {
		try {
			return byteArr2HexStr(encrypt(strIn.getBytes()));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn
	 *            需解密的字符串
	 * @return 解密后的字符串
	 */
	public String decrypt(String strIn) {
		try {
			return new String(decrypt(hexStr2ByteArr(strIn)));
		} catch (Exception e) {
			return "";
		}
	}

	public void setKey(String strKey) throws Exception {
		byte[] arrBTmp = strKey.getBytes();
		
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];

		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}

		// 生成密钥
		key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
		
		// 初始化
		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}

	public static void main(String[] args) {
		try {
			String orgStr = "{\"head\":{\"ret\":0,\"msg\":\"查询成功\"}}";
			String encryptStr = "";
			String decryptStr = "";
			
			long startMills = Calendar.getInstance().getTimeInMillis();
			for(int i=0;i<1000;++i){
				encryptStr = DesCryptUtil.getInstance().encrypt(orgStr);
				decryptStr = DesCryptUtil.getInstance().decrypt(encryptStr);
			}
			
			System.out.println("耗时："+ (Calendar.getInstance().getTimeInMillis() - startMills));
			System.out.println("加密前的字符：" + orgStr);
			System.out.println("加密前的length:"+orgStr.length());
			System.out.println("加密后的字符：" + encryptStr);
			System.out.println("加密后的length:"+encryptStr.length());
			System.out.println("解密后的字符：" + decryptStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
