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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.dhbw.t10.SuperFelix;
import edu.dhbw.t10.helper.Messages;


/**
 * 
 * Dialog for Update menu...
 * 
 * @author NicolaiO
 * 
 */
public class UpdateDlg extends JDialog {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final long	serialVersionUID	= -3739014603528510969L;
	protected JButton				updateBtn;
	protected UpdateDlg			me						= this;
	protected JLabel				lblMessage;


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public UpdateDlg() {
		this.setTitle(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		String version = "";
		
		
		updateBtn = new JButton(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO call external updater app
				JOptionPane.showMessageDialog(me, "Sorry. Update mechanism currently not implemented... Coming soon :)");
				// Controller.getInstance().closeSuperFelix();
			}
		});
		

		lblMessage = new JLabel("<html><p>Current Version: " + SuperFelix.VERSION + "</p><p>" + "Latest Version: "
				+ version + "</p>");
		
		
		this.setLayout(new BorderLayout());
		this.add(lblMessage, BorderLayout.NORTH);
		this.add(updateBtn, BorderLayout.SOUTH);
		
		
		this.pack();
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		

		new Thread() {
			public void start() {
				String version = getVersion();
				lblMessage = new JLabel("<html><p>Current Version: " + SuperFelix.VERSION + "</p><p>" + "Latest Version: "
						+ version + "</p>");
				me.remove(lblMessage);
				me.add(lblMessage, BorderLayout.NORTH);
				
				if (version.equals(SuperFelix.VERSION)) {
					updateBtn.setEnabled(false);
				}
				me.pack();
			}
		}.start();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	private String getVersion() {
		String version = "";
		try {
			URL versionUrl = new URL("http://t10-onscreen-keyboard.googlecode.com/git/latestversion");
			BufferedInputStream bin = new BufferedInputStream(versionUrl.openStream());
			
			// create a byte array
			byte[] contents = new byte[1024];
			
			int bytesRead = 0;
			String versionFile = "";
			
			while ((bytesRead = bin.read(contents)) != -1) {
				versionFile = new String(contents, 0, bytesRead);
			}
			
			version = versionFile.split("\n", 2)[0];
		} catch (MalformedURLException err) {
			err.printStackTrace();
			// TODO catch
		} catch (IOException err) {
			err.printStackTrace();
			// TODO catch
		}
		return version;
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
