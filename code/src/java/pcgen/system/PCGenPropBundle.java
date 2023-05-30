/*
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
 */
package pcgen.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import pcgen.io.ExportUtilities;
import pcgen.output.publish.OutputDB;
import pcgen.util.Logging;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is used to manage the properties of the PCGen application
 * itself, such as its version, release date, etc.
 *
 */
public final class PCGenPropBundle
{
	private static ResourceBundle d_properties;
	private static ResourceBundle autobuildProperties = null;

	/*
	 * This static initializer loads the resources from the PCGenProp resource bundle.
	 */
	static
	{
		d_properties = ResourceBundle.getBundle("pcgen.system.prop.PCGenProp");

		try
		{
			File autobuildProps = new File("autobuild.properties");
			if (autobuildProps.isFile() && autobuildProps.canRead())
			{
				FileInputStream fis = new FileInputStream(autobuildProps);
				autobuildProperties = new PropertyResourceBundle(fis);
			}
		}
		catch (MissingResourceException mre)
		{
			Logging.errorPrint("Failed to load autobuild.properties", mre);
			autobuildProperties = null;
		}
		catch (IOException e)
		{
			Logging.errorPrint("autobuildProperties. failed", e);
		}

		//Safe as d_properties was constructed earlier in this block
		try
		{
			TemplateModel wrappedVersion = ExportUtilities.getObjectWrapper().wrap(getVersionNumber());
			OutputDB.addGlobalModel("version", wrappedVersion);
		}
		catch (TemplateModelException e)
		{
			Logging.errorPrint("Failed to load version for FreeMarker", e);
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
		return getPropValue("MailingList", "https://pcgen.groups.io/g/main");
	}

	/**
	 * This method gets the Production Version Series, the major.minor version 
	 * of the prod release the current version is targeting.
	 * 
	 * @return String containing the Production Version Series number
	 */
	public static String getProdVersionSeries()
	{
		return getPropValue("ProdVersionSeries", null);
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
		return getPropValue("WWWHome", "http://pcgen.org/");
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
			try
			{
				result = d_properties.getString(propName);
			}
			catch (MissingResourceException mre)
			{
				Logging.errorPrint("Missing property %s", propName, mre);
				result = (StringUtils.isNotBlank(fallback) ? fallback : "Missing property " + propName);
			}
		}

		return result;
	}

	/**
	 * Retrieve the build number of the autobuild in which this PCGen instance 
	 * was built.
	 * @return The build number, or blank if unknown. 
	 */
	public static String getAutobuildNumber()
	{
		final String buildNumKey = "BuildNumber";
		if (autobuildProperties != null && autobuildProperties.containsKey(buildNumKey))
		{
			return autobuildProperties.getString(buildNumKey);
		}
		return "";
	}

	/**
	 * Retrieve the date of the autobuild in which this PCGen instance was 
	 * built.
	 * @return The build date, or blank if unknown. 
	 */
	public static String getAutobuildDate()
	{
		final String buildTimeKey = "BuildTime";
		if (autobuildProperties != null && autobuildProperties.containsKey(buildTimeKey))
		{
			return autobuildProperties.getString(buildTimeKey);
		}
		return "";
	}

	/**
	 * @return A display formatted version of the autobuild details, or blank if unknown.
	 */
	static String getAutobuildString()
	{
		String autobuildNumber = getAutobuildNumber();
		String autobuildDate = getAutobuildDate();
		if (StringUtils.isNotBlank(autobuildNumber))
		{
			return " autobuild #" + autobuildNumber + " built on " + autobuildDate;
		}
		return "";
	}

}
