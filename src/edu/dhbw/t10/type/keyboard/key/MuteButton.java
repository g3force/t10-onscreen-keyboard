/*
 * *********************************************************
 * Copyright (c) 2011 - 2011, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Oct 28, 2011
 * Author(s): DirkK
 * 
 * *********************************************************
 */
package edu.dhbw.t10.type.keyboard.key;

import java.awt.Color;
import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import edu.dhbw.t10.manager.Controller;


/**
 * button for the mute options
 * 
 * @author DirkK, NicolaiO
 * 
 */
public class MuteButton extends PhysicalButton {
	/**  */
	private static final long		serialVersionUID		= -4124533718708150504L;
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger					= Logger.getLogger(MuteButton.class);
	public static final int			UNKNOWN					= 0;
	public static final int			AUTO_PROFILE_CHANGE	= 1;
	public static final int			AUTO_COMPLETING		= 2;
	public static final int			TREE_EXPANDING			= 3;
	private int							type						= UNKNOWN;
	// private Color onColor;
	// private Color offColor;
	// private String onName = "";
	// private String offName = "";
	private Mode						on;
	private Mode						off;
	private boolean					activated				= false;
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Create new MuteButton with given size and position
	 * 
	 * @param size_x
	 * @param size_y
	 * @param pos_x
	 * @param pos_y
	 * @author DirkK, NicolaiO
	 */
	public MuteButton(int size_x, int size_y, int pos_x, int pos_y) {
		super(size_x, size_y, pos_x, pos_y);
		on = new Mode(this.getBackground());
		off = new Mode(this.getBackground());
		addMouseListener(Controller.getInstance());
	}
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Toggle state of mute button
	 * 
	 * @author NicolaiO
	 */
	public void push() {
		setActivated(!activated);
	}
	
	
	/**
	 * Set button state to off (deactivated)
	 * 
	 * @author NicolaiO
	 */
	public void release() {
		setActivated(false);
	}
	
	
	/**
	 * Set state and modify visibility of button
	 * 
	 * @param activated
	 * @author NicolaiO
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
		if (!activated) {
			setText(off.getName());
			setBackground(off.getColor());
			logger.debug("MuteButton deactivated"); //$NON-NLS-1$
		} else {
			setText(on.getName());
			setBackground(on.getColor());
			logger.debug("MuteButton activated"); //$NON-NLS-1$
		}
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
	
	
	public Mode getModeOn() {
		return on;
	}
	
	
	public void setModeOn(Mode on) {
		this.on = on;
	}
	
	
	public Mode getModeOff() {
		return off;
	}
	
	
	public void setModeOff(Mode off) {
		this.off = off;
	}
	
	
	public Mode getMode() {
		if (activated)
			return on;
		return off;
	}


	public boolean isActivated() {
		return activated;
	}
	
	
	// --------------------------------------------------------------------------
	// --- sub-classes ----------------------------------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * 
	 * container class for all attributes for a state like on and off
	 * 
	 * @author DirkK
	 * 
	 */
	public class Mode {
		private Color	color;
		private String	colorS;
		private String	name;
		private String	tooltip;
		
		
		private Mode(Color c) {
			colorS = "white"; //$NON-NLS-1$
			color = c;
		}
		
		
		public Color getColor() {
			return color;
		}
		
		
		public String getColorString() {
			return colorS;
		}
		
		
		public void setColor(String color) {
			colorS = color;
			Color c = getColorFromString(color);
			if (c != null)
				this.color = c;
		}
		
		
		public String getName() {
			return name;
		}
		
		
		public void setName(String name) {
			this.name = name;
		}
		
		
		public String getTooltip() {
			return tooltip;
		}
		
		
		public void setTooltip(String tooltip) {
			this.tooltip = tooltip;
		}
		
		
		/**
		 * @param bgColor
		 * @return
		 * @author NicolaiO
		 */
		private Color getColorFromString(String bgColor) {
			Color color;
			try {
				Field field = Class.forName("java.awt.Color").getField(bgColor); //$NON-NLS-1$
				color = (Color) field.get(null);
			} catch (Exception e) {
				color = null;
			}
			return color;
		}
	}
}
