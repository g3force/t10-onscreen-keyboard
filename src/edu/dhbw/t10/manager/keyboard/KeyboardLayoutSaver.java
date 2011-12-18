/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 29, 2011
 * Author(s): DirkK
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager.keyboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.dhbw.t10.type.keyboard.DropDownList;
import edu.dhbw.t10.type.keyboard.ILayoutElement;
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
 */
public class KeyboardLayoutSaver {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(KeyboardLayoutSaver.class);
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * This is a static class and should not be instantiated
	 * 
	 * @author DirkK
	 */
	private KeyboardLayoutSaver() {
		throw new AssertionError();
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Save the keyboardLayout
	 * 
	 * @param kbdLayout the layout which shall be converted to a xml file
	 * @param filePath to an keymap XML file
	 * 
	 * @author DirkK
	 */
	public static void save(KeyboardLayout kbdLayout, String filePath) {
		logger.info("Starting to save the KeyboardLayout to XML"); //$NON-NLS-1$
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element layout = doc.createElement("layout"); //$NON-NLS-1$
			doc.appendChild(layout);
			// ---------------HEADER-----------------
			Element sizex = doc.createElement("sizex"); //$NON-NLS-1$
			Text text = doc.createTextNode(kbdLayout.getOrigSize_x() + ""); //$NON-NLS-1$
			sizex.appendChild(text);
			layout.appendChild(sizex);
			
			Element sizey = doc.createElement("sizey"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getOrigSize_y() + ""); //$NON-NLS-1$
			sizey.appendChild(text);
			layout.appendChild(sizey);
			
			Element scalex = doc.createElement("scalex"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getScale_x() + ""); //$NON-NLS-1$
			scalex.appendChild(text);
			layout.appendChild(scalex);
			
			Element scaley = doc.createElement("scaley"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getScale_y() + ""); //$NON-NLS-1$
			scaley.appendChild(text);
			layout.appendChild(scaley);
			
			Element scale_font = doc.createElement("scale_font"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getScale_font() + ""); //$NON-NLS-1$
			scale_font.appendChild(text);
			layout.appendChild(scale_font);
			
			Element font = doc.createElement("font"); //$NON-NLS-1$
			layout.appendChild(font);
			
			Element name = doc.createElement("name"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getFont().getFontName() + ""); //$NON-NLS-1$
			name.appendChild(text);
			font.appendChild(name);
			
			Element style = doc.createElement("style"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getFont().getStyle() + ""); //$NON-NLS-1$
			style.appendChild(text);
			font.appendChild(style);
			
			Element size = doc.createElement("size"); //$NON-NLS-1$
			text = doc.createTextNode(kbdLayout.getFont().getSize() + ""); //$NON-NLS-1$
			size.appendChild(text);
			font.appendChild(size);
			
			// -----------------IMAGES------------------
			
			for (Image image : kbdLayout.getImages()) {
				Element imageEl = doc.createElement("image"); //$NON-NLS-1$
				imageEl.setAttribute("src", image.getSrc()); //$NON-NLS-1$
				setSizeOfElement(imageEl, image);
				// imageEl.setAttribute("size_x", ((int) image.getOrigSize().getWidth()) + "");
				// imageEl.setAttribute("size_y", ((int) image.getOrigSize().getHeight()) + "");
				// imageEl.setAttribute("pos_x", image.getPos_x() + "");
				// imageEl.setAttribute("pos_y", image.getPos_y() + "");
				layout.appendChild(imageEl);
			}
			// ---------------DROPDOWN-----------------
			for (DropDownList dd : kbdLayout.getDdls()) {
				Element dropdown = doc.createElement("dropdown"); //$NON-NLS-1$
				dropdown.setAttribute("type", dd.getTypeAsString()); //$NON-NLS-1$
				setSizeOfElement(dropdown, dd);
				// dropdown.setAttribute("size_x", ((int) dd.getOrigSize().getWidth()) + "");
				// dropdown.setAttribute("size_y", ((int) dd.getOrigSize().getHeight()) + "");
				// dropdown.setAttribute("pos_x", dd.getPos_x() + "");
				// dropdown.setAttribute("pos_y", dd.getPos_y() + "");
				layout.appendChild(dropdown);
			}
			// ---------------MUTEBUTTONS-----------------
			for (MuteButton muteButton : kbdLayout.getMuteButtons()) {
				Element muteButtonEl = doc.createElement("mutebutton"); //$NON-NLS-1$
				setSizeOfElement(muteButtonEl, muteButton);
				switch (muteButton.getType()) {
					case MuteButton.AUTO_COMPLETING:
						muteButtonEl.setAttribute("type", "auto_completing"); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					case MuteButton.AUTO_PROFILE_CHANGE:
						muteButtonEl.setAttribute("type", "auto_profile_change"); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					case MuteButton.TREE_EXPANDING:
						muteButtonEl.setAttribute("type", "tree_expanding"); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					default:
						muteButtonEl.setAttribute("type", "unknown"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				// muteButtonEl.setAttribute("type", muteButton.getType());
				// ON
				Element on = doc.createElement("on"); //$NON-NLS-1$
				on.setAttribute("color", muteButton.getModeOn().getColorString()); //$NON-NLS-1$
				on.setAttribute("tooltip", muteButton.getModeOn().getTooltip()); //$NON-NLS-1$
				text = doc.createTextNode(muteButton.getModeOn().getName());
				on.appendChild(text);
				muteButtonEl.appendChild(on);
				// OFF
				Element off = doc.createElement("off"); //$NON-NLS-1$
				off.setAttribute("color", muteButton.getModeOff().getColorString()); //$NON-NLS-1$
				off.setAttribute("tooltip", muteButton.getModeOff().getTooltip()); //$NON-NLS-1$
				text = doc.createTextNode(muteButton.getModeOff().getName());
				off.appendChild(text);
				muteButtonEl.appendChild(off);
				
				layout.appendChild(muteButtonEl);
			}
			// ---------------BUTTONS-----------------
			for (Button button : kbdLayout.getButtons()) {
				Element buttonEl = doc.createElement("button"); //$NON-NLS-1$
				setSizeOfElement(buttonEl, button);
				
				Element key = doc.createElement("key"); //$NON-NLS-1$
				text = doc.createTextNode(button.getKey().getId() + ""); //$NON-NLS-1$
				key.appendChild(text);
				buttonEl.appendChild(key);
				
				if (button.getKey().isAccept()) {
					key.setAttribute("accept", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				for (Entry<ModeKey, Key> entry : button.getModes().entrySet()) {
					Element modeEl = doc.createElement("mode"); //$NON-NLS-1$
					modeEl.setAttribute("modename", entry.getKey().getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
					text = doc.createTextNode(entry.getValue().getId() + ""); //$NON-NLS-1$
					modeEl.appendChild(text);
					buttonEl.appendChild(modeEl);
				}
				layout.appendChild(buttonEl);
			}
			// ---------------MODEBUTTONS-----------------
			for (ModeButton modeButton : kbdLayout.getModeButtons()) {
				Element modeButtonEl = doc.createElement("modebutton"); //$NON-NLS-1$
				setSizeOfElement(modeButtonEl, modeButton);
				
				Element key = doc.createElement("key"); //$NON-NLS-1$
				text = doc.createTextNode(modeButton.getModeKey().getId() + ""); //$NON-NLS-1$
				key.appendChild(text);
				modeButtonEl.appendChild(key);
				
				layout.appendChild(modeButtonEl);
			}
			
			String xml = convertDocToString(doc);
			printToPath(xml, filePath);
			
		} catch (ParserConfigurationException err) {
			logger.error("Could not initialize dBuilder"); //$NON-NLS-1$
			err.printStackTrace();
		}
		logger.info("The KeyboardLayout is saved to XML"); //$NON-NLS-1$
	}
	
	
	/**
	 * converts a XML document to a string, which can be written to a text file
	 * 
	 * @param doc the input xml document
	 * @return the document as a string
	 * @author DirkK
	 */
	private static String convertDocToString(Document doc) {
		// OUTPUT TO FILE
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;
		try {
			trans = transfac.newTransformer();
		} catch (TransformerConfigurationException err1) {
			logger.error("Failed to convert the keyboard XML-DOM to String (1)"); //$NON-NLS-1$
			trans = null;
			err1.printStackTrace();
		}
		
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
		trans.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		
		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		try {
			trans.transform(source, result);
		} catch (TransformerException err) {
			logger.error("Failed to convert the keyboard XML-DOM to String (2)"); //$NON-NLS-1$
			err.printStackTrace();
		}
		String xmlString = sw.toString();
		
		return xmlString;
	}
	
	
	/**
	 * prints the keyboard string to a given file
	 * 
	 * @param xmlString
	 * @param file path where the xml file shall be saved
	 * @author DirkK
	 */
	private static void printToPath(String xmlString, String file) {
		File confFile = new File(file);
		FileWriter fw;
		try {
			fw = new FileWriter(confFile);
		} catch (IOException err1) {
			logger.error("Failed to write the keyboard xml string to file (1)"); //$NON-NLS-1$
			fw = null;
			err1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write(xmlString);
			bw.close();
			logger.debug("XML written to file " + file); //$NON-NLS-1$
		} catch (IOException err) {
			logger.error("Failed to write the keyboard xml string to file (2)"); //$NON-NLS-1$
			err.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * sets the size values of a physical buttons, these values are for all button types the same
	 * 
	 * @param el the element shall get the size attributes
	 * @param button the origin physicalButton
	 * @author DirkK
	 */
	private static <T> void setSizeOfElement(Element el, ILayoutElement button) {
		el.setAttribute("size_x", ((int) button.getOrigSize().getWidth()) + ""); //$NON-NLS-1$ //$NON-NLS-2$
		el.setAttribute("size_y", ((int) button.getOrigSize().getHeight()) + ""); //$NON-NLS-1$ //$NON-NLS-2$
		el.setAttribute("pos_x", button.getPos_x() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		el.setAttribute("pos_y", button.getPos_y() + ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
