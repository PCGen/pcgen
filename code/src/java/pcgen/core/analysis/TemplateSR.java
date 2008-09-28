package pcgen.core.analysis;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

public class TemplateSR
{

	/**
	 * Get the Spell Resistance granted by this template to a character at a
	 * given level (Class and Hit Dice). This will include the absolute
	 * adjustment made with SR:, LEVEL:<num>:SR and HD:<num>:SR tags
	 * 
	 * Note: unlike DR and CR, the value returned here includes the PCs own
	 * Spell Resistance.
	 * 
	 * @param level
	 *            The level to calculate the SR for
	 * @param hitdice
	 *            The Hit dice to calculate the SR for
	 * @param aPC
	 *            DOCUMENT ME!
	 * 
	 * @return the Spell Resistance granted by this Template at the given level
	 *         and HD
	 */
	public static int getSR(PCTemplate pct, int level, int hitdice,
			PlayerCharacter aPC)
	{
		String qualifiedKey = pct.getQualifiedKey();
		int aSR = pct.getSafe(ObjectKey.SR).getReduction().resolve(aPC,
				qualifiedKey).intValue();

		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				if (lt.get(IntegerKey.LEVEL) <= level)
				{
					aSR = Math.max(aSR, lt.getSafe(ObjectKey.SR).getReduction()
							.resolve(aPC, qualifiedKey).intValue());
				}
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			if (lt.get(IntegerKey.LEVEL) <= level)
			{
				aSR = Math.max(aSR, lt.getSafe(ObjectKey.SR).getReduction()
						.resolve(aPC, qualifiedKey).intValue());
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			if (lt.get(IntegerKey.HD_MAX) <= hitdice
					&& lt.get(IntegerKey.HD_MIN) >= hitdice)
			{
				aSR = Math.max(aSR, lt.getSafe(ObjectKey.SR).getReduction()
						.resolve(aPC, qualifiedKey).intValue());
			}
		}

		return aSR;
	}

}
