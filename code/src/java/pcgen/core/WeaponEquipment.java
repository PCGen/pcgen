package pcgen.core;

import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;
import java.util.List;
import java.util.ArrayList;

public class WeaponEquipment implements Cloneable
{
	private Equipment theEquipment;
	private String theDamage = "";
	private String theDamageMod = "";
	private Integer theRange = new Integer(0);
	private String theCritRange = "";
	private int theCritMult = 0;
	private String theRateOfFire = "";
	private String theWieldString = "";
	private boolean hasWield = false;
	private int theReach = 0;
	/** if is true a BAB of 13 yields 13/8/3, if false, merely 13. */
	private boolean doAttacksProgress = true;

	private String theAltDamage = "";
	private String theAltCritRange = "";
	private int theAltCritMult = 0;

	public WeaponEquipment(final Equipment anOwner)
	{
		theEquipment = anOwner;
	}

	public void setOwner( final Equipment anOwner )
	{
		theEquipment = anOwner;
	}

	/**
	 * Gets the bonusToDamage attribute of the Equipment object
	 * @param aPC
	 *
	 * @param bPrimary Description of the Parameter
	 * @return     The bonusToDamage value
	 */
	public int getBonusToDamage(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) theEquipment.bonusTo(aPC, "WEAPON", "DAMAGE", bPrimary);
	}

	/**
	 * Gets the bonusToHit attribute of the Equipment object
	 * @param aPC
	 *
	 * @param bPrimary Description of the Parameter
	 * @return     The bonusToHit value
	 */
	public int getBonusToHit(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) theEquipment.bonusTo(aPC, "WEAPON", "TOHIT", bPrimary);
	}

	/**
	 * Gets the critRange attribute of the Equipment object
	 * @param aPC
	 *
	 * @return The critRange value
	 */
	public String getCritRange(final PlayerCharacter aPC)
	{
		return getCritRange(aPC, true);
	}

	/**
	 * Gets the altCritRange attribute of the Equipment object
	 * @param aPC
	 *
	 * @return The altCritRange value
	 */
	public String getAltCritRange(final PlayerCharacter aPC)
	{
		return getCritRange(aPC, false);
	}

	/**
	 * Gets the critRangeAdd attribute of the Equipment object
	 * @param aPC
	 *
	 * @param bPrimary Description of the Parameter
	 * @return     The critRangeAdd value
	 */
	public int getCritRangeAdd(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) theEquipment.bonusTo(aPC, "EQMWEAPON", "CRITRANGEADD", bPrimary);
	}

	/**
	 * Gets the critRangeDouble attribute of the Equipment object
	 * @param aPC
	 *
	 * @param bPrimary Description of the Parameter
	 * @return     The critRangeDouble value
	 */
	public int getCritRangeDouble(final PlayerCharacter aPC, final boolean bPrimary)
	{
		return (int) theEquipment.bonusTo(aPC, "EQMWEAPON", "CRITRANGEDOUBLE", bPrimary);
	}

	/**
	 * Sets the damage attribute of the Equipment object
	 *
	 * @param aString The new damage value
	 */
	public void setDamage(final String aString)
	{
		theDamage = aString;
	}

	/**
	 * Gets the damage attribute of the Equipment object
	 * @param aPC
	 *
	 * @return The damage value
	 */
	public String getDamage(final PlayerCharacter aPC)
	{
		return getDamage(aPC, true);
	}

	public String getAltDamage(final PlayerCharacter aPC)
	{
		return getDamage(aPC, false);
	}

	public void setAltDamage(final String anAltDamage)
	{
		theAltDamage = anAltDamage;
	}

	/**
	 * Sets the range attribute of the Equipment object
	 *
	 * @param aString The new range value
	 */
	public void setRange(final String aString)
	{
		try
		{
			theRange = Delta.decode(aString);
		}
		catch (NumberFormatException ignore)
		{
			// ignore
		}
	}

	/**
	 * Gets the range attribute of the Equipment object
	 *
	 * @return The range value
	 * @param aPC
	 */
	public Integer getRange(final PlayerCharacter aPC)
	{
		Integer myRange = theRange;

		if (myRange.intValue() == 0)
		{
			final String aRange = getWeaponInfo("RANGE", true);

			if (aRange.length() != 0)
			{
				myRange = new Integer(aRange);
			}
		}

		int r = myRange.intValue() + (int) theEquipment.bonusTo(aPC, "EQMWEAPON", "RANGEADD", true);
		final int i = (int) theEquipment.bonusTo(aPC, "EQMWEAPON", "RANGEMULT", true);
		double rangeMult = 1.0;

		if (i > 0)
		{
			rangeMult += (i - 1);
		}

		int postAdd = 0;

		if (aPC != null)
		{
			if (theEquipment.isThrown())
			{
				r += (int) aPC.getTotalBonusTo("RANGEADD", "THROWN");
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "THROWN");
				rangeMult += ((int) aPC.getTotalBonusTo("RANGEMULT", "THROWN") / 100.0);
			}
			else if (theEquipment.isProjectile())
			{
				r += (int) aPC.getTotalBonusTo("RANGEADD", "PROJECTILE");
				postAdd = (int) aPC.getTotalBonusTo("POSTRANGEADD", "PROJECTILE");
				rangeMult += ((int) aPC.getTotalBonusTo("RANGEMULT", "PROJECTILE") / 100.0);
			}
		}

		r *= rangeMult;
		r += postAdd;

		// If it's a ranged, thrown or projectile, it must have a range
		if ((theEquipment.isRanged() || theEquipment.isThrown() || theEquipment.isProjectile()) && (r <= 0))
		{
			r = 10;
		}

		return new Integer(r);
	}

	/**
	 * Gets the range list of the Equipment object, adding the 30' range, if not present and required
	 *
	 * @param addShortRange boolean
	 * @param aPC
	 * @return The range list
	 */
	public List<String> getRangeList(boolean addShortRange, final PlayerCharacter aPC)
	{
		final List<String> aList = new ArrayList<String>();
		final int baseRange = getRange(aPC).intValue();
		int aRange = baseRange;
		int maxIncrements = 0;

		if (theEquipment.isRanged())
		{
			if (theEquipment.isThrown())
			{
				maxIncrements = 5;
			}
			else
			{
				maxIncrements = 10;
			}
		}

		for (int numIncrements = 0; numIncrements < maxIncrements; ++numIncrements)
		{
			if (aRange == SettingsHandler.getGame().getShortRangeDistance())
			{
				addShortRange = false;
			}

			if ((aRange > SettingsHandler.getGame().getShortRangeDistance()) && addShortRange)
			{
				aList.add(Integer.toString(SettingsHandler.getGame().getShortRangeDistance()));
				addShortRange = false;
			}

			aList.add(Integer.toString(aRange));
			aRange += baseRange;
		}

		return aList;
	}

	/**
	 * Sets the critRange attribute of the Equipment object
	 *
	 * @param aString The new critRange value
	 */
	public void setCritRange(final String aCritRange)
	{
		theCritRange = aCritRange;
	}

	public void setAltCritRange(final String aCritRange)
	{
		theAltCritRange = aCritRange;
	}

	/**
	 * Gets the rawCritRange attribute of the Equipment object
	 *
	 * @return The rawCritRange value
	 */
	public int getRawCritRange()
	{
		return getRawCritRange(true);
	}

	/**
	 * Gets the rawCritRange attribute of the Equipment object
	 *
	 * @param bPrimary True=Primary Head
	 * @return The rawCritRange value
	 */
	public int getRawCritRange(final boolean bPrimary)
	{
		String cr = bPrimary ? theCritRange : theAltCritRange;

		if (cr.length() == 0)
		{
			cr = getWeaponInfo("CRITRANGE", true);
		}

		if (cr.length() != 0)
		{
			try
			{
				return Integer.parseInt(cr);
			}
			catch (NumberFormatException ignore)
			{
				//ignore
			}
		}

		return 0;
	}

	/**
	 * Get damage mod
	 * @return damage mod
	 */
	public String getDamageMod()
	{
		return theDamageMod;
	}

	/**
	 * Set damage (this is used to overide default equipment)
	 *
	 * @param aString The new damage value
	 **/
	public void setDamageMod(final String aDamageMod)
	{
		theDamageMod = aDamageMod;
	}

	public Integer getRange()
	{
		return theRange;
	}

	public Object clone()
	{
		WeaponEquipment weapon = null;

		try
		{
			weapon = (WeaponEquipment) super.clone();
			weapon.theDamage = new String(theDamage);
			weapon.theDamageMod = new String(theDamageMod);
			weapon.theRange = new Integer(theRange);
			weapon.theCritRange = new String(theCritRange);
			weapon.theCritMult = theCritMult;
			weapon.theRateOfFire = new String(theRateOfFire);
			weapon.theWieldString = new String(theWieldString);
			weapon.hasWield = hasWield;
			weapon.theReach = theReach;

			weapon.theAltDamage = new String(theAltDamage);
			weapon.theAltCritRange = new String(theAltCritRange);
			weapon.theAltCritMult = theAltCritMult;
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return weapon;
	}

	private String getDamage(final PlayerCharacter aPC, final boolean bPrimary)
	{
		String baseDamage = bPrimary ? theDamage : theAltDamage;

		if (!theEquipment.isWeapon() || (!bPrimary && !theEquipment.isDouble()))
		{
			return baseDamage;
		}

		if ( bPrimary && theDamage.length() == 0 && theAltDamage.length() != 0 )
		{
			baseDamage = theAltDamage;
		}

		if (bPrimary && (theDamageMod.length() != 0))
		{
			// this overides the base damage
			baseDamage = theDamageMod;
		}

		if (baseDamage.length() == 0)
		{
			baseDamage = getWeaponInfo("DAMAGE", bPrimary);
		}

		final int iSize = theEquipment.sizeInt();
		int iMod = iSize + (int) theEquipment.bonusTo(aPC, "EQMWEAPON", "DAMAGESIZE", bPrimary);
		iMod += (int) theEquipment.bonusTo(aPC, "WEAPON", "DAMAGESIZE", bPrimary);

		if (iMod < 0)
		{
			iMod = 0;
		}
		else if (iMod >= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1))
		{
			iMod = SettingsHandler.getGame().getSizeAdjustmentListSize() - 1;
		}

		final SizeAdjustment sadj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(iMod);
		String adjAbbrev = "";
		if (sadj != null)
		{
			adjAbbrev = sadj.getAbbreviation();
		}
		return adjustDamage(baseDamage, adjAbbrev);
	}

	private String getWeaponInfo(final String infoType, final boolean bPrimary)
	{
		final String it = infoType + "|";
		final EquipmentModifier eqMod = theEquipment.getEqModifierKeyed("PCGENi_WEAPON", bPrimary);

		if (eqMod != null)
		{
			for (int i = 0; i < eqMod.getAssociatedCount(); ++i)
			{
				final String aString = eqMod.getAssociated(i);

				if (aString.startsWith(it))
				{
					return aString.substring(it.length());
				}
			}
		}

		return "";
	}

	private String getCritRange(final PlayerCharacter aPC, final boolean bPrimary)
	{
		String cr = bPrimary ? theCritRange : theAltCritRange;

		if (cr.length() == 0)
		{
			cr = getWeaponInfo("CRITRANGE", bPrimary);
		}

		if ((cr.length() == 0) || (!bPrimary && !theEquipment.isDouble()))
		{
			return "";
		}

		return Integer.toString((getRawCritRange(bPrimary) * (getCritRangeDouble(aPC, bPrimary) + 1))
			+ getCritRangeAdd(aPC, bPrimary));
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 *
	 * @param aDamage The base damage
	 * @param aSize   The size to adjust for
	 * @return     The adjusted damage
	 */
	private String adjustDamage(final String aDamage, final String aSize)
	{
		if (!aDamage.equalsIgnoreCase("special") && !aDamage.equals("-"))
		{
			return Globals.adjustDamage(aDamage, theEquipment.getSize(), aSize);
		}

		return aDamage;
	}

	/**
	 * Gets the altDamageAdjustedForSize attribute of the Equipment object
	 *
	 * @param aSize The size to adjust for
	 * @return     The altDamageAdjustedForSize value
	 */
	protected String getAltDamageAdjustedForSize(final String aSize)
	{
		return getDamageAdjustedForSize(aSize, false);
	}

	/**
	 * Gets the damageAdjustedForSize attribute of the Equipment object
	 *
	 * @param aSize The size to adjust for
	 * @param bPrimary
	 * @return     The damageAdjustedForSize value
	 */
	private String getDamageAdjustedForSize(final String aSize, final boolean bPrimary)
	{
		String baseDamage = bPrimary ? theDamage : theAltDamage;

		if (theEquipment.isWeapon())
		{
			if (baseDamage.length() == 0)
			{
				baseDamage = getWeaponInfo("DAMAGE", bPrimary);
			}

			if (baseDamage.length() != 0)
			{
				return adjustDamage(baseDamage, aSize);
			}
		}

		return baseDamage;
	}

	protected String getDamageAdjustedForSize(final String aSize)
	{
		return getDamageAdjustedForSize(aSize, true);
	}

	private int getCritMultiplier(final boolean bPrimary)
	{
		int mult = bPrimary ? theCritMult : theAltCritMult;

		if (mult == 0)
		{
			final String cm = getWeaponInfo("CRITMULT", bPrimary);

			if (cm.length() != 0)
			{
				mult = Integer.parseInt(cm);
			}
		}

		return mult;
	}

	/**
	 * Sets the critMult attribute of the Equipment object
	 *
	 * @param aMult The new critMult value
	 */
	public void setCritMult(final int aMult)
	{
		theCritMult = aMult;
	}

	/**
	 * Gets the critMult attribute of the Equipment object
	 *
	 * @return The critMult value
	 */
	public String getCritMult()
	{
		return multAsString(getCritMultiplier(true));
	}

	/**
	 * Gets the unmodified crit multiplier for the weapon.
	 *
	 * @return the crit multiplier
	 */
	public int getRawCritMult()
	{
		return theCritMult;
	}

	/**
	 * Sets the altCritMult attribute of the Equipment object
	 *
	 * @param aMult The new altCritMult value
	 */
	public void setAltCritMult(final int aMult)
	{
		theAltCritMult = aMult;
	}

	/**
	 * Gets the altCritMult attribute of the Equipment object
	 *
	 * @return The altCritMult value
	 */
	public String getAltCritMult()
	{
		// Use primary if none defined
		if (theAltCritMult == 0)
		{
			return getCritMult();
		}

		return multAsString(getCritMultiplier(false));
	}

	/**
	 * Gets the unmodified alt crit multiplier for the weapon.
	 *
	 * @return the alt crit multiplier
	 */
	public int getRawAltCritMult()
	{
		return theAltCritMult;
	}

	/**
	 * Gets the critMultiplier attribute of the Equipment object
	 *
	 * @return The critMultiplier value
	 */
	public int getCritMultiplier()
	{
		return multAsInt(getCritMultiplier(true));
	}

	/**
	 * Gets the altCritMultiplier attribute of the Equipment object
	 *
	 * @return The altCritMultiplier value
	 */
	public int getAltCritMultiplier()
	{
		// Use primary if none defined
		if (theAltCritMult == 0)
		{
			return getCritMultiplier();
		}

		return multAsInt(getCritMultiplier(false));
	}

	/**
	 * Description of the Method
	 *
	 * @param mult Description of the Parameter
	 * @return    Description of the Return Value
	 */
	private static int multAsInt(final int mult)
	{
		if (mult < 0)
		{
			return 0;
		}

		return mult;
	}

	/**
	 * Description of the Method
	 *
	 * @param mult Description of the Parameter
	 * @return    Description of the Return Value
	 */
	private static String multAsString(final int mult)
	{
		if (mult == 0)
		{
			return "";
		}
		else if (mult < 0)
		{
			return "-";
		}

		return "x" + Integer.toString(mult);
	}

	/**
	 * Set the weapon's rate of fire
	 *
	 * @param rateOfFire A free-format string.
	 */
	public void setRateOfFire(final String aRateOfFire)
	{
		theRateOfFire = aRateOfFire;
	}

	/**
	 * Returns the weapon's rate of fire
	 * Defaults to empty string
	 *
	 * @return The weapon's rate of fire
	 */
	public String getRateOfFire()
	{
		return theRateOfFire;
	}

	/**
	 * new 3.5 Wield Category
	 * @param aString
	 */
	public void setWield(final String aString)
	{
		theWieldString = aString;
		hasWield = true;
	}

	/**
	 * Get weild
	 * @return weild
	 */
	public String getWield()
	{
		return theWieldString;
	}

	/**
	 * Returns TRUE true if it is weildable
	 * @return true if it is weildable
	 */
	public boolean hasWield()
	{
		return hasWield;
	}

	/**
	 * Sets the reach attribute of the Equipment object.
	 *
	 * @param newReach The new reach value
	 */
	public void setReach(final int newReach)
	{
		theReach = newReach;
	}

	/**
	 * Gets the reach attribute of the Equipment object.
	 *
	 * @return The reach value
	 */
	public int getReach()
	{
		return theReach;
	}

	/**
	 * Returns whether to give several attacks
	 *
	 * @param argAttacksProgress whether to give several attacks.
	 */
	public void setAttacksProgress(final boolean argAttacksProgress)
	{
		doAttacksProgress = argAttacksProgress;
	}

	/**
	 * if true a BAB of 13 yields 13/8/3, if false, merely 13
	 *
	 * @return whether it gives several attacks
	 */
	public boolean isAttacksProgress()
	{
		return doAttacksProgress;
	}


}
