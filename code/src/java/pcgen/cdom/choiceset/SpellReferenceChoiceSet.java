/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

/**
 * A SpellReferenceChoiceSet contains references to CDOMListObjects. This is a
 * specialized PrimitiveChoiceSet designed for use with CDOMListObject<Spell>
 * lists.
 * 
 * The contents of a SpellReferenceChoiceSet is defined at construction of the
 * SpellReferenceChoiceSet. The contents of a SpellReferenceChoiceSet is fixed,
 * and will not vary by the PlayerCharacter used to resolve the
 * SpellReferenceChoiceSet.
 */
public class SpellReferenceChoiceSet implements
		PrimitiveChoiceSet<CDOMListObject<Spell>>
{
	/**
	 * The underlying Set of CDOMReferences that contain the CDOMListObjects in
	 * this SpellReferenceChoiceSet
	 */
	private final Set<CDOMReference<? extends CDOMListObject<Spell>>> set;

	/**
	 * Constructs a new SpellReferenceChoiceSet which contains the Set of
	 * CDOMListObjects contained within the given CDOMReferences. The
	 * CDOMReferences do not need to be resolved at the time of construction of
	 * the SpellReferenceChoiceSet.
	 * 
	 * This constructor is reference-semantic, meaning that ownership of the
	 * Collection provided to this constructor is not transferred. Modification
	 * of the Collection (after this constructor completes) does not result in
	 * modifying the ReferenceChoiceSet, and the SpellReferenceChoiceSet will
	 * not modify the given Collection.
	 * 
	 * @param col
	 *            A Collection of CDOMReferences which define the Set of
	 *            CDOMListObjects contained within the SpellReferenceChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public SpellReferenceChoiceSet(
			Collection<CDOMReference<? extends CDOMListObject<Spell>>> col)
	{
		if (col == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (col.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		set = new HashSet<CDOMReference<? extends CDOMListObject<Spell>>>(col);
	}

	/**
	 * Returns a representation of this SpellReferenceChoiceSet, suitable for
	 * storing in an LST file.
	 */
	public String getLSTformat()
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				ReferenceUtilities.REFERENCE_SORTER);
		sortedSet.addAll(set);
		StringBuilder sb = new StringBuilder();
		List<CDOMReference<?>> domainList = new ArrayList<CDOMReference<?>>();
		boolean needComma = false;
		for (CDOMReference<?> ref : sortedSet)
		{
			if (DomainSpellList.class.equals(ref.getReferenceClass()))
			{
				domainList.add(ref);
			}
			else
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append(ref.getLSTformat());
				needComma = true;
			}
		}
		for (CDOMReference<?> ref : domainList)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			sb.append("DOMAIN.");
			sb.append(ref.getLSTformat());
			needComma = true;
		}
		return sb.toString();
	}

	/**
	 * The class of object this SpellReferenceChoiceSet contains.
	 * 
	 * @return The class of object this SpellReferenceChoiceSet contains.
	 */
	public Class<CDOMListObject> getChoiceClass()
	{
		return CDOMListObject.class;
	}

	/**
	 * Returns a Set containing the CDOMListObjects which this
	 * SpellReferenceChoiceSet contains. The contents of a
	 * SpellReferenceChoiceSet is fixed, and will not vary by the
	 * PlayerCharacter used to resolve the SpellReferenceChoiceSet.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this SpellReferenceChoiceSet are not
	 * yet resolved.
	 * 
	 * This method is reference-semantic, meaning that ownership of the Set
	 * returned by this method will be transferred to the calling object.
	 * Modification of the returned Set should not result in modifying the
	 * SpellReferenceChoiceSet, and modifying the SpellReferenceChoiceSet after
	 * the Set is returned should not modify the Set.
	 * 
	 * @return A Set containing the CDOMListObjects which this
	 *         SpellReferenceChoiceSet contains.
	 */
	public Set<CDOMListObject<Spell>> getSet(PlayerCharacter pc)
	{
		Set<CDOMListObject<Spell>> returnSet = new HashSet<CDOMListObject<Spell>>();
		for (CDOMReference<? extends CDOMListObject<Spell>> ref : set)
		{
			returnSet.addAll(ref.getContainedObjects());
		}
		return returnSet;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * SpellReferenceChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.size();
	}

	/**
	 * Returns true if this SpellReferenceChoiceSet is equal to the given
	 * Object. Equality is defined as being another SpellReferenceChoiceSet
	 * object with equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof SpellReferenceChoiceSet)
		{
			SpellReferenceChoiceSet other = (SpellReferenceChoiceSet) o;
			return set.equals(other.set);
		}
		return false;
	}

}
