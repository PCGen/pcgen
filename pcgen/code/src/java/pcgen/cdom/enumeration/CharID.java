/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.enumeration;

import java.util.Map;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.facet.base.AbstractStorageFacet;

/**
 * 
 * This Class is a Type Safe Constant. It is designed to hold a unique Character
 * Identifier in a type-safe fashion
 */
public final class CharID implements TypeSafeConstant, PCGenIdentifier
{

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	/**
	 * The ordinal of this Constant
	 */
	private final int ordinal;
	
	/**
	 * The DataSet under which the Character was created
	 */
	private final DataSetID datasetID;

	/**
	 * A view of the cache for this CharID. Generally useful for debuggers,
	 * since this is a consolidated point for the cache for a single
	 * CharID/PlayerCharacter (and useful to be here in CharID since there is
	 * now code that no longer has any PlayerCharacter reference).
	 */
	@SuppressWarnings("unused")
	private Map<Class<?>, Object> myFacetCache;

	private CharID(DataSetID dsid)
	{
		if (dsid == null)
		{
			throw new IllegalArgumentException("DataSetID cannot be null");
		}
		ordinal = ordinalCount++;
		datasetID = dsid;
	}

	/**
	 * Gets the ordinal of this Constant
	 */
	@Override
	public int getOrdinal()
	{
		return ordinal;
	}

	public DataSetID getDatasetID()
	{
		return datasetID;
	}

	public static CharID getID(DataSetID dsid)
	{
		CharID id = new CharID(dsid);
		id.myFacetCache = AbstractStorageFacet.peekAtCache(id);
		return id;
	}
}
