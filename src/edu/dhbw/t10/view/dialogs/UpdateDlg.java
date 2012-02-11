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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import edu.dhbw.t10.SuperFelix;
import edu.dhbw.t10.Updater;
import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;


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
	private static final long		serialVersionUID	= -3739014603528510969L;
	protected JButton					updateBtn;
	protected UpdateDlg				me						= this;
	protected JLabel					lblMessage;
	private static final Logger	logger				= Logger.getLogger(UpdateDlg.class);


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public UpdateDlg() {
		this.setTitle(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		this.setLocationByPlatform(true);
		String version = ""; //$NON-NLS-1$
		
		
		updateBtn = new JButton(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File curDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
				File files[] = curDir.listFiles();
				File exec = null;
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith("t10-keyboard-updater") && files[i].getName().endsWith(".exe")) { //$NON-NLS-1$ //$NON-NLS-2$
						// TODO NicolaiO deal with more than one file
						exec = files[i];
						logger.info("updater found:" + exec);
						break;
					}
				}
				if (exec == null) {
					// no updater found, try downloading
					logger.info("No updater found.");
					String latestUpdaterVersion = Updater.getLatestVersion("latestupdaterversion"); //$NON-NLS-1$
					String filename = "t10-keyboard-updater-" + latestUpdaterVersion + ".exe"; //$NON-NLS-1$ //$NON-NLS-2$
					String filepath = System.getProperty("user.dir") + "/" + filename; //$NON-NLS-1$ //$NON-NLS-2$
					try {
						URL url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
						logger.info("Try downloading it from " + url.toString());
						Updater.downloadFile(url, filepath);
						exec = new File(filepath);
					} catch (MalformedURLException err) {
						logger.error("Download URL malformed"); //$NON-NLS-1$
					}
				}
				if (exec.exists()) {
					try {
						logger.info("Starting Updater");
						//Process pb = new ProcessBuilder("javac.exe", "-jar", exec.getAbsolutePath(), "").start(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						Process pb = new ProcessBuilder(exec.getAbsolutePath()).start();
						// Runtime.getRuntime().exec(exec.getAbsolutePath());

						Controller.getInstance().closeSuperFelix();
					} catch (IOException err) {
						err.printStackTrace();
					}
				} else {
					logger.error("Exec file does not exist"); //$NON-NLS-1$
				}

				// TODO call external updater app
				// JOptionPane.showMessageDialog(me, "Sorry. Update mechanism currently not implemented... Coming soon :)");
				// Controller.getInstance().closeSuperFelix();
			}
		});
		

		lblMessage = new JLabel("<html><p>" + Messages.getString("UpdateDlg.16") + SuperFelix.VERSION + "</p><p>" + Messages.getString("UpdateDlg.18") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ version + "</p>"); //$NON-NLS-1$
		
		
		this.setLayout(new BorderLayout());
		this.add(lblMessage, BorderLayout.NORTH);
		this.add(updateBtn, BorderLayout.SOUTH);
		
		
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
		

		new Thread() {
			public void start() {
				String version = Updater.getLatestVersion("latestversion"); //$NON-NLS-1$
				lblMessage = new JLabel("<html><p>" + Messages.getString("UpdateDlg.16") + SuperFelix.VERSION + "</p><p>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+ Messages.getString("UpdateDlg.18") //$NON-NLS-1$
						+ version + "</p>"); //$NON-NLS-1$
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


	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
