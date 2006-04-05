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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>Kit</code>.
 *
 * @author   Greg Bingleman <byngl@hotmail.com>
 * @version  $Revision$
 */
public final class Kit extends PObject implements Comparable
{
	private static final int VISIBLE_NO        = 0;
	private static final int VISIBLE_YES       = 1;
	private static final int VISIBLE_QUALIFIED = 2;

	/** APPLY_PERMANENT = 0 */
	public static final int APPLY_PERMANENT = 0;
	/** APPLY_INSTANT = 1 */
	public static final int APPLY_INSTANT   = 1;

	private int       kitVisible     = VISIBLE_YES;
	private final List theObjects = new ArrayList();

	private final HashMap lookupTables = new HashMap();

	private List      statList       = new ArrayList();
	private String    buyRate        = null;
	private String    region         = Constants.s_NONE;
	private int applyMode = APPLY_PERMANENT;

	private int selectValue = -1;

	private boolean doLevelAbilitiesFlag = true;

	/**
	 * Constructor for Kit
	 *
	 * @param  argRegion  String
	 */
	public Kit(final String argRegion)
	{
		super();

		if ((argRegion != null) && (argRegion.length() > 0))
		{
			final StringTokenizer aTok = new StringTokenizer(argRegion, "\t", false);
			region = aTok.nextToken();

			if (!region.equalsIgnoreCase(Constants.s_NONE))
			{
				// Add a real prereq for the REGION: tag
				List prereqList = getPreReqList();
				if (prereqList == null)
				{
					prereqList = new ArrayList();
				}
				Prerequisite r = null;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					r = factory.parse("PREREGION:" + region);
				}
				catch (PersistenceLayerException ple)
				{
					// TODO Deal with this Exception?
				}
				if (r != null && !prereqList.contains(r))
				{
					addPreReq(r);
				}
			}

			if (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();

				if (aString.startsWith("LANGAUTO:"))
				{
					try {
						PObjectLoader.parseTag(this, aString);
					} catch (PersistenceLayerException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Adds an object to be applied as part of the kit.  Objects are applied
	 * by the kit in the order they are added.
	 *
	 * @param anObject A BaseKit object to apply as part of the kit
	 */
	public void addObject(final BaseKit anObject)
	{
		theObjects.add(anObject);
	}

	/**
	 * Sets the buy rate of the kit.
	 *
	 * @param  argRate the string-based representation of a percentage of the
	 * cost equipment will be purchased for
	 */
	public void setBuyRate(final String argRate)
	{
		buyRate = argRate;
	}

	/**
	 * Returns the list of base stats that are set by the kit.
	 *
	 * @return List the List of stats to be set by the kit.  The stats are
	 * wrapped in a KitStat object.
	 */
	public List getStats()
	{
		return statList;
	}

	/**
	 * Sets the name of the kit.
	 *
	 * @param  argName  the kit name
	 */
	public void setName(final String argName)
	{
		if (!argName.endsWith(".MOD"))
		{
			name    = argName;
			keyName = region + "|" + argName;
		}
	}

	/**
	 * Get the method by which the kit is applied.
	 *
	 * @return A string representation of the mode
	 */
	public int getApplyMode()
	{
		return applyMode;
	}

	/**
	 * Set the mode this kit should be applied with.  PERMANENT means the kit
	 * is added to the list of kits applied to the PC and will not be allowed
	 * to be applied again.  INSTANT means it is not saved with the character
	 * and can be applied as many times as desirable.
	 *
	 * @param aMode The mode (PERMANENT, INSTANT)
	 */
	public void setApplyMode(final String aMode)
	{
		if ("PERMANENT".equals(aMode))
		{
			applyMode = APPLY_PERMANENT;
		}
		else if ("INSTANT".equals(aMode))
		{
			applyMode = APPLY_INSTANT;
		}
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
				statList = new ArrayList();
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
	public int compareTo(final Object other)
	{
		// this should throw a ClassCastException for non-Kit, like the Comparable
		// interface calls for
		if (!(other instanceof Kit))
		{
			throw new ClassCastException();
		}
		final Kit oKit   = (Kit) other;
		int       retVal = region.compareToIgnoreCase(oKit.getRegion());

		if (retVal == 0)
		{
			retVal = getName().compareToIgnoreCase(oKit.getName());
		}

		return retVal;
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
	public void processKit(final PlayerCharacter pc, final List thingsToAdd)
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
	public void processKit(
		final PlayerCharacter pc,
		final List            thingsToAdd,
		final int             kitNo)
	{
//		if (characterName != null)
//		{
//			pc.setName(characterName);
//		}

		for (Iterator s = statList.iterator(); s.hasNext(); )
		{
			KitStat kStat = (KitStat)s.next();
			kStat.apply(pc);
		}

		for (Iterator e = thingsToAdd.iterator(); e.hasNext();)
		{
			Object obj = e.next();
			BaseKit bk = (BaseKit)obj;
			bk.apply(pc);
		}

		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoInventory());
		pane.refresh();

		if (applyMode == APPLY_PERMANENT)
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
		int aBuyRate = SettingsHandler.getGearTab_BuyRate();
		final String purchaseFormula = buyRate;

		if ((purchaseFormula != null) && (purchaseFormula.length() != 0))
		{
			final String costFormula = getCostFromFormula(aPC, purchaseFormula);

			if (costFormula != null)
			{
				aBuyRate = aPC.getVariableValue(costFormula, "").intValue();
			}
		}
		return aBuyRate;
	}

	/**
	 * Processes a formula string which may have prerequisites to return just
	 * the formula.  If there are no prerequisites, or the prerequisites that
	 * are there pass, the Formula will be passed back.  If there is no formula,
	 * or the prerequisites fail, null will be passed back. Takes a string
	 * representing a formula to calculate the buyrate of equipment in this kit.
	 *
	 * @param   pc       A Player Character object, will be used to test
	 *                   prerequisites against and look up the value of
	 *                   variables i.e. the PC we will be applying the kit to.
	 * @param   formula  A string representing a formula that will be used to
	 *                   calculate the buy rate of equipment in this kit. The
	 *                   formula may have prerequisites enclosed in [] attached
	 *                   to the end.
	 *
	 * @return  The cost formula if it passed any prerequisites that were
	 *          attached to it. null otherwise.
	 */
	private String getCostFromFormula(final PlayerCharacter pc, final String formula)
	{
		final StringTokenizer aTok        = new StringTokenizer(formula, "[]", true);
		String                costFormula = null;

		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();

			if ("]".equals(tok))
			{
				costFormula = null;
			}
			else
			{
				if (costFormula == null)
				{
					costFormula = tok;
				}
				else
				{
					final List al = new ArrayList();
					al.add(tok);

					if (!PrereqHandler.passesAll(al, pc, this))
					{
						costFormula = null;

						continue;
					}

					break;
				}
			}
		}

		return costFormula;
	}

	/**
	 * Most things (Skills, Equipment, Abilities, Spells, Weapon Profs) that can be
	 * added with Kits take prerequisites.  They will only be added if the pc we
	 * are adding the kit to meets the prerequisites. This routine performs the
	 * checks.  It there are no checks associated with this item, or the checks
	 * pass, the itemname (with prerequisites stripped) is passed back.  If the
	 * checks fail, the method returns null.
	 *
	 * @param   aString  "Item name[PRE1|PRE2|...|PREn]"
	 * @param   pc       The pc that will be used for the checks (i.e. the pc
	 *                   the kit will be added to).
	 *
	 * @return  "Item name" or null
	 */
/*
	private String itemPassesPrereqs(String aString, final PlayerCharacter pc)
	{
		final int idxStart = aString.indexOf('[');

		if ((idxStart < 0) || !aString.endsWith("]"))
		{
			return aString;
		}

		final String itemName = aString.substring(0, idxStart);
		aString = aString.substring(idxStart + 1, aString.length() - 1);

		final List prereqList = CoreUtility.split(aString, '|');

		if (PrereqHandler.passesAll(prereqList, pc, this))
		{
			return itemName;
		}

		return null;
	}
*/

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
		if (kitVisible == VISIBLE_QUALIFIED)
		{
			final List prereqList = getPreReqList();

			if (PrereqHandler.passesAll(prereqList, aPC, this))
			{
				return true;
			}

			return false;
		}
		else if (kitVisible == VISIBLE_YES)
		{
			return true;
		}

		return false;
	}

	/**
	 * Set how visible this Kit is
	 *
	 * @param  aString beginning with "Y" for visible, "Q" for only
	 * if pc qualifies.  Any other letter makes the kit invisible.
	 */
	public final void setVisible(final String aString)
	{
		if ((aString.length() > 0) && (aString.charAt(0) == 'Y'))
		{
			kitVisible = VISIBLE_YES;
		}
		else if ((aString.length() > 0) && (aString.charAt(0) == 'Q'))
		{
			kitVisible = VISIBLE_QUALIFIED;
		}
		else
		{
			kitVisible = VISIBLE_NO;
		}
	}

	/**
	 *
	 * @param aPC PlayerCharacter
	 * @param thingsToAdd List
	 * @param warnings List
	 */
	public void testApplyKit(PlayerCharacter aPC, List thingsToAdd, List warnings)
	{
		// We will create a copy of the PC since we may need to add classes and
		// levels to the PC that the user may choose not to apply.
		// NOTE: These methods need to be called in the correct order.
		PlayerCharacter tempPC = (PlayerCharacter)aPC.clone();
		for (Iterator s = statList.iterator(); s.hasNext(); )
		{
			KitStat kStat = (KitStat) s.next();
			kStat.testApply(this, tempPC, warnings);
		}

		for (Iterator i = theObjects.iterator(); i.hasNext(); )
		{
			BaseKit bk = (BaseKit)((BaseKit)i.next()).clone();
			if (!PrereqHandler.passesAll(bk.getPrereqs(), tempPC, this))
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

	private class ObjectTypeComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			BaseKit bk1 = (BaseKit)o1;
			BaseKit bk2 = (BaseKit)o2;
			String name1 = bk1.getObjectName();
			String name2 = bk2.getObjectName();
			return name1.compareTo(name2);
		}

		public boolean equals(Object obj)
		{
			return compare(this, obj) == 0;
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
		info.append("<b>").append(getName()).append("</b> - ");

		String aString = getPreReqHTMLStrings(aPC);

		if (aString.length() != 0)
		{
			info.append("  <b>Requirements</b>: ").append(aString);
		}

		List sortedObjects = new ArrayList();
		sortedObjects.addAll(theObjects);
		Collections.sort(sortedObjects, new ObjectTypeComparator());

		String lastObjectName = "";
		for (Iterator i = sortedObjects.iterator(); i.hasNext(); )
		{
			BaseKit bk = (BaseKit)i.next();
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
		info.append("  <b>Source</b>: ").append(getSource());
		info.append("</html>");
		return info.toString();
	}

	private String getPreReqHTMLStrings(PlayerCharacter aPC)
	{
		return PrerequisiteUtilities.preReqHTMLStringsForList(aPC, this, getPreReqList(), false);
	}

	/**
	 * Add new lookup table
	 * @param tableName
	 */
	public void addLookupTable(final String tableName)
	{
		lookupTables.put(tableName, new LookupTable());
	}

	/**
	 * Add a new lookup value to the table
	 * @param tableName
	 * @param lookupValue
	 * @param lowVal
	 * @param highVal
	 */
	public void addLookupValue(final String tableName, final String lookupValue, final String lowVal, final String highVal)
	{
		LookupTable table = (LookupTable)lookupTables.get(tableName);
		table.addEntry(lookupValue, lowVal, highVal);
	}

	/**
	 * Get a value out of the table
	 * @param pc
	 * @param tableName
	 * @param value
	 * @return A value out of the table
	 */
	public String getTableValue(PlayerCharacter pc, final String tableName, String value)
	{
		LookupTable t = (LookupTable)lookupTables.get(tableName);
		if (t == null)
		{
			return "";
		}
		int val = pc.getVariableValue(value, Math.random()+"").intValue();
		return t.getEntry(pc, val);
	}

	/**
	 * Perform a lookup
	 * @param aPC
	 * @param aValue
	 * @return result
	 */
	public String lookup(PlayerCharacter aPC, String aValue)
	{
		int commaInd = aValue.indexOf(",");
		String tableName = aValue.substring(0, commaInd);
		String value = aValue.substring(commaInd+1,aValue.length());
		String result = getTableValue(aPC, tableName, value);
		return result;
	}

	class LookupTable
	{
		ArrayList values = new ArrayList();

		/**
		 * Constructor
		 */
		public LookupTable()
		{
			// Empty Constructor
		}

		/**
		 * Add an entry to the table
		 * @param value
		 * @param lowVal
		 * @param highVal
		 */
		public void addEntry(final String value, final String lowVal, final String highVal)
		{
			values.add(new TableEntry(value, lowVal, highVal));
		}

		/**
		 * Get an entry from the table
		 * @param pc
		 * @param value
		 * @return entry
		 */
		public String getEntry(PlayerCharacter pc, int value)
		{
			for (Iterator i = values.iterator(); i.hasNext(); )
			{
				TableEntry e = (TableEntry)i.next();
				if (e.isIn(pc, value))
				{
					return e.getValue();
				}
			}
			return "";
		}

		class TableEntry
		{
			private String value = "";
			private String lowValue = "" + Integer.MIN_VALUE;
			private String highValue = "" + Integer.MAX_VALUE;

			/**
			 * Constructor
			 * @param val the value for this table entry
			 * @param lowVal the low value of for this table entry
			 * @param highVal the high value for this table entry
			 */
			public TableEntry(String val, String lowVal, String highVal)
			{
				value = val;
				lowValue = lowVal;
				highValue = highVal;
			}

			/**
			 * True if value falls within a range
			 * @param pc the PC this Kit is being applied to
			 * @param inValue the value to test.
			 * @return True if value falls within a range
			 */
			public boolean isIn(PlayerCharacter pc, int inValue)
			{
				int lv = pc.getVariableValue(lowValue, "").intValue();
				int hv = pc.getVariableValue(highValue, "").intValue();
				if (inValue >= lv && inValue <= hv)
					return true;
				return false;
			}

			/**
			 * Get the value
			 * @return value
			 */
			public String getValue()
			{
				return value;
			}
		}
	}
}
