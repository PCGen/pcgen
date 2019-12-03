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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst.output.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Alignment;

/**
 * Tests PrerequisiteWriter code
 * TODO: convert to ParamertizedTest
 */
class PrerequisiteWriterTest
{
	private static final FormatManager<String> STR_MGR = new StringManager();

	private static final String[] testparams = {
	//
	// Examples from the PCGen documentation - note that there are two columns for each entry below
	//
"PREABILITY:1,CATEGORY=Special Attack,Sneak Attack",							"PREABILITY:1,CATEGORY=Special Attack,Sneak Attack",
"PREALIGN:LG,NG,CG",															"PREALIGN:LG,NG,CG",
"PREALIGN:LG,Deity",															"PREALIGN:LG,Deity",
"PREPROFWITHARMOR:1,Chainmail,Full Plate",										"PREPROFWITHARMOR:1,Chainmail,Full Plate",
"PREPROFWITHARMOR:1,TYPE.Medium",												"PREPROFWITHARMOR:1,TYPE.Medium",
"PREATT:6",																		"PREATT:6",
"PREBASESIZELTEQ:Medium",														"PREBASESIZELTEQ:M",
"PREBIRTHPLACE:Klamath",														"PREBIRTHPLACE:Klamath",
"PRECHECK:1,Fortitude=5,Reflex=3",												"PRECHECK:1,Fortitude=5,Reflex=3",
"PRECHECK:2,Fortitude=5,Reflex=3,Will=4",										"PRECHECK:2,Fortitude=5,Reflex=3,Will=4",
"PRECHECKBASE:2,Fortitude=3,Reflex=3",											"PRECHECKBASE:2,Fortitude=3,Reflex=3",
"PRECHECKBASE:1,Fortitude=5,Reflex=3",											"PRECHECKBASE:1,Fortitude=5,Reflex=3",
"PRECITY:Klamath",																"PRECITY:Klamath",
"PRECLASS:2,Wizard=5,Sorceror=6,Cleric=7",										"PRECLASS:2,Wizard=5,Sorceror=6,Cleric=7",
"PRECLASS:1,SPELLCASTER=2",														"PRECLASS:1,SPELLCASTER=2",
"PRECLASS:1,SPELLCASTER.Arcane=2",												"PRECLASS:1,SPELLCASTER.Arcane=2",
"PRECLASS:2,TYPE.Base=5,TYPE.Prestige=1",										"PRECLASS:2,TYPE.Base=5,TYPE.Prestige=1",
"PRECLASSLEVELMAX:2,Fighter,SPELLCASTER=2",										"!PRECLASS:1,Fighter=3,SPELLCASTER=3",
"PRECSKILL:1,Spot,Listen",														"PRECSKILL:1,Spot,Listen",
"PRECSKILL:2,TYPE.Spy",															"PRECSKILL:2,TYPE.Spy",
"PREDEITY:1,Y",																	"PREDEITY:1,Y",
"PREDEITY:1,N",																	"PREDEITY:1,N",
"PREDEITY:1,Zeus,Odin",															"PREDEITY:1,Zeus,Odin",
"PREDEITYDOMAIN:1,Good,Law",													"PREDEITYDOMAIN:1,Good,Law",
"PREDOMAIN:1,Good,Law",															"PREDOMAIN:1,Good,Law",
"PREDR:1,+1=10",																"PREDR:1,+1=10",
"PREDR:1,-=10,+1=10,+2=10,+3=10,+4=10,+5=10,Silver=10",							"PREDR:1,-=10,+1=10,+2=10,+3=10,+4=10,+5=10,Silver=10",
"PREEQUIP:1,Leather Armor",														"PREEQUIP:1,Leather Armor",
"PREEQUIP:1,Leather Armor%",													"PREEQUIP:1,Leather Armor%",
"PREEQUIP:1,TYPE=Armor",														"PREEQUIP:1,TYPE=Armor",
"PREEQUIP:2,TYPE=Armor,Sword (Long)%",											"PREEQUIP:2,TYPE=Armor,Sword (Long)%",
"PREEQUIP:2,TYPE=Armor,TYPE=Shield",											"PREEQUIP:2,TYPE=Armor,TYPE=Shield",
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
"PREFACT:1,RACE,IsPC=TRUE",														"PREFACT:1,RACE,IsPC=TRUE",
"PREFACTSET:1,DEITY,PANTHEONS=Greek",											"PREFACTSET:1,DEITY,PANTHEONS=Greek",
"PREFEAT:1,Dodge,Combat Reflexes",												"PREABILITY:1,CATEGORY=FEAT,Dodge,Combat Reflexes",
"PREFEAT:2,CHECKMULT,Spell Focus",												"PREABILITY:2,CHECKMULT,CATEGORY=FEAT,Spell Focus",
//"PREFEAT:2,CHECKMULT,Spell Focus,[Spell Focus(Enchantment)]",					"PREABILITY:2,CATEGORY=FEAT,CHECKMULT,Spell Focus\t!PREABILITY:1,CATEGORY=FEAT,CHECKMULT,Spell Focus (Enchantment)",
"PREFEAT:2,Weapon Focus (TYPE=Bow),Weapon Focus (Longsword)",					"PREABILITY:2,CATEGORY=FEAT,Weapon Focus (TYPE=Bow),Weapon Focus (Longsword)",
"PREFEAT:2,CHECKMULT,Weapon Focus(TYPE=Sword)",									"PREABILITY:2,CHECKMULT,CATEGORY=FEAT,Weapon Focus (TYPE=Sword)",
"PREFEAT:2,Skill Focus (Spot),Skill Focus (Listen),Skill Focus (Search)",		"PREABILITY:2,CATEGORY=FEAT,Skill Focus (Spot),Skill Focus (Listen),Skill Focus (Search)",
"PREFEAT:2,TYPE=ItemCreation",													"PREABILITY:2,CATEGORY=FEAT,TYPE=ItemCreation",
"PREGENDER:M",																	"PREGENDER:M",
"PREHANDSGT:2",																	"PREHANDSGT:2",
"PREITEM:1,Sword (Long),Sword (Short)",											"PREITEM:1,Sword (Long),Sword (Short)",
"PREITEM:2,TYPE=Armor,TYPE=Armor",												"PREITEM:2,TYPE=Armor,TYPE=Armor",
"PREKIT:1,Starting Gold",														"PREKIT:1,Starting Gold",
"PREKIT:2,Flumph Abilities,Flumph Skills",										"PREKIT:2,Flumph Abilities,Flumph Skills",
"!PREKIT:1,Alchemist's Kit",													"!PREKIT:1,Alchemist's Kit",
"PRELANG:1,Dwarven,Elven",														"PRELANG:1,Dwarven,Elven",
"PRELANG:2,Dwarven,Elven",														"PRELANG:2,Dwarven,Elven",
"PRELANG:2,Dwarven,Elven,Halfling",												"PRELANG:2,Dwarven,Elven,Halfling",
"PRELANG:3,ANY",																"PRELANG:3,ANY",
"PRELEGSGTEQ:4",																"PRELEGSGTEQ:4",
"PRELEVEL:MIN=5",																"PRELEVEL:MIN=5",
"PRELEVEL:MAX=5",																"PRELEVEL:MAX=5",
"PRELEVEL:MIN=4,MAX=5",															"PRELEVEL:MIN=4,MAX=5",
"PRELEVELMAX:10",																"PRELEVELMAX:10",

"PREPCLEVEL:MIN=5",																"PREPCLEVEL:MIN=5",
"PREPCLEVEL:MAX=5",																"PREPCLEVEL:MAX=5",
"PREPCLEVEL:MIN=4,MAX=5",														"PREPCLEVEL:MIN=4,MAX=5",
		
"PREMOVE:1,Walk=30,Fly=20",														"PREMOVE:1,Walk=30,Fly=20",
"PREMOVE:1,Swim=10",																"PREMOVE:1,Swim=10",
"PREMULT:1,[PRERACE:1,Gnome],[PRECLASS:1,Cleric=1]",								"PREMULT:1,[PRERACE:1,Gnome],[PRECLASS:1,Cleric=1]",
"PREMULT:1,[PRERACE:1,Gnome],[PREMULT:2,[PRESIZEGTEQ:M],[PREFEAT:1,Alertness]]",	"PREMULT:1,[PRERACE:1,Gnome],[PREMULT:2,[PRESIZEGTEQ:M],[PREABILITY:1,CATEGORY=FEAT,Alertness]]",
"PRERACE:1,Dwarf,Elf,Human",														"PRERACE:1,Dwarf,Elf,Human",
"PRERACE:1,Elf,[Elf (aquatic)]",													"PREMULT:2,[PRERACE:1,Elf],[!PRERACE:1,Elf (aquatic)]",
"PREREGION:Slithe",																"PREREGION:Slithe",
"PREREGION:Slithe (Barrows)",													"PREREGION:Slithe (Barrows)",
"PRERULE:1,SYS_WTPSK",															"PRERULE:1,SYS_WTPSK",
"PRESA:1,Turn undead,Rebuke undead,Smite Evil",									"PRESA:1,Turn undead,Rebuke undead,Smite Evil",
"PREPROFWITHSHIELD:1,Buckler,Large Shield",										"PREPROFWITHSHIELD:1,Buckler,Large Shield",
"PREPROFWITHARMOR:1,TYPE.Tower",												"PREPROFWITHARMOR:1,TYPE.Tower",
"PRESIZEEQ:H",																	"PRESIZEEQ:H",
"PRESKILL:1,Spot=10,Listen=10",													"PRESKILL:1,Spot=10,Listen=10",
"PRESKILL:2,TYPE.Spy=2",														"PRESKILL:2,TYPE.Spy=2",
"PRESKILLMULT:1,Spot=10,Listen=10",												"PRESKILLMULT:1,Spot=10,Listen=10",
"PRESKILLTOT:Spot,Listen,Search=30",											"PRESKILLTOT:Spot,Listen,Search=30",
"PRESPELL:1,Magic Missile,Lightning Bolt",										"PRESPELL:1,Magic Missile,Lightning Bolt",
"PRESPELLBOOK:YES",																"PRESPELLBOOK:YES",
"PRESPELLBOOK:NO",																"PRESPELLBOOK:NO",
"PRESPELLCAST:MEMORIZE=Y",														"PRESPELLCAST:MEMORIZE=Y",
"PRESPELLCAST:MEMORIZE=N",														"PRESPELLCAST:MEMORIZE=N",
"PRESPELLCAST:TYPE=Arcane",														"PRESPELLCAST:TYPE=Arcane",
"PRESPELLCAST:TYPE=Divine",														"PRESPELLCAST:TYPE=Divine",
"PRESPELLDESCRIPTOR:4,Mind-Affecting=3",										"PRESPELLDESCRIPTOR:4,Mind-Affecting=3",
"PRESPELLSCHOOL:3,Necromancy=2",												"PRESPELLSCHOOL:3,Necromancy=2",
"PRESPELLSCHOOLSUB:3,Creation=2",												"PRESPELLSCHOOLSUB:3,Creation=2",
"PRESPELLTYPE:4,Arcane=5",														"PRESPELLTYPE:4,Arcane=5",
"PRESRGTEQ:10",																	"PRESRGTEQ:10",
"PRESTAT:1,STR=18",																"PRESTAT:1,STR=18",
"PRESTAT:1,STR=18,WIS=18",														"PRESTAT:1,STR=18,WIS=18",
"PRESTAT:2,STR=18,WIS=18",														"PRESTAT:2,STR=18,WIS=18",
"PRESTAT:1,STR=15,WIS=13",														"PRESTAT:1,STR=15,WIS=13",
"PRESTAT:2,STR=13,INT=10,CHA=13",												"PRESTAT:2,STR=13,INT=10,CHA=13",
"PRESUBCLASS:1,Evoker,Abjurer,Enchanter,Illusionist",							"PRESUBCLASS:1,Evoker,Abjurer,Enchanter,Illusionist",
"PRETEMPLATE:1,Celestial,Fiendish",												"PRETEMPLATE:1,Celestial,Fiendish",
"PRETEXT:Character must make a sacrifice of bananas to the Monkey God",			"PRETEXT:Character must make a sacrifice of bananas to the Monkey God",
"PRETYPE:1,Elemental,Fey,Outsider",												"PRETYPE:1,Elemental,Fey,Outsider",
"PRETYPE:2,Humanoid,Undead",													"PRETYPE:2,Humanoid,Undead",
"PREUATT:4",																	"PREUATT:4",
"PREVARGT:Rage,4",																"PREVARGT:Rage,4",
"PREVARGT:SneakAttack,5",														"PREVARGT:SneakAttack,5",
"PREVARGT:SneakAttack,5,Rage,4",												"PREVARGT:SneakAttack,5,Rage,4",
"PREVISION:2,Normal=1,Darkvision=1",										"PREVISION:2,Normal=1,Darkvision=1",
"PREVISION:1,Blindsight=30,Darkvision=30",										"PREVISION:1,Blindsight=30,Darkvision=30",
"PREWEAPONPROF:2,Kama,Katana",													"PREWEAPONPROF:2,Kama,Katana",
"PREWEAPONPROF:1,TYPE.Exotic",													"PREWEAPONPROF:1,TYPE.Exotic",
"PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)",									"PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)",
"PREWEAPONPROF:1,DEITYWEAPON",													"PREWEAPONPROF:1,DEITYWEAPON",
"PREWIELD:1,Light,OneHanded",													"PREWIELD:1,Light,OneHanded",
"PREWIELD:1,TwoHanded",															"PREWIELD:1,TwoHanded",
"PREWIELD:Q:1,TwoHanded",														"PREWIELD:Q:1,TwoHanded",

"PRERACE:1,Orc,[%]",															"PREMULT:2,[PRERACE:1,Orc],[!PRERACE:1,%]",

//
// Not in the docs
"PREARMORTYPE:1,TYPE.Exotic",													"PREARMORTYPE:1,TYPE.Exotic",
"PREARMORTYPE:1,Leather%",														"PREARMORTYPE:1,Leather%",
"PREARMORTYPE:1,LIST",															"PREARMORTYPE:1,LIST",
"PREHD:MIN=3",																	"PREHD:MIN=3",
"PREHD:MIN=3,MAX=12",															"PREHD:MIN=3,MAX=12",
"PREHP:12",																		"PREHP:12",
"PREPOINTBUYMETHOD:1,Standard",													"PREPOINTBUYMETHOD:1,Standard",
"PREPOINTBUYMETHOD:1,Standard,High-powered",									"PREPOINTBUYMETHOD:1,Standard,High-powered",

//
// Just for good measure
//
"PREMULT:1,[PREFEAT:1,CHECKMULT,Dodge],[PREFEAT:1,CHECKMULT,Combat Reflexes]",	"PREMULT:1,[PREABILITY:1,CHECKMULT,CATEGORY=FEAT,Dodge],[PREABILITY:1,CHECKMULT,CATEGORY=FEAT,Combat Reflexes]",
"PRERACE:1,Dwarf",																"PRERACE:1,Dwarf",
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
"PRESTATLT:1,STR=18",															"!PRESTAT:1,STR=18",
"PRESTATLTEQ:1,STR=18",															"PRESTATLTEQ:1,STR=18",
"PRESTATGT:1,STR=18",															"PRESTATGT:1,STR=18",
"PRECSKILL:1,Craft (Basketweaving)",											"PRECSKILL:1,Craft (Basketweaving)",
"!PRECSKILL:1,Craft (Basketweaving)",											"!PRECSKILL:1,Craft (Basketweaving)",
//
// Test default logic cases
//
"PREBASESIZE:Medium",															"PREBASESIZEGTEQ:M",
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
"!PRECSKILL:1,Spot,Listen",														"!PRECSKILL:1,Spot,Listen",
"!PREBASESIZELTEQ:Medium",														"PREBASESIZEGT:M",
"!PREHANDSGT:2",																"PREHANDSLTEQ:2",
"!PRELEGSGTEQ:4",																"PRELEGSLT:4",
"!PRESIZEEQ:H",																	"PRESIZENEQ:H",
"!PRESRGTEQ:10",																"PRESRLT:10",
"!PREVARGT:Rage,4",																"PREVARLTEQ:Rage,4",
"!PREPROFWITHARMOR:1,TYPE.Medium",												"!PREPROFWITHARMOR:1,TYPE.Medium",
"!PREARMORTYPE:1,TYPE.Exotic",													"!PREARMORTYPE:1,TYPE.Exotic",
"!PREATT:6",																	"!PREATT:6",
"!PREBIRTHPLACE:Klamath",														"!PREBIRTHPLACE:Klamath",
"!PRECITY:Klamath",																"!PRECITY:Klamath",
"!PREDEITYDOMAIN:1,Good",														"!PREDEITYDOMAIN:1,Good",
"!PREDEITY:1,Odin",																"!PREDEITY:1,Odin",
"!PREEQUIP:1,Leather Armor",													"!PREEQUIP:1,Leather Armor",
"!PREEQUIPBOTH:1,Quarterstaff",													"!PREEQUIPBOTH:1,Quarterstaff",
"!PREEQUIPPRIMARY:1,Dagger",													"!PREEQUIPPRIMARY:1,Dagger",
"!PREEQUIPSECONDARY:1,Dagger",													"!PREEQUIPSECONDARY:1,Dagger",
"!PREEQUIPTWOWEAPON:1,Sword (Short)",											"!PREEQUIPTWOWEAPON:1,Sword (Short)",
"!PREFACT:1,RACE,LEGS=2",														"!PREFACT:1,RACE,LEGS=2",
"!PREFACTSET:1,DEITY,PANTHEONS=Greek",											"!PREFACTSET:1,DEITY,PANTHEONS=Greek",
"!PREGENDER:M",																	"!PREGENDER:M",
"!PREHP:12",																	"!PREHP:12",
"!PREDEITY:1,Y",																"!PREDEITY:1,Y",
"!PREITEM:1,Sword (Long)",														"!PREITEM:1,Sword (Long)",
"!PRELEVELMAX:10",																"!PRELEVELMAX:10",
"!PRELEVEL:MIN=4",																"!PRELEVEL:MIN=4",
"!PRELEVEL:MAX=4",																"!PRELEVEL:MAX=4",
"!PRELEVEL:MIN=4,MAX=6",														"!PRELEVEL:MIN=4,MAX=6",

"!PREPCLEVEL:MIN=4",															"!PREPCLEVEL:MIN=4",
"!PREPCLEVEL:MAX=4",															"!PREPCLEVEL:MAX=4",
"!PREPCLEVEL:MIN=4,MAX=6",														"!PREPCLEVEL:MIN=4,MAX=6",

"!PREREGION:Slithe",															"!PREREGION:Slithe",
"!PRERULE:1,SYS_WTPSK",															"!PRERULE:1,SYS_WTPSK",
"!PREPROFWITHSHIELD:1,Buckler",													"!PREPROFWITHSHIELD:1,Buckler",
"!PRESA:1,Turn undead",															"!PRESA:1,Turn undead",
"!PRESPELLBOOK:YES",															"!PRESPELLBOOK:YES",
"!PRESPELL:1,Magic Missile",													"!PRESPELL:1,Magic Missile",
"!PRESUBCLASS:1,Evoker",														"!PRESUBCLASS:1,Evoker",
"!PRETEMPLATE:1,Celestial",														"!PRETEMPLATE:1,Celestial",
"!PREUATT:4",																	"!PREUATT:4",
"!PREWEAPONPROF:1,TYPE.Exotic",													"!PREWEAPONPROF:1,TYPE.Exotic",
"!PREWIELD:1,TwoHanded",														"!PREWIELD:1,TwoHanded",
"!PRECHECKBASE:1,Fortitude=5",													"!PRECHECKBASE:1,Fortitude=5",
"!PRECHECK:1,Fortitude=5",														"!PRECHECK:1,Fortitude=5",
"!PREDOMAIN:1,Travel",															"!PREDOMAIN:1,Travel",
"!PREDR:1,+1=10",																"!PREDR:1,+1=10",
"!PREMOVE:1,Swim=10",															"!PREMOVE:1,Swim=10",
"!PRESTAT:1,STR=18",															"!PRESTAT:1,STR=18",
"!PRESTATEQ:1,STR=18",															"PRESTATNEQ:1,STR=18",
"!PRESTATNEQ:1,STR=18",															"PRESTATEQ:1,STR=18",
"!PRESTATGTEQ:1,STR=18",														"!PRESTAT:1,STR=18",
"!PRESTATLT:1,STR=18",															"PRESTAT:1,STR=18",
"!PRESTATLTEQ:1,STR=18",														"PRESTATGT:1,STR=18",
"!PRESTATGT:1,STR=18",															"PRESTATLTEQ:1,STR=18",
"!PREVISION:1,Blindsight=ANY",													"!PREVISION:1,Blindsight=ANY",
"!PREPOINTBUYMETHOD:1,Standard",												"!PREPOINTBUYMETHOD:1,Standard",
"!PRESKILLTOT:Spot,Listen,Search=30",											"!PRESKILLTOT:Spot,Listen,Search=30",
"!PREFEAT:1,Dodge",																"!PREABILITY:1,CATEGORY=FEAT,Dodge",
"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3",										"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3",
"!PRESPELLSCHOOLSUB:3,Creation=2",												"!PRESPELLSCHOOLSUB:3,Creation=2",
"!PRESPELLSCHOOL:3,Necromancy=2",												"!PRESPELLSCHOOL:3,Necromancy=2",
"!PRESPELLTYPE:4,Arcane=5",														"!PRESPELLTYPE:4,Arcane=5",
"PREHD:MIN=3",																	"PREHD:MIN=3",
"PREHD:MIN=3,MAX=12",															"PREHD:MIN=3,MAX=12",
"!PRECLASS:1,SPELLCASTER=2",													"!PRECLASS:1,SPELLCASTER=2",
"!PRESPELLTYPE:4,Arcane=5,Divine=2",											"!PRESPELLTYPE:4,Arcane=5,Divine=2",
"!PRESPELLSCHOOL:3,Necromancy=2,Divination=4",									"!PRESPELLSCHOOL:3,Necromancy=2,Divination=4",
"!PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",									"!PRESPELLSCHOOLSUB:3,Subshool1=2,Subshool2=4",
"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",								"!PRESPELLDESCRIPTOR:4,Mind-Affecting=3,Fire=2",
"!PRESKILLTOT:TYPE.Knowledge=20",												"!PRESKILLTOT:TYPE.Knowledge=20",
"!PREPOINTBUYMETHOD:1,Standard,High-powered",									"!PREPOINTBUYMETHOD:1,Standard,High-powered",

"PREBASESIZEEQ:Medium",															"PREBASESIZEEQ:M",
"PREBASESIZEGT:Medium",															"PREBASESIZEGT:M",
"PREBASESIZEGTEQ:Medium",														"PREBASESIZEGTEQ:M",
"PREBASESIZELT:Medium",															"PREBASESIZELT:M",
"PREBASESIZENEQ:Medium",														"PREBASESIZENEQ:M",
"PRECHECK:1,Fortitude=3",														"PRECHECK:1,Fortitude=3",
"PRECHECKBASE:1,Will=3",														"PRECHECKBASE:1,Will=3",
"PRECLASSLEVELMAX:1,Barbarian=1",												"!PRECLASS:1,Barbarian=2",
"!PRECLASSLEVELMAX:1,Barbarian=1",												"PRECLASS:1,Barbarian=2",
"PRECLASSLEVELMAX:4,Rogue=5,Fighter=2,SPELLCASTER=2,Monk=3",					"!PRECLASS:1,Rogue=6,Fighter=3,SPELLCASTER=3,Monk=4",
"PRECLASSLEVELMAX:1,Cleric=1,Fighter=1,Monk=1,Rogue=1",							"PREMULT:1,[!PRECLASS:1,Cleric=2],[!PRECLASS:1,Fighter=2],[!PRECLASS:1,Monk=2],[!PRECLASS:1,Rogue=2]",
"!PRECLASS:1,Battlemind=7",														"!PRECLASS:1,Battlemind=7",
"PRECSKILL:1,Swim",																"PRECSKILL:1,Swim",
"PREDR:1,Silver.10",															"PREDR:1,Silver=10",
"PREDR:1,Silver=10",															"PREDR:1,Silver=10",
"PREDR:1,-=10,Cold Iron=10,+1=10",												"PREDR:1,-=10,Cold Iron=10,+1=10",
"PREDEITY:1,Thor",																"PREDEITY:1,Thor",
"PREDEITYDOMAIN:1,Travel",														"PREDEITYDOMAIN:1,Travel",
"PREDOMAIN:1,Sex,Travel",														"PREDOMAIN:1,Sex,Travel",
"PREDOMAIN:1,Travel",															"PREDOMAIN:1,Travel",

"PRERULE:1,SYS_DOMAIN",															"PRERULE:1,SYS_DOMAIN",
"PRESKILL:1,Knowledge (History)=1",												"PRESKILL:1,Knowledge (History)=1",
"PRESKILL:3,TYPE.Knowledge=2",													"PRESKILL:3,TYPE.Knowledge=2",
"!PRESKILL:3,TYPE.Knowledge=2",													"!PRESKILL:3,TYPE.Knowledge=2",
"PREVARGTEQ:BarbRagePowerLVL,11",												"PREVARGTEQ:BarbRagePowerLVL,11",
"PREVAREQ:TL,CL",																"PREVAREQ:TL,CL",
"PREVAREQ:TL,(CL=Barbarian+CL=Ex Barbarian)",									"PREVAREQ:TL,(CL=Barbarian+CL=Ex Barbarian)",
"PREVARLT:ENCUMBERANCE,2",														"PREVARLT:ENCUMBERANCE,2",
"PREMULT:3,[PRETYPE:1,type1],[PRETYPE:1,type2,type3],[!PRETYPE:1,type4]",		"PREMULT:3,[PRETYPE:1,type1],[PRETYPE:1,type2,type3],[!PRETYPE:1,type4]",
"PRETYPE:1,Animal",																"PRETYPE:1,Animal",
"!PRETYPE:1,Animal",															"!PRETYPE:1,Animal",
"!PRELANG:1,Dwarven,Elven",														"!PRELANG:1,Dwarven,Elven",
"PRELANG:2,Any",																"PRELANG:2,Any",
"!PRELANG:2,Any",																"!PRELANG:2,Any",
"PRESKILL:1,Ride=10",															"PRESKILL:1,Ride=10",
"!PRESKILL:1,Ride=10",															"!PRESKILL:1,Ride=10",
"PRESKILL:1,Ride=10,Listen=5",													"PRESKILL:1,Ride=10,Listen=5",
"PRESKILL:2,Ride=10,Listen=5",													"PRESKILL:2,Ride=10,Listen=5",
"!PRESKILL:2,Ride=10,Listen=5",													"!PRESKILL:2,Ride=10,Listen=5",
"PRESKILLTOT:Ride,Listen=20",													"PRESKILLTOT:Ride,Listen=20",
"!PRESKILLTOT:Ride,Listen=20",													"!PRESKILLTOT:Ride,Listen=20",

//
// Other oddballs
//
"PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",			"PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",
"!PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",			"!PREMULT:1,[PRESPELLTYPE:4,Arcane=5],[PRESPELLSCHOOL:3,Necromancy=2]",
"PRECLASSLEVELMAX:1,Fighter=VARNAME",											"!PRECLASS:1,Fighter=(VARNAME)+1",
"!PRETEXT:Character must make a sacrifice of bananas to the Monkey God",		"!PRETEXT:Character must make a sacrifice of bananas to the Monkey God",
"PREWEAPONPROF:2,TYPE.Exotic,[Chain (Spiked)]",									"PREMULT:2,[PREWEAPONPROF:1,TYPE.Exotic],[!PREWEAPONPROF:1,Chain (Spiked)]",
"PREWEAPONPROF:1,TYPE.Exotic,[Chain (Spiked)]",									"PREMULT:1,[PREWEAPONPROF:1,TYPE.Exotic],[!PREWEAPONPROF:1,Chain (Spiked)]",
"PREWEAPONPROF:1,[Chain (Spiked)]",												"!PREWEAPONPROF:1,Chain (Spiked)",
"PREATT:should_be_numeric",														"PREATT:1",
"PRESTAT:should_be_numeric,STR=18",												"PRESTAT:1,STR=18",
"PRESTAT:1,Strength=18",														"PRESTAT:1,Str=18",
"PREABILITY:1,CATEGORY=Special Ability,Dire Animal (Dire Rat)_Companion",		"PREABILITY:1,CATEGORY=Special Ability,Dire Animal (Dire Rat)_Companion",
"PREABILITY:1,CATEGORY=FEAT,[Surprise Strike]",									"PREABILITY:1,CATEGORY=FEAT,[Surprise Strike]",
"PREABILITY:1,CATEGORY=FEAT,Sneak Attack,[Alertness]",							"PREABILITY:1,CATEGORY=FEAT,Sneak Attack,[Alertness]",

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
"PREHD:should_be_numeric",														"",		// should me numeric
"PREHD:MIN=x",																	"",		// bad int value
"PREHD:MAX=23asdc",																"",		// bad int value
"PREHD:MIN=3,MAX=asdf",															"",		// one bad int value


"PREVARGT:SneakAttack,Rage,4",													"",		// invalid # of tokens
"PRESTAT:1,S=18",																"",		// need 3 character attribute
"PRESTAT:1,STR",																"",		// missing '='
"PRESTAT:1,ST",																	"",		// need at least 3 characters
"PRESTAT",																		"",		// missing ':'
"PREHP:should_be_numeric",														"",
"PRESRGTEQ:should_be_numeric",													"",
"PREDUMMYKIND:1,arg",															""
	};

