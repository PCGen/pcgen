/*
 * KitLoader.java
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
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitAbilities;
import pcgen.core.kit.KitAlignment;
import pcgen.core.kit.KitBio;
import pcgen.core.kit.KitClass;
import pcgen.core.kit.KitDeity;
import pcgen.core.kit.KitFunds;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitKit;
import pcgen.core.kit.KitLangBonus;
import pcgen.core.kit.KitLevelAbility;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitRace;
import pcgen.core.kit.KitSelect;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSpells;
import pcgen.core.kit.KitStat;
import pcgen.core.kit.KitTable;
import pcgen.core.kit.KitTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMKitLoader;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.util.Logging;

/**
 * 
 * ???
 * 
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitLoader extends LstObjectFileLoader<Kit>
{

	private final CDOMKitLoader kitLoader = new CDOMKitLoader();

	public KitLoader()
	{
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitAlignment>(
			"*KITTOKEN", "ALIGN", KitAlignment.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitRace>("*KITTOKEN",
			"RACE", KitRace.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitSkill>("*KITTOKEN",
			"SKILL", KitSkill.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitGear>("*KITTOKEN",
			"GEAR", KitGear.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitSpells>("*KITTOKEN",
			"SPELLS", KitSpells.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitStat>("*KITTOKEN",
			"STAT", KitStat.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitProf>("*KITTOKEN",
			"PROF", KitProf.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitAbilities>(
			"*KITTOKEN", "FEAT", KitAbilities.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitAbilities>(
			"*KITTOKEN", "ABILITY", KitAbilities.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitBio>("*KITTOKEN",
			"NAME", KitBio.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitLevelAbility>(
			"*KITTOKEN", "LEVELABILITY", KitLevelAbility.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitClass>("*KITTOKEN",
			"CLASS", KitClass.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitTemplate>("*KITTOKEN",
			"TEMPLATE", KitTemplate.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitDeity>("*KITTOKEN",
			"DEITY", KitDeity.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitKit>("*KITTOKEN",
			"KIT", KitKit.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitTable>("*KITTOKEN",
			"TABLE", KitTable.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitSelect>("*KITTOKEN",
			"SELECT", KitSelect.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitBio>("*KITTOKEN",
			"GENDER", KitBio.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitFunds>("*KITTOKEN",
				"FUNDS", KitFunds.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<KitLangBonus>(
				"*KITTOKEN", "LANGBONUS", KitLangBonus.class));
	}

	@Override
	protected Kit getObjectKeyed(LoadContext context, String aKey)
	{
		return context.ref.silentlyGetConstructedCDOMObject(
			Kit.class, aKey);
	}

	@Override
	public Kit parseLine(LoadContext context, Kit target, String inputLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		if (inputLine.startsWith("STARTPACK:"))
		{
			if (target != null)
			{
				completeObject(context, source, target);
			}
			StringTokenizer st = new StringTokenizer(inputLine, "\t");
			String firstToken = st.nextToken();
			int colonLoc = firstToken.indexOf(':');
			target =
					context.ref.constructCDOMObject(Kit.class, firstToken
						.substring(colonLoc + 1));
			target.setSourceCampaign(source.getCampaign());
			target.setSourceURI(source.getURI());
			context.addStatefulInformation(target);
			while (st.hasMoreTokens())
			{
				String token = st.nextToken().trim();
				int cLoc = token.indexOf(':');
				if (cLoc == -1)
				{
					Logging
						.errorPrint("Invalid Token - does not contain a colon: "
							+ token);
					continue;
				}
				else if (cLoc == 0)
				{
					Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
					continue;
				}

				String key = token.substring(0, cLoc);
				String value =
						(cLoc == token.length() - 1) ? null : token
							.substring(cLoc + 1);
				context.processToken(target, key, value);
			}
		}
		else if (kitLoader.parseSubLine(context, target, inputLine, source
			.getURI()))
		{
			Logging.clearParseMessages();
			context.commit();
		}
		else
		{
			Logging.replayParsedMessages();
			Logging.clearParseMessages();
		}
		return target;
	}
}
