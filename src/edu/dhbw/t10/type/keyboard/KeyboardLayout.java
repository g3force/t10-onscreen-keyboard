/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 21, 2011
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.keyboard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;

import edu.dhbw.t10.type.keyboard.key.Button;


/**
 * TODO NicolaiO, add comment!
 * - What should this type do (in one sentence)?
 * - If not intuitive: A simple example how to use this class
 * 
 * @author NicolaiO
 * 
 */
public class KeyboardLayout {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private ArrayList<Button>			keys			= new ArrayList<Button>();
	private ArrayList<DropDownList>	ddls			= new ArrayList<DropDownList>();
	private int								size_x		= 0;
	private int								size_y		= 0;
	private float							scale_x		= 1;
	private float							scale_y		= 1;
	private float							scale_font	= 1;
	private Font							font			= new Font("Dialog", Font.PLAIN, 12);
	

	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	public KeyboardLayout(int size_x, int size_y, float scalex, float scaley, float scale_font) {
		this.size_x = size_x;
		this.size_y = size_y;
		this.scale_x = scalex;
		this.scale_y = scaley;
		this.scale_font = scale_font;
	}
	

	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	public void addKey(Button key) {
		keys.add(key);
	}
	
	
	public void addDdl(DropDownList ddl) {
		ddls.add(ddl);
	}
	

	public void rescale() {
		for (Button k : keys) {
			Rectangle rect = k.getBounds();
			rect.setBounds((int) (k.getPos_x() * scale_x), (int) (k.getPos_y() * scale_y),
					(int) (k.getOrigSize().width * scale_x), (int) (k.getOrigSize().height * scale_y));
			k.setBounds(rect);
			k.setFont(new Font(font.getName(), font.getStyle(), (int) (font.getSize() * scale_font)));
		}
		for (DropDownList ddl : ddls) {
			Rectangle rect = ddl.getBounds();
			rect.setBounds((int) (ddl.getPos_x() * scale_x), (int) (ddl.getPos_y() * scale_y),
					(int) (ddl.getOrigSize().width * scale_x), (int) (ddl.getOrigSize().height * scale_y));
			ddl.setBounds(rect);
		}
	}
	
	
	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
	
	public ArrayList<Button> getKeys() {
		return keys;
	}
	
	
	public void setKeys(ArrayList<Button> keys) {
		this.keys = keys;
	}
	
	
	public int getSize_x() {
		return (int) (size_x * scale_x);
	}
	
	
	public void setSize_x(int size_x) {
		this.size_x = size_x;
	}
	
	
	public int getSize_y() {
		return (int) (size_y * scale_y);
	}
	
	
	public int getOrigSize_y() {
		return size_y;
	}
	
	
	public int getOrigSize_x() {
		return size_x;
	}
	

	public void setSize_y(int size_y) {
		this.size_y = size_y;
	}
	
	
	public void setScale(float scale) {
		this.scale_x = scale;
		this.scale_y = scale;
		this.scale_font = scale;
	}
	
	
	public void setSize(Dimension size) {
		size_x = size.width;
		size_y = size.height;
	}
	

	public float getScale_x() {
		return scale_x;
	}
	
	
	public void setScale_x(float scale_x) {
		this.scale_x = scale_x;
	}
	
	
	public float getScale_y() {
		return scale_y;
	}
	
	
	public void setScale_y(float scale_y) {
		this.scale_y = scale_y;
	}
	
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	
	public void setFontSize(int size) {
		font = new Font(font.getName(), font.getStyle(), size);
	}
	
	
	public int getFontSize() {
		return font.getSize();
	}


	public Font getFont() {
		return font;
	}


	public float getScale_font() {
		return scale_font;
	}
	
	
	public void setScale_font(float scale_font) {
		this.scale_font = scale_font;
	}


	public ArrayList<DropDownList> getDdls() {
		return ddls;
	}


}
