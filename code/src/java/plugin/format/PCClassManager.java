/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.format;

import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.types.FormatManager;

/**
 * A PCClassManager is a FormatManager that provides services for PCClass
 * objects
 */
public class PCClassManager implements FormatManager<PCClass>
{
	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	/**
	 * @see pcgen.rules.types.FormatManager#convert(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public PCClass convert(LoadContext context, String classKey)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(
			PCCLASS_CLASS, classKey);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertIndirect(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public Indirect<PCClass> convertIndirect(LoadContext context,
		String classKey)
	{
		return context.getReferenceContext().getCDOMReference(PCCLASS_CLASS,
			classKey);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertObjectContainer(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public ObjectContainer<PCClass> convertObjectContainer(LoadContext context,
		String s)
	{
		return TokenUtilities.getTypeOrPrimitive(context.getReferenceContext()
			.getManufacturer(PCClass.class), s);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#unconvert(java.lang.Object)
	 */
	@Override
	public String unconvert(PCClass pcc)
	{
		return pcc.getKeyName();
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getType()
	 */
	@Override
	public Class<PCClass> getType()
	{
		return PCCLASS_CLASS;
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getIdentifierType()
	 */
	@Override
	public String getIdentifierType()
	{
		return "CLASS";
	}

	@Override
	public int hashCode()
	{
		return 234278;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof PCClassManager;
	}
}
