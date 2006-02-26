/**
 * TestHelper.java
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
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
 * Current Version: $Revision: 1.10 $
 * Last Editor:     $Author: karianna $
 * Last Edited:     $Date: 2006/02/13 15:47:26 $
 * 
 */

package pcgen.util;

import java.lang.String;
import java.net.URL;
import java.util.StringTokenizer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import gmgen.pluginmgr.PluginLoader;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.GameMode;
import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.EquipmentLoader;

/**
 * Helps Junit tests
 */
public class TestHelper {

	private static boolean loaded = false;

	/**
	 * Make some size adjustments
	 */
	public static void makeSizeAdjustments () {
		String sizes = "Fine|Diminutive|Tiny|Small|Medium|Large|Huge|Gargantuan|Colossal";
		StringTokenizer aTok = new StringTokenizer(sizes, "|");
		GameMode gamemode = SystemCollections.getGameModeNamed("3.5");
		if (gamemode == null)
		{
			gamemode = new GameMode("3.5");
			SystemCollections.addToGameModeList(gamemode);
		}
		SettingsHandler.setGame("3.5");
		while (aTok.hasMoreTokens()) {
			String name = aTok.nextToken();
			String abb  = name.substring(0, 1);

			SizeAdjustment sa = new SizeAdjustment();
			
			sa.setName(name);
			sa.setAbbreviation(abb);

			gamemode.addToSizeAdjustmentList(sa);
		}
		gamemode.getSizeAdjustmentNamed("Medium").setIsDefaultSize(true);
	}

	/**
	 * Make some equipment
	 * @param input
	 * @return true if OK
	 */
	public static boolean makeEquipment (String input)
	{
		if (!loaded)
		{
			loadPlugins();
		}
		Equipment eq = new Equipment();
		try
		{
			URL x = null;
			EquipmentLoader.parseLine(eq, input, x, 0);
			EquipmentList.addEquipment(eq);
			return true;
		}
		catch (Exception e)
		{
			// TODO Deal with Exception?
		}
		return false;
	}

	/**
	 * Load the plugins
	 */
	public static void loadPlugins()
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		loaded = true;
	}

	/**
	 * Get the field related to a name
	 * @param aClass
	 * @param fieldName
	 * @return the field related to a name in the class
	 */
	static public Object findField (Class aClass, String fieldName)
	{
		try {
			while (true)
			{
				final List theFields = Arrays.asList(aClass.getDeclaredFields());
				Iterator it = theFields.iterator();
				while (it.hasNext())
				{
					final Field f = (Field) it.next();
					if (f.getName().equals(fieldName))
					{
						return f;
					}
				}
				if (!aClass.getName().equals("Object"))
				{
					aClass = aClass.getSuperclass();
				}
				else
				{
					break;
				}
			}

		} catch (SecurityException e) {
			System.out.println(e);
		}
		return null;
	}
}
