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

import java.awt.geom.Point2D;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

/**
 * Faceacet is a Facet that tracks the Face of a Player Character (in game
 * rules, the exposed size of a Player Character on each side of the Player
 * Character)
 */
public class FaceFacet
{

	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

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
	public Point2D.Double getFace(CharID id)
	{
		final Race aRace = raceFacet.get(id);
		// Default to 5' by 5'
		Point2D.Double face = new Point2D.Double(5, 0);
		if (aRace != null)
		{
			Point2D.Double rf = aRace.getFace();
			if (rf != null)
			{
				face = rf;
			}
		}

		// Scan templates for any overrides
		for (PCTemplate template : templateFacet.getSet(id))
		{
			Point2D.Double tf = template.getFace();
			if (tf != null)
			{
				face = tf;
			}
		}
		return face;
	}

}
