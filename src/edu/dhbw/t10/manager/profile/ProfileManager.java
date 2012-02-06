/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO
 * z *
 * *********************************************************
 */
package edu.dhbw.t10.manager.profile;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import edu.dhbw.t10.SuperFelix;
import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.Config;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.Image;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.key.PhysicalButton;
import edu.dhbw.t10.type.profile.Profile;
import edu.dhbw.t10.type.profile.Profile_V2;
import edu.dhbw.t10.view.panels.MainPanel;


/**
 * The profile-manager handles all profiles, including the path to its profile-file.
 * 
 * @author SebastianN
 */
public class ProfileManager {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger					= Logger.getLogger(ProfileManager.class);
	private ArrayList<Profile_V2>		profiles					= new ArrayList<Profile_V2>();
	private Profile_V2					activeProfile;
	private MainPanel					mainPanel;
	private boolean					changeProfileBlocked	= false;
	
	/**
	 * This should always be a reference to the currently applied KeyboardLayout.
	 * On profile change, this attribute should be overridden with the new layout.
	 * Thus, we do not need a reference to the mainPanel! *
	 */
	private KeyboardLayout			realKeyboardLayout;
	

	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * 
	 * Constructor of the ProfileManager. <br/>
	 * Reads the config file, deserializes all profile based on read config file
	 * and marks one profile as active. If no profile was found, a default-profile will be created.
	 * 
	 * @author SebastianN, NicolaiO
	 */
	public ProfileManager(MainPanel mainPanel) {
		logger.debug("initializing..."); //$NON-NLS-1$
		
		this.mainPanel = mainPanel;

		File tf = new File(SuperFelix.getDatapath());
		if (!tf.exists()) {
			tf.mkdirs();
		}
		
		// fill activeProfileName and profilePathes with the data from the config object
		loadProfiles(); // deserializes all profiles, fills profiles

		// if no profiles were loaded, create a new one
		if (profiles.size() == 0) {
			logger.debug("No profiles loaded. New profile will be created."); //$NON-NLS-1$
			activeProfile = createProfile("default"); //$NON-NLS-1$
		}

		// set active profile by defauleActiveProfile which was either loaded from config file or is set to a default
		// value
		else {
			activeProfile = getProfileByName(Config.getConf().getProperty("ActiveProfile")); //$NON-NLS-1$
			if (activeProfile == null) {
				activeProfile = profiles.get(0);
			}
		}
		
		// change to chosen profile
		changeProfile(activeProfile);

		logger.debug("initialized."); //$NON-NLS-1$
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	
	// -------------------------profile---------------
	/**
	 * Create a new profile
	 * 
	 * @param profileName - String. Name of the profile.
	 * @param pathToNewProfile - String. Path to the new profile.
	 * @return Handle/Pointer to the new profile.
	 * @author SebastianN, NicolaiO
	 */
	public Profile_V2 createProfile(String profileName) {
		Profile_V2 newProfile = getProfileByName(profileName);
		
		if (newProfile != null) {
			logger.warn("Profile already exists."); //$NON-NLS-1$
		} else {
			newProfile = new Profile_V2(profileName, SuperFelix.getDatapath());
			profiles.add(newProfile);
		}
		return newProfile;
	}
	
	
	/**
	 * 
	 * Get a Profile based on its name
	 * 
	 * @param name - String. Name of the profile.
	 * @return If found, handle/reference to said profile. Otherwise NULL
	 * @author SebastianN
	 */
	public Profile_V2 getProfileByName(String name) {
		if (!profiles.isEmpty()) {
			for (int i = 0; i < profiles.size(); i++) {
				if (profiles.get(i).getName().equals(name))
					return profiles.get(i);
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * Deletes a profile depending on the ID.<br/>
	 * If the ID we deleted was currently active,
	 * we either mark the first profile as active or mark that we need a new profile.
	 * 
	 * @param id - int. ID of the profile you want to delete.
	 */
	public void deleteProfile(Profile_V2 profile) {
		if (profiles.size() <= 1) {
			logger.debug("Only one or zero profiles left. Can't delete."); //$NON-NLS-1$
			return;
		}
		profiles.remove(profile);
		File dir = new File(profile.getPaths().get("profile")); //$NON-NLS-1$
		dir = dir.getParentFile();
		for (Entry<String, String> file : profile.getPaths().entrySet()) {
			if (!existDependency(file.getValue()))
				deleteFile(file.getValue());
		}
		dir.delete();
		getActive().loadDDLs(profiles);
	}
	
	
	private boolean existDependency(String f) {
		int counter = 0;
		for (Profile_V2 profile : profiles) {
			for (String file : profile.getPaths().values()) {
				if (file.equals(f)) {
					counter++;
				}
			}
		}
		if (counter < 1) {
			return false;
		} else {
			return true;
		}
	}

	
	// -----------------------IMPORT/EXPORT------------------
	/**
	 * imports a new profile
	 * @param zipFile the zip file containing the data
	 * @throws ZipException
	 * @throws IOException
	 * @author dirk
	 */
	public void importProfiles(final File zipFile) throws ZipException, IOException {
		// Finding possible Profile Name
		String profileName = zipFile.getName();
		profileName = profileName.replace(".zip", ""); //$NON-NLS-1$ //$NON-NLS-2$
		int counter = 0;
		while (existProfile(profileName)) {
			counter++;
			if (counter == 1)
				profileName += counter;
			else
				profileName = profileName.substring(0, profileName.length() - 1) + counter;
		}
		
		// creating the profile
		final Profile_V2 prof = createProfile(profileName);

		// exporting the files form the zip archive to the pathes given in the profile
		new Thread() {
			public void run() {
				try {
					ImportExportManager.importProfiles(zipFile, prof);
				} catch (ZipException err) {
					Controller.getInstance().showStatusMessage(Messages.getString("ProfileManager.12")); //$NON-NLS-1$
					logger.error("Could not import profile from " + zipFile.toString()); //$NON-NLS-1$
					err.printStackTrace();
				} catch (IOException err) {
					Controller.getInstance().showStatusMessage(Messages.getString("ProfileManager.14")); //$NON-NLS-1$
					logger.error("Could not import profile from " + zipFile.toString()); //$NON-NLS-1$
					err.printStackTrace();
				}
			}
		}.start();
		logger.debug("Files from the zip File " + zipFile + " extracted"); //$NON-NLS-1$ //$NON-NLS-2$
		
		changeProfile(prof, false);
	}
	
	
	/**
	 * exports a profile to zip
	 * @param zipFile
	 * @throws IOException
	 * @author dirk
	 */
	
	public void exportProfiles(String zipFile) throws IOException {
		getActive().save();
		ImportExportManager.exportProfiles(getActive(), new File(zipFile));
	}

	
	// ---------------files--------------

	/**
	 * Delete the given file and log an error, if failed.
	 * 
	 * @param path to file
	 * @author NicolaiO
	 */
	private void deleteFile(String path) {
		File f;
		f = new File(path);
		if (!f.delete())
			logger.error(path + " could not be deleted."); //$NON-NLS-1$
	}
	
	
	// ---------------------active profile--------------
	/**
	 * Marks a profile as 'active'.
	 * 
	 * @param newActive - Handle of the to-be activated profile
	 * @author SebastianN, NicolaiO, DirkK
	 */
	public void changeProfile(Profile_V2 newActive) {
		changeProfile(newActive, true);
	}
	
	
	public void changeProfile(Profile_V2 newActive, boolean load) {
		if (!changeProfileBlocked) {
			changeProfileBlocked = true;
			
			if (newActive == null) {
				logger.error("changeProfile was called with null-Profile"); //$NON-NLS-1$
				return;
			}
			
			logger.info("Setting profile " + newActive + " active."); //$NON-NLS-1$ //$NON-NLS-2$

			// save currently active profile
			if (activeProfile != null) {
				activeProfile.save();
				activeProfile.unload();
			}
			
			// set and load new active profile
			activeProfile = newActive;
			activeProfile.load(load);
			activeProfile.loadDDLs(profiles);
			Config.getConf().setProperty("ActiveProfile", activeProfile.getName()); //$NON-NLS-1$
			
			// update GUI
			loadLayoutToGUI(activeProfile.getKbdLayout());
			Controller.getInstance().resizeWindow(getActive().getKbdLayout().getSize());
			
			logger.info("Profile now active: " + getActive()); //$NON-NLS-1$
			changeProfileBlocked = false;
		} else {
			logger.debug("changeProfile blocked"); //$NON-NLS-1$
		}
	}
	
	
	// ---------------------------layout-----------------
	/**
	 * Load the given KeyboardLayout into the Mainpanel and remove all other Components.
	 * This is neccessary, when you change the profile and thus the Layout!
	 * 
	 * @param kbd KeyboardLayout
	 * @author NicolaiO
	 */
	private void loadLayoutToGUI(KeyboardLayout kbd) {
		mainPanel.setPreferredSize(new Dimension(kbd.getSize_x(), kbd.getSize_y()));
		mainPanel.removeAll();
		for (PhysicalButton button : kbd.getAllPhysicalButtons()) {
			mainPanel.add(button);
		}
		for (DropDownList ddl : kbd.getDdls()) {
			mainPanel.add(ddl);
		}
		for (Image img : kbd.getImages()) {
			mainPanel.add(img);
		}
		logger.debug("GUI contains " + mainPanel.getComponentCount() + " Compontents now."); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/**
	 * Loads serialized profiles from file.
	 * 
	 * @author SebastianN
	 */
	private void loadProfiles() {
		int counter = 0;
		if (profiles == null) {
			profiles = new ArrayList<Profile_V2>();
		}

		// temp file containing all files
		LinkedList<File> profileFiles = new LinkedList<File>();
		
		// getting all profile files from the default directory
		profileFiles.addAll(getProfileFiles(new File(SuperFelix.getDatapath() + "/profiles")));

		// getting all profile files from the PROFILE_PATH directory
		String[] profilePathes = Config.getConf().getProperty("PROFILE_PATH").split(":"); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < profilePathes.length; i++) {
			File file = new File(profilePathes[i]);
			profileFiles.addAll(getProfileFiles(file));
		}

		// create the profiles on basis of the .profile file
		for (File profileFile : profileFiles) {
			Properties prop = new Properties();
			Profile_V2 prof;
			try {
				FileInputStream fis = new FileInputStream(profileFile);
				prop.load(fis);
				prof = new Profile_V2(prop, SuperFelix.getDatapath());
				profiles.add(prof);
			} catch (IOException err) {
				// prof = new Profile("toDelete, take the new profile format", datapath);
				logger.warn("Could not read the profile file"); //$NON-NLS-1$
			} catch (ExceptionInInitializerError e) {
				try {
					prop = new Properties();
					Profile p = Serializer.deserialize(profileFile.toString());
					prop.setProperty("name", p.getName()); //$NON-NLS-1$
					prop.setProperty("profile", p.getPaths().get("profile")); //$NON-NLS-1$ //$NON-NLS-2$
					prop.setProperty("tree", p.getPaths().get("tree")); //$NON-NLS-1$ //$NON-NLS-2$
					prop.setProperty("layout", p.getPaths().get("layout")); //$NON-NLS-1$ //$NON-NLS-2$
					prop.setProperty("chars", Config.getConf().getProperty("defaultAllowedChars")); //$NON-NLS-1$ //$NON-NLS-2$
					prop.setProperty("autoCompleting", String.valueOf(p.isAutoCompleting())); //$NON-NLS-1$
					prop.setProperty("treeExpanding", String.valueOf(p.isTreeExpanding())); //$NON-NLS-1$
					prop.setProperty("autoProfileChange", String.valueOf(p.isAutoProfileChange())); //$NON-NLS-1$
					prof = new Profile_V2(prop, SuperFelix.getDatapath());
					prof.save();
					profiles.add(prof);
					File oldCharFile = new File(p.getPaths().get("chars")); //$NON-NLS-1$
					if (oldCharFile.exists())
						oldCharFile.delete();
				} catch (IOException err) {
					logger.error("Found profile is neither a new profile config file, nor a serialized profile"); //$NON-NLS-1$
				}
			}
		}
		logger.info("Deserialized " + counter + " profiles."); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	/**
	 * get all files of type .profile in a given directory (recursive)
	 * @param f the directory
	 * @return
	 * @author dirk
	 */
	private LinkedList<File> getProfileFiles(File f) {
		//define filter for all directories in a given dir
		FilenameFilter isDir = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
				return new File(dir.toString() + "/" + name).isDirectory(); //$NON-NLS-1$
		    }
		};
		//define filter for all .proifle files in a given dir
		FilenameFilter isProject = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
				if (name.lastIndexOf(".") > 0) //$NON-NLS-1$
					return name.substring(name.lastIndexOf("."), name.length()).equals(".profile"); //$NON-NLS-1$ //$NON-NLS-2$
				else
					return false;
		    }
		};
		// getting all profile files directly in the folder
		LinkedList<File> retur = new LinkedList<File>();
		File[] ret = f.listFiles(isProject);
		if (ret != null) {
			for (File profile : ret) {
				retur.add(profile);
			}
		}
		// adding all profile files recursively
		File[] subdirs = f.listFiles(isDir);
		if(subdirs!=null)
			for (File subdir : subdirs) {
			retur.addAll(getProfileFiles(subdir));
		}
		return retur;
	}
	


	/**
	 * Check if the given profile name exists
	 * 
	 * @param profile
	 * @return profile exists -> true ...else false
	 * @author FelixP
	 */
	public boolean existProfile(String profile) {
		Profile_V2 p = getProfileByName(profile);
		if (p == null)
			return false;
		return true;
	}
	
	
	// ---------------config-------------
	public void saveConfig() {
		Config.saveConfig(SuperFelix.getDatapath());
	}

	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Return all known profiles
	 * 
	 * @return list of all profiles
	 * @author SebastianN
	 */
	public ArrayList<Profile_V2> getProfiles() {
		return profiles;
	}
	
	
	/**
	 * Return currently active profile
	 * 
	 * @return active profile
	 * @author SebastianN
	 */
	public Profile_V2 getActive() {
		return activeProfile;
	}
	
	
	public KeyboardLayout getRealKeyboardLayout() {
		return realKeyboardLayout;
	}
	
}
