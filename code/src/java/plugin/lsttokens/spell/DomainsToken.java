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
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken extends AbstractToken implements
		CDOMPrimaryToken<Spell>
{

	private static final Class<DomainSpellList> SPELLLIST_CLASS = DomainSpellList.class;

	@Override
	public String getTokenName()
	{
		return "DOMAINS";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (Constants.LST_DOT_CLEARALL.equals(value))
		{
			context.getListContext().clearAllMasterLists(getTokenName(), spell);
			return true;
		}

		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		// Note: May contain PRExxx
		String domainKey;
		Prerequisite prereq = null;
		int openBracketLoc = value.indexOf('[');
		if (openBracketLoc == -1)
		{
			domainKey = value;
		}
		else
		{
			if (value.lastIndexOf(']') != value.length() - 1)
			{
				Logging.errorPrint("Invalid " + getTokenName()
						+ " must end with ']' if it contains a PREREQ tag");
				return false;
			}
			domainKey = value.substring(0, openBracketLoc);
			String prereqString = value.substring(openBracketLoc + 1, value
					.length() - 1);
			if (prereqString.length() == 0)
			{
				Logging.errorPrint(getTokenName()
						+ " cannot have empty prerequisite : " + value);
				return false;
			}
			prereq = getPrerequisite(prereqString);
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer pipeTok = new StringTokenizer(domainKey, Constants.PIPE);

		while (pipeTok.hasMoreTokens())
		{
			// could be name=x or name,name=x
			String tokString = pipeTok.nextToken();

			int equalLoc = tokString.indexOf(Constants.EQUALS);
			if (equalLoc == -1)
			{
				Logging.errorPrint("Malformed " + getTokenName()
						+ " Token (expecting an =): " + tokString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}
			if (equalLoc != tokString.lastIndexOf(Constants.EQUALS))
			{
				Logging.errorPrint("Malformed " + getTokenName()
						+ " Token (more than one =): " + tokString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}

			String nameList = tokString.substring(0, equalLoc);
			String levelString = tokString.substring(equalLoc + 1);
			Integer level;
			try
			{
				level = Integer.valueOf(levelString);
				if (level.intValue() < -1)
				{
					Logging.errorPrint(getTokenName()
							+ " may not use a negative level: " + value);
					return false;
				}
				else if (level.intValue() == -1)
				{
					Logging.deprecationPrint(getTokenName()
							+ " should not use a negative level: " + value);
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Malformed Level in " + getTokenName()
						+ " (expected an Integer): " + levelString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}

			if (hasIllegalSeparator(',', nameList))
			{
				return false;
			}

			StringTokenizer commaTok = new StringTokenizer(nameList,
					Constants.COMMA);

			while (commaTok.hasMoreTokens())
			{
				CDOMReference<DomainSpellList> ref;
				String token = commaTok.nextToken();
				if (Constants.LST_ALL.equals(token))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(SPELLLIST_CLASS);
				}
				else
				{
					foundOther = true;
					ref = TokenUtilities.getTypeOrPrimitive(context,
							SPELLLIST_CLASS, token);
				}
				if (ref == null)
				{
					Logging.errorPrint("  error was in " + getTokenName());
					return false;
				}
				if (level == -1)
				{
					context.getListContext().removeFromMasterList(
							getTokenName(), spell, ref, spell);
				}
				else
				{
					AssociatedPrereqObject edge = context.getListContext()
							.addToMasterList(getTokenName(), spell, ref, spell);
					edge.setAssociation(AssociationKey.SPELL_LEVEL, level);
					if (prereq != null)
					{
						edge.addPrerequisite(prereq);
					}
				}
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		DoubleKeyMapToList<Prerequisite, Integer, CDOMReference<DomainSpellList>> dkmtl = new DoubleKeyMapToList<Prerequisite, Integer, CDOMReference<DomainSpellList>>();
		List<String> list = new ArrayList<String>();
		Changes<CDOMReference> masterChanges = context.getListContext()
				.getMasterListChanges(getTokenName(), spell, SPELLLIST_CLASS);
		if (masterChanges.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEARALL);
		}
		if (masterChanges.hasRemovedItems())
		{
			context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
			return null;
		}
		for (CDOMReference<DomainSpellList> swl : masterChanges.getAdded())
		{
			AssociatedChanges<Spell> changes = context.getListContext()
					.getChangesInMasterList(getTokenName(), spell, swl);
			Collection<Spell> removedItems = changes.getRemoved();
			if (removedItems != null && !removedItems.isEmpty()
					|| changes.includesGlobalClear())
			{
				context.addWriteMessage(getTokenName()
						+ " does not support .CLEAR.");
				return null;
			}
			MapToList<Spell, AssociatedPrereqObject> map = changes
					.getAddedAssociations();
			if (map != null && !map.isEmpty())
			{
				for (Spell added : map.getKeySet())
				{
					if (!spell.getLSTformat().equals(added.getLSTformat()))
					{
						context.addWriteMessage("Spell " + getTokenName()
								+ " token cannot allow another Spell "
								+ "(must only allow itself)");
						return null;
					}
					for (AssociatedPrereqObject assoc : map.getListFor(added))
					{
						List<Prerequisite> prereqs = assoc
								.getPrerequisiteList();
						Prerequisite prereq;
						if (prereqs == null || prereqs.size() == 0)
						{
							prereq = null;
						}
						else if (prereqs.size() == 1)
						{
							prereq = prereqs.get(0);
						}
						else
						{
							context.addWriteMessage("Incoming Edge to "
									+ spell.getKeyName()
									+ " had more than one " + "Prerequisite: "
									+ prereqs.size());
							return null;
						}
						Integer level = assoc
								.getAssociation(AssociationKey.SPELL_LEVEL);
						if (level == null)
						{
							context.addWriteMessage("Incoming Allows Edge to "
									+ spell.getKeyName()
									+ " had no Spell Level defined");
							return null;
						}
						if (level.intValue() < 0)
						{
							context.addWriteMessage("Incoming Allows Edge to "
									+ spell.getKeyName()
									+ " had invalid Level: " + level
									+ ". Must be >= 0.");
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
				// Legal if no DOMAINS was present in the Spell
				return null;
			}
			else
			{
				return list.toArray(new String[list.size()]);
			}
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CDOMReference<DomainSpellList>> set = new TreeSet<CDOMReference<DomainSpellList>>(
				TokenUtilities.REFERENCE_SORTER);
		SortedSet<Integer> levelSet = new TreeSet<Integer>();
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
				boolean needComma = false;
				for (LSTWriteable wr : set)
				{
					if (needComma)
					{
						sb.append(',');
					}
					needComma = true;
					String s = wr.getLSTformat();
					if (Constants.LST_ANY.equals(s))
					{
						s = Constants.LST_ALL;
					}
					sb.append(s);
				}
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
		return list.toArray(new String[list.size()]);
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
