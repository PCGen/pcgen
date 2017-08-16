/*
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 *
 */
package pcgen.core;

/**
 * {@code RuleConstants}
 * This interface holds all rules VAR names used in the code.
 *
 * (The reason for an interface rather than a class
 * is that an interface uses a little less memory.)
 *
 */
public interface RuleConstants
{
	String ABILRANGE			= "ABILRANGE";						// Allow any range for ability scores
	String AMMOSTACKSWITHWEAPON	= "AMMOSTACKSWITHWEAPON";			// Do ammunition enhancement bonus stack with those of the weapon
	String BONUSSPELLKNOWN		= "BONUSSPELLKNOWN";				// Add stat bonus to Spells Known
	String CLASSPRE				= "CLASSPRE";						// Bypass Class Prerequisites
	String EQUIPATTACK			= "EQUIPATTACK";					// Treat Weapons In Hand As Equipped For Attacks
	String FEATPRE				= "FEATPRE";						// Bypass Feat Prerequisites
	String FREECLOTHES			= "FREECLOTHES";					// Ask For Free Clothing at First Level
	String CLOTHINGENCUMBRANCE  = "CLOTHINGENCUMBRANCE";		   	// First set of equipped clothing counts towards encumbrance 
	String INTBEFORE			= "INTBEFORE";						// Increment STAT before calculating skill points when leveling
	String RETROSKILL			= "RETROSKILL";						// Changes to bonus skill points are retroactive
	String INTBONUSLANG			= "INTBONUSLANG";					// Allow Selection of Int bonus Languages after 1st level
	String LEVELCAP				= "LEVELCAP";						// Ignore Level Cap
	String PROHIBITSPELLS		= "PROHIBITSPELLS";					// Restict Cleric/Druid spells based on alignment
	String SIZECAT				= "SIZECAT";						// Use 3.5 Weapon Categories
	String SIZEOBJ				= "SIZEOBJ";						// Use 3.0 Weapon Size
	String SKILLMAX				= "SKILLMAX";						// Bypass Max Skill Ranks
	String SYS_35WP				= "SYS_35WP";						// Apply 3.5 Size Category Penalty to Attacks
//	String SYS_CIP				= "SYS_CIP";						// Improper tools incure a -2 circumstance penalty
//	String SYS_DOMAIN			= "SYS_DOMAIN";						// Apply Casterlevel Bonuses from Domains to Spells
	String SYS_LDPACSK			= "SYS_LDPACSK";					// Apply Load Penalty to AC and Skills
	String SYS_WTPSK			= "SYS_WTPSK";						// Apply Weight Penalty to Skills
}
