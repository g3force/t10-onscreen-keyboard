/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 27, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.keyboard.key;

import java.net.URL;

import javax.swing.ImageIcon;


/**
 * A key represents a combination of a keycode and a name together with some meta information.
 * e.g. 'a' is a key, but the a-Button on the keyboard is a Button, because it also includes an 'A'
 * 
 * A key can have a type and it can be an accept key (for accepting the currently suggested word)
 * 
 * @author NicolaiO
 * 
 */
public class Key {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	public static final int	UNKNOWN			= 0;
	public static final int	CONTROL			= 1;
	public static final int	UNICODE			= 2;
	public static final int	CHAR				= 3;
	
	private int					id					= 0;
	private String				keycode			= ""; //$NON-NLS-1$
	private String				name				= ""; //$NON-NLS-1$
	private int					type				= 0;
	private boolean			accept			= false;
	private ImageIcon			defaultIcon		= new ImageIcon();
	private String				defaultIconSrc	= ""; //$NON-NLS-1$
	private ImageIcon			holdIcon			= new ImageIcon();
	private String				holdIconSrc		= ""; //$NON-NLS-1$

	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * Create new Key.
	 * 
	 * @param id unique identification (mostly for keymap file)
	 * @param name visible name on keyboard
	 * @param keycode code for internal usage (from keymap)
	 * @param type one of UNKNOWN, CONTROL, UNICODE, CHAR
	 * @param accept is this key an accept key? (save word after entering key)
	 * @param icon
	 * @param holdIcon
	 * @author NicolaiO
	 */
	public Key(int id, String name, String keycode, int type, boolean accept, String icon, String holdIcon) {
		this.id = id;
		this.name = name;
		this.keycode = keycode;
		this.type = type;
		this.accept = accept;
		setIcon(icon);
		setHoldIcon(holdIcon);
	}

	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	public Key clone() {
		Key nk = new Key(id, name, keycode, type, accept, defaultIconSrc, holdIconSrc);
		return nk;
	}
	
	
	public String toString() {
		return "id:" + id + " n:" + name + " kc:" + keycode + " t:" + type + " a:" + accept + " i:" + defaultIcon; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getKeycode() {
		return keycode;
	}
	
	
	public void setKeycode(String keycode) {
		this.keycode = keycode;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getType() {
		return type;
	}
	
	
	public void setType(int type) {
		this.type = type;
	}
	
	
	public boolean isAccept() {
		return accept;
	}
	
	
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	
	
	public ImageIcon getDefaultIcon() {
		return defaultIcon;
	}
	
	
	public String getDefaultIconSrc() {
		return defaultIconSrc;
	}
	
	
	public ImageIcon getHoldIcon() {
		return holdIcon;
	}
	
	
	public void setHoldIcon(String holdIcon) {
		holdIconSrc = holdIcon;
		URL iconUrl = getClass().getResource(holdIcon);
		if (!holdIcon.equals("") && iconUrl != null) { //$NON-NLS-1$
			this.holdIcon = new ImageIcon(iconUrl);
		} else {
			holdIconSrc = ""; //$NON-NLS-1$
		}
	}
	
	
	public void setIcon(String icon) {
		defaultIconSrc = icon;
		URL iconUrl = getClass().getResource(icon);
		if (iconUrl != null) {
			this.defaultIcon = new ImageIcon(iconUrl);
		} else {
			defaultIconSrc = ""; //$NON-NLS-1$
		}
	}

	
	public String getHoldIconSrc() {
		return holdIconSrc;
	}

}
