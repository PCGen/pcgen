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


/**
 * Title:        FOPResourcesChecker.java
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.30 $
 */
public final class FOPResourceChecker
{
	private static int missingResourceCount;
	private static StringBuffer resourceBuffer;
	private static final String whereToGetIt = "<a href=\"http://prdownloads.sourceforge.net/pcgen/pdf_new.zip\">pdf.zip</a>";
	static final String getItHereMsg = PropertyFactory.getString("in_FollowLink");
	static final String missingLibMsg = PropertyFactory.getString("MissingLibMessage").replace('|', '\n');

	/**
	 *
	 */
	static
	{
		missingResourceCount = 0;

		//optimize stringbuffer initial size (0 should be right length. Hopefully we don't get an error. :)
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
			return resourceBuffer.toString() + "\n" + getItHereMsg + whereToGetIt + "\n" + missingLibMsg;
		}

		return "";
	}

	/**
	 * Return TRUE if the resource exists in the jar
	 * @param forName
	 * @param jarName
	 * @param sb
	 * @return TRUE if the resource exists in the jar
	 */
	public static boolean hasResource(final String forName, final String jarName, StringBuffer sb)
	{
		try
		{
			Class.forName(forName);

			return true;
		}
		catch (ClassNotFoundException cnfex)
		{
			sb.append("Missing resource: ").append(jarName).append('\n');
		}
		catch (NoClassDefFoundError ncdfer)
		{
			sb.append("Missing dependency of resource: ").append(jarName).append('\n');
			Logging.errorPrint("Error loading class " + forName + ": " + ncdfer.toString(), ncdfer);
		}

		return false;
	}

	/**
	 * 
	 */
	private static void checkResource()
	{
		final String[] resources = 
		{
			"org.apache.fop.apps.Fop", "fop.jar", "org.apache.xalan.xslt.Process", "xalan-2.4.1.jar",
			"org.apache.batik.dom.svg.SVGDocumentFactory", "batik.jar"
		};

		for (int i = 0; i < (resources.length / 2); ++i)
		{
			if (!hasResource(resources[i * 2], resources[(i * 2) + 1], resourceBuffer))
			{
				++missingResourceCount;
			}
		}

		if (missingResourceCount > 0)
		{
//			resourceBuffer.append("Look for the missing files on http://pcgen.sourceforge.net");
		}
	}
}
