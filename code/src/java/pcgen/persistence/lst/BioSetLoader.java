/*
 * bioSetLoader.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on October 10, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import pcgen.core.BioSet;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 */
final class BioSetLoader extends LstLineFileLoader
{
	private static String regionName = Constants.s_NONE;
	BioSet bioSet = new BioSet();
	/**
	 * The age set (bracket) currently being processed.
	 * Used by the parseLine method to hold state between calls.
	 */
	int currentAgeSetIndex = 0;

	/** Creates a new instance of bioSetLoader */
	public BioSetLoader()
	{
		// Empty Constructor
	}

	/**
	 * clear the regionName
	 */
	public static void clear()
	{
		regionName = Constants.s_NONE;
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#loadLstFile(String)
	 */
	@Override
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		currentAgeSetIndex = 0;
		super.loadLstFile(fileName);
		Globals.setBioSet(bioSet);
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	@Override
	public void parseLine(String lstLine, URL sourceURL)
	{
		if (lstLine.startsWith("AGESET:"))
		{
			currentAgeSetIndex =
					bioSet.addToAgeMap(regionName, lstLine.substring(7),
						currentAgeSetIndex);
		}
		else
		{
			final StringTokenizer colToken =
					new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
			String colString;
			String raceName = "";
			List<String> preReqList = null;

			while (colToken.hasMoreTokens())
			{
				colString = colToken.nextToken();

				if (colString.startsWith("RACENAME:"))
				{
					raceName = colString.substring(9);
				}
				else if (colString.startsWith("REGION:"))
				{
					regionName = colString.substring(7);
				}
				else if (colString.startsWith("PRE")
					|| colString.startsWith("!PRE"))
				{
					if (preReqList == null)
					{
						preReqList = new ArrayList<String>();
					}

					preReqList.add(colString);
				}
				else
				{
					String aString = "";

					if (preReqList != null)
					{
						final StringBuffer sBuf = new StringBuffer(100);

						for (int i = 0, x = preReqList.size(); i < x; ++i)
						{
							sBuf.append('[').append(preReqList.get(i)).append(
								']');
						}

						aString = sBuf.toString();
					}

					bioSet.addToUserMap(regionName, raceName, colString
						+ aString, currentAgeSetIndex);
				}
			}
		}
	}
}
