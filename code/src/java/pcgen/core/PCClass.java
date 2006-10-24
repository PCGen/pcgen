/*
 * PCClass.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.DoubleKeyMap;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.DefaultTriState;

/**
 * <code>PCClass</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 */
public class PCClass extends PObject {
	/*
	 * PROTECTIONREFACTOR spellBaseStat should be made private
	 */
	/*
	 * TYPESAFETY If this really is a Base stat, then this should be storing
	 * that Stat in a type safe form.
	 */
	/*
	 * ALLCLASSLEVELS The spellBaseStat needs to be stored in EACH individual
	 * PCClassLevel, since each individual PCClassLevel is granting spells, and
	 * will be "looked to" to determine the base 'properties' of those spells
	 */
	protected String spellBaseStat = Constants.s_NONE;

	/*
	 * PROTECTIONREFACTOR bonusSpellBaseStat should be made private
	 */
	/*
	 * TYPESAFETY This should really be storing a PCStat or something else that
	 * is type safe, not simply a String.
	 */
	/*
	 * ALLCLASSLEVELS The challenge here is that the bonus spells must be
	 * calculated by the PCClassLevel based on the current Stat (which may
	 * change over the life of a PC and from one PC to another). However, in a
	 * literal check, this variable then only needs to be present in the first
	 * PCClassLevel that can cast any given spell level (otherwise, bonus spells
	 * will unreasonably stack). I think another variable is needed to indicate
	 * to a particular PCClassLevel whether it is allowed to grant bonus spells,
	 * and for what level(s) it grants those bonuses.
	 * 
	 * Note this is dependent upon how PCClassLevel ends up calculating the
	 * known and cast spells for any given level.
	 */
	protected String bonusSpellBaseStat = Constants.s_DEFAULT;

	/*
	 * UNKNOWNDESTINATION Don't know where to put this yet
	 */
	/*
	 * PROTECTIONREFACTOR numSpellsFromSpecialty should be made private
	 */
	protected int numSpellsFromSpecialty = 0;

	/*
	 * LEVELONEONLY Since this is for a Class Line and not a ClassLevel line, a
	 * granting of Domains only needs to occur in first level.
	 * 
	 * Note this should be checked, as this is what the DOCUMENTATION says, but
	 * the code seems to want to allow a Domain on a Class Level line. (Not sure
	 * if this functions correctly, though)
	 */
	private ArrayList<LevelProperty> domainList = null;

	/*
	 * ALLCLASSLEVELS The automatic Feats appropriate to any given level (they
	 * should be stored in a series of LevelProperty objects) need to be placed
	 * into each individual PCClassLevel when it is constructed.
	 */
	private ArrayList<String> featAutos = null;

	/*
	 * TYPESAFETY The Feats should be type safe, not Strings...
	 */
	/*
	 * ALLCLASSLEVELS Since the Feats are being granted by level, this needs to
	 * account for that and actually store these by level and put them into the
	 * appropriate PCClassLevel.
	 */
	private ArrayList<String> featList = null;

	/*
	 * ALLCLASSLEVELS Since the known list is class level dependent, it needs to
	 * be stored into each PCClassLevel
	 */
	private ArrayList<String> knownList = null;

	/*
	 * LEVELONEONLY This variable (automatically known spells) only needs to be
	 * loaded into the first PCClassLevel returned by PCClass, because the data
	 * is static (doesn't change by level) and because it will be tested
	 * dynamically (does the PCClassLevel automatically know spell A?), it only
	 * needs to appear on one of the PlayerCharacter's PCClassLevels.
	 */
	private ArrayList<String> knownSpellsList = null;

	/*
	 * ALLCLASSLEVELS This is the list of specialties that were taken as part of
	 * leveling up at a certain point. Therefore this gets moved to PCClassLevel =
	 * byproduct of addLevel
	 */
	private ArrayList<String> specialtyList = null;

	/*
	 * ALLCLASSLEVELS The specialtyKnownList [based on their level and/or
	 * LevelProperty (if it gets used)] (not the raw Strings) need to be stored
	 * in EACH individual PCClassLevel.
	 */
	private ArrayList<String> specialtyknownList = null;

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch. (Actually building a LevelProperty<Template>
	 * or something like that)
	 */
	/*
	 * TYPESAFETY This is throwing around template names as Strings. :(
	 */
	/*
	 * ALLCLASSLEVELS The templates [based on their LevelProperty] (not the raw
	 * Strings) need to be stored in EACH individual PCClassLevel.
	 */
	private ArrayList<String> templates = null;

	/*
	 * ALLCLASSLEVELS The SR List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private ArrayList<LevelProperty> SR = null;

	/*
	 * ALLCLASSLEVELS Since this seems to allow for class dependent additions of
	 * Domains, this needs to occur in each class level as appropriate.
	 */
	private ArrayList<String> addDomains = null;

	/*
	 * ALLCLASSLEVELS This is pretty obvious, as these are already in a
	 * LevelProperty... these go into the PCClassLevel
	 */
	private ArrayList<LevelProperty> naturalWeapons = null;

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	private ArrayList<SubClass> subClassList = null; // list of SubClass
														// objects

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private ArrayList<SubstitutionClass> substitutionClassList = null; // list
																		// of
																		// SubstitutionClass
																		// objects

	/*
	 * DELETEVARIABLE This appears to be an unused cache, and will not be very
	 * useful once the Templates are level dependent and in different
	 * PCClassLevels
	 */
	private ArrayList<String> templatesAdded = null;

	/*
	 * DELETEVARIABLE There is NO use of this Tag at all in the data/* structure
	 * today, so support for this should be removed from this class.
	 */
	private ArrayList<String> uattList = new ArrayList<String>(); // TODO -
																	// This
																	// should be
																	// removed.

	/*
	 * ALLCLASSLEVELS castForLevelMap is part of PCClassLevel - or nothing at
	 * all since this seems to be a form of cache? - DELETEVARIABLE
	 */
	private HashMap<Integer, Integer> castForLevelMap = null;

	/*
	 * ALLCLASSLEVELS hitPointMap is part of PCClassLevel
	 */
	private HashMap<Integer, Integer> hitPointMap = null; // TODO - This
															// should be in
															// PCLevelInfo

	/*
	 * ALLCLASSLEVELS The virtual feats [based on their level or LevelProperty]
	 * (not the raw Strings) need to be stored in EACH individual PCClassLevel.
	 * (This object is level dependent based on the input Tag and the use of a
	 * Map)
	 */
	/*
	 * REFACTOR This is currently using a Map to map for levels - why is this
	 * not using LevelProperty?
	 */
	private HashMap<Integer, List<Ability>> vFeatMap = null;
	
	/*
	 * ALLCLASSLEVELS Hard to tell here yet, since this is part of the Ability
	 * project, but this will need similar support to vFeatMap
	 */
	private DoubleKeyMap<AbilityCategory, Integer, List<Ability>> vAbilityMap = null;

	/*
	 * STRINGREFACTOR This is actually some form of HITDIE formula, not
	 * necessarily a number; however, the complex processing that takes place on
	 * it is NOT a Formula, per se. Therefore, the processing can be done either
	 * at import of the Tag, or at least before a PCClassLevel is created.
	 */
	/*
	 * REFACTOR This name??? Lock what? It is really a modification of the HITDIE
	 * that can take place in at each class level
	 */
	/*
	 * REFACTOR This should really be stored in a LevelProperty, not in a Map?
	 */
	/*
	 * ALLCLASSLEVELS This is modifications of the Hit Die and therefore, needs
	 * to be placed into all of the ClassLevels, so that the PC can have HPs
	 * based on the ClassLevel.
	 */
	private HashMap<Integer, String> hitDieLockMap = null;

	/*
	 * ALLCLASSLEVELS skillPool is part each PCClassLevel and what that level
	 * grants to each PlayerCharacter (added by the PCClassLevel Factory, not
	 * by a tag)
	 */
	private int skillPool = 0;

	/*
	 * ALLCLASSLEVELS classSkillList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<String> classSkillList = null;

	/*
	 * ALLCLASSLEVELS classSpellList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<String> classSpellList = null;

	/*
	 * ALLCLASSLEVELS This is a list of the additional Class Skills that have
	 * been added by a LevelAbilityClassSkill (such as the additional class
	 * skills granted to the Expert class). Thease must be selected for the
	 * given class, and are thus NOT part of PCClass but PCClassLevel.
	 * 
	 * While looking at this, this needs to be tested, there was a TO-DO in the
	 * code indicating support for this may not be working properly.
	 */
	private List<String> skillList = null; // TODO - Not sure this support is
											// really working properly

	/*
	 * TYPESAFETY The VisionList should be something other than String! There
	 * should probably be a Typesafe Enumeration of Vision Types. This would
	 * then be stored in a VisionMap with the value being the vision distance.
	 */
	/*
	 * ALLCLASSLEVELS The Vision List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty> visionList = null;

	/*
	 * STRINGREFACTOR Need to rebuild this String into a List<Integer>
	 * 
	 * On the other hand, this could be int[][]?? might be smaller/faster?
	 * 
	 * Or what about also using LevelProperty, since if that is generally used,
	 * it could be looked at to optimize the speed of creation of a PCClassLevel
	 * as much as possible? (seems like that could be really fast in a CDOM 
	 * environment)
	 */
	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel, although some 'advanced'
	 * processing may need to take place to do a differential of each class
	 * (class tables should still be stored in their total form for human
	 * readibility!). Remember to consider PCClassLevels where 0 spells are
	 * acquired (vs -) since that is where ability bonuses are triggered...
	 */
	private Map<Integer, String> castMap = null; // TODO - Convert String to
													// List<Integer>

	/*
	 * UNKNOWNDESTINATION Actually, the question becomes: Does the CR simply get
	 * added to each PCClassLevel in order to be incremented (in which case the
	 * GAMEMODE files need to change to be incremental, not Formula based - and
	 * 35e NPC will break!) or whether this is stored elsewhere. Does this
	 * require a PCClassLevel0 that holds stuff like this? I don't want a
	 * contract in place to handle these types of things, but it's looking like
	 * a contract may be an efficient way to do this??
	 */
	private String CRFormula = null; // null or formula

	/*
	 * REFACTOR This gets really strange since this is part of a calculation
	 * based on differences between class levels. The question is: Is there a
	 * much more efficient way to do this calculation (it's in
	 * multiclassXPMultiplier in PlayerCharacter) given the new structure of a
	 * lot of PCClassLevels (and not one object for each PCClass that knows a
	 * level) ( or does PlayerCharacter just need to keep track of the matching
	 * keys?)
	 */
	/*
	 * UNKNOWNDESTINATION Not really sure where to put this, given the
	 * explanation above on when this is relevant and how it is calculated in
	 * PlayerCharacter. Perhaps this is best to dump into all PCClassLevels for
	 * safety? I really hate to get into that mode.
	 */
	private DefaultTriState XPPenalty = DefaultTriState.DEFAULT;

	/*
	 * ALLCLASSLEVELS The abbrev simply needs to be directly loaded into each
	 * individual PCClassLevel. No modification or level dependency on the way
	 * there.
	 */
	private String abbrev = Constants.EMPTY_STRING;

	/*
	 * DELETEVARIABLE CastAS is Deprecated, will not be brought into
	 * PCClass/PCClassLevel
	 */
	private String castAs = Constants.EMPTY_STRING;

	/*
	 * REFACTOR This is actually a challenge in refactoring PCClassLevel out of
	 * PCClass. This actually does a deferral to another class' Skill List.
	 * 
	 * This is possible to do in a reasonable way, given that there is a Global
	 * Class list.
	 * 
	 * However, this gets a LOT more complicated when you consider that this MAY
	 * have to enforce the same skikll list across multiple instantiations of
	 * this PCClass (meaning multiple PCClassLevels). This is because it could
	 * be CLASSSKILL:2,Druid|Ranger|Sorcerer ... the user only gets to select
	 * two... the question being, does it always have to be the same two?? If
	 * SO, can that trigger a multi-class situation, and still use the same
	 * class, or is the user stuck with the original choice?
	 */
	/*
	 * ALLCLASSLEVELS The selected delegate skill lists (not the raw
	 * classSkellString) need to be stored in EACH individual PCClassLevel. This
	 * is the case because each individual PCClassLevel will be capable of
	 * granting skills, and this is the delegate to determine what is
	 * appropriate (skill-wise) for any given PCClassLevel.
	 */
	private String classSkillString = null;

	/*
	 * REFACTOR This is actually a moderate challenge in refactoring
	 * PCClassLevel out of PCClass. This actually does a deferral to another
	 * class' Spell List. This is definitely possible to do in a reasonable way
	 * since the CLASS limitation is actually stored in the spell (as part of
	 * the CLASSES tag). However, this gets a LOT more complicated when you
	 * consider that this MAY have to enforce the same spell list across
	 * multiple instantiations of this PCClass (meaning multiple PCClassLevels).
	 * This is because it could be CLASSSPELL:2,Druid|Ranger|Sorcerer ... the
	 * user only gets to select two... the question being, does it always have
	 * to be the same two?? If SO, can that trigger a multi-class situation, and
	 * still use the same class, or is the user stuck with the original choice.
	 */
	/*
	 * ALLCLASSLEVELS The selected delegate spell lists (not the raw
	 * classSpellString) need to be stored in EACH individual PCClassLevel. This
	 * is the case because each individual PCClassLevel will be capable of
	 * holding individual spells known and spells cast (per day) and this is the
	 * delegate to determine what is appropriate for any given PCClassLevel.
	 */
	private String classSpellString = null;

	/*
	 * TYPESAFETY The Deity List should be something other than Strings
	 */
	/*
	 * ALLCLASSLEVELS Might as well place this into all PCCLassLevels, since it
	 * does seem to apply to all of them individually
	 */
	private List<String> deityList = new ArrayList<String>(2);

	/*
	 * TYPESAFETY This should not be a String, but a member of a Typesafe
	 * Enumeration of Classes...
	 */
	/*
	 * ALLCLASSLEVELS Because this indicates what a Class becomes when the
	 * prerequisites are no longer met, this must be passed into each and every
	 * PCClassLevel (though it can be given in its pure form).
	 */
	/*
	 * REFACTOR Consideration of HOW to actually perform this given the
	 * characteristics of PCClass vs. PCClassLevel are rather interesting...
	 * exactly how do you impose an ExClass on something in a way that is
	 * reasonably safe - given object structure - as well as being reasonably
	 * efficient?
	 */
	private String exClass = Constants.EMPTY_STRING;

	/*
	 * STRINGREFACTOR This is currently taking in a complex formula-ish string
	 * and needs to be put back into the Tag!
	 */
	/*
	 * UNKNOWNDESTINATION Not sure where to put this today. In fact, this is
	 * NEVER called from an instance of PCClass that is part of a
	 * PlayerCharacter, it is actually a REFERENCE item based on calculating the
	 * cost (GP cost?) of a piece of equipment. Therefore, there seems to be no
	 * reason at ALL to put this into PCClassLevel, but rather keep it as
	 * something entirely in PCClass. (Perhaps this really becomes a "static"
	 * method in PCClass: The Factory? This can't literally be static, since it
	 * is directly part of the PCClass instance, but it's also one of the few
	 * things that should be exposed for use in PCClass other than the
	 * PCClassLevel factory method.
	 */
	private String itemCreationMultiplier = Constants.EMPTY_STRING;

	/*
	 * STRINGREFACTOR This is currently processed outside of this set (format is
	 * of the form EXCHANGELEVEL:Ex Paladin|11|10|1). Should be processed in the
	 * tag and passed in here as an object.
	 */
	/*
	 * TYPESAFETY This is actually passing around a Class, and thus can be made
	 * type safe to the list of classes.
	 */
	/*
	 * BUG There is no code behind this tag - no ability to perform a level
	 * exchange exists in PCGen.
	 */
	/*
	 * UNKNOWNDESTINATION Don't know where to put this yet... this is a
	 * COMPLICATED function that allows the exchange of leveis (presumably on a
	 * one-time basis). Thus, this needs to be tagged as performed, and thus
	 * unrepeatable.
	 */
	private String levelExchange = Constants.EMPTY_STRING;

	/*
	 * TYPESAFETY Should this be a tri-state?
	 */
	/*
	 * ALLCLASSLEVELS Because this indicates a Class characteristic, it needs to
	 * be passed into each and every PCClassLevel created from this PCClass.
	 */
	private Boolean monsterFlag = null;

	/*
	 * UNKNOWNDESTINATION This is (yet again) a bit complicated due to the fact
	 * that this is a prerequisite test. First, this is LEVELONEONLY in the
	 * sense that this prerequisite might only be justifiably tested for the
	 * first time a class is taken (if the Race changes, then all bets are off,
	 * right?). To maintain the existing code function (always check on level
	 * up) this becomes ALLCLASSLEVELS and gets passed into each PCClassLevel.
	 */
	private String preRaceType = null;

	/*
	 * ALLCLASSLEVELS Because this indicates prohibited Spell Schools and Spells
	 * Known and Cast are granted by each PCClassLevel, this must be passed into
	 * each and every PCClassLevel (though it can be given in its pure form).
	 */
	private String prohibitedString = Constants.s_NONE;

	/*
	 * DELETEVARIABLE This variable is never used (get method is never called)
	 */
	private String specialsString = Constants.EMPTY_STRING;

	/*
	 * TYPESAFETY This should NOT be a String, as Spell Types are a specific set
	 * of items...
	 */
	/*
	 * ALLCLASSLEVELS Because this indicates a Spell Type of a Class and
	 * PCClassLevels are capable of issuing Spells Known and Cast per day, this
	 * must be passed into each and every PCClassLevel (though it can be given
	 * in its pure form).
	 */
	private String spellType = Constants.s_NONE;

	/*
	 * PCCLASSLEVELONLY Since this is not part of a tag and is related to how
	 * spells are related to a PCClassLevel
	 */
	private String stableSpellKey = null;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the sublevel actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private String subClassKey = Constants.s_NONE;

	/*
	 * PCCLASSONLY This is reference information from a Tag, and is used to
	 * allow a choice of sublevel that is made and placed into the PCClassLevel
	 * in the subClassKey.
	 */
	private String subClassString = Constants.s_NONE;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the substitution level actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private Map<Integer, String> substitutionClassKey = null;

	/*
	 * PCCLASSONLY This is reference information from a Tag, and is used to
	 * allow a choice of substituion level that is made and placed into the
	 * PCClassLevel in the substitutionClassKey.
	 */
	private String substitutionClassString = Constants.s_NONE;

	// private TreeSet<Language> languageBonus = new TreeSet<Language>();

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubClass
	 */
	private boolean hasSubClass = false;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubstitutionClass
	 */
	private boolean hasSubstitutionClass = false;

	/*
	 * ALLCLASSLEVELS I think this is appropriate to put into all PCClassLevels,
	 * since PCClassLevel has the ability to grant known spells and cast spells
	 * (per day).
	 */
	/*
	 * REFACTOR This gets VERY interesting as far as prerequisite checking.
	 * There is a PRESPELLCASTMEMORIZE tag (or some such) that tests how many
	 * classes the character has that memorizes spells. That test gets MUCH more
	 * complicated in a PCClassLevel world, since there will be multiple
	 * PCClassLevels that memorize spells; all of which will use the same
	 * PCClass as a base (therefore does the PREREQ need to keep track of the
	 * matching keys?)
	 */
	private boolean memorizeSpells = true;

	/*
	 * UNKNOWNDESTINATION This seems initially to be part of the Factory that
	 * creates the PCClassLevels, and not something that would end up in
	 * PCClassLevel. This should NOT need to be added to the PCClassLevel since
	 * the addition of Skill points is not retroactive (once a level is
	 * achieved, the bonus skills from INT (or the Stat in general) are not
	 * changed if the stat changes)
	 * 
	 * However, there is also a VERY weird call to getModToSkills() over in the
	 * KitStat class, and I CAN'T for the life of me figure out why it's there??
	 * I think what it's doing is going back and violating the rules and
	 * providing retroactive skill point increases based on the STAT... but I
	 * can't be sure of that.
	 */
	/*
	 * MONSTERONLY Should some special architecture be present for the
	 * subcomponents of PCClass/PCClassLevel that are only related to Monster
	 * Classes?
	 */
	private boolean modToSkills = true; // stat bonus applied to skills per
										// level

	/*
	 * BUG This is currently NOT processed in PCClass. The intent here is to
	 * only have this check when this particular class is taken as a 2nd class,
	 * not as the first class of a character (according to the Docs)
	 */
	/*
	 * ALLCLASSLEVELS Because this indicates prerequisites for a given
	 * PCClassLevel (though it's dependent upon the existing classes of the
	 * PlayerCharacter), it must be passed in to the PCClassLevel. This is not
	 * completely intuitive (it could be tested inside of the Factory that
	 * creates the PCClassLevel), but the system should also be able to do
	 * "post-hoc" verifications (to find cases later invalidated due to Data
	 * updates or code fixes) and therefore it should be stored in the
	 * PCClassLevel
	 */
	private boolean multiPreReqs = false;

	/*
	 * ALLCLASSLEVELS I think this is appropriate to put into all PCClassLevels,
	 * since PCClassLevel has the ability to grant known spells and cast spells
	 * (per day).
	 */
	/*
	 * REFACTOR This gets VERY interesting as far as prerequisite checking.
	 * There is a PRESPELLBOOKTESTER tag (or some such) that tests how many
	 * classes the character has that memorizes spells. That test gets MUCH more
	 * complicated in a PCClassLevel world, since there will be multiple
	 * PCClassLevels that use a spell book; all of which will use the same
	 * PCClass as a base (therefore does the PREREQ need to keep track of the
	 * matching keys?)
	 */
	private boolean usesSpellbook = false;

	/*
	 * LEVELONEONLY Because this indicates a set of prohibitions, it only needs
	 * to be inside one of the PCClassLevels. This is true because the
	 * prohibitions are based on School, Spell Name, etc. and are not specific
	 * to any particular level (they are in the Class line)
	 */
	private List<SpellProhibitor> prohibitSpellDescriptorList = null;

	/*
	 * ALLCLASSLEVELS This is the Hit Die and therefore, needs to be placed into
	 * all of the ClassLevels, so that the PC can have HPs based on the
	 * ClassLevel.
	 */
	private int hitDie = 0;

	/*
	 * DELETEVARIABLE This variable is set and never read.  Is there some unknown 
	 * use for this, such that this is a bug and not a delete?
	 */
	private int initMod = 0;

	/*
	 * LEVELONEONLY This is (by definition) how many additional feats the Class
	 * gets at level one. Therefore, this is only relevant for the first level
	 * of a PCClass.
	 */
	/*
	 * BUG This value is NOT ACTUALLY processed at all (today) in PCClass, and
	 * needs to be correctly processed in the future
	 */
	private int initialFeats = 0;

	/*
	 * ALLCLASSLEVELS Well, technically, NOT all classes, because this should
	 * only put the Feat into class levels that actually gain them. But
	 * theoretically, this is possible to have a value of 1 and thus place a new
	 * feat at every level. Note that addLevel currently adds these as a BONUS
	 * item (so the source can be tracked??)
	 */
	/*
	 * MONSTERONLY This is a tag/function that is only relevant for Monster
	 * classes and should therefore be in a separate sub-object that is only
	 * relevant to Monster Classes?
	 */
	/*
	 * REFACTOR can this use an int or is null significant?
	 */
	private Integer levelsPerFeat = null;

	/*
	 * PCCLASSONLY This is ONLY required in the construction of a PCClassLevel -
	 * it never needs (or shouldn't need) to be exported into the PCClassLevel.
	 * 
	 * Note: It is possibly useful to have a boolean isMaxLevel() available in a
	 * PCClassLevel, but that is TBD
	 */
	private int maxLevel = 20;

	/*
	 * DELETEVARIABLE MaxCastLevel is ONLY used in capping the level at which
	 * the Class can cast spells. This should easily be factorable out at very
	 * little CPU cost and hopefully a significant increase in clarity (caching
	 * can be confusing)...
	 */
	private int maxCastLevel = -1; // max level CAST: tag is found

	/*
	 * DELETEVARIABLE MaxKnownLevel is ONLY used in determining the known spells
	 * for a given level. This represents the maximum level at which the Class
	 * can gain spells. This should easily be factorable out at very little CPU
	 * cost and hopefully a significant increase in clarity (caching can be
	 * confusing)...
	 */
	private int maxKnownLevel = -1; // max level KNOWN: tag is found

	/*
	 * DELETEVARIABLE This is a cache, and a weird one at that, given that it
	 * only ever stores a key of "-1". This processing should probably be done
	 * by a Utility Class which parses through the PCClassLevels of a given
	 * PlayerCharacter... it can reasonably be cached there in the
	 * PlayerCharacter, but no longer in the PCClass or PCClassLevel.
	 */
	private HashMap<Integer, Integer> highestSpellLevelMap = null;

	/*
	 * FORMULAREFACTOR This is currently processed elsewhere - should be
	 * processed as a Formula, not a String...
	 */
	/*
	 * ALLCLASSLEVELS The RESULT of this formula - at least I think it's the
	 * result - need to check on what's legal in the formula and whether this
	 * must be calculated on the fly or not - can be placed into each
	 * PCClassLevel. NOTE: This placement into the PCClassLevel needs to be
	 * AFTER the SKILLMULTIPLIER from GameMode is properly handled... or perhaps
	 * PCClassLevel is GameMode aware??
	 */
	private String skillPointFormula = "0";

	/*
	 * ALLCLASSLEVELS This needs to appear in both PCClass and PCClassLevel, on
	 * all class levels, since it is controlling the behavior of the castMap.
	 */
	/*
	 * REFACTOR This is actually a pretty large concern: Like the CR formula,
	 * this makes the castMap impossible to do in an incremental fashion,
	 * because the castMap becomes dependent on things that make the
	 * calculations rather difficult.
	 */
	private boolean hasSpellFormulas = false;

