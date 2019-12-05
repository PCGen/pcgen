/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.core.display;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code SkillModifierTest} is responsible for checking that the
 * SkillModifier class is operating correctly.
 */
public class SkillCostDisplayTest extends AbstractCharacterTestCase
{

    private PCClass pcClass;
    private boolean firstTime = true;
    private Ability skillFocus = new Ability();
    private Ability persuasive = new Ability();
    private Skill bluff;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        LoadContext context = Globals.getContext();

        if (firstTime)
        {
            firstTime = false;

            pcClass = new PCClass();

            TestHelper.makeSkill("Bluff", "Charisma", cha, true,
                    SkillArmorCheck.NONE);
            TestHelper.makeSkill("Listen", "Wisdom", wis, true,
                    SkillArmorCheck.NONE);

            skillFocus =
                    TestHelper.makeAbility("Skill Focus", BuildUtilities.getFeatCat(), "General");
            BonusObj aBonus = Bonus.newBonus(context, "SKILL|LIST|3");

            if (aBonus != null)
            {
                skillFocus.addToListFor(ListKey.BONUS, aBonus);
            }
            skillFocus.put(ObjectKey.MULTIPLE_ALLOWED, true);
            Globals
                    .getContext()
                    .unconditionallyProcess(
                            skillFocus,
                            "CHOOSE",
                            "SKILL|TYPE.Strength|TYPE.Dexterity|TYPE.Constitution|TYPE.Intelligence|TYPE.Wisdom|TYPE.Charisma");

            persuasive =
                    TestHelper.makeAbility("Persuasive", BuildUtilities.getFeatCat(), "General");
            aBonus = Bonus.newBonus(context, "SKILL|KEY_Bluff,KEY_Listen|2");

            if (aBonus != null)
            {
                persuasive.addToListFor(ListKey.BONUS, aBonus);
            }
            persuasive.put(ObjectKey.MULTIPLE_ALLOWED, false);

        }

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception
    {
        pcClass = null;
        super.tearDown();
    }

    /**
     * Test getModifierExplanation for both lists and multiple
     * bonus feats.
     */
    @Test
    public void testGetModifierExplanation()
    {
        bluff =
                Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
                        Skill.class, "KEY_bluff");
        PlayerCharacter pc = getCharacter();
        setPCStat(pc, cha, 10);

        assertEquals("", SkillCostDisplay.getModifierExplanation(
                bluff, pc, false), "Initial state");

        AbstractCharacterTestCase.applyAbility(pc, BuildUtilities.getFeatCat(), skillFocus, "KEY_Bluff");
        pc.calcActiveBonuses();
        assertEquals("+3[Skill Focus]",
                SkillCostDisplay.getModifierExplanation(bluff, pc, false), "Bonus after skill focus"
        );

        addAbility(BuildUtilities.getFeatCat(), persuasive);
        String modifierExplanation = SkillCostDisplay
                .getModifierExplanation(bluff, pc, false);
        // Have to account for random order of the bonuses.
        assertTrue(
                modifierExplanation.equals("+2[Persuasive] +3[Skill Focus]")
                        || modifierExplanation.equals("+3[Skill Focus] +2[Persuasive]"), "Bonus after persuasive");
    }

}
