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
import edu.dhbw.t10.type.keyboard.ButtonKey;
import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.KeyboardLayout;
import edu.dhbw.t10.type.keyboard.SingleKey;


/**
 * TODO NicolaiO, add comment!
 * - What should this type do (in one sentence)?
 * - If not intuitive: A simple example how to use this class
 * 
 * @author NicolaiO
 * 
 */
public class KeyboardLayoutLoader {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger		logger		= Logger.getLogger(KeyboardLayoutLoader.class);


	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	

	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	public static KeyboardLayout load(String filePath, HashMap<Integer, SingleKey> keymap) {
		KeyboardLayout kbdLayout = new KeyboardLayout(0, 0, 1);
		File layoutFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(layoutFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("key");
			ArrayList<ButtonKey> keys = new ArrayList<ButtonKey>();
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ButtonKey newKey = getKey(eElement);
					if (newKey != null) {
						keys.add(newKey);
						// TODO listener
						// newKey.addActionListener(keyListener);
						newKey.addActionListener(Controller.getInstance()); // use EventCollector as listener
					}
				} else {
					logger.warn("key-node is not an element-node");
				}
			}
			logger.debug("Loaded " + keys.size() + " keys.");
			
			int sizex = 0, sizey = 0;
			float scale = 1.0f;
			nList = doc.getElementsByTagName("sizex");
			if (nList.getLength() > 0)
				sizex = Integer.parseInt(nList.item(0).getTextContent());
			nList = doc.getElementsByTagName("sizey");
			if (nList.getLength() > 0)
				sizey = Integer.parseInt(nList.item(0).getTextContent());
			nList = doc.getElementsByTagName("scale");
			if (nList.getLength() > 0)
				scale = Float.parseFloat(nList.item(0).getTextContent());
			kbdLayout = new KeyboardLayout(sizex, sizey, scale);
			
			nList = doc.getElementsByTagName("font");
			String fname = "";
			int fstyle = 0, fsize = 0;
			if (nList.getLength() > 0) {
				NodeList font = nList.item(0).getChildNodes();
				for (int i = 0; i < font.getLength(); i++) {
					Node n = font.item(i);
					if (n.getNodeName() == "name") {
						fname = n.getTextContent();
					} else if (n.getNodeName() == "style") {
						try {
							fstyle = Integer.parseInt(n.getTextContent());
						} catch (NumberFormatException e) {
						}
					} else if (n.getNodeName() == "size") {
						try {
							fsize = Integer.parseInt(n.getTextContent());
						} catch (NumberFormatException e) {
						}
					}
				}
			}
			
			nList = doc.getElementsByTagName("dropdown");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				try {
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						NamedNodeMap attr = eElement.getAttributes();
						DropDownList cb = new DropDownList(getAttribute(attr, "type"), getIntAttribute(attr, "size_x"),
								getIntAttribute(attr, "size_y"), getIntAttribute(attr, "pos_x"), getIntAttribute(attr, "pos_y"));
						kbdLayout.addDdl(cb);
						// TODO listener
					}
				} catch (NullPointerException e) {
					logger.warn("Dropdown-element found, but can not be read correctly! node nr " + temp + ": "
							+ nNode.toString());
				}
			}
			
			
			kbdLayout.setFont(new Font(fname, fstyle, fsize));
			kbdLayout.setKeys(keys);
			kbdLayout.setMode("default");
			kbdLayout.rescale();
			logger.info("loaded " + keys.size() + " Buttonkeys.");
		} catch (ParserConfigurationException err) {
			logger.error("Could not initialize dBuilder");
			err.printStackTrace();
		} catch (SAXException err) {
			logger.error("Could not parse document");
			err.printStackTrace();
		} catch (IOException err) {
			logger.error("Could not parse document");
			err.printStackTrace();
		}
		return kbdLayout;
	}
	
	
	/**
	 * Return Key-Object from given element
	 * 
	 * @param eElement must be a <key> node
	 * @return Key
	 */
	private static ButtonKey getKey(Element eElement, HashMap<Integer, SingleKey> keymap) {
		try {
			NamedNodeMap attr = eElement.getAttributes();
			ButtonKey key = new ButtonKey(getIntAttribute(attr, "size_x"), getIntAttribute(attr, "size_y"), getIntAttribute(attr,
					"pos_x"), getIntAttribute(attr, "pos_y"));
			
			// Modes
			NodeList modes = eElement.getElementsByTagName("mode");
			for (int i = 0; i < modes.getLength(); i++) {
				Node item = modes.item(i);
				if (item != null) {
					String sModeName = "";
					String sColor = "";
					Node modeName = item.getAttributes().getNamedItem("name");
					Node color = item.getAttributes().getNamedItem("color");
					if (modeName != null) {
						sModeName = modeName.getTextContent();
					}
					if (color != null) {
						sColor = color.getTextContent();
					}
					key.addMode(sModeName, item.getTextContent(), keymap.get(keycode), sColor);
				}
			}
			
			return key;
		} catch (NullPointerException e) {
			System.out.println("In getKey:");
			e.printStackTrace();
		}
		return new ButtonKey();
	}
	

	private static String getAttribute(NamedNodeMap attr, String name) throws NullPointerException {
		Node node = attr.getNamedItem(name);
		if (node != null) {
			return node.getTextContent();
		}
		return "";
	}
	

	private static int getIntAttribute(NamedNodeMap attr, String name) throws NullPointerException {
		try {
			int value = Integer.parseInt(getAttribute(attr, name));
			return value;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
