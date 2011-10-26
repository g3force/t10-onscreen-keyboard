/* 
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 15, 2011
 * Author(s): NicolaiO
 *
 * *********************************************************
 */
package edu.dhbw.t10.manager.output;

import org.apache.log4j.Logger;


/**
 * TODO NicolaiO, add comment!
 * - What should this type do (in one sentence)?
 * - If not intuitive: A simple example how to use this class
 * 
 * @author Andres
 * 
 */
public class OutputManager
{
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(Output.class);
	
	Output								out		= new Output();
	String								suggest	= "";										// FIXME Was ist mit Unicode zeichen
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------


	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	@SuppressWarnings("unused")
	/**
	 * Process the Input data for 5 cases. 
	 */
	public boolean print(String newSuggest, String typed) {
		if (isBackSpace(newSuggest)) {
			if (typed.length() > 0) {
				typed = typed.substring(0, typed.length() - 2);
				printSuggest(newSuggest, typed);
			} else {
				out.deleteChar(1);
			}
		} else if (isAcceptSpace(newSuggest)) {
			out.printString("\\SPACE\\");
			typed = "";
			suggest = "";
			return true;
		} else if (isDeclineSpace(newSuggest)) {
			int diff = suggest.length() - typed.length();
			out.deleteChar(diff);
			out.printString("\\SPACE\\");
			typed = "";
			suggest = "";
		} else if (isControl(newSuggest)) {
			out.printString(newSuggest);
		} else {
			out.printString(newSuggest);
			printSuggest(newSuggest, typed);
		}
		return false;
	}
	

	/**
	 * Checks if a Keyinput is a Control Character
	 */
	private boolean isControl(String input) {
		if (input.charAt(0) == '\\' && input.charAt(input.length() - 1) == '\\' && !input.substring(0).startsWith("\\U+")) {
			return true;
		} else
			return false;
	}
	

	/**
	 * Checks if a Keyinput is a Back Space
	 */
	private boolean isBackSpace(String input) {
		if (input == "\\BACK_SPACE\\") {
			return true;
		} else
			return false;
	}
	

	/**
	 * Checks if a Keyinput is a Accept Space
	 */
	private boolean isAcceptSpace(String input) {
		if (input == "\\SPACE\\") {
			return true;
		} else
			return false;
	}
	

	/**
	 * Checks if a Keyinput is a Decline Space
	 */
	private boolean isDeclineSpace(String input) {
		if (input == "\\SPACE\\") {
			return true;
		} else
			return false;
	}
	

	/**
	 * 
	 * printSuggest deletes the old suggest, prints it out and mark the chars that are added from the suggest word.
	 * 
	 */
	private void printSuggest(String newSuggest, String typed) {
		suggest = newSuggest;
		int diff = suggest.length() - typed.length();
		out.printString(suggest.substring(typed.length()));
		out.markChar(diff);
	}
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
