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
package plugin.lsttokens.spell;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractTokenWithSeparator<Spell> implements CDOMPrimaryToken<Spell>
{

	private static final Class<ClassSpellList> SPELLLIST_CLASS = ClassSpellList.class;

	@Override
	public String getTokenName()
	{
		return "CLASSES";
	}

	@Override
	public ParseResult parseToken(LoadContext context, Spell spell, String value)
	{
		if (Constants.LST_DOT_CLEAR_ALL.equals(value))
		{
			context.getListContext().clearAllMasterLists(getTokenName(), spell);
			return ParseResult.SUCCESS;
		}
		return super.parseToken(context, spell, value);
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, Spell spell, String value)
	{
		// Note: May contain PRExxx
		String classKey;
		Prerequisite prereq = null;

		int openBracketLoc = value.indexOf('[');
		if (openBracketLoc == -1)
		{
			classKey = value;
		}
		else
		{
			if (value.lastIndexOf(']') != value.length() - 1)
			{
				return new ParseResult.Fail(
					"Invalid " + getTokenName() + " must end with ']' if it contains a PREREQ tag");
			}
			if (value.lastIndexOf('|') > openBracketLoc)
			{
				return new ParseResult.Fail(
					"Invalid " + getTokenName() + ": PRExxx must be at the END of the Token. Token was " + value);
			}
			classKey = value.substring(0, openBracketLoc);
			String prereqString = value.substring(openBracketLoc + 1, value.length() - 1);
			if (prereqString.isEmpty())
			{
				return new ParseResult.Fail(getTokenName() + " cannot have empty prerequisite : " + value);
			}
			prereq = getPrerequisite(prereqString);
			if (prereq == null)
			{
				return new ParseResult.Fail(getTokenName() + " had invalid prerequisite : " + prereqString);
			}
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer pipeTok = new StringTokenizer(classKey, Constants.PIPE);

		while (pipeTok.hasMoreTokens())
		{
			// could be name=x or name,name=x
			String tokString = pipeTok.nextToken();

			int startPos = tokString.startsWith("TYPE=") ? "TYPE=".length() : 0;
			int equalLoc = tokString.indexOf(Constants.EQUALS, startPos);
			if (equalLoc == -1)
			{
				return new ParseResult.Fail("Malformed " + getTokenName() + " Token (expecting an =): " + tokString);
			}
			if (equalLoc != tokString.lastIndexOf(Constants.EQUALS))
			{
				return new ParseResult.Fail("Malformed " + getTokenName() + " Token (more than one =): " + tokString);
			}

			String nameList = tokString.substring(0, equalLoc);
			String levelString = tokString.substring(equalLoc + 1);
			int level;
			try
			{
				level = Integer.parseInt(levelString);
				if (level < -1)
				{
					return new ParseResult.Fail(getTokenName() + " may not use a negative level: " + value);
				}
				else if (level == -1)
				{
					if (prereq != null)
					{
						return new ParseResult.Fail(getTokenName() + " may not use -1 with a PREREQ: " + value);
					}
					// Logging.deprecationPrint(getTokenName()
					// + " should not use a negative level: " + value);
				}
			}
			catch (NumberFormatException nfe)
			{
				return new ParseResult.Fail(
					"Malformed Level in " + getTokenName() + " (expected an Integer): " + levelString);
			}

			ParseResult pr = checkForIllegalSeparator(',', nameList);
			if (!pr.passed())
			{
				return pr;
			}

			StringTokenizer commaTok = new StringTokenizer(nameList, Constants.COMMA);

			while (commaTok.hasMoreTokens())
			{
				CDOMReference<ClassSpellList> ref;
				String token = commaTok.nextToken();
				if (Constants.LST_ALL.equals(token))
				{
					foundAny = true;
					ref = context.getReferenceContext().getCDOMAllReference(SPELLLIST_CLASS);
				}
				else
				{
					foundOther = true;
					ref = TokenUtilities.getTypeOrPrimitive(context, SPELLLIST_CLASS, token);
					if (ref == null)
					{
						return new ParseResult.Fail("  error was in " + getTokenName());
					}
				}
				if (level == -1)
				{
					//No need to check for prereq here - done above
					context.getListContext().removeFromMasterList(getTokenName(), spell, ref, spell);
				}
				else
				{
					AssociatedPrereqObject edge =
							context.getListContext().addToMasterList(getTokenName(), spell, ref, spell);
					edge.setAssociation(AssociationKey.SPELL_LEVEL, level);
					if (prereq != null)
					{
						edge.addPrerequisite(prereq);
					}
					context.getObjectContext().addToList(spell, ListKey.SPELL_CLASSLEVEL, token + ' ' + level);
				}
			}
		}
		if (foundAny && foundOther)
		{
			return new ParseResult.Fail(
				"Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Spell spell)
	{
		DoubleKeyMapToList<Prerequisite, Integer, CDOMReference<ClassSpellList>> dkmtl = new DoubleKeyMapToList<>();
		List<String> list = new ArrayList<>();
		Changes<CDOMReference<ClassSpellList>> masterChanges =
				context.getListContext().getMasterListChanges(getTokenName(), spell, SPELLLIST_CLASS);
		if (masterChanges.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR_ALL);
		}
		if (masterChanges.hasRemovedItems())
		{
			for (CDOMReference<ClassSpellList> swl : masterChanges.getRemoved())
			{
				AssociatedChanges<Spell> changes =
						context.getListContext().getChangesInMasterList(getTokenName(), spell, swl);
				MapToList<Spell, AssociatedPrereqObject> map = changes.getRemovedAssociations();
				if (map != null && !map.isEmpty())
				{
					for (Spell added : map.getKeySet())
					{
						if (!spell.getKeyName().equals(added.getKeyName()))
						{
							context.addWriteMessage("Spell " + getTokenName() + " token cannot remove another Spell "
								+ "(must only remove itself)");
							return null;
						}
						for (AssociatedPrereqObject assoc : map.getListFor(added))
						{
							List<Prerequisite> prereqs = assoc.getPrerequisiteList();
							if (prereqs != null && !prereqs.isEmpty())
							{
								context.addWriteMessage("Incoming Remove " + "Edge to " + spell.getKeyName() + " had a "
									+ "Prerequisite: " + prereqs.size());
								return null;
							}
							dkmtl.addToListFor(null, -1, swl);
						}
					}
				}
			}
		}
		for (CDOMReference<ClassSpellList> swl : masterChanges.getAdded())
		{
			AssociatedChanges<Spell> changes =
					context.getListContext().getChangesInMasterList(getTokenName(), spell, swl);
			Collection<Spell> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName() + " does not support .CLEAR.");
				return null;
			}
			MapToList<Spell, AssociatedPrereqObject> map = changes.getAddedAssociations();
			if (map != null && !map.isEmpty())
			{
				for (Spell added : map.getKeySet())
				{
					if (!spell.getKeyName().equals(added.getKeyName()))
					{
						context.addWriteMessage("Spell " + getTokenName() + " token cannot allow another Spell "
							+ "(must only allow itself)");
						return null;
					}
					for (AssociatedPrereqObject assoc : map.getListFor(added))
					{
						List<Prerequisite> prereqs = assoc.getPrerequisiteList();
						Prerequisite prereq;
						if (prereqs == null || prereqs.isEmpty())
						{
							prereq = null;
						}
						else if (prereqs.size() == 1)
						{
							prereq = prereqs.get(0);
						}
						else
						{
							context.addWriteMessage("Incoming Edge to " + spell.getKeyName() + " had more than one "
								+ "Prerequisite: " + prereqs.size());
							return null;
						}
						Integer level = assoc.getAssociation(AssociationKey.SPELL_LEVEL);
						if (level == null)
						{
							context.addWriteMessage(
								"Incoming Allows Edge to " + spell.getKeyName() + " had no Spell Level defined");
							return null;
						}
						if (level < 0)
						{
							context.addWriteMessage("Incoming Allows Edge to " + spell.getKeyName()
								+ " had invalid Level: " + level + ". Must be >= 0.");
							return null;
						}
						dkmtl.addToListFor(prereq, level, swl);
					}
				}
			}
		}
		if (dkmtl.isEmpty())
		{
			if (list.isEmpty())
			{
				return null; // Legal if no CLASSES was present in the Spell
			}
			else
			{
				return list.toArray(new String[0]);
			}
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CDOMReference<ClassSpellList>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
		SortedSet<Integer> levelSet = new TreeSet<>();
		for (Prerequisite prereq : dkmtl.getKeySet())
		{
			StringBuilder sb = new StringBuilder();
			boolean needPipe = false;
			levelSet.clear();
			levelSet.addAll(dkmtl.getSecondaryKeySet(prereq));
			for (Integer i : levelSet)
			{
				set.clear();
				set.addAll(dkmtl.getListFor(prereq, i));
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(ReferenceUtilities.joinLstFormat(set, Constants.COMMA));
				sb.append('=').append(i);
				needPipe = true;
			}
			if (prereq != null)
			{
				sb.append('[');
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, prereq);
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				sb.append(swriter.toString());
				sb.append(']');
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[0]);
	}

	@Override
	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
