/*
 * Equipment.java
 * Copyright 2001 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.bonus.BonusObj;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.IntegerKey;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.StringKey;
import pcgen.io.FileAccess;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.JEPResourceChecker;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Load;

/**
 * <code>Equipment</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net> created December
 *         27
 * @author 2001
 * @version $Revision$
 */
public final class Equipment extends PObject implements Serializable,
		EquipmentCollection, Comparable<Object>, VariableContainer {
	private static final long serialVersionUID = 1;

	private static final String EQMOD_WEIGHT = "_WEIGHTADD";

	private static final String EQMOD_DAMAGE = "_DAMAGE";

	/** The item is held in neither hand */
	public static final int EQUIPPED_NEITHER = 0;

	/** The item is held in neither hand - String */
	public static final String EQUIPPED_NEITHER_STR = PropertyFactory
			.getString("EquipLocation.Neither");

	/** The item is held in the primary hand */
	public static final int EQUIPPED_PRIMARY = 1;

	/** The item is held in the primary hand - String */
	public static final String EQUIPPED_PRIMARY_STR = PropertyFactory
			.getString("EquipLocation.Primary");

	/** The item is held in the secondary hand */
	public static final int EQUIPPED_SECONDARY = 2;

	/** The item is held in the secondary hand - String */
	public static final String EQUIPPED_SECONDARY_STR = PropertyFactory
			.getString("EquipLocation.Secondary");

	/** The item is held in both hands */
	public static final int EQUIPPED_BOTH = 3;

	/** The item is held in both hands - String */
	public static final String EQUIPPED_BOTH_STR = PropertyFactory
			.getString("EquipLocation.Both");

	/** The item is either a double weapon or one of a pair of weapons */
	public static final int EQUIPPED_TWO_HANDS = 4;

	/** The item is either a double weapon or one of a pair of weapons - String */
	public static final String EQUIPPED_TWO_HANDS_STR = PropertyFactory
			.getString("EquipLocation.TwoHands");

	/** The item is held in neither hand and equipped for a temporary bonus */
	public static final int EQUIPPED_TEMPBONUS = 5;

	/**
	 * The item is held in neither hand and equipped for a temporary bonus -
	 * String
	 */
	public static final String EQUIPPED_TEMPBONUS_STR = PropertyFactory
			.getString("EquipLocation.TempBonus");

	/** The item is carried but not equipped */
	public static final int CARRIED_NEITHER = 6;

	/** The item is carried but not equipped - String */
	public static final String CARRIED_NEITHER_STR = PropertyFactory
			.getString("EquipLocation.Carried");

	/** The item is contained by another item */
	public static final int CONTAINED = 7;

	/** The item is contained by another item - String */
	public static final String CONTAINED_STR = PropertyFactory
			.getString("EquipLocation.Contained");

	/** The item is not carried */
	public static final int NOT_CARRIED = 8;

	/** The item is not carried - String */
	public static final String NOT_CARRIED_STR = PropertyFactory
			.getString("EquipLocation.NotCarried");

	// These are now initialized in the static{} initializer
	private static final String[] locationStringList = new String[9];

	private static final SortedSet<String> s_equipmentTypes = new TreeSet<String>();

	static {
		locationStringList[EQUIPPED_NEITHER] = EQUIPPED_NEITHER_STR;
		locationStringList[EQUIPPED_PRIMARY] = EQUIPPED_PRIMARY_STR;
		locationStringList[EQUIPPED_SECONDARY] = EQUIPPED_SECONDARY_STR;
		locationStringList[EQUIPPED_BOTH] = EQUIPPED_BOTH_STR;
		locationStringList[EQUIPPED_TWO_HANDS] = EQUIPPED_TWO_HANDS_STR;
		locationStringList[EQUIPPED_TEMPBONUS] = EQUIPPED_TEMPBONUS_STR;
		locationStringList[CARRIED_NEITHER] = CARRIED_NEITHER_STR;
		locationStringList[CONTAINED] = CONTAINED_STR;
		locationStringList[NOT_CARRIED] = NOT_CARRIED_STR;
	}

	private BigDecimal baseCost = BigDecimal.ZERO;

	private BigDecimal cost = BigDecimal.ZERO;

	private BigDecimal costMod = BigDecimal.ZERO;

	private BigDecimal weightMod = BigDecimal.ZERO;

	private String baseItem = Constants.EMPTY_STRING;

	private List<EquipmentModifier> eqModifierList = new ArrayList<EquipmentModifier>();

	private List<SpecialProperty> specialPropertyList = new ArrayList<SpecialProperty>();

	private List<String> vFeatList = null; // virtual feat list

	private boolean modifiersAllowed = true;

	private boolean modifiersRequired = false;

	private EquipmentCollection d_parent = null;

	private List<Equipment> d_containedEquipment = null;

	private Float carried = Float.valueOf(0); // OwnedItem

	private int location = NOT_CARRIED; // OwnedItem

	private boolean equipped = false; // OwnedItem

	private int numberEquipped = 0;

	private Float containerWeightCapacity = Float.valueOf(0);

	private Integer containerReduceWeight = Integer.valueOf(0);

	private boolean containerConstantWeight = false;

	private boolean d_acceptsChildren = false;

	private Integer acCheck = Integer.valueOf(0);

	private String fumbleRange = "";

	// effective DR vales for Armor
	private Integer eDR = Integer.valueOf(-1);

	private Integer maxDex = Integer.valueOf(100);

	private Integer spellFailure = Integer.valueOf(0);

	private WeaponEquipment theWeaponStats = null;

	private boolean isOnlyNaturalWeapon = false;

	private Map<String, Float> d_acceptsTypes = null;

	private Map<String, Float> d_childTypes = null;

	private String containerCapacityString = "";

	private String containerContentsString = "";

	private Map<String, String> qualityMap = new HashMap<String, String>();

	private List<EquipmentModifier> altEqModifierList = new ArrayList<EquipmentModifier>();

	private List<String> altTypeList = null;

	private String appliedBonusName = "";

	private String bonusType = null;

	private String indexedUnderType = "";

	private String longName = "";

	private String wholeItemName = "";

	private String modifiedName = "";

	private String moveString = "";

	// player added note
	private String noteString = "";

	private String profName = "";

	// How fast the weapon can be fired.
	private String size = "";

	private String sizeBase = "";

	private boolean automatic = false;

	private boolean bonusPrimary = true;

	private boolean calculatingCost = false;

	private boolean weightAlreadyUsed = false;

	private double qty = 0.0;

	private double weightInPounds = 0.0;

	// private Integer acMod = Integer.valueOf(0);
	private int outputIndex = 0;

	private int outputSubindex = 0;

	private int slots = 1;

	private int baseQuantity = 1;

	private List<String> typeListCachePrimary = null;

	private List<String> typeListCacheSecondary = null;

	private boolean dirty;

	private String cachedNameWithoutCharges;

	private String cachedNameWithCharges;

	private boolean virtualItem = false;

	{
		final SizeAdjustment sizeAdj = SettingsHandler.getGame()
				.getDefaultSizeAdjustment();
		if (sizeAdj != null) {
			size = sizeAdj.getAbbreviation();
		}
	}

	//
	// Name functions
	//
	/**
	 * Set's the Temporary Bonuses name used for Display on Output Sheets
	 * 
	 * @param aString
	 *            Name to use for temp bonus
	 */
	public void setAppliedName(final String aString) {
		appliedBonusName = aString;
	}

	/**
	 * Get Applied Name
	 * 
	 * @return Applied name
	 */
	public String getAppliedName() {
		if (appliedBonusName.length() > 0) {
			final StringBuffer aString = new StringBuffer();
			aString.append(" [").append(appliedBonusName).append("]");

			return aString.toString();
		}

		return "";
	}

	//
	// TYPE queries
	//
	/**
	 * Gets the ammunition attribute of the Equipment object
	 * 
	 * @return The ammunition value
	 */
	public boolean isAmmunition() {
		return isType("AMMUNITION");
	}

	/**
	 * Gets the armor attribute of the Equipment object
	 * 
	 * @return The armor value
	 */
	public boolean isArmor() {
		return isType("ARMOR");
	}

	/**
	 * Gets the double attribute of the Equipment object
	 * 
	 * @return The double value
	 */
	public boolean isDouble() {
		return isType("DOUBLE");
	}

	/**
	 * Gets the eitherType attribute of the Equipment object
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @return The eitherType value
	 */
	public boolean isEitherType(final String aType) {
		return isType(aType, true) || isType(aType, false);
	}

	/**
	 * Gets the extra attribute of the Equipment object
	 * 
	 * @return The extra value
	 */
	public boolean isExtra() {
		return isType("EXTRA");
	}

	/**
	 * Gets the heavy attribute of the Equipment object
	 * 
	 * @return The heavy value
	 */
	public boolean isHeavy() {
		return isType("HEAVY");
	}

	/**
	 * Gets the medium attribute of the Equipment object
	 * 
	 * @return The medium value
	 */
	public boolean isMedium() {
		return isType("MEDIUM");
	}

	/**
	 * Gets the light attribute of the Equipment object
	 * 
	 * @return The light value
	 */
	public boolean isLight() {
		return isType("LIGHT");
	}

	/**
	 * Gets the magic attribute of the Equipment object
	 * 
	 * @return The magic value
	 */
	public boolean isMagic() {
		return isType("MAGIC");
	}

	/**
	 * Gets the melee attribute of the Equipment object
	 * 
	 * @return The melee value
	 */
	public boolean isMelee() {
		return isType("MELEE");
	}

	/**
	 * Gets the monk attribute of the Equipment object
	 * 
	 * @return The monk value
	 */
	public boolean isMonk() {
		return isType("MONK");
	}

	/**
	 * Gets the natural attribute of the Equipment object
	 * 
	 * @return The natural value
	 */
	public boolean isNatural() {
		return isType("NATURAL");
	}

	/**
	 * Gets the ranged attribute of the Equipment object
	 * 
	 * @return The ranged value
	 */
	public boolean isRanged() {
		return isType("RANGED");
	}

	/**
	 * Gets the shield attribute of the Equipment object
	 * 
	 * @return The shield value
	 */
	public boolean isShield() {
		return isType("SHIELD");
	}

	/**
	 * Gets the suit attribute of the Equipment object
	 * 
	 * @return The suit value
	 */
	public boolean isSuit() {
		return isType("SUIT");
	}

	/**
	 * Gets the thrown attribute of the Equipment object
	 * 
	 * @return The thrown value
	 */
	public boolean isThrown() {
		return isType("THROWN");
	}

	/**
	 * Gets the type attribute of the Equipment object
	 * 
	 * @return The type
	 */
	public String getType() {
		return getType(true);
	}

	/**
	 * Gets the type attribute of the Equipment object
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @return The type value
	 */
	public boolean isType(final String aType) {
		return isType(aType, true);
	}

	/**
	 * Gets the type attribute of the Equipment object
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The type value
	 */
	public boolean isType(final String aType, final boolean bPrimary) {
		if (!bPrimary && !isDouble()) {
			return false;
		}

		final List<String> tList = typeList(bPrimary);

		return tList.contains(aType.toUpperCase());
	}

	/**
	 * Gets the unarmed attribute of the Equipment object
	 * 
	 * @return The unarmed value
	 */
	public boolean isUnarmed() {
		return isType("UNARMED");
	}

	/**
	 * Gets the weapon attribute of the Equipment object
	 * 
	 * @return The weapon value
	 */
	public boolean isWeapon() {
		return isType("WEAPON");
	}

	/**
	 * Gets the masterwork attribute of the Equipment object
	 * 
	 * @return The masterwork value
	 */
	boolean isMasterwork() {
		return isType("MASTERWORK");
	}

	/**
	 * Description of the Method
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean typeStringContains(final String aString) {
		return isType(aString);
	}

	/**
	 * Gets the projectile attribute of the Equipment object
	 * 
	 * @return The projectile value
	 */
	public boolean isProjectile() {
		// return isType("PROJECTILE");
		return isRanged() && !isThrown();
	}

	boolean isTypeHidden(final String type) {
		return Globals.isEquipmentTypeHidden(type);
	}

	protected List<String> getMyTypeList() {
		return typeList();
	}

	/**
	 * Return the set of equipment type names as a sorted set of strings.
	 * 
	 * @return The equipmentTypes value
	 */
	/*
	 * TODO Why is this in Equipment when Class, Race, WeaponProf are all in
	 * Globals? - thpr 10/20/06
	 */
	public static SortedSet<String> getEquipmentTypes() {
		return s_equipmentTypes;
	}

	//
	// Bonus functions
	//

	/**
	 * returns all BonusObj's that are "active"
	 * 
	 * @param aPC
	 *            PlayerCharacter used to check prereqs for bonuses
	 * @return active bonuses
	 */
	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC) {
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (BonusObj bonus : getBonusList()) {
			if (bonus.isApplied()) {
				aList.add(bonus);
			}
		}

		final List<EquipmentModifier> eqModList = getEqModifierList(true);

		for (EquipmentModifier eqMod : eqModList) {
			aList.addAll(eqMod.getActiveBonuses(this, aPC));
		}

		return aList;
	}

	/**
	 * get a list of BonusObj's of aType and aName
	 * 
	 * @param aType
	 *            a TYPE of bonus (such as "COMBAT" or "SKILL")
	 * @param aName
	 *            the NAME of the bonus (such as "ATTACKS" or "SPOT")
	 * @param bPrimary
	 *            used for double weapons (head1 vs head2)
	 * @return a list of bonusObj's of aType and aName
	 */
	public List<BonusObj> getBonusListOfType(final String aType,
			final String aName, final boolean bPrimary) {
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (BonusObj bonus : getBonusList()) {
			if ((bonus.getTypeOfBonus().indexOf(aType) >= 0)
					&& (bonus.getBonusInfo().indexOf(aName) >= 0)) {
				aList.add(bonus);
			}
		}

		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		if (!eqModList.isEmpty()) {
			for (EquipmentModifier eqMod : eqModList) {
				aList.addAll(eqMod.getBonusListOfType(aType, aName));
			}
		}

		return aList;
	}

	//
	// Misc properties
	//

	/**
	 * Sets the acCheck attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new acCheck value
	 */
	public void setACCheck(final String aString) {
		try {
			acCheck = Integer.valueOf(aString);
		} catch (NumberFormatException nfe) {
			acCheck = Integer.valueOf(0);
		}
	}

	/**
	 * Get ACCheck
	 * 
	 * @return AC Check
	 */
	public Integer getAcCheck() {
		return acCheck;
	}

	/**
	 * Returns the fumbleRange for this item.
	 * 
	 * Return the fumbleRange on the primary eqMod (if it exists) otherwise
	 * return it for the secondary eqMod otherwise return the fumbleRange for
	 * the item itself
	 * 
	 * @return the fumbleRange for this item.
	 */
	public String getFumbleRange() {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			if (eqMod.getFumbleRange().length() > 0)
				return eqMod.getFumbleRange();
		}

		for (EquipmentModifier eqMod : getEqModifierList(false)) {
			if (eqMod.getFumbleRange().length() > 0)
				return eqMod.getFumbleRange();
		}

		return fumbleRange;
	}

	/**
	 * Sets the fumbleRange for this item.
	 * 
	 * @param aString
	 *            the fumbleRange for this item.
	 */
	public void setFumbleRange(final String aString) {
		fumbleRange = aString;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	/**
	 * Set Automatic
	 * 
	 * @param arg
	 */
	public void setAutomatic(final boolean arg) {
		automatic = arg;
	}

	/**
	 * Set the base item
	 * 
	 * @param argBaseItem
	 */
	public void setBaseItem(final String argBaseItem) {
		baseItem = argBaseItem;
	}

	/**
	 * Gets the baseItemName attribute of the Equipment object
	 * 
	 * @return The baseItemName value
	 */
	public String getBaseItemName() {
		if (baseItem.length() == 0) {
			return getKeyName();
		}

		return baseItem;
	}

	/**
	 * Sets the cost attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new cost value
	 */
	public void setCost(final String aString) {
		setCost(aString, false);
	}

	/**
	 * Sets the cost attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new cost value
	 * @param bBase
	 *            if true, set the base cost along with the cost
	 */
	public void setCost(final String aString, final boolean bBase) {
		try {
			cost = new BigDecimal(aString);

			if (bBase) {
				baseCost = cost;
			}
		} catch (NumberFormatException ignore) {
			// ignore
		}
	}

	/**
	 * Gets the cost attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The cost value
	 */
	public BigDecimal getCost(final PlayerCharacter aPC) {
		BigDecimal c = BigDecimal.ZERO;

		if (this.isVirtual()) {
			return c;
		}

		//
		// Do pre-sizing cost increment.
		// eg. in the case of adamantine armor, want to add
		// the cost of the metal before the armor gets resized.
		//
		for (EquipmentModifier eqMod : eqModifierList) {
			int iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			final BigDecimal eqModCost = new BigDecimal(getVariableValue(
					eqMod.getPreCost(), "", true, aPC).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer
					.toString(getBaseQty() * iCount))));
			c = c.add(eqMod.addItemCosts(aPC, "ITEMCOST",
					getBaseQty() * iCount, this));
		}

		for (EquipmentModifier eqMod : altEqModifierList) {
			int iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			final BigDecimal eqModCost = new BigDecimal(getVariableValue(
					eqMod.getPreCost(), "", false, aPC).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer
					.toString(getBaseQty() * iCount))));
			c = c.add(eqMod.addItemCosts(aPC, "ITEMCOST", iCount, this));
		}

		//
		// c has cost of the item's modifications at the item's original size
		//
		double mult = 1.0;
		final SizeAdjustment newSA = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(getSize());
		final SizeAdjustment currSA = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(getBaseSize());

		if ((newSA != null) && (currSA != null)) {
			mult = newSA.getBonusTo(aPC, "ITEMCOST", typeList(), 1.0)
					/ currSA.getBonusTo(aPC, "ITEMCOST", typeList(), 1.0);
		}

		c = c.multiply(new BigDecimal(mult));

		BigDecimal itemCost = cost.add(c);

		final List<BigDecimal> modifierCosts = new ArrayList<BigDecimal>();

		BigDecimal nonDoubleCost = BigDecimal.ZERO;

		c = BigDecimal.ZERO;

		int iPlus = 0;
		int altPlus = 0;
		calculatingCost = true;
		weightAlreadyUsed = false;

		for (EquipmentModifier eqMod : eqModifierList) {
			int iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			BigDecimal eqModCost;
			String costFormula = eqMod.getCost();
			Pattern pat = Pattern.compile("BASECOST");
			Matcher mat;

			if ((eqMod.getAssociatedCount() > 0)
					&& !costFormula.equals(eqMod.getCost(0))) {
				eqModCost = BigDecimal.ZERO;

				for (int idx = 0; idx < eqMod.getAssociatedCount(); ++idx) {
					mat = pat.matcher(eqMod.getCost(idx));
					costFormula = mat.replaceAll("(BASECOST/" + getBaseQty()
							+ ")");

					final BigDecimal thisModCost = new BigDecimal(
							getVariableValue(costFormula, "", true, aPC)
									.toString());

					eqModCost = eqModCost.add(thisModCost);

					if (!eqMod.getCostDouble()) {
						nonDoubleCost = nonDoubleCost.add(thisModCost);
					} else {
						modifierCosts.add(thisModCost);
					}
				}

				iCount = 1;
			} else {
				mat = pat.matcher(eqMod.getCost());
				costFormula = mat.replaceAll("(BASECOST/" + getBaseQty() + ")");

				eqModCost = new BigDecimal(getVariableValue(costFormula, "",
						true, aPC).toString());

				if (!eqMod.getCostDouble()) {
					nonDoubleCost = nonDoubleCost.add(eqModCost);
				} else {
					modifierCosts.add(eqModCost);
				}
			}

			// Per D20 FAQ adjustments for special materials are per piece;
			if (eqMod.isType("BaseMaterial")) {
				eqModCost = eqModCost.multiply(new BigDecimal(getBaseQty()));
			}
			c = c.add(eqModCost);
			iPlus += (eqMod.getPlus() * iCount);
		}

		//
		// Get costs from lowest to highest
		//
		if (modifierCosts.size() > 1) {
			Collections.sort(modifierCosts);
		}

		for (EquipmentModifier eqMod : altEqModifierList) {
			int iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			final String costFormula = eqMod.getCost();
			final BigDecimal eqModCost = new BigDecimal(getVariableValue(
					costFormula, "", false, aPC).toString());
			c = c.add(eqModCost.multiply(new BigDecimal(Integer
					.toString(getBaseQty() * iCount))));
			altPlus += (eqMod.getPlus() * iCount);
		}

		calculatingCost = false;

		c = c.add(getCostFromPluses(iPlus, altPlus));

		//
		// Items with values less than 1 gp have their prices rounded up to 1 gp
		// per item
		// eg. 20 Arrows cost 1 gp, or 5 cp each. 1 MW Arrow costs 7 gp.
		//
		// Masterwork and Magical ammo is made in batches of 50, so the MW cost
		// per item
		// should be 6 gp. This would give a cost of 6.05 gp per arrow, 6.1 gp
		// per bolt and 6.01 gp
		// per bullet.
		//
		// if (c.compareTo(BigDecimal.ZERO) != 0)
		// {
		// //
		// // Convert to double and use math.ceil as ROUND_CEILING doesn't
		// appear to work
		// // on BigDecimal.divide
		// final int baseQ = getBaseQty();
		// itemCost = new BigDecimal(Math.ceil(itemCost.doubleValue() / baseQ) *
		// baseQ);
		// }

		if (!isAmmunition() && !isArmor() && !isShield() && !isWeapon()) {
			//
			// If item doesn't occupy a fixed location, then double the cost of
			// the modifications
			// DMG p.243
			//
			if (!isMagicLimitedType()) {
				//
				// TODO: Multiple similar abilities. 100% of costliest, 75% of
				// next, and 50% of rest
				//
				if (!ignoresCostDouble()) {
					c = c.subtract(nonDoubleCost).multiply(new BigDecimal("2"));
					c = c.add(nonDoubleCost);

					// c = c.multiply(new BigDecimal("2"));
				}
			} else {
				//
				// Add in the cost of 2nd, 3rd, etc. modifiers again (gives
				// times 2)
				//
				for (int i = modifierCosts.size() - 2; i >= 0; --i) {
					c = c.add(modifierCosts.get(i));
				}
			}
		}

		return c.add(itemCost).add(costMod);
	}

	/**
	 * Set cost mod
	 * 
	 * @param aString
	 */
	public void setCostMod(final String aString) {
		try {
			costMod = new BigDecimal(aString);
		} catch (NumberFormatException e) {
			costMod = BigDecimal.ZERO;
		}
	}

	/**
	 * Set cost mod
	 * 
	 * @param aCost
	 */
	public void setCostMod(final BigDecimal aCost) {
		costMod = aCost;
	}

	/**
	 * Get cost mod
	 * 
	 * @return cost mod
	 */
	public BigDecimal getCostMod() {
		return costMod;
	}

	// ---------------------------
	// Equipment Modifier Support
	// ---------------------------

	/**
	 * Gets the eqModifierKeyed attribute of the Equipment object
	 * 
	 * @param eqModKey
	 *            Description of the Parameter
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The eqModifierKeyed value
	 */
	public EquipmentModifier getEqModifierKeyed(final String eqModKey,
			final boolean bPrimary) {
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (EquipmentModifier eqMod : eqModList) {
			if (eqMod.getKeyName().equals(eqModKey)) {
				return eqMod;
			}
		}

		return null;
	}

	/**
	 * Gets the eqModifierList attribute of the Equipment object
	 * 
	 * @param bPrimary
	 *            if true, get the equipment modifiers for the primary head of
	 *            the weapon
	 * 
	 * @return The eqModifierList value
	 */
	public List<EquipmentModifier> getEqModifierList(final boolean bPrimary) {
		if (bPrimary) {
			return eqModifierList;
		}

		return altEqModifierList;
	}

	/**
	 * Add an EquipmentModifier object to the list
	 * 
	 * @param eqMod
	 *            The equipment modifier to add to list
	 * @param bPrimary
	 */
	public void addToEqModifierList(final EquipmentModifier eqMod,
			final boolean bPrimary) {
		typeListCachePrimary = null;
		getEqModifierList(bPrimary).add(eqMod);
		setDirty(true);
	}

	/**
	 * Set hands
	 * 
	 * @param argHands
	 */
	public void setHands(final int argHands) {
		slots = argHands;
	}

	/**
	 * Returns the number of slots required to use this item.
	 * 
	 * @param aPC
	 * 
	 * @return the number of slots required to use this item.
	 */
	public int getHands(final PlayerCharacter aPC) {
		return getSlots(aPC);
	}

	/**
	 * Returns the name of this hand
	 * 
	 * @param slotNumber
	 *            the slot for which a name is wanted
	 * @return the name of this slot
	 */
	public static String getLocationName(final int slotNumber) {
		if ((slotNumber < 0) || (slotNumber > locationStringList.length)) {
			return locationStringList[0];
		}
		return locationStringList[slotNumber];
	}

	/**
	 * Returns the number of a slot
	 * 
	 * @param locDesc
	 *            The name of a location one wants to know the number of
	 * @return the number of a location
	 */
	public static int getLocationNum(final String locDesc) {
		for (int i = 0; i < locationStringList.length; ++i) {
			if (locationStringList[i].equals(locDesc)) {
				return i;
			}
		}

		if (locDesc.equals(Constants.s_NONE)) {
			return NOT_CARRIED;
		}

		if (locDesc.startsWith(CONTAINED_STR)) {
			return CONTAINED;
		}

		try {
			return Integer.parseInt(locDesc);
		} catch (NumberFormatException nfe) {
			// Assume that the string is the name of another equipment item
			return CONTAINED;

			// GuiFacade.showMessageDialog(null, "Unable to interpret hand
			// setting: " + handDesc, Constants.s_APPNAME,
			// GuiFacade.ERROR_MESSAGE);
			// return NOT_CARRIED;
		}
	}

	/**
	 * Get display information for all "interesting" properties.
	 * 
	 * @param aPC
	 * 
	 * @return display string of bonuses and special properties
	 */
	public String getInterestingDisplayString(final PlayerCharacter aPC) {
		final StringBuffer s = new StringBuffer();
		String t = getSpecialProperties(aPC);

		if (t == null) {
			t = "";
		}

		for (BonusObj aBonus : getActiveBonuses(aPC)) {
			final String eqBonus = aBonus.toString();

			if ((eqBonus.length() > 0) && !eqBonus.startsWith("EQM")) {
				if (s.length() != 0) {
					s.append(", ");
				}

				s.append(eqBonus);
			}
		}

		// for (final Iterator<EquipmentModifier> e = eqModifierList.iterator();
		// e.hasNext();)
		// {
		// final EquipmentModifier eqMod = e.next();
		// for (final Iterator<BonusObj> mI = eqMod.getBonusList().iterator();
		// mI.hasNext();)
		// {
		// final BonusObj aBonus = mI.next();
		// final String eqModBonus = aBonus.toString();
		// if ((eqModBonus.length() != 0) && !eqModBonus.startsWith("EQM"))
		// {
		// if (s.length() != 0)
		// {
		// s.append(", ");
		// }
		// s.append(eqModBonus);
		// }
		// }
		// }
		if (t.length() != 0) {
			if (s.length() != 0) {
				s.append('|');
			}

			s.append(t);
		}

		return s.toString();
	}

	/**
	 * Sets the isEquipped attribute of the Equipment object.
	 * 
	 * @param aFlag
	 *            The new isEquipped value
	 * @param aPC
	 */
	public void setIsEquipped(final boolean aFlag, final PlayerCharacter aPC) {
		equipped = aFlag;

		if (equipped) {
			activateBonuses(aPC);
		} else {
			deactivateBonuses();
		}
	}

	/**
	 * Get the item name based off the modifiers
	 * 
	 * @return item name based off the modifiers
	 */
	public String getItemNameFromModifiers() {
		final List<EquipmentModifier> modList = new ArrayList<EquipmentModifier>(
				eqModifierList);
		final List<EquipmentModifier> altModList = new ArrayList<EquipmentModifier>(
				altEqModifierList);
		final List<EquipmentModifier> commonList = new ArrayList<EquipmentModifier>();
		// TODO Change the arrays of lists to be lists of lists instead to get
		// rid of eclipse warnings. See
		// http://www.angelikalanger.com/Articles/Papers/JavaGenerics/ArraysInJavaGenerics.htm
		final List<EquipmentModifier> modListByFC[] = initSplitModList();
		final List<EquipmentModifier> altModListByFC[] = initSplitModList();
		final List<EquipmentModifier> commonListByFC[] = initSplitModList();

		//
		// Remove any modifiers on the base item so they don't confuse the
		// naming
		//
		if (baseItem.length() == 0) {
			return getName();
		}

		final Equipment baseEquipment = EquipmentList
				.getEquipmentKeyed(baseItem);

		if (baseEquipment != null) {
			for (EquipmentModifier eqMod : baseEquipment
					.getEqModifierList(true)) {
				final int idx = modList.indexOf(eqMod);

				if (idx >= 0) {
					modList.remove(idx);
				}
			}

			for (EquipmentModifier eqMod : baseEquipment
					.getEqModifierList(false)) {
				final int idx = altModList.indexOf(eqMod);

				if (idx >= 0) {
					altModList.remove(idx);
				}
			}
		}

		for (int i = modList.size() - 1; i >= 0; --i) {
			final EquipmentModifier eqMod = modList.get(i);

			if (eqMod.getVisible() == EquipmentModifier.VISIBLE_NO) {
				modList.remove(i);

				continue;
			}
		}

		extractListFromCommon(commonList, modList);

		removeCommonFromList(altModList, commonList,
				"eqMod expected but not found: ");

		// Remove masterwork from the list if magic is present
		suppressMasterwork(commonList, modList, altModList);

		// Split the eqmod lists by format category
		splitModListByFormatCat(commonList, commonListByFC);
		splitModListByFormatCat(modList, modListByFC);
		splitModListByFormatCat(altModList, altModListByFC);

		final StringBuffer itemName = new StringBuffer();

		// Add in front eq mods
		String eqmodDesc = buildEqModDesc(
				commonListByFC[EquipmentModifier.FORMATCAT_FRONT],
				modListByFC[EquipmentModifier.FORMATCAT_FRONT],
				altModListByFC[EquipmentModifier.FORMATCAT_FRONT]);
		itemName.append(eqmodDesc);
		if (itemName.length() > 0) {
			itemName.append(' ');
		}

		//
		// Add in the base name, less any modifiers
		//
		final String baseName = getBaseItemName().trim();
		int idx = baseName.indexOf('(');
		if (idx >= 0) {
			itemName.append(baseName.substring(0, idx - 1).trim());
		} else {
			itemName.append(baseName);
		}

		// Add in middle mods
		eqmodDesc = buildEqModDesc(
				commonListByFC[EquipmentModifier.FORMATCAT_MIDDLE],
				modListByFC[EquipmentModifier.FORMATCAT_MIDDLE],
				altModListByFC[EquipmentModifier.FORMATCAT_MIDDLE]);
		if (eqmodDesc.length() > 0) {
			itemName.append(' ').append(eqmodDesc);
		}

		//
		// Tack on the original modifiers
		//
		if (idx >= 0) {
			itemName.append(' ');
			itemName.append(baseName.substring(idx));
		}

		//
		// Strip off the ending ')' in anticipation of more modifiers
		//
		idx = itemName.toString().lastIndexOf(')');

		if (idx >= 0) {
			itemName.setLength(idx);
			itemName.append('/');
		} else {
			itemName.append(" (");
		}

		//
		// Put size in name if not the same as the base item
		//
		final int iSize = Globals.sizeInt(getSize(), 4);
		if (Globals.sizeInt(getBaseSize(), 4) != iSize) {
			itemName.append((SettingsHandler.getGame()
					.getSizeAdjustmentAtIndex(iSize)).getDisplayName());
			itemName.append('/');
		}

		// Put in parens mods
		eqmodDesc = buildEqModDesc(
				commonListByFC[EquipmentModifier.FORMATCAT_PARENS],
				modListByFC[EquipmentModifier.FORMATCAT_PARENS],
				altModListByFC[EquipmentModifier.FORMATCAT_PARENS]);
		itemName.append(eqmodDesc);

		//
		// If there were no modifiers, then drop the trailing '/'
		//
		if (itemName.toString().endsWith("/")
				|| itemName.toString().endsWith(";")) {
			itemName.setLength(itemName.length() - 1);
		}

		itemName.append(')');

		//
		// If there were no modifiers, then strip the empty parenthesis
		//
		idx = itemName.toString().indexOf(" ()");

		if (idx >= 0) {
			itemName.setLength(idx);
		}

		return itemName.toString();
	}

	/**
	 * Where a magic eqmod is present, remove the masterwork eqmod from the
	 * list.
	 * 
	 * @param commonList
	 *            The list of eqmods on both heads (or only head)
	 * @param modList
	 *            The list of eqmods on the primary head
	 * @param altModList
	 *            The list of eqmods on the secondary head
	 */
	private void suppressMasterwork(List<EquipmentModifier> commonList,
			List<EquipmentModifier> modList, List<EquipmentModifier> altModList)

	{
		//
		// Look for a modifier named "masterwork" (assumption: this is marked as
		// "assigntoall")
		//
		EquipmentModifier eqMaster = null;
		for (EquipmentModifier eqMod : commonList) {
			if ("MASTERWORK".equalsIgnoreCase(eqMod.getDisplayName())
					|| eqMod.isIType("Masterwork")) {
				eqMaster = eqMod;

				break;
			}
		}

		if (eqMaster == null) {
			return;
		}

		final EquipmentModifier magicMod1 = getMagicBonus(eqModifierList);
		EquipmentModifier magicMod2 = null;
		if (isDouble()) {
			magicMod2 = getMagicBonus(altEqModifierList);
		}

		if (magicMod1 != null || magicMod2 != null) {
			commonList.remove(eqMaster);
		}
	}

	/**
	 * Build up the description of the listed equipmods for this equipment item.
	 * Takes into account if the item is a double weapon or not.
	 * 
	 * @param commonList
	 *            The list of common equipment modifiers.
	 * @param modList
	 *            The list of eqmods on the primary head.
	 * @param altModList
	 *            The list of eqmods on the secondary head.
	 * @return The description of these equipment modifiers.
	 */
	private String buildEqModDesc(List<EquipmentModifier> commonList,
			List<EquipmentModifier> modList, List<EquipmentModifier> altModList) {
		StringBuffer desc = new StringBuffer();

		String commonDesc = getNameFromModifiers(commonList);
		String modDesc = getNameFromModifiers(modList);
		String altModDesc = getNameFromModifiers(altModList);

		if ((modList.isEmpty()) && (altModList.isEmpty())) {
			desc.append(commonDesc);
		} else if (!isDouble()) {
			desc.append(modDesc);
			if (!modList.isEmpty() && !commonList.isEmpty()) {
				desc.append('/');
			}
			desc.append(commonDesc);
		} else {
			if (commonDesc.length() != 0) {
				desc.append(commonDesc).append(';');
			}

			if (modDesc.length() != 0) {
				desc.append(modDesc);
			} else {
				desc.append('-');
			}

			desc.append(';');

			if (altModDesc.length() != 0) {
				desc.append(altModDesc);
			} else {
				desc.append('-');
			}
		}
		return desc.toString();
	}

	/**
	 * OwnedItem Sets the location attribute of the Equipment object
	 * 
	 * @param newLocation
	 *            int containing the new location value
	 */
	public void setLocation(final int newLocation) {
		if ((newLocation < EQUIPPED_NEITHER) || (newLocation > NOT_CARRIED)) {
			final String errMsg = PropertyFactory.getFormattedString(
					"EquipLocation.Unknown", newLocation);
			ShowMessageDelegate.showMessageDialog(errMsg, Constants.s_APPNAME,
					MessageType.INFORMATION);

			return;
		}

		if (newLocation == EQUIPPED_TEMPBONUS) {
			location = newLocation;
			equipped = true;
		} else if ((newLocation >= EQUIPPED_NEITHER)
				&& (newLocation <= EQUIPPED_TWO_HANDS)) {
			location = newLocation;
			equipped = true;
		} else {
			if (newLocation == NOT_CARRIED) {
				location = NOT_CARRIED;
				equipped = false;
			} else {
				location = CARRIED_NEITHER;
				equipped = false;
			}
		}
	}

	/**
	 * OwnedItem Gets the hand attribute of the Equipment object
	 * 
	 * @return int containing the location value
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * Sets the longName attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new longName value
	 */
	public void setLongName(final String aString) {
		longName = aString;
	}

	/**
	 * Get maximum charges
	 * 
	 * @return maximum charges
	 */
	public int getMaxCharges() {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			final int maxCharges = eqMod.getMaxCharges();

			if (maxCharges > 0) {
				return maxCharges;
			}
		}

		return 0;
	}

	/**
	 * Sets the maxDex attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new maxDex value
	 */
	public void setMaxDex(final String aString) {
		try {
			maxDex = Delta.decode(aString);
		} catch (NumberFormatException ignore) {
			// ignore
		}
	}

	/**
	 * Gets the maxDex attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The maxDex value
	 */
	public Integer getMaxDex(final PlayerCharacter aPC) {
		int mdex = maxDex.intValue()
				+ (int) bonusTo(aPC, "EQMARMOR", "MAXDEX", true);

		if (mdex > Constants.MAX_MAXDEX) {
			mdex = Constants.MAX_MAXDEX;
		}

		if (mdex < 0) {
			mdex = 0;
		}

		return Integer.valueOf(mdex);
	}

	/**
	 * Get minimum charges
	 * 
	 * @return minimum charges
	 */
	public int getMinCharges() {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			final int minCharges = eqMod.getMinCharges();

			if (minCharges > 0) {
				return minCharges;
			}
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.PObject#setName(java.lang.String)
	 */
	public void setName(final String aString) {
		super.setName(aString);
		setDirty(true);
	}

	/**
	 * Sets the modifiedName attribute of the Equipment object
	 * 
	 * @param nameString
	 *            The new modifiedName value
	 */
	public void setModifiedName(final String nameString) {
		modifiedName = nameString;
		setDirty(true);
	}

	/**
	 * Set modifiers allowed
	 * 
	 * @param argModifiersAllowed
	 */
	public void setModifiersAllowed(final boolean argModifiersAllowed) {
		modifiersAllowed = argModifiersAllowed;
	}

	/**
	 * Set modifiers required
	 * 
	 * @param argModifiersRequired
	 */
	public void setModifiersRequired(final boolean argModifiersRequired) {
		modifiersRequired = argModifiersRequired;
	}

	/**
	 * Gets the modifiersRequired attribute of the Equipment object.
	 * 
	 * @return The modifiersRequired value
	 */
	public boolean getModifiersRequired() {
		return modifiersRequired;
	}

	/**
	 * Sets the moveString attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new moveString value
	 */
	public void setMoveString(final String aString) {
		moveString = aString;
	}

	/**
	 * Gets the name attribute of the Equipment object
	 * 
	 * @return The name value
	 */
	public String getName() {
		return toString();
	}

	/**
	 * set's the player added note for this item
	 * 
	 * @param aString
	 */
	public void setNote(final String aString) {
		noteString = aString;
	}

	/**
	 * return the player added note for this item
	 * 
	 * @return note
	 */
	public String getNote() {
		return noteString;
	}

	/**
	 * Sets the numberCarried attribute of the Equipment object.
	 * 
	 * @param aNumber
	 *            The new numberCarried value
	 */
	public void setNumberCarried(final Float aNumber) {
		carried = aNumber;
	}

	/**
	 * Sets the numberEquipped attribute of the Equipment object.
	 * 
	 * @param num
	 *            The new numberEquipped value
	 */
	public void setNumberEquipped(final int num) {
		numberEquipped = num;

		if (num > 0) {
			equipped = true;
		}
	}

	/**
	 * Gets the numberEquipped attribute of the Equipment object.
	 * 
	 * @return The numberEquipped value
	 */
	public int getNumberEquipped() {
		return numberEquipped;
	}

	/**
	 * Set to true if this is the only natural weapon.
	 * 
	 * @param onlyNaturalWeapon
	 *            set to true if this is the only natural weapon.
	 */
	public void setOnlyNaturalWeapon(final boolean onlyNaturalWeapon) {
		isOnlyNaturalWeapon = onlyNaturalWeapon;
	}

	/**
	 * Returns true if this is the only natural weapon.
	 * 
	 * @return true if this is the only natural weapon.
	 */
	public boolean isOnlyNaturalWeapon() {
		return isOnlyNaturalWeapon;
	}

	/**
	 * Set this item's output index, which controls the order in which the
	 * equipment appears on a character sheet. Note: -1 means hidden and 0 means
	 * not set <p/> <br>
	 * author: James Dempsey 17-Jun-02
	 * 
	 * @param newIndex
	 *            the new output index for this equipment item (-1=hidden, 0=not
	 *            set)
	 */
	public void setOutputIndex(final int newIndex) {
		outputIndex = newIndex;
	}

	/**
	 * Return the output index, which controls the order in which the equipment
	 * appears on a character sheet. Note: -1 means hidden and 0 means not set
	 * <p/> <br>
	 * author: James Dempsey 17-Jun-02
	 * 
	 * @return the output index for this equipment item (-1=hidden, 0=not set)
	 */
	public int getOutputIndex() {
		return outputIndex;
	}

	/**
	 * Set this item's output subindex, which controls the order in which
	 * equipment with the same output index appears on a character sheet. This
	 * basically applies to natural weapons only, since they have output index 0
	 * <p/> <br>
	 * author: Stefan Radermacher 11-Feb-05
	 * 
	 * @param newSubindex
	 *            the new output subindex for this equipment item
	 */
	public void setOutputSubindex(final int newSubindex) {
		outputSubindex = newSubindex;
	}

	/**
	 * Return the output subindex, which controls the order in which equipment
	 * with the same output index appears on a character sheet. This basically
	 * applies to natural weapons only, since they have output index 0 <p/> <br>
	 * author: Stefan Radermacher 11-Feb-05
	 * 
	 * @return the output subindex for this equipment item
	 */
	public int getOutputSubindex() {
		return outputSubindex;
	}

	/**
	 * Sets the parent attribute of the Equipment object
	 * 
	 * @param parent
	 *            The new parent value
	 */
	public void setParent(final EquipmentCollection parent) {
		d_parent = parent;
	}

	/**
	 * Gets the parent of the Equipment object
	 * 
	 * @return The parent
	 */
	public EquipmentCollection getParent() {
		return d_parent;
	}

	/**
	 * Gets the parentName of the Equipment object
	 * 
	 * @return The parentName
	 */
	public String getParentName() {
		final Equipment anEquip = (Equipment) getParent();

		if (anEquip != null) {
			return anEquip.toString();
		}

		if (isEquipped()) {
			return "Equipped";
		}

		if (numberCarried().intValue() > 0) {
			return "Carried";
		}

		return "";
	}

	/**
	 * Callback function from PObject.passesPreReqToGainForList()
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @return The preType value
	 */
	public boolean isPreType(String aType) {
		//
		// PRETYPE:EQMODTYPE=MagicalEnhancement
		// PRETYPE:[EQMOD=Holy],EQMOD=WEAP+5
		// PRETYPE:.IF.TYPE=Armor.Shield.Weapon.THEN.EQMODTYPE=MagicalEnhancement.ELSE.
		//
		if (aType.startsWith(".IF.TYPE=")) {
			final StringTokenizer aTok = new StringTokenizer(
					aType.substring(9), ".");
			boolean typeFound = false;
			String truePart;
			String falsePart = "";

			int idx = aType.indexOf(".THEN.");

			if (idx < 0) {
				return false;
			}

			truePart = aType.substring(idx + 6);
			aType = aType.substring(0, idx); // TODO: value never used
			idx = truePart.indexOf(".ELSE.");

			if (idx >= 0) {
				falsePart = truePart.substring(idx + 6);
				truePart = truePart.substring(0, idx);
			}

			while (aTok.hasMoreTokens()) {
				final String aString = aTok.nextToken();

				if (isType(aString, bonusPrimary)) {
					typeFound = true;

					break;
				}
			}

			if (typeFound) {
				aType = truePart;
			} else {
				aType = falsePart;
			}

			if (aType.length() == 0) {
				return true;
			}
		}

		if (aType.startsWith("EQMODTYPE=") || aType.startsWith("EQMODTYPE.")) {
			aType = aType.substring(10);

			for (EquipmentModifier eqMod : getEqModifierList(bonusPrimary)) {
				if (eqMod.isType(aType)) {
					return true;
				}
			}

			return false;
		} else if (aType.startsWith("EQMOD=") || aType.startsWith("EQMOD.")) {
			aType = aType.substring(6);

			if (getEqModifierKeyed(aType, bonusPrimary) != null) {
				return true;
			}

			return false;
		}

		return isType(aType, bonusPrimary);
	}

	/**
	 * Set proficiency name
	 * 
	 * @param aString
	 */
	public void setProfName(final String aString) {
		profName = aString;
	}

	/**
	 * Get proficiency name
	 * 
	 * @return proficiency name
	 */
	public String getProfName() {
		return profName;
	}

	/**
	 * Sets the qty attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new qty value
	 */
	public void setQty(final String aString) {
		try {
			setQty(Double.parseDouble(aString));
		} catch (NumberFormatException nfe) {
			qty = 0.0;
		}
	}

	/**
	 * Sets the qty attribute of the Equipment object
	 * 
	 * @param aFloat
	 *            The new qty value
	 */
	public void setQty(final Float aFloat) {
		setQty(aFloat.doubleValue());
	}

	/**
	 * Get the quantity of items
	 * 
	 * @return return a Float of the quantity
	 */
	public Float getQty() {
		return new Float(qty);
	}

	/**
	 * Sets the range attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new range value
	 */
	public void setRange(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setRange(aString);
	}

	/**
	 * Gets the range attribute of the Equipment object
	 * 
	 * @return The range value
	 * @param aPC
	 */
	public Integer getRange(final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			return theWeaponStats.getRange(aPC);
		}
		return 0;
	}

	/**
	 * Set the weapon's rate of fire
	 * 
	 * @param rateOfFire
	 *            A free-format string.
	 */
	public void setRateOfFire(final String rateOfFire) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setRateOfFire(rateOfFire);
	}

	/**
	 * Returns the weapon's rate of fire Defaults to empty string
	 * 
	 * @return The weapon's rate of fire
	 */
	public String getRateOfFire() {
		if (theWeaponStats != null) {
			return theWeaponStats.getRateOfFire();
		}
		return "";
	}

	/**
	 * Gets the rawCritRange attribute of the Equipment object
	 * 
	 * @return The rawCritRange value
	 */
	public int getRawCritRange() {
		return getRawCritRange(true);
	}

	/**
	 * Gets the rawCritRange attribute of the Equipment object
	 * 
	 * @param bPrimary
	 *            True=Primary Head
	 * @return The rawCritRange value
	 */
	public int getRawCritRange(final boolean bPrimary) {
		if (theWeaponStats != null) {
			return theWeaponStats.getRawCritRange(bPrimary);
		}
		return 0;
	}

	/**
	 * Get the raw special properties
	 * 
	 * @return raw special propertie
	 */
	public String getRawSpecialProperties() {
		final StringBuffer retString = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < specialPropertyList.size(); i++) {
			final SpecialProperty sprop = specialPropertyList.get(i);
			if (!first) {
				retString.append(", ");
			}
			first = false;
			retString.append(sprop.getParsedText());
		}
		return retString.toString();
	}

	/**
	 * Sets the reach attribute of the Equipment object.
	 * 
	 * @param newReach
	 *            The new reach value
	 */
	public void setReach(final int newReach) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setReach(newReach);
	}

	/**
	 * Gets the reach attribute of the Equipment object.
	 * 
	 * @return The reach value
	 */
	public int getReach() {
		if (theWeaponStats != null) {
			return theWeaponStats.getReach();
		}
		return 0;
	}

	/**
	 * Sets the reach multiplier attribute of the Equipment object
	 * 
	 * @param i
	 *            the new reach multiplier
	 */
	public void setReachMult(int i) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setReachMult(i);
	}

	/**
	 * Gets the reach multiplier attribute of the Equipment Object
	 * 
	 * @return the reach multiplier value
	 */
	public int getReachMult() {
		if (theWeaponStats != null) {
			return theWeaponStats.getReachMult();
		}
		return 0;
	}

	/**
	 * Set the remaining charges
	 * 
	 * @param remainingCharges
	 */
	public void setRemainingCharges(final int remainingCharges) {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			if (eqMod.getMinCharges() > 0) {
				eqMod.setRemainingCharges(remainingCharges);
			}
		}
	}

	/**
	 * Get the remaining charges
	 * 
	 * @return remaining charges
	 */
	public int getRemainingCharges() {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			if (eqMod.getMinCharges() > 0) {
				return eqMod.getRemainingCharges();
			}
		}

		return -1;
	}

	/**
	 * Gets the simple name attribute of the Equipment object
	 * 
	 * @return The name value
	 */
	public String getSimpleName() {
		return displayName;
	}

	/**
	 * Sets the size attribute of the Equipment object
	 * 
	 * @param sizeString
	 *            The new size value
	 * @param bBase
	 *            The new size value
	 */
	public void setSize(String sizeString, final boolean bBase) {
		if (sizeString.length() > 1) {
			sizeString = sizeString.toUpperCase().substring(0, 1);
		}

		size = sizeString;

		if (bBase) {
			sizeBase = sizeString;
		}
	}

	/**
	 * Gets the size attribute of the Equipment object
	 * 
	 * @return The size value
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Set the number of "Slots" required to equip this item
	 * 
	 * @param i
	 */
	public void setSlots(final int i) {
		slots = i;
	}

	/**
	 * The number of "Slots" that this item requires The slot type is derived
	 * from system/special/equipmentslot.lst
	 * 
	 * @param aPC
	 * @return slots
	 */
	public int getSlots(final PlayerCharacter aPC) {
		int iSlots = slots;

		for (EquipmentModifier eqMod : eqModifierList) {
			iSlots += (int) eqMod.bonusTo(aPC, "EQM", "HANDS", this);
			iSlots += (int) eqMod.bonusTo(aPC, "EQM", "SLOTS", this);
		}

		if (iSlots < 0) {
			iSlots = 0;
		}

		return iSlots;
	}

	/**
	 * This method overrides the PObject method to add "Custom" to the front of
	 * the source string if the equipment was customized.
	 * 
	 * @see pcgen.core.PObject#getDefaultSourceString()
	 */
	@Override
	public String getDefaultSourceString() {
		if (isType(Constants.s_CUSTOM)) {
			return PropertyFactory.getString("in_custom") + " - " //$NON-NLS-1$ //$NON-NLS-2$
					+ super.getDefaultSourceString();
		}

		return super.getDefaultSourceString();
	}

	/**
	 * Clears all special properties of an Equipment.
	 */
	public void clearSpecialProperties() {
		specialPropertyList.clear();
	}

	/**
	 * Sets special properties of an Equipment.
	 * 
	 * @param sprop
	 *            The properties to set
	 */
	public void addSpecialProperty(final SpecialProperty sprop) {
		specialPropertyList.add(sprop);
	}

	/**
	 * Returns special properties of an Equipment.
	 * 
	 * @param aPC
	 * @return special properties of an Equipment.
	 */
	public String getSpecialProperties(final PlayerCharacter aPC) {
		final List<EquipmentModifier> list1 = new ArrayList<EquipmentModifier>(
				eqModifierList);
		final List<EquipmentModifier> list2 = new ArrayList<EquipmentModifier>(
				altEqModifierList);
		final List<EquipmentModifier> comn = new ArrayList<EquipmentModifier>();

		extractListFromCommon(comn, list1);

		removeCommonFromList(list2, comn,
				"SPROP: eqMod expected but not found: ");

		final String common = CoreUtility
				.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(
						comn, aPC)));
		final String saList1 = CoreUtility
				.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(
						list1, aPC)));
		final String saList2 = CoreUtility
				.commaDelimit(getSpecialAbilityTimesList(getSpecialAbilityList(
						list2, aPC)));
		final StringBuffer sp = new StringBuffer();

		boolean first = true;
		for (int i = 0; i < specialPropertyList.size(); i++) {
			final SpecialProperty sprop = specialPropertyList.get(i);
			final String text = sprop.getParsedText(aPC, this);
			if (!text.equals("")) {
				if (!first) {
					sp.append(", ");
				}
				first = false;
				sp.append(text);
			}
		}

		if (common.length() != 0) {
			if (!first) {
				sp.append(", ");
			}
			first = false;

			sp.append(common);
		}

		if (saList1.length() != 0) {
			if (!first) {
				sp.append(", ");
			}
			first = false;

			if (isDouble()) {
				sp.append("Head1: ");
			}

			sp.append(saList1);
		}

		if (isDouble() && (saList2.length() != 0)) {
			if (!first) {
				sp.append(", ");
			}
			first = false; // TODO: value never used

			sp.append("Head2: ").append(saList2);
		}

		return sp.toString();
	}

	/**
	 * Sets the spellFailure attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new spellFailure value
	 */
	public void setSpellFailure(final String aString) {
		try {
			spellFailure = Delta.decode(aString);
		} catch (NumberFormatException ignore) {
			// ignore
		}
	}

	/**
	 * Gets the uberParent attribute of the Equipment object
	 * 
	 * @return The uberParent value
	 */
	public Equipment getUberParent() {
		if (getParent() == null) {
			return this;
		}

		Equipment anEquip = (Equipment) getParent();

		while (anEquip.getParent() != null) {
			anEquip = (Equipment) anEquip.getParent();
		}

		return anEquip;
	}

	/**
	 * Get used charges
	 * 
	 * @return used charges
	 */
	public int getUsedCharges() {
		for (EquipmentModifier eqMod : getEqModifierList(true)) {
			if (eqMod.getMinCharges() > 0) {
				return eqMod.getUsedCharges();
			}
		}

		return -1;
	}

	/**
	 * Get the value of a variable passed as aString. This uses a different
	 * variable processor than Player character because equipment has different
	 * "hard coded" variables than a Player Character.
	 * 
	 * @param varName
	 *            The name of the variable to look up
	 * @param src
	 * @param aPC
	 *            The PC this equipment is associated with
	 * 
	 * @return the value of the variable
	 */
	public Float getVariableValue(final String varName, final String src,
			final PlayerCharacter aPC) {
		return getVariableValue(varName, src, bonusPrimary, aPC);
	}

	/**
	 * Get the value of a variable passed as aString. This uses a different
	 * variable processor than Player character because equipment has different
	 * "hard coded" variables than a Player Character.
	 * 
	 * @param varName
	 *            The name of the variable to look up
	 * @param src
	 * @param bPrimary
	 *            If the head of the weapon has any effect on the variable
	 *            value, this flag stipulates which head to use (true means use
	 *            the primary head).
	 * @param aPC
	 *            The PC this equipment is associated with
	 * 
	 * @return The value of the variable
	 */
	public Float getVariableValue(String varName, final String src,
			final boolean bPrimary, final PlayerCharacter aPC) {
		VariableProcessor vp = new VariableProcessorEq(this, aPC, bPrimary);
		return vp.getVariableValue(null, varName, src, 0);
	}

	/**
	 * Get the list of virtual feats that this item grants to its wielder.
	 * 
	 * @return a list of virtual feats granted by this item.
	 */
	// public List<Ability> getVirtualFeatList()
	// {
	// List<Ability> vFeats = new
	// ArrayList<Ability>(super.getVirtualFeatList());
	//
	// vFeats = addEqModList(true, vFeats);
	// vFeats = addEqModList(false, vFeats);
	//
	// return vFeats;
	// }
	/**
	 * Returns true if the equipment modifier is visible
	 * 
	 * @param eqMod
	 *            The equipment modifier
	 * @return The visible value
	 */
	public boolean isVisible(final EquipmentModifier eqMod) {
		final int vis = eqMod.getVisible();

		if (vis == EquipmentModifier.VISIBLE_QUALIFIED) {
			bonusPrimary = true;
			if (eqMod.passesPreReqToGain(this, null)) {
				return true;
			}
			//
			// Check the secondary head if the primary head doesn't qualify (and
			// the item has a secondary head)
			//
			if (isDouble()) {
				bonusPrimary = false;
				return eqMod.passesPreReqToGain(this, null);
			}
			return false;
		}

		return vis == EquipmentModifier.VISIBLE_YES;
	}

	/**
	 * Sets the weight attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new weight value
	 */
	public void setWeight(final String aString) {
		try {
			weightInPounds = Double.parseDouble(aString);
		} catch (NumberFormatException ignore) {
			// ignore
		}
	}

	/**
	 * Gets the weight attribute of the Equipment object.
	 * 
	 * @param aPC
	 * 
	 * @return The weight value
	 */
	public Float getWeight(final PlayerCharacter aPC) {
		if (this.isVirtual()) {
			return new Float(0.0);
		}
		return new Float(getWeightAsDouble(aPC));
	}

	/**
	 * get base weight as double
	 * 
	 * @return base weight (as a double)
	 */
	public double getBaseWeightAsDouble() {
		if (this.isVirtual()) {
			return 0.0;
		}

		double aWeight = weightInPounds;
		aWeight += weightMod.doubleValue();

		return aWeight;
	}

	/**
	 * Get the weight as a double
	 * 
	 * @param aPC
	 * @return weight as as double
	 */
	public double getWeightAsDouble(final PlayerCharacter aPC) {
		if (this.isVirtual()) {
			return 0.0;
		}

		double f = bonusTo(aPC, "EQM", "WEIGHTMULT", true);

		if (CoreUtility.doublesEqual(f, 0.0)) {
			f = 1.0;
		}

		double aWeight = weightInPounds * f;

		f = bonusTo(aPC, "EQM", "WEIGHTDIV", true);

		if (CoreUtility.doublesEqual(f, 0)) {
			f = 1;
		}

		aWeight /= f;

		aWeight += bonusTo(aPC, "EQM", "WEIGHTADD", true);
		aWeight += weightMod.doubleValue();

		return aWeight;
	}

	/**
	 * Set weight mod
	 * 
	 * @param aString
	 */
	public void setWeightMod(final String aString) {
		try {
			weightMod = new BigDecimal(aString);
		} catch (NumberFormatException e) {
			weightMod = BigDecimal.ZERO;
		}
	}

	/**
	 * Set damage (this is used to overide default equipment)
	 * 
	 * @param aString
	 *            The new damage value
	 */
	public void setDamageMod(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setDamageMod(aString);
	}

	/**
	 * Get damage mod
	 * 
	 * @return damage mod
	 */
	public String getDamageMod() {
		if (theWeaponStats != null) {
			return theWeaponStats.getDamageMod();
		}
		return "";
	}

	/**
	 * new 3.5 Wield Category
	 * 
	 * @param aString
	 */
	public void setWield(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setWield(aString);
	}

	/**
	 * Get weild
	 * 
	 * @return weild
	 */
	public String getWield() {
		if (theWeaponStats != null) {
			return theWeaponStats.getWield();
		}
		return "";
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC
	 * 
	 * @return Description of the Return Value
	 */
	public Integer acCheck(final PlayerCharacter aPC) {
		int check = acCheck.intValue()
				+ (int) bonusTo(aPC, "EQMARMOR", "ACCHECK", true);

		if (check > 0) {
			check = 0;
		}

		return Integer.valueOf(check);
	}

	/**
	 * Returns true if the Equipment can take children.
	 * 
	 * @return true if the Equipment can take children.
	 */
	public boolean acceptsChildren() {
		return d_acceptsChildren;
	}

	/**
	 * Remove all equipment modifiers (EQMOD) from this equipment item.
	 */
	public void clearAllEqModifiers() {
		if (eqModifierList != null) {
			eqModifierList.clear();
		}
	}

	/**
	 * Add an equipment modifier and its associated information eg:
	 * Bane|Vermin|Fey eg: Keen Adds a feature to the EqModifier attribute of
	 * the Equipment object
	 * 
	 * @param aString
	 *            The feature to be added to the EqModifier attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifier attribute
	 */
	public void addEqModifier(final String aString, final boolean bPrimary) {
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		// The type of EqMod, eg: ABILITYPLUS
		final String eqModKey = aTok.nextToken();

		EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);

		// If not already attached, then add a new one
		if (eqMod == null) {
			if (eqModKey.equals(EQMOD_WEIGHT)) {
				if (aTok.hasMoreTokens()) {
					setWeightMod(aTok.nextToken().replace(',', '.'));
				}
				return;
			}

			if (eqModKey.equals(EQMOD_DAMAGE)) {
				if (aTok.hasMoreTokens()) {
					setDamageMod(aTok.nextToken());
				}
				return;
			}

			eqMod = EquipmentList.getModifierKeyed(eqModKey);

			if (eqMod == null) {
				Logging.errorPrint("Could not find EquipmentModifier: "
						+ eqModKey);

				return;
			}

			// only make a copy if we need to
			// add qualifiers to modifier
			if (eqMod.getChoiceString().length() != 0) {
				eqMod = eqMod.clone();
			}

			addToEqModifierList(eqMod, bPrimary);
		}

		// Add the associated choices
		if (eqMod.getChoiceString().length() != 0) {
			while (aTok.hasMoreTokens()) {
				final String x = aTok.nextToken();
				if (eqMod.getChoiceString().startsWith("EQBUILDER")) {
					// We clear the associated info to avoid a buildup of info
					// like number of charges.
					eqMod.clearAssociated();
				}
				eqMod.addAssociated(x.replace('=', '|'));
			}
		}
	}

	/**
	 * Adds a feature to the EqModifier attribute of the Equipment object. If a
	 * choice is required, a dialog will be displayed asking the user for the
	 * choice.
	 * 
	 * @param eqMod
	 *            The feature to be added to the EqModifier attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifier attribute
	 * @param aPC
	 *            The PC that the modifier is being added for.
	 */
	public void addEqModifier(final EquipmentModifier eqMod,
			final boolean bPrimary, final PlayerCharacter aPC) {
		addEqModifier(eqMod, bPrimary, aPC, null, null);
	}

	/**
	 * Adds a feature to the EqModifier attribute of the Equipment object. If a
	 * non-null selectedChoice is supplied, this method will not be interactive,
	 * and will not show a dialog if a choice is required. Instead, the provided
	 * value will be used.
	 * 
	 * @param eqMod
	 *            The feature to be added to the EqModifier attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifier attribute
	 * @param aPC
	 *            The PC that the modifier is being added for.
	 * @param selectedChoice
	 *            The choice to be used instead of asking the user, should a
	 *            choice be required.
	 * @param equipChoice
	 *            The details of the choice to be made. Used when there are
	 *            secondary options.
	 */
	public void addEqModifier(final EquipmentModifier eqMod,
			final boolean bPrimary, final PlayerCharacter aPC,
			final String selectedChoice, final EquipmentChoice equipChoice) {
		boolean bImporting = false;

		if ((aPC != null) && aPC.isImporting()) {
			bImporting = true;
		}

		if (!bImporting && !canAddModifier(eqMod, bPrimary)) {
			return;
		}

		//
		// Remove any modifiers that this one will replace
		//
		List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (int i = eqModList.size() - 1; i >= 0; --i) {
			final EquipmentModifier aMod = eqModList.get(i);

			if (eqMod.willReplace(aMod.getKeyName())) {
				eqModList.remove(i);
				if (bPrimary) {
					typeListCachePrimary = null;
				} else {
					typeListCacheSecondary = null;
				}
				setDirty(true);
			}
		}

		if (eqMod.isType("BaseMaterial")) {
			for (int i = eqModList.size() - 1; i >= 0; --i) {
				final EquipmentModifier aMod = eqModList.get(i);

				if (aMod.isType("BaseMaterial")) {
					eqModList.remove(i);
					if (bPrimary) {
						typeListCachePrimary = null;
					} else {
						typeListCacheSecondary = null;
					}
					setDirty(true);
				}
			}
		} else if (eqMod.isType("MagicalEnhancement")) {
			for (int i = eqModList.size() - 1; i >= 0; --i) {
				final EquipmentModifier aMod = eqModList.get(i);

				if (aMod.isType("MagicalEnhancement")) {
					eqModList.remove(i);
					if (bPrimary) {
						typeListCachePrimary = null;
					} else {
						typeListCacheSecondary = null;
					}
				}
			}
		}

		//
		// Add the modifier if it's not already there
		//
		EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(),
				bPrimary);

		if (aMod == null) {
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0) {
				aMod = eqMod.clone();

				if (aMod == null) {
					return;
				}
			} else {
				aMod = eqMod;
			}

			eqModList.add(aMod);
			if (bPrimary) {
				typeListCachePrimary = null;
			} else {
				typeListCacheSecondary = null;
			}
		}

		//
		// If a choice is required, either get a response from user or
		// apply the provided choice.
		// Remove the modifier if all associated choices are deleted
		//
		if (!bImporting) {
			boolean allRemoved = false;
			if (selectedChoice != null && selectedChoice.length() > 0) {
				if (!eqMod.getChoiceString().startsWith("EQBUILDER.")) {
					aMod.setChoice(selectedChoice, equipChoice);
					allRemoved = aMod.getAssociatedCount() == 0;
				}
			} else if (aMod.getChoice(1, this, true) == 0) {
				allRemoved = true;
			}

			if (allRemoved) {
				eqModList.remove(aMod);
				if (bPrimary) {
					typeListCachePrimary = null;
				} else {
					typeListCacheSecondary = null;
				}
			}
		}

		eqModList = Globals.sortPObjectListByName(eqModList);

		setBase(aPC);
	}

	/**
	 * Add a list equipment modifiers and their associated information eg:
	 * Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6 <p/> Adds a feature to the
	 * EqModifiers attribute of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be added to the EqModifiers attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifiers attribute
	 */
	public void addEqModifiers(final String aString, final boolean bPrimary) {
		final StringTokenizer aTok = new StringTokenizer(aString, ".");

		while (aTok.hasMoreTokens()) {
			final String aEqModName = aTok.nextToken();

			if (!aEqModName.equalsIgnoreCase(Constants.s_NONE)) {
				addEqModifier(aEqModName, bPrimary);
			}
		}

		List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);
		eqModList = Globals.sortPObjectListByName(eqModList);
	}

	/**
	 * Adds to the altTypeList attribute of the Equipment object
	 * 
	 * @param argAltType
	 *            The new altTypeList value
	 */
	public void addToAltTypeList(final String argAltType) {
		final String altType = argAltType.toUpperCase();
		final StringTokenizer aTok = new StringTokenizer(altType, ".");

		while (aTok.hasMoreTokens()) {
			final String type = aTok.nextToken();
			addAltType(type);
			typeListCachePrimary = null;
			typeListCacheSecondary = null;
		}
	}

	/**
	 * Adds to the virtual feat list this item bestows upon its weilder.
	 * 
	 * @param vList
	 *            a | delimited list of feats to add to the list
	 */
	public void addVFeatList(final String vList) {
		final StringTokenizer aTok = new StringTokenizer(vList, "|");

		while (aTok.hasMoreTokens()) {
			if (vFeatList == null) {
				vFeatList = new ArrayList<String>();
			}

			vFeatList.add(aTok.nextToken());
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC
	 * 
	 * @param aType
	 *            a TYPE of BONUS (such as "COMBAT" or "AC")
	 * @param aName
	 *            the NAME of the BONUS (such as "ATTACKS" or "ARMOR")
	 * @param bPrimary
	 *            should we ask the parent object also?
	 * @return returns a double which is the sum of all bonuses
	 */
	public double bonusTo(final PlayerCharacter aPC, final String aType,
			final String aName, final boolean bPrimary) {
		return bonusTo(aPC, aType, aName, this, bPrimary);
	}

	/**
	 * Add bonuses
	 * 
	 * @param aPC
	 * @param aType
	 * @param aName
	 * @param anObj
	 * @param bPrimary
	 * @return bonus
	 */
	public double bonusTo(final PlayerCharacter aPC, final String aType,
			final String aName, final Object anObj, final boolean bPrimary) {
		final String aBonusKey = new StringBuffer(aType.toUpperCase()).append(
				'.').append(aName.toUpperCase()).append('.').toString();

		// go through bonus hashmap and zero out all
		// entries that deal with this bonus request
		for (String aKey : getBonusMap().keySet()) {
			if (aKey.startsWith(aBonusKey)) {
				putBonusMap(aKey, "0");
			}
		}

		double iBonus = 0;
		bonusPrimary = bPrimary;

		if (bPrimary) {
			super.bonusTo(aType, aName, this, aPC);

			// now do temp bonuses
			final List<BonusObj> tbList = new ArrayList<BonusObj>();

			for (BonusObj aBonus : getTempBonusList()) {
				if (!tbList.contains(aBonus)) {
					tbList.add(aBonus);
				}
			}

			super.bonusTo(aType, aName, anObj, tbList, aPC);
		}

		// If using 3.5 weapon penalties, add them in also
		if (Globals.checkRule(RuleConstants.SYS_35WP)) {
			final List<BonusObj> aList = GameMode.getEqSizePenaltyObj()
					.getBonusList();
			super.bonusTo(aType, aName, this, aList, aPC);
		}

		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (EquipmentModifier eqMod : eqModList) {
			// Only add bonuses for items that are not ignored.
			// eg. Masterwork is ignored for Adamantine
			//
			if (!willIgnore(eqMod.getKeyName(), bPrimary)) {
				eqMod.bonusTo(aPC, aType, aName, this);
			}
		}

		for (String key : getBonusMap().keySet()) {
			if (key.startsWith(aBonusKey)) {
				iBonus += Float.parseFloat(getBonusMap().get(key));
			}
		}

		return iBonus;
	}

	/**
	 * Calculates the plusForCosting attribute of the Equipment object
	 * 
	 * @return The plusForCosting value
	 */
	public int calcPlusForCosting() {
		int iPlus = 0;
		int iCount;

		for (EquipmentModifier eqMod : eqModifierList) {
			iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			iPlus += (iCount * eqMod.getPlus());
		}

		for (EquipmentModifier eqMod : altEqModifierList) {
			iCount = eqMod.getAssociatedCount();

			if (iCount < 1) {
				iCount = 1;
			}

			iPlus += (iCount * eqMod.getPlus());
		}

		return iPlus;
	}

	/**
	 * Can we add eqMod to this equipment?
	 * 
	 * @param eqMod
	 *            The Equipment Modifier we would like to add
	 * @param bPrimary
	 *            whether adding to the primary or secondary head
	 * 
	 * @return True if eqMod is addable
	 */
	public boolean canAddModifier(final EquipmentModifier eqMod,
			final boolean bPrimary) {
		// Make sure we are qualified
		bonusPrimary = bPrimary;

		if (!modifiersAllowed || !eqMod.passesPreReqToGain(this, null)) {
			return false;
		}

		// Don't allow adding of modifiers with %CHOICE cost to secondary head,
		// as
		// cost is only calculated for these modifiers on primary head

		// if (!bPrimary && (eqMod.getCost().indexOf("%CHOICE") >= 0))
		// {
		// return false;
		// }

		return true;
	}

	/**
	 * Returns 0 on object error, 1 on can fit, 2 on too heavy, 3 on properties
	 * problem (unimplemented), 4 on capacity error
	 * 
	 * @param aPC
	 * 
	 * @param obj
	 *            The equipment to check
	 * @return 0 on object error, 1 on can fit, 2 on too heavy, 3 on properties
	 *         problem (unimplemented), 4 on capacity error
	 */
	public int canContain(final PlayerCharacter aPC, final Object obj) {
		if (obj instanceof Equipment) {
			final Equipment anEquip = (Equipment) obj;

			if (checkChildWeight(aPC, new Float(anEquip.getWeightAsDouble(aPC)
					* anEquip.numberCarried().floatValue()))) {
				// canHold(my HashMap())) //quick hack since the properties
				// hashmap doesn't exist
				if (checkContainerCapacity(anEquip.eqTypeList(), anEquip
						.numberCarried())) {
					// the qty value is a temporary hack - insert all or
					// nothing. should reset person to be a container, with
					// capacity=capacity
					return 1;
				}
				return 4;
			}
			return 2;
		}

		return 0;
	}

	/**
	 * Clears all child types
	 */
	public void clearChildTypes() {
		d_childTypes = null;
	}

	/**
	 * Removes all items from this container.
	 */
	public void clearContainedEquipment() {
		d_containedEquipment = null;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	@Override
	public Equipment clone() {
		Equipment eq = null;

		try {
			eq = (Equipment) super.clone();

			// set DR
			eq.seteDR(eDR.toString());
			eq.setACCheck(acCheck.toString());
			eq.setSpellFailure(spellFailure.toString());
			eq.setMaxDex(maxDex.toString());

			if (theWeaponStats != null) {
				eq.theWeaponStats = theWeaponStats.clone();
				eq.theWeaponStats.setOwner(eq);
			}

			//
			eq.setMoveString(moveString());

			// eq.setTypeString(super.getType());
			// none of the types associated with modifiers
			eq.profName = profName;
			eq.carried = carried;
			eq.equipped = equipped;
			eq.location = location;
			eq.slots = slots;
			eq.bonusType = bonusType;
			eq.numberEquipped = numberEquipped;
			eq.qty = qty;
			eq.outputIndex = outputIndex;
			eq.containerWeightCapacity = containerWeightCapacity;
			eq.containerReduceWeight = containerReduceWeight;
			eq.d_acceptsChildren = d_acceptsChildren;

			if (d_acceptsTypes != null) {
				eq.d_acceptsTypes = new HashMap<String, Float>(d_acceptsTypes);
			}

			eq.containerConstantWeight = containerConstantWeight;

			if (d_childTypes != null) {
				eq.d_childTypes = new HashMap<String, Float>(d_childTypes);
			}

			eq.containerContentsString = containerContentsString;
			eq.containerCapacityString = containerCapacityString;

			if (d_containedEquipment != null) {
				eq.d_containedEquipment = new ArrayList<Equipment>(
						d_containedEquipment);
			}

			eq.eqModifierList = cloneEqModList(true);
			eq.altEqModifierList = cloneEqModList(false);
			eq.modifiersAllowed = modifiersAllowed;
			eq.modifiersRequired = modifiersRequired;

			// Make sure any lists aren't shared
			eq.specialPropertyList = new ArrayList<SpecialProperty>();
			eq.specialPropertyList.addAll(specialPropertyList);
		} catch (CloneNotSupportedException e) {
			ShowMessageDelegate.showMessageDialog(e.getMessage(),
					Constants.s_APPNAME, MessageType.ERROR);
		}

		return eq;
	}

	/**
	 * Description of the Method
	 * 
	 * @param o
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public int compareTo(final Object o) {
		final Equipment e = (Equipment) o;

		return getName().compareToIgnoreCase(e.getName());
	}

	/**
	 * contains
	 * 
	 * @param e
	 * 
	 * @return true if containedEquipment contains the passed item
	 */
	public boolean containsContainedEquipment(final Equipment e) {
		if (d_containedEquipment == null) {
			return false;
		}

		return d_containedEquipment.contains(e);
	}

	/**
	 * DR for equipment
	 * 
	 * @param aPC
	 * @return Integer
	 */
	public Integer eDR(final PlayerCharacter aPC) {
		int check = eDR.intValue()
				+ (int) bonusTo(aPC, "EQMARMOR", "EDR", true);

		if (check < 0) {
			check = 0;
		}

		return Integer.valueOf(check);
	}

	/**
	 * Description of the Method
	 * 
	 * @param o
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean equals(final Object o) {
		return (o != null) && (o instanceof Equipment)
				&& ((o == this) || getName().equals(((Equipment) o).getName()));
	}

	/**
	 * Build a String used to save this items special properties in a .pcg file
	 * 
	 * @param sep
	 * @param endPart
	 * @return String
	 */
	public String formatSaveLine(final char sep, final char endPart) {
		final StringBuffer sbuf = new StringBuffer(100);

		final Equipment base;

		if (baseItem.length() != 0) {
			base = EquipmentList.getEquipmentNamed(baseItem);
			sbuf.append(baseItem);
			sbuf.append(sep).append("NAME").append(endPart).append(
					toString(false));
		} else {
			base = this;
			sbuf.append(getBaseItemName());
		}

		if (base == null) {
			return sbuf.toString();
		}

		// When you customise a piece of equipment using the customiser, it sets
		// the keyName equal to the Name. The autoresizer doesn't do that, it
		// makes a new key. This is to cope with the auto resizer.

		if (!this.getKeyName().equals(this.getName())) {
			sbuf.append(sep).append("KEY").append(endPart).append(
					this.getKeyName());
		}

		if (!size.equals(base.getSize())) {
			sbuf.append(sep).append("SIZE").append(endPart).append(size);
		}

		String aString = getEqModifierString(true); // key1.key2|assoc1|assoc2.key3.key4

		if (aString.length() > 0) {
			sbuf.append(sep).append("EQMOD").append(endPart).append(aString);
		}

		aString = getEqModifierString(false); // key1.key2|assoc1|assoc2.key3.key4

		if (aString.length() > 0) {
			sbuf.append(sep).append("ALTEQMOD").append(endPart).append(aString);
		}

		aString = getRawSpecialProperties();

		if ((aString.length() > 0)
				&& !aString.equals(base.getRawSpecialProperties())) {
			sbuf.append(sep).append("SPROP").append(endPart).append(aString);
		}

		if (!costMod.equals(BigDecimal.ZERO)) {
			sbuf.append(sep).append("COSTMOD").append(endPart).append(
					costMod.toString());
		}

		return sbuf.toString();
	}

	/**
	 * Has virtual feats
	 * 
	 * @return true if it has virtual feats
	 */
	public boolean hasVFeats() {
		final List<Ability> vFeats = getVirtualFeatList();

		return (vFeats != null) && (vFeats.size() > 0);
	}

	/**
	 * Returns TRUE true if it is weildable
	 * 
	 * @return true if it is weildable
	 */
	public boolean hasWield() {
		if (theWeaponStats != null) {
			return theWeaponStats.hasWield();
		}
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Gets the index of a child
	 * 
	 * @param child
	 *            The child
	 * @return the index of the child
	 */
	public int indexOfChild(final Object child) {
		if (!(child instanceof Equipment)) {
			return -1;
		}

		return getContainedEquipmentIndexOf((Equipment) child);
	}

	/**
	 * Adds a child to this Equipment
	 * 
	 * TODO Why does it accept an Object
	 * 
	 * @param aPC
	 * @param child
	 *            The child to add
	 */
	public void insertChild(final PlayerCharacter aPC, final Object child) {
		if (child == null) {
			return;
		}

		Equipment anEquip = (Equipment) child;
		Float aFloat = anEquip.numberCarried();
		Float bFloat = aFloat;

		final String aString = pickChildType(anEquip.eqTypeList(), aFloat);

		if (containsChildType(aString)) {
			aFloat = new Float(getChildType(aString).floatValue()
					+ aFloat.floatValue());
		}

		bFloat = new Float(getChildType("Total").floatValue()
				+ bFloat.floatValue());
		setChildType(aString, aFloat);
		setChildType("Total", bFloat);
		addContainedEquipment(anEquip);
		anEquip.setIndexedUnderType(aString);
		anEquip.setParent(this);

		// hmm probably not needed; but as it currently isn't hurting
		// anything...
		updateContainerContentsString(aPC);

		while (anEquip.getParent() != null) {
			anEquip = (Equipment) anEquip.getParent();
			anEquip.updateContainerContentsString(aPC);
		}
	}

	/**
	 * Returns how 'deep' in a structure an Equipment is.
	 * 
	 * @return how 'deep' in a structure an Equipment is.
	 */
	public int itemDepth() {
		if (getParent() == null) {
			return 0;
		}

		int i = 1;
		Equipment anEquip = (Equipment) getParent();

		while (anEquip.getParent() != null) {
			anEquip = (Equipment) anEquip.getParent();
			++i;
		}

		return i;
	}

	/**
	 * Load
	 * 
	 * @param aLine
	 */
	public void load(final String aLine) {
		load(aLine, "\t", ":");
	}

	/**
	 * Load
	 * 
	 * @param aLine
	 * @param sep
	 * @param endPart
	 */
	public void load(final String aLine, final String sep, final String endPart) {
		load(aLine, sep, endPart, null);
	}

	/**
	 * load
	 * 
	 * @param aLine
	 * @param sep
	 * @param endPart
	 * @param aPC
	 */
	public void load(final String aLine, final String sep,
			final String endPart, final PlayerCharacter aPC) {
		final StringTokenizer aTok = new StringTokenizer(aLine, sep);
		final int endPartLen = endPart.length();
		String newSize = size;
		baseItem = getKeyName();

		while (aTok.hasMoreTokens()) {
			final String aString = aTok.nextToken();

			if (aString.startsWith("NAME" + endPart)) {
				setName(aString.substring(4 + endPartLen));
			} else if (aString.startsWith("KEY" + endPart)) {
				setKeyName(aString.substring(3 + endPartLen));
			} else if (aString.startsWith("SIZE" + endPart)) {
				newSize = aString.substring(4 + endPartLen);
			} else if (aString.startsWith("EQMOD" + endPart)) {
				addEqModifiers(aString.substring(5 + endPartLen), true);
			} else if (aString.startsWith("ALTEQMOD" + endPart)) {
				addEqModifiers(aString.substring(8 + endPartLen), false);
			} else if (aString.startsWith("SPROP" + endPart)) {
				addSpecialProperty(SpecialProperty.createFromLst(aString
						.substring(5 + endPartLen)));
			} else if (aString.startsWith("COSTMOD" + endPart)) {
				setCostMod(aString.substring(7 + endPartLen));
			} else if (aString.startsWith("WEIGHTMOD" + endPart)) {
				setWeightMod(aString.substring(9 + endPartLen));
			}
		}
		resizeItem(aPC, newSize);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public String longName() {
		if (longName.length() == 0) {
			return toString();
		}

		return longName;
	}

	/**
	 * Description of the Method
	 * 
	 * @param currentPC
	 * 
	 * @return Description of the Return Value
	 */
	public boolean meetsPreReqs(PlayerCharacter currentPC) {
		return PrereqHandler.passesAll(this.getPreReqList(), this, currentPC);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public String modifiedName() {
		return modifiedName;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public String moveString() {
		if (moveString.length() > 0) {
			final Load eqLoad;

			if (isHeavy()) {
				eqLoad = Load.HEAVY;
			} else if (isMedium()) {
				eqLoad = Load.MEDIUM;
			} else if (isLight()) {
				eqLoad = Load.LIGHT;
			} else {
				eqLoad = Load.OVERLOAD;
			}

			//
			// This will generate a list for base moves 30,20
			// or 60,50,40 depending on how many tokens are
			// in the original tag
			//
			final StringTokenizer aTok = new StringTokenizer(moveString, ",");
			int baseMove = -1;
			int tokenCount = aTok.countTokens();

			switch (tokenCount) {
			case 2:
				baseMove = 30;

				break;

			case 3:
				baseMove = 60;

				break;

			default:
				tokenCount = -1;

				break;
			}

			if (tokenCount > 0) {
				final StringBuffer retString = new StringBuffer(moveString
						.length());

				for (int i = 0; i < tokenCount; ++i) {
					if (i != 0) {
						retString.append(',');
					}

					retString.append(Globals.calcEncumberedMove(eqLoad,
							baseMove, true));
					baseMove -= 10;
				}

				return retString.toString();
			}
		}

		return moveString;
	}

	/**
	 * ???
	 * 
	 * @param aPC
	 * 
	 * @return ???
	 */
	public String nameItemFromModifiers(final PlayerCharacter aPC) {
		final String itemName = getItemNameFromModifiers();
		cleanTypes(aPC);
		setName(itemName);
		setOutputName("");

		return getName();
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public Float numberCarried() {
		Equipment eqParent = (Equipment) getParent();

		if (isEquipped() || (eqParent == null)) {
			return carried;
		}

		for (; eqParent != null; eqParent = (Equipment) eqParent.getParent()) {
			if (eqParent.isEquipped()
					|| ((eqParent.getParent() == null) && (eqParent
							.numberCarried().intValue() != 0))) {
				return carried;
			}
		}

		return Float.valueOf(0);
	}

	/**
	 * Returns the name of the proficiency required to use this weapon
	 * 
	 * @param aPC
	 *            The PlayerCharacter wielding the weapon
	 * 
	 * @return The string name of the weapon proficiency required.
	 */
	public String profKey(final PlayerCharacter aPC) {
		try {
			if (isWeapon()) {
				WeaponProf wp = getExpandedWeaponProf(aPC);
				if (wp != null) {
					return wp.getKeyName();
				}
				Logging
						.debugPrint("Could not find weapon proficiency with key '"
								+ profName + "'.");
			}
			if (profName == null || profName.length() == 0) {
				// For autogenerated proficiencies return the display name since
				// we don't have anything else to return
				return this.getDisplayName();
			}
		} catch (RuntimeException e) {
			Logging.errorPrint("Problem with: " + this.getDisplayName(), e);
		}
		return profName;
	}

	/**
	 * Get the quantity of items
	 * 
	 * @return Description of the Return Value
	 */
	public double qty() {
		return qty;
	}

	/**
	 * Removes a child from the Equipment
	 * 
	 * @param aPC
	 * 
	 * @param child
	 *            The child to remove
	 */
	public void removeChild(final PlayerCharacter aPC, final Object child) {
		final int i = indexOfChild(child);
		Equipment anEquip = (Equipment) child;
		final Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", new Float(getChildType("Total").floatValue()
				- qtyRemoved.floatValue()));

		final String aString = anEquip.isIndexedUnderType();
		setChildType(aString, new Float(getChildType(aString).floatValue()
				- qtyRemoved.floatValue()));
		anEquip.setParent(null);
		removeContainedEquipment(i);
		updateContainerContentsString(aPC);
		anEquip = this;

		while (anEquip.getParent() != null) {
			anEquip = (Equipment) anEquip.getParent();
			anEquip.updateContainerContentsString(aPC);
		}
	}

	/**
	 * Removes a child from the Equipment
	 * 
	 * @param aPC
	 * 
	 * @param childIndex
	 *            The number of the child to remove
	 */
	public void removeChild(final PlayerCharacter aPC, final int childIndex) {
		removeChild(aPC, getChild(childIndex));
	}

	/**
	 * Description of the Method
	 * 
	 * @param eqMod
	 *            Description of the Parameter
	 * @param bPrimary
	 *            Description of the Parameter
	 */
	public void removeEqModifier(final EquipmentModifier eqMod,
			final boolean bPrimary) {
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);
		final EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(),
				bPrimary);

		if (aMod == null) {
			return;
		}

		// Get a response from user (if one required)
		// Remove the modifier if all associated choices are deleted
		if ((aMod.getAssociatedCount() == 0)
				|| (aMod.getChoice(0, this, false) == 0)) {
			eqModList.remove(aMod);
			if (bPrimary) {
				typeListCachePrimary = null;
			} else {
				typeListCacheSecondary = null;
			}

			if (false)
				removeUnqualified(bPrimary); // TODO: used?
			setDirty(true);
		}
	}

	/**
	 * Remove a list equipment modifiers and their associated information eg:
	 * Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6 <p/> Removes a feature
	 * from the EqModifiers attribute of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be removed from the EqModifiers attribute
	 * @param bPrimary
	 *            The feature to be removed from the EqModifiers attribute
	 */
	public void removeEqModifiers(final String aString, final boolean bPrimary) {
		final StringTokenizer aTok = new StringTokenizer(aString, ".");

		while (aTok.hasMoreTokens()) {
			final String aEqModName = aTok.nextToken();

			if (!aEqModName.equalsIgnoreCase(Constants.s_NONE)) {
				removeEqModifier(aEqModName, bPrimary);
			}
		}
	}

	/**
	 * Change the size of an item
	 * 
	 * @param aPC
	 * 
	 * @param newSize
	 *            the new size for the item
	 */
	public void resizeItem(final PlayerCharacter aPC, final String newSize) {
		setBase(aPC);

		final int iOldSize = sizeInt();
		int iNewSize = Globals.sizeInt(newSize);

		if (iNewSize != iOldSize) {
			setSize(newSize);

			final Equipment eq = EquipmentList.getEquipmentKeyed(baseItem);

			if (eq != null) {
				setCost(eq.getCostAdjustedForSize(aPC, newSize).toString());
				setWeight(eq.getWeightAdjustedForSize(aPC, newSize).toString());
				adjustACForSize(aPC, eq, newSize);
				if (eq.theWeaponStats != null) {
					setDamage(eq.theWeaponStats
							.getDamageAdjustedForSize(newSize));
					setAltDamage(eq.theWeaponStats
							.getAltDamageAdjustedForSize(newSize));
				}
				//
				// Adjust the capacity of the container (if it is one)
				//
				if (containerCapacityString.length() > 0) {
					double mult = 1.0;
					final SizeAdjustment newSA = SettingsHandler.getGame()
							.getSizeAdjustmentNamed(newSize);

					if (newSA != null) {
						mult = newSA.getBonusTo(aPC, "ITEMCAPACITY", eq
								.typeList(), 1.0);
					}

					if (containerWeightCapacity.intValue() != -1) {
						containerWeightCapacity = new Float(
								eq.containerWeightCapacity.doubleValue() * mult);
					}

					if (getAcceptsTypeCount() > 0) {
						for (String aString : eq.d_acceptsTypes.keySet()) {
							Float aWeight = eq.getAcceptsType(aString);

							if (aWeight.intValue() != -1) {
								aWeight = new Float(aWeight.doubleValue()
										* mult);
								setAcceptsType(aString, aWeight);
							}
						}
					}

					updateContainerCapacityString();
				}
			}

			//
			// Since we've just resized the item, we need to modify any PRESIZE
			// prerequisites
			//
			if (hasPreReqs())
			{
				for (Prerequisite aBonus : getPreReqList()) {
					if ("SIZE".equalsIgnoreCase(aBonus.getKind())) {
						final int iOldPre = Globals.sizeInt(aBonus.getOperand());
						iNewSize += (iOldPre - iOldSize);

						if ((iNewSize >= 0)
								&& (iNewSize <= (SettingsHandler.getGame()
										.getSizeAdjustmentListSize() - 1))) {
							// Note: This actually impacts the Prereq in this
							// Equipment, since it is returned
							// by reference from the get above ... thus no need to
							// perform a set
							aBonus.setOperand(SettingsHandler.getGame()
									.getSizeAdjustmentAtIndex(iNewSize)
									.getAbbreviation());
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the acCheck attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new acCheck value
	 */
	public void seteDR(final String aString) {
		try {
			eDR = Integer.valueOf(aString);
		} catch (NumberFormatException nfe) {
			eDR = Integer.valueOf(0);

			// ignore
		}
	}

	/**
	 * Get the int size of the Equipment object
	 * 
	 * @return size as int
	 */
	public int sizeInt() {
		return Globals.sizeInt(getSize());
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC
	 * 
	 * @return Description of the Return Value
	 */
	public Integer spellFailure(final PlayerCharacter aPC) {
		int fail = spellFailure.intValue()
				+ (int) bonusTo(aPC, "EQMARMOR", "SPELLFAILURE", true);

		if (fail < 0) {
			fail = 0;
		}

		return Integer.valueOf(fail);
	}

	/**
	 * Returns the Equipment as a String
	 * 
	 * @return the Equipment as a String
	 */
	public String toString() {
		return toString(true);
	}

	/**
	 * toString
	 * 
	 * @param addCharges
	 * @return String
	 */
	public String toString(final boolean addCharges) {
		if (isDirty()
				|| (cachedNameWithCharges == null && cachedNameWithoutCharges == null)) {
			// If we have modified the equipment details with
			// respect to the name then rebuid the names
			final StringBuffer buffer = new StringBuffer(displayName);

			if (modifiedName.length() > 0) {
				buffer.append(" (").append(modifiedName).append(")");
			}
			cachedNameWithoutCharges = buffer.toString();

			if (addCharges && (getRemainingCharges() > getMinCharges())
					&& (getRemainingCharges() < getMaxCharges())) {
				buffer.append("(").append(getRemainingCharges()).append(")");
			}
			cachedNameWithCharges = buffer.toString();
			setDirty(false);
		}

		// Return the cached names.
		if (addCharges) {
			return cachedNameWithCharges;
		}
		return cachedNameWithoutCharges;
	}

	private boolean isDirty() {
		return dirty;
	}

	private void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Returns the type with the requested index
	 * 
	 * @param index
	 *            the index
	 * @return the type with the requested index
	 */
	public String typeIndex(final int index) {
		final List<String> tList = typeList();

		if ((index < 0) || (index >= tList.size())) {
			return "";
		}

		return tList.get(index);
	}

	/**
	 * Returns a list of the types of this item.
	 * 
	 * @return a list of the types of this item.
	 */
	public List<String> typeList() {
		return typeList(true);
	}

	/**
	 * Update the container contents String
	 */
	public void updateContainerContentsString() {
		containerContentsString = "";

		final StringBuffer tempStringBuffer = new StringBuffer(
				getChildCount() * 20);

		// Make sure there's no bug here.
		if (acceptsChildren()
				&& (getBaseContainedWeight(true).floatValue() >= 0.0f)) {
			tempStringBuffer.append(
					Globals.getGameModeUnitSet().displayWeightInUnitSet(
							getBaseContainedWeight(true).doubleValue()))
					.append(Globals.getGameModeUnitSet().getWeightUnit());
		} else {
			// have to put something
			tempStringBuffer.append("0.0 ");
			tempStringBuffer.append(Globals.getGameModeUnitSet()
					.getWeightUnit());
		}

		for (int e = 0; e < getChildCount(); ++e) {
			final Equipment anEquip = (Equipment) getChild(e);

			if (anEquip.getQty().floatValue() > 0.0f) {
				tempStringBuffer.append(", ");
				tempStringBuffer.append(BigDecimalHelper.trimZeros(anEquip
						.getQty().toString()));
				tempStringBuffer.append(" ");
				tempStringBuffer.append(anEquip);
			}
		}

		containerContentsString = tempStringBuffer.toString();
	}

	/**
	 * Updates the containerContentsString from children of this item
	 * 
	 * @param aPC
	 */
	public void updateContainerContentsString(final PlayerCharacter aPC) {
		containerContentsString = "";

		final StringBuffer tempStringBuffer = new StringBuffer(
				getChildCount() * 20);

		// Make sure there's no bug here.
		if (aPC != null && acceptsChildren()
				&& (getContainedWeight(aPC, true).floatValue() >= 0.0f)) {
			tempStringBuffer.append(
					Globals.getGameModeUnitSet().displayWeightInUnitSet(
							getContainedWeight(aPC, true).doubleValue()))
					.append(Globals.getGameModeUnitSet().getWeightUnit());
		} else {
			// have to put something
			tempStringBuffer.append("0.0 ");
			tempStringBuffer.append(Globals.getGameModeUnitSet()
					.getWeightUnit());
		}

		for (int e = 0; e < getChildCount(); ++e) {
			final Equipment anEquip = (Equipment) getChild(e);

			if (anEquip.getQty().floatValue() > 0.0f) {
				tempStringBuffer.append(", ");
				tempStringBuffer.append(BigDecimalHelper.trimZeros(anEquip
						.getQty().toString()));
				tempStringBuffer.append(" ");
				// karianna os bug 1414564
				tempStringBuffer.append(anEquip.getOutputName());
			}
		}

		containerContentsString = tempStringBuffer.toString();
	}

	protected void doGlobalTypeUpdate(final String aString) {
		s_equipmentTypes.add(aString);
	}

	/**
	 * @param aPC
	 */
	private void setDefaultCrit(final PlayerCharacter aPC) {
		if (isWeapon()) {
			if (getCritRange(aPC).length() == 0) {
				setCritRange("1");
			}

			if (getCritMult().length() == 0) {
				setCritMult(2);
			}
		}
	}

	/**
	 * Set quantity
	 * 
	 * @param argQty
	 */
	public void setQty(final double argQty) {
		qty = argQty;
	}

	/**
	 * Description of the Method
	 */
	static void clearEquipmentTypes() {
		s_equipmentTypes.clear();
	}

	/**
	 * Gets the modifiersAllowed attribute of the Equipment object.
	 * 
	 * @return The modifiersAllowed value
	 */
	boolean getModifiersAllowed() {
		return modifiersAllowed;
	}

	/**
	 * Get the type list as a period-delimited string
	 * 
	 * @param bPrimary
	 *            ???
	 * @return The type value
	 */
	String getType(final boolean bPrimary) {
		final List<String> typeList = typeList(bPrimary);
		final int typeSize = typeList.size();
		final StringBuffer aType = new StringBuffer(typeSize * 5); // Just a
																	// guess.

		for (String s : typeList) {
			if (aType.length() != 0) {
				aType.append('.');
			}

			aType.append(s);
		}

		return aType.toString();
	}

	boolean equalTo(final Object o) {
		return super.equals(o);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	String rawProfName() {
		return profName;
	}

	boolean save(final BufferedWriter output) {
		FileAccess.write(output, "BASEITEM:" + formatSaveLine('\t', ':'));
		FileAccess.newLine(output);

		return true;
	}

	/**
	 * Sets the acceptence of a type
	 * 
	 * @param parameter
	 *            Description of the Parameter
	 * @param acceptsType
	 *            Acceptance
	 */
	private void setAcceptsType(final String parameter, final Float acceptsType) {
		if (d_acceptsTypes == null) {
			d_acceptsTypes = new HashMap<String, Float>();
		}

		d_acceptsTypes.put(parameter.toUpperCase(), acceptsType);
	}

	/**
	 * Gets the acceptsTypes attribute of the Equipment object
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return The acceptsTypes value
	 */
	private Float getAcceptsType(final String aString) {
		if (d_acceptsTypes == null) {
			return null;
		}

		return d_acceptsTypes.get(aString.toUpperCase());
	}

	/**
	 * Gets the number of accepted types
	 * 
	 * @return The number of distinct types
	 */
	private int getAcceptsTypeCount() {
		if (d_acceptsTypes == null) {
			return 0;
		}

		return d_acceptsTypes.size();
	}

	private int getAltTypeCount() {
		if (altTypeList == null) {
			return 0;
		}

		return altTypeList.size();
	}

	/**
	 * Sets the base attribute of the Equipment object
	 * 
	 * @param aPC
	 */
	private void setBase(final PlayerCharacter aPC) {
		if (baseItem.length() == 0) {
			baseItem = getKeyName();
		}

		if (profName.length() == 0) {
			final Equipment eq = EquipmentList.getEquipmentKeyed(baseItem);

			if (eq != null) {
				profName = eq.rawProfName();

				if (profName.length() == 0) {
					profName = eq.getName();
				}
			}
		}

		// Scan through the modifiers checking for one that requires
		// an item have a different weapon proficiency
		EquipmentModifier profType = null;

		for (EquipmentModifier eqMod : eqModifierList) {
			final String aString = eqMod.getProficiency();

			if (aString.length() != 0) {
				profType = eqMod;

				break;
			}
		}

		// If we haven't found one yet, check the secondary head
		if (profType == null) {
			for (EquipmentModifier eqMod : altEqModifierList) {
				final String aString = eqMod.getProficiency();

				if (aString.length() != 0) {
					profType = eqMod;

					break;
				}
			}
		}

		// If we've found a modifier that requires a different weapon
		// prof, then generate the proficiency's name
		if (profType != null) {
			final List<String> profTypeInfo = CoreUtility.split(profType
					.getProficiency(), '.');

			if (profTypeInfo.size() == 2) {
				final StringBuffer proficiencyName = new StringBuffer(profName);

				if (profName.endsWith(")")) {
					proficiencyName.setLength(proficiencyName.length() - 1);
					proficiencyName.append('/');
				} else {
					proficiencyName.append(" (");
				}

				// Only add if not already there
				if (!proficiencyName.toString().endsWith(
						profTypeInfo.get(1) + '/')) {
					proficiencyName.append(profTypeInfo.get(1));
				} else {
					proficiencyName.setLength(proficiencyName.length() - 1);
				}

				proficiencyName.append(')');
				profName = proficiencyName.toString();

				//
				// Strip out the [Hands] variable as, according
				// to the so-called sage,
				// "if someone can wield a weapon 1 handed
				// they can always wield it 2 handed"
				// so we'll just force them to take the
				// 1-handed variety.
				final int iOffs = profName.indexOf("[Hands]");

				if (iOffs >= 0) {
					profName = profName.substring(0, iOffs)
							+ profName.substring(iOffs + 7);
				}

				WeaponProf wp = Globals.getWeaponProfKeyed(profName);

				if (wp == null) {
					wp = new WeaponProf();
					wp.setName(profName);
					wp.setKeyName(profName);
					wp.setTypeInfo(profTypeInfo.get(0));
					setDefaultCrit(aPC);
					Globals.addWeaponProf(wp);
				}
			}
		}

		if (getSize().length() == 0) {
			setSize("M");
		}
	}

	/**
	 * Gets the baseCost attribute of the Equipment object
	 * 
	 * @return The baseCost value
	 */
	BigDecimal getBaseCost() {
		return baseCost;
	}

	/**
	 * Gets the baseSize attribute of the Equipment object
	 * 
	 * @return The baseSize value
	 */
	private String getBaseSize() {
		return sizeBase;
	}

	/**
	 * Gets the acceptsTypes attribute of the Equipment object
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return The acceptsTypes value
	 */
	private Float getChildType(final String aString) {
		if (d_childTypes == null) {
			return null;
		}

		return d_childTypes.get(aString);
	}

	/**
	 * accessor
	 * 
	 * @param e
	 * 
	 * @return index of containedEquipment object
	 */
	private int getContainedEquipmentIndexOf(final Equipment e) {
		if (d_containedEquipment == null) {
			return -1;
		}

		return d_containedEquipment.indexOf(e);
	}

	/**
	 * @param aPC
	 * @param aSize
	 *            The size to adjust for
	 * @return The costAdjustedForSize value
	 */
	private BigDecimal getCostAdjustedForSize(final PlayerCharacter aPC,
			final String aSize) {
		BigDecimal c = getBaseCost();

		//
		// Scale everything to medium before conversion
		//
		final SizeAdjustment saSize = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(aSize);
		final SizeAdjustment saBase = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(getBaseSize());

		if ((saSize == null) || (saBase == null)) {
			return c;
		}

		final double saDbl = saSize
				.getBonusTo(aPC, "ITEMCOST", typeList(), 1.0);
		final double saBaseDbl = saBase.getBonusTo(aPC, "ITEMCOST", typeList(),
				1.0);
		final double mult = saDbl / saBaseDbl;
		c = c.multiply(new BigDecimal(mult));

		//
		// TODO:Non-humanoid races can also double the cost (armor)
		//
		return c;
	}

	/**
	 * return the list of modifier keys as a period-delimeted string
	 * 
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The eqModifierString value
	 */
	private String getEqModifierString(final boolean bPrimary) {
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);
		final StringBuffer aString = new StringBuffer(eqModList.size() * 10);

		for (EquipmentModifier eqMod : eqModList) {
			if (aString.length() != 0) {
				aString.append('.');
			}

			aString.append(eqMod.getKeyName());

			// Add the modifiers
			for (int e2 = 0; e2 < eqMod.getAssociatedCount(); ++e2) {
				final String strMod = eqMod.getAssociated(e2);
				aString.append('|').append(strMod.replace('|', '='));
			}
		}

		if (bPrimary && (weightMod.compareTo(BigDecimal.ZERO) != 0)) {
			if (aString.length() != 0) {
				aString.append('.');
			}
			aString.append(EQMOD_WEIGHT).append('|').append(
					weightMod.toString().replace('.', ','));
		}

		if (theWeaponStats != null) {
			if (theWeaponStats.getDamageMod().length() != 0) {
				if (aString.length() != 0) {
					aString.append('.');
				}
				aString.append(EQMOD_DAMAGE).append('|').append(
						theWeaponStats.getDamageMod().replace('.', ','));
			}
		}
		return aString.toString();
	}

	/**
	 * @param aString
	 */
	private void setIndexedUnderType(final String aString) {
		indexedUnderType = aString;
	}

	/**
	 * Gets the indexedUnderType attribute of the Equipment object
	 * 
	 * @return The indexedUnderType value
	 */
	private String isIndexedUnderType() {
		return indexedUnderType;
	}

	/**
	 * Look for a modifier that grants type "magic"
	 * 
	 * @param eqModList
	 *            Description of the Parameter
	 * @return The magicBonus value
	 */
	private static EquipmentModifier getMagicBonus(
			final List<EquipmentModifier> eqModList) {
		for (EquipmentModifier eqMod : eqModList) {
			if (eqMod.isType("MagicalEnhancement") || (eqMod.isIType("Magic"))) {
				return eqMod;
			}
		}

		return null;
	}

	/**
	 * Gets the nameFromModifiers attribute of the Equipment object
	 * 
	 * @param eqModList
	 *            The list of modifiers
	 * @return The nameFromModifiers value
	 */
	private static String getNameFromModifiers(
			final List<EquipmentModifier> eqModList) {
		//
		// Get a sorted list so that the description will always come
		// out the same reguardless of the order we've added the modifiers
		//
		final List<EquipmentModifier> eqList = new ArrayList<EquipmentModifier>(
				eqModList);
		Globals.sortPObjectList(eqList);

		final StringBuffer sMod = new StringBuffer(70);

		for (EquipmentModifier eqMod : eqList) {
			if (sMod.length() != 0) {
				sMod.append('/');
			}

			sMod.append(eqMod.getEquipNamePortion());
		}

		return sMod.toString();
	}

	/**
	 * Sets the size attribute of the Equipment object
	 * 
	 * @param sizeString
	 *            The new size value
	 */
	private void setSize(final String sizeString) {
		setSize(sizeString, false);
	}

	/**
	 * Gets the specialAbilityList attribute of the Equipment object
	 * 
	 * @param eqModList
	 *            Description of the Parameter
	 * @param pc
	 * @return The specialAbilityList value
	 */
	private List<String> getSpecialAbilityList(
			final List<EquipmentModifier> eqModList, final PlayerCharacter pc) {
		final List<String> saList = new ArrayList<String>();

		for (EquipmentModifier eqMod : eqModList) {
			saList.addAll(eqMod.getSpecialProperties(this, pc));
		}

		return saList;
	}

	/**
	 * Tack on the cost of the magical enhancement(s).
	 * 
	 * @param iPlus
	 * @param altPlus
	 * @return cost from pluses
	 */
	private BigDecimal getCostFromPluses(final int iPlus, final int altPlus) {
		if (((iPlus != 0) || (altPlus != 0))
				&& (JEPResourceChecker.getMissingResourceCount() == 0)) {
			PJEP myParser = null;
			try {
				myParser = PjepPool.getInstance().aquire();
				myParser.addVariable("PLUS", iPlus);
				myParser.addVariable("ALTPLUS", altPlus);
				myParser.addVariable("BASECOST", getBaseCost().doubleValue());

				if (isAmmunition()) {
					myParser.addVariable("BASEQTY", getBaseQty());
				}

				String typeMatched;

				// Look for an expression for all of this item's types
				// If there is more than 1, use the most expensive.
				String costExpr;
				BigDecimal maxCost = null;
				final List<String> itemTypes = typeList();

				for (int idx = 0; idx < itemTypes.size(); ++idx) {
					typeMatched = itemTypes.get(idx);
					costExpr = SettingsHandler.getGame().getPlusCalculation(
							typeMatched);

					if (costExpr != null) {
						final BigDecimal thisCost = evaluateCost(myParser,
								costExpr);

						if ((maxCost == null)
								|| (thisCost.compareTo(maxCost) > 1)) {
							maxCost = thisCost;
						}
					}
				}

				if (maxCost != null) {
					return maxCost;
				}

				//
				// No cost formula found, check for catch-all definition
				//
				typeMatched = "ANY";
				costExpr = SettingsHandler.getGame().getPlusCalculation(
						typeMatched);

				if (costExpr != null) {
					return evaluateCost(myParser, costExpr);
				}
			} finally {
				PjepPool.getInstance().release(myParser);
			}
		}

		return BigDecimal.ZERO;
	}

	/**
	 * As per p.176 of DMG.
	 * 
	 * @return TRUE if limited, else FALSE
	 */
	private boolean isMagicLimitedType() {
		boolean limited = false;

		if (isType("HEADGEAR") || isType("EYEGEAR") || isType("CAPE")
				|| isType("AMULET") || isSuit() || isType("ROBE")
				|| isType("SHIRT") || isType("BRACER") || isType("GLOVE")
				|| isType("RING") || isType("BELT") || isType("BOOT")) {
			limited = true;
		}

		return limited;
	}

	/**
	 * same as getSpecialAbilityList except if if you have the same ability
	 * twice, it only lists it once with (2) at the end.
	 * 
	 * @param abilityList
	 * @return The specialAbilityTimesList value
	 */
	private List<String> getSpecialAbilityTimesList(
			final List<String> abilityList) {
		final List<String> sortList = new ArrayList<String>();
		final int[] numTimes = new int[abilityList.size()];

		for (int i = 0; i < abilityList.size(); i++) {
			final String ability = abilityList.get(i);
			if (!sortList.contains(ability)) {
				sortList.add(ability);
				numTimes[i] = 1;
			} else {
				for (int j = 0; j < sortList.size(); j++) {
					final String testAbility = sortList.get(j);
					if (testAbility.equals(ability)) {
						numTimes[j]++;
					}
				}
			}
		}

		final List<String> retList = new ArrayList<String>();
		for (int i = 0; i < sortList.size(); i++) {
			String ability = sortList.get(i);
			if (numTimes[i] > 1) {
				ability = ability + " (" + numTimes[i] + ")";
			}
			retList.add(ability);
		}

		return retList;
	}

	/**
	 * Gets the weightAdjustedForSize attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param aSize
	 *            the size to adjust for
	 * @return The weightAdjustedForSize value
	 */
	private Float getWeightAdjustedForSize(final PlayerCharacter aPC,
			final String aSize) {
		if (this.isVirtual()) {
			return new Float(0.0);
		}

		final SizeAdjustment newSA = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(aSize);
		final SizeAdjustment currSA = SettingsHandler.getGame()
				.getSizeAdjustmentNamed(getSize());

		if ((newSA == null) || (currSA == null)) {
			return new Float(getBaseWeightAsDouble());
		}

		final double mult = newSA
				.getBonusTo(aPC, "ITEMWEIGHT", typeList(), 1.0)
				/ currSA.getBonusTo(aPC, "ITEMWEIGHT", typeList(), 1.0);

		return new Float(getBaseWeightAsDouble() * mult);
	}

	/**
	 * Checks whether the proposed type is one that is accepted
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return The acceptsTypes value
	 */
	private boolean acceptsType(final String aString) {
		if (d_acceptsTypes == null) {
			return false;
		}

		return d_acceptsTypes.containsKey(aString.toUpperCase());
	}

	private void addAltType(final String type) {
		if (altTypeList == null) {
			altTypeList = new ArrayList<String>(1);
		}

		altTypeList.add(type);
		typeListCachePrimary = null;
		typeListCacheSecondary = null;
	}

	/**
	 * setter
	 * 
	 * @param e
	 */
	private void addContainedEquipment(final Equipment e) {
		if (d_containedEquipment == null) {
			d_containedEquipment = new ArrayList<Equipment>();
		}

		d_containedEquipment.add(e);
	}

	/**
	 * Adds
	 * 
	 * @param bPrimary
	 * @param argVFeats
	 * @return modified argVFeats list of virtual feats from EQ Mods
	 */
	private List<Ability> addEqModList(final boolean bPrimary,
			final List<Ability> argVFeats) {
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);
		final List<Ability> vFeats = argVFeats;

		if (eqModList.size() != 0) {
			for (EquipmentModifier aEqMod : eqModList) {
				vFeats.addAll(aEqMod.getVirtualFeatList());
			}
		}

		return vFeats;
	}

	/**
	 * Gets the acModAdjustedForSize attribute of the Equipment object
	 * 
	 * @param aPC
	 * @param baseEq
	 * 
	 * @param aSize
	 *            The size to adjust for
	 */
	private void adjustACForSize(final PlayerCharacter aPC,
			final Equipment baseEq, final String aSize) {
		if ((getBonusList() != null) && isArmor()) {
			double mult = 1.0;
			final SizeAdjustment newSA = SettingsHandler.getGame()
					.getSizeAdjustmentNamed(aSize);
			final SizeAdjustment currSA = SettingsHandler.getGame()
					.getSizeAdjustmentNamed(baseEq.getSize());

			if ((newSA != null) && (currSA != null)) {
				mult = newSA.getBonusTo(aPC, "ACVALUE", baseEq.typeList(), 1.0)
						/ currSA.getBonusTo(aPC, "ACVALUE", baseEq.typeList(),
								1.0);
			}

			final List<BonusObj> baseEqBonusList = baseEq.getBonusList();
			final List<BonusObj> eqBonusList = getBonusList();

			//
			// Go through the bonus list looking for COMBAT|AC|x and resize
			// bonus
			// Assumption: baseEq.bonusList and this.bonusList only differ in
			// COMBAT|AC|x bonuses
			//
			for (int i = eqBonusList.size() - 1; i >= 0; --i) {
				final BonusObj aBonus = eqBonusList.get(i);
				String aString = aBonus.toString();

				if (aString.startsWith("COMBAT|AC|")) {
					final int iOffs = aString.indexOf('|', 10);

					if (iOffs > 10) {
						removeBonusList(aBonus);
					}
				}
			}
			for (int i = 0; i < baseEqBonusList.size(); ++i) {
				final BonusObj aBonus = baseEqBonusList.get(i);
				String aString = aBonus.toString();

				if (aString.startsWith("COMBAT|AC|")) {
					final int iOffs = aString.indexOf('|', 10);

					if (iOffs > 10) {
						Integer acCombatBonus = Integer.valueOf(aString
								.substring(10, iOffs));
						acCombatBonus = Integer.valueOf(new Float(acCombatBonus
								.doubleValue()
								* mult).intValue());
						aString = aString.substring(0, 10)
								+ acCombatBonus.toString()
								+ aString.substring(iOffs);
						addBonusList(aString);
					}
				}
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC
	 * 
	 * @param aFloat
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private boolean checkChildWeight(final PlayerCharacter aPC,
			final Float aFloat) {
		if (containerWeightCapacity.intValue() == -1) {
			return true;
		}

		if ((aFloat.floatValue() + getContainedWeight(aPC).floatValue()) <= containerWeightCapacity
				.floatValue()) {
			return true;
		}

		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param aTypeList
	 *            Description of the Parameter
	 * @param aQuant
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private boolean checkContainerCapacity(final SortedSet<String> aTypeList,
			final Float aQuant) {
		if (acceptsType("Any")) {
			if (getAcceptsType("Any").intValue() == -1) {
				return true;
			}
		}

		return !("".equals(pickChildType(aTypeList, aQuant)));
	}

	private List<EquipmentModifier> cloneEqModList(final boolean primary) {
		final List<EquipmentModifier> clonedList = new ArrayList<EquipmentModifier>();

		for (EquipmentModifier eqMod : getEqModifierList(primary)) {
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getChoiceString().length() != 0) {
				eqMod = eqMod.clone();
			}

			clonedList.add(eqMod);
		}

		return clonedList;
	}

	/**
	 * Checks whether the child type is possessed
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return true if has child type
	 */
	private boolean containsChildType(final String aString) {
		if (d_childTypes == null) {
			return false;
		}

		return d_childTypes.containsKey(aString);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	private SortedSet<String> eqTypeList() {
		return new TreeSet<String>(typeList());
	}

	/**
	 * Get all the modifiers that apply to the entire item into a separate list
	 * 
	 * @param commonList
	 *            The list to extract from
	 * @param extractList
	 *            The list to extract.
	 */
	private static void extractListFromCommon(
			final List<EquipmentModifier> commonList,
			final List<EquipmentModifier> extractList) {
		for (int i = extractList.size() - 1; i >= 0; --i) {
			final EquipmentModifier eqMod = extractList.get(i);

			if (!eqMod.getAssignToAll()) {
				continue;
			}

			commonList.add(0, eqMod);
			extractList.remove(i);
		}
	}

	/**
	 * Strip sizes and "Standard" from type string.
	 * 
	 * @param aPC
	 */
	private void cleanTypes(final PlayerCharacter aPC) {
		final String aType = super.getType();
		final StringTokenizer aTok = new StringTokenizer(aType, ".");
		final StringBuffer aCleaned = new StringBuffer(aType.length());
		aCleaned.append(".CLEAR");

		while (aTok.hasMoreTokens()) {
			final String aString = aTok.nextToken();
			int i;

			for (i = 0; i <= (SettingsHandler.getGame()
					.getSizeAdjustmentListSize() - 1); ++i) {
				if (aString.equalsIgnoreCase(SettingsHandler.getGame()
						.getSizeAdjustmentAtIndex(i).getDisplayName())) {
					break;
				}
			}

			//
			// Ignore size or "Standard" unless previous tag
			// was "ARMOR" and this is "MEDIUM"
			//
			if ("Standard".equalsIgnoreCase(aString)) {
				continue;
			}

			if (i < SettingsHandler.getGame().getSizeAdjustmentListSize()) {
				final SizeAdjustment sa = SettingsHandler.getGame()
						.getSizeAdjustmentAtIndex(i);

				if ((!sa.isDefaultSize())
						|| !aCleaned.toString().toUpperCase().endsWith("ARMOR")) {
					continue;
				}
			}

			//
			// Make sure "Magic" is the first thing in the list
			//
			if ("Magic".equalsIgnoreCase(aString)) {
				if (aCleaned.length() > 0) {
					aCleaned.insert(0, '.');
				}

				aCleaned.insert(0, aString);
			} else {
				if (aCleaned.length() > 0) {
					aCleaned.append('.');
				}

				aCleaned.append(aString);
			}
		}

		setTypeInfo(aCleaned.toString());
		setDefaultCrit(aPC);
	}

	private BigDecimal evaluateCost(final PJEP myParser, final String costExpr) {
		myParser.parseExpression(costExpr);

		if (!myParser.hasError()) {
			final Object result = myParser.getValueAsObject();

			if (result != null) {
				return new BigDecimal(result.toString());
			}
		}

		Logging.errorPrint("Bad equipment cost expression: " + costExpr);

		return BigDecimal.ZERO;
	}

	private boolean ignoresCostDouble() {
		boolean noDouble = false;

		if (isType("MANTLE") // Mantle of Spell Resistance doesn't double
								// cost
				|| isType("POTION") || isType("SCROLL")
				|| isType("STAFF")
				|| isType("WAND")) {
			noDouble = true;
		}

		return noDouble;
	}

	/**
	 * Description of the Method
	 * 
	 * @param aTypeList
	 *            Description of the Parameter
	 * @param aQuant
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private String pickChildType(final SortedSet<String> aTypeList,
			final Float aQuant) {
		String canContain = "";
		Float acceptsType = getAcceptsType("TOTAL");

		//
		// Sanity check
		//
		if (acceptsType == null) {
			acceptsType = Float.valueOf(0);
		}

		if (getChildType("Total") == null) {
			setChildType("Total", Float.valueOf(0));
		}

		if ((getChildType("Total").floatValue() + aQuant.floatValue()) <= acceptsType
				.floatValue()) {
			for (String aString : aTypeList) {
				if (!"".equals(canContain)) {
					break;
				}
				if (acceptsType(aString)) {
					if (containsChildType(aString)
							&& ((getChildType(aString).floatValue() + aQuant
									.floatValue()) <= getAcceptsType(aString)
									.floatValue())) {
						canContain = aString;
					} else if (aQuant.floatValue() <= getAcceptsType(aString)
							.floatValue()) {
						canContain = aString;
					}
				}
			}

			if (("".equals(canContain)) && acceptsType("Any")) {
				if (!containsChildType("Any")) {
					setChildType("Any", Float.valueOf(0));
				}

				if ((getChildType("Any").floatValue() + aQuant.floatValue()) <= getAcceptsType(
						"Any").floatValue()) {
					canContain = "Any";
				}
			}
		}

		return canContain;
	}

	/**
	 * Remove the common modifiers from the alternate list.
	 * 
	 * @param altList
	 * @param commonList
	 * @param errMsg
	 */
	private void removeCommonFromList(final List<EquipmentModifier> altList,
			final List<EquipmentModifier> commonList, final String errMsg) {
		for (int i = altList.size() - 1; i >= 0; --i) {
			final EquipmentModifier eqMod = altList.get(i);

			if (!eqMod.getAssignToAll()) {
				continue;
			}

			final int j = commonList.indexOf(eqMod);

			if (j >= 0) {
				altList.remove(i);
			} else {
				Logging.errorPrint(errMsg + eqMod.getDisplayName());
			}
		}
	}

	/**
	 * Initialise an array of equipment modifier lists with an entry for each
	 * format category.
	 * 
	 * @return An array of equipmod lists.
	 */
	private List<EquipmentModifier>[] initSplitModList() {
		List<EquipmentModifier>[] modListArray = new List[EquipmentModifier.FORMATCAT_PARENS + 1];

		for (int i = 0; i < modListArray.length; i++) {
			modListArray[i] = new ArrayList<EquipmentModifier>();
		}

		return modListArray;
	}

	/**
	 * Split the equipmod list into seperate lists by format category.
	 * 
	 * @param modList
	 *            The list to be split.
	 * @param splitModList
	 *            The array of receiving lists, one for each format cat.
	 */
	private void splitModListByFormatCat(final List<EquipmentModifier> modList,
			final List<EquipmentModifier>[] splitModList) {
		for (Iterator<EquipmentModifier> iter = modList.iterator(); iter
				.hasNext();) {
			EquipmentModifier eqMod = iter.next();
			splitModList[eqMod.getFormatCat()].add(eqMod);

		}
	}

	/**
	 * remover
	 * 
	 * @param i
	 */
	private void removeContainedEquipment(final int i) {
		d_containedEquipment.remove(i);
	}

	/**
	 * Remove an equipment modifier and specified associated information eg.
	 * Bane|Vermin|Fey eg. Keen Removes a feature from the EqModifier attribute
	 * of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be removed from the EqModifier attribute
	 * @param bPrimary
	 *            The feature to be removed from the EqModifier attribute
	 */
	private void removeEqModifier(final String aString, final boolean bPrimary) {
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		final String eqModKey = aTok.nextToken();
		final EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);

		if (eqMod == null) {
			return;
		}

		//
		// Remove the associated choices
		//
		while (aTok.hasMoreTokens()) {
			final String x = aTok.nextToken().replace('=', '|');

			for (int i = eqMod.getAssociatedCount() - 1; i >= 0; --i) {
				final String aChoice = eqMod.getAssociated(i);

				if (aChoice.startsWith(x)) {
					eqMod.removeAssociated(i);
				}
			}
		}

		if (eqMod.getAssociatedCount() == 0) {
			removeEqModifier(eqMod, bPrimary);
		}
	}

	/**
	 * Remove any modifiers that this weapon doesn't pass the prereqs for
	 * 
	 * @param bPrimary
	 *            Deal with the primary head if true, otherwise, deal with the
	 *            secondary head.
	 */
	private void removeUnqualified(final boolean bPrimary) {
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (int i = eqModList.size() - 1; i >= 0; --i) {
			final EquipmentModifier eqMod = eqModList.get(i);

			// The problem is that you have entries
			// like the following for Adamantine:
			// PRETYPE:Weapon,Metal !PRETYPE:Masterwork
			// Which have nothing to do with the eqMod, so
			// they are always going to fail and be removed
			// The entries in equip_enhancing are used by
			// the GUI to know what to display, not for
			// actual passesPreReq checks

			if (!eqMod.passesPreReqToGain(this, null)) {
				Logging.errorPrint("reUnq:Removing: " + eqMod.getDisplayName());
				Logging.errorPrint("reUnq:preReqs: " + eqMod.preReqStrings());
			}
		}
	}

	public final void addMyType(final String myType) {
		typeListCachePrimary = null;
		typeListCacheSecondary = null;
		super.addMyType(myType);
	}

	protected void clearMyType() {
		typeListCachePrimary = null;
		typeListCacheSecondary = null;
		super.clearMyType();
	}

	protected void removeMyType(final String myType) {
		typeListCachePrimary = null;
		typeListCacheSecondary = null;
		super.removeMyType(myType);
	}

	/**
	 * Returns a list of the types of this item.
	 * 
	 * @param bPrimary
	 *            ???
	 * @return a list of the types of this item.
	 */
	private List<String> typeList(final boolean bPrimary) {
		// if (cacheHit%100==0 ||cacheMiss%100==0) {
		// System.out.println("cacheHit="+cacheHit + ", cacheMiss="+cacheMiss);
		// }
		if (bPrimary && typeListCachePrimary != null) {
			return typeListCachePrimary;
		}
		if (!bPrimary && typeListCacheSecondary != null) {
			return typeListCacheSecondary;
		}

		//
		// Use the primary type(s) if none defined for secondary
		//
		final List<String> calculatedTypeList;

		if (bPrimary || (getAltTypeCount() == 0)) {
			calculatedTypeList = new ArrayList<String>(getTypeList(false));
		} else {
			if (!isDouble()) {
				return new ArrayList<String>();
			}

			calculatedTypeList = new ArrayList<String>(getAltTypeCount());

			if (altTypeList != null) {
				calculatedTypeList.addAll(altTypeList);
			}
		}

		final List<String> modTypeList = new ArrayList<String>();

		//
		// Add in all type modfiers from "ADDTYPE" modifier
		//
		EquipmentModifier aEqMod = getEqModifierKeyed("ADDTYPE", bPrimary);

		if (aEqMod != null) {
			for (int e = 0; e < aEqMod.getAssociatedCount(); ++e) {
				String aType = aEqMod.getAssociated(e);
				aType = aType.toUpperCase();

				if (!calculatedTypeList.contains(aType)) {
					modTypeList.add(aType);
				}
			}
		}

		//
		// Add in all of the types from each EquipmentModifier
		// currently applied to this piece of equipment
		//
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (EquipmentModifier eqMod : eqModList) {
			if (!willIgnore(eqMod.getKeyName(), bPrimary)) {
				//
				// If we've just replaced the armor type, then make sure it is
				// not in the equipment modifier list
				//
				final String armorType = eqMod
						.replaceArmorType(calculatedTypeList);

				if (armorType != null) {
					final int idx = modTypeList.indexOf(armorType);

					if (idx >= 0) {
						modTypeList.remove(idx);
					}
				}

				final List<String> eqModTypeList = eqMod.getItemType();

				for (String aType : eqModTypeList) {
					aType = aType.toUpperCase();

					// If it's BOTH & MELEE, we cannot add RANGED or THROWN to
					// it
					// BOTH is only used after the split of a Thrown weapon in 2
					// (melee and ranged)
					if (calculatedTypeList.contains("BOTH")
							&& calculatedTypeList.contains("MELEE")
							&& ("RANGED".equals(aType) || "THROWN"
									.equals(aType))) {
						continue;
					}

					if (!calculatedTypeList.contains(aType)
							&& !modTypeList.contains(aType)) {
						modTypeList.add(aType);
					}
				}
			}
		}

		calculatedTypeList.addAll(modTypeList);

		//
		// Make sure MAGIC tag is the 1st entry
		//
		final int idx = calculatedTypeList.indexOf("MAGIC");

		if (idx > 0) {
			calculatedTypeList.remove(idx);
			calculatedTypeList.add(0, "MAGIC");
		}

		if (bPrimary) {
			typeListCachePrimary = calculatedTypeList;
		} else {
			typeListCacheSecondary = calculatedTypeList;
		}
		return calculatedTypeList;
	}

	/**
	 * Creates the containerCapacityString from children of this object
	 */
	private void updateContainerCapacityString() {
		final StringBuffer tempStringBuffer = new StringBuffer();
		boolean comma = false;

		if (containerWeightCapacity.intValue() != -1) {
			tempStringBuffer.append(containerWeightCapacity).append(' ')
					.append(Globals.getGameModeUnitSet().getWeightUnit());
			comma = true;
		}

		if (getAcceptsTypeCount() > 0) {
			for (String aString : d_acceptsTypes.keySet()) {
				if (comma) {
					tempStringBuffer.append(", ");
					comma = false;
				}

				if (getAcceptsType(aString).intValue() != -1) {
					tempStringBuffer.append(
							getAcceptsType(aString).floatValue()).append(' ');
					tempStringBuffer.append(aString);
					comma = true;
				} else if (!"TOTAL".equals(aString)) {
					comma = true;
					tempStringBuffer.append(aString);
				}
			}
		}

		containerCapacityString = tempStringBuffer.toString();
	}

	/**
	 * Description of the Method
	 * 
	 * @param eqModKey
	 *            Description of the Parameter
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	private boolean willIgnore(final String eqModKey, final boolean bPrimary) {
		for (EquipmentModifier eqMod : getEqModifierList(bPrimary)) {
			if (eqMod.willIgnore(eqModKey)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets all the BonusObj's to "active". Note this version overrides the
	 * PObject implementation as it will check the bonuses against the
	 * equipment, rather than the PC.
	 * 
	 * @param aPC
	 *            The character being checked.
	 */
	public void activateBonuses(final PlayerCharacter aPC) {
		for (final BonusObj bonus : getBonusList()) {
			bonus.setApplied(false);

			if (bonus.passesPreReqToGain(this, aPC)) {
				bonus.setApplied(true);
			} else {
				bonus.setApplied(false);
			}
		}
	}

	boolean isCalculatingCost() {
		return calculatingCost;
	}

	boolean isWeightAlreadyUsed() {
		return weightAlreadyUsed;
	}

	double getWeightInPounds() {
		if (this.isVirtual()) {
			return 0.0;
		}

		return weightInPounds;
	}

	void setWeightAlreadyUsed(boolean weightAlreadyUsed) {
		this.weightAlreadyUsed = weightAlreadyUsed;
	}

	int getSlots() {
		return slots;
	}

	Integer getSpellFailure() {
		return spellFailure;
	}

	Integer getRange() {
		if (theWeaponStats != null) {
			return theWeaponStats.getRange();
		}
		return 0;
	}

	/**
	 * Get non headed name
	 * 
	 * @return non headed name
	 */
	public final String getNonHeadedName() {
		if (wholeItemName == null || wholeItemName.length() == 0) {
			return getName();
		}
		return wholeItemName;
	}

	/**
	 * Get whole item name
	 * 
	 * @return whole item name
	 */
	public final String getWholeItemName() {
		return wholeItemName;
	}

	/**
	 * Set whole item name
	 * 
	 * @param wholeItemName
	 */
	public final void setWholeItemName(String wholeItemName) {
		this.wholeItemName = wholeItemName;
	}

	/**
	 * Set base quantity
	 * 
	 * @param aString
	 */
	public final void setBaseQty(final String aString) {
		try {
			baseQuantity = Integer.parseInt(aString);
		} catch (NumberFormatException e) {
			baseQuantity = 0;
			Logging.errorPrint("Badly formed BaseQty string: " + aString);
		}
	}

	/**
	 * Get base quantity
	 * 
	 * @return base quantity
	 */
	public final int getBaseQty() {
		return baseQuantity;
	}

	/**
	 * Create a Key for the new custom piece of resized equipment. The new key
	 * will start with the auto resized constant and then the size abbreviation
	 * (as per SizeAdjustment) followed by the existing key. This should
	 * generate a unique nam unless we've already auto resized this piece of
	 * equipment to this size in which case it already exists in the equipment
	 * list and does not need to be created.
	 * 
	 * @param newSize
	 *            The size of equipment to make a key for. This needs to be the
	 *            abbreviated form, not the full name.
	 * @return The generated key
	 */

	public String createKeyForAutoResize(String newSize) {
		// Make sure newSize has at least one letter
		if (newSize.length() < 1) {
			return getKeyName();
		}

		// Make sure the new size is a configured sizeAdjustment
		SizeAdjustment sa = SettingsHandler.getGame().getSizeAdjustmentNamed(
				newSize);
		if (sa == null) {
			return getKeyName();
		}
		newSize = sa.getDisplayName();

		// Make sure newSize is a single upper case letter
		newSize = newSize.toUpperCase().substring(0, 1);

		String thisKey = getKeyName();

		if (thisKey.startsWith(Constants.s_AUTO_RESIZE)) {
			int index = Constants.s_AUTO_RESIZE.length();
			String keySize = thisKey.substring(index, index + 1).toUpperCase();

			// If the key of this object already has the newSize in the correct
			// place then just return it, the item has already been adjusted.
			// This should never happen because if the key has an s_AUTO_RESIZE
			// prefix and the correct size then it should already be newSize
			if (keySize.equals(newSize)) {
				return thisKey;
			}

			// remove the s_AUTO_RESIZE and the following size abbreviation
			// from the key
			thisKey = thisKey.substring(index + 1);
		}

		return Constants.s_AUTO_RESIZE + newSize + thisKey;
	}

	/**
	 * Create a Name for the new custom piece of resized equipment. The name
	 * will be constructed by searching for the size of the equipment in its
	 * name. If found (and surrounded by '(', '/', or ')', it will be replaced.
	 * If not found, it will be added to the end surrounded by parenthesis.
	 * 
	 * @param newSize
	 *            The size of equipment to make a key for
	 * @return The generated Name
	 */

	public String createNameForAutoResize(String newSize) {
		// Make sure newSize has at least one letter
		if (newSize.length() < 1) {
			return getName();
		}

		// Make sure the new size is a configured sizeAdjustment
		SizeAdjustment sa = SettingsHandler.getGame().getSizeAdjustmentNamed(
				newSize);
		if (sa == null) {
			return getName();
		}
		newSize = sa.getDisplayName();

		// Cannonise newSize (will expand abbreviation to full name of size
		// and convert to correct case)
		newSize.toUpperCase();

		String thisName = getName();
		String upName = thisName.toUpperCase();

		String thisSize = getSize();

		// Get the full name of the current size
		sa = SettingsHandler.getGame().getSizeAdjustmentNamed(thisSize);
		thisSize = (sa == null) ? "Medium" : sa.getDisplayName();
		String upThisSize = thisSize.toUpperCase();

		int start = upName.indexOf(upThisSize);
		int end = start + upThisSize.length();

		/*
		 * if the name contains thisSize surrounded by /, ( or ) then replace
		 * thisSize with newSize
		 */
		if (start > -1
				&& (upName.substring(start - 1).startsWith("(") || upName
						.substring(start - 1).startsWith("/"))
				&& (upName.substring(end).startsWith(")") || upName.substring(
						end).startsWith("/"))) {
			return thisName.substring(0, start) + newSize
					+ thisName.substring(end);
		}

		return thisName + " (" + newSize + ")";
	}

	/**
	 * Set QUALITY
	 * 
	 * @param key
	 * @param value
	 */
	public void setQuality(String key, String value) {
		qualityMap.put(key, value);
	}

	/**
	 * Get QUALITY
	 * 
	 * @param key
	 * @return quality
	 */
	public String getQuality(String key) {
		return qualityMap.get(key);
	}

	/**
	 * Get quality
	 * 
	 * @param num
	 * @return quality
	 */
	public String getQuality(int num) {
		for (String key : qualityMap.keySet()) {
			num--;
			if (num == 0) {
				return key + ": " + qualityMap.get(key);
			}
		}
		return "";
	}

	/**
	 * Get quality map
	 * 
	 * @return quality map
	 */
	public Map<String, String> getQualityMap() {
		return qualityMap;
	}

	/**
	 * Get quality string
	 * 
	 * @return quality string
	 */
	public String getQualityString() {
		StringBuffer sb = new StringBuffer();
		boolean firstTime = true;
		for (String key : qualityMap.keySet()) {
			if (!firstTime) {
				sb.append(", ");
			}
			sb.append(key).append(": ").append(qualityMap.get(key));
			firstTime = false;
		}
		return sb.toString();
	}

	/**
	 * Make this item virtual i.e. one that doesn't really exist and is only
	 * used to hold temporary bonuses
	 */
	public void makeVirtual() {
		this.virtualItem = true;
	}

	/**
	 * Does this item really exist, or is it a phantom created to hold a bonus
	 * 
	 * @return Returns the virtualItem.
	 */
	private boolean isVirtual() {
		return virtualItem;
	}

	/**
	 * Sets the critMult attribute of the Equipment object
	 * 
	 * @param aMult
	 *            The new critMult value
	 */
	public void setCritMult(final int aMult) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setCritMult(aMult);
	}

	/**
	 * Gets the critMult attribute of the Equipment object
	 * 
	 * @return The critMult value
	 */
	public String getCritMult() {
		if (theWeaponStats != null) {
			return theWeaponStats.getCritMult();
		}
		return "";
	}

	/**
	 * Gets the unmodified crit multiplier for the weapon.
	 * 
	 * @return the crit multiplier
	 */
	public int getRawCritMult() {
		if (theWeaponStats != null) {
			return theWeaponStats.getRawCritMult();
		}
		return 0;
	}

	/**
	 * Sets the altCritMult attribute of the Equipment object
	 * 
	 * @param aMult
	 *            The new altCritMult value
	 */
	public void setAltCritMult(final int aMult) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setAltCritMult(aMult);
	}

	/**
	 * Gets the altCritMult attribute of the Equipment object
	 * 
	 * @return The altCritMult value
	 */
	public String getAltCritMult() {
		if (theWeaponStats != null) {
			return theWeaponStats.getAltCritMult();
		}
		return "";
	}

	/**
	 * Gets the unmodified alt crit multiplier for the weapon.
	 * 
	 * @return the alt crit multiplier
	 */
	public int getRawAltCritMult() {
		if (theWeaponStats != null) {
			return theWeaponStats.getRawAltCritMult();
		}
		return 0;
	}

	/**
	 * Gets the critMultiplier attribute of the Equipment object
	 * 
	 * @return The critMultiplier value
	 */
	public int getCritMultiplier() {
		if (theWeaponStats != null) {
			return theWeaponStats.getCritMultiplier();
		}
		return 0;
	}

	/**
	 * Gets the altCritMultiplier attribute of the Equipment object
	 * 
	 * @return The altCritMultiplier value
	 */
	public int getAltCritMultiplier() {
		if (theWeaponStats != null) {
			return theWeaponStats.getAltCritMultiplier();
		}
		return 0;
	}

	/**
	 * Test to see if a weapon is Finesseable or not for a PC
	 * 
	 * @param aPC
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if finessable
	 */
	public boolean isFinessable(final PlayerCharacter aPC) {
		if (isType("Finesseable")) {
			return true;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(aPC);

		return (wCat.isFinessable());
	}

	/**
	 * Tests if this weapon is a light weapon for the specied PC.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is light for the specified pc.
	 */
	public boolean isWeaponLightForPC(final PlayerCharacter pc) {
		if ((pc == null) || (!isWeapon())) {
			return false;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);

		return (wCat.getName().equals("Light"));
	}

	/**
	 * Tests if this weapon can be used in one hand by the specified PC.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if the weapon can be used one handed.
	 */
	public boolean isWeaponOneHanded(final PlayerCharacter pc) {
		if ((pc == null) || (!isWeapon())) {
			return false;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);

		return wCat.getHands() == 1;
	}

	/**
	 * Tests if the weapon is either too large OR too small for the specified PC
	 * to wield.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is too large or too small.
	 */
	public boolean isWeaponOutsizedForPC(final PlayerCharacter pc) {
		if ((pc == null) || (!isWeapon())) {
			return true;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);

		return (wCat.getHands() > 2 || wCat.getHands() < 0);
	}

	/**
	 * Tests is the weapon is too large for the PC to use.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon
	 * @return true if the weapon is too large.
	 */
	public boolean isWeaponTooLargeForPC(final PlayerCharacter pc) {
		if ((pc == null) || (!isWeapon())) {
			return false;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);

		return (wCat.getHands() > 2);
	}

	/**
	 * Tests if this weapon requires two hands to use.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is two-handed for the specified pc
	 */
	public boolean isWeaponTwoHanded(final PlayerCharacter pc) {
		if ((pc == null) || (!isWeapon())) {
			return false;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);

		return wCat.getHands() == 2;
	}

	/**
	 * Gets the minimum WieldCategory this weapon can be used at. Accounts for
	 * all modifiers that affect WieldCategory. 3.0 weapon sizes are mapped to
	 * appropriate WieldCategories.
	 * 
	 * @param aPC
	 *            The PlayerCharacter using the weapon
	 * @return The minimum WieldCategory required to use the weapon.
	 */
	public WieldCategory getEffectiveWieldCategory(final PlayerCharacter aPC) {
		WieldCategory wCat = null;

		WeaponProf wp = getExpandedWeaponProf(aPC);

		// Get this equipments WieldCategory from gameMode
		wCat = WieldCategory.findByName(getWield());

		if (hasWield() && !Globals.checkRule(RuleConstants.SIZEOBJ)) {
			// Get the starting effective wield category
			wCat = wCat.adjustForSize(aPC, this);
		} else {
			int sizeDiff = 0;
			int pcSize = aPC.sizeInt();

			if (wp != null) {
				pcSize += aPC.getTotalBonusTo("WEAPONPROF=" + wp.getKeyName(),
						"PCSIZE");
			}
			if (wCat != null && Globals.checkRule(RuleConstants.SIZEOBJ)) {
				// In this case we have a 3.5 style equipments size.
				// We need to map to a 3.0 style
				sizeDiff = wCat.getObjectSizeInt(this) - pcSize;
			} else {
				sizeDiff = sizeInt() - pcSize;
			}

			if (sizeDiff > 1) {
				wCat = WieldCategory.findByName("TooLarge");
			} else if (sizeDiff == 1) {
				wCat = WieldCategory.findByName("TwoHanded");
			} else if (sizeDiff == 0) {
				wCat = WieldCategory.findByName("OneHanded");
			} else {
				wCat = WieldCategory.findByName("Light");
			}
		}

		int aBump = 0;

		// TODO Remove this code when support for this "feature" goes away
		if (wp != null) {
			int iHands = wp.getHands();

			if (iHands == WeaponProf.HANDS_SIZEDEPENDENT) {
				if (aPC.sizeInt() > sizeInt()) {
					iHands = 1;
				} else {
					iHands = 2;
				}
			}
			while (wCat.getHands() < iHands) {
				wCat = wCat.getWieldCategoryStep(1);
			}

			// See if there is a bonus associated with just this weapon
			final String expProfName = wp.getKeyName();
			aBump += (int) aPC.getTotalBonusTo("WEAPONPROF=" + expProfName,
					"WIELDCATEGORY");
		}

		// or a bonus from the weapon itself
		aBump += (int) bonusTo(aPC, "WEAPON", "WIELDCATEGORY", true);

		if (aBump == 0) {
			return wCat;
		}

		return wCat.getWieldCategoryStep(aBump);
	}

	/**
	 * Gets the WeaponProf required to wield this weapon. Handles the "Hands"
	 * version of the proficiency by returning the correct version of the
	 * WeaponProf based on if the PC possesses the Exotic version.
	 * 
	 * @param aPC
	 *            The PlayerCharacter using the weapon
	 * @return The WeaponProf required to use the weapon.
	 */
	public WeaponProf getExpandedWeaponProf(final PlayerCharacter aPC) {
		String aWProf = profName;

		if (aWProf.length() == 0) {
			aWProf = getName();
		}

		final int iOffs = aWProf.indexOf("[Hands]");

		if (iOffs >= 0) {
			// "Sword (Bastard/[Hands])"
			// expands to:
			// "Sword (Bastard/Exotic)"
			// or
			// "Sword (Bastard/Martial)"
			//
			final String sExotic = aWProf.substring(0, iOffs) + "Exotic"
					+ aWProf.substring(iOffs + 7);
			final String sMartial = aWProf.substring(0, iOffs) + "Martial"
					+ aWProf.substring(iOffs + 7);

			final WeaponProf wpExotic = Globals.getWeaponProfKeyed(sExotic);
			final WeaponProf wpMartial = Globals.getWeaponProfKeyed(sMartial);

			if (wpMartial != null && wpExotic != null) {
				if (aPC.hasWeaponProfKeyed(sExotic)) {
					return wpExotic;
				}
				return wpMartial;
			}
		}
		return Globals.getWeaponProfKeyed(aWProf);
	}

	/**
	 * Set the number of pages for this object
	 * 
	 * @param value
	 */
	public final void setNumPages(final int value) {
		integerChar.put(IntegerKey.NUM_PAGES, value);
	}

	/**
	 * Get the number of pages of this object
	 * 
	 * @return the number of pages of this object
	 */
	public final int getNumPages() {
		Integer characteristic = integerChar.get(IntegerKey.NUM_PAGES);
		return characteristic == null ? 0 : characteristic.intValue();
	}

	/**
	 * Set the page usage formula for this object
	 * 
	 * @param aString
	 */
	public final void setPageUsage(final String aString) {
		stringChar.put(StringKey.PAGE_USAGE, aString);
	}

	/**
	 * Get the page usage formula of this object
	 * 
	 * @return the page usage formula of this object
	 */
	public final String getPageUsage() {
		String characteristic = stringChar.get(StringKey.PAGE_USAGE);
		return characteristic == null ? "" : characteristic;
	}

	//
	// Protective Item Support
	//
	/**
	 * Gets the AC attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The acBonus value
	 * @todo BONUS:EQMARMOR|ACBONUS|x should be documented.
	 */
	public Integer getACBonus(final PlayerCharacter aPC) {
		int dbon = (int) bonusTo(aPC, "COMBAT", "AC", true);
		dbon += (int) bonusTo(aPC, "EQMARMOR", "ACBONUS", true);

		return Integer.valueOf(dbon);
	}

	/**
	 * Gets the acMod attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The acMod value
	 * @todo This should be documented
	 */
	public Integer getACMod(final PlayerCharacter aPC) {
		final int mod = (int) bonusTo(aPC, "EQMARMOR", "AC", true)
				+ (int) bonusTo(aPC, "COMBAT", "AC", true);

		return Integer.valueOf(mod);
	}

	//
	// Weapon Support
	//

	/**
	 * Sets the critRange attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new critRange value
	 */
	public void setCritRange(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setCritRange(aString);
	}

	/**
	 * Gets the critRange attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The critRange value
	 */
	public String getCritRange(final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			return theWeaponStats.getCritRange(aPC);
		}
		return "";
	}

	/**
	 * Gets the critRangeAdd attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The critRangeAdd value
	 */
	public int getCritRangeAdd(final PlayerCharacter aPC, final boolean bPrimary) {
		if (theWeaponStats != null) {
			return theWeaponStats.getCritRangeAdd(aPC, bPrimary);
		}
		return 0;
	}

	/**
	 * Gets the critRangeDouble attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The critRangeDouble value
	 */
	public int getCritRangeDouble(final PlayerCharacter aPC,
			final boolean bPrimary) {
		if (theWeaponStats != null) {
			return theWeaponStats.getCritRangeDouble(aPC, bPrimary);
		}
		return 0;
	}

	/**
	 * Sets the damage attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new damage value
	 */
	public void setDamage(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setDamage(aString);
	}

	/**
	 * Gets the damage attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The damage value
	 */
	public String getDamage(final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			return theWeaponStats.getDamage(aPC);
		}
		return "";
	}

	/**
	 * Sets the altCritRange attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new altCritRange value
	 */
	public void setAltCritRange(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setAltCritRange(aString);
	}

	//
	// This can be different if one head is Keen and the other is not
	//

	/**
	 * Gets the altCritRange attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @return The altCritRange value
	 */
	public String getAltCritRange(final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			return theWeaponStats.getAltCritRange(aPC);
		}
		return "";
	}

	/**
	 * Sets the alternate damage for this item.
	 * 
	 * @param aString
	 *            the alternate damage for this item.
	 */
	public void setAltDamage(final String aString) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setAltDamage(aString);
	}

	/**
	 * Returns the alternate damage for this item.
	 * 
	 * @param aPC
	 * 
	 * @return the alternate damage for this item.
	 */
	public String getAltDamage(final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			theWeaponStats.getAltDamage(aPC);
		}
		return "";
	}

	/**
	 * Returns whether to give several attacks
	 * 
	 * @param argAttacksProgress
	 *            whether to give several attacks.
	 */
	public void setAttacksProgress(final boolean argAttacksProgress) {
		if (theWeaponStats == null) {
			theWeaponStats = new WeaponEquipment(this);
		}
		theWeaponStats.setAttacksProgress(argAttacksProgress);
	}

	/**
	 * if true a BAB of 13 yields 13/8/3, if false, merely 13
	 * 
	 * @return whether it gives several attacks
	 */
	public boolean isAttacksProgress() {
		if (theWeaponStats != null) {
			return theWeaponStats.isAttacksProgress();
		}
		return false;
	}

	/**
	 * Gets the bonusToDamage attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The bonusToDamage value
	 */
	public int getBonusToDamage(final PlayerCharacter aPC,
			final boolean bPrimary) {
		if (theWeaponStats != null) {
			return theWeaponStats.getBonusToDamage(aPC, bPrimary);
		}
		return 0;
	}

	/**
	 * Gets the bonusToHit attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param bPrimary
	 *            Description of the Parameter
	 * @return The bonusToHit value
	 */
	public int getBonusToHit(final PlayerCharacter aPC, final boolean bPrimary) {
		if (theWeaponStats != null) {
			return theWeaponStats.getBonusToHit(aPC, bPrimary);
		}
		return 0;
	}

	/**
	 * Gets the range list of the Equipment object, adding the 30' range, if not
	 * present and required
	 * 
	 * @param addShortRange
	 *            boolean
	 * @param aPC
	 * @return The range list
	 */
	public List<String> getRangeList(boolean addShortRange,
			final PlayerCharacter aPC) {
		if (theWeaponStats != null) {
			return theWeaponStats.getRangeList(addShortRange, aPC);
		}
		return new ArrayList<String>();
	}

	// ---------------------------
	// Owned Equipment
	// ---------------------------
	/**
	 * Sets the number of items of this type that are carried.
	 * 
	 * @param argCarried
	 *            the number of items of this type that are carried.
	 */
	public void setCarried(final Float argCarried) {
		carried = argCarried;
	}

	/**
	 * Returns the number of items of this type that are carried.
	 * 
	 * @return the number of items of this type that are carried.
	 */
	public Float getCarried() {
		return carried;
	}

	/**
	 * Gets the equipped attribute of the Equipment object
	 * 
	 * @return The equipped value
	 */
	public boolean isEquipped() {
		return equipped;
	}

	// ---------------------------
	// Container Support
	// ---------------------------

	/**
	 * Gets a child of the Equipment object
	 * 
	 * @param childIndex
	 *            The index of the child to get
	 * @return The child value
	 */
	public Object getChild(final int childIndex) {
		return getContainedEquipment(childIndex);
	}

	/**
	 * Gets the childCount attribute of the Equipment object
	 * 
	 * @return The childCount value
	 */
	public int getChildCount() {
		return getContainedEquipmentCount();
	}

	/**
	 * Sets the child type value
	 * 
	 * @param parameter
	 *            Description of the Parameter
	 * @param childType
	 *            child type
	 */
	public void setChildType(final String parameter, final Float childType) {
		if (d_childTypes == null) {
			d_childTypes = new HashMap<String, Float>();
		}

		d_childTypes.put(parameter, childType);
	}

	/**
	 * @param index
	 *            integer indicating which object (contained in this object) to
	 *            return
	 * @return the equipment object contained at this position.
	 */
	public Equipment getContainedByIndex(final int index) {
		final List<Equipment> contents = new ArrayList<Equipment>(getContents());

		if (contents.size() > 0) {
			if (index <= contents.size()) {
				return contents.get(index);
			}
		}

		return null;
	}

	/**
	 * accessor
	 * 
	 * @param i
	 * 
	 * @return containedEquipment object
	 */
	public Equipment getContainedEquipment(final int i) {
		return d_containedEquipment.get(i);
	}

	/**
	 * count
	 * 
	 * @return number of containedEquipment objects
	 */
	public int getContainedEquipmentCount() {
		if (d_containedEquipment == null) {
			return 0;
		}

		return d_containedEquipment.size();
	}

	/**
	 * calculates the value of all items in this container If this container
	 * contains containers, also add the value of all items within that
	 * container, etc, etc, etc.
	 * 
	 * @param aPC
	 * @return contained value
	 */
	public double getContainedValue(final PlayerCharacter aPC) {
		double total = 0;

		if (getChildCount() == 0) {
			return total;
		}

		for (int e = 0; e < getContainedEquipmentCount(); ++e) {
			final Equipment anEquip = getContainedEquipment(e);

			if (anEquip.getContainedEquipmentCount() > 0) {
				total += anEquip.getContainedValue(aPC);
			} else {
				total += anEquip.getCost(aPC).floatValue();
			}
		}

		return total;
	}

	/**
	 * Gets the contained Weight this object recursis all child objects to get
	 * their contained weight
	 * 
	 * @param aPC
	 * 
	 * @return The containedWeight value
	 */
	public Float getContainedWeight(final PlayerCharacter aPC) {
		return getContainedWeight(aPC, false);
	}

	/**
	 * Get Base contained weight
	 * 
	 * @return base contained weight
	 */
	public Float getBaseContainedWeight() {
		return getBaseContainedWeight(false);
	}

	/**
	 * Get Base contained weight
	 * 
	 * @param effective
	 * @return Base contained weight
	 */
	public Float getBaseContainedWeight(final boolean effective) {
		Float total = Float.valueOf(0);

		if ((containerConstantWeight && !effective) || (getChildCount() == 0)) {
			return total;
		}
		for (int e = 0; e < getContainedEquipmentCount(); ++e) {
			final Equipment anEquip = getContainedEquipment(e);

			if (anEquip.getContainedEquipmentCount() > 0) {
				total = new Float(total.floatValue()
						+ anEquip.getBaseWeightAsDouble()
						+ anEquip.getBaseContainedWeight().floatValue());
			} else {
				total = new Float(total.floatValue()
						+ (anEquip.getBaseWeightAsDouble() * anEquip
								.getCarried().floatValue()));
			}
		}

		if (containerReduceWeight.intValue() > 0) {
			total = new Float(total.floatValue()
					* (containerReduceWeight.floatValue() / 100));
		}

		return total;
	}

	/**
	 * Gets the contained Weight this object recursis all child objects to get
	 * their contained weight
	 * 
	 * @param aPC
	 * 
	 * @param effective
	 *            Should we recurse child objects?
	 * @return The containedWeight value
	 */
	public Float getContainedWeight(final PlayerCharacter aPC,
			final boolean effective) {
		Float total = Float.valueOf(0);

		if ((containerConstantWeight && !effective) || (getChildCount() == 0)) {
			return total;
		}
		for (int e = 0; e < getContainedEquipmentCount(); ++e) {
			final Equipment anEquip = getContainedEquipment(e);

			if (anEquip.getContainedEquipmentCount() > 0) {
				total = new Float(total.floatValue()
						+ anEquip.getWeightAsDouble(aPC)
						+ anEquip.getContainedWeight(aPC).floatValue());
			} else {
				total = new Float(total.floatValue()
						+ (anEquip.getWeightAsDouble(aPC) * anEquip
								.getCarried().floatValue()));
			}
		}

		if (containerReduceWeight.intValue() > 0) {
			total = new Float(total.floatValue()
					* (containerReduceWeight.floatValue() / 100));
		}

		return total;
	}

	/**
	 * @param aType
	 *            Type and sequencer (e.g. Liquid3)
	 * @param aSubTag
	 *            SubTag (NAME or SPROP)
	 * @return a String containing the specified subtag
	 */
	public String getContainerByType(String aType, final String aSubTag) {
		final List<Equipment> contents = new ArrayList<Equipment>(getContents());

		// Separate the Type from the sequencer (Liquid from 3)
		int typeIndex = -1;
		int numCharToRemove = 0;

		for (int i = aType.length() - 1; i > 0; i--) {
			if ((aType.charAt(i) >= '0') && (aType.charAt(i) <= '9')) {
				if (typeIndex == -1) {
					typeIndex = 0; // TODO: value never used
				}

				typeIndex = Integer.parseInt(aType.substring(i));
				numCharToRemove++;
			} else {
				i = 0;
			}
		}

		if (numCharToRemove > 0) {
			aType = aType.substring(0, aType.length() - numCharToRemove);
		}

		for (Equipment eq : contents) {
			if (!eq.isType(aType)) {
				contents.remove(eq);
			}
		}

		if (typeIndex < contents.size()) {
			if ("SPROP".equals(aSubTag)) {
				return contents.get(typeIndex).getRawSpecialProperties();
			}
			return contents.get(typeIndex).getName();
		}
		return " ";
	}

	/**
	 * Gets the containerContentsString attribute of the Equipment object
	 * 
	 * @return The containerContentsString value
	 */
	public String getContainerContentsString() {
		return containerContentsString;
	}

	/**
	 * Convenience method. <p/> <br>
	 * author: Thomas Behr 27-03-02
	 * 
	 * @return a list with all Equipment objects this container holds; if this
	 *         instance is no container, the list will be empty.
	 */
	public Collection<Equipment> getContents() {
		final List<Equipment> contents = new ArrayList<Equipment>();

		Equipment aEquip;

		for (int it = 0; it < getContainedEquipmentCount(); ++it) {
			aEquip = getContainedEquipment(it);

			if (aEquip.getCarried().floatValue() > 0.0f) {
				contents.add(aEquip);
			}
		}

		return contents;
	}

	// ---------------------------
	// Container Definition methods
	// ---------------------------

	/**
	 * Set the container
	 * 
	 * @param aString
	 */
	public void setContainer(final String aString) {
		setContainer(null, aString);
	}

	/**
	 * Sets the container attribute of the Equipment object
	 * 
	 * @param aPC
	 * 
	 * @param aString
	 *            The new container value
	 */
	public void setContainer(final PlayerCharacter aPC, final String aString) {
		// -1 means unlimited
		boolean limited = true;
		Float aFloat = Float.valueOf(0);
		d_acceptsChildren = true;

		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		if (aTok.hasMoreTokens()) {
			String bString = aTok.nextToken();

			if ((bString != null) && (bString.charAt(0) == '*')) {
				containerConstantWeight = true;
				bString = bString.substring(1);
			}

			if ((bString != null) && (bString.indexOf('%') > 0)) {
				final int pos = bString.indexOf('%');
				final String redString = bString.substring(0, pos);
				bString = bString.substring(pos + 1);

				try {
					containerReduceWeight = Integer.valueOf(redString);
				} catch (NumberFormatException ex) {
					Logging.errorPrint("Error in CONTAINS line: " + aString);
					containerReduceWeight = Integer.valueOf(0);
				}
			}

			try {
				containerWeightCapacity = new Float(bString);
			} catch (NumberFormatException ex) {
				Logging.errorPrint("Error in CONTAINS line: " + aString);
				containerWeightCapacity = Float.valueOf(-1);
			}
		} else {
			containerWeightCapacity = Float.valueOf(-1);
		}

		if (!aTok.hasMoreTokens()) {
			limited = false;
			setAcceptsType("Any", Float.valueOf(-1));
		}

		String itemType;
		Float itemNumber;

		while (aTok.hasMoreTokens()) {
			final StringTokenizer typeTok = new StringTokenizer(aTok
					.nextToken(), "=");
			itemType = typeTok.nextToken();

			if (typeTok.hasMoreTokens()) {
				itemNumber = new Float(typeTok.nextToken());

				if (limited) {
					aFloat = new Float(aFloat.floatValue()
							+ itemNumber.floatValue());
				}
			} else {
				limited = false;
				itemNumber = Float.valueOf(-1);
			}

			if (!"Any".equals(itemType) && !"Total".equals(itemType)) {
				setAcceptsType(itemType, itemNumber);
			} else {
				setAcceptsType(itemType, itemNumber);
			}
		}

		if (!acceptsType("Total")) {
			if (!limited) {
				aFloat = Float.valueOf(-1);
			}

			setAcceptsType("Total", aFloat);
		}

		updateContainerCapacityString();
		updateContainerContentsString(aPC);
	}

	/**
	 * Gets the containerCapacityString attribute of the Equipment object
	 * 
	 * @return The containerCapacityString value
	 */
	public String getContainerCapacityString() {
		return containerCapacityString;
	}

	/**
	 * Convenience method. <p/> <br>
	 * author: Thomas Behr 27-03-02
	 * 
	 * @return <code>true</code>, if this instance is a container;
	 *         <code>false</code>, otherwise
	 */
	public boolean isContainer() {
		return acceptsChildren();
	}

}
