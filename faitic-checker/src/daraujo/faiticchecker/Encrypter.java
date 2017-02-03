/*
 * 	Faicheck - A NON OFFICIAL application to manage the Faitic Platform
 * 	Copyright (C) 2016, 2017 David Ricardo Araújo Piñeiro
 * 	
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package daraujo.faiticchecker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {

	/*public static byte[] createSalt(int length){
		
		Random random=new SecureRandom();
		
		byte[] outputSalt=new byte[length];
		random.nextBytes(outputSalt);
		
		return outputSalt;
		
	}
	
	*/
	
	public static byte[] encodeToSHA256(String password) throws Exception{
		
		MessageDigest digester=MessageDigest.getInstance("SHA-256");
		
		digester.update(password.getBytes("UTF-8"));
		return digester.digest();

	}
	
	public static byte[] encodeToMD5(String password) throws Exception{
		
		MessageDigest digester=MessageDigest.getInstance("MD5");
		
		digester.update(password.getBytes("UTF-8"));
		return digester.digest();

	}
	
	
	//private static final String saltStr="FaiticChecker";	// No more, now the salt string is the password itself
	
	public static String encrpytAES(String toEncrypt, String password) throws Exception{
		
		byte[] salt=encodeToMD5(password);
		
		IvParameterSpec iv = new IvParameterSpec(salt);
		
		// Iterates the password
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec keyspec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKey tempkey = factory.generateSecret(keyspec);
		
		// And that is assigned as the key
		SecretKeySpec key = new SecretKeySpec(tempkey.getEncoded(),"AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        
        byte[] result = cipher.doFinal(toEncrypt.getBytes("UTF-8"));
        
        StringBuffer resultStr=new StringBuffer();
        
        for(byte b : result){
        	resultStr.append(String.format("%02x", b));
        }
        
        return resultStr.toString();
		
	}
	
	public static String decryptAES(String toDecrypt, String password) throws Exception{

		byte[] salt=encodeToMD5(password);
		
		IvParameterSpec iv = new IvParameterSpec(salt);

		// Iterates the password
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec keyspec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKey tempkey = factory.generateSecret(keyspec);
		
		// And that is assigned as the key
		SecretKeySpec key = new SecretKeySpec(tempkey.getEncoded(),"AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        
        byte[] toDecryptByte=new byte[toDecrypt.length()/2];
        
        String hexbecedary="0123456789ABCDEF";
        
        toDecrypt=toDecrypt.toUpperCase();
        
        for(int i=0; i<toDecrypt.length(); i+=2){
        	
        	toDecryptByte[i/2]=(byte)(hexbecedary.indexOf(toDecrypt.charAt(i))*16 + hexbecedary.indexOf(toDecrypt.charAt(i+1)));
        	
        }
        
        byte[] decrypted = cipher.doFinal(toDecryptByte);
        
        return new String(decrypted, "UTF-8");
		
	}
	
	
}
