/*
 * CompanionListLst.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ListMatchingReference;
import pcgen.cdom.reference.ObjectMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.FollowerOption;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.LstUtils;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * This class implments the parsing for the COMPANIONLIST token.
 * <p>
 * <b>Tag Name</b>: {@code COMPANIONLIST}:x|y,y|z
 * <p>
 * <b>Variables Used (x)</b>: <i>Text</i> (The type of companion list to add
 * to).<br>
 * <b>Variables Used (y)</b>: <i>Text</i> (A race of companion to allow to the
 * character).<br>
 * <b>Variables Used (y)</b>: {@code RACETYPE}=<i>Text</i> (all races
 * with the specified {@code RACETYPE} are available as this type of
 * companion). <br>
 * <b>Variables Used (y)</b>: {@code ANY} (Any race can be a companion
 * of this type).<br>
 * <b>Variables Used (z)</b>: {@code FOLLOWERADJUSTMENT}=<i>Number</i>
 * (Adjustment to the follower level variable).
 * <p>
 * <b>What it does:</b>
 * <ul>
 * <li>Adds a specific race or races to the list of available companions for
 * the specified companion type.</li>
 * <li>PRExxx tags can be added at the end of COMPANIONLIST tags, PRExxx tags
 * are checked against the master.</li>
 * <li>If the master does not meet the prereqs the companion will be displayed
 * in the list but will be listed in red and cannot be added as a companion.
 * </li>
 * </ul>
 * <p>
 * <b>Examples:</b><br>
 * {@code COMPANIONLIST:Familiar|Bat,Cat,Hawk,Lizard,Owl,Rat,Raven,
 * Snake (Tiny/Viper),Toad,Weasel}<br>
 * Would build the list of standard familiars available to a Sorcerer or Wizard.
 * <p>
 * {@code COMPANIONLIST:Pet|RACETYPE=Animal}<br>
 * Would build a list of all animals to available as a Pet.
 * <p>
 * {@code COMPANIONLIST:Familiar|Quasit|PREFEAT:1,Special Familiar|
 * PREALIGN:CE}<br>
 * A Quasit can be chosen as a Familiar but only if the master is evil and has
 * the Special Familiar feat.
 * <p>
 * {@code COMPANIONLIST:Animal Companion|Ape|FOLLOWERADJUSTMENT:-3}
 * An Ape companion to a 4th level Druid gains the benefits normally granted to
 * a companion of a 1st level Druid.
 */
