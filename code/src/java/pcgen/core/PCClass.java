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
import java.util.Set;
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
import pcgen.core.utils.ChoiceList;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MapKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.DoubleKeyMap;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.MapCollection;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.AttackType;
import pcgen.util.enumeration.DefaultTriState;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

/**
 * <code>PCClass</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 */
public class PCClass extends PObject {
	public static final int NO_LEVEL_LIMIT = -1;

	/*
	 * FINALALLCLASSLEVELS Since this applies to a ClassLevel line
	 */
	private List<LevelProperty<Domain>> domainList = null;

	/*
	 * FINALALLCLASSLEVELS Since this applies to a ClassLevel line
	 */
	private List<LevelProperty<Movement>> movementList = null;
	
	/*
	 * FUTURETYPESAFETY This is throwing around Feat names as Strings. :(
	 * 
	 * This requires a Chooser of some type to be able to be present in PCClass, as
	 * this may be a CHOOSE: String rather than an individual Feat
	 */
	/*
	 * FINALALLCLASSLEVELS The automatic Feats appropriate to any given level (they
	 * should be stored in a series of LevelProperty objects) need to be placed
	 * into each individual PCClassLevel when it is constructed.
	 */
	private List<LevelProperty<String>> featAutos = null;

	/*
	 * FUTURETYPESAFETY The Feats should be type safe, not Strings... The challenge
	 * here is that this also is difficult to make Type Safe.  The problem is not 
	 * in having the Abilities themselves be passed in (That is distinctly 
	 * possible), it is in getting the associated Strings correct, as those
	 * are magically processed based on the PC's deity - so the DEITYWEAPON 
	 * associated String would need to be recognized as magical and processed
	 * correctly in the code before it is added to the PC, but still appear as
	 * the magical string here (and somehow do that in a typesafe way).
	 */
	/*
	 * FINALALLCLASSLEVELS Since the Feats are being granted by level, this needs to
	 * account for that and actually store these by level and put them into the
	 * appropriate PCClassLevel.
	 */
	private List<LevelProperty<String>> featList = null;

	/*
	 * LEVELONEONLY This variable (automatically known spells) only needs to be
	 * loaded into the first PCClassLevel returned by PCClass, because the data
	 * is static (doesn't change by level) and because it will be tested
	 * dynamically (does the PCClassLevel automatically know spell A?), it only
	 * needs to appear on one of the PlayerCharacter's PCClassLevels.
	 */
	private List<SpellFilter> knownSpellsList = null;

