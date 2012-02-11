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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
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
	private JButton					btnDownload			= new JButton();
	private JButton					btnVisit				= new JButton();
	private static final Logger	logger				= Logger.getLogger(UpdateDlg.class);
	private URL							url;


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public UpdateDlg() {
		this.setTitle(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		this.setLocationByPlatform(true);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setLayout(new BorderLayout());
		
		
		//		updateBtn = new JButton(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		// updateBtn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		//				File curDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
		// File files[] = curDir.listFiles();
		// File exec = null;
		// for (int i = 0; i < files.length; i++) {
		//					if (files[i].getName().startsWith("t10-keyboard-updater") && files[i].getName().endsWith(".exe")) { //$NON-NLS-1$ //$NON-NLS-2$
		// // TODO NicolaiO deal with more than one file
		// exec = files[i];
		// break;
		// }
		// }
		// if (exec == null) {
		// // no updater found, try downloading
		//					String latestUpdaterVersion = Updater.getLatestVersion("latestupdaterversion"); //$NON-NLS-1$
		//					String filename = "t10-keyboard-updater-" + latestUpdaterVersion + ".exe"; //$NON-NLS-1$ //$NON-NLS-2$
		//					String filepath = System.getProperty("user.dir") + "/" + filename; //$NON-NLS-1$ //$NON-NLS-2$
		// try {
		//						URL url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
		// Updater.downloadFile(url, filepath);
		// exec = new File(filepath);
		// } catch (MalformedURLException err) {
		//						logger.error("Download URL malformed"); //$NON-NLS-1$
		// }
		// }
		// if (exec.exists()) {
		// try {
		//						new ProcessBuilder("javac.exe", "-jar", exec.getAbsolutePath(), "").start(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// // Runtime.getRuntime().exec(exec.getAbsolutePath());
		// System.exit(0);
		// } catch (IOException err) {
		// // TODO Auto-generated catch block
		// err.printStackTrace();
		// }
		// } else {
		//					logger.error("Exec file does not exist"); //$NON-NLS-1$
		// }
		//
		// // TODO call external updater app
		// // JOptionPane.showMessageDialog(me, "Sorry. Update mechanism currently not implemented... Coming soon :)");
		// // Controller.getInstance().closeSuperFelix();
		// }
		// });
		
		// new Thread() {
		// public void start() {
		String version = Updater.getLatestVersion("latestversion");
		String filename = "t10-keyboard-" + version + ".exe";
		logger.info("Latest version: " + version);

		lblMessage = new JLabel("<html><p>" + Messages.getString("UpdateDlg.16") + SuperFelix.VERSION + "</p><p>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ Messages.getString("UpdateDlg.18") //$NON-NLS-1$
				+ version + "</p>"); //$NON-NLS-1$
		
		try {
			url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename); //$NON-NLS-1$
			logger.info("url: " + url.toString());
			
			btnDownload = new JButton(Messages.getString("UpdateDlg.dlNewVersion"));
			btnDownload.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					AboutDlg.openBrowser(url.toString());
				}
			});
			btnVisit = new JButton(Messages.getString("UpdateDlg.visitSite"));
			btnVisit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					AboutDlg.openBrowser("http://code.google.com/p/t10-onscreen-keyboard/downloads/list"); //$NON-NLS-1$
				}
			});
		} catch (MalformedURLException err) {
			err.printStackTrace();
		}
		if (Updater.isLatest(SuperFelix.VERSION)) {
			btnDownload.setEnabled(false);
			logger.info("Latest Version installed");
		}
		me.add(lblMessage, BorderLayout.NORTH);
		me.add(btnDownload, BorderLayout.CENTER);
		me.add(btnVisit, BorderLayout.SOUTH);
		me.pack();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------


	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
