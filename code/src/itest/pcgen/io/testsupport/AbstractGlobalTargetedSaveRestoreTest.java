/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.io.testsupport;

import static org.hamcrest.Matchers.closeTo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.ShieldProf;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.SkillRankControl;
import plugin.lsttokens.CcskillLst;
import plugin.lsttokens.CskillLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.auto.WeaponProfToken;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.pcclass.HdToken;
import plugin.lsttokens.skill.ExclusiveToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalTargetedSaveRestoreTest<T extends CDOMObject>
        extends AbstractSaveRestoreTest
{

    public abstract Class<T> getObjectClass();

    protected abstract Object prepare(T obj);

    protected abstract void remove(Object o);

    protected abstract void applyObject(T obj);

    protected void additionalChooseSet(T obj)
    {
        //Default to no action necessary
    }

    protected boolean isSymmetric()
    {
        //Default to true
        return true;
    }

    @Test
    public void testGlobalCSkill()
    {
        PCClass monclass = create(PCClass.class, "MonClass");
        new HdToken().parseToken(context, monclass, "8");
        new TypeLst().parseToken(context, monclass, "Monster");
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        T target = create(getObjectClass(), "Target");
        Skill skill = create(Skill.class, "MySkill");
        new ExclusiveToken().parseToken(context, skill, "Yes");
        new CskillLst().parseToken(context, target, "Granted");
        Object o = prepare(target);
        finishLoad();
        pc.incrementClassLevel(1, monclass);
        pc.setHP(pc.getActiveClassLevel(monclass, 0), 3);
        assertEquals(SkillCost.EXCLUSIVE,
                pc.getSkillCostForClass(granted, monclass));
        applyObject(target);
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, monclass));
        final Runnable cleanup = getPreEqualityCleanup();
        Runnable fullcleanup = () -> {
            if (cleanup != null)
            {
                cleanup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "MonClass");
            reloadedPC.getSpellSupport(cl);
        };
        runRoundRobin(fullcleanup);
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, monclass));
        assertEquals(SkillCost.CLASS,
                reloadedPC.getSkillCostForClass(granted, monclass));
        remove(o);
        reloadedPC.setDirty(true);
        assertEquals(SkillCost.EXCLUSIVE,
                reloadedPC.getSkillCostForClass(granted, monclass));
    }

    @Test
    public void testGlobalCSkillList()
    {
        PCClass monclass = create(PCClass.class, "MonClass");
        new HdToken().parseToken(context, monclass, "8");
        new TypeLst().parseToken(context, monclass, "Monster");
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        T target = create(getObjectClass(), "Target");
        Skill skill = create(Skill.class, "MySkill");
        new ExclusiveToken().parseToken(context, skill, "Yes");
        new CskillLst().parseToken(context, target, "LIST");
        new SkillToken().parseToken(context, target, "Granted|MySkill");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        pc.incrementClassLevel(1, monclass);
        pc.setHP(pc.getActiveClassLevel(monclass, 0), 3);
        assertEquals(SkillCost.EXCLUSIVE,
                pc.getSkillCostForClass(granted, monclass));
        applyObject(target);
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, monclass));
        final Runnable cleanup = getPreEqualityCleanup();
        Runnable fullcleanup = () -> {
            if (cleanup != null)
            {
                cleanup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "MonClass");
            reloadedPC.getSpellSupport(cl);
        };
        runRoundRobin(fullcleanup);
        assertEquals(SkillCost.CLASS,
                pc.getSkillCostForClass(granted, monclass));
        assertEquals(SkillCost.CLASS,
                reloadedPC.getSkillCostForClass(granted, monclass));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertEquals(SkillCost.EXCLUSIVE,
                    reloadedPC.getSkillCostForClass(granted, monclass));
        }
    }

    @Test
    public void testGlobalCCSkill()
    {
        PCClass myclass = create(PCClass.class, "SomeClass");
        new HdToken().parseToken(context, myclass, "8");
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        T target = create(getObjectClass(), "Target");
        create(Skill.class, "MySkill");
        new CcskillLst().parseToken(context, target, "Granted");
        Object o = prepare(target);
        finishLoad();
        pc.incrementClassLevel(1, myclass);
        pc.setHP(pc.getActiveClassLevel(myclass, 0), 3);
        assertEquals(SkillCost.EXCLUSIVE,
                pc.getSkillCostForClass(granted, myclass));
        applyObject(target);
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        final Runnable cleanup = getPreEqualityCleanup();
        Runnable fullcleanup = () -> {
            if (cleanup != null)
            {
                cleanup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "SomeClass");
            reloadedPC.getSpellSupport(cl);
        };
        runRoundRobin(fullcleanup);
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        assertEquals(SkillCost.CROSS_CLASS,
                reloadedPC.getSkillCostForClass(granted, myclass));
        remove(o);
        reloadedPC.setDirty(true);
        assertEquals(SkillCost.EXCLUSIVE,
                reloadedPC.getSkillCostForClass(granted, myclass));
    }

    @Test
    public void testGlobalCCSkillList()
    {
        PCClass myclass = create(PCClass.class, "SomeClass");
        new HdToken().parseToken(context, myclass, "8");
        Skill granted = create(Skill.class, "Granted");
        new ExclusiveToken().parseToken(context, granted, "Yes");
        T target = create(getObjectClass(), "Target");
        create(Skill.class, "MySkill");
        new CcskillLst().parseToken(context, target, "LIST");
        new SkillToken().parseToken(context, target, "Granted|MySkill");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        pc.incrementClassLevel(1, myclass);
        pc.setHP(pc.getActiveClassLevel(myclass, 0), 3);
        assertEquals(SkillCost.EXCLUSIVE,
                pc.getSkillCostForClass(granted, myclass));
        applyObject(target);
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        final Runnable cleanup = getPreEqualityCleanup();
        Runnable fullcleanup = () -> {
            if (cleanup != null)
            {
                cleanup.run();
            }
            //TODO need this to create the spell support :/
            PCClass cl =
                    context.getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                            "SomeClass");
            reloadedPC.getSpellSupport(cl);
        };
        runRoundRobin(fullcleanup);
        assertEquals(SkillCost.CROSS_CLASS,
                pc.getSkillCostForClass(granted, myclass));
        assertEquals(SkillCost.CROSS_CLASS,
                reloadedPC.getSkillCostForClass(granted, myclass));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertEquals(SkillCost.EXCLUSIVE,
                    reloadedPC.getSkillCostForClass(granted, myclass));
        }
    }

    @Test
    public void testAutoWeaponProf()
    {
        WeaponProf granted = create(WeaponProf.class, "Granted");
        create(WeaponProf.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new WeaponProfToken().parseToken(context, target, "Granted");
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasWeaponProf(granted));
        applyObject(target);
        assertTrue(pc.hasWeaponProf(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasWeaponProf(granted));
        assertTrue(reloadedPC.hasWeaponProf(granted));
        remove(o);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.hasWeaponProf(granted));
    }

    @Test
    public void testAutoWeaponProfList()
    {
        WeaponProf granted = create(WeaponProf.class, "Granted");
        create(WeaponProf.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new WeaponProfToken().parseToken(context, target, "%LIST");
        new plugin.lsttokens.choose.WeaponProficiencyToken().parseToken(
                context, target, "Granted|Ignored");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasWeaponProf(granted));
        applyObject(target);
        assertTrue(pc.hasWeaponProf(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasWeaponProf(granted));
        assertTrue(reloadedPC.hasWeaponProf(granted));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertFalse(reloadedPC.hasWeaponProf(granted));
        }
    }

    @Test
    public void testAutoShieldProf()
    {
        ShieldProf granted = create(ShieldProf.class, "Granted");
        create(ShieldProf.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new plugin.lsttokens.auto.ShieldProfToken().parseToken(context, target,
                "Granted");
        Object o = prepare(target);
        finishLoad();
        Equipment e = new Equipment();
        e.addToListFor(ListKey.TYPE, Type.SHIELD);
        e.put(ObjectKey.SHIELD_PROF, CDOMDirectSingleRef.getRef(granted));
        assertFalse(pc.isProficientWith(e));
        applyObject(target);
        assertTrue(pc.isProficientWith(e));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.isProficientWith(e));
        assertTrue(reloadedPC.isProficientWith(e));
        remove(o);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.isProficientWith(e));
    }

    @Test
    public void testAutoShieldProfList()
    {
        ShieldProf granted = create(ShieldProf.class, "Granted");
        create(ShieldProf.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new plugin.lsttokens.auto.ShieldProfToken().parseToken(context, target,
                "%LIST");
        new plugin.lsttokens.choose.ShieldProficiencyToken().parseToken(
                context, target, "Granted|Ignored");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        Equipment e = new Equipment();
        e.addToListFor(ListKey.TYPE, Type.SHIELD);
        e.put(ObjectKey.SHIELD_PROF, CDOMDirectSingleRef.getRef(granted));
        assertFalse(pc.isProficientWith(e));
        applyObject(target);
        assertTrue(pc.isProficientWith(e));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.isProficientWith(e));
        assertTrue(reloadedPC.isProficientWith(e));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertFalse(reloadedPC.isProficientWith(e));
        }
    }

    @Test
    public void testAutoArmorProf()
    {
        T target = create(getObjectClass(), "Target");
        ArmorProf granted = create(ArmorProf.class, "Granted");
        create(ArmorProf.class, "Ignored");
        new plugin.lsttokens.auto.ArmorProfToken().parseToken(context, target,
                "Granted");
        Object o = prepare(target);
        finishLoad();
        Equipment e = new Equipment();
        e.addToListFor(ListKey.TYPE, Type.ARMOR);
        e.put(ObjectKey.ARMOR_PROF, CDOMDirectSingleRef.getRef(granted));
        assertFalse(pc.isProficientWith(e));
        applyObject(target);
        assertTrue(pc.isProficientWith(e));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.isProficientWith(e));
        assertTrue(reloadedPC.isProficientWith(e));
        remove(o);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.isProficientWith(e));
    }

    @Test
    public void testAutoArmorProfList()
    {
        T target = create(getObjectClass(), "Target");
        ArmorProf granted = create(ArmorProf.class, "Granted");
        create(ArmorProf.class, "Ignored");
        new plugin.lsttokens.auto.ArmorProfToken().parseToken(context, target,
                "%LIST");
        new plugin.lsttokens.choose.ArmorProficiencyToken().parseToken(context,
                target, "Granted|Ignored");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        Equipment e = new Equipment();
        e.addToListFor(ListKey.TYPE, Type.ARMOR);
        e.put(ObjectKey.ARMOR_PROF, CDOMDirectSingleRef.getRef(granted));
        assertFalse(pc.isProficientWith(e));
        applyObject(target);
        assertTrue(pc.isProficientWith(e));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.isProficientWith(e));
        assertTrue(reloadedPC.isProficientWith(e));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertFalse(reloadedPC.isProficientWith(e));
        }
    }

    @Test
    public void testAutoLanguage()
    {
        T target = create(getObjectClass(), "Target");
        Language granted = create(Language.class, "Granted");
        create(Language.class, "Ignored");
        new plugin.lsttokens.auto.LangToken().parseToken(context, target,
                "Granted");
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasLanguage(granted));
        applyObject(target);
        assertTrue(pc.hasLanguage(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasLanguage(granted));
        assertTrue(reloadedPC.hasLanguage(granted));
        remove(o);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.hasLanguage(granted));
    }

    @Test
    public void testAutoLanguageList()
    {
        T target = create(getObjectClass(), "Target");
        Language granted = create(Language.class, "Granted");
        create(Language.class, "Ignored");
        new plugin.lsttokens.auto.LangToken().parseToken(context, target,
                "%LIST");
        new plugin.lsttokens.choose.LangToken().parseToken(context,
                target, "Granted|Ignored");
        additionalChooseSet(target);
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasLanguage(granted));
        applyObject(target);
        assertTrue(pc.hasLanguage(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasLanguage(granted));
        assertTrue(reloadedPC.hasLanguage(granted));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertFalse(reloadedPC.hasLanguage(granted));
        }
    }

    //TODO We accept this is not symmetric now due to equipment cloning
    //	@Test
    //	public void testAutoEquipment()
    //	{
    //		T target = create(getObjectClass(), "Target");
    //		Equipment granted = create(Equipment.class, "Granted");
    //		create(Equipment.class, "Ignored");
    //		new plugin.lsttokens.auto.EquipToken().parseToken(context, target,
    //			"%LIST");
    //		new plugin.lsttokens.choose.EquipmentToken().parseToken(context,
    //			target, "Granted|Ignored");
    //		Object o = prepare(target);
    //		finishLoad();
    //		assertFalse(pc.getEquipmentMasterList().contains(granted));
    //		applyObject(target);
    //		assertTrue(pc.getEquipmentMasterList().contains(granted));
    //		dumpPC(pc);
    //		runRoundRobin();
    //		assertTrue(pc.getEquipmentMasterList().contains(granted));
    //		assertTrue(reloadedPC.getEquipmentMasterList().contains(granted));
    //		remove(o);
    //		reloadedPC.setDirty(true);
    //		assertFalse(reloadedPC.getEquipmentMasterList().contains(granted));
    //	}

    @Test
    public void testAddLanguage()
    {
        Language granted = create(Language.class, "MyLanguage");
        create(Language.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new plugin.lsttokens.add.LanguageToken().parseToken(context, target,
                "MyLanguage");
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasLanguage(granted));
        applyObject(target);
        assertTrue(pc.hasLanguage(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasLanguage(granted));
        assertTrue(reloadedPC.hasLanguage(granted));
        remove(o);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.hasLanguage(granted));
    }

    @Test
    public void testAddTemplate()
    {
        PCTemplate granted = create(PCTemplate.class, "MyTemplate");
        create(PCTemplate.class, "Ignored");
        T target = create(getObjectClass(), "Target");
        new plugin.lsttokens.add.TemplateToken().parseToken(context, target,
                "MyTemplate");
        Object o = prepare(target);
        finishLoad();
        assertFalse(pc.hasTemplate(granted));
        applyObject(target);
        assertTrue(pc.hasTemplate(granted));
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.hasTemplate(granted));
        assertTrue(reloadedPC.hasTemplate(granted));
        remove(o);
        reloadedPC.setDirty(true);
        if (isSymmetric())
        {
            assertFalse(reloadedPC.hasTemplate(granted));
        }
    }

    @Test
    public void testAddAbilityNormalTarget()
    {
        TokenRegistration.register(plugin.bonustokens.SkillRank.class);
        T target = create(getObjectClass(), "Target");
        Ability abil = BuildUtilities.buildAbility(context, BuildUtilities.getFeatCat(),
                "GrantedAbility");
        new plugin.lsttokens.add.AbilityToken().parseToken(context, target,
                "FEAT|NORMAL|GrantedAbility");
        Skill granted = create(Skill.class, "GrantedSkill");
        create(Skill.class, "IgnoredSkill");
        new plugin.lsttokens.choose.SkillToken().parseToken(context,
                abil, "GrantedSkill|IgnoredSkill");
        new plugin.lsttokens.BonusLst().parseToken(context, abil, "SKILLRANK|%LIST|1");
        abil.put(ObjectKey.MULTIPLE_ALLOWED, true);
        Object o = prepare(target);
        finishLoad();
        MatcherAssert.assertThat((double) SkillRankControl.getTotalRank(pc, granted), closeTo(0.0f, 0.1));
        applyObject(target);
        pc.setDirty(true);
        MatcherAssert.assertThat((double) SkillRankControl.getTotalRank(pc, granted), closeTo(1.0f, 0.1));
        runRoundRobin(getPreEqualityCleanup());
        MatcherAssert.assertThat((double) SkillRankControl.getTotalRank(pc, granted), closeTo(1.0f, 0.1));
        MatcherAssert.assertThat((double) SkillRankControl.getTotalRank(reloadedPC, granted), closeTo(1.0f, 0.1));
        remove(o);
        reloadedPC.setDirty(true);
        //This fails (see CODE-2387)
        //assertEquals(0.0f, SkillRankControl.getTotalRank(reloadedPC, granted));
    }

    @Test
    public void testAddAbilityVirtualTarget()
    {
        TokenRegistration.register(plugin.bonustokens.SkillRank.class);
        T target = create(getObjectClass(), "Target");
        Ability abil = BuildUtilities.buildAbility(context, BuildUtilities.getFeatCat(),
                "GrantedAbility");
        new plugin.lsttokens.add.AbilityToken().parseToken(context, target,
                "FEAT|VIRTUAL|GrantedAbility");
        Skill granted = create(Skill.class, "GrantedSkill");
        create(Skill.class, "IgnoredSkill");
        new plugin.lsttokens.choose.SkillToken().parseToken(context,
                abil, "GrantedSkill|IgnoredSkill");
        new plugin.lsttokens.BonusLst().parseToken(context, abil, "SKILLRANK|%LIST|1");
        abil.put(ObjectKey.MULTIPLE_ALLOWED, true);
        Object o = prepare(target);
        finishLoad();
        assertEquals(0.0, SkillRankControl.getTotalRank(pc, granted), 0.1);
        applyObject(target);
        pc.setDirty(true);
        pc.calcActiveBonuses();
        assertEquals(1.0, SkillRankControl.getTotalRank(pc, granted), 0.1);
        runRoundRobin(getPreEqualityCleanup());
        assertEquals(1.0, SkillRankControl.getTotalRank(pc, granted), 0.1);
        assertEquals(1.0, SkillRankControl.getTotalRank(reloadedPC, granted), 0.1);
        remove(o);
        reloadedPC.setDirty(true);
        //This fails (see CODE-2387)
        //assertEquals(0.0f, SkillRankControl.getTotalRank(reloadedPC, granted));
    }

    //Fails due to issues highlighted in CODE-2283
    //	@Test
    //	public void testAddSkill()
    //	{
    //		PCClass myclass = create(PCClass.class, "SomeClass");
    //		Skill granted = create(Skill.class, "GrantedSkill");
    //		create(Skill.class, "Ignored");
    //		T target = create(getObjectClass(), "Target");
    //		new plugin.lsttokens.add.SkillToken().parseToken(context, target,
    //				"GrantedSkill");
    //		Object o = prepare(target);
    //		finishLoad();
    //		pc.incrementClassLevel(1, myclass);
    //		assertNull(pc.getSkillRankForClass(granted, null));
    //		applyObject(target);
    //		assertEquals(1.0, pc.getSkillRankForClass(granted, null));
    //		runRoundRobin();
    //		assertEquals(1.0, pc.getSkillRankForClass(granted, null));
    //		assertEquals(1.0, reloadedPC.getSkillRankForClass(granted, null));
    //		remove(o);
    //		reloadedPC.setDirty(true);
    //		assertNull(pc.getSkillRankForClass(granted, null));
    //	}

    protected Runnable getPreEqualityCleanup()
    {
        return null;
    }

}
