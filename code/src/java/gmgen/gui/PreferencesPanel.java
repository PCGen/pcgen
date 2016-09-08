/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */
package gmgen.gui;


import javax.swing.JPanel;

/**
 * This interface defines what a preferences panel needs to be able to do.
 *
 * @author  devon
 */
public abstract class PreferencesPanel extends JPanel
{
	/** Apply the preferences */ 
	public abstract void applyPreferences();

	/** Initialize Preferences */
	public abstract void initPreferences();

	@Override
	public abstract String toString();
}
