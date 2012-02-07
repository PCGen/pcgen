/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;
import pcgen.core.character.SpellBook;

/**
 * SpellBookFacet is a Facet that tracks the SpellBooks possessed by a Player
 * Character.
 */
public class SpellBookFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<Equipment>
{
	private EquipmentFacet equipmentFacet;

	/**
	 * Triggered when one of the Facets to which SpellBookFacet listens fires a
	 * DataFacetChangeEvent to indicate a Equipment was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<Equipment> dfce)
	{
		Equipment eq = dfce.getCDOMObject();
		if (eq.isType(Constants.TYPE_SPELLBOOK))
		{
			CharID id = dfce.getCharID();
			String baseBookname = eq.getName();
			String bookName = eq.getName();
			int qty = (int) eq.qty();
			for (int i = 0; i < qty; i++)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + (i + 1);
				}
				SpellBook book =
						new SpellBook(bookName, SpellBook.TYPE_SPELL_BOOK);
				book.setEquip(eq);
				if (!containsBookNamed(id, book.getName()))
				{
					add(id, book);
				}
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which SpellBookFacet listens fires a
	 * DataFacetChangeEvent to indicate a Equipment was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<Equipment> dfce)
	{
		//Ignore - for now this is one in PlayerCharacter...
	}

	public void addAll(CharID id, Collection<SpellBook> list)
	{
		for (SpellBook sb : list)
		{
			add(id, sb);
		}
	}

	public void add(CharID id, SpellBook sb)
	{
		if (sb == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Map<String, SpellBook> sbMap = getConstructingCachedMap(id);
		String name = sb.getName();
		sbMap.put(name, sb);
	}

	public void removeAll(CharID id)
	{
		removeCache(id, getClass());
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractSourcedListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSourcedListFacet for the Player Character.
	 */
	private Map<String, SpellBook> getCachedMap(CharID id)
	{
		return (Map<String, SpellBook>) getCache(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AbstractSourcedListFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<String, SpellBook> getConstructingCachedMap(CharID id)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = new LinkedHashMap<String, SpellBook>();
			setCache(id, getClass(), componentMap);
		}
		return componentMap;
	}

	public SpellBook getBookNamed(CharID id, String name)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return null;
		}
		return componentMap.get(name);
	}

	public Collection<String> getBookNames(CharID id)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentMap.keySet());
	}

	public Collection<SpellBook> getBooks(CharID id)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(componentMap.values());
	}

	public boolean containsBookNamed(CharID id, String name)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		return (componentMap != null) && componentMap.containsKey(name);
	}

	public double getCount(CharID id)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		return (componentMap == null) ? 0 : componentMap.size();
	}

	public void removeBookNamed(CharID id, String name)
	{
		Map<String, SpellBook> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			componentMap.remove(name);
		}
	}
	
	public void setEquipmentFacet(EquipmentFacet equipmentFacet)
	{
		this.equipmentFacet = equipmentFacet;
	}

	public void init()
	{
		equipmentFacet.addDataFacetChangeListener(this);
	}

	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Map<String, SpellBook> map = getCachedMap(source);
		if (map != null)
		{
			addAll(copy, map.values());
		}
	}
}
