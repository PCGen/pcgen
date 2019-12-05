/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.format.StringManager;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.BonusSpellInfo;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.Qualifier;
import pcgen.core.analysis.PCClassKeyChange;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.parser.PreVariableParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;


public class PCClassTest extends AbstractCharacterTestCase
{
    private PCClass humanoidClass;
    private Race bugbearRace;
    private Race bigBugbearRace;
    private PCClass nymphClass;
    private Race nymphRace;
    private PCClass prClass;
    private PCClass qClass;
    private PCClass nqClass;

    /**
     * Test name change
     */
    @Test
    void testFireNameChangedVariable()
    {
        finishLoad();
        final PCClass myClass = new PCClass();
        myClass.setName("myClass");
        myClass.put(StringKey.KEY_NAME, "KEY_myClass");

        PCClassLevel cl2 = myClass.getOriginalClassLevel(2);
        cl2.put(VariableKey.getConstant("someVar"), FormulaFactory
                .getFormulaFor("(CL=KEY_myClass/2) + CL=KEY_myClass"));

        assertEquals(1, cl2.getVariableKeys().size());
        assertEquals("someVar", cl2.getVariableKeys().iterator().next()
                .toString());
        assertNotNull(cl2.get(VariableKey.getConstant("someVar")));
        assertEquals("(CL=KEY_myClass/2) + CL=KEY_myClass", cl2.get(
                VariableKey.getConstant("someVar")).toString());

        myClass.setName("someOtherClass");

        PCClassKeyChange.changeReferences("myClass", myClass);

        assertEquals(1, cl2.getVariableKeys().size());
        assertEquals("someVar", cl2.getVariableKeys().iterator().next()
                .toString());
        assertEquals("(CL=KEY_myClass/2) + CL=KEY_myClass", cl2.get(
                VariableKey.getConstant("someVar")).toString());

        PCClassKeyChange.changeReferences("KEY_myClass", myClass);

        assertEquals(1, cl2.getVariableKeys().size());
        assertEquals("someVar", cl2.getVariableKeys().iterator().next()
                .toString());
        assertEquals("(CL=someOtherClass/2) + CL=someOtherClass", cl2.get(
                VariableKey.getConstant("someVar")).toString());
    }

    /**
     * Test monster classes generating the correct number of skill points.
     */
    @Test
    void testMonsterSkillPoints()
    {
        finishLoad();
        // Create a medium bugbear first level
        PlayerCharacter bugbear = new PlayerCharacter();
        bugbear.setRace(bugbearRace);
        setPCStat(bugbear, intel, 12);

        // Test skills granted for each level
        bugbear.incrementClassLevel(1, humanoidClass);
        PCLevelInfo levelInfo = bugbear.getLevelInfo(0);
        assertEquals(7, levelInfo
                .getSkillPointsGained(bugbear), "First level of bugbear");

        bugbear.incrementClassLevel(1, humanoidClass);
        levelInfo = bugbear.getLevelInfo(1);
        assertEquals(1, levelInfo
                .getSkillPointsGained(bugbear), "2nd level of bugbear");

        bugbear.incrementClassLevel(1, humanoidClass);
        levelInfo = bugbear.getLevelInfo(2);
        assertEquals(1, levelInfo
                .getSkillPointsGained(bugbear), "3rd level of bugbear");

        // Craete a huge bugbear first level
        bugbear = new PlayerCharacter();
        bugbear.setRace(bigBugbearRace);
        assertEquals("L", bugbear.getSizeAdjustment().getKeyName(), "big bugbear");
        setPCStat(bugbear, intel, 10);
        bugbear.incrementClassLevel(1, humanoidClass);
        // Test skills granted for each level
        levelInfo = bugbear.getLevelInfo(0);
        assertEquals(6, levelInfo
                .getSkillPointsGained(bugbear), "First level of big bugbear");

        bugbear.incrementClassLevel(1, humanoidClass);
        levelInfo = bugbear.getLevelInfo(1);
        assertEquals(0, levelInfo
                .getSkillPointsGained(bugbear), "2nd level of big bugbear");

        bugbear.incrementClassLevel(1, humanoidClass);
        levelInfo = bugbear.getLevelInfo(2);
        assertEquals(1, levelInfo
                .getSkillPointsGained(bugbear), "3rd level of big bugbear");

        // Create a nymph - first level
        PlayerCharacter nymph = new PlayerCharacter();
        nymph.setRace(nymphRace);
        assertEquals("M", nymph.getSizeAdjustment().getKeyName(), "nymph");
        setPCStat(nymph, intel, 10);
        nymph.incrementClassLevel(1, nymphClass);
        // Test skills granted for each level
        levelInfo = nymph.getLevelInfo(0);
        assertEquals(24, levelInfo
                .getSkillPointsGained(bugbear), "First level of nymph");

        nymph.incrementClassLevel(1, nymphClass);
        levelInfo = nymph.getLevelInfo(1);
        assertEquals(6, levelInfo.getSkillPointsGained(bugbear), "2nd level of nymph");

    }

