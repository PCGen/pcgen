/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.core.term;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Campaign;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PCRacialHDSizeTermEvaluatorTest checks the function of the RACIALHDSIZE variable.
 */
public class PCRacialHDSizeTermEvaluatorTest extends AbstractCharacterTestCase
{
    PCClass humanoidClass;
    PCClass pcClass;
    Race bugbearRace = new Race();
    Race humanRace = new Race();
    PCRacialHDSizeTermEvaluator eval = new PCRacialHDSizeTermEvaluator("RACIALHDSIZE");

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        Campaign customCampaign = new Campaign();
        customCampaign.setName("Unit Test");
        customCampaign.setName("KEY_Unit Test");
        customCampaign.addToListFor(ListKey.DESCRIPTION, new Description("Unit Test data"));
        CampaignSourceEntry source = new CampaignSourceEntry(customCampaign,
                new URI("file:/" + getClass().getName() + ".java"));
        LoadContext context = Globals.getContext();
        PCClassLoader classLoader = new PCClassLoader();

        // Create the humanoid monster class
        final String humanoidClassLine =
                "CLASS:Humanoid	KEY:KEY_Humanoid	HD:8	TYPE:Monster	CLASSTYPE:Monster	"
                        + "STARTSKILLPTS:2	MODTOSKILLS:YES";
        humanoidClass = classLoader.parseLine(context, null, humanoidClassLine, source);
        context.getReferenceContext().importObject(humanoidClass);

        // Create the pc class
        final String pcClassLine = "CLASS:TestPCClass	TYPE:PC		HD:10";
        pcClass = classLoader.parseLine(context, null, pcClassLine, source);
        context.getReferenceContext().importObject(pcClass);

        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        // Create the BugBear race
        bugbearRace.setName("Bugbear");
        bugbearRace.put(StringKey.KEY_NAME, "KEY_Bugbear");
        bugbearRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        bugbearRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
        bugbearRace.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
                CDOMDirectSingleRef.getRef(humanoidClass), FormulaFactory.getFormulaFor(3)));
        context.getReferenceContext().importObject(bugbearRace);

        // Create the human race
        humanRace.setName("Human");
        humanRace.put(StringKey.KEY_NAME, "KEY_Human");
        humanRace.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        context.getReferenceContext().importObject(humanRace);
    }

    /**
     * Check for creature with racial HD but no class levels
     */
    @Test
    public void testBugbearWithNoClassLevels()
    {
        PlayerCharacter pc = getCharacter();
        pc.setRace(bugbearRace);
        assertEquals("Bugbear racial HD size should be 8", 8, eval.resolve(pc.getDisplay()), 0.001);
    }

    /**
     * Check for creature with racial HD and class levels
     */
    @Test
    public void testBugbearWithClassLevels()
    {
        PlayerCharacter pc = getCharacter();
        pc.setRace(bugbearRace);
        pc.incrementClassLevel(1, pcClass);
        assertEquals("Bugbear racial HD size should be 8", 8, eval.resolve(pc.getDisplay()), 0.001);
    }

    /**
     * Check for creature with no racial HD
     */
    @Test
    public void testHuman()
    {
        PlayerCharacter pc = getCharacter();
        pc.setRace(humanRace);
        pc.incrementClassLevel(1, pcClass);
        assertEquals("Human racial HD size should be 0", 0, eval.resolve(pc.getDisplay()), 0.001);
    }
}
