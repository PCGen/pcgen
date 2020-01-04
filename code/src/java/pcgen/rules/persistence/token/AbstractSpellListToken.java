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
 *
 *
 */

package pcgen.rules.persistence.token;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;

/**
 * The Class {@code AbstractSpellListToken} ...
 *
 *
 */
public abstract class AbstractSpellListToken extends AbstractTokenWithSeparator<CDOMObject>
{
	@Override
	protected char separator()
	{
		return '|';
	}

	/**
	 * Gets the map.
	 *
	 * @param context the context
	 * @param obj the obj
	 * @param changedLists the changed lists
	 * @param knownSpells Should this scan be for known spells
	 * @return the map
	 */
	protected TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<?>>, CDOMReference<Spell>> getMap(
		LoadContext context, CDOMObject obj, Collection<CDOMReference<? extends CDOMList<?>>> changedLists,
		boolean knownSpells)
	{
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<?>>, CDOMReference<Spell>> map =
				new TripleKeyMapToList<>();

		for (CDOMReference listRef : changedLists)
		{
			AssociatedChanges<CDOMReference<Spell>> changes =
					context.getListContext().getChangesInList(getTokenName(), obj, listRef);
			Collection<?> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName() + " does not support .CLEAR");
				return null;
			}
			MapToList<CDOMReference<Spell>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
			if (mtl == null || mtl.isEmpty())
			{
				// Zero indicates no Token
				// TODO Error message - unexpected?
				continue;
			}
			for (CDOMReference<Spell> added : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(added))
				{
					Integer lvl = assoc.getAssociation(AssociationKey.SPELL_LEVEL);
					String prereqString = getPrerequisiteString(context, assoc.getPrerequisiteList());
					Boolean known = assoc.getAssociation(AssociationKey.KNOWN);
					boolean isKnown = known != null && known;
					if (knownSpells == isKnown)
					{
						map.addToListFor(prereqString, lvl, listRef, added);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Process unparse.
	 *
	 * @param type the type
	 * @param domainMap the domain map
	 * @param prereqs the prereqs
	 *
	 * @return the string builder
	 */
	protected StringBuilder processUnparse(String type,
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<?>>, CDOMReference<Spell>> domainMap,
		String prereqs)
	{
		StringBuilder sb = new StringBuilder(type);
		Set<Integer> levels = domainMap.getSecondaryKeySet(prereqs);
		for (Integer level : new TreeSet<>(levels))
		{
			for (CDOMReference<? extends CDOMList<?>> list : domainMap.getTertiaryKeySet(prereqs, level))
			{
				sb.append(Constants.PIPE);
				String lsts = list.getLSTformat(false);
				if (lsts.startsWith("TYPE="))
				{
					lsts = "SPELLCASTER." + lsts.substring(5);
				}
				sb.append(lsts);
				sb.append(Constants.EQUALS);
				sb.append(level);
				sb.append(Constants.PIPE);
				List<CDOMReference<Spell>> refs = domainMap.getListFor(prereqs, level, list);
				boolean first = true;
				for (CDOMReference<Spell> lw : refs)
				{
					if (!first)
					{
						sb.append(',');
					}
					first = false;
					sb.append(lw.getLSTformat(false));
				}
			}
		}
		if (prereqs != null)
		{
			sb.append(Constants.PIPE);
			sb.append(prereqs);
		}
		return sb;
	}

}
