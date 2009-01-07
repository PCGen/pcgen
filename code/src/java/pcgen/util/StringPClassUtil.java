package pcgen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;

public class StringPClassUtil
{

	private static Map<String, Class<? extends CDOMObject>> classMap;
	private static Map<Class<? extends CDOMObject>, String> stringMap;

	static
	{
		classMap = new HashMap<String, Class<? extends CDOMObject>>();
		stringMap = new HashMap<Class<? extends CDOMObject>, String>();

		classMap.put("ABILITY", Ability.class);
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
		classMap.put("TEMPLATE", PCTemplate.class);
		classMap.put("WEAPONPROF", WeaponProf.class);
		classMap.put("CLASSSPELLLIST", ClassSpellList.class);
		classMap.put("CLASSSKILLLIST", ClassSkillList.class);
		classMap.put("DOMAINSPELLLIST", DomainSpellList.class);

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
		stringMap.put(PCTemplate.class, "TEMPLATE");
		stringMap.put(WeaponProf.class, "WEAPONPROF");
		stringMap.put(ClassSpellList.class, "CLASSSPELLLIST");
		stringMap.put(ClassSkillList.class, "CLASSSKILLLIST");
		stringMap.put(DomainSpellList.class, "DOMAINSPELLLIST");

		// Hacks for ServesAs
		stringMap.put(SubClass.class, "CLASS");
		stringMap.put(SubstitutionClass.class, "CLASS");
	}

	public static Class<? extends CDOMObject> getClassFor(String key)
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

	public static <T extends CDOMObject & CategorizedCDOMObject<T>> Category<T> getCategoryFor(
			Class<T> cl, String s)
	{
		if (cl.equals(Ability.class))
		{
			return (Category) SettingsHandler.getGame()
					.silentlyGetAbilityCategory(s);
		}
		else
		{
			return null;
		}
	}

}
