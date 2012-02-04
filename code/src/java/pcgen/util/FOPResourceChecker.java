/*
 * FOPResourceChecker.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.util;

import pcgen.system.LanguageBundle;

/**
 * This class checks that PCGen has the right resources in order to run Fop
 */
public final class FOPResourceChecker
{
	/** Count of the number of missing resources */
	private static int missingResourceCount;

	/** A buffer containing the resources */
	private static StringBuffer resourceBuffer;

	/** String containing URL link if there are missing resources */
	private static final String whereToGetIt =
			"<a href=\"http://prdownloads.sourceforge.net/pcgen/pdf_new.zip\">pdf.zip</a>";

	/** Handle to resource bundle message for where to get missing resources */
	static final String getItHereMsg =
			LanguageBundle.getString("in_FollowLink");

	/** 
	 * Handle to resource bundle message for when there is a missing resource
	 *  
	 * TODO Why does this have a hard coded line separator? JK070115
	 */
	static final String missingLibMsg =
			LanguageBundle.getString("MissingLibMessage").replace('|', '\n');

	// This will automatically run once when this class is referenced 
	static
	{
		missingResourceCount = 0;
		resourceBuffer = new StringBuffer(0);
		checkResource();
	}

	/**
	 * Get the missing resource count
	 * @return missing resource count
	 */
	public static int getMissingResourceCount()
	{
		return missingResourceCount;
	}

	/**
	 * Get the missing resource message
	 * @return missing resource message
	 */
	public static String getMissingResourceMessage()
	{
		if (missingResourceCount != 0)
		{
			// TODO Why does this have hardcoded line separators? JK070115
			return resourceBuffer.toString() + "\n" + getItHereMsg
				+ whereToGetIt + "\n" + missingLibMsg;
		}
		return "";
	}

	/**
	 * Return TRUE if the resource exists in the jar
	 * 
	 * @param resourceName
	 * @param jarName
	 * @param sb
	 * @return TRUE if the resource exists in the jar
	 */
	public static boolean hasResource(final String resourceName,
		final String jarName, StringBuffer sb)
	{
		try
		{
			Class.forName(resourceName);
			return true;
		}
		catch (ClassNotFoundException cnfex)
		{
			// TODO Why does this have a hard coded line separator? JK070115
			sb.append("Missing resource: ").append(jarName).append('\n');
		}
		catch (NoClassDefFoundError ncdfer)
		{
			// TODO Why does this have a hard coded line separator? JK070115
			sb.append("Missing dependency of resource: ").append(jarName)
				.append('\n');
			Logging.errorPrint("Error loading class " + resourceName + ": "
				+ ncdfer.toString(), ncdfer);
		}
		return false;
	}

	/**
	 * Helper method, checks that the resources are in the corresponding jar file
	 */
	private static void checkResource()
	{
		final String[] resources =
				{"org.apache.fop.apps.Fop", "fop.jar",
					"org.apache.xalan.xslt.Process", "xalan-2.5.2.jar",
					"org.apache.batik.dom.svg.SVGDocumentFactory", "batik.jar"};

		String resource = null;
		String jar = null;

		for (int i = 0; i < (resources.length / 2); ++i)
		{
			resource = resources[i * 2];
			jar = resources[(i * 2) + 1];
			if (!hasResource(resource, jar, resourceBuffer))
			{
				++missingResourceCount;
			}
		}
	}
}
