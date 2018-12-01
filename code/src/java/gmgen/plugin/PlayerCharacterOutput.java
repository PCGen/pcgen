/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.commons.lang3.math.Fraction;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.QualifiedName;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.VisionDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.MovementToken;
import pcgen.util.Delta;
import pcgen.util.enumeration.AttackType;

/*
 * TODO This needs to be merged with pcgen.core.display.CharacterDisplay
 */
public class PlayerCharacterOutput
{
	private final PlayerCharacter pc;
	private final CharacterDisplay display;

	public PlayerCharacterOutput(PlayerCharacter pc)
	{
		this.pc = pc;
		this.display = (pc == null) ? null : pc.getDisplay();
	}

	public String getAC()
	{
		return Integer.toString(display.calcACOfType("Total"));
	}

	String getACFlatFooted()
	{
		return Integer.toString(display.calcACOfType("Flatfooted"));
	}

	String getACTouch()
	{
		return Integer.toString(display.calcACOfType("Touch"));
	}

	String getAlignment()
	{
		PCAlignment pcAlignment = display.getPCAlignment();
		return (pcAlignment == null) ? "" : pcAlignment.getKeyName();
	}

	public String getBAB()
	{
		return Integer.toString(pc.baseAttackBonus());
	}

	/**
	 * TODO Much of this code is repeated in CRToken, Race, Combatant and PlayerCharacterOutput
	 *  
	 * @return An output version of the CR
	 */
	public String getCR()
	{
		Integer calcCR = display.calcCR();
		float cr = (calcCR == null) ? -1 : calcCR;
		String retString = "";

		// If the CR is a fractional CR then we convert to a 1/x format
		if ((cr > 0) && (cr < 1))
		{
			Fraction fraction = Fraction.getFraction(cr); // new Fraction(CR);
			int denominator = fraction.getDenominator();
			int numerator = fraction.getNumerator();
			retString = numerator + "/" + denominator;
		}
		else if ((cr >= 1) || (cr == 0))
		{
			int newCr = -99;
			String crAsString = Float.toString(cr);
			String decimalPlaceValue = crAsString.substring(crAsString.length() - 2);
			if (decimalPlaceValue.equals(".0"))
			{
				newCr = (int) cr;
			}

			retString += ((newCr > -99) ? newCr : cr);
		}
		return retString;
	}

	public String getClasses()
	{
		StringBuilder sb = new StringBuilder();
		for (PCClass mClass : display.getClassSet())
		{
			sb.append(mClass.getDisplayName()).append(display.getLevel(mClass)).append(" ");
		}

		return sb.toString();
	}

	/**
	 * Retrieve the type of race the character is.
	 */
	public String getRaceType()
	{
		return display.getRaceType();
	}

	public String getDeity()
	{
		Deity deity = display.getDeity();

		if (deity != null)
		{
			return deity.getOutputName();
		}

		return null;
	}

	static String getDomainName(Domain domain)
	{
		return domain.getDisplayName();
	}

	String getEquipmentList()
	{
		StringBuilder sb = new StringBuilder();
		boolean firstLine = true;

		for (Equipment eq : pc.getEquipmentListInOutputOrder())
		{
			if (!firstLine)
			{
				sb.append(", ");
			}

			firstLine = false;

			NumberFormat formater = new DecimalFormat();
			formater.setMaximumFractionDigits(1);
			formater.setMinimumFractionDigits(0);
			sb.append(formater.format(eq.getQty())).append(" ").append(eq.getName());
		}

		return sb.toString();
	}

	String getExportToken(String token)
	{
		try
		{
			StringWriter retWriter = new StringWriter();
			BufferedWriter bufWriter = new BufferedWriter(retWriter);
			ExportHandler export = new ExportHandler(new File(""));
			export.replaceTokenSkipMath(pc, token, bufWriter);
			retWriter.flush();

			try
			{
				bufWriter.flush();
			}
			catch (IOException e)
			{
				// TODO - Handle Exception
			}

			return retWriter.toString();
		}
		catch (Exception e)
		{
			System.out.println("Failure fetching token: " + token);
			return "";
		}
	}

	String getFeatList()
	{
		StringBuilder sb = new StringBuilder();

		boolean firstLine = true;

		for (CNAbility cna : pc.getCNAbilities(AbilityCategory.FEAT, Nature.NORMAL))
		{
			if (!firstLine)
			{
				sb.append(", ");
			}

			firstLine = false;
			sb.append(QualifiedName.qualifiedName(pc, Collections.singletonList(cna)));
		}

		return sb.toString();
	}

	public String getGender()
	{
		return pc.getGenderString();
	}

	String getHitDice()
	{
		return getExportToken("HITDICE");
	}

	public String getHitPoints()
	{
		return Integer.toString(pc.hitPoints());
	}

