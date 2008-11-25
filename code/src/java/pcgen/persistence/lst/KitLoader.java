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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.kit.KitAlignment;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMKitLoader;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

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
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitRace>("*KITTOKEN",
		//			"RACE", CDOMKitRace.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSkill>(
		//			"*KITTOKEN", "SKILL", CDOMKitSkill.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitGear>("*KITTOKEN",
		//			"GEAR", CDOMKitGear.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSpells>(
		//			"*KITTOKEN", "SPELLS", CDOMKitSpells.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitStat>("*KITTOKEN",
		//			"STAT", CDOMKitStat.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitAbility>(
		//			"*KITTOKEN", "FEAT", CDOMKitAbility.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitAbility>(
		//			"*KITTOKEN", "ABILITY", CDOMKitAbility.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitName>("*KITTOKEN",
		//			"NAME", CDOMKitName.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitLevelAbility>(
		//			"*KITTOKEN", "LEVELABILITY", CDOMKitLevelAbility.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitClass>(
		//			"*KITTOKEN", "CLASS", CDOMKitClass.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitTemplate>(
		//			"*KITTOKEN", "TEMPLATE", CDOMKitTemplate.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitDeity>(
		//			"*KITTOKEN", "DEITY", CDOMKitDeity.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitKit>("*KITTOKEN",
		//			"KIT", CDOMKitKit.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitTable>(
		//			"*KITTOKEN", "TABLE", CDOMKitTable.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSelect>(
		//			"*KITTOKEN", "SELECT", CDOMKitSelect.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitGender>(
		//			"*KITTOKEN", "GENDER", CDOMKitGender.class));
		//		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitFunds>(
		//			"*KITTOKEN", "FUNDS", CDOMKitFunds.class));
	}

	@Override
	protected Kit getObjectKeyed(String aKey)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
			Kit.class, aKey);
	}

	@Override
	public Kit parseLine(LoadContext context, Kit target, String inputLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(KitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = inputLine.indexOf(':');
		String key = "";
		try
		{
			key = inputLine.substring(0, idxColon);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// TODO Handle Exception
		}
		KitLstToken token = (KitLstToken) tokenMap.get(key);

		if (inputLine.startsWith("STARTPACK:"))
		{
			target = new Kit();
			target.setSourceCampaign(source.getCampaign());
			target.setSourceURI(source.getURI());
			if (kitPrereq != null)
			{
				target.addPrerequisite(KitLoader.kitPrereq);
			}
			if (globalTokens != null)
			{
				for (String tag : globalTokens)
				{
					final String gt = tag.trim();
					final int colonLoc = gt.indexOf(':');
					if (colonLoc == -1)
					{
						Logging
							.errorPrint("Invalid Token - does not contain a colon: "
								+ gt);
						continue;
					}
					else if (colonLoc == 0)
					{
						Logging
							.errorPrint("Invalid Token - starts with a colon: "
								+ gt);
						continue;
					}

					String gkey = gt.substring(0, colonLoc);
					String value =
							(colonLoc == gt.length() - 1) ? null : gt
								.substring(colonLoc + 1);
					if (context.processToken(target, gkey, value))
					{
						context.commit();
					}
					else if (!PObjectLoader.parseTag(target, gt))
					{
						Logging.replayParsedMessages();
					}
					Logging.clearParseMessages();
				}
			}
		}
		if (kitLoader.parseSubLine(context, target, inputLine, source.getURI()))
		{
			Logging.clearParseMessages();
			context.commit();
		}
		else
		{
			if (token == null)
			{
				Logging.replayParsedMessages();
			}
			else
			{
				final String value = inputLine.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, target, value);
				if (!token.parse(target, value, source.getURI()))
				{
					Logging.replayParsedMessages();
				}
			}
			Logging.clearParseMessages();
		}
//System.err.println(target.getListFor(ListKey.KIT_TASKS));
		return target;
	}

	static List<String> globalTokens = null;

	static Prerequisite kitPrereq = null;

	public static void addGlobalToken(String string)
	{
		if (globalTokens == null)
		{
			globalTokens = new ArrayList<String>();
		}
		globalTokens.add(string);
	}

	public static void setKitPrerequisite(Prerequisite p)
	{
		kitPrereq = p;
	}

	public static void clearGlobalTokens()
	{
		globalTokens = null;
	}

	public static void clearKitPrerequisites()
	{
		kitPrereq = null;
	}

	@Override
	protected void loadLstFile(LoadContext context, CampaignSourceEntry cse)
	{
		clearGlobalTokens();
		clearKitPrerequisites();
		super.loadLstFile(context, cse);
	}
}
