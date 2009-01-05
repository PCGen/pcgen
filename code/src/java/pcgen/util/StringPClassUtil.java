package pcgen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;

public class StringPClassUtil
{

	private static Map<String, Class<? extends PObject>> classMap;
	private static Map<Class<? extends PObject>, String> stringMap;

	static
	{
		classMap = new HashMap<String, Class<? extends PObject>>();
		stringMap = new HashMap<Class<? extends PObject>, String>();

		classMap.put("ABILITY", Ability.class);
		classMap.put("DEITY", Deity.class);
		classMap.put("DOMAIN", Domain.class);
		classMap.put("EQUIPMENT", Equipment.class);
		classMap.put("EQMOD", EquipmentModifier.class);
		classMap.put("CLASS", PCClass.class);
		classMap.put("LANGUAGE", Language.class);
		classMap.put("RACE", Race.class);
		classMap.put("SPELL", Spell.class);
		classMap.put("SKILL", Skill.class);
		classMap.put("TEMPLATE", PCTemplate.class);
		classMap.put("WEAPONPROF", WeaponProf.class);

		stringMap.put(Deity.class, "DEITY");
		stringMap.put(Domain.class, "DOMAIN");
		stringMap.put(Equipment.class, "EQUIPMENT");
		stringMap.put(EquipmentModifier.class, "EQMOD");
		stringMap.put(Ability.class, "ABILITY");
		stringMap.put(PCClass.class, "CLASS");
		stringMap.put(Language.class, "LANGUAGE");
		stringMap.put(Race.class, "RACE");
		stringMap.put(Spell.class, "SPELL");
		stringMap.put(Skill.class, "SKILL");
		stringMap.put(PCTemplate.class, "TEMPLATE");
		stringMap.put(WeaponProf.class, "WEAPONPROF");

		// Hacks for ServesAs
		stringMap.put(SubClass.class, "CLASS");
		stringMap.put(SubstitutionClass.class, "CLASS");
	}

	public static Class<? extends PObject> getClassFor(String key)
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
