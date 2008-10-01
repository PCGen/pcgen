package pcgen.core.analysis;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.util.Logging;

public final class BonusAddition
{
	/**
	 * Apply the bonus to a character. The bonus can optionally only be added
	 * once no matter how many associated choices this object has. This is
	 * normally used where a bonus is added for each associated choice.
	 * 
	 * @param bonusString
	 *            The unparsed bonus to be added.
	 * @param chooseString
	 *            The choice to be added.
	 * @param aPC
	 *            The character to apply thr bonus to.
	 * @param addOnceOnly
	 *            Should the bonus only be added once irrespective of number of
	 *            choices
	 */
	public static void applyBonus(String bonusString, String chooseString,
			PlayerCharacter aPC, PObject target, boolean addOnceOnly)
	{
		bonusString = target.bonusStringPrefix() + makeBonusString(bonusString, chooseString, aPC);
		target.addBonusList(bonusString, addOnceOnly);
		target.addSave("BONUS|" + bonusString);
	}

	/**
	 * Remove the bonus from this objects list of bonuses.
	 * 
	 * @param bonusString
	 *            The string representing the bonus
	 * @param chooseString
	 *            The choice that was made.
	 * @param aPC
	 *            The player character to remove th bonus from.
	 */
	public static void removeBonus(String bonusString, String chooseString,
			PlayerCharacter aPC, PObject target)
	{
		String bonus = target.bonusStringPrefix() + makeBonusString(bonusString, chooseString, aPC);

		int index = -1;

		BonusObj aBonus = Bonus.newBonus(bonus);
		String bonusStrRep = String.valueOf(aBonus);

		if (target.getBonusList() != null)
		{
			int count = 0;
			for (BonusObj listBonus : target.getBonusList())
			{
				if (listBonus.getCreatorObject().equals(target)
						&& listBonus.toString().equals(bonusStrRep))
				{
					index = count;
				}
				count++;
			}
		}

		if (index >= 0)
		{
			target.getBonusList().remove(index);
		}
		else
		{
			Logging.errorPrint("removeBonus: Could not find bonus: " + bonus
					+ " in bonusList " + target.getBonusList());
		}

		target.removeSave("BONUS|" + bonus);
	}

	private static String makeBonusString(String bonusString,
			String chooseString, PlayerCharacter aPC)
	{
		// assumption is that the chooseString is in the form
		// class/type[space]level
		int i = chooseString.lastIndexOf(' ');
		String classString = "";
		String levelString = "";

		if (bonusString.startsWith("BONUS:"))
		{
			bonusString = bonusString.substring(6);
		}

		boolean lockIt = bonusString.endsWith(".LOCK");

		if (lockIt)
		{
			bonusString = bonusString.substring(0, bonusString
					.lastIndexOf(".LOCK"));
		}

		if (i >= 0)
		{
			classString = chooseString.substring(0, i);

			if (i < chooseString.length())
			{
				levelString = chooseString.substring(i + 1);
			}
		}

		while (bonusString.lastIndexOf("TYPE=%") >= 0)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString
					+ bonusString.substring(i + 6);
		}

		while (bonusString.lastIndexOf("CLASS=%") >= 0)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString
					+ bonusString.substring(i + 7);
		}

		while (bonusString.lastIndexOf("LEVEL=%") >= 0)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString
					+ bonusString.substring(i + 7);
		}

		if (lockIt)
		{
			i = bonusString.lastIndexOf('|');

			Float val = aPC.getVariableValue(bonusString.substring(i + 1), "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}

		return bonusString;
	}

}
