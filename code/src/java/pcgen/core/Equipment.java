/*
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
 */
package pcgen.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.FixedStringList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.EqModFormatCat;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.ResultFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.inst.EqSizePenalty;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.processor.ChangeArmorType;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.EqModCost;
import pcgen.core.analysis.EqModSpellInfo;
import pcgen.core.analysis.EquipmentChoiceDriver;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.character.EquipSlot;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.EquipmentFacade;
import pcgen.io.FileAccess;
import pcgen.io.exporttoken.EqToken;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.BigDecimalHelper;
import pcgen.util.JEPResourceChecker;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents Equipment for a PC.
 */
public final class Equipment extends PObject
		implements Serializable, Comparable<Object>, VariableContainer, EquipmentFacade, PCGenScoped
{

	private static final long serialVersionUID = 1;

	private static final String EQMOD_WEIGHT = "_WEIGHTADD";

	private static final String EQMOD_DAMAGE = "_DAMAGE";

	private static final SortedSet<String> S_EQUIPMENT_TYPES = new TreeSet<>();

	private AssociationSupport assocSupt = new AssociationSupport();

	private BigDecimal costMod = BigDecimal.ZERO;

	private Equipment d_parent;

	private List<Equipment> d_containedEquipment = new ArrayList<>();

	private Float carried = (float) 0; // OwnedItem

	private EquipmentLocation location = EquipmentLocation.NOT_CARRIED; // OwnedItem

	private boolean equipped; // OwnedItem

	private int numberEquipped;

	private Map<String, Float> d_childTypes = new HashMap<>();

	private String containerCapacityString = null;

	private String containerContentsString = "";

	private String appliedBonusName = "";

	private String indexedUnderType = "";

	private String wholeItemName = "";

	private String modifiedName = "";

	private String moveString = "";

	// player added note
	private String noteString = "";

	private boolean automatic;

	private boolean bonusPrimary = true;

	private boolean calculatingCost;

	private boolean weightAlreadyUsed;

	private double qty;

	private int outputIndex;

	private int outputSubindex;

	private List<String> typeListCachePrimary;

	private List<String> typeListCacheSecondary;

	private boolean usePrimaryCache;

	private boolean useSecondaryCache;

	private boolean dirty;

	private String cachedNameWithoutCharges;

	private String cachedNameWithCharges;

	/** Map of the bonuses for the object  */
	private Map<String, String> bonusMap;

	private boolean virtualItem;

	public Equipment()
	{
		final SizeAdjustment sizeAdj = SizeUtilities.getDefaultSizeAdjustment();
		if (sizeAdj != null)
		{
			put(ObjectKey.SIZE, CDOMDirectSingleRef.getRef(sizeAdj));
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
	public void setAppliedName(final String aString)
	{
		appliedBonusName = aString;
	}

	/**
	 * Get Applied Name
	 * 
	 * @return Applied name
	 */
	public String getAppliedName()
	{
		if (!appliedBonusName.isEmpty())
		{

			return " [" + appliedBonusName + "]";
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
	public boolean isAmmunition()
	{
		return isType("AMMUNITION");
	}

	/**
	 * Gets the armor attribute of the Equipment object
	 * 
	 * @return The armor value
	 */
	public boolean isArmor()
	{
		return isType("ARMOR");
	}

	/**
	 * Gets the double attribute of the Equipment object
	 * 
	 * @return The double value
	 */
	public boolean isDouble()
	{
		return isType("DOUBLE");
	}

	/**
	 * Gets the eitherType attribute of the Equipment object
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @return The eitherType value
	 */
	public boolean isEitherType(final String aType)
	{
		return isType(aType, true) || isType(aType, false);
	}

	/**
	 * Gets the extra attribute of the Equipment object
	 * 
	 * @return The extra value
	 */
	public boolean isExtra()
	{
		return isType("EXTRA");
	}

	/**
	 * Gets the heavy attribute of the Equipment object
	 * 
	 * @return The heavy value
	 */
	public boolean isHeavy()
	{
		return isType("HEAVY");
	}

	/**
	 * Gets the medium attribute of the Equipment object
	 * 
	 * @return The medium value
	 */
	public boolean isMedium()
	{
		return isType("MEDIUM");
	}

	/**
	 * Gets the light attribute of the Equipment object
	 * 
	 * @return The light value
	 */
	public boolean isLight()
	{
		return isType("LIGHT");
	}

	/**
	 * Gets the magic attribute of the Equipment object
	 * 
	 * @return The magic value
	 */
	public boolean isMagic()
	{
		return isType("MAGIC");
	}

	/**
	 * Gets the melee attribute of the Equipment object
	 * 
	 * @return The melee value
	 */
	public boolean isMelee()
	{
		return isType("MELEE");
	}

	/**
	 * Gets the monk attribute of the Equipment object
	 * 
	 * @return The monk value
	 */
	public boolean isMonk()
	{
		return isType("MONK");
	}

	/**
	 * Gets the natural weapon attribute of the Equipment object
	 * 
	 * @return The natural value
	 */
	public boolean isNatural()
	{
		return isType("NATURAL");
	}

	/**
	 * Identify if this is a primary natural weapon.
	 * @return true for a primary natural weapons, false if not natural or a secondary natural weapon.
	 */
	public boolean isPrimaryNaturalWeapon()
	{
		// The name is generated by the NATURALATTACKS token, so we place some trust in it.
		return isNatural() && modifiedName().endsWith("Primary"); //$NON-NLS-1$
	}

	/**
	 * Gets the ranged attribute of the Equipment object
	 * 
	 * @return The ranged value
	 */
	public boolean isRanged()
	{
		return isType("RANGED");
	}

	/**
	 * Gets the shield attribute of the Equipment object
	 * 
	 * @return The shield value
	 */
	public boolean isShield()
	{
		return isType("SHIELD");
	}

	/**
	 * Gets the suit attribute of the Equipment object
	 * 
	 * @return The suit value
	 */
	private boolean isSuit()
	{
		return isType("SUIT");
	}

	/**
	 * Gets the thrown attribute of the Equipment object
	 * 
	 * @return The thrown value
	 */
	public boolean isThrown()
	{
		return isType("THROWN");
	}

	/**
	 * Gets the type attribute of the Equipment object
	 * 
	 * @return The type
	 */
	@Override
	public String getType()
	{
		return getType(true);
	}

	/**
	 * Gets the type attribute of the Equipment object
	 * 
	 * @param aType
	 *            Description of the Parameter
	 * @return The type value
	 */
	@Override
	public boolean isType(final String aType)
	{
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
	public boolean isType(final String aType, final boolean bPrimary)
	{
		if (!bPrimary && !isDouble())
		{
			return false;
		}

		final List<String> tList = typeList(bPrimary);
		final String myType;

		if (aType.startsWith("TYPE=") || aType.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			myType = aType.substring(5).toUpperCase();
		}
		else
		{
			myType = aType.toUpperCase();
		}

		//
		// Must match all listed types in order to qualify
		//
		StringTokenizer tok = new StringTokenizer(myType, ".");
		if (tok.hasMoreTokens())
		{
			while (tok.hasMoreTokens())
			{
				final String type = tok.nextToken();
				//CONSIDER Faster method? Case sensitivity is a problem for containsInList
				boolean found = false;
				if (tList != null)
				{
					found = tList.stream().anyMatch(type::equalsIgnoreCase);
				}
				if (!found)
				{
					return false;
				}
			}
			return true;
		}
		return tList.contains(aType);
	}

	/**
	 * Gets the unarmed attribute of the Equipment object
	 * 
	 * @return The unarmed value
	 */
	public boolean isUnarmed()
	{
		return isType("UNARMED");
	}

	/**
	 * Gets the weapon attribute of the Equipment object
	 * 
	 * @return The weapon value
	 */
	public boolean isWeapon()
	{
		return isType("WEAPON");
	}

	/**
	 * Gets the masterwork attribute of the Equipment object
	 * 
	 * @return The masterwork value
	 */
	boolean isMasterwork()
	{
		return isType("MASTERWORK");
	}

	/**
	 * Identifies if this item is one that is sold like cash (e.g. coins, trade goods)
	 * @return true if the item is tradeable
	 */
	public boolean isSellAsCash()
	{
		return isType("Coin") || isType("Gem") //$NON-NLS-1$ //$NON-NLS-2$
			|| isType("Trade"); //$NON-NLS-1$
	}

	/**
	 * Description of the Method
	 * 
	 * @param aString
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean typeStringContains(final String aString)
	{
		return isType(aString);
	}

	/**
	 * Gets the projectile attribute of the Equipment object
	 * 
	 * @return The projectile value
	 */
	public boolean isProjectile()
	{
		// return isType("PROJECTILE");
		return isRanged() && !isThrown();
	}

	/**
	 * returns all BonusObj's that are "active"
	 * 
	 * @param aPC
	 *            PlayerCharacter used to check prereqs for bonuses
	 * @return active bonuses
	 */
	@Override
	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC)
	{
		final List<BonusObj> aList = getRawBonusList(aPC).stream().filter(aPC::isApplied).collect(Collectors.toList());

		List<EquipmentModifier> eqModList = getEqModifierList(true);

		eqModList.stream().map(eqMod -> eqMod.getActiveBonuses(this, aPC)).forEach(aList::addAll);

		eqModList = getEqModifierList(false);

		eqModList.stream().map(eqMod -> eqMod.getActiveBonuses(this, aPC)).forEach(aList::addAll);

		return aList;
	}

	/**
	 * get a list of BonusObj's of aType and aName
	 * @param pc
	 *            The PC with the Equipment 
	 * @param aType
	 *            a TYPE of bonus (such as "COMBAT" or "SKILL")
	 * @param aName
	 *            the NAME of the bonus (such as "ATTACKS" or "SPOT")
	 * @param bPrimary
	 *            used for double weapons (head1 vs head2)
	 * 
	 * @return a list of bonusObj's of aType and aName
	 */
	List<BonusObj> getBonusListOfType(PlayerCharacter pc, final String aType, final String aName,
		final boolean bPrimary)
	{

		final List<BonusObj> aList = new ArrayList<>(BonusUtilities.getBonusFromList(getBonusList(pc), aType, aName));

		getEqModifierList(bPrimary).stream()
			.map(eqMod -> BonusUtilities.getBonusFromList(eqMod.getBonusList(this), aType, aName))
			.forEach(aList::addAll);

		return aList;
	}

	//
	// Misc properties
	//

	public boolean isAutomatic()
	{
		return automatic;
	}

	/**
	 * Set Automatic
	 * 
	 * @param arg sets the isAutomatic property of the Equipment
	 */
	public void setAutomatic(final boolean arg)
	{
		automatic = arg;
	}

	/**
	 * Gets the baseItemName attribute of the Equipment object
	 * 
	 * @return The baseItemName value
	 */
	public String getBaseItemName()
	{
		CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
		if (baseItem == null)
		{
			return getKeyName();
		}
		return baseItem.get().getDisplayName();
	}

	/**
	 * Gets the keyName attribute of the base item of this Equipment object.
	 * 
	 * @return The base item's keyName value
	 */
	public String getBaseItemKeyName()
	{
		CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
		if (baseItem == null)
		{
			return getKeyName();
		}
		return baseItem.get().getKeyName();
	}

	/**
	 * Gets the cost attribute of the Equipment object
	 * 
	 * @param aPC The PC with the Equipment
	 * 
	 * @return The cost value
	 */
	public BigDecimal getCost(final PlayerCharacter aPC)
	{
		BigDecimal c = BigDecimal.ZERO;

		if (this.isVirtual())
		{
			return c;
		}

		//
		// Do pre-sizing cost increment.
		// eg. in the case of adamantine armor, want to add
		// the cost of the metal before the armor gets resized.
		//
		c = c.add(getPreSizingCostForHead(aPC, true));
		c = c.add(getPreSizingCostForHead(aPC, false));

		// c has cost of the item's modifications at the item's original size

		BigDecimal currentcost = get(ObjectKey.CURRENT_COST);
		if (currentcost == null)
		{
			currentcost = getSafe(ObjectKey.COST);
		}
		BigDecimal itemCost = currentcost.add(c);

		final List<BigDecimal> modifierCosts = new ArrayList<>();

		calculatingCost = true;
		weightAlreadyUsed = false;

		EquipmentHeadCostSummary costSum = getPostSizingCostForHead(aPC, modifierCosts, true);
		BigDecimal nonDoubleCost = costSum.nonDoubleCost;
		BigDecimal c1 = costSum.postSizeCost;
		int iPlus = costSum.headPlus;

		//
		// Get costs from lowest to highest
		//
		if (modifierCosts.size() > 1)
		{
			Collections.sort(modifierCosts);
		}

		// Note: When calculating the second head's costs we expect not to see 
		// any modifier costs and discard them if they do occur. These should be 
		// applicable for weapons, which are the only dual headed items currently.
		EquipmentHeadCostSummary altCostSum = getPostSizingCostForHead(aPC, new ArrayList<>(), false);
		nonDoubleCost = nonDoubleCost.add(altCostSum.nonDoubleCost);
		c1 = c1.add(altCostSum.postSizeCost);
		int altPlus = altCostSum.headPlus;

		calculatingCost = false;

		c1 = c1.add(getCostFromPluses(iPlus, altPlus));

		// Items with values less than 1 gp have their prices rounded up to 1 gp
		// per item
		// eg. 20 Arrows cost 1 gp, or 5 cp each. 1 MW Arrow costs 7 gp.
		//
		// Masterwork and Magical ammo is made in batches of 50, so the MW cost
		// per item should be 6 gp. This would give a cost of 6.05 gp per arrow,
		// 6.1 gp per bolt and 6.01 gp per bullet.
		//
		// if (c.compareTo(BigDecimal.ZERO) != 0)
		// {
		// //
		// // Convert to double and use math.ceil as ROUND_CEILING doesn't appear to work
		// // on BigDecimal.divide
		// final int baseQ = getBaseQty();
		// itemCost = new BigDecimal(Math.ceil(itemCost.doubleValue() / baseQ) *
		// baseQ);
		// }

		if (!isAmmunition() && !isArmor() && !isShield() && !isWeapon())
		{
			//
			// If item doesn't occupy a fixed location, then double the cost of
			// the modifications
			// DMG p.243
			//
			if (!isMagicLimitedType())
			{
				//
				// TODO: Multiple similar abilities. 100% of costliest, 75% of
				// next, and 50% of rest
				//
				if (!ignoresCostDouble())
				{
					c1 = c1.subtract(nonDoubleCost).multiply(new BigDecimal("2"));
					c1 = c1.add(nonDoubleCost);

					// c = c.multiply(new BigDecimal("2"));
				}
			}
			else
			{
				//
				// Add in the cost of 2nd, 3rd, etc. modifiers again (gives
				// times 2)
				//
				for (int i = modifierCosts.size() - 2; i >= 0; --i)
				{
					c1 = c1.add(modifierCosts.get(i));
				}
			}
		}

		// Don't allow the cost modifier to push the value further into the negatives
		if (c1.compareTo(BigDecimal.ZERO) >= 0 && c1.add(itemCost).add(costMod).compareTo(BigDecimal.ZERO) < 0)
		{
			return BigDecimal.ZERO;
		}

		return c1.add(itemCost).add(costMod);
	}

	/**
	 * Calculate the parts of the cost for the equipment's head that are 
	 * affected by size.
	 *  
	 * @param aPC The character who owns the equipment.
	 * @param primaryHead Are we calculating for the primary or alternate head.
	 * @return The cost of the head
	 */
	private BigDecimal getPreSizingCostForHead(final PlayerCharacter aPC, boolean primaryHead)
	{
		BigDecimal c = BigDecimal.ZERO;
		EquipmentHead head = getEquipmentHeadReference(primaryHead ? 1 : 2);
		if (head != null)
		{
			bonusPrimary = primaryHead;
			for (EquipmentModifier eqMod : head.getSafeListFor(ListKey.EQMOD))
			{
				int iCount = getSelectCorrectedAssociationCount(eqMod);

				if (iCount < 1)
				{
					iCount = 1;
				}

				Formula baseCost = eqMod.getSafe(FormulaKey.BASECOST);
				Number bc = baseCost.resolve(this, primaryHead, aPC, "");
				final BigDecimal eqModCost = new BigDecimal(bc.toString());
				c = c.add(
					eqModCost.multiply(new BigDecimal(Integer.toString(getSafe(IntegerKey.BASE_QUANTITY) * iCount))));
				c = c.add(
					EqModCost.addItemCosts(eqMod, aPC, "ITEMCOST", getSafe(IntegerKey.BASE_QUANTITY) * iCount, this));
			}
		}
		return c;
	}

	/**
	 * Calculate the parts of the cost for the equipment's head that are not 
	 * affected by size.
	 *  
	 * @param aPC The character who owns the equipment.
	 * @param modifierCosts The array of costs to be doubled if the location demands it
	 * @param primaryHead Are we calculating for the primary or alternate head.
	 * @return The cost, non doubling cost and total plus of the head
	 */
	private EquipmentHeadCostSummary getPostSizingCostForHead(final PlayerCharacter aPC,
		final List<BigDecimal> modifierCosts, boolean primaryHead)
	{
		EquipmentHeadCostSummary costSum = new EquipmentHeadCostSummary();
		EquipmentHead head = getEquipmentHeadReference(primaryHead ? 1 : 2);

		if (head != null)
		{
			for (EquipmentModifier eqMod : head.getSafeListFor(ListKey.EQMOD))
			{
				int iCount = getSelectCorrectedAssociationCount(eqMod);

				if (iCount < 1)
				{
					iCount = 1;
				}

				BigDecimal eqModCost;
				Formula cost = eqMod.getSafe(FormulaKey.COST);
				String costFormula = cost.toString();

				if (hasAssociations(eqMod) && !costFormula.equals(EqModCost.getCost(eqMod, getFirstAssociation(eqMod))))
				{
					eqModCost = BigDecimal.ZERO;

					for (String assoc : getAssociationList(eqMod))
					{
						String v = calcEqModCost(aPC, EqModCost.getCost(eqMod, assoc), primaryHead);
						final BigDecimal thisModCost = new BigDecimal(v);
						eqModCost = eqModCost.add(thisModCost);

						if (!EqModCost.getCostDouble(eqMod))
						{
							costSum.nonDoubleCost = costSum.nonDoubleCost.add(thisModCost);
						}
						else
						{
							modifierCosts.add(thisModCost);
						}
					}

					iCount = 1;
				}
				else
				{
					String v = calcEqModCost(aPC, cost.toString(), primaryHead);
					eqModCost = new BigDecimal(v);

					if (EqModCost.getCostDouble(eqMod))
					{
						modifierCosts.add(eqModCost);
					}
					else
					{
						costSum.nonDoubleCost = costSum.nonDoubleCost.add(eqModCost);
					}
				}

				// Per D20 FAQ adjustments for special materials are per piece;
				if (eqMod.isType("BaseMaterial"))
				{
					eqModCost = eqModCost.multiply(new BigDecimal(getSafe(IntegerKey.BASE_QUANTITY)));
				}
				costSum.postSizeCost = costSum.postSizeCost.add(eqModCost);

				costSum.headPlus += (eqMod.getSafe(IntegerKey.PLUS) * iCount);
			}
		}
		return costSum;
	}

	/**
	 * Calculates the value of a formula. Does some preprocesing for variables 
	 * that cannot be properly evaluated with just the equipment context that 
	 * is held by the variable processor.  
	 * @param aPC The character we are calculating the cost for.
	 * @param costFormula The formula to be evaluated.
	 * @param primaryHead Is the formula for an eqmod on the main (or only) head  
	 * @return The value of the formula
	 */
	private String calcEqModCost(final PlayerCharacter aPC, String costFormula, boolean primaryHead)
	{
		Pattern pat = Pattern.compile("BASECOST");
		Matcher mat = pat.matcher(costFormula);

		// make string (BASECOST/X) which will be substituted into
		// the cost string which is then converted to a number
		String sB = "(BASECOST/" + getSafe(IntegerKey.BASE_QUANTITY)
				+ ")";
		String s = mat.replaceAll(sB);

		return getVariableValue(s, "", primaryHead, aPC).toString();
	}

	/**
	 * Set cost mod
	 * 
	 * @param aString the cost modifier in String form
	 */
	public void setCostMod(final String aString)
	{

		try
		{
			costMod = new BigDecimal(aString);
		}
		catch (NumberFormatException e)
		{
			costMod = BigDecimal.ZERO;
		}
	}

	/**
	 * Set cost mod
	 * 
	 * @param aCost the cost modifier in BigDecimal form
	 */
	public void setCostMod(final BigDecimal aCost)
	{
		costMod = aCost;
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
	 *            if True then deal with the primary head
	 * @return The eqModifierKeyed value
	 */
	public EquipmentModifier getEqModifierKeyed(final Object eqModKey, final boolean bPrimary)
	{

		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		return eqModList.stream().filter(eqMod -> eqMod.getKeyName().equals(eqModKey)).findFirst().orElse(null);

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
	public List<EquipmentModifier> getEqModifierList(final boolean bPrimary)
	{
		return getEquipmentHead(bPrimary ? 1 : 2).getSafeListFor(ListKey.EQMOD);
	}

	/**
	 * Add an EquipmentModifier object to the list
	 * 
	 * @param eqMod
	 *            The equipment modifier to add to list
	 * @param bPrimary
	 *            if True then deal with the primary head
	 */
	public void addToEqModifierList(final EquipmentModifier eqMod, final boolean bPrimary)
	{
		if (bPrimary)
		{
			usePrimaryCache = false;
		}
		else
		{
			useSecondaryCache = false;
		}
		eqMod.setVariableParent(this);
		getEquipmentHead(bPrimary ? 1 : 2).addToListFor(ListKey.EQMOD, eqMod);
		setDirty(true);
	}

	/**
	 * Get display information for all "interesting" properties.
	 * 
	 * @param aPC The PC with the Equipment
	 * 
	 * @return display string of bonuses and special properties
	 */
	public String getInterestingDisplayString(final PlayerCharacter aPC)
	{
		final StringBuilder s = new StringBuilder(100);
		String t = getSpecialProperties(aPC);

		getActiveBonuses(aPC).stream().map(BonusObj::toString)
			.filter(eqBonus -> (!eqBonus.isEmpty()) && !eqBonus.startsWith("EQM")).forEach(eqBonus -> {
				if (s.length() != 0)
				{
					s.append(", ");
				}
				s.append(eqBonus);
			});

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
		if (!t.isEmpty())
		{
			if (s.length() != 0)
			{
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
	 *            The PC with the Equipment
	 */
	public void setIsEquipped(final boolean aFlag, final PlayerCharacter aPC)
	{

		equipped = aFlag;

		if (equipped)
		{
			activateBonuses(aPC);
		}
		else
		{
			BonusActivation.deactivateBonuses(this, aPC);
		}
	}

	/**
	 * Get the item name based off the modifiers
	 * 
	 * @return item name based off the modifiers
	 */
	public String getItemNameFromModifiers()
	{
		return getItemNameFromModifiers(getBaseItemName());
	}

	/**
	 * Get the item name based off the modifiers
	 * 
	 * @param baseName base name of the object, may instead be the base key if generating a key
	 * @return item name based off the modifiers
	 */
	private String getItemNameFromModifiers(String baseName)
	{
		CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
		if (baseItem == null)
		{
			return getName();
		}
		final List<EquipmentModifier> modList;
		EquipmentHead head = getEquipmentHeadReference(1);
		if (head == null)
		{
			modList = Collections.emptyList();
		}
		else
		{
			modList = head.getSafeListFor(ListKey.EQMOD);
		}
		EquipmentHead althead = getEquipmentHeadReference(2);
		final List<EquipmentModifier> altModList;
		if (althead == null)
		{
			altModList = Collections.emptyList();
		}
		else
		{
			altModList = althead.getSafeListFor(ListKey.EQMOD);
		}
		final List<EquipmentModifier> commonList = new ArrayList<>();
		final List<List<EquipmentModifier>> modListByFC = initSplitModList();
		final List<List<EquipmentModifier>> altModListByFC = initSplitModList();
		final List<List<EquipmentModifier>> commonListByFC = initSplitModList();
		final Equipment baseEquipment = baseItem.get();
		// Remove any modifiers on the base item so they don't confuse the
		// naming
		if (baseEquipment != null)
		{
			modList.removeAll(baseEquipment.getEqModifierList(true));
			altModList.removeAll(baseEquipment.getEqModifierList(false));
		}
		for (Iterator<EquipmentModifier> it = modList.iterator(); it.hasNext();)
		{
			EquipmentModifier eqMod = it.next();
			if (eqMod.getSafe(ObjectKey.VISIBILITY).equals(Visibility.HIDDEN))
			{
				it.remove();
			}
		}
		extractListFromCommon(commonList, modList);
		removeCommonFromList(altModList, commonList, "eqMod expected but not found: ");
		// Remove masterwork from the list if magic is present
		suppressMasterwork(commonList);
		// Split the eqmod lists by format category
		splitModListByFormatCat(commonList, commonListByFC);
		splitModListByFormatCat(modList, modListByFC);
		splitModListByFormatCat(altModList, altModListByFC);
		final StringBuilder itemName = new StringBuilder(100);
		// Add in front eq mods
		int fcf = EqModFormatCat.FRONT.ordinal();
		itemName.append(buildEqModDesc(commonListByFC.get(fcf), modListByFC.get(fcf), altModListByFC.get(fcf)));
		if (itemName.length() > 0)
		{
			itemName.append(' ');
		}
		// Add in the base name, less any modifiers
		baseName = baseName.trim();
		int idx = baseName.indexOf('(');
		if (idx >= 0)
		{
			itemName.append(baseName.substring(0, idx - 1).trim());
		}
		else
		{
			itemName.append(baseName);
		}
		// Add in middle mods
		int fcm = EqModFormatCat.MIDDLE.ordinal();
		String eqmodDesc1 = buildEqModDesc(commonListByFC.get(fcm), modListByFC.get(fcm), altModListByFC.get(fcm));
		if (!eqmodDesc1.isEmpty())
		{
			itemName.append(' ').append(eqmodDesc1);
		}
		// Tack on the original modifiers
		if (idx >= 0)
		{
			itemName.append(' ');
			itemName.append(baseName.substring(idx));
		}
		// Strip off the ending ')' in anticipation of more modifiers
		final int idx1 = itemName.toString().lastIndexOf(')');
		if (idx1 >= 0)
		{
			itemName.setLength(idx1);
			itemName.append('/');
		}
		else
		{
			itemName.append(" (");
		}
		//
		// Put size in name if not the same as the base item
		//
		SizeAdjustment thisSize = getSizeAdjustment();
		if (!getSafe(ObjectKey.BASESIZE).get().equals(thisSize))
		{
			itemName.append(thisSize.getDisplayName());
			itemName.append('/');
		}
		// Put in parens mods
		int fcp = EqModFormatCat.PARENS.ordinal();
		itemName.append(buildEqModDesc(commonListByFC.get(fcp), modListByFC.get(fcp), altModListByFC.get(fcp)));
		//
		// If there were no modifiers, then drop the trailing '/'
		//
		if (itemName.toString().endsWith("/") || itemName.toString().endsWith(";"))
		{
			itemName.setLength(itemName.length() - 1);
		}
		itemName.append(')');
		// If there were no modifiers, then strip the empty parenthesis
		final int idx2 = itemName.toString().indexOf(" ()");
		if (idx2 >= 0)
		{
			itemName.setLength(idx2);
		}
		return itemName.toString();
	}
	/**
	 * Where a magic eqmod is present, remove the masterwork eqmod from the
	 * list.
	 * 
	 * @param commonList
	 *            The list of eqmods on both heads (or only head)
	 */
	private void suppressMasterwork(Collection<EquipmentModifier> commonList)
	{
		// Look for a modifier named "masterwork" (assumption: this is marked as
		// "assigntoall")
		EquipmentModifier eqMaster = commonList.stream()
			.filter(eqMod -> "MASTERWORK".equalsIgnoreCase(eqMod.getDisplayName()) || eqMod.isIType(Type.MASTERWORK))
			.findFirst().orElse(null);

		if (eqMaster == null)
		{
			return;
		}
		if (heads.stream().anyMatch(head -> getMagicBonus(head.getListFor(ListKey.EQMOD)) != null))
		{
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
	private String buildEqModDesc(List<EquipmentModifier> commonList, List<EquipmentModifier> modList,
		List<EquipmentModifier> altModList)
	{
		StringBuilder desc = new StringBuilder(250);

		String commonDesc = getNameFromModifiers(commonList);
		String modDesc = getNameFromModifiers(modList);
		String altModDesc = getNameFromModifiers(altModList);

		if ((modList.isEmpty()) && (altModList.isEmpty()))
		{
			desc.append(commonDesc);
		}
		else if (!isDouble())
		{
			desc.append(modDesc);
			if (!modList.isEmpty() && !commonList.isEmpty())
			{
				desc.append('/');
			}
			desc.append(commonDesc);
		}
		else
		{
			if (!commonDesc.isEmpty())
			{
				desc.append(commonDesc).append(';');
			}

			if (!modDesc.isEmpty())
			{
				desc.append(modDesc);
			}
			else
			{
				desc.append('-');
			}

			desc.append(';');

			if (!altModDesc.isEmpty())
			{
				desc.append(altModDesc);
			}
			else
			{
				desc.append('-');
			}
		}
		return desc.toString();
	}

	/**
	 * OwnedItem Sets the location attribute of the Equipment object
	 * @param newLocation
	 *            EquipmentLocation containing the new location value
	 */
	public void setLocation(final EquipmentLocation newLocation)
	{
		if (EquipmentLocation.CONTAINED.equals(newLocation))
		{
			location = EquipmentLocation.CARRIED_NEITHER;
		}
		else
		{
			location = newLocation;
		}
		equipped = location.isEquipped();
	}

	/**
	 * OwnedItem Gets the hand attribute of the Equipment object
	 * 
	 * @return EquipmentLocation containing the location value
	 */
	public EquipmentLocation getLocation()
	{
		return location;
	}

	/**
	 * Get maximum charges
	 * 
	 * @return maximum charges
	 */
	public int getMaxCharges()
	{
		return getEqModifierList(true).stream().map(eqMod -> eqMod.get(IntegerKey.MAX_CHARGES))
			.filter(max -> max != null && max > 0).findFirst().orElse(0);

	}

	/**
	 * Get minimum charges
	 * 
	 * @return minimum charges
	 */
	public int getMinCharges()
	{
		return getEqModifierList(true).stream().map(eqMod -> eqMod.get(IntegerKey.MIN_CHARGES)).filter(Objects::nonNull)
			.findFirst().orElse(0);

	}

	/**
	 * Set the name (sets keyname also)
	 *
	 * @param aString The new name
	 */

	@Override
	public void setName(final String aString)
	{
		super.setName(aString);
		setDirty(true);
	}

	/**
	 * Sets the modifiedName attribute of the Equipment object
	 * 
	 * @param nameString
	 *            The new modifiedName value
	 */
	public void setModifiedName(final String nameString)
	{
		modifiedName = nameString;
		setDirty(true);
	}

	/**
	 * Sets the moveString attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new moveString value
	 */
	private void setMoveString(final String aString)
	{
		moveString = aString;
	}

	/**
	 * Gets the name attribute of the Equipment object. Note 
	 * this is separate from toStirng to avoid side effects on keys.
	 * 
	 * @return The name value
	 */
	public String getName()
	{
		final StringBuilder buffer = new StringBuilder(100);

		buffer.append(getDisplayName());
		if (!modifiedName.isEmpty())
		{
			buffer.append(" (").append(modifiedName).append(")");
		}

		return buffer.toString();
	}

	/**
	 * set's the player added note for this item
	 * 
	 * @param aString the value of the note
	 */
	public void setNote(final String aString)
	{
		noteString = aString;
	}

	/**
	 * return the player added note for this item
	 * 
	 * @return note
	 */
	public String getNote()
	{
		return noteString;
	}

	/**
	 * Sets the numberCarried attribute of the Equipment object.
	 * 
	 * @param aNumber
	 *            The new numberCarried value
	 */
	public void setNumberCarried(final Float aNumber)
	{
		carried = aNumber;
	}

	/**
	 * Sets the numberEquipped attribute of the Equipment object.
	 * @param num
	 *            The new numberEquipped value
	 */
	public void setNumberEquipped(final int num)
	{
		numberEquipped = num;

		if (num > 0)
		{
			equipped = true;
		}
	}

	/**
	 * Gets the numberEquipped attribute of the Equipment object.
	 * 
	 * @return The numberEquipped value
	 */
	public int getNumberEquipped()
	{
		return numberEquipped;
	}

	/**
	 * Set this item's output index, which controls the order in which the
	 * equipment appears on a character sheet. Note: -1 means hidden and 0 means
	 * not set <p> <br>
	 * author: James Dempsey 17-Jun-02
	 * 
	 * @param newIndex
	 *            the new output index for this equipment item (-1=hidden, 0=not
	 *            set)
	 */
	public void setOutputIndex(final int newIndex)
	{
		outputIndex = newIndex;
	}

	/**
	 * Return the output index, which controls the order in which the equipment
	 * appears on a character sheet. Note: -1 means hidden and 0 means not set
	 * <p> <br>
	 * author: James Dempsey 17-Jun-02
	 * 
	 * @return the output index for this equipment item (-1=hidden, 0=not set)
	 */
	public int getOutputIndex()
	{
		return outputIndex;
	}

	/**
	 * Set this item's output subindex, which controls the order in which
	 * equipment with the same output index appears on a character sheet. This
	 * basically applies to natural weapons only, since they have output index 0
	 * <p> <br>
	 * author: Stefan Radermacher 11-Feb-05
	 * 
	 * @param newSubindex
	 *            the new output subindex for this equipment item
	 */
	public void setOutputSubindex(final int newSubindex)
	{
		outputSubindex = newSubindex;
	}

	/**
	 * Return the output subindex, which controls the order in which equipment
	 * with the same output index appears on a character sheet. This basically
	 * applies to natural weapons only, since they have output index 0 <p> <br>
	 * author: Stefan Radermacher 11-Feb-05
	 * 
	 * @return the output subindex for this equipment item
	 */
	public int getOutputSubindex()
	{
		return outputSubindex;
	}

	/**
	 * Sets the parent attribute of the Equipment object
	 * 
	 * @param parent
	 *            The new parent value
	 */
	public void setParent(final Equipment parent)
	{
		d_parent = parent;
	}

	/**
	 * Gets the parent of the Equipment object
	 * 
	 * @return The parent
	 */
	public Equipment getParent()
	{
		return d_parent;
	}

	/**
	 * Gets the parentName of the Equipment object
	 * 
	 * @return The parentName
	 */
	public String getParentName()
	{
		final Equipment anEquip = getParent();

		if (anEquip != null)
		{
			return anEquip.toString();
		}

		if (isEquipped())
		{
			return "Equipped";
		}

		if (numberCarried().intValue() > 0)
		{
			return "Carried";
		}

		return "";
	}

	/**
	 * Callback function from PObject.passesPreReqToGainForList()
	 * 
	 * @param aType The string to be tested for PRETYPEness
	 *              PRETYPE:EQMODTYPE=MagicalEnhancement
	 *              PRETYPE:[EQMOD=Holy],EQMOD=WEAP+5
	 *              PRETYPE:.IF.TYPE=Armor.Shield.Weapon.THEN.EQMODTYPE=MagicalEnhancement.ELSE.
	 *
	 * @return true if the Equipment's types match the aType string
	 */
	public boolean isPreType(String aType)
	{

		String tString = aType;

		// PRETYPE:EQMODTYPE=MagicalEnhancement
		// PRETYPE:[EQMOD=Holy],EQMOD=WEAP+5
		// PRETYPE:.IF.TYPE=Armor.Shield.Weapon.THEN.EQMODTYPE=MagicalEnhancement.ELSE.

		if (tString.startsWith(".IF.TYPE="))
		{

			final StringTokenizer aTok = new StringTokenizer(tString.substring(9), ".");

			int idx = tString.indexOf(".THEN.");

			if (idx < 0)
			{
				return false;
			}

			String truePart = tString.substring(idx + 6);
			int idx1 = truePart.indexOf(".ELSE.");

			String falsePart = "";
			if (idx1 >= 0)
			{
				falsePart = truePart.substring(idx1 + 6);
				truePart = truePart.substring(0, idx1);
			}

			boolean typeFound = false;
			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();

				if (isType(aString, bonusPrimary))
				{
					typeFound = true;

					break;
				}
			}

			if (typeFound)
			{
				tString = truePart;
			}
			else
			{
				tString = falsePart;
			}

			if (tString.isEmpty())
			{
				return true;
			}
		}

		if (tString.startsWith("EQMODTYPE=") || tString.startsWith("EQMODTYPE."))
		{
			tString = tString.substring(10);

			for (EquipmentModifier eqMod : getEqModifierList(bonusPrimary))
			{
				if (eqMod.isType(tString))
				{
					return true;
				}
			}

			return false;
		}
		else if (tString.startsWith("EQMOD=") || tString.startsWith("EQMOD."))
		{
			String key = tString.substring(6);
			String choice = "";
			if (key.indexOf('(') > 0)
			{
				int i = key.indexOf('(');
				choice = key.substring(i + 1, key.lastIndexOf(')'));
				key = key.substring(0, i);
			}

			EquipmentModifier eqMod = getEqModifierKeyed(key, bonusPrimary);
			if (eqMod != null)
			{
				if (StringUtils.isEmpty(choice))
				{
					return true;
				}
				return (hasAssociations(eqMod) && choice.equalsIgnoreCase(getFirstAssociation(eqMod)));
			}
			return false;
		}

		return isType(tString, bonusPrimary);
	}

	/**
	 * Sets the qty attribute of the Equipment object
	 * 
	 * @param aString
	 *            The new qty value
	 */
	public void setQty(final String aString)
	{
		try
		{
			setQty(Double.parseDouble(aString));
		}
		catch (NumberFormatException nfe)
		{
			qty = 0.0;
		}
	}

	/**
	 * Sets the qty attribute of the Equipment object
	 * 
	 * @param aFloat
	 *            The new qty value
	 */
	public void setQty(final Float aFloat)
	{
		setQty(aFloat.doubleValue());
	}

	/**
	 * Get the quantity of items
	 * 
	 * @return return a Float of the quantity
	 */
	public Float getQty()
	{
		return (float) qty;
	}

	/**
	 * Gets the rawCritRange attribute of the Equipment object
	 * 
	 * @param bPrimary
	 *            True=Primary Head
	 * @return The rawCritRange value
	 * @deprecated due to CRITRANGE code control
	 */
	@Deprecated
	public int getRawCritRange(final boolean bPrimary)
	{
		int range = getHeadInfo(bPrimary ? 1 : 2, IntegerKey.CRIT_RANGE);
		if (range == 0)
		{
			String cr = getWeaponInfo("CRITRANGE", bPrimary);
			if (!cr.isEmpty())
			{
				try
				{
					range = Integer.parseInt(cr);
				}
				catch (NumberFormatException ignore)
				{
					//ignore
				}
			}
		}
		return range;
	}

	/**
	 * Get the raw special properties
	 * 
	 * @return raw special propertie
	 */
	@Override
	public String getRawSpecialProperties()
	{
		//CONSIDER standardize this with other joins?
		final StringBuilder retString = new StringBuilder(200);
		boolean first = true;
		for (SpecialProperty sprop : getSafeListFor(ListKey.SPECIAL_PROPERTIES))
		{
			if (!first)
			{
				retString.append(", ");
			}
			first = false;
			retString.append(sprop.getText());
		}
		return retString.toString();
	}

	/**
	 * Set the remaining charges
	 * 
	 * @param remainingCharges The number of charges remaining
	 */
	public void setRemainingCharges(final int remainingCharges)
	{

		for (EquipmentModifier eqMod : getEqModifierList(true))
		{

			Integer min = eqMod.get(IntegerKey.MIN_CHARGES);

			if (min != null && min > 0)
			{
				EqModSpellInfo.setRemainingCharges(this, eqMod, remainingCharges);
			}
		}
	}

	/**
	 * Get the remaining charges
	 * 
	 * @return remaining charges
	 */
	public int getRemainingCharges()
	{
		for (EquipmentModifier eqMod : getEqModifierList(true))
		{
			Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
			if (min != null && min > 0)
			{
				return EqModSpellInfo.getRemainingCharges(this, eqMod);
			}
		}

		return -1;
	}

	/**
	 * Gets the simple name attribute of the Equipment object
	 * 
	 * @return The name value
	 */
	public String getSimpleName()
	{
		return getDisplayName();
	}

	/**
	 * Gets the size attribute of the Equipment object
	 * 
	 * @return The size value
	 */
	public String getSize()
	{
		return getSizeAdjustment().getKeyName();
	}

	public SizeAdjustment getSizeAdjustment()
	{
		return getSafe(ObjectKey.SIZE).get();
	}

	/**
	 * The number of "Slots" that this item requires The slot type is derived
	 * from system/special/equipmentslot.lst
	 * 
	 * @param aPC the PC with the Equipment
	 * @return slots
	 */
	public int getSlots(final PlayerCharacter aPC)
	{
		int iSlots = getSafe(IntegerKey.SLOTS);

		EquipmentHead head = getEquipmentHeadReference(1);
		if (head != null)
		{
			for (EquipmentModifier eqMod : head.getSafeListFor(ListKey.EQMOD))
			{
				iSlots += (int) eqMod.bonusTo(aPC, "EQM", "HANDS", this);
				iSlots += (int) eqMod.bonusTo(aPC, "EQM", "SLOTS", this);
			}
		}

		if (iSlots < 0)
		{
			iSlots = 0;
		}

		return iSlots;
	}

	public String getSlot()
	{
		return SystemCollections.getUnmodifiableEquipSlotList().stream().filter(es -> es.canContainType(getType()))
			.findFirst().map(EquipSlot::getSlotName).orElse(null);
	}

	/**
	 * Returns special properties of an Equipment.
	 * 
	 * @param aPC The PC with the Equipment
	 * @return special properties of an Equipment.
	 */
	public String getSpecialProperties(final PlayerCharacter aPC)
	{
		final List<EquipmentModifier> modList;
		EquipmentHead head = getEquipmentHeadReference(1);
		if (head == null)
		{
			modList = Collections.emptyList();
		}
		else
		{
			modList = head.getSafeListFor(ListKey.EQMOD);
		}

		EquipmentHead althead = getEquipmentHeadReference(2);
		final List<EquipmentModifier> altModList;
		if (althead == null)
		{
			altModList = Collections.emptyList();
		}
		else
		{
			altModList = althead.getSafeListFor(ListKey.EQMOD);
		}
		final List<EquipmentModifier> comn = new ArrayList<>();

		extractListFromCommon(comn, modList);

		removeCommonFromList(altModList, comn, "SPROP: eqMod expected but not found: ");

		final String common = StringUtil.join(getSpecialAbilityTimesList(getSpecialAbilityList(comn, aPC)), ", ");
		final String saList1 = StringUtil.join(getSpecialAbilityTimesList(getSpecialAbilityList(modList, aPC)), ", ");
		final String saList2 =
				StringUtil.join(getSpecialAbilityTimesList(getSpecialAbilityList(altModList, aPC)), ", ");
		final StringBuilder sp = new StringBuilder(200);

		boolean first = true;
		for (SpecialProperty sprop : getSafeListFor(ListKey.SPECIAL_PROPERTIES))
		{
			final String text = sprop.getParsedText(aPC, this, this);
			if (!"".equals(text))
			{
				if (!first)
				{
					sp.append(", ");
				}
				first = false;
				sp.append(text);
			}
		}

		if (!common.isEmpty())
		{
			if (!first)
			{
				sp.append(", ");
			}
			first = false;

			sp.append(common);
		}

		if (!saList1.isEmpty())
		{
			if (!first)
			{
				sp.append(", ");
			}
			first = false;

			if (isDouble())
			{
				sp.append("Head1: ");
			}

			sp.append(saList1);
		}

		if (isDouble() && (!saList2.isEmpty()))
		{
			if (!first)
			{
				sp.append(", ");
			}

			sp.append("Head2: ").append(saList2);
		}

		return sp.toString();
	}

	/**
	 * Gets the uberParent attribute of the Equipment object
	 * 
	 * @return The uberParent value
	 */
	public Equipment getUberParent()
	{
		if (getParent() == null)
		{
			return this;
		}

		Equipment anEquip = getParent();

		while (anEquip.getParent() != null)
		{
			anEquip = anEquip.getParent();
		}

		return anEquip;
	}

	/**
	 * Get used charges
	 * 
	 * @return used charges
	 */
	public int getUsedCharges()
	{
		for (EquipmentModifier eqMod : getEqModifierList(true))
		{
			Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
			if (min != null && min > 0)
			{
				return EqModSpellInfo.getUsedCharges(this, eqMod);
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
	 * @param src The Source of the variable
	 * @param aPC
	 *            The PC this equipment is associated with
	 * 
	 * @return the value of the variable
	 */
	@Override
	public Float getVariableValue(final String varName, final String src, final PlayerCharacter aPC)
	{
		return getVariableValue(varName, src, bonusPrimary, aPC);
	}

	/**
	 * Get the value of a variable passed as aString. This uses a different
	 * variable processor than Player character because equipment has different
	 * "hard coded" variables than a Player Character.
	 * 
	 * @param varName
	 *            The name of the variable to look up
	 * @param src The Source of the variable
	 * @param bPrimary
	 *            If the head of the weapon has any effect on the variable
	 *            value, this flag stipulates which head to use (true means use
	 *            the primary head).
	 * @param aPC
	 *            The PC this equipment is associated with
	 * 
	 * @return The value of the variable
	 */
	public Float getVariableValue(String varName, final String src, final boolean bPrimary, final PlayerCharacter aPC)
	{
		VariableProcessor vp = new VariableProcessorEq(this, aPC, bPrimary);
		return vp.getVariableValue(null, varName, src, 0);
	}

	/**
	 * Returns true if the equipment modifier is visible
	 * 
	 * @param eqMod
	 *            The equipment modifier
	 * @return The visible value
	 */
	public boolean isVisible(final EquipmentModifier eqMod, View v)
	{
		Visibility vis = eqMod.getSafe(ObjectKey.VISIBILITY);

		if (Visibility.QUALIFY.equals(vis))
		{
			bonusPrimary = true;
			if (PrereqHandler.passesAll(eqMod, this, null))
			{
				return true;
			}
			//
			// Check the secondary head if the primary head doesn't qualify (and
			// the item has a secondary head)
			//
			if (isDouble())
			{
				bonusPrimary = false;
				return PrereqHandler.passesAll(eqMod, this, null);
			}
			return false;
		}

		return vis.isVisibleTo(v);
	}

	/**
	 * Returns true if the equipment modifier is visible
	 * 
	 * @param eqMod
	 *            The equipment modifier
	 * @param primaryHead
	 * 			  Is this for the main head (true), or the secondary one (false)?
	 * @return The visible value
	 */
	public boolean isVisible(PlayerCharacter pc, EquipmentModifier eqMod, boolean primaryHead, View v)
	{
		Visibility vis = eqMod.getSafe(ObjectKey.VISIBILITY);

		if (Visibility.QUALIFY.equals(vis))
		{
			bonusPrimary = primaryHead;
			return PrereqHandler.passesAll(eqMod, this, pc);
		}

		return vis.isVisibleTo(v);
	}

	/**
	 * Gets the weight attribute of the Equipment object.
	 * 
	 * @param aPC The PC that has this Equipment
	 * 
	 * @return The weight value
	 */
	public Float getWeight(final PlayerCharacter aPC)
	{
		if (virtualItem)
		{
			return (float) 0.0;
		}
		return (float) getWeightAsDouble(aPC);
	}

	/**
	 * get base weight as double
	 * 
	 * @return base weight (as a double)
	 */
	private BigDecimal getBaseWeight()
	{
		if (this.isVirtual())
		{
			return BigDecimal.ZERO;
		}
		return getWeightInPounds().add(getSafe(ObjectKey.WEIGHT_MOD));
	}

	/**
	 * Get the weight as a double
	 * 
	 * @param aPC The PC that has this Equipment
	 * @return weight as double
	 */
	public double getWeightAsDouble(final PlayerCharacter aPC)
	{
		if (isVirtual())
		{
			return 0.0;
		}

		double d1 = bonusTo(aPC, "EQM", "WEIGHTMULT", true);

		double aWeight = getWeightInPounds().doubleValue();

		if (!CoreUtility.doublesEqual(d1, 0.0))
		{
			aWeight *= d1;
		}

		double d2 = bonusTo(aPC, "EQM", "WEIGHTDIV", true);

		if (!CoreUtility.doublesEqual(d2, 0))
		{
			aWeight /= d2;
		}

		aWeight += bonusTo(aPC, "EQM", "WEIGHTADD", true);
		aWeight += getSafe(ObjectKey.WEIGHT_MOD).doubleValue();

		return aWeight;
	}

	/**
	 * Get wield
	 * 
	 * @return wield
	 */
	public String getWieldName()
	{
		WieldCategory wield = get(ObjectKey.WIELD);
		return wield == null ? "" : wield.getKeyName();
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC The PC that has this Equipment
	 * 
	 * @return Description of the Return Value
	 * @deprecated due to ACCHECK code control
	 */
	@Deprecated
	public Integer preFormulaAcCheck(final PlayerCharacter aPC)
	{
		return Math.min(getSafe(IntegerKey.AC_CHECK) + (int) bonusTo(aPC, "EQMARMOR", "ACCHECK", true), 0);
	}

	/**
	 * Returns true if the Equipment can take children.
	 * 
	 * @return true if the Equipment can take children.
	 */
	public boolean isContainer()
	{
		return get(ObjectKey.CONTAINER_WEIGHT_CAPACITY) != null;
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
	 * @param isLoading Is the equipment item being loaded currently. 
	 */
	private void addEqModifier(final String aString, final boolean bPrimary, final boolean isLoading)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		// The type of EqMod, eg: ABILITYPLUS
		final String eqModKey = aTok.nextToken();

		EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);

		// If not already attached, then add a new one
		if (eqMod == null)
		{
			if (eqModKey.equals(EQMOD_WEIGHT))
			{
				if (aTok.hasMoreTokens())
				{
					put(ObjectKey.WEIGHT_MOD, new BigDecimal(aTok.nextToken().replace(',', '.')));
				}
				return;
			}

			if (eqModKey.equals(EQMOD_DAMAGE))
			{
				if (aTok.hasMoreTokens())
				{
					put(StringKey.DAMAGE_OVERRIDE, aTok.nextToken());
				}
				return;
			}

			eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(EquipmentModifier.class,
				eqModKey);

			if (eqMod == null)
			{
				Logging.errorPrint("Could not find EquipmentModifier: " + eqModKey);

				return;
			}

			// only make a copy if we need to
			// add qualifiers to modifier
			if (!eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
			{
				eqMod = eqMod.clone();
			}

			addToEqModifierList(eqMod, bPrimary);
		}

		// Add the associated choices
		if (!eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
		{
			while (aTok.hasMoreTokens())
			{
				final String x = aTok.nextToken();
				Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
				if (min != null && min > 0
					|| (eqMod.getSafe(StringKey.CHOICE_STRING).startsWith("EQBUILDER") && !isLoading))
				{
					// We clear the associated info to avoid a buildup of info
					// like number of charges.
					removeAllAssociations(eqMod);
				}
				addAssociation(eqMod, x.replace('=', '|'));
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
	public void addEqModifier(final EquipmentModifier eqMod, final boolean bPrimary, final PlayerCharacter aPC)
	{
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
	void addEqModifier(final EquipmentModifier eqMod, final boolean bPrimary, final PlayerCharacter aPC,
		final String selectedChoice, final EquipmentChoice equipChoice)
	{
		boolean bImporting = false;

		if ((aPC != null) && aPC.isImporting())
		{
			bImporting = true;
		}

		if (!bImporting && !canAddModifier(aPC, eqMod, bPrimary))
		{
			return;
		}

		List<CDOMSingleRef<EquipmentModifier>> replaces = eqMod.getListFor(ListKey.REPLACED_KEYS);

		EquipmentHead head = getEquipmentHead(bPrimary ? 1 : 2);
		if (replaces != null)
		{
			//
			// Remove any modifiers that this one will replace
			//
			replaces.stream().map(CDOMSingleRef::get).map(CDOMObject::getKeyName)
				.forEach(key -> head.getSafeListFor(ListKey.EQMOD).stream()
					.filter(aMod -> key.equalsIgnoreCase(aMod.getKeyName())).forEach(aMod -> {
						head.removeFromListFor(ListKey.EQMOD, aMod);
						if (bPrimary)
						{
							usePrimaryCache = false;
						}
						else
						{
							useSecondaryCache = false;
						}
						setDirty(true);
					}));
		}

		if (eqMod.isType("BaseMaterial"))
		{
			head.getSafeListFor(ListKey.EQMOD).stream().filter(aMod -> aMod.isType("BaseMaterial")).forEach(aMod -> {
				head.removeFromListFor(ListKey.EQMOD, aMod);
				if (bPrimary)
				{
					usePrimaryCache = false;
				}
				else
				{
					useSecondaryCache = false;
				}
				setDirty(true);
			});
		}
		else if (eqMod.isType("MagicalEnhancement"))
		{
			head.getSafeListFor(ListKey.EQMOD).stream().filter(aMod -> aMod.isType("MagicalEnhancement"))
				.forEach(aMod -> {
					head.removeFromListFor(ListKey.EQMOD, aMod);
					if (bPrimary)
					{
						usePrimaryCache = false;
					}
					else
					{
						useSecondaryCache = false;
					}
				});
		}

		//
		// Add the modifier if it's not already there
		//
		EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);

		if (aMod == null)
		{
			//
			// only make a copy if we need to add qualifiers to modifier
			//
			if (eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
			{
				aMod = eqMod;
			}
			else
			{
				aMod = eqMod.clone();

				if (aMod == null)
				{
					return;
				}
			}

			addToEqModifierList(aMod, bPrimary);
		}

		//
		// If a choice is required, either get a response from user or
		// apply the provided choice.
		// Remove the modifier if all associated choices are deleted
		//
		if (!bImporting)
		{
			boolean allRemoved = false;
			if (selectedChoice != null && !selectedChoice.isEmpty())
			{
				if (!eqMod.getSafe(StringKey.CHOICE_STRING).startsWith("EQBUILDER."))
				{
					EquipmentChoiceDriver.setChoice(this, aMod, selectedChoice, equipChoice);
					allRemoved = !hasAssociations(aMod);
				}
			}
			else if (!EquipmentChoiceDriver.getChoice(1, this, aMod, true, aPC))
			{
				allRemoved = true;
			}

			if (allRemoved)
			{
				head.removeFromListFor(ListKey.EQMOD, aMod);
				if (bPrimary)
				{
					usePrimaryCache = false;
				}
				else
				{
					useSecondaryCache = false;
				}
			}
		}

		setBase();
	}

	/**
	 * Add a list equipment modifiers and their associated information eg:
	 * Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6 <p> Adds a feature to the
	 * EqModifiers attribute of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be added to the EqModifiers attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifiers attribute
	 */
	public void addEqModifiers(final String aString, final boolean bPrimary)
	{
		addEqModifiers(aString, bPrimary, false);
	}

	/**
	 * Add a list equipment modifiers and their associated information eg:
	 * Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6 <p> Adds a feature to the
	 * EqModifiers attribute of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be added to the EqModifiers attribute
	 * @param bPrimary
	 *            The feature to be added to the EqModifiers attribute
	 * @param isLoading Is the equipment item being loaded currently. 
	 */
	private void addEqModifiers(final String aString, final boolean bPrimary, final boolean isLoading)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");

		while (aTok.hasMoreTokens())
		{
			final String aEqModName = aTok.nextToken();

			if (!aEqModName.equalsIgnoreCase(Constants.NONE))
			{
				addEqModifier(aEqModName, bPrimary, isLoading);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param aPC
	 *            The PC that has this Equipment
	 * @param aType
	 *            a TYPE of BONUS (such as "COMBAT" or "AC")
	 * @param aName
	 *            the NAME of the BONUS (such as "ATTACKS" or "ARMOR")
	 * @param bPrimary
	 *            should we ask the parent object also?
	 * @return returns a double which is the sum of all bonuses
	 */
	public double bonusTo(final PlayerCharacter aPC, final String aType, final String aName, final boolean bPrimary)
	{
		return bonusTo(aPC, aType, aName, this, bPrimary);
	}

	/**
	 * Add bonuses
	 * 
	 * @param aPC
	 *          The PC that has this Equipment
	 * @param aType
	 *          The type of the Bonus
	 * @param aName
	 *          The name of the Bonus
	 * @param anObj
	 *          An object used in the bonus calculations, should be a 
	 *          PC or a piece of Equipment.
	 * @param bPrimary
	 *          If true get bonuses for primary head
	 * @return bonus
	 */
	private double bonusTo(final PlayerCharacter aPC, final String aType, final String aName, final Object anObj,
		final boolean bPrimary)
	{

		final String aBonusKey = aType.toUpperCase() + '.'
				+ aName.toUpperCase()
				+ '.';

		// go through bonus hashmap and zero out all
		// entries that deal with this bonus request
		getBonusMap().keySet().stream().filter(aKey -> aKey.startsWith(aBonusKey))
			.forEach(aKey -> putBonusMap(aKey, "0"));

		bonusPrimary = bPrimary;

		if (bPrimary)
		{
			BonusCalc.equipBonusTo(this, aType, aName, aPC);

			// now do temp bonuses
			final List<BonusObj> tbList = getTempBonusList().stream().distinct().collect(Collectors.toList());

			BonusCalc.bonusTo(this, aType, aName, anObj, tbList, aPC);
		}

		// If using 3.5 weapon penalties, add them in also
		if (Globals.checkRule(RuleConstants.SYS_35WP))
		{
			for (EqSizePenalty esp : Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(EqSizePenalty.class))
			{
				BonusCalc.bonusTo(this, aType, aName, this, esp.getBonuses(), aPC);
			}
		}

		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (EquipmentModifier eqMod : eqModList)
		{
			eqMod.bonusTo(aPC, aType, aName, this);
		}

		return getBonusMap().keySet().stream().filter(key -> key.startsWith(aBonusKey))
			.mapToDouble(key -> Float.parseFloat(getBonusMap().get(key))).sum();
	}

	/**
	 * Calculates the plus value fo the specified head
	 * 
	 * @param bPrimary Which head is required, the primary (true) or the secondary (false)
	 * @return The plus for the equipment head
	 */
	public int calcPlusForHead(boolean bPrimary)
	{
		int iPlus = 0;

		int headnum = bPrimary ? 1 : 2;
		EquipmentHead head = getEquipmentHeadReference(headnum);
		if (head == null)
		{
			return iPlus;
		}

		for (EquipmentModifier eqMod : head.getSafeListFor(ListKey.EQMOD))
		{
			int iCount = getSelectCorrectedAssociationCount(eqMod);

			if (iCount < 1)
			{
				iCount = 1;
			}

			iPlus += (iCount * eqMod.getSafe(IntegerKey.PLUS));
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
	public boolean canAddModifier(PlayerCharacter pc, PrereqObject eqMod, boolean bPrimary)
	{

		// Make sure we are qualified
		bonusPrimary = bPrimary;

		return getSafe(ObjectKey.MOD_CONTROL).getModifiersAllowed() && PrereqHandler.passesAll(eqMod, this, pc);
	}

	/**
	 * Returns 0 on object error, 1 on can fit, 2 on too heavy, 3 on properties
	 * problem (unimplemented), 4 on capacity error
	 * 
	 * @param aPC
	 *          The PC that has the Equipment
	 * @param obj
	 *          The equipment to check
	 * @return 0 on object error, 1 on can fit, 2 on too heavy, 3 on properties
	 *         problem (unimplemented), 4 on capacity error
	 */
	public int canContain(final PlayerCharacter aPC, final Object obj)
	{

		if (obj instanceof Equipment)
		{
			final Equipment anEquip = (Equipment) obj;

			Float f = (float) (anEquip.getWeightAsDouble(aPC) * anEquip.numberCarried());

			if (checkChildWeight(aPC, f))
			{

				// canHold(my HashMap())) //quick hack since the properties
				// hashmap doesn't exist
				if (checkContainerCapacity(anEquip.eqTypeList(), anEquip.numberCarried()))
				{

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
	 * Description of the Method
	 *
	 * FIXME: PMD Check is false as the parent of this class does implement Cloneable, so we suppress the warning
	 *
	 * @return Description of the Return Value
	 */
	@Override
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	public Equipment clone()
	{
		Equipment eq = null;

		try
		{
			eq = (Equipment) super.clone();

			eq.heads = new ArrayList<>();
			for (EquipmentHead head : heads)
			{
				if (head == null)
				{
					eq.heads.add(null);
				}
				else
				{
					EquipmentHead eh = new EquipmentHead(eq, head.getHeadIndex());
					eh.overlayCDOMObject(head);
					eq.heads.add(eh);
				}
			}

			//
			if (bonusMap != null)
			{
				eq.bonusMap = new HashMap<>(bonusMap);
			}
			eq.setMoveString(moveString());

			// eq.setTypeString(super.getType());
			// none of the types associated with modifiers
			eq.carried = carried;
			eq.equipped = equipped;
			eq.location = location;
			eq.numberEquipped = numberEquipped;
			eq.qty = qty;
			eq.outputIndex = outputIndex;

			eq.d_childTypes = new HashMap<>(d_childTypes);

			eq.d_containedEquipment = new ArrayList<>(d_containedEquipment);

			eq.assocSupt = assocSupt.clone();
			eq.getEquipmentHead(1).removeListFor(ListKey.EQMOD);
			eq.getEquipmentHead(2).removeListFor(ListKey.EQMOD);
			eq.getEquipmentHead(1).addAllToListFor(ListKey.EQMOD, cloneEqModList(eq, true));
			eq.getEquipmentHead(2).addAllToListFor(ListKey.EQMOD, cloneEqModList(eq, false));
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
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
	@Override
	public int compareTo(final Object o)
	{
		final Equipment e = (Equipment) o;

		return getName().compareToIgnoreCase(e.getName());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Equipment other = (Equipment) obj;

		String displayName = getDisplayName();
		if (displayName == null)
		{
			if (other.getDisplayName() != null)
			{
				return false;
			}
		}
		else if (!displayName.equals(other.getDisplayName()))
		{
			return false;
		}

		if (modifiedName == null)
		{
			return other.modifiedName == null;
		}
		else
		{
			return modifiedName.equals(other.modifiedName);
		}
	}

	/**
	 * Build a String used to save this items special properties in a .pcg file
	 * 
	 * @param sep used to separate the items in the string
	 * @param endPart
	 *          used as a separatot between the label and the data for
	 *          each item in the string
	 * @return String
	 */
	public String formatSaveLine(final char sep, final char endPart)
	{

		final StringBuilder sbuf = new StringBuilder(100);

		final Equipment base;

		CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
		if (baseItem == null)
		{
			base = this;
			sbuf.append(getBaseItemName());
		}
		else
		{
			base = baseItem.get();
			sbuf.append(base.getKeyName());
			sbuf.append(sep).append("NAME").append(endPart).append(toString(false));
		}

		// When you customise a piece of equipment using the customiser, it sets
		// the keyName equal to the Name. The autoresizer doesn't do that, it
		// makes a new key. This is to cope with the auto resizer.

		if (!this.getKeyName().equals(this.getName()))
		{
			sbuf.append(sep).append("KEY").append(endPart).append(this.getKeyName());
		}

		SizeAdjustment thisSize = getSizeAdjustment();
		if (!thisSize.equals(base.getSizeAdjustment()))
		{
			sbuf.append(sep).append("SIZE").append(endPart).append(thisSize.getKeyName());
		}

		String string1 = getEqModifierString(true); // key1.key2|assoc1|assoc2.key3.key4

		if (!string1.isEmpty())
		{
			sbuf.append(sep).append("EQMOD").append(endPart).append(string1);
		}

		String string2 = getEqModifierString(false); // key1.key2|assoc1|assoc2.key3.key4

		if (!string2.isEmpty())
		{
			sbuf.append(sep).append("ALTEQMOD").append(endPart).append(string2);
		}

		String string3 = getRawSpecialProperties();

		if ((!string3.isEmpty()) && !string3.equals(base.getRawSpecialProperties()))
		{
			sbuf.append(sep).append("SPROP").append(endPart).append(string3);
		}

		if (!costMod.equals(BigDecimal.ZERO))
		{
			sbuf.append(sep).append("COSTMOD").append(endPart).append(costMod.toString());
		}

		return sbuf.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		String displayName = getDisplayName();
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((modifiedName == null) ? 0 : modifiedName.hashCode());
		return result;
	}

	/**
	 * Gets the index of a child
	 * 
	 * @param child
	 *            The child
	 * @return the index of the child
	 */
	private int indexOfChild(final Object child)
	{
		if (!(child instanceof Equipment))
		{
			return -1;
		}

		return getContainedEquipmentIndexOf((Equipment) child);
	}

	/**
	 * Adds a child to this Equipment
	 * 
	 * @param aPC The PC that has the Equipment
	 * @param child
	 *            The child to add
	 */
	public void insertChild(final PlayerCharacter aPC, final Object child)
	{

		if (child == null)
		{
			return;
		}

		Equipment anEquip = (Equipment) child;
		Float aFloat = anEquip.numberCarried();
		Float bFloat = aFloat;

		final String aString = pickChildType(anEquip.eqTypeList(), aFloat);

		if (containsChildType(aString))
		{
			aFloat = getChildType(aString) + aFloat;
		}

		bFloat = getChildType("Total") + bFloat;
		setChildType(aString, aFloat);
		setChildType("Total", bFloat);
		addContainedEquipment(anEquip);
		anEquip.setIndexedUnderType(aString);
		anEquip.setParent(this);

		// hmm probably not needed; but as it currently isn't hurting
		// anything...
		updateContainerContentsString(aPC);

		while (anEquip.getParent() != null)
		{
			anEquip = anEquip.getParent();
			anEquip.updateContainerContentsString(aPC);
		}
	}

	/**
	 * Returns how 'deep' in a structure an Equipment is.
	 * 
	 * @return how 'deep' in a structure an Equipment is.
	 */
	public int itemDepth()
	{
		if (getParent() == null)
		{
			return 0;
		}

		int i = 1;
		Equipment anEquip = getParent();

		while (anEquip.getParent() != null)
		{
			anEquip = anEquip.getParent();
			++i;
		}

		return i;
	}

	/**
	 * load a "line" i.e. a String and use its data to populate the attributes
	 * of this Equipment
	 * 
	 * @param aLine
	 *             The data to parse
	 * @param sep  
	 *             The item separator used in the data
	 * @param endPart
	 *             The separator used between a label and its associated data
	 * @param aPC
	 *             The PC used to size the Equipment (may be null)
	 */
	public void load(final String aLine, final String sep, final String endPart, final PlayerCharacter aPC)
	{

		final StringTokenizer aTok = new StringTokenizer(aLine, sep);
		final int endPartLen = endPart.length();
		CDOMSingleRef<SizeAdjustment> size = getSafe(ObjectKey.SIZE);
		boolean firstSprop = true;

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (aString.startsWith("NAME" + endPart))
			{
				setName(aString.substring(4 + endPartLen));
				put(StringKey.OUTPUT_NAME, getDisplayName());
			}
			else if (aString.startsWith("KEY" + endPart))
			{
				put(StringKey.KEY_NAME, aString.substring(3 + endPartLen));
			}
			else if (aString.startsWith("SIZE" + endPart))
			{
				size = Globals.getContext().getReferenceContext().getCDOMReference(SizeAdjustment.class,
					aString.substring(4 + endPartLen));
			}
			else if (aString.startsWith("EQMOD" + endPart))
			{
				addEqModifiers(aString.substring(5 + endPartLen), true, true);
			}
			else if (aString.startsWith("ALTEQMOD" + endPart))
			{
				addEqModifiers(aString.substring(8 + endPartLen), false);
			}
			else if (aString.startsWith("SPROP" + endPart))
			{
				if (firstSprop)
				{
					removeListFor(ListKey.SPECIAL_PROPERTIES);
					firstSprop = false;
				}
				addToListFor(ListKey.SPECIAL_PROPERTIES,
					SpecialProperty.createFromLst(aString.substring(5 + endPartLen)));
			}
			else if (aString.startsWith("COSTMOD" + endPart))
			{
				setCostMod(aString.substring(7 + endPartLen));
			}
			else if (aString.startsWith("WEIGHTMOD" + endPart))
			{
				put(ObjectKey.WEIGHT_MOD, new BigDecimal(aString.substring(9 + endPartLen)));
			}
		}
		put(ObjectKey.CUSTOMSIZE, size);
	}

	/**
	 * Sets this Equipment to the size defined in ObjectKey.CUSTOMSIZE. This
	 * should be done after equipment load but before use of the Equipment.
	 * 
	 * Note that this *should not* be done until full data load is complete to
	 * ensure that there is not a race condition on resolving sizes.
	 */
	public void setToCustomSize(PlayerCharacter pc)
	{
		CDOMSingleRef<SizeAdjustment> csr = get(ObjectKey.CUSTOMSIZE);
		if (csr != null)
		{
			SizeAdjustment customSize = csr.get();
			if (!getSizeAdjustment().equals(customSize))
			{
				resizeItem(pc, customSize);
			}
		}
	}

	/**
	 * Get the long name of this piece of equipment
	 * 
	 * @return the verbose name
	 */
	public String longName()
	{
		return toString(true);
	}

	/**
	 * Is the PC qualified to use this equipment
	 * 
	 * @param pc The PC to check the prerequisites against
	 * 
	 * @return Description of the Return Value
	 */
	public boolean meetsPreReqs(PlayerCharacter pc)
	{
		return PrereqHandler.passesAll(this, this, pc);
	}

	/**
	 * Get the modified name e.g. "Natural/Primary" of this Equipment.
	 * Is mostly unset and (if set) is added to the display name when
	 * producing the long name
	 * 
	 * @return the modified name
	 */
	public String modifiedName()
	{
		return modifiedName;
	}

	/**
	 * Process and return a movement string
	 * 
	 * @return the Movement string
	 */
	public String moveString()
	{
		if (!moveString.isEmpty())
		{
			final Load eqLoad;

			if (isHeavy())
			{
				eqLoad = Load.HEAVY;
			}
			else if (isMedium())
			{
				eqLoad = Load.MEDIUM;
			}
			else if (isLight())
			{
				eqLoad = Load.LIGHT;
			}
			else
			{
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

			switch (tokenCount)
			{
				case 2 -> baseMove = 30;
				case 3 -> baseMove = 60;
				default -> tokenCount = -1;
			}

			if (tokenCount > 0)
			{
				final StringBuilder retString = new StringBuilder(moveString.length());

				for (int i = 0; i < tokenCount; ++i)
				{
					if (i != 0)
					{
						retString.append(',');
					}

					retString.append(eqLoad.calcEncumberedMove(baseMove));
					baseMove -= 10;
				}

				return retString.toString();
			}
		}

		return moveString;
	}

	/**
	 * Generate a name from the Base Equipement name and any EqMods that 
	 * have been applied.
	 * 
	 * @param pc the PC that has the equipment
	 * 
	 * @return a name generated from the Base Equipement type and any EqMods applied
	 */
	public String nameItemFromModifiers(final PlayerCharacter pc)
	{

		final String itemName = getItemNameFromModifiers(getBaseItemName());
		setDefaultCrit(pc);
		setName(itemName);
		String itemKey = getItemNameFromModifiers(getBaseItemKeyName()).replaceAll("[^A-Za-z0-9/_() +-]", "_");
		setKeyName(itemKey);
		remove(StringKey.OUTPUT_NAME);

		return getKeyName();
	}

	/**
	 * Get the number of items of this Equipment being carried
	 * 
	 * @return the number of this Equipment carried
	 */
	public Float numberCarried()
	{
		Equipment eqParent = getParent();

		if (isEquipped() || (eqParent == null))
		{
			return carried;
		}

		for (; eqParent != null; eqParent = eqParent.getParent())
		{

			if (eqParent.isEquipped() || ((eqParent.getParent() == null) && (eqParent.numberCarried().intValue() != 0)))
			{
				return carried;
			}
		}

		return (float) 0;
	}

	/**
	 * Get the quantity of items
	 * 
	 * @return the quantity of items
	 */
	public double qty()
	{
		return qty;
	}

	/**
	 * Removes a child from the Equipment
	 * 
	 * @param pc
	 *            The PC carrying the item
	 * 
	 * @param child
	 *            The child to remove
	 */
	public void removeChild(final PlayerCharacter pc, final Object child)
	{

		final int i = indexOfChild(child);
		Equipment anEquip = (Equipment) child;
		final Float qtyRemoved = anEquip.numberCarried();
		setChildType("Total", getChildType("Total") - qtyRemoved);

		final String aString = anEquip.isIndexedUnderType();
		setChildType(aString, getChildType(aString) - qtyRemoved);
		anEquip.setParent(null);
		removeContainedEquipment(i);
		updateContainerContentsString(pc);

		Equipment equipment = this;

		while (equipment.getParent() != null)
		{
			equipment = equipment.getParent();
			equipment.updateContainerContentsString(pc);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param eqMod
	 *            Description of the Parameter
	 * @param bPrimary
	 *            Description of the Parameter
	 * @param pc
	 *            The PC carrying the item
	 */
	public void removeEqModifier(final EquipmentModifier eqMod, final boolean bPrimary, PlayerCharacter pc)
	{

		final EquipmentModifier aMod = getEqModifierKeyed(eqMod.getKeyName(), bPrimary);

		if (aMod == null)
		{
			return;
		}

		// Get a response from user (if one required)
		// Remove the modifier if all associated choices are deleted
		if (!hasAssociations(aMod) || !EquipmentChoiceDriver.getChoice(0, this, aMod, false, pc))
		{
			EquipmentHead head = getEquipmentHead(bPrimary ? 1 : 2);
			head.removeFromListFor(ListKey.EQMOD, aMod);
			if (bPrimary)
			{
				usePrimaryCache = false;
			}
			else
			{
				useSecondaryCache = false;
			}

			restoreEqModsAfterRemove(pc, eqMod, bPrimary, head);

			setDirty(true);
		}
	}

	/**
	 * Add back in modifiers that this one previously removed.
	 * 
	 * @param eqMod The equipment modifier being removed.
	 * @param bPrimary Which head is this for?
	 * @param head The head being updated.
	 */
	private void restoreEqModsAfterRemove(PlayerCharacter pc, final EquipmentModifier eqMod, final boolean bPrimary,
		EquipmentHead head)
	{
		CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
		if (baseItem == null)
		{
			return;
		}

		List<CDOMSingleRef<EquipmentModifier>> replaces = eqMod.getListFor(ListKey.REPLACED_KEYS);
		if (replaces != null)
		{
			//
			// Add back in modifiers that this one previously removed
			//
			replaces.stream().map(CDOMSingleRef::get).map(CDOMObject::getKeyName)
				.forEach(key -> baseItem.get().getEquipmentHead(bPrimary ? 1 : 2).getSafeListFor(ListKey.EQMOD).stream()
					.filter(baseMod -> key.equalsIgnoreCase(baseMod.getKeyName())).forEach(baseMod -> head.addToListFor(ListKey.EQMOD, baseMod)));
		}

		if (eqMod.isType("BaseMaterial"))
		{
			baseItem.get().getEquipmentHead(bPrimary ? 1 : 2).getSafeListFor(ListKey.EQMOD).stream()
				.filter(baseMod -> baseMod.isType("BaseMaterial")).forEach(baseMod -> head.addToListFor(ListKey.EQMOD, baseMod));
		}
		else if (eqMod.isType("MagicalEnhancement"))
		{
			baseItem.get().getEquipmentHead(bPrimary ? 1 : 2).getSafeListFor(ListKey.EQMOD).stream()
				.filter(baseMod -> baseMod.isType("MagicalEnhancement")).forEach(baseMod -> head.addToListFor(ListKey.EQMOD, baseMod));
		}
	}

	/**
	 * Remove a list equipment modifiers and their associated information eg:
	 * Bane|Vermin|Fey.Keen.Vorpal.ABILITYPLUS|CHA=+6 <p> Removes a feature
	 * from the EqModifiers attribute of the Equipment object
	 * 
	 * @param aString
	 *            The feature to be removed from the EqModifiers attribute
	 * @param bPrimary
	 *            The feature to be removed from the EqModifiers attribute
	 * @param pc
	 *            The PC carrying the item
	 */
	public void removeEqModifiers(final String aString, final boolean bPrimary, PlayerCharacter pc)
	{

		final StringTokenizer aTok = new StringTokenizer(aString, ".");

		while (aTok.hasMoreTokens())
		{
			final String aEqModName = aTok.nextToken();

			if (!aEqModName.equalsIgnoreCase(Constants.NONE))
			{
				removeEqModifier(aEqModName, bPrimary, pc);
			}
		}
	}

	/**
	 * Change the size of an item
	 * 
	 * @param pc
	 *            The PC carrying the item
	 * @param newSize
	 *            The new size for the item
	 */
	public void resizeItem(final PlayerCharacter pc, SizeAdjustment newSize)
	{
		setBase();

		final int iOldSize = sizeInt();
		int iNewSize = newSize.get(IntegerKey.SIZEORDER);

		if (iNewSize != iOldSize)
		{
			put(ObjectKey.SIZE, CDOMDirectSingleRef.getRef(newSize));
			CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);

			Equipment eq;
			if (baseItem == null)
			{
				eq = this;
			}
			else
			{
				eq = baseItem.get();
			}

			put(ObjectKey.CURRENT_COST, eq.getCostAdjustedForSize(pc, newSize));
			put(ObjectKey.WEIGHT, eq.getWeightAdjustedForSize(pc, newSize));
			adjustACForSize(pc, eq, newSize);
			String dam = eq.getDamageAdjustedForSize(iNewSize, true);
			if (dam != null && !dam.isEmpty())
			{
				getEquipmentHead(1).put(StringKey.DAMAGE, dam);
			}
			String adam = eq.getDamageAdjustedForSize(iNewSize, false);
			if (adam != null && !adam.isEmpty())
			{
				getEquipmentHead(2).put(StringKey.DAMAGE, adam);
			}
			//
			// Adjust the capacity of the container (if it is one)
			//
			BigDecimal weightCap = get(ObjectKey.CONTAINER_WEIGHT_CAPACITY);
			if (weightCap != null)
			{
				double mult = 1.0;

				if (pc != null)
				{
					mult = pc.getSizeBonusTo(newSize, "ITEMCAPACITY", eq.typeList(), 1.0);
				}

				BigDecimal multbd = new BigDecimal(String.valueOf(mult));
				if (!Capacity.UNLIMITED.equals(weightCap))
				{
					// CONSIDER ICK, ICK, direct access bad
					put(ObjectKey.CONTAINER_WEIGHT_CAPACITY, weightCap.multiply(multbd));
				}
				List<Capacity> capacity = removeListFor(ListKey.CAPACITY);
				if (capacity != null)
				{
					for (Capacity cap : capacity)
					{
						BigDecimal content = cap.getCapacity();
						if (!Capacity.UNLIMITED.equals(content))
						{
							content = content.multiply(multbd);
						}
						// CONSIDER ICK, ICK, direct access bad
						addToListFor(ListKey.CAPACITY, new Capacity(cap.getType(), content));
					}
				}

				updateContainerCapacityString();
			}
		}

		//
		// Since we've just resized the item, we need to modify any PRESIZE
		// prerequisites
		//
		if (hasPrerequisites())
		{
			AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
			int maxIndex = ref.getConstructedObjectCount(SizeAdjustment.class);
			for (Prerequisite aBonus : getPrerequisiteList())
			{
				if ("SIZE".equalsIgnoreCase(aBonus.getKind()))
				{
					SizeAdjustment sa = ref.silentlyGetConstructedCDOMObject(SizeAdjustment.class, aBonus.getOperand());
					final int iOldPre = sa.get(IntegerKey.SIZEORDER);
					iNewSize += (iOldPre - iOldSize);

					if ((iNewSize >= 0) && (iNewSize <= maxIndex))
					{
						// Note: This actually impacts the Prereq in this
						// Equipment, since it is returned
						// by reference from the get above ... thus no need to
						// perform a set
						SizeAdjustment size =
								ref.getSortedList(SizeAdjustment.class, IntegerKey.SIZEORDER).get(iNewSize);
						aBonus.setOperand(size.getKeyName());
					}
				}
			}
		}
	}

	/**
	 * Get the int size of the Equipment object
	 * 
	 * @return size as int
	 */
	public int sizeInt()
	{
		SizeAdjustment size = getSizeAdjustment();
		return size.get(IntegerKey.SIZEORDER);
	}

	/**
	 * Returns the Equipment as a String
	 * 
	 * @return the Equipment as a String
	 */
	@Override
	public String toString()
	{
		return toString(true);
	}

	/**
	 * Returns a String representation of the Equipment
	 * 
	 * @param addCharges
	 *             if true include the number of charges the Item has in the
	 *             returned string
	 * 
	 * @return the Equipment as a String
	 */
	private String toString(final boolean addCharges)
	{
		if (isDirty() || (cachedNameWithCharges == null && cachedNameWithoutCharges == null))
		{
			// If we have modified the equipment details with
			// respect to the name then rebuid the names
			final StringBuilder buffer = new StringBuilder(100);

			if (SettingsHandler.guiUsesOutputNameEquipment())
			{
				buffer.append(getOutputName());
			}
			else
			{
				buffer.append(getDisplayName());
			}

			if (!modifiedName.isEmpty())
			{
				buffer.append(" (").append(modifiedName).append(")");
			}
			cachedNameWithoutCharges = buffer.toString();

			if (addCharges)
			{
				int rem = getRemainingCharges();
				if ((rem > 0) && (rem < getMaxCharges()))
				{
					buffer.append("(").append(rem).append(")");
				}
			}
			cachedNameWithCharges = buffer.toString();
			setDirty(false);
		}

		// Return the cached names.
		if (addCharges)
		{
			return cachedNameWithCharges;
		}
		return cachedNameWithoutCharges;
	}

	private boolean isDirty()
	{
		return dirty;
	}

	private void setDirty(final boolean dirty)
	{
		this.dirty = dirty;
	}

	/**
	 * Returns the type with the requested index
	 * 
	 * @param index
	 *            the index
	 * @return the type with the requested index
	 */
	public String typeIndex(final int index)
	{
		final List<String> tList = typeList();

		if ((index < 0) || (index >= tList.size()))
		{
			return "";
		}

		return tList.get(index);
	}

	/**
	 * Returns a list of the types of this item.
	 * 
	 * @return a list of the types of this item.
	 */
	public List<String> typeList()
	{
		return typeList(true);
	}

	/**
	 * Updates the containerContentsString from children of this item
	 * 
	 * @param pc The PC carrying the item
	 */
	void updateContainerContentsString(final PlayerCharacter pc)
	{
		final StringBuilder tempStringBuilder = new StringBuilder(getChildCount() * 20);

		// Make sure there's no bug here.
		if (pc != null && isContainer() && (getContainedWeight(pc, true) >= 0.0f))
		{
			tempStringBuilder
				.append(Globals.getGameModeUnitSet().displayWeightInUnitSet(getContainedWeight(pc, true).doubleValue()))
				.append(Globals.getGameModeUnitSet().getWeightUnit());
		}
		else
		{
			// have to put something
			tempStringBuilder.append("0.0 ");
			tempStringBuilder.append(Globals.getGameModeUnitSet().getWeightUnit());
		}

		// karianna os bug 1414564
		IntStream.range(0, getChildCount()).mapToObj(e -> (Equipment) getChild(e))
			.filter(anEquip -> anEquip.getQty() > 0.0f).forEach(anEquip -> {
				tempStringBuilder.append(", ");
				tempStringBuilder.append(BigDecimalHelper.trimZeros(anEquip.getQty().toString()));
				tempStringBuilder.append(" ");
				tempStringBuilder.append(anEquip.getOutputName());
			});

		containerContentsString = tempStringBuilder.toString();
	}

	/**
	 * @param aPC The PC carrying the item
	 */
	private void setDefaultCrit(final PlayerCharacter aPC)
	{

		if (isWeapon())
		{
			if (aPC != null && EqToken.getOldBonusedCritRange(aPC, this, true) == 0)
			{
				getEquipmentHead(1).put(IntegerKey.CRIT_RANGE, 1);
			}

			if (getCritMultiplier() == 0)
			{
				getEquipmentHead(1).put(IntegerKey.CRIT_MULT, 2);
			}
		}
	}

	/**
	 * Set the quantity of items
	 * 
	 * @param argQty the quantity of items to set
	 */
	public void setQty(final double argQty)
	{
		qty = argQty;
	}

	/**
	 * Clear out the Equipment types 
	 */
	static void clearEquipmentTypes()
	{
		S_EQUIPMENT_TYPES.clear();
	}

	/**
	 * Get the type list as a period-delimited string
	 * 
	 * @param bPrimary
	 *            if true the types for the porimary head, otherwise the
	 *            secondary head.
	 * @return The type value
	 */
	String getType(final boolean bPrimary)
	{

		final List<String> typeList = typeList(bPrimary);

		return String.join(".", typeList); // just a guess
	}

	boolean save(final BufferedWriter output)
	{
		FileAccess.write(output, "BASEITEM:" + formatSaveLine('\t', ':'));
		FileAccess.newLine(output);

		return true;
	}

	/**
	 * Sets the base attribute of the Equipment object
	 * 
	 * Todo remove the pc parameter, it is unused.
	 */
	public void setBase()
	{

		if (get(ObjectKey.BASE_ITEM) == null)
		{
			Equipment eq = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class,
				getKeyName());
			if (eq != null)
			{
				put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(eq));
			}
			else
			{
				Logging
					.errorPrint("Unable to find base item for " + this.getDisplayName() + " with key " + getKeyName());
			}
		}

		if (hasConsolidatedProfName())
		{
			CDOMSingleRef<Equipment> baseItem = get(ObjectKey.BASE_ITEM);
			if (baseItem != null)
			{
				Equipment eq = baseItem.get();
				CDOMSingleRef<WeaponProf> wpRef = eq.get(ObjectKey.WEAPON_PROF);
				if (wpRef != null)
				{
					put(ObjectKey.WEAPON_PROF, wpRef);
				}
				CDOMSingleRef<ArmorProf> apRef = eq.get(ObjectKey.ARMOR_PROF);
				if (apRef != null)
				{
					put(ObjectKey.ARMOR_PROF, apRef);
				}
				CDOMSingleRef<ShieldProf> spRef = eq.get(ObjectKey.SHIELD_PROF);
				if (spRef != null)
				{
					put(ObjectKey.SHIELD_PROF, spRef);
				}
			}
		}
	}

	public String consolidatedProfName()
	{
		if (isWeapon())
		{
			CDOMSingleRef<WeaponProf> wpRef = get(ObjectKey.WEAPON_PROF);
			if (wpRef != null)
			{
				return wpRef.get().getKeyName();
			}
		}
		else if (isArmor())
		{
			return getArmorProf().getKeyName();
		}
		else if (isShield())
		{
			return getShieldProf().getKeyName();
		}
		return "";
	}

	private boolean hasConsolidatedProfName()
	{
		if (isWeapon())
		{
			return get(ObjectKey.WEAPON_PROF) != null;
		}
		else if (isArmor())
		{
			return get(ObjectKey.ARMOR_PROF) != null;
		}
		else if (isShield())
		{
			return get(ObjectKey.SHIELD_PROF) != null;
		}
		return false;
	}

	/**
	 * Gets the acceptsTypes attribute of the Equipment object
	 * 
	 * @param aType the Type of Equipment that may be contained in this one
	 *            
	 * @return the number of aType that may be contained in this Equipement
	 */
	private Float getChildType(final String aType)
	{
		return d_childTypes.get(aType);
	}

	/**
	 * Get the index of a piece of Equipment contained in this one. 
	 * 
	 * @param e the contained equipment
	 * 
	 * @return index of containedEquipment object
	 */
	private int getContainedEquipmentIndexOf(final Equipment e)
	{
		return d_containedEquipment.indexOf(e);
	}

	/**
	 * @param aPC The PC with the equipment
	 * @param saSize The size to adjust for
	 * @return The costAdjustedForSize value
	 */
	private BigDecimal getCostAdjustedForSize(final PlayerCharacter aPC, final SizeAdjustment saSize)
	{
		BigDecimal c = getSafe(ObjectKey.COST);

		//
		// Scale everything to medium before conversion
		//
		SizeAdjustment saBase = getSafe(ObjectKey.BASESIZE).get();

		if (saSize == null)
		{
			return c;
		}

		if (aPC != null)
		{
			String costMultiplierVar = aPC.getControl(CControl.COSTMULTIPLIER);
			if (costMultiplierVar == null)
			{
				final double saDbl = aPC.getSizeBonusTo(saSize, "ITEMCOST", typeList(), 1.0);
				final double saBaseDbl = aPC.getSizeBonusTo(saBase, "ITEMCOST", typeList(), 1.0);
				final double mult = saDbl / saBaseDbl;
				c = c.multiply(BigDecimal.valueOf(mult));
			}
			else
			{
				final double mult = ((Number) getLocalVariable(aPC.getCharID(), costMultiplierVar)).doubleValue();
				c = c.multiply(BigDecimal.valueOf(mult));

			}
		}

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
	private String getEqModifierString(final boolean bPrimary)
	{
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);
		final StringBuilder aString = new StringBuilder(eqModList.size() * 10);

		for (EquipmentModifier eqMod : eqModList)
		{
			if (aString.length() != 0)
			{
				aString.append('.');
			}

			aString.append(eqMod.getKeyName());

			// Add the modifiers
			for (String strMod : getAssociationList(eqMod))
			{
				aString.append('|').append(strMod.replace('|', '='));
			}
		}

		if (bPrimary)
		{
			BigDecimal mod = get(ObjectKey.WEIGHT_MOD);
			if (mod != null)
			{
				if (aString.length() != 0)
				{
					aString.append('.');
				}
				aString.append(EQMOD_WEIGHT).append('|').append(mod.toString().replace('.', ','));
			}
		}

		String dmg = get(StringKey.DAMAGE_OVERRIDE);
		if (dmg != null)
		{
			if (aString.length() != 0)
			{
				aString.append('.');
			}
			aString.append(EQMOD_DAMAGE).append('|').append(dmg.replace('.', ','));
		}
		return aString.toString();
	}

	/**
	 * Set the Type this item will be indexed under
	 *
	 * @param aType the Type this item is indexed under
	 */
	private void setIndexedUnderType(final String aType)
	{
		indexedUnderType = aType;
	}

	/**
	 * Gets the Type this item is indexed under
	 * 
	 * @return The Type
	 */
	private String isIndexedUnderType()
	{
		return indexedUnderType;
	}

	/**
	 * Look for a modifier that grants type "magic"
	 * 
	 * @param eqModList
	 *            Description of the Parameter
	 * @return The magicBonus value
	 */
	private static EquipmentModifier getMagicBonus(final Iterable<EquipmentModifier> eqModList)
	{

		if (eqModList != null)
		{

			for (EquipmentModifier eqMod : eqModList)
			{
				if (eqMod.isType("MagicalEnhancement") || (eqMod.isIType(Type.MAGIC)))
				{
					return eqMod;
				}
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
	private String getNameFromModifiers(final List<EquipmentModifier> eqModList)
	{
		//
		// Get a sorted list so that the description will always come
		// out the same reguardless of the order we've added the modifiers
		//
		final List<EquipmentModifier> eqList = new ArrayList<>(eqModList);
		Globals.sortPObjectList(eqList);

		final StringBuilder sMod = new StringBuilder(70);

		eqList.stream().map(eqMod -> eqMod.getSafe(ObjectKey.NAME_OPT).returnName(this, eqMod)).forEach(modDesc -> {
			if (sMod.length() > 0 && !modDesc.isEmpty())
			{
				sMod.append('/');
			}
			sMod.append(modDesc);
		});

		return sMod.toString();
	}

	/**
	 * Gets the specialAbilityList attribute of the Equipment object
	 * 
	 * @param eqModList
	 *            Description of the Parameter
	 * @param pc
	 *            The PC with the equipment
	 * @return The specialAbilityList value
	 */
	private List<String> getSpecialAbilityList(final Iterable<EquipmentModifier> eqModList, final PlayerCharacter pc)
	{

		final List<String> saList = new ArrayList<>();

		for (EquipmentModifier eqMod : eqModList)
		{
			saList.addAll(eqMod.getSpecialProperties(this, pc));
		}

		return saList;
	}

	/**
	 * Tack on the cost of the magical enhancement(s).
	 * 
	 * @param iPlus the Pluses of the primary head
	 * @param altPlus the Pluses of the secondary head
	 * @return cost from pluses
	 */
	private BigDecimal getCostFromPluses(final int iPlus, final int altPlus)
	{

		if (((iPlus != 0) || (altPlus != 0)) && (JEPResourceChecker.getMissingResourceCount() == 0))
		{
			PJEP myParser = null;
			try
			{
				myParser = PjepPool.getInstance().aquire();
				myParser.addVariable("PLUS", iPlus);
				myParser.addVariable("ALTPLUS", altPlus);
				myParser.addVariable("BASECOST", getSafe(ObjectKey.COST).doubleValue());

				if (isAmmunition())
				{
					myParser.addVariable("BASEQTY", getSafe(IntegerKey.BASE_QUANTITY));
				}

				// Look for an expression for all of this item's types
				// If there is more than 1, use the most expensive.
				String costExpr;
				BigDecimal maxCost = null;
				final List<String> itemTypes = typeList();

                for (String typeMatched : itemTypes)
                {
                    costExpr = SettingsHandler.getGameAsProperty().get().getPlusCalculation(Type.getConstant(typeMatched));

                    if (costExpr != null)
                    {
                        final BigDecimal thisCost = evaluateCost(myParser, costExpr);

                        if ((maxCost == null) || (thisCost.compareTo(maxCost) > 1))
                        {
                            maxCost = thisCost;
                        }
                    }
                }

				if (maxCost != null)
				{
					return maxCost;
				}

				//
				// No cost formula found, check for catch-all definition
				//
				costExpr = SettingsHandler.getGameAsProperty().get().getPlusCalculation(Type.ANY);

				if (costExpr != null)
				{
					return evaluateCost(myParser, costExpr);
				}
			}
			finally
			{
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
	private boolean isMagicLimitedType()
	{
		boolean limited = false;

		if (isType("HEADGEAR") || isType("EYEGEAR") || isType("CAPE") || isType("AMULET") || isSuit() || isType("ROBE")
			|| isType("SHIRT") || isType("BRACER") || isType("GLOVE") || isType("RING") || isType("BELT")
			|| isType("BOOT"))
		{
			limited = true;
		}

		return limited;
	}

	/**
	 * same as getSpecialAbilityList except if you have the same ability
	 * twice, it only lists it once with (2) at the end.
	 * 
	 * @param abilityList The list of abilities
	 * @return The specialAbilityTimesList value
	 */
	private List<String> getSpecialAbilityTimesList(final List<String> abilityList)
	{

		final List<String> sortList = new ArrayList<>();
		final int[] numTimes = new int[abilityList.size()];

		for (int i = 0; i < abilityList.size(); i++)
		{
			final String ability = abilityList.get(i);
			if (!sortList.contains(ability))
			{
				sortList.add(ability);
				numTimes[i] = 1;
			}
			else
			{
				for (int j = 0; j < sortList.size(); j++)
				{
					final String testAbility = sortList.get(j);
					if (testAbility.equals(ability))
					{
						numTimes[j]++;
					}
				}
			}
		}

		final List<String> retList = new ArrayList<>();
		for (int i = 0; i < sortList.size(); i++)
		{
			String ability = sortList.get(i);
			if (numTimes[i] > 1)
			{
				ability = ability + " (" + numTimes[i] + ")";
			}
			retList.add(ability);
		}

		return retList;
	}

	/**
	 * Gets the weightAdjustedForSize attribute of the Equipment object
	 * 
	 * @param aPC the PC with the Equipment
	 * @param newSA the size to adjust for
	 * @return The weightAdjustedForSize value
	 */
	private BigDecimal getWeightAdjustedForSize(final PlayerCharacter aPC, final SizeAdjustment newSA)
	{

		if (this.virtualItem)
		{
			return BigDecimal.ZERO;
		}

		BigDecimal weight = getBaseWeight();
		if ((newSA == null) || (getSizeAdjustment() == null))
		{
			return weight;
		}

		if (aPC != null)
		{
			final double mult = getWeightMultiplier(aPC, newSA);
			weight = weight.multiply(new BigDecimal(String.valueOf(mult)));
		}

		return weight;
	}

	private double getWeightMultiplier(final PlayerCharacter aPC,
		final SizeAdjustment newSA)
	{
		String multiplierVar = aPC.getControl(CControl.WEIGHTMULTIPLIER);
		if (multiplierVar == null)
		{
			return aPC.getSizeBonusTo(newSA, "ITEMWEIGHT", typeList(), 1.0)
					/ aPC.getSizeBonusTo(getSizeAdjustment(), "ITEMWEIGHT", typeList(), 1.0);
		}
		return ((Number) getLocalVariable(aPC.getCharID(), multiplierVar)).doubleValue();
	}

	/**
	 * Add a piece of Equipment
	 * 
	 * @param e the Equipment to add
	 */
	private void addContainedEquipment(final Equipment e)
	{
		d_containedEquipment.add(e);
	}

	/**
	 * Gets the acModAdjustedForSize attribute of the Equipment object
	 * 
	 * @param aPC    The PC with the Equipment
	 * @param baseEq The unmodified Equipment
	 * @param newSA  The size to adjust for
	 */
	private void adjustACForSize(final PlayerCharacter aPC, final Equipment baseEq, final SizeAdjustment newSA)
	{
		if ((getRawBonusList(aPC) != null) && isArmor())
		{
			double mult = 1.0;
			final SizeAdjustment currSA = baseEq.getSizeAdjustment();

			if ((newSA != null) && aPC != null)
			{
				mult = aPC.getSizeBonusTo(newSA, "ACVALUE", baseEq.typeList(), 1.0)
					/ aPC.getSizeBonusTo(currSA, "ACVALUE", baseEq.typeList(), 1.0);
			}

			final List<BonusObj> baseEqBonusList = baseEq.getRawBonusList(aPC);
			final List<BonusObj> eqBonusList = getRawBonusList(aPC);

			//
			// Go through the bonus list looking for COMBAT|AC|x and resize
			// bonus
			// Assumption: baseEq.bonusList and this.bonusList only differ in
			// COMBAT|AC|x bonuses
			//
			for (int i = eqBonusList.size() - 1; i >= 0; --i)
			{
				final BonusObj aBonus = eqBonusList.get(i);
				String aString = aBonus.toString();

				if (aString.startsWith("COMBAT|AC|"))
				{
					final int iOffs = aString.indexOf('|', 10);

					if (iOffs > 10)
					{
						/*
						 * TODO This is bad behavior to alter this list, 
						 * which - theoretically - shouldn't be altered 
						 * after data load.  However, given .REPLACE
						 * potential in BONUS objects, I can't find
						 * another quick solution to this problem
						 * - thpr 10/9/08
						 */
						removeFromListFor(ListKey.BONUS, aBonus);
					}
				}
			}

			for (final BonusObj aBonus : baseEqBonusList)
			{
				String aString = aBonus.toString();

				if (aString.startsWith("COMBAT|AC|"))
				{
					final int iOffs = aString.indexOf('|', 10);

					if (iOffs > 10)
					{
						int acCombatBonus = Integer.parseInt(aString.substring(10, iOffs));
						double d = acCombatBonus * mult;
						acCombatBonus = (int) d;
						aString = aString.substring(0, 10) + Integer.toString(acCombatBonus) + aString.substring(iOffs);
						/*
						 * TODO This is bad behavior to alter this list, 
						 * which - theoretically - shouldn't be altered 
						 * after data load.  However, given .REPLACE
						 * potential in BONUS objects, I can't find
						 * another quick solution to this problem
						 * - thpr 10/9/08
						 */
						BonusObj b = Bonus.newBonus(Globals.getContext(), aString);
						if (b != null)
						{
							addToListFor(ListKey.BONUS, b);
						}
					}
				}
			}
		}
	}

	/**
	 * Test whether the container would be within its weight limits if we 
	 * added an item of weight aFloat
	 * 
	 * @param aPC 
	 *            The PC with the Equipment
	 * @param aFloat
	 *            The weight of the item we want to add to the container
	 * @return 
	 *            True if the container is capable of holding the item
	 */
	private boolean checkChildWeight(final PlayerCharacter aPC, final Float aFloat)
	{

		BigDecimal weightCap = get(ObjectKey.CONTAINER_WEIGHT_CAPACITY);
		return weightCap != null
			&& (Capacity.UNLIMITED.equals(weightCap) || (aFloat + getContainedWeight(aPC)) <= weightCap.doubleValue());
	}

	/**
	 * Does this item fit in this container
	 * 
	 * @param aTypeList
	 *            The type list
	 * @param aQuant 
	 *            The total number of the item
	 * @return Does the item fit
	 */
	private boolean checkContainerCapacity(final SortedSet<String> aTypeList, final Float aQuant)
	{

		return Capacity.ANY.equals(get(ObjectKey.TOTAL_CAPACITY)) || !("".equals(pickChildType(aTypeList, aQuant)));
	}

	private List<EquipmentModifier> cloneEqModList(Equipment other, boolean primary)
	{

		final List<EquipmentModifier> clonedList = new ArrayList<>();

		for (EquipmentModifier eqMod : getEqModifierList(primary))
		{

			// only make a copy if we need to add qualifiers to modifier
			if (!eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
			{
				EquipmentModifier newEqMod = eqMod.clone();
				other.assocSupt.convertAssociations(eqMod, newEqMod);
				eqMod = newEqMod;
			}

			clonedList.add(eqMod);
		}

		return clonedList;
	}

	/**
	 * Checks whether the child type is possessed
	 * 
	 * @param aType the Type to check
	 *            
	 * @return true if has child type
	 */
	private boolean containsChildType(final String aType)
	{
		return d_childTypes.containsKey(aType);
	}

	/**
	 * a set which is a sorted collection of the types in the Equipment
	 * 
	 * @return a sorted set of the types
	 */
	private SortedSet<String> eqTypeList()
	{
		return new TreeSet<>(typeList());
	}

	/**
	 * Get all the modifiers that apply to the entire item into a separate list
	 * 
	 * @param commonList
	 *            The list to extract from
	 * @param extractList
	 *            The list to extract.
	 */
	private static void extractListFromCommon(final List<EquipmentModifier> commonList,
		final List<EquipmentModifier> extractList)
	{
		for (int i = extractList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier eqMod = extractList.get(i);

			if (!eqMod.getSafe(ObjectKey.ASSIGN_TO_ALL))
			{
				continue;
			}

			commonList.add(0, eqMod);
			extractList.remove(i);
		}
	}

	private BigDecimal evaluateCost(final PJEP myParser, final String costExpr)
	{
		myParser.parseExpression(costExpr);

		if (!myParser.hasError())
		{
			final Object result = myParser.getValueAsObject();

			if (result != null)
			{
				return new BigDecimal(result.toString());
			}
		}

		Logging.errorPrint("Bad equipment cost expression: " + costExpr);

		return BigDecimal.ZERO;
	}

	private boolean ignoresCostDouble()
	{
		boolean noDouble = false;

		if (isType("MANTLE") // Mantle of Spell Resistance doesn't double
			// cost
			|| isType("POTION") || isType("SCROLL") || isType("STAFF") || isType("WAND"))
		{
			noDouble = true;
		}

		return noDouble;
	}

	/**
	 * Checks whether the Equipment can hold quantToAdd more of an item which
	 * has the types in aTypeList
	 *  
	 * @param aTypeList The types of the Equipment we want to add
	 *            
	 * @param quantToAdd how many to add
	 *            
	 * @return true if the Equipement can hold quantToAdd more of the item
	 */
	private String pickChildType(final SortedSet<String> aTypeList, final Float quantToAdd)
	{

		Capacity totalCap = get(ObjectKey.TOTAL_CAPACITY);
		BigDecimal capValue = totalCap == null ? BigDecimal.ZERO : totalCap.getCapacity();

		if (getChildType("Total") == null)
		{
			setChildType("Total", 0.0f);
		}

		String canContain = "";
		if ((getChildType("Total") + quantToAdd) <= capValue.doubleValue())
		{
			boolean anyContain = false;
			float childType = containsChildType("Any") ? getChildType("Any") : 0.0f;
			CAPFOR: for (Capacity c : getSafeListFor(ListKey.CAPACITY))
			{
				String capType = c.getType();
				double val = c.getCapacity().doubleValue();
				for (String aType : aTypeList)
				{
					if (capType.equalsIgnoreCase(aType))
					{
						if (containsChildType(aType) && ((getChildType(aType) + quantToAdd) <= val)
							|| quantToAdd <= val)
						{
							canContain = aType;
							break CAPFOR;
						}
					}
					else if ("Any".equalsIgnoreCase(capType))
					{
						if ((childType + quantToAdd) <= val)
						{
							anyContain = true;
						}
					}
				}
			}

			if (("".equals(canContain)) && anyContain)
			{
				if (!containsChildType("Any"))
				{
					setChildType("Any", (float) 0);
				}
				canContain = "Any";
			}
		}

		return canContain;
	}

	/**
	 * Remove the common modifiers from the alternate list.
	 * 
	 * @param altList the list of modifiers on the secondary head
	 * @param commonList The list of modifiers common between the two heads
	 * @param errMsg the error message to print if something goes wrong
	 */
	private void removeCommonFromList(final List<EquipmentModifier> altList, final List<EquipmentModifier> commonList,
		final String errMsg)
	{

		for (int i = altList.size() - 1; i >= 0; --i)
		{
			final EquipmentModifier eqMod = altList.get(i);

			if (!eqMod.getSafe(ObjectKey.ASSIGN_TO_ALL))
			{
				continue;
			}

			final int j = commonList.indexOf(eqMod);

			if (j >= 0)
			{
				altList.remove(i);
			}
			else
			{
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
	private List<List<EquipmentModifier>> initSplitModList()
	{

		return IntStream.range(0, EqModFormatCat.values().length)
			.<List<EquipmentModifier>> mapToObj(i -> new ArrayList<>()).collect(Collectors.toList());
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
		final List<List<EquipmentModifier>> splitModList)
	{

		for (EquipmentModifier aModList : modList)
		{
			int o = aModList.getSafe(ObjectKey.FORMAT).ordinal();
			splitModList.get(o).add(aModList);
		}
	}

	/**
	 * remove contained Equipment
	 * 
	 * @param i the index of the item to remove
	 */
	private void removeContainedEquipment(final int i)
	{
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
	 * @param aPC
	 *            the PC that has the Equipment
	 */
	private void removeEqModifier(final String aString, final boolean bPrimary, PlayerCharacter aPC)
	{

		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		final String eqModKey = aTok.nextToken();
		final EquipmentModifier eqMod = getEqModifierKeyed(eqModKey, bPrimary);

		if (eqMod == null)
		{
			return;
		}

		//
		// Remove the associated choices
		//
		while (aTok.hasMoreTokens())
		{
			final String x = aTok.nextToken().replace('=', '|');

			getAssociationList(eqMod).stream().filter(aChoice -> aChoice.startsWith(x))
				.forEach(aChoice -> removeAssociation(eqMod, aChoice));
		}

		if (!hasAssociations(eqMod))
		{
			removeEqModifier(eqMod, bPrimary, aPC);
		}
	}

	/**
	 * Returns a list of the types of this item.
	 * 
	 * @param bPrimary
	 *            if true return the types if the primary head, otherwise
	 *            return the types of the secondary head
	 * @return a list of the types of this item.
	 */
	private List<String> typeList(final boolean bPrimary)
	{

		if (bPrimary && usePrimaryCache)
		{
			return typeListCachePrimary;
		}
		if (!bPrimary && useSecondaryCache)
		{
			return typeListCacheSecondary;
		}

		// Use the primary type(s) if none defined for secondary
		List<Type> initializingList = getEquipmentHead(2).getListFor(ListKey.TYPE);
		if (bPrimary || (initializingList == null) || initializingList.isEmpty())
		{
			initializingList = getTrueTypeList(false);
		}
		else if (!isDouble())
		{
			return new ArrayList<>();
		}

		Set<String> calculatedTypeList = new LinkedHashSet<>();
		for (Type t : initializingList)
		{
			calculatedTypeList.add(t.getComparisonString());
		}
		final Collection<String> modTypeList = new ArrayList<>();

		//
		// Add in all type modfiers from "ADDTYPE" modifier
		//
		EquipmentModifier aEqMod = getEqModifierKeyed("ADDTYPE", bPrimary);

		if (aEqMod != null)
		{
			for (String aType : getAssociationList(aEqMod))
			{
				aType = aType.toUpperCase();

				if (!calculatedTypeList.contains(aType))
				{
					modTypeList.add(aType);
				}
			}
		}

		/*
		 * CONSIDER I think there is a weird order of operations issue nere, need to check
		 * if it existed way back, e.g. SVN 6206.  The issue is if a Type is introduced by a 
		 * MOD, then the ChangeArmorType system doesn't seem to be able to grab/modify it
		 * Is that correct? - thpr 10/3/08
		 */
		//
		// Add in all of the types from each EquipmentModifier
		// currently applied to this piece of equipment
		//
		final List<EquipmentModifier> eqModList = getEqModifierList(bPrimary);

		for (EquipmentModifier eqMod : eqModList)
		{
			//
			// If we've just replaced the armor type, then make sure it is
			// not in the equipment modifier list
			//
			Set<String> newTypeList = new LinkedHashSet<>(calculatedTypeList);
			for (ChangeArmorType cat : eqMod.getSafeListFor(ListKey.ARMORTYPE))
			{
				List<String> tempTypeList = cat.applyProcessor(newTypeList);
				LinkedHashSet<String> tempTypeSet = new LinkedHashSet<>(tempTypeList);
				boolean noMatch = newTypeList.size() != tempTypeList.size() || newTypeList.equals(tempTypeSet);
				newTypeList = tempTypeSet;
				if (!noMatch)
				{
					break;
				}
			}

			Collection<String> removedTypeList = new ArrayList<>(calculatedTypeList);
			removedTypeList.removeAll(newTypeList);
			modTypeList.removeAll(removedTypeList);
			calculatedTypeList = newTypeList;

			for (Type type : eqMod.getSafeListFor(ListKey.ITEM_TYPES))
			{
				String aType = type.toString().toUpperCase();

				// If it's BOTH & MELEE, we cannot add RANGED or THROWN to
				// it
				// BOTH is only used after the split of a Thrown weapon in 2
				// (melee and ranged)
				if (calculatedTypeList.contains("BOTH") && calculatedTypeList.contains("MELEE")
					&& (Type.RANGED.equals(type) || Type.THROWN.equals(type)))
				{
					continue;
				}

				if (!calculatedTypeList.contains(aType) && !modTypeList.contains(aType))
				{
					modTypeList.add(aType);
				}
			}
		}

		calculatedTypeList.addAll(modTypeList);

		//
		// Make sure MAGIC tag is the 1st entry
		//
		List<String> resultingTypeList = new ArrayList<>(calculatedTypeList);
		final int idx = resultingTypeList.indexOf("MAGIC");

		if (idx > 0)
		{
			resultingTypeList.remove(idx);
			resultingTypeList.add(0, "MAGIC");
		}

		if (bPrimary)
		{
			typeListCachePrimary = resultingTypeList;
			usePrimaryCache = true;
		}
		else
		{
			typeListCacheSecondary = resultingTypeList;
			useSecondaryCache = true;
		}
		return resultingTypeList;
	}

	/**
	 * Creates the containerCapacityString from children of this object
	 */
	private void updateContainerCapacityString()
	{
		final StringBuilder tempStringBuilder = new StringBuilder(100);
		boolean comma = false;

		BigDecimal weightCap = get(ObjectKey.CONTAINER_WEIGHT_CAPACITY);
		if (weightCap != null && !Capacity.UNLIMITED.equals(weightCap))
		{
			tempStringBuilder.append(weightCap).append(' ').append(Globals.getGameModeUnitSet().getWeightUnit());
			comma = true;
		}

		List<Capacity> capacity = getListFor(ListKey.CAPACITY);
		if (capacity != null)
		{
			for (Capacity c : capacity)
			{
				if (comma)
				{
					tempStringBuilder.append(", ");
					comma = false;
				}

				BigDecimal capValue = c.getCapacity();
				if (!Capacity.UNLIMITED.equals(capValue))
				{
					tempStringBuilder.append(capValue).append(' ');
					tempStringBuilder.append(c.getType());
					comma = true;
				}
				else if (c.getType() != null)
				{
					comma = true;
					tempStringBuilder.append(c.getType());
				}
			}
		}

		containerCapacityString = tempStringBuilder.toString();
	}

	/**
	 * Sets all the BonusObj's to "active". Note this version overrides the
	 * PObject implementation as it will check the bonuses against the
	 * equipment, rather than the PC.
	 * 
	 * @param aPC
	 *            The character being checked.
	 */
	@Override
	public void activateBonuses(final PlayerCharacter aPC)
	{
		for (final BonusObj bonus : getRawBonusList(aPC))
		{
			aPC.setApplied(bonus, PrereqHandler.passesAll(bonus, this, aPC));
		}
	}

	public boolean isCalculatingCost()
	{
		return calculatingCost;
	}

	public boolean isWeightAlreadyUsed()
	{
		return weightAlreadyUsed;
	}

	public BigDecimal getWeightInPounds()
	{
		return isVirtual() ? BigDecimal.ZERO : getSafe(ObjectKey.WEIGHT);
	}

	public void setWeightAlreadyUsed(boolean weightAlreadyUsed)
	{
		this.weightAlreadyUsed = weightAlreadyUsed;
	}

	/**
	 * Get non headed name
	 * 
	 * @return non headed name
	 */
	public String getNonHeadedName()
	{
		if (wholeItemName == null || wholeItemName.isEmpty())
		{
			return getName();
		}
		return wholeItemName;
	}

	/**
	 * Get whole item name
	 * 
	 * @return whole item name
	 */
	public String getWholeItemName()
	{
		return wholeItemName;
	}

	/**
	 * Set whole item name
	 * 
	 * @param wholeItemName the name to set
	 */
	void setWholeItemName(String wholeItemName)
	{
		this.wholeItemName = wholeItemName;
	}

	/**
	 * Create a Key for the new custom piece of resized equipment. The new key
	 * will start with the auto resized constant and then the size abbreviation
	 * (as per SizeAdjustment) followed by the existing key. This should
	 * generate a unique name unless we've already auto resized this piece of
	 * equipment to this size in which case it already exists in the equipment
	 * list and does not need to be created.
	 * 
	 * @param newSize
	 *            The size of equipment to make a key for. This needs to be the
	 *            abbreviated form, not the full name.
	 * @return The generated key
	 */

	public String createKeyForAutoResize(SizeAdjustment newSize)
	{
		// Make sure newSize is not null
		if (newSize == null)
		{
			return getKeyName();
		}

		String displayName = newSize.getDisplayName();

		// Make sure finalSize is a single upper case letter
		String finalSize = displayName.toUpperCase().substring(0, 1);

		String thisKey = getKeyName();

		if (thisKey.startsWith(Constants.AUTO_RESIZE_PREFIX))
		{
			int index = Constants.AUTO_RESIZE_PREFIX.length();
			String keySize = thisKey.substring(index, index + 1).toUpperCase();

			// If the key of this object already has the finalSize in the correct
			// place then just return it, the item has already been adjusted.
			// This should never happen because if the key has an AUTO_RESIZE_PREFIX
			// prefix and the correct size then it should already be finalSize
			if (keySize.equals(finalSize))
			{
				return thisKey;
			}

			// remove the AUTO_RESIZE_PREFIX and the following size abbreviation
			// from the key
			thisKey = thisKey.substring(index + 1);
		}

		return Constants.AUTO_RESIZE_PREFIX + finalSize + thisKey;
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

	public String createNameForAutoResize(SizeAdjustment newSize)
	{
		// Make sure newSize is not null
		if (newSize == null)
		{
			return getName();
		}

		String displayName = newSize.getDisplayName();
		String thisName = getName();
		String upName = thisName.toUpperCase();

		String upThisSize = getSizeAdjustment().getDisplayName().toUpperCase();

		int start = upName.indexOf(upThisSize);
		int end = start + upThisSize.length();

		/*
		 * if the name contains thisSize surrounded by /, ( or ) then replace
		 * thisSize with newSize
		 */
		if (start > -1 && (upName.substring(start - 1).startsWith("(") || upName.substring(start - 1).startsWith("/"))
			&& (upName.substring(end).startsWith(")") || upName.substring(end).startsWith("/")))
		{
			return thisName.substring(0, start) + displayName + thisName.substring(end);
		}

		return thisName + " (" + displayName + ")";
	}

	/**
	 * Make this item virtual i.e. one that doesn't really exist and is only
	 * used to hold temporary bonuses
	 */
	public void makeVirtual()
	{
		this.virtualItem = true;
	}

	/**
	 * Does this item really exist, or is it a phantom created to hold a bonus
	 * 
	 * @return Returns the virtualItem.
	 */
	private boolean isVirtual()
	{
		return virtualItem;
	}

	/**
	 * Gets the critMultiplier attribute of the Equipment object
	 * 
	 * @return The critMultiplier value
	 * @deprecated due to CRITMULT code control
	 */
	@Deprecated
	public int getCritMultiplier()
	{
		int mult = getHeadInfo(1, IntegerKey.CRIT_MULT);
		if (mult == 0)
		{
			final String cm = getWeaponInfo("CRITMULT", true);

			if (!cm.isEmpty())
			{
				mult = Integer.parseInt(cm);
			}
		}
		return mult;
	}

	/**
	 * Gets the altCritMultiplier attribute of the Equipment object
	 * 
	 * @return The altCritMultiplier value
	 * @deprecated due to CRITMULT code control
	 */
	@Deprecated
	public int getAltCritMultiplier()
	{
		int mult = getHeadInfo(2, IntegerKey.CRIT_MULT);
		if (mult == 0)
		{
			final String cm = getWeaponInfo("CRITMULT", false);

			if (!cm.isEmpty())
			{
				mult = Integer.parseInt(cm);
			}
		}
		return mult;
	}

	/**
	 * @deprecated due to CRITMULT and CRITRANGE code controls
	 */
	@Deprecated
	private int getHeadInfo(int headnum, IntegerKey ik)
	{
		EquipmentHead head = getEquipmentHeadReference(headnum);
		return head == null ? 0 : head.getSafe(ik);
	}

	/**
	 * Test to see if a weapon is Finesseable or not for a PC
	 *
	 * @param pc The PlayerCharacter wielding the weapon.
	 * @return true if finessable
	 */
	public boolean isFinessable(final PlayerCharacter pc)
	{
		WieldCategory wCat = getEffectiveWieldCategory(pc);
		return isType("Finesseable") || (wCat != null && wCat.isFinessable());
	}

	/**
	 * Tests if this weapon is a light weapon for the specied PC.
	 * 
	 * @param pc The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is light for the specified pc.
	 */
	public boolean isWeaponLightForPC(final PlayerCharacter pc)
	{

		if (pc == null || !isWeapon())
		{
			return false;
		}
		WieldCategory wc = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(WieldCategory.class, "Light");
		return (wc != null) && wc.equals(getEffectiveWieldCategory(pc));
	}

	/**
	 * Tests if this weapon can be used in one hand by the specified PC.
	 * 
	 * @param pc The PlayerCharacter wielding the weapon.
	 * @return true if the weapon can be used one handed.
	 */
	public boolean isWeaponOneHanded(PlayerCharacter pc)
	{

		if (pc == null && !isWeapon())
		{
			return false;
		}

		WieldCategory wCat = getEffectiveWieldCategory(pc);
		return wCat != null && getHandsRequired(pc, wCat) == 1;
	}

	private int getHandsRequired(PlayerCharacter pc, WieldCategory wCat)
	{
		String handsControl = pc.getControl(CControl.WEAPONHANDS);
		if (handsControl != null)
		{
			return ((Number) getLocalVariable(pc.getCharID(), handsControl)).intValue();
		}
		else
		{
			return wCat.getHandsRequired();
		}
	}

	/**
	 * Tests if the weapon is either too large OR too small for the specified PC
	 * to wield.
	 * 
	 * @param pc The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is too large or too small.
	 */
	public boolean isWeaponOutsizedForPC(PlayerCharacter pc)
	{

		if (pc == null || !isWeapon())
		{
			return true;
		}

		final WieldCategory wCat = getEffectiveWieldCategory(pc);
		if (wCat == null)
		{
			return false;
		}
		int handsRequired = getHandsRequired(pc, wCat);
		return (handsRequired > 2 || handsRequired < 0);
	}

	/**
	 * Tests if this weapon requires two hands to use.
	 * 
	 * @param pc -
	 *            The PlayerCharacter wielding the weapon.
	 * @return true if the weapon is two-handed for the specified pc
	 */
	public boolean isWeaponTwoHanded(PlayerCharacter pc)
	{

		if (pc == null || !isWeapon())
		{
			return false;
		}

		WieldCategory wieldCategory = getEffectiveWieldCategory(pc);
		return wieldCategory != null && getHandsRequired(pc, wieldCategory) == 2;
	}

	/**
	 * Gets the minimum WieldCategory this weapon can be used at. Accounts for
	 * all modifiers that affect WieldCategory. 3.0 weapon sizes are mapped to
	 * appropriate WieldCategories.
	 * 
	 * @param aPC The PlayerCharacter using the weapon
	 * @return The minimum WieldCategory required to use the weapon.
	 */
	public WieldCategory getEffectiveWieldCategory(final PlayerCharacter aPC)
	{
		CDOMSingleRef<WeaponProf> ref = get(ObjectKey.WEAPON_PROF);
		WeaponProf wp = ref == null ? null : ref.get();

		WieldCategory wCat = get(ObjectKey.WIELD);
		if (wCat != null && !Globals.checkRule(RuleConstants.SIZEOBJ))
		{
			// Get the starting effective wield category
			wCat = wCat.adjustForSize(aPC, this);
		}
		else
		{
			int pcSize = aPC.sizeInt();

			if (wp != null)
			{
				pcSize += aPC.getTotalBonusTo("WEAPONPROF=" + wp.getKeyName(), "PCSIZE");
			}

			int sizeDiff;
			if (wCat != null && Globals.checkRule(RuleConstants.SIZEOBJ))
			{
				// In this case we have a 3.5 style equipments size.
				// We need to map to a 3.0 style
				sizeDiff = wCat.getObjectSizeInt(this) - pcSize;
			}
			else
			{
				sizeDiff = sizeInt() - pcSize;
			}

			if (sizeDiff > 1)
			{
				wCat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WieldCategory.class,
					"TooLarge");
			}
			else if (sizeDiff == 1)
			{
				wCat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WieldCategory.class,
					"TwoHanded");
			}
			else if (sizeDiff == 0)
			{
				wCat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WieldCategory.class,
					"OneHanded");
			}
			else
			{
				wCat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WieldCategory.class,
					"Light");
			}
		}

		int aBump = 0;

		// TODO Remove this code when support for this "feature" goes away
		if (wp != null)
		{
			int iHands = wp.getSafe(IntegerKey.HANDS);

			if (iHands == Constants.HANDS_SIZE_DEPENDENT)
			{
				if (aPC.sizeInt() > sizeInt())
				{
					iHands = 1;
				}
				else
				{
					iHands = 2;
				}
			}
			while (getHandsRequired(aPC, wCat) < iHands)
			{
				wCat = wCat.getWieldCategoryStep(1);
			}

			// See if there is a bonus associated with just this weapon
			final String expProfName = wp.getKeyName();
			aBump += (int) aPC.getTotalBonusTo("WEAPONPROF=" + expProfName, "WIELDCATEGORY");

			// loops for each equipment type
			int modWield = 0;
			for (String eqType : typeList())
			{

				// get the type bonus (ex TYPE.MARTIAL)
				final int i = (int) aPC.getTotalBonusTo("WEAPONPROF=TYPE." + eqType
						// get the type bonus (ex TYPE.MARTIAL)
						, "WIELDCATEGORY");

				// get the highest bonus
				if (i < modWield)
				{
					modWield = i;
				}
			}
			aBump += modWield;
		}

		// or a bonus from the weapon itself
		aBump += (int) bonusTo(aPC, "WEAPON", "WIELDCATEGORY", true);

		if (aBump == 0)
		{
			return wCat;
		}

		return wCat.getWieldCategoryStep(aBump);
	}

	//
	// Protective Item Support
	//
	/**
	 * Gets the acMod attribute of the Equipment object
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @return The acMod value
	 */
	public Integer getACMod(final PlayerCharacter aPC)
	{
		String acMod = aPC.getControl(CControl.EQACMOD);
		if (acMod != null)
		{
			Object o = aPC.getLocal(this, acMod);
			return ((Number) o).intValue();
		}
		//TODO This should be documented
		return (int) bonusTo(aPC, "EQMARMOR", "AC", true) + (int) bonusTo(aPC, "COMBAT", "AC", true);
	}

	//
	// Weapon Support
	//

	/**
	 * Gets the damage attribute of the Equipment object
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @return The damage value
	 */
	public String getDamage(final PlayerCharacter aPC)
	{
		return getDamage(aPC, true);
	}

	private String getDamage(PlayerCharacter apc, boolean bPrimary)
	{
		int headnum = bPrimary ? 1 : 2;
		EquipmentHead head = getEquipmentHeadReference(headnum);
		if (head == null)
		{
			return "";
		}
		String dam = head.get(StringKey.DAMAGE);
		if (!isWeapon() || (!bPrimary && !isDouble()))
		{
			return dam == null ? "" : dam;
		}
		if (bPrimary && dam == null)
		{
			// No need to grab reference, always exists due to if above
			EquipmentHead altHead = getEquipmentHead(2);
			dam = altHead.get(StringKey.DAMAGE);
		}
		String override = get(StringKey.DAMAGE_OVERRIDE);
		if (bPrimary && override != null)
		{
			// this overides the base damage
			dam = override;
		}

		if (dam == null)
		{
			dam = getWeaponInfo("DAMAGE", bPrimary);
		}

		final int iSize = sizeInt();
		int iMod = iSize + (int) bonusTo(apc, "EQMWEAPON", "DAMAGESIZE", bPrimary);
		iMod += (int) bonusTo(apc, "WEAPON", "DAMAGESIZE", bPrimary);

		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		if (iMod < 0)
		{
			iMod = 0;
		}
		else
		{
			int maxIndex = ref.getConstructedObjectCount(SizeAdjustment.class) - 1;
			if (iMod > maxIndex)
			{
				iMod = maxIndex;
			}
		}
		return adjustDamage(dam, iMod);
	}

	/**
	 * Returns the alternate damage for this item.
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @return the alternate damage for this item.
	 */
	public String getAltDamage(final PlayerCharacter aPC)
	{
		return getDamage(aPC, false);
	}

	/**
	 * Gets the bonusToDamage attribute of the Equipment object
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @param bPrimary
	 *            if true get info about the priomary head, else get info
	 *            about the secondary head.
	 * @return The bonusToDamage value
	 */
	public int getBonusToDamage(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) bonusTo(aPC, "WEAPON", "DAMAGE", bPrimary);
	}

	/**
	 * Gets the bonusToHit attribute of the Equipment object
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @param bPrimary
	 *            if true get info about the priomary head, else get info
	 *            about the secondary head.
	 * @return The bonusToHit value
	 */
	public int getBonusToHit(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) bonusTo(aPC, "WEAPON", "TOHIT", bPrimary);
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
	public void setCarried(final Float argCarried)
	{
		carried = argCarried;
	}

	/**
	 * Returns the number of items of this type that are carried.
	 * 
	 * @return the number of items of this type that are carried.
	 */
	public Float getCarried()
	{
		return carried;
	}

	/**
	 * Gets the equipped attribute of the Equipment object
	 * 
	 * @return The equipped value
	 */
	public boolean isEquipped()
	{
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
	private Object getChild(final int childIndex)
	{
		return getContainedEquipment(childIndex);
	}

	/**
	 * Gets the childCount attribute of the Equipment object
	 * 
	 * @return The childCount value
	 */
	public int getChildCount()
	{
		return getContainedEquipmentCount();
	}

	/**
	 * Sets how many of a child the Equipment currently holds
	 * 
	 * @param aType
	 *            A type of Equiupment that may be contained in this one
	 * @param quantity
	 *            How many of the Child Type are currently contained
	 */
	private void setChildType(final String aType, final Float quantity)
	{
		d_childTypes.put(aType, quantity);
	}

	/**
	 * @param index
	 *            integer indicating which object (contained in this object) to
	 *            return
	 * @return the equipment object contained at this position.
	 */
	public Equipment getContainedByIndex(final int index)
	{

		final List<Equipment> contents = new ArrayList<>(getContents());

		if (!contents.isEmpty())
		{
			if (index <= contents.size())
			{
				return contents.get(index);
			}
		}

		return null;
	}

	/**
	 * Get a piece of contained equipment
	 * 
	 * @param i the index of the contained equipment
	 * 
	 * @return containedEquipment object
	 */
	public Equipment getContainedEquipment(final int i)
	{
		return d_containedEquipment.get(i);
	}

	/**
	 * count
	 * 
	 * @return number of containedEquipment objects
	 */
	public int getContainedEquipmentCount()
	{
		return d_containedEquipment.size();
	}

	/**
	 * Gets the contained Weight this object recursis all child objects to get
	 * their contained weight
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @return The containedWeight value
	 */
	public Float getContainedWeight(final PlayerCharacter aPC)
	{
		return getContainedWeight(aPC, false);
	}

	/**
	 * Get Base contained weight
	 * 
	 * @return Base contained weight
	 */
	public Float getBaseContainedWeight()
	{

		float total = 0.0f;

		if ((getSafe(ObjectKey.CONTAINER_CONSTANT_WEIGHT)) || (getChildCount() == 0))
		{
			return total;
		}

		for (int e = 0; e < getContainedEquipmentCount(); ++e)
		{
			final Equipment anEquip = getContainedEquipment(e);

			if (anEquip.getContainedEquipmentCount() > 0)
			{
				total = total + anEquip.getBaseWeight().floatValue() + anEquip.getBaseContainedWeight();
			}
			else
			{
				total += anEquip.getBaseWeight().floatValue() * anEquip.getCarried();
			}
		}

		Integer crw = get(IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (crw != null && crw != 0)
		{
			total *= (crw.floatValue() / 100);
		}

		return total;
	}

	/**
	 * Gets the contained Weight this object recursis all child objects to get
	 * their contained weight
	 * 
	 * @param aPC The PC that has the Equipment
	 * 
	 * @param effective
	 *            Should we recurse child objects?
	 * @return The containedWeight value
	 */
	public Float getContainedWeight(final PlayerCharacter aPC, final boolean effective)
	{
		float total = 0.0f;

		if ((getSafe(ObjectKey.CONTAINER_CONSTANT_WEIGHT) && !effective) || (getChildCount() == 0))
		{
			return total;
		}

		for (int e = 0; e < getContainedEquipmentCount(); ++e)
		{

			final Equipment anEquip = getContainedEquipment(e);

			if (anEquip.getContainedEquipmentCount() > 0)
			{
				total = (float) (total + anEquip.getWeightAsDouble(aPC) + anEquip.getContainedWeight(aPC));
			}
			else
			{
				total = (float) (total + (anEquip.getWeightAsDouble(aPC) * anEquip.getQty()));
			}
		}

		Integer crw = get(IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (crw != null && crw != 0)
		{
			total *= (crw.floatValue() / 100);
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
	public String getContainerByType(String aType, final String aSubTag)
	{

		final List<Equipment> contents = new ArrayList<>(getContents());

		// Separate the Type from the sequencer (Liquid from 3)
		int numCharToRemove = 0;

		for (int i = aType.length() - 1; i > 0; i--)
		{

			if ((aType.charAt(i) >= '0') && (aType.charAt(i) <= '9'))
			{
				numCharToRemove++;
			}
			else
			{
				break;
			}
		}

		int typeIndex;
		String type;

		if (numCharToRemove > 0)
		{
			int l = aType.length() - numCharToRemove;

			type = aType.substring(0, l);
			typeIndex = Integer.parseInt(aType.substring(l));

		}
		else
		{

			type = aType;
			typeIndex = -1;
		}

		contents.stream().filter(eq -> !eq.isType(type)).forEach(contents::remove);

		if (typeIndex < contents.size())
		{
			if ("SPROP".equals(aSubTag))
			{
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
	public String getContainerContentsString()
	{
		if (containerContentsString == null)
		{
			updateContainerContentsString(null);
		}
		return containerContentsString;
	}

	/**
	 * Convenience method. <p> <br>
	 * author: Thomas Behr 27-03-02
	 * 
	 * @return a list with all Equipment objects this container holds; if this
	 *         instance is no container, the list will be empty.
	 */
	public Collection<Equipment> getContents()
	{

		return IntStream.range(0, getContainedEquipmentCount())
			.mapToObj(this::getContainedEquipment).collect(Collectors.toList());
	}

	// ---------------------------
	// Container Definition methods
	// ---------------------------

	/**
	 * Gets the containerCapacityString attribute of the Equipment object
	 * 
	 * @return The containerCapacityString value
	 */
	public String getContainerCapacityString()
	{
		if (containerCapacityString == null)
		{
			updateContainerCapacityString();
		}
		return containerCapacityString;
	}

	private List<EquipmentHead> heads = new ArrayList<>();

	public EquipmentHead getEquipmentHead(int index)
	{
		if (index <= 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		int headsIndex = index - 1;
		int currentSize = heads.size();

		EquipmentHead head;
		if (headsIndex >= currentSize)
		{
			for (int i = 0; i < headsIndex - currentSize; i++)
			{
				heads.add(null);
			}
			head = new EquipmentHead(this, index);
			heads.add(head);
		}
		else
		{
			head = heads.get(headsIndex);
			if (head == null)
			{
				head = new EquipmentHead(this, index);
				heads.set(headsIndex, head);
			}
		}
		return head;
	}

	public EquipmentHead getEquipmentHeadReference(int index)
	{
		if (index <= 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		else if (index <= heads.size())
		{
			return heads.get(index - 1);
		}
		return null;
	}

	public List<EquipmentHead> getEquipmentHeads()
	{
		return new ArrayList<>(heads);
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 *
	 * @param aDamage The base damage
	 * @param newSizeInt The size to adjust for
	 * @return     The adjusted damage
	 */
	private String adjustDamage(final String aDamage, int newSizeInt)
	{
		if (aDamage == null)
		{
			return null;
		}
		if (!"special".equalsIgnoreCase(aDamage) && !"-".equals(aDamage))
		{
			return Globals.adjustDamage(aDamage, newSizeInt - sizeInt());
		}

		return aDamage;
	}

	/**
	 * Gets the damageAdjustedForSize attribute of the Equipment object
	 *
	 * @param newSizeInt
	 *           The size to adjust for
	 * @param bPrimary
	 *           If true get the damage for the primary head, otherwise
	 *           get the damage for the secondary head
	 * @return     The damageAdjustedForSize value
	 */
	private String getDamageAdjustedForSize(int newSizeInt, final boolean bPrimary)
	{
		int headnum = bPrimary ? 1 : 2;
		EquipmentHead head = getEquipmentHeadReference(headnum);
		if (head == null)
		{
			return null;
		}
		String dam = head.get(StringKey.DAMAGE);
		if (!isWeapon() || (!bPrimary && !isDouble()))
		{
			return dam;
		}
		if (dam == null)
		{
			dam = getWeaponInfo("DAMAGE", bPrimary);
		}
		return adjustDamage(dam, newSizeInt);
	}

	public String getWeaponInfo(final String infoType, final boolean bPrimary)
	{
		final String it = infoType + "|";
		final EquipmentModifier eqMod = getEqModifierKeyed(Constants.INTERNAL_EQMOD_WEAPON, bPrimary);

		if (eqMod != null)
		{
			return getAssociationList(eqMod).stream().filter(aString -> aString.startsWith(it)).findFirst()
				.map(aString -> aString.substring(it.length())).orElse("");
		}

		return "";
	}

	public ShieldProf getShieldProf()
	{
		if (isShield())
		{
			CDOMSingleRef<ShieldProf> ref = get(ObjectKey.SHIELD_PROF);
			if (ref == null)
			{
				ShieldProf sp = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(ShieldProf.class, getKeyName());
				if (sp == null)
				{
					return Globals.getContext().getReferenceContext().constructCDOMObject(ShieldProf.class,
						getKeyName());
				}
				else
				{
					return sp;
				}
			}
			else
			{
				return ref.get();
			}
		}
		return null;
	}

	public ArmorProf getArmorProf()
	{
		if (isArmor())
		{
			CDOMSingleRef<ArmorProf> ref = get(ObjectKey.ARMOR_PROF);
			if (ref == null)
			{
				ArmorProf ap = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(ArmorProf.class, getKeyName());
				if (ap == null)
				{
					return Globals.getContext().getReferenceContext().constructCDOMObject(ArmorProf.class,
						getKeyName());
				}
				else
				{
					return ap;
				}
			}
			else
			{
				return ref.get();
			}
		}
		return null;
	}

	public void addAssociation(CDOMObject obj, String o)
	{
		assocSupt.addAssoc(obj, AssociationListKey.CHOICES, new FixedStringList(o));
	}

	public boolean containsAssociated(CDOMObject obj, String o)
	{
		List<FixedStringList> list = assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		if (list != null)
		{
			return list.stream()
				.anyMatch(fsl -> FixedStringList.CASE_INSENSITIVE_ORDER.compare(fsl, new FixedStringList(o)) == 0);
		}
		return false;
	}

	private int getSelectCorrectedAssociationCount(CDOMObject obj)
	{
		Formula f = obj.getSafe(FormulaKey.SELECT);

		//TODO Null here is probably a problem for the PC :/
		int select = f.resolve(this, true, null, "").intValue();
		return assocSupt.getAssocCount(obj, AssociationListKey.CHOICES) / select;
	}

	public List<String> getAssociationList(CDOMObject obj)
	{
		List<String> list = new ArrayList<>();
		List<FixedStringList> assocList = assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		if (assocList != null)
		{
			assocList.stream().map(ac -> ac.get(0)).forEach(choiceStr -> {
				if (Constants.EMPTY_STRING.equals(choiceStr))
				{
					list.add(null);
				}
				else
				{
					list.add(choiceStr);
				}
			});
		}
		return list;
	}

	public boolean hasAssociations(Object obj)
	{
		return assocSupt.hasAssocs(obj, AssociationListKey.CHOICES);
	}

	public List<String> removeAllAssociations(CDOMObject obj)
	{
		List<String> list = getAssociationList(obj);
		assocSupt.removeAllAssocs(obj, AssociationListKey.CHOICES);
		return list;
	}

	private void removeAssociation(CDOMObject obj, String o)
	{
		assocSupt.removeAssoc(obj, AssociationListKey.CHOICES, new FixedStringList(o));
	}

	public String getFirstAssociation(CDOMObject obj)
	{
		return assocSupt.getAssocList(obj, AssociationListKey.CHOICES).get(0).get(0);
	}

	/**
	 * Get the map of bonuses for this object
	 * @return bonusMap
	 */
	public Map<String, String> getBonusMap()
	{
		if (bonusMap == null)
		{
			bonusMap = new HashMap<>();
		}

		return bonusMap;
	}

	/**
	 * Put the key/value pair into the bonus map
	 * 
	 * @param aKey The Key to store the bonus under
	 * @param aVal The value of the Bonus
	 */
	private void putBonusMap(final String aKey, final String aVal)
	{
		getBonusMap().put(aKey, aVal);
	}

	/**
	 * @param bonus  a Number (such as 2)
	 * @param aType  "COMBAT.AC.Dodge" or "COMBAT.AC.Dodge.STACK"
	 */
	public void setBonusStackFor(final double bonus, String aType)
	{
		String bType = (aType != null) ? aType.toUpperCase() : null;

		// Default to non-stacking bonuses
		int index = -1;

		// e.g. "COMBAT.AC.DODGE"
		if (aType != null)
		{
			final StringTokenizer aTok = new StringTokenizer(bType, ".");
			if (aTok.countTokens() >= 2)
			{

				// we need to get the 3rd token to see
				// if it should .STACK or .REPLACE
				aTok.nextToken(); // Discard token
				String aString = aTok.nextToken();
				String nextTok = null;

				// if the 3rd token is "BASE" we have something like
				// CHECKS.BASE.Fortitude
				// Type: .DODGE
				if ("BASE".equals(aString))
				{
					if (aTok.hasMoreTokens())
					{
						// discard next token (Fortitude)
						aTok.nextToken();
					}

				}
				if (aTok.hasMoreTokens())
				{
					// check for a TYPE
					nextTok = aTok.nextToken();
				}

				if (nextTok != null)
				{
					index = SettingsHandler.getGameAsProperty().get().getUnmodifiableBonusStackList().indexOf(nextTok); // e.g.
					// Dodge
				}

				// un-named (or un-TYPE'd) bonus should stack
				if ((nextTok == null) || "NULL".equals(nextTok))
				{
					index = 1;
				}
			}
		}
		// .STACK means stack
		// .REPLACE stacks with other .REPLACE bonuses
		if ((bType != null) && (bType.endsWith(".STACK") || bType.endsWith(".REPLACE")))
		{
			index = 1;
		}

		// If it's a negative bonus, it always needs to be added
		if (bonus < 0)
		{
			index = 1;
		}

		if (index == -1) // a non-stacking bonus
		{
			final String aVal = getBonusMap().get(bType);

			if (aVal == null)
			{
				putBonusMap(bType, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(bType, String.valueOf(Math.max(bonus, Float.parseFloat(aVal))));
			}
		}
		else
		// a stacking bonus
		{
			String type = bType == null ? ""
				: bType.endsWith(".REPLACE.STACK") ? bType.substring(0, bType.length() - 6) : bType;

			final String aVal = getBonusMap().get(type);

			if (aVal == null)
			{
				putBonusMap(type, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(type, String.valueOf(bonus + Float.parseFloat(aVal)));
			}
		}
	}

	private void dumpTypeCache()
	{
		usePrimaryCache = false;
		useSecondaryCache = false;
	}

	public void addType(Type newType)
	{
		addToListFor(ListKey.TYPE, newType);
		dumpTypeCache();
	}

	public void removeType(Type t)
	{
		boolean moreToRemove = true;
		while (moreToRemove)
		{
			moreToRemove = removeFromListFor(ListKey.TYPE, t);
		}
		dumpTypeCache();
	}

	/**
	 * Add a Weapon to an Equipment Location.
	 * @param num how many pieces to add
	 * @param eLoc the Location to add the weapon to
	 * @param aPC the PC to quip the weapon on
	 */
	public void addWeaponToLocation(Float num, EquipmentLocation eLoc, PlayerCharacter aPC)
	{
		float numEquipped = (eLoc == EquipmentLocation.EQUIPPED_TWO_HANDS) ? 2.0f : num;
		setNumberEquipped((int) numEquipped);

		setLocation(eLoc);

		if (eLoc != EquipmentLocation.EQUIPPED_NEITHER)
		{
			setQty(num);
			setNumberCarried(num);
			setIsEquipped(true, aPC);
		}
	}

	/**
	 * Add a piece of general equipment to an Equipment Location.
	 * @param num how many pieces to add
	 * @param eLoc the Location to add the equipment to
	 * @param equip whether to equip the item
	 * @param aPC the PC to quip the weapon on
	 */
	public void addEquipmentToLocation(Float num, EquipmentLocation eLoc, boolean equip, PlayerCharacter aPC)
	{
		setLocation(eLoc);
		setQty(num);
		setIsEquipped(equip, aPC);

		Float numCarried = (eLoc == EquipmentLocation.NOT_CARRIED) ? 0.0f : num;

		setNumberCarried(numCarried);
	}

	/**
	 * The Class {@code EquipmentHeadCostSummary} carries the multi
	 * valued response back when calculating the cost of a head.  
	 */
	private static class EquipmentHeadCostSummary
	{
		private BigDecimal postSizeCost = BigDecimal.ZERO;
		private BigDecimal nonDoubleCost = BigDecimal.ZERO;
		private int headPlus = 0;
	}

	/**
	 * Get the list of temporary bonuses for this list
	 * @return the list of temporary bonuses for this list
	 */
	private List<BonusObj> getTempBonusList()
	{
		return getSafeListFor(ListKey.TEMP_BONUS);
	}

	/**
	 * Add to the list of temporary bonuses
	 * @param aBonus
	 */
	public void addTempBonus(final BonusObj aBonus)
	{
		addToListFor(ListKey.TEMP_BONUS, aBonus);
	}

	/**
	 * Remove from the list of temporary bonuses
	 * @param aBonus
	 */
	public void removeTempBonus(final BonusObj aBonus)
	{
		removeFromListFor(ListKey.TEMP_BONUS, aBonus);
	}

	/**
	 * Reset (Clear) the temporary bonus list
	 */
	public void resetTempBonusList()
	{
		removeListFor(ListKey.TEMP_BONUS);
	}

	public boolean altersAC(PlayerCharacter pc)
	{
		String alterAC = pc.getControl(CControl.ALTERSAC);
		if (alterAC != null)
		{
			Object o = pc.getLocal(this, alterAC);
			return (Boolean) o;
		}

		return getRawBonusList(pc).stream().anyMatch(bonus -> bonus.getBonusInfo().equalsIgnoreCase("AC"));
	}

	@Override
	public String[] getTypes()
	{
		String type = getType();
		return type.split("\\.");
	}

	@Override
	public List<String> getTypesForDisplay()
	{
		List<Type> trueTypeList = getTrueTypeList(true);
		List<String> result = new ArrayList<>(trueTypeList.size());
		trueTypeList.stream().map(Type::toString).forEach(result::add);
		return result;
	}

	/**
	 * Retrieve the icon for this equipment item. This may be directly set for 
	 * the item, or it may be for one of the item's types. The types are 
	 * checked from right to left.
	 *  
	 * @return The icon for this equipment item, or null if none
	 */
	@Override
	public File getIcon()
	{
		// Check for icon on this specific item
		URI uri = this.get(ObjectKey.ICON_URI);
		if (uri != null)
		{
			return new File(uri);
		}

		// If not defined, then try the types 
		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		List<String> typeList = typeList(true);
		String iconPath = null;
		int iconPriority = 0;
		for (String type : typeList)
		{
			String path = gameMode.getEquipTypeIcon(type);
			if (path != null)
			{
				int priority = gameMode.getEquipTypeIconPriority(type);
				// Later types will win priority ties
				if (iconPath == null || priority >= iconPriority)
				{
					iconPath = path;
					iconPriority = priority;
				}
			}
		}
		if (iconPath != null)
		{
			return new File(iconPath);
		}

		// A default fallback
		String path = gameMode.getEquipTypeIcon(Constants.DEFAULT);
		if (path != null)
		{
			return new File(path);
		}

		// No icon can be found
		return null;
	}

	@Override
	public Optional<String> getLocalScopeName()
	{
		return Optional.of("PC.EQUIPMENT");
	}

	public Object getLocalVariable(CharID id, String varName)
	{
		ResultFacet resultFacet = FacetLibrary.getFacet(ResultFacet.class);
		return resultFacet.getLocalVariable(id, this, varName);
	}

	@Override
	public CDOMObject getLocalChild(String childType, String childName)
	{
		if ("EQUIPMENT.PART".equals(childType))
		{
			return getEquipmentHead(Integer.parseInt(childName));
		}
		return null;
	}

	@Override
	public List<String> getChildTypes()
	{
		return Collections.singletonList("EQUIPMENT.PART");
	}

	@Override
	public List<PCGenScoped> getChildren(String childType)
	{
		if ("EQUIPMENT.PART".equals(childType))
		{
			return new ArrayList<>(heads);
		}
		return null;
	}

	public boolean isType(Type type, boolean bPrimary)
	{
		return isType(type.toString(), bPrimary);
	}
}
