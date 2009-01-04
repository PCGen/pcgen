/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public class ClassLoader extends AbstractTokenLoader
{
	public ClassLoader(LoadContext lc)
	{
		super(lc);
	}

	@Override
	public void process(StringBuilder result, int line, String lineString,
			CampaignSourceEntry cse) throws PersistenceLayerException
	{
		String[] tokens = lineString.split(FIELD_SEPARATOR);
		String firstToken = tokens[0];
		result.append(firstToken);

		Class<? extends CDOMObject> buildClass;
		Class<? extends CDOMObject> buildParent = null;
		if (firstToken.startsWith("SUBCLASS:"))
		{
			buildClass = SubClass.class;
		}
		else if (firstToken.startsWith("SUBCLASSLEVEL:"))
		{
			buildClass = PCClassLevel.class;
			buildParent = SubClass.class;
		}
		else if (firstToken.startsWith("SUBSTITUTIONCLASS:"))
		{
			buildClass = SubstitutionClass.class;
		}
		else if (firstToken.startsWith("SUBSTITUTIONLEVEL:"))
		{
			buildClass = PCClassLevel.class;
			buildParent = SubstitutionClass.class;
		}
		else if (firstToken.startsWith("CLASS:"))
		{
			buildClass = PCClass.class;
		}
		else
		{
			buildClass = PCClassLevel.class;
			buildParent = PCClass.class;
		}
		for (int tok = 1; tok < tokens.length; tok++)
		{
			String token = tokens[tok];
			if (token.length() == 0)
			{
				result.append(FIELD_SEPARATOR);
				continue;
			}

			CDOMObject obj = getContext().ref.constructCDOMObject(buildClass,
					line + "Test" + tok);
			CDOMObject parent = null;
			if (obj instanceof PCClassLevel)
			{
				obj.put(IntegerKey.LEVEL, 1);
				parent = getContext().ref.constructCDOMObject(buildParent, line
						+ "Test" + tok);
				obj.put(ObjectKey.PARENT, parent);
			}
			processToken(result, obj, parent, token);
		}
	}

	@Override
	protected List<CampaignSourceEntry> getFiles(Campaign c)
	{
		return c.getSafeListFor(ListKey.FILE_CLASS);
	}

}
