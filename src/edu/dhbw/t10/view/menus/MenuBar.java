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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
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
	protected static final Object[]	eventCache			= null;
	
	
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
		
		// Help Menu
		JMenu mHelp = new JMenu(Messages.getString("MenuBar.11")); //$NON-NLS-1$
		JMenuItem iUpdate = new JMenuItem(Messages.getString("MenuBar.15")); //$NON-NLS-1$
		JMenuItem iAbout = new JMenuItem(Messages.getString("MenuBar.12")); //$NON-NLS-1$

		// View Menu
		JMenu mView = new JMenu(Messages.getString("MenuBar.View"));
		JMenuItem iLockSize = new JMenuItem(Messages.getString("MenuBar.LockSize"));
		JMenuItem iLockMaximize = new JMenuItem(Messages.getString("MenuBar.LockMaximize"));

		// add menus to GUI
		add(mFile);
		add(mProfile);
		add(mView);
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
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
