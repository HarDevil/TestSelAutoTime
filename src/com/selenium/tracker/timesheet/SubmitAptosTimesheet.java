package com.selenium.tracker.timesheet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SubmitAptosTimesheet {

	private static String error_Message = "Something went Wrong.. Please contact at Harshil.Chokshi@pmcretail.com";

	public static void main(String... args) throws InterruptedException, IOException {

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter tracker username...");
		String username = sc.nextLine();
		System.out.println("Enter your password here...");
		String password = sc.nextLine();
//		Runtime.getRuntime().exec("cls");
		generateCredFile(username, encryptPassword(password));
	}

	/**
	 * @param password
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	private final static String encryptPassword(String password)  {
		try {
			return bytesToHex(getCipher(Cipher.ENCRYPT_MODE).doFinal(password.getBytes()));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			System.out.println(error_Message);
		}
	}

	/**
	 * @param password
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	private final static String decryptPassword(String password) throws IllegalBlockSizeException, BadPaddingException {
		return bytesToHex(getCipher(Cipher.DECRYPT_MODE).doFinal(password.getBytes()));
	}

	private static Cipher getCipher(int mode) {
		Key aesKey = new SecretKeySpec(getPhysicalIp().getBytes(), "AES");
		Cipher cipher = null;
		String encryption = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(mode, aesKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			System.out.println(error_Message);
		}
		return cipher;
	}

	/**
	 * @param username
	 * @param password
	 */
	private final static void generateCredFile(String username, String password) {
		String credFile = System.getProperty("user.dir") + "\\source\\cred.properties";
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			fileWriter = new FileWriter(credFile);
			bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write("username=" + username);
			bufferWriter.newLine();
			bufferWriter.write("password=" + password);
		} catch (IOException e) {
			System.out.println(error_Message);
		} finally {
			try {
				if (fileWriter != null && bufferWriter != null) {
					bufferWriter.close();
					fileWriter.close();
				}
			} catch (IOException e) {
				System.out.println(error_Message);
			}
		}

	}

	/**
	 * @return
	 */
	private final static String getPhysicalIp() {
		String salt = null;
		try {
			NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			salt = bytesToHex(network.getHardwareAddress());
		} catch (SocketException | UnknownHostException e) {
			System.out.println(error_Message);
		}
		return salt;
	}

	/*
	 * @param hash
	 * 
	 * @return
	 */
	private final static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * @param userName
	 * @param password
	 */
	private final static void submitTimesheet(String userName, String password) {
		System.setProperty("webdriver.chrome.driver", "driver\\chromedriver.exe");

		WebDriver driver = new ChromeDriver(); // create object of ChromeDriver
		driver.manage().window().maximize(); // maximize the browser window
		driver.get("https://tracker.pmcretail.com/login.jsp"); // enter url
		driver.findElement(By.id("j_username")).sendKeys(userName);
		driver.findElement(By.id("j_password")).sendKeys(password);
		driver.findElement(By.name("login")).click();
		driver.findElement(By.xpath("//a[@href='timeSheet.html']")).click();

		for (int i = 1; i <= 5; i++) {
			WebElement day = driver.findElement(By.id("annuityTasks[1].day" + i));
			day.clear();
			day.sendKeys("8.0");
		}
	}
	/**
	 * String text = "Hello World"; String key = "Bar12345Bar12345"; // 128 bit
	 * key // Create key and cipher Key aesKey = new
	 * SecretKeySpec(key.getBytes(), "AES"); Cipher cipher =
	 * Cipher.getInstance("AES"); // encrypt the text
	 * cipher.init(Cipher.ENCRYPT_MODE, aesKey); byte[] encrypted =
	 * cipher.doFinal(text.getBytes()); System.err.println(new
	 * String(encrypted)); // decrypt the text cipher.init(Cipher.DECRYPT_MODE,
	 * aesKey); String decrypted = new String(cipher.doFinal(encrypted));
	 * System.err.println(decrypted);
	 */
}