    @Test
    void runTest()
    {
	    for (var idx = 0; idx < (testparams.length >> 1); ++idx)
	    {
		    var preString = testparams[idx << 1];
		    var postString = testparams[(idx << 1) + 1];
		    PreTest(preString, postString);
	    }
	}

    @BeforeEach
    void setUp() {
		TestHelper.loadPlugins();
		Globals.setUseGUI(false);
		Globals.emptyLists();
		GameMode gamemode = new GameMode("3.5");
		SystemCollections.addToGameModeList(gamemode);
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SettingsHandler.setGame("3.5");
		Alignment.createAllAlignments();
		TestHelper.makeSizeAdjustments();
		FactKey.getConstant("IsPC", STR_MGR);
		FactKey.getConstant("LEGS", STR_MGR);
		FactSetKey.getConstant("PANTHEONS", STR_MGR);
	}

	/**
	 * @param aPreString
	 * @param
	 * expectedOutput
	 */
	private static void PreTest(final String aPreString, final String expectedOutput)
	{
		Prerequisite prereq = null;
		boolean bExceptionThrown = false;
		boolean bExceptionExpected = expectedOutput.isEmpty();
		try
		{
			prereq = PreParserFactory.getInstance().parse(aPreString);
		}
		catch (PersistenceLayerException ple)
		{
			if (!bExceptionExpected)
			{
				ple.printStackTrace();
				fail(() -> "parse caused PersistenceLayerException: " + ple);
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
					System.out.println(prereq);
				}
				fail("exception expected but not thrown");
			}
			return;
		}

