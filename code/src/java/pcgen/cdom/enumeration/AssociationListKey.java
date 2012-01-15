/*
 * Copyright 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision: 513 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006-03-29 12:17:43 -0500 (Wed, 29 Mar 2006) $
 */
package pcgen.cdom.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.helper.SpellLevel;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
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
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

/**
 * @author Tom Parker <thpr@users.sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal Characteristics of an Association. It
 * is designed to act as an index to a specific Objects within an item that
 * forms associations.
 * 
 * @see pcgen.cdom.base.AssociatedObject
 * 
 * AssociationKeys are designed to store items in an AssociatedObject in a
 * type-safe fashion. Note that it is possible to use the AssociationKey to cast
 * the object to the type of object stored by the AssociationKey. (This assists
 * with Generics)
 * 
 * @param <T>
 *            The class of object stored by this AssociationKey.
 */
public final class AssociationListKey<T>
{

	public static final AssociationListKey<FixedStringList> CHOICES = new AssociationListKey<FixedStringList>();

	public static final AssociationListKey<Object> ADD = new AssociationListKey<Object>();

	public static final AssociationListKey<NamedValue> SKILL_RANK = new AssociationListKey<NamedValue>();

	public static final AssociationListKey<ClassSkillList> CLASSSKILLLIST = new AssociationListKey<ClassSkillList>();

	public static final AssociationListKey<CDOMListObject<Spell>> CLASSSPELLLIST = new AssociationListKey<CDOMListObject<Spell>>();

	public static final AssociationListKey<Ability> ADDED_FEAT = new AssociationListKey<Ability>();

	public static final AssociationListKey<CDOMList<Spell>> SPELL_LIST_CACHE = new AssociationListKey<CDOMList<Spell>>();

	public static final AssociationListKey<AbilitySelection> TEMPLATE_FEAT = new AssociationListKey<AbilitySelection>();

	public static final AssociationListKey<SpecialAbility> SPECIAL_ABILITY = new AssociationListKey<SpecialAbility>();

	public static final AssociationListKey<PCTemplate> CHOOSE_TEMPLATE = new AssociationListKey<PCTemplate>();

	public static final AssociationListKey<PCStat> CHOOSE_PCSTAT = new AssociationListKey<PCStat>();

	public static final AssociationListKey<PCCheck> CHOOSE_PCCHECK = new AssociationListKey<PCCheck>();

	public static final AssociationListKey<SizeAdjustment> CHOOSE_SIZEADJUSTMENT = new AssociationListKey<SizeAdjustment>();

	public static final AssociationListKey<PCAlignment> CHOOSE_PCALIGNMENT = new AssociationListKey<PCAlignment>();

	public static final AssociationListKey<Language> CHOOSE_LANGAUGE = new AssociationListKey<Language>();

	public static final AssociationListKey<Equipment> CHOOSE_EQUIPMENT = new AssociationListKey<Equipment>();

	public static final AssociationListKey<ArmorProf> CHOOSE_ARMORPROFICIENCY = new AssociationListKey<ArmorProf>();

	public static final AssociationListKey<ShieldProf> CHOOSE_SHIELDPROFICIENCY = new AssociationListKey<ShieldProf>();

	public static final AssociationListKey<WeaponProf> CHOOSE_WEAPONPROFICIENCY = new AssociationListKey<WeaponProf>();

	public static final AssociationListKey<Ability> CHOOSE_FEAT = new AssociationListKey<Ability>();

	public static final AssociationListKey<Deity> CHOOSE_DEITY = new AssociationListKey<Deity>();

	public static final AssociationListKey<Skill> CHOOSE_SKILL = new AssociationListKey<Skill>();

	public static final AssociationListKey<Domain> CHOOSE_DOMAIN = new AssociationListKey<Domain>();

	public static final AssociationListKey<Race> CHOOSE_RACE = new AssociationListKey<Race>();

	public static final AssociationListKey<PCClass> CHOOSE_CLASS = new AssociationListKey<PCClass>();

	public static final AssociationListKey<Ability> CHOOSE_ABILITY = new AssociationListKey<Ability>();

	public static final AssociationListKey<SpellSchool> CHOOSE_SCHOOL = new AssociationListKey<SpellSchool>();

	public static final AssociationListKey<String> CHOOSE_STRING = new AssociationListKey<String>();

	public static final AssociationListKey<Spell> CHOOSE_SPELL = new AssociationListKey<Spell>();

	public static final AssociationListKey<SpellLevel> CHOOSE_SPELLLEVEL = new AssociationListKey<SpellLevel>();

	public static final AssociationListKey<String> CHOOSE_NOCHOICE = new AssociationListKey<String>();

	public static final AssociationListKey<AbilitySelection> CHOOSE_FEATSELECTION = new AssociationListKey<AbilitySelection>();

	private static CaseInsensitiveMap<AssociationListKey<?>> map = null;

	private AssociationListKey()
	{
		// Only allow instantation here
	}

	public T cast(Object obj)
	{
		return (T) obj;
	}

	public static <OT> AssociationListKey<OT> getKeyFor(Class<OT> keyClass,
			String keyName)
	{
		if (map == null)
		{
			buildMap();
		}
		/*
		 * CONSIDER This is actually not type safe, there is a case of asking
		 * for a String a second time with a different Class that ObjectKey
		 * currently does not handle. Two solutions: One, store this in a
		 * Two-Key map and allow a String to map to more than one ObjectKey
		 * given different output types (considered confusing) or Two, store the
		 * Class and validate that with a an error message if a different class
		 * is requested.
		 */
		AssociationListKey<OT> key = (AssociationListKey<OT>) map.get(keyName);
		if (key == null)
		{
			key = new AssociationListKey<OT>();
			map.put(keyName, key);
		}
		return key;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<AssociationListKey<?>>();
		Field[] fields = AssociationListKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object obj = fields[i].get(null);
					if (obj instanceof AssociationListKey)
					{
						map.put(fields[i].getName(),
								(AssociationListKey<?>) obj);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new UnreachableError(e);
				}
				catch (IllegalAccessException e)
				{
					throw new UnreachableError(e);
				}
			}
		}
	}

	@Override
	public String toString()
	{
		if (map == null)
		{
			buildMap();
		}
		/*
		 * CONSIDER Should this find a way to do a Two-Way Map or something to
		 * that effect?
		 */
		for (Map.Entry<?, AssociationListKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}
}
