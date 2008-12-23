/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * A ClassReferenceChoiceSet contains references to PCClass Objects.
 * 
 * The contents of a ClassReferenceChoiceSet is defined at construction of the
 * ClassReferenceChoiceSet. The contents of a ClassReferenceChoiceSet is fixed,
 * and will not vary by the PlayerCharacter used to resolve the
 * ClassReferenceChoiceSet.
 * 
 * @param <T>
 *            The class of object this ReferenceChoiceSet contains.
 */
public class SpellCasterChoiceSet extends ChoiceSet<PCClass> implements
		PrimitiveChoiceSet<PCClass>
{

	private static final PrimitiveChoiceSet<PCClass> EMPTY_CHOICE_SET = new PrimitiveChoiceSet<PCClass>()
	{

		public Class<PCClass> getChoiceClass()
		{
			return PCClass.class;
		}

		public String getLSTformat()
		{
			return Constants.EMPTY_STRING;
		}

		public Set<PCClass> getSet(PlayerCharacter pc)
		{
			return Collections.emptySet();
		}
	};

	private final CDOMGroupRef<PCClass> allClasses;

	private final List<String> spelltypes;

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * ClassReferenceChoiceSet
	 */
	private final PrimitiveChoiceSet<PCClass> pcset;

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * ClassReferenceChoiceSet
	 */
	private final PrimitiveChoiceSet<PCClass> primitives;

	/**
	 * Constructs a new ClassReferenceChoiceSet which contains the Set of
	 * objects contained within the given CDOMReferences. The CDOMReferences do
	 * not need to be resolved at the time of construction of the
	 * ClassReferenceChoiceSet.
	 * 
	 * This constructor is reference-semantic, meaning that ownership of the
	 * Collection provided to this constructor is not transferred. Modification
	 * of the Collection (after this constructor completes) does not result in
	 * modifying the ClassReferenceChoiceSet, and the ClassReferenceChoiceSet
	 * will not modify the given Collection.
	 * @param spelltypes 
	 * @param allRef 
	 * 
	 * @param col
	 *            A Collection of CDOMReferences which define the Set of objects
	 *            contained within the ClassReferenceChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public SpellCasterChoiceSet(CDOMGroupRef<PCClass> allRef,
		List<String> spelltype, PrimitiveChoiceSet<PCClass> col,
		PrimitiveChoiceSet<PCClass> prim)
	{
		super("SPELLCASTER", col == null ? EMPTY_CHOICE_SET : col);
		pcset = col;
		primitives = prim;
		spelltypes = new ArrayList<String>(spelltype);
		allClasses = allRef;
	}

	/**
	 * Returns a representation of this ClassReferenceChoiceSet, suitable for
	 * storing in an LST file.
	 */
	@Override
	public String getLSTformat()
	{
		List<String> list = new ArrayList<String>();
		if (pcset != null)
		{
			list.add(pcset.getLSTformat());
		}
		if (!spelltypes.isEmpty())
		{
			list.addAll(spelltypes);
		}
		if (primitives != null)
		{
			list.add(primitives.getLSTformat());
		}
		return StringUtil.join(list, Constants.COMMA);
	}

	/**
	 * The class of object this ClassReferenceChoiceSet contains.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this ClassReferenceChoiceSet are not
	 * yet resolved.
	 * 
	 * @return The class of object this ClassReferenceChoiceSet contains.
	 */
	@Override
	public Class<PCClass> getChoiceClass()
	{
		return PCClass.class;
	}

	/**
	 * Returns a Set containing the Objects which this ClassReferenceChoiceSet
	 * contains. The contents of a ClassReferenceChoiceSet is fixed, and will
	 * not vary by the PlayerCharacter used to resolve the
	 * ClassReferenceChoiceSet.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this ClassReferenceChoiceSet are not
	 * yet resolved.
	 * 
	 * This method is reference-semantic, meaning that ownership of the Set
	 * returned by this method will be transferred to the calling object.
	 * Modification of the returned Set should not result in modifying the
	 * ClassReferenceChoiceSet, and modifying the ClassReferenceChoiceSet after
	 * the Set is returned should not modify the Set.
	 * 
	 * @return A Set containing the Objects which this ClassReferenceChoiceSet
	 *         contains.
	 */
	@Override
	public Set<PCClass> getSet(PlayerCharacter pc)
	{
		Set<PCClass> returnSet = new HashSet<PCClass>();
		if (pcset != null)
		{
			for (PCClass pcc : pcset.getSet(pc))
			{
				if (pc.getClassKeyed(pcc.getKeyName()) != null)
				{
					returnSet.add(pcc);
				}
			}
		}
		if (primitives != null)
		{
			returnSet.addAll(primitives.getSet(pc));
		}
		if (spelltypes != null)
		{
			for (PCClass pcc : allClasses.getContainedObjects())
			{
				TYPE: for (String type : spelltypes)
				{
					if (type.equals(pcc.get(StringKey.SPELLTYPE)))
					{
						if (pc.getClassKeyed(pcc.getKeyName()) != null)
						{
							returnSet.add(pcc);
							break TYPE;
						}
					}
				}
			}
		}
		return returnSet;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * ClassReferenceChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return pcset.hashCode() * 29
				+ (primitives == null ? 0 : primitives.hashCode());
	}

	/**
	 * Returns true if this ClassReferenceChoiceSet is equal to the given
	 * Object. Equality is defined as being another ClassReferenceChoiceSet
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
		if (o instanceof SpellCasterChoiceSet)
		{
			SpellCasterChoiceSet other = (SpellCasterChoiceSet) o;
			if (pcset == null)
			{
				if (other.pcset != null)
				{
					return false;
				}
			}
			else
			{
				if (!pcset.equals(other.pcset))
				{
					return false;
				}
			}
			if (primitives == null)
			{
				if (other.primitives != null)
				{
					return false;
				}
			}
			return primitives.equals(other.primitives);
		}
		return false;
	}
}
