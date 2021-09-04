/*
 * Copyright 2012 (C) James Dempsey
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
package plugin.primitive.pcclass;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * ClassToken is a Primitive that filters based on the Class (this will include both Class
 * and Subclass objects for a class).
 */
public class ClassToken implements PrimitiveToken<PCClass>, PrimitiveFilter<PCClass>
{
	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private String pcclass;
	private CDOMReference<PCClass> allClasses;

	@Override
	public boolean initialize(LoadContext context, Class<PCClass> cl, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		pcclass = value;
		allClasses = context.getReferenceContext().getCDOMAllReference(PCCLASS_CLASS);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "CLASS";
	}

	@Override
	public Class<PCClass> getReferenceClass()
	{
		return PCCLASS_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return getTokenName() + '=' + pcclass;
	}

	@Override
	public boolean allow(PlayerCharacter pc, PCClass cl)
	{
		return pcclass.equals(cl.get(StringKey.KEY_NAME));
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof ClassToken other)
		{
			return pcclass.equals(other.pcclass);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return pcclass == null ? -7 : pcclass.hashCode();
	}

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<PCClass, R> c)
	{
		return c.convert(allClasses, this);
	}
}
