/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 */
package pcgen.facade.core;

import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.Skill;

/**
 * InfoFactory defines the interface to be used by the UI for extracting
 * information about objects for display to the user.
 */
public interface InfoFactory
{

	public String getLevelAdjustment(PCTemplate template);

	public String getModifier(PCTemplate template);

	public String getPreReqHTML(PCTemplate template);

	public float getCost(EquipmentFacade equipment);

	public float getWeight(EquipmentFacade equipment);

	public String getPreReqHTML(Race race);

	public String getStatAdjustments(Race race);

	public String getVision(Race race);

	public String getFavoredClass(Race race);

	public String getLevelAdjustment(Race race);

	public String getMovement(Race race);

	/**
	 * Calculate the number of monster class levels (aka racial hit dice)
	 * that the race receives.
	 *
	 * @param raceFacade The race to check.
	 * @return The number of levels of monster classes, or 0 if none.
	 */
	public int getNumMonsterClassLevels(Race raceFacade);

	public String getHTMLInfo(Race race);

	public String getHTMLInfo(PCClass pcClass, PCClass parentClass);

	public String getHTMLInfo(Skill skill);

	public String getHTMLInfo(AbilityFacade ability);

	public String getHTMLInfo(Deity deity);

	public String getHTMLInfo(QualifiedObject<Domain> domain);

	/**
	 * Produce the HTML information string for an item for equipment.
	 *
	 * @param equipFacade The equipment item
	 * @return The HTML information string
	 */
	public String getHTMLInfo(EquipmentFacade equipFacade);

	/**
	 * Produce the HTML information string for an equipment modifier.
	 *
	 * @param equipmod The equipment modifier.
	 * @param equipFacade The equipment item the modifier relates to.
	 * @return The HTML information string.
	 */
	public String getHTMLInfo(EquipmentModifier equipmod, EquipmentFacade equipFacade);

	public String getHTMLInfo(PCTemplate template);

	public String getHTMLInfo(SpellFacade spell);

	/**
	 * Produce the HTML information string for a kit.
	 *
	 * @param kitFacade The kit
	 * @return The HTML information string
	 */
	public String getHTMLInfo(Kit kitFacade);

	/**
	 * Produce the HTML information string for a temporary bonus.
	 *
	 * @param tempBonusFacade The temporary bonus.
	 * @return The HTML information string
	 */
	public String getHTMLInfo(TempBonusFacade tempBonusFacade);

	/**
	 * Produce the HTML information string for a facade.
	 *
	 * @param facade The object to be described.
	 * @return The HTML information string
	 */
	public String getHTMLInfo(InfoFacade facade);

	/**
	 * Produce the HTML information string for spell book or spell list.
	 *
	 * @param name The spell book or spell list.
	 * @return The HTML information string
	 */
	public String getSpellBookInfo(String name);

	/**
	 * Get the description for the ability for this character.
	 *
	 * @param ability The ability to be described.
	 * @return The description.
	 */
	public String getDescription(AbilityFacade ability);

	/**
	 * Get the description for a race for this character.
	 *
	 * @param raceFacade The race to be described.
	 * @return The description.
	 */
	public String getDescription(Race raceFacade);

	/**
	 * Get the description for a template for this character.
	 *
	 * @param templateFacade The template to be described.
	 * @return The description.
	 */
	public String getDescription(PCTemplate templateFacade);

	/**
	 * Get the description for a class for this character.
	 *
	 * @param pcClass The class to be described.
	 * @return The description.
	 */
	public String getDescription(PCClass pcClass);

	/**
	 * Get the description for a skill for this character.
	 *
	 * @param skillFacade The skill to be described.
	 * @return The description.
	 */
	public String getDescription(Skill skillFacade);

	/**
	 * Get the description of a piece of equipment for this character.
	 *
	 * @param equipFacade The equipment to be described.
	 * @return The description.
	 */
	public String getDescription(EquipmentFacade equipFacade);

	/**
	 * Get the description for a kit for this character.
	 *
	 * @param kitFacade The kit to be described.
	 * @return The description.
	 */
	public String getDescription(Kit kitFacade);

	/**
	 * Get the description for a deity for this character.
	 *
	 * @param deityFacade The deity to be described.
	 * @return The description.
	 */
	public String getDescription(Deity deityFacade);

	/**
	 * Get the description for a domain for this character.
	 *
	 * @param domain The domain to be described.
	 * @return The description.
	 */
	public String getDescription(Domain domain);

	/**
	 * Get the description for a spell for this character.
	 *
	 * @param spellFacade The spell to be described.
	 * @return The description.
	 */
	public String getDescription(SpellFacade spellFacade);

	/**
	 * Get the description for a temp bonus for this character.
	 *
	 * @param tempBonusFacade The temp bonus to be described.
	 * @return The description.
	 */
	public String getDescription(TempBonusFacade tempBonusFacade);

	/**
	 * Get a display string of the deity's domains.
	 *
	 * @param deityFacade The deity to be output.
	 * @return The comma separated list of domains.
	 */
	public String getDomains(Deity deityFacade);

	/**
	 * Get a display string of the deity's pantheons.
	 *
	 * @param deityFacade The deity to be output.
	 * @return The comma separated list of pantheons.
	 */
	public String getPantheons(Deity deityFacade);

	/**
	 * Get a display string of the deity's favored weapons.
	 *
	 * @param deityFacade The deity to be output.
	 * @return The comma separated list of weapons.
	 */
	public String getFavoredWeapons(Deity deityFacade);

	/**
	 * Get a display string of the choices made for this character for the
	 * ability. The format may be either a, b or x3.
	 *
	 * @param abilityFacade The ability to be output.
	 * @return The comma separated list of choices.
	 */
	public String getChoices(AbilityFacade abilityFacade);

	/**
	 * Retrieve the description of the targets to which the temporary bonus
	 * can be applied. e.g. Character, or weapon,ranged.
	 *
	 * @param tempBonusFacade The temporary bonus.
	 * @return The targets.
	 */
	public String getTempBonusTarget(TempBonusFacade tempBonusFacade);

	public String getSize(Race obj);

}
