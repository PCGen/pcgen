/*
 * WeaponProf.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.helper.WeaponBonusListActor;
import pcgen.cdom.list.WeaponProfList;
import pcgen.cdom.reference.CDOMDirectSingleRef;

/**
 * <code>WeaponProf</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class WeaponProf extends PObject implements Comparable<Object>
{
	public static final CDOMReference<WeaponProfList> STARTING_LIST;

	public static final PersistentChoiceActor<WeaponProf> STARTING_ACTOR = new WeaponBonusListActor();

	static
	{
		WeaponProfList wpl = new WeaponProfList();
		wpl.setName("*Starting");
		STARTING_LIST = CDOMDirectSingleRef.getRef(wpl);
	}

	/**
	 * Compares keyName only.
	 * 
	 * @param o1
	 * @return a negative integer, zero, or a positive integer as WeaponProf is
	 *         less than, equal to, or greater than the specified WeaponProf.
	 */
	@Override
	public int compareTo(final Object o1)
	{
		return getKeyName().compareToIgnoreCase(((WeaponProf) o1).getKeyName());
	}

	/**
	 * Compares keyName only.
	 * 
	 * @param obj
	 *            the WeaponProf with which to compare.
	 * @return <code>true</code> if this WeaponProf is the same as the obj
	 *         argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof WeaponProf
				&& getKeyName().equals(((WeaponProf) obj).getKeyName());
	}

	/**
	 * Hashcode of the keyName.
	 * 
	 * @return Hashcode of the keyName.
	 */
	@Override
	public int hashCode()
	{
		return getKeyName().hashCode();
	}
}
