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
 * $Id: SkillLoader.java,v 1.44 2006/02/14 21:00:18 soulcatcher Exp $
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.44 $
 */
public final class SkillLoader extends LstObjectFileLoader
{
	/** Creates a new instance of SkillLoader */
	public SkillLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Skill skill = (Skill) target;

		if (skill == null)
		{
			skill = new Skill();
			skill.setSourceCampaign(source.getCampaign());
			skill.setSourceFile(source.getFile());
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		// first column is the name; after that are LST tags
		skill.setName(colToken.nextToken());

		Map tokenMap = TokenStore.inst().getTokenMap(SkillLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e) {
				// TODO Handle Exception
			}
			SkillLstToken token = (SkillLstToken) tokenMap.get(key);

			if ("REQ".equals(colString))
			{
				skill.setRequired(true);
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, skill, value);
				if (!token.parse(skill, value))
				{
					Logging.errorPrint("Error parsing skill " + skill.getName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(skill, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal skill info '" + lstLine + "' in " + source.toString());
			}
		}

		finishObject(skill);

		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getSkillNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (includeObject(target))
		{
			Skill skill = (Skill) target;
			final Skill aSkill = Globals.getSkillKeyed(skill.getKeyName());

			if (aSkill == null)
			{
				Globals.getSkillList().add(skill);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		Globals.getSkillList().remove(objToForget);
	}
}
