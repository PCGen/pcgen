/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.pretokens.parser;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

public class PreSkillSitParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{
	@Override
	public String[] kindsHandled()
	{
		return new String[]{"SKILLSIT"};
	}

	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);

		extractSkill(prereq);

		return prereq;
	}

	private static void extractSkill(Prerequisite prereq) throws PersistenceLayerException
	{
		String skill = "";
		if (prereq.getPrerequisiteCount() == 0)
		{
			Logging.errorPrint("PRESKILLSIT Requires a skill and situation=value");
			return;
		}

		// Copy to a temporary list as we will be adjusting the main one.
		List<Prerequisite> prereqList = new ArrayList<>(prereq.getPrerequisites());
		for (Prerequisite p : prereqList)
		{
			if (p.getKind() == null) // PREMULT
			{
				extractSkill(p);
			}
			else
			{
				String preKey = p.getKey();
				if (preKey.toUpperCase().startsWith("SKILL="))
				{
					String skillName = preKey.substring(6);
					if (!skill.isEmpty())
					{
						throw new PersistenceLayerException("PRESKILLSIT must only have one skill");
					}
					else if (p != prereqList.get(0))
					{
						throw new PersistenceLayerException("SKILL= must be first in PRESKILLSIT");
					}

					if (skillName.trim().equalsIgnoreCase("ANY"))
					{
						Logging.errorPrint("ANY not supported in PRESKILLSIT");
					}
					else
					{
						skill = skillName;
					}
					prereq.removePrerequisite(p);
				}
			}
		}
		/*
		 * TODO There is a special case here where
		 * prereq.getPrerequisiteList().size() == 1 That can be consolidated
		 * into one prereq ... question is how (and keep the operator, etc.
		 * correct)
		 */
		if (!skill.isEmpty())
		{
			for (Prerequisite p : prereq.getPrerequisites())
			{
				p.setCategoryName(skill);
			}
		}
		else
		{
			String preKey;
			if (prereq.getPrerequisiteCount() == 0)
			{
				preKey = prereq.getKey();
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				for (Prerequisite p : prereq.getPrerequisites())
				{
					sb.append(p.getKey()).append(',');
				}
				sb.setLength(sb.length() - 1);
				preKey = sb.toString();
			}
			Logging.errorPrint("PRESKILLSIT: found without SKILL=: " + preKey);
		}
	}

	@Override
	protected boolean requiresValue()
	{
		return true;
	}

	@Override
	protected boolean isNoWarnElement(String thisElement)
	{
		return thisElement.startsWith("SKILL=");
	}

}
