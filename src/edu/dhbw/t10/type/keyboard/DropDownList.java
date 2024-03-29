/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 24, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.keyboard;

import java.awt.Dimension;

import javax.swing.JComboBox;


/**
 * Drop down list on the keyboard, especially for profile dropdown
 * 
 * @author NicolaiO
 * 
 */
public class DropDownList extends JComboBox implements ILayoutElement {
	private static final long	serialVersionUID	= 5433669829791012176L;
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	public static final int		PROFILE				= 0;
	private int						type					= PROFILE;
	private Dimension				origSize				= new Dimension(10, 10);
	private int						pos_x					= 0;
	private int						pos_y					= 0;
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	/**
	 * Create a new DropDownList with given type and bounds.
	 * 
	 * @param type
	 * @param size_x
	 * @param size_y
	 * @param pos_x
	 * @param pos_y
	 * @author NicolaiO
	 */
	public DropDownList(int type, int size_x, int size_y, int pos_x, int pos_y) {
		init(type, size_x, size_y, pos_x, pos_y);
	}
	
	
	/**
	 * 
	 * @param type
	 * @param size_x
	 * @param size_y
	 * @param pos_x
	 * @param pos_y
	 */
	public DropDownList(String type, int size_x, int size_y, int pos_x, int pos_y) {
		if (type.equals("profile")) { //$NON-NLS-1$
			this.type = PROFILE;
		}
		init(this.type, size_x, size_y, pos_x, pos_y);
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Initialize DDL with type, size and position
	 * 
	 * @param type
	 * @param size_x
	 * @param size_y
	 * @param pos_x
	 * @param pos_y
	 * @author NicolaiO
	 */
	private void init(int type, int size_x, int size_y, int pos_x, int pos_y) {
		this.type = type;
		this.origSize = new Dimension(size_x, size_y);
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		setLayout(null);
		setBounds(getPos_x(), getPos_y(), getSize().width, getSize().height);
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	public int getType() {
		return type;
	}
	
	
	public void setType(int type) {
		this.type = type;
	}
	
	
	public String getTypeAsString() {
		if (type == DropDownList.PROFILE)
			return "profile"; //$NON-NLS-1$
		return ""; //$NON-NLS-1$
	}
	
	
	public Dimension getOrigSize() {
		return origSize;
	}
	
	
	public void setOrigSize(Dimension origSize) {
		this.origSize = origSize;
	}
	
	
	public int getPos_x() {
		return pos_x;
	}
	
	
	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}
	
	
	public int getPos_y() {
		return pos_y;
	}
	
	
	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}
}
