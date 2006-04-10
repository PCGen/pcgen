/*
 * WieldCategory.java
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on November 21, 2003, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.character;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * <code>WieldCategory.java</code>
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public final class WieldCategory
{
	private final Map switchMap = new HashMap();
	private final Map wcStepMap = new HashMap();

	/*
	 * WieldCategory contains the following:
	 * Hands: Minimum hands required to wield this category of weapon
	 * Finessable: Can this weapon be used with weapon finesse feat?
	 * Damage Multiplier: Multiplier to damage based on hands used
	 * PREVAR and SWITCH map: If meet PREVAR, then switch category
	 */
	private String name = "";
	private boolean finessBool;
	private int hands = 999;
	private int sizeDiff;

	/** A map storing an integer key for the number of hands being used and
	 * a float value which sets the damage multiplier.  Values in the table
	 * are wrapped in Integer/Float objects.
	 */
	private Map damageMultipliers = new HashMap();

	private static WieldCategory DEFAULT_TOOSMALL = null;
	private static WieldCategory DEFAULT_LIGHT = null;
	private static WieldCategory DEFAULT_ONEHANDED = null;
	private static WieldCategory DEFAULT_TWOHANDED = null;
	private static WieldCategory DEFAULT_TOOLARGE = null;
	private static WieldCategory DEFAULT_UNUSABLE = null;

	/**
	 * New constructor
	 * @param aName
	 */
	public WieldCategory(final String aName)
	{
		name = aName;
	}

	/**
	 * Set whether a weapon be used with weapon finesse Feat?
	 * @param aBool
	 */
	public void setFinessable(final boolean aBool)
	{
		finessBool = aBool;
	}

	/**
	 * Can the weapon be used with weapon finesse Feat.
	 * @return boolean
	 */
	public boolean isFinessable()
	{
		return finessBool;
	}

	/**
	 * Minumum hands required to wield this category of weapon
	 * @param x
	 */
	public void setHands(final int x)
	{
		hands = x;
	}

	/**
	 * Get the minumum hands required to wield this category of weapon.
	 * @return hands
	 */
	public int getHands()
	{
		return hands;
	}

	/**
	 * Get the name of wield category.
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get object size, equip size + size diff.
	 * @param eq
	 * @return object size
	 */
	public int getObjectSizeInt(final Equipment eq)
	{
		final int eqSize = eq.sizeInt();

		return eqSize + sizeDiff;
	}

	/**
	 * Number of size categories object size is different than Equip size
	 * @param x
	 **/
	public void setSizeDiff(final int x)
	{
		sizeDiff = x;
	}

	/**
	 * Map of Steps up or down the wield category chain
	 * @param aInt
	 * @param aVal
	 **/
	public void setWCStep(final int aInt, final String aVal)
	{
		final String aKey = (new Integer(aInt)).toString();
		wcStepMap.put(aKey, aVal);
	}

	/**
	 * Get the WieldCategory adjusted for the size difference between the weapon
	 * and the PC.  This uses the 3.5 equipment sizes.
	 *
	 * @param aPC Player character to get the weild category for.
	 * @param eq Equipment to get the weild category for.
	 * @return The ajusted WieldCategory
	 */
	public WieldCategory adjustForSize(final PlayerCharacter aPC,
									   final Equipment eq)
	{
		if (aPC == null || eq == null || !eq.hasWield())
		{
			return this;
		}

		try
		{
			// Check if we have a bonus that changes the weapons effective size
			// for wield purposes.
			final String oldEqSize = eq.getSize();
			if (aPC.sizeInt() != eq.sizeInt())
			{
				int aBump = 0;
				aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", eq.getWield());
				aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", "ALL");
				if (aBump != 0)
				{
					final int newSizeInt = eq.sizeInt() + aBump;
					final SizeAdjustment sadj = SettingsHandler.getGame().
						getSizeAdjustmentAtIndex(newSizeInt);
					eq.setSize(sadj.getAbbreviation(), true);
				}
			}
			final PrerequisiteParserInterface parser = PreParserFactory.
				getInstance().getParser("VAR");
			for (Iterator pc = switchMap.keySet().iterator(); pc.hasNext(); )
			{
				String aKey = (String) pc.next();

				boolean invertResult = false;
				if (aKey.startsWith("!"))
				{
					invertResult = true;
					aKey = aKey.substring(1);
				}
				final String aType = aKey.substring(3, aKey.indexOf(":"));
				final String preVar = aKey.substring(aKey.indexOf(":") + 1);

				try
				{
					final Prerequisite prereq = parser.parse(aType, preVar,
						invertResult, false);
					if (PrereqHandler.passes(prereq, eq, aPC))
					{
						final String mappedCat = (String) switchMap.get(aKey);
						WieldCategory wCat = SettingsHandler.getGame().
							getWieldCategory(mappedCat);
						if (wCat != null)
						{
							return wCat;
						}
						return this;
					}
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
			eq.setSize(oldEqSize, true);
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint(ple.getMessage(), ple);
		}

		return this;
	}

	/**
	 * Wield Category step is used to figure a bonus to WIELDCATEGORY
	 * Thus it should always return the best possible wield category
	 * and never a "bad" wield category
	 * @param aBump
	 * @param wieldString
	 * @param aSizeDiff
	 * @param aHands
	 * @return weild category step
	 **/
	public WieldCategory getWieldCategoryStep(int aBump)
	{
		final String aKey = Integer.toString(aBump);
		final String newWC = (String) wcStepMap.get(aKey);

		if (newWC != null)
		{
			return findByName(newWC);
		}

		return this;
	}

	/**
	 * Map of PREVAR and wield category to switch to
	 * @param aKey
	 * @param aVal
	 */
	public void addSwitchMap(final String aKey, final String aVal)
	{
		switchMap.put(aKey, aVal);
	}

	/**
	 * Add a new Damage Mult entry.
	 * @param numHands Number of hands used to wield the weapon
	 * @param multiplier Amount to multiply STR damage by
	 */
	public void addDamageMult(int numHands, float multiplier)
	{
		damageMultipliers.put(new Integer(numHands), new Float(multiplier));
	}

	/**
	 * Returns the STR damage multiplier for this wield category.
	 *
	 * @param hands number of hands the weapon is wielded with
	 * @return float the multiplier
	 */
	public float getDamageMult(int hands)
	{
		Float ret = (Float) damageMultipliers.get(new Integer(hands));
		if (ret == null)
		{
			return 0.0f;
		}
		return ret.floatValue();
	}

	/**
	 * Finds a WieldCategory object by name.  If a Game mode does not define
	 * wield categories a default set will be used.
	 * @param aCategory The wield category name to find
	 * @return The WieldCategory matching the name or the "Unusable" category.
	 */
	public static WieldCategory findByName(final String aCategory)
	{
		WieldCategory wCat = SettingsHandler.getGame().getWieldCategory(
			aCategory);
		if (wCat == null)
		{
			// Handle Default WieldCategories
			if ("Light".equals(aCategory))
			{
				if (DEFAULT_LIGHT == null)
				{
					DEFAULT_LIGHT = new WieldCategory("Light");
					DEFAULT_LIGHT.setHands(1);
					DEFAULT_LIGHT.setFinessable(true);
					DEFAULT_LIGHT.addDamageMult(1, 1.0f);
					DEFAULT_LIGHT.addDamageMult(2, 1.0f);
					DEFAULT_LIGHT.addSwitchMap(
						"PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-1", "TooSmall");
					DEFAULT_LIGHT.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1", "OneHanded");
					DEFAULT_LIGHT.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+2", "TwoHanded");
					DEFAULT_LIGHT.addSwitchMap(
						"PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+3", "TooLarge");
					DEFAULT_LIGHT.setWCStep(1, "OneHanded");
					DEFAULT_LIGHT.setWCStep(2, "TwoHanded");
				}
				wCat = DEFAULT_LIGHT;
			}
			else if ("OneHanded".equals(aCategory))
			{
				if (DEFAULT_ONEHANDED == null)
				{
					DEFAULT_ONEHANDED = new WieldCategory("OneHanded");
					DEFAULT_ONEHANDED.setHands(1);
					DEFAULT_ONEHANDED.setFinessable(false);
					DEFAULT_ONEHANDED.addDamageMult(1, 1.0f);
					DEFAULT_ONEHANDED.addDamageMult(2, 1.5f);
					DEFAULT_ONEHANDED.addSwitchMap(
						"PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-2", "TooSmall");
					DEFAULT_ONEHANDED.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1", "Light");
					DEFAULT_ONEHANDED.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1", "TwoHanded");
					DEFAULT_ONEHANDED.addSwitchMap(
						"PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+2", "TooLarge");
					DEFAULT_ONEHANDED.setWCStep( -1, "Light");
					DEFAULT_ONEHANDED.setWCStep(1, "TwoHanded");
				}
				wCat = DEFAULT_ONEHANDED;
			}
			else if ("TwoHanded".equals(aCategory))
			{
				if (DEFAULT_TWOHANDED == null)
				{
					DEFAULT_TWOHANDED = new WieldCategory("TwoHanded");
					DEFAULT_TWOHANDED.setFinessable(false);
					DEFAULT_TWOHANDED.setHands(2);
					DEFAULT_TWOHANDED.addDamageMult(2, 1.5f);
					DEFAULT_TWOHANDED.addSwitchMap(
						"PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-3", "TooSmall");
					DEFAULT_TWOHANDED.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2", "Light");
					DEFAULT_TWOHANDED.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1", "OneHanded");
					DEFAULT_TWOHANDED.addSwitchMap(
						"PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1", "TooLarge");
					DEFAULT_TWOHANDED.setWCStep( -2, "Light");
					DEFAULT_TWOHANDED.setWCStep( -1, "OneHanded");
				}
				wCat = DEFAULT_TWOHANDED;
			}
			else if ("TooSmall".equals(aCategory))
			{
				if (DEFAULT_TOOSMALL == null)
				{
					DEFAULT_TOOSMALL = new WieldCategory("TooSmall");
					DEFAULT_TOOSMALL.setFinessable(false);
					DEFAULT_TOOSMALL.setHands(2);
					DEFAULT_TOOSMALL.addDamageMult(2, 1.5f);
					DEFAULT_TOOSMALL.addSwitchMap(
						"PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-3", "TooSmall");
					DEFAULT_TOOSMALL.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2", "Light");
					DEFAULT_TOOSMALL.addSwitchMap(
						"PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1", "OneHanded");
					DEFAULT_TOOSMALL.addSwitchMap(
						"PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1", "TooLarge");
					DEFAULT_TOOSMALL.setWCStep( -2, "Light");
					DEFAULT_TOOSMALL.setWCStep( -1, "OneHanded");
				}
				wCat = DEFAULT_TOOSMALL;
			}
			else if ("TooLarge".equals(aCategory))
			{
				if (DEFAULT_TOOLARGE == null)
				{
					DEFAULT_TOOLARGE = new WieldCategory("TooLarge");
					DEFAULT_TOOLARGE.setFinessable(false);
					DEFAULT_TOOLARGE.setHands(999);
					DEFAULT_TOOLARGE.setWCStep( -3, "Light");
					DEFAULT_TOOLARGE.setWCStep( -2, "OneHanded");
					DEFAULT_TOOLARGE.setWCStep( -1, "TwoHanded");
					DEFAULT_TOOLARGE.setWCStep(0, "TwoHanded");
				}
				wCat = DEFAULT_TOOLARGE;
			}
			else
			{
				if (DEFAULT_UNUSABLE == null)
				{
					DEFAULT_UNUSABLE = new WieldCategory("Unusable");
					DEFAULT_UNUSABLE.setHands(999);
				}
				wCat = DEFAULT_UNUSABLE;
			}
		}
		return wCat;
	}
}
