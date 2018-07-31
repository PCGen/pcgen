/*
 * EqContainersToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.exporttokens;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.Token;
import pcgen.util.BigDecimalHelper;

/**
 * Deals with Tokens below:
 *
 * EQCONTAINERS.x.ACCHECK
 * EQCONTAINERS.x.ACMOD
 * EQCONTAINERS.x.ALTCRIT
 * EQCONTAINERS.x.ALTDAMAGE
 * EQCONTAINERS.x.ATTACKS
 * EQCONTAINERS.x.CARRIED
 * EQCONTAINERS.x.CONTENTS.?
 * EQCONTAINERS.x.CONTENTWEIGHT
 * EQCONTAINERS.x.COST
 * EQCONTAINERS.x.CRITMULT
 * EQCONTAINERS.x.CRITRANGE
 * EQCONTAINERS.x.DAMAGE
 * EQCONTAINERS.x.EDR
 * EQCONTAINERS.x.EQUIPPED
 * EQCONTAINERS.x.ITEMWEIGHT
 * EQCONTAINERS.x.LOCATION
 * EQCONTAINERS.x.LONGNAME
 * EQCONTAINERS.x.MAXDEX
 * EQCONTAINERS.x.MOVE
 * EQCONTAINERS.x.NAME
 * EQCONTAINERS.x.OUTPUTNAME
 * EQCONTAINERS.x.PROF
 * EQCONTAINERS.x.QTY
 * EQCONTAINERS.x.RANGE
 * EQCONTAINERS.x.SIZE
 * EQCONTAINERS.x.SPELLFAILURE
 * EQCONTAINERS.x.SPROP
 * EQCONTAINERS.x.TOTALWEIGHT
 * EQCONTAINERS.x.TYPE.?
 * EQCONTAINERS.x.WT
 */
