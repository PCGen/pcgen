/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * AboutBox.java
 *
 * Created on September 18, 2002, 5:38 PM
 */
package gmgen.gui;


/** 
 * This interface defines what a preferences panel needs to be able to do.
 *
 * @author  devon
 */
public abstract class PreferencesPanel extends javax.swing.JPanel
{
	/** Apply the preferences */ 
	public abstract void applyPreferences();

	/** Initialize Preferences */
	public abstract void initPreferences();

	@Override
	public abstract String toString();
}
