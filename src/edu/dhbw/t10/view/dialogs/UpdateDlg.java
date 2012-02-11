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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import edu.dhbw.t10.SuperFelix;
import edu.dhbw.t10.Updater;
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
	private static final long		serialVersionUID	= -3739014603528510969L;
	// protected JButton updateBtn;
	protected UpdateDlg				me						= this;
	protected JLabel					lblMessage			= new JLabel("");
	protected JLabel					lblLink				= new JLabel("");
	private static final Logger	logger				= Logger.getLogger(UpdateDlg.class);


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public UpdateDlg() {
		this.setTitle(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		this.setLocationByPlatform(true);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		
		//		updateBtn = new JButton(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		// updateBtn.addActionListener(new ActionListener() {
		//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				File curDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
//				File files[] = curDir.listFiles();
//				File exec = null;
//				for (int i = 0; i < files.length; i++) {
//					if (files[i].getName().startsWith("t10-keyboard-updater") && files[i].getName().endsWith(".exe")) { //$NON-NLS-1$ //$NON-NLS-2$
//						// TODO NicolaiO deal with more than one file
//						exec = files[i];
//						break;
//					}
//				}
//				if (exec == null) {
//					// no updater found, try downloading
//					String latestUpdaterVersion = Updater.getLatestVersion("latestupdaterversion"); //$NON-NLS-1$
//					String filename = "t10-keyboard-updater-" + latestUpdaterVersion + ".exe"; //$NON-NLS-1$ //$NON-NLS-2$
//					String filepath = System.getProperty("user.dir") + "/" + filename; //$NON-NLS-1$ //$NON-NLS-2$
//					try {
//						URL url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
//						Updater.downloadFile(url, filepath);
//						exec = new File(filepath);
//					} catch (MalformedURLException err) {
//						logger.error("Download URL malformed"); //$NON-NLS-1$
//					}
//				}
//				if (exec.exists()) {
//					try {
//						new ProcessBuilder("javac.exe", "-jar", exec.getAbsolutePath(), "").start(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//						// Runtime.getRuntime().exec(exec.getAbsolutePath());
//						System.exit(0);
//					} catch (IOException err) {
//						// TODO Auto-generated catch block
//						err.printStackTrace();
//					}
//				} else {
//					logger.error("Exec file does not exist"); //$NON-NLS-1$
//				}
//
//				// TODO call external updater app
//				// JOptionPane.showMessageDialog(me, "Sorry. Update mechanism currently not implemented... Coming soon :)");
//				// Controller.getInstance().closeSuperFelix();
//			}
//		});
		

		String version = ""; //$NON-NLS-1$
		lblMessage = new JLabel("<html><p>" + Messages.getString("UpdateDlg.16") + SuperFelix.VERSION + "</p><p>" + Messages.getString("UpdateDlg.18") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ version + "</p>"); //$NON-NLS-1$
		
		this.setLayout(new BorderLayout());
		this.add(lblMessage, BorderLayout.NORTH);
		// this.add(updateBtn, BorderLayout.SOUTH);
		this.add(lblLink, BorderLayout.SOUTH);
		
		this.pack();
		

		new Thread() {
			public void start() {
				URL url;
				String version = Updater.getLatestVersion("latestversion"); //$NON-NLS-1$
				lblMessage = new JLabel("<html><p>" + Messages.getString("UpdateDlg.16") + SuperFelix.VERSION + "</p><p>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+ Messages.getString("UpdateDlg.18") //$NON-NLS-1$
						+ version + "</p>"); //$NON-NLS-1$
				me.remove(lblMessage);
				me.add(lblMessage, BorderLayout.NORTH);
				
				if (!Updater.isLatest(SuperFelix.VERSION)) { //$NON-NLS-1$
					String filename = "t10-keyboard-" + version + ".exe"; //$NON-NLS-1$ //$NON-NLS-2$

					try {
						url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
						lblLink = new JLabel("<html>A new version is available: <a href=\"" + url.toString() + "\" /></html>");
					} catch (MalformedURLException err) {
						err.printStackTrace();
					}
				} else {
					logger.error("Could not find version.");
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
