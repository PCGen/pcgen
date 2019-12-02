/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.AllowUtilities;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitStat;
import pcgen.core.kit.KitTable;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

/**
 * {@code Kit}.
 */
public final class Kit extends PObject
{
	private int selectValue = -1;

	private boolean doLevelAbilitiesFlag = true;

	/**
	 * Returns the list of base stats that are set by the kit.
	 *
	 * @return List the List of stats to be set by the kit.  The stats are
	 * wrapped in a KitStat object.
	 */
	public List<KitStat> getStats()
	{
		return getSafeListFor(ListKey.STAT_LIST);
	}

	/**
	 * Set the Select value
	 * @param aValue The currently active select value to use for the kit.
	 */
	public void setSelectValue(final int aValue)
	{
		selectValue = aValue;
	}

	/**
	 * Returns true if we are doing level abilities for the Kit
	 * @return true if we are doing level abilities for the Kit
	 */
	public boolean doLevelAbilities()
	{
		return doLevelAbilitiesFlag;
	}

	/**
	 * Set whether we will do the level abilities for the Kit
	 * @param yesNo true means we are handling level abilities in the kit.
	 */
	public void setDoLevelAbilities(final boolean yesNo)
	{
		doLevelAbilitiesFlag = yesNo;
	}

	/**
	 * Add a stat, wrapped in a KitStat, that will be set by this kit.
	 *
	 * @param kitStat The stat-value pair to set.
	 */
	public void addStat(final KitStat kitStat)
	{
		if (kitStat != null)
		{
			addToListFor(ListKey.STAT_LIST, kitStat);
		}
	}

	/**
	 * Used to compare Kits.
	 *
	 * @param   other  Object
	 *
	 * @return  int
	 */
	@Override
	public int compareTo(final Object other)
	{
		final Kit oKit = (Kit) other;
		return getKeyName().compareToIgnoreCase(oKit.getKeyName());
	}

	/**
	 * The method that actually adds the various items in this Kit to the PC.
	 * Takes account of Kit Number
	 *
	 * @param  pc           The Player Character object that we will be applying
	 *                      the kit to.
	 * @param  thingsToAdd  The list of things that will be added by this kit
	 *                      wrapped in KitWrapper objects
	 */
	public void processKit(final PlayerCharacter pc, final Collection<BaseKit> thingsToAdd)
	{
		BigDecimal totalCostToBeCharged = getTotalCostToBeCharged(pc);
		if (totalCostToBeCharged != null)
		{
			pc.setGold(pc.getGold().subtract(totalCostToBeCharged));
		}

		for (KitStat kStat : getStats())
		{
			kStat.apply(pc);
		}

		for (BaseKit bk : thingsToAdd)
		{
			bk.apply(pc);
		}
		pc.setCalcEquipmentList();

		if (getSafe(ObjectKey.APPLY_MODE) == KitApply.PERMANENT)
		{
			pc.addKit(this);
		}
	}

	/**
	 * Gets the buy rate of the kit.
	 *
	 * @param	aPC The character used to evaluate expressions in relation to
	 * @return  String
	 */
	public int getBuyRate(PlayerCharacter aPC)
	{
		QualifiedObject<Formula> buy = get(ObjectKey.EQUIP_BUY);
		Formula f = (buy == null ? null : buy.getObject(aPC, this));
		int buyRate;
		if (f == null)
		{
			buyRate = SettingsHandler.getGearTab_BuyRate();
		}
		else
		{
			buyRate = f.resolve(aPC, "").intValue();
			if (buyRate == 100)
			{
				buyRate = SettingsHandler.getGearTab_BuyRate();
			}
		}
		return buyRate;
	}

	/**
	 * Gets the specified total cost of the kit. Note this is a base total cost 
	 * which would then be modified by the chosen gear purchase rate on the gear 
	 * tab. 
	 *
	 * @param	aPC The character used to evaluate expressions in relation to
	 * @return  total cost, or null if no total cost was specified.
	 */
	public BigDecimal getTotalCost(PlayerCharacter aPC)
	{
		QualifiedObject<Formula> buy = get(ObjectKey.KIT_TOTAL_COST);
		Formula f = (buy == null ? null : buy.getObject(aPC, this));
		BigDecimal totalCost = null;
		if (f != null)
		{
			totalCost = BigDecimal.valueOf(f.resolve(aPC, "").doubleValue());
		}
		return totalCost;
	}

	/**
	 * Gets the specified total cost of the kit modified by the user's chosen 
	 * gear purchase rate on the gear tab.
	 *
	 * @param	aPC The character used to evaluate expressions in relation to
	 * @return  Cost to be charged, or null if no total cost was specified.
	 */
	public BigDecimal getTotalCostToBeCharged(PlayerCharacter aPC)
	{
		BigDecimal theCost = null;
		BigDecimal fixedTotalCost = getTotalCost(aPC);
		if (fixedTotalCost != null)
		{
			fixedTotalCost = fixedTotalCost.setScale(3);
			BigDecimal buyRate = new BigDecimal(SettingsHandler.getGearTab_BuyRate());
			theCost = fixedTotalCost.multiply(buyRate).divide(new BigDecimal(100).setScale(3), RoundingMode.FLOOR);
		}

		return theCost;
	}

	/**
	 * Returns true if the kit is visible
	 *
	 * @param   aPC  if the kit visibility depends on the PC, this is the PC
	 *               that will be used to check the prerequisites.
	 *
	 * @return  Whether the kit is visible
	 */
	public boolean isVisible(PlayerCharacter aPC, View v)
	{
		Visibility kitVisible = getSafe(ObjectKey.VISIBILITY);
		if (kitVisible == Visibility.QUALIFY)
		{
			return qualifies(aPC, this);
		}
		else if (kitVisible.isVisibleTo(v))
		{
			return true;
		}

		return false;
	}

