/*
 * Copyright 2012 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.weaponprof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.Converter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * TYPE needs special care for WeaponProf due to CHANGEPROF
 */
public class TypeToken implements PrimitiveToken<WeaponProf>
{
	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;
	private CDOMGroupRef<WeaponProf> typeRef;

	@Override
	public String getTokenName()
	{
		return "TYPE";
	}

	@Override
	public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<WeaponProf, R> c)
	{
		List<WeaponProf> profs = pc.getWeaponProfsInTarget(typeRef);
		List<R> returnList = new ArrayList<>(profs.size() + 10);
		for (WeaponProf wp : profs)
		{
			returnList.addAll(c.convert(CDOMDirectSingleRef.getRef(wp)));
		}
		return returnList;
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public Class<? super WeaponProf> getReferenceClass()
	{
		return WEAPONPROF_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return typeRef.getLSTformat(useAny);
	}

	@Override
	public boolean initialize(LoadContext context, Class<WeaponProf> cl, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		typeRef = context.getReferenceContext().getCDOMTypeReference(WEAPONPROF_CLASS, value);
		return typeRef != null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof TypeToken other)
		{
			return typeRef.equals(other.typeRef);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return typeRef == null ? -11 : typeRef.hashCode();
	}

}
