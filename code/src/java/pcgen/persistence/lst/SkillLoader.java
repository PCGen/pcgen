/*
 * SkillLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class SkillLoader extends LstObjectFileLoader<Skill>
{
	/** Creates a new instance of SkillLoader */
	public SkillLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Skill parseLine(Skill aSkill, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		Skill skill = aSkill;

		if (skill == null)
		{
			skill = new Skill();
			skill.setSourceCampaign(source.getCampaign());
			skill.setSourceURI(source.getURI());
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		// first column is the name; after that are LST tags
		skill.setName(colToken.nextToken());

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(SkillLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			SkillLstToken token = (SkillLstToken) tokenMap.get(key);

			if ("REQ".equals(colString))
			{
				Logging.errorPrint("You are using a deprecated tag "
						+ "(REQ) in Skills " + skill.getDisplayName() + ':'
						+ source.getURI() + ':' + colString);
				Logging.errorPrint("  Use USEUNTRAINED instead");
				skill.setRequired(true);
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, skill, value);
				if (!token.parse(skill, value))
				{
					Logging.errorPrint("Error parsing skill "
						+ skill.getDisplayName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(skill, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal skill info '" + lstLine + "' in "
					+ source.toString());
			}
		}

		completeObject(source, skill);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Skill getObjectKeyed(String aKey)
	{
		return Globals.getSkillKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final Skill objToForget)
	{
		Globals.getSkillList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		// TODO - Create Globals.addSkill(pObj);
		Globals.getSkillList().add((Skill) pObj);
	}
}
