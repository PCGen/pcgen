/*
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * $$Revision$$
 * $$Date$$
 * $$Time$$
 *
 * $$id$$
 */
package pcgen.core.levelability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.helper.ClassSkillChoiceActor;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for Level Ability Class Skills
 */
@SuppressWarnings("nls")
public class AddClassSkillsTest extends AbstractCharacterTestCase
{

    PCClass pcClass;
    boolean firstTime = true;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        if (firstTime)
        {
            firstTime = false;

            pcClass = new PCClass();

            TestHelper.makeSkill("Bluff", "Charisma", cha, true, SkillArmorCheck.NONE);
            TestHelper.makeSkill("Listen", "Wisdom", wis, true, SkillArmorCheck.NONE);
            TestHelper.makeSkill("Move Silently", "Dexterity", dex, true,
                    SkillArmorCheck.YES);
            TestHelper.makeSkill("Knowledge (Arcana)",
                    "Intelligence.Knowledge", intel, false, SkillArmorCheck.NONE);
            TestHelper.makeSkill("Knowledge (Dungeoneering)",
                    "Intelligence.Knowledge", intel, false, SkillArmorCheck.NONE);
        }

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception
    {
        pcClass = null;
        super.tearDown();
    }

    /**
     * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
     */
    @Test
    public void testBasicChoicesList()
    {
        PCClass po = new PCClass();
        PlayerCharacter pc = getCharacter();

        Globals.getContext().unconditionallyProcess(po, "ADD",
                "CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Move Silently");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));
        List<PersistentTransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
        assertEquals(1, choiceList.size());
        TransitionChoice<?> choice = choiceList.get(0);
        Collection<?> choiceSet = choice.getChoices().getSet(pc);
        assertEquals(3, choiceSet.size());
        assertEquals(2, choice.getCount().resolve(pc, ""));

        List<String> choiceStrings = new ArrayList<>();
        for (Object o : choiceSet)
        {
            choiceStrings.add(o.toString());
        }
        assertTrue(choiceStrings.contains("Bluff"));
        assertTrue(choiceStrings.contains("Listen"));
        assertTrue(choiceStrings.contains("Move Silently"));
    }

    /**
     * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
     */
    @Test
    public void testGetChoicesListWithParens()
    {
        PCClass po = new PCClass();

        Globals.getContext().unconditionallyProcess(po, "ADD",
                "CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Knowledge (Arcana)");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        List<PersistentTransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
        assertEquals(1, choiceList.size());
        TransitionChoice<?> choice = choiceList.get(0);
        Collection<?> choiceSet = choice.getChoices().getSet(getCharacter());
        assertEquals(3, choiceSet.size());
        assertEquals(2, choice.getCount().resolve(getCharacter(), ""));

        List<String> choiceStrings = new ArrayList<>();
        for (Object o : choiceSet)
        {
            choiceStrings.add(o.toString());
        }
        assertTrue(choiceStrings.contains("Bluff"));
        assertTrue(choiceStrings.contains("Listen"));
        assertTrue(choiceStrings.contains("Knowledge (Arcana)"));
    }

    /**
     * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
     */
    @Test
    public void testGetChoicesListWithClassSkill()
    {
        CampaignSourceEntry source;
        try
        {
            source = new CampaignSourceEntry(new Campaign(),
                    new URI("file:/" + getClass().getName() + ".java"));
        } catch (URISyntaxException e)
        {
            throw new UnreachableError(e);
        }
        String classPCCText = "CLASS:Cleric	HD:8		TYPE:Base.PC	ABB:Clr\n"
                + "CLASS:Cleric	STARTSKILLPTS:2	CSKILL:KEY_Knowledge (Dungeoneering)";
        PCClass po;
        try
        {
            po = parsePCClassText(classPCCText, source);
        } catch (PersistenceLayerException e)
        {
            throw new UnreachableError(e);
        }
        getCharacter().incrementClassLevel(1, po, false);

        PCTemplate pct = new PCTemplate();
        Skill bluff = Globals.getContext().getReferenceContext()
                .silentlyGetConstructedCDOMObject(Skill.class, "KEY_Bluff");
        pct.addToListFor(ListKey.CSKILL, CDOMDirectSingleRef.getRef(bluff));
        getCharacter().addTemplate(pct);

        Globals.getContext().unconditionallyProcess(po, "ADD",
                "CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Knowledge (Arcana)");
        assertTrue(Globals.getContext().getReferenceContext().resolveReferences(null));

        List<PersistentTransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
        assertEquals(1, choiceList.size());
        TransitionChoice<?> choice = choiceList.get(0);
        Collection<?> choiceSet = choice.getChoices().getSet(getCharacter());
        assertEquals(3, choiceSet.size());
        Set<Object> limitedSet = new HashSet<>();
        ClassSkillChoiceActor csca = new ClassSkillChoiceActor(po, 0);
        for (Object sc : choiceSet)
        {
            if (csca.allow((Skill) sc, getCharacter(), true))
            {
                limitedSet.add(sc);
            }
        }
        assertEquals(2, limitedSet.size());
        assertEquals(2, choice.getCount().resolve(getCharacter(), ""));

        List<String> choiceStrings = new ArrayList<>();
        for (Object o : limitedSet)
        {
            choiceStrings.add(o.toString());
        }
        assertTrue(choiceStrings.contains("Listen"));
        assertTrue(choiceStrings.contains("Knowledge (Arcana)"));
    }

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

}