public class EqContainersToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "EQCONTAINERS";
	/** Indent, a Tab Character */
	public static final String INDENT = "\t";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		aTok.nextToken(); //clear EQCONTAINERS Token
		Equipment eq = null;
		if (aTok.hasMoreElements())
		{
			try
			{
				int containerNo = Integer.parseInt(aTok.nextToken());
				eq = getContainer(pc, containerNo);
			}
			catch (NumberFormatException e)
			{
				// TODO - This exception needs to be handled
			}
		}

		if (eq != null)
		{
			String property = "NAME";
			if (aTok.hasMoreElements())
			{
				property = aTok.nextToken();
			}

			if (property.equals("ACCHECK"))
			{
				retString = Integer.toString(EqToken.getAcCheckTokenInt(pc, eq));
			}
			else if (property.equals("ACMOD"))
			{
				retString = Integer.toString(getAcModToken(pc, eq));
			}
			else if (property.equals("ALTCRIT"))
			{
				retString = getAltCritToken(eq);
			}
			else if (property.equals("ALTDAMAGE"))
			{
				retString = getAltDamageToken(pc, eq);
			}
			else if (property.equals("ATTACKS"))
			{
				retString = Double.toString(getAttacksToken(pc, eq));
			}
			else if (property.equals("CARRIED"))
			{
				retString = Float.toString(getCarriedToken(eq));
			}
			else if (property.equals("CONTENTS"))
			{
				retString = getContentsToken(eq, aTok);
			}
			else if (property.equals("CONTENTWEIGHT"))
			{
				retString = BigDecimalHelper.trimZeros(Float.toString(getContentWeightToken(pc, eq)));
			}
			else if (property.equals("COST"))
			{
				retString = BigDecimalHelper.trimZeros(getCostToken(pc, eq));
			}
			else if (property.equals("CRITMULT"))
			{
				retString = getCritMultToken(eq);
			}
			else if (property.equals("CRITRANGE"))
			{
				retString = EqToken.getCritRangeToken(pc, eq);
			}
			else if (property.equals("DAMAGE"))
			{
				retString = getDamageToken(pc, eq);
			}
			else if (property.equals("EDR"))
			{
				retString = Integer.toString(EqToken.getEdrTokenInt(pc, eq));
			}
			else if (property.equals("EQUIPPED"))
			{
				retString = getEquippedToken(eq);
			}
			else if (property.equals("ITEMWEIGHT"))
			{
				retString = BigDecimalHelper.trimZeros(Float.toString(getItemWeightToken(pc, eq)));
			}
			else if (property.equals("LOCATION"))
			{
				retString = getLocationToken(eq);
			}
			else if (property.equals("LONGNAME"))
			{
				retString = getLongNameToken(eq);
			}
			else if (property.equals("MAXDEX"))
			{
				retString = Integer.toString(EqToken.getMaxDexTokenInt(pc, eq));
			}
			else if (property.equals("MOVE"))
			{
				retString = getMoveToken(eq);
			}
			else if (property.equals("NAME") || property.equals("OUTPUTNAME"))
			{
				retString = getNameToken(eq, pc);
			}
			else if (property.equals("PROF"))
			{
				retString = eq.consolidatedProfName();
			}
			else if (property.equals("QTY"))
			{
				retString = BigDecimalHelper.trimZeros(Double.toString((getQuantityToken(eq))));
			}
			else if (property.equals("RANGE"))
			{
				retString = Integer.toString(EqToken.getRange(pc, eq).intValue());
			}
			else if (property.equals("SIZE"))
			{
				retString = getSizeToken(eq);
			}
			else if (property.equals("SPELLFAILURE"))
			{
				retString = Integer.toString(EqToken.getSpellFailureTokenInt(pc, eq));
			}
			else if (property.equals("SPROP"))
			{
				retString = getSPropToken(pc, eq);
			}
			else if (property.equals("TOTALWEIGHT") || property.equals("WT"))
			{
				retString = BigDecimalHelper.trimZeros(Float.toString(getTotalWeightToken(pc, eq)));
			}
			else if (property.equals("TYPE"))
			{
				retString = getTypeToken(eq, aTok);
			}
		}
		return retString;
	}

	/**
	 * Get the AC Mod Token
	 * @param pc
	 * @param eq
	 * @return AC Mod Token
	 */
	public static int getAcModToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getACMod(pc).intValue();
	}

	/**
	 * Get Alternative Critical Token
	 * @param eq
	 * @return Alternative Critical Token
	 */
	public static String getAltCritToken(Equipment eq)
	{
		return EqToken.getAltCritMultToken(eq);
	}

	/**
	 * Get alternative damage token
	 * @param pc
	 * @param eq
	 * @return alternative damage token
	 */
	public static String getAltDamageToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getAltDamage(pc);
	}

	/**
	 * Get Attacks token
	 * @param pc
	 * @param eq
	 * @return Attacks token
	 */
	public static double getAttacksToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.bonusTo(pc, "COMBAT", "ATTACKS", true);
	}

	/**
	 * Get Carried token
	 * @param eq
	 * @return Carried token
	 */
	public static float getCarriedToken(Equipment eq)
	{
		return eq.numberCarried().floatValue();
	}

	/**
	 * Get Contents Token
	 * @param eq
	 * @param aTok
	 * @return Contents Token
	 */
	public static String getContentsToken(Equipment eq, StringTokenizer aTok)
	{
		String retString = "";
		if (aTok.hasMoreTokens())
		{
			String aType = aTok.nextToken();
			String aSubTag = "NAME";

			if (aTok.hasMoreTokens())
			{
				aSubTag = aTok.nextToken();
			}

			retString = eq.getContainerByType(aType, aSubTag);
		}
		else
		{
			retString = eq.getContainerContentsString();
		}
		return retString;
	}

	/**
	 * Get Content Weight Token
	 * @param pc
	 * @param eq
	 * @return Content Weight Token
	 */
	public static float getContentWeightToken(PlayerCharacter pc, Equipment eq)
	{
		if (eq.getChildCount() == 0)
		{
			return 0;
		}
		return eq.getContainedWeight(pc).floatValue();
	}

	/**
	 * Get Cost token
	 * @param pc
	 * @param eq
	 * @return Cost token
	 */
	public static BigDecimal getCostToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getCost(pc);
	}

	/**
	 * Get Critical Multiplier Token
	 * @param eq
	 * @return Critical Multiplier Token
	 */
	public static String getCritMultToken(Equipment eq)
	{
		return EqToken.getCritMultToken(eq);
	}

	/**
	 * Get Damage Token
	 * @param pc
	 * @param eq
	 * @return Damage Token
	 */
	public static String getDamageToken(PlayerCharacter pc, Equipment eq)
	{
		String retString = eq.getDamage(pc);

		if ((pc != null) && (eq.isNatural()))
		{
			retString = Globals.adjustDamage(retString,
				pc.getDisplay().getRace().getSafe(FormulaKey.SIZE).resolve(pc, "").intValue(),
				pc.getDisplay().sizeInt());
		}

		return retString;
	}

	/**
	 * Get Equipped Token
	 * @param eq
	 * @return Equipped Token
	 */
	public static String getEquippedToken(Equipment eq)
	{
		if (eq.isEquipped())
		{
			return "Y";
		}
		return "N";
	}

	/**
	 * Get Item Weight Token
	 * @param pc
	 * @param eq
	 * @return Item Weight Token
	 */
	public static float getItemWeightToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getWeight(pc).floatValue();
	}

	/**
	 * Get Location Token
	 * @param eq
	 * @return Location Token
	 */
	public static String getLocationToken(Equipment eq)
	{
		return eq.getParentName();
	}

	/**
	 * Get Long Name Token
	 * @param eq
	 * @return Long Name Token
	 */
	public static String getLongNameToken(Equipment eq)
	{
		StringBuilder retString = new StringBuilder();
		int depth = eq.itemDepth();

		while (depth > 0)
		{
			retString.append(INDENT);
			--depth;
		}

		retString.append(eq.longName());
		return retString.toString();
	}

	/**
	 * Get Move Token
	 * @param eq
	 * @return Move Token
	 */
	public static String getMoveToken(Equipment eq)
	{
		return eq.moveString();
	}

	/**
	 * Get Name Token
	 * @param eq
	 * @param pc
	 * @return Name Token
	 */
	public static String getNameToken(Equipment eq, PlayerCharacter pc)
	{
		return OutputNameFormatting.parseOutputName(eq, pc);
	}

	/**
	 * Get Quantity Token
	 * @param eq
	 * @return Quantity Token
	 */
	public static double getQuantityToken(Equipment eq)
	{
		return eq.qty();
	}

	/**
	 * Get Size Token
	 * @param eq
	 * @return Size Token
	 */
	public static String getSizeToken(Equipment eq)
	{
		return eq.getSize();
	}

	/**
	 * Get Special Property Token
	 * @param pc
	 * @param eq
	 * @return Special Property Token
	 */
	public static String getSPropToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getSpecialProperties(pc);
	}

	/**
	 * Get Total Weight Token
	 * @param pc
	 * @param eq
	 * @return Total Weight Token
	 */
	public static float getTotalWeightToken(PlayerCharacter pc, Equipment eq)
	{
		return getContentWeightToken(pc, eq) + getItemWeightToken(pc, eq);
	}

	/**
	 * Get Type Token
	 * @param eq
	 * @param aTok
	 * @return Type Token
	 */
	public static String getTypeToken(Equipment eq, StringTokenizer aTok)
	{
		String retString = "";
		if (aTok.hasMoreTokens())
		{
			try
			{
				int x = Integer.parseInt(aTok.nextToken());
				retString = eq.typeIndex(x);
			}
			catch (NumberFormatException e)
			{
				// TODO - This exception needs to be handled
			}
		}
		else
		{
			retString = eq.getType();
		}
		return retString;
	}

	private Equipment getContainer(PlayerCharacter pc, int no)
	{
		for (Equipment eq : pc.getEquipmentListInOutputOrder())
		{
			if (eq.isContainer())
			{
				no--;
			}

			if (no < 0)
			{
				return eq;
			}
		}
		return null;
	}
}
