/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Class deals with PRERACETYPE Token
 */
public class PreracetypeToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>, DeferredToken<PCClass>
{

	private static final String TOKEN_ROOT = "RACETYPE";

	@Override
	public String getTokenName()
	{
		return "PRERACETYPE";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		Prerequisite p = new Prerequisite();
		p.setKind("RACETYPE");
		p.setOperand("1");
		p.setKey(value);
		p.setOperator(PrerequisiteOperator.GTEQ);
		context.obj.put(pcc, p);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		Set<String> set = new TreeSet<String>();
		Changes<Prerequisite> changes = context.obj.getPrerequisiteChanges(obj);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		for (Prerequisite p : changes.getAdded())
		{
			String kind = p.getKind();
			if (kind == null
					|| kind.regionMatches(true, 0, TOKEN_ROOT, 0, Math.min(
							TOKEN_ROOT.length(), kind.length())))
			{
				set.add(p.getKey());
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

	public boolean process(LoadContext context, PCClass obj)
	{
		List<Prerequisite> prerequisiteList = obj.getPrerequisiteList();
		if (prerequisiteList != null)
		{
			for (Prerequisite p : prerequisiteList)
			{
				if ("RACETYPE".equalsIgnoreCase(p.getKind()))
				{
					if (!obj.isMonster())
					{
						Logging.errorPrint("PCClass " + obj.getKeyName()
								+ " is not a Monster, but used PRERACETYPE");
						return false;
					}
				}
			}
		}
		return true;
	}

	public Class<PCClass> getDeferredTokenClass()
	{
		return getTokenClass();
	}

}
