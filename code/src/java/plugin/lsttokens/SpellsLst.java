/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.MapToList;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SpellsLst extends AbstractNonEmptyToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SPELLS";
	}

	/**
	 * {@literal 
	 * SPELLS:<spellbook name>|[<optional parameters, pipe deliminated>] |<spell
	 * name>[,<formula for DC>] |<spell name2>[,<formula2 for DC>] |PRExxx
	 * |PRExxx
	 *
	 * CASTERLEVEL=<formula> Casterlevel of spells TIMES=<formula> Cast Times
	 * per day, -1=At Will
	 *}
	 * @param sourceLine
	 *            Line from the LST file without the SPELLS:
	 * @return spells list
	 */
	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String sourceLine)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		if ((sourceLine == null) || sourceLine.isEmpty())
		{
			return new ParseResult.Fail("Argument in " + getTokenName() + " cannot be empty");
		}
		if (sourceLine.equals(Constants.LST_DOT_CLEAR_ALL))
		{
			context.getListContext().removeAllFromList(getTokenName(), obj, Spell.SPELLS);
			return ParseResult.SUCCESS;
		}
		ParsingSeparator sep = new ParsingSeparator(sourceLine, '|');
		sep.addGroupingPair('[', ']');
		sep.addGroupingPair('(', ')');

		String spellBook = sep.next();
		if (spellBook.isEmpty())
		{
			return new ParseResult.Fail("SpellBook in " + getTokenName() + " cannot be empty");
		}
		// Formula casterLevel = null;
		String casterLevel = null;
		String times = null;
		String timeunit = null;

		if (!sep.hasNext())
		{
			return new ParseResult.Fail(getTokenName() + ": minimally requires a Spell Name");
		}
		String token = sep.next();

		while (true)
		{
			if (token.startsWith("TIMES="))
			{
				if (times != null)
				{
					return new ParseResult.Fail(
						"Found two TIMES entries in " + getTokenName() + ": invalid: " + sourceLine);
				}
				times = token.substring(6);
				if (times.isEmpty())
				{
					return new ParseResult.Fail("Error in Times in " + getTokenName() + ": argument was empty");
				}
				if (!sep.hasNext())
				{
					return new ParseResult.Fail(
						getTokenName() + ": minimally requires " + "a Spell Name (after TIMES=)");
				}
				token = sep.next();
			}
			else if (token.startsWith("TIMEUNIT="))
			{
				if (timeunit != null)
				{
					return new ParseResult.Fail(
						"Found two TIMEUNIT entries in " + getTokenName() + ": invalid: " + sourceLine);
				}
				timeunit = token.substring(9);
				if (timeunit.isEmpty())
				{
					return new ParseResult.Fail("Error in TimeUnit in " + getTokenName() + ": argument was empty");
				}
				if (!sep.hasNext())
				{
					return new ParseResult.Fail(
						getTokenName() + ": minimally requires " + "a Spell Name (after TIMEUNIT=)");
				}
				token = sep.next();
			}
			else if (token.startsWith("CASTERLEVEL="))
			{
				if (casterLevel != null)
				{
					return new ParseResult.Fail(
						"Found two CASTERLEVEL entries in " + getTokenName() + ": invalid: " + sourceLine);
				}
				casterLevel = token.substring(12);
				if (casterLevel.isEmpty())
				{
					return new ParseResult.Fail("Error in Caster Level in " + getTokenName() + ": argument was empty");
				}
				if (!sep.hasNext())
				{
					return new ParseResult.Fail(
						getTokenName() + ": minimally requires a " + "Spell Name (after CASTERLEVEL=)");
				}
				token = sep.next();
			}
			else
			{
				break;
			}
		}
		if (times == null)
		{
			times = "1";
		}

		if (token.isEmpty())
		{
			return new ParseResult.Fail("Spell arguments may not be empty in " + getTokenName() + ": " + sourceLine);
		}
		if (token.charAt(0) == ',')
		{
			return new ParseResult.Fail(getTokenName() + " Spell arguments may not start with , : " + token);
		}
		if (token.charAt(token.length() - 1) == ',')
		{
			return new ParseResult.Fail(getTokenName() + " Spell arguments may not end with , : " + token);
		}
		if (token.contains(",,"))
		{
			return new ParseResult.Fail(getTokenName() + " Spell arguments uses double separator ,, : " + token);
		}

		/*
		 * CONSIDER This is currently order enforcing the reference fetching to
		 * match the integration tests that we perform, and their current
		 * behavior. Not sure if this is really tbe best solution?
		 *
		 * See CDOMObject.
		 */
		DoubleKeyMap<CDOMReference<Spell>, AssociationKey<?>, Object> dkm =
				new DoubleKeyMap<>(LinkedHashMap.class, HashMap.class);
		while (true)
		{
			if (token.isEmpty())
			{
				return new ParseResult.Fail(
					"Spell arguments may not end with comma or pipe in " + getTokenName() + ": " + sourceLine);
			}
			int commaLoc = token.indexOf(',');
			String name = commaLoc == -1 ? token : token.substring(0, commaLoc);
			CDOMReference<Spell> spell = context.getReferenceContext().getCDOMReference(Spell.class, name);
			dkm.put(spell, AssociationKey.CASTER_LEVEL, casterLevel);
			Formula timesFormula = FormulaFactory.getFormulaFor(times);
			if (!timesFormula.isValid())
			{
				return new ParseResult.Fail(
					"Times in " + getTokenName() + " was not valid: " + timesFormula.toString());
			}
			dkm.put(spell, AssociationKey.TIMES_PER_UNIT, timesFormula);
			if (timeunit != null)
			{
				dkm.put(spell, AssociationKey.TIME_UNIT, timeunit);
			}
			dkm.put(spell, AssociationKey.SPELLBOOK, spellBook);
			if (commaLoc != -1)
			{
				dkm.put(spell, AssociationKey.DC_FORMULA, token.substring(commaLoc + 1));
			}
			if (!sep.hasNext())
			{
				// No prereqs, so we're done
				finish(context, obj, dkm, null);
				return ParseResult.SUCCESS;
			}
			token = sep.next();
			if (looksLikeAPrerequisite(token))
			{
				break;
			}
		}

		List<Prerequisite> prereqs = new ArrayList<>();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				return new ParseResult.Fail("   (Did you put spells after the " + "PRExxx tags in SPELLS:?)");
			}
			prereqs.add(prereq);
			if (!sep.hasNext())
			{
				break;
			}
			token = sep.next();
		}

		finish(context, obj, dkm, prereqs);
		return ParseResult.SUCCESS;
	}

	public void finish(LoadContext context, CDOMObject obj,
		DoubleKeyMap<CDOMReference<Spell>, AssociationKey<?>, Object> dkm, List<Prerequisite> prereqs)
	{
		for (CDOMReference<Spell> spell : dkm.getKeySet())
		{
			AssociatedPrereqObject edge = context.getListContext().addToList(getTokenName(), obj, Spell.SPELLS, spell);
			for (AssociationKey ak : dkm.getSecondaryKeySet(spell))
			{
				edge.setAssociation(ak, dkm.get(spell, ak));
			}
			if (prereqs != null)
			{
				for (Prerequisite prereq : prereqs)
				{
					edge.addPrerequisite(prereq);
				}
			}
		}
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<CDOMReference<Spell>> changes =
				context.getListContext().getChangesInList(getTokenName(), obj, Spell.SPELLS);
		List<String> list = new ArrayList<>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR_ALL);
		}
		MapToList<CDOMReference<Spell>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			list.addAll(processAdds(context, mtl));
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[0]);
	}

	private Collection<? extends String> processAdds(LoadContext context,
		MapToList<CDOMReference<Spell>, AssociatedPrereqObject> mtl)
	{
		TripleKeyMap<Set<Prerequisite>, Map<AssociationKey<?>, Object>, CDOMReference<Spell>, String> m =
				new TripleKeyMap<>();
		for (CDOMReference<Spell> lw : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(lw))
			{
				Map<AssociationKey<?>, Object> am = new HashMap<>();
				String dc = null;
				for (AssociationKey<?> ak : assoc.getAssociationKeys())
				{
					// if (AssociationKey.SOURCE_URI.equals(ak)
					// || AssociationKey.FILE_LOCATION.equals(ak))
					// {
					// // Do nothing
					// }
					// else
					if (AssociationKey.DC_FORMULA.equals(ak))
					{
						dc = assoc.getAssociation(AssociationKey.DC_FORMULA);
					}
					else
					{
						am.put(ak, assoc.getAssociation(ak));
					}
				}
				m.put(new HashSet<>(assoc.getPrerequisiteList()), am, lw, dc);
			}
		}

		Set<String> set = new TreeSet<>();
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			for (Map<AssociationKey<?>, Object> am : m.getSecondaryKeySet(prereqs))
			{
				StringBuilder sb = new StringBuilder();
				sb.append(am.get(AssociationKey.SPELLBOOK));
				Formula times = AssociationKey.TIMES_PER_UNIT.cast(am.get(AssociationKey.TIMES_PER_UNIT));
				sb.append(Constants.PIPE).append("TIMES=").append(times);
				String timeunit = AssociationKey.TIME_UNIT.cast(am.get(AssociationKey.TIME_UNIT));
				if (timeunit != null)
				{
					sb.append(Constants.PIPE).append("TIMEUNIT=").append(timeunit);
				}
				String casterLvl = AssociationKey.CASTER_LEVEL.cast(am.get(AssociationKey.CASTER_LEVEL));
				if (casterLvl != null)
				{
					sb.append(Constants.PIPE).append("CASTERLEVEL=").append(casterLvl);
				}
				Set<String> spellSet = new TreeSet<>();
				for (CDOMReference<Spell> spell : m.getTertiaryKeySet(prereqs, am))
				{
					String spellString = spell.getLSTformat(false);
					String dc = m.get(prereqs, am, spell);
					if (dc != null)
					{
						spellString += Constants.COMMA + dc;
					}
					spellSet.add(spellString);
				}
				sb.append(Constants.PIPE);
				sb.append(StringUtil.join(spellSet, Constants.PIPE));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				set.add(sb.toString());
			}
		}
		return set;
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
