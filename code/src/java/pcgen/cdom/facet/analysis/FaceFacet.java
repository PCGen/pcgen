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
package pcgen.cdom.facet.analysis;

import java.math.BigDecimal;

import pcgen.base.math.OrderedPair;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.output.publish.OutputDB;

/**
 * FaceFacet is a Facet that tracks the Face of a Player Character (in game
 * rules, the exposed size of a Player Character on each side of the Player
 * Character).
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class FaceFacet implements ItemFacet<CharID, OrderedPair>
{

	private static final Integer ZERO = Integer.valueOf(0);
	private static final Integer FIVE = Integer.valueOf(5);

	private TemplateFacet templateFacet;
	private RaceFacet raceFacet;

	/**
	 * Returns the Face of the Player Character represented by the given CharID.
	 * The Face is a Point2D, where the X value of the Point represents the
	 * front/rear facing size and the Y value of the Point represents the
	 * left/right side facing size.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Face will be returned
	 * @return The Face of the Player Character represented by the given CharID
	 */
	@Override
	public OrderedPair get(CharID id)
	{
		final Race aRace = raceFacet.get(id);
		// Default to 5' by 5'
		OrderedPair face = new OrderedPair(FIVE, ZERO);
		if (aRace != null)
		{
			OrderedPair rf = getFace(aRace);
			if (rf != null)
			{
				face = rf;
			}
		}

		// Scan templates for any overrides
		for (PCTemplate template : templateFacet.getSet(id))
		{
			OrderedPair tf = getFace(template);
			if (tf != null)
			{
				face = tf;
			}
		}
		return face;
	}

	/**
	 * Returns the Face for the Player Character as defined by a single
	 * CDOMObject.
	 * 
	 * @param cdo
	 *            The CDOMObject in which the Face information is stored
	 * @return The Point2D indicating the Face as defined by the given
	 *         CDOMObject.
	 */
	private OrderedPair getFace(CDOMObject cdo)
	{
		BigDecimal width = cdo.get(ObjectKey.FACE_WIDTH);
		BigDecimal height = cdo.get(ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		return new OrderedPair(width, height);
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void init()
	{
		OutputDB.register("face", this);
	}
}
