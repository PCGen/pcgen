/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class SaLst implements GlobalLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Skill)
		{
			Logging.errorPrint("SA not supported in Skills");
			return false;
		}
		parseSpecialAbility(obj, value, anInt);
		return true;
	}

	/**
	 * This method sets the special abilities granted by this [object]. For
	 * efficiency, avoid calling this method except from I/O routines.
	 *
	 * @param obj
	 *          the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *          String of special abilities delimited by pipes
	 * @param level
	 *          int level at which the ability is gained
	 */
	public void parseSpecialAbility(PObject obj, String aString,
		int level)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", true);

		if (!aTok.hasMoreTokens())
		{
			return;
		}

		StringBuffer saName = new StringBuffer();
		saName.append(aTok.nextToken());

		SpecialAbility sa = new SpecialAbility();

		boolean isPre = false;
		
		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(cString))
			{
				isPre = true;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(cString);
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
				}
			}
			else
			{
				if (isPre)
				{
					if (!"|".equals(cString))
					{
						Logging.errorPrint("Invalid " + getTokenName() + ": "
							+ aString);
						Logging
							.errorPrint("  PRExxx must be at the END of the Token");
						isPre = false;
					}
				}
				saName.append(cString);
			}

			if (".CLEAR".equals(cString))
			{
				obj.clearSpecialAbilityList();
				saName.setLength(0);
			}
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
				Logging.errorPrint("Failed to assign level prerequisite.", notUsed);
			}
		}
		if (obj instanceof PCClass)
		{
			sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		}

		if (!aString.equals(".CLEAR"))
		{
			Globals.addToSASet(sa);
			obj.addSpecialAbilityToList(sa);
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return "SA has been deprecated due to cross-level interaction.\n  " +
				"Please use SAB: for most situations.\n  " +
				"If you are using .CLEAR in an SA to remove items granted\n  " +
				"  at a lower level, you must now use a PRExxx token attached to the SAB";
	}
}
