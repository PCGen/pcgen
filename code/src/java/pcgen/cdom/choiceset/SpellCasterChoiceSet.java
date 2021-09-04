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
 * 
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
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * A SpellCasterChoiceSet contains references to PCClass Objects.
 * 
 * The contents of a SpellCasterChoiceSet is defined at construction of the
 * SpellCasterChoiceSet. The contents of a SpellCasterChoiceSet is fixed, and
 * will not vary by the PlayerCharacter used to resolve the
 * SpellCasterChoiceSet.
 */
public class SpellCasterChoiceSet extends ChoiceSet<PCClass> implements PrimitiveChoiceSet<PCClass>
{

	/**
	 * Caches the special case of an empty PrimitiveChoiceSet. This is used in
	 * order to avoid wasteful processing and memory in cases where a
	 * SpellCasterChoiceSet does not contain any Class-based items. (If it only
	 * contains spell types and spells [primitives], then this empty
	 * PrimitiveChoiceSet will be used.)
	 */
	private static final PrimitiveChoiceSet<PCClass> EMPTY_CHOICE_SET = new PrimitiveChoiceSet<>()
	{

		@Override
		public Class<PCClass> getChoiceClass()
		{
			return PCClass.class;
		}

		@Override
		public String getLSTformat(boolean useAny)
		{
			return Constants.EMPTY_STRING;
		}

		@Override
		public Set<PCClass> getSet(PlayerCharacter pc)
		{
			return Collections.emptySet();
		}

		@Override
		public GroupingState getGroupingState()
		{
			// CONSIDER throwing something here, never should be called?
			return GroupingState.ANY;
		}
	};

	/**
	 * Storage of all PCClass objects potentially available. This is effectively
	 * used as a cache to avoid a reference to the ReferenceContext during
	 * runtime. This is used to establish which Classes are available based on
	 * the String-based spell types provided at construction.
	 */
	private final CDOMGroupRef<PCClass> allClasses;

	/**
	 * Contains a list of spell types that are contained in this
	 * SpellCasterChoiceSet
	 */
	private final List<String> spelltypes;

	/**
	 * The underlying PrimitiveChoiceSet that contains the objects in this
	 * SpellCasterChoiceSet. This includes Group-based references to PCClasses.
	 * 
	 * It is necessary to keep this separate from the individual references due
	 * to quirks in addition behavior of SpellCaster choices. If provided a
	 * group reference, then only items that the PlayerCharacter has levels in
	 * out of that group are available for selection. If items are provided as a
	 * primitive, then that item is universally added to the potential choices.
	 * 
	 * CONSIDER is this separation good behavior or a bug?
	 */
	private final PrimitiveChoiceSet<PCClass> types;

	/**
	 * The underlying PrimitiveChoiceSet that contains the objects in this
	 * SpellCasterChoiceSet. This includes individual references to (primitive)
	 * PCClasses.
	 */
	private final PrimitiveChoiceSet<PCClass> primitives;

	/**
	 * Constructs a new SpellCasterChoiceSet which contains the Set of objects
	 * contained within the given CDOMReferences. The CDOMReferences do not need
	 * to be resolved at the time of construction of the SpellCasterChoiceSet.
	 * 
	 * This constructor is value-semantic, meaning that ownership of the List
	 * provided to this constructor is not transferred. Modification of the List
	 * (after this constructor completes) does not result in modifying the
	 * SpellCasterChoiceSet, and the SpellCasterChoiceSet will not modify the
	 * given List.
	 * 
	 * @param allRef
	 *            The "ALL" Reference for PCClass objects
	 * @param spelltype
	 *            A List of spell types that this SpellCasterChoiceSet will
	 *            allow to be used to select a PCClass
	 * @param typePCS
	 *            A PrimitiveChoiceSet which defines the Set of objects
	 *            contained within the SpellCasterChoiceSet that were referenced
	 *            by group (e.g. TYPE=)
	 * @param primPCS
	 *            A PrimitiveChoiceSet which defines the Set of primitive
	 *            objects contained within the SpellCasterChoiceSet that were
	 *            referenced by key
	 */
	public SpellCasterChoiceSet(CDOMGroupRef<PCClass> allRef, List<String> spelltype,
		PrimitiveChoiceSet<PCClass> typePCS, PrimitiveChoiceSet<PCClass> primPCS)
	{
		super("SPELLCASTER", typePCS == null ? EMPTY_CHOICE_SET : typePCS);
		types = typePCS;
		primitives = primPCS;
		spelltypes = new ArrayList<>(spelltype);
		allClasses = allRef;
	}

