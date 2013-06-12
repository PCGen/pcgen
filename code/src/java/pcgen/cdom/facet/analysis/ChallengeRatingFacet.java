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

import org.apache.commons.lang.math.Fraction;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
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
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;

/**
 * ChallengeRatingFacet is a Facet that calculates the Challenge Rating of a
 * Player Character
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
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
	public Float getCR(CharID id)
	{
		Float CR = new Float(0);
		
		if (levelFacet.getMonsterLevelCount(id) == 0)
		{
			if (levelFacet.getNonMonsterLevelCount(id) == 0)
			{
				return Float.NaN;
			}
			// calculate and add class CR for 0-HD races
			CR += calcClassesCR(id);
		}
		else
		{
			// calculate and add race CR and classes CR for 
			// races with racial hit dice
			Float classRaceCR = calcClassesForRaceCR(id);
			if (classRaceCR.isNaN())
			{
				return Float.NaN;
			}
			CR += calcRaceCR(id);
			CR += classRaceCR;
		}
		
		// calculate and add CR bonus from templates
		CR += getTemplateCR(id);

		// calculate and add in the MISC bonus to CR
		CR += (float) bonusCheckingFacet.getBonus(id, "MISC", "CR");

		// change calculated results of less than CR 1 to fraction format
		if (CR < 1)
		{
			Float crMod = SettingsHandler.getGame().getCRSteps().get((int)CR.floatValue());
			if (crMod != null)
			{
				CR = crMod;
			}
			else
			{
				CR = Math.max(CR, 0);
			}
		}

		return CR;
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
	public Float calcRaceCR(CharID id)
	{
		// Calculate and add the CR from race
		ChallengeRating cr = raceFacet.get(id).getSafe(
				ObjectKey.CHALLENGE_RATING);
		final Float raceCR = formulaResolvingFacet.resolve(id, cr.getRating(), "")
				.floatValue();
		return raceCR;
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
	private Float getTemplateCR(CharID id)
	{
		Float CR = new Float(0);

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
	 * the Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character
	 * @return the Challenge Rating provided by the PCClass objects granted to
	 *         the Player Character identified by the given CharID
	 */
	private Float calcClassesCR(CharID id)
	{
		Float CR = new Float(0);
		Float CRMod = new Float(0);
		int CRModPriority = 0;
		
		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			CR += calcClassCR(id, pcClass);
			int crmp = getClassCRModPriority(id, pcClass);
			if (crmp != 0 && (crmp < CRModPriority || CRModPriority == 0))
			{
				Float raceMod = getClassRaceCRMod(id, pcClass);
				if (raceMod != null)
				{
					CRMod = raceMod;
				}
				else
				{
					CRMod = getClassCRMod(id, pcClass);
				}
				CRModPriority = crmp;
			}
		}
		CR += CRMod;
		
		return CR;
	}
	private Float calcClassesForRaceCR(CharID id)
	{
		Float CR = new Float(0);
		int levelsKey = 0;
		int levelsNonKey = 0;
		int levelsConverted = 0;
		int threshold = 0;

		List<String> raceRoleList = raceFacet.get(id).getListFor(ListKey.MONSTER_ROLES);
		if (raceRoleList == null || raceRoleList.isEmpty())
		{
			raceRoleList = SettingsHandler.getGame().getMonsterRoleDefaultList();
		}
		
		// Calculate and add the CR from the PC Classes
		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			Float levels = calcClassCR(id, pcClass);
			if (levels.isNaN())
			{
				return Float.NaN;
			}
			
			List<String> classRoleList= pcClass.getListFor(ListKey.MONSTER_ROLES);
			if (classRoleList != null) 
			{
				classRoleList.retainAll(raceRoleList);
				if (classRoleList.size() > 0)
				{
					levelsKey += (int) levels.floatValue();
				}
				else
				{
					levelsNonKey += (int) levels.floatValue();
				}
			}
			else
			{
				if (raceRoleList != null)
				{
					levelsNonKey += (int) levels.floatValue();
				}
				else
				{
					levelsKey += (int) levels.floatValue();
				}
			}
			
		}
		String sThreshold = SettingsHandler.getGame().getCRThreshold();
		if (sThreshold != null)
		{
			threshold = formulaResolvingFacet.resolve(id, 
					FormulaFactory.getFormulaFor(sThreshold), "").intValue();
		}
			

		while (levelsNonKey > 1)
		{
			CR++;
			// TODO: maybe the divisor 2 should be be made configurable, 
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
			CR += levelsNonKey;
		}
		CR += levelsKey;
	
		return CR;
	}

	private Float getClassRaceCRMod(CharID id, PCClass cl)
	{
		String classType = cl.getClassType();
		
		if (classType != null)
		{
			if (SettingsHandler.getGame().getClassTypeByName(classType) != null)
			{
				Float crMod = raceFacet.get(id).get(MapKey.CRMOD, classType);
				if (crMod != null)
				{
					return crMod;
				}
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
					Float crMod = raceFacet.get(id).get(MapKey.CRMOD, classType);
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
	private Float calcClassCR(CharID id, PCClass cl)
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
			ClassType aClassType = SettingsHandler.getGame()
						.getClassTypeByName(cl.getClassType());
			if (aClassType != null)
			{
				String crf = aClassType.getCRFormula();
				if ("NONE".equalsIgnoreCase(crf))
				{
					return Float.NaN;
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
					aClassType = SettingsHandler.getGame()
							.getClassTypeByName(type.toString());
					if (aClassType != null)
					{
						String crf = aClassType.getCRFormula();
						if ("NONE".equalsIgnoreCase(crf))
						{
							return Float.NaN;
						}
						else if (!"0".equals(crf))
						{
							cr = FormulaFactory.getFormulaFor(crf);
						}
					}
				}
			}
		}

		return cr == null ? 0 : formulaResolvingFacet.resolve(id, cr,
				cl.getQualifiedKey()).floatValue();
	}

	private Float getClassCRMod(CharID id, PCClass cl)
	{
		Formula crm = cl.get(FormulaKey.CRMOD);
		Float crMod = new Float(0);

		ClassType aClassType = SettingsHandler.getGame()
				.getClassTypeByName(cl.getClassType());
		if (aClassType != null)
		{
			String crmf = aClassType.getCRMod();
			crm = FormulaFactory.getFormulaFor(crmf);
			
			crMod = Math.min(crMod, formulaResolvingFacet.resolve(id, crm,
					cl.getQualifiedKey()).floatValue());
		}
		else
		{
			// For migration purposes, if CLASSTYPE is not set, 
			// use old method to determine the class type from TYPE. 
			for (Type type : cl.getTrueTypeList(false))
			{
				aClassType = SettingsHandler.getGame()
						.getClassTypeByName(type.toString());
				if (aClassType != null)
				{
					String crmf = aClassType.getCRMod();
					crm = FormulaFactory.getFormulaFor(crmf);
					
					crMod = Math.min(crMod, formulaResolvingFacet.resolve(id, crm,
							cl.getQualifiedKey()).floatValue());
				}
			}
		}

		return crMod;
	}

	private int getClassCRModPriority(CharID id, PCClass cl)
	{
		int crModPriority = 0;
		
		ClassType aClassType = SettingsHandler.getGame()
				.getClassTypeByName(cl.getClassType());
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
				aClassType = SettingsHandler.getGame()
						.getClassTypeByName(type.toString());
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
		Map<String, Integer> xpAwardsMap = SettingsHandler.getGame().getXPAwards();

		if (xpAwardsMap.size() > 0)
		{
			Float cr = getCR(id);
			if (cr.isNaN()|| cr == 0)
			{
				return 0;
			}
			String crString = "";
			String crAsString = Float.toString(cr);
			String decimalPlaceValue =
					crAsString.substring(crAsString.length() - 2);
			
			// If the CR is a fractional CR then we convert to a 1/x format
			if (cr > 0 && cr < 1)
			{
				Fraction fraction = Fraction.getFraction(cr);// new Fraction(CR);
				int denominator = fraction.getDenominator();
				int numerator = fraction.getNumerator();
				crString = numerator + "/" + denominator;
			}
			else if (cr >= 1 || cr == 0)
			{
				int newCr = -99;
				if (decimalPlaceValue.equals(".0"))
				{
					newCr = (int) cr.floatValue();
				}
			
				if (newCr > -99)
				{
					crString = crString + newCr;
				}
				else
				{
					crString = crString + cr;
				}
			}
			return xpAwardsMap.get(crString);
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
