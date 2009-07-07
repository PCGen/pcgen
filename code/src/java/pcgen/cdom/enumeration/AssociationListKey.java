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

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpellProhibitor;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.BonusObj;
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

	public static final AssociationListKey<WeaponProf> WEAPONPROF = new AssociationListKey<WeaponProf>();

	public static final AssociationListKey<Equipment> EQUIPMENT = new AssociationListKey<Equipment>();

	public static final AssociationListKey<ProfProvider<ArmorProf>> ARMORPROF = new AssociationListKey<ProfProvider<ArmorProf>>();

	public static final AssociationListKey<ProfProvider<ShieldProf>> SHIELDPROF = new AssociationListKey<ProfProvider<ShieldProf>>();

	public static final AssociationListKey<Skill> CCSKILL = new AssociationListKey<Skill>();

	public static final AssociationListKey<Skill> CSKILL = new AssociationListKey<Skill>();

	public static final AssociationListKey<Skill> MONCSKILL = new AssociationListKey<Skill>();

	public static final AssociationListKey<BonusObj> BONUS = new AssociationListKey<BonusObj>();

	public static final AssociationListKey<SpellProhibitor> PROHIBITED_SCHOOLS =
			new AssociationListKey<SpellProhibitor>();

	public static final AssociationListKey<Ability> ADDED_FEAT = new AssociationListKey<Ability>();

	public static final AssociationListKey<CDOMList<Spell>> SPELL_LIST_CACHE = new AssociationListKey<CDOMList<Spell>>();

	public static final AssociationListKey<CharacterSpell> CHARACTER_SPELLS =
			new AssociationListKey<CharacterSpell>();

	public static final AssociationListKey<PCClass> FAVCLASS =
			new AssociationListKey<PCClass>();

	public static final AssociationListKey<String> TEMPLATE_FEAT = new AssociationListKey<String>();

	public static final AssociationListKey<String> SELECTED_WEAPON_PROF_BONUS = new AssociationListKey<String>();

	public static final AssociationListKey<SpecialAbility> SPECIAL_ABILITY = new AssociationListKey<SpecialAbility>();

	public static final AssociationListKey<PCTemplate> CHOOSE_TEMPLATE = new AssociationListKey<PCTemplate>();

	public static final AssociationListKey<PCStat> CHOOSE_PCSTAT = new AssociationListKey<PCStat>();

	public static final AssociationListKey<PCCheck> CHOOSE_PCCHECK = new AssociationListKey<PCCheck>();

	public static final AssociationListKey<SizeAdjustment> CHOOSE_SIZEADJUSTMENT = new AssociationListKey<SizeAdjustment>();
	
	private static CaseInsensitiveMap<AssociationListKey<?>> map = null;

	private AssociationListKey()
	{
		// Only allow instantation here
	}

	public T cast(Object o)
	{
		return (T) o;
	}

	public static <OT> AssociationListKey<OT> getKeyFor(Class<OT> c, String s)
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
		AssociationListKey<OT> o = (AssociationListKey<OT>) map.get(s);
		if (o == null)
		{
			o = new AssociationListKey<OT>();
			map.put(s, o);
		}
		return o;
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
					Object o = fields[i].get(null);
					if (o instanceof AssociationListKey)
					{
						map.put(fields[i].getName(), (AssociationListKey<?>) o);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new InternalError();
				}
				catch (IllegalAccessException e)
				{
					throw new InternalError();
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
