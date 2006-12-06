/*
 * PrerequisiteWriterTest.java
 *
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 26, 2005, 10:32 PM
 *
 * @author	Greg Bingleman <byngl@hotmail.com>
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.output.prereq;

import gmgen.pluginmgr.PluginLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests PrerequisiteWriter code
 */
public class PrerequisiteWriterTest extends TestCase
{
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(PrerequisiteWriterTest.suite());
	}

	
	/**
	 * @return Test
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		for (int i = 0; i < testparams.length >> 1; ++i)
		{
			suite.addTest(new PrerequisiteWriterTest(i));
		}
		return suite;
	}

	static String testparams[] = {
	//
	// Examples from the PCGen documentation
	//
		"PREALIGN:LG,NG,CG",															"PREMULT:1,[PREALIGN:LG],[PREALIGN:NG],[PREALIGN:CG]",
		"PREALIGN:2,5,8",																"PREMULT:1,[PREALIGN:LE],[PREALIGN:NE],[PREALIGN:CE]",
		"PREALIGN:LG,Deity",															"PREMULT:1,[PREALIGN:LG],[PREALIGN:Deity]",
		"PREAPPLY:ANYPC",																"PREAPPLY:ANYPC",
		"PREAPPLY:PC",																	"PREAPPLY:PC",
		"PREAPPLY:Ranged;Melee",														"PREAPPLY:Ranged;Melee",
		"PREAPPLY:Weapon,Blunt",														"PREAPPLY:Weapon,Blunt",
		"PREARMORPROF:1,Chainmail,Full Plate",											"PREMULT:1,[PREARMORPROF:1,Chainmail],[PREARMORPROF:1,Full Plate]",
		"PREARMORPROF:1,TYPE.Medium",													"PREARMORPROF:1,TYPE.Medium",
		"PREATT:6",																		"PREATT:6",
		"PREBASESIZELTEQ:Medium",														"PREBASESIZELTEQ:Medium",
		"PREBIRTHPLACE:Klamath",														"PREBIRTHPLACE:Klamath",
		"PRECHECK:1,Fortitude=5,Reflex=3",												"PREMULT:1,[PRECHECK:1,Fortitude=5],[PRECHECK:1,Reflex=3]",
		"PRECHECK:2,Fortitude=5,Reflex=3,Will=4",										"PREMULT:2,[PRECHECK:1,Fortitude=5],[PRECHECK:1,Reflex=3],[PRECHECK:1,Will=4]",
		"PRECHECKBASE:2,Fortitude=3,Reflex=3",											"PREMULT:2,[PRECHECKBASE:1,Fortitude=3],[PRECHECKBASE:1,Reflex=3]",
		"PRECHECKBASE:1,Fortitude=5,Reflex=3",											"PREMULT:1,[PRECHECKBASE:1,Fortitude=5],[PRECHECKBASE:1,Reflex=3]",
		"PRECITY:Klamath",																"PRECITY:Klamath",
		"PRECLASS:2,Wizard=5,Sorceror=6,Cleric=7",										"PREMULT:2,[PRECLASS:1,Wizard=5],[PRECLASS:1,Sorceror=6],[PRECLASS:1,Cleric=7]",
		"PRECLASS:1,SPELLCASTER=2",														"PRECLASS:1,SPELLCASTER=2",
		"PRECLASS:1,SPELLCASTER.Arcane=2",												"PRECLASS:1,SPELLCASTER.Arcane=2",
		"PRECLASS:2,TYPE.Base=5,TYPE.Prestige=1",										"PREMULT:2,[PRECLASS:1,TYPE.Base=5],[PRECLASS:1,TYPE.Prestige=1]",
		"PRECLASSLEVELMAX:Fighter,SPELLCASTER=2",										"PREMULT:2,[!PRECLASS:1,Fighter=3],[!PRECLASS:1,SPELLCASTER=3]",
		"PRECSKILL:1,Spot,Listen",														"PREMULT:1,[PRECSKILL:1,Spot],[PRECSKILL:1,Listen]",
		"PRECSKILL:2,TYPE.Spy",															"PRECSKILL:2,TYPE.Spy",
		"PREDEFAULTMONSTER:Y",															"PREDEFAULTMONSTER:Y",
		"PREDEITY:Y",																	"PREDEITY:1,Y",
		"PREDEITY:N",																	"PREDEITY:1,N",
		"PREDEITY:Zeus,Odin",															"PREMULT:1,[PREDEITY:1,Zeus],[PREDEITY:1,Odin]",
		"PREDEITYALIGN:0",																"PREDEITYALIGN:LG",
		"PREDEITYDOMAIN:1,Good,Law",													"PREMULT:1,[PREDEITYDOMAIN:1,Good],[PREDEITYDOMAIN:1,Law]",
		"PREDOMAIN:1,Good,Law",															"PREMULT:1,[PREDOMAIN:1,Good],[PREDOMAIN:1,Law]",
		"PREDR:1,+1=10",																"PREDR:1,+1=10",
		"PREDR:1,-=10,+1=10,+2=10,+3=10,+4=10,+5=10,Silver=10",							"PREMULT:1,[PREDR:1,-=10],[PREDR:1,+1=10],[PREDR:1,+2=10],[PREDR:1,+3=10],[PREDR:1,+4=10],[PREDR:1,+5=10],[PREDR:1,Silver=10]",
		"PREEQUIP:1,Leather Armor",														"PREEQUIP:1,Leather Armor",
		"PREEQUIP:1,Leather Armor%",													"PREEQUIP:1,Leather Armor%",
		"PREEQUIP:1,TYPE=Armor",														"PREEQUIP:1,TYPE=Armor",
		"PREEQUIP:2,TYPE=Armor,Sword (Long)%",											"PREMULT:2,[PREEQUIP:1,TYPE=Armor],[PREEQUIP:1,Sword (Long)%]",
		"PREEQUIP:2,TYPE=Armor,TYPE=Shield",											"PREMULT:2,[PREEQUIP:1,TYPE=Armor],[PREEQUIP:1,TYPE=Shield]",
		"PREEQUIPPRIMARY:1,Dagger",														"PREEQUIPPRIMARY:1,Dagger",
		"PREEQUIPPRIMARY:1,Dagger%",													"PREEQUIPPRIMARY:1,Dagger%",
		"PREEQUIPPRIMARY:1,TYPE=Slashing",												"PREEQUIPPRIMARY:1,TYPE=Slashing",
		"PREEQUIPSECONDARY:1,Dagger",													"PREEQUIPSECONDARY:1,Dagger",
		"PREEQUIPSECONDARY:1,Dagger%",													"PREEQUIPSECONDARY:1,Dagger%",
		"PREEQUIPSECONDARY:1,TYPE=Slashing",											"PREEQUIPSECONDARY:1,TYPE=Slashing",
		"PREEQUIPBOTH:1,Quarterstaff",													"PREEQUIPBOTH:1,Quarterstaff",
		"PREEQUIPBOTH:1,Sword (Great%",													"PREEQUIPBOTH:1,Sword (Great%",
		"PREEQUIPBOTH:1,TYPE=Slashing",													"PREEQUIPBOTH:1,TYPE=Slashing",
		"PREEQUIPTWOWEAPON:1,Sword (Short)",											"PREEQUIPTWOWEAPON:1,Sword (Short)",
		"PREEQUIPTWOWEAPON:1,Sword (Short%",											"PREEQUIPTWOWEAPON:1,Sword (Short%",
		"PREEQUIPTWOWEAPON:1,TYPE=Slashing",											"PREEQUIPTWOWEAPON:1,TYPE=Slashing",
		"PREFEAT:1,Dodge,Combat Reflexes",												"PREMULT:1,[PREFEAT:1,CHECKMULT,Dodge],[PREFEAT:1,CHECKMULT,Combat Reflexes]",
		"PREFEAT:2,CHECKMULT,Spell Focus",												"PREFEAT:2,CHECKMULT,Spell Focus",
//		"PREFEAT:2,CHECKMULT,Spell Focus,[Spell Focus(Enchantment)]",					"PREFEAT:2,CHECKMULT,Spell Focus\t!PREFEAT:1,CHECKMULT,Spell Focus (Enchantment)",
		"PREFEAT:2,Weapon Focus(TYPE=Bow),Weapon Focus(Longsword)",						"PREMULT:2,[PREFEAT:1,CHECKMULT,Weapon Focus (TYPE=Bow)],[PREFEAT:1,CHECKMULT,Weapon Focus (Longsword)]",
		"PREFEAT:2,CHECKMULT,Weapon Focus(TYPE=Sword)",									"PREFEAT:2,CHECKMULT,Weapon Focus (TYPE=Sword)",
		"PREFEAT:2,Skill Focus(Spot),Skill Focus(Listen),Skill Focus(Search)",			"PREMULT:2,[PREFEAT:1,CHECKMULT,Skill Focus (Spot)],[PREFEAT:1,CHECKMULT,Skill Focus (Listen)],[PREFEAT:1,CHECKMULT,Skill Focus (Search)]",
		"PREFEAT:2,TYPE=ItemCreation",													"PREFEAT:2,TYPE=ItemCreation",
		"PREGENDER:M",																	"PREGENDER:M",
		"PREHANDSGT:2",																	"PREHANDSGT:2",
		"PREITEM:1,Sword (Long),Sword (Short)",											"PREMULT:1,[PREITEM:1,Sword (Long)],[PREITEM:1,Sword (Short)]",
		"PREITEM:2,TYPE=Armor,TYPE=Armor",												"PREMULT:2,[PREITEM:1,TYPE=Armor],[PREITEM:1,TYPE=Armor]",
		"PRELANG:1,Dwarven,Elven",														"PREMULT:1,[PRELANG:1,Dwarven],[PRELANG:1,Elven]",
		"PRELANG:2,Dwarven,Elven",														"PREMULT:2,[PRELANG:1,Dwarven],[PRELANG:1,Elven]",
		"PRELANG:2,Dwarven,Elven,Halfling",												"PREMULT:2,[PRELANG:1,Dwarven],[PRELANG:1,Elven],[PRELANG:1,Halfling]",
		"PRELANG:3,ANY",																"PRELANG:3,ANY",
		"PRELEGSGTEQ:4",																"PRELEGSGTEQ:4",
		"PRELEVEL:5",																	"PRELEVEL:5",
		"PRELEVELMAX:10",																"PRELEVELMAX:10",
		"PREMOVE:Walk=30,Fly=20",														"PREMULT:1,[PREMOVE:1,Walk=30],[PREMOVE:1,Fly=20]",
		"PREMOVE:Swim=10",																"PREMOVE:1,Swim=10",
		"PREMULT:1,[PRERACE:Gnome],[PRECLASS:1,Cleric=1]",								"PREMULT:1,[PRERACE:Gnome],[PRECLASS:1,Cleric=1]",
		"PREMULT:1,[PRERACE:Gnome],[PREMULT:2,[PRESIZEGTEQ:M],[PREFEAT:1,Alertness]]",	"PREMULT:1,[PRERACE:Gnome],[PREMULT:2,[PRESIZEGTEQ:M],[PREFEAT:1,Alertness]]",
		"PRERACE:Dwarf,Elf,Human",														"PREMULT:1,[PRERACE:Dwarf],[PRERACE:Elf],[PRERACE:Human]",
		"PRERACE:Elf,[Elf (aquatic)]",													"PREMULT:2,[PRERACE:Elf],[!PRERACE:Elf (aquatic)]",
		"PREREGION:Slithe",																"PREREGION:Slithe",
		"PREREGION:Slithe (Barrows)",													"PREREGION:Slithe (Barrows)",
		"PRERULE:SYS_WTPSK",															"PRERULE:1,SYS_WTPSK",
		"PRESA:1,Turn undead,Rebuke undead,Smite Evil",									"PREMULT:1,[PRESA:1,Turn undead],[PRESA:1,Rebuke undead],[PRESA:1,Smite Evil]",
		"PRESHIELDPROF:1,Buckler,Large Shield",											"PREMULT:1,[PRESHIELDPROF:1,Buckler],[PRESHIELDPROF:1,Large Shield]",
		"PREARMORPROF:1,TYPE.Tower",													"PREARMORPROF:1,TYPE.Tower",
		"PRESIZEEQ:H",																	"PRESIZEEQ:H",
		"PRESKILL:1,Spot,Listen=10",													"PREMULT:1,[PRESKILL:1,Spot=10],[PRESKILL:1,Listen=10]",
		"PRESKILL:2,TYPE.Spy,TYPE.Spy=2",												"PREMULT:2,[PRESKILL:1,TYPE.Spy=2]",
		"PRESKILLMULT:1,Spot,Listen=10",												"PREMULT:1,[PRESKILL:1,Spot=10],[PRESKILL:1,Listen=10]",
		"PRESKILLTOT:Spot,Listen,Search=30",											"PRESKILLTOT:Spot,Listen,Search=30",
		"PRESPELL:1,Magic Missile,Lightning Bolt",										"PREMULT:1,[PRESPELL:1,Magic Missile],[PRESPELL:1,Lightning Bolt]",
		"PRESPELLBOOK:YES",																"PRESPELLBOOK:YES",
		"PRESPELLBOOK:NO",																"PRESPELLBOOK:NO",
		"PRESPELLCAST:MEMORIZE=Y",														"PRESPELLCAST:MEMORIZE=Y",
		"PRESPELLCAST:MEMORIZE=N",														"PRESPELLCAST:MEMORIZE=N",
		"PRESPELLCAST:TYPE=Arcane",														"PRESPELLCAST:TYPE=Arcane",
		"PRESPELLCAST:TYPE=Divine",														"PRESPELLCAST:TYPE=Divine",
		"PRESPELLDESCRIPTOR:Mind-Affecting,4,3",										"PRESPELLDESCRIPTOR:4,Mind-Affecting=3",
		"PRESPELLSCHOOL:Necromancy,3,2",												"PRESPELLSCHOOL:3,Necromancy=2",
		"PRESPELLSCHOOLSUB:Creation,3,2",												"PRESPELLSCHOOLSUB:3,Creation=2",
		"PRESPELLTYPE:Arcane,4,5",														"PRESPELLTYPE:4,Arcane=5",
		"PRESRGTEQ:10",																	"PRESRGTEQ:10",
		"PRESTAT:1,STR=18",																"PRESTAT:1,STR=18",
		"PRESTAT:1,STR=18,WIS=18",														"PREMULT:1,[PRESTAT:1,STR=18],[PRESTAT:1,WIS=18]",
		"PRESTAT:2,STR=18,WIS=18",														"PREMULT:2,[PRESTAT:1,STR=18],[PRESTAT:1,WIS=18]",
		"PRESTAT:1,STR=15,WIS=13",														"PREMULT:1,[PRESTAT:1,STR=15],[PRESTAT:1,WIS=13]",
		"PRESTAT:2,STR=13,INT=10,CHA=13",												"PREMULT:2,[PRESTAT:1,STR=13],[PRESTAT:1,INT=10],[PRESTAT:1,CHA=13]",
		"PRESUBCLASS:1,Evoker,Abjurer,Enchanter,Illusionist",							"PREMULT:1,[PRESUBCLASS:1,Evoker],[PRESUBCLASS:1,Abjurer],[PRESUBCLASS:1,Enchanter],[PRESUBCLASS:1,Illusionist]",
		"PRETEMPLATE:Celestial,Fiendish",												"PREMULT:1,[PRETEMPLATE:1,Celestial],[PRETEMPLATE:1,Fiendish]",
		"PRETEXT:Character must make a sacrifice of bananas to the Monkey God",			"PRETEXT:Character must make a sacrifice of bananas to the Monkey God",
		"PRETYPE:1,Elemental,Fey,Outsider",												"PREMULT:1,[PRETYPE:1,Elemental],[PRETYPE:1,Fey],[PRETYPE:1,Outsider]",
		"PRETYPE:2,Humanoid,Undead",													"PREMULT:2,[PRETYPE:1,Humanoid],[PRETYPE:1,Undead]",
		"PREUATT:4",																	"PREUATT:4",
		"PREVARGT:Rage,4",																"PREVARGT:Rage,4",
		"PREVARGT:SneakAttack,5",														"PREVARGT:SneakAttack,5",
		"PREVARGT:SneakAttack,5,Rage,4",												"PREMULT:2,[PREVARGT:SneakAttack,5],[PREVARGT:Rage,4]",
		"PREVISION:2,Normal,Darkvision",												"PREMULT:2,[PREVISION:1,Normal=1],[PREVISION:1,Darkvision=1]",
		"PREVISION:1,Blindsight,Darkvision=30",											"PREMULT:1,[PREVISION:1,Blindsight=30],[PREVISION:1,Darkvision=30]",
		"PREWEAPONPROF:2,Kama,Katana",													"PREMULT:2,[PREWEAPONPROF:1,Kama],[PREWEAPONPROF:1,Katana]",
		"PREWEAPONPROF:1,TYPE.Exotic",													"PREWEAPONPROF:1,TYPE.Exotic",
		"PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)",									"PREMULT:1,[PREWEAPONPROF:1,TYPE.Martial],[PREWEAPONPROF:1,Chain (Spiked)]",
		"PREWEAPONPROF:1,DEITYWEAPON",													"PREWEAPONPROF:1,DEITYWEAPON",
		"PREWIELD:1,Light,OneHanded",													"PREMULT:1,[PREWIELD:1,Light],[PREWIELD:1,OneHanded]",
		"PREWIELD:1,TwoHanded",															"PREWIELD:1,TwoHanded",

		"PRERACE:Orc,[%]",																"PREMULT:2,[PRERACE:Orc],[!PRERACE:%]",

//
// Not in the docs
		"PREARMORTYPE:1,TYPE.Exotic",													"PREARMORTYPE:1,TYPE.Exotic",
		"PREARMORTYPE:1,Leather%",														"PREARMORTYPE:1,Leather%",
		"PREARMORTYPE:1,LIST",															"PREARMORTYPE:1,LIST",
		"PREHD:3+",																		"PREHD:3+",
		"PREHD:3-12",																	"PREHD:3-12",
		"PREHP:12",																		"PREHP:12",
		"PREPOINTBUYMETHOD:Standard",													"PREPOINTBUYMETHOD:Standard",
		"PREPOINTBUYMETHOD:Standard,High-powered",										"PREPOINTBUYMETHOD:Standard,High-powered",

//
// Just for good measure
//
		"PREMULT:1,[PREFEAT:1,CHECKMULT,Dodge],[PREFEAT:1,CHECKMULT,Combat Reflexes]",	"PREMULT:1,[PREFEAT:1,CHECKMULT,Dodge],[PREFEAT:1,CHECKMULT,Combat Reflexes]",
		"PRERACE:Dwarf",																"PRERACE:Dwarf",
		"PRESPELLDESCRIPTOR:4,Mind-Affecting=3",										"PRESPELLDESCRIPTOR:4,Mind-Affecting=3",
		"PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",									"PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",
		"PRESPELLSCHOOL:3,Necromancy=2",												"PRESPELLSCHOOL:3,Necromancy=2",
		"PRESPELLSCHOOL:3,Necromancy=2,Divination=4",									"PRESPELLSCHOOL:3,Necromancy=2,Divination=4",
		"PRESPELLSCHOOLSUB:3,Creation=2",												"PRESPELLSCHOOLSUB:3,Creation=2",
		"PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",									"PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",
		"PRESPELLTYPE:4,Arcane=5",														"PRESPELLTYPE:4,Arcane=5",
		"PRESPELLTYPE:4,Arcane=5,Divine=2",												"PRESPELLTYPE:4,Arcane=5,Divine=2",
		"PRESKILLTOT:TYPE.Knowledge=20",												"PRESKILLTOT:TYPE.Knowledge=20",
		"PRESTATEQ:1,STR=18",															"PRESTATEQ:1,STR=18",
		"PRESTATNEQ:1,STR=18",															"PRESTATNEQ:1,STR=18",
		"PRESTATGTEQ:1,STR=18",															"PRESTAT:1,STR=18",
		"PRESTATLT:1,STR=18",															"PRESTATLT:1,STR=18",
		"PRESTATLTEQ:1,STR=18",															"PRESTATLTEQ:1,STR=18",
		"PRESTATGT:1,STR=18",															"PRESTATGT:1,STR=18",
		"PRECSKILL:1,Craft (Basketweaving)",											"PRECSKILL:1,Craft (Basketweaving)",
		"!PRECSKILL:1,Craft (Basketweaving)",											"!PRECSKILL:1,Craft (Basketweaving)",
//
// Test default logic cases
//
		"PREBASESIZE:Medium",															"PREBASESIZEGTEQ:Medium",
		"PREHANDS:2",																	"PREHANDSGTEQ:2",
		"PRELEGS:4",																	"PRELEGSGTEQ:4",
		"PRESIZE:H",																	"PRESIZEGTEQ:H",
		"PRESR:10",																		"PRESRGTEQ:10",
		"PREVAR:Rage,4",																"PREVARGTEQ:Rage,4",

//
//
// Invert to test the negative logic
//
		"!PRESPELLCAST:MEMORIZE=Y",														"!PRESPELLCAST:MEMORIZE=Y",
		"!PRESPELLCAST:MEMORIZE=N",														"!PRESPELLCAST:MEMORIZE=N",
		"!PRESPELLCAST:TYPE=Arcane",													"!PRESPELLCAST:TYPE=Arcane",
		"!PRESPELLCAST:TYPE=Divine",													"!PRESPELLCAST:TYPE=Divine",
		"!PRECSKILL:1,Spot,Listen",														"!PREMULT:1,[PRECSKILL:1,Spot],[PRECSKILL:1,Listen]",
		"!PREBASESIZELTEQ:Medium",														"PREBASESIZEGT:Medium",
		"!PREHANDSGT:2",																"PREHANDSLTEQ:2",
		"!PRELEGSGTEQ:4",																"PRELEGSLT:4",
		"!PRESIZEEQ:H",																	"PRESIZENEQ:H",
		"!PRESRGTEQ:10",																"PRESRLT:10",
		"!PREVARGT:Rage,4",																"PREVARLTEQ:Rage,4",
		"!PREALIGN:8",																	"!PREALIGN:CE",
		"!PREARMORPROF:1,TYPE.Medium",													"!PREARMORPROF:1,TYPE.Medium",
		"!PREARMORTYPE:1,TYPE.Exotic",													"!PREARMORTYPE:1,TYPE.Exotic",
		"!PREATT:6",																	"!PREATT:6",
		"!PREBIRTHPLACE:Klamath",														"!PREBIRTHPLACE:Klamath",
		"!PRECITY:Klamath",																"!PRECITY:Klamath",
		"!PREDEFAULTMONSTER:Y",															"!PREDEFAULTMONSTER:Y",
		"!PREDEITYALIGN:0",																"!PREDEITYALIGN:LG",
		"!PREDEITYDOMAIN:1,Good",														"!PREDEITYDOMAIN:1,Good",
		"!PREDEITY:Odin",																"!PREDEITY:1,Odin",
		"!PREEQUIP:1,Leather Armor",													"!PREEQUIP:1,Leather Armor",
		"!PREEQUIPBOTH:1,Quarterstaff",													"!PREEQUIPBOTH:1,Quarterstaff",
		"!PREEQUIPPRIMARY:1,Dagger",													"!PREEQUIPPRIMARY:1,Dagger",
		"!PREEQUIPSECONDARY:1,Dagger",													"!PREEQUIPSECONDARY:1,Dagger",
		"!PREEQUIPTWOWEAPON:1,Sword (Short)",											"!PREEQUIPTWOWEAPON:1,Sword (Short)",
		"!PREGENDER:M",																	"!PREGENDER:M",
		"!PREHP:12",																	"!PREHP:12",
		"!PREDEITY:Y",																	"!PREDEITY:1,Y",
		"!PREITEM:1,Sword (Long)",														"!PREITEM:1,Sword (Long)",
		"!PRELEVELMAX:10",																"!PRELEVELMAX:10",
		"!PRELEVEL:5",																	"!PRELEVEL:5",
		"!PREREGION:Slithe",															"!PREREGION:Slithe",
		"!PRERULE:SYS_WTPSK",															"!PRERULE:1,SYS_WTPSK",
		"!PRESHIELDPROF:1,Buckler",														"!PRESHIELDPROF:1,Buckler",
		"!PRESA:1,Turn undead",															"!PRESA:1,Turn undead",
		"!PRESPELLBOOK:YES",															"!PRESPELLBOOK:YES",
		"!PRESPELL:1,Magic Missile",													"!PRESPELL:1,Magic Missile",
		"!PRESUBCLASS:1,Evoker",														"!PRESUBCLASS:1,Evoker",
		"!PRETEMPLATE:Celestial",														"!PRETEMPLATE:1,Celestial",
		"!PREUATT:4",																	"!PREUATT:4",
		"!PREWEAPONPROF:1,TYPE.Exotic",													"!PREWEAPONPROF:1,TYPE.Exotic",
		"!PREWIELD:1,TwoHanded",														"!PREWIELD:1,TwoHanded",
		"!PRECHECKBASE:1,Fortitude=5",													"!PRECHECKBASE:1,Fortitude=5",
		"!PRECHECK:1,Fortitude=5",														"!PRECHECK:1,Fortitude=5",
		"!PREDOMAIN:1,Travel",															"!PREDOMAIN:1,Travel",
		"!PREDR:1,+1=10",																"!PREDR:1,+1=10",
		"!PREMOVE:Swim=10",																"!PREMOVE:1,Swim=10",
		"!PRESTAT:1,STR=18",															"PRESTATLT:1,STR=18",
		"!PRESTATEQ:1,STR=18",															"PRESTATNEQ:1,STR=18",
		"!PRESTATNEQ:1,STR=18",															"PRESTATEQ:1,STR=18",
		"!PRESTATGTEQ:1,STR=18",														"PRESTATLT:1,STR=18",
		"!PRESTATLT:1,STR=18",															"PRESTAT:1,STR=18",
		"!PRESTATLTEQ:1,STR=18",														"PRESTATGT:1,STR=18",
		"!PRESTATGT:1,STR=18",															"PRESTATLTEQ:1,STR=18",
		"!PREVISION:1,Blindsight",														"!PREVISION:1,Blindsight=1",
		"!PREPOINTBUYMETHOD:Standard",													"!PREPOINTBUYMETHOD:Standard",
		"!PRESKILLTOT:Spot,Listen,Search=30",											"!PRESKILLTOT:Spot,Listen,Search=30",
		"!PREFEAT:1,Dodge",																"!PREFEAT:1,Dodge",
		"!PRESPELLDESCRIPTOR:Mind-Affecting,4,3",										"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3",
		"!PRESPELLSCHOOLSUB:Creation,3,2",												"!PRESPELLSCHOOLSUB:3,Creation=2",
		"!PRESPELLSCHOOL:Necromancy,3,2",												"!PRESPELLSCHOOL:3,Necromancy=2",
		"!PRESPELLTYPE:Arcane,4,5",														"!PRESPELLTYPE:4,Arcane=5",
		"!PREHD:3+",																	"!PREHD:3+",
		"!PREHD:3-12",																	"!PREHD:3-12",
		"!PRECLASS:1,SPELLCASTER=2",													"!PRECLASS:1,SPELLCASTER=2",
		"!PREAPPLY:ANYPC",																"!PREAPPLY:ANYPC",
		"!PRESPELLTYPE:4,Arcane=5,Divine=2",											"!PRESPELLTYPE:4,Arcane=5,Divine=2",
		"!PRESPELLSCHOOL:3,Necromancy=2,Divination=4",									"!PRESPELLSCHOOL:3,Necromancy=2,Divination=4",
		"!PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",									"!PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",
		"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",								"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",
		"!PRESKILLTOT:TYPE.Knowledge=20",												"!PRESKILLTOT:TYPE.Knowledge=20",
		"!PREPOINTBUYMETHOD:Standard,High-powered",										"!PREPOINTBUYMETHOD:Standard,High-powered",

//
		"PREALIGN:8",																	"PREALIGN:CE",
//		"PREBASESIZE:Medium",															"PREBASESIZEGTEQ:Medium",
		"PREBASESIZEEQ:Medium",															"PREBASESIZEEQ:Medium",
		"PREBASESIZEGT:Medium",															"PREBASESIZEGT:Medium",
		"PREBASESIZEGTEQ:Medium",														"PREBASESIZEGTEQ:Medium",
		"PREBASESIZELT:Medium",															"PREBASESIZELT:Medium",
		"PREBASESIZENEQ:Medium",														"PREBASESIZENEQ:Medium",
		"PRECHECK:1,Fortitude=3",														"PRECHECK:1,Fortitude=3",
		"PRECHECKBASE:1,Will=3",														"PRECHECKBASE:1,Will=3",
		"PRECLASSLEVELMAX:Barbarian=1",													"!PRECLASS:1,Barbarian=2",
		"!PRECLASSLEVELMAX:Barbarian=1",												"PRECLASS:1,Barbarian=2",
		"PRECLASSLEVELMAX:Rogue=5,Fighter,SPELLCASTER=2,Monk=3",						"PREMULT:4,[!PRECLASS:1,Rogue=6],[!PRECLASS:1,Fighter=3],[!PRECLASS:1,SPELLCASTER=3],[!PRECLASS:1,Monk=4]",
		"PRECLASSLEVELMAX:1,Cleric=1,Fighter=1,Monk=1,Rogue=1",							"PREMULT:1,[!PRECLASS:1,Cleric=2],[!PRECLASS:1,Fighter=2],[!PRECLASS:1,Monk=2],[!PRECLASS:1,Rogue=2]",
		"!PRECLASS:1,Battlemind=7",														"!PRECLASS:1,Battlemind=7",
		"PRECSKILL:1,Swim",																"PRECSKILL:1,Swim",
		"PREDR:1,Silver.10",															"PREDR:1,Silver=10",
		"PREDR:1,Silver=10",															"PREDR:1,Silver=10",
		"PREDR:1,-=10,Cold Iron=10,+1=10",												"PREMULT:1,[PREDR:1,-=10],[PREDR:1,Cold Iron=10],[PREDR:1,+1=10]",
		"PREDEFAULTMONSTER:N",															"PREDEFAULTMONSTER:N",
		"PREDEITY:Thor",																"PREDEITY:1,Thor",
		"PREDEITYALIGN:2,5,8",															"PREMULT:1,[PREDEITYALIGN:LE],[PREDEITYALIGN:NE],[PREDEITYALIGN:CE]",
		"PREDEITYDOMAIN:1,Travel",														"PREDEITYDOMAIN:1,Travel",
		"PREDOMAIN:1,Sex,Travel",														"PREMULT:1,[PREDOMAIN:1,Sex],[PREDOMAIN:1,Travel]",
		"PREDOMAIN:1,Travel",															"PREDOMAIN:1,Travel",

		"PRERULE:SYS_DOMAIN",															"PRERULE:1,SYS_DOMAIN",
		"PRESKILL:1,Knowledge (History)=1",												"PRESKILL:1,Knowledge (History)=1",
		"PRESKILL:3,TYPE.Knowledge=2",													"PREMULT:3,[PRESKILL:1,TYPE.Knowledge=2]",
		"!PRESKILL:3,TYPE.Knowledge=2",													"!PREMULT:3,[PRESKILL:1,TYPE.Knowledge=2]",
		"PREVARGTEQ:BarbRagePowerLVL,11",												"PREVARGTEQ:BarbRagePowerLVL,11",
		"PREVAREQ:TL,CL",																"PREVAREQ:TL,CL",
		"PREVAREQ:TL,(CL=Barbarian+CL=Ex Barbarian)",									"PREVAREQ:TL,(CL=Barbarian+CL=Ex Barbarian)",
		"PREVARLT:ENCUMBERANCE,2",														"PREVARLT:ENCUMBERANCE,2",
		"PRETYPE:type1,type2|type3,[type4]",											"PREMULT:3,[PRETYPE:1,type1],[PREMULT:1,[PRETYPE:1,type2],[PRETYPE:1,type3]],[!PRETYPE:1,type4]",
		"PREMULT:3,[PRETYPE:1,type1],[PRETYPE:1,type2,type3],[!PRETYPE:1,type4]",		"PREMULT:3,[PRETYPE:1,type1],[PREMULT:1,[PRETYPE:1,type2],[PRETYPE:1,type3]],[!PRETYPE:1,type4]",
		"PRETYPE:1,Animal",																"PRETYPE:1,Animal",
		"!PRETYPE:1,Animal",															"!PRETYPE:1,Animal",
	// From PrerequisiteApplyWriterTest.java
		"!PREAPPLY:Ranged;Melee",														"!PREAPPLY:Ranged;Melee",
		"PREAPPLY:Wooden,Blunt",														"PREAPPLY:Wooden,Blunt",
		"!PREAPPLY:Wooden,Blunt",														"!PREAPPLY:Wooden,Blunt",
		"!PREAPPLY:ANYPC",																"!PREAPPLY:ANYPC",
	// From PrerequisiteLanguageWriterTest.java
		"!PRELANG:1,Dwarven,Elven",														"!PREMULT:1,[PRELANG:1,Dwarven],[PRELANG:1,Elven]",
		"PRELANG:2,Any",																"PRELANG:2,Any",
		"!PRELANG:2,Any",																"!PRELANG:2,Any",
	// From PrerequisiteSkillWriterTest.java
		"PRESKILL:1,Ride=10",															"PRESKILL:1,Ride=10",
		"!PRESKILL:1,Ride=10",															"!PRESKILL:1,Ride=10",
		"PRESKILL:1,Ride=10,Listen=5",													"PREMULT:1,[PRESKILL:1,Ride=10],[PRESKILL:1,Listen=5]",
		"PRESKILL:2,Ride=10,Listen=5",													"PREMULT:2,[PRESKILL:1,Ride=10],[PRESKILL:1,Listen=5]",
		"!PRESKILL:2,Ride=10,Listen=5",													"!PREMULT:2,[PRESKILL:1,Ride=10],[PRESKILL:1,Listen=5]",
		"PRESKILLTOT:Ride,Listen=20",													"PRESKILLTOT:Ride,Listen=20",
		"!PRESKILLTOT:Ride,Listen=20",													"!PRESKILLTOT:Ride,Listen=20",

	//
	// Other oddballs
	//
		"PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",			"PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",
		"!PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",			"!PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",
		"PRECLASSLEVELMAX:Fighter=VARNAME",												"!PRECLASS:1,Fighter=(VARNAME)+1",
		"!PRETEXT:Character must make a sacrifice of bananas to the Monkey God",		"!PRETEXT:Character must make a sacrifice of bananas to the Monkey God",
		"PREWEAPONPROF:2,TYPE.Exotic,[Chain (Spiked)]",									"PREMULT:2,[PREWEAPONPROF:1,TYPE.Exotic],[!PREWEAPONPROF:1,Chain (Spiked)]",
		"PREWEAPONPROF:1,TYPE.Exotic,[Chain (Spiked)]",									"PREMULT:1,[PREWEAPONPROF:1,TYPE.Exotic],[!PREWEAPONPROF:1,Chain (Spiked)]",
		"PREWEAPONPROF:1,[Chain (Spiked)]",												"!PREWEAPONPROF:1,Chain (Spiked)",
		"PREATT:should_be_numeric",														"PREATT:1",
		"PRESTAT:should_be_numeric,STR=18",												"PRESTAT:1,STR=18",
		"PRESTAT:1,Strength=18",														"PRESTAT:1,STR=18",

	//
	// To cause exceptions
		"PREDR:1,+1=should_be_numeric",													"",
		"PRESPELLTYPE:Arcane,4,5,2",													"",		// too many tokens
		"PRESPELLTYPE:4,Arcane",														"",		// missing '='
		"PRESPELLTYPE:4",																"",		// missing tokens
		"PRESPELLSCHOOLSUB:Creation,3,2,2",												"",		// too many tokens
		"PRESPELLSCHOOLSUB:3,Creation",													"",		// missing '='
		"PRESPELLSCHOOLSUB:3",															"",		// missing tokens
		"PRESPELLSCHOOL:Necromancy,3,2,2",												"",		// too many tokens
		"PRESPELLSCHOOL:3,Necromancy",													"",		// missing '='
		"PRESPELLSCHOOL:3",																"",		// missing tokens
		"PRESPELLDESCRIPTOR:Mind-Affecting,4,3,2",										"",		// too many tokens
		"PRESPELLDESCRIPTOR:4,Mind-Affecting",											"",		// missing '='
		"PRESPELLDESCRIPTOR:4",															"",		// missing tokens
		"PREHD:3-12+3",																	"",		// too many tokens
		"PREHD:should_be_numeric",														"",		// too many tokens
		"PREVARGT:SneakAttack,Rage,4",													"",		// invalid # of tokens
		"PRESTAT:1,S=18",																"",		// need 3 character attribute
		"PRESTAT:1,STR",																"",		// missing '='
		"PRESTAT:1,ST",																	"",		// need at least 3 characters
		"PRESTAT",																		"",		// missing ':'
		"PREHP:should_be_numeric",														"",
		"PRESRGTEQ:should_be_numeric",													"",
		"PREDUMMYKIND:1,arg",															""
	};

	private String preString = "";
	private String postString = "";

	private PrerequisiteWriterTest(final int idx)
	{
		super("Test_" + testparams[idx << 1]);
		preString  = testparams[idx << 1];
		postString = testparams[(idx << 1) + 1];
	}


    protected void runTest() throws Throwable
    {
    	setName(preString);
		PreTest(preString, postString);
    }


    protected void setUp() throws Exception
    {
		try {
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		}
		catch(Exception e) {
			// TODO Handle Exception
		}
        Globals.setUseGUI(false);
        Globals.emptyLists();
        GameMode gamemode = new GameMode("3.5");
        gamemode.addToAlignmentList(createAlignment("Lawful Good", "LG"));
        gamemode.addToAlignmentList(createAlignment("Lawful Neutral", "LN"));
        gamemode.addToAlignmentList(createAlignment("Lawful Evil", "LE"));
        gamemode.addToAlignmentList(createAlignment("Neutral Good", "NG"));
        gamemode.addToAlignmentList(createAlignment("True Neutral", "TN"));
        gamemode.addToAlignmentList(createAlignment("Neutral Evil", "NE"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Good", "CG"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Neutral", "CN"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Evil", "CE"));
        gamemode.addToAlignmentList(createAlignment("None", "NONE"));
        gamemode.addToAlignmentList(createAlignment("Deity's", "Deity"));
        SystemCollections.addToGameModeList(gamemode);
        SettingsHandler.setGame("3.5");
    }

    private PCAlignment createAlignment(String longName, String shortName)
    {
        PCAlignment align = new PCAlignment();
        align.setName(longName);
        align.setKeyName(shortName);
        return align;
    }


	/**
	 * @param aPreString
	 * @param expectedOutput
	 */
	private void PreTest(final String aPreString, final String expectedOutput)
	{
		Prerequisite prereq = null;
		boolean bExceptionThrown = false;
		boolean bExceptionExpected = (expectedOutput.length() == 0);
		try
		{
			prereq = PreParserFactory.getInstance().parse(aPreString);
		}
		catch (PersistenceLayerException ple)
		{
			if (!bExceptionExpected)
			{
				ple.printStackTrace();
				fail("parse caused PersistenceLayerException: " + ple.toString());
			}
			bExceptionThrown = true;
		}

		//
		// Did we expect an exception to be thrown?
		//
		if (bExceptionExpected)
		{
			if (!bExceptionThrown)
			{
				if (prereq != null)
				{
					System.out.println(prereq.toString());
				}
				fail("exception expected but not thrown");
			}
			return;
		}



		if (prereq == null)
		{
			fail("Could not parse prereq: '" + aPreString + "'");
		}

		StringWriter sw = new StringWriter();
		PrerequisiteWriter writer = new PrerequisiteWriter();
		try
		{
			writer.write(sw, prereq);
		}
		catch (PersistenceLayerException ple)
		{
			fail("write caused PersistenceLayerException: " + ple.toString());
		}
		final String writerOutput = sw.toString();
		System.out.println("'" + aPreString + "' returned '" + writerOutput + "'");
		assertTrue(aPreString +
						" returned '" +
						writerOutput +
						"' (expected '" +
						expectedOutput +
						"'). " +
						prereq.toString(),
					expectedOutput.equals(writerOutput));

		//
		// Test .lst output
		//
		pcgen.core.PObject pobj = new pcgen.core.PObject();
		pobj.addPreReq(prereq);
		assertTrue("PrerequisiteWriter.prereqsToString failure", PrerequisiteWriter.prereqsToString(pobj).equals("\t" + expectedOutput));

		try
		{
			writer.write(new myWriter(), prereq);
		}
		catch (PersistenceLayerException ple)
		{
			// expect a PersistenceLayerException as passed custom writer that throws IOExceptions
			return;
		}
		fail("test writer failed to throw PersistenceLayerException");
	}

	private class myWriter extends Writer
	{
		public void flush() throws IOException
		{
			throwException();
		}

		public void close() throws IOException
		{
			throwException();
		}

		public void write(char[] cbuf, int off, int len) throws IOException
		{
			throwException();
		}

		public void write(int c) throws IOException
		{
			throwException();
		}

		public void write(String str) throws IOException
		{
			throwException();
		}

		public void write(String str, int off, int len) throws IOException
		{
			throwException();
		}

		private void throwException() throws IOException
		{
			throw new IOException("intentionally generated exception");
		}


	}


}
