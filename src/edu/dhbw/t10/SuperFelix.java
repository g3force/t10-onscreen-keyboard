/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO, DanielAl, DirkK, SebastianN, FelixP
 * 
 * *********************************************************
 */
package edu.dhbw.t10;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.helper.WindowHelper;
import edu.dhbw.t10.manager.Controller;


/**
 * This is the main class of the t10 keyboard. It only initializes the first important classes.
 * 
 * @author NicolaiO, DanielAl, FelixP, DirkK, SebastianN
 * 
 */
public class SuperFelix {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(SuperFelix.class);
	/**
	 * For information:
	 * Revision of Git Repository: Look in file .git/refs/heads/master
	 * automatic: git shortlog | grep -E '^[ ]+\w+' | wc -l
	 */
	public static String				VERSION	= "unknown";									//$NON-NLS-1$


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	private SuperFelix(String[] args) {
		
		singleInstance();

		StringBuilder versionFile = new StringBuilder();
		// String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(getClass().getResourceAsStream("/res/version"), "UTF-8");
		try {
			// while (scanner.hasNextLine()) {
			versionFile.append(scanner.nextLine()); // + NL);
			// }
		} finally {
			scanner.close();
		}
		
		VERSION = versionFile.toString();

		if (args.length >= 1) {
			if (args[0].equals("-v") || args[0].equals("--version")) { //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println(Messages.getString("SuperFelix.6") + VERSION); //$NON-NLS-1$
			} else { //if (args[0].equals("-h") || args[0].equals("--help")) { //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println(Messages.getString("SuperFelix.2")); //$NON-NLS-1$
				System.out.println("-v (--version)"); //$NON-NLS-1$
				System.out.println("-h (--help)"); //$NON-NLS-1$
				System.out.println(Messages.getString("SuperFelix.5")); //$NON-NLS-1$
				System.out.println(Messages.getString("SuperFelix.6") + VERSION); //$NON-NLS-1$
			}
			System.exit(0);
		}

		/*
		 * initialize log4j, a logger from apache.
		 * See http://logging.apache.org/log4j/1.2/manual.html for more details
		 * Log Levels: TRACE, DEBUG, INFO, WARN, ERROR and FATAL
		 * 
		 * configuration is stored in a config file. If it does not exist, use basic config
		 */
		URL logUrl = getClass().getResource("/res/log4j.conf"); //$NON-NLS-1$

		if (logUrl != null) {
			PropertyConfigurator.configure(logUrl);
		} else {
			// basic config with only a console appender
			BasicConfigurator.configure();
			logger.setLevel(Level.ALL);
		}
		
		// Locale.setDefault(new Locale("en", "EN"));
		Locale.setDefault(new Locale("de", "DE")); //$NON-NLS-1$ //$NON-NLS-2$

		Controller.getInstance();
		logger.info("Keyboard started."); //$NON-NLS-1$
		
		checkForExternalComm();

		String activeWindow = "";
		do {
			try {
				String newActiveWindow = WindowHelper.getActiveWindowTitle();
				if (!newActiveWindow.equals(activeWindow)) {
					activeWindow = newActiveWindow;
					logger.info("Active Window changed: " + activeWindow);
				}
				Thread.sleep(500);
			} catch (InterruptedException err) {
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		} while (true);
	}
	

	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Initialize the logger and start the application
	 * 
	 * @param args
	 * @author NicolaiO
	 */
	public static void main(String[] args) {
		new SuperFelix(args);
	}
	

	private static void checkForExternalComm() {
		ServerSocket echoServer = null;
		Byte response;
		DataInputStream is;
		Socket clientSocket = null;
		try {
			echoServer = new ServerSocket(4242);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				clientSocket = echoServer.accept();
				is = new DataInputStream(clientSocket.getInputStream());
				while (true) {
					response = is.readByte();
					logger.info("Received response: " + response);
					Controller.getInstance().setWindowVisible();
				}
			} catch (IOException e) {
				logger.info("Connection lost");
			}
		}
	}
	
	
	private static void singleInstance() {
		Socket socket = null;
		DataOutputStream os = null;
		try {
			socket = new Socket("localhost", 4242);
			os = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: localhost");
		} catch (IOException e) {
			System.out.println("I seem to be the first one :) no other instance detected");
			return;
		}
		if (socket != null && os != null) {
			try {
				os.writeBytes("42 :)");
				os.close();
				socket.close();
				System.out.println("Instance detected and notified. Exit.");
				System.exit(42);
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