	/*
	 * TYPESAFETY This is definitely something that needs to NOT be a String,
	 * but it gets VERY complicated to do that, since the keys are widely used
	 * in the variable processor.
	 */
	/*
	 * ALLCLASSLEVELS Must be in all PCClassLevels, since this is the master
	 * index of what this PCClassLevel was created from. Best for debugging, and
	 * not necessarily creation on the fly?
	 */
	/*
	 * REFACTOR This brings up a FASCINATING point, in that the Class Key today
	 * is used in variable processing. How is that to be handled going forward -
	 * so that PCClassLevels are actually what is checked and NOT the PCClass?
	 * What needs to be done to teach the system to iterate over all
	 * PCClassLevels that implement this key?
	 */
	private String classKey = Constants.EMPTY_STRING;

	/** The level of this class for the PC this PCClass is assigned to. */
	/*
	 * ALLCLASSLEVELS This is a fundamental part of the PCClassLevel creation
	 * and stored information
	 */
	protected int level = 0; // TODO - This should be moved.

	/*
	 * DELETEVARIABLE This can be refactored out of PCClass and put into the
	 * ATTACKCYCLE tag. Then the tag needs to build the attackCycleMap (below)
	 * and properly load that map into PCClass. There will also need to be
	 * something to UNDO the map creation (the export tags do UNTAG or
	 * something?) in order to convert back into the String.
	 */
	private String attackCycle = Constants.EMPTY_STRING;

	/*
	 * attackCycleMap is part of PCClass (not PCClassLevel) because it is loaded
	 * from the LST file. attackCycleMap should NOT be loaded into ANY
	 * PCClassLevel as this is an IMPLICIT part of the Attack Bonuses of a
	 * PCClassLevel.
	 * 
	 * ALLCLASSLEVELS PCClassLevel will have a BONUS to COMBAT|BAB for each
	 * level where a BAB is added (these will have to stack) - is this 
	 * appropriate, and will the bonus correctly trigger the appropriate 
	 * attack cycle when additional attacks are gained?
	 */
	/*
	 * MEMORYREFACTOR This attackCycleMap can be MUCH smaller if it is not a
	 * HashMap. This gets into tiny levels of memory optimization, but it may
	 * make sense going forward to have a TinyMap class (which directly stores
	 * two arrays and searches through the first one to grab the appropriate
	 * item from the second one. For small maps, this can be > 50% memory
	 * savings.
	 */
	private HashMap<String, String> attackCycleMap = null;

	
//	private DoubleKeyMap<AbilityCategory, Integer, List<String>> theAutoAbilities = null;
	
	/**
	 * Default Constructor. Constructs an empty PCClass.
	 */
	public PCClass() {
		super();
		deityList.add("ANY");
	}

	/**
	 * Sets the abbreviation used for this class.
	 * 
	 * <p>
	 * There are no constraints placed on what the allowable abbreviation can
	 * be.
	 * 
	 * @param argAbbrev
	 *            The abbreviation to use.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClassLevel since
	 * it is a Tag
	 */
	public final void setAbbrev(final String argAbbrev) {
		abbrev = argAbbrev;
	}

	/**
	 * Returns the abbreviation for this class.
	 * 
	 * @return The abbreviation string.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getAbbrev() {
		return abbrev;
	}

	/**
	 * Sets the Key name for the class.
	 * 
	 * <p>
	 * This overrides the <tt>setKeyName</tt> method in <tt>PObject</tt> to
	 * also set the spell key for the class.
	 * 
	 * @param aKey
	 *            A (non-internationalized) string used to refer to this class
	 *            object.
	 * 
	 * @see pcgen.core.PObject#setKeyName(String)
	 */
	/*
	 * PCCLASSANDLEVEL Since the classKey is generally the universal index of whether
	 * two PCClassLevels are off of the same base, classKey will be populated into 
	 * each PCClassLevel.  This method must therefore also be in both PCClass and 
	 * PCClassLevel
	 */
	@Override
	public void setKeyName(final String aKey) {
		super.setKeyName(aKey);
		classKey = "CLASS:" + keyName; //$NON-NLS-1$
	}

	/**
	 * Return the qualified key, usually used as the source in a
	 * getVariableValue call. Overriden here to return CLASS:keyname
	 * 
	 * @return The qualified key of the object
	 */
	/*
	 * PCCLASSANDLEVEL Since the classKey is generally the universal index of whether
	 * two PCClassLevels are off of the same base, classKey will be populated into 
	 * each PCClassLevel.  This method must therefore also be in both PCClass and 
	 * PCClassLevel
	 */
	@Override
	public String getQualifiedKey() {
		return classKey;
	}

	/**
	 * Returns the list of domains that this class grants access to.
	 * 
	 * <p>
	 * The list returned actually consists of a level|domainkey|domainkey
	 * series.
	 * <p>
	 * TODO - Fix this.
	 * 
	 * @return List of strings representing additional domain choices for this
	 *         class.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getAddDomains() {
		if (addDomains == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(addDomains);
	}

	/**
	 * Sets the CASTAS: key for this class.
	 * 
	 * @param aString
	 *            The class key to use to determine casting ability for this
	 *            class.
	 * @deprecated The way to replicate another classes spellcasting
	 *             capabilities is to copy that class' CAST, KNOWN and other
	 *             spell related tags into the new class and then use the
	 *             SPELLLIST tag to assign it the spell list from the class
	 *             being replicated.
	 * 
	 */
	/*
	 * DELETEMETHOD Remove this as castas is removed
	 */
	@Deprecated
	public final void setCastAs(final String aString) {
		castAs = aString;
	}

	/**
	 * returns the CASTAS: tag for this class, or just the name of the class if
	 * one hasn't been set
	 * 
	 * @return Class key to use when looking up spell ability functions.
	 * 
	 * @deprecated
	 * 
	 */
	/*
	 * DELETEMETHOD Remove this as CastAs is removed
	 */
	@Deprecated
	public final String getCastAs() {
		if (castAs == null || castAs.equals(Constants.EMPTY_STRING))
			return keyName;
		return castAs;
	}

	/**
	 * Method sets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 * 
	 * @author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 * @param baseStat
	 *            Stat abbreviation to use as bonus spell stat.
	 */
	/*
	 * PCCLASSANDLEVEL Since this comes from a tag and is requried by at least
	 * some PCClassLevels in order to calculate bonus spells, this must appear
	 * in both PCClass and PCClassLevel
	 */
	public final void setBonusSpellBaseStat(final String baseStat) {
		bonusSpellBaseStat = baseStat;
	}

	/**
	 * Method gets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 * 
	 * @author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 * 
	 * @return Stat abbreviation that should be used to calculate bonus spells.
	 */
	/*
	 * PCCLASSANDLEVEL Since this comes from a tag and is requried by at least
	 * some PCClassLevels in order to calculate bonus spells, this must appear
	 * in both PCClass and PCClassLevel
	 */
	public final String getBonusSpellBaseStat() {
		return bonusSpellBaseStat;
	}

