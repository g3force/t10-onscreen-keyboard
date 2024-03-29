/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): DanielAl
 * 
 * *********************************************************
 */
package edu.dhbw.t10.manager.output;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.dhbw.t10.helper.Messages;
import edu.dhbw.t10.manager.Controller;
import edu.dhbw.t10.type.Config;
import edu.dhbw.t10.type.keyboard.key.Button;
import edu.dhbw.t10.type.keyboard.key.Key;
import edu.dhbw.t10.type.keyboard.key.ModeKey;
import edu.dhbw.t10.type.keyboard.key.MuteButton;
import edu.dhbw.t10.type.profile.Profile_V2;


/**
 * The OutputManager provides the interface between the controller and Output. <br>
 * It gives different meta methods for a better handling in the Output. <br>
 * 
 * @author DanielAl
 */
public class OutputManager {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(OutputManager.class);
	
	// Output instance
	Output								out;
	private String						typedWord;
	private String						suggest;
	// if unMark is true the method unMark() is used for unmarking suggested chars, otherwise all suggests are deleted a
	// newly printed;
	// also if this value is true marked strings could be overwritten without deleting them first...
	private boolean					unMark;
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * Constructor for this class with no parameters<br>
	 * Instantiate Output. If this fails with an UnknownOSException, the Keyboard is closed.
	 * 
	 * @author DanielAl
	 */
	public OutputManager() {
		logger.debug("initializing..."); //$NON-NLS-1$
		try {
			out = new Output();
		} catch (UnknownOSException err) {
			logger.fatal(err.getMessage());
			// If no Output could be instanciated close the Application
			System.exit(-1);
		}
		clearWord();
		unMark = Config.getConf().getProperty("unMark", "false").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Config.getConf().setProperty("unMark", String.valueOf(unMark)); //$NON-NLS-1$
		logger.debug("initialized"); //$NON-NLS-1$
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Calls Output.printChar(c)
	 * @param c
	 * @return boolean
	 * 
	 * @author DanielAl
	 */
	public boolean printKey(Key c) {
		return out.printKey(c);
	}
	
	
	/**
	 * Deletes 'num' chars via sending so many Back_Spaces...
	 * Implemented directly and not with printCombi...
	 * 
	 * @param num
	 * @return boolean
	 * @author DanielAl
	 */
	public boolean deleteChar(int num) {
		if (num <= 0)
			return false;
		else {
			for (int i = 0; i < num; i++) {
				out.printString("\\BACK_SPACE\\", Key.CONTROL); //$NON-NLS-1$
			}
			return true;
		}
	}
	
	
	/**
	 * Marks 'num' chars backwards via holding SHIFT and press the LEFT Key.
	 * 
	 * @author DanielAl
	 * @return boolean
	 * @param num
	 */
	public boolean mark(int num) {
		// Use a ArrayList to be able to use the printCombi
		ArrayList<Key> markCombiHold = new ArrayList<Key>();
		ArrayList<Key> markCombiPress = new ArrayList<Key>();
		markCombiHold.add(new Key(0, "Shift", "\\SHIFT\\", Key.CONTROL, false, "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for (int j = 1; j < num + 1; j++) {
			// Add one marked char via one LEFT Key...
			markCombiPress.add(new Key(j, "Left", "\\LEFT\\", Key.CONTROL, false, "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.trace("Added one mark..."); //$NON-NLS-1$
		}
		boolean mark = out.printCombi(markCombiHold, markCombiPress);
		logger.info(num + " Symboly marked"); //$NON-NLS-1$
		return mark;
	}
	
	
	/**
	 * Unmark all things via pressing the RIGHT Key.
	 * Is used when unMark property is set to true.
	 * 
	 * @author DanielAl
	 */
	public void unMark() {
		out.printString("\\RIGHT\\", Key.CONTROL); //$NON-NLS-1$
		logger.trace("Keys unmarked"); //$NON-NLS-1$
	}
	
	
	/**
	 * Delete all marked things via pressing the DELETE Key
	 * Is used when unMark property is set to false.
	 * 
	 * @author DanielAl
	 */
	public void delMark(int num) {
		if (num > 0)
			out.printString("\\DELETE\\", Key.CONTROL); //$NON-NLS-1$
		logger.trace("marked Keys are deleted"); //$NON-NLS-1$
	}
	
	
	/**
	 * Overloaded method printSuggest to call the default function of printSoggest (wich marks the Suggested chars, func
	 * = 0)
	 * @param newSuggest
	 * @param typed
	 * @author DanielAl
	 */
	public void printSuggest(String newSuggest, String typed) {
		printSuggest(newSuggest, typed, 0);
	}
	
	
	/**
	 * Prints a new Suggest for given chars and mark the suggested chars, which aren't yet typed, if func = 0. This is
	 * the default function of printSuggest.<br>
	 * If func !=0 this function will only prints the suggest without marking the suggested chars. This is used,
	 * beacause, the unMark() method, which unmarks the suggest, doesn't work with all applications, and that is the
	 * workaround.
	 * 
	 * @author DanielAl
	 * @param newSuggest
	 * @param typed
	 * @param func
	 */
	public void printSuggest(String newSuggest, String typed, int func) {
		// only used if there are really chars that aren't typed yet...
		if (newSuggest.length() > typed.length()) {
			out.printString(newSuggest.substring(typed.length()), Key.CHAR);
			if (func == 0)
				mark(newSuggest.length() - typed.length());
			logger.debug("Suggest: " + newSuggest + " printed"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	
	/**
	 * Print a combination of keys, given by a list of ModeKeys and a finishing key
	 * 
	 * @param mks list of ModeKeys to be pressed
	 * @param key key to be pressed at the end
	 * @author NicolaiO
	 */
	public void printCombi(ArrayList<ModeKey> mks, Key key) {
		ArrayList<Key> pressed = new ArrayList<Key>();
		ArrayList<Key> hold = new ArrayList<Key>();

		for (ModeKey mk : mks) {
			if (mk.getState() != ModeKey.DEFAULT) {
				hold.add((Key) mk);
			}
		}
		pressed.add(key);
		out.printCombi(hold, pressed);
	}
	

	// --------------------------------------------------------------------------
	// --- keyIs Actions --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Accept a suggested word, unmarks it and prints the given key.
	 * 
	 * @param key
	 * @author DanielAl
	 */
	public void keyIsAccept(Key key, String typedWord, String suggest) {
		if (suggest.length() > typedWord.length()) {
			if (unMark)
				unMark();
			else
				printSuggest(suggest, typedWord, 1);
		}
		printKey(key);
	}
	
	
	/**
	 * Prints the given key, added it to the typed String and get a new suggest and prints it...
	 * @param key
	 * @author DanielAl
	 */
	public void keyIsChar(Key key) {
		printKey(key);
		printSuggest(suggest, typedWord);
	}
	
	
	/**
	 * Handles a typed BackSpace.<br>
	 * If there is a typedWord delete the Mark if exists (delMark), delete the last typed char and print a new suggest.<br>
	 * If there is no typedWord send a BackSpace.
	 * 
	 * @author DanielAl
	 */
	
	public void keyIsBackspace(String oldTypedWord, String oldSuggest) {
		if (oldTypedWord.length() > 0) {
			// If oldSuggest not equal to oldTypedWord, it must be longer and so it is marked. Then this mark has to be
			// deleted first.
			if (!oldTypedWord.equals(oldSuggest))
					delMark(oldSuggest.length() - oldTypedWord.length());
			deleteChar(1);
			printSuggest(suggest, typedWord);
		} else {
			// No OldTypedWord exists so only the left char of the cursor is deleted
			deleteChar(1);
		}
	}
	

	/**
	 * Prints a Control or Unicode Key, <br>
	 * if no DELETE or BACK_SPACE, these are special Keys and handled with extra methods...
	 * 
	 * @param key
	 * @author DanielAl
	 */
	public void keyIsControlOrUnicode(Key key) {
		if (typedWord.length() < suggest.length() && !unMark) {
			delMark(suggest.length() - typedWord.length());
		}
		printKey(key);
	}
	
	
	// --------------------------------------------------------------------------
	// --- buttons pressed actions ----------------------------------------------
	// --------------------------------------------------------------------------
	
	
	/**
	 * Switchs between the three different Mute modes...<br>
	 * Modes are:<br>
	 * - AUTO_COMPLETING - If activated, this prints a suggested Word behind the typed chars and mark them...
	 * - AUTO_PROFILE_CHANGE - If activated, this changes the profiles based on the surrounded context.
	 * - TREE_EXPANDING - If activated, accepted words are saved in the dictionary...
	 * @param muteB
	 * @author DanielAl
	 */
	public void muteButtonPressed(MuteButton muteB, Profile_V2 activeProfile) {
		muteB.push();
		int type = muteB.getType();
		switch (type) {
			case MuteButton.AUTO_COMPLETING:
				if (muteB.isActivated()) {
					clearWord();
				}
				activeProfile.setAutoCompleting(muteB.isActivated());
				break;
			case MuteButton.AUTO_PROFILE_CHANGE:
				activeProfile.setAutoProfileChange(muteB.isActivated());
				break;
			case MuteButton.TREE_EXPANDING:
				if (muteB.isActivated()) {
					clearWord();
				}
				activeProfile.setTreeExpanding(muteB.isActivated());
				break;
		}
		logger.debug("MuteButton pressed"); //$NON-NLS-1$
	}
	
	
	/**
	 * Do the logic for a button event. Switch between different types, specific Keys and a Key Combination...
	 * 
	 * @param button
	 * @author DanielAl
	 * @param activeProfile
	 */
	public void buttonPressed(Button button, Profile_V2 activeProfile) {
		Key key = (Key) button.getPressedKey();
		
		// currently we do not support some buttons for linux...
		if (Output.getOs() == Output.LINUX
				&& (button.getKey().getKeycode().equals("\\WINDOWS\\") || button.getKey().getKeycode() //$NON-NLS-1$
						.equals("\\CONTEXT_MENU\\"))) { //$NON-NLS-1$
			Controller.getInstance().showStatusMessage(Messages.getString("OutputManager.26")); //$NON-NLS-1$
			return;
		}
		
		// get all currently pressed Modekeys
		ArrayList<ModeKey> pressedModeKeys = activeProfile.getKbdLayout().getPressedModeKeys();
		
		if (key.getKeycode().equals("\\CAPS_LOCK\\")) { //$NON-NLS-1$
			keyIsCapsLock(activeProfile);
		} else {
			// Print the key iff zero or one ModeKeys is pressed
			if (pressedModeKeys.size() - button.getActiveModes().size() < 1) {
				if (key.isAccept()) {
					keyIsAccept(key, typedWord, suggest);
					acceptWord(suggest, activeProfile);
				} else if (key.getType() == Key.CHAR) {
					typedWord = typedWord + key.getName();
					suggest = activeProfile.getWordSuggest(typedWord);
					keyIsChar(key);
				} else if (key.getKeycode().equals("\\BACK_SPACE\\")) { //$NON-NLS-1$
					if (typedWord.length() > 0) {
						String oldTypedWord = typedWord;
						String oldSuggest = suggest;
						typedWord = typedWord.substring(0, typedWord.length() - 1);
						suggest = activeProfile.getWordSuggest(typedWord);
						keyIsBackspace(oldTypedWord, oldSuggest);
					} else {
						keyIsBackspace(typedWord, suggest);
					}
				} else if (key.getKeycode().equals("\\DELETE\\")) { //$NON-NLS-1$
					printKey(key);
					suggest = typedWord;
				} else if (key.getType() == Key.CONTROL || key.getType() == Key.UNICODE) {
					keyIsControlOrUnicode(key);
					if (key.getType() == Key.UNICODE
							|| (key.getKeycode().equals("\\SPACE\\") || key.getKeycode().equals("\\ENTER\\"))) { //$NON-NLS-1$ //$NON-NLS-2$
						acceptWord(typedWord, activeProfile);
					} else if (key.getType() == Key.CONTROL) {
						clearWord();
					}
				}
				logger.debug("Key pressed: " + key.toString()); //$NON-NLS-1$
			} else {
				// print the key combi else (-> pressedModeKeys.size() - button.getActiveModes().size() >= 1
				logger.debug("Keycombi will be executed. Hint: " + pressedModeKeys.size() + "-" //$NON-NLS-1$ //$NON-NLS-2$
						+ button.getActiveModes().size() + " >= 1"); //$NON-NLS-1$
				logger.trace(pressedModeKeys);
				printCombi(pressedModeKeys, button.getKey());
			}
		}
		
		// unset all ModeButtons, that are in PRESSED state
		activeProfile.getKbdLayout().unsetPressedModes();
	}
	
	
	/**
	 * Sets the typedWord and the suggest to an empty String. So you can begin again with a new word.
	 * 
	 * @author DanielAl
	 */
	public void clearWord() {
		typedWord = ""; //$NON-NLS-1$
		suggest = ""; //$NON-NLS-1$
	}
	
	/**
	 * is called whenever a word shall be accepted
	 * 
	 * @param word
	 * @author DirkK
	 */
	private void acceptWord(String word, Profile_V2 activeProfile) {
		boolean success = activeProfile.acceptWord(word);
		if (success) {
			Controller.getInstance().showStatusMessage(Messages.getString("OutputManager.38") + word); //$NON-NLS-1$
			logger.trace("Word accepted"); //$NON-NLS-1$
		}
		clearWord();
	}
	
	/**
	 * Run this with a caps_lock key to trigger all shift buttons.
	 * If Shift state is DEFAULT, it will be changed to HOLD, else to DEFAULT
	 * 
	 * @param key
	 * @author NicolaiO
	 */
	private void keyIsCapsLock(Profile_V2 activeProfile) {
		logger.trace("CapsLock"); //$NON-NLS-1$
		for (ModeKey mk : activeProfile.getKbdLayout().getModeKeys()) {
			if (mk.getKeycode().equals("\\SHIFT\\")) { //$NON-NLS-1$
				if (mk.getState() == ModeKey.DEFAULT) {
					mk.setState(ModeKey.HOLD);
				} else {
					mk.setState(ModeKey.DEFAULT);
				}
				break;
			}
		}
		// presenter.pack();
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
}
