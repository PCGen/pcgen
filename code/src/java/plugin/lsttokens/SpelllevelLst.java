/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class SpelllevelLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// SPELLLEVEL:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx

		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		String workingValue = value;
		List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
		while (true)
		{
			int lastPipeLoc = workingValue.lastIndexOf('|');
			if (lastPipeLoc == -1)
			{
				Logging.log(Logging.LST_ERROR, "Invalid " + getTokenName()
						+ " not enough tokens: " + value);
				return false;
			}
			String lastToken = workingValue.substring(lastPipeLoc + 1);
			if (lastToken.startsWith("PRE") || lastToken.startsWith("!PRE"))
			{
				workingValue = workingValue.substring(0, lastPipeLoc);
				prereqs.add(getPrerequisite(lastToken));
			}
			else
			{
				break;
			}
		}

		StringTokenizer tok = new StringTokenizer(workingValue, "|");

		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("Insufficient values in SPELLLEVEL tag: "
					+ value);
			return false;
		}

		String tagType = tok.nextToken(); // CLASS or DOMAIN

		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			String spellString = tok.nextToken();

			if (tagType.equalsIgnoreCase("CLASS"))
			{
				if (!subParse(context, obj, ClassSpellList.class, tokString,
						spellString, prereqs))
				{
					Logging.log(Logging.LST_ERROR, "  " + getTokenName()
							+ " error - entire token was " + value);
					return false;
				}
			}
			else if (tagType.equalsIgnoreCase("DOMAIN"))
			{
				if (!subParse(context, obj, DomainSpellList.class, tokString,
						spellString, prereqs))
				{
					Logging.log(Logging.LST_ERROR, "  " + getTokenName()
							+ " error - entire token was " + value);
					return false;
				}
			}
			else
			{
				Logging.errorPrint("First token of " + getTokenName()
						+ " must be CLASS or DOMAIN:" + value);
				return false;
			}
		}

		return true;
	}

	private <CL extends CDOMObject & CDOMList<Spell>> boolean subParse(
			LoadContext context, CDOMObject obj, Class<CL> tagType,
			String tokString, String spellString, List<Prerequisite> prereqs)
	{
		int equalLoc = tokString.indexOf(Constants.EQUALS);
		if (equalLoc == -1)
		{
			Logging.errorPrint("Expected an = in SPELLLEVEL " + "definition: "
					+ tokString);
			return false;
		}

		String casterString = tokString.substring(0, equalLoc);
		String spellLevel = tokString.substring(equalLoc + 1);
		Integer splLevel;
		try
		{
			splLevel = Integer.decode(spellLevel);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Expected a number for SPELLLEVEL, found: "
					+ spellLevel);
			return false;
		}

		if (hasIllegalSeparator(',', casterString))
		{
			return false;
		}

		StringTokenizer clTok = new StringTokenizer(casterString,
				Constants.COMMA);
		List<CDOMReference<? extends CDOMList<Spell>>> slList = new ArrayList<CDOMReference<? extends CDOMList<Spell>>>();
		while (clTok.hasMoreTokens())
		{
			String classString = clTok.nextToken();
			CDOMReference<CL> ref;
			if (classString.length() == 0)
			{
				Logging
						.errorPrint("Cannot resolve empty SpellList reference in "
								+ getTokenName());
				return false;
			}
			else if (classString.startsWith("SPELLCASTER."))
			{
				/*
				 * This is actually a TYPE
				 */
				ref = context.ref.getCDOMTypeReference(tagType, classString
						.substring(12));
			}
			else
			{
				ref = context.ref.getCDOMReference(tagType, classString);
			}
			slList.add(ref);
		}

		if (hasIllegalSeparator(',', spellString))
		{
			return false;
		}

		StringTokenizer spTok = new StringTokenizer(spellString, ",");

		while (spTok.hasMoreTokens())
		{
			String spellName = spTok.nextToken();
			CDOMReference<Spell> sp = context.ref.getCDOMReference(Spell.class,
					spellName);
			for (CDOMReference<? extends CDOMList<Spell>> sl : slList)
			{
				AssociatedPrereqObject tpr = context.getListContext()
						.addToList(getTokenName(), obj, sl, sp);
				tpr.setAssociation(AssociationKey.SPELL_LEVEL, splLevel);
				tpr.addAllPrerequisites(prereqs);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<String> set = new TreeSet<String>();

		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> changedDomainLists = context
				.getListContext().getChangedLists(obj, DomainSpellList.class);
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>> domainMap = getMap(
				context, obj, changedDomainLists);
		for (String prereqs : domainMap.getKeySet())
		{
			set.add(processUnparse("DOMAIN", domainMap, prereqs).toString());
		}

		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> changedClassLists = context
				.getListContext().getChangedLists(obj, ClassSpellList.class);
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>> classMap = getMap(
				context, obj, changedClassLists);
		for (String prereqs : classMap.getKeySet())
		{
			set.add(processUnparse("CLASS", classMap, prereqs).toString());
		}

		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	private StringBuilder processUnparse(
			String type,
			TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>> domainMap,
			String prereqs)
	{
		StringBuilder sb = new StringBuilder(type);
		Set<Integer> levels = domainMap.getSecondaryKeySet(prereqs);
		for (Integer level : new TreeSet<Integer>(levels))
		{
			for (CDOMReference<? extends CDOMList<? extends PrereqObject>> list : domainMap
					.getTertiaryKeySet(prereqs, level))
			{
				sb.append(Constants.PIPE);
				sb.append(list.getLSTformat());
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
					String lsts = lw.getLSTformat();
					if (lsts.startsWith("TYPE="))
					{
						lsts = "SPELLCASTER." + lsts.substring(5);
					}
					sb.append(lsts);
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

	private TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>> getMap(
			LoadContext context,
			CDOMObject obj,
			Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> changedLists)
	{
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>> map = new TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<Spell>>();

		for (CDOMReference listRef : changedLists)
		{
			AssociatedChanges changes = context.getListContext()
					.getChangesInList(getTokenName(), obj, listRef);
			Collection<Spell> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty()
					|| changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
						+ " does not support .CLEAR");
				return null;
			}
			MapToList<CDOMReference<Spell>, AssociatedPrereqObject> mtl = changes
					.getAddedAssociations();
			if (mtl == null || mtl.isEmpty())
			{
				// Zero indicates no Token
				// TODO Error message - unexpected?
				return null;
			}
			for (CDOMReference<Spell> added : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(added))
				{
					Integer lvl = assoc
							.getAssociation(AssociationKey.SPELL_LEVEL);
					String prereqString = getPrerequisiteString(context, assoc
							.getPrerequisiteList());
					map.addToListFor(prereqString, lvl, listRef, added);
				}
			}
		}
		return map;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
