/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * @author djones4
 *
 */
public class SaLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
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
	public static void parseSpecialAbility(PObject obj, String aString,
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

		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();

			// Check to see if it's a PRExxx: tag
			if (PreParserFactory.isPreReqString(cString))
			{
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
				saName.append(cString);
			}

			if (".CLEAR".equals(cString))
			{
				obj.clearSpecialAbilityList();
				saName.setLength(0);
			}
		}

		sa.setName(saName.toString());

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
}
