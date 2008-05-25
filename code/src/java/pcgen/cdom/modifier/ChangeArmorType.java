/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.modifier;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.Modifier;

public class ChangeArmorType extends ConcretePrereqObject implements
		Modifier<String>, LSTWriteable
{

	private final String source;
	private final String result;

	public ChangeArmorType(String sourceType, String resultType)
	{
		if (sourceType == null)
		{
			throw new IllegalArgumentException(
					"Source Type for ChangeArmorType cannot be null");
		}
		// if (resultType == null)
		// {
		// throw new IllegalArgumentException(
		// "Resulting Type for ChangeArmorType cannot be null");
		// }
		result = resultType;
		source = sourceType;
	}

	public String applyModifier(String obj)
	{
		return source.equals(obj) ? result : obj;
	}

	public Class<String> getModifiedClass()
	{
		return String.class;
	}

	public String getSourceType()
	{
		return source;
	}

	public String getResultType()
	{
		return result;
	}

	@Override
	public int hashCode()
	{
		return 31 * source.hashCode() + result.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof ChangeArmorType))
		{
			return false;
		}
		ChangeArmorType other = (ChangeArmorType) o;
		if (result == null)
		{
			return other.result == null;
		}
		return result.equals(other.result) && source.equals(other.source);
	}

	public String getLSTformat()
	{
		// TODO Don't like this = fix Generics in GraphChanges
		return "";
	}

	public List<String> applyModifier(List<String> calculatedTypeList)
	{
		List<String> returnList = new ArrayList<String>();
		for (String type : calculatedTypeList)
		{
			String mod = applyModifier(type);
			if (mod != null)
			{
				returnList.add(mod);
			}
		}
		return returnList;
	}
}
