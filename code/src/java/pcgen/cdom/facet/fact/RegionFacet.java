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
package pcgen.cdom.facet.fact;

import java.util.Objects;
import java.util.Optional;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.facet.base.AbstractDataFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

/**
 * RegionFacet is a Facet that tracks the Region and SubRegion of a Player
 * Character. The Region and SubRegion can be set explicitly or inferred from
 * the PCTemplate objects possessed by the PlayerCharacter.
 * 
 */
public class RegionFacet extends AbstractDataFacet<CharID, String>
		implements DataFacetChangeListener<CharID, PCTemplate>
{

	private TemplateFacet templateFacet;

	/**
	 * Returns the type-safe RegionCacheInfo for this RegionFacet and the given
	 * CharID. Will return a new, empty RegionCacheInfo if no Region information
	 * has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The RegionCacheInfo object is
	 * owned by RegionFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than RegionFacet.
	 * 
	 * @param id
	 *            The CharID for which the RegionCacheInfo should be returned
	 * @return The RegionCacheInfo for the Player Character represented by the
	 *         given CharID.
	 */
	private RegionCacheInfo getConstructingInfo(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new RegionCacheInfo();
			setCache(id, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe RegionCacheInfo for this RegionFacet and the given
	 * CharID. May return null if no Region information has been set for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The RegionCacheInfo object is
	 * owned by RegionFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than RegionFacet.
	 * 
	 * @param id
	 *            The CharID for which the RegionCacheInfo should be returned
	 * @return The RegionCacheInfo for the Player Character represented by the
	 *         given CharID; null if no Region information has been set for the
	 *         Player Character.
	 */
	private RegionCacheInfo getInfo(CharID id)
	{
		return (RegionCacheInfo) getCache(id);
	}

	/**
	 * Sets the character Region for the Player Character represented by the
	 * given CharID to the given Region. This set Region will override any
	 * Region provided by a PCTemplate.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Region will be set
	 * @param region
	 *            The Region for the Player Character represented by the given
	 *            CharID
	 */
	public void setRegion(CharID id, Region region)
	{
		getConstructingInfo(id).region = region;
		updateRegion(id);
	}

	/**
	 * Sets the character SubRegion for the Player Character represented by the
	 * given CharID to the given SubRegion. This set SubRegion will override any
	 * SubRegion provided by a PCTemplate.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            SubRegion will be set
	 * @param subregion
	 *            The SubRegion for the Player Character represented by the
	 *            given CharID
	 */
	public void setSubRegion(CharID id, String subregion)
	{
		getConstructingInfo(id).subregion = subregion;
	}

	/**
	 * Returns a String representation of the character Region for the Player
	 * Character represented by the given CharID. Returns "NONE" if no character
	 * Region is set for the Player Character
	 * 
	 * **NOTE** Unless you are analyzing (or storing) raw values for a Player
	 * Character, it is unlikely that you want this method. It is more likely
	 * that you should be using getRegion(CharID id)
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            character Region should be returned.
	 * @return A String representation of the character Region for the Player
	 *         Character represented by the given CharID; "NONE" if no character
	 *         Region is set for the Player Character
	 */
	public Optional<Region> getCharacterRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		if (rci != null && rci.region != null)
		{
			return Optional.of(rci.region);
		}
		return Optional.empty();
	}

	/**
	 * Returns an Optional Region for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the Region should
	 *            be returned.
	 * @return An Optional Region for the Player Character represented by the given CharID
	 */
	public Optional<Region> getRegion(CharID id)
	{
		Optional<Region> charRegion = getCharacterRegion(id);
		return charRegion.isPresent() ? charRegion : getTemplateRegion(id);
	}

	/**
	 * Returns a String representation of the Region for the Player Character
	 * represented by the given CharID. Returns "NONE" if no Region is set for
	 * the Player Character.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Region should be returned.
	 * @return A String representation of the Region for the Player Character
	 *         represented by the given CharID; "NONE" if no Region is set for
	 *         the Player Character
	 */
	public String getRegionString(CharID id)
	{
		Optional<Region> charRegion = getCharacterRegion(id);
		return charRegion.orElse(getTemplateRegion(id).orElse(Region.NONE)).toString();
	}

	private Optional<Region> getTemplateRegion(CharID id)
	{
		return templateFacet.getSet(id)
				.stream()
				.map(template -> Optional.ofNullable(template.get(ObjectKey.REGION)))
				.filter(Optional::isPresent)
				.reduce(Optional.empty(), (current, next) -> next);
	}

	/**
	 * Returns true if the Region of the Player Character represented by the
	 * given CharID matches the given Region. This method tests the Region
	 * (which includes Region as set by PCTemplate objects), not solely the
	 * character Region. This does not compare the SubRegion.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given Region will be tested to see if it matches the Player
	 *            Character's Region.
	 * @param r
	 *            The Region to be tested to determine if it matches the Player
	 *            Character's Region
	 * @return true if the Region of the Player Character represented by the
	 *         given CharID matches the given Region; false otherwise.
	 */
	public boolean matchesRegion(CharID id, Region r)
	{
		return getRegion(id).orElse(Region.NONE)
			.equals(Optional.ofNullable(r).orElse(Region.NONE));
	}

	/**
	 * Returns a String representation of the character SubRegion for the Player
	 * Character represented by the given CharID. Returns "NONE" if no character
	 * SubRegion is set for the Player Character
	 * 
	 * **NOTE** Unless you are analyzing (or storing) raw values for a Player
	 * Character, it is unlikely that you want this method. It is more likely
	 * that you should be using getSubRegion(CharID id)
	 * 
	 * @see RegionFacet#getSubRegion(CharID)
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            character SubRegion should be returned.
	 * @return A String representation of the character SubRegion for the Player
	 *         Character represented by the given CharID; "NONE" if no character
	 *         SubRegion is set for the Player Character
	 */
	public Optional<String> getCharacterSubRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		// character's subregion trumps any from templates
		if (rci != null && rci.subregion != null)
		{
			return Optional.of(rci.subregion);
		}
		return Optional.empty();
	}

	/**
	 * Returns a String representation of the SubRegion for the Player Character
	 * represented by the given CharID. Returns "NONE" if no SubRegion is set
	 * for the Player Character.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Region should be returned.
	 * @return A String representation of the SubRegion for the Player Character
	 *         represented by the given CharID; "NONE" if no SubRegion is set
	 *         for the Player Character
	 */
	public Optional<String> getSubRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		// character's subregion trumps any from templates
		if (rci != null && rci.subregion != null)
		{
			return Optional.of(rci.subregion);
		}

		return templateFacet.getSet(id).stream()
			.map(
				template -> Optional.ofNullable(template.get(StringKey.SUBREGION)))
			.filter(Optional::isPresent)
			.reduce(Optional.empty(), (current, next) -> next);
	}

	/**
	 * Returns a String representation of the full Region (Region and SubRegion)
	 * for the Player Character represented by the given CharID. Returns "NONE"
	 * if no Region is set for the Player Character.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            full Region should be returned.
	 * @return A String representation of the full Region for the Player
	 *         Character represented by the given CharID; "NONE" if no Region is
	 *         set for the Player Character
	 */
	public String getFullRegion(CharID id)
	{
		Optional<String> sub = getSubRegion(id);
		StringBuilder tempRegName = new StringBuilder(40).append(getRegionString(id));

		sub.ifPresent(subRegion -> tempRegName.append(" (").append(subRegion.toString()).append(')'));

		return tempRegName.toString();
	}

	/**
	 * RegionClassInfo is the data structure used by RegionFacet to store a
	 * Player Character's Region and SubRegion if they are directly set by a
	 * user.
	 */
	private static class RegionCacheInfo
	{
		public Optional<Region> cachedRegion = Optional.empty();

		public Region region;

		public String subregion;

		@Override
		public String toString()
		{
			return region + " " + subregion + " " + cachedRegion.orElse(Region.NONE);
		}

		@Override
		public int hashCode()
		{
			return (region == null ? -1 : region.hashCode());
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof RegionCacheInfo other)
			{
				return Objects.equals(region, other.region) && Objects.equals(subregion, other.subregion)
					&& Objects.equals(cachedRegion, other.cachedRegion);
			}
			return false;
		}

	}

	/**
	 * Copies the contents of the RegionFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in RegionFacet in order to avoid exposing the mutable
	 * RegionCacheInfo object to other classes. This should not be inlined, as
	 * RegionCacheInfo is internal information to RegionFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no Region references are
	 * maintained between the Player Characters represented by the given CharIDs
	 * (meaning once this copy takes place, any change to the Region will only
	 * impact the Player Character where the Region was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            Region information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            Region information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID destination)
	{
		RegionCacheInfo sourceRCI = getInfo(source);
		if (sourceRCI != null)
		{
			RegionCacheInfo destRCI = getConstructingInfo(destination);
			destRCI.region = sourceRCI.region;
			destRCI.subregion = sourceRCI.subregion;
		}
	}

	/**
	 * Drives an update of the Region and SubRegion for a Player Character when
	 * a CDOMObject is added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which RegionFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
	{
		updateRegion(dfce.getCharID());
	}

	private void updateRegion(CharID id)
	{
		RegionCacheInfo rci = getInfo(id);
		Optional<Region> current = rci.cachedRegion;
		Optional<Region> newRegion = getRegion(id);
		if (current.isEmpty() || !current.equals(newRegion))
		{
			current.ifPresent(region -> fireDataFacetChangeEvent(id, region.toString(), DataFacetChangeEvent.DATA_REMOVED));
			rci.cachedRegion = newRegion;
			fireDataFacetChangeEvent(id, newRegion.get().toString(), DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Drives an update of the Region and SubRegion for a Player Character when
	 * a CDOMObject is removed from a Player Character.
	 * 
	 * Triggered when one of the Facets to which RegionFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
	{
		updateRegion(dfce.getCharID());
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

}
