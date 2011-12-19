/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: 15.11.2011
 * Author(s): felix
 * 
 * *********************************************************
 */
package edu.dhbw.t10.view.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.dhbw.t10.SuperFelix;
import edu.dhbw.t10.helper.Messages;


/**
 * 
 * Dialog for About menu...
 * 
 * @author felix
 * 
 */
public class AboutDlg extends JDialog {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private JButton				likeBtn;
	private JButton				codeBtn;
	private ImageIcon				icon;
	private static final long	serialVersionUID	= -3739014603528510969L;
	

	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public AboutDlg() {
		this.setTitle(Messages.getString("AboutDlg.0")); //$NON-NLS-1$

		likeBtn = new JButton(Messages.getString("AboutDlg.1")); //$NON-NLS-1$
		likeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				openBrowser("https://plus.google.com/100091571390634776061/posts"); //$NON-NLS-1$
				openBrowser("http://www.facebook.com/UseAcc"); //$NON-NLS-1$
			}
		});
		
		codeBtn = new JButton(Messages.getString("AboutDlg.4")); //$NON-NLS-1$
		codeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				openBrowser("http://code.google.com/p/t10-onscreen-keyboard/"); //$NON-NLS-1$
			}
		});

		JLabel iconLbl = new JLabel();
		icon = new ImageIcon(getClass().getResource("/res/icons/logo_mittel.png")); //$NON-NLS-1$
		if (icon != null) {
			iconLbl = new JLabel(icon);
		}


		JLabel titleLbl = new JLabel("<html>" + Messages.getString("AboutDlg.8") + "<br>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ Messages.getString("AboutDlg.10") + SuperFelix.VERSION + "</html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		titleLbl.setAlignmentX(CENTER_ALIGNMENT);
		titleLbl.setAlignmentY(CENTER_ALIGNMENT);
		JLabel descriptionLbl = new JLabel("<html>" + Messages.getString("AboutDlg.15") + "<br>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ Messages.getString("AboutDlg.17") + "<br>" + Messages.getString("AboutDlg.19") + "</html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JLabel authorLbl = new JLabel("<html>" + "Daniel Andres Lopez, Nicolai Ommer," + "<br>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ "Dirk Klostermann, Sebastian Nickel," + "<br>" + "Felix Pistorius" + "<html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		
		JPanel centerPnl = new JPanel();
		centerPnl.add(iconLbl, BorderLayout.WEST);
		centerPnl.add(descriptionLbl, BorderLayout.EAST);
		
		JPanel southPnl = new JPanel();
		southPnl.add(authorLbl, BorderLayout.WEST);
		southPnl.add(codeBtn, BorderLayout.CENTER);
		southPnl.add(likeBtn, BorderLayout.EAST);

		this.add(titleLbl, BorderLayout.NORTH);
		this.add(centerPnl, BorderLayout.CENTER);
		// this.add(iconLbl, BorderLayout.WEST);
		// this.add(eastPnl, BorderLayout.EAST);
		// this.add(likeBtn, BorderLayout.SOUTH);
		this.add(southPnl, BorderLayout.SOUTH);

		this.pack();
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}


	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	private void openBrowser(String path) {
		final String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		try {
			if (os.indexOf("mac") >= 0) { //$NON-NLS-1$
				Runtime.getRuntime().exec("open " + path); //$NON-NLS-1$
			}

			if (os.indexOf("windows") >= 0) { //$NON-NLS-1$
				if (os.equals("windows nt")) { //$NON-NLS-1$
					Runtime.getRuntime().exec("cmd.exe /C " + path); //$NON-NLS-1$
				} else if (os.equals("windows 95")) { //$NON-NLS-1$
					Runtime.getRuntime().exec("command.com /C " + path); //$NON-NLS-1$
				} else {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path); //$NON-NLS-1$
				}
			}
			
			if (os.indexOf("linux") >= 0) { //$NON-NLS-1$
				// x-www-browser -newwindow -fullscreen "http://www.facebook.com/UseAcc"
				Runtime.getRuntime().exec("x-www-browser -newwindow -fullscreen " + path); //$NON-NLS-1$
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
