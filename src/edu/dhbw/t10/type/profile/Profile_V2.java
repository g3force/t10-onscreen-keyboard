/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.profile;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.helper.StringHelper;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.manager.keyboard.KeyboardLayoutLoader;
import edu.dhbw.t10.manager.keyboard.KeyboardLayoutSaver;
import edu.dhbw.t10.manager.keyboard.KeymapLoader;
import edu.dhbw.t10.manager.profile.ImportExportManager;
import edu.dhbw.t10.type.Config;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.key.MuteButton;
import edu.dhbw.t10.type.tree.PriorityTree;


/**
 * 
 * Profile-Handle. It includes the name, the paths to its PriorityTree-/Profile-file,
 * as well as the PriorityTree itself.
 * 
 * @author SebastianN
 * 
 */
public class Profile_V2 implements Serializable {
	/**  */
	private static final long			serialVersionUID	= 5085464540715301878L;
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * name = profilename
	 * profile = path to profile config file
	 * layout = path to layout
	 * chars = String containing the allowed chars
	 * tree = path to the tree file
	 * autoCompleting = true/false
	 * treeExpanding = true/false
	 * autoProfileChange = true/false
	 */

	private Properties					properties			= new Properties();
	private transient InputStream		defaultLayoutXML;
	private transient InputStream		defaultKeymapXML;
	private transient PriorityTree	tree;
	private transient KeyboardLayout	kbdLayout;
	
	private boolean						dictionaryLoaded	= false;

	private static final Logger		logger				= Logger.getLogger(Profile_V2.class);
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * 
	 * Constructor of Profile.
	 * 
	 * @param pName - Name of the new profile
	 * @author SebastianN
	 */
	public Profile_V2(String pName, String datapath) {
		properties = createDefaultProperties(pName, datapath);
		load();
		save();
	}
	
