/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;

public class GenderFacet
{

	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);

	private final Class<?> thisClass = getClass();

	public void setGender(CharID id, Gender obj)
	{
		FacetCache.set(id, thisClass, obj);
	}

	public void removeGender(CharID id)
	{
		FacetCache.remove(id, thisClass);
	}

	public Gender getGender(CharID id)
	{
		Gender g = findTemplateGender(id);
		return (g == null) ? (Gender) FacetCache.get(id, thisClass) : g;
	}

	public boolean matchesGender(CharID id, Gender obj)
	{
		Gender current = getGender(id);
		return (obj == null && current == null)
				|| (obj != null && obj.equals(current));
	}

	public boolean canSetGender(CharID id)
	{
		return findTemplateGender(id) == null;
	}

	private Gender findTemplateGender(CharID id)
	{
		Gender g = null;

		for (PCTemplate template : templateFacet.getSet(id))
		{
			Gender lock = template.get(ObjectKey.GENDER_LOCK);
			if (lock != null)
			{
				g = lock;
			}
		}

		return g;
	}

}
