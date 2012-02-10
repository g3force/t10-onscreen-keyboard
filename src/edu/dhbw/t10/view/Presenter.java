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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
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
public class Presenter extends JFrame implements WindowStateListener {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger				= Logger.getLogger(Presenter.class);
	private static final long		serialVersionUID	= 6217926957357225677L;
	private JPanel						contentPane;
	private JPanel						glassPane;
	private Point						mousePos				= new Point();
	
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
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setTitle(Messages.getString("Presenter.1")); //$NON-NLS-1$
		this.setAlwaysOnTop(true);
		// Window can't be focussed, so you can type at your current position with the On-Screen Keyboard
		this.setFocusableWindowState(false);
		this.setVisible(true);
		this.addWindowStateListener(this);
		
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
		
		// glassPane = (JPanel) getGlassPane();
		// glassPane.addMouseListener(new MouseListener() {
		contentPane.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				// System.out.println(e.getLocationOnScreen());
			}
			
			
			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("dragged");
				Point newMousePos = e.getLocationOnScreen();
				Point windowPos = getLocationOnScreen();
				Point newWindowPos = new Point(windowPos.x + newMousePos.x - mousePos.x, windowPos.y + newMousePos.y
						- mousePos.y);
				if (newMousePos.x - mousePos.x >= 5 || newMousePos.y - mousePos.y >= 5) {
					// System.out.println(windowPos);
					// System.out.println(newWindowPos);
					setLocation(newWindowPos);
					// System.out.println(newMousePos);
				}
			}
		});
		contentPane.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println("released");
				Point newMousePos = e.getLocationOnScreen();
				Point windowPos = getLocationOnScreen();
				Point newWindowPos = new Point(windowPos.x + newMousePos.x - mousePos.x, windowPos.y + newMousePos.y
						- mousePos.y);
				System.out.println(newWindowPos);
				setLocation(newWindowPos);
				System.out.println(newMousePos);
			}
			
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println("pressed");
				mousePos = e.getLocationOnScreen();
				System.out.println(mousePos);
			}
			
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		// glassPane.setVisible(true);

		// build GUI
		logger.debug("pack() now"); //$NON-NLS-1$
		pack();
		
		logger.debug("Initialized."); //$NON-NLS-1$
	}
	
	
	@Override
	public void windowStateChanged(WindowEvent e) {
		if (Controller.getInstance().isMaximizeWindowLocked() && e.getNewState() == JFrame.MAXIMIZED_BOTH) {
			super.setExtendedState(JFrame.NORMAL);
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
}
