package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "STARTFEATS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append("FEAT|POOL|").append(Integer.parseInt(value));

			final BonusObj bon = Bonus.newBonus(sb.toString());
			final PreParserFactory factory = PreParserFactory.getInstance();
			final StringBuffer buf = new StringBuffer();

			buf.append("PREMULT:1,[PREHD:MIN=1],[PRELEVEL:1]");

			final Prerequisite prereq = factory.parse(buf.toString());
			bon.addPreReq(prereq);

			race.setBonusInitialFeats(bon);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
			return false;
		}
	}
}
