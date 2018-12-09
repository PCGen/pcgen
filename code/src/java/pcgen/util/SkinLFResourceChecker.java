/*
 * Copyright 2001 (C) Jason Buchanan
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
 */
package pcgen.util;

public final class SkinLFResourceChecker
{
	private static boolean hasMissingResources = false;
	private static final StringBuilder resourceBuffer;

	static
	{
		//optimize StringBuilder initial size (0 should be right length. Hopefully we don't get an error. :)
		resourceBuffer = new StringBuilder(0);
		checkResource();
	}

	private SkinLFResourceChecker()
	{
	}

	/**
	 * Get the missing resource count
	 * @return the missing resource count
	 */
	public static boolean hasMissingResources()
	{
		return hasMissingResources;
	}

	private static void checkResource()
	{
		if (!ResourceChecker.hasResource("com.l2fprod.gui.plaf.skin.SkinLookAndFeel", "skinlf.jar", resourceBuffer))
		{
			hasMissingResources = true;
		}
	}
}
