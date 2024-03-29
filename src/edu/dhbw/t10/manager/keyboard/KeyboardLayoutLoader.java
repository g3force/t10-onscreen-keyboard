/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 20, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager.keyboard;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.Image;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.key.Button;
import edu.dhbw.t10.type.keyboard.key.Key;
import edu.dhbw.t10.type.keyboard.key.ModeButton;
import edu.dhbw.t10.type.keyboard.key.ModeKey;
import edu.dhbw.t10.type.keyboard.key.MuteButton;


/**
 * This class loads a layout file. It needs a keymap to map the buttons to their according keys!
 * 
 * @author NicolaiO
 * 
 */
public class KeyboardLayoutLoader {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger				logger	= Logger.getLogger(KeyboardLayoutLoader.class);
	private static HashMap<Integer, Key>	keymap;
	private static Document						doc;
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * do not call me... I'm a static class...
	 */
	private KeyboardLayoutLoader() {
		throw new AssertionError();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Load KeyboardLayout file given InputStream
	 * 
	 * @param filePath to an keyboard layout XML file
	 * @param keymap that was loaded from file
	 * @return
	 * @author NicolaiO
	 */
	public static KeyboardLayout load(InputStream filePath, HashMap<Integer, Key> _keymap) {
		logger.debug("Loading KeyboardLayout..."); //$NON-NLS-1$
		KeyboardLayout kbdLayout = new KeyboardLayout(0, 0, 1, 1, 1);
		DocumentBuilder dBuilder;
		keymap = _keymap;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// initialize document reader
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(filePath);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException err) {
			logger.error("Could not initialize dBuilder"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		} catch (SAXException err) {
			logger.error("Could not parse document"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		} catch (IOException err) {
			logger.error("Could not parse document"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		}
		return load(kbdLayout);
	}
	
	
	/**
	 * Load KeyboardLayout with given File
	 * 
	 * @param filePath to an keyboard layout XML file
	 * @param keymap that was loaded from file
	 * @return
	 * @author NicolaiO
	 */
	public static KeyboardLayout load(File filePath, HashMap<Integer, Key> _keymap) {
		logger.debug("Loading KeyboardLayout..."); //$NON-NLS-1$
		KeyboardLayout kbdLayout = new KeyboardLayout(0, 0, 1, 1, 1);
		DocumentBuilder dBuilder;
		keymap = _keymap;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// initialize document reader
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(filePath);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException err) {
			logger.error("Could not initialize dBuilder"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		} catch (SAXException err) {
			logger.error("Could not parse document"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		} catch (IOException err) {
			logger.error("Could not parse document"); //$NON-NLS-1$
			err.printStackTrace();
			return kbdLayout;
		}
		return load(kbdLayout);
	}


	/**
	 * Load a keyboardLayout
	 * 1. read ModeButtons
	 * 2. read Buttons
	 * 3. read MuteButtons
	 * 4. read default config for layout
	 * 5. read DropDownLists
	 * 6. add everything to keyboardlayout
	 * 
	 * @return KeyboardLayout
	 * @author NicolaiO
	 */
	private static KeyboardLayout load(KeyboardLayout kbdLayout) {
		NodeList nList;
		
		// ########################## read ModeButtons ########################
		ArrayList<ModeKey> modeKeys = new ArrayList<ModeKey>();
		ArrayList<ModeButton> modeButtons = getModeButtons(modeKeys);
		logger.info("loaded " + modeButtons.size() + " ModeButtons."); //$NON-NLS-1$ //$NON-NLS-2$
		logger.info("loaded " + modeKeys.size() + " ModeKeys."); //$NON-NLS-1$ //$NON-NLS-2$
		// ########################## read Buttons ############################
		ArrayList<Button> buttons = getButtons(modeKeys);
		logger.info("loaded " + buttons.size() + " Buttons."); //$NON-NLS-1$ //$NON-NLS-2$
		// ########################## read MuteButtons ###########################
		ArrayList<MuteButton> muteButtons = getMuteButtons();
		logger.info("loaded " + muteButtons.size() + " MuteButtons."); //$NON-NLS-1$ //$NON-NLS-2$
		// ########################## read DDLs ###########################
		ArrayList<DropDownList> ddls = getDdls();
		logger.info("loaded " + ddls.size() + " DropDownLists."); //$NON-NLS-1$ //$NON-NLS-2$
		// ########################## read Images ###########################
		ArrayList<Image> images = getImages();
		logger.info("loaded " + images.size() + " Images."); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		// read default sizes and scale of layout
		int sizex = 0, sizey = 0;
		float scalex = 1.0f, scaley = 1.0f, scale_font = 1.0f;
		
		sizex = getIntFromNode("sizex", 1010); //$NON-NLS-1$
		sizey = getIntFromNode("sizey", 335); //$NON-NLS-1$
		scalex = getFloatFromNode("scalex", 1f); //$NON-NLS-1$
		scaley = getFloatFromNode("scaley", 1f); //$NON-NLS-1$
		scale_font = getFloatFromNode("scale_font", 1f); //$NON-NLS-1$
		
		kbdLayout = new KeyboardLayout(sizex, sizey, scalex, scaley, scale_font);
		
		// read font (especially for size!)
		nList = doc.getElementsByTagName("font"); //$NON-NLS-1$
		String fname = ""; //$NON-NLS-1$
		int fstyle = 0, fsize = 0;
		if (nList.getLength() > 0) {
			NodeList font = nList.item(0).getChildNodes();
			for (int i = 0; i < font.getLength(); i++) {
				Node n = font.item(i);
				if (n.getNodeName() == "name") { //$NON-NLS-1$
					fname = n.getTextContent();
				} else if (n.getNodeName() == "style") { //$NON-NLS-1$
					try {
						fstyle = Integer.parseInt(n.getTextContent());
					} catch (NumberFormatException e) {
						logger.warn("Could not read global style value: " + n.getTextContent()); //$NON-NLS-1$
					}
				} else if (n.getNodeName() == "size") { //$NON-NLS-1$
					try {
						fsize = Integer.parseInt(n.getTextContent());
					} catch (NumberFormatException e) {
						logger.warn("Could not read global size value"); //$NON-NLS-1$
					}
				}
			}
		}
		
		// add everything to layout and rescale layout to set button sizes correctly (scale!)
		kbdLayout.setModeKeys(modeKeys);
		kbdLayout.setButtons(buttons);
		kbdLayout.setModeButtons(modeButtons);
		kbdLayout.setMuteButtons(muteButtons);
		kbdLayout.setImages(images);
		kbdLayout.setDdls(ddls);
		kbdLayout.setFont(new Font(fname, fstyle, fsize));
		kbdLayout.rescale();
		
		logger.debug("keyboard Layout loaded."); //$NON-NLS-1$
		return kbdLayout;
	}
	
	
	/**
	 * ddl = Drop Down List
	 * load all drop down lists from file...
	 * 
	 * @return ArrayList of all ddls
	 * @author NicolaiO
	 */
	private static ArrayList<DropDownList> getDdls() {
		ArrayList<DropDownList> ddls = new ArrayList<DropDownList>();
		// read dropdown lists
		NodeList nList = doc.getElementsByTagName("dropdown"); //$NON-NLS-1$
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			try {
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					NamedNodeMap attr = eElement.getAttributes();
					DropDownList cb = new DropDownList(getAttribute(attr, "type"), getIntAttribute(attr, "size_x"), //$NON-NLS-1$ //$NON-NLS-2$
							getIntAttribute(attr, "size_y"), getIntAttribute(attr, "pos_x"), getIntAttribute(attr, "pos_y")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					ddls.add(cb);
					cb.addActionListener(Controller.getInstance());
				}
			} catch (NullPointerException e) {
				logger.warn("Dropdown-element found, but can not be read correctly! node nr " + temp + ": " //$NON-NLS-1$ //$NON-NLS-2$
						+ nNode.toString());
			}
		}
		return ddls;
	}
	
	
	/**
	 * Loads all images...
	 * 
	 * @return
	 * @author NicolaiO
	 */
	private static ArrayList<Image> getImages() {
		ArrayList<Image> images = new ArrayList<Image>();
		// read image lists
		NodeList nList = doc.getElementsByTagName("image"); //$NON-NLS-1$
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			try {
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					NamedNodeMap attr = eElement.getAttributes();
					Image cb = new Image(getAttribute(attr, "src"), getIntAttribute(attr, "size_x"), //$NON-NLS-1$ //$NON-NLS-2$
							getIntAttribute(attr, "size_y"), getIntAttribute(attr, "pos_x"), getIntAttribute(attr, "pos_y")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					images.add(cb);
				}
			} catch (NullPointerException e) {
				logger.warn("Dropdown-element found, but can not be read correctly! node nr " + temp + ": " //$NON-NLS-1$ //$NON-NLS-2$
						+ nNode.toString());
			}
		}
		return images;
	}


	/**
	 * Get all Buttons. This are those Buttons on the keyboard, that are neither ModeButtons nor MuteButtons.
	 * All Buttons are saved in a list that will be returned.
	 * A button looks like this:
	 * 
	 * <button size_x="50" size_y="50" pos_x="390" pos_y="60">
	 * <key>22</key>
	 * <mode modename="1001">36</mode>
	 * <mode modename="1007">45</mode>
	 * </button>
	 * 
	 * where key is the default key and mode is a key that is used with the occording mode. The mode numbers are
	 * references to the mode keys
	 * 
	 * @param doc document
	 * @param modeKeys available modebuttons, which are needed for reference in button-modes
	 * @return list of all buttons
	 * @author NicolaiO
	 */
	private static ArrayList<Button> getButtons(ArrayList<ModeKey> modeKeys) {
		ArrayList<Button> buttons = new ArrayList<Button>();
		NodeList nList = doc.getElementsByTagName("button"); //$NON-NLS-1$
		
		// loop through buttons
		for (int temp = 0; temp < nList.getLength(); temp++) {
			try {
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() != Node.ELEMENT_NODE) {
					logger.warn("key-node is not an element-node"); //$NON-NLS-1$
					continue;
				}
				
				Element eElement = (Element) nNode;
				Bounds b = getBounds(nNode);
				Button button = new Button(b.size_x, b.size_y, b.pos_x, b.pos_y);
				
				// receive default key
				NodeList defkey = eElement.getElementsByTagName("key"); //$NON-NLS-1$
				if (defkey.getLength() == 1) {
					try {
						int id = Integer.parseInt(defkey.item(0).getTextContent());
						Key key = keymap.get(id).clone();
						if (key == null) {
							logger.warn("key not found in keymap. temp=" + temp + " id=" + id); //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
						Node nAccept = defkey.item(0).getAttributes().getNamedItem("accept"); //$NON-NLS-1$
						if (nAccept != null && nAccept.getTextContent().equals("true")) { //$NON-NLS-1$
							key.setAccept(true);
						}
						button.setKey(key);
					} catch (NumberFormatException e) {
						logger.warn("key id could not be parsed to Integer. id=" + defkey.item(0).getTextContent()); //$NON-NLS-1$
					}
					
				} else {
					logger.warn("Number of key-elements is not 1: " + defkey.getLength()); //$NON-NLS-1$
					continue;
				}
				
				button.addActionListener(Controller.getInstance()); // use EventCollector as listener
				button.addMouseListener(Controller.getInstance().getButtonMouseListener());
				buttons.add(button);
				
				// receive Modes
				NodeList modes = eElement.getElementsByTagName("mode"); //$NON-NLS-1$
				for (int i = 0; i < modes.getLength(); i++) {
					Node item = modes.item(i);
					if (item != null) {
						int iModeName = 0;
						boolean accept = false;
						Node modeName = item.getAttributes().getNamedItem("modename"); //$NON-NLS-1$
						Node nAccept = item.getAttributes().getNamedItem("accept"); //$NON-NLS-1$
						if (modeName != null) {
							try {
								iModeName = Integer.parseInt(modeName.getTextContent());
							} catch (NumberFormatException e) {
								logger.warn("modename could not be parsed to Integer. modename=" + modeName.getTextContent() //$NON-NLS-1$
										+ "i=" + i); //$NON-NLS-1$
							}
						}
						if (nAccept != null && nAccept.getTextContent().equals("true")) { //$NON-NLS-1$
							accept = true;
						}
						try {
							Key key = keymap.get(Integer.parseInt(item.getTextContent()));
							if (key == null) {
								logger.warn("key not found in keymap. key content=" + item.getTextContent()); //$NON-NLS-1$
							}
							key.setAccept(accept);
							for (ModeKey mb : modeKeys) {
								if (mb.getId() == iModeName) {
									button.addMode(mb, key);
								}
							}
							if (button.getModes().size() == 0) {
								logger.warn("A modeButton could not be found in keymap: " + iModeName + " i=" + i + "key=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										+ key.getName());
							}
							
						} catch (NumberFormatException e) {
							logger.warn("Could not parse key to Integer. i=" + i); //$NON-NLS-1$
						}
					}
				}
			} catch (NullPointerException e) {
				logger.warn("A Button could not be read: NullPointerException"); //$NON-NLS-1$
			} catch (NumberFormatException e) {
				logger.warn("A Button could not be read: NumberFormatException"); //$NON-NLS-1$
			}
		}
		return buttons;
	}
	
	
	/**
	 * Get all ModeButtons (Buttons on GUI, like Shift, Alt, etc.) and all ModeKeys (the different Modes, like Shift,
	 * Alt, etc.)
	 * 
	 * @param modeKeys (empty) modeKey list to be filled (because return is reserved for ModeButtons)
	 * @return list of ModeButtons
	 * @author NicolaiO
	 */
	private static ArrayList<ModeButton> getModeButtons(ArrayList<ModeKey> modeKeys) {
		ArrayList<ModeButton> modeButtons = new ArrayList<ModeButton>();
		NodeList nList = doc.getElementsByTagName("modebutton"); //$NON-NLS-1$
		
		// loop through buttons
		for (int temp = 0; temp < nList.getLength(); temp++) {
			try {
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() != Node.ELEMENT_NODE) {
					logger.warn("key-node is not an element-node"); //$NON-NLS-1$
					continue;
				}
				
				Element eElement = (Element) nNode;
				ModeKey modeKey = null;
				// receive default key
				NodeList defkey = eElement.getElementsByTagName("key"); //$NON-NLS-1$
				if (defkey.getLength() == 1) {
					try {
						Key tempModeKey = keymap.get(Integer.parseInt(defkey.item(0).getTextContent()));
						boolean exists = false;
						for (ModeKey mk : modeKeys) {
							if (mk.getName().equals(tempModeKey.getName())) {
								exists = true;
								modeKey = mk;
								break;
							}
						}
						if (!exists) {
							modeKey = new ModeKey(tempModeKey);
							modeKeys.add(modeKey);
							logger.trace("New ModeKey found: " + modeKey.getName()); //$NON-NLS-1$
						}
					} catch (NumberFormatException e) {
						logger.warn("Could not parse key: " + defkey.item(0).getTextContent()); //$NON-NLS-1$
					}
					if (modeKey == null) {
						logger.warn("Could not find key in keymap: " + defkey.item(0).getTextContent()); //$NON-NLS-1$
						continue;
					}
				} else {
					logger.warn("Number of key-elements is not 1: " + defkey.getLength()); //$NON-NLS-1$
					continue;
				}
				Bounds b = getBounds(nNode);
				ModeButton modeButton = new ModeButton(modeKey, b.size_x, b.size_y, b.pos_x, b.pos_y);
				modeButton.addActionListener(Controller.getInstance());
				modeButtons.add(modeButton);
			} catch (NullPointerException e) {
				logger.warn("A ModeButton could not be read."); //$NON-NLS-1$
			}
		}
		return modeButtons;
	}
	
	
	/**
	 * Get all MuteButtons. This are buttons for enabling or disabling certain functions, like saving into dictionary.
	 * 
	 * @return list of MuteButtons
	 * @author NicolaiO
	 */
	private static ArrayList<MuteButton> getMuteButtons() {
		ArrayList<MuteButton> muteButtons = new ArrayList<MuteButton>();
		NodeList nList = doc.getElementsByTagName("mutebutton"); //$NON-NLS-1$
		
		// loop through buttons
		for (int temp = 0; temp < nList.getLength(); temp++) {
			try {
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() != Node.ELEMENT_NODE) {
					logger.warn("key-node is not an element-node"); //$NON-NLS-1$
					continue;
				}
				
				Element eElement = (Element) nNode;
				NamedNodeMap attr = eElement.getAttributes();
				int type = MuteButton.UNKNOWN;
				Bounds b = getBounds(nNode);
				MuteButton button = new MuteButton(b.size_x, b.size_y, b.pos_x, b.pos_y);
				
				try {
					String ttype = getAttribute(attr, "type"); //$NON-NLS-1$
					if (ttype.equals("auto_completing")) { //$NON-NLS-1$
						type = MuteButton.AUTO_COMPLETING;
					} else if (ttype.equals("auto_profile_change")) { //$NON-NLS-1$
						type = MuteButton.AUTO_PROFILE_CHANGE;
					} else if (ttype.equals("tree_expanding")) { //$NON-NLS-1$
						type = MuteButton.TREE_EXPANDING;
					} else {
						type = MuteButton.UNKNOWN;
					}
					button.setType(type);
				} catch (NullPointerException e) {
					logger.warn("type-attribute not found/invalid in MuteButton. temp=" + temp); //$NON-NLS-1$
					continue;
				}
				
				
				// following blocks are nearly equal... Not very nice implemented, but working
				NodeList on = eElement.getElementsByTagName("on"); //$NON-NLS-1$
				if (on.getLength() == 1) {
					String name = on.item(0).getTextContent();
					if (name != null) {
						button.getModeOn().setName(name);
					}
					try {
						String color = on.item(0).getAttributes().getNamedItem("color").getTextContent(); //$NON-NLS-1$
						button.getModeOn().setColor(color);
						String tooltip = on.item(0).getAttributes().getNamedItem("tooltip").getTextContent(); //$NON-NLS-1$
						button.getModeOn().setTooltip(tooltip);
					} catch (NullPointerException e) {
						logger.info("No color found/specified"); //$NON-NLS-1$
					}
				} else {
					logger.warn("Number of on-elements is not 1: " + on.getLength()); //$NON-NLS-1$
					continue;
				}
				
				NodeList off = eElement.getElementsByTagName("off"); //$NON-NLS-1$
				if (off.getLength() == 1) {
					String name = off.item(0).getTextContent();
					if (name != null) {
						button.getModeOff().setName(name);
					}
					try {
						String color = off.item(0).getAttributes().getNamedItem("color").getTextContent(); //$NON-NLS-1$
						button.getModeOff().setColor(color);
						String tooltip = off.item(0).getAttributes().getNamedItem("tooltip").getTextContent(); //$NON-NLS-1$
						button.getModeOff().setTooltip(tooltip);
					} catch (NullPointerException e) {
						logger.info("No color found/specified"); //$NON-NLS-1$
					}
				} else {
					logger.warn("Number of on-elements is not 1: " + off.getLength()); //$NON-NLS-1$
					continue;
				}
				
				button.addActionListener(Controller.getInstance());
				button.release();
				muteButtons.add(button);
			} catch (NullPointerException e) {
				logger.warn("A ModeButton could not be read."); //$NON-NLS-1$
			}
		}
		return muteButtons;
	}
	
	
	/**
	 * Get size and position (bound) from a given node.
	 * 
	 * @param node node, containing the attributes for size and position
	 * @return Bounds of Component in node (0-bounds, if node could not be read)
	 * @author NicolaiO
	 */
	private static Bounds getBounds(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			try {
				Element eElement = (Element) node;
				NamedNodeMap attr = eElement.getAttributes();
				return new Bounds(getIntAttribute(attr, "size_x"), getIntAttribute(attr, "size_y"), getIntAttribute(attr, //$NON-NLS-1$ //$NON-NLS-2$
						"pos_x"), getIntAttribute(attr, "pos_y")); //$NON-NLS-1$ //$NON-NLS-2$
				
			} catch (NullPointerException e) {
				logger.warn("Could not read bounds from given node. Node: " + node); //$NON-NLS-1$
			}
		}
		return new Bounds(0, 0, 0, 0);
	}
	
	
	/**
	 * Helper function for receiving an attribute by name
	 * 
	 * @param attr
	 * @param name
	 * @return
	 * @throws NullPointerException
	 * @author NicolaiO
	 */
	private static String getAttribute(NamedNodeMap attr, String name) throws NullPointerException {
		Node node = attr.getNamedItem(name);
		if (node != null) {
			return node.getTextContent();
		}
		return ""; //$NON-NLS-1$
	}
	
	
	/**
	 * Helper function for receiving an attribute by name, returning as int
	 * 
	 * @param attr
	 * @param name
	 * @return
	 * @throws NullPointerException
	 * @author NicolaiO
	 */
	private static int getIntAttribute(NamedNodeMap attr, String name) throws NullPointerException {
		try {
			int value = Integer.parseInt(getAttribute(attr, name));
			return value;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	
	/**
	 * Returns the value of the first node with given name in the xml doc
	 * 
	 * @param nodename to search for
	 * @param defaultValue value to be returned, if no value could be found or parsed
	 * @return value of first node as int
	 * @author NicolaiO
	 */
	private static int getIntFromNode(String nodename, int defaultValue) {
		NodeList nList = doc.getElementsByTagName(nodename);
		try {
			if (nList.getLength() > 0) {
				return Integer.parseInt(nList.item(0).getTextContent());
			}
		} catch (NumberFormatException e) {
			logger.warn("The number value of \"" + nodename + "\" could not be parsed."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultValue;
	}
	
	
	/**
	 * Same as getIntFromNode, but returning a float
	 * 
	 * @param nodename to search for
	 * @param defaultValue value to be returned, if no value could be found or parsed
	 * @return value of first node as float
	 * @author NicolaiO
	 */
	private static float getFloatFromNode(String nodename, float defaultValue) {
		NodeList nList = doc.getElementsByTagName(nodename);
		try {
			if (nList.getLength() > 0) {
				return Float.parseFloat(nList.item(0).getTextContent());
			}
		} catch (NumberFormatException e) {
			logger.warn("The number value of \"" + nodename + "\" could not be parsed."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultValue;
	}
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- sub-classes ----------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Bounds describe the size and position of a Component on the GUI, such as Buttons or DDLs
	 * 
	 * @author NicolaiO
	 * 
	 */
	private static class Bounds {
		public int	size_x, size_y, pos_x, pos_y;
		
		
		/**
		 * Create new Bounds with given size and position
		 * 
		 * @param size_x
		 * @param size_y
		 * @param pos_x
		 * @param pos_y
		 * @author NicolaiO
		 */
		public Bounds(int size_x, int size_y, int pos_x, int pos_y) {
			this.size_x = size_x;
			this.size_y = size_y;
			this.pos_x = pos_x;
			this.pos_y = pos_y;
		}
	}
}