	/**
	 * Test applying the top level kit and record the choices made and any 
	 * warnings encountered. Note these changes are made on a copy of the 
	 * character.
	 * 
	 * @param aPC PlayerCharacter
	 * @param thingsToAdd List of kit actions to be taken.
	 * @param warnings List of issues to be reported to the user.
	 */
	public void testApplyKit(PlayerCharacter aPC, List<BaseKit> thingsToAdd, List<String> warnings)
	{
		testApplyKit(aPC, thingsToAdd, warnings, false);
	}

	/**
	 * Test applying the kit and record the choices made and any warnings 
	 * encountered. Note these changes are made on a copy of the character.
	 * 
	 * @param aPC PlayerCharacter
	 * @param thingsToAdd List of kit actions to be taken.
	 * @param warnings List of issues to be reported to the user.
	 * @param subkit Is this kit being added by a parent kit?
	 */
	public void testApplyKit(PlayerCharacter aPC, List<BaseKit> thingsToAdd, List<String> warnings, boolean subkit)
	{
		// Ensure a reset of random values from a prior run
		selectValue = -1;

		// We will create a copy of the PC since we may need to add classes and
		// levels to the PC that the user may choose not to apply.
		// NOTE: These methods need to be called in the correct order.
		PlayerCharacter tempPC = subkit ? aPC : aPC.clone();

		for (KitStat kStat : getStats())
		{
			kStat.testApply(this, tempPC, warnings);
		}

		for (BaseKit bk : getSafeListFor(ListKey.KIT_TASKS))
		{
			if (!PrereqHandler.passesAll(bk, tempPC, this))
			{
				continue;
			}
			if (selectValue != -1 && bk.isOptional() && !bk.isOption(tempPC, selectValue))
			{
				continue;
			}
			if (bk.testApply(this, tempPC, warnings))
			{
				thingsToAdd.add(bk);
			}
		}

		BigDecimal totalCostToBeCharged = getTotalCostToBeCharged(tempPC);
		if (totalCostToBeCharged != null)
		{
			BigDecimal pcGold = tempPC.getGold();
			if (pcGold.compareTo(BigDecimal.ZERO) >= 0 && pcGold.compareTo(totalCostToBeCharged) < 0)
			{
				warnings.add("Could not purchase kit. Not enough funds.");
			}
			else
			{
				tempPC.setGold(pcGold.subtract(totalCostToBeCharged));
			}
		}

	}

	/**
	 * Get the Kit info for this PC
	 * @param aPC the PC this kit is being applied to.
	 * @return the Kit info for this PC
	 */
	public String getInfo(PlayerCharacter aPC)
	{
		StringBuilder info = new StringBuilder(255);
		info.append("<html>");
		info.append("<b><font size=+1>");
		info.append(OutputNameFormatting.piString(this));
		info.append("</font></b><br>\n");

		String aString = getPreReqHTMLStrings(aPC);

		if (!aString.isEmpty())
		{
			info.append("  <b>Requirements</b>: ").append(aString);
		}

		List<BaseKit> sortedObjects = new ArrayList<>(getSafeListFor(ListKey.KIT_TASKS));
		sortedObjects.sort(Comparator.comparing(BaseKit::getObjectName));

		String lastObjectName = "";
		for (BaseKit bk : sortedObjects)
		{
			String objName = bk.getObjectName();
			if (!objName.equals(lastObjectName))
			{
				if (!lastObjectName.isEmpty())
				{
					info.append("; ");
				}
				info.append("  <b>").append(objName).append("</b>: ");
				lastObjectName = objName;
			}
			else
			{
				info.append(", ");
			}
			info.append(bk);
		}
		info.append("  <b>Source</b>: ")
			.append(SourceFormat.getFormattedString(this, Globals.getSourceDisplay(), true));
		info.append("</html>");
		//TODO ListKey.KIT_TASKS
		return info.toString();
	}

	private String getPreReqHTMLStrings(PlayerCharacter aPC)
	{
		String sb = PrerequisiteUtilities.preReqHTMLStringsForList(aPC, this, getPrerequisiteList(), false)
				+ AllowUtilities.getAllowInfo(aPC, this);
		return sb;
	}

	public static void applyKit(final Kit aKit, final PlayerCharacter aPC)
	{
		if (aKit == null)
		{
			return;
		}
		if (aKit.getSafe(ObjectKey.APPLY_MODE) == KitApply.PERMANENT && aPC.containsKit(aKit))
		{
			return;
		}

		final List<BaseKit> thingsToAdd = new ArrayList<>();
		final List<String> warnings = new ArrayList<>();
		aKit.testApplyKit(aPC, thingsToAdd, warnings);
		if (Logging.isLoggable(Logging.WARNING))
		{
			if (!warnings.isEmpty())
			{
				Logging.log(Logging.WARNING,
					"The following warnings were encountered when applying the kit " + aKit.getKeyName());
				for (String string : warnings)
				{
					Logging.log(Logging.WARNING, "  " + string);
				}
			}
		}
		aKit.processKit(aPC, thingsToAdd);
	}

	public KitTable getTable(String name)
	{
		return get(MapKey.KIT_TABLE, name);
	}

	public KitTable addTable(KitTable table)
	{
		return addToMapFor(MapKey.KIT_TABLE, table.getTableName(), table);
	}

	public String getDisplayType()
	{
		List<Type> trueTypeList = getTrueTypeList(true);
		return StringUtil.join(trueTypeList, ".");
	}

	public boolean isPermanent()
	{
		return getSafe(ObjectKey.APPLY_MODE) == KitApply.PERMANENT;
	}
}
