package plugin.lsttokens;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.StringTokenizer;

public class SabLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "SAB";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Skill)
		{
			Logging.errorPrint("SA not supported in Skills");
			return false;
		}
		return parseSpecialAbility(obj, value, anInt);
	}

	/**
	 * This method sets the special abilities granted by this [object].
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public boolean parseSpecialAbility(PObject obj, String value, int level)
	{
		if (value.startsWith(".CLEAR."))
		{
			String saName = value.substring(7);
			if (saName.indexOf("|") != -1)
			{
				Logging
					.errorPrint("Cannot .CLEAR. an SAB with a | in the token: "
						+ value);
				return false;
			}
			obj.removeSAB(saName, level);
			return true;
		}
		StringTokenizer tok = new StringTokenizer(value, "|");

		String token = tok.nextToken();

		if (".CLEAR".equals(token))
		{
			obj.clearSABList(level);
			if (!tok.hasMoreTokens())
			{
				return true;
			}
			token = tok.nextToken();
		}

		StringBuffer saName = new StringBuffer();
		saName.append(token);
		SpecialAbility sa = new SpecialAbility();

		boolean isPre = false;
		boolean first = false;

		while (tok.hasMoreTokens())
		{
			String argument = tok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(argument))
			{
				isPre = true;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(argument);
					if (obj instanceof PCClass
						&& "var".equals(prereq.getKind()))
					{
						prereq.setSubKey("CLASS:" + obj.getKeyName());
					}
					sa.addPreReq(prereq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
					return false;
				}
			}
			else if (token.startsWith(".CLEAR"))
			{
				Logging.errorPrint("Embedded .CLEAR in " + getTokenName()
					+ " is not supported: " + value);
				return false;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
						+ value);
					Logging
						.errorPrint("  PRExxx must be at the END of the Token");
					return false;
				}
				if (!first)
				{
					saName.append("|");
				}
				saName.append(argument);
			}
			first = false;
		}

		sa.setName(saName.toString());

		if (level >= 0)
		{
			try
			{
				sa.addPreReq(PreParserFactory.createLevelPrereq(obj, level));
			}
			catch (PersistenceLayerException notUsed)
			{
				Logging.errorPrint("Failed to assign level prerequisite.",
					notUsed);
			}
		}
		if (obj instanceof PCClass)
		{
			sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		}

		Globals.addToSASet(sa);
		obj.addSAB(sa, level);
		return true;
	}
}
