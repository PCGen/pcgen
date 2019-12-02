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
import java.util.Map;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FixedStringList;

/**
 * 
 * This is a Typesafe enumeration of legal Characteristics of an Association. It
 * is designed to act as an index to a specific Objects within an item that
 * forms associations.
 * 
 * @see pcgen.cdom.base.AssociatedObject
 * 
 * AssociationKeys are designed to store items in an AssociatedObject in a
 * type-safe fashion. Note that it is possible to use the AssociationKey to cast
 * the object to the type of object stored by the AssociationKey. (This assists
 * with Generics)
 * 
 * @param <T>
 *            The class of object stored by this AssociationKey.
 */
public final class AssociationListKey<T>
{

	/*
	 * CHOICES is a list used to store the choices made in a CHOOSE in a String
	 * (non-type-safe) fashion
	 * 
	 * This should eventually be retired as the CHOOSE system becomes type safe.
	 * This is closely related to the transition to ChooseSelectionActor instead
	 * of ChooseResultActor, so this item is related to CODE-1902
	 */
	public static final AssociationListKey<FixedStringList> CHOICES = new AssociationListKey<>();

	/*
	 * ADD is a widely used key used to store the information about items added to the PC.
	 * These items are stored against he TransitionChoice.
	 * This is a candidate to be sunset as part of CODE-1908
	 */
	public static final AssociationListKey<Object> ADD = new AssociationListKey<>();

	/*
	 * End non-local token-related keys
	 */

	private static CaseInsensitiveMap<AssociationListKey<?>> map = null;

	private AssociationListKey()
	{
		// Only allow instantiation here
	}

	/**
	 * Casts an object with the Generics on this AssociationListKey.
	 * 
	 * @return An object cast to the Generics on this AssociationListKey
	 */
	@SuppressWarnings("unchecked")
	public T cast(Object obj)
	{
		return (T) obj;
	}

	public static <OT> AssociationListKey<OT> getKeyFor(Class<OT> keyClass, String keyName)
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
		AssociationListKey<OT> key = (AssociationListKey<OT>) map.get(keyName);
		if (key == null)
		{
			key = new AssociationListKey<>();
			map.put(keyName, key);
		}
		return key;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<>();
		Field[] fields = AssociationListKey.class.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();

            if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod)) {
                try {
                    Object obj = field.get(null);
                    if (obj instanceof AssociationListKey) {
                        map.put(field.getName(), (AssociationListKey<?>) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
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
		for (Map.Entry<?, AssociationListKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}
}
