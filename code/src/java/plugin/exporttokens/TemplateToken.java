/*
 * TemplateToken.java
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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SpecialAbility;
import pcgen.core.utils.CoreUtility;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deals with returning the values for the TEMPALTE Token
 * and it's Sub Tokens
 *
 * TEMPLATE
 * TEMPLATE.x.NAME
 * TEMPLATE.x.OUTPUTNAME
 * TEMPLATE.x.SA
 * TEMPLATE.x.FEAT
 * TEMPLATE.x.SR
 * TEMPLATE.x.CR
 * TEMPLATE.x.DR
 * TEMPLATE.x.xxxMOD
 */
public class TemplateToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "TEMPLATE";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";
		PCTemplate template;

		List<PCTemplate> tl = pc.getOutputVisibleTemplateList();

		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		int indexOfTemplate;
		indexOfTemplate = Integer.parseInt(aTok.nextToken());

		String aLabel = (aTok.hasMoreTokens()) ? aTok.nextToken() : "NAME";

		if ((indexOfTemplate > -1) && (indexOfTemplate < tl.size()))
		{
			template = tl.get(indexOfTemplate);

			if ("NAME".equals(aLabel))
			{
				retString = getOutputNameToken(template);
			}
			else if ("OUTPUTNAME".equals(aLabel))
			{
				retString = getOutputNameToken(template);
			}
			else if ("SA".equals(aLabel))
			{
				retString = getSAToken(template, pc);
			}
			else if ("FEAT".equals(aLabel))
			{
				retString = getFeatToken(template, pc);
			}
			else if ("SR".equals(aLabel))
			{
				retString = Integer.toString(getSRToken(template, pc));
			}
			else if ("CR".equals(aLabel))
			{
				retString = Integer.toString(getCRToken(template, pc));
			}
			else if ("DR".equals(aLabel))
			{
				retString = pc.calcDR();
				//				retString = getDRToken(template, pc);
			}
			else
			{
				retString = getModToken(pc, template, aLabel);
			}
		}

		return retString;
	}

	/**
	 * Get value of CR Sub Token
	 * @param template
	 * @param pc
	 * @return value of CR Sub Token
	 */
	public static int getCRToken(PCTemplate template, PlayerCharacter pc)
	{
		return template.getCR(pc.getTotalLevels(), pc.totalHitDice());
	}

	/**
	 * Get value of DR sub token
	 * @param template
	 * @param pc
	 * @return value of DR sub token
	 */
	//	public static String getDRToken(PCTemplate template, PlayerCharacter pc)
	//	{
	//		return template.getDR(pc.getTotalLevels(), pc.totalHitDice());
	//	}
	/**
	 * Get value of FEAT sub token
	 * @param template
	 * @param pc
	 * @return value of FEAT sub token
	 */
	public static String getFeatToken(PCTemplate template, PlayerCharacter pc)
	{
		List<String> fList =
				template.feats(pc.getTotalLevels(), pc.totalHitDice(), pc,
					false);
		return CoreUtility.join(fList, ", ");
	}

	/**
	 * Get value of MOD sub token
	 * @param pc
	 * @param template
	 * @param aLabel
	 * @return value of MOD sub token
	 */
	public static String getModToken(PlayerCharacter pc, PCTemplate template,
		String aLabel)
	{
		StringBuffer retString = new StringBuffer();

		for (int iMod = 0; iMod < SettingsHandler.getGame().s_ATTRIBSHORT.length; ++iMod)
		{
			String modName =
					SettingsHandler.getGame().s_ATTRIBSHORT[iMod] + "MOD";

			if (aLabel.equals(modName))
			{
				if (template.isNonAbility(iMod))
				{
					retString.append("*");
				}
				else
				{
					retString.append(template.getStatMod(iMod, pc));
				}

				break;
			}
		}

		return retString.toString();
	}

	/**
	 * Get value of NAME sub token
	 * @param template
	 * @return value of NAME sub token
	 */
	public static String getNameToken(PCTemplate template)
	{
		return template.toString();
	}

	/**
	 * Get value of OUTPUTNAME sub token
	 * @param template
	 * @return value of OUTPUTNAME sub token
	 */
	public static String getOutputNameToken(PCTemplate template)
	{
		return template.getOutputName();
	}

	/**
	 * Get value of SA sub token
	 * @param template
	 * @param pc
	 * @return value of SA sub token
	 */
	public static String getSAToken(PCTemplate template, PlayerCharacter pc)
	{
		List<SpecialAbility> saList = new ArrayList<SpecialAbility>();
		template.addSpecialAbilitiesToList(saList, pc);
		template.addSABToList(saList, pc);
		return CoreUtility.join(saList, ", ");
	}

	/**
	 * Get value of SR Sub token
	 * @param template
	 * @param pc
	 * @return value of SR Sub token
	 */
	public static int getSRToken(PCTemplate template, PlayerCharacter pc)
	{
		return template.getSR(pc.getTotalLevels(), pc.totalHitDice(), pc);
	}
}
