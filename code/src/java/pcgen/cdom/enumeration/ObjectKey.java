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
 */
package pcgen.cdom.enumeration;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.content.Processor;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.QualifiedObject;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;

/**
 *
 * This is a Typesafe enumeration of legal Object Characteristics of an object.
 * It is designed to act as an index to a specific Objects within a
 * CDOMObject.
 *
 * ObjectKeys are designed to store items in a CDOMObject in a type-safe
 * fashion. Note that it is possible to use the ObjectKey to cast the object to
 * the type of object stored by the ObjectKey. (This assists with Generics)
 *
 * A "default value" (may be null) must be provided at object construction (the
 * default is provided when getSafe(ObjectKey) is called in CDOMObject). This
 * default value is especially useful for Boolean ObjectKeys.
 *
 * @param <T>
 *            The class of object stored by this ObjectKey.
 */
@SuppressWarnings({"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal", "checkstyle:FinalClass"})
public class ObjectKey<T>
{

	private static CaseInsensitiveMap<ObjectKey<?>> map = null;

	public static final ObjectKey<Boolean> USE_UNTRAINED = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> EXCLUSIVE = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<CDOMSingleRef<PCAlignment>> ALIGNMENT = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> READ_ONLY = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<CDOMSingleRef<PCStat>> KEY_STAT = new ObjectKey<>(null);

	public static final ObjectKey<SkillArmorCheck> ARMOR_CHECK = new ObjectKey<>(SkillArmorCheck.NONE);

	public static final ObjectKey<Visibility> VISIBILITY = new ObjectKey<>(Visibility.DEFAULT);

	public static final ObjectKey<Boolean> REMOVABLE = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Region> REGION = new ObjectKey<>(null);

	public static final ObjectKey<Gender> GENDER_LOCK = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> USETEMPLATENAMEFORSUBRACE = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<SubRace> SUBRACE = new ObjectKey<>(null);

	public static final ObjectKey<BigDecimal> CR_MODIFIER = new ObjectKey<>(BigDecimal.ZERO);

	public static final ObjectKey<RaceType> RACETYPE = new ObjectKey<>(null);

	public static final ObjectKey<BigDecimal> COST = new ObjectKey<>(BigDecimal.ZERO);

	public static final ObjectKey<CDOMSingleRef<PCStat>> SPELL_STAT = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> COST_DOUBLE = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> ASSIGN_TO_ALL = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<EqModNameOpt> NAME_OPT = new ObjectKey<>(EqModNameOpt.NORMAL);

	public static final ObjectKey<EqModFormatCat> FORMAT = new ObjectKey<>(EqModFormatCat.PARENS);

	public static final ObjectKey<Boolean> ATTACKS_PROGRESS = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<WieldCategory> WIELD = new ObjectKey<>(null);

	public static final ObjectKey<BigDecimal> WEIGHT = new ObjectKey<>(BigDecimal.ZERO);

	public static final ObjectKey<BigDecimal> WEIGHT_MOD = new ObjectKey<>(BigDecimal.ZERO);

	public static final ObjectKey<CDOMSingleRef<WeaponProf>> WEAPON_PROF = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<ArmorProf>> ARMOR_PROF = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<ShieldProf>> SHIELD_PROF = new ObjectKey<>(null);

	public static final ObjectKey<EqModControl> MOD_CONTROL = new ObjectKey<>(EqModControl.YES);

	public static final ObjectKey<BigDecimal> CURRENT_COST = new ObjectKey<>(null);

	/*
	 * This MUST Stay Object! Otherwise the code hierarchy ends up with circular
	 * references/tangles
	 */
	public static final ObjectKey<Object> PARENT = new ObjectKey<>(null);

	public static final ObjectKey<Object> TOKEN_PARENT = new ObjectKey<>(null);

	public static final ObjectKey<Processor<HitDie>> HITDIE = new ObjectKey<>(null);

	public static final ObjectKey<ChallengeRating> CHALLENGE_RATING = new ObjectKey<>(ChallengeRating.ZERO);

	public static final ObjectKey<Boolean> USE_SPELL_SPELL_STAT = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> CASTER_WITHOUT_SPELL_STAT = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> SPELLBOOK = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> MOD_TO_SKILLS = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> MEMORIZE_SPELLS = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> IS_MONSTER = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> ALLOWBASECLASS = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> HAS_BONUS_SPELL_STAT = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<PCStat>> BONUS_SPELL_STAT = new ObjectKey<>(null);

	public static final ObjectKey<HitDie> LEVEL_HITDIE = new ObjectKey<>(HitDie.ZERO);

	public static final ObjectKey<ClassSpellList> CLASS_SPELLLIST = new ObjectKey<>(null);

	public static final ObjectKey<DomainSpellList> DOMAIN_SPELLLIST = new ObjectKey<>(null);

	public static final ObjectKey<TransitionChoice<CDOMListObject<Spell>>> SPELLLIST_CHOICE = new ObjectKey<>(null);

	public static final ObjectKey<TransitionChoice<ClassSkillList>> SKILLLIST_CHOICE = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> STACKS = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> MULTIPLE_ALLOWED = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<BigDecimal> SELECTION_COST = new ObjectKey<>(BigDecimal.ONE);

	public static final ObjectKey<Boolean> NAME_PI = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> DESC_PI = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Category<Ability>> ABILITY_CAT = new ObjectKey<>(null);

	public static final ObjectKey<Load> UNENCUMBERED_LOAD = new ObjectKey<>(Load.LIGHT);

