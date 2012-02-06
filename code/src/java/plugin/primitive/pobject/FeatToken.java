/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.pobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Converter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class FeatToken<T> implements PrimitiveToken<T>
{

	private CDOMSingleRef<Ability> ref;

	private Class<T> refClass;

	public boolean initialize(LoadContext context, Class<T> cl, String value,
			String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getCDOMReference(Ability.class, AbilityCategory.FEAT,
				value);
		refClass = cl;
		return true;
	}

	public String getTokenName()
	{
		return "FEAT";
	}

	public Class<? super T> getReferenceClass()
	{
		if (refClass == null)
		{
			return Object.class;
		}
		else
		{
			return refClass;
		}
	}

	public String getLSTformat(boolean useAny)
	{
		return getTokenName() + "=" + ref.getLSTformat(useAny);
	}

	public boolean allow(PlayerCharacter pc, T obj)
	{
		Ability a = ref.resolvesTo();
		ChooseInformation info = a.get(ObjectKey.CHOOSE_INFO);
		List<?> currentItems = getList(pc, a, info);
		return (currentItems != null) && currentItems.contains(obj);
	}

	private <R> List<R> getList(PlayerCharacter pc, Ability a,
			ChooseInformation<R> info)
	{
		// workaround for cloning issue
		List<R> availableList = new ArrayList<R>();
		List<Ability> theFeats = pc.getFeatNamedAnyCat(a);
		for (Ability ability : theFeats)
		{
			List<? extends R> list = info.getChoiceActor().getCurrentlySelected(ability,
					pc);
			availableList.addAll(list);
		}
		return availableList;
	}

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
		if (obj instanceof FeatToken)
		{
			FeatToken<?> other = (FeatToken<?>) obj;
			if (ref == null)
			{
				return (other.ref == null) && (refClass == null)
					&& (other.refClass == null);
			}
			return refClass.equals(other.refClass) && ref.equals(other.ref);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return ref == null ? -57 : ref.hashCode();
	}

	public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<T, R> c)
	{
		/*
		 * In theory the converter can be ignored here, since an equivalent
		 * would exist within the ChooseInformation below
		 */
		Ability a = ref.resolvesTo();
		ChooseInformation info = a.get(ObjectKey.CHOOSE_INFO);
		List<R> currentItems = getList(pc, a, info);
		if (currentItems == null)
		{
			return Collections.emptySet();
		}
		return new HashSet<R>(currentItems);
	}

}
