/*
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 *
 */
package pcgen.persistence.lst;

import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

import java.net.URI;

/**
 * This class is a LstFileLoader used to load character locations.
 *
 * <p>
 *
 */
public class LocationLoader extends LstLineFileLoader
{
	private int traitType = -1;

	/**
	 * Constructor for TraitLoader.
	 */
	public LocationLoader()
	{
		super();
	}

	@Override
	public void loadLstFile(LoadContext context, URI source) throws PersistenceLayerException
	{
		traitType = -1;
		super.loadLstFile(context, source);
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(LoadContext, String, URI)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		if (lstLine.charAt(0) != '[')
		{
			switch (traitType)
			{
				case 0:
					SystemCollections.addToLocationList(lstLine.intern(), gameMode);

					break;

				case 1:
					SystemCollections.addToBirthplaceList(lstLine.intern(), gameMode);

					break;

				case 2:
					SystemCollections.addToCityList(lstLine.intern(), gameMode);

					break;

				default:
					break;
			}
		}
		else
		{
			if (lstLine.startsWith("[LOCATION]"))
			{
				traitType = 0;
			}
			else if (lstLine.startsWith("[BIRTHPLACE]"))
			{
				traitType = 1;
			}
			else if (lstLine.startsWith("[CITY]"))
			{
				traitType = 2;
			}
			else
			{
				traitType = -1;
			}
		}
	}
}
