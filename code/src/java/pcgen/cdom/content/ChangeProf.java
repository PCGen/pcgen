/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.WeaponProf;

/**
 * Represents a change to a type of WeaponProficiency for a PlayerCharacter. The
 * change impacts a given WeaponProf primitive and effectively places that
 * WeaponProf into a separate TYPE.
 */
public class ChangeProf extends ConcretePrereqObject
{

	/**
	 * A reference to the source WeaponProf that this ChangeProf impacts
	 * (effectively changes the TYPE)
	 */
	private final CDOMReference<WeaponProf> source;

	/**
	 * The resulting Group into which the source WeaponProf is effectively
	 * placed for the PlayerCharacter that possesses this ChangeProf.
	 */
	private final CDOMGroupRef<WeaponProf> result;

	/**
	 * Constructs a new ChangeProf with the given reference to a source
	 * WeaponProf and given resulting Group into which the source WeaponProf is
	 * effectively placed for a PlayerCharacter that possesses this ChangeProfF.
	 * 
	 * @param sourceProf
	 *            A reference to the source WeaponProf that this ChangeProf
	 *            impacts
	 * @param resultType
	 *            The resulting Group into which the source WeaponProf is
	 *            effectively placed for the PlayerCharacter that possesses this
	 *            ChangeProf.
	 */
	public ChangeProf(CDOMReference<WeaponProf> sourceProf, CDOMGroupRef<WeaponProf> resultType)
	{
		Objects.requireNonNull(sourceProf, "Source Prof for ChangeProf cannot be null");
		Objects.requireNonNull(resultType, "Resulting Prof Type for ChangeProf cannot be null");
		source = sourceProf;
		result = resultType;
	}

	/**
	 * Returns a reference to the source WeaponProf for this ChangeProf
	 * 
	 * @return A reference to the source WeaponProf for this ChangeProf
	 */
	public CDOMReference<WeaponProf> getSource()
	{
		return source;
	}

	/**
	 * Returns a reference to the Group into which the source WeaponProf is
	 * effectively placed for the PlayerCharacter that possesses this
	 * ChangeProf.
	 * 
	 * @return A reference to the Group into which the source WeaponProf is
	 *         effectively placed.
	 */
	public CDOMGroupRef<WeaponProf> getResult()
	{
		return result;
	}

	@Override
	public int hashCode()
	{
		return 31 * source.hashCode() + result.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof ChangeProf other))
		{
			return false;
		}
		return source.equals(other.source) && result.equals(other.result);
	}

	@Override
	public String toString()
	{
		return getClass() + "[" + source + " -> " + result + "]";
	}
}
