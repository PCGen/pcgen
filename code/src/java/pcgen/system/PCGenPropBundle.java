/*
 * MainAbout.java
 * Copyright 2001 (C) Tom Epperly <tomepperly@home.com>
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
 * Created on August 25, 2003, 12:00 PM
 */
package pcgen.system;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used to manage the properties of the PCGen application
 * itself, such as its version, release date, etc.  Created during refactoring
 * for RFE #782127.
 *
 * <p>
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 * @author sage_sam
 */
public class PCGenPropBundle
{
	private static ResourceBundle d_properties = null;

	/**
	 * This static initializer loads the resources from the PCGenProp resource bundle.
	 */
	static
	{
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/resources/prop/PCGenProp");
		}
		catch (MissingResourceException mre)
		{
			d_properties = null;
		}
	}

	/**
	 * Constructor for PCGenPropBundle.
	 */
	private PCGenPropBundle()
	{
		super();
	}

	/**
	 * This method gets the Code Monkeys.
	 * @return String containing the Code Monkeys
	 */
	public static String getCodeMonkeys()
	{
		return getPropValue("CodeMonkeys", null);
	}

	/**
	 * This method gets the Engineering Monkeys.
	 * @return String containing the Engineering Monkeys
	 */
	public static String getEngineeringMonkeys()
	{
		return getPropValue("EngineeringMonkeys", null);
	}

	/**
	 * This method gets the Head Code Monkey.
	 * @return String containing the Head Code Monkey
	 */
	public static String getHeadCodeMonkey()
	{
		return getPropValue("HeadCodeMonkey", "Bryan McRoberts");
	}

	/**
	 * This method gets the List Monkeys.
	 * @return String containing the List Monkeys
	 */
	public static String getListMonkeys()
	{
		return getPropValue("ListMonkeys", null);
	}

	/**
	 * This method gets the PCGen Mailing List.
	 * @return String containing the PCGen Mailing List
	 */
	public static String getMailingList()
	{
		return getPropValue("MailingList", "http://groups.yahoo.com/group/pcgen");
	}

	/**
	 * This method gets the Release Date.
	 * @return String containing the Release Date
	 */
	public static String getReleaseDate()
	{
		return getPropValue("ReleaseDate", null);
	}

	/**
	 * This method gets the Test Monkeys.
	 * @return String containing the Test Monkeys
	 */
	public static String getTestMonkeys()
	{
		return getPropValue("TestMonkeys", null);
	}

	/**
	 * This method gets the Version Number.
	 * @return String containing the Version Number
	 */
	public static String getVersionNumber()
	{
		return getPropValue("VersionNumber", null);
	}

	/**
	 * This method gets the WWW Home page.
	 * @return String containing the WWW Home page
	 */
	public static String getWWWHome()
	{
		return getPropValue("WWWHome", "http://pcgen.sourceforge.net/");
	}

	/**
	 * This method gets a Property/resource value.  If the value is not available,
	 * the provided fallback (or a default, if the fallback is null) will be returned.
	 * @param propName the name of the resource/property to get
	 * @param fallback the default value or null if a generic string should be used
	 * @return String containing the displayable property/resource value
	 */
	private static String getPropValue(String propName, String fallback)
	{
		String result = null;

		// Make sure the value is retrievable (no NullPointerExceptions)				
		if (propName != null)
		{
			if (d_properties != null)
			{
				try
				{
					result = d_properties.getString(propName);
				}
				catch (MissingResourceException mre)
				{
					// ignore; will handle later
				}
			}
		}

		// Set a missing string if the value is missing		
		if (result == null)
		{
			if (fallback != null)
			{
				result = fallback;
			}
			else
			{
				result = "Misting property " + propName;
			}
		}

		return result;
	}
}