	/**
	 * Returns the total bonus to the specified bonus type and name.
	 * 
	 * <p>
	 * This method checks only bonuses associated with the class. It makes sure
	 * to return bonuses that are active only to the max level specified. What
	 * that means is that bonuses specified on class level lines will have a
	 * level parameter associated with them. Only bonuses specified on the level
	 * specified or lower will be totalled.
	 * 
	 * @param argType
	 *            Bonus type e.g. <code>BONUS:<b>DOMAIN</b></code>
	 * @param argMname
	 *            Bonus name e.g. <code>BONUS:DOMAIN|<b>NUMBER</b></code>
	 * @param asLevel
	 *            The maximum level to apply bonuses for.
	 * @param aPC
	 *            The <tt>PlayerCharacter</tt> bonuses are being calculated
	 *            for.
	 * 
	 * @return Total bonus value.
	 */
	/*
	 * REFACTOR There is potentially redundant information here - level and PC... 
	 * is this ever out of sync or can this method be removed/made private??
	 */
	public double getBonusTo(final String argType, final String argMname,
			final int asLevel, final PlayerCharacter aPC) {
		double i = 0;

		if ((asLevel == 0) || getBonusList().isEmpty()) {
			return 0;
		}

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();

		for (final BonusObj bonus : getBonusList()) {
			final StringTokenizer breakOnPipes = new StringTokenizer(bonus
					.toString().toUpperCase(), Constants.PIPE, false);
			final int aLevel = Integer.parseInt(breakOnPipes.nextToken());
			final String theType = breakOnPipes.nextToken();

			if (!theType.equals(type)) {
				continue;
			}

			final String str = breakOnPipes.nextToken();
			final StringTokenizer breakOnCommas = new StringTokenizer(str,
					Constants.COMMA, false);

			while (breakOnCommas.hasMoreTokens()) {
				final String theName = breakOnCommas.nextToken();

				if ((aLevel <= asLevel) && theName.equals(mname)) {
					final String aString = breakOnPipes.nextToken();
					final List<Prerequisite> localPreReqList = new ArrayList<Prerequisite>();
					if (bonus.hasPreReqs()) {
						localPreReqList.addAll(bonus.getPreReqList());
					}

					// TODO: This code should be removed after the 5.8 release
					// as the prereqs are processed by the bonus loading code.
					while (breakOnPipes.hasMoreTokens()) {
						final String bString = breakOnPipes.nextToken();

						if (bString.startsWith("PRE")
								|| bString.startsWith("!PRE")) {
							Logging
									.debugPrint("Why is this prerequisite '" + bString + "' parsed in '" + getClass().getName() + ".getBonusTo(String,String,int)' rather than in the persistence layer?"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
							try {
								final PreParserFactory factory = PreParserFactory
										.getInstance();
								localPreReqList.add(factory.parse(bString));
							} catch (PersistenceLayerException ple) {
								Logging.errorPrint(ple.getMessage(), ple);
							}
						}
					}

					// must meet criteria for bonuses before adding them in
					// TODO: This is a hack to avoid VARs etc in class defs
					// being qualified for when Bypass class prereqs is
					// selected.
					// Should we be passing in the BonusObj here to allow it to
					// be referenced in Qualifies statements?
					if (PrereqHandler.passesAll(localPreReqList, aPC, null)) {
						final double j = aPC
								.getVariableValue(aString, classKey)
								.doubleValue();
						i += j;
					}
				}
			}
		}

		return i;
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param pcLevel
	 *            The number of levels in this class that the character has
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel,
			final String bookName, final PlayerCharacter aPC) {
		return getCastForLevel(pcLevel, spellLevel, bookName, true, aPC);
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param pcLevel
	 *            The number of levels in this class that the character has
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param includeAdj
	 *            Seems to have something to do with speciality spells
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel,
			final String bookName, final boolean includeAdj,
			final PlayerCharacter aPC) {
		return getCastForLevel(pcLevel, spellLevel, bookName, includeAdj, true,
				aPC);
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param pcLevel
	 *            The number of levels in this class that the character has
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param includeAdj
	 *            Seems to have something to do with speciality spells
	 * @param limitByStat
	 *            Do we return 0 for any spell level that the character does not
	 *            have a high enough stat to cast
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	public int getCastForLevel(int pcLevel, final int spellLevel,
			final String bookName, final boolean includeAdj,
			final boolean limitByStat, final PlayerCharacter aPC) {
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE."
				+ getSpellType());

		if (getNumFromCastList(pcLevel, spellLevel, aPC) < 0) {
			// can't cast spells of this level
			// however, character might have a bonus spell slot e.g. from
			// certain feats
			return (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
					+ levelSpellLevel);
		}

		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
				+ levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE."
				+ getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any"
				+ levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
				+ allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE."
				+ getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any"
				+ allSpellLevel);

		final int index = bonusSpellIndex();

		final PCStat aStat;

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size())) {
			aStat = aPC.getStatList().getStatAt(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0) {
			statString = SettingsHandler.getGame().s_ATTRIBSHORT[index];
		}

		final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "CAST."
				+ statString)
				+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT")
				+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS."
						+ keyName);

		if ((index > -2) && limitByStat) {
			final int maxSpellLevel = aPC.getVariableValue(
					"MAXLEVELSTAT=" + statString, "").intValue();

			if ((maxSpellLevel + bonusStat) < spellLevel) {
				return total;
			}
		}

		stat += bonusStat;

		// Now we decide whether to adjust the number of slots down
		// the road by adding specialty slots.
		// Reworked to consider the fact that a lower-level
		// specialty spell can go into this level of specialty slot
		//
		int adj = 0;

		if (includeAdj
				&& !bookName.equals(Globals.getDefaultSpellBook())
				&& ((getSpecialtyList().size() > 0) || (aPC
						.getCharacterDomainList().size() > 0))) {
			// We need to do this for EVERY spell level up to the
			// one really under consideration, because if there
			// are any specialty spells available BELOW this level,
			// we might wind up using THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ++ix) {
				final List<CharacterSpell> aList = getSpellSupport()
						.getCharacterSpell(null, Constants.EMPTY_STRING, ix);
				List<Spell> bList = new ArrayList<Spell>();

				if (!aList.isEmpty()) {
					if ((ix > 0) && "DIVINE".equalsIgnoreCase(spellType)) {
						for (CharacterDomain cd : aPC.getCharacterDomainList()) {
							if (cd.isFromPCClass(getKeyName())
									&& (cd.getDomain() != null)) {
								bList = Globals.getSpellsIn(ix,
										Constants.EMPTY_STRING, cd.getDomain()
												.getKeyName());
							}
						}
					}

					for (CharacterSpell cs : aList) {
						int x = -1;

						if (!bList.isEmpty()) {
							if (bList.contains(cs.getSpell())) {
								x = 0;
							}
						} else {
							x = cs.getInfoIndexFor(Constants.EMPTY_STRING, ix,
									1);
						}

						if (x > -1) {
							adj = 1;

							break;
						}
					}
				}
				// end of what to do if aList is not empty

				if (adj == 1) {
					break;
				}
			}
			// end of looping up to this level looking for specialty spells that
			// can be cast
		}
		// end of deciding whether there are specialty slots to distribute

		int mult = (int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName
				+ levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE."
				+ getSpellType() + levelSpellLevel);

		if (mult < 1) {
			mult = 1;
		}

		final int t = getNumFromCastList(pcLevel, spellLevel, aPC);

		total += ((t * mult) + adj);

		// TODO - God I hate all these strings. Return an array or list.
		final String bonusSpell = Globals.getBonusSpellMap().get(
				String.valueOf(spellLevel));

		// TODO - Yuck. Figure out how to get rid of hardcoded "0|0"

		if ((bonusSpell != null) && !bonusSpell.equals("0|0")) //$NON-NLS-1$
		{
			final StringTokenizer s = new StringTokenizer(bonusSpell,
					Constants.PIPE);
			final int base = Integer.parseInt(s.nextToken());
			final int range = Integer.parseInt(s.nextToken());

			if (stat >= base) {
				total += Math.max(0, (stat - base + range) / range);
			}
		}

		return total;
	}

	/**
	 * Set the Class Skill string
	 * 
	 * @param aString
	 */
	/*
	 * STRINGREFACTOR This is currently passing in a 1,A|B String that needs to
	 * be parsed back in the SKILLLIST tag
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since it is 
	 * a Tag
	 */
	public final void setClassSkillString(final String aString) {
		classSkillString = aString;
	}

	/**
	 * Return the value set by the SKILLLIST token
	 * 
	 * @return The pipe-delimited list of class skills
	 */
	/*
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getClassSkillString() {
		return classSkillString;
	}

	/*
	 * REFACTOR this to get it out into the exporttoken class (is that possible?)
	 */
	public List<String> getClassSpecialAbilityList(final PlayerCharacter aPC) {
		final List<String> aList = new ArrayList<String>();
		final List<String> formattedList = new ArrayList<String>();
		final List<SpecialAbility> abilityList = getListFor(ListKey.SPECIAL_ABILITY);

		//
		// Determine the list of abilities from this class
		// that the character is eligable for
		//
		if (abilityList == null) {
			return aList;
		}

		if (!abilityList.isEmpty()) {
			for (SpecialAbility saAbility : abilityList) {
				final String aString = saAbility.toString();

				boolean found = false;

				// TODO shouldn't this be
				// if ( aList.contains( aString ) )
				for (String str : aList) {
					if (aString.equals(str)) {
						found = true;

						break;
					}
				}

				if (!found && saAbility.pcQualifiesFor(aPC)) {
					aList.add(aString);
				}
			}
		}

		//
		// From the list of allowed SAs, format the output strings
		// to include all of the variables
		for (String str : aList) {
			StringTokenizer varTok = new StringTokenizer(str, Constants.PIPE,
					false);
			final String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();

			if (varCount != 0) {
				varValue = new int[varCount];

				for (int j = 0; j < varCount; ++j) {
					// Get the value for each variable
					final String vString = varTok.nextToken();
					varValue[j] = aPC.getVariable(vString, true, true, "", "",
							0).intValue();
				}
			}

			final StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;

			boolean isZero = false;

			// Fill in each % with the value of the appropriate token
			while (varTok.hasMoreTokens()) {
				final String nextTok = varTok.nextToken();

				if ("%".equals(nextTok)) {
					if (varCount == 0) {
						// If this is the first token, then set the count of
						// successfull token replacements to 0
						isZero = true;
					}

					if ((varValue != null) && (varCount < varValue.length)) {
						final int thisVar = varValue[varCount++];

						// Update isZero if this token has a value of anything
						// other than 0
						isZero &= (thisVar == 0);
						newAbility.append(thisVar);
					} else {
						newAbility.append('%');
					}
				} else {
					newAbility.append(nextTok);
				}
			}

			if (!isZero) {
				// If all of the tokens for this ability were 0 then we do not
				// show it,
				// otherwise we add it to the return list.
				formattedList.add(newAbility.toString());
			}
		}

		return formattedList;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public final List<String> getClassSpellList() {
		return classSpellList;
	}

	/*
	 * PCCLASSANDLEVEL This is appropriate for both PCClassLevel and 
	 * PCClass since it is a Tag
	 */
	public final void setDeityList(final List<String> aDeityList) {
		// deityList must be a concrete list so we can clone it,
		// but we can not guarantee that the list passed in is
		// a ArrayList, so we have to copy the entries.
		// This should not be onerous as it is done infrequently
		// and the lists are short (1-2 entries).
		this.deityList = new ArrayList<String>(aDeityList);
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getDeityList() {
		return deityList;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/**
	 * Returns the Domains provided to a character up to and including
	 * the given level
	 * 
	 * There is a contract on the users of getDomainList: If you take 
	 * a Domain out of the List returned, you MUST clone the Domain or you 
	 * can cause problems for PCClass.  This is allowed for speed today and
	 * in the hopes that Domain and the PObjects become immutable soon :)
	 */
	public final List<Domain> getDomainList(int domainLevel) {
		if (domainList == null) {
			final List<Domain> ret = Collections.emptyList();
			return ret;
		}
		List<Domain> returnList = new ArrayList<Domain>();
		for (LevelProperty prop : domainList)
		{
			if (prop.getLevel() <= domainLevel)
			{
				returnList.add((Domain) prop.getObject());
			}
		}
		return returnList;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since it
	 * is a Tag
	 */
	public final void setExClass(final String aString) {
		exClass = aString;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getExClass() {
		return exClass;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final Collection<String> getFeatAutos() {
		if (featAutos == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(featAutos);
	}

//	public Collection<String> getAutoAbilityList(final AbilityCategory aCategory)
//	{
//		if ( aCategory == AbilityCategory.FEAT )
//		{
//			return getFeatAutos();
//		}
//		if ( theAutoAbilities == null )
//		{
//			return Collections.emptyList();
//		}
//		final List<String> ret = new ArrayList<String>();
//		for ( final int lvl : theAutoAbilities.getSecondaryKeySet(aCategory) )
//		{
//			if ( lvl <= level )
//			{
//				ret.addAll(theAutoAbilities.get(aCategory, lvl));
//			}
//		}
//		return Collections.unmodifiableList(ret);
//	}
	/**
	 * Removes an AUTO feat from the list of feats this class grants.
	 * 
	 * @param aLevel
	 *            The level the feat would have been granted at.
	 * @param aFeat
	 *            The feat string to remove.
	 */
	/*
	 * PCCLASSONLY This is for GUI construction of a PCClass and is therefore
	 * only required in PCClass and not PCClassLevel
	 */
	public final void removeFeatAuto(final int aLevel, final String aFeat) {
		if (featAutos == null) {
			return;
		}
		featAutos.remove(Integer.toString(aLevel) + Constants.PIPE + aFeat);
	}

//	public void removeAutoAbility(final AbilityCategory aCategory, final int aLevel, final String aKey)	
//	{
//		if ( aCategory == AbilityCategory.FEAT )
//		{
//			removeFeatAuto(aLevel, aKey);
//			return;
//		}
//		if ( theAutoAbilities == null )
//		{
//			return;
//		}
//		final List<String> abilities = theAutoAbilities.get(aCategory, aLevel);
//		if ( abilities != null )
//		{
//			abilities.remove(aKey);
//		}
//	}
	
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getFeatList() {
		if (featList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(featList);
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void setHitDie(final int dice) {
		hitDie = dice;
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public int getBaseHitDie() {
		return hitDie;
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (with PCClassLevel not having the level argument, of
	 * course)
	 */
	public void putHitDieLock(final String hitDieLock, final int aLevel) {
		if (hitDieLockMap == null) {
			hitDieLockMap = new HashMap<Integer, String>();
		}
		hitDieLockMap.put(aLevel, hitDieLock);
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory) (with level dependent
	 * differences, of course)
	 */
	protected String getHitDieLock(final int aLevel) {
		if (hitDieLockMap == null) {
			return null;
		}
		return hitDieLockMap.get(aLevel);
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void setInitialFeats(final int feats) {
		initialFeats = feats;
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final int getInitialFeats() {
		return initialFeats;
	}

	/*
	 * PCCLASSONLY Since this is a reference variable, it will likely
	 * only appear in PCCLASS
	 */
	public final void setItemCreationMultiplier(
			final String argItemCreationMultiplier) {
		itemCreationMultiplier = argItemCreationMultiplier;
	}

	/*
	 * PCCLASSONLY Since this is a reference variable, it will likely
	 * only appear in PCCLASS
	 */
	public final String getItemCreationMultiplier() {
		return itemCreationMultiplier;
	}

	/*
	 * PCCLASSLEVELONLY This is only relevant for the PCClassLevel (obviously?)
	 */
	public final int getLevel() {
		return level;
	}

	/*
	 * UNKNOWNDESTINATION Because this is a VERY strange variable and function,
	 * I have yet to architect exactly how this will work in the PCGen system.
	 * There will end up having to do some form of verification across multiple
	 * PCClassLevels, so this is really a similar solution to how the Challenge
	 * Rating (CRFormula) works.
	 */
	public final void setLevelExchange(final String aString) {
		levelExchange = aString;
	}

	/*
	 * UNKNOWNDESTINATION Because this is a VERY strange variable and function,
	 * I have yet to architect exactly how this will work in the PCGen system.
	 * There will end up having to do some form of verification across multiple
	 * PCClassLevels, so this is really a similar solution to how the Challenge
	 * Rating (CRFormula) works.
	 */
	public final String getLevelExchange() {
		return levelExchange;
	}

	/**
	 * set the level to arg without impacting spells, hp, or anything else - use
	 * this with great caution only TODO Then why is it even here, What is it
	 * used for (JSC 07/21/03)
	 * 
	 * @param arg
	 */
	/*
	 * DELETEMETHOD This method is NOT appropriate for either PCClass or
	 * PCClassLevel.  The equivalent functionality in order to sustain the 
	 * maxbablevel and maxcheckslevel globals will have to be done by
	 * PlayerCharacter as it filters out the PCClassLevels that are allowed
	 * to be used for those calculations.
	 */
	public final void setLevelWithoutConsequence(final int arg) {
		level = arg;
	}

	/*
	 * PCCLASSLEVELONLY maxLevel is only required for factory creation/verification
	 * of a PCClassLevel
	 */
	public final void setMaxLevel(final int maxLevel) {
		this.maxLevel = maxLevel;
	}

	/*
	 * PCCLASSLEVELONLY maxLevel is only required for factory creation/verification
	 * of a PCClassLevel
	 */
	public final int getMaxLevel() {
		return maxLevel;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (because they grant spells)
	 */
	public final void setMemorizeSpells(final boolean memorizeSpells) {
		this.memorizeSpells = memorizeSpells;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (because they grant spells)
	 */
	public final boolean getMemorizeSpells() {
		return memorizeSpells;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (for later verification)
	 */
	public final void setMultiPreReqs(final boolean multiPreReqs) {
		this.multiPreReqs = multiPreReqs;
	}

	public final void setPreRaceType(final String preRaceType) {
		this.preRaceType = preRaceType.toUpperCase();
	}

	// public final void setSkillPool(final Integer argSkillPool)
	// {
	// skillPool = argSkillPool;
	// }

	/*
	 * REFACTOR This is BAD that this is referring to PCLevelInfo - that gets
	 * VERY confusing as far as object interaction. Can we get rid of
	 * PCLevelInfo altogether?
	 */
	public final int getSkillPool(final PlayerCharacter aPC) {
		int returnValue = 0;
		// //////////////////////////////////
		// Using this method will return skills for level 0 even when there is
		// no information
		// Byngl - December 28, 2004
		// for (int i = 0; i <= level; i++)
		// {
		// final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), i);
		//
		// if ((pcl != null) && pcl.getClassKeyName().equals(getKeyName()))
		// {
		// returnValue += pcl.getSkillPointsRemaining();
		// }
		// }
		for (PCLevelInfo pcl : aPC.getLevelInfo()) {
			if (pcl.getClassKeyName().equals(getKeyName())) {
				returnValue += pcl.getSkillPointsRemaining();
			}
		}
		// //////////////////////////////////

		return returnValue;
	}

	/*
	 * DELETEMETHOD This is associated with the unused variable specialsString
	 */
	public final void setSpecialsString(final String aString) {
		specialsString = aString;
	}

	/*
	 * PCCLASSLEVELONLY created during PCClassLevel creation (in the factory)
	 */
	public final Collection<String> getSpecialtyList() {
		if (specialtyList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(specialtyList);
	}

	/*
	 * PCCLASSLEVELONLY Input during construction of a PCClassLevel
	 */
	public final void addSpecialty(final String aSpecialty) {
		if (specialtyList == null) {
			specialtyList = new ArrayList<String>();
		}
		specialtyList.add(aSpecialty);
	}

	/**
	 * 
	 * @param level
	 * @param aString
	 *            <p>
	 *            TODO - Fix this to not store level|key. Ideally should store a
	 *            <tt>LevelProperty</tt> object with a <tt>Domain</tt>
	 *            reference.
	 */
	/*
	 * STRINGREFACTOR This needs to store a LevelProperty object that contains a
	 * Domain, NOT a String, especially a | delimited String... !!
	 */
	/*
	 * TYPESAFETY should also be introduced by String refactoring from
	 * domainList
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (of course, a level independent version for PCClassLevel
	 */
	public void addAddDomain(final int level, final String aString) {
		final String prefix = Integer.toString(level) + Constants.PIPE;

		if (addDomains == null) {
			addDomains = new ArrayList<String>();
		}
		addDomains.add(prefix + aString);
	}

	/*
	 * PCCLASSANDLEVEL This can be simplified, however, since there won't be the
	 * same subClass type delegation within the new PCClassLevel.
	 */
	/*
	 * Note this is NOT a bug in that this handles subLevel but does not do any
	 * work for substitution levels... remember that substitution levels are for
	 * a certain advancement level, not across the entire Class level
	 * progression.
	 */
	public String getDisplayClassName() {
		if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE)) {
			return getSubClassKeyed(subClassKey).getDisplayName();
		}

		return displayName;
	}

	/*
	 * PCCLASSANDLEVEL This can be simplified, however, since there won't be the
	 * same subClass type delegation within the new PCClassLevel. - note this
	 * method is really the PCClassLevel implementation of getDisplayClassName()
	 * above [so technically this method doesn't go into PCClass, the method
	 * above does)
	 */
	public String getDisplayClassName(final int aLevel) {
		String aKey = getSubstitutionClassKey(aLevel);
		if (aKey == null) {
			return getDisplayClassName();
		}
		String name = getSubstitutionClassKeyed(aKey).getDisplayName();
		if (name == null) {
			return getDisplayClassName();
		}

		return name;
	}

	/*
	 * PCCLASSLEVELONLY Must only be the PCClassLevel since this refers to the 
	 * level in the String that is returned.
	 */
	public String getFullDisplayClassName() {
		final StringBuffer buf = new StringBuffer();

		buf.append(getDisplayClassName());

		return buf.append(" ").append(level).toString();
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the subclass is activated, this is required in both locations.
	 */
	public final void setHasSubClass(final boolean arg) {
		hasSubClass = arg;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the substitution class is activated, this is required in both
	 * locations.
	 */
	public final void setHasSubstitutionClass(final boolean arg) {
		hasSubstitutionClass = arg;
	}

	/*
	 * PCCLASSANDLEVEL Since this is altering (or controlling the behavior of) 
	 * the castMap, this has to be both at the PCClass domain (since it's a tag)
	 * and at the PCClassLevel domain (since that is where the castMap is 
	 * active)
	 */
	public final void setHasSpellFormula(final boolean arg) {
		hasSpellFormulas = arg;
	}

	/*
	 * STRINGREFACTOR This is currently processed outside of this set (it is a ,
	 * delimited list) This processing needs to be moved back into the
	 * PROHIBITED Tag
	 */
	/*
	 * TYPESAFETY This is actually passing around DOMAINs of Spells, and thus
	 * can be made type safe.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass because it
	 * is a Tag
	 */
	public final void setProhibitedString(final String aString) {
		prohibitedString = aString;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getProhibitedString() {
		return prohibitedString;
	}

	/*
	 * PCCLASSLEVELONLY This is an active level calculation, and is therefore
	 * only appropriate in the PCClassLevel that has the particular Hit Die for
	 * which the calculation is required.
	 */
	public int getLevelHitDie(final PlayerCharacter aPC, final int classLevel) {
		// Class Base Hit Die
		int currHitDie = getLevelHitDieUnadjusted(aPC, classLevel);

		// Race
		String dieLock = aPC.getRace().getHitDieLock();
		if (dieLock.length() != 0) {
			currHitDie = calcHitDieLock(dieLock, currHitDie);
		}

		// Templates
		for (PCTemplate template : aPC.getTemplateList()) {
			if (template != null) {
				dieLock = template.getHitDieLock();
				if (dieLock.length() != 0) {
					currHitDie = calcHitDieLock(dieLock, currHitDie);
				}
			}
		}

		// Levels
		dieLock = getHitDieLock(classLevel);
		if (dieLock != null && dieLock.length() != 0) {
			currHitDie = calcHitDieLock(dieLock, currHitDie);
		}

		return currHitDie;
	}

	// HITDIE:num --- sets the hit die to num regardless of class.
	// HITDIE:%/num --- divides the classes hit die by num.
	// HITDIE:%*num --- multiplies the classes hit die by num.
	// HITDIE:%+num --- adds num to the classes hit die.
	// HITDIE:%-num --- subtracts num from the classes hit die.
	// HITDIE:%upnum --- moves the hit die num steps up the die size list
	// d4,d6,d8,d10,d12. Stops at d12.
	// HITDIE:%downnum --- moves the hit die num steps down the die size list
	// d4,d6,d8,d10,d12. Stops at d4.
	// Regardless of num it will never allow a hit die below 1.
	/*
	 * PCCLASSLEVELONLY This is an active level calculation, and is therefore
	 * only appropriate in the PCClassLevel that has the particular Hit Die for
	 * which the calculation is required.
	 */
	private int calcHitDieLock(String dieLock, final int currDie) {
		final int[] dieSizes = Globals.getDieSizes();
		int diedivide;

		StringTokenizer tok = new StringTokenizer(dieLock, Constants.PIPE);
		dieLock = tok.nextToken();
		String prereq = null;
		if (tok.hasMoreTokens()) {
			prereq = tok.nextToken();
		}

		if (prereq != null) {
			if (prereq.startsWith("CLASS.TYPE")) {
				if (!isType(prereq.substring(prereq.indexOf("=") + 1, prereq
						.length()))) {
					return currDie;
				}
			} else if (prereq.startsWith("CLASS=")) {
				if (!getKeyName().equals(
						prereq.substring(prereq.indexOf("="), prereq.length()))) {
					return currDie;
				}
			}
		}

		if (dieLock.startsWith("%/")) {
			diedivide = Integer.parseInt(dieLock.substring(2));

			if (diedivide <= 0) {
				diedivide = 1; // Idiot proof it. Stop Divide by zero errors.
			}

			diedivide = currDie / diedivide;
		} else if (dieLock.startsWith("%*")) {
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide *= currDie;
		} else if (dieLock.startsWith("%+")) { // possibly redundant with
												// BONUS:HD MAX|num
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide += currDie;
		} else if (dieLock.startsWith("%-")) { // possibly redundant with
												// BONUS:HD MAX|num if that will
												// take negative numbers.
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = currDie - diedivide;
		} else if (dieLock.startsWith("%up")) {
			diedivide = Integer.parseInt(dieLock.substring(3));

			// lock in valid values.
			if (diedivide > 4) {
				diedivide = 4;
			}

			if (diedivide < 0) {
				diedivide = 0;
			}

			for (int i = 3; i <= (7 - diedivide); ++i) {
				if (currDie == dieSizes[i]) {
					return dieSizes[i + diedivide];
				}
			}

			diedivide = dieSizes[7]; // If they went too high, they get maxed
										// out.
		} else if (dieLock.startsWith("%Hup")) {
			diedivide = Integer.parseInt(dieLock.substring(4));

			for (int i = 0; i < ((dieSizes.length) - diedivide); ++i) {
				if (currDie == dieSizes[i]) {
					return dieSizes[i + diedivide];
				}
			}

			diedivide = dieSizes[dieSizes.length]; // If they went too high,
													// they get maxed out.
		} else if (dieLock.startsWith("%down")) {
			diedivide = Integer.parseInt(dieLock.substring(5));

			// lock in valid values.
			if (diedivide > 4) {
				diedivide = 4;
			}

			if (diedivide < 0) {
				diedivide = 0;
			}

			for (int i = (3 + diedivide); i <= 7; ++i) {
				if (currDie == dieSizes[i]) {
					return dieSizes[i - diedivide];
				}
			}

			diedivide = dieSizes[3]; // Minimum valid if too low.
		} else if (dieLock.startsWith("%Hdown")) {
			diedivide = Integer.parseInt(dieLock.substring(5));

			for (int i = diedivide; i < dieSizes.length; ++i) {
				if (currDie == dieSizes[i]) {
					return dieSizes[i - diedivide];
				}
			}

			diedivide = dieSizes[0]; // floor them if they're too low.
		} else {
			diedivide = Integer.parseInt(dieLock);
		}

		if (diedivide <= 0) {
			diedivide = 1; // Idiot proof it.
		}
		return diedivide;
	}

	/*
	 * PCCLASSLEVELONLY This is an active level calculation, and is therefore
	 * only appropriate in the PCClassLevel that has the particular Hit Die for
	 * which the calculation is required.
	 */
	public final int getLevelHitDieUnadjusted(final PlayerCharacter aPC,
			final int classLevel) {
		if ("None".equals(subClassKey)) {
			return hitDie;
		}
		final SubClass aSubClass = getSubClassKeyed(subClassKey);
		if (aSubClass != null) {
			return aSubClass.getLevelHitDie(aPC, classLevel);
		}
		return hitDie;
	}

	/*
	 * sets whether stat modifier is applied to skill points at level-up time
	 */
	public final void setModToSkills(final boolean bool) {
		modToSkills = bool;
	}

	public final boolean getModToSkills() {
		return modToSkills;
	}

	/*
	 * PCCLASSANDLEVEL Since this is a tag, and also impacts the number of
	 * skills at a particular level, this (or perhaps just the result?) needs to
	 * be in both PCClass and PCClassLevel
	 */
	public final void setSkillPointFormula(final String argFormula) {
		skillPointFormula = argFormula;
	}

	/*
	 * PCCLASSANDLEVEL Since this is a tag, and also impacts the number of
	 * skills at a particular level, this (or perhaps just the result?) needs to
	 * be in both PCClass and PCClassLevel
	 */
	public String getSkillPointFormula() {
		return skillPointFormula;
	}

	// public final void setSkillPoints(final int points)
	// {
	// skillPoints = points;
	// }

	// public int getSkillPoints()
	// {
	// return skillPoints;
	// }

	/*
	 * PCCLASSLEVELONLY since the specialtyList is 
	 * created during PCClassLevel creation (in the factory)
	 */
	public String getSpecialtyListString(final PlayerCharacter aPC) {
		final StringBuffer retString = new StringBuffer();

		for (final String spec : getSpecialtyList()) {
			if (retString.length() > 0) {
				retString.append(',');
			}

			retString.append(spec);
		}

		for (CharacterDomain cd : aPC.getCharacterDomainList()) {
			if (cd.getDomain() != null) {
				if (retString.length() > 0) {
					retString.append(',');
				}

				retString.append(cd.getDomain().getKeyName());
			}
		}

		return retString.toString();
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void setSpellBaseStat(final String baseStat) {
		spellBaseStat = baseStat;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellBaseStat() {
		return spellBaseStat;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the class spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public String getSpellKey() {
		if (stableSpellKey != null) {
			return stableSpellKey;
		}

		if (classSpellList == null) {
			chooseClassSpellList();

			if (classSpellList == null) {
				stableSpellKey = "CLASS" + Constants.PIPE + keyName;

				return stableSpellKey;
			}
		}

		final StringBuffer aBuf = new StringBuffer();

		for (String keyStr : classSpellList) {
			if (aBuf.length() > 0) {
				aBuf.append(Constants.PIPE);
			}

			if (keyStr.endsWith("(Domain)")) {
				aBuf.append("DOMAIN").append(Constants.PIPE).append(
						keyStr.substring(0, keyStr.length() - 8));
			} else {
				aBuf.append("CLASS").append(Constants.PIPE).append(keyStr);
			}
		}

		stableSpellKey = aBuf.toString();

		return stableSpellKey;
	}

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch.
	 */
	/*
	 * TYPESAFETY This is throwing around Spell names as Strings. :(
	 */
	/*
	 * BUG This is currently NOT processed correctly by PCClass or PCGen, in
	 * that duplicate spell casting ability is never assigned.
	 */
	/*
	 * UPPERLEVELPREREQ classSpellString is supposed to allow the spell use of
	 * another class to this PCClass. Because it is possible to assign only a
	 * subset of the items in the CLASSSPELL tag (if the tag is
	 * CLASSSPELL:2,Druid|Ranger|Sorcerer, for example, only two of those are
	 * used), the later levels (after level 1) have a prerequisite that the
	 * CLASSSPELL assignments stay the same. Thus, this requires a prerequisite
	 * test of some sort on upper levels of this PCClass to ensure it is
	 * consistent with the lower levels (or first level) of this PCClass
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass
	 * because it is a Tag
	 */
	public final void setSpellLevelString(final String aString) {
		classSpellString = aString;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellLevelString() {
		return classSpellString;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since 
	 * it is a Tag
	 */
	public final void setSpellType(final String newType) {
		spellType = newType;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType() {
		return spellType;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since
	 * it is a Tag [with level dependent differences, of course)
	 */
	public void setCastMap(final int aLevel, final String cast) {
		if (castMap == null) {
			castMap = new HashMap<Integer, String>();
		}
		if (aLevel > maxCastLevel) {
			maxCastLevel = aLevel;
		}
		castMap.put(aLevel, cast);
	}

	/**
	 * Return CAST: string for a level
	 * 
	 * @param aLevel
	 * @return String
	 */
	/*
	 * REFACTOR to DELETEMETHOD This should really be handled elsewhere, such
	 * as in an export Token or something that is the universal location for 
	 * recreating Strings out of more advanced structures.  PCClass and 
	 * PCClassLevel are not the place for this
	 */
	public String getCastStringForLevel(int aLevel) {
		if (castMap != null) {
			final int lvl = Math.min(aLevel, maxCastLevel);

			if (castMap.containsKey(lvl)) {
				return castMap.get(lvl);
			}
		}

		return Constants.EMPTY_STRING;
	}

	/*
	 * REFACTOR This really needs to be an external item to both PCClass and
	 * PCClassLevel, in some external Utility Class which is smart enough to do
	 * mass PCClassLevel processing. Thus this is really a candidate for
	 * DELETEMETHOD
	 */
	public int minLevelForSpellLevel(final int spellLevel,
			final boolean allowBonus) {
		int minLevel = Constants.INVALID_LEVEL;

		int loopMax = castMap.keySet().size();
		for (int i = 0; i < loopMax; i++) {
			final String castPerDay = castMap.get(i);

			if ((castPerDay == null) || (castPerDay.length() <= 0)) {
				continue;
			}

			final StringTokenizer bTok = new StringTokenizer(castPerDay, ",");
			int maxCastable = -1;

			if (allowBonus) {
				maxCastable = bTok.countTokens() - 1;
			} else {
				int j = 0;

				while (bTok.hasMoreTokens()) {
					try {
						if (Integer.parseInt(bTok.nextToken()) != 0) {
							maxCastable = j;
						}
					} catch (NumberFormatException ignore) {
						// ignore
					}

					j++;
				}
			}

			if (maxCastable >= spellLevel) {
				minLevel = i;

				break;
			}
		}

		if (minLevel < Constants.INVALID_LEVEL) {
			return minLevel;
		}

		loopMax = knownList.size();

		for (int i = 0; i < loopMax; ++i) {
			final String knownSpells = knownList.get(i);

			if ("0".equals(knownSpells)) {
				continue;
			}

			final StringTokenizer bTok = new StringTokenizer(knownSpells, ",");
			int maxCastable = -1;

			if (allowBonus) {
				maxCastable = bTok.countTokens() - 1;
			} else {
				int j = 0;

				while (bTok.hasMoreTokens()) {
					try {
						if (Integer.parseInt(bTok.nextToken()) != 0) {
							maxCastable = j;
						}
					} catch (NumberFormatException e) {
						// TODO: Should this really be ignored?
						Logging.errorPrint("", e);
					}

					j += 1;
				}
			}

			if (maxCastable >= spellLevel) {
				minLevel = i + 1;

				break;
			}
		}

		return minLevel;
	}

	/**
	 * Return the level of the highest level spell offered by the class.
	 * 
	 * @return The level of the highest level spell available.
	 */
	/*
	 * REFACTOR to eliminate the caching (highestSpellLevelMap is useless
	 */
	/*
	 * 
	 */
	public int getHighestLevelSpell() {
		// check to see if we have a cached value first
		if (highestSpellLevelMap != null) {
			final Integer highest = highestSpellLevelMap.get(-1);
			if (highest != null) {
				return highest.intValue();
			}
		}

		int highestCastable = -1;
		if (castMap != null) {
			for (final String entry : castMap.values()) {
				highestCastable = Math.max(highestCastable,
						entry.split(",").length - 1);
			}
		}

		// Highest Known spell for level
		final List<String> known = getKnownList();
		int highestKnown = -1;
		for (final String element : known) {
			highestKnown = Math
					.max(highestKnown, element.split(",").length - 1);
		}

		int highest = Math.max(highestCastable, highestKnown);

		// cache the value
		if (highestSpellLevelMap == null) {
			highestSpellLevelMap = new HashMap<Integer, Integer>();
			highestSpellLevelMap.put(-1, highest);
		}
		return highest;
	}

	/**
	 * Return the level of the highest level spell the character could possibly
	 * cast in this class. This can return a higher level than the class allows
	 * if the character has feats which give a bonus to casting.
	 * 
	 * @param pc
	 *            The character to be checked.
	 * @return The level of the highest level spell available.
	 */
	public int getHighestLevelSpell(PlayerCharacter pc) {
		final String classKeyName = "CLASS." + getKeyName();
		int mapHigh = getHighestLevelSpell();
		int high = mapHigh;
		for (int i = mapHigh; i < mapHigh + 30; i++) {
			final String levelSpellLevel = ";LEVEL." + i;
			if (pc.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel) > 0) {
				high = i;
			} else if (pc.getTotalBonusTo("SPELLKNOWN", classKeyName
					+ levelSpellLevel) > 0) {
				high = i;
			}
		}
		return high;
	}


	/**
	 * Return number of spells known for a level.
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 */
	/*
	 * REFACTOR This, like many of the spell counting methods, contains both
	 * redundant information (the PC and the PC Level) and also is rather ugly.
	 * This should really be part of PCClassLevel only, but some major
	 * rethinking of these methods needs to take place to minimize the number of
	 * them, and to make sure they are efficiently used.
	 */
	public int getKnownForLevel(final int pcLevel, final int spellLevel,
			final PlayerCharacter aPC) {
		return getKnownForLevel(pcLevel, spellLevel, "null", aPC);
	}

	/*
	 * PCCLASSANDLEVEL Some variant of this needs to be in both the PCClass
	 * (since it it from a tag) and PCClassLevel (since those will 'own' the
	 * feats).
	 */
	public void setLevelsPerFeat(final Integer newLevels) {
		if (newLevels.intValue() < 0) {
			return;
		}

		levelsPerFeat = newLevels;
	}

	/*
	 * PCCLASSANDLEVEL Some variant of this needs to be in both the PCClass
	 * (since it it from a tag) and PCClassLevel (since those will 'own' the
	 * feats).
	 */
	public final Integer getLevelsPerFeat() {
		if (levelsPerFeat == null) {
			/*
			 * REFACTOR This SHOULD NOT protect against a NPE, that should be up
			 * to the CALLING method to do that... protecting against bad coding
			 * is a bad practice.
			 */
			levelsPerFeat = Integer.valueOf(-1); // -1 to indicate it's not a
												// 'set' value, this is to avoid
												// null pointer errors
		}
		return levelsPerFeat;
	}

	/**
	 * if castAs has been set, return knownList from that class
	 * 
	 * @return List
	 */
	/*
	 * REFACTOR This contains castAs which is Cross-Class behavior and is VERY
	 * bad. The caller of this method or the method calling getKnownList() or
	 * whatever is setting the castAs should really be able to do some other
	 * check first, rather than using castAs. Since CASTAS is deprecated, this
	 * CASTAS functionality (hopefully) can be removed from PCClass and
	 * PCClassLevel
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<String> getKnownList() {
		if (Constants.EMPTY_STRING.equals(castAs)
				|| getKeyName().equals(castAs)) {
			if (knownList == null) {
				final List<String> ret = Collections.emptyList();
				return Collections.unmodifiableList(ret);
			}
			return knownList;
		}

		final PCClass aClass = Globals.getClassKeyed(castAs);

		if (aClass != null) {
			return aClass.getKnownList();
		}

		if (knownList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return knownList;
	}

	/**
	 * Return KNOWN: string for a level
	 * 
	 * @param aInt
	 *            The level of the class to be retrieved
	 * @return String The KNOWN string, if any
	 *         <p>
	 *         TODO - Why is this a String???
	 */
	/*
	 * PCCLASSLEVELONLY This is required in PCClassLevel since it is level
	 * dependent (though it no longer needs the int argument :) ). Note this
	 * should also be refactored (as part of the getKnownList() rebuild) to pass
	 * around something more intelligent than a String.
	 */
	public String getKnownStringForLevel(int aInt) {
		final List<String> known = getKnownList();

		if (aInt > maxKnownLevel) {
			aInt = maxKnownLevel;
		}
		if (aInt >= 0 && aInt < known.size()) {
			return known.get(aInt);
		}

		return Constants.EMPTY_STRING;
	}

	/**
	 * @return The list of automatically known spells.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<String> getKnownSpellsList() {
		if (knownSpellsList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(knownSpellsList);
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final Collection<String> getSpecialtyKnownList() {
		if (specialtyknownList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(specialtyknownList);
	}

	/**
	 * Adds the numeric value given to the number of specialty school spells
	 * that the class can cast per spell level.
	 * 
	 * <p>
	 * If not listed, the default value is 0.
	 * 
	 * @param aNumber
	 *            String version of the number of bonus spells.
	 * 
	 * <p>
	 * TODO - Why is this stored as a String????
	 * <p>
	 * TODO - Why is this a list?
	 */
	/*
	 * STRINGREFACTOR This should really be stored as an Array, not as a String,
	 * since it is listing the specialty known "spells" for a level.
	 * 
	 * In reality, one needs to consider whether this is stored as an array of
	 * arrays or whether this is yet another LevelProperty, and searches can be
	 * done from there.
	 */
	/*
	 * BUG This is not correctly accounting for the LEVEL of the SPECIALTYKNOWN.
	 * If SPECIALTYKNOWN did not appear in EACH AND EVERY level then something
	 * would break. It should be stored as a Map of levels, taking the level
	 * from the SPECIALTYKNOWN Tag call.
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void addSpecialtyKnown(final String aNumber) {
		if (specialtyknownList == null) {
			specialtyknownList = new ArrayList<String>(2);
		}
		specialtyknownList.add(aNumber);
	}

	/**
	 * Get the number of spells this PC can cast based on Caster Level and
	 * desired Spell Level ex: how many 5th level spells can a 17th level wizard
	 * cast?
	 * 
	 * @param iCasterLevel
	 * @param iSpellLevel
	 * @param aPC
	 *            The character we are interested in
	 * @return int
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	public int getNumFromCastList(final int iCasterLevel,
			final int iSpellLevel, final PlayerCharacter aPC) {
		int aNum = -1;
		if (iCasterLevel == 0) {
			// can't cast spells!
			return aNum;
		}

		if (castMap != null) {
			if (!castMap.containsKey(iCasterLevel)) {
				// Recurse in case we are actually past the end of a class's
				// definition - use the last enterd value
				return getNumFromCastList(iCasterLevel - 1, iSpellLevel, aPC);
			}
		}

		int iCount = 0;
		String aString = getCastStringForLevel(iCasterLevel);

		final StringTokenizer aTok = new StringTokenizer(aString, ",");

		while (aTok.hasMoreTokens()) {
			aString = aTok.nextToken();

			if (iCount == iSpellLevel) {
				if ((aPC != null) && hasSpellFormula()) {
					aNum = aPC.getVariableValue(aString, "").intValue();
				} else {
					try {
						aNum = Integer.parseInt(aString);
					} catch (NumberFormatException ex) {
						// ignore
						aNum = 0;
					}
				}
				return aNum;
			}

			++iCount;
		}

		return aNum;
	}

	/*
	 * REFACTOR This is a real challenge. This method is actually set from
	 * Domain, as Domain can actually grant domain spells as an addition to the
	 * PCClass. This is a challenge to define how this will actually play out in
	 * the new implementation (When PCClass is static and creates a
	 * PCClassLevel, where do the spells from the Domain come into play?? How
	 * are they added to the character?) Potentially they are added directly to
	 * the PCClassLevel and the refactoring to make PCClassLevel immutable will
	 * have to take place at a later time (As we approach 6.0)
	 */
	public final void setNumSpellsFromSpecialty(final int anInt) {
		numSpellsFromSpecialty = anInt;
	}

	/*
	 * REFACTOR Again, there is rudundant information here in the fetching of
	 * what is currently possible for the current character level. This is
	 * generally something that should only appear in the PCClassLevel, but
	 * should be considered with the wider range of "what can I really cast"
	 * methods that are tagged to be refactored.
	 */
	public String getBonusCastForLevelString(final int pcLevel,
			final int spellLevel, final String bookName,
			final PlayerCharacter aPC) {
		if (getCastForLevel(pcLevel, spellLevel, bookName, aPC) > 0) {
			// if this class has a specialty, return +1
			if (getSpecialtyList().size() > 0) {
				return "+1";
			}

			if (aPC.getCharacterDomainList().isEmpty()) {
				return "";
			}

			// if the spelllevel is >0 and this class has a characterdomain
			// associated with it, return +1
			if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(spellType)) {
				for (CharacterDomain cd : aPC.getCharacterDomainList()) {
					if (cd.isFromPCClass(getKeyName())) {
						return "+1";
					}
				}
			}
		}

		return "";
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param pcLevel
	 *            The number of levels in this class that the character has
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	/*
	 * REFACTOR Yea, Yea, yet another interface to the same internal methods... 
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel,
			final PlayerCharacter aPC) {
		return getCastForLevel(pcLevel, spellLevel, Globals
				.getDefaultSpellBook(), true, aPC);
	}

	/**
	 * Return number of speciality spells known for a level for a given
	 * spellbook
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 */
	/*
	 * REFACTOR More redundant information and opportunity to refactor to 
	 * simplify the interface of PCClassLevel
	 */
	public int getSpecialtyKnownForLevel(int pcLevel, final int spellLevel,
			final PlayerCharacter aPC) {
		int total;
		total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS."
				+ getKeyName() + ";LEVEL." + spellLevel);
		total += (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE."
				+ getSpellType() + ";LEVEL." + spellLevel);

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE."
				+ getSpellType());

		final int index = baseSpellIndex();

		if (index != -2) {
			final PCStat aStat = aPC.getStatList().getStatAt(index);
			final int maxSpellLevel = aPC.getVariableValue(
					"MAXLEVELSTAT=" + aStat.getAbb(), "").intValue();

			if (spellLevel > maxSpellLevel) {
				return total;
			}
		}

		StringTokenizer aTok;
		int x = spellLevel;

		/*
		 * REFACTOR This should really be calling a getSpecialtyKnownList(int
		 * level) method.
		 */
		for (final String aString : getSpecialtyKnownList()) {
			if (pcLevel == 1) {
				aTok = new StringTokenizer(aString, ",");

				while (aTok.hasMoreTokens()) {
					final String spells = aTok.nextToken();
					final int t = Integer.parseInt(spells);

					if (x == 0) {
						total += t;

						break;
					}

					--x;
				}
			}

			--pcLevel;

			if (pcLevel < 1) {
				break;
			}
		}

		// if we have known spells (0==no known spells recorded) or a psi
		// specialty.
		if ((total > 0) && (spellLevel > 0)) {
			// make sure any slots due from specialties (including domains) are
			// added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

	/*
	 * PCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (called during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public void setSubstitutionClassKey(final String aKey, final Integer aLevel) {
		if (substitutionClassKey == null) {
			substitutionClassKey = new HashMap<Integer, String>();
		}
		substitutionClassKey.put(aLevel, aKey);
	}

	/*
	 * PCCLASSLEVELONLY Since this is getting the key that will appear in
	 * the PCClassLevel (was set during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public String getSubstitutionClassKey(final Integer aLevel) {
		if (substitutionClassKey == null) {
			return null;
		}
		return substitutionClassKey.get(aLevel);
	}

	/*
	 * PCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (called during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public void setSubClassKey(final String aKey) {
		subClassKey = aKey;

		if (!aKey.equals(getKeyName())) {
			final SubClass a = getSubClassKeyed(aKey);

			if (a != null) {
				inheritAttributesFrom(a);
			}
		}

		stableSpellKey = null;
		getSpellKey();
	}

	/*
	 * PCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (was set during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public String getSubClassKey() {
		if (subClassKey == null) {
			subClassKey = "";
		}

		return subClassKey;
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final SubClass getSubClassKeyed(final String aKey) {
		if (subClassList == null) {
			return null;
		}

		for (SubClass subClass : subClassList) {
			if (subClass.getKeyName().equals(aKey)) {
				return subClass;
			}
		}

		return null;
	}

	/*
	 * PCCLASSONLY Since this is setting the possible subclasses, this is only
	 * part of PCClass, since the specific subclass key will be set during
	 * PCClassLevel construction
	 */
	public final void setSubClassString(final String aString) {
		subClassString = aString;
	}

	/*
	 * PCCLASSONLY Since this is setting the possible subclasses, this is only
	 * part of PCClass, since the specific subclass key will be set during
	 * PCClassLevel construction
	 */
	public final String getSubClassString() {
		return subClassString;
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final SubstitutionClass getSubstitutionClassKeyed(final String aKey) {
		if (substitutionClassList == null) {
			return null;
		}

		for (Iterator i = substitutionClassList.iterator(); i.hasNext();) {
			final SubstitutionClass a = (SubstitutionClass) i.next();

			if (a.getKeyName().equals(aKey)) {
				return a;
			}
		}

		return null;
	}

	/*
	 * PCCLASSONLY Since this is setting the possible substitution classes, this
	 * is only part of PCClass, since the specific substitution class key will
	 * be set during PCClassLevel construction
	 */
	public final void setSubstitutionClassString(final String aString) {
		substitutionClassString = aString;
	}

	/*
	 * PCCLASSONLY Since this is getting the possible substitution classes, this
	 * is only part of PCClass, since the specific substitution class key will
	 * be set during PCClassLevel construction
	 */
	public final String getSubstitutionClassString() {
		return substitutionClassString;
	}

	/*
	 * CONSIDER Should this be overriding getTemplateList from PObject?? (it
	 * isn't)
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<String> getTemplates() {
		if (templates == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(templates);
	}

	/*
	 * DELETEMETHOD I don't believe (?) that this is used in a place that
	 * actually has any effect???  Will (obviously) need to test that!!
	 */
	public void clearTemplates() {
		templates = null;
	}

	/**
	 * 
	 * @return uattList
	 *         <p>
	 *         TODO - This should be removed.
	 */
	/*
	 * DELETEMETHOD Associated with the unused List uattList
	 */
	public final Collection<String> getUattList() {
		return uattList;
	}

	/**
	 * Set whether or not this class should be displayed to the user in the UI.
	 * 
	 * @param visible
	 *            true if the class should be displayed to the user.
	 */
	/*
	 * DELETEMETHOD This should be refactored to use the setVisibility 
	 * and getVisibility methods of PObject.
	 */
	public final void setVisible(final boolean visible) {
		this.visible = visible;
	}

	/**
	 * Identify if this class should be displayed to the user in the UI.
	 * 
	 * @return true if the class should be displayed to the user.
	 */
	/*
	 * DELETEMETHOD This should be refactored to use the setVisibility 
	 * and getVisibility methods of PObject.
	 */
	public final boolean isVisible() {
		return visible;
	}

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch. The processing needs to be moved back
	 * into the FEATAUTO tag
	 * 
	 * In addition, this needs to be using LevelProperty here in PCClass, since
	 * it is level dependent.
	 */
	/*
	 * TYPESAFETY This is throwing around Feat names as Strings. :(
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method.  The PCClassLevelversion should NOT be level 
	 * dependent
	 */
	public void setFeatAutos(final int aLevel, final String aString) {
		final StringTokenizer aTok = new StringTokenizer(aString,
				Constants.PIPE);
		final String prefix = aLevel + Constants.PIPE;

		if (featAutos == null) {
			featAutos = new ArrayList<String>();
		}
		// TODO - This token processing should happen in the token.
		while (aTok.hasMoreTokens()) {
			final String fName = aTok.nextToken();

			if (fName.startsWith(".CLEAR")) {
				if (fName.startsWith(".CLEAR.")) {
					final String postFix = Constants.PIPE + fName.substring(7);

					// remove feat by name, must run through all 20 levels
					for (int i = 0; i < 45; ++i) {
						featAutos.remove(i + postFix);
					}
				} else // clear em all
				{
					featAutos.clear();
				}
			} else {
				featAutos.add(prefix + fName);
			}
		}
	}

//	public void setAutoAbilities(final AbilityCategory aCategory, final int aLevel, final List<String> aList)
//	{
//		if ( aCategory == AbilityCategory.FEAT )
//		{
//			setFeatAutos(aLevel, CoreUtility.join(aList, Constants.PIPE));
//			return;
//		}
//		if ( theAutoAbilities == null )
//		{
//			theAutoAbilities = new DoubleKeyMap<AbilityCategory, Integer, List<String>>();
//		}
//		List<String> abilities = theAutoAbilities.get(aCategory, aLevel);
//		if ( abilities == null )
//		{
//			abilities = new ArrayList<String>();
//		}
//		abilities.addAll(aList);
//	}
	
	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public void setHitPoint(final int aLevel, final Integer iRoll) {
		if (hitPointMap == null) {
			hitPointMap = new HashMap<Integer, Integer>();
		}
		hitPointMap.put(aLevel, iRoll);
	}

	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public int getHitPoint(final int aLevel) {
		if (hitPointMap == null) {
			return 0;
		}
		final Integer aHP = hitPointMap.get(aLevel);

		if (aHP == null) {
			return 0;
		}

		return aHP;
	}

	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public final void setHitPointMap(final PCClass otherClass) {
		hitPointMap = null;
		if (otherClass.hitPointMap != null) {
			hitPointMap = new HashMap<Integer, Integer>(otherClass.hitPointMap);
		}
	}

	/**
	 * <p>
	 * TODO - This should be removed
	 * 
	 * @param aLevel
	 * @return BAB for unarmed attacks
	 */
	/*
	 * DELETEMETHOD Associated with the unused List uattList
	 */
	public String getUattForLevel(int aLevel) {
		final String aString = "0";

		if (uattList.isEmpty()) {
			return aString;
		}

		for (String uatt : uattList) {
			if (aLevel == 1) {
				return uatt;
			}

			--aLevel;

			if (aLevel < 1) {
				break;
			}
		}

		return null;
	}

	/*
	 * PCCLASSANDLEVEL Since this is set in Level one of a PCClassLevel, it
	 * will need to be present in the PCClass (to handle import from the Tag) 
	 * and PCClassLevel
	 */
	public void setProhibitSpell(SpellProhibitor prohibitor) {
		if (prohibitSpellDescriptorList == null) {
			prohibitSpellDescriptorList = new ArrayList<SpellProhibitor>();
		}
		prohibitSpellDescriptorList.add(prohibitor);
	}

	/**
	 * we over ride the PObject setVision() function to keep track of what
	 * levels this VISION: tag should take effect
	 * 
	 * @param aString
	 * @param aPC
	 */
	/*
	 * REFACTOR The PlayerCharacter here is NEVER used in production code, only
	 * test code - that is bad behavior, and thus the reference to
	 * PlayerCharacter in this method should be removed from both PCClass and
	 * PObject
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel PCClass since it is
	 * a Tag [with level dependence differences, of course)
	 */
	public final void setVision(final String aString, final PlayerCharacter aPC) {
		/*
		 * STRINGREFACTOR This can be refactored out of PCClass and put into the
		 * VISION tag. Then the tag needs to build the visionMap (below) and
		 * properly load that map into PCClass.
		 */
		// Class based vision lines are of the form:
		// 1|Darkvision(60'),Lowlight
		if (".CLEAR".equals(aString)) {
			visionList = null;

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(aString,
				Constants.PIPE, false);
		final int lvl = Integer.parseInt(aTok.nextToken());
		final String newString = aString.substring(aString
				.indexOf(Constants.PIPE) + 1);

		if (visionList == null) {
			visionList = new ArrayList<LevelProperty>();
		}

		final LevelProperty lp = new LevelProperty(lvl, newString);
		visionList.add(lp);
	}

	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public boolean isAutoKnownSpell(final String spellName,
			final int spellLevel, final PlayerCharacter aPC) {
		return isAutoKnownSpell(spellName, spellLevel, false, aPC);
	}

	public void setLevel(final int newLevel, final PlayerCharacter aPC) {
		final int curLevel = level;

		if (newLevel >= 0) {
			level = newLevel;
		}

		if (level == 1) {
			if (level > curLevel) {
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();

					      StringBuffer    formula;
					final String          aString = Globals.getBonusFeatString();
					final StringTokenizer aTok    = new StringTokenizer(aString, "|", false);
					final int startLevel = Integer.parseInt(aTok.nextToken());
					final int rangeLevel = Integer.parseInt(aTok.nextToken());
					      int divisor    = 1;
					      
					
					if (aPC.getRace().getMonsterClass(aPC,false) != null &&
							aPC.getRace().getMonsterClass(aPC,false).equalsIgnoreCase(this.getKeyName()))
					{
						int monLev = aPC.getRace().getMonsterClassLevels(aPC, false);

						int MLevPerFeat = this.getLevelsPerFeat().intValue();
						divisor = (MLevPerFeat >= 1) ? MLevPerFeat : rangeLevel;
						formula = new StringBuffer("max(0,floor((CL-");
						formula.append(monLev);
						formula.append(")/");
						formula.append(divisor);
						formula.append("))");

						StringBuffer aBuf = new StringBuffer("0|FEAT|MONSTERPOOL|");
						aBuf.append(formula);
						BonusObj bon = Bonus.newBonus(aBuf.toString());
						bon.setCreatorObject(this);
						Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:Y");
						bon.addPreReq(prereq);
						addBonusList(bon);

					}
					else
					{
						divisor = rangeLevel;
						formula = new StringBuffer("CL/");
						formula.append(divisor);

						StringBuffer aBuf = new StringBuffer("0|FEAT|MONSTERPOOL|");
						aBuf.append(formula);
						BonusObj bon = Bonus.newBonus(aBuf.toString());
						bon.setCreatorObject(this);
						Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:Y");
						bon.addPreReq(prereq);
						addBonusList(bon);
					}

					StringBuffer aBuf = new StringBuffer("0|FEAT|PCPOOL|CL/");
					aBuf.append(rangeLevel);
					BonusObj bon = Bonus.newBonus(aBuf.toString());
					bon.setCreatorObject(this);
					Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:N");
					bon.addPreReq(prereq);
					addBonusList(bon);
				}

				catch (PersistenceLayerException e)
				{
					Logging.errorPrint("Caught " + e);
				}
			}
			
			chooseClassSkillList();
		}

		if (!aPC.isImporting()) {
			aPC.calcActiveBonuses();
			aPC.getSpellTracker().buildSpellLevelMap(newLevel);
		}

		if ((level == 1) && !aPC.isImporting() && (curLevel == 0)) {
			checkForSubClass(aPC);
			getSpellKey();
		}

		if (!aPC.isImporting()) {
			checkForSubstitutionClass(level, aPC);
		}

		for (PCClass pcClass : aPC.getClassList()) {
			pcClass.calculateKnownSpellsForClassLevel(aPC);
		}
	}

	/*
	 * PCCLASSLEVELONLY This really is modification of a PlayerCharacter from
	 * losing a level, so this is definitely part of PCClassLevel, if it is even
	 * used at all. Because this is losing a level, this will no longer be
	 * required in a CDOM world, perhaps this can be refactored out of existance
	 * before then?
	 * 
	 * May be DELETEMETHOD (related to subLevel)
	 */
	protected void removeKnownSpellsForClassLevel(final PlayerCharacter aPC) {
		final String spellKey = getSpellKey();

		if (((knownSpellsList != null) && (knownSpellsList.size() == 0)) || 
				aPC.isImporting() || !aPC.getAutoSpells())
		{
			return;
		}

		if (getSpellSupport().getCharacterSpellCount() == 0) {
			return;
		}

		for (Iterator<CharacterSpell> iter = getSpellSupport()
				.getCharacterSpellList().iterator(); iter.hasNext();) {
			final CharacterSpell charSpell = iter.next();

			final Spell aSpell = charSpell.getSpell();

			// Check that the character can still cast spells of this level.
			final int[] spellLevels = aSpell.levelForKey(spellKey, aPC);
			for (int i = 0; i < spellLevels.length; i++) {
				final int spellLevel = spellLevels[i];

				final boolean isKnownAtThisLevel = isAutoKnownSpell(aSpell
						.getKeyName(), spellLevel, true, aPC);

				if (!isKnownAtThisLevel) {
					iter.remove();
				}
			}
		}
	}

	/*
	 * PCCLASSLEVELONLY This modifies the PlayerCharacter to have the
	 * appropriate known spells for this classlevel. As this is called from
	 * setLevel (really from addLevel), this is clearly part of PCClassLevel. In
	 * fact, I wonder how much this can be refactored out (this is really a CDOM
	 * issue) and no longer be loading lots of gunk into the PlayerCharacter.
	 */
	protected void calculateKnownSpellsForClassLevel(final PlayerCharacter aPC) {
		// If this class has at least one entry in the "Known spells" tag
		// And we aer set up to automatically assign known spells...
		if (knownSpellsList != null && (knownSpellsList.size() > 0)
				&& !aPC.isImporting() && aPC.getAutoSpells()) {
			// Get every spell that can be cast by this class.
			final List<Spell> cspelllist = Globals.getSpellsIn(-1,
					getSpellKey(), Constants.EMPTY_STRING);
			if (cspelllist.isEmpty()) {
				return;
			}

			// Recalculate the number of spells per day of each level
			// that this chracter can cast in this class.
			calcCastPerDayMapForLevel(aPC);

			// Get the maximum spell level that this character can cast.
			final int _maxLevel = getMaxCastLevel();

			// Get the key for this class (i.e. "CLASS|Cleric")
			final String spellKey = getSpellKey();

			// For every spell that this class can ever cast.
			for (Spell spell : cspelllist) {
				// For each spell level that this class can cast this spell at
				final int[] spellLevels = spell.levelForKey(spellKey, aPC);
				for (int si = 0; si < spellLevels.length; ++si) {
					final int spellLevel = spellLevels[si];

					if (spellLevel <= _maxLevel) {
						// If the spell is autoknown at this level
						if (isAutoKnownSpell(spell.getKeyName(), spellLevel,
								true, aPC)) {
							CharacterSpell cs = getSpellSupport()
									.getCharacterSpellForSpell(spell, this);
							if (cs == null) {
								// Create a new character spell for this level.
								cs = new CharacterSpell(this, spell);
								cs.addInfo(spellLevel, 1, Globals
										.getDefaultSpellBook());
								getSpellSupport().addCharacterSpell(cs);
							} else {
								if (cs.getSpellInfoFor(Globals
										.getDefaultSpellBook(), spellLevel, -1) == null) {
									cs.addInfo(spellLevel, 1, Globals
											.getDefaultSpellBook());
								} else {
									// already know this one
								}
							}
						}
					}
				}
			}

			for (CharacterDomain cd : aPC.getCharacterDomainList()) {
				if ((cd.getDomain() != null) && cd.isFromPCClass(getKeyName())) {
					cd.getDomain()
							.addSpellsToClassForLevels(this, 0, _maxLevel);
				}
			}
		}
	}

	/**
	 * Get the highest level of spell that this class can cast.
	 * 
	 * @return the highest level of spells that this class can cast, or -1 if
	 *         this class can not cast spells
	 */
	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public int getMaxCastLevel() {
		int currHighest = -1;
		if (castForLevelMap != null) {
			for (int key : castForLevelMap.keySet()) {
				final Integer value = castForLevelMap.get(key);
				if (value != null) {
					if (value > 0 && key > currHighest) {
						currHighest = key;
					}
				}
			}
		}
		return currHighest;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public boolean isMonster() {
		if (monsterFlag != null) {
			return monsterFlag.equals(Boolean.TRUE);
		}

		if (getMyTypeCount() == 0) {
			return false;
		}

		for (String type : getSafeListFor(ListKey.TYPE)) {
			final ClassType aClassType = SettingsHandler.getGame()
					.getClassTypeByName(type);

			if ((aClassType != null) && aClassType.isMonster()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Class section of Natural Attacks Is just a wrapper to remove the level
	 * dependent stuff
	 * 
	 * @param obj
	 * @param aString
	 */
	/*
	 * DELETEMETHOD This should be refactored out and this work (prior to the
	 * proposed call to addNaturalWeapon) should be done in the Tag
	 */
	public void setNaturalAttacks(final PObject obj, final String aString) {
		final StringTokenizer attackTok = new StringTokenizer(aString,
				Constants.PIPE, false);
		final int lvl = Integer.parseInt(attackTok.nextToken());
		final String sNat = attackTok.nextToken();
		/*
		 * REFACTOR This should be using addNaturalWeapon(eq, lvl);
		 */
		final LevelProperty lp = new LevelProperty(lvl, sNat);

		if (naturalWeapons == null) {
			naturalWeapons = new ArrayList<LevelProperty>();
		}

		naturalWeapons.add(lp);
	}

	/**
	 * get the Natural Attacks for this level
	 * 
	 * @return natural weapons list
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<Equipment> getNaturalWeapons() {
		final List<Equipment> tempArray = new ArrayList<Equipment>();

		if ((naturalWeapons == null) || (naturalWeapons.isEmpty())) {
			return tempArray;
		}

		for (LevelProperty lp : naturalWeapons) {
			if (lp.getLevel() <= level) {
				final Equipment weapon = (Equipment) lp.getObject();
				tempArray.add(weapon);
//				addWeaponProfAutos(weapon.getName());
			}
		}

		return tempArray;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see pcgen.core.PObject#getWeaponProfAutos()
//	 */
//	public List<String> getWeaponProfAutos() {
//		// first build up the list of the standard auto weapon profs
//		final List<String> list = super.getWeaponProfAutos();
//
//		// then add in the proficiencies for each natural weapon
//		// we have active.
//		if (naturalWeapons != null) {
//			for (Iterator<LevelProperty> li = naturalWeapons.iterator(); li
//					.hasNext();) {
//				final LevelProperty lp = li.next();
//				if (lp.getLevel() <= level) {
//					final Equipment weapon = (Equipment) lp.getObject();
//					list.add(weapon.getSimpleName());
//				}
//			}
//		}
//		return list;
//	}

	/*
	 * TYPESAFETY This needs to be checking vs. a RaceType TypesafeEnumeration,
	 * not a general String
	 */
	/*
	 * REFACTOR How exactly does this work in the new PCClass is a PCClassLevel
	 * Factory model? Does this exist as a method in PCClass that should be
	 * called before getLevel() is used (and before the user tries to add the
	 * level to PlayerCharacter?) or is this a check that the level performs 
	 * when the code attempts to add it to the PC?
	 */
	public boolean isQualified(final PlayerCharacter aPC) {

		if (aPC == null) {
			return false;
		}

		// if (isMonster() && (preRaceType != null) &&
		// !contains(aPC.getCritterType(), preRaceType))
		if (isMonster()
				&& (preRaceType != null)
				&& (!aPC.getRace().getRaceType().equalsIgnoreCase(preRaceType) && !contains(
						aPC.getCritterType(), preRaceType)))
		// Move the check for type out of race and into PlayerCharacter to make
		// it easier for a template to adjust it.
		{
			return false;
		}

		if (!canBePrestige(aPC)) {
			return false;
		}

		return true;
	}

	/**
	 * should be "5|4/-" where 5 = level, 4/- is the SR value.
	 * 
	 * @param srString
	 */
	/*
	 * STRINGREFACTOR This should be moved OUT of PCClass and put into the SR
	 * Tag.
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void setSR(final String srString) {
		if (".CLEAR".equals(srString)) {
			SR = null;

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(srString,
				Constants.PIPE, false);
		final int lvl = Integer.parseInt(aTok.nextToken());
		final String tokenSrString = aTok.nextToken();

		if (".CLEAR".equals(tokenSrString)) {
			SR = null;
		} else {
			if (SR == null) {
				SR = new ArrayList<LevelProperty>();
			}

			final LevelProperty lp = new LevelProperty(lvl, tokenSrString);
			SR.add(lp);
		}
	}

	/**
	 * Assumption: SR list is sorted by level.
	 * 
	 * REFACTOR This needs to be changed so that there isn't an assumption that
	 * the SR is sorted by level...
	 * 
	 * @return SR formula
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public String getSRFormula() {
		LevelProperty lp = null;

		if (SR != null) {
			final int lvl = level;

			for (int i = 0, x = SR.size(); i < x; ++i) {
				if (SR.get(i).getLevel() > lvl) {
					break;
				}

				lp = SR.get(i);
			}
		}

		if (lp != null) {
			return lp.getProperty();
		}

		return null;
	}

	/**
	 * needed for Class Editor - returns contents of SR(index).
	 * 
	 * @param index
	 * @param delimiter
	 * @return String
	 */
	/*
	 * REFACTOR to DELETEMETHOD This should be done by the ClassEditor, if it
	 * really wants it, or by some other entity (like the SR Tag class or even
	 * the SRToken class in pcgen.io.exporttoken?). PCClass should NOT reconvert
	 * into a String... :(
	 */
	public String getSRListString(final int index, final String delimiter) {
		if ((SR != null) && (SR.size() > index)) {
			final LevelProperty lp = SR.get(index);

			return lp.getLevel() + delimiter + lp.getProperty();
		}

		return null;
	}

	/*
	 * PCCLASSANDLEVEL Since this is in the PCClass (from a Tag) and
	 * PCClassLevel (as an indication of the spells granted by the PCClassLevel)
	 */
	public final void setSpellBookUsed(final boolean argUseBook) {
		usesSpellbook = argUseBook;
	}

	/*
	 * PCCLASSANDLEVEL Since this is in the PCClass (from a Tag) and
	 * PCClassLevel (as an indication of the spells granted by the PCClassLevel)
	 */
	public final boolean getSpellBookUsed() {
		return usesSpellbook;
	}

	public void setCRFormula(final String argCRFormula) {
		CRFormula = argCRFormula;
	}

	/**
	 * Sets this class as a "monster" class.
	 * 
	 * <p>
	 * Monster classes have special behaviour in certain cases.
	 * 
	 * <p>
	 * TODO - Figure out what those cases are.
	 * 
	 * @param aFlag
	 *            true if this is a monster class.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since
	 * it is a Tag
	 */
	public void setMonsterFlag(final boolean aFlag) {
		if (monsterFlag == null) {
			monsterFlag = Boolean.valueOf(aFlag);
		}
		monsterFlag = aFlag;
	}

	public String getPCCText() {
		final StringBuffer pccTxt = new StringBuffer(200);
		pccTxt.append("CLASS:").append(getDisplayName());
		pccTxt.append(super.getPCCText(false));
		pccTxt.append("\tABB:").append(getAbbrev());
		checkAdd(pccTxt, "", "EXCLASS:", exClass);

		checkAdd(pccTxt, "", "EXCHANGELEVEL:", levelExchange);

		if (hasSubClass) {
			pccTxt.append("\tHASSUBCLASS:Y");
		}
		if (hasSubstitutionClass)
		{
			pccTxt.append("\tHASSUBSTITUTIONLEVEL:Y");
		}

		pccTxt.append("\tHD:").append(hitDie);
		checkAdd(pccTxt, "ANY", "DEITY:", CoreUtility.join(deityList, '|'));
		checkAdd(pccTxt, "", "ATTACKCYCLE", attackCycle);
		checkAdd(pccTxt, "", "CASTAS:", castAs);
		checkAdd(pccTxt, Constants.s_NONE, "PROHIBITED:", prohibitedString);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLSTAT:", spellBaseStat);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLTYPE:", spellType);

		if (usesSpellbook) {
			pccTxt.append("\tSPELLBOOK:Y");
		}

		// if (skillPoints != 0)
		// {
		// pccTxt.append("\tSTARTSKILLPTS:").append(skillPoints);
		// }
		if (skillPointFormula.length() != 0) {
			pccTxt.append("\tSTARTSKILLPTS:").append(skillPointFormula);
		}

		if (!visible) {
			pccTxt.append("\tVISIBLE:N");
		}

		if (initialFeats != 0) {
			pccTxt.append("\tXTRAFEATS:").append(initialFeats);
		}

		if (levelsPerFeat != null) {
			pccTxt.append("\tLEVELSPERFEAT:").append(levelsPerFeat.intValue());
		}

		if (maxLevel != 20) {
			pccTxt.append("\tMAXLEVEL:").append(maxLevel);
		}

		if (!memorizeSpells) {
			pccTxt.append("\tMEMORIZE:N");
		}

		if (multiPreReqs) {
			pccTxt.append("\tMULTIPREREQS:Y");
		}

		if (!getKnownSpellsList().isEmpty()) {
			pccTxt.append("\tKNOWNSPELLS:");

			boolean flag = false;

			for (String spell : knownSpellsList) {
				if (flag) {
					pccTxt.append(Constants.PIPE);
				}

				flag = true;
				pccTxt.append(spell);
			}
		}

		if (itemCreationMultiplier.length() != 0) {
			pccTxt.append("\tITEMCREATE:").append(itemCreationMultiplier);
		}

		if (classSpellString != null) {
			pccTxt.append("\tSPELLLIST:").append(classSpellString).append('\t');
		}

		checkAdd(pccTxt, "", "SPECIALS:", specialsString);
		checkAdd(pccTxt, "", "SKILLLIST:", classSkillString);

		if (getWeaponProfBonus().size() != 0) {
			pccTxt.append("\tWEAPONBONUS:");

			boolean first = true;
			for (final String prof : getWeaponProfBonus()) {
				if (first != true) {
					pccTxt.append(Constants.PIPE);
				}

				pccTxt.append(prof);
				first = false;
			}
		}

		// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		String regionString = getRegionString();
		if ((regionString != null) && !regionString.startsWith("0|")) {
			final int x = regionString.indexOf('|');
			pccTxt.append(lineSep).append(regionString.substring(0, x)).append(
					"\tREGION:").append(regionString.substring(x + 1));
		}

		// if (kitString != null && !kitString.startsWith("0|"))
		// {
		// int x = kitString.indexOf('|');
		// pccTxt.append(lineSep + kitString.substring(0,
		// x)).append("\tKIT:").append(kitString.substring(x + 1));
		// }
		List<String> kits = getSafeListFor(ListKey.KITS);
		for (int iKit = 0; iKit < kits.size(); ++iKit) {
			final String kitString = kits.get(iKit);
			final int x = kitString.indexOf('|');

			if (x >= 0) {
				pccTxt.append(lineSep + kitString.substring(0, x)).append(
						"\tKIT:").append(kitString.substring(x + 1));
			}
		}

		for (final String known : getSpecialtyKnownList()) {
			pccTxt.append("\tSPECIALTYKNOWN:").append(known);
		}

		pccTxt.append(lineSep);

		if (castMap != null) {
			for (int x = 0; x < castMap.size(); ++x) {
				if (castMap.containsKey(x)) {
					final String c = castMap.get(x);
					final String l = lineSep + String.valueOf(x) + "\tCAST:";
					checkAdd(pccTxt, "0", l, c);
				}
			}
		}

		for (int x = 0; x < getKnownList().size(); ++x) {
			final String c = knownList.get(x);
			final String l = lineSep + String.valueOf(x + 1) + "\tKNOWN:";
			checkAdd(pccTxt, "0", l, c);
		}

		// Output the level based DR only
		for (DamageReduction reduction : getDRList()) {
			for (Prerequisite prereq : reduction.getPreReqList()) {
				if (DamageReduction.isPrereqForClassLevel(prereq, getKeyName())) {
					pccTxt.append(lineSep).append(prereq.getOperand()).append(
							"\t").append(reduction.getPCCText(false));
				}
			}
		}

		if (SR != null) {
			for (LevelProperty lp : SR) {
				pccTxt.append(lineSep).append(lp.getLevel()).append("\tSR:")
						.append(lp.getProperty());
			}
		}

		// Output the list of spells associated with the class.
		for (int i = 0; i <= maxLevel; i++) {
			final List<PCSpell> spellList = getSpellSupport()
					.getSpellListForLevel(i);

			if (spellList != null) {
				for (PCSpell spell : spellList) {
					pccTxt.append(lineSep).append(i).append("\tSPELLS:")
							.append(spell.getPCCText());
				}
			}

		}

		for (final String template : getTemplates()) {
			final int y = template.indexOf('|');
			pccTxt.append(lineSep).append(template.substring(0, y));
			pccTxt.append("\tTEMPLATE:").append(template.substring(y + 1));
		}

		for (int x = 0; x < getBonusList().size(); ++x) {
			final BonusObj aBonus = getBonusList().get(x);
			String bonusString = aBonus.toString();
			final int levelEnd = bonusString.indexOf('|');
			final String maybeLevel = bonusString.substring(0, levelEnd);

			pccTxt.append(lineSep);

			if (CoreUtility.isIntegerString(maybeLevel)) {
				pccTxt.append(maybeLevel);
				bonusString = bonusString.substring(levelEnd + 1);
			} else {
				pccTxt.append("0");
			}

			pccTxt.append("\tBONUS:").append(bonusString);
		}

		for (int x = 0; x < getVariableCount(); ++x) {
			final String c = getVariableDefinition(x);
			final int y = c.indexOf('|');
			pccTxt.append(lineSep).append(c.substring(0, y))
					.append("\tDEFINE:").append(c.substring(y + 1));
		}

		List<LevelAbility> levelAbilityList = getLevelAbilityList();
		if ((levelAbilityList != null) && !levelAbilityList.isEmpty()) {
			for (LevelAbility ability : levelAbilityList) {
				pccTxt.append(lineSep).append(String.valueOf(ability.level()))
						.append("\tADD:").append(ability.getTagData());
			}
		}

		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList != null) && (specialAbilityList.size() != 0)) {
			for (SpecialAbility sa : specialAbilityList) {
				final String src = sa.getSASource();
				final String lev = src.substring(src.lastIndexOf('|') + 1);
				pccTxt.append(lineSep).append(lev).append("\tSA:").append(
						sa.toString());
			}
		}

		if ((addDomains != null) && (addDomains.size() != 0)) {
			buildPccText(pccTxt, addDomains.iterator(), Constants.PIPE,
					"\tADDDOMAINS:", lineSep);
		}

		if (domainList != null)
		{
			for (LevelProperty domainProp : domainList)
			{
				pccTxt.append(lineSep).append(domainProp.getLevel());
				pccTxt.append("\tDOMAIN:").append(
						domainProp.getObject().getKeyName());
			}
		}

		buildPccText(pccTxt, getFeatList().iterator(), ":", "\tFEAT:", lineSep);

		// TODO - Add ABILITY tokens.
		buildPccText(pccTxt, getFeatAutos().iterator(), Constants.PIPE,
				"\tFEATAUTO:", lineSep);

		// TODO - This should be removed.
		if ((uattList != null) && (uattList.size() != 0)) {
			for (int x = 0; x < uattList.size(); ++x) {
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append(
						"\tUATT:").append(uattList.get(x));
			}
		}

		List<String> udamList = getListFor(ListKey.UDAM);
		if ((udamList != null) && (udamList.size() != 0)) {
			for (int x = 0; x < udamList.size(); ++x) {
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append(
						"\tUDAM:").append(udamList.get(x));
			}
		}

		List<String> umultList = getListFor(ListKey.UMULT);
		if ((umultList != null) && (umultList.size() != 0)) {
			buildPccText(pccTxt, umultList.iterator(), "|", "\tUMULT:", lineSep);
		}

		return pccTxt.toString();
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<Ability> getVirtualFeatList(final int aLevel) {
		final List<Ability> aList = new ArrayList<Ability>();

		if (vFeatMap != null) {
			for (int i = -9; i <= aLevel; i++) {
				if (vFeatMap.containsKey(i)) {
					aList.addAll(vFeatMap.get(i));
				}
			}
		}

		return aList;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	@Override
	public List<Ability> getVirtualAbilityList(final AbilityCategory aCategory)
	{
		if ( aCategory == AbilityCategory.FEAT )
		{
			return getVirtualFeatList();
		}
		final List<Ability> ret = new ArrayList<Ability>();
		
		if ( vAbilityMap != null )
		{
			for ( final int lvl : vAbilityMap.getSecondaryKeySet(aCategory) )
			{
				if ( lvl <= level )
				{
					ret.addAll(vAbilityMap.get(aCategory, lvl));
				}
			}
		}
		return ret;
	}

	/**
	 * Here is where we do the real work of setting the vision information on
	 * the PObject
	 * 
	 * @param aPC
	 * @return Map
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory) [with level dependent
	 * differences, of course]
	 */
	public Map<String, String> getVision(final PlayerCharacter aPC) {
		if (visionList != null) {
			for (LevelProperty vision : visionList) {
				if (vision.getLevel() <= level) {
					super.setVision(vision.getProperty(), aPC);
				}
			}
		}

		return super.getVision();
	}

	public void setXPPenalty(final DefaultTriState argXPPenalty) {
		XPPenalty = argXPPenalty;
	}

	/**
	 * Sets qualified BonusObj's to "active"
	 * 
	 * @param aPC
	 */
	/*
	 * DELETEMETHOD Because this appears to be simply a correction for PCLevel
	 * (above and beyond what PObject's activateBonuses method does), this 
	 * becomes useless in the new architecture, as the bonuses will only be 
	 * present and visible to the PlayerCharacter in the PCClassLevels that the
	 * PlayerCharacter has.
	 * 
	 * The hasPreReqs test here which is not in PObject is simply a shortcut
	 * of what is already done in bonus.qualifies, so the operation of this
	 * (while looking more complicated) really only differs from PObject in 
	 * the level dependence
	 */
	public void activateBonuses(final PlayerCharacter aPC) {
		for (BonusObj bonus : getBonusList()) {
			if ((bonus.getPCLevel() <= level)) {
				if (bonus.hasPreReqs()) {
					// TODO: This is a hack to avoid VARs etc in class defs
					// being qualified for when Bypass class prereqs is
					// selected.
					// Should we be passing in the BonusObj here to allow it to
					// be referenced in Qualifies statements?
					if ( bonus.qualifies(aPC) )
					{
						bonus.setApplied(true);
					} 
					else 
					{
						bonus.setApplied(false);
					}
				} 
				else 
				{
					bonus.setApplied(true);
				}
			}
		}
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSpellList(final String tok) {
		if (classSpellList == null) {
			newClassSpellList();
		}

		classSpellList.add(tok);
		classSpellString = null;
		stableSpellKey = null;
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addDomain(final Domain domain, int domainLevel) {
		if (domainList == null) {
			domainList = new ArrayList<LevelProperty>();
		}
		domainList.add(new LevelProperty(domainLevel, domain));
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final void addSubClass(final SubClass sClass) {
		if (subClassList == null) {
			subClassList = new ArrayList<SubClass>();
		}

		sClass.setHitPointMap(this);
		sClass.setHitDie(hitDie);
		subClassList.add(sClass);
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final void addSubstitutionClass(final SubstitutionClass sClass) {
		if (substitutionClassList == null) {
			substitutionClassList = new ArrayList<SubstitutionClass>();
		}

		sClass.setHitPointMap(this);
		sClass.setHitDie(hitDie);
		substitutionClassList.add(sClass);
	}

	/*
	 * STRINGREFACTOR This needs to store a LevelProperty object that contains a
	 * Domain, NOT a String, especially a | or : delimited String... !!
	 */
	/*
	 * PCCLASSANDLEVEL This needs to be in both PCClass (since it's imported from
	 * a Tag) and PCClassLevel (although the PCClassLevel version should not be 
	 * level dependent)
	 */
	public void addFeatList(final int aLevel, final String aFeatList) {
		// TODO - Why oh Why do we need yet another separator.
		// TODO - Make this not string based.
		final String aString = aLevel + ":" + aFeatList;
		if (featList == null) {
			featList = new ArrayList<String>();
		}
		featList.add(aString);
	}

	/*
	 * STRINGREFACTOR This is currently storing a String with lots of gunk in it
	 * to identify what Spell levels, et al are known - this should really be an
	 * Array of Arrays or something to that effect...
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addKnown(final int iLevel, final String aString) {
		if (knownList == null) {
			knownList = new ArrayList<String>();
		}

		if (iLevel > maxKnownLevel) {
			maxKnownLevel = iLevel;
		}
		// pad to with empty entries
		while (knownList.size() < (iLevel - 1)) {
			knownList.add("0");
		}

		// Replace existing with new entry
		if (knownList.size() >= iLevel) {
			knownList.set(iLevel - 1, aString);
		} else {
			knownList.add(aString);
		}
	}

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch. The processing needs to be moved back
	 * into the KNOWNSPELL tag. Actually, this needs to use LevelProperty to
	 * make the proper assignments.
	 */
	/*
	 * TYPESAFETY This is throwing around Spell names as Strings. :(
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addKnownSpellsList(final String aString) {

		if (knownSpellsList == null) {
			knownSpellsList = new ArrayList<String>();
		}
		final StringTokenizer aTok;

		if (aString.startsWith(".CLEAR")) {
			knownSpellsList.clear();

			if (".CLEAR".equals(aString)) {
				return;
			}

			aTok = new StringTokenizer(aString.substring(6), "|", false);
		} else {
			aTok = new StringTokenizer(aString, "|", false);
		}

		while (aTok.hasMoreTokens()) {
			knownSpellsList.add(aTok.nextToken());
		}
	}

	/**
	 * Add a level of this class to the character. Note this call is assumed to
	 * only be used when loading characters, and some behaviour is tailored for
	 * this.
	 * 
	 * @param pcLevelInfo
	 * 
	 * @param levelMax
	 *            True if the level caps, if any, for this class should be
	 *            respected.
	 * @param aPC
	 *            The character having the level added.
	 */
	/*
	 * DELETEMETHOD This really becomes a factory call to PCClass to get an
	 * appropriate PCClassLevel to be added to the PlayerCharacter
	 */
	public void addLevel(final PCLevelInfo pcLevelInfo, final boolean levelMax,
			final PlayerCharacter aPC) {
		addLevel(pcLevelInfo, levelMax, false, aPC, true);
	}

	/*
	 * PCCLASSLEVELONLY Since this is a selection made during levelup (from
	 * a LevelAbilityClassSkill) this is only required in PCClassLevel
	 */
	public void addSkillToList(final String aString) {
		if (skillList == null) {
			skillList = new ArrayList<String>();
		}
		if (!skillList.contains(aString)) {
			skillList.add(aString);
		}
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addTemplate(final String template) {
		if (templates == null) {
			templates = new ArrayList<String>();
		}
		templates.add(template);
	}

	/*
	 * DELETEMETHOD - or at least that's my dream. I would like to have this
	 * system be intelligent enough to distinguish items that are level
	 * dependent from those that are not and to then process those correctly and
	 * load them correctly into the PCClassLevels. Therefore, since this is
	 * really only overriding what PObject is doing to make a different case for
	 * items that are level dependent, this becomes an irrelevant method (the
	 * PObject methods should be used for non-level dependent, and PCClass
	 * should handle the rest as LevelPropertys to be loaded into the
	 * appropriate PCClassLevel)
	 */
	@Override
	public void addVirtualAbility(final AbilityCategory aCategory, final Ability anAbility)
	{
		if ( aCategory == AbilityCategory.FEAT )
		{
			addVirtualFeat(anAbility);
		}
		addVirtualAbility(aCategory, -9, anAbility);
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addVirtualAbility(final AbilityCategory aCategory, 
								  final int aLevel, 
								  final Ability anAbility)
	{
		if ( aCategory == AbilityCategory.FEAT )
		{
			addVirtualFeat(anAbility);
			return;
		}
		if ( vAbilityMap == null )
		{
			vAbilityMap = new DoubleKeyMap<AbilityCategory, Integer, List<Ability>>();
		}
		List<Ability> abilities = vAbilityMap.get(aCategory, aLevel);
		if ( abilities == null )
		{
			abilities = new ArrayList<Ability>();
		}
		abilities.add(anAbility);
	}


	/**
	 * Adds virtual feats to the vFeatMao
	 * 
	 * @param aLevel
	 *            level
	 * @param vList
	 *            list of feats
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addVirtualFeats(final int aLevel, final List<Ability> vList) {
		List<Ability> vFeatsAtLevel;

		if (vFeatMap == null) {
			vFeatMap = new HashMap<Integer, List<Ability>>();
		}
		if (vFeatMap.containsKey(aLevel)) {
			vFeatsAtLevel = vFeatMap.get(aLevel);
		} else {
			vFeatsAtLevel = new ArrayList<Ability>();
			vFeatMap.put(aLevel, vFeatsAtLevel);
		}
		vFeatsAtLevel.addAll(vList);

		super.addVirtualFeats(vList);
	}

	/*
	 * DELETEMETHOD - or at least that's my dream. I would like to have this
	 * system be intelligent enough to distinguish items that are level
	 * dependent from those that are not and to then process those correctly and
	 * load them correctly into the PCClassLevels. Therefore, since this is
	 * really only overriding what PObject is doing to make a different case for
	 * items that are level dependent, this becomes an irrelevant method (the
	 * PObject methods should be used for non-level dependent, and PCClass
	 * should handle the rest as LevelPropertys to be loaded into the
	 * appropriate PCClassLevel)
	 */
	@Override
	public void addVirtualAbilities(final AbilityCategory aCategory, final List<Ability> aList)
	{
		if ( aCategory == AbilityCategory.FEAT )
		{
			addVirtualFeats(aList);
			return;
		}
		addVirtualAbilities(aCategory, -9, aList);
	}
	
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addVirtualAbilities(final AbilityCategory aCategory, 
									final int aLevel, 
									final List<Ability> aList)
	{
		if ( aCategory == AbilityCategory.FEAT )
		{
			addVirtualFeats(aLevel, aList);
			return;
		}
		if ( vAbilityMap == null )
		{
			vAbilityMap = new DoubleKeyMap<AbilityCategory, Integer, List<Ability>>();
		}
		vAbilityMap.put(aCategory, aLevel, aList);
	}
	
	/**
	 * returns the value at which another attack is gained attackCycle of 4
	 * means a second attack is gained at a BAB of +5/+1
	 * 
	 * @param index
	 * @return int
	 */
	/*
	 * PCCLASSANDLEVEL Some derivative of this will likely need to exist in both
	 * PCClass (since it's a tag) and PCClassLevel (since there will have to be
	 * some method of detecting what the BAB of a given PCClassLevel is and then
	 * grouping those in the proper groups (see
	 * PlayerCharacter.getAttackString()) to determine what the final attack
	 * bonuses are.
	 */
	public int attackCycle(final int index) {
		String aKey = null;

		if (attackCycleMap != null) {
			/*
			 * TYPESAFETY These Constants could be a Typesafe Enumeration, and
			 * that would be a good thing for memory use (less strings) and
			 * error catching (note that this method does not complain if the
			 * index is out of bounds)
			 */
			if (index == Constants.ATTACKSTRING_MELEE) {
				// Base attack
				aKey = "BAB";
			} else if (index == Constants.ATTACKSTRING_RANGED) {
				// Ranged attack
				aKey = "RAB";
			} else if (index == Constants.ATTACKSTRING_UNARMED) {
				// Unarmed attack
				aKey = "UAB";
			}

			final String aString = attackCycleMap.get(aKey);

			if (aString != null) {
				return Integer.parseInt(aString);
			}
		}
		return SettingsHandler.getGame().getBabAttCyc();
	}

	public int baseAttackBonus(final PlayerCharacter aPC) {
		if (level == 0) {
			return 0;
		}

		// final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int)
		// getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB", aPC);

		return i;
	}

	/*
	 * -2 means that the spell itself indicates what stat should be used,
	 * otherwise this method returns an index into the global list of stats for
	 * which stat the bonus spells are based upon.
	 * 
	 * @return int
	 */
	/*
	 * REFACTOR Why is this returning an INT and not a PCStat or something like
	 * that? or why is the user not just using getSpellBaseStat and processing
	 * the response by itself??
	 */
	public int baseSpellIndex() {
		String tmpSpellBaseStat = getSpellBaseStat();

		return "SPELL".equals(tmpSpellBaseStat) ? (-2 // means base spell stat
														// is based upon spell
														// itself
		)
				: SettingsHandler.getGame().getStatFromAbbrev(tmpSpellBaseStat);
	}

	/**
	 * Returns the index of the stat to use for bonus spells.
	 * 
	 * <p>
	 * The method checks to see if a BONUSSPELLSTAT: has been set for the class.
	 * If it is set to a stat that stat is returned. If it is set to None -1 is
	 * returned. If it is set to Default then the BASESPELLSTAT is returned.
	 * 
	 * @return An index into the stat array or -1 if no bonus spells should be
	 *         granted for this class.
	 * 
	 * TODO - Why doesn't this return a PCStat?
	 */
	/*
	 * REFACTOR Why is this returning an INT and not a PCStat or something like
	 * that? or why is the user not just using getBonusSpellBaseStat and
	 * processing the response by itself??
	 */
	public int bonusSpellIndex() {
		String tmpSpellBaseStat = getBonusSpellBaseStat();

		if (tmpSpellBaseStat.equals(Constants.s_NONE)) {
			return -1;
		} else if (tmpSpellBaseStat.equals(Constants.s_DEFAULT)) {
			/*
			 * CONSIDER I agree with the todo that this seems fuzzy logic
			 */
			// TODO - Shouldn't this return baseSpellIndex so that
			// the "logic" in that method gets executed?
			tmpSpellBaseStat = getSpellBaseStat();
		}

		return SettingsHandler.getGame().getStatFromAbbrev(tmpSpellBaseStat);
	}

	/*
	 * FORMULAREFACTOR This situation may no longer need a formula to calculate
	 * the Challenge Rating of the PCClass, due to the fact that the challenge
	 * rating may be level based. That is not to say it won't be required, but
	 * may be avoided?? Not necessarily, because a ClassType of NPC still uses a
	 * Formula that can't be resolved.
	 */
	/*
	 * REFACTOR I don't like the fact that this method is accessing the
	 * ClassTypes and using one of those to set one of its variables. Should
	 * this be done when a PCClassLevel is built? Is that possible?
	 */
	public int calcCR(final PlayerCharacter aPC) {
		String wCRFormula = "0";

		if (CRFormula != null) {
			wCRFormula = CRFormula;
		} else {
			for (String type : getSafeListFor(ListKey.TYPE)) {
				final ClassType aClassType = SettingsHandler.getGame()
						.getClassTypeByName(type);

				if ((aClassType != null)
						&& !"0".equals(aClassType.getCRFormula())) {
					wCRFormula = aClassType.getCRFormula();
				}
			}
		}

		return aPC.getVariableValue(wCRFormula, classKey).intValue();
	}

	/*
	 * PCCLASSLEVELONLY Since this is level dependent it only makes sense there.
	 */
	public String classLevelString() {
		StringBuffer aString = new StringBuffer();

		if (!getSubClassKey().equals(Constants.s_NONE)
				&& !"".equals(getSubClassKey())) {
			aString.append(getSubClassKey());
		} else {
			aString.append(getKeyName());
		}

		aString = aString.append(' ').append(level);

		return aString.toString();
	}

	public Object clone() {
		PCClass aClass = null;

		try {
			aClass = (PCClass) super.clone();
			aClass.setSubClassKey(getSubClassKey());

			// aClass.setSubClassString(getSubClassString());
			aClass.setProhibitedString(getProhibitedString());
			aClass.setHitDie(hitDie);
			// aClass.setSkillPoints(skillPoints);
			aClass.setSkillPointFormula(skillPointFormula);
			aClass.setInitialFeats(initialFeats);
			aClass.setSpellBaseStat(spellBaseStat);
			aClass.setBonusSpellBaseStat(bonusSpellBaseStat);
			aClass.setSpellType(spellType);
			// aClass.setAttackBonusType(attackBonusType);
			if (specialtyknownList != null) {
				aClass.specialtyknownList = new ArrayList<String>(
						specialtyknownList);
			}
			if (knownList != null) {
				aClass.knownList = new ArrayList<String>(knownList);
			}
			if (castMap != null) {
				aClass.castMap = new HashMap<Integer, String>(castMap);
			}
			// TODO - This should be removed
			aClass.uattList = new ArrayList<String>(uattList);
			// aClass.acList = new ArrayList<String>(acList);
			if (featList != null) {
				aClass.featList = new ArrayList<String>(featList);
			}
			// aClass.vFeatList = (ArrayList) vFeatList.clone();
			if (vFeatMap != null) {
				aClass.vFeatMap = new HashMap<Integer, List<Ability>>(vFeatMap);
			}
			if ( vAbilityMap != null )
			{
				aClass.vAbilityMap = new DoubleKeyMap<AbilityCategory, Integer, List<Ability>>(vAbilityMap);
			}
			if (hitDieLockMap != null) {
				aClass.hitDieLockMap = new HashMap<Integer, String>(
						hitDieLockMap);
			}
			if (featAutos != null) {
				aClass.featAutos = new ArrayList<String>(featAutos);
			}
//			if ( theAutoAbilities != null )
//			{
//				aClass.theAutoAbilities = new DoubleKeyMap<AbilityCategory, Integer, List<String>>(theAutoAbilities);
//			}
			// TODO - Why is this not copying the skillList from the master?
			aClass.skillList = null;

			aClass.classSkillString = classSkillString;
			aClass.classSkillList = null;
			aClass.classSpellString = classSpellString;
			aClass.classSpellList = null;
			aClass.stableSpellKey = null;

			aClass.setSpecialsString(specialsString);
			aClass.setExClass(exClass);
			aClass.setLevelExchange(levelExchange);
			aClass.maxCastLevel = maxCastLevel;
			aClass.maxKnownLevel = maxKnownLevel;

			aClass.abbrev = abbrev;
			aClass.memorizeSpells = memorizeSpells;
			aClass.multiPreReqs = multiPreReqs;
			aClass.deityList = new ArrayList<String>(deityList);
			aClass.maxLevel = maxLevel;
			if (knownSpellsList != null) {
				aClass.knownSpellsList = new ArrayList<String>(knownSpellsList);
			}
			aClass.attackCycle = attackCycle;
			if (attackCycleMap != null) {
				aClass.attackCycleMap = new HashMap<String, String>(
						attackCycleMap);
			}
			aClass.castAs = castAs;
			aClass.preRaceType = preRaceType;
			aClass.modToSkills = modToSkills;
			aClass.levelsPerFeat = levelsPerFeat;
			aClass.initMod = initMod;
			if (specialtyList != null) {
				aClass.specialtyList = new ArrayList<String>(specialtyList);
			}

			// aClass.ageSet = ageSet;
			if (domainList != null) {
				//This is ok as a shallow copy - contract on readers of domainList
				aClass.domainList = new ArrayList<LevelProperty>(domainList);
			}
			if (addDomains != null) {
				aClass.addDomains = new ArrayList<String>(addDomains);
			}
			aClass.setHitPointMap(this);
			aClass.hasSubClass = hasSubClass;
			aClass.subClassList = subClassList;
			aClass.hasSubstitutionClass = hasSubstitutionClass;
			aClass.substitutionClassList = substitutionClassList; 

			aClass.hasSpellFormulas = hasSpellFormulas;

			if (naturalWeapons != null) {
				aClass.naturalWeapons = new ArrayList<LevelProperty>(
						naturalWeapons);
			}
		} catch (CloneNotSupportedException exc) {
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
					Constants.s_APPNAME, MessageType.ERROR);
		}

		return aClass;
	}

	/*
	 * PCCLASSANDLEVEL Since this is altering (or controlling the behavior of) 
	 * the castMap, this has to be both at the PCClass domain (since it's a tag)
	 * and at the PCClassLevel domain (since that is where the castMap is 
	 * active)
	 */
	public final boolean hasSpellFormula() {
		return hasSpellFormulas;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the subclass is activated, this is required in both locations.
	 */
	public final boolean hasSubClass() {
		return hasSubClass;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the substitution class is activated, this is required in both
	 * locations.
	 */
	public final boolean hasSubstitutionClass() {
		return hasSubstitutionClass;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (because they grant spells)
	 */
	public final boolean multiPreReqs() {
		return multiPreReqs;
	}

	/*
	 * PCCLASSANDLEVEL Since this is required in both places...
	 */
	public final String toString() {
		return displayName;
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass, since it is a Tag
	 * 
	 * Need to look into the details of stableSpellKey to figure out the appropriate
	 * place for that
	 */
	public void setName(final String newName) {
		super.setName(newName);

		int i = 3;

		if ("".equals(abbrev)) {
			if (newName.length() < 3) {
				i = newName.length();
			}

			abbrev = newName.substring(0, i);
		}

		stableSpellKey = null;
		getSpellKey();
	}

	/*
	 * PCCLASSLEVELONLY since the specialty list is created during PCClassLevel
	 * creation (in the factory)
	 */
	public boolean isSpecialtySpell(final Spell aSpell) {
		final Collection<String> aList = getSpecialtyList();

		if ((aList == null) || (aList.size() == 0)) {
			return false;
		}

		return (aSpell.descriptorListContains(aList)
				|| aSpell.schoolContains(aList) || aSpell
				.subschoolContains(aList));
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public ArrayList<SubClass> getSubClassList() {
		return subClassList;
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public ArrayList getSubstitutionClassList() {
		return substitutionClassList;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * 
	 * @param aBonus
	 * @param anObj
	 * @param aPC
	 * @return double
	 */
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj,
			PlayerCharacter aPC) {
		double retVal = 0;
		int iTimes = 1;

		final String aType = aBonus.getTypeOfBonus();

		// String aName = aBonus.getBonusInfo();
		if ("VAR".equals(aType)) {
			iTimes = Math.max(1, getAssociatedCount());

			String choiceString = getChoiceString();
			if (choiceString.startsWith("SALIST|")
					&& (choiceString.indexOf("|VAR|") >= 0)) {
				iTimes = 1;
			}
		}

		String bString = aBonus.toString();

		if (getAssociatedCount() != 0) {
			int span = 4;
			int idx = bString.indexOf("%VAR");

			if (idx == -1) {
				idx = bString.indexOf("%LIST");
				span = 5;
			}

			if (idx >= 0) {
				final String firstPart = bString.substring(0, idx);
				final String secondPart = bString.substring(idx + span);

				for (int i = 1; i < getAssociatedCount(); ++i) {
					final String xString = new StringBuffer().append(firstPart)
							.append(getAssociated(i)).append(secondPart)
							.toString();
					retVal += calcPartialBonus(xString, iTimes, aBonus, anObj);
				}

				bString = new StringBuffer().append(firstPart).append(
						getAssociated(0)).append(secondPart).toString();
			}
		}

		retVal += calcPartialBonus(bString, iTimes, aBonus, anObj);

		return retVal;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * 
	 * @param aBonus
	 * @param anObj
	 * @param listString
	 * @param aPC
	 * @return double
	 */
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj,
			final String listString, PlayerCharacter aPC) {
		return calcBonusFrom(aBonus, anObj, aPC);
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public boolean hasClassSkillList(final String aString) {
		if ((classSkillList == null) || classSkillList.isEmpty()) {
			return false;
		}

		for (String key : classSkillList) {
			final PCClass pcClass = Globals.getClassKeyed(key);

			if ((pcClass != null) && pcClass.hasCSkill(aString)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * PCCLASSLEVELONLY? This really seems to be a shortcut test that is part of
	 * the GUI presentation of a PlayerCharacter. It would be really nice if
	 * this could be deleted, but it may actually be providing some speed (need
	 * to evaluate whether a better test exists, however)
	 */
	public boolean hasKnownSpells(final PlayerCharacter aPC) {
		for (int i = 0; i <= getHighestLevelSpell(); i++) {
			if (getKnownForLevel(getLevel(), i, aPC) > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <p>
	 * TODO - I am not sure this code is really working properly.
	 * 
	 * @param aString
	 * @return
	 */
	/*
	 * PCCLASSLEVELONLY Since this is a selection made during levelup (from
	 * a LevelAbilityClassSkill) this is only required in PCClassLevel
	 */
	public boolean hasSkill(final String aString) {
		if (skillList == null) {
			return false;
		}
		for (String key : skillList) {
			if (key.equalsIgnoreCase(aString)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * PCCLASSLEVELONLY Since this is really only something that will be done
	 * within a PlayerCharacter (real processing) it is only required in
	 * PCClassLevel.
	 * 
	 * As a side note, I'm not sure what I think of accessing the ClassTypes and
	 * using one of those to set the response to this request. Should this be
	 * done when a PCClassLevel is built? Is that possible? How does that
	 * interact with a PlayerCharacter being reimported if those rules change?
	 */
	public boolean hasXPPenalty() {
		if (XPPenalty.equals(DefaultTriState.DEFAULT))
		{
			for (String type : getSafeListFor(ListKey.TYPE)) {
				final ClassType aClassType = SettingsHandler.getGame()
						.getClassTypeByName(type);

				if ((aClassType != null) && !aClassType.getXPPenalty()) {
					return false;
				}
			}
			return true;
		}
		return XPPenalty.booleanValue();
	}

	/*
	 * REFACTOR to DELETEMETHOD this really can't be anywhere in PCClass or
	 * PCClassLevel since this is acting across a group of PCClassLevels.
	 * Perhaps a Utility method is required to calculate this across a group of
	 * PCClassLevels?
	 */
	public int hitPoints(final int iConMod) {
		int total = 0;

		for (int i = 0; i <= getLevel(); ++i) {
			if (getHitPoint(i) > 0) {
				int iHp = getHitPoint(i) + iConMod;

				if (iHp < 1) {
					iHp = 1;
				}

				total += iHp;
			}
		}

		return total;
	}

	public int recalcSkillPointMod(final PlayerCharacter aPC, final int total) {
		final int spMod;

		if (isMonster() && aPC.isMonsterDefault()) {
			spMod = getMonsterSkillPointMod(aPC, total);
		} else {
			spMod = getNonMonsterSkillPointMod(aPC, total);
		}
		return spMod;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill pool is
	 * calculated based on other factors, it is not a Tag
	 */
	public final int skillPool() {
		return skillPool;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill pool is
	 * calculated based on other factors, it is not a Tag
	 */
	public void setSkillPool(final int i) {
		skillPool = i;
	}

	/*
	 * DELETEMETHOD This uses the unused variable specialsString
	 */
	public String specialsString() {
		return specialsString;
	}

	/*
	 * REFACTOR TO DELETEMETHOD I would really like to get rid of this, since it
	 * it used as a "funky spells" test - which should be more explicit than
	 * implicit in zero cast spells.
	 * 
	 * Not to mention, this isn't entirely true, is it? I mean, if the only
	 * spells a Class can get are from the ability score related to their
	 * spells, then the book system would use a 0 rather than a -. This 0
	 * actually contains information which indicates the system may not be zero
	 * after all...
	 */
	public boolean zeroCastSpells() {
		if (castMap == null) {
			return true;
		}
		for (final int key : castMap.keySet()) {
			final String aVal = castMap.get(key);
			final StringTokenizer aTok = new StringTokenizer(aVal, ",");
			int numSpells = 0;

			while (aTok.hasMoreTokens()) {
				final String spellNum = aTok.nextToken();

				try {
					numSpells = Integer.parseInt(spellNum);
				} catch (NumberFormatException nfe) {
					// ignore
				}

				if (numSpells > 0) {
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * REFACTOR TO DELETEMETHOD I don't understand why the .CLEAR related
	 * functionality only exists in PCClass and not other PObjects??? Perhaps
	 * this should be up in PObject and therefore not be necessary here?... No,
	 * I suspect this is level related based on how SpecialAbilitys store their
	 * source and then use that to check if the PC qualifies for the Special
	 * Ability. Hopefully that String processing can be factored out and this
	 * can only load the appropriate SpecialAbilitys into the PCClassLevels that
	 * a PlayerCharacter has.
	 */
	protected List<SpecialAbility> addSpecialAbilitiesToList(
			final List<SpecialAbility> aList, final PlayerCharacter aPC) {
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList == null) || specialAbilityList.isEmpty()) {
			return aList;
		}

		final List<SpecialAbility> bList = new ArrayList<SpecialAbility>();

		for (SpecialAbility sa : specialAbilityList) {
			//CONSIDER This can be optimized to create saKey inside the next IF
			final String saKey = sa.getKeyName();

			if (sa.pcQualifiesFor(aPC)) {
				if (saKey.startsWith(".CLEAR")) {
					if (".CLEARALL".equals(saKey)) {
						bList.clear();
					} else if (saKey.startsWith(".CLEAR.")) {
						final String saToRemove = saKey.substring(7);

						for (int itIdx = bList.size() - 1; itIdx >= 0; --itIdx) {
							final String subKey = bList.get(itIdx).getKeyName();

							if (subKey.equals(saToRemove)) {
								bList.remove(itIdx);
							} else if (subKey.indexOf('(') >= 0) {
								if (subKey.substring(0, subKey.indexOf('('))
										.trim().equals(saToRemove)) {
									bList.remove(itIdx);
								}
							}
						}
					}
					//CONSIDER else what?  No error checking here?

					continue;
				}

				bList.add(sa);
			}
		}

		aList.addAll(bList);

		return aList;
	}

	/*
	 * PCCLASSONLY Since this is really most important during import (and the
	 * same types can be used for PCClassLevels, at least today) this should
	 * only be performed at import and not on each PCClassLevel creation (since
	 * that wouldn't create any new types)
	 */
	protected void doGlobalTypeUpdate(final String aString) {
		// add to global PCClassType list for future filtering
		if (!Globals.getPCClassTypeList().contains(aString)) {
			Globals.getPCClassTypeList().add(aString);
		}
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	final List<String> getClassSkillList() {
		return classSkillList;
	}

	/**
	 * Return number of spells known for a level for a given spellbook.
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param bookName
	 * @param aPC
	 * @return known for spell level
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	int getKnownForLevel(int pcLevel, final int spellLevel,
			final String bookName, final PlayerCharacter aPC) {
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE."
				+ getSpellType());

		if ((castMap != null)
				&& (getNumFromCastList(pcLevel, spellLevel, aPC) < 0)) {
			// Don't know any spells of this level
			// however, character might have a bonus spells e.g. from certain
			// feats
			return (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
					+ levelSpellLevel);
		}
		if (pcLevel > maxKnownLevel) {
			pcLevel = maxKnownLevel;
		}

		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
				+ levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE."
				+ getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any"
				+ levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
				+ allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE."
				+ getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any"
				+ allSpellLevel);

		final int index = baseSpellIndex();
		final PCStat aStat;

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size())) {
			aStat = aPC.getStatList().getStatAt(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0) {
			statString = SettingsHandler.getGame().s_ATTRIBSHORT[index];
		}

		final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "KNOWN."
				+ statString)
				+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT")
				+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT;CLASS."
						+ keyName);

		if (index > -2) {
			final int maxSpellLevel = aPC.getVariableValue(
					"MAXLEVELSTAT=" + statString, "").intValue();

			if ((maxSpellLevel + bonusStat) < spellLevel) {
				return total;
			}
		}

		stat += bonusStat;

		int mult = (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", classKeyName
				+ levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", "TYPE."
				+ getSpellType() + levelSpellLevel);

		if (mult < 1) {
			mult = 1;
		}

		boolean psiSpecialty = false;

		if (!getKnownList().isEmpty()) {
			if (pcLevel > getKnownList().size()) {
				// doesn't know any spells of this level
				return 0;
			}

			final String aString = getKnownList().get(pcLevel - 1);
			final StringTokenizer aTok = new StringTokenizer(aString, ",");
			int iCount = 0;

			while (aTok.hasMoreTokens()) {
				String spells = aTok.nextToken();

				if (iCount == spellLevel) {
					if (spells.endsWith("+d")) {
						psiSpecialty = true;

						if (spells.length() > 1) {
							spells = spells.substring(0, spells.length() - 2);
						}
					}

					int t;
					if (hasSpellFormula()) {
						t = aPC.getVariableValue(spells, "").intValue();
					} else {
						t = Integer.parseInt(spells);
					}
					total += (t * mult);

					// add Stat based bonus
					final String bonusSpell = Globals.getBonusSpellMap().get(
							String.valueOf(spellLevel));

					if (Globals.checkRule(RuleConstants.BONUSSPELLKNOWN)
							&& (bonusSpell != null)
							&& !bonusSpell.equals("0|0")) {
						final StringTokenizer s = new StringTokenizer(
								bonusSpell, "|");
						final int base = Integer.parseInt(s.nextToken());
						final int range = Integer.parseInt(s.nextToken());

						if (stat >= base) {
							total += Math.max(0, (stat - base + range) / range);
						}
					}

					if (psiSpecialty) {
						total += numSpellsFromSpecialty;
					}
				}

				iCount++;
			}
		}

		// if we have known spells (0==no known spells recorded)
		// or a psi specialty.
		if (((total > 0) && (spellLevel > 0)) && !psiSpecialty) {
			// make sure any slots due from specialties
			// (including domains) are added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

	final int getNumSpellsFromSpecialty() {
		return numSpellsFromSpecialty;
	}

	/*
	 * REFACTOR Exactly where does this end up? I think that passing a
	 * PlayerCharacter into an object like PCClass is generally (but certainly
	 * not always) bad form. In this case, the PC is present in order to test
	 * prerequisites, so perhaps this is an OK use of passing in
	 * PlayerCharacter...
	 */
	public boolean isProhibited(final Spell aSpell, final PlayerCharacter aPC) {
		if (!PrereqHandler.passesAll(aSpell.getPreReqList(), aPC, this)) {
			return true;
		}

		if (prohibitSpellDescriptorList != null) {
			for (SpellProhibitor prohibit : prohibitSpellDescriptorList) {
				if (prohibit.isProhibited(aSpell, aPC)) {
					return true;
				}
			}
		}

		final StringTokenizer aTok = new StringTokenizer(prohibitedString, ",",
				false);

		while (aTok.hasMoreTokens()) {
			final String a = aTok.nextToken();

			if (aSpell.getSchools().contains(a)
					|| aSpell.getSubschools().contains(a)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Get the unarmed Damage for this class at the given level.
	 * 
	 * @param aLevel
	 * @param includeCrit
	 * @param includeStrBonus
	 * @param aPC
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	/*
	 * REFACTOR There is redundant information being sent in here (level 
	 * and PlayerCharacter).
	 */
	/*
	 * PCCLASSLEVELONLY Since this is a level dependent calculation, this should
	 * be performed by the PCClassLevel.
	 */
	String getUdamForLevel(int aLevel, final boolean includeCrit,
			final boolean includeStrBonus, final PlayerCharacter aPC,
			boolean adjustForPCSize) {
		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		aLevel += (int) aPC.getTotalBonusTo("UDAM", "CLASS." + keyName);

		int iLevel = aLevel;
		final Equipment eq = EquipmentList
				.getEquipmentKeyed("KEY_Unarmed Strike");

		if (eq != null) {
			aDamage = eq.getDamage(aPC);
		} else {
			aDamage = "1d3";
		}

		// resize the damage as if it were a weapon
		int iSize = Globals.sizeInt(aPC.getSize());

		if (adjustForPCSize) {
			aDamage = Globals.adjustDamage(aDamage, SettingsHandler.getGame()
					.getDefaultSizeAdjustment().getAbbreviation(),
					SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize)
							.getAbbreviation());
		}

		//
		// Check the UDAM list for monk-like damage
		//
		List<String> udamList = Globals.getClassKeyed(keyName).getListFor(
				ListKey.UDAM);

		if ((udamList != null) && !udamList.isEmpty()) {
			if (udamList.size() == 1) {
				final String aString = udamList.get(0);

				if (aString.startsWith("CLASS=")
						|| aString.startsWith("CLASS.")) {
					final PCClass aClass = Globals.getClassKeyed(aString
							.substring(6));

					if (aClass != null) {
						return aClass.getUdamForLevel(aLevel, includeCrit,
								includeStrBonus, aPC, adjustForPCSize);
					}

					Logging.errorPrint(keyName + " refers to "
							+ aString.substring(6) + " which isn't loaded.");

					return aDamage;
				}
			}

			if (aLevel > udamList.size()) {
				iLevel = udamList.size();
			}

			final StringTokenizer aTok = new StringTokenizer(udamList.get(
					Math.max(iLevel - 1, 0)).toString(), ",", false);

			while ((iSize > -1) && aTok.hasMoreTokens()) {
				aDamage = aTok.nextToken();

				if (iSize == 0) {
					break;
				}

				iSize -= 1;
			}
		}

		final StringBuffer aString = new StringBuffer(aDamage);
		int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE.MELEE");
		b += (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");

		if (includeStrBonus && (b > 0)) {
			aString.append('+');
		}

		if (includeStrBonus && (b != 0)) {
			aString.append(String.valueOf(b));
		}

		if (includeCrit) {
			final String dString = getUMultForLevel(aLevel);

			if (!"0".equals(dString)) {
				aString.append("(x").append(dString).append(')');
			}
		}

		return aString.toString();
	}

	/**
	 * Increases or decreases the initiative modifier by the given value.
	 * 
	 * @param initModDelta
	 */
	/*
	 * DELETEMETHOD for an unused variable
	 */
	public void addInitMod(final int initModDelta) {
		initMod += initModDelta;
	}

	/**
	 * Adds a level of this class to the current Global PC.
	 * 
	 * This method is deeply evil. This instance of the PCClass has been
	 * assigned to a PlayerCharacter, but the only way we can get from this
	 * class back to the PlayerCharacter is to get the current global character
	 * and hope that the caller is only calling this method on a PCClass
	 * embedded within the current global PC.
	 * 
	 * TODO: Split the PlayerCharacter code out of PCClass (i.e. the level
	 * property). Then have a joining class assigned to PlayerCharacter that
	 * maps PCClass and number of levels in the class.
	 * 
	 * @param pcLevelInfo
	 * 
	 * @param argLevelMax
	 *            True if we should only allow extra levels if there are still
	 *            levels in this class to take. (i.e. a lot of prestige classes
	 *            stop at level 10, so if this is true it would not allow an
	 *            11th level of the class to be added
	 * @param bSilent
	 *            True if we are not to show any dialog boxes about errors or
	 *            questions.
	 * @param aPC
	 *            The character we are adding the level to.
	 * @param isLoading
	 *            True if the character is being loaded and prereqs for the
	 *            level should be ignored.
	 * @return true or false
	 */
	/*
	 * REFACTOR Clearly this is part of the PCClass factory method that produces
	 * PCClassLevels combined with some other work that will need to be done to
	 * extract some of the complicated gunk out of here that goes out and puts
	 * information into PCLevelInfo and PlayerCharacter.
	 */
	boolean addLevel(final PCLevelInfo pcLevelInfo, final boolean argLevelMax,
			final boolean bSilent, final PlayerCharacter aPC,
			final boolean isLoading) {

		// Check to see if we can add a level of this class to the
		// current character
		final int newLevel = level + 1;
		boolean levelMax = argLevelMax;

		level += 1;
		if (!isLoading) {
			// When loading a character, classes are added before feats, so
			// this test would always fail on loading if feats are required
			boolean doReturn = false;
			if (!PrereqHandler.passesAll(getPreReqList(), aPC, this)) {
				doReturn = true;
				if (!bSilent) {
					ShowMessageDelegate.showMessageDialog(
							"This character does not qualify for level "
									+ level, Constants.s_APPNAME,
							MessageType.ERROR);
				}
			}
			level -= 1;
			if (doReturn) {
				return false;
			}
		}

		if (isMonster()) {
			levelMax = false;
		}

		if ((newLevel > maxLevel) && levelMax) {
			if (!bSilent) {
				ShowMessageDelegate.showMessageDialog(
						"This class cannot be raised above level "
								+ Integer.toString(maxLevel),
						Constants.s_APPNAME, MessageType.ERROR);
			}

			return false;
		}

		// Add the level to the current character
		int total = aPC.getTotalLevels();

		// No longer need this since the race now sets a bonus itself and Templates
		// are not able to reassign their feats.  There was nothing else returned in
		// this number
		//		if (total == 0) {
		//			aPC.setFeats(aPC.getInitialFeats());
		//		}
		setLevel(newLevel, aPC);

		// the level has now been added to the character,
		// so now assign the attributes of this class level to the
		// character...
		for (final String template : getTemplates(aPC.isImporting(), aPC)) {
			aPC.addTemplateKeyed(template);
		}

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		final int dnum = aPC.getMaxCharacterDomains(this, aPC)
				- aPC.getCharacterDomainUsed();

		if (!aPC.hasDomainSource("PCClass", getKeyName(), newLevel)) {
			if (dnum > 0) {
				aPC.addDomainSource("PCClass", getKeyName(), newLevel, dnum);
			}
		}

		aPC.setAutomaticAbilitiesStable(null, false);
//		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel, aPC, pcLevelInfo);

		// Don't roll the hit points if the gui is not being used.
		// This is so GMGen can add classes to a person without pcgen flipping
		// out
		if (Globals.getUseGUI()) {
			rollHP(aPC, level, aPC.getTotalLevels() == 1);
		}

		if (!aPC.isImporting()) {
			modDomainsForLevel(newLevel, true, aPC);
		}

		int levelUpStats = 0;

		// Add any bonus feats or stats that will be gained from this level
		// i.e. a bonus feat every 3 levels
		if (aPC.getTotalLevels() > total) {
			boolean processBonusStats = true;
			boolean processBonusFeats = true;
			total = aPC.getTotalLevels();

			if (isMonster()) {
				// If we have less levels that the races monster levels
				// then we can not give a stat bonus (i.e. an Ogre has
				// 4 levels of Giant, so it does not get a stat increase at
				// 4th level because that is already taken into account in
				// its racial stat modifiers, but it will get one at 8th
				if (total <= aPC.getRace().getMonsterClassLevels(aPC)) {
					processBonusStats = false;
				}

				/*
				 * If we are using default monsters and we have not yet added
				 * all of the racial monster levels then we can not add any
				 * feats. i.e. a default monster Ogre will not get a feat at 1st
				 * or 3rd level because they have already been allocated in the
				 * race, but a non default monster will get the 2 bonus feats
				 * instead. Both versions of the monster will get one at 6th
				 * level. i.e. default Ogre with 2 class levels, or no default
				 * Ogre with 4 giant levels and 2 class levels.
				 */
				if (aPC.isMonsterDefault()
						&& total <= aPC.getRace().getMonsterClassLevels(aPC)) {
					processBonusFeats = false;
				}
			}

			if (!aPC.isImporting()) {
				// We do not want to do these
				// calculations a second time when are
				// importing a character. The feat
				// number and the stat point pool are
				// already saved in the import file.

//				if (processBonusFeats) {
//					final double bonusFeats = aPC.getBonusFeatsForNewLevel(this);
//					if (bonusFeats > 0) {
//						// aPC.setFeats(aPC.getFeats() + bonusFeats);
//						aPC.adjustFeats(bonusFeats);
//					}
//				}

				if (processBonusStats) {
					final int bonusStats = Globals.getBonusStatsForLevel(total);
					if (bonusStats > 0) {
						aPC.setPoolAmount(aPC.getPoolAmount() + bonusStats);

						if (!bSilent
								&& SettingsHandler.getShowStatDialogAtLevelUp()) {
							levelUpStats = askForStatIncrease(aPC, bonusStats,
									true);
						}
					}
				}
			}
		}

		// Update Skill Points. Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		final int spMod;
		spMod = recalcSkillPointMod(aPC, total);

		PCLevelInfo pcl;

		if (aPC.getLevelInfoSize() > 0) {
			pcl = aPC.getLevelInfo().get(aPC.getLevelInfoSize() - 1);

			if (pcl != null) {
				pcl.setLevel(level);
				pcl.setSkillPointsGained(spMod);
				pcl.setSkillPointsRemaining(pcl.getSkillPointsGained());
			}
		}

		skillPool = skillPool() + spMod;

		aPC.setSkillPoints(spMod + aPC.getSkillPoints());

		if (!aPC.isImporting()) {
			//
			// Ask for stat increase after skill points have been calculated
			//
			if (levelUpStats > 0) {
				askForStatIncrease(aPC, levelUpStats, false);
			}

			if (newLevel == 1) {
				List<String> l = getSafeListFor(ListKey.KITS);
				for (int i = 0; i > l.size(); i++) {
					KitUtilities.makeKitSelections(0, l.get(i), i, aPC);
				}
				makeRegionSelection(0, aPC);
			}

			List<String> l = getSafeListFor(ListKey.KITS);
			for (int i = 0; i > l.size(); i++) {
				KitUtilities.makeKitSelections(newLevel, l.get(i), i, aPC);
			}
			makeRegionSelection(newLevel, aPC);

			// Make sure any natural weapons are added
			aPC.addNaturalWeapons(getNaturalWeapons());
		}

		// this is a monster class, so don't worry about experience
		if (isMonster()) {
			return true;
		}

		if (!aPC.isImporting()) {
			checkRemovals(aPC);
			final int minxp = aPC.minXPForECL();
			if (aPC.getXP() < minxp) {
				aPC.setXP(minxp);
			} else if (aPC.getXP() >= aPC.minXPForNextECL()) {
				if (!bSilent) {
					ShowMessageDelegate.showMessageDialog(SettingsHandler
							.getGame().getLevelUpMessage(),
							Constants.s_APPNAME, MessageType.INFORMATION);
				}
			}
		}

		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel() == 1)
				&& !aPC.isImporting()) {
			exchangeLevels(aPC);
		}
		return true;
	}

	/*
	 * REFACTOR This is going to require some inginuity to be able to do this in
	 * the new PCClass/PCClassLevel world, since this is an interaction across
	 * multiple PCClassLevels.
	 */
	private void exchangeLevels(final PlayerCharacter aPC) {
		final StringTokenizer aTok = new StringTokenizer(levelExchange, "|",
				false);

		if (aTok.countTokens() != 4) {
			Logging.errorPrint("levelExhange: invalid token count: "
					+ aTok.countTokens());
		} else {
			try {
				final String classKey = aTok.nextToken(); // Class to get
															// levels from
				final int iMinLevel = Integer.parseInt(aTok.nextToken()); // Minimum
																			// level
																			// required
																			// in
																			// donating
																			// class
				int iMaxDonation = Integer.parseInt(aTok.nextToken()); // Maximum
																		// levels
																		// donated
																		// from
																		// class
				final int iLowest = Integer.parseInt(aTok.nextToken()); // Lowest
																		// that
																		// donation
																		// can
																		// lower
																		// donating
																		// class
																		// level
																		// to

				final PCClass aClass = aPC.getClassKeyed(classKey);

				if (aClass != null) {
					final int iLevel = aClass.getLevel();

					if (iLevel >= iMinLevel) {
						iMaxDonation = Math.min(Math.min(iMaxDonation, iLevel
								- iLowest), getMaxLevel() - 1);

						if (iMaxDonation > 0) {
							//
							// Build the choice list
							//
							final List<String> choiceNames = new ArrayList<String>();

							for (int i = 0; i <= iMaxDonation; ++i) {
								choiceNames.add(Integer.toString(i));
							}

							//
							// Get number of levels to exchange for this class
							//
							final ChooserInterface c = ChooserFactory
									.getChooserInstance();
							c
									.setTitle("Select number of levels to convert from "
											+ aClass.getDisplayName()
											+ " to "
											+ getDisplayName());
							c.setPool(1);
							c.setPoolFlag(false);
							c.setAvailableList(choiceNames);
							c.setVisible(true);

							final List<String> selectedList = c
									.getSelectedList();
							int iLevels = 0;

							if (!selectedList.isEmpty()) {
								iLevels = Integer.parseInt(selectedList.get(0));
							}

							if (iLevels > 0) {
								aPC.giveClassesAway(this, aClass, iLevels);
							}
						}
					}
				}
			} catch (NumberFormatException exc) {
				ShowMessageDelegate.showMessageDialog("levelExchange:"
						+ Constants.s_LINE_SEP + exc.getMessage(),
						Constants.s_APPNAME, MessageType.ERROR);
			}
		}
	}

	/*
	 * DELETEMETHOD I hope this can be deleted, since minus level support will not
	 * work the same way in the new PCClass/PCClassLevel world. If nothing else, it
	 * is massively a REFACTOR item to put this into the PlayerCharacter that is
	 * doing the removal.
	 */
	void doMinusLevelMods(final PlayerCharacter aPC, final int oldLevel) {
		if (!isMonster()) {
			changeFeatsForLevel(oldLevel, false, aPC);
		}

		subAddsForLevel(oldLevel, aPC);
		aPC.removeVariable("CLASS:" + getKeyName() + "|"
				+ Integer.toString(oldLevel));
	}

	/*
	 * REFACTOR Since this is side effects of adding a level, the
	 * PlayerCharacter needs to perform this work, not PCClass.
	 * Then again, this could be in PCClassLevel.
	 */
	void doPlusLevelMods(final int newLevel, final PlayerCharacter aPC,
			final PCLevelInfo pcLevelInfo) {
		if (!isMonster()) {
			changeFeatsForLevel(newLevel, true, aPC);
		}

		addVariablesForLevel(newLevel, aPC);

		// moved after changeSpecials and addVariablesForLevel
		// for bug #688564 -- sage_sam, 18 March 2003
		aPC.calcActiveBonuses();
		addAddsForLevel(newLevel, aPC, pcLevelInfo);
	}

	/**
	 * Update the name of the required class for all special abilites, DEFINE's,
	 * and BONUS's
	 * 
	 * @param oldClass
	 *            The name of the class that should have the special abliities
	 *            changed
	 * @param newClass
	 *            The name of the new class for the altered special abilities
	 */
	/*
	 * DELETEMETHOD Great theory, wrong universe.  This is unused code, except for
	 * in the test system.  Either someone should explain where this is headed, or
	 * it should be removed for simplification of the code.
	 */
	void fireNameChanged(final String oldClass, final String newClass) {
		//
		// This gets called on clone(), so don't traverse the list if the names
		// are the same
		//
		if (oldClass.equals(newClass)) {
			return;
		}

		//
		// Go through the specialty list (SA) and adjust the class to the new
		// name
		//
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList != null) {
			for (int idx = specialAbilityList.size() - 1; idx >= 0; --idx) {
				SpecialAbility sa = specialAbilityList.get(idx);

				// TODO - This looks like it should have always been a reference
				// to getSASource not getSource.
				if (sa.getSASource().length() != 0)
				// if (sa.getSource().length() != 0)
				{
					sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(),
							sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					specialAbilityList.set(idx, sa);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new
		// name
		//
		if (getVariableCount() > 0) {
			for (int idx = getVariableCount() - 1; idx >= 0; --idx) {
				final Variable variable = getVariable(idx);
				String formula = variable.getValue();

				formula = formula.replaceAll("=" + oldClass, "=" + newClass);

				variable.setValue(formula);
			}
		}

		//
		// Go through the bonus list (BONUS) and adjust the class to the new
		// name
		//
		if (getBonusList() != null) {
			for (BonusObj bonusObj : getBonusList()) {
				final String bonus = bonusObj.toString();
				int offs = -1;

				for (;;) {
					offs = bonus.indexOf('=' + oldClass, offs + 1);

					if (offs < 0) {
						break;
					}

					addBonusList(bonus.substring(0, offs + 1) + newClass
							+ bonus.substring(offs + oldClass.length() + 1));
					removeBonusList(bonusObj);
				}
			}
		}

		//
		// Go through the damage reduction list (DR) and adjust the class to the
		// new name
		//
		for (DamageReduction reduction : getDRList()) {
			for (Prerequisite prereq : reduction.getPreReqList()) {
				if (DamageReduction.isPrereqForClassLevel(prereq, oldClass)) {
					prereq.setKey(newClass);
				}
			}
		}
	}

	String makeBonusString(final String bonusString, final String chooseString,
			final PlayerCharacter aPC) {
		return "0|" + super.makeBonusString(bonusString, chooseString, aPC);
	}

	/**
	 * Added to help deal with lower-level spells prepared in higher-level
	 * slots. BUG [569517] Works in conjunction with PlayerCharacter method
	 * availableSpells() sk4p 13 Dec 2002
	 * 
	 * @param aLevel
	 * @param bookName
	 * @return int
	 */
	int memorizedSpecialtiesForLevelBook(final int aLevel, final String bookName) {
		int m = 0;
		final List<CharacterSpell> aList = getSpellSupport().getCharacterSpell(
				null, bookName, aLevel);

		if (aList.isEmpty()) {
			return m;
		}

		for (CharacterSpell cs : aList) {
			if (cs.isSpecialtySpell()) {
				m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
			}
		}

		return m;
	}

	int memorizedSpellForLevelBook(final int aLevel, final String bookName) {
		int m = 0;
		final List<CharacterSpell> aList = getSpellSupport().getCharacterSpell(
				null, bookName, aLevel);

		if (aList.isEmpty()) {
			return m;
		}

		for (CharacterSpell cs : aList) {
			m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
		}

		return m;
	}

	void subLevel(final boolean bSilent, final PlayerCharacter aPC) {

		if (aPC != null) {
			int total = aPC.getTotalLevels();
			final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

			if ((specialAbilityList != null) && !specialAbilityList.isEmpty()) {
				// remove any choice or SPECIALS: related special abilities the
				// PC no longer qualifies for
				for (int i = specialAbilityList.size() - 1; i >= 0; --i) {
					final SpecialAbility sa = specialAbilityList.get(i);

					if (sa.getSASource().startsWith("PCCLASS|")
							&& !sa.pcQualifiesFor(aPC))
					// if (sa.getSource().startsWith("PCCLASS|") &&
					// !sa.pcQualifiesFor(aPC))
					{
						specialAbilityList.remove(sa);
					}
				}
			}

			int spMod = 0;
			final PCLevelInfo pcl = aPC.getLevelInfoFor(keyName, level);

			if (pcl != null) {
				spMod = pcl.getSkillPointsGained();
			} else {
				Logging
						.errorPrint("ERROR: could not find class/level info for "
								+ displayName + "/" + level);
			}

			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			List<LevelAbility> levelAbilityList = getLevelAbilityList();
			if ((levelAbilityList != null) && !levelAbilityList.isEmpty()) {
				for (LevelAbility levAbility : levelAbilityList) {
					if ((levAbility.level() == level) && levAbility.isFeat()) {
						aPC.adjustFeats(-1);
					}
				}
			}

			final Integer zeroInt = Integer.valueOf(0);
			final int newLevel = level - 1;

			if (level > 0) {
				setHitPoint(level - 1, zeroInt);
			}

//			aPC.adjustFeats(-aPC.getBonusFeatsForNewLevel(this));
			setLevel(newLevel, aPC);
			removeKnownSpellsForClassLevel(aPC);

			doMinusLevelMods(aPC, newLevel + 1);

			modDomainsForLevel(newLevel, false, aPC);

			if (newLevel == 0) {
				setSubClassKey(Constants.s_NONE);

				//
				// Remove all skills associated with this class
				//
				for (Skill skill : aPC.getSkillList()) {
					skill.setZeroRanks(this, aPC);
				}

				spMod = skillPool();
			}

			if (!isMonster() && (total > aPC.getTotalLevels())) {
				total = aPC.getTotalLevels();

				// Roll back any stat changes that were made as part of the
				// level

				final List<PCLevelInfoStat> moddedStats = new ArrayList<PCLevelInfoStat>();
				if (pcl.getModifiedStats(true) != null) {
					moddedStats.addAll(pcl.getModifiedStats(true));
				}
				if (pcl.getModifiedStats(false) != null) {
					moddedStats.addAll(pcl.getModifiedStats(false));
				}
				if (!moddedStats.isEmpty()) {
					for (PCLevelInfoStat statToRollback : moddedStats) {
						for (Iterator<PCStat> i = aPC.getStatList().iterator(); i
								.hasNext();) {
							final PCStat aStat = i.next();

							if (aStat.getAbb().equalsIgnoreCase(
									statToRollback.getStatAbb())) {
								aStat.setBaseScore(aStat.getBaseScore()
										- statToRollback.getStatMod());
								break;
							}
						}
					}
				}
			}

			if (!isMonster() && (total == 0)) {
				aPC.setSkillPoints(0);
				// aPC.setFeats(0);
				aPC.getSkillList().clear();
				aPC.clearRealAbilities(null);
//				aPC.clearRealFeats();
//				aPC.getWeaponProfList().clear();
				aPC.setDirty(true);
			} else {
				aPC.setSkillPoints(aPC.getSkillPoints() - spMod);
				skillPool = skillPool() - spMod;
			}

			if (getLevel() == 0) {
				aPC.getClassList().remove(this);
			}

			aPC.validateCharacterDomains();

			// be sure to remove any natural weapons
			aPC.removeNaturalWeapons(this);
		} else {
			Logging
					.errorPrint("No current pc in subLevel()? How did this happen?");

			return;
		}
	}

	/*
	 * DELETEMETHOD This only has one local call and adds no value to the interface
	 * of PCClass (delete through inlining)
	 */
	private double getBonusTo(final String type, final String mname,
			final PlayerCharacter aPC) {
		return getBonusTo(type, mname, level, aPC);
	}

	/**
	 * This method can be called to determine if the number of extra HD for
	 * purposes of skill points, feats, etc. See MM p. 11 extracted 03 Dec 2002
	 * by sage_sam for bug #646816
	 * 
	 * @param aPC
	 *            currently selected PlayerCharacter
	 * @param hdTotal
	 *            int number of monster HD the character has
	 * @return int number of HD considered "Extra"
	 */
	/*
	 * DELETEMETHOD This seems like something that violates the hardcoding rules -
	 * need to check what this should really be and how it should be structured
	 * in the code, new tags, etc. Message sent to PCGen-Experimental on Yahoo
	 */
	private static int getExtraHD(final PlayerCharacter aPC, final int hdTotal) {
		// Determine the EHD modifier based on the size category
		final int sizeInt = Globals.sizeInt(aPC.getRace().getSize());
		final int ehdMod;

		switch (sizeInt) {
		case 8: // Collossal
			ehdMod = 32;

			break;

		case 7: // Gargantuan
			ehdMod = 16;

			break;

		case 6: // Huge
			ehdMod = 4;

			break;

		case 5: // Large
			ehdMod = 2;

			break;

		default: // Medium and smaller
			ehdMod = 1;

			break;
		}

		// EHD = total HD - base HD for size (min of zero)
		return Math.max(0, hdTotal - ehdMod);
	}

	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	private boolean isAutoKnownSpell(final String aSpellKey,
			final int spellLevel, final boolean useMap,
			final PlayerCharacter aPC) {
		if (getKnownSpellsList().isEmpty()) {
			return false;
		}

		final Spell aSpell = Globals.getSpellKeyed(aSpellKey);

		if (useMap) {
			final Integer val = castForLevelMap.get(spellLevel);

			if ((val == null) || val == 0 || (aSpell == null)) {
				return false;
			}
		} else if ((getCastForLevel(level, spellLevel, aPC) == 0)
				|| (aSpell == null)) {
			return false;
		}

		if (isProhibited(aSpell, aPC) && !isSpecialtySpell(aSpell)) {
			return false;
		}

		boolean flag = true;

		// iterate through the KNOWNSPELLS: tag
		for (String spellStr : getKnownSpellsList()) {
			flag = true;

			final StringTokenizer spellTok = new StringTokenizer(spellStr, ",",
					false);

			// must satisfy all elements in a comma delimited list
			while (spellTok.hasMoreTokens() && flag) {
				final String bString = spellTok.nextToken();

				// if the argument starts with LEVEL=, compare the level to the
				// desired spellLevel
				if (bString.startsWith("LEVEL=")
						|| bString.startsWith("LEVEL.")) {
					flag = Integer.parseInt(bString.substring(6)) == spellLevel;
				}

				// if it starts with TYPE=, compare it to the spells type list
				else if (bString.startsWith("TYPE=")
						|| bString.startsWith("TYPE.")) {
					flag = aSpell.isType(bString.substring(5));
				}

				// otherwise it must be the spell's name
				else {
					flag = bString.equals(aSpellKey);
				}
			}

			// if we found an entry in KNOWNSPELLS: that is satisfied, we can
			// stop
			if (flag) {
				break;
			}
		}

		return flag;
	}

	/*
	 * This method calculates skill modifier for a monster character.
	 * 
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam for bug #629643
	 * and updated to fix the bug.
	 */
	private int getMonsterSkillPointMod(final PlayerCharacter aPC,
			final int total) {
		int spMod = 0;
		final int lockedMonsterSkillPoints = (int) aPC.getTotalBonusTo(
				"MONSKILLPTS", "LOCKNUMBER");

		// Set the monster's base skills at the first level
		if (total == 1) {
			if (lockedMonsterSkillPoints == 0) {
				spMod = (int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER");
			} else {
				spMod = lockedMonsterSkillPoints;
			}
		}

		// This is not the first level added...
		else {
			if (getExtraHD(aPC, total) > 0) {
				// spMod = getSkillPoints();
				spMod = aPC.getVariableValue(getSkillPointFormula(), classKey)
						.intValue();
				if (lockedMonsterSkillPoints == 0) {
					spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");
				} else {
					spMod += lockedMonsterSkillPoints;
				}
				spMod = updateBaseSkillMod(aPC, spMod);
			}
		}

		if (spMod < 0) {
			spMod = 0;
		}

		return spMod;
	}

	/*
	 * This method calculates skill modifier for a non-monster character.
	 * 
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam for bug #629643
	 */
	private int getNonMonsterSkillPointMod(final PlayerCharacter aPC,
			final int total) {
		// int spMod = getSkillPoints();
		int lockedMonsterSkillPoints;
		int spMod = aPC.getVariableValue(getSkillPointFormula(), classKey)
				.intValue();

		spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");

		if (isMonster()) {
			lockedMonsterSkillPoints = (int) aPC.getTotalBonusTo("MONSKILLPTS",
					"LOCKNUMBER");
			if (lockedMonsterSkillPoints > 0) {
				spMod = lockedMonsterSkillPoints;
			} else if (total == 1) {
				int monSkillPts = (int) aPC.getTotalBonusTo("MONSKILLPTS",
						"NUMBER");
				if (monSkillPts != 0) {
					spMod = monSkillPts;
				}
			}

			if (total != 1) {
				// If this level is one that is not entitled to skill points
				// based
				// on the monster's size, zero out the skills for this level
				final int nonSkillHD = (int) aPC.getTotalBonusTo(
						"MONNONSKILLHD", "NUMBER");
				if (total <= nonSkillHD) {
					spMod = 0;
				}
			}
		}

		spMod = updateBaseSkillMod(aPC, spMod);

		if (total == 1) {
			if (SettingsHandler.getGame().isPurchaseStatMode()) {
				aPC.setPoolAmount(0);
			}

			spMod *= Math.min(Globals.getSkillMultiplierForLevel(total), aPC
					.getRace().getInitialSkillMultiplier());
			Globals.getBioSet().randomize("AGE", aPC);
		} else {
			spMod *= Globals.getSkillMultiplierForLevel(total);
		}

		return spMod;
	}

	/**
	 * Build a caster level map for this class. The map will be of the form
	 * <String,String> where the key is the spell level and the value is the
	 * number of times per day that spell level can be cast by the character
	 * 
	 * @param aPC
	 * 
	 * TODO: Why is this not a Map<Integer,Integer>
	 */
	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	private void calcCastPerDayMapForLevel(final PlayerCharacter aPC) {
		//
		// TODO: Shouldn't we be using Globals.getLevelInfo().size() instead of
		// 100?
		// Byngl -- November 25, 2002
		//
		if (castForLevelMap == null) {
			castForLevelMap = new HashMap<Integer, Integer>(100);
		}
		for (int i = 0; i < 100; i++) {
			final int s = getCastForLevel(level, i, aPC);
			castForLevelMap.put(i, s);
		}
	}

	/**
	 * <p>
	 * This function adds all templates up to the current level to
	 * <code>templatesAdded</code> and returns a list of the names of those
	 * templates.
	 * </p>
	 * <p>
	 * The function requires that templates be stored in the
	 * <code>templates</code> list as a string in the form
	 * LVL|[CHOOSE:]Template|Template|Template...
	 * </p>
	 * <p>
	 * Passing <code>false</code> to this function results in nothing
	 * happening, although the function still parses all of the template lines,
	 * it doesn't add anything to the class.
	 * </p>
	 * 
	 * @param flag
	 *            If false, function returns empty <code>ArrayList</code> (?)
	 * @param aPC
	 * @return A list of templates added by this function
	 */
	/*
	 * DELETEMETHOD if not PCCLASSONLY.  This is required in PCClass since
	 * it is really done during addLevel.  Note that templatesAdded is 
	 * never used and will be deleted.
	 */
	public List<String> getTemplates(final boolean flag,
			final PlayerCharacter aPC) {
		final ArrayList<String> newTemplates = new ArrayList<String>();
		templatesAdded = new ArrayList<String>();

		for (final String template : getTemplates()) {
			final StringTokenizer aTok = new StringTokenizer(template, "|",
					false);

			if (level < Integer.parseInt(aTok.nextToken())) {
				continue;
			}

			// The next token will either be a CHOOSE: tag or a template;
			// we handle CHOOSE: tags by retrieving the rest of the string
			final String tString = aTok.nextToken();

			if (tString.startsWith("CHOOSE:") && !flag) {
				newTemplates
						.add(PCTemplate.chooseTemplate(this, template
								.substring(template.indexOf("CHOOSE:") + 7),
								true, aPC));
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
			} else if (!flag) {
				newTemplates.add(tString);
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));

				while (aTok.hasMoreTokens()) {
					newTemplates.add(aTok.nextToken());
					templatesAdded.add(newTemplates
							.get(newTemplates.size() - 1));
				}
			}
		}

		return newTemplates;
	}

	/*
	 * REFACTOR to DELETEMETHOD While this is (perhaps?) a useful
	 * Utility method, it is NOT to be placed in PCClass, but in a general
	 * utility class for Tokens.
	 */
	private static String getToken(int tokenNum, final String aList,
			final String delim) {
		final StringTokenizer aTok = new StringTokenizer(aList, delim, false);

		while (aTok.hasMoreElements() && (tokenNum >= 0)) {
			final String aString = aTok.nextToken();

			if (tokenNum == 0) {
				return aString;
			}

			--tokenNum;
		}

		return null;
	}

	/*
	 * PCCLASSLEVELONLY Since this is a level dependent calculation, this should
	 * be performed by the PCClassLevel.
	 */
	private String getUMultForLevel(final int aLevel) {
		String aString = "0";

		List<String> umultList = getListFor(ListKey.UMULT);
		if ((umultList == null) || umultList.isEmpty()) {
			return aString;
		}

		for (String umult : umultList) {
			final int pos = umult.lastIndexOf('|');

			if ((pos >= 0)
					&& (aLevel <= Integer.parseInt(umult.substring(0, pos)))) {
				aString = umult.substring(pos + 1);
			}
		}

		return aString;
	}

	/*
	 * REFACTOR This should really be better at recognizing level dependent
	 * items and storing them appropriately and not relying on PObject to ever
	 * be level aware. This is a general problem across PCClass and I would like
	 * this to be solved before the great PCClass/PCClassLevel splitting.
	 */
	/*
	 * PCCLASSONLY Since this is really just an extracted method of something
	 * that happens in the factory production of a PCClassLevel, it is only
	 * required in PCClass.
	 */
	private void addVariablesForLevel(final int aLevel,
			final PlayerCharacter aPC) {
		if (getVariableCount() == 0) {
			return;
		}

		if (aLevel == 1) {
			addVariablesForLevel(0, aPC);
		}

		final String prefix = classKey + '|';

		for (Iterator<Variable> i = getVariableIterator(); i.hasNext();) {
			final Variable v = i.next();

			if (v.getLevel() == aLevel) {
				aPC.addVariable(prefix + v.getDefinition());
			}
		}
	}

	//
	// Ask user to select a stat to increment. This can happen before skill
	// points
	// are calculated, so an increase to the appropriate stat can give more
	// skill points
	//
	/*
	 * REFACTOR This is part of the leveling up process, but really can't be
	 * part of the production of a PCClassLevel object, since that is
	 * PlayerCharacter independent. This really needs to happen during the
	 * addition of a PCClassLevel to a PlayerCharacter.
	 */
	private final int askForStatIncrease(final PlayerCharacter aPC,
			final int statsToChoose, final boolean isPre) {
		//
		// If 1st time here (checks for preincrement), then will only ask if
		// want to ask before level up
		// If 2nd time here, will ask if there are any remaining points
		// unassigned.
		// So, hitting cancel on the 1st popup will cause the 2nd popup to ask
		// again.
		// This is to handle cases where the user is adding multiple levels, so
		// the SKILL point total
		// won't be too messed up
		//
		if (isPre) {
			if (!Globals.checkRule(RuleConstants.INTBEFORE)) {
				return statsToChoose;
			}
		}

		String extraMsg = "";

		if (isPre) {
			extraMsg = "\nRaising a stat here may award more skill points.";
		}

		int iCount = 0;

		for (int ix = 0; ix < statsToChoose; ++ix) {
			final StringBuffer sStats = new StringBuffer();

			for (Iterator<PCStat> i = aPC.getStatList().iterator(); i.hasNext();) {
				final PCStat aStat = i.next();
				final int iAdjStat = aPC.getStatList().getTotalStatFor(
						aStat.getAbb());
				final int iCurStat = aPC.getStatList().getBaseStatFor(
						aStat.getAbb());
				sStats.append(aStat.getAbb()).append(": ").append(iCurStat);

				if (iCurStat != iAdjStat) {
					sStats.append(" adjusted: ").append(iAdjStat);
				}

				sStats.append(" (").append(
						aPC.getStatList().getStatModFor(aStat.getAbb()))
						.append(")\n");
			}

			final InputInterface ii = InputFactory.getInputInstance();
			final Object selectedValue = ii
					.showInputDialog(
							null,
							"Choose stat to increment or select Cancel to increment stat on the Summary tab."
									+ extraMsg
									+ "\n\n"
									+ "Current Stats:\n"
									+ sStats + "\n", Constants.s_APPNAME,
							MessageType.INFORMATION,
							SettingsHandler.getGame().s_ATTRIBLONG,
							SettingsHandler.getGame().s_ATTRIBLONG[0]);

			if (selectedValue != null) {
				for (Iterator<PCStat> i = aPC.getStatList().iterator(); i
						.hasNext();) {
					final PCStat aStat = i.next();

					if (aStat.getDisplayName().equalsIgnoreCase(
							selectedValue.toString())) {
						aPC.saveStatIncrease(aStat.getAbb(), 1, isPre);
						aStat.setBaseScore(aStat.getBaseScore() + 1);
						aPC.setPoolAmount(aPC.getPoolAmount() - 1);
						++iCount;

						break;
					}
				}
			}
		}

		return statsToChoose - iCount;
	}

	private static void buildPccText(final StringBuffer pccTxt,
			final Iterator<String> listIterator, final String separator,
			final String label, final String lineSep) {
		while (listIterator.hasNext()) {
			final String listItem = listIterator.next();
			final int sepPos = listItem.indexOf(separator);
			pccTxt.append(lineSep).append(listItem.substring(0, sepPos))
					.append(label).append(listItem.substring(sepPos + 1));
		}
	}

	/**
	 * Build a list of Sub-Classes for the user to choose from. The two lists
	 * passed in will be populated.
	 * 
	 * @param choiceList
	 *            The list of sub-classes to choose from.
	 * @param removeList
	 *            The list of sub-classes that cannot be chosen
	 * @param useProhibitCost
	 *            SHould the prohibited cost be used rather than the cost of the
	 *            sub-class.
	 * @param aPC
	 */
	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private void buildSubClassChoiceList(final List<List> choiceList,
			final List<List> removeList, final boolean useProhibitCost,
			final PlayerCharacter aPC) {
		int displayedCost;

		boolean subClassSelected = false;
		for (SubClass sc : subClassList) {
			if (!PrereqHandler.passesAll(sc.getPreReqList(), aPC, this)) {
				continue;
			}

			final List<Object> columnList = new ArrayList<Object>(3);

			if (useProhibitCost) {
				displayedCost = sc.getProhibitCost();
			} else {
				if (!this.getSubClassKey().equals("None")) {
					// We already have a subclass requested.
					// If it is legal we will return that.
					subClassSelected = true;
				}
				displayedCost = sc.getCost();
			}

			boolean added = false;
			columnList.add(sc);
			columnList.add(String.valueOf(displayedCost));

			StringBuffer otherColumn = new StringBuffer();
			if (sc.getNumSpellsFromSpecialty() != 0) {
				otherColumn.append("SPECIALTY SPELLS:").append(
						sc.getNumSpellsFromSpecialty());
				added = true;
			}

			if (sc.getSpellBaseStat() != null) {
				if (otherColumn.length() > 0) {
					otherColumn.append(" ");
				}
				otherColumn.append("SPELL BASE STAT:").append(
						sc.getSpellBaseStat());
				added = true;
			}

			if (!added) {
				otherColumn.append(' ');
			}
			columnList.add(otherColumn.toString());

			if (displayedCost == 0) {
				removeList.add(columnList);
			}

			choiceList.add(columnList);
		}
		if (useProhibitCost == false && subClassSelected == true) {
			// We want to return just the selected class.
			for (Iterator<List> i = choiceList.iterator(); i.hasNext();) {
				List columns = i.next();
				SubClass sc = (SubClass) columns.get(0);
				if (!sc.getKeyName().equals(this.getSubClassKey())) {
					i.remove();
				}
			}
		}
	}

	/**
	 * Build a list of Substitution Classes for the user to choose from. The
	 * list passed in will be populated.
	 * 
	 * @param choiceNames
	 *            The list of substitution classes to choose from.
	 * @param level
	 *            The class level to determine the choices for  
	 * @param aPC
	 */
	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private void buildSubstitutionClassChoiceList(final List<PCClass> choiceList,
			final int level, final PlayerCharacter aPC) {

		for (SubstitutionClass sc : substitutionClassList) {
			if (!PrereqHandler.passesAll(sc.getPreReqList(), aPC, this)) {
				continue;
			}
			if (!sc.hasLevelArrayModsForLevel(level)) {
				continue;
			}

			choiceList.add(sc);
		}
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 * 
	 * @param bString
	 *            Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST
	 *            or %VAR bonus section
	 * @param iTimes
	 *            multiply bonus * iTimes
	 * @param aBonus
	 *            The bonuse Object used for calcs
	 * @param anObj
	 * @return partial bonus
	 */
	private double calcPartialBonus(final String bString, final int iTimes,
			final BonusObj aBonus, final Object anObj) {
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);

		if (aBonus.getPCLevel() >= 0) {
			// discard first token (Level)
			aTok.nextToken();
		}

		aTok.nextToken(); // Is this intended to be thrown away? Why?

		final String aList = aTok.nextToken();
		final String aVal = aTok.nextToken();

		double iBonus = 0;

		if (aList.equals("ALL")) {
			return 0;
		}

		if (anObj instanceof PlayerCharacter) {
			iBonus = ((PlayerCharacter) anObj).getVariableValue(aVal, classKey)
					.doubleValue();
		} else {
			try {
				iBonus = Float.parseFloat(aVal);
			} catch (NumberFormatException e) {
				// Should this be ignored?
				Logging
						.errorPrint("PCClass calcPartialBonus NumberFormatException in BONUS: "
								+ bString);
			}
		}

		return iBonus * iTimes;
	}

	/*
	 * DELETEMETHOD This is private, used once, and only one line long.
	 * Refactor this out by inlining it.
	 */
	private boolean canBePrestige(final PlayerCharacter aPC) {
		return PrereqHandler.passesAll(getPreReqList(), aPC, this);
	}

	/**
	 * This method adds or deletes feats for a level.
	 * 
	 * @param aLevel
	 *            the level to affect
	 * @param addThem
	 *            whether to add or remove feats
	 * @param aPC
	 */
	/*
	 * PCCLASSONLY This (or really a derivative of it, since this is actually
	 * making some choices) is part of getLevel method of PCClass (the factory
	 * that produces PCClassLevels.
	 * 
	 * Note (theoretically) that Feat removal should not need to be possible, as
	 * the subLevel method of PCClass will not be present moving forward
	 * (hopefully)
	 */
	private void changeFeatsForLevel(final int aLevel, final boolean addThem,
			final PlayerCharacter aPC) {
		if ((aPC == null) || featList == null || featList.isEmpty()) {
			return;
		}

		PCLevelInfo pcLevelInfo = null;
		for (PCLevelInfo pcl : aPC.getLevelInfo()) {
			if (pcl.getClassKeyName().equalsIgnoreCase(getKeyName())
					&& pcl.getLevel() == aLevel) {
				pcLevelInfo = pcl;
				break;
			}
		}

		for (String feats : getFeatList()) {
			if (aLevel == Integer.parseInt(getToken(0, feats, ":"))) {
				final double preFeatCount = aPC.getUsedFeatCount();
				AbilityUtilities.modFeatsFromList(aPC, pcLevelInfo, getToken(1,
						feats, ":"), addThem, aLevel == 1);

				final double postFeatCount = aPC.getUsedFeatCount();

				// Adjust the feat count by the total number that were given
				aPC.adjustFeats(postFeatCount - preFeatCount);
			}
		}
	}

	/*
	 * DELETEMETHOD through refactoring this to another location. While this is
	 * yet another potentially useful utility function, PCClass really isn't the
	 * appropriate place for this method.
	 */
	private static void checkAdd(final StringBuffer txt, final String comp,
			final String label, final String value) {
		if ((value != null) && !comp.equals(value)) {
			txt.append('\t').append(label).append(value);
		}
	}

	/*
	 * PCCLASSONLY This is really part of the PCClassLevel Factory, and
	 * therefore only needs to be placed in PCClass
	 */
	private void checkForSubClass(final PlayerCharacter aPC) {
		if (!hasSubClass || (subClassList == null) || (subClassList.isEmpty())) {
			return;
		}

		List<String> columnNames = new ArrayList<String>(3);
		columnNames.add("Name");
		columnNames.add("Cost");
		columnNames.add("Other");

		List<List> choiceList = new ArrayList<List>();
		List<List> removeList = new ArrayList<List>();
		buildSubClassChoiceList(choiceList, removeList, false, aPC);

		/*
		 * REFACTOR This makes an assumption that SubClasses are ONLY Schools, which may
		 * not be a fabulous assumption
		 */
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("School Choice (Specialisation)");
		c
				.setMessageText("Make a selection.  The cost column indicates the cost of that selection. "
						+ "If this cost is non-zero, you will be asked to also "
						+ "select items from this list to give up to cover that cost.");
		c.setPool(1);
		c.setPoolFlag(false);

		// c.setCostColumnNumber(1); // Allow 1 choice, regardless of
		// cost...cost will be applied in second phase
		c.setAvailableColumnNames(columnNames);
		c.setAvailableList(choiceList);

		if (choiceList.size() == 1) {
			c.setSelectedList(choiceList);
		} else if (choiceList.size() != 0) {
			c.setVisible(true);
		}

		List<List<SubClass>> selectedList = c.getSelectedList();

		if (!selectedList.isEmpty()) {
			setProhibitedString("");
			specialtyList = null;

			SubClass sc = selectedList.get(0).get(0);
			choiceList = new ArrayList<List>();
			removeList = new ArrayList<List>();
			buildSubClassChoiceList(choiceList, removeList, true, aPC);

			// Remove the specialist school
			for (Iterator<List> iter = choiceList.iterator(); iter.hasNext();) {
				final List columns = iter.next();

				if (columns.get(0).equals(sc)) {
					iter.remove();

					break;
				}
			}

			choiceList.removeAll(removeList);
			setSubClassKey(sc.getKeyName());

			if (sc.getChoice().length() > 0) {
				addSpecialty(sc.getChoice());
			}

			if (sc.getCost() != 0) {
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1
						.setMessageText("Make a selection.  You must make as many selections "
								+ "necessary to cover the cost of your previous selections.");
				c1.setPool(sc.getCost());
				c1.setPoolFlag(true);
				c1.setCostColumnNumber(1);
				c1.setNegativeAllowed(true);
				c1.setVisible(true);
				selectedList = c1.getSelectedList();

				for (Iterator<List<SubClass>> i = selectedList.iterator(); i
						.hasNext();) {
					final List columns = i.next();
					sc = (SubClass) columns.get(0);

					if (prohibitedString.length() > 0) {
						prohibitedString = prohibitedString.concat(",");
					}

					prohibitedString = prohibitedString.concat(sc.getChoice());
				}
			}
		}
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private void checkForSubstitutionClass(final int aLevel, final PlayerCharacter aPC) {
		if (!hasSubstitutionClass || (substitutionClassList == null)
				|| (substitutionClassList.isEmpty())) {
			return;
		}
		List<String> columnNames = new ArrayList<String>(1);
		columnNames.add("Name");

		List<PCClass> choiceList = new ArrayList<PCClass>();
		buildSubstitutionClassChoiceList(choiceList, level, aPC);
		if (choiceList.size() == 0) {
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Substitution Levels");
		c.setMessageText("Choose one of the listed substitution levels " +
                         "or press Close to take the standard class level.");
		c.setPool(1);
		c.setPoolFlag(false);

		c.setAvailableColumnNames(columnNames);
		c.setAvailableList(choiceList);

		c.setVisible(true);

		List<SubstitutionClass> selectedList = c.getSelectedList();

		if (!selectedList.isEmpty()) {
			SubstitutionClass sc = selectedList.get(0);
			setSubstitutionClassKey(sc.getKeyName(), aLevel);
			sc.applyLevelArrayModsToLevel(this, aLevel);
			return;
		}
		setSubstitutionClassKey(null, aLevel);
	}

	/*
	 * PCCLASSONLY Since this is a choice of ClassSkillList, this is part of the
	 * PCClass factory of PCClassLevels??
	 */
	private void chooseClassSkillList() {
		// if no entry or no choices, just return
		if (classSkillString == null) {
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(classSkillString, "|",
				false);
		int amt = 0;

		if (classSkillString.indexOf('|') >= 0) {
			amt = Integer.parseInt(aTok.nextToken());
		}

		final List<String> aList = new ArrayList<String>();

		while (aTok.hasMoreTokens()) {
			aList.add(aTok.nextToken());
		}

		if (aList.size() == 1) {
			classSkillList = aList;

			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose class-skills this class will inherit");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.setVisible(true);

		final List<String> selectedList = c.getSelectedList();
		classSkillList = new ArrayList<String>();

		for (String sel : selectedList) {
			classSkillList.add(sel);
		}
	}

	/*
	 * PCCLASSONLY Since this is part of the construction of a PCClassLevel,
	 * this is only part of PCClass...
	 */
	private void chooseClassSpellList() {
		// if no entry or no choices, just return
		if ((classSpellString == null) || (level < 1)) {
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(classSpellString, "|",
				false);
		int amt = 0;

		if (classSpellString.indexOf('|') >= 0) {
			amt = Integer.parseInt(aTok.nextToken());
		}

		final List<String> aList = new ArrayList<String>();

		while (aTok.hasMoreTokens()) {
			aList.add(aTok.nextToken());
		}

		if (aList.size() == amt) {
			classSpellList = aList;

			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose list of spells this class will use");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.setVisible(true);

		final List<String> selectedList = c.getSelectedList();
		classSpellList = new ArrayList<String>();

		classSpellList.addAll(selectedList);
	}

	/*
	 * DELETEMETHOD Looks like a semi-useful utility method, but it's only
	 * used once, and since we're trying to eliminate String processing, 
	 * let's inline this one and ditch this method.
	 */
	private static boolean contains(final String big, final String little) {
		return big.indexOf(little) >= 0;
	}

	/*
	 * REFACTOR Some derivative of this method will be in PCClass only as part
	 * of the factory creation of a PCClassLevel... or perhaps in PCClassLevel
	 * so it can steal some information from other PCClassLevels of that
	 * PCClass. Either way, this will be far from its current form in the final
	 * solution.
	 */
	private void inheritAttributesFrom(final PCClass otherClass) {
		if (otherClass.getBonusSpellBaseStat() != null) {
			setBonusSpellBaseStat(otherClass.getBonusSpellBaseStat());
		}

		if (otherClass.getSpellBaseStat() != null) {
			setSpellBaseStat(otherClass.getSpellBaseStat());
		}

		if (otherClass.classSpellString != null) {
			classSpellString = otherClass.classSpellString;
		}

		addAutoArray(otherClass.getSafeListFor(ListKey.AUTO_ARRAY));

		if (!otherClass.getBonusList().isEmpty()) {
			getBonusList().addAll(otherClass.getBonusList());
		}

		if (otherClass.getVariableCount() > 0) {
			addAllVariablesFrom(otherClass);
		}

		if (otherClass.getCSkillList() != null) {
			clearCSkills();
			addAllCSkills(otherClass.getCSkillList());
		}

		if (otherClass.getCcSkillList() != null) {
			clearCcSkills();
			addAllCcSkills(otherClass.getCcSkillList());
		}

		otherClass.setKitList(getSafeListFor(ListKey.KITS));

		if (otherClass.getRegionString() != null) {
			setRegionString(otherClass.getRegionString());
		}

		final List<SpecialAbility> specialAbilityList = getSafeListFor(ListKey.SPECIAL_ABILITY);
		specialAbilityList.addAll(otherClass
				.getSafeListFor(ListKey.SPECIAL_ABILITY));

		if (!otherClass.getDRList().isEmpty()) {
			for (DamageReduction dr : otherClass.getDRList()) {
				try {
					addDR(dr.clone());
				} catch (CloneNotSupportedException e) {
					Logging.errorPrint("Failed to clone DR for PCClass "
							+ keyName + ".", e);
				}
			}
		}

		if (otherClass.SR != null) {
			SR = (ArrayList<LevelProperty>) otherClass.SR.clone();
		}

		if (otherClass.vision != null) {
			vision = otherClass.vision;
		}

		if (otherClass instanceof SubClass) {
			((SubClass) otherClass).applyLevelArrayModsTo(this);
		}

		if (otherClass.naturalWeapons != null) {
			naturalWeapons = (ArrayList<LevelProperty>) otherClass.naturalWeapons
					.clone();
		}
	}

	private void modDomainsForLevel(final int aLevel, final boolean adding,
			final PlayerCharacter aPC) {

		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting()) {
			return;
		}

		/*
		 * Note this uses ALL of the domains up to and including this level,
		 * because there is the possibility (albeit strange) that the PC was not
		 * qualified at a previous level change, but the PlayerCharacter is now
		 * qualified for the given Domain. Even this has quirks, since it is
		 * only applied at the time of level increase, but I think that quirk
		 * should be resolved by a CDOM system around 6.0 - thpr 10/23/06
		 */
		for (final Domain dom : getDomainList(aLevel)) {
			if (dom.qualifies(aPC))
			{
				String domKey = dom.getKeyName();
				if (adding)
				{
					if (!aPC.containsCharacterDomain(domKey))
					{
						Domain aDomain = dom.clone();

						final CharacterDomain aCD = aPC
								.getNewCharacterDomain(getKeyName());
						aCD.setDomain(aDomain, aPC);
						aPC.addCharacterDomain(aCD);
						aDomain = aCD.getDomain();
						aDomain.setIsLocked(true, aPC);
					}
				}
				else
				{
					if (aPC.containsCharacterDomain(domKey))
					{
						aPC.removeCharacterDomain(domKey);
					}
				}
			}
		}
	}

	/*
	 * REFACTOR to DELETEMETHOD This is only called in one place, it should
	 * be inlined for clarity - not worth the separate method call
	 */
	private void newClassSpellList() {
		if (classSpellList == null) {
			classSpellList = new ArrayList<String>();
		} else {
			classSpellList.clear();
		}
	}

	/**
	 * Rolls hp for the current level according to the rules set in options.
	 * 
	 * @param aLevel
	 * @param aPC
	 * @param first
	 */
	/*
	 * REFACTOR This really needs to be part of the PCClassLevel importing into
	 * a PlayerCharacter. Some thought needs to be put into where this stuff is
	 * stored - should PCLevelInfo be adapted to store all of the non-static
	 * information about a PCClassLevel?
	 */
	public void rollHP(final PlayerCharacter aPC, int aLevel, boolean first) {
		int roll = 0;

		final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN")
				+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + keyName);
		final int max = getLevelHitDie(aPC, aLevel)
				+ (int) aPC.getTotalBonusTo("HD", "MAX")
				+ (int) aPC.getTotalBonusTo("HD", "MAX;CLASS." + keyName);

		if (Globals.getGameModeHPFormula().length() == 0) {
			if ((first && aLevel == 1) && SettingsHandler.isHPMaxAtFirstLevel()) {
				roll = max;
			} else {
				if (!aPC.isImporting()) {
					roll = Globals.rollHP(min, max, getDisplayName(), aLevel);
				}
			}
		}

		roll += ((int) aPC.getTotalBonusTo("HP", "CURRENTMAXPERLEVEL"));
		setHitPoint(aLevel - 1, Integer.valueOf(roll));
		aPC.setCurrentHP(aPC.hitPoints());
	}

	/*
	 * This method updates the base skill modifier based on stat bonus, race
	 * bonus, and template bonus. Created(Extracted from addLevel) 20 Nov 2002
	 * by sage_sam for bug #629643
	 */
	private int updateBaseSkillMod(final PlayerCharacter aPC, int spMod) {
		// skill min is 1, unless class gets 0 skillpoints per level (for second
		// apprentice class)
		final int skillMin = (spMod > 0) ? 1 : 0;

		if (modToSkills) {
			spMod += (int) aPC.getStatBonusTo("MODSKILLPOINTS", "NUMBER");

			if (spMod < 1) {
				spMod = 1;
			}
		}

		// Race modifiers apply after Intellegence. BUG 577462
		spMod += aPC.getRace().getBonusSkillsPerLevel();
		spMod = Math.max(skillMin, spMod); // Minimum 1, not sure if bonus
											// skills per

		// level can be < 1, better safe than sorry
		if (!aPC.getTemplateList().isEmpty()) {
			for (PCTemplate template : aPC.getTemplateList()) {
				spMod += template.getBonusSkillsPerLevel();
			}
		}

		return spMod;
	}

	/*
	 * REFACTOR This can be done in a Generic typesafe way (e.g. LevelProperty<T>)
	 * to avoid some casting
	 */
	private static class LevelProperty {
		private String property = "";

		private int propLevel = 0;

		private PObject object;

		LevelProperty(final int argLevel, final String argProperty) {
			propLevel = argLevel;
			property = argProperty;
		}

		LevelProperty(final int argLevel, final PObject argObject) {
			propLevel = argLevel;
			object = argObject;
		}

		public final int getLevel() {
			return propLevel;
		}

		public final String getProperty() {
			return property;
		}

		public final PObject getObject() {
			return object;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.PObject#addNaturalWeapon(pcgen.core.Equipment, int)
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (of course, a level independent version for PCClassLevel)
	 */
	public void addNaturalWeapon(final Equipment weapon, final int aLevel) {
		/*
		 * CONSIDER Should this be checking for a null list??
		 */
		final LevelProperty lp = new LevelProperty(aLevel, weapon);
		naturalWeapons.add(lp);
	}

	/**
	 * Retrieve the list of spells for the class. Warning this overrides the
	 * PObject method getSpellList and obnly returns the spells up to the level
	 * held in the class. This may not be what you expect.
	 * 
	 * @see pcgen.core.PObject#getSpellList()
	 */
	/*
	 * DELETEMETHOD This implies a whole host of work to be done in order to use
	 * PObject's version of this method.
	 * 
	 * First of all, SpellSupport is level aware, and for many reasons, that is
	 * bad (and will be unnecessary, yea!). Once SpellSupport's knowledge of
	 * levels is eliminated, this can be removed, as it will not differ from
	 * PObject's version of this method.
	 * 
	 * However, in order NOT to break PCClass and PCClassLevel, the level
	 * awareness that is in SpellSupport needs to be transferred out to the
	 * PCClass, which will likely have to create one SpellSupport object per
	 * level and have those stored and ready for passing to PCClassLevels.
	 */
	@Override
	public List<PCSpell> getSpellList() {
		return getSpellSupport().getSpellList(getLevel());
	}

	/**
	 * Retrieve the full list of spells for the class. This will return all
	 * spells defined for the class irrespective of the level in the class
	 * currently held.
	 * 
	 * @return The full list of spells for the class.
	 */
	/*
	 * DELETEMETHOD No one uses this, so remove it.
	 */
	public List<PCSpell> getFullSpellList() {
		return getSpellSupport().getSpellList(-1);
	}

	/**
	 * Returns the highest spell level for which a KNOWN: tag entry was found.
	 * 
	 * @return The highest spell level this class can know spells to.
	 */
	/*
	 * DELETEMETHOD since maxknownlevel is going to be deleted
	 */
	public int getMaxKnownSpellLevel() {
		return this.maxKnownLevel;
	}

	/**
	 * Parse the ATTACKCYCLE: string and build HashMap Only allowed values in
	 * attackCycle are: BAB, RAB or UAB
	 * 
	 * @param aString
	 *            Unparsed ATTACKCYCLE string.
	 */
	/*
	 * DELETEMETHOD by putting this back in the ATTACKCYCLE tag. That should 
	 * then provide an attackCycleMap into PCClass.
	 */
	public final void setAttackCycle(final String aString) {
		attackCycle = aString;
		if (aString.indexOf('|') == -1)
			return;

		final StringTokenizer aTok = new StringTokenizer(attackCycle,
				Constants.PIPE);

		while (aTok.hasMoreTokens()) {
			final String attackType = aTok.nextToken();
			final String aVal = aTok.nextToken();
			if (attackCycleMap == null) {
				attackCycleMap = new HashMap<String, String>();
			}
			attackCycleMap.put(attackType, aVal);
		}
	}

	/**
	 * Returns the unadjusted unprocessed attackCycle.
	 * 
	 * @return The base attackCycle string.
	 */
	/*
	 * DELETEMETHOD since attackCycle will be deleted
	 */
	public final String getAttackCycle() {
		return attackCycle;
	}

	/**
	 * Remove all auto feats gained via a level
	 * @param aLevel
	 */
	/*
	 * PCCLASSONLY I think (heh) that committing this to the PCClassLevel really should
	 * be part of the PCClass Factory, and thus part of the creation of the PCClassLevel
	 * and thus only in PCClass
	 */
	public void removeAllAutoFeats(final int aLevel)
	{
		if (featAutos != null)
		{
			for (int x = featAutos.size() - 1; x >= 0; --x)
			{
				StringTokenizer aTok = new StringTokenizer(featAutos.get(x), "|", false);
				final int level = Integer.parseInt(aTok.nextToken());

				if (level == aLevel)
				{
					featAutos.remove(x);
				}
			}
		}
		return;
	}
	
//	public void removeAutoAbilities(final AbilityCategory aCategory, final int aLevel)
//	{
//		if ( aCategory == AbilityCategory.FEAT )
//		{
//			removeAllAutoFeats(aLevel);
//			return;
//		}
//		
//		if ( theAutoAbilities == null )
//		{
//			return;
//		}
//		theAutoAbilities.put(aCategory, aLevel, null);
//	}
}
