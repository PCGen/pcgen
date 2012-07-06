/*
 * MapKey.java
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
 *
 * Created on 11/09/2008 19:28:45
 *
 * $Id: $
 */
package pcgen.cdom.enumeration;

import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.helper.Aspect;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.kit.KitTable;
import pcgen.core.spell.Spell;
import pcgen.util.enumeration.AttackType;

/**
 * This is a Typesafe enumeration of legal Map Characteristics of an object. It
 * is designed to act as an index to a specific Object items within a
 * CDOMObject.
 * 
 * ListKeys are designed to store items in a CDOMObject in a type-safe
 * fashion. Note that it is possible to use the MapKey to cast the object to
 * the type of object stored by the ListKey. (This assists with Generics)
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public final class MapKey<K, V>
{

	/** ASPECT - a map key. */
	public static final MapKey<AspectName, List<Aspect>> ASPECT = new MapKey<AspectName, List<Aspect>>();
	public static final MapKey<String, String> PROPERTY = new MapKey<String, String>();
	public static final MapKey<Spell, HashMapToList<CDOMList<Spell>, Integer>> SPELL_MASTER_INFO = new MapKey<Spell, HashMapToList<CDOMList<Spell>, Integer>>();
	public static final MapKey<Spell, HashMapToList<CDOMList<Spell>, Integer>> SPELL_PC_INFO = new MapKey<Spell, HashMapToList<CDOMList<Spell>, Integer>>();

	public static final MapKey<CDOMSingleRef<? extends PCClass>, Integer> APPLIED_CLASS = new MapKey<CDOMSingleRef<? extends PCClass>, Integer>();
	public static final MapKey<String, Integer> APPLIED_VARIABLE = new MapKey<String, Integer>();
	public static final MapKey<String, String> QUALITY = new MapKey<String, String>();
	public static final MapKey<AttackType, Integer> ATTACK_CYCLE = new MapKey<AttackType, Integer>();
	public static final MapKey<String, KitTable> KIT_TABLE = new MapKey<String, KitTable>();

	/**
	 * Private constructor to prevent instantiation of this class.
	 */
	private MapKey()
	{
		//Only allow instantation here
	}

	/**
	 * Cast an object into the MapKey's value type
	 * 
	 * @param obj the object to cast
	 * 
	 * @return the object as the MapKey's value type
	 */
	public V cast(Object obj)
	{
		return (V) obj;
	}
}
