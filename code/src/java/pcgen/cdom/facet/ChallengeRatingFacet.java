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

/**
 * ChallengeRatingFacet is a Facet that calculates the Challenge Rating of a
 * Player Character
 */
public class ChallengeRatingFacet
{
	private TemplateFacet templateFacet;
	private RaceFacet raceFacet;
	private ClassFacet classFacet;
	private FormulaResolvingFacet formulaResolvingFacet;
	private BonusCheckingFacet bonusCheckingFacet;
	private LevelFacet levelFacet;

	/**
	 * Returns the Challenge Rating of the Player Character represented by the
	 * given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Chellenge Rating should be returned
	 * @return The Challenge Rating of the Player Character represented by the
	 *         given CharID
	 */
	public float getCR(CharID id)
	{
		float CR = getClassCR(id) + getTemplateCR(id);

		final float raceCR = calcRaceCR(id);
		// If the total CR to date is 0 then add race CR, e.g. A Lizard has CR
		// of 1/2
		if (CR == 0)
		{
			CR += raceCR;
		}
		// Else if the total CR so far is 1 or greater and the race CR is
		// greater than or equal to 1 then add the race CR
		else if (CR >= 1 && raceCR >= 1)
		{
			CR += raceCR;
		}

		// Calculate and add in the MISC bonus to CR
		CR += (float) bonusCheckingFacet.getBonus(id, "MISC", "CR");

		return CR;
	}

	/**
	 * Returns the ChallengeRating provided solely by PCTemplate objects granted
	 * to the Player Character
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the PCTemplate objects granted
	 *         to the Player Character
	 */
	private float getTemplateCR(CharID id)
	{
		float CR = 0;

		// Calculate and add the CR from the templates
		for (PCTemplate template : templateFacet.getSet(id))
		{
			CR +=
					template.getCR(levelFacet.getTotalLevels(id), levelFacet
						.getMonsterLevelCount(id));
		}
		return CR;
	}

	/**
	 * Returns the ChallengeRating provided solely by PCClass objects granted to
	 * the Player Character
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the PCClass objects granted to
	 *         the Player Character
	 */
	private float getClassCR(CharID id)
	{
		float CR = 0;

		// Calculate and add the CR from the PC Classes
		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			CR += calcClassCR(id, pcClass);
		}
		return CR;
	}

	/**
	 * Returns the ChallengeRating provided solely by the Race of the Player
	 * Character
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the Race of the Player Character
	 */
	private float calcRaceCR(CharID id)
	{
		// Calculate and add the CR from race
		ChallengeRating cr = raceFacet.get(id).getSafe(
				ObjectKey.CHALLENGE_RATING);
		final float raceCR = formulaResolvingFacet.resolve(id, cr.getRating(), "")
				.floatValue();
		return raceCR;
	}

	private float calcClassCR(CharID id, PCClass cl)
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

		return cr == null ? 0 : formulaResolvingFacet.resolve(id, cr,
				cl.getQualifiedKey()).floatValue();
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	public void setLevelFacet(LevelFacet levelFacet)
	{
		this.levelFacet = levelFacet;
	}

}
