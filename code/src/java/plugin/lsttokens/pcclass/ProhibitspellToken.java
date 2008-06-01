package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "PROHIBITSPELL";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		SpellProhibitor sp = subParse(context, pcc, value);
		if (sp == null)
		{
			return false;
		}
		context.getObjectContext().addToList(pcc, ListKey.SPELL_PROHIBITOR, sp);
		return true;
	}

	public SpellProhibitor subParse(LoadContext context, PCClass pcc,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		int dotLoc = token.indexOf(Constants.DOT);
		if (dotLoc == -1)
		{
			Logging.errorPrint(getTokenName()
					+ " has no . separator for arguments: " + value);
			return null;
		}
		String pstString = token.substring(0, dotLoc);
		ProhibitedSpellType type;

		try
		{
			type = ProhibitedSpellType.valueOf(pstString);
		}
		catch (IllegalArgumentException e)
		{
			Logging
					.errorPrint(getTokenName()
							+ " encountered an invalid Prohibited Spell Type: "
							+ value);
			Logging.errorPrint("  Legal values are: "
					+ StringUtil.join(Arrays.asList(ProhibitedSpellType
							.values()), ", "));
			return null;
		}

		SpellProhibitor spellProb = typeSafeParse(context, pcc, type, token
				.substring(dotLoc + 1));
		if (spellProb == null)
		{
			Logging.errorPrint("  entire token value was: " + value);
			return null;
		}
		if (!tok.hasMoreTokens())
		{
			// No prereqs, so we're done
			return spellProb;
		}
		token = tok.nextToken();

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging
						.errorPrint("   (Did you put more than one limit, or items after the "
								+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
			}
			spellProb.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		return spellProb;
	}

	private SpellProhibitor typeSafeParse(LoadContext context, PCClass pcc,
			ProhibitedSpellType type, String args)
	{
		SpellProhibitor spellProb = new SpellProhibitor();
		spellProb.setType(type);
		if (args.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " " + type
					+ " has no arguments");
			return null;
		}

		String joinChar = getJoinChar(type, new LinkedList<String>());
		if (args.indexOf(joinChar) == 0)
		{
			Logging.errorPrint(getTokenName()
					+ " arguments may not start with " + joinChar);
			return null;
		}
		if (args.lastIndexOf(joinChar) == args.length() - 1)
		{
			Logging.errorPrint(getTokenName() + " arguments may not end with "
					+ joinChar);
			return null;
		}
		if (args.indexOf(joinChar + joinChar) != -1)
		{
			Logging
					.errorPrint(getTokenName()
							+ " arguments uses double separator " + joinChar
							+ joinChar);
			return null;
		}

		StringTokenizer elements = new StringTokenizer(args, joinChar);
		while (elements.hasMoreTokens())
		{
			String aValue = elements.nextToken();
			if (type.equals(ProhibitedSpellType.ALIGNMENT)
					&& (!aValue.equalsIgnoreCase("GOOD"))
					&& (!aValue.equalsIgnoreCase("EVIL"))
					&& (!aValue.equalsIgnoreCase("LAWFUL"))
					&& (!aValue.equalsIgnoreCase("CHAOTIC")))
			{
				Logging.errorPrint("Illegal PROHIBITSPELL:ALIGNMENT subtag '"
						+ aValue + "'");
				return null;
			}
			else
			{
				spellProb.addValue(aValue);
			}
		}
		return spellProb;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<SpellProhibitor> changes = context.getObjectContext()
				.getListChanges(pcc, ListKey.SPELL_PROHIBITOR);
		Collection<SpellProhibitor> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (SpellProhibitor sp : added)
		{
			StringBuilder sb = new StringBuilder();
			ProhibitedSpellType pst = sp.getType();
			sb.append(pst.toString().toUpperCase());
			sb.append('.');
			Collection<String> valueSet = sp.getValueList();
			String joinChar = getJoinChar(pst, valueSet);
			sb.append(StringUtil.join(new TreeSet<String>(valueSet), joinChar));

			if (sp.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, sp
						.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	private <T> String getJoinChar(ProhibitedSpellType pst,
			Collection<String> spValues)
	{
		return pst.getRequiredCount(spValues) == 1 ? Constants.COMMA
				: Constants.DOT;
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
