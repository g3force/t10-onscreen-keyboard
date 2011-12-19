/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: 18.12.2011
 * Author(s): DanielAl
 * 
 * *********************************************************
 */
package edu.dhbw.t10.helper;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * This class provides the functionality for Multi-Language support...
 * Use the Eclipse function "Source-->Externalize Strings" to set the strings you want to the lang file...
 * 
 * @author DanielAl
 * 
 */
public class Messages {
	private static final String			BUNDLE_NAME			= "res.lang.t10";								//$NON-NLS-1$
																																	
	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);
	
	
	private Messages() {
	}
	
	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