	String getInitMiscMod()
	{
		String initiativeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.INITIATIVEMISC);
		if (initiativeVar == null)
		{
			PCStat dex =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, "DEX");
			int statMod = pc.getStatModFor(dex);
			return "+" + (display.processOldInitiativeMod() - statMod);
		}
		return Delta.toString(((Number) pc.getGlobal(initiativeVar)).intValue());
	}

	String getInitStatMod()
	{
		String initiativeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.INITIATIVESTAT);
		if (initiativeVar == null)
		{
			PCStat dex =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, "DEX");
			return "+" + pc.getStatModFor(dex);
		}
		return Delta.toString(((Number) pc.getGlobal(initiativeVar)).intValue());
	}

	String getInitTotal()
	{
		String initiativeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.INITIATIVE);
		if (initiativeVar == null)
		{
			return "+" + display.processOldInitiativeMod();
		}
		return Delta.toString(((Number) pc.getGlobal(initiativeVar)).intValue());
	}

	String getMeleeTotal()
	{
		int tohitBonus = (int) pc.getTotalBonusTo("TOHIT", "TOHIT") + (int) pc.getTotalBonusTo("TOHIT", "TYPE.MELEE")
			+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT") + (int) pc.getTotalBonusTo("COMBAT", "TOHIT.MELEE");

		return pc.getAttackString(AttackType.MELEE, tohitBonus);
	}

	public String getName()
	{
		return display.getName();
	}

	public String getRaceName()
	{
		return display.getRace().getDisplayName();
	}

	String getRangedTotal()
	{
		int tohitBonus = (int) pc.getTotalBonusTo("TOHIT", "TOHIT") + (int) pc.getTotalBonusTo("TOHIT", "TYPE.RANGED")
			+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT") + (int) pc.getTotalBonusTo("COMBAT", "TOHIT.RANGED");

		return pc.getAttackString(AttackType.MELEE, tohitBonus);
	}

	public String getRegion()
	{
		return display.getRegionString();
	}

	public String getSize()
	{
		return pc.getSizeAdjustment().getKeyName();
	}

	String getSpecialAbilities()
	{
		return StringUtil.join(pc.getSpecialAbilityTimesList(), ", ");
	}

	public String getSpeed()
	{
		return MovementToken.getMovementToken(display);
	}

	public String getStat(PCStat stat)
	{
		return Integer.toString(pc.getTotalStatFor(stat));
	}

	public String getStatMod(PCStat stat)
	{
		int returnValue;

		returnValue = pc.getStatModFor(stat);

		return (returnValue < 0) ? Integer.toString(returnValue) : ("+" + returnValue);
	}

	public String getVision()
	{
		return VisionDisplay.getVision(display);
	}

	private String getWeaponToken(int weaponNo, String Token)
	{
		return getExportToken("WEAPON." + weaponNo + "." + Token);
	}

	String getWeaponCritMult(int weaponNo)
	{
		return getWeaponToken(weaponNo, "MULT");
	}

	String getWeaponCritRange(int weaponNo)
	{
		return getWeaponToken(weaponNo, "CRIT");
	}

	String getWeaponDamage(int weaponNo)
	{
		return getWeaponToken(weaponNo, "DAMAGE");
	}

	static String getWeaponHand(Equipment eq)
	{
		String location = eq.getLocation().getString();
		final int start = location.indexOf('(') + 1; // move past the paren

		if (start > 0)
		{
			int end = location.indexOf(')', start);

			if (end > 0)
			{
				location = location.substring(start, end);
			}
		}

		return location;
	}

	static String getWeaponName(Equipment eq)
	{
		return eq.getOutputName() + eq.getAppliedName();
	}

	String getWeaponRange(Equipment eq)
	{
		return EqToken.getRange(pc, eq) + Globals.getGameModeUnitSet().getDistanceUnit();
	}

	static String getWeaponSize(Equipment eq)
	{
		return eq.getSize();
	}

	String getWeaponSpecialProperties(Equipment eq)
	{
		return eq.getSpecialProperties(pc);
	}

	String getWeaponToHit(int weaponNo)
	{
		return getWeaponToken(weaponNo, "TOTALHIT");
	}

	static String getWeaponType(Equipment eq)
	{
		String types = getWeaponType(eq, true);

		if (eq.isDouble())
		{
			types += ('/' + getWeaponType(eq, false));
		}

		return types;
	}

	private static String getWeaponType(Equipment eq, boolean primary)
	{
		StringBuilder sb = new StringBuilder();
		StringTokenizer aTok = new StringTokenizer(SettingsHandler.getGame().getWeaponTypes(), "|", false);

		while (aTok.countTokens() >= 2)
		{
			final String aType = aTok.nextToken();
			final String abbrev = aTok.nextToken();

			if (eq.isType(aType, true))
			{
				sb.append(abbrev);
			}
		}

		return sb.toString();
	}

	Collection<PCStat> getUnmodifiableStatList()
	{
		return display.getStatSet();
	}
}
