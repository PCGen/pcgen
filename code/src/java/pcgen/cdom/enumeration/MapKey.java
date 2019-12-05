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
package pcgen.cdom.enumeration;

import java.text.MessageFormat;
import java.util.List;

import pcgen.base.lang.CaseInsensitiveString;
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
 * <p>
 * ListKeys are designed to store items in a CDOMObject in a type-safe
 * fashion. Note that it is possible to use the MapKey to cast the object to
 * the type of object stored by the ListKey. (This assists with Generics)
 */
public final class MapKey<K, V>
{

    /**
     * ASPECT - a map key.
     */
    public static final MapKey<AspectName, List<Aspect>> ASPECT = new MapKey<>();
    public static final MapKey<String, String> PROPERTY = new MapKey<>();
    public static final MapKey<Spell, HashMapToList<CDOMList<Spell>, Integer>> SPELL_PC_INFO = new MapKey<>();

    public static final MapKey<CDOMSingleRef<? extends PCClass>, Integer> APPLIED_CLASS = new MapKey<>();
    public static final MapKey<String, Integer> APPLIED_VARIABLE = new MapKey<>();
    public static final MapKey<String, String> QUALITY = new MapKey<>();
    public static final MapKey<AttackType, Integer> ATTACK_CYCLE = new MapKey<>();
    public static final MapKey<String, KitTable> KIT_TABLE = new MapKey<>();

    public static final MapKey<String, Integer> CRMOD = new MapKey<>();
    //TODO Ugh, using this stinks due to CIS
    public static final MapKey<CaseInsensitiveString, MessageFormat> INFO = new MapKey<>();
    public static final MapKey<CaseInsensitiveString, String[]> INFOVARS = new MapKey<>();

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
     * @return the object as the MapKey's value type
     */
    @SuppressWarnings("unchecked")
    public V cast(Object obj)
    {
        return (V) obj;
    }
}
