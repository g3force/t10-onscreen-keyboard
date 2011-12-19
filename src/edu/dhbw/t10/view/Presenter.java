/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.view;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.view.menus.MenuBar;
import edu.dhbw.t10.view.menus.StatusPane;
import edu.dhbw.t10.view.panels.MainPanel;


/**
 * This is the main Window. This class initializes settings for the window and adds the MainPanel.
 * @author NicolaiO, DanielAl
 * 
 */
public class Presenter extends JFrame implements WindowFocusListener {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger				= Logger.getLogger(Presenter.class);
	private static final long		serialVersionUID	= 6217926957357225677L;
	private JPanel						contentPane;
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Create a new presenter (the main window/JFrame of the app)
	 * Define some attributes for the window and add mainPanel and statusPane to contentPane
	 * 
	 * @param mainPanel
	 * @param statusPane
	 * @author NicolaiO, DanielAl
	 */
	public Presenter(MainPanel mainPanel, StatusPane statusPane) {
		logger.debug("Initializing..."); //$NON-NLS-1$
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setTitle(Messages.getString("Presenter.1")); //$NON-NLS-1$
		this.setAlwaysOnTop(true);
		// Window can't be focussed, so you can type at your current position with the On-Screen Keyboard
		this.setFocusableWindowState(false);
		this.setVisible(true);
		this.addWindowListener(Controller.getInstance());
		this.addWindowFocusListener(this);
		
		// add new MenuBar
		logger.debug("add new MenuBar now"); //$NON-NLS-1$
		this.setJMenuBar(new MenuBar());

		// load icon
		logger.debug("load icon now"); //$NON-NLS-1$
		URL iconUrl = getClass().getResource("/res/icons/useacc_logo.png"); //$NON-NLS-1$
		if (iconUrl != null) {
			this.setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
		}

		// get a reference to the content pane
		logger.debug("add content now"); //$NON-NLS-1$
		contentPane = (JPanel) getContentPane();
		contentPane.add(mainPanel);
		contentPane.add(statusPane, java.awt.BorderLayout.SOUTH);
		
		// build GUI
		logger.debug("pack() now"); //$NON-NLS-1$
		pack();
		
		logger.debug("Initialized."); //$NON-NLS-1$
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------

	
	// TODO NicolaiO or any other... detect a change in focus...
	@Override
	public void windowGainedFocus(WindowEvent e) {
		logger.error("1"); //$NON-NLS-1$
	}
	
	
	@Override
	public void windowLostFocus(WindowEvent e) {
		logger.error("2"); //$NON-NLS-1$
	}
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
}
