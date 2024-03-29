/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 26, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager.keyboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.dhbw.t10.type.keyboard.key.Key;


/**
 * This is a loader class for a keymap. This is a static class with only a load method for getting a hashmap from a
 * given path to an keymap file
 * 
 * @author NicolaiO
 * 
 */
public class KeymapLoader {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(KeymapLoader.class);
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * do not call me... I'm a static class...
	 */
	private KeymapLoader() {
		throw new AssertionError();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * Load a keymap file.
	 * 1. initialize the builder and open XML file
	 * 2. loop over all keytypes (control, unicode, char)
	 * 2.1. read keytype name
	 * 3. for each keytype loop over all keys
	 * 3.1. read id and keycode from key
	 * 3.2 save key in keymap
	 * 
	 * @param filePath to an keymap XML file
	 * @return HashMap with id->SingleKey
	 * @author NicolaiO
	 */
	public static HashMap<Integer, Key> load(InputStream filePath) {
		logger.debug("Loadung keymap..."); //$NON-NLS-1$
		// do everything in a try and return only empty keymap, if XML could not be loaded
		try {
			// load file and initialize document builder factory for XML parsing
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			// create keymap that will be returned
			HashMap<Integer, Key> keymap = new HashMap<Integer, Key>();
			NodeList keytypes = doc.getElementsByTagName("keytype"); //$NON-NLS-1$
			int type = Key.UNKNOWN;
			
			// loop over all keytype like "control", "char", "unicode"
			for (int i = 0; i < keytypes.getLength(); i++) {
				NodeList keys = keytypes.item(i).getChildNodes();
				try {
					// get name of keytype
					String stype = keytypes.item(i).getAttributes().getNamedItem("name").getTextContent(); //$NON-NLS-1$
					type = convertType(stype);
				} catch (NullPointerException e) {
					logger.warn("A keytype could not be read. i=" + i); //$NON-NLS-1$
					type = Key.UNKNOWN;
				}
				// loop over all keys in keytype
				for (int j = 0; j < keys.getLength(); j++) {
					Node key = keys.item(j);
					try {
						if (key.getNodeName().equals("key")) { //$NON-NLS-1$
							int id = Integer.parseInt(key.getAttributes().getNamedItem("id").getTextContent()); //$NON-NLS-1$
							String keycode = key.getAttributes().getNamedItem("keycode").getTextContent(); //$NON-NLS-1$
							String iconUrl = ""; //$NON-NLS-1$
							if (key.getAttributes().getNamedItem("icon") != null) //$NON-NLS-1$
								iconUrl = key.getAttributes().getNamedItem("icon").getTextContent(); //$NON-NLS-1$
							String holdiconUrl = ""; //$NON-NLS-1$
							if (key.getAttributes().getNamedItem("holdicon") != null) { //$NON-NLS-1$
								holdiconUrl = key.getAttributes().getNamedItem("holdicon").getTextContent(); //$NON-NLS-1$
							}
							String name = key.getTextContent();
							// save key in keymap
							Key newKey = new Key(id, name, keycode, type, false, iconUrl, holdiconUrl);
							// if (!holdiconUrl.equals("")) {
							// newKey.setHoldIcon(holdiconUrl);
							// logger.warn("a" + holdiconUrl + "b");
							// }
							keymap.put(id, newKey);
						}
					} catch (NullPointerException e) {
						logger.warn("A key in keymap could not be read. j=" + j + " i=" + i); //$NON-NLS-1$ //$NON-NLS-2$
						e.printStackTrace();
					} catch (NumberFormatException e) {
						logger.warn("A key in keymap had a bad number format. j=" + j + " i=" + i); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
			logger.info("loaded " + keymap.size() + " keys."); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("Keymap loaded."); //$NON-NLS-1$
			return keymap;
		} catch (ParserConfigurationException err) {
			logger.error("Could not initialize dBuilder"); //$NON-NLS-1$
			err.printStackTrace();
		} catch (SAXException err) {
			logger.error("Could not parse document"); //$NON-NLS-1$
			err.printStackTrace();
		} catch (IOException err) {
			logger.error("Could not parse document. IOException with path " + filePath.toString()); //$NON-NLS-1$
		}
		logger.warn("Loading Keymap not finished."); //$NON-NLS-1$
		return new HashMap<Integer, Key>();
	}
	
	
	/**
	 * Convert type from String to occording int
	 * 
	 * @param stype like "control", "char", "unicode"
	 * @return keytype like Key.CONTROL etc.
	 * @author NicolaiO
	 */
	private static int convertType(String stype) {
		if (stype.equals("control")) //$NON-NLS-1$
			return Key.CONTROL;
		else if (stype.equals("char")) //$NON-NLS-1$
			return Key.CHAR;
		else if (stype.equals("unicode")) //$NON-NLS-1$
			return Key.UNICODE;
		return Key.UNKNOWN;
	}
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
