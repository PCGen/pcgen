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
package pcgen.cdom.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.content.Processor;

/**
 * ChangeArmorType is a Processor that alters Armor Types.
 * 
 * If the type to be modified matches the source type of the ChangeArmorType
 * object, then the result type is returned. Otherwise, the incoming type is not
 * modified.
 * 
 * NOTE: It is possible (albeit strange) to use ChangeArmorType as a
 * "RemoveArmorType", in that the result type can be null. Therefore, users
 * should expect that applyProcessor(String, Object) may return null.
 */
public class ChangeArmorType implements Processor<String>
{

	/**
	 * The source type for this ChangeArmorType. If the type to be modified
	 * matches the source type of the ChangeArmorType object, then this Processor
	 * will act on the incoming type.
	 */
	private final String source;

	/**
	 * The result type for this ChangeArmorType. If the Processor acts on the
	 * incoming type, it will return this type in place of the incoming type.
	 */
	private final String result;

	/**
	 * Constructs a new ChangeArmorType with the given source and result types.
	 * 
	 * @param sourceType
	 *            The source type for this ChangeArmorType, to be tested against
	 *            the types provided in applyProcessor
	 * @param resultType
	 *            The result type for this ChangeArmorType, to be returned from
	 *            applyProcessor if the Processor acts on the incoming type. May
	 *            be null to indicate this ChangeArmorType should remove the
	 *            source armor type
	 * @throws IllegalArgumentException
	 *             if the given source type is null
	 */
	public ChangeArmorType(String sourceType, String resultType)
	{
		Objects.requireNonNull(sourceType, "Source Type for ChangeArmorType cannot be null");
		Objects.requireNonNull(resultType, "Resulting Type for ChangeArmorType cannot be null");
		result = resultType;
		source = sourceType;
	}

	/**
	 * Applies this Processor to the given input armor type.
	 * 
	 * If the type to be modified matches the source type of the ChangeArmorType
	 * object, then the result type is returned. Otherwise, the incoming type is
	 * not modified (and the incoming type is returned).
	 * 
	 * Since ChangeArmorType is universal, the given context is ignored.
	 * 
	 * NOTE: It is possible (albeit strange) to use ChangeArmorType as a
	 * "RemoveArmorType", in that the result type can be null. Therefore, users
	 * should account for the possibility that this method may return null.
	 * 
	 * @param sourceType
	 *            The input armor type this Processor will act upon
	 * @param context
	 *            The context of this Processor, ignored by ChangeArmorType.
	 * @return The modified armor type, if the type to be modified matches the
	 *         source type of the ChangeArmorType object; otherwise the source
	 *         armor type
	 */
	@Override
	public String applyProcessor(String sourceType, Object context)
	{
		return source.equalsIgnoreCase(sourceType) ? result : sourceType;
	}

	/**
	 * The class of object this Processor acts upon (String).
	 * 
	 * @return The class of object this Processor acts upon (String.class)
	 */
	@Override
	public Class<String> getModifiedClass()
	{
		return String.class;
	}

	@Override
	public int hashCode()
	{
		return 31 * source.hashCode() + result.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof ChangeArmorType other))
		{
			return false;
		}
		if (result == null)
		{
			return other.result == null;
		}
		return result.equals(other.result) && source.equals(other.source);
	}

	/**
	 * Applies this Processor to the given list of input armor types.
	 * 
	 * If any type in the given list matches the source type of the
	 * ChangeArmorType object, then the result type is placed into the returned
	 * list instead of the type to be modified. Other incoming types are not
	 * modified (and the incoming type is included in the returned list).
	 * 
	 * Note: This method is reference-semantic. The ownership of the returned
	 * List is transferred to the calling Object; therefore, changes to the
	 * returned List will NOT impact the ChangeArmorType and will NOT impact the
	 * incoming list. The returned list is guaranteed to be distinct from the
	 * given list.
	 * 
	 * NOTE: As it is possible (albeit strange) to use ChangeArmorType as a
	 * "RemoveArmorType", there is no guarantee that the returned list is the
	 * same size as the given list. null values (removed armor types) will not
	 * be included in the returned list. If the incoming list has only one type,
	 * and that type is removed, this method will return an empty list. This
	 * method will not return null.
	 * 
	 * WARNING: This method is "strange" in that it causes the returned types to
	 * always be in upper case. This may be an unexpected side-effect to this
	 * method.
	 * 
	 * @param armorTypes
	 *            The list of input armor types this Processor will act upon
	 * @return The modified List of armor types.
	 * @throws NullPointerException
	 *             if the given List is null
	 */
	public List<String> applyProcessor(Collection<String> armorTypes)
	{
		List<String> returnList = new ArrayList<>();
		for (String type : armorTypes)
		{
			String mod = applyProcessor(type, null);
			if (mod != null)
			{
				returnList.add(mod.toUpperCase());
			}
		}
		return returnList;
	}

	/**
	 * Returns a representation of this ChangeArmorType, suitable for storing in
	 * an LST file.
	 * 
	 * @return A representation of this ChangeArmorType, suitable for storing in
	 *         an LST file.
	 */
	@Override
	public String getLSTformat()
	{
		return source + (result == null ? "" : "|" + result);
	}
}
