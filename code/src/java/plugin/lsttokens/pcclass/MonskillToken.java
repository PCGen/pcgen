package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MONSKILL Token
 */
public class MonskillToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "MONSKILL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final BonusObj aBonus = Bonus.newBonus("0|MONSKILLPTS|NUMBER|" + value + "|PRELEVELMAX:1");
		
		if (aBonus != null)
		{
			aBonus.setCreatorObject(pcclass);
			pcclass.addToListFor(ListKey.BONUS, aBonus);
		}
		return (aBonus != null);
	}
}