		if (prereq == null)
		{
			fail(() -> "Could not parse prereq: '" + aPreString + "'");
		}

		StringWriter sw = new StringWriter();
		PrerequisiteWriter writer = new PrerequisiteWriter();
		try
		{
			writer.write(sw, prereq);
		}
		catch (PersistenceLayerException ple)
		{
			fail(() -> "write caused PersistenceLayerException: " + ple);
		}
		final String writerOutput = sw.toString();
		System.out.println("'" + aPreString + "' returned '" + writerOutput
			+ "'");
		assertEquals(expectedOutput, writerOutput, aPreString + " returned '" + writerOutput + "' (expected '"
				+ expectedOutput + "'). " + prereq);

		//
		// Test .lst output
		//
		pcgen.core.PObject pobj = new pcgen.core.PObject();
		pobj.addPrerequisite(prereq);
		assertEquals(
				PrerequisiteWriter.prereqsToString(pobj),
				expectedOutput,
				"PrerequisiteWriter.prereqsToString failure"
		);

		// expect a PersistenceLayerException as passed custom writer that throws IOExceptions
		Prerequisite finalPrereq = prereq;
		assertThrows(PersistenceLayerException.class,
				() -> writer.write(new myWriter(), finalPrereq));
	}

	private static class myWriter extends Writer
	{
        @Override
		public void flush() throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

        @Override
		public void close() throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

        @Override
		public void write(char[] cbuf, int off, int len) throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

        @Override
		public void write(int c) throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

        @Override
		public void write(String str) throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

        @Override
		public void write(String str, int off, int len) throws IOException
		{
			throw new IOException("intentionally generated exception");
		}

	}

}
