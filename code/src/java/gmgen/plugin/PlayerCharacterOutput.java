package gmgen.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import org.apache.commons.lang.math.Fraction;

import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.StatList;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.util.enumeration.AttackType;

public class PlayerCharacterOutput
{
	private PlayerCharacter pc;

	public PlayerCharacterOutput(PlayerCharacter pc)
	{
		this.pc = pc;
		Globals.setCurrentPC(pc);
	}

	public String getAC()
	{
		return Integer.toString(pc.getACTotal());
	}

	public String getACFlatFooted()
	{
		return Integer.toString(pc.flatfootedAC());
	}

	public String getACTouch()
	{
		return Integer.toString(pc.touchAC());
	}

	public String getAlignmentLong()
	{
		return SettingsHandler.getGame().getLongAlignmentAtIndex(
			pc.getAlignment());
	}

	public String getAlignmentShort()
	{
		return SettingsHandler.getGame().getShortAlignmentAtIndex(
			pc.getAlignment());
	}

	public String getBAB()
	{
		return Integer.toString(pc.baseAttackBonus());
	}

	/**
	 * TODO Much of this code is repeated in CRToken, Race, XMLCombatant and PlayerCharacterOutput
	 *  
	 * @return An output version of the CR
	 */
	public String getCR()
	{
		float cr = pc.calcCR();
		String retString = "";
		String crAsString = Float.toString(cr);
		String decimalPlaceValue =
				crAsString.substring(crAsString.length() - 2);

		// If the CR is a fractional CR then we convert to a 1/x format
		if (cr > 0 && cr < 1)
		{
			Fraction fraction = Fraction.getFraction(cr);// new Fraction(CR);
			int denominator = fraction.getDenominator();
			int numerator = fraction.getNumerator();
			retString = numerator + "/" + denominator;
		}
		else if (cr >= 1 || cr == 0)
		{
			int newCr = -99;
			if (decimalPlaceValue.equals(".0"))
			{
				newCr = (int) cr;
			}

			if (newCr > -99)
			{
				retString = retString + newCr;
			}
			else
			{
				retString = retString + cr;
			}
		}
		return retString;
	}

	public String getClasses()
	{
		StringBuffer sb = new StringBuffer();
		for (PCClass mClass : pc.getClassList())
		{
			sb.append(mClass.getDisplayName() + mClass.getLevel() + " ");
		}

		return sb.toString();
	}

	/**
	 * Retrieve the type of race the character is.
	 */
	public String getRaceType()
	{
		return pc.getRaceType();
	}

	public String getDeity()
	{
		Deity deity = pc.getDeity();

		if (deity != null)
		{
			return deity.getOutputName();
		}

		return null;
	}

	public String getDomainName(Domain domain)
	{
		return domain.getDisplayName();
	}

	public String getDomainPower(final PlayerCharacter aPC, Domain domain)
	{
		return domain.piDescString(aPC);
	}

	public String getEquipmentList()
	{
		StringBuffer sb = new StringBuffer();
		boolean firstLine = true;

		for (Equipment eq : pc.getEquipmentListInOutputOrder())
		{
			if (!firstLine)
			{
				sb.append(", ");
			}

			firstLine = false;

			DecimalFormat formater = new DecimalFormat();
			formater.setMaximumFractionDigits(1);
			formater.setMinimumFractionDigits(0);
			sb.append(formater.format(eq.getQty()) + " " + eq.getName());
		}

		return sb.toString();
	}

	public String getExportToken(String token)
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

	public String getFeatList()
	{
		StringBuffer sb = new StringBuffer();

		boolean firstLine = true;

		for (Ability feat : pc.getRealFeatList())
		{
			if (!firstLine)
			{
				sb.append(", ");
			}

			firstLine = false;
			sb.append(feat.qualifiedName());
		}

		return sb.toString();
	}

	public String getGender()
	{
		return pc.getGender();
	}

	public String getHitDice()
	{
		return getExportToken("HITDICE");
	}

	public String getHitPoints()
	{
		return Integer.toString(pc.hitPoints());
	}

	public String getInitMiscMod()
	{
		StatList sl = pc.getStatList();
		int statMod = sl.getStatModFor("DEX");
		int miscMod = pc.initiativeMod() - statMod;

		return "+" + miscMod;
	}

	public String getInitStatMod()
	{
		StatList sl = pc.getStatList();
		int statMod = sl.getStatModFor("DEX");

		return "+" + statMod;
	}

	public String getInitTotal()
	{
		return "+" + pc.initiativeMod();
	}

