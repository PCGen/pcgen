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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.PCTemplate;

public class RegionFacet
{

	/*
	 * TODO A LOT of this should be type-safe to Region and SubRegion objects,
	 * but that is currently gated by how Template returns objects.
	 */
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);

	private final Class<?> thisClass = getClass();

	private RegionCacheInfo getConstructingInfo(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new RegionCacheInfo();
			FacetCache.set(id, thisClass, rci);
		}
		return rci;
	}

	private RegionCacheInfo getInfo(CharID id)
	{
		return (RegionCacheInfo) FacetCache.get(id, thisClass);
	}

	public void setRegion(CharID id, Region region)
	{
		getConstructingInfo(id).region = region;
	}

	public void setSubRegion(CharID id, SubRegion subregion)
	{
		getConstructingInfo(id).subregion = subregion;
	}

	public String getCharacterRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		if (rci != null && rci.region != null)
		{
			return rci.region.toString();
		}
		return Constants.s_NONE;
	}

	public String getRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		// character's region trumps any from templates
		if (rci != null && rci.region != null)
		{
			return rci.region.toString();
		}

		String region = Constants.s_NONE;

		for (PCTemplate template : templateFacet.getSet(id))
		{
			String tempRegion = template.getRegion();

			if (!tempRegion.equals(Constants.s_NONE))
			{
				region = tempRegion;
			}
		}

		return region;
	}

	public boolean matchesRegion(CharID id, Region r)
	{
		String current = getRegion(id);
		return (r == null && current == null)
				|| (r != null && r.toString().equalsIgnoreCase(current));
	}

	public String getCharacterSubRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		// character's subregion trumps any from templates
		if (rci != null && rci.subregion != null)
		{
			return rci.subregion.toString();
		}
		return Constants.s_NONE;
	}

	public String getSubRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		// character's subregion trumps any from templates
		if (rci != null && rci.subregion != null)
		{
			return rci.subregion.toString();
		}

		String s = Constants.s_NONE;

		for (PCTemplate template : templateFacet.getSet(id))
		{
			final String tempSubRegion = template.getSubRegion();

			if (!tempSubRegion.equals(Constants.s_NONE))
			{
				s = tempSubRegion;
			}
		}

		return s;
	}

	public String getFullRegion(CharID id)
	{
		final String sub = getSubRegion(id);
		final StringBuffer tempRegName = new StringBuffer()
				.append(getRegion(id));

		if (!sub.equals(Constants.s_NONE))
		{
			tempRegName.append(" (").append(sub).append(')');
		}

		return tempRegName.toString();
	}

	private class RegionCacheInfo
	{
		public Region region;

		public SubRegion subregion;
	}

}
