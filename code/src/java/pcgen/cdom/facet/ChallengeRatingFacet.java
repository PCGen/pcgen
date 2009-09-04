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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.ClassType;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;

public class ChallengeRatingFacet
{
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
	private FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);
	private BonusCheckingFacet bonusFacet = FacetLibrary
			.getFacet(BonusCheckingFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);

	public float getCR(CharID id)
	{
		float CR = 0;

		// Calculate and add the CR from the PC Classes
		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			CR += calcClassCR(id, pcClass);
		}

		// Calculate and add the CR from the templates
		for (PCTemplate template : templateFacet.getSet(id))
		{
			CR += template.getCR(levelFacet.getTotalLevels(id), levelFacet
					.getMonsterLevelCount(id));
		}

		final float raceCR = calcRaceCR(id);
		// If the total CR to date is 0 then add race CR, e.g. A Lizard has CR
		// of 1/2
		if (CR == 0)
		{
			CR += raceCR;
		}
		// Else if the total CR so far is 1 or greater and the race CR is
		// greater
		// than or equal to 1 then add the race CR
		else if (CR >= 1 && raceCR >= 1)
		{
			CR += raceCR;
		}

		// Calculate and add in the MISC bonus to CR
		CR += (float) bonusFacet.getBonus(id, "MISC", "CR");

		return CR;
	}

	private float calcRaceCR(CharID id)
	{
		// Calculate and add the CR from race
		ChallengeRating cr = raceFacet.get(id).getSafe(
				ObjectKey.CHALLENGE_RATING);
		final float raceCR = resolveFacet.resolve(id, cr.getRating(), "")
				.floatValue();
		return raceCR;
	}

	public float calcClassCR(CharID id, PCClass cl)
	{
		Formula cr = cl.get(FormulaKey.CR);
		if (cr == null)
		{
			/*
			 * TODO I don't like the fact that this method is accessing the
			 * ClassTypes and using one of those to set one of its variables. In
			 * theory, we should have a ClassType that triggers CR that is not a
			 * TYPE, but a unique token. See this thread:
			 * http://tech.groups.yahoo.com/group/pcgen_experimental/message/10778
			 */
			for (Type type : cl.getTrueTypeList(false))
			{
				final ClassType aClassType = SettingsHandler.getGame()
						.getClassTypeByName(type.toString());
				if (aClassType != null)
				{
					String crf = aClassType.getCRFormula();
					if (!"0".equals(crf))
					{
						cr = FormulaFactory.getFormulaFor(crf);
					}
				}
			}
		}

		return cr == null ? 0 : resolveFacet.resolve(id, cr,
				cl.getQualifiedKey()).floatValue();
	}
}
