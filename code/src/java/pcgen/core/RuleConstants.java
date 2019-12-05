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
 * This class holds all rules VAR names used in the code.
 */
@SuppressWarnings("WeakerAccess")
public final class RuleConstants
{
    public static final String ABILRANGE = "ABILRANGE"; // Allow any range for ability scores
    public static final String AMMOSTACKSWITHWEAPON = "AMMOSTACKSWITHWEAPON"; // Do ammo enhancement bonus stack with those of the weapon
    public static final String BONUSSPELLKNOWN = "BONUSSPELLKNOWN"; // Add stat bonus to Spells Known
    public static final String CLASSPRE = "CLASSPRE"; // Bypass Class Prerequisites
    public static final String EQUIPATTACK = "EQUIPATTACK"; // Treat Weapons In Hand As Equipped For Attacks
    public static final String FEATPRE = "FEATPRE"; // Bypass Feat Prerequisites
    public static final String FREECLOTHES = "FREECLOTHES"; // Ask For Free Clothing at First Level
    public static final String CLOTHINGENCUMBRANCE = "CLOTHINGENCUMBRANCE"; // First set of equipped clothing counts towards encumbrance
    public static final String INTBEFORE = "INTBEFORE"; // Increment STAT before calculating skill points when leveling
    public static final String RETROSKILL = "RETROSKILL"; // Changes to bonus skill points are retroactive
    public static final String INTBONUSLANG = "INTBONUSLANG"; // Allow Selection of Int bonus Languages after 1st level
    public static final String LEVELCAP = "LEVELCAP"; // Ignore Level Cap
    public static final String PROHIBITSPELLS = "PROHIBITSPELLS"; // Restrict Cleric/Druid spells based on alignment
    public static final String SIZECAT = "SIZECAT"; // Use 3.5 Weapon Categories
    public static final String SIZEOBJ = "SIZEOBJ"; // Use 3.0 Weapon Size
    public static final String SKILLMAX = "SKILLMAX"; // Bypass Max Skill Ranks
    public static final String SYS_35WP = "SYS_35WP"; // Apply 3.5 Size Category Penalty to Attacks
    //	String SYS_CIP				= "SYS_CIP";					// Improper tools incurs a -2 circumstance penalty
    //	String SYS_DOMAIN			= "SYS_DOMAIN";					// Apply Casterlevel Bonuses from Domains to Spells
    public static final String SYS_LDPACSK = "SYS_LDPACSK"; // Apply Load Penalty to AC and Skills
    public static final String SYS_WTPSK = "SYS_WTPSK"; // Apply Weight Penalty to Skills

    private RuleConstants()
    {
    }
}
