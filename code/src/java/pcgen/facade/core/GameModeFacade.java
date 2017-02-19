/*
 * GameModeFacade.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package pcgen.facade.core;

import java.util.List;

import pcgen.util.enumeration.Tab;

/**
 *
 */
public interface GameModeFacade
{

    @Override
	public String toString();

	public String getName();

	public String getDisplayName();
	
	public String getDefaultSourceTitle();

	public List<String> getDefaultDataSetList();

	public String getInfoSheet();

	public String getInfoSheetSkill();
	
	public String getOutputSheetDirectory();
	
	public String getOutputSheetDefault(String type);
	
	public String getCharSheetDir();

	public String getDefaultCharSheet();
	
	/**
	 * @return The displayable name for the character height units
	 */
	public String getHeightUnit();
	
	/**
	 * @return The displayable name for the character weight units
	 */
	public String getWeightUnit();

	/**
	 * Get game mode specific add with meta magic message
	 * @return add with meta magic message
	 */
	public String getAddWithMetamagicMessage();

	/**
	 * @return The abbreviation for the currency unit.
	 */
	public String getCurrencyDisplay();

	/**
	 * Retrieve the name the game mode would like to be used for the tab.
	 * If the name starts with in_ it is a key for an internationalized string 
	 * which should be looked up from the language bundle. 
	 * @param tab The tab to be queried.
	 * @return The name to be used. 
	 */
	public String getTabName(Tab tab);

	/**
	 * Should the tab be shown for characters in this game mode.  
	 * @param tab The tab to be queried.
	 * @return true if the tab should be displayed, false if not.
	 */
	public boolean getTabShown(Tab tab);
	
}
