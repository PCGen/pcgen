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

import java.util.Collections;
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

/**
 * An AbstractProfProvider is an object that contains the ability to contain
 * Proficiencies, either by TYPE of Equipment or direct references. Explicit
 * Storage of TYPE vs. primitive is necessary due to the ability of the TYPE
 * being a resolved against Equipment.
 * 
 * @param <T>
 *            The type of Proficiency (CDOMObject) that this
 *            AbstractProfProvider provides
 */
public abstract class AbstractProfProvider<T extends CDOMObject> extends ConcretePrereqObject implements ProfProvider<T>
{

	/**
	 * Contains the set of primitive proficiencies objects that this
	 * AbstractProfProvider grants
	 */
	private final Set<CDOMReference<T>> direct;

	/**
	 * Contains the set of TYPEs of Equipment objects for which this
	 * AbstractProfProvider grants proficiency
	 */
	private final Set<CDOMReference<Equipment>> byEquipType;

	/**
	 * Constructs a new AbstractProfProvider with the given List of proficiency
	 * references and Equipment TYPE references.
	 * 
	 * No reference is maintained to the internal structure of the given Lists,
	 * so modifications to this AbstractProfProvider are not reflected in the
	 * given Lists (and vice versa).
	 * 
	 * @param profs
	 *            The List of proficiency references indicating the primitive
	 *            proficiency objects this AbstractProfProvider will contain.
	 * @param equipTypes
	 *            The List of Equipment references indicating the TYPEs of
	 *            Equipment objects this AbstractProfProvider will contain.
	 */
	public AbstractProfProvider(List<CDOMReference<T>> profs, List<CDOMReference<Equipment>> equipTypes)
	{
		direct = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
		direct.addAll(profs);
		byEquipType = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
		byEquipType.addAll(equipTypes);
	}

	/**
	 * Returns true if this AbstractProfProvider provides proficiency for the
	 * given Equipment; false otherwise.
	 * 
	 * @param equipment
	 *            The Equipment to be tested to see if this AbstractProfProvider
	 *            provides proficiency for the Equipment
	 * @return true if this AbstractProfProvider provides proficiency for the
	 *         given Equipment; false otherwise.
	 */
	@Override
	public abstract boolean providesProficiencyFor(Equipment equipment);

	/**
	 * Returns true if this AbstractProfProvider provides the given proficiency.
	 * This only tests against the direct proficiency list provided during
	 * construction of the AbstractProfProvider.
	 * 
	 * @param proficiency
	 *            The proficiency to be tested to see if this
	 *            AbstractProfProvider provides the given proficiency
	 * @return true if this AbstractProfProvider provides the given proficiency;
	 *         false otherwise.
	 */
	@Override
	public boolean providesProficiency(T proficiency)
	{
		for (CDOMReference<T> ref : direct)
		{
			if (ref.contains(proficiency))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if this AbstractProfProvider provides proficiency with the
	 * given Equipment TYPE. This only tests against the Equipment TYPE
	 * reference list provided during construction of the AbstractProfProvider.
	 * 
	 * @param typeString
	 *            The TYPE of Equipment to be tested to see if this
	 *            AbstractProfProvider provides proficiency with the given
	 *            Equipment TYPE
	 * @return true if this AbstractProfProvider provides proficiency with the
	 *         given Equipment TYPE.
	 */
	@Override
	@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
	public boolean providesEquipmentType(String typeString)
	{
		if (typeString == null || typeString.isEmpty())
		{
			return false;
		}
		Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		Collections.addAll(types, typeString.split("\\."));
		REF: for (CDOMReference<Equipment> ref : byEquipType)
		{
			StringTokenizer tok = new StringTokenizer(ref.getLSTformat(false).substring(5), ".");
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

	/**
	 * Returns a String indicating the type of proficiency granted by this
	 * AbstractProfProvider.
	 * 
	 * @return A String indicating the type of proficiency granted by this
	 *         AbstractProfProvider
	 */
	protected abstract String getSubType();

	/**
	 * Returns the LST format for this AbstractProfProvider. Provided primarily
	 * to allow the Token/Loader system to properly unparse the
	 * AbstractProfProvider.
	 * 
	 * @return The LST format of this AbstractProfProvider
	 */
	@Override
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
				String lstFormat = ref.getLSTformat(false);
				if (lstFormat.startsWith("TYPE="))
				{
					sb.append(subType).append("TYPE=");
					StringTokenizer st = new StringTokenizer(lstFormat.substring(5), dot);
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
		if (obj instanceof AbstractProfProvider<?> other)
		{
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
			return this.equalsPrereqObject(other);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (direct == null ? 0 : direct.hashCode() * 29) + (byEquipType == null ? 0 : byEquipType.hashCode());
	}

}