    /**
     * Test the interaction of prerequisites on PCClasses and bonuses and the
     * Bypass Class Prereqs flag.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    void testBypassClassPrereqs() throws PersistenceLayerException
    {
        LoadContext context = Globals.getContext();

        // Setup class with prereqs and var based abilities with prereqs.
        final PreVariableParser parser = new PreVariableParser();
        final Prerequisite aPrereq =
                parser.parse("VARGTEQ", "Foo,1", false, false);
        final GameMode gameMode = SettingsHandler.getGame();
        RuleCheck aClassPreRule = gameMode.getModeContext().getReferenceContext()
                .silentlyGetConstructedCDOMObject(RuleCheck.class, "CLASSPRE");
        aClassPreRule.setDefault(false);

        final PCClass aPrClass = new PCClass();
        aPrClass.setName("PreReqClass");
        aPrClass.put(StringKey.KEY_NAME, "KEY_PreReqClass");
        final BonusObj aBonus = Bonus.newBonus(context, "MISC|SR|10|PREVARGTEQ:Foo,2");

        if (aBonus != null)
        {
            aPrClass.addToListFor(ListKey.BONUS, aBonus);
        }
        aPrClass.addPrerequisite(aPrereq);
        final PCClass aQClass = new PCClass();
        aQClass.setName("QualClass");
        aQClass.put(StringKey.KEY_NAME, "KEY_QualClass");
        CDOMDirectSingleRef<PCClass> ref = CDOMDirectSingleRef.getRef(aPrClass);
        aQClass.addToListFor(ListKey.QUALIFY, new Qualifier(ref));

        final PCClass aNqClass = new PCClass();
        aNqClass.setName("NonQualClass");
        aNqClass.put(StringKey.KEY_NAME, "KEY_NonQualClass");
        aNqClass.put(VariableKey.getConstant("Foo"), FormulaFactory.ONE);
        aNqClass.getOriginalClassLevel(2).put(VariableKey.getConstant("Foo"),
                FormulaFactory.getFormulaFor(2));

        finishLoad();

        // Setup character without prereqs
        final PlayerCharacter character = getCharacter();

        // Test no prereqs and no bypass fails class and var
        assertFalse(aPrClass
                .qualifies(character, aPrClass), "PC with no prereqs should fail class qual test.");
        assertEquals(0.0,
                aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1, "PC with no prereqs should fail var qual test."
        );

        // Test no prereqs and bypass passes class and fails var
        aClassPreRule.setDefault(true);
        assertTrue(
                aPrClass.qualifies(character, aPrClass),
                "PC with no prereqs should pass class qual test when bypassing prereqs is on.");
        assertEquals(
                0.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with no prereqs should fail var qual test when bypass prereqs is on.");

        // Test prereqs and bypass pass class and var
        character.incrementClassLevel(1, aNqClass);
        assertTrue(
                aPrClass.qualifies(character, aPrClass), "PC with prereqs and bypass should pass class qual test.");
        character.incrementClassLevel(1, aNqClass);
        assertEquals(
                10.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with prereqs and bypass should pass var qual test.");

        // Test prereqs and no bypass passes class and var
        aClassPreRule.setDefault(false);
        assertTrue(
                aPrClass.qualifies(character, aPrClass), "PC with prereqs and no bypass should pass class qual test.");
        assertEquals(
                10.0,
                aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with prereqs and no bypass should pass var qual test."
        );

    }

    /**
     * Test the interaction of prerequisites on PCClasses and bonuses and the
     * Bypass Class Prereqs flag.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    void testBypassClassPrereqsDeprecated() throws PersistenceLayerException
    {
        LoadContext context = Globals.getContext();

        // Setup class with prereqs and var based abilities with prereqs.
        final PreVariableParser parser = new PreVariableParser();
        final Prerequisite aPrereq =
                parser.parse("VARGTEQ", "Foo,1", false, false);
        final GameMode gameMode = SettingsHandler.getGame();
        RuleCheck aClassPreRule = gameMode.getModeContext().getReferenceContext()
                .silentlyGetConstructedCDOMObject(RuleCheck.class, "CLASSPRE");
        aClassPreRule.setDefault(false);

        final PCClass aPrClass = new PCClass();
        aPrClass.setName("PreReqClass");
        aPrClass.put(StringKey.KEY_NAME, "KEY_PreReqClass");
        final BonusObj aBonus = Bonus.newBonus(context, "MISC|SR|10|PREVARGTEQ:Foo,2");

        if (aBonus != null)
        {
            aPrClass.addToListFor(ListKey.BONUS, aBonus);
        }
        aPrClass.addPrerequisite(aPrereq);
        final PCClass aQClass = new PCClass();
        aQClass.setName("QualClass");
        aQClass.put(StringKey.KEY_NAME, "KEY_QualClass");
        CDOMDirectSingleRef<PCClass> ref = CDOMDirectSingleRef.getRef(aPrClass);
        aQClass.addToListFor(ListKey.QUALIFY, new Qualifier(ref));

        final PCClass aNqClass = new PCClass();
        aNqClass.setName("NonQualClass");
        aNqClass.put(StringKey.KEY_NAME, "KEY_NonQualClass");
        aNqClass.put(VariableKey.getConstant("Foo"), FormulaFactory.ONE);
        aNqClass.getOriginalClassLevel(2).put(VariableKey.getConstant("Foo"),
                FormulaFactory.getFormulaFor(2));

        finishLoad();

        // Setup character without prereqs
        final PlayerCharacter character = getCharacter();

        // Test no prereqs and no bypass fails class and var
        assertFalse(aPrClass
                .qualifies(character, aPrClass), "PC with no prereqs should fail class qual test.");
        assertEquals(0.0,
                aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1, "PC with no prereqs should fail var qual test."
        );

        // Test no prereqs and bypass passes class and fails var
        aClassPreRule.setDefault(true);
        assertTrue(
                aPrClass.qualifies(character, aPrClass),
                "PC with no prereqs should pass class qual test when bypassing prereqs is on.");
        assertEquals(
                0.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with no prereqs should fail var qual test when bypass prereqs is on.");

        // Test prereqs and bypass pass class and var
        character.incrementClassLevel(1, aNqClass);
        assertTrue(
                aPrClass.qualifies(character, aPrClass), "PC with prereqs and bypass should pass class qual test.");
        character.incrementClassLevel(1, aNqClass);
        assertEquals(
                10.0, aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with prereqs and bypass should pass var qual test.");

        // Test prereqs and no bypass passes class and var
        aClassPreRule.setDefault(false);
        assertTrue(
                aPrClass.qualifies(character, aPrClass), "PC with prereqs and no bypass should pass class qual test.");
        assertEquals(
                10.0,
                aPrClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with prereqs and no bypass should pass var qual test."
        );

    }

    /**
     * Test the interaction of prerequisites on PCClasses and bonuses and the
     * Qualifies functionality associated with a class.
     */
    @Test
    void testQualifies()
    {
        finishLoad();
        // Setup character without prereqs
        final PlayerCharacter character = getCharacter();

        // Test no prereqs and no qualifies fails class and var
        assertFalse(prClass
                .qualifies(character, prClass), "PC with no prereqs should fail class qual test.");
        assertEquals(0.0,
                prClass.getBonusTo("MISC", "SR", 1, character), 0.1, "PC with no prereqs should fail var qual test."
        );

        // Test no prereqs and qualifies passes class and fails var
        character.incrementClassLevel(1, qClass);
        assertTrue(
                prClass.qualifies(character, prClass),
                "PC with no prereqs but a qualifies should pass class qual test.");
        assertEquals(
                0.0, prClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with no prereqs but a qualifies should fail var qual test.");

        // Test prereqs and qualifies pass class and var
        character.incrementClassLevel(1, nqClass);
        assertTrue(
                prClass.qualifies(character, prClass), "PC with prereqs and qualifies should pass class qual test.");
        character.incrementClassLevel(1, nqClass);
        assertEquals(
                10.0,
                prClass.getBonusTo("MISC", "SR", 1, character), 0.1,
                "PC with prereqs and qualifies should pass var qual test."
        );
    }

    /**
     * Test the processing of getPCCText to ensure that it correctly produces
     * an LST representation of an object and that the LST can then be reloaded
     * to recreate the object.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    void testGetPCCText() throws PersistenceLayerException
    {
        FactKey.getConstant("Abb", new StringManager());
        // Test a basic class
        String classPCCText = humanoidClass.getPCCText();
        assertNotNull(classPCCText, "PCC Text for race should not be null");

        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
        PCClass reconstClass = null;
        System.out.println("Got text:" + classPCCText);
        reconstClass = parsePCClassText(classPCCText, source);
        assertEquals(
                classPCCText, reconstClass.getPCCText(),
                "getPCCText should be the same after being encoded and reloaded");
        assertEquals(
                humanoidClass.getAbbrev(), reconstClass.getAbbrev(),
                "Class abbrev was not restored after saving and reloading.");

        // Test a class with some innate spells
        String b =
                "1"
                        + "\t"
                        + "SPELLS:"
                        + "Humanoid|TIMES=1|CASTERLEVEL=var(\"TCL\")|Create undead,11+WIS";
        PCClassLoader classLoader = new PCClassLoader();
        classLoader.parseLine(Globals.getContext(), humanoidClass, b, source);
        classPCCText = humanoidClass.getPCCText();
        assertNotNull(classPCCText, "PCC Text for race should not be null");

        reconstClass = null;
        System.out.println("Got text:" + classPCCText);
        reconstClass = parsePCClassText(classPCCText, source);
        assertEquals(
                classPCCText, reconstClass.getPCCText(),
                "getPCCText should be the same after being encoded and reloaded");
        assertEquals(
                humanoidClass.getAbbrev(), reconstClass.getAbbrev(),
                "Class abbrev was not restored after saving and reloading.");
        Collection<CDOMReference<Spell>> startSpells =
                humanoidClass.getOriginalClassLevel(1).getListMods(Spell.SPELLS);
        Collection<CDOMReference<Spell>> reconstSpells =
                reconstClass.getOriginalClassLevel(1).getListMods(Spell.SPELLS);
        assertEquals(startSpells
                .size(), reconstSpells.size(), "All spell should have been reconstituted.");
        assertEquals(startSpells, reconstSpells, "Spell names should been preserved.");

    }

    /**
     * Test the function of the getHighestLevelSpell method.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    void testGetHighestLevelSpell() throws PersistenceLayerException
    {
        LoadContext context = Globals.getContext();
        PCClass megaCasterClass = new PCClass();
        megaCasterClass.setName("MegaCaster");
        BuildUtilities.setFact(megaCasterClass, "SpellType", "Arcane");
        context.unconditionallyProcess(megaCasterClass, "SPELLSTAT", "CHA");
        megaCasterClass.put(ObjectKey.SPELLBOOK, false);
        megaCasterClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "KNOWN", "4,2,2,3,4,5");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "CAST", "3,1,2,3,4,5");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "KNOWN", "4,2,2,3,4,5,6,7,8,9,10");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "CAST", "3,1,2,3,4,5,6,7,8,9,10");
        Globals.getContext().getReferenceContext().importObject(megaCasterClass);

        finishLoad();

        final PlayerCharacter character = getCharacter();
        assertEquals(10,
                character.getSpellSupport(megaCasterClass).getHighestLevelSpell(), "Highest spell level for class"
        );

        character.incrementClassLevel(1, megaCasterClass);
        PCClass charClass =
                character.getClassKeyed(megaCasterClass.getKeyName());
        assertEquals(10,
                character.getSpellSupport(charClass).getHighestLevelSpell(), "Highest spell level for character's class"
        );

        String sbook = Globals.getDefaultSpellBook();

        String cast =
                character.getSpellSupport(charClass).getCastForLevel(10, sbook, true, false, character)
                        + character.getSpellSupport(charClass).getBonusCastForLevelString(10, sbook, character);
        assertEquals(
                "0",
                cast, "Should not be able to cast 10th level spells at 1st level"
        );
        cast =
                character.getSpellSupport(charClass).getCastForLevel(5, sbook, true, false, character)
                        + character.getSpellSupport(charClass).getBonusCastForLevelString(5, sbook, character);
        assertEquals(
                "5", cast, "Should be able to cast 5th level spells at 1st level");

        Ability casterFeat = new Ability();
        FeatLoader featLoader = new FeatLoader();
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
        featLoader
                .parseLine(
                        Globals.getContext(),
                        casterFeat,
                        "CasterBoost	TYPE:General	BONUS:SPELLCAST|CLASS=MegaCaster;LEVEL=11|1", source);
        casterFeat.setCDOMCategory(BuildUtilities.getFeatCat());
        context.getReferenceContext().importObject(casterFeat);

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), casterFeat, null);
        cast =
                character.getSpellSupport(charClass).getCastForLevel(11, sbook, true, false, character)
                        + character.getSpellSupport(charClass).getBonusCastForLevelString(11, sbook, character);
        assertEquals("1",
                cast, "Should be able to cast 11th level spells with feat"
        );
        assertEquals(11,
                character.getSpellSupport(charClass).getHighestLevelSpell(character),
                "Should be able to cast 11th level spells with feat"
        );
    }

    /**
     * Test if SPELLCAST bonus handles high stat bonus spells well
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    void testGetBonusSpellSplots() throws PersistenceLayerException
    {
        LoadContext context = Globals.getContext();
        PCClass megaCasterClass = new PCClass();
        megaCasterClass.setName("MegaCaster");
        BuildUtilities.setFact(megaCasterClass, "SpellType", "Arcane");
        context.unconditionallyProcess(megaCasterClass, "SPELLSTAT", "CHA");
        megaCasterClass.put(ObjectKey.SPELLBOOK, false);
        megaCasterClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "KNOWN", "4,2,2,3,4,5");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "CAST", "3,1,2,3,4,5");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "KNOWN", "4,2,2,3,4,5,6,7,8,9,10");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "CAST", "3,1,2,3,4,5,6,7,8,9,10");
        Globals.getContext().getReferenceContext().importObject(megaCasterClass);

        finishLoad();

        PlayerCharacter character = getCharacter();

        character.incrementClassLevel(1, megaCasterClass);
        PCClass charClass =
                character.getClassKeyed(megaCasterClass.getKeyName());

        String sbook = Globals.getDefaultSpellBook();

        Ability casterFeat = new Ability();
        FeatLoader featLoader = new FeatLoader();
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
        featLoader
                .parseLine(
                        Globals.getContext(),
                        casterFeat,
                        "CasterBoost	TYPE:General	BONUS:SPELLCAST|CLASS=MegaCaster;LEVEL=11|1", source);
        casterFeat.setCDOMCategory(BuildUtilities.getFeatCat());
        context.getReferenceContext().importObject(casterFeat);

        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), casterFeat, null);
        String cast =
                character.getSpellSupport(charClass).getCastForLevel(11, sbook, true, false, character)
                        + character.getSpellSupport(charClass).getBonusCastForLevelString(11, sbook, character);
        assertEquals("1",
                cast, "Should be able to cast 11th level spells with feat"
        );
        assertEquals(11,
                character.getSpellSupport(charClass).getHighestLevelSpell(character),
                "Should be able to cast 11th level spells with feat"
        );

        for (int li = 1;li < 15;++li)
        {
            BonusSpellInfo bsi = new BonusSpellInfo();
            bsi.setName(Integer.toString(li));
            bsi.setStatScore(10 + 2 * li);
            bsi.setStatRange(8);
            context.getReferenceContext().importObject(bsi);
        }

        character.setStat(cha, 20);
        int numSpellCast = character.getSpellSupport(charClass).getCastForLevel(11, character);
        assertEquals(1, numSpellCast, "Should be able to cast one 11th level spell with feat");
        character.setStat(cha, 34);
        numSpellCast = character.getSpellSupport(charClass).getCastForLevel(11, character);
        assertEquals(2, numSpellCast, "Should be able to cast two 11th level spells with feat and stat");
        character.setStat(cha, 40);
        numSpellCast = character.getSpellSupport(charClass).getCastForLevel(11, character);
        assertEquals(3, numSpellCast, "Should be able to cast three 11th level spells with feat and stat");
        character.setStat(cha, 46);
        numSpellCast = character.getSpellSupport(charClass).getCastForLevel(11, character);
        assertEquals(3, numSpellCast, "Should be able to cast three 11th level spells with feat and stat");
    }

    @Test
    void testGetKnownForLevel()
    {
        LoadContext context = Globals.getContext();
        PCClass megaCasterClass = new PCClass();
        megaCasterClass.setName("MegaCaster");
        BuildUtilities.setFact(megaCasterClass, "SpellType", "Arcane");
        context.unconditionallyProcess(megaCasterClass, "SPELLSTAT", "CHA");
        megaCasterClass.put(ObjectKey.SPELLBOOK, false);
        megaCasterClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "KNOWN", "4,2,2,3,4,5,0");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "CAST", "3,1,2,3,4,5,0,0");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "KNOWN", "4,2,2,3,4,5,6,7,8,9,10");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "CAST", "3,1,2,3,4,5,6,7,8,9,10");
        Globals.getContext().getReferenceContext().importObject(megaCasterClass);

        finishLoad();

        final PlayerCharacter character = getCharacter();

        // Test retrieval for a non-spell casting class.
        character.incrementClassLevel(1, nqClass);
        PCClass charClass = character.getClassKeyed(nqClass.getKeyName());
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(0, character),
                "Known 0th level for non spell casting class"
        );

        // Test retrieval for a spell casting class.
        character.incrementClassLevel(1, megaCasterClass);
        charClass = character.getClassKeyed(megaCasterClass.getKeyName());
        setPCStat(character, cha, 10);
        assertEquals(4,
                character.getSpellSupport(charClass).getKnownForLevel(0, character), "Known 0th level for character's class"
        );
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(1, character), "Known 1st level where stat is too low"
        );
        setPCStat(character, cha, 11);
        character.calcActiveBonuses();
        assertEquals(
                2, character.getSpellSupport(charClass).getKnownForLevel(1, character),
                "Known 1st level where stat is high enough, but no bonus");
        setPCStat(character, cha, 18);
        character.calcActiveBonuses();
        assertEquals(
                2, character.getSpellSupport(charClass).getKnownForLevel(1, character),
                "Known 1st level where stat gives bonus but not active");

        RuleCheck bonusKnownRule = new RuleCheck();
        bonusKnownRule.setName(RuleConstants.BONUSSPELLKNOWN);
        bonusKnownRule.setDefault(true);
        GameMode gameMode = SettingsHandler.getGame();
        gameMode.getModeContext().getReferenceContext().importObject(bonusKnownRule);
        BonusSpellInfo bsi = new BonusSpellInfo();
        bsi.setName("1");
        bsi.setStatScore(12);
        bsi.setStatRange(8);
        context.getReferenceContext().importObject(bsi);
        bsi = new BonusSpellInfo();
        bsi.setName("5");
        bsi.setStatScore(20);
        bsi.setStatRange(8);
        assertEquals(3,
                character.getSpellSupport(charClass).getKnownForLevel(1, character),
                "Known 1st level where stat gives bonus and active"
        );

        assertEquals(2,
                character.getSpellSupport(charClass).getKnownForLevel(2, character), "Known 2nd level for character's class"
        );
        assertEquals(3,
                character.getSpellSupport(charClass).getKnownForLevel(3, character), "Known 3rd level for character's class"
        );
        assertEquals(4,
                character.getSpellSupport(charClass).getKnownForLevel(4, character), "Known 4th level for character's class"
        );
        charClass.put(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY, 1);
        assertEquals(6,
                character.getSpellSupport(charClass).getKnownForLevel(5, character), "Known 5th level for character's class"
        );
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(6, character),
                "Known 6th level for character's class"
        );
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(7, character), "Known 7th level for character's "
                        + "class"
        );

        // Add spell bonus for level above known max
        bsi = new BonusSpellInfo();
        bsi.setName("7");
        bsi.setStatScore(12);
        bsi.setStatRange(8);
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(7, character), "Known 7th level for character's class"
        );

        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(8, character), "Known 8th level for character's class"
        );

    }

    @Test
    void testGetKnownForLevelSpellstatOther()
    {
        LoadContext context = Globals.getContext();
        PCClass megaCasterClass = new PCClass();
        megaCasterClass.setName("MegaCasterOther");
        BuildUtilities.setFact(megaCasterClass, "SpellType", "Arcane");
        context.unconditionallyProcess(megaCasterClass, "SPELLSTAT", "OTHER");
        megaCasterClass.put(ObjectKey.SPELLBOOK, false);
        megaCasterClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "KNOWN", "4,2,2,3,4,5,0");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(1), "CAST", "3,1,2,3,4,5,0,0");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "KNOWN", "4,2,2,3,4,5,6,7,8,9,10");
        context.unconditionallyProcess(megaCasterClass.getOriginalClassLevel(2), "CAST", "3,1,2,3,4,5,6,7,8,9,10");
        Globals.getContext().getReferenceContext().importObject(megaCasterClass);

        finishLoad();

        final PlayerCharacter character = getCharacter();

        // Test retrieval for a non-spell casting class.
        character.incrementClassLevel(1, nqClass);
        PCClass charClass = character.getClassKeyed(nqClass.getKeyName());
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(0, character),
                "Known 0th level for non spell casting class"
        );

        // Test retrieval for a spell casting class.
        character.incrementClassLevel(1, megaCasterClass);
        charClass = character.getClassKeyed(megaCasterClass.getKeyName());
        setPCStat(character, cha, 10);
        assertEquals(4,
                character.getSpellSupport(charClass).getKnownForLevel(0, character), "Known 0th level for character's class"
        );
        character.calcActiveBonuses();
        assertEquals(
                2, character.getSpellSupport(charClass).getKnownForLevel(1, character),
                "Known 1st level where stat is high enough, but no bonus");

        RuleCheck bonusKnownRule = new RuleCheck();
        bonusKnownRule.setName(RuleConstants.BONUSSPELLKNOWN);
        bonusKnownRule.setDefault(true);
        GameMode gameMode = SettingsHandler.getGame();
        gameMode.getModeContext().getReferenceContext().importObject(bonusKnownRule);
        BonusSpellInfo bsi = new BonusSpellInfo();
        bsi.setName("1");
        bsi.setStatScore(12);
        bsi.setStatRange(8);
        context.getReferenceContext().importObject(bsi);
        bsi = new BonusSpellInfo();
        bsi.setName("5");
        bsi.setStatScore(20);
        bsi.setStatRange(8);
        assertEquals(2,
                character.getSpellSupport(charClass).getKnownForLevel(1, character),
                "Known 1st level where stat would give bonus and active"
        );

        assertEquals(2,
                character.getSpellSupport(charClass).getKnownForLevel(2, character), "Known 2nd level for character's class"
        );
        assertEquals(3,
                character.getSpellSupport(charClass).getKnownForLevel(3, character), "Known 3rd level for character's class"
        );
        assertEquals(4,
                character.getSpellSupport(charClass).getKnownForLevel(4, character),
                "Known 4th level for character's class"
        );
        charClass.put(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY, 1);
        assertEquals(6,
                character.getSpellSupport(charClass).getKnownForLevel(5, character), "Known 5th level for character's class"
        );
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(6, character), "Known 6th level for character's class"
        );
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(7, character), "Known 7th level for character's class"
        );

        // Add spell bonus for level above known max
        bsi = new BonusSpellInfo();
        bsi.setName("7");
        bsi.setStatScore(12);
        bsi.setStatRange(8);
        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(7, character), "Known 7th level for character's class"
        );

        assertEquals(0,
                character.getSpellSupport(charClass).getKnownForLevel(8, character), "Known 8th level for character's class"
        );

    }

    /**
     * Test the definition and application of abilities.
     *
     * @throws PersistenceLayerException
     */
    @Test
    void testAddAbility() throws PersistenceLayerException
    {
        LoadContext context = Globals.getContext();
        // Create some abilities to be added
        AbilityCategory cat = context.getReferenceContext().constructCDOMObject(
                AbilityCategory.class, "TestCat");
        Ability ab1 = new Ability();
        ab1.setName("Ability1");
        ab1.setCDOMCategory(cat);
        context.getReferenceContext().importObject(ab1);
        Ability ab2 = new Ability();
        ab2.setName("Ability2");
        ab2.setCDOMCategory(cat);
        context.getReferenceContext().importObject(ab2);

        // Link them to a template
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
        String classPCCText =
                "CLASS:Cleric	HD:8		CLASSTYPE:PC	TYPE:Base.PC	ABB:Clr	ABILITY:TestCat|AUTOMATIC|Ability1\n"
                        + "CLASS:Cleric	STARTSKILLPTS:2\n"
                        + "2	ABILITY:TestCat|AUTOMATIC|Ability2";
        PCClass pcclass = parsePCClassText(classPCCText, source);
        CDOMSingleRef<AbilityCategory> acRef =
                context.getReferenceContext().getCDOMReference(
                        AbilityCategory.class, "TestCat");

        finishLoad();

        CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(acRef, Nature.AUTOMATIC);
        Collection<CDOMReference<Ability>> mods = pcclass.getListMods(autoList);
        assertEquals(1, mods.size());
        CDOMReference<Ability> ref = mods.iterator().next();
        Collection<Ability> abilities = ref.getContainedObjects();
        assertEquals(1, abilities.size());
        assertEquals(ab1, abilities.iterator().next());
        Collection<AssociatedPrereqObject> assocs = pcclass.getListAssociations(autoList, ref);
        assertEquals(1, assocs.size());

        PCClassLevel level = pcclass.getOriginalClassLevel(2);
        mods = level.getListMods(autoList);
        assertEquals(1, mods.size());
        ref = mods.iterator().next();
        abilities = ref.getContainedObjects();
        assertEquals(1, abilities.size());
        assertEquals(ab2, abilities.iterator().next());
        assocs = level.getListAssociations(autoList, ref);
        assertEquals(1, assocs.size());

        // Add the class to the character
        PlayerCharacter pc = getCharacter();
        pc.incrementClassLevel(1, pcclass, true);
        assertTrue(hasAbility(pc, cat,
                Nature.AUTOMATIC, ab1), "Character should have ability1.");
        assertFalse(hasAbility(pc, cat,
                Nature.AUTOMATIC, ab2), "Character should not have ability2.");

        pc.incrementClassLevel(1, pcclass, true);
        assertTrue(hasAbility(pc, cat,
                Nature.AUTOMATIC, ab1), "Character should have ability1.");
        assertTrue(hasAbility(pc, cat,
                Nature.AUTOMATIC, ab2), "Character should have ability2.");
    }

    /**
     * Test the function of the LEVELSPERFEAT in setLevel()
     * Monster class without a levels per feat setting.
     */
    @Test
    void testDefaultLevelsPerFeatMonster()
    {
        finishLoad();
        PlayerCharacter pc = getCharacter();
        pc.setRace(nymphRace);
        List<BonusObj> bonusList = nymphClass.getRawBonusList(pc);
        assertEquals(0, bonusList.size(), "Bonus list empty");

        pc.incrementClassLevel(1, nymphClass);
        bonusList = pc.getClassKeyed(nymphClass.getKeyName()).getRawBonusList(pc);
        assertEquals(1, bonusList.size(), "Only one bonus");
        assertEquals("FEAT|PCPOOL|MAX(CL-3+3,0)/3", bonusList.get(0).toString(), "Bonus added ");
    }

    /**
     * Test the function of the LEVELSPERFEAT in setLevel()
     * Monster class with a levels per feat setting.
     */
    @Test
    void testLevelsPerFeatMonster()
    {
        finishLoad();
        PlayerCharacter pc = getCharacter();
        nymphClass.put(IntegerKey.LEVELS_PER_FEAT, 4);
        List<BonusObj> bonusList = nymphClass.getRawBonusList(pc);
        assertEquals(0, bonusList.size(), "Bonus list empty");
        pc.setRace(nymphRace);
        bonusList = nymphClass.getRawBonusList(pc);
        assertEquals(0, bonusList.size(), "Bonus list empty");

        pc.incrementClassLevel(1, nymphClass);
        bonusList = pc.getClassKeyed(nymphClass.getKeyName()).getRawBonusList(pc);
        assertEquals(0, bonusList.size(), "No bonus due to the LEVELSPERFEAT");
    }

    /**
     * Test the function of the LEVELSPERFEAT in setLevel()
     * Non monster class without a levels per feat setting.
     */
    @Test
    void testDefaultLevelsPerFeatNonMonster()
    {
        finishLoad();
        PlayerCharacter pc = getCharacter();
        pc.setRace(nymphRace);
        List<BonusObj> bonusList = humanoidClass.getRawBonusList(pc);
        assertEquals(3, bonusList.size(), "Bonus list starting size");

        pc.incrementClassLevel(1, humanoidClass);
        bonusList = pc.getClassKeyed(humanoidClass.getKeyName()).getRawBonusList(pc);
        assertEquals("FEAT|PCPOOL|MAX(CL-3+3,0)/3", bonusList.get(3).toString(), "Bonus added ");
        assertEquals(4, bonusList.size(), "Only one new bonus");
    }

    /**
     * Test the function of the LEVELSPERFEAT in setLevel()
     * Non monster class with a levels per feat setting.
     */
    @Test
    void testLevelsPerFeatNonMonster()
    {
        finishLoad();
        PlayerCharacter pc = getCharacter();
        pc.setRace(nymphRace);
        humanoidClass.put(IntegerKey.LEVELS_PER_FEAT, 4);
        List<BonusObj> bonusList = humanoidClass.getRawBonusList(pc);
        assertEquals(3, bonusList.size(), "Bonus list starting size");

        pc.incrementClassLevel(1, humanoidClass);
        bonusList = pc.getClassKeyed(humanoidClass.getKeyName()).getRawBonusList(pc);
        assertEquals(3, bonusList.size(), "No new bonus due to the LEVELSPERFEAT");
    }

    /**
     * Parse a class definition and return the populated PCClass object.
     *
     * @param classPCCText The textual definition of the class.
     * @param source       The source that the class is from.
     * @return The populated class.
     * @throws PersistenceLayerException
     */
    private static PCClass parsePCClassText(String classPCCText,
            CampaignSourceEntry source) throws PersistenceLayerException
    {
        PCClassLoader pcClassLoader = new PCClassLoader();
        PCClass reconstClass = null;
        StringTokenizer tok = new StringTokenizer(classPCCText, "\n");
        while (tok.hasMoreTokens())
        {
            String line = tok.nextToken();
            if (!line.trim().isEmpty())
            {
                System.out.println("Processing line:'" + line + "'.");
                reconstClass =
                        pcClassLoader.parseLine(Globals.getContext(), reconstClass, line, source);
            }
        }
        return reconstClass;
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        Campaign customCampaign = new Campaign();
        customCampaign.setName("Unit Test");
        customCampaign.setName("KEY_Unit Test");
        customCampaign.addToListFor(ListKey.DESCRIPTION, new Description("Unit Test data"));
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(customCampaign,
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }

        // Create the monseter class type
        GameMode gamemode = SettingsHandler.getGame();
        SimpleLoader<ClassType> methodLoader = new SimpleLoader<>(ClassType.class);
        methodLoader.parseLine(gamemode.getModeContext(),
                "Monster		CRFORMULA:0			ISMONSTER:YES	XPPENALTY:NO",
                TestURI.getURI());
        gamemode.removeSkillMultiplierLevels();
        gamemode.addSkillMultiplierLevel("4");

        // Create the humanoid class
        String classDef =
                "CLASS:Humanoid	KEY:KEY_Humanoid	HD:8		CLASSTYPE:Monster	STARTSKILLPTS:1	"
                        + "MODTOSKILLS:NO	MONSKILL:6+INT	MONNONSKILLHD:1|PRESIZELTEQ:M	"
                        + "MONNONSKILLHD:2|PRESIZEEQ:L";
        PCClassLoader classLoader = new PCClassLoader();
        LoadContext context = Globals.getContext();
        humanoidClass = classLoader.parseLine(context, null, classDef, source);

        classDef =
                "CLASS:Nymph		KEY:KEY_Nymph	CLASSTYPE:Monster	HD:6	STARTSKILLPTS:6	MODTOSKILLS:YES	";
        classLoader = new PCClassLoader();
        nymphClass = classLoader.parseLine(context, null, classDef, source);

        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
        // Create the large size mod
        // Create the BugBear race
        bugbearRace = new Race();
        bugbearRace.setName("Bugbear");
        bugbearRace.put(StringKey.KEY_NAME, "KEY_Bugbear");
        bugbearRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        bugbearRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        bugbearRace.put(IntegerKey.INITIAL_SKILL_MULT, 1);
        Globals.getContext().getReferenceContext().importObject(bugbearRace);

        bigBugbearRace = new Race();
        bigBugbearRace.setName("BigBugbear");
        bigBugbearRace.put(StringKey.KEY_NAME, "KEY_BigBugbear");
        bigBugbearRace.put(FormulaKey.SIZE, new FixedSizeFormula(largeRef));
        bigBugbearRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        bigBugbearRace.put(IntegerKey.INITIAL_SKILL_MULT, 1);
        Globals.getContext().getReferenceContext().importObject(bigBugbearRace);

        // Create the Nymph race
        nymphRace = new Race();
        nymphRace.setName("Nymph");
        nymphRace.put(StringKey.KEY_NAME, "KEY_Nymph");
        nymphRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        nymphRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        nymphRace.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
                CDOMDirectSingleRef.getRef(nymphClass), FormulaFactory
                .getFormulaFor(0)));
        Globals.getContext().getReferenceContext().importObject(nymphRace);

