package pcgen.core.analysis;

import java.util.List;

import pcgen.core.Ability;
import pcgen.core.AssociationStore;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.bonus.BonusObj;

public class BonusCalc
{

	/**
	 * gets the bonuses to a stat based on the stat Index
	 * @param statIdx
	 * @param aPC
	 * @return stat mod
	 */
	public static int getStatMod(PObject po, PCStat stat, final PlayerCharacter aPC)
	{
		return (int) BonusCalc.bonusTo(po, "STAT", stat.getAbb(), aPC, aPC);
	}

	public static final double bonusTo(PObject po, String aType, String aName, final Object obj, final List<BonusObj> aBonusList, final PlayerCharacter aPC)
	{
		if ((aBonusList == null) || (aBonusList.size() == 0))
		{
			return 0;
		}
	
		double retVal = 0;
	
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
	
		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();
	
		if (!BonusCalc.dontRecurse && (po instanceof Ability) && !Globals.checkRule(RuleConstants.FEATPRE))
		{
			// SUCK!  This is horrid, but bonusTo is actually recursive with respect to
			// passesPreReqToGain and there is no other way to do this without decomposing the
			// dependencies.  I am loathe to break working code.
			// This addresses bug #709677 -- Feats give bonuses even if you no longer qualify
			BonusCalc.dontRecurse = true;
	
			boolean returnZero = false;
	
			if (!po.qualifies(aPC))
			{
				returnZero = true;
			}
	
			BonusCalc.dontRecurse = false;
	
			if (returnZero)
			{
				return 0;
			}
		}
	
		int iTimes = 1;
	
		if (aPC != null && "VAR".equals(aType))
		{
			iTimes = Math.max(1, aPC.getDetailedAssociationCount(po));
		}
	
		for ( BonusObj bonus : aBonusList )
		{
			String bString = bonus.toString().toUpperCase();
	
			if (aPC != null && aPC.hasAssociations(po))
			{
				int span = 4;
				int idx = bString.indexOf("%VAR");
	
				if (idx == -1)
				{
					idx = bString.indexOf("%LIST|");
					span = 5;
				}
	
				if (idx >= 0)
				{
					final String firstPart = bString.substring(0, idx);
					final String secondPart = bString.substring(idx + span);
	
					for (String assoc : aPC.getAssociationList(po))
					{
						final String xString = new StringBuffer().append(firstPart).append(assoc).append(secondPart)
							.toString().toUpperCase();
						retVal += po.calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
					}
				}
			}
			else
			{
				retVal += po.calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
			}
		}
	
		return retVal;
	}

	/** a boolean for whether something should recurse, default is false */
	public static boolean dontRecurse = false;

	/**
	 * Apply the bonus to a PC, pass through object's default bonuslist
	 *
	 * @param aType
	 * @param aName
	 * @param obj
	 * @param aPC
	 * @return the bonus
	 */
	public static final double bonusTo(PObject po, final String aType, final String aName, final AssociationStore obj, final PlayerCharacter aPC)
	{
		return bonusTo(po, aType, aName, obj, po.getBonusList(obj), aPC);
	}

}
