/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

package pcgen.util;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.WeaponProf;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.WieldCategory;
import pcgen.core.spell.Spell;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class StringPClassUtil
{

    private static final Map<String, Class<? extends Loadable>> baseMap;
    private static final Map<String, Class<? extends Loadable>> classMap;
    private static final Map<Class<? extends Loadable>, String> stringMap;

    static
    {
        baseMap = new HashMap<>();
        classMap = new HashMap<>();
        stringMap = new HashMap<>();

        baseMap.put("ALIGNMENT", PCAlignment.class);
        baseMap.put("CHECK", PCCheck.class);
        baseMap.put("DEITY", Deity.class);
        baseMap.put("DOMAIN", Domain.class);
        baseMap.put("CLASS", PCClass.class);
        baseMap.put("LANGUAGE", Language.class);
        baseMap.put("RACE", Race.class);
        baseMap.put("SPELL", Spell.class);
        baseMap.put("SKILL", Skill.class);
        baseMap.put("STAT", PCStat.class);
        baseMap.put("SIZEADJUSTMENT", SizeAdjustment.class);
        baseMap.put("TEMPLATE", PCTemplate.class);
        baseMap.put("WEAPONPROF", WeaponProf.class);
        baseMap.put("ARMORPROF", ArmorProf.class);
        baseMap.put("SHIELDPROF", ShieldProf.class);
        baseMap.put("CLASSSPELLLIST", ClassSpellList.class);
        baseMap.put("CLASSSKILLLIST", ClassSkillList.class);
        baseMap.put("DOMAINSPELLLIST", DomainSpellList.class);
        baseMap.put("WIELDCATEGORY", WieldCategory.class);

        classMap.putAll(baseMap);
        classMap.put("COMPANIONMOD", CompanionMod.class);
        classMap.put("EQUIPMENT", Equipment.class);
        classMap.put("EQMOD", EquipmentModifier.class);
        classMap.put("KIT", Kit.class);
        classMap.put("ABILITY", Ability.class);

        stringMap.put(PCAlignment.class, "ALIGNMENT");
        stringMap.put(PCCheck.class, "CHECK");
        stringMap.put(CompanionMod.class, "COMPANIONMOD");
        stringMap.put(Deity.class, "DEITY");
        stringMap.put(Domain.class, "DOMAIN");
        stringMap.put(Equipment.class, "EQUIPMENT");
        stringMap.put(EquipmentModifier.class, "EQMOD");
        stringMap.put(Ability.class, "ABILITY");
        stringMap.put(PCClass.class, "CLASS");
        stringMap.put(Kit.class, "KIT");
        stringMap.put(Language.class, "LANGUAGE");
        stringMap.put(Race.class, "RACE");
        stringMap.put(Spell.class, "SPELL");
        stringMap.put(Skill.class, "SKILL");
        stringMap.put(PCStat.class, "STAT");
        stringMap.put(SizeAdjustment.class, "SIZEADJUSTMENT");
        stringMap.put(PCTemplate.class, "TEMPLATE");
        stringMap.put(WeaponProf.class, "WEAPONPROF");
        stringMap.put(ArmorProf.class, "ARMORPROF");
        stringMap.put(ShieldProf.class, "SHIELDPROF");
        stringMap.put(ClassSpellList.class, "CLASSSPELLLIST");
        stringMap.put(ClassSkillList.class, "CLASSSKILLLIST");
        stringMap.put(DomainSpellList.class, "DOMAINSPELLLIST");
        stringMap.put(WieldCategory.class, "WIELDCATEGORY");

        // Hacks for ServesAs
        stringMap.put(SubClass.class, "CLASS");
        stringMap.put(SubstitutionClass.class, "CLASS");
    }

    private StringPClassUtil()
    {
    }

    public static Class<? extends Loadable> getClassFor(String key)
    {
        return classMap.get(key);
    }

    public static Set<String> getValidStrings()
    {
        return classMap.keySet();
    }

    public static String getStringFor(Class<?> cl)
    {
        return stringMap.get(cl);
    }

    /**
     * Returns a Collection of the "Base" Classes in StringPClassUtil. These are the
     * "basic" CDOMObject classes that are simple to convert/unconvert and not
     * significantly dependent on other items (e.g. no Ability, no EquipmentModifier,
     * etc.)
     *
     * @return A Collection of the "Base" Classes in StringPClassUtil.
     */
    public static Collection<Class<? extends Loadable>> getBaseClasses()
    {
        return Collections.unmodifiableCollection(baseMap.values());
    }
}