        // Setup class with prereqs and var based abilities with prereqs.
        PreVariableParser parser = new PreVariableParser();
        Prerequisite prereq = parser.parse("VARGTEQ", "Foo,1", false, false);
        RuleCheck classPreRule = new RuleCheck();
        classPreRule.setName("CLASSPRE");
        classPreRule.setDefault(false);
        gamemode.getModeContext().getReferenceContext().importObject(classPreRule);

        prClass = new PCClass();
        prClass.setName("PreReqClass");
        prClass.put(StringKey.KEY_NAME, "KEY_PreReqClass");
        final BonusObj aBonus = Bonus.newBonus(context, "MISC|SR|10|PREVARGTEQ:Foo,2");

        if (aBonus != null)
        {
            prClass.addToListFor(ListKey.BONUS, aBonus);
        }
        prClass.addPrerequisite(prereq);
        qClass = new PCClass();
        qClass.setName("QualClass");
        qClass.put(StringKey.KEY_NAME, "KEY_QualClass");
        CDOMDirectSingleRef<PCClass> ref = CDOMDirectSingleRef.getRef(prClass);
        qClass.addToListFor(ListKey.QUALIFY, new Qualifier(ref));
        nqClass = new PCClass();
        nqClass.setName("NonQualClass");
        nqClass.put(StringKey.KEY_NAME, "KEY_NonQualClass");
        nqClass.put(VariableKey.getConstant("Foo"), FormulaFactory.ONE);
        nqClass.getOriginalClassLevel(2).put(VariableKey.getConstant("Foo"),
                FormulaFactory.getFormulaFor(2));
    }

    @Override
    protected void defaultSetupEnd()
    {
        //Nothing, we will trigger ourselves
    }
}
