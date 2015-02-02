/*
 * Copyright (c) Thomas Parker, 2015.
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
package pcgen.output.publish;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.output.base.ModelFactory;
import pcgen.output.factory.ItemModelFactory;
import pcgen.output.factory.SetModelFactory;
import freemarker.template.TemplateModel;

/**
 * OutputDB is the OutputDatabase for building the Map to be provided to
 * FreeMarker for output.
 */
public final class OutputDB
{

	private OutputDB()
	{
		//Utility class should not be constructed
	}

	/**
	 * The Map of string names to output models
	 */
	private static DoubleKeyMap<Object, Object, ModelFactory> outModels =
			new DoubleKeyMap<Object, Object, ModelFactory>(
				CaseInsensitiveMap.class, CaseInsensitiveMap.class);

	/**
	 * Registers a new ModelFactory to be used in output
	 * 
	 * @param name
	 *            The interpolation for the ModelFactory
	 * @param modelFactory
	 *            The ModelFactory to be used to generate the Models when the
	 *            output Map is built
	 */
	public static void registerModelFactory(String name,
		ModelFactory modelFactory)
	{
		if (modelFactory == null)
		{
			throw new IllegalArgumentException("Model Factory may not be null");
		}
		String[] locationElements = name.split("\\.");
		if (locationElements.length == 0)
		{
			throw new IllegalArgumentException(
				"Name may not be null or empty: " + name);
		}
		if (locationElements.length > 2)
		{
			throw new IllegalArgumentException(
				"Name may only contain zero or one period");
		}
		String secondName =
				(locationElements.length == 1) ? "" : locationElements[1];
		ModelFactory old =
				outModels.put(locationElements[0], secondName, modelFactory);
		if (old != null)
		{
			throw new UnsupportedOperationException(
				"Cannot have two Output Models using the same name: " + name);
		}
	}

	/**
	 * Registers a new ItemFacet with the OutputDatabase using the given name as
	 * the interpolation for fetching information from the given ItemFacet.
	 * 
	 * @param name
	 *            The name as the interpolation for fetching information from
	 *            the given ItemFacet during output
	 * @param facet
	 *            The ItemFacet to be registered with the given name
	 */
	public static void register(String name, ItemFacet<CharID, ?> facet)
	{
		registerModelFactory(name, new ItemModelFactory(facet));
	}

	/**
	 * Registers a new SetFacet with the OutputDatabase using the given name as
	 * the interpolation for fetching information from the given SetFacet.
	 * 
	 * @param name
	 *            The name as the interpolation for fetching information from
	 *            the given SetFacet during output
	 * @param facet
	 *            The SetFacet to be registered with the given name
	 */
	public static void register(String name, SetFacet<CharID, ?> facet)
	{
		registerModelFactory(name, new SetModelFactory(facet));
	}

	/**
	 * Builds the PlayerCharacter data model for the given CharID.
	 * 
	 * @param id
	 *            The CharID for which the data model should be built
	 * @return A Map of the data model for the PlayerCharacter identified by the
	 *         given CharID
	 */
	public static Map<String, Object> buildDataModel(CharID id)
	{
		Map<String, Object> input = new HashMap<String, Object>();
		for (Object k1 : outModels.getKeySet())
		{
			for (Object k2 : outModels.getSecondaryKeySet(k1))
			{
				ModelFactory modelFactory = outModels.get(k1, k2);
				TemplateModel model = modelFactory.generate(id);
				String k1String = k1.toString();
				if ("".equals(k2.toString()))
				{
					input.put(k1String, model);
				}
				else
				{
					ensureMap(input, k1String);
					Map<Object, Object> m =
							(Map<Object, Object>) input.get(k1String);
					m.put(k2.toString(), model);
				}
			}
		}
		return input;
	}

	private static void ensureMap(Map<String, Object> input, String k1String)
	{
		if (!input.containsKey(k1String))
		{
			input.put(k1String, new HashMap<Object, Object>());
		}
	}

	/**
	 * Returns true if the given interpolation is legal based on the items
	 * registered with OutputDB.
	 * 
	 * @param interpolation
	 *            The interpolation to be checked to see if it is legal
	 * @return true if the given interpolation is legal based on the items
	 *         registered with OutputDB; false otherwise
	 */
	public static boolean isLegal(String interpolation)
	{
		return outModels.containsKey(interpolation);
	}

	/**
	 * Resets the Output Database, to be used when sources are purged/reloaded
	 * or around testing
	 */
	public static void reset()
	{
		outModels.clear();
	}

}