	/*
	 * FUTURETYPESAFETY This is an interesting case of Type Safety, that may not be
	 * possible, as this is a big magical in what it could be - School,
	 * Subschool, and other things. Need lots of investigation as to what the
	 * appropriate Type for this is or whether it's stuck as a String.
	 */
	/*
	 * FINALALLCLASSLEVELS This is the list of specialties that were taken as part of
	 * leveling up at a certain point. Therefore this gets moved to PCClassLevel =
	 * byproduct of addLevel
	 */
	private List<String> specialtyList = null;

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch.
	 * 
	 * The challenge here (which requires a new CHOICE class - is that this may
	 * not be simply a list, but could include a CHOICE as well... need to figure
	 * out how to do that in a type safe way :/
	 */
	/*
	 * TYPESAFETY This is throwing around template names as Strings. :(
	 */
	/*
	 * ALLCLASSLEVELS The templates [based on their LevelProperty] (not the raw
	 * Strings) need to be stored in EACH individual PCClassLevel.
	 */
	private List<LevelProperty<String>> templates = null;

	/*
	 * FINALALLCLASSLEVELS The SR List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<String>> SR = null;

	/*
	 * FINALALLCLASSLEVELS The encumberedLoadMove List is level dependent -
	 * heck, it's in a LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Load>> encumberedLoadMove = null;

	/*
	 * FINALALLCLASSLEVELS The encumberedArmorMove List is level dependent -
	 * heck, it's in a LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Load>> encumberedArmorMove = null;

	/*
	 * FINALALLCLASSLEVELS Since this seems to allow for class dependent additions of
	 * Domains, this needs to occur in each class level as appropriate.
	 */
	private List<LevelProperty<Domain>> addDomains = null;

	/*
	 * FINALALLCLASSLEVELS This is pretty obvious, as these are already in a
	 * LevelProperty... these go into the PCClassLevel
	 */
	private List<LevelProperty<Equipment>> naturalWeapons = null;

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel (inheritAttributesFrom - although that 
	 * could really be cleaned up and a better method found)
	 */
	private List<SubClass> subClassList = null;

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel (inheritAttributesFrom -
	 * although that could really be cleaned up and a better method found)
	 */
	private List<SubstitutionClass> substitutionClassList = null;
	
	/*
	 * DELETEVARIABLE There is NO use of this Tag at all in the data/* structure
	 * today, so support for this should be removed from this class.
	 */
	private List<String> uattList = new ArrayList<String>(); // TODO -
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
	 * REFACTOR Should decide whether this should be LevelProperty<Ability> and
	 * allow multiple instances of the same level within the List... that is how
	 * other variables are working - not this one because of the conversion from
	 * vFeatMap (which didn't use LevelProperty)
	 */
	private List<LevelProperty<List<Ability>>> vFeatList = null;
	
	/*
	 * ALLCLASSLEVELS Hard to tell here yet, since this is part of the Ability
	 * project, but this will need similar support to vFeatList??
	 */
	private DoubleKeyMap<AbilityCategory, Integer, List<Ability>> vAbilityMap = null;

	/*
	 * STRINGREFACTOR This is actually some form of HITDIE formula, not
	 * necessarily a number; however, the complex processing that takes place on
	 * it is NOT a Formula, per se. Therefore, the processing can be done either
	 * at import of the Tag, or at least before a PCClassLevel is created.
	 * 
	 * This is, unfortunately, a bit more difficult than it looks, since it is
	 * processing based on currently known data (the hit die size).  This makes
	 * it a bit more complicated to get the formula processing correct.
	 * 
	 * Note hat .MOD processing needs to be considered here - what happens when 
	 * a later .MOD copies a class and changes the HITDIE?  This can't be 
	 * completely pre-processed or it won't work
	 */
	/*
	 * REFACTOR This name??? Lock what? It is really a modification of the HITDIE
	 * that can take place in at each class level
	 */
	/*
	 * ALLCLASSLEVELS This is modifications of the Hit Die and therefore, needs
	 * to be placed into all of the ClassLevels, so that the PC can have HPs
	 * based on the ClassLevel.
	 */
	private List<LevelProperty<String>> hitDieLockList = null;

	/*
	 * ALLCLASSLEVELS skillPool is part each PCClassLevel and what that level
	 * grants to each PlayerCharacter (added by the PCClassLevel Factory, not
	 * by a tag)
	 */
	private int skillPool = 0;

	/*
	 * FUTURETYPESAFETY Dependent upon classSkillChoices being type safe, which
	 * in turn is dependent on the Chooser system being type safe :/
	 */
	/*
	 * FINALPCCLASSLEVELONLY
	 * FINALALLCLASSLEVELS classSkillList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<String> classSkillList = null;

	/*
	 * FUTURETYPESAFETY Dependent upon classSpellChoices being type safe, which
	 * in turn is dependent on the Chooser system being type safe :/
	 */
	/*
	 * FINALPCCLASSLEVELONLY
	 * FINALALLCLASSLEVELS classSpellList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<String> classSpellList = null;

	/*
	 * TYPESAFETY This should be working with Skill objects, not Strings
	 */
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
	 * FINALALLCLASSLEVELS The Vision List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Vision>> visionList = null;

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
	 * FINALALLCLASSLEVELS The abbrev simply needs to be directly loaded into each
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
	 * FINALPCCLASSONLY The selected delegate skill lists [see classSkillList]
	 * (not the raw classSkillChoices) need to be stored in EACH individual
	 * PCClassLevel. This is the case because each individual PCClassLevel will
	 * be capable of granting skills, and this is the delegate to determine what
	 * is appropriate (skill-wise) for any given PCClassLevel.
	 */
	/*
	 * FUTURETYPESAFETY This should be better than a String... the problem here
	 * is that this is dependent upon the Choice system being type safe. While
	 * it doesn't LOOK type-hostile, it is using a LOT of unchecked Objects,
	 * which makes me sensitive to the fact that it may NOT be entirely type
	 * safe. Better to rebuild that at some point to be Java 1.5 friendly and
	 * then have this as a later dependency.
	 */
	private ChoiceList<String> classSkillChoices = null;

	/*
	 * FUTURETYPESAFETY This should be better than a String... the problem here
	 * is that this is dependent upon the Choice system being type safe. While
	 * it doesn't LOOK type-hostile, it is using a LOT of unchecked Objects,
	 * which makes me sensitive to the fact that it may NOT be entirely type
	 * safe. Better to rebuild that at some point to be Java 1.5 friendly and
	 * then have this as a later dependency.
	 */
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
	 * FINALPCCLASSONLY The selected delegate spell lists [see classSpellList]
	 * (not the raw classSpellString) need to be stored in EACH individual
	 * PCClassLevel. This is the case because each individual PCClassLevel will
	 * be capable of holding individual spells known and spells cast (per day)
	 * and this is the delegate to determine what is appropriate for any given
	 * PCClassLevel.
	 */
	private ChoiceList<String> classSpellChoices = null;

	/*
	 * FUTURETYPESAFETY The Deity List should be something other than Strings
	 * 
	 * Question: How to do this with the 'Magical' ANY Deity?
	 */
	/*
	 * FINALALLCLASSLEVELS Might as well place this into all PCCLassLevels, since it
	 * does seem to apply to all of them individually
	 */
	private List<String> deityList = new ArrayList<String>();

	/*
	 * FUTURETYPESAFETY This should not be a String, but a member of a Typesafe
	 * Enumeration of Classes... unfortunately, that really requires a two-pass
	 * design for the LST files... because the EX Class may not exist at the 
	 * time this tag is hit.
	 */
	/*
	 * FINALALLCLASSLEVELS Because this indicates what a Class becomes when the
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
	 * UNKNOWNDESTINATION Don't know where to put this yet... this is a
	 * COMPLICATED function that allows the exchange of levels (presumably on a
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
	 * FINALALLCLASSLEVELS Because this indicates prohibited Spell Schools and Spells
	 * Known and Cast are granted by each PCClassLevel, this must be passed into
	 * each and every PCClassLevel (though it can be given in its pure form).
	 */
	/*
	 * TYPESAFETY Since this is storing Spell Schools/SubSchools, this should
	 * be type safe
	 */
	private List<String> prohibitedSchools = null;

	/*
	 * PCCLASSLEVELONLY Since this is not part of a tag and is related to how
	 * spells are related to a PCClassLevel
	 * 
	 * Actually, I may want to DELETEVARIABLE - this is really a cache, and 
	 * need to consider how valuable it is to have a cache vs. not have it.
	 */
	private String stableSpellKey = null;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the sublevel actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private String subClassKey = Constants.s_NONE;

	/*
	 * FINALALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the substitution level actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private Map<Integer, String> substitutionClassKey = null;

	// private TreeSet<Language> languageBonus = new TreeSet<Language>();

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubClass
	 * 
	 * CONSIDER Technically, that's not true ... this is really an indication of 
	 * whether the class HAS subclasses or not (an abstract test, not a practical
	 * "a subclass has been selected" test.  Is this therefore duplicate information??
	 * 
	 * Can I DELETEVARIABLE?
	 */
	private boolean hasSubClass = false;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubstitutionClass
	 * 
	 * CONSIDER Technically, that's not true ... this is really an indication of
	 * whether the class HAS substitution classes or not (an abstract test, not
	 * a practical "a substitution class has been selected" test. Is this
	 * therefore duplicate information??
	 * 
	 * Can I DELETEVARIABLE?
	 */
	private boolean hasSubstitutionClass = false;

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
	 * ALLCLASSLEVELS Because this indicates prerequisites for a given
	 * PCClassLevel (though it's dependent upon the existing classes of the
	 * PlayerCharacter), it must be passed in to the PCClassLevel. This is not
	 * completely intuitive (it could be tested inside of the Factory that
	 * creates the PCClassLevel), but the system should also be able to do
	 * "post-hoc" verifications (to find cases later invalidated due to Data
	 * updates or code fixes) and therefore it should be stored in the
	 * PCClassLevel
	 * 
	 * I really want to DELETEVARIABLE
	 */
	private boolean multiPreReqs = false;

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
	 * 
	 * Well, it looks like null is at least marginally significant, but nothing
	 * that a special (perhaps negative) value couldn't fix. The key here is in
	 * getting the legal parameters of this method established. The setter is
	 * gating the possible values (yea!) but then there are later assumptions in
	 * other parts of the code that the value can be anything, and they retest
	 * the value. Make consistent and eliminate useless code - thpr 11/6/06
	 */
	private Integer levelsPerFeat = null;

	/*
	 * PCCLASSONLY This is ONLY required in the construction of a PCClassLevel -
	 * it never needs (or shouldn't need) to be exported into the PCClassLevel.
	 * 
	 * Note: It is possibly useful to have a boolean isMaxLevel() available in a
	 * PCClassLevel, but that is TBD
	 */
	private int maxLevel = NO_LEVEL_LIMIT;

	/*
	 * FORMULAREFACTOR This is currently processed elsewhere - should be
	 * processed as a Formula, not a String...
	 */
	/*
	 * FINALALLCLASSLEVELS The RESULT of this formula - at least I think it's the
	 * result - need to check on what's legal in the formula and whether this
	 * must be calculated on the fly or not - can be placed into each
	 * PCClassLevel. NOTE: This placement into the PCClassLevel needs to be
	 * AFTER the SKILLMULTIPLIER from GameMode is properly handled... or perhaps
	 * PCClassLevel is GameMode aware??
	 */
	private String skillPointFormula = "0";

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
	private HashMap<AttackType, String> attackCycleMap = null;

	private SpellProgressionInfo castInfo = null;

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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClassLevel since
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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
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
	 * Returns the list of domains that this class grants access to for ONLY the
	 * specifically given level
	 * 
	 * @return List of Domain choices for the given level of this class.
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<Domain> getAddDomains(int domainLevel) {
		if (addDomains == null) {
			final List<Domain> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		List<Domain> returnList = new ArrayList<Domain>();
		for (LevelProperty<Domain> prop : addDomains)
		{
			if (prop.getLevel() == domainLevel)
			{
				returnList.add(prop.getObject());
			}
		}
		return returnList;
	}
	
	/*
	 * FINALPCCLASSONLY This is only for editing of a PCClass; therefore
	 * not required for a PCClassLevel
	 */
	public List<LevelProperty<Domain>> getAddDomains() {
		if (addDomains == null) {
			return null;
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
	 * FINALPCCLASSANDLEVEL Since this comes from a tag and is requried by at least
	 * some PCClassLevels in order to calculate bonus spells, this must appear
	 * in both PCClass and PCClassLevel
	 */
	public final void setBonusSpellBaseStat(final String baseStat) {
		getConstructingSpellProgressionInfo().setBonusSpellBaseStatAbbr(baseStat);
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
	 * FINALPCCLASSANDLEVEL Since this comes from a tag and is requried by at least
	 * some PCClassLevels in order to calculate bonus spells, this must appear
	 * in both PCClass and PCClassLevel
	 */
	public final String getBonusSpellBaseStat() {
		return castInfo == null ? Constants.s_DEFAULT : castInfo.getBonusSpellBaseStatAbbr();
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

						if (PreParserFactory.isPreReqString(bString)) {
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
	 * Return the number of spells a character can cast in this class for the
	 * current level.
	 * 
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	public int getCastForLevel(final int spellLevel, final String bookName,
			final PlayerCharacter aPC) {
		return getCastForLevel(spellLevel, bookName, true, true, aPC);
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
	public int getCastForLevel(final int spellLevel,
			final String bookName, final boolean includeAdj,
			final boolean limitByStat, final PlayerCharacter aPC) {
		int pcLevel = getLevel();
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
				&& (hasSpecialtyList() || aPC.hasCharacterDomainList())) {
			// We need to do this for EVERY spell level up to the
			// one really under consideration, because if there
			// are any specialty spells available BELOW this level,
			// we might wind up using THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ++ix) {
				final List<CharacterSpell> aList = getSpellSupport()
						.getCharacterSpell(null, Constants.EMPTY_STRING, ix);
				List<Spell> bList = new ArrayList<Spell>();

				if (!aList.isEmpty()) {
					// Assume no null check on castInfo requried, because
					// getNumFromCastList above would have returned -1
					if ((ix > 0) && "DIVINE".equalsIgnoreCase(castInfo.getSpellType())) {
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

	/*
	 * FINALPCCLASSONLY This is required in PCClass since it is 
	 * a Tag, but is used for construction of PCClassLevels and therefore
	 * not passed into a PCCLassLevel
	 */
	public void setClassSkillChoices(int choiceCount, List<String> choices) {
		classSkillChoices = ChoiceList.getChoiceList(choiceCount, choices);
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public final List<String> getClassSpellList() {
		return classSpellList;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is appropriate for both PCClassLevel and 
	 * PCClass since it is a Tag
	 */
	public final void addDeity(String aDeity) {
		deityList.add(aDeity);
	}
	
	/*
	 * FINALPCCLASSONLY Since this is for constructing a PCClass
	 */
	public final void clearDeityList() {
		deityList.clear();
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getDeityList() {
		return deityList;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/**
	 * Returns the Domains provided to a character by ONLY the given level
	 * 
	 * There is a contract on the users of getDomainList: If you take 
	 * a Domain out of the List returned, you MUST clone the Domain or you 
	 * can cause problems for PCClass.  This is allowed for speed today and
	 * in the hopes that Domain and the PObjects become immutable soon :)
	 */
	public final List<Domain> getDomainList(int domainLevel) {
		if (domainList == null) {
			return Collections.emptyList();
		}
		List<Domain> returnList = new ArrayList<Domain>();
		for (LevelProperty<Domain> prop : domainList)
		{
			if (prop.getLevel() == domainLevel)
			{
				returnList.add(prop.getObject());
			}
		}
		return returnList;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass since it
	 * is a Tag
	 */
	public final void setExClass(final String aString) {
		exClass = aString;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getExClass() {
		return exClass;
	}

	/*
	 * FINALPCCLASSONLY This is only for PCClass - used to edit the class
	 */
	public final Collection<LevelProperty<String>> getAllFeatAutos() {
		Collection<LevelProperty<String>> returnList = null;
		if (featAutos == null) {
			List<LevelProperty<String>> empty = Collections.emptyList();
			returnList = Collections.unmodifiableCollection(empty);
		} else {
			returnList = Collections.unmodifiableCollection(featAutos);
		}
		return returnList;
	}
	
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final Collection<String> getFeatAutos(int aLevel) {
		List<String> returnList = new ArrayList<String>();
		if (featAutos != null) {
			for (LevelProperty<String> autoFeat : featAutos) {
				if (autoFeat.getLevel() == aLevel) {
					returnList.add(autoFeat.getObject());
				}
			}
		}
		return returnList;
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
	 * @param aFeat
	 *            The feat string to remove.
	 */
	/*
	 * FINALPCCLASSONLY This is for GUI construction of a PCClass and is therefore
	 * only required in PCClass and not PCClassLevel
	 */
	public boolean removeFeatAuto(String type) {
		if (featAutos == null) {
			return false;
		}
		for (LevelProperty<String> autoFeat : featAutos) {
			if (autoFeat.getObject().equals(type)) {
				return featAutos.remove(autoFeat);
			}
		}
		return false;
	}
	
	/**
	 * Removes an AUTO feat from the list of feats this class grants.
	 * 
	 * @param aLevel
	 *            The level the feat would have been granted at.
	 * @param aFeat
	 *            The feat string to remove.
	 */
	/*
	 * FINALPCCLASSONLY This is for GUI construction of a PCClass and is therefore
	 * only required in PCClass and not PCClassLevel
	 */
	public boolean removeFeatAuto(int aLevel, String type) {
		if (featAutos == null) {
			return false;
		}
		for (LevelProperty<String> autoFeat : featAutos) {
			if (autoFeat.getLevel() == aLevel && autoFeat.getObject().equals(type)) {
				return featAutos.remove(autoFeat);
			}
		}
		return false;
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
	 * FINALPCCLASSONLY This is for construction of a PCClass
	 */
	public final List<LevelProperty<String>> getFeatList() {
		if (featList == null) {
			final List<LevelProperty<String>> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(featList);
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void setHitDie(final int dice) {
		hitDie = dice;
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
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
		if (hitDieLockList == null) {
			hitDieLockList = new ArrayList<LevelProperty<String>>();
		}
		hitDieLockList.add(LevelProperty.getLevelProperty(aLevel, hitDieLock));
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory) (with level dependent
	 * differences, of course)
	 */
	protected String getHitDieLock(final int aLevel) {
		if (hitDieLockList != null) {
			for (LevelProperty<String> lp : hitDieLockList) {
				if (lp.getLevel() == aLevel) {
					return lp.getObject();
				}
			}
		}
		return null;
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

	/**
	 * Identify if this class has a cap on the number of levels it is 
	 * possible to take.
	 * @return true if a cap on levels exists, false otherwise.
	 */
	public final boolean hasMaxLevel()
	{
		return maxLevel != NO_LEVEL_LIMIT;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (because they grant spells)
	 */
	public final void setMemorizeSpells(final boolean memorizeSpells) {
		getConstructingSpellProgressionInfo().setMemorizeSpells(memorizeSpells);
	}

	/*
	 * FINALPCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (because they grant spells)
	 */
	public final boolean getMemorizeSpells() {
		//Defaults to true, so null returns true
		return castInfo == null || castInfo.memorizesSpells();
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (for later verification)
	 * 
	 * I am trying to DELETEMETHOD by deletign multipreqs - thpr 11/6/06
	 */
	public final void setMultiPreReqs(final boolean multiPreReqs) {
		this.multiPreReqs = multiPreReqs;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (since the prereq needs to be 
	 * enforced at every level-up)
	 * 
	 * Trying to DELETEMETHOD by cleaning out PRERACETYPE - but need
	 * some more guidance from Tir on how this should work - thpr 11/6/06
	 */
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
	 * FINALPCCLASSLEVELONLY created during PCClassLevel creation (in the factory)
	 */
	public final Collection<String> getSpecialtyList() {
		if (specialtyList == null) {
			final List<String> ret = Collections.emptyList();
			return Collections.unmodifiableList(ret);
		}
		return Collections.unmodifiableList(specialtyList);
	}

	/*
	 * FINALPCCLASSLEVELONLY For boolean testing of possession
	 */
	public final boolean hasSpecialtyList() {
		return specialtyList != null && specialtyList.size() > 0;
	}
	
	/*
	 * FINALPCCLASSLEVELONLY Input during construction of a PCClassLevel
	 */
	public final void addSpecialty(final String aSpecialty) {
		if (specialtyList == null) {
			specialtyList = new ArrayList<String>();
		}
		specialtyList.add(aSpecialty);
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (of course, a level independent version for PCClassLevel
	 */
	public void addAddDomain(final int aLevel, final Domain aDomain) {
		if (addDomains == null) {
			addDomains = new ArrayList<LevelProperty<Domain>>();
		}
		addDomains.add(LevelProperty.getLevelProperty(aLevel, aDomain));
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
	/*
	 * REFACTOR At least CONSIDER Whether this should be an @Override of 
	 * getDisplayName()?  What additional value does this provide by being
	 * a separate method?? - thpr 11/6/06
	 */
	public String getDisplayClassName() {
		if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE)) {
			return getSubClassKeyed(subClassKey).getDisplayName();
		}

		return getDisplayName();
	}

	/*
	 * PCCLASSLEVELONLY This can be simplified, however, since there won't be the
	 * same subClass type delegation within the new PCClassLevel. - note this
	 * method is really the PCClassLevel implementation of getDisplayClassName()
	 * above [so technically this method doesn't go into PCClass, the method
	 * above does)
	 */
	/*
	 * REFACTOR Once this is in PCClassLevel, at least CONSIDER Whether this
	 * should be an @Override of getDisplayName()? What additional value does
	 * this provide by being a separate method?? - thpr 11/6/06
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
	 * 
	 * Trying to DELETEMETHOD by deleting HASSUBCLASS - thpr 11/6/06
	 */
	public final void setHasSubClass(final boolean arg) {
		hasSubClass = arg;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the substitution class is activated, this is required in both
	 * locations.
	 * 
	 * Trying to DELETEMETHOD by deleting HASSUBSTITUTIONCLASS - thpr 11/6/06
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
		getConstructingSpellProgressionInfo().setContainsSpellFormula(arg);
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass because it
	 * is a Tag
	 */
	public final boolean addProhibitedSchool(String school) {
		if (prohibitedSchools == null) {
			prohibitedSchools = new ArrayList<String>();
		}
		boolean addedSchool = false;
		if (!prohibitedSchools.contains(school)) {
			addedSchool = prohibitedSchools.add(school);
		}
		return addedSchool;
	}
	
	/*
	 * FINALPCCLASSONLY This is required in PCClass because it is used in
	 * construction of PCClassLevel... (or is it required there too??)
	 */
	public final void clearProhibitedSchools() {
		prohibitedSchools = null;
	}
	
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getProhibitedSchools() {
		return prohibitedSchools;
	}

	/*
	 * PCCLASSLEVELONLY This is an active level calculation, and is therefore
	 * only appropriate in the PCClassLevel that has the particular Hit Die for
	 * which the calculation is required.
	 */
	public int getLevelHitDie(final PlayerCharacter aPC, final int classLevel) {
		// Class Base Hit Die
		int currHitDie = getBaseHitDie();

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
	/*
	 * REFACTOR I think this should be a separate Class. Since this modification
	 * can't be done before it is time to do it "on the fly", we can at least
	 * "compile" the Die Lock, so that this string parsing doesn't happen every
	 * time. The question is: Is this an enumeration, Interface with
	 * implementations, or what is the best method for getting this effect
	 * without making the system far too complicated for something that really
	 * is very simple. - thpr 11/6/06
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
	 * sets whether stat modifier is applied to skill points at level-up time
	 */
	public final void setModToSkills(final boolean bool) {
		modToSkills = bool;
	}

	public final boolean getModToSkills() {
		return modToSkills;
	}

	/*
	 * FINALPCCLASSANDLEVEL Since this is a tag, and also impacts the number of
	 * skills at a particular level, this (or perhaps just the result?) needs to
	 * be in both PCClass and PCClassLevel
	 */
	public final void setSkillPointFormula(final String argFormula) {
		skillPointFormula = argFormula;
	}

	/*
	 * FINALPCCLASSANDLEVEL Since this is a tag, and also impacts the number of
	 * skills at a particular level, this (or perhaps just the result?) needs to
	 * be in both PCClass and PCClassLevel
	 */
	public String getSkillPointFormula() {
		return skillPointFormula;
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void setSpellBaseStat(final String baseStat) {
		getConstructingSpellProgressionInfo().setSpellBaseStatAbbr(baseStat);
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellBaseStat() {
		return castInfo == null ? Constants.s_NONE : castInfo.getSpellBaseStatAbbr();
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
		boolean needPipe = false;

		for (String keyStr : classSpellList) {
			if (needPipe) {
				aBuf.append(Constants.PIPE);
			}
			needPipe = true;

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
	 * FINALPCCLASSONLY This is required in PCClass since it is 
	 * a Tag, but is used for construction of PCClassLevels and therefore
	 * not passed into a PCCLassLevel
	 */
	public final void setClassSpellChoices(int choiceCount, List<String> choices) {
		classSpellChoices = ChoiceList.getChoiceList(choiceCount, choices);
	}

	/*
	 * FINALPCCLASSONLY This is only the choice list, not the actual choices
	 * that were made, so this is only required in the PCClass
	 */
	public final ChoiceList<String> getClassSpellChoices() {
		return classSpellChoices;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass since 
	 * it is a Tag
	 */
	public final void setSpellType(final String newType) {
		if (castInfo == null && Constants.s_NONE.equals(newType)) {
			//Don't create a SpellProgressionInfo to set to default!!
			return;
		}
		getConstructingSpellProgressionInfo().setSpellType(newType);
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType() {
		return castInfo == null ? Constants.s_NONE : castInfo.getSpellType();
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and PCClass since
	 * it is a Tag [with level dependent differences, of course)
	 */
	public void setCast(final int aLevel, final List<String> cast) {
		getConstructingSpellProgressionInfo().setCast(aLevel, cast);
	}

	/*
	 * PCCLASSLEVELONLY Since this is the PCClassLevel specific version
	 * of getCastList, it is only appropriate for the class levels.
	 * 
	 * May also be required in the Factory for PCClassLevels, so might
	 * also appear in PCClass.
	 */
	public List<String> getCastListForLevel(int aLevel) {
		if (castInfo == null) {
			return null;
		}
		return castInfo.getCastForLevel(aLevel);
	}
	
	public boolean hasCastList() {
		return castInfo != null && castInfo.hasCastProgression();
	}
	
	/*
	 * PCCLASSONLY For editing a PCClass and Global checks...
	 */
	public Map<Integer, List<String>> getCastProgression() {
		if (castInfo == null) {
			return null;
		}
		return castInfo.getCastProgression();
	}

	/**
	 * Return the level of the highest level spell offered by the class.
	 * 
	 * @return The level of the highest level spell available.
	 */
	public int getHighestLevelSpell() {
		if (castInfo == null) {
			return -1;
		}
		return Math.max(castInfo.getHighestCastSpellLevel(),
				castInfo.getHighestKnownSpellLevel());
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
	public int getKnownForLevel(final int spellLevel,
			final PlayerCharacter aPC) {
		return getKnownForLevel(spellLevel, "null", aPC);
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
	 * PCCLASSONLY This is for PCClass construction and Global queries
	 */
	public Map<Integer, List<String>> getKnownMap() {
		if (Constants.EMPTY_STRING.equals(castAs)
				|| getKeyName().equals(castAs)) {
			if (castInfo == null) {
				return null;
			}
			return castInfo.getKnownProgression();
		}

		final PCClass aClass = Globals.getClassKeyed(castAs);
		if (aClass != null) {
			return aClass.getKnownMap();
		}

		throw new IllegalStateException("Unknown Class for CASTAS: " + castAs);
	}
	
	/*
	 * PCCLASSANDLEVEL This is used for detecting spell casting ability
	 */
	public boolean hasKnownList() {
		if (!Constants.EMPTY_STRING.equals(castAs) && !getKeyName().equals(castAs)) {
			final PCClass aClass = Globals.getClassKeyed(castAs);
			if (aClass != null) {
				return aClass.hasKnownList();
			}
		}
		return castInfo != null && castInfo.hasKnownProgression();
	}

	/**
	 * @return The list of automatically known spells.
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<SpellFilter> getKnownSpellsList() {
		if (knownSpellsList == null) {
			final List<SpellFilter> ret = Collections.emptyList();
			return ret;
		}
		return Collections.unmodifiableList(knownSpellsList);
	}

	/*
	 * PCCLASSONLY This is required in PCClass for PCClass editing
	 * 
	 * DELETEMETHOD - this isn't used??? Or perhaps that indicates 
	 * that the GUI LST CLASS editor is incomplete :)
	 */
	public final Map<Integer, List<String>> getSpecialtyKnownList() {
		if (castInfo == null) {
			return null;
		}
		return castInfo.getSpecialtyKnownMap();
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
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public final void addSpecialtyKnown(int aLevel, List<String> aList) {
		getConstructingSpellProgressionInfo().setSpecialtyKnown(aLevel, aList);
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
		if (iCasterLevel == 0) {
			// can't cast spells!
			return -1;
		}

		List<String> castListForLevel = getCastListForLevel(iCasterLevel);
		if (castListForLevel == null || iSpellLevel >= castListForLevel.size()) {
			return -1;
		}
		String aString = castListForLevel.get(iSpellLevel);

		int aNum;
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

	/*
	 * REFACTOR Again, there is rudundant information here in the fetching of
	 * what is currently possible for the current character level. This is
	 * generally something that should only appear in the PCClassLevel, but
	 * should be considered with the wider range of "what can I really cast"
	 * methods that are tagged to be refactored.
	 */
	public String getBonusCastForLevelString(
			final int spellLevel, final String bookName,
			final PlayerCharacter aPC) {
		if (getCastForLevel(spellLevel, bookName, true, true, aPC) > 0) {
			// if this class has a specialty, return +1
			if (hasSpecialtyList()) {
				return "+1";
			}

			if (!aPC.hasCharacterDomainList()) {
				return "";
			}

			// if the spelllevel is >0 and this class has a characterdomain
			// associated with it, return +1
			if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(getSpellType())) {
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
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this character can cast of the
	 *         given spell level.
	 */
	public int getCastForLevel(final int spellLevel, final PlayerCharacter aPC) {
		return getCastForLevel(spellLevel, Globals.getDefaultSpellBook(), true,
				true, aPC);
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
	public int getSpecialtyKnownForLevel(final int spellLevel,
			final PlayerCharacter aPC) {
		int total;
		total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS."
				+ getKeyName() + ";LEVEL." + spellLevel);
		total += (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE."
				+ getSpellType() + ";LEVEL." + spellLevel);

		int pcLevel = getLevel();
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

		if (castInfo != null) {
			List<String> specKnown = castInfo.getSpecialtyKnownForLevel(pcLevel);
			if (specKnown != null) 
			{
				if (specKnown.size() > spellLevel) {
					int t;
					if (castInfo.containsSpellFormula()) {
						t = aPC.getVariableValue(specKnown.get(spellLevel), "").intValue();
					} else {
						t = Integer.parseInt(specKnown.get(spellLevel));
					}
					total += t;
				}
			}

			// if we have known spells (0==no known spells recorded) or a psi
			// specialty.
			if ((total > 0) && (spellLevel > 0)) {
				// make sure any slots due from specialties (including domains) are
				// added
				total += castInfo.getKnownSpellsFromSpecialty();
			}
		}

		return total;
	}

	/*
	 * FINALPCCLASSLEVELONLY Since this is setting the key that will appear in
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
	 * FINALPCCLASSLEVELONLY Since this is getting the key that will appear in
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
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
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
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final SubstitutionClass getSubstitutionClassKeyed(final String aKey) {
		if (substitutionClassList == null) {
			return null;
		}

		for (SubstitutionClass sc : substitutionClassList) {
			if (sc.getKeyName().equals(aKey)) {
				return sc;
			}
		}

		return null;
	}

	/*
	 * Note: Since this is local access, it does not need to override
	 * getTemplateList from PObject (although it is probably confusing that it
	 * doesn't). The key point is ensuring that getTemplates(final boolean flag,
	 * final PlayerCharacter aPC) is still properly overriding the PObject
	 * method so that the proper templates for this PCClass are applied to the
	 * PlayerCharacter.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<LevelProperty<String>> getTemplates() {
		if (templates == null) {
			final List<LevelProperty<String>> ret = Collections.emptyList();
			return ret;
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
	 *         TODO - This should be removed.
	 */
	/*
	 * DELETEMETHOD Associated with the unused List uattList
	 */
	public final void addUatt(String s) {
		uattList.add(s);
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method.  The PCClassLevelversion should NOT be level 
	 * dependent
	 */
	public void addFeatAuto(final int aLevel, final String aString) {
		if (featAutos == null) {
			featAutos = new ArrayList<LevelProperty<String>>();
		}
		featAutos.add(LevelProperty.getLevelProperty(aLevel, aString));
	}

	/*
	 * FINALPCCLASSONLY This is an editor and loader requirement, therefore
	 * PCClass only
	 */
	public void clearFeatAutos() {
		featAutos = null;
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
	 * PCCLASSLEVELONLY This is required for PlayerCharacter.makeEXclass()
	 */
	public Map<Integer, Integer> getHitPointMap() {
		return new HashMap<Integer, Integer>(hitPointMap);
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
	public final void setHitPointMap(Map<Integer, Integer> hpMap) {
		hitPointMap = null;
		if (hpMap != null) {
			hitPointMap = new HashMap<Integer, Integer>(hpMap);
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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel PCClass since it is
	 * a Tag [with level dependence differences, of course)
	 */
	public final void addVision(int aLevel, Vision vis) {
		if (visionList == null) {
			visionList = new ArrayList<LevelProperty<Vision>>();
		}
		visionList.add(LevelProperty.getLevelProperty(aLevel, vis));
	}

	/*
	 * FINALPCCLASSONLY This is an editor and loader requirement, therefore
	 * PCClass only
	 */
	public void clearVisionList() {
		if (visionList != null)
		{
			visionList.clear();
		}
	}
	
	/*
	 * FINALPCCLASSONLY This is an editor and loader requirement, therefore
	 * PCClass only
	 */
	public boolean removeVisionType(VisionType type) {
		if (visionList == null) {
			return false;
		}
		for (LevelProperty<Vision> lp : visionList) {
			if (lp.getObject().getType().equals(type)) {
				return visionList.remove(lp);
			}
		}
		return false;
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
			if (level > curLevel || aPC.isImporting()) {
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();

					      StringBuffer    formula;
					final String          aString = Globals.getBonusFeatString();
					final StringTokenizer aTok    = new StringTokenizer(aString, "|", false);
					final int startLevel = Integer.parseInt(aTok.nextToken());
					final int rangeLevel = Integer.parseInt(aTok.nextToken());
					       int divisor    = 1;
					      
					if (SettingsHandler.isMonsterDefault())
					{
						if (aPC.getRace().getMonsterClass(aPC,false) != null &&
								aPC.getRace().getMonsterClass(aPC,false).equalsIgnoreCase(this.getKeyName()))
						{
							int monLev = aPC.getRace().getMonsterClassLevels(aPC, false);

							Integer mLevPerFeat = getLevelsPerFeat();
							divisor = (mLevPerFeat != null && mLevPerFeat >= 1) ? mLevPerFeat : rangeLevel;
							formula = new StringBuffer("max(0,floor((CL-");
							formula.append(monLev);
							formula.append(")/");
							formula.append(divisor);
							formula.append("))");

							StringBuffer aBuf = new StringBuffer("0|FEAT|MONSTERPOOL|");
							aBuf.append(formula);
							BonusObj bon = Bonus.newBonus(aBuf.toString());
							bon.setCreatorObject(this);
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
							addBonusList(bon);
						}
					}
					else
					{
						StringBuffer aBuf = new StringBuffer("0|FEAT|PCPOOL|CL/");
						aBuf.append(rangeLevel);
						BonusObj bon = Bonus.newBonus(aBuf.toString());
						bon.setCreatorObject(this);
						addBonusList(bon);
					}
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

		if (!aPC.isImporting() && (curLevel < level)) {
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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/*
	 * FUTUREREFACTOR This would really be nice to have initilized when the LST files
	 * are read in, which is possible because the ClassTypes are all defined as part
	 * of the GameMode... however the problem is that the order of the ISMONSTER tag
	 * and the TYPE tags cannot be defined - .MODs and .COPYs make it impossible to 
	 * guarantee an order.  Therefore, this must wait for a two-pass design in the
	 * import system - thpr 10/4/06
	 */
	public boolean isMonster() {
		if (monsterFlag != null) {
			return monsterFlag.booleanValue();
		}

		if (getMyTypeCount() == 0) {
			return false;
		}

		for (String type : getTypeList(false)) {
			final ClassType aClassType = SettingsHandler.getGame()
					.getClassTypeByName(type);

			if ((aClassType != null) && aClassType.isMonster()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * get the Natural Attacks for this level
	 * 
	 * @return natural weapons list
	 */
	/*
	 * FINALPCCLASSLEVELONLY This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/*
	 * CONSIDER The fact that this does NOT override PObject would break
	 * EditorMainForm, if EditorMainForm was smart enough to realize that a
	 * PCClass can have natural weapons. Currently it does not. It would also
	 * need to be smart enough to deal with level dependencies of Natural
	 * Weapons in PCClasses, so that is non-trivial. This issue is trackered as
	 * 1590553
	 */
	public List<Equipment> getNaturalWeapons(int aLevel) {
		if (naturalWeapons == null) {
			return new ArrayList<Equipment>();
		}
		
		final List<Equipment> tempArray = new ArrayList<Equipment>();

		for (LevelProperty<Equipment> lp : naturalWeapons) {
			if (lp.getLevel() == level) {
				tempArray.add(lp.getObject());
			}
		}

		return tempArray;
	}
	
	/*
	 * FINALPCCLASSONLY For editing PCClasses
	 */
	public List<LevelProperty<Equipment>> getAllNaturalWeapons() {
		if (naturalWeapons == null) {
			return new ArrayList<LevelProperty<Equipment>>();
		}
		return naturalWeapons;
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
				&& (!aPC.getRace().getRaceType().equalsIgnoreCase(preRaceType) && !(aPC
						.getCritterType().indexOf(preRaceType) >= 0)))
		// Move the check for type out of race and into PlayerCharacter to make
		// it easier for a template to adjust it.
		{
			return false;
		}

		if (multiPreReqs && aPC.getClassList().isEmpty()) {
			return true;
		}
		
		if (!PrereqHandler.passesAll(getPreReqList(), aPC, this)) {
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
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void setSR(int aLevel, String srString) {
		if (SR == null) {
			SR = new ArrayList<LevelProperty<String>>();
		}
		SR.add(LevelProperty.getLevelProperty(aLevel, srString));
	}
	
	/*
	 * FINALPCCLASSONLY Since this is part of LST file import
	 */
	public void clearSR() {
		SR = null;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is required to fetch the SR
	 * 
	 * UNWIND the level checking will have to be unwound into the users of this
	 * class, as these SRs will not pass from one PCClassLevel to another unless
	 * they are specified...
	 */
	protected int getSR(PlayerCharacter aPC) {
		if (aPC == null) {
			return 0;
		}
		
		LevelProperty<String> activeLP = null;

		if (SR != null) {
			final int lvl = level;
			for (LevelProperty<String> lp : SR) {
				if (lp.getLevel() > lvl) {
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel()) {
					activeLP = lp;
					continue;
				}
			}
		}

		//if there's a current PC, go ahead and evaluate the formula
		if (activeLP != null)
		{
			return aPC.getVariableValue(activeLP.getObject(), getQualifiedKey()).intValue();
		}

		return 0;
		
	}
	
	/*
	 * FINALPCCLASSONLY This is for building a PCClass
	 */
	public List<LevelProperty<String>> getSRlist() {
		if (SR == null)
		{
			/*
			 * CONSIDER This is a heavy-weight get... and inconsistent with
			 * getFeatList, et al. What should be the proper method (should the
			 * caller be required to gate on null, should these be safe gets,
			 * should the variables be initialized empty lists??) - thpr
			 * 11/10/06
			 */
			SR = new ArrayList<LevelProperty<String>>();
		}
		return SR;
	}

	/*
	 * FINALPCCLASSONLY This is for editing classes
	 */
	public LevelProperty<String> getSRforLevel(int aLevel) {
		if (SR != null) {
			for (LevelProperty<String> lp : SR) {
				if (lp.getLevel() == aLevel) {
					return lp;
				}
			}
		}
		return null;
	}

	/*
	 * FINALPCCLASSANDLEVEL Since this is in the PCClass (from a Tag) and
	 * PCClassLevel (as an indication of the spells granted by the PCClassLevel)
	 */
	public final void setSpellBookUsed(final boolean argUseBook) {
		getConstructingSpellProgressionInfo().setSpellBookUsed(argUseBook);
	}

	/*
	 * FINALPCCLASSANDLEVEL Since this is in the PCClass (from a Tag) and
	 * PCClassLevel (as an indication of the spells granted by the PCClassLevel)
	 */
	public final boolean getSpellBookUsed() {
		return castInfo != null && castInfo.usesSpellBook();
	}

	public void setCRFormula(final String argCRFormula) {
		CRFormula = argCRFormula;
	}

	/**
	 * Sets this class as a "monster" class.
	 * <p>
	 * Monster classes have special behaviour in certain cases.
	 * 
	 * @param aFlag
	 *            true if this is a monster class.
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass since
	 * it is a Tag
	 */
	public void setMonsterFlag(final boolean aFlag) {
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
		checkAdd(pccTxt, "ANY", "DEITY:", CoreUtility.join(deityList,
			Constants.PIPE));
		if (attackCycleMap != null) {
			checkAdd(pccTxt, "", "ATTACKCYCLE", CoreUtility.join(new MapCollection(
					attackCycleMap), Constants.PIPE));
		}
		
		if (prohibitedSchools != null) {
			pccTxt.append('\t').append("PROHIBITED:");
			pccTxt.append(CoreUtility.join(prohibitedSchools, ","));
		}
		
		if (castInfo != null) {
			checkAdd(pccTxt, Constants.s_NONE, "SPELLSTAT:", castInfo.getSpellBaseStatAbbr());
			checkAdd(pccTxt, Constants.s_NONE, "SPELLTYPE:", castInfo.getSpellType());
			if (castInfo.usesSpellBook()) {
				pccTxt.append("\tSPELLBOOK:Y");
			}
		}

		// if (skillPoints != 0)
		// {
		// pccTxt.append("\tSTARTSKILLPTS:").append(skillPoints);
		// }
		if (skillPointFormula.length() != 0) {
			pccTxt.append("\tSTARTSKILLPTS:").append(skillPointFormula);
		}

		if (!getVisibility().equals(Visibility.DEFAULT)) {
			pccTxt.append("\tVISIBLE:" + getVisibility().toString());
		}

		if (initialFeats != 0) {
			pccTxt.append("\tXTRAFEATS:").append(initialFeats);
		}

		if (levelsPerFeat != null) {
			pccTxt.append("\tLEVELSPERFEAT:").append(levelsPerFeat.intValue());
		}

		if (maxLevel != 0) {
			pccTxt.append("\tMAXLEVEL:").append(maxLevel);
		}

		if (castInfo != null) {
			pccTxt.append("\tMEMORIZE:" + (castInfo.memorizesSpells() ? "Y" : "N"));
		}

		if (multiPreReqs) {
			pccTxt.append("\tMULTIPREREQS:Y");
		}

		if (!getKnownSpellsList().isEmpty()) {
			pccTxt.append("\tKNOWNSPELLS:");
			pccTxt.append(CoreUtility.join(knownSpellsList, Constants.PIPE));
		}

		if (itemCreationMultiplier.length() != 0) {
			pccTxt.append("\tITEMCREATE:").append(itemCreationMultiplier);
		}

		if (classSpellChoices != null) {
			checkAdd(pccTxt, "", "SPELLLIST:", classSpellChoices.toString());
		}

		if (classSkillChoices != null) {
			checkAdd(pccTxt, "", "SKILLLIST:", classSkillChoices.toString());
		}

		if (getWeaponProfBonus().size() != 0) {
			pccTxt.append("\tWEAPONBONUS:");
			pccTxt.append(CoreUtility.join(getWeaponProfBonus(), Constants.PIPE));
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

		if (castInfo != null && castInfo.hasSpecialtyKnownProgression()) {
			pccTxt.append(castInfo.getSpecialtyKnownPCC(lineSep));
		}
		
		if (castInfo != null && castInfo.hasCastProgression()) {
			pccTxt.append(castInfo.getCastPCC(lineSep));
		}
		
		if (castInfo != null && castInfo.hasKnownProgression()) {
			pccTxt.append(castInfo.getKnownPCC(lineSep));
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
			for (LevelProperty<String> lp : SR) {
				pccTxt.append(lineSep).append(lp.getLevel()).append("\tSR:")
						.append(lp.getObject());
			}
		}

		// Output the list of spells associated with the class.
		int cap = getSpellSupport().getMaxSpellListLevel();
		if (hasMaxLevel() && cap > maxLevel)
		{
			cap = maxLevel;
		}
		for (int i = 0; i <= cap; i++) {
			final List<PCSpell> spellList = getSpellSupport()
					.getSpellListForLevel(i);

			if (spellList != null) {
				for (PCSpell spell : spellList) {
					pccTxt.append(lineSep).append(i).append("\tSPELLS:")
							.append(spell.getPCCText());
				}
			}

		}

		if (templates != null) {
			for (final LevelProperty<String> lp : templates) {
				pccTxt.append(lineSep).append(lp.getLevel());
				pccTxt.append("\tTEMPLATE:").append(lp.getObject());
			}
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
		
		List<SpecialAbility> saList = new ArrayList<SpecialAbility>();
		addSABToList(saList, null);
		for (SpecialAbility sa : saList)
		{
			final String src = sa.getSASource();
			final String lev = src.substring(src.lastIndexOf('|') + 1);
			pccTxt.append(lineSep).append(lev).append("\tSAB:").append(
					sa.toString());
		}

		if (addDomains != null)
		{
			for (LevelProperty<Domain> domainLP : addDomains)
			{
				pccTxt.append(lineSep).append(domainLP.getLevel());
				pccTxt.append("\tADDDOMAINS:").append(
						domainLP.getObject().getKeyName());
			}
		}

		if (domainList != null)
		{
			for (LevelProperty<Domain> domainLP : domainList)
			{
				pccTxt.append(lineSep).append(domainLP.getLevel());
				pccTxt.append("\tDOMAIN:").append(
						domainLP.getObject().getKeyName());
			}
		}

		if (featList != null)
		{
			for (LevelProperty<String> lp : featList)
			{
				pccTxt.append(lineSep).append(lp.getLevel());
				pccTxt.append("\tFEATAUTO:").append(lp.getObject());
			}
		}

		// TODO - Add ABILITY tokens.
		if (featAutos != null)
		{
			for (LevelProperty<String> autoFeat : featAutos)
			{
				pccTxt.append(lineSep).append(autoFeat.getLevel());
				pccTxt.append("\tFEATAUTO:").append(autoFeat.getObject());
			}
		}

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
		if (umultList != null) {
			for (String st : umultList) {
				final int sepPos = st.indexOf("|");
				pccTxt.append(lineSep).append(st.substring(0, sepPos))
						.append("\tUMULT:").append(st.substring(sepPos + 1));
			}
		}

		return pccTxt.toString();
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<Ability> getVirtualFeatList(final int aLevel) {
		final List<Ability> aList = new ArrayList<Ability>();

		if (vFeatList != null) {
			for (LevelProperty<List<Ability>> lp : vFeatList) {
				if (lp.getLevel() <= aLevel) {
					aList.addAll(lp.getObject());
				}
			}
		}

		return aList;
	}

	/**
	 * Here is where we do the real work of setting the vision information on
	 * the PObject
	 * 
	 * Must Override to fix 1489300
	 * 
	 * @param aPC
	 * @return Map
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory) [with level dependent
	 * differences, of course]
	 */
	/*
	 * UNWIND I'm not going to unwind this one early, because PC.getVisionList 
	 * actually depends on this for a bug fix - this will 'naturally' be fixed 
	 * when PCClassLevel is broken out because it is used in few places.
	 */
	@Override
	public List<Vision> getVision() {
		List<Vision> returnList = super.getVision();
		if (visionList != null) {
			if (returnList == null) {
				returnList = new ArrayList<Vision>();
			}
			for (LevelProperty<Vision> vis : visionList) {
				if (vis.getLevel() <= level) {
					returnList.add(vis.getObject());
				}
			}
		}

		return returnList;
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSkill(final String tok) {
		if (classSkillList == null) {
			classSkillList = new ArrayList<String>();
		}
		classSkillList.add(tok);
	}
	
	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void clearClassSkillList() {
		classSkillList = null;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSpellList(final String tok) {
		if (classSpellList == null) {
			classSpellList = new ArrayList<String>();
		}
		classSpellList.add(tok);
		/*
		 * CONSIDER I have taken out classSpellString = null; which is now the
		 * equivalent of classSpellChoices = null; ... I don't understand why in
		 * this unique situation of Player Character Import that resetting this
		 * produces better behavior than adding a class (which doesn't delete
		 * the list). Seems to me a case of unnecessary (and confusing)
		 * deletion... - thpr 10/29/06
		 */
		/*
		 * CONSIDER This is a confusing side-effect - this really needs to be
		 * identified better as a cached variable (transient) so that it is
		 * recognized that this is simply making a 'cache' dirty - thpr 11/4/06
		 */
		stableSpellKey = null;
	}
	
	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void clearClassSpellList() {
		classSpellList = null;
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addDomain(int domainLevel, final Domain domain) {
		if (domainList == null) {
			domainList = new ArrayList<LevelProperty<Domain>>();
		}
		domainList.add(LevelProperty.getLevelProperty(domainLevel, domain));
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final void addSubClass(final SubClass sClass) {
		if (subClassList == null) {
			subClassList = new ArrayList<SubClass>();
		}

		sClass.setHitPointMap(hitPointMap);
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

		sClass.setHitPointMap(hitPointMap);
		sClass.setHitDie(hitDie);
		substitutionClassList.add(sClass);
	}

	/*
	 * FINALPCCLASSANDLEVEL This needs to be in both PCClass (since it's imported from
	 * a Tag) and PCClassLevel (although the PCClassLevel version should not be 
	 * level dependent)
	 */
	public void addFeatList(final int aLevel, final String aFeatList) {
		// TODO - Make this not string based.
		if (featList == null) {
			featList = new ArrayList<LevelProperty<String>>();
		}
		featList.add(LevelProperty.getLevelProperty(aLevel, aFeatList));
	}

	private SpellProgressionInfo getConstructingSpellProgressionInfo() {
		if (castInfo == null) {
			castInfo = new SpellProgressionInfo();
		}
		return castInfo;
	}
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void setKnown(final int iLevel, final List<String> aList) {
		getConstructingSpellProgressionInfo().setKnown(iLevel, aList);
	}

	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addKnownSpell(final SpellFilter aFilter) {
		if (knownSpellsList == null) {
			knownSpellsList = new ArrayList<SpellFilter>();
		}
		knownSpellsList.add(aFilter);
	}
	
	/*
	 * FINALPCCLASSONLY - for class construction
	 */
	public void clearKnownSpellsList() {
		knownSpellsList = null;
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
	public void addTemplate(int lvl, final String template) {
		if (templates == null) {
			templates = new ArrayList<LevelProperty<String>>();
		}
		templates.add(LevelProperty.getLevelProperty(lvl, template));
	}


	/**
	 * Adds virtual feats to the vFeatList
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
		if (vFeatList == null) {
			vFeatList = new ArrayList<LevelProperty<List<Ability>>>();
		}
		boolean found = false;
		for (LevelProperty<List<Ability>> lp : vFeatList) {
			if (lp.getLevel() == aLevel) {
				found = true;
				lp.getObject().addAll(vList);
			}
		}
		if (!found) {
			List<Ability> arrayList = new ArrayList<Ability>(vList);
			vFeatList.add(LevelProperty.getLevelProperty(aLevel, arrayList));
		}

		super.addVirtualFeats(vList);
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
	public int attackCycle(final AttackType at) {
		if (attackCycleMap != null) {
			final String aString = attackCycleMap.get(at);

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
		final int i = (int) getBonusTo("COMBAT", "BAB", level, aPC);

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
			for (String type : getTypeList(false)) {
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

	@Override
	public PCClass clone() {
		PCClass aClass = null;

		try {
			aClass = (PCClass) super.clone();
			aClass.setSubClassKey(getSubClassKey());

			// aClass.setSubClassString(getSubClassString());
			if (prohibitedSchools != null) {
				aClass.prohibitedSchools = new ArrayList<String>(prohibitedSchools);
			}
			// aClass.setSkillPoints(skillPoints);
			// aClass.setAttackBonusType(attackBonusType);
			if (castInfo != null) {
				aClass.castInfo = castInfo.clone();
			}
			// TODO - This should be removed
			aClass.uattList = new ArrayList<String>(uattList);
			// aClass.acList = new ArrayList<String>(acList);
			if (featList != null) {
				aClass.featList = new ArrayList<LevelProperty<String>>(featList);
			}
			// aClass.vFeatList = (ArrayList) vFeatList.clone();
			if (vFeatList != null) {
				//I guess a shallow clone is OK???? already was that way ... - thpr 11/2/06
				aClass.vFeatList = new ArrayList<LevelProperty<List<Ability>>>(vFeatList);
			}
			if ( vAbilityMap != null )
			{
				aClass.vAbilityMap = new DoubleKeyMap<AbilityCategory, Integer, List<Ability>>(vAbilityMap);
			}
			if (hitDieLockList != null) {
				aClass.hitDieLockList = new ArrayList<LevelProperty<String>>(
						hitDieLockList);
			}
			if (featAutos != null) {
				aClass.featAutos = new ArrayList<LevelProperty<String>>(featAutos);
			}
//			if ( theAutoAbilities != null )
//			{
//				aClass.theAutoAbilities = new DoubleKeyMap<AbilityCategory, Integer, List<String>>(theAutoAbilities);
//			}
			// TODO - Why is this not copying the skillList from the master?
			aClass.skillList = null;

			aClass.classSkillList = null;
			aClass.classSpellList = null;
			aClass.stableSpellKey = null;

			aClass.setLevelExchange(levelExchange);

			aClass.multiPreReqs = multiPreReqs;
			aClass.deityList = new ArrayList<String>(deityList);
			if (knownSpellsList != null) {
				aClass.knownSpellsList = new ArrayList<SpellFilter>(knownSpellsList);
			}
			if (attackCycleMap != null) {
				aClass.attackCycleMap = new HashMap<AttackType, String>(
						attackCycleMap);
			}
			aClass.modToSkills = modToSkills;
			aClass.initMod = initMod;
			if (specialtyList != null) {
				aClass.specialtyList = new ArrayList<String>(specialtyList);
			}

			// aClass.ageSet = ageSet;
			if (domainList != null) {
				//This is ok as a shallow copy - contract on readers of domainList
				aClass.domainList = new ArrayList<LevelProperty<Domain>>(domainList);
			}
			if (addDomains != null) {
				//This is ok as a shallow copy - contract on readers of domainList
				aClass.addDomains = new ArrayList<LevelProperty<Domain>>(addDomains);
			}
			if (hitPointMap != null) {
				aClass.hitPointMap = new HashMap<Integer, Integer>(hitPointMap);
			}
			aClass.substitutionClassList = substitutionClassList; 

			if (naturalWeapons != null) {
				aClass.naturalWeapons = new ArrayList<LevelProperty<Equipment>>(
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
		return castInfo != null && castInfo.containsSpellFormula();
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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass, since it is a Tag
	 * 
	 * Need to look into the details of stableSpellKey to figure out the appropriate
	 * place for that
	 */
	@Override
	public void setName(final String newName) {
		super.setName(newName);

		int i = 3;

		if ("".equals(abbrev)) {
			if (newName.length() < 3) {
				i = newName.length();
			}

			abbrev = newName.substring(0, i);
		}

		/*
		 * CONSIDER This (below) is a bit of a confusing side effect - needs to be
		 * far more explicit that stableSpellKey is a cache, and that there are 
		 * also side effects to getSpellKey (side effects are bad :( ) - 
		 * thpr 11/4/06
		 */
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
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public List<SubClass> getSubClassList() {
		return subClassList;
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public List getSubstitutionClassList() {
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public boolean hasClassSkill(final String aString) {
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
	 * to evaluate whether a better test exists, however, since it seems REALLY 
	 * slow for something that should be a quick test!)
	 */
	public boolean hasKnownSpells(final PlayerCharacter aPC) {
		for (int i = 0; i <= getHighestLevelSpell(); i++) {
			if (getKnownForLevel(i, aPC) > 0) {
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
			for (String type : getTypeList(false)) {
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
		if (castInfo == null || !castInfo.hasCastProgression()) {
			return true;
		}
		/*
		 * CONSIDER This is just blatently wrong because it is not considering
		 * formulas, and not considering bonuses... - thpr 11/8/06
		 *
		 * May not be a big issue other than a poorly named method, but
		 * need to check what is really required here
		 */
		for (List<String> l : castInfo.getCastProgression().values()) {
			for (String st : l) {
				try {
					if (Integer.parseInt(st) > 0) {
						return false;
					}
				} catch (NumberFormatException nfe) {
					// ignore
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
	public List<SpecialAbility> addSpecialAbilitiesToList(
			final List<SpecialAbility> aList, final PlayerCharacter aPC) {
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList == null) || specialAbilityList.isEmpty()) {
			return aList;
		}

		final List<SpecialAbility> bList = new ArrayList<SpecialAbility>();

		for (SpecialAbility sa : specialAbilityList) {
			if (sa.pcQualifiesFor(aPC)) {
				final String saKey = sa.getKeyName();
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
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
	int getKnownForLevel(final int spellLevel,
			final String bookName, final PlayerCharacter aPC) {
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		int pcLevel = getLevel();
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE."
				+ getSpellType());

		/*
		 * CONSIDER Why is known testing getNumFromCastList??? - thpr 11/8/06
		 */
		if ((castInfo != null) && castInfo.hasCastProgression()
				&& (getNumFromCastList(pcLevel, spellLevel, aPC) < 0)) {
			// Don't know any spells of this level
			// however, character might have a bonus spells e.g. from certain
			// feats
			return (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
					+ levelSpellLevel);
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
		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size())) {
			final PCStat aStat = aPC.getStatList().getStatAt(index);
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

		if (castInfo == null) {
			return total;
		}

		boolean psiSpecialty = false;

		if (castInfo.hasKnownProgression()) {
			List<String> knownList = castInfo.getKnownForLevel(pcLevel);
			if (spellLevel >= 0 && spellLevel < knownList.size())
			{
				String spells = knownList.get(spellLevel);
			
				if (spells.endsWith("+d")) {
					psiSpecialty = true;
					spells = spells.substring(0, spells.length() - 2);
				}
	
				int t;
				if (castInfo.containsSpellFormula()) {
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
					total += castInfo.getKnownSpellsFromSpecialty();
				}
			}
		}

		// if we have known spells (0==no known spells recorded)
		// or a psi specialty.
		if (((total > 0) && (spellLevel > 0)) && !psiSpecialty) {
			// make sure any slots due from specialties
			// (including domains) are added
			total += castInfo.getKnownSpellsFromSpecialty();
		}

		return total;
	}

	/*
	 * REFACTOR Exactly where does this end up? I think that passing a
	 * PlayerCharacter into an object like PCClass is generally (but certainly
	 * not always) bad form. In this case, the PC is present in order to test
	 * prerequisites, so perhaps this is an OK use of passing in
	 * PlayerCharacter... (double dispatch)
	 */
	public boolean isProhibited(final Spell aSpell, final PlayerCharacter aPC) {
		if (!PrereqHandler.passesAll(aSpell.getPreReqList(), aPC, aSpell)) {
			return true;
		}

		if (prohibitSpellDescriptorList != null) {
			for (SpellProhibitor prohibit : prohibitSpellDescriptorList) {
				if (prohibit.isProhibited(aSpell, aPC)) {
					return true;
				}
			}
		}

		if (prohibitedSchools != null) {
			for (String school : prohibitedSchools) {
				if (aSpell.getSchools().contains(school)
						|| aSpell.getSubschools().contains(school)) {
					return true;
				}
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
					Math.max(iLevel - 1, 0)), ",", false);

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
	 * Adds a level of this class to the character.
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
	 * @param ignorePrereqs
	 *            True if prereqs for the level should be ignored. Used in 
	 *            situations such as when the character is being loaded.
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
			final boolean ignorePrereqs) {

		// Check to see if we can add a level of this class to the
		// current character
		final int newLevel = level + 1;
		boolean levelMax = argLevelMax;

		level += 1;
		if (!ignorePrereqs) {
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

		if (hasMaxLevel() && (newLevel > maxLevel) && levelMax) {
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
			rollHP(aPC, level, (SettingsHandler.isHPMaxAtFirstClassLevel() ? aPC.totalNonMonsterLevels() : aPC.getTotalLevels()) == 1);
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
			if (naturalWeapons != null) {
				List<Equipment> natWeap = new ArrayList<Equipment>();
				for (LevelProperty<Equipment> lp : naturalWeapons) {
					if (lp.getLevel() <= newLevel) {
						natWeap.add(lp.getObject());
					}
				}
				aPC.addNaturalWeapons(natWeap);
			}
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
						iMaxDonation = Math.min(iMaxDonation, iLevel - iLowest);
						if (hasMaxLevel())
						{
							iMaxDonation =
									Math.min(iMaxDonation, getMaxLevel() - 1);
						}

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
	 * REFACTOR Great theory, wrong universe.  Well, mayne not, but the name implies 
	 * events which aren't occurring here.  Need to at least rename this...
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
					removeSpecialAbility(sa);
					sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(),
							sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					addSpecialAbilityToList(sa);
				}
			}
		}
		
		for (int lev : mapChar.getSecondaryKeySet(MapKey.SAB))
		{
			for (SpecialAbility sa : mapChar.getListFor(MapKey.SAB, lev))
			{
				if (sa.getSASource().length() != 0)
				{
					mapChar.removeFromListFor(MapKey.SAB, lev, sa);
					sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(),
							sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					addSAB(sa, lev);
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
			for (Equipment eq : this.getNaturalWeapons(newLevel + 1)) {
				/*
				 * This is a COMPLETE hack to emulate PC.removeNaturalWeapons 
				 * without going through the convoluted "I am level dependent,
				 * you're not" game that is required in today's PCClass is
				 * level-aware world.  This becomes a lot easier in a 
				 * PCClass/PCClassLevel world, and the system can go back to
				 * using PC.removeNaturalWeapons
				 */
				aPC.removeEquipment(eq);
				aPC.delEquipSetItem(eq);
				aPC.setDirty(true);
			}
		} else {
			Logging
					.errorPrint("No current pc in subLevel()? How did this happen?");

			return;
		}
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
		if (knownSpellsList == null || knownSpellsList.size() == 0) {
			return false;
		}

		final Spell aSpell = Globals.getSpellKeyed(aSpellKey);

		if (useMap) {
			final Integer val = castForLevelMap.get(spellLevel);

			if ((val == null) || val == 0 || (aSpell == null)) {
				return false;
			}
		} else if ((getCastForLevel(spellLevel, aPC) == 0)
				|| (aSpell == null)) {
			return false;
		}

		if (isProhibited(aSpell, aPC) && !isSpecialtySpell(aSpell)) {
			return false;
		}

		// iterate through the KNOWNSPELLS: tag
		for (SpellFilter filter : knownSpellsList) {
			if (filter.matchesFilter(aSpellKey, spellLevel)) {
				return true;
			}
		}
		return false;
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

			spMod *= aPC.getRace().getInitialSkillMultiplier();
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
			final int s = getCastForLevel(i, aPC);
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
	 * happening, it doesn't add anything to the class.
	 * </p>
	 * 
	 * @param flag
	 *            If false, function returns empty <code>ArrayList</code> (?)
	 * @param aPC
	 * @return A list of templates added by this function
	 */
	/*
	 * DELETEMETHOD if not PCCLASSONLY.  This is required in PCClass since
	 * it is really done during addLevel.
	 */
	public List<String> getTemplates(final boolean flag,
			final PlayerCharacter aPC) {
		final ArrayList<String> newTemplates = new ArrayList<String>();

		if (flag) {
			return newTemplates;
		}
		
		for (final LevelProperty<String> template : getTemplates()) {
			if (level < template.getLevel()) {
				continue;
			}

			/*
			 * The template string will either be a CHOOSE: tag or a bar
			 * separated list of templates
			 */
			final String tString = template.getObject();

			if (tString.startsWith("CHOOSE:")) {
				newTemplates.add(PCTemplate.chooseTemplate(this, tString
						.substring(7), true, aPC));
			} else {
				for (String templ : tString.split("\\|")) {
					newTemplates.add(templ);
				}
			}
		}

		return newTemplates;
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

		for (LevelProperty<String> lp : featList) {
			if (lp.getLevel() == aLevel)
			{
				final double preFeatCount = aPC.getUsedFeatCount();
				AbilityUtilities.modFeatsFromList(aPC, pcLevelInfo, lp
						.getObject(), addThem, aLevel == 1);
				
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
		
		for (SubClass sc : subClassList) {
			/*
			 * BUG MULTIPREREQS would fail here on a SubClass :( - thpr 11/4/06
			 * 
			 * STOP THE MAGIC, I want to delete MULTIPREREQs
			 */
			if (!PrereqHandler.passesAll(sc.getPreReqList(), aPC, this)) {
				continue;
			}

			final List<Object> columnList = new ArrayList<Object>(3);

			columnList.add(sc);
			columnList.add(Integer.toString(sc.getCost()));
			columnList.add(sc.getSupplementalDisplayInfo());
			
			if (!getSubClassKey().equals(Constants.s_NONE)) {
				// We already have a subclass requested.
				// If it is legal we will return that.
				choiceList.clear();
				choiceList.add(columnList);
				break;
			}

			choiceList.add(columnList);
		}
		
		/*
		 * REFACTOR This makes an assumption that SubClasses are ONLY Schools, which may
		 * not be a fabulous assumption
		 */
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("School Choice (Specialisation)");
		c.setMessageText("Make a selection.  The cost column indicates the cost of that selection. "
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
			clearProhibitedSchools();
			/*
			 * CONSIDER What happens to this reset during PCClass/PCClassLevel split
			 */
			specialtyList = null;

			SubClass sc = selectedList.get(0).get(0);
			choiceList = new ArrayList<List>();
			
			for (SubClass sub : subClassList) {
				if (sub.equals(sc)) {
					//Skip the selected specialist school
					continue;
				}
				/*
				 * BUG MULTIPREREQS would fail here on a SubClass :( - thpr 11/4/06
				 * 
				 * STOP THE MAGIC, I want to delete MULTIPREREQs
				 */
				if (!PrereqHandler.passesAll(sub.getPreReqList(), aPC, this)) {
					continue;
				}

				final List<Object> columnList = new ArrayList<Object>(3);

				int displayedCost = sub.getProhibitCost();
				if (displayedCost == 0) {
					continue;
				}

				columnList.add(sub);
				columnList.add(Integer.toString(displayedCost));
				columnList.add(sub.getSupplementalDisplayInfo());

				choiceList.add(columnList);
			}

			setSubClassKey(sc.getKeyName());

			if (sc.getChoice().length() > 0) {
				addSpecialty(sc.getChoice());
			}

			if (sc.getCost() != 0) {
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1.setMessageText("Make a selection.  You must make as many selections "
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
					addProhibitedSchool(sc.getChoice());
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
		if (classSkillChoices == null) {
			return;
		}

		clearClassSkillList();
		
		List<String> classSkillChoiceList = classSkillChoices.getList();
		if (classSkillChoiceList.size() == 1) {
			addClassSkill(classSkillChoiceList.get(0));
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose class-skills this class will inherit");
		c.setPool(classSkillChoices.getCount());
		c.setPoolFlag(false);
		c.setAvailableList(classSkillChoiceList);
		c.setVisible(true);

		final List<String> selectedList = c.getSelectedList();
		for (String sel : selectedList) {
			addClassSkill(sel);
		}
	}

	/*
	 * PCCLASSONLY Since this is part of the construction of a PCClassLevel,
	 * this is only part of PCClass...
	 */
	private void chooseClassSpellList() {
		// if no entry or no choices, just return
		if (classSpellChoices == null || (level < 1)) {
			return;
		}
		
		clearClassSpellList();

		List<String> classSpellChoiceList = classSpellChoices.getList();
		if (classSpellChoiceList.size() == 1) {
			addClassSpellList(classSpellChoiceList.get(0));
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose list of spells this class will use");
		c.setPool(classSpellChoices.getCount());
		c.setPoolFlag(false);
		c.setAvailableList(classSpellChoiceList);
		c.setVisible(true);

		List<String> selectedList = c.getSelectedList();
		for (String st : selectedList) {
			addClassSpellList(st);
		}
	}

	/*
	 * REFACTOR Some derivative of this method will be in PCClass only as part
	 * of the factory creation of a PCClassLevel... or perhaps in PCClassLevel
	 * so it can steal some information from other PCClassLevels of that
	 * PCClass. Either way, this will be far from its current form in the final
	 * solution.
	 */
	/*
	 * CONSIDER Why does this not inherit classSkillChoices?
	 */
	private void inheritAttributesFrom(final PCClass otherClass) {
		if (otherClass.getBonusSpellBaseStat() != null) {
			setBonusSpellBaseStat(otherClass.getBonusSpellBaseStat());
		}

		if (otherClass.getSpellBaseStat() != null) {
			setSpellBaseStat(otherClass.getSpellBaseStat());
		}

		classSpellChoices = otherClass.classSpellChoices;

		Set<String> s = otherClass.getAutoMapKeys();
		if (s != null)
		{
			for (String key : s)
			{
				for (String value : otherClass.getAuto(key))
				{
					addAutoArray(key, value);
				}
			}
		}

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

		for (SpecialAbility sa : otherClass.getSafeListFor(ListKey.SPECIAL_ABILITY))
		{
			addSpecialAbilityToList(sa);
		}

		for (int lev : otherClass.mapChar.getSecondaryKeySet(MapKey.SAB))
		{
			for (SpecialAbility sa : otherClass.mapChar.getListFor(MapKey.SAB, lev))
			{
				addSAB(sa, lev);
			}
		}

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
			SR = new ArrayList<LevelProperty<String>>(otherClass.SR);
		}

		if (otherClass.vision != null) {
			vision = otherClass.vision;
		}

		if (otherClass instanceof SubClass) {
			((SubClass) otherClass).applyLevelArrayModsTo(this);
		}

		if (otherClass.naturalWeapons != null) {
			naturalWeapons = new ArrayList<LevelProperty<Equipment>>(otherClass.naturalWeapons);
		}
		
		hitDie = otherClass.hitDie;
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
		List<Domain> domList = new ArrayList<Domain>();
		for (int lvl = 0 ; lvl <= aLevel ; lvl++) {
			for (Domain dom : getDomainList(lvl)) {
				domList.add(dom);
			}
		}
		for (final Domain dom : domList) {
			if (dom.qualifies(aPC))
			{
				String domKey = dom.getKeyName();
				if (adding)
				{
					if (!aPC.containsCharacterDomain(this.getKeyName(), domKey))
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
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.PObject#addNaturalWeapon(pcgen.core.Equipment, int)
	 */
	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (of course, a level independent version for PCClassLevel)
	 */
	public void addNaturalWeapon(final Equipment weapon, final int aLevel) {
		if (naturalWeapons == null) {
			naturalWeapons = new ArrayList<LevelProperty<Equipment>>();
		}
		naturalWeapons.add(LevelProperty.getLevelProperty(aLevel, weapon));
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
	 * Parse the ATTACKCYCLE: string and build HashMap Only allowed values in
	 * attackCycle are: BAB, RAB or UAB
	 * 
	 * @param aString
	 *            Unparsed ATTACKCYCLE string.
	 */
	/*
	 * PCCLASSANDLEVEL since this is from a TAG and also is required in 
	 * the PCClassLevel
	 */
	public final void setAttackCycle(AttackType at, String aString) {
		if (attackCycleMap == null) {
			attackCycleMap = new HashMap<AttackType, String>();
		}
		attackCycleMap.put(at, aString);
	}
	
	/*
	 * PCCLASSONLY Only for editing classes
	 */
	public final Map<AttackType, String> getAttackCycle() {
		if (attackCycleMap == null) {
			return null;
		}
		return Collections.unmodifiableMap(attackCycleMap);
	}

	/**
	 * Remove all auto feats gained via a level
	 * @param aLevel
	 */
	/*
	 * FINALPCCLASSONLY I think (heh) that committing this to the PCClassLevel really should
	 * be part of the PCClass Factory, and thus part of the creation of the PCClassLevel
	 * and thus only in PCClass
	 */
	public void removeAllAutoFeats(final int aLevel)
	{
		if (featAutos != null)
		{
			for (Iterator<LevelProperty<String>> it = featAutos.iterator(); it
					.hasNext();)
			{
				LevelProperty<String> autoFeat = it.next();
				if (autoFeat.getLevel() == aLevel)
				{
					it.remove();
				}
			}
		}
	}

	public int getMinLevelForSpellLevel(int spellLevel, boolean allowBonus) {
		if (castInfo == null) {
			return -1;
		}
		return castInfo.getMinLevelForSpellLevel(spellLevel, allowBonus);
	}

	public int getMaxSpellLevelForClassLevel(int classLevel) {
		if (castInfo == null) {
			return -1;
		}
		return castInfo.getMaxSpellLevelForClassLevel(classLevel);
	}
	
	public void setKnownSpellsFromSpecialty(int i) {
		if (castInfo == null && i == 0) {
			//Avoid useless construction of castInfo
			return;
		}
		getConstructingSpellProgressionInfo().setKnownSpellsFromSpecialty(i);
	}
	
	public int getKnownSpellsFromSpecialty() {
		return castInfo == null ? 0 : castInfo.getKnownSpellsFromSpecialty();
	}

	@Override
	public Load getEncumberedArmorMove() {
		LevelProperty<Load> activeLP = null;

		if (encumberedArmorMove != null) {
			for (LevelProperty<Load> lp : encumberedArmorMove) {
				if (lp.getLevel() > level) {
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel()) {
					activeLP = lp;
					continue;
				}
			}
		}

		return activeLP == null ? super.getEncumberedArmorMove() : activeLP
				.getObject();
	}

	@Override
	public Load getEncumberedLoadMove() {
		LevelProperty<Load> activeLP = null;

		if (encumberedLoadMove != null) {
			for (LevelProperty<Load> lp : encumberedLoadMove) {
				if (lp.getLevel() > level) {
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel()) {
					activeLP = lp;
					continue;
				}
			}
		}

		return activeLP == null ? super.getEncumberedLoadMove() : activeLP
				.getObject();
	}

	@Override
	public void setEncumberedArmorMove(Load load, int lvl) {
		if (encumberedArmorMove == null) {
			encumberedArmorMove = new ArrayList<LevelProperty<Load>>();
		}
		encumberedArmorMove.add(LevelProperty.getLevelProperty(lvl, load));
	}

	@Override
	public void setEncumberedLoadMove(Load load, int lvl) {
		if (encumberedLoadMove == null) {
			encumberedLoadMove = new ArrayList<LevelProperty<Load>>();
		}
		encumberedLoadMove.add(LevelProperty.getLevelProperty(lvl, load));
	}

	@Override
	public void setMovement(Movement m, int level)
	{
		if (movementList == null) {
			movementList = new ArrayList<LevelProperty<Movement>>();
		}
		movementList.add(LevelProperty.getLevelProperty(level, m));
		
	}
	
	@Override
	public List<Movement> getMovements()
	{
		if (movementList == null) {
			return Collections.emptyList();
		}
		List<Movement> returnList = new ArrayList<Movement>();
		for (LevelProperty<Movement> prop : movementList)
		{
			if (prop.getLevel() <= level)
			{
				returnList.add(prop.getObject());
			}
		}
		return returnList;
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
