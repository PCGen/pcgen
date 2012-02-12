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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FactFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.LevelFacet;
import pcgen.cdom.facet.RaceTypeFacet;
import pcgen.cdom.facet.SuppressBioFieldFacet;
import pcgen.cdom.facet.TemplateFacet;
import pcgen.cdom.facet.VisionFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Vision;
import pcgen.util.enumeration.Visibility;

public class CharacterDisplay
{

	private final CharID id;

	private FactFacet factFacet = FacetLibrary.getFacet(FactFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
	private RaceTypeFacet raceTypeFacet = FacetLibrary.getFacet(RaceTypeFacet.class);
	private SuppressBioFieldFacet suppressBioFieldFacet = FacetLibrary.getFacet(SuppressBioFieldFacet.class);
	private TemplateFacet templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
	private VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
	private FormulaResolvingFacet formulaResolvingFacet = FacetLibrary.getFacet(FormulaResolvingFacet.class);

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
	 * Get tab name.
	 * 
	 * @return name on tab
	 */
	public String getTabName()
	{
		return getSafeStringFor(StringKey.TAB_NAME);
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

	/**
	 * Returns a String with the characters Race Type (e.g. Humanoid).
	 * 
	 * @return The character's race type or &quot;None&quot;
	 */
	public String getRaceType()
	{
		RaceType rt = raceTypeFacet.getRaceType(id);
		return rt == null ? Constants.NONE : rt.toString();
	}

	public int getTotalLevels()
	{
		return levelFacet.getTotalLevels(id);
	}

	/**
	 * Get the Spell Resistance granted by the given template to a character at a
	 * given level (Class and Hit Dice). This will include the absolute
	 * adjustment made with SR:, LEVEL:<num>:SR and HD:<num>:SR tags
	 * 
	 * Note: unlike DR and CR, the value returned here includes the PCs own
	 * Spell Resistance.
	 * 
	 * @param pct
	 * 			The PCTemplate for which the Spell Resistance will be returned.
	 * @param level
	 *            The level to calculate the SR for
	 * @param hitdice
	 *            The Hit dice to calculate the SR for
	 * 
	 * @return the Spell Resistance granted by the given Template at the given level
	 *         and HD
	 */
	public int getTemplateSR(PCTemplate pct, int level, int hitdice)
	{
		String qualifiedKey = pct.getQualifiedKey();
		Formula reduction = pct.getSafe(ObjectKey.SR).getReduction();
		int aSR =
				formulaResolvingFacet.resolve(id, reduction, qualifiedKey)
					.intValue();

		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				if (lt.get(IntegerKey.LEVEL) <= level)
				{
					Formula ltReduction =
							lt.getSafe(ObjectKey.SR).getReduction();
					int ltSR =
							formulaResolvingFacet.resolve(id, ltReduction,
								qualifiedKey).intValue();
					aSR = Math.max(aSR, ltSR);
				}
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			if (lt.get(IntegerKey.LEVEL) <= level)
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR =
						formulaResolvingFacet.resolve(id, ltReduction,
							qualifiedKey).intValue();
				aSR = Math.max(aSR, ltSR);
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			if (lt.get(IntegerKey.HD_MAX) <= hitdice
				&& lt.get(IntegerKey.HD_MIN) >= hitdice)
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR =
						formulaResolvingFacet.resolve(id, ltReduction,
							qualifiedKey).intValue();
				aSR = Math.max(aSR, ltSR);
			}
		}

		return aSR;
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on output.
	 * 
	 * @return The list of templates visible on output sheets.
	 */
	public List<PCTemplate> getOutputVisibleTemplateList()
	{
		List<PCTemplate> tl = new ArrayList<PCTemplate>();

		for (PCTemplate template : templateFacet.getSet(id))
		{
			if ((template.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
				|| (template.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY))
			{
				tl.add(template);
			}
		}
		return tl;
	}

}
