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
		String version = "";
		
		
		updateBtn = new JButton(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File curDir = new File(System.getProperty("user.dir"));
				File files[] = curDir.listFiles();
				File exec = null;
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith("t10-keyboard-updater") && files[i].getName().endsWith(".exe")) {
						// TODO NicolaiO deal with more than one file
						exec = files[i];
						break;
					}
				}
				if (exec == null) {
					// no updater found, try downloading
					String latestUpdaterVersion = Updater.getLatestVersion("latestupdaterversion");
					String filename = "t10-keyboard-updater-" + latestUpdaterVersion + ".exe";
					String filepath = System.getProperty("user.dir") + "/" + filename;
					try {
						URL url = new URL("http://t10-onscreen-keyboard.googlecode.com/files/" + filename);
						Updater.downloadFile(url, filepath);
						exec = new File(filepath);
					} catch (MalformedURLException err) {
						logger.error("Download URL malformed");
					}
				}
				if (exec.exists()) {
					try {
						Runtime.getRuntime().exec(exec.getAbsolutePath());
						System.exit(0);
					} catch (IOException err) {
						// TODO Auto-generated catch block
						err.printStackTrace();
					}
				} else {
					logger.error("Exec file does not exist");
				}

				// TODO call external updater app
				// JOptionPane.showMessageDialog(me, "Sorry. Update mechanism currently not implemented... Coming soon :)");
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
				String version = Updater.getLatestVersion("latestversion");
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


	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