public class CompanionListLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
	private static final String COMPANIONLIST = "COMPANIONLIST"; //$NON-NLS-1$
	private static final String FOLLOWERADJUSTMENT = "FOLLOWERADJUSTMENT"; //$NON-NLS-1$

	@Override
	public String getTokenName()
	{
		return COMPANIONLIST;
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		StringTokenizer tok = new StringTokenizer(value, LstUtils.PIPE);
		String companionType = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(getTokenName() + " requires more than just a Type: " + value);
		}
		String list = tok.nextToken();
		ParseResult pr = checkForIllegalSeparator(',', list);
		if (!pr.passed())
		{
			return pr;
		}
		StringTokenizer subTok = new StringTokenizer(list, LstUtils.COMMA);
		Set<CDOMReference<Race>> races = new HashSet<>();
		boolean foundAny = false;
		while (subTok.hasMoreTokens())
		{
			String tokString = subTok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(tokString))
			{
				foundAny = true;
				races.add(context.getReferenceContext().getCDOMAllReference(Race.class));
			}
			else if (tokString.startsWith("RACETYPE="))
			{
				String raceType = tokString.substring(9);
				if (raceType.isEmpty())
				{
					return new ParseResult.Fail(getTokenName() + " Error: RaceType was not specified.");
				}
				races.add(new ObjectMatchingReference<>(tokString,
					context.getReferenceContext().getCDOMAllReference(Race.class), ObjectKey.RACETYPE,
					RaceType.getConstant(raceType)));
			}
			else if (tokString.startsWith("RACESUBTYPE="))
			{
				String raceSubType = tokString.substring(12);
				if (raceSubType.isEmpty())
				{
					return new ParseResult.Fail(getTokenName() + " Error: RaceSubType was not specified.");
				}
				races.add(new ListMatchingReference<>(tokString,
					context.getReferenceContext().getCDOMAllReference(Race.class), ListKey.RACESUBTYPE,
					RaceSubType.getConstant(raceSubType)));
			}
			else if (looksLikeAPrerequisite(tokString))
			{
				return new ParseResult.Fail(
					getTokenName() + " Error: " + tokString + " found where companion race expected.");
			}
			else
			{
				races.add(context.getReferenceContext().getCDOMReference(Race.class, tokString));
			}
		}
		if (foundAny && races.size() > 1)
		{
			return new ParseResult.Fail("Non-sensical Race List includes Any and specific races: " + value);
		}
		if (!tok.hasMoreTokens())
		{
			// No other args, so we're done
			finish(context, obj, companionType, races, null, null);
			return ParseResult.SUCCESS;
		}
		// The remainder of the elements are optional.
		Integer followerAdjustment = null;
		String optArg = tok.nextToken();
		while (true)
		{
			if (optArg.startsWith(FOLLOWERADJUSTMENT))
			{
				if (followerAdjustment != null)
				{
					return new ParseResult.Fail(
						getTokenName() + " Error: Multiple " + FOLLOWERADJUSTMENT + " tags specified.");
				}
				int faStringLength = FOLLOWERADJUSTMENT.length();
				if (optArg.length() <= faStringLength + 1)
				{
					return new ParseResult.Fail(
						"Empty FOLLOWERADJUSTMENT value in " + getTokenName() + " is prohibited");
				}
				String adj = optArg.substring(faStringLength + 1);
				try
				{
					followerAdjustment = Integer.valueOf(adj);
				}
				catch (NumberFormatException nfe)
				{
					ComplexParseResult cpr = new ComplexParseResult();
					cpr.addErrorMessage("Expecting a number for FOLLOWERADJUSTMENT: " + adj);
					cpr.addErrorMessage("  was parsing Token " + getTokenName());
					return cpr;
				}
			}
			else if (looksLikeAPrerequisite(optArg))
			{
				break;
			}
			else
			{
				return new ParseResult.Fail(
					getTokenName() + ": Unknown argument (was expecting FOLLOWERADJUSTMENT: or PRExxx): " + optArg);
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				finish(context, obj, companionType, races, followerAdjustment, null);
				return ParseResult.SUCCESS;
			}
			optArg = tok.nextToken();
		}
		List<Prerequisite> prereqs = new ArrayList<>();
		while (true)
		{
			Prerequisite prereq = getPrerequisite(optArg);
			if (prereq == null)
			{
				return new ParseResult.Fail(
					"   (Did you put items after the " + "PRExxx tags in " + getTokenName() + ":?)");
			}
			prereqs.add(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			optArg = tok.nextToken();
		}

		finish(context, obj, companionType, races, followerAdjustment, prereqs);
		return ParseResult.SUCCESS;
	}

	private void finish(LoadContext context, CDOMObject obj, String companionType, Set<CDOMReference<Race>> races,
		Integer followerAdjustment, List<Prerequisite> prereqs)
	{
		context.getReferenceContext().constructIfNecessary(CompanionList.class, companionType);
		CDOMSingleRef<CompanionList> ref =
				context.getReferenceContext().getCDOMReference(CompanionList.class, companionType);

		for (CDOMReference<Race> race : races)
		{
			final FollowerOption option = new FollowerOption(race, ref);
			if (prereqs != null && !prereqs.isEmpty())
			{
				option.addAllPrerequisites(prereqs);
			}
			if (followerAdjustment != null && followerAdjustment != 0)
			{
				option.setAdjustment(followerAdjustment);
			}
			context.getObjectContext().addToList(obj, ListKey.COMPANIONLIST, option);
		}
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<FollowerOption> changes = context.getObjectContext().getListChanges(obj, ListKey.COMPANIONLIST);
		Collection<FollowerOption> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
		{
			context.addWriteMessage(getTokenName() + " does not support .CLEAR");
			return null;
		}
		Collection<FollowerOption> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		TripleKeyMapToList<Set<Prerequisite>, CDOMReference<? extends CDOMList<?>>, Integer, CDOMReference<Race>> m =
				new TripleKeyMapToList<>();
		for (FollowerOption fo : added)
		{
			m.addToListFor(new HashSet<>(fo.getPrerequisiteList()), fo.getListRef(), fo.getAdjustment(),
				fo.getRaceRef());
		}
		Set<String> set = new TreeSet<>();
		StringBuilder sb = new StringBuilder();
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String prereqString = null;
			if (prereqs != null && !prereqs.isEmpty())
			{
				prereqString = getPrerequisiteString(context, prereqs);
			}

			for (CDOMReference<? extends CDOMList<?>> cl : m.getSecondaryKeySet(prereqs))
			{
				for (Integer fa : m.getTertiaryKeySet(prereqs, cl))
				{
					sb.setLength(0);
					sb.append(cl.getLSTformat(false));
					sb.append(Constants.PIPE);
					Set<CDOMReference<Race>> raceSet = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
					raceSet.addAll(m.getListFor(prereqs, cl, fa));
					sb.append(ReferenceUtilities.joinLstFormat(raceSet, Constants.COMMA, true));
					if (fa != null && fa != 0)
					{
						sb.append(Constants.PIPE);
						sb.append("FOLLOWERADJUSTMENT:");
						sb.append(fa);
					}
					if (prereqString != null)
					{
						sb.append(Constants.PIPE);
						sb.append(prereqString);
					}
					set.add(sb.toString());
				}
			}
		}
		return set.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
