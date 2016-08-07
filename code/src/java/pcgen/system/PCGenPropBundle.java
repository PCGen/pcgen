/*
 * PCGenPropBundle.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import pcgen.output.publish.OutputDB;
import pcgen.util.Logging;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

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
	private static ResourceBundle autobuildProperties = null;
	private static ResourceBundle svnProperties = null;

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
		
		try
		{
			File svnProps = new File("svn.properties");
			if (svnProps.isFile() && svnProps.canRead())
			{
				FileInputStream fis = new FileInputStream(svnProps);
				svnProperties = new PropertyResourceBundle(fis);
			}
		}
		catch (MissingResourceException mre)
		{
			Logging.errorPrint("Failed to load autobuild.properties", mre);
			svnProperties = null;
		}
		catch (IOException e)
		{
			Logging.errorPrint("Failed to load autobuild.properties", e);
			svnProperties = null;
		}
		//Safe as d_properties was constructed earlier in this block
		try
		{
			TemplateModel wrappedVersion =
					ObjectWrapper.DEFAULT_WRAPPER.wrap(getVersionNumber());
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
		return getPropValue("MailingList", "http://groups.yahoo.com/group/pcgen");
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
				result = "Missing property " + propName;
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
		if (autobuildProperties != null
			&& autobuildProperties.containsKey(buildNumKey))
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
		if (autobuildProperties != null
			&& autobuildProperties.containsKey(buildTimeKey))
		{
			return autobuildProperties.getString(buildTimeKey);
		}
		return "";
	}

	/**
	 * @return A display formatted version of the autobuild details, or blank if unknown. 
	 */
	public static String getAutobuildString()
	{
		String autobuildNumber = getAutobuildNumber();
		String autobuildDate = getAutobuildDate();
		if (StringUtils.isNotBlank(autobuildNumber))
		{
			return " autobuild #" + autobuildNumber + " built on "
				+ autobuildDate;
		}
		return "";
	}

	/**
	 * Retrieve the subversion revision number from which this PCGen instance 
	 * was built.
	 * @return The SVN revision number, or blank if unknown. 
	 */
	public static String getSvnRevisionNumber()
	{
		final String svnRevNumKey = "svnrevision";
		if (svnProperties != null && svnProperties.containsKey(svnRevNumKey))
		{
			return svnProperties.getString(svnRevNumKey);
		}
		return "";
	}

	/**
	 * @return A display formatted version of the SVN revision, or blank if unknown. 
	 */
	public static String getSvnRevisionString()
	{
		String svnRevisionNumber = getSvnRevisionNumber();
		if (StringUtils.isNotBlank(svnRevisionNumber))
		{
			return " r" + svnRevisionNumber;
		}
		return "";
	}
}
