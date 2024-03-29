/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 24, 2011
 * Author(s): FelixP, DanielAl, NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipException;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.helper.StringHelper;
import edu.dhbw.t10.helper.WindowHelper;
import edu.dhbw.t10.manager.output.Output;
import edu.dhbw.t10.manager.output.OutputManager;
import edu.dhbw.t10.manager.profile.ImportExportManager;
import edu.dhbw.t10.manager.profile.ProfileManager;
import edu.dhbw.t10.type.Config;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.key.Button;
import edu.dhbw.t10.type.keyboard.key.Key;
import edu.dhbw.t10.type.keyboard.key.ModeButton;
import edu.dhbw.t10.type.keyboard.key.MuteButton;
import edu.dhbw.t10.type.profile.Profile_V2;
import edu.dhbw.t10.type.tree.PriorityTree;
import edu.dhbw.t10.view.Presenter;
import edu.dhbw.t10.view.dialogs.InputDlg;
import edu.dhbw.t10.view.dialogs.ProfileChooser;
import edu.dhbw.t10.view.dialogs.ProfileCleanerDlg;
import edu.dhbw.t10.view.menus.EMenuItem;
import edu.dhbw.t10.view.menus.StatusPane;
import edu.dhbw.t10.view.panels.MainPanel;


/**
 * The Controller Class provides the central interface to combine the functionality of the program. the data flows
 * through it. <br>
 * Here all Managers and the view is initialized...<br>
 * It provides overwritten methods to handles actionEvents...<br>
 * 
 * @author NicolaiO, DirkK, FelixP, SebastianN, DanielAl
 */
public class Controller implements ActionListener, MouseListener {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger					= Logger.getLogger(Controller.class);
	private static Controller		instance;
	
	private ProfileManager			profileMan;
	private OutputManager			outputMan;
	private MainPanel					mainPanel;
	private Presenter					presenter;
	private StatusPane				statusPane;
	private ButtonMouseListener	buttonMouseListener	= new ButtonMouseListener();
	
	private boolean					readyForActionEvents	= false;
	private boolean					resizeWindowLocked	= false;
	private boolean					maximizeWindowLocked;
	/** only loose focus, if a key is pressed (so window will be active on mostly any time) */
	private boolean					activeWindowWA			= false;
	/** mouselistener, that tries to detect, when mouse is leaving and entering window */
	private boolean					detectMouseLeavingWA			= false;
	
