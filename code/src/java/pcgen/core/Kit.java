/*
 * Kit.java
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
 *
 * Created on September 23, 2002, 1:49 PM
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitStat;
import pcgen.core.kit.KitTable;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>Kit</code>.
 *
 * @author   Greg Bingleman <byngl@hotmail.com>
 * @version  $Revision$
 */
public final class Kit extends PObject implements Comparable<Object>
{
	private List<KitStat> statList = new ArrayList<KitStat>();
	private Map<String, KitTable> tableMap = new HashMap<String, KitTable>();
	private String region = Constants.s_NONE;

	private int selectValue = -1;

	private boolean doLevelAbilitiesFlag = true;

	public Kit()
	{
		//
	}

	/**
	 * Returns the list of base stats that are set by the kit.
	 *
	 * @return List the List of stats to be set by the kit.  The stats are
	 * wrapped in a KitStat object.
	 */
	public List<KitStat> getStats()
	{
		return statList;
	}

	/**
	 * Returns the region of the kit.
	 *
	 * @return  String
	 */
	public String getRegion()
	{
		return region;
	}

	/**
	 * Sets the sell rate of the kit.
	 *
	 * @param  argRate  String
	 */
	public void setSellRate(final String argRate)
	{
		// Do Nothing at this stage
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
			if (statList == null)
			{
				statList = new ArrayList<KitStat>();
			}
			statList.add(kitStat);
		}
	}

	/**
	 * Used to compare Kits.
	 *
	 * @param   other  Object
	 *
	 * @return  int
	 *
	 * @see     java.lang.Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(final Object other)
	{
		final Kit oKit = (Kit) other;
		return getKeyName().compareToIgnoreCase(oKit.getKeyName());
	}

	/**
	 * The method that actually adds the various items in this Kit to the PC.
	 * Does not take account of Kit Number.
	 *
	 * @param  pc           The Player Character object that we will be applying
	 *                      the kit to.
	 * @param  thingsToAdd  The list of things that will be added by this kit
	 *                      wrapped in KitWrapper objects
	 */
	public void processKit(final PlayerCharacter pc,
		final List<BaseKit> thingsToAdd)
	{
		processKit(pc, thingsToAdd, -1);
	}

	/**
	 * The method that actually adds the various items in this Kit to the PC.
	 * Takes account of Kit Number
	 *
	 * @param  pc           The Player Character object that we will be applying
	 *                      the kit to.
	 * @param  thingsToAdd  The list of things that will be added by this kit
	 *                      wrapped in KitWrapper objects
	 * @param  kitNo        An integer that will be used to set the kit number
	 *                      in items of equipment added by this kit
	 */
	public void processKit(final PlayerCharacter pc,
		final List<BaseKit> thingsToAdd, final int kitNo)
	{
		for (KitStat kStat : statList)
		{
			kStat.apply(pc);
		}

		for (BaseKit bk : thingsToAdd)
		{
			bk.apply(pc);
		}

		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		if (pane != null)
		{
			pane.setPaneForUpdate(pane.infoInventory());
			pane.refresh();
		}

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
		Formula f = buy.getObject(aPC);
		int buyRate;
		if (f == null)
		{
			buyRate = SettingsHandler.getGearTab_BuyRate();
		}
		else
		{
			buyRate = f.resolve(aPC, "").intValue();
		}
		return buyRate;
	}

	/**
	 * Returns true if the kit is visible
	 *
	 * @param   aPC  if the kit visibility depends on the PC, this is the PC
	 *               that will be used to check the prerequisites.
	 *
	 * @return  Whether the kit is visible
	 */
	public final boolean isVisible(PlayerCharacter aPC)
	{
		Visibility kitVisible = getSafe(ObjectKey.VISIBILITY);
		if (kitVisible == Visibility.QUALIFY)
		{
			final List<Prerequisite> prereqList = getPrerequisiteList();

			if (PrereqHandler.passesAll(prereqList, aPC, this))
			{
				return true;
			}

			return false;
		}
		else if (kitVisible == Visibility.DEFAULT)
		{
			return true;
		}

		return false;
	}

	/**
	 *
	 * @param aPC PlayerCharacter
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void testApplyKit(PlayerCharacter aPC, List<BaseKit> thingsToAdd,
		List<String> warnings)
	{
		// We will create a copy of the PC since we may need to add classes and
		// levels to the PC that the user may choose not to apply.
		// NOTE: These methods need to be called in the correct order.
		PlayerCharacter tempPC = (PlayerCharacter) aPC.clone();
		for (KitStat kStat : statList)
		{
			kStat.testApply(this, tempPC, warnings);
		}

		for (BaseKit bk : getSafeListFor(ListKey.KIT_TASKS))
		{
			if (!PrereqHandler
				.passesAll(bk.getPrerequisiteList(), tempPC, this))
			{
				continue;
			}
			if (selectValue != -1 && !bk.isOption(tempPC, selectValue))
			{
				continue;
			}
			if (bk.testApply(this, tempPC, warnings))
			{
				thingsToAdd.add(bk);
			}
		}
	}

	private class ObjectTypeComparator implements Comparator<BaseKit>
	{
		public int compare(BaseKit bk1, BaseKit bk2)
		{
			String name1 = bk1.getObjectName();
			String name2 = bk2.getObjectName();
			return name1.compareTo(name2);
		}
	}

	/**
	 * Get the Kit info for this PC
	 * @param aPC the PC this kit is being applied to.
	 * @return the Kit info for this PC
	 */
	public String getInfo(PlayerCharacter aPC)
	{
		StringBuffer info = new StringBuffer(255);
		info.append("<html>");
		info.append("<b><font size=+1>");
		info.append(OutputNameFormatting.piString(this, false));
		info.append("</font></b><br/>\n");

		String aString = getPreReqHTMLStrings(aPC);

		if (aString.length() != 0)
		{
			info.append("  <b>Requirements</b>: ").append(aString);
		}

		List<BaseKit> sortedObjects = new ArrayList<BaseKit>();
		sortedObjects.addAll(getSafeListFor(ListKey.KIT_TASKS));
		Collections.sort(sortedObjects, new ObjectTypeComparator());

		String lastObjectName = "";
		for (BaseKit bk : sortedObjects)
		{
			String objName = bk.getObjectName();
			if (!objName.equals(lastObjectName))
			{
				if (!"".equals(lastObjectName))
				{
					info.append("; ");
				}
				info.append("  <b>" + objName + "</b>: ");
				lastObjectName = objName;
			}
			else
			{
				info.append(", ");
			}
			info.append(bk.toString());
		}
		info.append("  <b>Source</b>: ").append(getDefaultSourceString());
		info.append("</html>");
		//TODO ListKey.KIT_TASKS
		return info.toString();
	}

	private String getPreReqHTMLStrings(PlayerCharacter aPC)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, this,
			getPrerequisiteList(), false);
	}

	public static void applyKit(final Kit aKit, final PlayerCharacter aPC)
	{
		if (aKit == null)
		{
			return;
		}
		if (aKit.getSafe(ObjectKey.APPLY_MODE) == KitApply.PERMANENT
			&& aPC.getKitInfo().contains(aKit))
		{
			return;
		}

		final List<BaseKit> thingsToAdd = new ArrayList<BaseKit>();
		final List<String> warnings = new ArrayList<String>();
		aKit.testApplyKit(aPC, thingsToAdd, warnings);
		if (warnings.size() != 0)
		{
			Logging.log(Logging.WARNING,
				"The following warnings were encountered when applying the kit "
					+ aKit.getKeyName());
			for (String string : warnings)
			{
				Logging.log(Logging.WARNING, "  " + string);
			}
		}
		aKit.processKit(aPC, thingsToAdd, 0);
	}

	public KitTable getTable(String name)
	{
		return tableMap.get(name);
	}

	public KitTable addTable(KitTable table)
	{
		return tableMap.put(table.getTableName(), table);
	}
}
