/*
 * Copyright 2007 (C) andrew wilson <nuance@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.jepcommands;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Campaign;
import pcgen.core.ClassType;
import pcgen.core.Description;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SimpleLoader;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

/**
 * {@code OrCommandTest} tests the functioning of the jep or plugin
 */
public class ClassLevelCommandTest extends AbstractCharacterTestCase
{
    private Race nymphRace;
    private PCClass megaCasterClass;
    private PCClass humanoidClass;
    private PCClass nymphClass;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        Campaign customCampaign = new Campaign();
        customCampaign.setName("Unit Test");
        customCampaign.setName("KEY_Unit Test");
        customCampaign.addToListFor(ListKey.DESCRIPTION, new Description("Unit Test data"));

        // Create the monseter class type
        GameMode gamemode = SettingsHandler.getGame();
        SimpleLoader<ClassType> methodLoader = new SimpleLoader<>(ClassType.class);
        methodLoader.parseLine(gamemode.getModeContext(),
                "Monster		CRFORMULA:0			ISMONSTER:YES	XPPENALTY:NO",
                TestURI.getURI());
        gamemode.removeSkillMultiplierLevels();
        gamemode.addSkillMultiplierLevel("4");
        gamemode.setMaxNonEpicLevel(20);

        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        // Create the Nymph race
        nymphRace = new Race();
        nymphRace.setName("Nymph");
        nymphRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        nymphRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        Globals.getContext().getReferenceContext().importObject(nymphRace);

        // Create the humanoid class
        humanoidClass = new PCClass();
        humanoidClass.setName("Humanoid");
        humanoidClass.addToListFor(ListKey.TYPE, Type.getConstant("Monster"));
        Globals.getContext().getReferenceContext().importObject(humanoidClass);

        nymphClass = new PCClass();
        nymphClass.setName("Nymph");
        nymphClass.addToListFor(ListKey.TYPE, Type.getConstant("Monster"));
        Globals.getContext().getReferenceContext().importObject(nymphClass);

        megaCasterClass = new PCClass();
        megaCasterClass.setName("MegaCaster");
        BuildUtilities.setFact(megaCasterClass, "SpellType", "Arcane");
        Globals.getContext().unconditionallyProcess(megaCasterClass, "SPELLSTAT", "CHA");
        megaCasterClass.put(ObjectKey.SPELLBOOK, false);
        megaCasterClass.put(ObjectKey.MEMORIZE_SPELLS, false);
        Globals.getContext().getReferenceContext().importObject(megaCasterClass);

    }

    @Test
    public void testClassLevel()
    {
        PlayerCharacter pc = this.getCharacter();
        pc.setRace(nymphRace);
        pc.incrementClassLevel(1, megaCasterClass);
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Humanoid\")", ""), closeTo(0.0, 0.001));
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Nymph\")", ""), closeTo(0.0, 0.001));
        assertThat("classlevel(\"MegaCaster\")", (double) pc.getVariableValue("classlevel(\"MegaCaster\")", ""), closeTo(1.0, 0.001));
        assertThat("classlevel(\"TYPE=Monster\")", (double) pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), closeTo(0.0, 0.001));
        pc.incrementClassLevel(1, megaCasterClass);
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Humanoid\")", ""), closeTo(0.0, 0.001));
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Nymph\")", ""), closeTo(0.0, 0.001));
        assertThat("classlevel(\"MegaCaster\")", (double) pc.getVariableValue("classlevel(\"MegaCaster\")", ""), closeTo(2.0, 0.001));
        assertThat("classlevel(\"TYPE=Monster\")", (double) pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), closeTo(0.0, 0.001));
        pc.incrementClassLevel(1, humanoidClass);
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Humanoid\")", ""), closeTo(1.0, 0.001));
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Nymph\")", ""), closeTo(0.0, 0.001));
        assertThat("classlevel(\"MegaCaster\")", (double) pc.getVariableValue("classlevel(\"MegaCaster\")", ""), closeTo(2.0, 0.001));
        assertThat("classlevel(\"TYPE=Monster\")", (double) pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), closeTo(1.0, 0.001));
        pc.incrementClassLevel(1, nymphClass);
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Humanoid\")", ""), closeTo(1.0, 0.001));
        assertThat("classlevel(\"Humanoid\")", (double) pc.getVariableValue("classlevel(\"Nymph\")", ""), closeTo(1.0, 0.001));
        assertThat("classlevel(\"MegaCaster\")", (double) pc.getVariableValue("classlevel(\"MegaCaster\")", ""), closeTo(2.0, 0.001));
        assertThat("classlevel(\"TYPE=Monster\")", (double) pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), closeTo(2.0, 0.001));
    }

    @Test
    public void testClassLevelAppliedAs()
    {
        PlayerCharacter pc = this.getCharacter();
        pc.setRace(nymphRace);
        pc.incrementClassLevel(1, megaCasterClass);
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:MegaCaster", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:MegaCaster"), closeTo(1.0, 0.001));
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:Nymph", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:Nymph"), closeTo(0.0, 0.001));
        pc.incrementClassLevel(1, megaCasterClass);
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:MegaCaster", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:MegaCaster"), closeTo(2.0, 0.001));
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:Nymph", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:Nymph"), closeTo(0.0, 0.001));
        pc.incrementClassLevel(1, nymphClass);
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:MegaCaster", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:MegaCaster"), closeTo(2.0, 0.001));
        assertThat("classlevel(\"APPLIEDAS=NONEPIC\") CLASS:Nymph", (double) pc.getVariableValue("classlevel(\"APPLIEDAS=NONEPIC\")", "CLASS:Nymph"), closeTo(1.0, 0.001));
    }
}
