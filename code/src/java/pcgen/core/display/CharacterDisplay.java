/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import java.util.Collection;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FactFacet;
import pcgen.cdom.facet.SuppressBioFieldFacet;
import pcgen.cdom.facet.VisionFacet;
import pcgen.core.Vision;

public class CharacterDisplay
{

	private final CharID id;

	private FactFacet factFacet = FacetLibrary.getFacet(FactFacet.class);
	private SuppressBioFieldFacet suppressBioFieldFacet = FacetLibrary.getFacet(SuppressBioFieldFacet.class);
	private VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
	
	public CharacterDisplay(CharID id)
	{
		this.id = id;
	}

	/**
	 * Gets a 'safe' String representation
	 * 
	 * @param key
	 * @return a 'safe' String
	 */
	public String getSafeStringFor(StringKey key)
	{
		String s = factFacet.get(id, key);
		if (s == null)
		{
			s = Constants.EMPTY_STRING;
		}
		return s;
	}

	/**
	 * Get the BIO.
	 * 
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(StringKey.BIO);
	}

	/**
	 * Get the birthday.
	 * 
	 * @return birthday
	 */
	public String getBirthday()
	{
		return getSafeStringFor(StringKey.BIRTHDAY);
	}

	/**
	 * Get the catchphrase.
	 * 
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(StringKey.CATCH_PHRASE);
	}

	/**
	 * Get the description.
	 * 
	 * @return description
	 */
	public String getDescription()
	{
		return getSafeStringFor(StringKey.DESCRIPTION);
	}

	/**
	 * Get the characters eye colour.
	 * 
	 * @return the colour of their eyes
	 */
	public String getEyeColor()
	{
		return getSafeStringFor(StringKey.EYE_COLOR);
	}

	/**
	 * Gets the character's hair color.
	 * 
	 * @return A hair color string.
	 */
	public String getHairColor()
	{
		return getSafeStringFor(StringKey.HAIR_COLOR);
	}

	/**
	 * Gets the character's hair style.
	 * 
	 * @return The character's hair style.
	 */
	public String getHairStyle()
	{
		return getSafeStringFor(StringKey.HAIR_STYLE);
	}

	/**
	 * Returns the character's handedness string.
	 * 
	 * @return A String for handedness.
	 */
	public String getHanded()
	{
		return getSafeStringFor(StringKey.HANDED);
	}

	/**
	 * Gets a string of interests for the character.
	 * 
	 * @return A String of interests or an empty string.
	 */
	public String getInterests()
	{
		return getSafeStringFor(StringKey.INTERESTS);
	}

	/**
	 * Gets the character's location.
	 * 
	 * @return The character's location.
	 */
	public String getLocation()
	{
		return getSafeStringFor(StringKey.LOCATION);
	}

	/**
	 * Gets the phobia string for the character.
	 * 
	 * @return A phobia string.
	 */
	public String getPhobias()
	{
		return getSafeStringFor(StringKey.PHOBIAS);
	}

	/**
	 * Get skin colour.
	 * 
	 * @return skin colour
	 */
	public String getSkinColor()
	{
		return getSafeStringFor(StringKey.SKIN_COLOR);
	}

	/**
	 * Get speech tendency.
	 * 
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(StringKey.SPEECH_TENDENCY);
	}

	/**
	 * Get trait 1.
	 * 
	 * @return trait 1
	 */
	public String getTrait1()
	{
		return getSafeStringFor(StringKey.TRAIT1);
	}

	/**
	 * Get trait 2.
	 * 
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(StringKey.TRAIT2);
	}

	/**
	 * Check  whether the field should be hidden from output. 
	 * @param field The BiographyField to check export suppression rules for.
	 * @return true if the field should not be output, false if it may be.
	 */
	public boolean getSuppressBioField(BiographyField field)
	{
		return suppressBioFieldFacet.getSuppressField(id, field);
	}
	
	public Collection<Vision> getVisionList()
	{
		return visionFacet.getActiveVision(id);
	}

}
