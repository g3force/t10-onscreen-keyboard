/* 
 * *********************************************************
 * Copyright (c) 2011 - 2012, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: 11.02.2012
 * Author(s): geforce
 *
 * *********************************************************
 */
package edu.dhbw.t10.manager.output;

import java.awt.AWTException;
import java.awt.Robot;

import edu.dhbw.t10.manager.Controller;


/**
 * TODO geforce, add comment!
 * - What should this type do (in one sentence)?
 * - If not intuitive: A simple example how to use this class
 * 
 * @author geforce
 * 
 */
public class T10Robot extends Robot {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * TODO geforce, add comment!
	 * 
	 * @throws AWTException
	 * @author geforce
	 */
	public T10Robot() throws AWTException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public synchronized void keyPress(int keycode) {
		// TODO Auto-generated method stub
		if (Controller.getInstance().isActiveWindowWA())
			Controller.getInstance().hideKeyboard(true);
		super.keyPress(keycode);
	}
	
	
	@Override
	public synchronized void keyRelease(int keycode) {
		// TODO Auto-generated method stub
		super.keyRelease(keycode);
		if (Controller.getInstance().isActiveWindowWA())
			Controller.getInstance().hideKeyboard(false);
	}
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
