package plugin.lsttokens.subclass;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken extends AbstractToken implements
		CDOMPrimaryToken<SubClass>
{

	@Override
	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(LoadContext context, SubClass pcc, String value)
	{
		SpellProhibitor sp = subParse(context, pcc, value);
		if (sp == null)
		{
			return false;
		}
		context.getObjectContext().put(pcc, ObjectKey.CHOICE, sp);
		return true;
	}

	public SpellProhibitor subParse(LoadContext context, SubClass pcc,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return null;
		}

		int pipeLoc = value.indexOf('|');
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName()
					+ " has no | separator for arguments: " + value);
			return null;
		}

		if (value.lastIndexOf('|') != pipeLoc)
		{
			Logging.errorPrint(getTokenName()
					+ " has more than two | separated arguments: " + value);
			return null;
		}

		String pstString = value.substring(0, pipeLoc);
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
		if (type.equals(ProhibitedSpellType.SCHOOL)
				|| type.equals(ProhibitedSpellType.SUBSCHOOL)
				|| type.equals(ProhibitedSpellType.DESCRIPTOR))
		{
			SpellProhibitor spellProb = typeSafeParse(context, pcc, type, value
					.substring(pipeLoc + 1));
			if (spellProb == null)
			{
				Logging.errorPrint("  entire token value was: " + value);
				return null;
			}
			return spellProb;
		}

		Logging.errorPrint("Invalid TYPE in " + getTokenName() + ": "
				+ pstString);
		return null;
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
		spellProb.addValue(args);
		return spellProb;
	}

	public String[] unparse(LoadContext context, SubClass pcc)
	{
		SpellProhibitor sp = context.getObjectContext().getObject(pcc,
				ObjectKey.CHOICE);
		if (sp == null)
		{
			// Zero indicates no Token present
			return null;
		}
		StringBuilder sb = new StringBuilder();
		ProhibitedSpellType pst = sp.getType();
		sb.append(pst.toString().toUpperCase());
		sb.append('|');
		Collection<String> valueSet = sp.getValueList();
		sb.append(StringUtil
				.join(new TreeSet<String>(valueSet), Constants.PIPE));
		return new String[] { sb.toString() };
	}

	public Class<SubClass> getTokenClass()
	{
		return SubClass.class;
	}

}
