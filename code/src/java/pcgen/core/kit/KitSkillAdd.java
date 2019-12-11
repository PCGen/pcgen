/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.kit;

import java.util.List;

import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * {@code KitSkillAdd}.
 */
public final class KitSkillAdd //implements java.io.Serializable
{
	private Skill skill;
	private double ranks;
	private int cost;
	private final List<Language> languages;
	private final PCClass pcclass;

	/**
	 * Constructor
	 * @param argSkill
	 * @param aRanks
	 * @param aCost
	 * @param aLanguages The languages which should be selected.
	 * @param pcClass 
	 */
	public KitSkillAdd(final Skill argSkill, double aRanks, int aCost, List<Language> aLanguages, PCClass pcClass)
	{
		skill = argSkill;
		ranks = aRanks;
		cost = aCost;
		languages = aLanguages;
		pcclass = pcClass;
	}

	/**
	 * Get ranks
	 * @return ranks
	 */
	public double getRanks()
	{
		return ranks;
	}

	/**
	 * Get skill
	 * @return skill
	 */
	public Skill getSkill()
	{
		return skill;
	}

	/**
	 * Get cost
	 * @return cost
	 */
	public int getCost()
	{
		return cost;
	}

	/**
	 * Gets the list of languages.
	 * @return the languages
	 */
	public List<Language> getLanguages()
	{
		return languages;
	}

	public PCClass getPCClass()
	{
		return pcclass;
	}

}
