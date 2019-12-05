/*
 * Copyright 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;

/**
 * This is a Typesafe enumeration of legal Characteristics of an Association. It
 * is designed to act as an index to a specific Objects within an item that
 * forms associations.
 *
 * @param <T> The class of object stored by this AssociationKey.
 * @see pcgen.cdom.base.AssociatedObject
 * <p>
 * AssociationKeys are designed to store items in an AssociatedObject in a
 * type-safe fashion. Note that it is possible to use the AssociationKey to cast
 * the object to the type of object stored by the AssociationKey. (This assists
 * with Generics)
 */
public final class AssociationKey<T>
{

    /*
     * These items are used by the Load (Context) System to identify the owning
     * CDOMObject and the TOKEN that processed to load the item into the
     * Context.
     */
    public static final AssociationKey<CDOMObject> OWNER = new AssociationKey<>();

    public static final AssociationKey<String> TOKEN = new AssociationKey<>();

    /*
     * End Load (Context) items
     */

    /*
     * These items are used by Tokens to store relationship information to specific items.
     */

    public static final AssociationKey<SkillCost> SKILL_COST = new AssociationKey<>();

    public static final AssociationKey<Integer> SPELL_LEVEL = new AssociationKey<>();

    public static final AssociationKey<Boolean> KNOWN = new AssociationKey<>();

    public static final AssociationKey<List<String>> ASSOC_CHOICES = new AssociationKey<>();

    public static final AssociationKey<Nature> NATURE = new AssociationKey<>();

    public static final AssociationKey<CDOMSingleRef<AbilityCategory>> CATEGORY = new AssociationKey<>();

    public static final AssociationKey<String> CASTER_LEVEL = new AssociationKey<>();

    public static final AssociationKey<Formula> TIMES_PER_UNIT = new AssociationKey<>();

    public static final AssociationKey<String> TIME_UNIT = new AssociationKey<>();

    public static final AssociationKey<String> SPELLBOOK = new AssociationKey<>();

    public static final AssociationKey<String> DC_FORMULA = new AssociationKey<>();

    /*
     * End token items
     */

    /*
     * The following items are Associations used for Player Characters and thus
     * fall into the domain of CODE-1908 (to have the information stored in the
     * associations of these keys moved into facets)
     */

    /*
     * Note: SPECIALTY is best done after SubClassFacet is made type safe.
     * Making SubClassFacet type safe is gated by CODE-1928
     */
    public static final AssociationKey<String> SPECIALTY = new AssociationKey<>();

    /*
     * End Player Character items related to CODE-1908
     */

    private static CaseInsensitiveMap<AssociationKey<?>> map = null;

    private AssociationKey()
    {
        // Only allow instantiation here
    }

    /**
     * Casts an object with the Generics on this AssociationKey.
     *
     * @return An object cast to the Generics on this AssociationKey
     */
    @SuppressWarnings("unchecked")
    public T cast(Object obj)
    {
        return (T) obj;
    }

    public static <OT> AssociationKey<OT> getKeyFor(Class<OT> assocClass, String assocName)
    {
        if (map == null)
        {
            buildMap();
        }
        /*
         * CONSIDER This is actually not type safe, there is a case of asking
         * for a String a second time with a different Class that ObjectKey
         * currently does not handle. Two solutions: One, store this in a
         * Two-Key map and allow a String to map to more than one ObjectKey
         * given different output types (considered confusing) or Two, store the
         * Class and validate that with a an error message if a different class
         * is requested.
         */
        AssociationKey<OT> key = (AssociationKey<OT>) map.get(assocName);
        if (key == null)
        {
            key = new AssociationKey<>();
            map.put(assocName, key);
        }
        return key;
    }

    private static void buildMap()
    {
        map = new CaseInsensitiveMap<>();
        Field[] fields = AssociationKey.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof AssociationKey)
                    {
                        map.put(field.getName(), (AssociationKey<?>) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        if (map == null)
        {
            buildMap();
        }
        /*
         * CONSIDER Should this find a way to do a Two-Way Map or something to
         * that effect?
         */
        return map.entrySet()
                .stream()
                .filter(me -> me.getValue() == this)
                .findFirst()
                .map(me -> me.getKey().toString())
                .orElse("");
        // Error
    }
}
