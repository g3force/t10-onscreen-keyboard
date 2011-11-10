/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): DirkK
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.tree;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.dhbw.t10.manager.profile.ImportExportManager;


/**
 * data container for the dictionary, the data it self is stored in the PriorityElements, PriorityTree has functions
 * like insert, delete,...
 * @author DirkK
 * 
 */
public class PriorityTree implements Serializable {
	/**  */
	private static final long				serialVersionUID	= 662040913098286336L;
	
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private PriorityElement					root;
	private transient LinkedList<int[]>	allowedChars;
	private HashMap<String, Integer>		words;
	
	private static final Logger			logger				= Logger.getLogger(PriorityTree.class);
	private transient String				pathToAllowedChars;
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public PriorityTree(String chars) {
		pathToAllowedChars = chars;
		allowedChars = new LinkedList<int[]>();
		loadAllowedChars();
		root = new PriorityElement('\u0000', null, null, 0);
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * inserts a word to the tree
	 * if the word already exist, frequency is increased by one and suggests are adujsted
	 * @param word the word that should be inserted
	 */
	public boolean insert(String word) {
		return insert(word, 1, false);
	}
	
	
	/**
	 * inserts a word to the tree
	 * if the word already exist, frequency is increased by one and suggests are adjusted
	 * @param word the word that should be inserted
	 * @param frequency the start frequency of the inserting word
	 */
	private boolean insert(String word, int frequency, boolean setFreq) {
		if (inputValid(word)) {
			// logger.trace("Insertig Word...");
			PriorityElement node = root;
			char[] inChar = word.toCharArray(); // put every letter of the word alone in an char array
			for (int i = 0; i < inChar.length; i++) {
				if (node.hasFollower(inChar[i])) {
					// node exists
					if (i < inChar.length - 1) {
						node = node.getFollower(inChar[i]);
					} else {
						// current char is last char, end of the word -> insert
						node = node.getFollower(inChar[i]);
						if (setFreq) // only for import functions
							node.setFrequency(frequency - 1);
						node.increase(); // increases frequency by one and arranges suggests
					}
					// logger.trace("Inserting Node... (Node Increased)");
				} else {
					// node has to be created
					node = node.addFollower(inChar[i]);
					if (i == inChar.length - 1) {
						// current char is last char, end of the word -> insert
						if (setFreq)
							node.setFrequency(frequency - 1);
						node.increase(); // increases frequency by one and arranges suggests
					}
					// logger.trace("Inserting Node... (New Node Added)");
				}
			}
			// logger.debug("Word Inserted");
			return true;
		} else {
			// logger.warn("Word (" + word + ") Ignored - not valid");
			return false;
		}
	}
	
	
	/**
	 * takes a String with the beginning of the word, goes to the according node and returns the stored suggest word
	 * 
	 * @param wordPart beginning of a word
	 * @return suggested Word
	 */
	public String getSuggest(String wordPart) {
		// logger.debug("Creating suggest for " + wordPart + "...");
		PriorityElement suggest = getElement(wordPart);
		if (suggest == null) {
			// logger.info("Suggest created (same as wordPart)");
			return wordPart;
		} else {
			String out = suggest.getSuggest().buildWord();
			// logger.info("Suggest created (suggest word: " + out + ")");
			return out;
		}
	}
	
	
	/**
	 * delete a given word in the tree
	 * if the according word has got followers, the frequency is set to 0
	 * no followers -> it is deleted; all fathers are also deleted if they have no followers
	 * suggests are adjusted in both cases
	 * 
	 * @param word the word to be deleted
	 */
	public void delete(String word) {
		// logger.debug("Deleting Node...");
		PriorityElement deleteEl = getElement(word);
		if (deleteEl != null) {
			// logger.debug("Deleting Node... (Node exist)");
			deleteEl.setFrequency(0); // frequency==0 -> same as delete
			PriorityElement node = deleteEl;
			while (node.getFollowers().isEmpty()) {
				// delete the node and all fathers, if they have not got any other followers (not part of another word)
				// logger.debug("Deleting Node... (Node deleted)");
				node = node.getFather();
				node.deleteFollower(word);
			}
			while (node.getFather() != null && node.getSuggest().buildWord().equals(word)) {
				// have a look at all the fathers of the deleted node, until a node is found which has not got the deleted
				// node as suggest
				// logger.debug("Deleting Node... (Suggest changed)");
				node.resetSuggest(); // searches for a new suggest
				node = node.getFather();
			}
		}
		// logger.info("Node deleted");
	}
	
	
	/**
	 * gets the according PriorityElement to a given word
	 * returns null if the word is not in the tree
	 * 
	 * @param word according word
	 * @return the according PriorityElement
	 */
	private PriorityElement getElement(String word) {
		PriorityElement node = root;
		char[] elChar = word.toCharArray(); // put every letter of the word alone in a char array
		for (int i = 0; i < elChar.length; i++) {
			if (node.hasFollower(elChar[i])) {
				node = node.getFollower(elChar[i]);
				if (i == elChar.length - 1) { // current char is last char
					// logger.debug("Node found");
					return node;
				}
			} else {
				// child not found -> element is not in the tree
				// logger.debug("Node not found (getElement)");
				return null;
			}
		}
		// logger.debug("Node not found (getElement)");
		return null;
	}
	
	
	/**
	 * prints the tree
	 */
	public void printTree() {
		printTree(true, "");
	}
	
	
	/**
	 * print subtree starting at a PrirorityElement specified through the according word
	 * @param rootElement
	 */
	public void printTree(String rootElement) {
		printTree(false, rootElement);
	}
	
	
	/**
	 * helper method for printTree, does the work
	 * and yes, i know that takeRoot is useless, but I do not know the word of root
	 * @param takeRoot decides whether the root element should be took or the given attribute
	 * @param rootElement the element which shall be took, if takeRoot is false
	 */
	private void printTree(boolean takeRoot, String rootElement) {
		PriorityElement start = root;
		if (!takeRoot) {
			start = getElement(rootElement);
			if (start == null)
				start = root;
		}
		logger.debug("Printing output...");
		start.print();
		for (PriorityElement pe : start.getListOfFollowers()) {
			pe.print();
		}
		logger.debug("Complete amount of Elements: " + start.getListOfFollowers().size());
		logger.debug("Output printed");
	}
	
	
	/**
	 * inserts a list of words to a tree
	 * @param input HashMap referencing a word (String) to its frequency (int)
	 */
	public void importFromHashMap(HashMap<String, Integer> input) {
		for (Entry<String, Integer> entry : input.entrySet()) {
			insert(entry.getKey(), entry.getValue(), true);
		}
		logger.debug("imported from HashMap");
	}
	
	
	/**
	 * exports the dictionary tree as a HashMap
	 * @return the dictionary tree as a HashMap
	 */
	public HashMap<String, Integer> exportToHashMap() {
		logger.debug("exporting to HashMap");
		return root.getHashMapOfFollowers();
		// HashMap<String, Integer> exportMap = new HashMap<String, Integer>();
		// for (PriorityElement pe : root.getListOfFollowers()) {
		// if (pe.getFrequency() != 0)
		// exportMap.put(pe.buildWord(), pe.getFrequency());
		// }
		// logger.error("exported to HashMap");
		// return exportMap;
	}
	
	
	/**
	 * just a testing method for Kruse
	 * TODO DirkK delete
	 * @param in
	 * @return
	 * @author DirkK
	 */
	public String suggestInHashMap(String in) {
		String word = "";
		int amount = 0;
		for (Entry<String, Integer> entry : words.entrySet()) {
			if(entry.getKey().length()>=in.length())
				if (entry.getKey().substring(0, in.length()).equals(in) && entry.getValue() > amount) {
					word = entry.getKey();
					amount = entry.getValue();
				}
		}
		return word;
	}
	
	
	/**
	 * any PriorityElement with has got a bottomBorder or less frequency is deleted
	 * @param bottomBorder border to decide if a PriorityElement has to be deleted
	 * @param olderThan not implemented yet
	 * @param flag 0 -> only bottomBorder
	 *           1 -> only olderThan
	 *           2 -> bottomBorder OR olderThan
	 *           3 -> bottomBorder AND olderThan
	 * @return the amount of deleted items
	 * @author DirkK
	 */
	public int autoCleaning(int bottomBorder, long olderThan, int flag) {
		LinkedList<PriorityElement> toDelete = new LinkedList<PriorityElement>();
		for (PriorityElement pe : root.getListOfFollowers()) {
			if ((flag == 0 && (pe.getFrequency() <= bottomBorder)) || (flag == 1 && (pe.getLastUse() <= olderThan))
					|| (flag == 2 && (pe.getFrequency() <= bottomBorder || pe.getLastUse() <= olderThan))
					|| (flag == 3 && (pe.getFrequency() <= bottomBorder && pe.getLastUse() <= olderThan))) {
				if (pe.getFollowers().size() == 0) {
					toDelete.add(pe);
				} else {
					pe.setFrequency(0);
				}
			}
		}
		int length = toDelete.size();
		while (!toDelete.isEmpty()) {
			PriorityElement pe = toDelete.pop();
			delete(pe.buildWord());
		}
		logger.info("Cleaned (removed elements: " + length + ")");
		return length;
	}
	
	
	/**
	 * prints out the dictionary, beginning with the word with the highest frequency
	 * bad in performance
	 * TODO DirkK delete
	 * @author DirkK
	 */
	public LinkedList<PriorityElement> getFreqSortedList() {
		logger.debug("fetching ordered list");
		LinkedList<PriorityElement> ll = new LinkedList<PriorityElement>();
		for (PriorityElement pe : root.getListOfFollowers()) {
			// boolean sorted = false;
			if (ll.isEmpty())
				ll.add(pe);
			if(ll.size()==1) {
				if (ll.get(0).getFrequency() < pe.getFrequency())
					ll.push(pe);
				else
					ll.add(0, pe);
			}
			if (ll.size() == 2) {
				if (ll.get(0).getFrequency() < pe.getFrequency())
					ll.push(pe);
				else
					ll.add(0, pe);
			}
			
			int index = 1;
			while (ll.get(index).getFrequency() >= pe.getFrequency()
					&& ll.get(index + 1).getFrequency() <= pe.getFrequency()) {
				if (ll.get(index).getFrequency() > pe.getFrequency())
					index = index / 2;
				else
					index = index + index / 2;
			}
			ll.add(index, pe);
			// for (int i = 0; i < ll.size() && !sorted; i++) {
			// if (ll.get(i).getFrequency() < pe.getFrequency()) {
			// ll.add(i, pe);
			// sorted = true;
			// }
			// }
		}
		logger.debug("fetched ordered list");
		return ll;
	}
	
	
	/**
	 * 
	 * takes the range of chars defined int the .chars file
	 * if the string contains another char, false will be returned
	 * @param in string
	 * 
	 * @return true if, all chars are in the alphabet
	 * @author DirkK
	 */
	
	private boolean inputValid(String in) {
		if (in.length() == 0) {
			return false;
		}
		for (char letter : in.toCharArray()) {
			int counter = 0;
			// check for every range, if the char is in a range counter is increased by one
			for (int[] range : allowedChars) {
				if ((int) letter >= range[0] && (int) letter <= range[1])
					counter++;
			}
			if (counter == 0)
				return false;
		}
		return true;

	}
	
	
	/**
	 * save the rules which chars are allowed
	 * not used yet
	 * @author DirkK
	 */
	
	public void saveAllowedChars() {
		try {
			ImportExportManager.saveChars(pathToAllowedChars, allowedChars);
		} catch (IOException io) {
			logger.error("IOException in readConfig()");
			io.printStackTrace();
		} catch (Exception ex) {
			logger.error("Exception in readConfig(): " + ex.toString());
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * reads form a config file which chars are allowed to be added to the tree
	 * avoids to insert UNICODE into the tree
	 * Not used yet
	 * @author DirkK
	 */
	private void loadAllowedChars() {
		try {
			allowedChars = ImportExportManager.loadChars(pathToAllowedChars);
		} catch (IOException io) {
			logger.error("IOException in loadAllowedChars()");
			io.printStackTrace();
		} catch (Exception ex) {
			logger.error("Exception in loadAllowedChars(): " + ex.toString());
			ex.printStackTrace();
		}
	}

	
	public HashMap<String, Integer> getWords() {
		return words;
	}
	
	
	public void setWords(HashMap<String, Integer> words) {
		this.words = words;
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
}
