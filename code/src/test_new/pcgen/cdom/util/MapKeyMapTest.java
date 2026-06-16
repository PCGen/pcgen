/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.cdom.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.helper.Aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code MapKeyMapTest} test that the MapKeyMap
 * class is functioning correctly. 
 */
class MapKeyMapTest
{
	
	private static final String BREED = "shetland sheepdog";
	private static final String AGE = "1";
	private static final String NAME = "Mistletoe";

	/** The map key map. */
	private MapKeyMap mapKeyMap;
	
	private AspectName ageKey;
	private AspectName nameKey;
	private AspectName breedKey;

	private Aspect ageAspect;
	private Aspect nameAspect;
	private Aspect breedAspect;

	@BeforeEach
	void setUp() throws Exception
	{
		mapKeyMap = new MapKeyMap();
		ageKey = AspectName.getConstant("agE");
		nameKey = AspectName.getConstant("Name");
		breedKey = AspectName.getConstant("breed");
		
		List<Aspect> ageList = new ArrayList<>();
		ageAspect = new Aspect("age", AGE);
		ageList.add(ageAspect);
		mapKeyMap.addToMapFor(MapKey.ASPECT, ageKey, ageList);
		List<Aspect> nameList = new ArrayList<>();
		nameAspect = new Aspect("name", NAME);
		nameList.add(nameAspect);
		mapKeyMap.addToMapFor(MapKey.ASPECT, nameKey, nameList);
		List<Aspect> breedList = new ArrayList<>();
		breedAspect = new Aspect("breed", BREED);
		breedList.add(breedAspect);
		mapKeyMap.addToMapFor(MapKey.ASPECT, breedKey, breedList);

	}

	//TODO Tear Down

	/**
	 * Test retrieval of items from the map
	 */
	@Test
	public void testGet()
	{
		assertEquals(breedAspect, mapKeyMap
			.get(MapKey.ASPECT, breedKey).get(0), "Retrieve 3rd item by both keys");
		assertEquals(nameAspect, mapKeyMap
			.get(MapKey.ASPECT, nameKey).get(0), "Retrieve 2nd item by both keys");
		assertEquals(ageAspect, mapKeyMap
			.get(MapKey.ASPECT, ageKey).get(0), "Retrieve 1st item by both keys");
	}

	/**
	 * Test loading the map with another map.
	 */
	@Test
	public void testPutAll()
	{
		MapKeyMap newMap = new MapKeyMap();

		assertNull(newMap
				.get(MapKey.ASPECT, ageKey), "Expect an empty map intially");
		
		newMap.putAll(mapKeyMap);
		assertEquals(breedAspect, newMap
			.get(MapKey.ASPECT, breedKey).get(0), "Retrieve 3rd item by both keys");
		assertEquals(ageAspect, newMap
			.get(MapKey.ASPECT, ageKey).get(0), "Retrieve 1st item by both keys");
	}

	/**
	 * Test adding an entry to the map overwriting an existing entry.
	 */
	@Test
	public void testAddToMapFor()
	{
		assertEquals(ageAspect, mapKeyMap
			.get(MapKey.ASPECT, ageKey).get(0), "Validate initial value of age");
		Aspect newage = new Aspect("age", "2");
		List<Aspect> ageList = new ArrayList<>();
		ageList.add(newage);
		mapKeyMap.addToMapFor(MapKey.ASPECT, ageKey, ageList);
		assertEquals(newage, mapKeyMap
				.get(MapKey.ASPECT, ageKey).get(0), "Validate new value of age");
	}

	/**
	 * Test removing items from the list.
	 */
	@Test
	public void testRemoveFromListFor()
	{
		assertEquals(breedAspect, mapKeyMap
			.get(MapKey.ASPECT, breedKey).get(0), "Validate initial value of breed");
		assertTrue(mapKeyMap
			.removeFromMapFor(MapKey.ASPECT, breedKey), "Should be true as item is present");
		assertNull(mapKeyMap
				.get(MapKey.ASPECT, breedKey), "Validate breed is no longer present");
		assertFalse(mapKeyMap
			.removeFromMapFor(MapKey.ASPECT, breedKey), "Should be false as item is no longer present");
	}

	/**
	 * Test retrieval of the primary key set and that the retrieved set is 
	 * not a pointer to the master set in the MapKeyMap.
	 */
	@Test
	public void testGetKeySet()
	{
		Set<MapKey<?, ?>> keySet = mapKeyMap.getKeySet();

		assertEquals(1, keySet.size(), "only one primary key");
		assertEquals(MapKey.ASPECT, keySet
			.toArray()[0], "Only element should be an aspect");
		
		mapKeyMap.addToMapFor(MapKey.PROPERTY, "foo", "bar");
		assertEquals(
				1, keySet.size(), "Still only one primary key, returned set should be independant of main collection");
		assertFalse(keySet.contains(MapKey.PROPERTY), "Set should not include test yet");

		keySet = mapKeyMap.getKeySet();
		assertEquals(2, keySet.size(), "Now two primary keys");
		assertTrue(keySet.contains(MapKey.ASPECT), "Set should include aspect");
		assertTrue(keySet.contains(MapKey.PROPERTY), "Set should include test");
	}

	/**
	 * Test the containsMapFor method and the effect of adding and 
	 * removing items on it. 
	 */
	@Test
	public void testContainsMapFor()
	{
		assertTrue(mapKeyMap.containsMapFor(MapKey.ASPECT), "Should have ASPECT");
		assertFalse(mapKeyMap.containsMapFor(MapKey.PROPERTY), "Should not have TEST");

		mapKeyMap.addToMapFor(MapKey.PROPERTY, "foo", "bar");
		assertTrue(mapKeyMap.containsMapFor(MapKey.ASPECT), "Should have ASPECT");
		assertTrue(mapKeyMap.containsMapFor(MapKey.PROPERTY), "Should have TEST now");

		assertTrue(mapKeyMap
			.removeFromMapFor(MapKey.PROPERTY, "foo"), "Should be true as item is present");
		assertTrue(mapKeyMap.containsMapFor(MapKey.ASPECT), "Should have ASPECT");
		assertFalse(mapKeyMap.containsMapFor(MapKey.PROPERTY), "Should not have TEST");

		assertTrue(mapKeyMap
			.removeFromMapFor(MapKey.ASPECT, breedKey), "Should be true as item is present");
		assertTrue(mapKeyMap.containsMapFor(MapKey.ASPECT), "Should still have ASPECT");
		assertFalse(mapKeyMap.containsMapFor(MapKey.PROPERTY), "Should not have TEST");
	}
	
	
	/**
	 * Test the removal of a map by key.
	 */
	@Test
	public void testRemoveMapFor()
	{
		assertTrue(mapKeyMap
			.containsMapFor(MapKey.ASPECT), "Should have ASPECT");

		Map<AspectName, List<Aspect>> removed = mapKeyMap.removeMapFor(MapKey.ASPECT);
		assertFalse(mapKeyMap
			.containsMapFor(MapKey.ASPECT), "Should not have ASPECT");
		assertEquals(3,
			removed.size(), "Removed map should have all expected elements"
		);
		assertEquals(breedAspect, removed
			.get(breedKey).get(0), "Retrieve 3rd item");
		assertEquals(nameAspect, removed
			.get(nameKey).get(0), "Retrieve 2nd item");
		assertEquals(ageAspect, removed
			.get(ageKey).get(0), "Retrieve 1st item");
	}
}
