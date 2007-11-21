/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;

public class FeatToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(PObject target, String value, int level)
	{
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		
		tok.nextToken(); // Throw away FEAT

		ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
		if (level > -9)
		{
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				String preLevelString = "PRELEVEL:MIN=" + level; //$NON-NLS-1$
				if (target instanceof PCClass)
				{
					// Classes handle this differently
					preLevelString =
							"PRECLASS:1," + target.getKeyName() + "=" + level; //$NON-NLS-1$ //$NON-NLS-2$
				}
				Prerequisite r = factory.parse(preLevelString);
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				return false;
			}
		}
		while (tok.hasMoreTokens())
		{
			String feat = tok.nextToken();
			if (feat.startsWith(".CLEAR."))
			{
				List<QualifiedObject<String>> ao =
						target.getRawAbilityObjects(AbilityCategory.FEAT,
							Ability.Nature.AUTOMATIC);
				for (QualifiedObject<String> qo : ao)
				{
					if (qo instanceof QualifiedObject.AutoQualifiedObject)
					{
						String name = feat.substring(7);
						if (name.equalsIgnoreCase(qo.getObject(null))
							&& preReqs.equals(qo.getPrereqs()))
						{
							target.removeAbility(AbilityCategory.FEAT,
								Ability.Nature.AUTOMATIC, qo);
						}
					}
				}
			}
			else
			{
				target.addAbility(AbilityCategory.FEAT,
					Ability.Nature.AUTOMATIC,
					new QualifiedObject.AutoQualifiedObject<String>(feat,
						preReqs));
			}
		}
		return true;
	}

}