	public Profile_V2(Properties prop, String datapath) {
		properties = prop;
		if (!properties.containsKey("name")) { //$NON-NLS-1$
			logger.error("Tried to load profile with invalid properties"); //$NON-NLS-1$
			throw new ExceptionInInitializerError();
		} else {
			// to prevent not set attributes
			properties = createDefaultProperties(properties.getProperty("name"), datapath); //$NON-NLS-1$
			for (Entry<Object, Object> p : prop.entrySet())
				properties.setProperty(prop.getProperty((String) p.getKey()), (String) p.getValue());
		}
		load();
	}



	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	public Properties createDefaultProperties(String pName, String datapath) {
		Properties p = new Properties();
		p.setProperty("name", pName); //$NON-NLS-1$
		p.setProperty("autoCompleting", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("treeExpanding", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("autoProfileChange", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("chars", Config.getConf().getProperty("defaultAllowedChars")); //$NON-NLS-1$ //$NON-NLS-2$
		
		String name = p.getProperty("name"); //$NON-NLS-1$
		File file = new File(datapath + "/profiles"); //$NON-NLS-1$
		if (!file.isDirectory()) {
			file.mkdir();
		}
		File profileDir = new File(datapath + "/profiles/" + name); //$NON-NLS-1$
		if (!profileDir.isDirectory()) {
			profileDir.mkdir();
		}
		p.setProperty("layout", datapath + "/profiles/" + name + "/" + name + ".layout"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		p.setProperty("profile", datapath + "/profiles/" + name + "/" + name + ".profile"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		p.setProperty("tree", datapath + "/profiles/" + name + "/" + name + ".tree"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		logger.debug("Profile " + name + " created"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return p;
	}


	public String toString() {
		return getName();
	}


	/**
	 * Load layout and tree from file
	 * 
	 * @author NicolaiO
	 */
	public void load() {
		load(true);
	}
	
	
	public void load(boolean loadTree) {
		loadDefaultPathes();
		loadLayout();
		if (loadTree)
			loadTree();
	}
	
	
	/**
	 * Save profile (layout and tree)
	 * 
	 * @author NicolaiO
	 */
	public void save() {
		saveLayout();
		saveTree();
		saveProfile();
	}
	
	
	private void saveProfile() {
		FileOutputStream fis;
		try {
			fis = new FileOutputStream(properties.getProperty("profile")); //$NON-NLS-1$
			properties.store(fis, "stored by saving the profile"); //$NON-NLS-1$
		} catch (IOException err) {
			logger.info("Could not save the profile"); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * Save layout to file
	 * 
	 * @author NicolaiO
	 */
	private void saveLayout() {
		if (kbdLayout != null) {
			KeyboardLayoutSaver.save(kbdLayout, properties.getProperty("layout")); //$NON-NLS-1$
		}
	}
	
	
	private void loadDefaultPathes() {
		defaultLayoutXML = getClass().getResourceAsStream("/res/default/layout_default.xml"); //$NON-NLS-1$
		defaultKeymapXML = getClass().getResourceAsStream("/res/default/keymap_default.xml"); //$NON-NLS-1$
		if (defaultLayoutXML == null || defaultKeymapXML == null) {
			logger.error("Could not load default layout file. Program will not run well..."); //$NON-NLS-1$
		}
	}


	/**
	 * load layout from layout file
	 * 
	 * @author NicolaiO
	 */
	private void loadLayout() {
		File file = new File(properties.getProperty("layout")); //$NON-NLS-1$
		if (file.exists()) {
			kbdLayout = KeyboardLayoutLoader.load(file, KeymapLoader.load(defaultKeymapXML));
		} else {
			logger.info("Default Layout loaded"); //$NON-NLS-1$
			kbdLayout = KeyboardLayoutLoader.load(defaultLayoutXML, KeymapLoader.load(defaultKeymapXML));
		}
		for (MuteButton mb : kbdLayout.getMuteButtons()) {
			switch (mb.getType()) {
				case MuteButton.AUTO_COMPLETING:
					mb.setActivated(properties.getProperty("autoCompleting").equals("true")); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case MuteButton.AUTO_PROFILE_CHANGE:
					mb.setActivated(properties.getProperty("autoProfileChange").equals("true")); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case MuteButton.TREE_EXPANDING:
					mb.setActivated(properties.getProperty("treeExpanding").equals("true")); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				default:
					break;
			}
		}
	}
	
	
	/**
	 * Loads the (serialized) PriorityTree.
	 * 
	 * @author DirkK
	 */
	private void loadTree() {
		tree = new PriorityTree();
		boolean successfullyCharsLoaded = tree.loadChars(properties.getProperty("chars")); //$NON-NLS-1$
		if (!successfullyCharsLoaded) {
			properties.setProperty("chars", Config.getConf().getProperty("defaultAllowedChars")); //$NON-NLS-1$ //$NON-NLS-2$
			tree.loadChars(Config.getConf().getProperty("defaultAllowedChars")); //$NON-NLS-1$
		}
		new Thread() {
			public void run() {
				try {
					tree.importFromHashMap(ImportExportManager.importFromFile(properties.getProperty("tree"), true)); //$NON-NLS-1$
				} catch (IOException err) {
					logger.warn("Could not fetch the dictionary for the proifle " + properties.getProperty("name") + ", File: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							+ properties.getProperty("tree")); //$NON-NLS-1$
				}
				logger.debug("Tree successfully loaded"); //$NON-NLS-1$
				dictionaryLoaded = true;
				Controller.getInstance().showStatusMessage(Messages.getString("Profile_V2.0")); //$NON-NLS-1$
			}
		}.start();
	}
	
	
	/**
	 * Saves the PriorityTree as serialized object
	 * 
	 * @author DirkK
	 */
	private void saveTree() {
		if (tree != null && dictionaryLoaded) {
			logger.debug("save tree to " + properties.getProperty("tree")); //$NON-NLS-1$ //$NON-NLS-2$
			final PriorityTree tempTree = tree.clone();
			new Thread() {
				public void run() {
					try {
						ImportExportManager.exportToFile(tempTree.exportToHashMap(), properties.getProperty("tree")); //$NON-NLS-1$
						} catch (IOException err) {
							logger.error("Not able to save the tree for proifle " + properties.getProperty("name") + " to " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									+ properties.getProperty("tree")); //$NON-NLS-1$
						}
				}
			}.start();
			logger.debug("save the allowed chars(" + properties.getProperty("chars") + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			logger.debug("Tree not saved, because not existend"); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * Controller requests a Word suggestion with an given Startstring.
	 * 
	 * @param givenChars
	 * @return wordsuggest
	 * @author DirkK
	 */
	public String getWordSuggest(String givenChars) {
		if (isAutoCompleting() && dictionaryLoaded) {
			if (getTree() == null) {
				logger.error("PriorityTree of activeProfile==NULL at getWordSuggest"); //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
			return getTree().getSuggest(givenChars);
		} else {
			return givenChars;
		}
	}
	
	
	/**
	 * Gives a word which have to be inserted or updated in the data.
	 * 
	 * @param word A complete word to be inserted into tree
	 * @author SebastianN
	 */
	public boolean acceptWord(String word) {
		word = StringHelper.removePunctuation(word);
		if (isTreeExpanding() && dictionaryLoaded)
			return getTree().insert(word);
		return false;
	}
	
	
	/**
	 * Load the lists of all ddls. (currently only one exists)
	 * Existing items will be removed!
	 * 
	 * @author NicolaiO
	 */
	public void loadDDLs(ArrayList<Profile_V2> profiles) {
		ArrayList<DropDownList> DDLs = getKbdLayout().getDdls();
		for (DropDownList ddl : DDLs) {
			switch (ddl.getType()) {
				case DropDownList.PROFILE:
					// save all action listeners
					ActionListener[] als = ddl.getActionListeners();
					// delete all action listeners, so that they can't be called until we are done
					// e.g. addItem will trigger an ActionEvent!
					for (int i = 0; i < als.length; i++) {
						ddl.removeActionListener(als[i]);
					}
					
					// remove all existing items (normally, there shouldn't be any...
					ddl.removeAllItems();
					
					// add all profiles
					for (Profile_V2 p : profiles) {
						ddl.addItem(p.getName());
					}
					
					// set active profile selected
					ddl.setSelectedItem(getName());
					
					logger.debug("loaded " + ddl.getItemCount() + " items in profile-ddl"); //$NON-NLS-1$ //$NON-NLS-2$
					logger.debug("Selected item is: " + ddl.getSelectedItem() + " should be: " + this); //$NON-NLS-1$ //$NON-NLS-2$
					
					// now, where we are done, add all listeners back
					for (int i = 0; i < als.length; i++) {
						ddl.addActionListener(als[i]);
					}
					
					// do a revalidate to reload the ddl
					ddl.revalidate();
					ddl.repaint();
					
					break;
				default:
					logger.warn("UNKOWN DDL found!"); //$NON-NLS-1$
			}
		}
	}
	
	
	public void unload() {
		tree = null;
		kbdLayout = null;
		dictionaryLoaded = false;
		logger.debug("Tree and Layout \"deleted\" from main memory"); //$NON-NLS-1$
	}


	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Gets a profile's name
	 * 
	 * @return name
	 * @author SebastianN
	 */
	public String getName() {
		return properties.getProperty("name"); //$NON-NLS-1$
	}
	
	
	/**
	 * Sets a profile's name
	 * 
	 * @param newName - String
	 * @author SebastianN
	 */
	public void setName(String newName) {
		properties.setProperty("name", newName); //$NON-NLS-1$
	}
	
	
	/**
	 * @return
	 * @author dirk
	 */
	public PriorityTree getTree() {
		return tree;
	}
	
	
	/**
	 * @author dirk
	 */
	public void setTree(PriorityTree tree) {
		this.tree = tree;
	}
	
	
	public KeyboardLayout getKbdLayout() {
		return kbdLayout;
	}
	
	
	public void setKbdLayout(KeyboardLayout kbdLayout) {
		this.kbdLayout = kbdLayout;
	}
	
	
	public boolean isAutoProfileChange() {
		return properties.getProperty("autoProfileChange").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	public void setAutoProfileChange(boolean autoProfileChange) {
		properties.setProperty("autoProfileChange", String.valueOf(autoProfileChange)); //$NON-NLS-1$
	}
	
	
	public boolean isAutoCompleting() {
		return properties.getProperty("autoCompleting").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	public void setAutoCompleting(boolean autoCompleting) {
		properties.setProperty("autoCompleting", String.valueOf(autoCompleting)); //$NON-NLS-1$
	}
	
	
	public boolean isTreeExpanding() {
		return properties.getProperty("treeExpanding").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	public void setTreeExpanding(boolean treeExpanding) {
		properties.setProperty("treeExpanding", String.valueOf(treeExpanding)); //$NON-NLS-1$
	}
	
	
	public Properties getProperties() {
		return properties;
	}
	
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	
	public HashMap<String, String> getPaths() {
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("profile", properties.getProperty("profile")); //$NON-NLS-1$ //$NON-NLS-2$
		hash.put("layout", properties.getProperty("layout")); //$NON-NLS-1$ //$NON-NLS-2$
		hash.put("tree", properties.getProperty("tree")); //$NON-NLS-1$ //$NON-NLS-2$
		return hash;
	}
	
	
	public void setAllowedChars(String allowedChars) {
		tree.loadChars(allowedChars);
		properties.setProperty("chars", allowedChars); //$NON-NLS-1$
	}
}
