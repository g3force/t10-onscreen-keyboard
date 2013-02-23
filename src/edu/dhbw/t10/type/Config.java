/* 
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Dec 5, 2011
 * Author(s): dirk
 *
 * *********************************************************
 */
package edu.dhbw.t10.type;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * contains the conf object, containg all properties which have to be saved
 * ActiveProfile = active profile
 * PROFILE_PATH = pathes, where to search for profiles, divided by :
 * defaultAllowedChars = "A-Za-z\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC", something like this
 * unMark = if unMark is true the method unMark() is used for unmarking suggested chars, otherwise all suggests are
 * deleted and newly printed;
 * also if this value is true marked strings could be overwritten without deleting them first...
 * 
 * @author dirk
 */
public class Config {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger		= Logger.getLogger(Config.class);
	private static Properties		conf;
	private static String			configFile	= "t10keyboard.conf"; //$NON-NLS-1$
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * loads the config file, fills the conf property attribute
	 * @author dirk
	 * @param datapath
	 */
	public static void loadConfig(String datapath) {
		conf = new Properties();
		FileInputStream fis;
		try {
			// reading the config file
			fis = new FileInputStream(datapath + "/" + configFile); //$NON-NLS-1$
			conf.load(fis);
			logger.info("Config file read"); //$NON-NLS-1$
		} catch (IOException err) {
			logger.warn("Could not read the config file"); //$NON-NLS-1$
			// config file not found, set the config values to default
		}
		if (!conf.containsKey("ActiveProfile")) { //$NON-NLS-1$
			logger.debug("ActiveProfile was not in the config file"); //$NON-NLS-1$
			conf.setProperty("ActiveProfile", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!conf.containsKey("PROFILE_PATH")) //$NON-NLS-1$
			conf.setProperty("PROFILE_PATH", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!conf.contains("defaultAllowedChars")) //$NON-NLS-1$
			conf.setProperty("defaultAllowedChars","A-Za-z\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!conf.containsKey("singletonPort")) //$NON-NLS-1$
			conf.setProperty("singletonPort", "1440");
		if (!conf.containsKey("keyRepeatMs")) //$NON-NLS-1$
			conf.setProperty("keyRepeatMs", "100");
		if (!conf.containsKey("keyDelayMs")) //$NON-NLS-1$
			conf.setProperty("keyDelayMs", "300");
		if (!conf.containsKey("debug")) //$NON-NLS-1$
			conf.setProperty("debug", "false");
		if (!conf.containsKey("view.lockmaximize")) //$NON-NLS-1$
			conf.setProperty("view.lockmaximize", "true");
		if (!conf.containsKey("view.lockwindowsize")) //$NON-NLS-1$
			conf.setProperty("view.lockwindowsize", "false");
	}
	
	
	/**
	 * saves the config, should be called at program exit
	 * @param datapath
	 * @author dirk
	 */
	public static void saveConfig(String datapath) {
		try {
			FileOutputStream fos = new FileOutputStream(datapath + "/" + configFile); //$NON-NLS-1$
			conf.store(fos, "Stored by closing the program"); //$NON-NLS-1$
			logger.debug("config file saved to" + datapath + "/" + configFile); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException err) {
			logger.error("Could not store the properties at " + datapath + " / " + configFile); //$NON-NLS-1$ //$NON-NLS-2$
			err.printStackTrace();
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * @return conf
	 */
	public static Properties getConf() {
		return conf;
	}
	
	
	/**
	 * @param conf
	 */
	public static void setConf(final Properties conf) {
		Config.conf = conf;
	}

}
