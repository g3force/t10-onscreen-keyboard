/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager.profile;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.Image;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.key.PhysicalButton;
import edu.dhbw.t10.type.profile.Profile;
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
	private String						datapath;
	private String						configFile				= "t10keyboard.conf";
	private ArrayList<Profile>		profiles					= new ArrayList<Profile>();
	private ArrayList<String>		profilePathes			= new ArrayList<String>();
	private Profile					activeProfile;
	private String						defaultActiveProfile	= "default";
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
		logger.debug("initializing...");
		
		this.mainPanel = mainPanel;

		// load datapath
		// works for Windows and Linux... so the data is stored in the systems userdata folder...
		datapath = System.getProperty("user.home") + "/.t10keyboard";
		File tf = new File(datapath);
		if (!tf.exists()) {
			tf.mkdirs();
		}

		// fill activeProfileName and profilePathes with the data from the config file
		readConfig();
		loadSerializedProfiles(); // deserializes all profiles, fills profiles
		// if no profiles were loaded, create a new one
		if (profiles.size() == 0) {
			logger.debug("No profiles loaded. New profile will be created.");
			createProfile(defaultActiveProfile);
		}

		// set active profile by defauleActiveProfile which was either loaded from config file or is set to a default
		// value
		activeProfile = getProfileByName(defaultActiveProfile);

		// if the defaultActiveProfile in the config file references a non existent profile, create a new profile with the
		// given name
		if (activeProfile == null) {
			activeProfile = createProfile(defaultActiveProfile);
		}
		
		// change to chosen profile
		changeProfile(activeProfile);

		logger.debug("initialized.");
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Reads the config-file with all entrys and assigns
	 * the read values.
	 * 
	 * @author SebastianN
	 */
	private void readConfig() {
		try {
			File confFile = new File(datapath + "/" + configFile);
			if (confFile.exists()) {
				FileReader fr = new FileReader(confFile);
				BufferedReader br = new BufferedReader(fr);
				
				String entry = "";
				while ((entry = br.readLine()) != null) {
					// Commentary-Indicator: //
					if (entry.indexOf("//") >= 0)
						entry = entry.substring(0, entry.indexOf("//"));
					
					if (entry.isEmpty()) // In case an entry was just a comment.
						continue;
					
					// Comment-Indicators deleted.
					// Regular Format:
					// ActiveProfile=NAMEOFPROFILE
					// ProfilePath=config.cfg
					// ProfilePath=C:\lol.cfg
					int posOfEql = entry.indexOf("=");
					
					// Split and afterwards assign values.
					try {
						String valName = entry.substring(0, posOfEql);
						String value = entry.substring(posOfEql + 1, entry.length());
						if (valName.toLowerCase().equals("profilepath")) {
							if (value.isEmpty())
								continue;
							profilePathes.add(value);
						} else if (valName.toLowerCase().equals("activeprofile")) {
							if (value.isEmpty())
								continue;
							defaultActiveProfile = value;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						logger.error("Exception in readConfig().assignValues: " + ex.toString());
					}
				}
				br.close();
				logger.info("config loaded: activeProfileName=" + defaultActiveProfile + " profiles="
						+ profilePathes.size());
			} else {
				logger.debug("Config file could not be found. Doesn't matter, though.");
			}
		} catch (IOException io) {
			logger.debug("IOException in readConfig()");
			io.printStackTrace();
		} catch (Exception ex) {
			logger.debug("Exception in readConfig(): " + ex.toString());
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Creates a comment for config-files.
	 * 
	 * @param comment - String. Comment you want to add.
	 * @return Changed comment as String.
	 * @author SebastianN
	 */
	private String createComment(String comment) {
		comment = "//" + comment;
		return comment;
	}
	
	
	/**
	 * Add an entry to the config file.
	 * 
	 * @param bw - Handle/Reference to a BufferedWriter
	 * @param entry - String containing what you want to write.
	 * @author SebastianN
	 */
	private void addEntry(BufferedWriter bw, String entry) {
		try {
			bw.write(entry + "\n");
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	
	/**
	 * Saves the name of the active profile and the path to all profile-files.
	 * 
	 * @author SebastianN
	 */
	public void saveConfig() {
		try {
			File confFile = new File(datapath + "/" + configFile);
			FileWriter fw = new FileWriter(confFile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			addEntry(bw, createComment("Configfile for T10"));
			
			
			if (activeProfile != null)
				addEntry(bw, "ActiveProfile=" + activeProfile.getName());
			
			for (int i = 0; i < profiles.size(); i++) {
				if (profiles.get(i).getPaths().get("profile").isEmpty()) {
					logger.error("Profile " + profiles.get(i).getName() + " has no path to profile");
					continue;
				}
				addEntry(bw, "ProfilePath=" + profiles.get(i).getPaths().get("profile"));
			}
			logger.info("Config file saved");
			bw.close();
		} catch (IOException io) {
			logger.debug("IOException in readConfig()");
			io.printStackTrace();
		} catch (Exception ex) {
			logger.debug("Exception in readConfig(): " + ex.toString());
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Create a new profile
	 * 
	 * @param profileName - String. Name of the profile.
	 * @param pathToNewProfile - String. Path to the new profile.
	 * @return Handle/Pointer to the new profile.
	 * @author SebastianN, NicolaiO
	 */
	public Profile createProfile(String profileName) {
		Profile newProfile = getProfileByName(profileName);
		
		if (newProfile != null) {
			logger.warn("Profile already exists.");
		} else {
			newProfile = new Profile(profileName, datapath);
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
	public Profile getProfileByName(String name) {
		if (!profiles.isEmpty()) {
			for (int i = 0; i < profiles.size(); i++) {
				if (profiles.get(i).getName().equals(name))
					return profiles.get(i);
			}
		}
		return null;
	}
	
	
	public void importProfiles(File zipFile) throws ZipException, IOException {
		// Finding possible Profile Name
		String profileName = zipFile.getName();
		profileName = profileName.replace(".zip", "");
		int counter = 0;
		while (existProfile(profileName)) {
			counter++;
			if (counter == 1)
				profileName += counter;
			else
				profileName = profileName.substring(0, profileName.length() - 1) + counter;
		}
		
		// creating the profile
		Profile prof = createProfile(profileName);

		// exporting the files form the zip archive to the pathes given in the profile
		ImportExportManager.importProfiles(zipFile, prof);
		logger.debug("Files from the zip File " + zipFile + " extracted");
		
		changeProfile(prof);
	}
	
	
	public void exportProfiles(String zipFile) throws IOException {
		getActive().save();
		ImportExportManager.exportProfiles(getActive(), new File(zipFile));
	}

	
	/**
	 * 
	 * Deletes a profile depending on the ID.<br/>
	 * If the ID we deleted was currently active,
	 * we either mark the first profile as active or mark that we need a new profile.
	 * 
	 * @param id - int. ID of the profile you want to delete.
	 */
	public void deleteProfile(Profile profile) {
		if (profiles.size() <= 1) {
			logger.debug("Only one or zero profiles left. Can't delete.");
			return;
		}
		profiles.remove(profile);
		File dir = new File(profile.getPaths().get("profile"));
		dir = dir.getParentFile();
		for (Entry<String, String> file : profile.getPaths().entrySet()) {
			deleteFile(file.getValue());
		}
		dir.delete();
		getActive().loadDDLs(profiles);
	}
	
	
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
			logger.error(path + " could not be deleted.");
	}
	

	/**
	 * Marks a profile as 'active'.
	 * 
	 * @param newActive - Handle of the to-be activated profile
	 * @author SebastianN, NicolaiO
	 */
	public void changeProfile(Profile newActive) {
		if (!changeProfileBlocked) {
			changeProfileBlocked = true;
			
			if (newActive == null) {
				logger.error("changeProfile was called with null-Profile");
				return;
			}
			
			logger.info("Setting profile " + newActive + " active.");

			// save currently active profile
			if (activeProfile != null) {
				activeProfile.save();
			}
			
			// set and load new active profile
			activeProfile = newActive;
			activeProfile.load();
			activeProfile.loadDDLs(profiles);
			
			// update GUI
			loadLayoutToGUI(activeProfile.getKbdLayout());
			Controller.getInstance().resizeWindow(getActive().getKbdLayout().getSize());
			
			logger.info("Profile now active: " + getActive());
			changeProfileBlocked = false;
		} else {
			logger.debug("changeProfile blocked");
		}
	}
	
	
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
		logger.debug("GUI contains " + mainPanel.getComponentCount() + " Compontents now.");
	}


	/**
	 * Loads serialized profiles from file.
	 * 
	 * @author SebastianN
	 */
	private void loadSerializedProfiles() {
		int counter = 0;
		if (profiles == null) {
			profiles = new ArrayList<Profile>();
		}
		for (int i = 0; i < profilePathes.size(); i++) {
			try {
				Profile dProf = (Profile) Serializer.deserialize(profilePathes.get(i));
				profiles.add(dProf);
				counter++;
			} catch (IOException io) {
				logger.error("Not able to deserialize Profile from file " + profilePathes.get(i));
			}
		}
		logger.info("Deserialized " + counter + " profiles.");
	}
	
	
	/**
	 * Check if the given profile name exists
	 * 
	 * @param profile
	 * @return profile exists -> true ...else false
	 * @author FelixP
	 */
	public boolean existProfile(String profile) {
		Profile p = getProfileByName(profile);
		if (p == null)
			return false;
		return true;
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
	public ArrayList<Profile> getProfiles() {
		return profiles;
	}
	
	
	/**
	 * Return currently active profile
	 * 
	 * @return active profile
	 * @author SebastianN
	 */
	public Profile getActive() {
		return activeProfile;
	}
	
	
	public KeyboardLayout getRealKeyboardLayout() {
		return realKeyboardLayout;
	}
	
}