	public static final ObjectKey<Load> UNENCUMBERED_ARMOR = new ObjectKey<>(Load.LIGHT);

	public static final ObjectKey<Boolean> ANY_FAVORED_CLASS = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<LevelCommandFactory> MONSTER_CLASS = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<Equipment>> BASE_ITEM = new ObjectKey<>(null);

	public static final ObjectKey<LevelExchange> EXCHANGE_LEVEL = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<PCClass>> EX_CLASS = new ObjectKey<>(null);

	public static final ObjectKey<SpellResistance> SR = new ObjectKey<>(SpellResistance.NONE);

	public static final ObjectKey<QualifiedObject<Boolean>> HAS_DEITY_WEAPONPROF =
			new ObjectKey<>(new QualifiedObject<>(Boolean.FALSE));

	public static final ObjectKey<SpellProhibitor> CHOICE = new ObjectKey<>(null);

	public static final ObjectKey<TransitionChoice<CNAbility>> MODIFY_CHOICE = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> CONTAINER_CONSTANT_WEIGHT = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<BigDecimal> CONTAINER_WEIGHT_CAPACITY = new ObjectKey<>(null);

	public static final ObjectKey<Capacity> TOTAL_CAPACITY = new ObjectKey<>(null);

	public static final ObjectKey<Category<SubClass>> SUBCLASS_CATEGORY = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<SizeAdjustment>> BASESIZE;

	public static final ObjectKey<CDOMSingleRef<SizeAdjustment>> SIZE;

	public static final ObjectKey<Boolean> USE_MASTER_SKILL = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> DONTADD_HITDIE = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> DONTADD_SKILLPOINTS = new ObjectKey<>(null);

	public static final ObjectKey<KitApply> APPLY_MODE = new ObjectKey<>(KitApply.PERMANENT);

	public static final ObjectKey<QualifiedObject<Formula>> EQUIP_BUY = new ObjectKey<>(null);

	public static final ObjectKey<QualifiedObject<Formula>> KIT_TOTAL_COST = new ObjectKey<>(null);

	public static final ObjectKey<Date> SOURCE_DATE = new ObjectKey<>(null);

	public static final ObjectKey<Campaign> SOURCE_CAMPAIGN = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> IS_OGL = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> IS_MATURE = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> IS_LICENSED = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> IS_D20 = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> SHOW_IN_MENU = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<PersistentTransitionChoice<Language>> CHOOSE_LANGAUTO = new ObjectKey<>(null);

	public static final ObjectKey<Prerequisite> PRERACETYPE = new ObjectKey<>(null);

	public static final ObjectKey<File> DIRECTORY = new ObjectKey<>(null);

	public static final ObjectKey<File> WRITE_DIRECTORY = new ObjectKey<>(null);

	public static final ObjectKey<GameMode> GAME_MODE = new ObjectKey<>(null);

	public static final ObjectKey<PersistentTransitionChoice<CNAbilitySelection>> TEMPLATE_FEAT = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> VALID_FOR_DEITY = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> VALID_FOR_FOLLOWER = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<Boolean> IS_DEFAULT_SIZE = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<Boolean> ROLLED = new ObjectKey<>(Boolean.TRUE);

	public static final ObjectKey<ChooseInformation<?>> CHOOSE_INFO = new ObjectKey<>(null);

	public static final ObjectKey<Destination> DESTINATION = new ObjectKey<>(null);

	public static final ObjectKey<Boolean> INTERNAL = new ObjectKey<>(Boolean.FALSE);

	public static final ObjectKey<ClassSkillList> CLASS_SKILLLIST = new ObjectKey<>(null);

	public static final ObjectKey<Status> STATUS = new ObjectKey<>(Status.Release);

	public static final ObjectKey<URI> ICON_URI = new ObjectKey<>(null);

	public static final ObjectKey<Category<CompanionMod>> MOD_CATEGORY = new ObjectKey<>(null);

	public static final ObjectKey<CDOMSingleRef<SizeAdjustment>> CUSTOMSIZE = new ObjectKey<>(null);

	/*
	 * TODO Okay, this is a hack.
	 */

	static
	{
		buildMap();
		BASESIZE = new ObjectKey<>(null)
		{
			@Override
			public CDOMSingleRef<SizeAdjustment> getDefault()
			{
				return CDOMDirectSingleRef.getRef(SizeUtilities.getDefaultSizeAdjustment());
			}

		};
		map.put("BASESIZE", BASESIZE);
		SIZE = new ObjectKey<>(null)
		{
			@Override
			public CDOMSingleRef<SizeAdjustment> getDefault()
			{
				return CDOMDirectSingleRef.getRef(SizeUtilities.getDefaultSizeAdjustment());
			}

		};
		map.put("SIZE", SIZE);
	}

	private final T defaultValue;

	private ObjectKey(T def)
	{
		defaultValue = def;
	}

	public T getDefault()
	{
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public T cast(Object obj)
	{
		return (T) obj;
	}

	public static <OT> ObjectKey<OT> getKeyFor(Class<OT> objectClass, String name)
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
		ObjectKey<OT> key = (ObjectKey<OT>) map.get(name);
		if (key == null)
		{
			key = new ObjectKey<>(null);
			map.put(name, key);
		}
		return key;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<>();
		Field[] fields = ObjectKey.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (java.lang.reflect.Modifier.isStatic(mod) && java.lang.reflect.Modifier.isFinal(mod)
                    && java.lang.reflect.Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof ObjectKey)
                    {
                        map.put(field.getName(), (ObjectKey<?>) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
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
		return new HashSet<>(map.values());
	}
}
