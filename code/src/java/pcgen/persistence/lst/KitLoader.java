/*
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
 *
 */
package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
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
 */
public final class KitLoader extends LstObjectFileLoader<Kit>
{

	private final CDOMKitLoader kitLoader = new CDOMKitLoader();

	public KitLoader()
	{
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("ALIGN", KitAlignment.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("RACE", KitRace.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("SKILL", KitSkill.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("GEAR", KitGear.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("SPELLS", KitSpells.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("STAT", KitStat.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("PROF", KitProf.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("FEAT", KitAbilities.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("ABILITY", KitAbilities.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("NAME", KitBio.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("LEVELABILITY", KitLevelAbility.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("CLASS", KitClass.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("TEMPLATE", KitTemplate.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("DEITY", KitDeity.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("KIT", KitKit.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("TABLE", KitTable.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("SELECT", KitSelect.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("GENDER", KitBio.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("FUNDS", KitFunds.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("LANGBONUS", KitLangBonus.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<>("AGE", KitBio.class));
	}

	@Override
	protected Kit getObjectKeyed(LoadContext context, String aKey)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(Kit.class, aKey);
	}

	@Override
	public Kit parseLine(LoadContext context, Kit target, String inputLine, SourceEntry source)
		throws PersistenceLayerException
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
			target = context.getReferenceContext().constructCDOMObject(Kit.class,
				firstToken.substring(colonLoc + 1));
			target.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			target.setSourceURI(source.getURI());
			context.addStatefulInformation(target);
			while (st.hasMoreTokens())
			{
				String token = st.nextToken().trim();
				int cLoc = token.indexOf(':');
				if (cLoc == -1)
				{
					Logging.errorPrint("Invalid Token - " + "does not contain a colon: '" + token + "' on line :"
						+ inputLine + " in " + source.getURI());
					continue;
				}
				else if (cLoc == 0)
				{
					Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' on line :" + inputLine
						+ " in " + source.getURI());
					continue;
				}

				String key = token.substring(0, cLoc);
				String value = (cLoc == token.length() - 1) ? null : token.substring(cLoc + 1);
				if (context.processToken(target, key, value))
				{
					context.commit();
				}
				else
				{
					context.rollback();
					Logging.replayParsedMessages();
				}
			}
		}
		else if (inputLine.startsWith("REGION:"))
		{
			String value = inputLine.substring(7);
			context.clearStatefulInformation();
			if (!value.isEmpty())
			{
				StringTokenizer st = new StringTokenizer(value, "\t");

				String region = st.nextToken();
				if (!region.equalsIgnoreCase(Constants.LST_NONE))
				{
					// Add a real prereq for the REGION: tag
					if (context.addStatefulToken("PREREGION:" + region))
					{
						context.commit();
					}
					else
					{
						context.rollback();
						Logging.errorPrint("Invalid Stateful Token" + " from Region NONE: PREREGION:'" + region
							+ "' on line :" + inputLine + " in " + source.getURI());
						Logging.replayParsedMessages();
					}
					Logging.clearParseMessages();
				}

				while (st.hasMoreTokens())
				{
					String gt = st.nextToken();
					if (!context.addStatefulToken(gt))
					{
						Logging.errorPrint(
							"Invalid Stateful Token: '" + gt + "' on line :" + inputLine + " in " + source.getURI());
					}
				}
			}
		}
		else
		{
			context.rollback();
			if (kitLoader.parseSubLine(context, target, inputLine, source.getURI()))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
				Logging.clearParseMessages();
			}
		}
		return target;
	}
}
