/*
 * JEPResourceChecker.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 30, 2003, 10:34 AM
 *
 * @(#) $Id$
 */
package pcgen.util;


/**
 * <code>JEPResourceChecker</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class JEPResourceChecker
{
	private static int missingResourceCount;
	private static StringBuffer resourceBuffer;
	private static final String whereToGetIt = "<a href=\"http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/*checkout*/pcgen/lib/jep-2.24.jar?rev=HEAD&content-type=text/plain\">jep-2.24.jar</a>";

	static
	{
		missingResourceCount = 0;

		//optimize stringbuffer initial size (0 should be right length. Hopefully we don't get an error. :)
		resourceBuffer = new StringBuffer(0);
		checkResource();
	}

	/**
	 * Get the number of missing resources
	 * @return the number of missing resources
	 */
	public static int getMissingResourceCount()
	{
		return missingResourceCount;
	}

	/**
	 * Get the missing resource message
	 * @return the missing resource message
	 */
	public static String getMissingResourceMessage()
	{
		if (missingResourceCount != 0)
		{
			return resourceBuffer.toString() + "\n" + FOPResourceChecker.getItHereMsg + whereToGetIt + "\n"
			+ FOPResourceChecker.missingLibMsg;
		}

		return "";
	}

	private static void checkResource()
	{
		if (!FOPResourceChecker.hasResource("org.nfunk.jep.JEP", "jep-2.24.jar", resourceBuffer))
		{
			++missingResourceCount;
		}
	}
}
