package pcgen.core.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.PointCost;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.SpellPointCostInfo;
import pcgen.core.bonus.util.SpellPointCostInfo.SpellPointFilterType;
import pcgen.core.spell.Spell;

public class SpellPoint
{

	public static String getSpellPointCostPartValue(Spell sp,
			final int elementNumber)
	{
		Map<String, Integer> spCosts = getSpellPointCostActualParts(sp);
		Set<String> spKeys = new TreeSet<String>();
		spKeys.addAll(spCosts.keySet());
		String[] theKeys = spKeys.toArray(new String[spKeys.size()]);
		int size = spKeys.size();
		if (elementNumber < size)
		{
			return spCosts.get(theKeys[elementNumber]).toString();
		}

		return "";
	}

	public static String getSpellPointCostPartName(Spell sp,
			final int elementNumber)
	{
		Map<String, Integer> spCosts = getSpellPointCostActualParts(sp);
		Set<String> spKeys = new TreeSet<String>();
		spKeys.addAll(spCosts.keySet());
		String[] theKeys = spKeys.toArray(new String[spKeys.size()]);
		int size = spKeys.size();
		if (elementNumber < size)
		{
			return theKeys[elementNumber];
		}

		return "";
	}

	public static int getSpellPointCostActual(PlayerCharacter aPC, Spell sp)
	{
		int runningTotal = 0;
		Map<String, Integer> spCost = getSpellPointCostActualParts(sp);
		for (Integer i : spCost.values())
		{
			runningTotal += i;
		}
		if (!aPC.hasSpellInSpellbook(sp, aPC.getSpellBookNameToAutoAddKnown()))
		{
			return runningTotal;
		}
		for (BonusObj b : aPC.getActiveBonusList())
		{
			if (b.toString().contains("SPELLPOINTCOST"))
			{
				try
				{
					List<SpellPointCostInfo> spBonusInfo = (List<SpellPointCostInfo>) b
							.getBonusInfoList();
					for (SpellPointCostInfo info : spBonusInfo)
					{
						if (!info.isVirtual())
						{
							boolean getBonus = false;
							if (info.getSpellPointPartFilter() == SpellPointFilterType.SCHOOL)
							{
								for (SpellSchool aSchool : sp
										.getSafeListFor(ListKey.SPELL_SCHOOL))
								{
									if (info.getSpellPointPartFilterValue()
											.equalsIgnoreCase(aSchool.toString()))
										getBonus = true;
								}
							}
							else if (info.getSpellPointPartFilter() == SpellPointFilterType.SUBSCHOOL)
							{
								for (String aSchool : sp
										.getSafeListFor(ListKey.SPELL_SUBSCHOOL))
								{
									if (info.getSpellPointPartFilterValue()
											.equalsIgnoreCase(aSchool))
										getBonus = true;
								}
							}
							else if (info.getSpellPointPartFilter() == SpellPointFilterType.SPELL
									&& sp.getDisplayName().equalsIgnoreCase(
											info.getSpellPointPartFilterValue()
													.toUpperCase()))
							{
								getBonus = true;
							}
							if (getBonus)
							{
								runningTotal += b.resolve(aPC, "").intValue();
							}
						}
					}
				}
				catch (Exception e)
				{

				}
			}
		}
		return runningTotal;
	}

	public static int getSpellPointCostActual(Spell sp)
	{
		int runningTotal = 0;
		Map<String, Integer> spCost = getSpellPointCostActualParts(sp);
		for (Integer i : spCost.values())
		{
			runningTotal += i;
		}
		return runningTotal;
	}

	/**
	 * For a passed component name and PC, this returns any bonus from SCHOOL,
	 * SUBSCHOOL, or SPELL name
	 * 
	 * @param aPC
	 * @param aComponent
	 * @return aBonus
	 */
	private static int getBonusForSpellPointCostComponent(
			final PlayerCharacter aPC, Spell sp, final String aComponent)
	{
		int aBonus = 0;
		for (SpellSchool school : sp.getSafeListFor(ListKey.SPELL_SCHOOL))
		{
			aBonus += (int) aPC.getTotalBonusTo("SPELLPOINTCOST", "SCHOOL."
					+ school.toString().toUpperCase() + ";" + aComponent.toUpperCase());
		}
		for (String subSchool : sp.getSafeListFor(ListKey.SPELL_SUBSCHOOL))
		{
			aBonus += (int) aPC.getTotalBonusTo("SPELLPOINTCOST", "SUBSCHOOL."
					+ subSchool.toUpperCase() + ";" + aComponent.toUpperCase());
		}
		aBonus += (int) aPC.getTotalBonusTo("SPELLPOINTCOST", "SPELL."
				+ sp.getKeyName() + ";" + aComponent.toUpperCase());
		return aBonus;
	}

	public static String getSPCostStrings(PlayerCharacter aPC, Spell sp)
	{
		Map<String, Integer> spCost = getSpellPointCostActualParts(sp);
		int totalSpellPoints = getSpellPointCostActual(aPC, sp);
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();

		int bonus = 0;
		int tempbonus = 0;
		tempbonus = getBonusForSpellPointCostComponent(aPC, sp, "TOTAL");
		if (spCost.size() == 0)
		{
			sb.append(totalSpellPoints + bonus);
			return sb.toString();
		}
		else if (spCost.size() == 1 && spCost.containsKey("TOTAL"))
		{
			sb.append(totalSpellPoints + bonus);
			return sb.toString();
		}

		// sb.append(totalSpellPoints);

		// Using a TreeSet so they are sorted no matter what order the data is
		// input
		// by the lst coder
		TreeSet<String> fields = new TreeSet<String>();
		fields.addAll(spCost.keySet());

		for (String aComponent : fields)
		{
			if (aComponent.equalsIgnoreCase("Range"))
			{
				bonus = getBonusForSpellPointCostComponent(aPC, sp, aComponent);

				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent) + bonus);
				sb2.append("/");
			}
			else if (aComponent.equalsIgnoreCase("Area of Effect"))
			{
				bonus = getBonusForSpellPointCostComponent(aPC, sp, aComponent);

				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent) + bonus);
				sb2.append("/");
			}
			else if (aComponent.equalsIgnoreCase("Duration"))
			{
				bonus = getBonusForSpellPointCostComponent(aPC, sp, aComponent);

				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent) + bonus);
				sb2.append("/");
			}
			else
			{
				bonus = getBonusForSpellPointCostComponent(aPC, sp, aComponent);

				sb3.append(aComponent);
				sb3.append(" ");
				sb3.append(spCost.get(aComponent) + bonus);
				sb3.append("/");
			}
			bonus = 0;
		}
		int total = totalSpellPoints + tempbonus;

		if (sb2.length() < 1)
		{
			sb.replace(sb.length() - 1, sb.length(), "");
		}
		sb2.replace(sb2.length() - 1, sb2.length(), "");
		sb.append(total);
		sb.append(" [");
		sb.append(sb3.toString());
		sb.append(sb2.toString());
		sb.append("]");
		return sb.toString();
	}

	public static Map<String, Integer> getSpellPointCostActualParts(Spell sp)
	{
		/*
		 * TODO Emulating the old form here until I understand how some items
		 * (e.g. TOTAL) are resolved in .MOD situations
		 */
		Map<String, Integer> spCost = new HashMap<String, Integer>();

		for (PointCost pc : sp.getSafeListFor(ListKey.SPELL_POINT_COST))
		{
			spCost.put(pc.getType(), pc.getCost());
		}
		return spCost;
	}

}
