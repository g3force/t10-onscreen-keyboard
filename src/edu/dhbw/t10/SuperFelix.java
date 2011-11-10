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

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.dhbw.t10.manager.Controller;


/**
 * This is the main class of the t10 keyboard. It only initializes the first important classes.
 * 
 * TODO OPTIONAL ALL graphical buttons (keys) e.g. pause- and arrow keys
 * TODO FelixP menus
 * TODO OPTIONAL ALL StatusBar
 * 
 * @author NicolaiO
 * 
 */
public class SuperFelix {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(SuperFelix.class);
	

	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * initialize log4j, a logger from apache.
		 * See http://logging.apache.org/log4j/1.2/manual.html for more details
		 * Log Levels: TRACE, DEBUG, INFO, WARN, ERROR and FATAL
		 * 
		 * configuration is stored in a config file. If it does not exist, use basic config
		 */
		File logConfFile = new File("data/log4j.conf");
		if (logConfFile.exists()) {
			PropertyConfigurator.configure(logConfFile.getPath());
		} else {
			// basic config with only a console appender
			BasicConfigurator.configure();
			logger.setLevel(Level.ALL);
		}
		
		Controller.getInstance();
		logger.info("Keyboard started.");
	}
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