	public String getMeleeTotal()
	{
		int tohitBonus =
				(int) pc.getTotalBonusTo("TOHIT", "TOHIT")
					+ (int) pc.getTotalBonusTo("TOHIT", "TYPE.MELEE")
					+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT")
					+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT.MELEE");

		return pc.getAttackString(AttackType.MELEE, tohitBonus);
	}

	public String getName()
	{
		return pc.getName();
	}

	public String getRaceName()
	{
		return pc.getRace().getDisplayName();
	}

	public String getRangedTotal()
	{
		int tohitBonus =
				(int) pc.getTotalBonusTo("TOHIT", "TOHIT")
					+ (int) pc.getTotalBonusTo("TOHIT", "TYPE.RANGED")
					+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT")
					+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT.RANGED");

		return pc.getAttackString(AttackType.MELEE, tohitBonus);
	}

	public String getRegion()
	{
		return pc.getRegion();
	}

	public String getSaveFort()
	{
		return "+" + pc.getTotalCheck(0);
	}

	public String getSaveRef()
	{
		return "+" + pc.getTotalCheck(1);
	}

	public String getSaveWill()
	{
		return "+" + pc.getTotalCheck(2);
	}

	public String getSize()
	{
		return pc.getSize();
	}

	public String getSpecialAbilities()
	{
		return CoreUtility.join(pc.getSpecialAbilityTimesList(), ", ");
	}

	public String getSpeed()
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < pc.getNumberOfMovements(); i++)
		{
			sb.append(pc.getMovementType(i)
				+ " "
				+ Globals.getGameModeUnitSet().convertDistanceToUnitSet(
					pc.movement(i))
				+ Globals.getGameModeUnitSet().getDistanceUnit());
		}

		return sb.toString();
	}

	public String getStat(String statAbbrev)
	{
		StatList sl = pc.getStatList();

		return Integer.toString(sl.getTotalStatFor(statAbbrev));
	}

	public StatList getStatList()
	{
		return pc.getStatList();
	}

	public String getStatMod(String statAbbrev)
	{
		int returnValue;

		StatList sl = pc.getStatList();
		returnValue = sl.getStatModFor(statAbbrev);

		return (returnValue < 0) ? Integer.toString(returnValue) : "+"
			+ returnValue;
	}

	public String getVision()
	{
		return pc.getVision();
	}

	public String getWeaponToken(int weaponNo, String Token)
	{
		return getExportToken("WEAPON." + weaponNo + "." + Token);
	}

	public String getWeaponCritMult(int weaponNo)
	{
		return getWeaponToken(weaponNo, "MULT");
	}

	public String getWeaponCritRange(int weaponNo)
	{
		return getWeaponToken(weaponNo, "CRIT");
	}

	public String getWeaponDamage(int weaponNo)
	{
		return getWeaponToken(weaponNo, "DAMAGE");
	}

	public String getWeaponHand(Equipment eq)
	{
		String location = Equipment.getLocationName(eq.getLocation());
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

	public String getWeaponName(Equipment eq)
	{
		return eq.getOutputName() + eq.getAppliedName();
	}

	public String getWeaponRange(Equipment eq)
	{
		return eq.getRange(pc).toString()
			+ Globals.getGameModeUnitSet().getDistanceUnit();
	}

	public String getWeaponSize(Equipment eq)
	{
		return eq.getSize();
	}

	public String getWeaponSpecialProperties(Equipment eq)
	{
		return eq.getSpecialProperties(pc);
	}

	public String getWeaponToHit(int weaponNo)
	{
		return getWeaponToken(weaponNo, "TOTALHIT");
	}

	public String getWeaponType(Equipment eq)
	{
		String types = getWeaponType(eq, true);

		if (eq.isDouble())
		{
			types += ('/' + getWeaponType(eq, false));
		}

		return types;
	}

	public String getWeaponType(Equipment eq, boolean primary)
	{
		StringBuffer sb = new StringBuffer();
		StringTokenizer aTok =
				new StringTokenizer(SettingsHandler.getGame().getWeaponTypes(),
					"|", false);

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

	public String getUnarmedAttack()
	{
		return getExportToken("WEAPONH.TOTALHIT");
	}

	public String getUnarmedDamage()
	{
		return getExportToken("WEAPONH.DAMAGE");
	}

	public String getUnarmedCritRange()
	{
		return getExportToken("WEAPONH.CRIT");
	}

	public String getUnarmedCritMult()
	{
		return getExportToken("WEAPONH.MULT");
	}
}