	private final int					keyRepeatMs;
	private final int					keyDelayMs;


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * This Constructor instantiate all other objects... The Application is loaded...<br>
	 * The Controller is implemented as a Singleton<br>
	 * 
	 * @author NicolaiO, DirkK, FelixP, SebastianN, DanielAl
	 */
	private Controller() {
		instance = this;
		logger.debug("initializing..."); //$NON-NLS-1$
		
		maximizeWindowLocked = Boolean.valueOf(Config.getConf().getProperty("view.lockmaximize"));

		// load GUI
		mainPanel = new MainPanel();
		statusPane = new StatusPane();
		presenter = new Presenter(mainPanel, statusPane);
		presenter.setResizable(!Boolean.valueOf(Config.getConf().getProperty("view.lockwindowsize")));

		// This message is important! Otherwise, The StatusPane has a wrong height and the layout will be decreased
		// meaning, it gets smaller with each start...
		showStatusMessage(Messages.getString("Controller.1")); //$NON-NLS-1$
		
		// load Managers
		profileMan = new ProfileManager(mainPanel);
		outputMan = new OutputManager();
		
		int tmpKeyRepeatMs = 100;
		int tmpKeyDelayMs = 300;
		try {
			tmpKeyRepeatMs = Integer.valueOf(Config.getConf().getProperty("keyRepeatMs"));
		} catch (NumberFormatException e) {
			// use default value from above
		}
		try {
			tmpKeyDelayMs = Integer.valueOf(Config.getConf().getProperty("keyDelayMs"));
		} catch (NumberFormatException e) {
			// use default value from above
		}
		keyRepeatMs = tmpKeyRepeatMs;
		keyDelayMs = tmpKeyDelayMs;

		// now, the Controller should be ready!
		// hereafter, you should call methods, that need the controllers ActionEvents!
		readyForActionEvents = true;
		resizeWindow(profileMan.getActive().getKbdLayout().getSize());
		
		// checking for changing active window
		checkForActiveWindow();

		// now we are done.
		showStatusMessage(Messages.getString("Controller.2")); //$NON-NLS-1$
		logger.debug("initialized."); //$NON-NLS-1$
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Save the actual Profile and dictionary to be able to close the application.
	 * 
	 * @author DirkK
	 */
	public void closeSuperFelix() {
		try {
			logger.debug("closing - saving profile"); //$NON-NLS-1$
			profileMan.getActive().save();
			logger.debug("closing - saving the config"); //$NON-NLS-1$
			profileMan.saveConfig();
			logger.debug("closed - good BUY"); //$NON-NLS-1$
			logger.info("(c) FIT 42"); //$NON-NLS-1$
			System.exit(0);
		} catch (Exception e) {
			logger.error("closing routine produced an error: " + e.toString()); //$NON-NLS-1$
		}
		System.exit(1);
	}
	
	
	/**
	 * Resizes the Window and rescale the buttons to fit in there...
	 * 
	 * @param size
	 * @author NicolaiO
	 */
	public void resizeWindow(Dimension size) {
		if (readyForActionEvents && !resizeWindowLocked) {
			KeyboardLayout kbdLayout = profileMan.getActive().getKbdLayout();
			if (kbdLayout != null) {
				float xscale = (float) size.width / (float) kbdLayout.getOrigSize_x();
				float yscale = (float) size.height / (float) kbdLayout.getOrigSize_y();
				float fontScale = xscale + yscale / 2;
				kbdLayout.setScale_x(xscale);
				kbdLayout.setScale_y(yscale);
				kbdLayout.setScale_font(fontScale);
				kbdLayout.rescale();
				mainPanel.setPreferredSize(new Dimension(kbdLayout.getSize_x(), kbdLayout.getSize_y()));
				presenter.pack();
				logger.debug("Window rescaled"); //$NON-NLS-1$
			}
		}
	}
	
	
	/**
	 * update the GUI
	 * 
	 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
	 */
	public void updateWindow() {
		presenter.repaint();
	}

	
	/**
	 * Toggling the status of the window between sizable or not
	 * In Linux, the window will sometimes make strange movements...
	 * For this reason, the old location will be saved and restored.
	 * 
	 * @author NicolaiO
	 * @param locked
	 */
	public void setlockWindowSize(boolean locked) {
		Point p = presenter.getLocationOnScreen();
		presenter.setResizable(!locked);
		// without sleep, setLocation is called too early and window might
		// move to another location and won't move pack with the setLocation cmd
		try {
			Thread.sleep(20);
		} catch (InterruptedException err) {
			err.printStackTrace();
		}
		presenter.setLocation(p);
		Config.getConf().setProperty("view.lockwindowsize", String.valueOf(!presenter.isResizable()));
	}
	
	
	/**
	 * Set if window is visible or not and restore it,
	 * if it was minimized and should be visible
	 * 
	 * @param visible
	 * @author NicolaiO
	 */
	public void setWindowVisible(boolean visible) {
		presenter.setVisible(visible);
		if (visible)
			presenter.setState(Presenter.NORMAL);
	}
	
	
	/**
	 * Toggle lock on maximizing window
	 * 
	 * @author NicolaiO
	 * @param locked
	 */
	public void setMaximizeWindowLock(boolean locked) {
		maximizeWindowLocked = locked;
		Config.getConf().setProperty("view.lockmaximize", String.valueOf(maximizeWindowLocked));
		System.out.println(maximizeWindowLocked);
	}
	
	
	/**
	 * Enable/Disable active window workaround
	 * 
	 * @author geforce
	 */
	public void toggleActiveWindowWA() {
		if (activeWindowWA) {
			activeWindowWA = false;
			presenter.setFocusableWindowState(false);
		} else {
			activeWindowWA = true;
			presenter.setFocusableWindowState(true);
		}
	}
	
	
	/**
	 * Enable/Disable detection of mouse leaving window workaround
	 * 
	 * @author geforce
	 */
	public void toggleDetectMouseLeavingWA() {
		if (detectMouseLeavingWA) {
			detectMouseLeavingWA = false;
		} else {
			detectMouseLeavingWA = true;
		}
	}


	/**
	 * Starts a thread that checks in an interval, if
	 * the active window has changed.
	 * If it has been changed, clear the currently typed word in outputManager
	 * 
	 * @author NicolaiO
	 */
	private void checkForActiveWindow() {
		new Thread() {
			@Override
			public void run() {
				String activeWindow = "";
				do {
					try {
						String newActiveWindow = WindowHelper.getActiveWindowTitle();
						if (!newActiveWindow.equals(activeWindow)) {
							activeWindow = newActiveWindow;
							logger.info("Active Window changed: " + activeWindow);
							outputMan.clearWord();
						}
						Thread.sleep(500);
					} catch (InterruptedException err) {
						err.printStackTrace();
					}
				} while (true);
			}
		}.start();
	}
	
	
	/**
	 * Hide the keyboard
	 * 
	 * @param bool
	 */
	public void hideKeyboard(boolean bool) {
		presenter.setVisible(!bool);
	}


	// --------------------------------------------------------------------------
	// --- Profile --------------------------------------------------------------
	// --------------------------------------------------------------------------

	/**
	 * Creates a profile by name.
	 * 
	 * @param name
	 * @author SebastianN
	 */
	public void addNewProfile(String name) {
		Profile_V2 profile = profileMan.createProfile(name);
		if (profile != null) {
			profileMan.changeProfile(profile);
		} else {
			logger.error("can not add new profile, it could not be created!"); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * Deletes the active profile
	 * 
	 * @author NicolaiO
	 */
	public void deleteActiveProfile() {
		// get active profile to be delete
		Profile_V2 todelete = profileMan.getActive();
		// get potential new profile
		Profile_V2 newProfile = profileMan.getProfiles().get(0);

		// after deleting profile, first or second profile should be made active
		if (todelete == newProfile) {
			if (profileMan.getProfiles().size() > 1) {
				newProfile = profileMan.getProfiles().get(1);
			} else {
				logger.debug("Only one or zero profiles left. Can't delete."); //$NON-NLS-1$
				return;
			}
		}
		profileMan.changeProfile(newProfile);
		profileMan.deleteProfile(todelete);
	}
	
	
	/**
	 * Check, if a given profile already exists.
	 * 
	 * @param name of the profile to check
	 * @return true if it exists, false else
	 * @author NicolaiO
	 */
	public boolean existProfile(String name) {
		return profileMan.existProfile(name);
	}
	
	
	// --------------------------------------------------------------------------
	// --- Statusbar interface --------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Display a tooltip in the statusbar (It will be there, until another tooltip is set or it is manually hidden
	 * 
	 * @param message
	 * @author NicolaiO
	 */
	public void showTooltip(String message) {
		statusPane.enqueueMessage(message, StatusPane.RIGHT);
	}
	
	
	/**
	 * Hide current tooltip
	 * 
	 * @author NicolaiO
	 */
	public void hideTooltip() {
		statusPane.enqueueMessage("", StatusPane.RIGHT); //$NON-NLS-1$
	}
	

	/**
	 * Show a status message in the statusbar. It will be enqueued and displayed, after all other message were displayed.
	 * Each message has a fixed display time.
	 * 
	 * @param message
	 * @author NicolaiO
	 */
	public void showStatusMessage(String message) {
		statusPane.enqueueMessage(message, StatusPane.LEFT);
	}


	// --------------------------------------------------------------------------
	// --- Action handler -------------------------------------------------------
	// --------------------------------------------------------------------------

	/**
	 * Starts the right activities for a specific event...
	 * The events come from Buttons, ModeButtons, MuteButtons, DDLs and other elements
	 * 
	 * @param e
	 * @author NicolaiO, DirkK, FelixP, SebastianN, DanielAl
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!readyForActionEvents) {
			logger.debug("An ActionEvent was blocked, because controller is not ready yet."); //$NON-NLS-1$
			return;
		}

		if (e.getSource() instanceof Button) {
			logger.debug("Normal Button pressed."); //$NON-NLS-1$
			outputMan.buttonPressed((Button) e.getSource(), profileMan.getActive());
		}
		
		if (e.getSource() instanceof ModeButton) {
			logger.debug("ModeButton pressed."); //$NON-NLS-1$
			ModeButton modeB = (ModeButton) e.getSource();
			// currently we do not support some buttons for linux...
			if (Output.getOs() == Output.LINUX
					&& (modeB.getModeKey().getKeycode().equals("\\WINDOWS\\") || modeB.getModeKey().getKeycode() //$NON-NLS-1$
							.equals("\\CONTEXT_MENU\\"))) { //$NON-NLS-1$
				showStatusMessage(Messages.getString("Controller.0")); //$NON-NLS-1$
			} else {
				if (modeB.isModesDisabled()) {
					// The helpB is created, because a Modebuttoon should with a right click treated as a normal button. So a
					// new Button with the Key of the ModeButton is created.
					Button helpB = new Button(1, 1, 1, 1);
					Key helpKey = ((ModeButton) e.getSource()).getModeKey().clone();
					helpB.setKey(helpKey);
					outputMan.buttonPressed(helpB, profileMan.getActive());
					// eIsButton(helpB);
				} else {
					modeB.push();
				}
			}
		}
		
		if (e.getSource() instanceof MuteButton) {
			logger.debug("MuteButton pressed."); //$NON-NLS-1$
			outputMan.muteButtonPressed((MuteButton) e.getSource(), profileMan.getActive());
		}
		
		if (e.getSource() instanceof DropDownList) {
			logger.debug("DropDownList pressed."); //$NON-NLS-1$
			eIsDropDownList((DropDownList) e.getSource());
		}
		
		if (e.getSource() instanceof ProfileChooser) {
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
				eIsProfileChooser((ProfileChooser) e.getSource());
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- eIs Actions ----------------------------------------------------------
	// --------------------------------------------------------------------------


	/**
	 * Do something, if ProfileChooser was activated. o_O
	 * 
	 * @param pc
	 * @author FelixP
	 */
	private void eIsProfileChooser(ProfileChooser pc) {
		final File path = pc.getSelectedFile();
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		pc.setVisible(false);
		
		switch (pc.getMenuType()) {
		// import profile
			case iImport:
				try {
					profileMan.importProfiles(path);
				} catch (ZipException err1) {
					logger.error("unable to extract file " + path.toString()); //$NON-NLS-1$
				} catch (IOException err1) {
					logger.error("Error by importing Profile from " + path.toString()); //$NON-NLS-1$
				}
				break;
			
			// export profile
			case iExport:
				String pathToFile = StringHelper.addEnding(path.toString(), ".zip"); //$NON-NLS-1$
				try {
					profileMan.exportProfiles(pathToFile);
					logger.debug("Profile exported"); //$NON-NLS-1$
					showStatusMessage(Messages.getString("Controller.25")); //$NON-NLS-1$
				} catch (IOException err1) {
					logger.error("Unable to export profile " + pathToFile); //$NON-NLS-1$
				}
				break;
			
			// Extend Dictionary By Text
			case iT2D:
				new Thread() {
					public void run() {
						profileMan.getActive().save();
						try {
							HashMap<String, Integer> words = ImportExportManager.importFromText(path.toString());
							profileMan.getActive().getTree().importFromHashMap(words);
						} catch (IOException err) {
							showStatusMessage(Messages.getString("Controller.27")); //$NON-NLS-1$
						}
						showStatusMessage(Messages.getString("Controller.28")); //$NON-NLS-1$
					}
				}.start();
				break;
			
			// Extend Dictionary From File
			case iF2D:
				new Thread() {
					public void run() {
						try {
							HashMap<String, Integer> words = ImportExportManager.importFromFile(path.toString(), true);
							profileMan.getActive().getTree().importFromHashMap(words);
							showStatusMessage(Messages.getString("Controller.29")); //$NON-NLS-1$
						} catch (IOException err) {
							showStatusMessage(Messages.getString("Controller.30")); //$NON-NLS-1$
						}
					}
				}.start();
				break;
			
			// Export Dictionary To File
			case iD2F:
				words = profileMan.getActive().getTree().exportToHashMap();
				try {
					pathToFile = StringHelper.addEnding(path.toString(), ".tree"); //$NON-NLS-1$
					ImportExportManager.exportToFile(words, pathToFile);
					showStatusMessage(Messages.getString("Controller.32") + pathToFile + "."); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (IOException err) {
					showStatusMessage(Messages.getString("Controller.34")); //$NON-NLS-1$
				}
				break;
		}
	}
	

	/**
	 * 
	 * Do something, if dialog was opened.
	 * 
	 * @param menuItem
	 * @param o
	 * @author FelixP
	 */
	public void eIsDlg(EMenuItem menuItem, Object o) {
		switch (menuItem) {
		// new profile
			case iNewProfile:
				InputDlg iDlg = (InputDlg) o;
				String newProfile = iDlg.getProfileName();
				if (!existProfile(newProfile)) {
					this.addNewProfile(newProfile);
					iDlg.setVisible(false);
				} else {
					iDlg.setLblText(Messages.getString("Controller.35")); //$NON-NLS-1$
				}
				break;
			
			// Clean Dictionary
			case iClean:
				ProfileCleanerDlg iCleanDlg = (ProfileCleanerDlg) o;
				Integer freq = iCleanDlg.getFrequency();
				Date date = iCleanDlg.getDate();
				int deleted = profileMan.getActive().getTree()
						.autoCleaning(freq, date.getTime(), PriorityTree.BOTTOM_OR_OLDER);
				showStatusMessage(Messages.getString("Controller.36") + deleted + Messages.getString("Controller.37")); //$NON-NLS-1$ //$NON-NLS-2$
				break;
		}
	}
	
	
	/**
	 * Switches the profiles based on a Dropdownlist... <br>
	 * 
	 * @param currentDdl
	 * @author DanielAl, NicolaiO
	 */
	private void eIsDropDownList(DropDownList currentDdl) {
		if (currentDdl.getType() == DropDownList.PROFILE) {
			Profile_V2 selectedProfile = profileMan.getProfileByName(currentDdl.getSelectedItem().toString());
			if (selectedProfile != null) {
				logger.debug("selected Profilename: " + selectedProfile.getName()); //$NON-NLS-1$
				profileMan.changeProfile(selectedProfile);
			} else {
				logger.warn("Selected Item refers to a non valid profile: \"" + currentDdl.getSelectedItem() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- Mouse Events ---------------------------------------------------------
	// --------------------------------------------------------------------------
	

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof MuteButton) {
			// show tooltip in statusbar
			MuteButton pb = (MuteButton) e.getSource();
			showTooltip(pb.getMode().getTooltip());
		}
	}
	
	
	@Override
	public void mouseExited(MouseEvent e) {
		// delete tooltip in statusbar
		hideTooltip();
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	
	private class ButtonMouseListener implements MouseListener {
		private ScheduledExecutorService	executor	= Executors.newScheduledThreadPool(1);
		private ScheduledFuture<?>			future;


		@Override
		public void mouseClicked(MouseEvent e) {
		}
		
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource() instanceof Button) {
				final Button button = (Button) e.getSource();
				
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						final boolean autoCompleting = profileMan.getActive().isAutoCompleting();
						profileMan.getActive().setAutoCompleting(false);
						outputMan.buttonPressed(button, profileMan.getActive());
						profileMan.getActive().setAutoCompleting(autoCompleting);
					}
				};
				
				future = executor.scheduleAtFixedRate(runnable, keyDelayMs, keyRepeatMs, TimeUnit.MILLISECONDS);
			}
		}
		
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (future != null) {
				future.cancel(true);
			}
		}
		
		
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		
		
		@Override
		public void mouseExited(MouseEvent e) {
		}
		
	}


	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Calls the constructor if no instance exist. Singleton Design Pattern...
	 * 
	 * @return Controller
	 * @author NicolaiO
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	
	
	/**
	 * @return readyForActionEvents
	 */
	public boolean isReadyForActionEvents() {
		return readyForActionEvents;
	}
	
	
	/**
	 * @return maximizeWindowLocked
	 */
	public boolean isMaximizeWindowLocked() {
		return maximizeWindowLocked;
	}
	
	
	/**
	 * @return the activeWindowWA
	 */
	public boolean isActiveWindowWA() {
		return activeWindowWA;
	}
	
	
	/**
	 * @param activeWindowWA the activeWindowWA to set
	 */
	public void setActiveWindowWA(boolean activeWindowWA) {
		this.activeWindowWA = activeWindowWA;
	}
	
	
	/**
	 * @return detectMouseLeavingWA
	 */
	public boolean isDetectMouseLeavingWA() {
		return detectMouseLeavingWA;
	}
	
	
	/**
	 * @param detectMouseLeavingWA
	 */
	public void setDetectMouseLeavingWA(final boolean detectMouseLeavingWA) {
		this.detectMouseLeavingWA = detectMouseLeavingWA;
	}
	
	
	/**
	 * @return the buttonMouseListener
	 */
	public final ButtonMouseListener getButtonMouseListener() {
		return buttonMouseListener;
	}
}
