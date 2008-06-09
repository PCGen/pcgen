/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.content.TransitionChoice;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.ArmorProf;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.core.character.WieldCategory;
import pcgen.core.spell.Spell;
import pcgen.util.enumeration.Visibility;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 */
public final class ObjectKey<T>
{

	/*
	 * TODO Should ObjectKey take in the Class in order to be able to cast to
	 * the given class?
	 * 
	 * have a .cast(Object o) method on ObjectKey???
	 */
	public static final ObjectKey<Boolean> USE_UNTRAINED = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<Boolean> EXCLUSIVE = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<URI> SOURCE_URI = new ObjectKey<URI>(null);

	public static final ObjectKey<PCAlignment> ALIGNMENT = new ObjectKey<PCAlignment>(null);

	public static final ObjectKey<Boolean> READ_ONLY = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<PCStat> KEY_STAT = new ObjectKey<PCStat>(null);

	public static final ObjectKey<SkillArmorCheck> ARMOR_CHECK = new ObjectKey<SkillArmorCheck>(SkillArmorCheck.NONE);

	public static final ObjectKey<Visibility> VISIBILITY = new ObjectKey<Visibility>(Visibility.DEFAULT);

	public static final ObjectKey<Boolean> REMOVABLE = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<SubRegion> SUBREGION = new ObjectKey<SubRegion>(null);

	public static final ObjectKey<Region> REGION = new ObjectKey<Region>(null);

	public static final ObjectKey<Boolean> USETEMPLATENAMEFORSUBREGION = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Boolean> USETEMPLATENAMEFORREGION = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Gender> GENDER_LOCK = new ObjectKey<Gender>(null);

	public static final ObjectKey<BigDecimal> FACE_WIDTH = new ObjectKey<BigDecimal>(null);

	public static final ObjectKey<BigDecimal> FACE_HEIGHT = new ObjectKey<BigDecimal>(null);

	public static final ObjectKey<Boolean> USETEMPLATENAMEFORSUBRACE = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<SubRace> SUBRACE = new ObjectKey<SubRace>(null);

	public static final ObjectKey<BigDecimal> CR_MODIFIER = new ObjectKey<BigDecimal>(BigDecimal.ZERO);

	public static final ObjectKey<RaceType> RACETYPE = new ObjectKey<RaceType>(null);

	public static final ObjectKey<BigDecimal> COST = new ObjectKey<BigDecimal>(BigDecimal.ZERO);

	public static final ObjectKey<PCStat> SPELL_STAT = new ObjectKey<PCStat>(null);

	public static final ObjectKey<Boolean> COST_DOUBLE = new ObjectKey<Boolean>(null);

	public static final ObjectKey<Boolean> ASSIGN_TO_ALL = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<EqModNameOpt> NAME_OPT = new ObjectKey<EqModNameOpt>(EqModNameOpt.NORMAL);

	public static final ObjectKey<EqModFormatCat> FORMAT = new ObjectKey<EqModFormatCat>(EqModFormatCat.PARENS);

	public static final ObjectKey<Boolean> ATTACKS_PROGRESS = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<WieldCategory> WIELD = new ObjectKey<WieldCategory>(null);

	public static final ObjectKey<BigDecimal> WEIGHT = new ObjectKey<BigDecimal>(BigDecimal.ZERO);

	public static final ObjectKey<BigDecimal> WEIGHT_MOD = new ObjectKey<BigDecimal>(BigDecimal.ZERO);

	public static final ObjectKey<CDOMSingleRef<WeaponProf>> WEAPON_PROF = new ObjectKey<CDOMSingleRef<WeaponProf>>(null);

	public static final ObjectKey<CDOMSingleRef<ArmorProf>> ARMOR_PROF = new ObjectKey<CDOMSingleRef<ArmorProf>>(null);

	public static final ObjectKey<CDOMSingleRef<ShieldProf>> SHIELD_PROF = new ObjectKey<CDOMSingleRef<ShieldProf>>(null);

