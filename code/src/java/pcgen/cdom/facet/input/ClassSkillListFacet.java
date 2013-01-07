/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.facet.input;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * ClassSkillListFacet stores the ClassSkillListFacet choices for a
 * PCClass of a Player Character
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ClassSkillListFacet extends AbstractStorageFacet implements
		ClassLevelChangeListener
{
	private final Class<?> thisClass = getClass();

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private ClassFacet classFacet;

	/**
	 * Returns the type-safe CacheInfo for this ClassSkillListFacet and the
	 * given CharID. Will return a new, empty CacheInfo if no ClassSkillList
	 * information has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by ClassSkillListFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than
	 * ClassSkillListFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private Map<PCClass, Set<ClassSkillList>> getConstructingInfo(CharID id)
	{
		Map<PCClass, Set<ClassSkillList>> rci = getInfo(id);
		if (rci == null)
		{
			rci = new HashMap<PCClass, Set<ClassSkillList>>();
			setCache(id, thisClass, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe CacheInfo for this ClassSkillListFacet and the
	 * given CharID. May return null if no ClassSkillList information has been
	 * set for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by ClassSkillListFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than
	 * ClassSkillListFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no ClassSkillList information has been set for
	 *         the Player Character.
	 */
	@SuppressWarnings("unchecked")
	private Map<PCClass, Set<ClassSkillList>> getInfo(CharID id)
	{
		return (Map<PCClass, Set<ClassSkillList>>) getCache(id, thisClass);
	}

	/**
	 * Adds a new PCClass to this ClassSkillListFacet for the given
	 * ClassSkillList and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Character for which the
	 *            ClassSkillList is being added
	 * @param pcc
	 *            The PCClass to which the given ClassSkillList is being added,
	 *            for the Player Character identified by the given CharID
	 * @param csl
	 *            The ClassSkillList being added
	 */
	public void add(CharID id, PCClass pcc, ClassSkillList csl)
	{
		if (pcc == null)
		{
			throw new IllegalArgumentException("PCClass for add cannot be null");
		}
		if (csl == null)
		{
			throw new IllegalArgumentException(
				"ClassSkillList for add cannot be null");
		}
		Map<PCClass, Set<ClassSkillList>> map = getConstructingInfo(id);
		Set<ClassSkillList> cslSet = map.get(pcc);
		if (cslSet == null)
		{
			cslSet = new WrappedMapSet<ClassSkillList>(IdentityHashMap.class);
			map.put(pcc, cslSet);
		}
		cslSet.add(csl);
	}

	/**
	 * Removes a ClassSkillList from this ClassSkillListFacet for the given
	 * PCClass and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Character for which the
	 *            ClassSkillList is being removed
	 * @param pcc
	 *            The PCClass from which the given ClassSkillList is being
	 *            removed
	 * @param csl
	 *            The ClassSkillList to remove for the given PCClass and Player
	 *            Character identified by the given CharID
	 */
	public void remove(CharID id, PCClass pcc, ClassSkillList csl)
	{
		if (pcc == null)
		{
			throw new IllegalArgumentException(
				"PCClass for remove cannot be null");
		}
		if (csl == null)
		{
			throw new IllegalArgumentException(
				"ClassSkillList for remove cannot be null");
		}
		Map<PCClass, Set<ClassSkillList>> map = getInfo(id);
		if (map != null)
		{
			Set<ClassSkillList> cslSet = map.get(pcc);
			if (cslSet != null)
			{
				if (cslSet.remove(csl) && cslSet.isEmpty())
				{
					map.remove(pcc);
				}
			}
		}
	}

	/**
	 * Removes all ClassSkillLists from this ClassSkillListFacet for the given
	 * PCClass and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Character for which the
	 *            ClassSkillList is being removed
	 * @param pcc
	 *            The PCClass from which the ClassSkillLists are being removed
	 */
	public void removeAll(CharID id, PCClass pcc)
	{
		if (pcc == null)
		{
			throw new IllegalArgumentException(
				"PCClass for remove cannot be null");
		}
		Map<PCClass, Set<ClassSkillList>> map = getInfo(id);
		if (map != null)
		{
			map.remove(pcc);
		}
	}

	/**
	 * Returns true if this ClassSkillListFacet contains the given
	 * ClassSkillList for the given PCClass and Player Character identified by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character to be tested to
	 *            see if this ClassSkillListFacet contains the given
	 *            ClassSkillList for the given PCClass
	 * @param pcc
	 *            The PCClass for which the given ClassSkillList is being
	 *            checked
	 * @param csl
	 *            The ClassSkillList to be tested to see if if this
	 *            ClassSkillListFacet contains the given ClassSkillList for the
	 *            given PCClass
	 * @return true if this ClassSkillListFacet contains the given PCClass for
	 *         the given ClassSkillList and Player Character identified by the
	 *         given CharID; false otherwise
	 */
	public boolean contains(CharID id, PCClass pcc, ClassSkillList csl)
	{
		Map<PCClass, Set<ClassSkillList>> map = getInfo(id);
		if (map == null)
		{
			return false;
		}
		Set<ClassSkillList> cslSet = map.get(pcc);
		return (cslSet != null) && cslSet.contains(csl);
	}

	/**
	 * Returns a Collection of ClassSkillList objects contained by the given
	 * PCClass and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Collection of ClassSkillList objects should be returned for
	 *            the given PCClass
	 * @param pcc
	 *            The PCClass for which the given ClassSkillList Collection is
	 *            being returned
	 * @return a Collection of ClassSkillList objects contained by the given
	 *         PCClass and Player Character identified by the given CharID
	 */
	public Collection<ClassSkillList> getSet(CharID id, PCClass pcc)
	{
		Map<PCClass, Set<ClassSkillList>> map = getInfo(id);
		if (map == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(map.get(pcc));
	}

	/**
	 * Copies the contents of the ClassSkillListFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in ClassSkillListFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to ClassSkillListFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the ClassSkillListFacet of one
	 * Player Character will only impact the Player Character where the
	 * ClassSkillListFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Map<PCClass, Set<ClassSkillList>> map = getInfo(source);
		if (map != null)
		{
			for (Entry<PCClass, Set<ClassSkillList>> fme : map.entrySet())
			{
				PCClass pcc = fme.getKey();
				for (ClassSkillList csl : fme.getValue())
				{
					add(copy, pcc, csl);
				}
			}
		}
	}

	@Override
	public void levelChanged(ClassLevelChangeEvent lce)
	{
		if ((lce.getOldLevel() == 0) && (lce.getNewLevel() > 0))
		{
			PCClass cl = lce.getPCClass();
			CharID id = lce.getCharID();
			TransitionChoice<ClassSkillList> csc =
					cl.get(ObjectKey.SKILLLIST_CHOICE);
			if (csc != null)
			{
				removeAll(id, cl);
				PlayerCharacter pc = trackingFacet.getPC(id);
				for (ClassSkillList st : csc.driveChoice(pc))
				{
					add(id, cl, st);
				}
			}
		}
		else if ((lce.getOldLevel() > 0) && (lce.getNewLevel() == 0))
		{
			removeAll(lce.getCharID(), lce.getPCClass());
		}
	}

	@Override
	public void levelObjectChanged(ClassLevelObjectChangeEvent lce)
	{
		//ignore
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void init()
	{
		classFacet.addLevelChangeListener(this);
	}
}
