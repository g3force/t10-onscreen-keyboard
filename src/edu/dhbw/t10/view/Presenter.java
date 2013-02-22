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
	private Point						mousePos				= new Point();
	private Point						windowPos			= new Point();
	private Presenter					me						= this;
	

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
		// Window can't be focused, so you can type at your current position with the On-Screen Keyboard
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
		
		contentPane.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
			
			@Override
			public void mouseDragged(MouseEvent e) {
				Point newMousePos = e.getLocationOnScreen();
				Point newWindowPos = new Point(windowPos.x + newMousePos.x - mousePos.x, windowPos.y + newMousePos.y
						- mousePos.y);
				setLocation(newWindowPos);
			}
		});
		contentPane.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			
			@Override
			public void mousePressed(MouseEvent e) {
				mousePos = e.getLocationOnScreen();
				windowPos = getLocationOnScreen();
			}
			
			
			@Override
			public void mouseExited(MouseEvent e) {
				if (Controller.getInstance().isDetectMouseLeavingWA()) {
					Point mouse = e.getLocationOnScreen();
					Point windowlt = contentPane.getLocationOnScreen();
					Point windowrb = new Point(windowlt.x + contentPane.getWidth(), windowlt.y + contentPane.getHeight());
					int offset = 5;
					if (!Controller.getInstance().isActiveWindowWA()
							&& !inSquare(mouse, windowlt.x + offset, windowrb.x - offset, windowlt.y + offset, windowrb.y
									- offset)) {
						logger.debug("mouseExited");
						me.setFocusableWindowState(true);
					}
				}
			}
			
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if (Controller.getInstance().isDetectMouseLeavingWA()) {
					if (!Controller.getInstance().isActiveWindowWA() && eventInFrame(e)) {
						logger.debug("mouseEntered");
						me.setVisible(false);
						me.setFocusableWindowState(false);
						me.setVisible(true);
					}
				}
			}
			
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

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
	
	private boolean inSquare(Point p, int xl, int xr, int yt, int yb) {
		if (p.x < xl)
			return false;
		if (p.x > xr)
			return false;
		if (p.y < yt)
			return false;
		if (p.y > yb)
			return false;
		
		return true;
	}
	
	
	private boolean eventInFrame(MouseEvent e) {
		Point mouse = e.getLocationOnScreen();
		Point windowlt = contentPane.getLocationOnScreen();
		Point windowrb = new Point(windowlt.x + contentPane.getWidth(), windowlt.y + contentPane.getHeight());
		int offset = 20;
		if (inSquare(mouse, windowlt.x, windowlt.x + offset, windowlt.y, windowrb.y))
			return true; // left
		if (inSquare(mouse, windowrb.x - offset, windowrb.x, windowlt.y, windowrb.y))
			return true; // right
		if (inSquare(mouse, windowlt.x, windowrb.x, windowlt.y, windowlt.y + offset))
			return true; // top
		if (inSquare(mouse, windowlt.x, windowrb.x, windowrb.y - offset, windowrb.y))
			return true; // bottom
			
		// System.out.println("mouse=" + mouse);
		// System.out.println("windowlt=" + windowlt);
		// System.out.println("windowrb=" + windowrb);
		return false;
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
}
