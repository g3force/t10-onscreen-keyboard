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
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.Config;


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
	private static int				port		= 4242;
	private static String			datapath;


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	private SuperFelix(String[] args) {
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
		
		// load datapath
		// works for Windows and Linux... so the data is stored in the systems userdata folder...
		datapath = System.getProperty("user.home") + "/.t10keyboard"; //$NON-NLS-1$ //$NON-NLS-2$
		
		// reading the config file once, if properties not found, use default ones; updates itself
		Config.loadConfig(datapath);

		try {

			port = Integer.parseInt(Config.getConf().getProperty("singletonPort"));
		} catch (NumberFormatException e) {
			System.err.println("Port could not be parsed from config file.");
			System.exit(2);
		}

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

		
		// Locale.setDefault(new Locale("en", "EN"));
		Locale.setDefault(new Locale("de", "DE")); //$NON-NLS-1$ //$NON-NLS-2$

		Controller.getInstance();
		logger.info("Keyboard started."); //$NON-NLS-1$
		
		checkForExternalComm();
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
		new Thread() {
			@Override
			public void run() {
				ServerSocket server = null;
				int response;
				DataInputStream is;
				PrintStream os;
				Socket clientSocket = null;
				try {
					server = new ServerSocket(port);
				} catch (IOException e) {
					System.err.println(e);
				}
				while (true) {
					try {
						clientSocket = server.accept();
						is = new DataInputStream(clientSocket.getInputStream());
						os = new PrintStream(clientSocket.getOutputStream());
						while (true) {
							response = is.readInt();
							logger.info("Received response: " + response);
							os.println(42);
							Controller.getInstance().setWindowVisible(true);
						}
					} catch (EOFException e) {
						logger.info("Connection lost");
					} catch (SocketException e) {
						logger.info("Connection lost");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	
	private static void singleInstance() {
		Socket socket = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		try {
			socket = new Socket("localhost", port);
			os = new DataOutputStream(socket.getOutputStream());
			is = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			logger.error("Don't know about host: localhost");
		} catch (IOException e) {
			// no open socket
			logger.info("I seem to be the first one :) no other instance detected");
			return;
		}
		if (socket != null && os != null && is != null) {
			// socket open. Send some data to notify application
			try {
				os.writeInt(42);
				String responseLine;
				while ((responseLine = is.readLine()) != null) {
					logger.info("Server: " + responseLine);
					if (responseLine.indexOf("42") != -1) {
						// received expected answer.
						logger.info("Instance detected and notified. Exit.");
						System.exit(42);
						break;
					} else {
						// wrong answer... Maybe not connected to keyboard?
						logger.info("Found open socket, but did not receive correct answer");
						return;
					}
				}
				os.close();
				socket.close();
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		} else {
			System.err.println("one of socket, os or is is null");
		}
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	public static String getDatapath() {
		return datapath;
	}
}