	public static final ObjectKey<EqModControl> MOD_CONTROL = new ObjectKey<EqModControl>(EqModControl.YES);

	public static final ObjectKey<BigDecimal> CURRENT_COST = new ObjectKey<BigDecimal>(null);

	/*
	 * This MUST Stay Object! Otherwise the code hierarchy ends up with circular
	 * references/tangles
	 */
	public static final ObjectKey<Object> PARENT = new ObjectKey<Object>(null);

	public static final ObjectKey<Modifier<HitDie>> HITDIE = new ObjectKey<Modifier<HitDie>>(null);

	public static final ObjectKey<ChallengeRating> CHALLENGE_RATING = new ObjectKey<ChallengeRating>(ChallengeRating.ZERO);

	public static final ObjectKey<Boolean> USE_SPELL_SPELL_STAT = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Boolean> CASTER_WITHOUT_SPELL_STAT = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Boolean> SPELLBOOK = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Boolean> MOD_TO_SKILLS = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<Boolean> MEMORIZE_SPELLS = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<Boolean> IS_MONSTER = new ObjectKey<Boolean>(null);

	public static final ObjectKey<Boolean> ALLOWBASECLASS = new ObjectKey<Boolean>(Boolean.TRUE);

	public static final ObjectKey<Boolean> HAS_BONUS_SPELL_STAT = new ObjectKey<Boolean>(null);

	public static final ObjectKey<PCStat> BONUS_SPELL_STAT = new ObjectKey<PCStat>(null);

	public static final ObjectKey<HitDie> LEVEL_HITDIE = new ObjectKey<HitDie>(HitDie.ZERO);

	public static final ObjectKey<ClassSpellList> CLASS_SPELLLIST = new ObjectKey<ClassSpellList>(null);

	public static final ObjectKey<DomainSpellList> DOMAIN_SPELLLIST = new ObjectKey<DomainSpellList>(null);

	public static final ObjectKey<TransitionChoice<CDOMListObject<Spell>>> SPELLLIST_CHOICE = new ObjectKey<TransitionChoice<CDOMListObject<Spell>>>(null);

	public static final ObjectKey<TransitionChoice<ClassSkillList>> SKILLLIST_CHOICE = new ObjectKey<TransitionChoice<ClassSkillList>>(null);

	public static final ObjectKey<Boolean> STACKS = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<Boolean> MULTIPLE_ALLOWED = new ObjectKey<Boolean>(Boolean.FALSE);

	public static final ObjectKey<BigDecimal> SELECTION_COST = new ObjectKey<BigDecimal>(BigDecimal.ONE);;

	private static CaseInsensitiveMap<ObjectKey<?>> map = null;

	private final T defaultValue;
	
	private ObjectKey(T def)
	{
		defaultValue = def;
	}

	public T getDefault()
	{
		return defaultValue;
	}

	public T cast(Object o)
	{
		return (T) o;
	}

	public static <OT> ObjectKey<OT> getKeyFor(Class<OT> c, String s)
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
		ObjectKey<OT> o = (ObjectKey<OT>) map.get(s);
		if (o == null)
		{
			o = new ObjectKey<OT>(null);
			map.put(s, o);
		}
		return o;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<ObjectKey<?>>();
		Field[] fields = ObjectKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (java.lang.reflect.Modifier.isStatic(mod)
					&& java.lang.reflect.Modifier.isFinal(mod)
					&& java.lang.reflect.Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof ObjectKey)
					{
						map.put(fields[i].getName(), (ObjectKey<?>) o);
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
		/*
		 * CONSIDER Should this find a way to do a Two-Way Map or something to
		 * that effect?
		 */
		if (map == null)
		{
			buildMap();
		}
		for (Map.Entry<?, ObjectKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static Collection<ObjectKey<?>> getAllConstants()
	{
		if (map == null)
		{
			buildMap();
		}
		return new HashSet<ObjectKey<?>>(map.values());
	}
}
