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

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.dhbw.t10.view.panels.MainPanel;


/**
 * TODO NicolaiO, add comment!
 * - What should this type do (in one sentence)?
 * - If not intuitive: A simple example how to use this class
 * 
 * @author NicolaiO
 * 
 */
public class Presenter extends JFrame {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	
	private static final long	serialVersionUID	= 6217926957357225677L;
	private static Presenter	instance;
	private JPanel					contentPane;

	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	  * 
	  */
	private Presenter() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("icons/useacc_logo.png"));
		this.setTitle("T10 On-Screen Keyboard");
		this.setVisible(true);
		// this.addComponentListener(this);

		// get a reference to the content pane
		contentPane = (JPanel) getContentPane();
		contentPane.add(MainPanel.getInstance());
		
		// build GUI
		pack();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	public static Presenter getInstance() {
		if (instance == null) {
			instance = new Presenter();
		}
		return instance;
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------

	
}
