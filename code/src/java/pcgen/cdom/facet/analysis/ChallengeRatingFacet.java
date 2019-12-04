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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.ClassType;
import pcgen.core.PCClass;
import pcgen.core.SettingsHandler;

import org.jetbrains.annotations.Nullable;

/**
 * ChallengeRatingFacet is a Facet that calculates the Challenge Rating of a
 * Player Character
 * 
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
	 *            Challenge Rating should be returned
	 * @return The Challenge Rating of the Player Character represented by the
	 *         given CharID
	 */
	@Nullable
	public Integer getCR(CharID id)
	{
		int cr = 0;

		if (levelFacet.getMonsterLevelCount(id) == 0)
		{
			if (levelFacet.getNonMonsterLevelCount(id) == 0)
			{
				return null;
			}
			// calculate and add class CR for 0-HD races
			cr += calcClassesCR(id);
		}
		else
		{
			// calculate and add race CR and classes CR for 
			// races with racial hit dice
			Integer classRaceCR = calcClassesForRaceCR(id);
			if (classRaceCR == null)
			{
				return null;
			}
			Integer raceCR = calcRaceCR(id);
			if (raceCR != null)
			{
				cr += raceCR;
			}
			cr += classRaceCR;
		}

		// calculate and add CR bonus from templates
		cr += getTemplateCR(id);

		// calculate and add in the MISC bonus to CR
		cr += (int) bonusCheckingFacet.getBonus(id, "MISC", "CR");

		return cr;
	}

	/**
	 * Returns the ChallengeRating provided solely by the Race of the Player
	 * Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the Race of the Player Character
	 *         identified by the given CharID
	 */
	public Integer calcRaceCR(CharID id)
	{
		// Calculate and add the CR from race
		ChallengeRating cr = raceFacet.get(id).getSafe(ObjectKey.CHALLENGE_RATING);
		return cr.toInteger();
	}

	/**
	 * Returns the racial hit dice provided solely by the race of the
	 * character identified by the given CharID.
	 * 
	 * @param id  The CharID representing the character
	 * 
	 * @return the racial hit dice provided by the race of the character
	 *         identified by the given CharID
	 */
	public int getBaseHD(CharID id)
	{
		final LevelCommandFactory lcf = raceFacet.get(id).getSafe(ObjectKey.MONSTER_CLASS);
		/*
		 * BUG This converts a formula to a String?  What if it's a formula, NFE?
		 */
		return Integer.parseInt(lcf.getLevelCount().toString());
	}

	/**
	 * Returns the ChallengeRating provided solely by PCTemplate objects granted
	 * to the Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the PCTemplate objects granted
	 *         to the Player Character identified by the given CharID
	 */
	private int getTemplateCR(CharID id)
	{
		// Calculate and add the CR from the templates
		return templateFacet.getSet(id)
		                    .stream()
		                    .mapToInt(template -> template.getCR(
				                      levelFacet.getTotalLevels(id),
				                      levelFacet.getMonsterLevelCount(id)
		                      ))
		                    .sum();
	}

	/**
	 * Returns the ChallengeRating provided solely by PCClass objects granted to
	 * the Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the PCClass objects granted to
	 *         the Player Character identified by the given CharID
	 */
	private int calcClassesCR(CharID id)
	{
		int cr = 0;
		int crMod = 0;
		int crModPriority = 0;

		for (PCClass pcClass : classFacet.getSet(id))
		{
			cr += calcClassCR(id, pcClass);
			int crmp = getClassCRModPriority(pcClass);
			if (crmp != 0 && (crmp < crModPriority || crModPriority == 0))
			{
				Integer raceMod = getClassRaceCRMod(id, pcClass);
                crMod = Objects.requireNonNullElseGet(raceMod, () -> getClassCRMod(id, pcClass));
				crModPriority = crmp;
			}
		}
		cr += crMod;

		return cr;
	}

	private Integer calcClassesForRaceCR(CharID id)
	{
		int cr = 0;
		int levelsKey = 0;
		int levelsNonKey = 0;
		int levelsConverted = 0;
		int threshold = 0;

		List<String> raceRoleList = raceFacet.get(id).getListFor(ListKey.MONSTER_ROLES);
		if ((raceRoleList == null) || raceRoleList.isEmpty())
		{
			raceRoleList = SettingsHandler.getGame().getMonsterRoleDefaultList();
		}

		// Calculate and add the CR from the PC Classes
		for (PCClass pcClass : classFacet.getSet(id))
		{
			Integer levels = calcClassCR(id, pcClass);
			if (levels == null)
			{
				return null;
			}

			List<String> classRoleList = pcClass.getListFor(ListKey.MONSTER_ROLES);
			if (classRoleList != null)
			{
				classRoleList.retainAll(raceRoleList);
				if (classRoleList.isEmpty())
				{
					levelsNonKey += levels;
				}
				else
				{
					levelsKey += levels;
				}
			}
			else
			{
				levelsNonKey += levels;
			}

		}
		String sThreshold = SettingsHandler.getGame().getCRThreshold();
		if (sThreshold != null)
		{
			threshold = formulaResolvingFacet.resolve(id, FormulaFactory.getFormulaFor(sThreshold), "").intValue();
		}

		while (levelsNonKey > 1)
		{
			cr++;
			// TODO: maybe the divisor 2 should be made configurable, 
			// or the whole calculation put into a formula
			levelsNonKey -= 2;
			levelsConverted += 2;
			if (levelsConverted >= threshold)
			{
				break;
			}
		}
		if (levelsConverted > 0)
		{
			cr += levelsNonKey;
		}
		cr += levelsKey;

		return cr;
	}

	private Integer getClassRaceCRMod(CharID id, PCClass cl)
	{
		String classType = cl.getClassType();

		if (classType != null)
		{
			if (SettingsHandler.getGame().getClassTypeByName(classType) != null)
			{
				Integer crMod = raceFacet.get(id).get(MapKey.CRMOD, classType);
                return crMod;
			}
		}
		else
		{
			// For migration purposes, if CLASSTYPE is not set, 
			// use old method to determine the class type from TYPE. 
			for (Type type : cl.getTrueTypeList(false))
			{
				classType = type.toString();
				if (SettingsHandler.getGame().getClassTypeByName(classType) != null)
				{
					Integer crMod = raceFacet.get(id).get(MapKey.CRMOD, classType);
					if (crMod != null)
					{
						return crMod;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the ChallengeRating provided solely by the given Class of the
	 * Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @param cl
	 *            The PCClass for which the class Challenge Rating should be
	 *            calculated
	 * @return the Challenge Rating provided solely by the given Class of the
	 *         Player Character identified by the given CharID
	 */
	private Integer calcClassCR(CharID id, PCClass cl)
	{
		Formula cr = cl.get(FormulaKey.CR);
		if (cr == null)
		{
			ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(cl.getClassType());
			if (aClassType != null)
			{
				String crf = aClassType.getCRFormula();
				if ("NONE".equalsIgnoreCase(crf))
				{
					return null;
				}
				else if (!"0".equals(crf))
				{
					cr = FormulaFactory.getFormulaFor(crf);
				}
			}
			else
			{
				// For migration purposes, if CLASSTYPE is not set, 
				// use old method to determine the class type from TYPE. 
				for (Type type : cl.getTrueTypeList(false))
				{
					aClassType = SettingsHandler.getGame().getClassTypeByName(type.toString());
					if (aClassType != null)
					{
						String crf = aClassType.getCRFormula();
						if ("NONE".equalsIgnoreCase(crf))
						{
							return null;
						}
						else if (!"0".equals(crf))
						{
							cr = FormulaFactory.getFormulaFor(crf);
						}
					}
				}
			}
		}

		return cr == null ? 0 : formulaResolvingFacet.resolve(id, cr, cl.getQualifiedKey()).intValue();
	}

	private int getClassCRMod(CharID id, PCClass cl)
	{
		int crMod = 0;

		ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(cl.getClassType());
		if (aClassType != null)
		{
			String crmf = aClassType.getCRMod();
			Formula crm = FormulaFactory.getFormulaFor(crmf);
			crMod = Math.min(crMod, formulaResolvingFacet.resolve(id, crm, cl.getQualifiedKey()).intValue());
		}
		else
		{
			// For migration purposes, if CLASSTYPE is not set, 
			// use old method to determine the class type from TYPE. 
			for (Type type : cl.getTrueTypeList(false))
			{
				aClassType = SettingsHandler.getGame().getClassTypeByName(type.toString());
				if (aClassType != null)
				{
					String crmf = aClassType.getCRMod();
					Formula crm = FormulaFactory.getFormulaFor(crmf);
					crMod = Math.min(crMod, formulaResolvingFacet.resolve(id, crm, cl.getQualifiedKey()).intValue());
				}
			}
		}

		return crMod;
	}

	private static int getClassCRModPriority(PCClass cl)
	{
		int crModPriority = 0;

		ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(cl.getClassType());
		if (aClassType != null)
		{
			int crmp = aClassType.getCRModPriority();
			if (crmp != 0)
			{
				crModPriority = crmp;
			}
		}
		else
		{
			// For migration purposes, if CLASSTYPE is not set, 
			// use old method to determine the class type from TYPE. 
			for (Type type : cl.getTrueTypeList(false))
			{
				aClassType = SettingsHandler.getGame().getClassTypeByName(type.toString());
				if (aClassType != null)
				{
					int crmp = aClassType.getCRModPriority();
					if (crmp != 0)
					{
						crModPriority = aClassType.getCRModPriority();
					}
				}
			}
		}

		return crModPriority;
	}

	public int getXPAward(CharID id)
	{
		Map<Integer, Integer> xpAwardsMap = SettingsHandler.getGame().getXPAwards();

		if (!xpAwardsMap.isEmpty())
		{
			Integer cr = getCR(id);
			if (cr == null)
			{
				return 0;
			}
			Integer xp = xpAwardsMap.get(cr);
			return xp == null ? 0 : xp;
		}
		return 0;
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
