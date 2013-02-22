/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 21, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.view.menus;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.Config;
import edu.dhbw.t10.view.dialogs.AboutDlg;
import edu.dhbw.t10.view.dialogs.DialogContainer;
import edu.dhbw.t10.view.dialogs.InputDlg;
import edu.dhbw.t10.view.dialogs.ProfileCleanerDlg;
import edu.dhbw.t10.view.dialogs.UpdateDlg;


/**
 * As class name says: This class represents the MenuBar and all the included menus
 * The ActionListeners are also directly implemented here...
 * 
 * T O D O FelixP optional menu item "Modify"
 * 
 * @author NicolaiO
 */
public class MenuBar extends JMenuBar {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final long			serialVersionUID	= -2903181098465204289L;
	private static final Logger		logger				= Logger.getLogger(Controller.class);

	protected static final Object[]	eventCache			= null;
	private JCheckBoxMenuItem			iLockSize;
	private JCheckBoxMenuItem			iLockMaximize;
	private JCheckBoxMenuItem			iActiveWindowWA;
	private JCheckBoxMenuItem			iDetectMouseLeavingWA;
	

	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Create a MenuBar with all its items and define action events
	 * 
	 * @author NicolaiO
	 */
	public MenuBar() {
		// File Menu
		JMenu mFile = new JMenu(Messages.getString("MenuBar.0")); //$NON-NLS-1$
		JMenuItem iNewProfile = new JMenuItem(Messages.getString("MenuBar.1")); //$NON-NLS-1$
		JMenuItem iImport = new JMenuItem(Messages.getString("MenuBar.2")); //$NON-NLS-1$
		JMenuItem iClose = new JMenuItem(Messages.getString("MenuBar.3")); //$NON-NLS-1$
		JMenuItem iExit = new JMenuItem(Messages.getString("MenuBar.Exit")); //$NON-NLS-1$
		
		// ProfileMenu
		JMenu mProfile = new JMenu(Messages.getString("MenuBar.4")); //$NON-NLS-1$
		// JMenuItem iChange = new JMenuItem("Modify");
		JMenuItem iExport = new JMenuItem(Messages.getString("MenuBar.5")); //$NON-NLS-1$
		JMenuItem iT2D = new JMenuItem(Messages.getString("MenuBar.6")); //$NON-NLS-1$
		JMenuItem iF2D = new JMenuItem(Messages.getString("MenuBar.7")); //$NON-NLS-1$
		JMenuItem iD2F = new JMenuItem(Messages.getString("MenuBar.8")); //$NON-NLS-1$
		JMenuItem iClean = new JMenuItem(Messages.getString("MenuBar.9")); //$NON-NLS-1$
		JMenuItem iDelete = new JMenuItem(Messages.getString("MenuBar.10")); //$NON-NLS-1$
		
		// View Menu
		JMenu mView = new JMenu(Messages.getString("MenuBar.View"));
		iLockSize = new JCheckBoxMenuItem(Messages.getString("MenuBar.LockSize"));
		iLockMaximize = new JCheckBoxMenuItem(Messages.getString("MenuBar.LockMaximize"));
		
		// Debug Menu
		JMenu mDebug = new JMenu("Debug");
		iActiveWindowWA = new JCheckBoxMenuItem("Active Window Workaround");
		iDetectMouseLeavingWA = new JCheckBoxMenuItem("Detect Mouse leaving Workaround");
		mDebug.add(iActiveWindowWA);
		mDebug.add(iDetectMouseLeavingWA);
		
		// Help Menu
		JMenu mHelp = new JMenu(Messages.getString("MenuBar.11")); //$NON-NLS-1$
		JMenuItem iUpdate = new JMenuItem(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		JMenuItem iAbout = new JMenuItem(Messages.getString("MenuBar.12")); //$NON-NLS-1$
		
		// add menus to GUI
		add(mFile);
		add(mProfile);
		add(mView);
		if (Boolean.valueOf(Config.getConf().getProperty("debug"))) {
			add(mDebug);
		}
		add(mHelp);
		mFile.add(iNewProfile);
		mFile.add(iImport);
		mFile.add(iClose);
		mFile.add(iExit);
		// mProfile.add(iChange);
		mProfile.add(iExport);
		mProfile.add(iT2D);
		mProfile.add(iF2D);
		mProfile.add(iD2F);
		mProfile.add(iClean);
		mProfile.add(iDelete);
		if (System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$
			mHelp.add(iUpdate);
		}
		mHelp.add(iAbout);
		mView.add(iLockSize);
		mView.add(iLockMaximize);
		
		// look and feel menu
		final JMenu lookAndFeelMenu = new JMenu("Design");
		final LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (final LookAndFeelInfo info : lafs) {
			final JRadioButtonMenuItem item = new JRadioButtonMenuItem(info.getName());
			item.setActionCommand(info.getClassName());
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					for (final LookAndFeelInfo info : lafs)
					{
						if (info.getClassName().equals(item.getActionCommand()))
						{
							try {
								UIManager.setLookAndFeel(info.getClassName());
								for (int i = 0; i < lookAndFeelMenu.getItemCount(); i++) {
									JMenuItem item = lookAndFeelMenu.getItem(i);
									if (item.getActionCommand().equals(info.getClassName())) {
										item.setSelected(true);
									} else {
										item.setSelected(false);
									}
								}
								Controller.getInstance().updateWindow();
							} catch (ClassNotFoundException err) {
								logger.error("Could not set lookAndFeel", err);
							} catch (InstantiationException err) {
								logger.error("Could not set lookAndFeel", err);
							} catch (IllegalAccessException err) {
								logger.error("Could not set lookAndFeel", err);
							} catch (UnsupportedLookAndFeelException err) {
								logger.error("Could not set lookAndFeel", err);
							}
							break;
						}
					}
				}
			});
			lookAndFeelMenu.add(item);
			add(lookAndFeelMenu);
			if (info.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
				item.setSelected(true);
			}
		}
		
		
		// Action Listener for menu items
		// iChange.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		//
		// }
		// });
		
		iNewProfile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new InputDlg(EMenuItem.iNewProfile, Messages.getString("MenuBar.13"), Messages.getString("MenuBar.14")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		
		iImport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogContainer(EMenuItem.iImport);
			}
		});
		
		iExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogContainer(EMenuItem.iExport);
			}
		});
		
		iClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().setWindowVisible(false);
			}
		});
		
		iExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().closeSuperFelix();
			}
		});
		
		// iChange.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO FelixP Menu bearbeiten(eingabe: Name und Pfad, vor ausgefuellt)
		// }
		// });
		
		iT2D.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogContainer(EMenuItem.iT2D);
			}
		});
		
		iF2D.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogContainer(EMenuItem.iF2D);
			}
		});
		
		iD2F.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogContainer(EMenuItem.iD2F);
			}
		});
		
		iClean.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ProfileCleanerDlg();
			}
		});
		
		iDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().deleteActiveProfile();
			}
		});
		
		iAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDlg();
			}
		});
		
		iUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new UpdateDlg();
			}
		});
		
		iLockSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Controller.getInstance().togglelockWindowSize();
			}
		});
		
		iLockMaximize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Controller.getInstance().toggleMaximizeWindowLock();
			}
		});
		
		iActiveWindowWA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Controller.getInstance().toggleActiveWindowWA();
			}
		});
		
		iDetectMouseLeavingWA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Controller.getInstance().toggleDetectMouseLeavingWA();
			}
		});
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
