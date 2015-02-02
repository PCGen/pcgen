package pcgen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
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
import pcgen.core.spell.Spell;

public class StringPClassUtil
{

	private static Map<String, Class<? extends Loadable>> classMap;
	private static Map<Class<? extends Loadable>, String> stringMap;
	private static Map<String, Class<? extends Category<?>>> catClassMap;

	static
	{
		classMap = new HashMap<String, Class<? extends Loadable>>();
		stringMap = new HashMap<Class<? extends Loadable>, String>();
		catClassMap = new HashMap<String, Class<? extends Category<?>>>();

		classMap.put("ABILITY", Ability.class);
		classMap.put("ALIGNMENT", PCAlignment.class);
		classMap.put("CHECK", PCCheck.class);
		classMap.put("COMPANIONMOD", CompanionMod.class);
		classMap.put("DEITY", Deity.class);
		classMap.put("DOMAIN", Domain.class);
		classMap.put("EQUIPMENT", Equipment.class);
		classMap.put("EQMOD", EquipmentModifier.class);
		classMap.put("CLASS", PCClass.class);
		classMap.put("KIT", Kit.class);
		classMap.put("LANGUAGE", Language.class);
		classMap.put("RACE", Race.class);
		classMap.put("SPELL", Spell.class);
		classMap.put("SKILL", Skill.class);
		classMap.put("STAT", PCStat.class);
		classMap.put("SIZEADJUSTMENT", SizeAdjustment.class);
		classMap.put("TEMPLATE", PCTemplate.class);
		classMap.put("WEAPONPROF", WeaponProf.class);
		classMap.put("ARMORPROF", ArmorProf.class);
		classMap.put("SHIELDPROF", ShieldProf.class);
		classMap.put("CLASSSPELLLIST", ClassSpellList.class);
		classMap.put("CLASSSKILLLIST", ClassSkillList.class);
		classMap.put("DOMAINSPELLLIST", DomainSpellList.class);

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

		// Hacks for ServesAs
		stringMap.put(SubClass.class, "CLASS");
		stringMap.put(SubstitutionClass.class, "CLASS");
		
		catClassMap.put("ABILITY", AbilityCategory.class);
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

	public static Class<? extends Category<?>> getCategoryClassFor(
			String className)
	{
		return catClassMap.get(className);
	}
}
