/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;

/**
 * A ModifyChoiceDecorator is a PrimitiveChoiceSet that returns the MULT:YES
 * Feats that are possessed by the PlayerCharacter. This is a special case
 * PrimitiveChoiceSet for the MODIFYFEATCHOICE token.
 */
public class ModifyChoiceDecorator implements PrimitiveChoiceSet<Ability>
{

	/**
	 * The starting set from which to select objects. This is the
	 * PrimitiveChoiceSet that identifies the Ability objects (Feats, really)
	 * that were listed in the LST file.
	 */
	private final PrimitiveChoiceSet<Ability> set;

	/**
	 * Constructs a new ModifyChoiceDecorator with the given underlying
	 * PrimitiveChoiceSet.
	 * 
	 * @param underlyingSet
	 *            The PrimitiveChoiceSet that identifies the Feats that were
	 *            listed in the LST file.
	 */
	public ModifyChoiceDecorator(PrimitiveChoiceSet<Ability> underlyingSet)
	{
		set = underlyingSet;
	}

	/**
	 * The class of object this ModifyChoiceDecorator contains (Ability.class)
	 * 
	 * @return The class of object this ModifyChoiceDecorator contains.
	 */
	public Class<? super Ability> getChoiceClass()
	{
		return set.getChoiceClass();
	}

	/**
	 * Returns a representation of this ModifyChoiceDecorator, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 */
	public String getLSTformat(boolean useAny)
	{
		return set.getLSTformat(useAny);
	}

	/**
	 * Returns a Set containing the Objects which this ModifyChoiceDecorator
	 * contains and which are also possessed by the PlayerCharacter.
	 * 
	 * It is intended that classes which implement ModifyChoiceDecorator will
	 * make this method value-semantic, meaning that ownership of the Set
	 * returned by this method will be transferred to the calling object.
	 * Modification of the returned Set will not result in modifying the
	 * ModifyChoiceDecorator (and vice versa since the ModifyChoiceDecorator is
	 * near immutable)
	 * 
	 * @return A Set containing the Objects which this ModifyChoiceDecorator
	 *         contains and which are also possessed by the PlayerCharacter.
	 */
	public Set<Ability> getSet(PlayerCharacter pc)
	{
		Set<Ability> ab = set.getSet(pc);
		List<Ability> pcfeats = pc.aggregateFeatList();
		Set<Ability> returnSet = new HashSet<Ability>();
		for (Ability a : pcfeats)
		{
			if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED) && ab.contains(a))
			{
				returnSet.add(a);
			}
		}
		return returnSet;
	}

	/**
	 * Returns true if this ModifyChoiceDecorator is equal to the given Object.
	 * Equality is defined as being another ModifyChoiceDecorator object with
	 * equal underlying PrimitiveChoiceSet.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof ModifyChoiceDecorator)
				&& ((ModifyChoiceDecorator) obj).set.equals(set);
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * ModifyChoiceDecorator
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.hashCode();
	}

}
