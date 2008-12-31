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
package pcgen.cdom.helper;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Equipment;

public abstract class AbstractProfProvider<T extends CDOMObject> extends
		ConcretePrereqObject implements ProfProvider<T>
{

	private final List<CDOMReference<T>> direct;

	private final List<CDOMReference<Equipment>> byEquipType;

	public AbstractProfProvider(List<CDOMReference<T>> profs,
			List<CDOMReference<Equipment>> equipTypes)
	{
		direct = profs;
		byEquipType = equipTypes;
	}

	public abstract boolean providesProficiencyFor(Equipment eq);

	public boolean providesProficiency(T sp)
	{
		for (CDOMReference<T> ref : direct)
		{
			if (ref.contains(sp))
			{
				return true;
			}
		}
		return false;
	}

	public boolean providesEquipmentType(String typeString)
	{
		if (typeString == null || typeString.length() == 0)
		{
			return false;
		}
		Set<String> types = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		for (String s : typeString.split("\\."))
		{
			types.add(s);
		}
		REF: for (CDOMReference<Equipment> ref : byEquipType)
		{
			StringTokenizer tok = new StringTokenizer(ref.getLSTformat()
					.substring(5), ".");
			while (tok.hasMoreTokens())
			{
				if (!types.contains(tok.nextToken()))
				{
					continue REF;
				}
			}
			return true;
		}
		return false;
	}

	protected abstract String getSubType();

	public String getLstFormat()
	{
		StringBuilder sb = new StringBuilder();
		boolean typeEmpty = byEquipType.isEmpty();
		if (!direct.isEmpty())
		{
			sb.append(ReferenceUtilities.joinLstFormat(direct, Constants.PIPE));
			if (!typeEmpty)
			{
				sb.append(Constants.PIPE);
			}
		}
		if (!typeEmpty)
		{
			boolean needPipe = false;
			String subType = getSubType();
			String dot = Constants.DOT;
			for (CDOMReference<Equipment> ref : byEquipType)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				needPipe = true;
				String lstFormat = ref.getLSTformat();
				if (lstFormat.startsWith("TYPE="))
				{
					sb.append(subType).append("TYPE=");
					StringTokenizer st = new StringTokenizer(lstFormat
							.substring(5), dot);
					boolean needDot = false;
					while (st.hasMoreTokens())
					{
						String tok = st.nextToken();
						if (!tok.equals(subType))
						{
							if (needDot)
							{
								sb.append(dot);
							}
							needDot = true;
							sb.append(tok);
						}
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AbstractProfProvider)
		{
			AbstractProfProvider<T> other = (AbstractProfProvider<T>) obj;
			if (!other.getSubType().equals(getSubType()))
			{
				return false;
			}
			if (direct == null)
			{
				if (other.direct != null)
				{
					return false;
				}
			}
			else
			{
				if (!direct.equals(other.direct))
				{
					return false;
				}
			}
			if (byEquipType == null)
			{
				if (other.byEquipType != null)
				{
					return false;
				}
			}
			else
			{
				if (!byEquipType.equals(other.byEquipType))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (direct == null ? 0 : direct.hashCode() * 29)
				+ (byEquipType == null ? 0 : byEquipType.hashCode());
	}
	
	
}
