package pcgen.core.analysis;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.DamageReduction;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class TemplateModifier
{

	/**
	 * Generate a string that represents the changes this Template will apply.
	 * 
	 * @param aPC
	 *            the Pc we'd like the string generated with reference to
	 * 
	 * @return a string explaining the Template
	 */
	public static String modifierString(PCTemplate pct, PlayerCharacter aPC)
	{
		StringBuffer mods = new StringBuffer(50); // More likely to be
		// true than 16
		// (the default)

		for (int x = 0; x < SettingsHandler.getGame().getUnmodifiableStatList()
				.size(); ++x)
		{
			if (TemplateStat.isNonAbility(pct, x))
			{
				mods.append(SettingsHandler.getGame().s_ATTRIBSHORT[x]).append(":nonability ");
			}
			else
			{
				int statMod = pct.getStatMod(x, aPC);

				if (statMod != 0)
				{
					mods.append(SettingsHandler.getGame().s_ATTRIBSHORT[x]).append(':').append(
							statMod).append(' ');
				}
			}
		}

		if (pct.getDRList().size() != 0)
		{
			mods.append("DR:").append(
					DamageReduction.getDRString(aPC, pct.getDRList()));
		}

		if (aPC == null)
		{
			BigDecimal cr = pct.get(ObjectKey.CR_MODIFIER);

			if (cr != null)
			{
				mods.append("CR:").append(cr).append(' ');
			}

			int x = pct.getSR(aPC);

			if (x != 0)
			{
				mods.append("SR:").append(x).append(' ');
			}

			// if ((getDR() != null) && !"".equals(getDR()))
			// {
			// mods.append("DR:").append(getDR()).append(' ');
			// }

			return mods.toString();
		}

		int nat = (int) pct.bonusTo("COMBAT", "AC", aPC, aPC);

		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}

		if (pct.getCR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("CR:").append(
					pct.getCR(aPC.getTotalLevels(), aPC.totalHitDice()))
					.append(' ');
		}

		if (TemplateSR
				.getSR(pct, aPC.getTotalLevels(), aPC.totalHitDice(), aPC) != 0)
		{
			mods.append("SR:").append(
					TemplateSR.getSR(pct, aPC.getTotalLevels(), aPC
							.totalHitDice(), aPC)).append(' ');
		}

		// if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
		// {
		// mods.append("DR:").append(getDR(aPC.getTotalLevels(),
		// aPC.totalHitDice()))
		// .append(' ');
		// }

		return mods.toString();
	}

}
