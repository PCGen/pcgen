/*
 * SpellknownLst.java
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
 */

package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractSpellListToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The Class {@code SpellknownLst} is responsible for parsing and
 * unparsing the SPELLKNOWN tag. This class is heavily based on the
 * SpelllevelLst class. <p>
 * Syntax is:
 * <pre>
 * SPELLKNOWN:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx
 * </pre>
 */
public class SpellknownLst extends AbstractSpellListToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SPELLKNOWN";
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail("Cannot use " + getTokenName()
				+ " on an Ungranted object type: "
				+ obj.getClass().getSimpleName());
		}
		String workingValue = value;
		List<Prerequisite> prereqs = new ArrayList<>();
		while (true)
		{
			int lastPipeLoc = workingValue.lastIndexOf('|');
			if (lastPipeLoc == -1)
			{
				return new ParseResult.Fail("Invalid " + getTokenName()
						+ " not enough tokens: " + value);
			}
			String lastToken = workingValue.substring(lastPipeLoc + 1);
			if (looksLikeAPrerequisite(lastToken))
			{
				workingValue = workingValue.substring(0, lastPipeLoc);
				Prerequisite prerequisite = getPrerequisite(lastToken);
				if (prerequisite == null)
				{
					return new ParseResult.Fail("Invalid prerequisite "
						+ lastToken + " in " + getTokenName() + " tag: "
						+ value);
				}
				prereqs.add(prerequisite);
			}
			else
			{
				break;
			}
		}

		StringTokenizer tok = new StringTokenizer(workingValue, Constants.PIPE);

		if (tok.countTokens() < 3)
		{
			return new ParseResult.Fail("Insufficient values in SPELLKNOWN tag: "
					+ value);
		}

		String tagType = tok.nextToken(); // CLASS only

		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			String spellString = tok.nextToken();

			if (tagType.equalsIgnoreCase("CLASS"))
			{
				ParseResult pr = subParse(context, obj, ClassSpellList.class, tokString,
					spellString, prereqs);
				if (!pr.passed())
				{
					return new ParseResult.Fail(getTokenName() + " failed due to " + pr
						+ ".  Entire token was: " + value);
				}
			}
			else
			{
				return new ParseResult.Fail("First token of " + getTokenName()
						+ " must be CLASS: " + value);
			}
		}

		return ParseResult.SUCCESS;
	}

	/**
	 * Parse the tag contents after the SPELLKNOWN:CLASS| section.
	 *
	 * @param context the context under which the tag is being parsed.
	 * @param obj the obj The object owning the tag.
	 * @param tagType the type of object the tag creates
	 * @param tokString the tok string The string defining the caster type/class and spell level.
	 * @param spellString the spell string The string containing the spell name(s)
	 * @param prereqs the prereqs The prerequisites to be applied.
	 *
	 * @return true, if successful
	 */
	private <CL extends Loadable & CDOMList<Spell>> ParseResult subParse(
			LoadContext context, CDOMObject obj, Class<CL> tagType,
			String tokString, String spellString, List<Prerequisite> prereqs)
	{
		int equalLoc = tokString.indexOf(Constants.EQUALS);
		if (equalLoc == -1)
		{
			return new ParseResult.Fail(
				"Expected an = in SPELLKNOWN " + "definition: " + tokString);
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
			return new ParseResult.Fail(
				"Expected a number for SPELLKNOWN, found: " + spellLevel);
		}

		ParseResult pr = checkSeparatorsAndNonEmpty(',', casterString);
		if (!pr.passed())
		{
			return pr;
		}

		StringTokenizer clTok = new StringTokenizer(casterString,
				Constants.COMMA);
		List<CDOMReference<? extends CDOMList<Spell>>> slList =
				new ArrayList<>();
		while (clTok.hasMoreTokens())
		{
			String classString = clTok.nextToken();
			CDOMReference<CL> ref;
			if (classString.startsWith("SPELLCASTER."))
			{
				/*
				 * This is actually a TYPE
				 */
				ref = context.getReferenceContext().getCDOMTypeReference(tagType, classString
						.substring(12));
			}
			else
			{
				ref = context.getReferenceContext().getCDOMReference(tagType, classString);
			}
			slList.add(ref);
		}

		pr = checkForIllegalSeparator(',', spellString);
		if (!pr.passed())
		{
			return pr;
		}

		StringTokenizer spTok = new StringTokenizer(spellString, ",");

		while (spTok.hasMoreTokens())
		{
			String spellName = spTok.nextToken();
			CDOMReference<Spell> sp = context.getReferenceContext().getCDOMReference(Spell.class,
					spellName);
			for (CDOMReference<? extends CDOMList<Spell>> sl : slList)
			{
				AssociatedPrereqObject tpr = context.getListContext()
						.addToList(getTokenName(), obj, sl, sp);
				tpr.setAssociation(AssociationKey.SPELL_LEVEL, splLevel);
				tpr.setAssociation(AssociationKey.KNOWN, Boolean.TRUE);
				tpr.addAllPrerequisites(prereqs);
			}
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<String> set = new TreeSet<>();

		Collection<CDOMReference<? extends CDOMList<?>>> changedClassLists = context
				.getListContext().getChangedLists(obj, ClassSpellList.class);
		TripleKeyMapToList<String, Integer, CDOMReference<? extends CDOMList<?>>, CDOMReference<Spell>> classMap =
				getMap(context, obj, changedClassLists, true);
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

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
