/*
 * *********************************************************
 * Copyright (c) 2011 - 2012, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Jan 8, 2012
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;


/**
 * This is a stand-alone class that is compiled separately to offer a stand-alone Updater application
 * for updating the keyboard.
 * The Updater is only intended for Windows Users.
 * 
 * @author NicolaiO
 * 
 */
public class Updater {
	/**
	 * Here is all the magic
	 * 
	 * @param args
	 * @author NicolaiO
	 */
	public static void main(String[] args) {
		String version = getLatestVersion("latestversion");
		if (!version.equals("")) {
			URL url;
			String filename = "t10-keyboard-" + version + ".exe";
			String filepath = System.getProperty("user.dir") + "/" + filename;
			if ((new File(filepath)).exists()) {
				JOptionPane.showMessageDialog(null, "Already up-to-date", "Already up-to-date",
						JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			try {
				url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename);
				downloadFile(url, filepath);
			} catch (MalformedURLException err) {
				error("Download URL malformed");
			}
		} else {
			error("Version could not be read.");
		}
		JOptionPane.showMessageDialog(null, "Update successful.", "Update successful", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
	

	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Download the file given by url and save it to given filepath.
	 * 
	 * @param url
	 * @param filepath
	 * @author NicolaiO
	 */
	public static void downloadFile(URL url, String filepath) {
		try {
			BufferedInputStream in = new BufferedInputStream(url.openStream());
			FileOutputStream fos = new FileOutputStream(filepath);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte data[] = new byte[1024];
			while (in.read(data, 0, 1024) >= 0) {
				bout.write(data);
			}
			bout.close();
			in.close();
		} catch (MalformedURLException err) {
			error("The url is malformed: " + url.toString());
		} catch (IOException err) {
			error("The file " + url.toString() + " could not be found on the server.");
		}

	}


	/**
	 * Return Latest Version by downloading latestversion file from server
	 * 
	 * @return
	 * @author NicolaiO
	 */
	public static String getLatestVersion(String versionfile) {
		String version = "";
		try {
			URL versionUrl = new URL("http://t10-onscreen-keyboard.googlecode.com/git/" + versionfile);
			BufferedInputStream bin = new BufferedInputStream(versionUrl.openStream());
			
			// create a byte array
			byte[] contents = new byte[1024];
			
			int bytesRead = 0;
			String versionFile = "";
			
			while ((bytesRead = bin.read(contents)) != -1) {
				versionFile = new String(contents, 0, bytesRead);
			}
			
			version = versionFile.split("\n", 2)[0];
		} catch (MalformedURLException err) {
			error("URL for latestversion malformed");
		} catch (IOException err) {
			error("The lastestversion file could not be found on the server.");
		}
		return version;
	}
	
	
	/**
	 * Check, if given version is latest according to the latestversion file on server
	 * 
	 * @param version
	 * @return
	 * @author NicolaiO
	 */
	public static boolean isLatest(String version) {
		String latestVersion = getLatestVersion("latestversion");
		if (getMajorPart(latestVersion) > getMajorPart(version)) {
			return false;
		} else if (getMinorPart(latestVersion) > getMinorPart(version)) {
			return false;
		} else if (getRevisionPart(latestVersion) > getRevisionPart(version)) {
			return false;
		}
		return true;
	}
	
	
	private static int getMajorPart(String version) {
		try {
			return Integer.parseInt(version.split("\\.", 2)[0].substring(1));
		} catch (NumberFormatException e) {
			error("Parsing version failed.");
		}
		return 0;
	}
	
	
	private static int getMinorPart(String version) {
		try {
			return Integer.parseInt(version.split("\\.", 2)[1].split("-", 2)[0]);
		} catch (NumberFormatException e) {
			error("Parsing version failed.");
		}
		return 0;
	}
	
	
	private static int getRevisionPart(String version) {
		try {
			return Integer.parseInt(version.split("-", 3)[1]);
		} catch (NumberFormatException e) {
			error("Parsing version failed.");
		}
		return 0;
	}
	
	
	/**
	 * Display a dialog box with the given error message and exit program afterwards
	 * 
	 * @param message
	 * @author NicolaiO
	 */
	private static void error(String message) {
		JOptionPane.showMessageDialog(null, "Error. " + message, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
