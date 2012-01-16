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

import edu.dhbw.t10.helper.Messages;


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
		String version = getLatestVersion("latestversion"); //$NON-NLS-1$
		if (!version.equals("")) { //$NON-NLS-1$
			URL url;
			String filename = "t10-keyboard-" + version + ".exe"; //$NON-NLS-1$ //$NON-NLS-2$
			String filepath = System.getProperty("user.dir") + "/" + filename; //$NON-NLS-1$ //$NON-NLS-2$
			if ((new File(filepath)).exists()) {
				JOptionPane.showMessageDialog(null, Messages.getString("Updater.0"), Messages.getString("Updater.0"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			try {
				url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
				downloadFile(url, filepath);
			} catch (MalformedURLException err) {
				error("Download URL malformed"); //$NON-NLS-1$
			}
		} else {
			error("Version could not be read."); //$NON-NLS-1$
		}
		JOptionPane.showMessageDialog(null, Messages.getString("Updater.11"), Messages.getString("Updater.12"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
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
			error(Messages.getString("Updater.13") + url.toString()); //$NON-NLS-1$
		} catch (IOException err) {
			error(Messages.getString("Updater.14") + url.toString() + Messages.getString("Updater.15")); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}


	/**
	 * Return Latest Version by downloading latestversion file from server
	 * 
	 * @return
	 * @author NicolaiO
	 */
	public static String getLatestVersion(String versionfile) {
		String version = ""; //$NON-NLS-1$
		try {
			URL versionUrl = new URL("http://t10-onscreen-keyboard.googlecode.com/git/" + versionfile); //$NON-NLS-1$
			BufferedInputStream bin = new BufferedInputStream(versionUrl.openStream());
			
			// create a byte array
			byte[] contents = new byte[1024];
			
			int bytesRead = 0;
			String versionFile = ""; //$NON-NLS-1$
			
			while ((bytesRead = bin.read(contents)) != -1) {
				versionFile = new String(contents, 0, bytesRead);
			}
			
			version = versionFile.split("\n", 2)[0]; //$NON-NLS-1$
		} catch (MalformedURLException err) {
			error(Messages.getString("Updater.20")); //$NON-NLS-1$
		} catch (IOException err) {
			error(Messages.getString("Updater.21")); //$NON-NLS-1$
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
		String latestVersion = getLatestVersion("latestversion"); //$NON-NLS-1$
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
			return Integer.parseInt(version.split("\\.", 2)[0].substring(1)); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			error(Messages.getString("Updater.1")); //$NON-NLS-1$
		}
		return 0;
	}
	
	
	private static int getMinorPart(String version) {
		try {
			return Integer.parseInt(version.split("\\.", 2)[1].split("-", 2)[0]); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NumberFormatException e) {
			error(Messages.getString("Updater.1")); //$NON-NLS-1$
		}
		return 0;
	}
	
	
	private static int getRevisionPart(String version) {
		try {
			return Integer.parseInt(version.split("-", 3)[1]); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			error(Messages.getString("Updater.1")); //$NON-NLS-1$
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
		JOptionPane.showMessageDialog(null, Messages.getString("Updater.2") + message, Messages.getString("Updater.3"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		System.exit(1);
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