	/**
	 * Returns a representation of this SpellCasterChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @return A representation of this SpellCasterChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	@Override
	public String getLSTformat()
	{
		return getLSTformat(true);
	}

	/**
	 * Returns a representation of this SpellCasterChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this SpellCasterChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		List<String> list = new ArrayList<>();
		if (primitives != null)
		{
			list.add(primitives.getLSTformat(useAny));
		}
		if (types != null)
		{
			list.add(types.getLSTformat(useAny));
		}
		if (!spelltypes.isEmpty())
		{
			list.addAll(spelltypes);
		}
		return StringUtil.join(list, Constants.COMMA);
	}

	/**
	 * The class of object this SpellCasterChoiceSet contains.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this SpellCasterChoiceSet are not yet
	 * resolved.
	 * 
	 * @return The class of object this SpellCasterChoiceSet contains.
	 */
	@Override
	public Class<PCClass> getChoiceClass()
	{
		return PCClass.class;
	}

	/**
	 * Returns a Set containing the Objects which this SpellCasterChoiceSet
	 * contains. The contents of a SpellCasterChoiceSet is fixed, and will not
	 * vary by the PlayerCharacter used to resolve the SpellCasterChoiceSet.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this SpellCasterChoiceSet are not yet
	 * resolved.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the SpellCasterChoiceSet, and modifying the
	 * SpellCasterChoiceSet after the Set is returned should not modify the Set.
	 * However, modification of the PCClass objects contained within the
	 * returned set will result in modification of the PCClass objects contained
	 * within this SpellCasterChoiceSet.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            SpellCasterChoiceSet should be returned.
	 * @return A Set containing the Objects which this SpellCasterChoiceSet
	 *         contains.
	 */
	@Override
	public Set<PCClass> getSet(PlayerCharacter pc)
	{
		FactKey<String> fk = FactKey.valueOf("SpellType");
		Set<PCClass> returnSet = new HashSet<>();
		if (types != null)
		{
			for (PCClass pcc : types.getSet(pc))
			{
				if ((pcc.get(fk) != null) && (pc.getClassKeyed(pcc.getKeyName()) != null))
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
					String spelltype = pcc.getResolved(fk);
					if (type.equalsIgnoreCase(spelltype) && pc.getClassKeyed(pcc.getKeyName()) != null)
					{
						returnSet.add(pcc);
						break TYPE;
					}
				}
			}
		}
		return returnSet;
	}

	@Override
	public int hashCode()
	{
		return (types == null ? 0 : types.hashCode() * 29) + (primitives == null ? 0 : primitives.hashCode());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof SpellCasterChoiceSet other)
		{
			if (types == null)
			{
				if (other.types != null)
				{
					return false;
				}
			}
			else
			{
				if (!types.equals(other.types))
				{
					return false;
				}
			}
			if (primitives == null)
			{
				return other.primitives == null;
			}
			return primitives.equals(other.primitives);
		}
		return false;
	}

	/**
	 * Returns the GroupingState for this SpellCasterChoiceSet. The
	 * GroupingState indicates how this SpellCasterChoiceSet can be combined
	 * with other PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this SpellCasterChoiceSet.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		GroupingState state = GroupingState.EMPTY;
		if (primitives != null)
		{
			state = primitives.getGroupingState().add(state);
		}
		if (types != null)
		{
			state = types.getGroupingState().add(state);
		}
		if (!spelltypes.isEmpty())
		{
			state = GroupingState.ANY.add(state);
		}
		//TODO I think this needs state.compound(GroupingState.ALLOWS_UNION)??
		return state;
	}
}
