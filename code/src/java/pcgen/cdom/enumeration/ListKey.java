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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.CampaignURL;
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.helper.FollowerLimit;
import pcgen.cdom.helper.InfoBoolean;
import pcgen.cdom.helper.ShieldProfProvider;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.cdom.inst.Dynamic;
import pcgen.cdom.processor.ChangeArmorType;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.Qualifier;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Description;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.FollowerOption;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.MoveClone;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.SimpleMovement;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpecialProperty;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.Vision;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.EquipBonus;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitStat;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.utils.DeferredLine;

/**
 * 
 * This is a Typesafe enumeration of legal List Characteristics of an object. It
 * is designed to act as an index to a specific Object items within a
 * CDOMObject.
 * 
 * ListKeys are designed to store items in a CDOMObject in a type-safe
 * fashion. Note that it is possible to use the ListKey to cast the object to
 * the type of object stored by the ListKey. (This assists with Generics)
 * 
 * @param <T>
 *            The class of object stored by this ListKey.
 */
public final class ListKey<T>
{

	/** FILE_ABILITY_CATEGORY - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_ABILITY_CATEGORY = new ListKey<>();
	/** FILE_BIO_SET - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_BIO_SET = new ListKey<>();
	/** FILE_CLASS - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_CLASS = new ListKey<>();
	/** FILE_COMPANION_MOD - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_COMPANION_MOD = new ListKey<>();
	/** FILE_COVER - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_COVER = new ListKey<>();
	/** FILE_DEITY - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_DEITY = new ListKey<>();
	/** FILE_DOMAIN - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_DOMAIN = new ListKey<>();
	/** FILE_EQUIP - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_EQUIP = new ListKey<>();
	/** FILE_EQUIP_MOD - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_EQUIP_MOD = new ListKey<>();
	/** FILE_ABILITY - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_ABILITY = new ListKey<>();
	/** FILE_FEAT - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_FEAT = new ListKey<>();
	/** FILE_KIT - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_KIT = new ListKey<>();
	/** FILE_LANGUAGE - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_LANGUAGE = new ListKey<>();
	/** FILE_LST_EXCLUDE - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_LST_EXCLUDE = new ListKey<>();
	/** FILE_PCC - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_PCC = new ListKey<>();
	/** FILE_RACE - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_RACE = new ListKey<>();
	/** FILE_SKILL - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_SKILL = new ListKey<>();
	/** FILE_SPELL - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_SPELL = new ListKey<>();
	/** FILE_TEMPLATE - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_TEMPLATE = new ListKey<>();
	/** FILE_WEAPON_PROF - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_WEAPON_PROF = new ListKey<>();
	/** GAME_MODE - a ListKey */
	public static final ListKey<String> GAME_MODE = new ListKey<>();
	/** LICENSE - a ListKey */
	public static final ListKey<String> LICENSE = new ListKey<>();
	/** LICENSE_FILE - a ListKey */
	public static final ListKey<CampaignSourceEntry> LICENSE_FILE = new ListKey<>();
	/** FILE_LOGO - a ListKey */
	public static final ListKey<CampaignSourceEntry> FILE_LOGO = new ListKey<>();
	/** SECTION 15 - a ListKey */
	public static final ListKey<String> SECTION_15 = new ListKey<>();
	/** INFO_TEXT - a ListKey */
	public static final ListKey<String> INFO_TEXT = new ListKey<>();
	/** TEMP_BONUS - a ListKey */
	public static final ListKey<BonusObj> TEMP_BONUS = new ListKey<>();
	/** BOOK_TYPE - a ListKey */
	public static final ListKey<String> BOOK_TYPE = new ListKey<>();
	//	/** Key for a list of weapon proficiencies */
	//	public static final ListKey<String> WEAPON_PROF = new ListKey<String>();
	public static final ListKey<CampaignSourceEntry> FILE_ARMOR_PROF = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_SHIELD_PROF = new ListKey<>();
	public static final ListKey<CDOMReference<WeaponProf>> DEITYWEAPON = new ListKey<>();
	public static final ListKey<RaceSubType> RACESUBTYPE = new ListKey<>();
	public static final ListKey<RaceSubType> REMOVED_RACESUBTYPE = new ListKey<>();
	public static final ListKey<LevelCommandFactory> ADD_LEVEL = new ListKey<>();
	public static final ListKey<String> RANGE = new ListKey<>();
	public static final ListKey<String> SAVE_INFO = new ListKey<>();
	public static final ListKey<String> DURATION = new ListKey<>();
	public static final ListKey<String> COMPONENTS = new ListKey<>();
	public static final ListKey<String> CASTTIME = new ListKey<>();
	public static final ListKey<String> SPELL_RESISTANCE = new ListKey<>();
	public static final ListKey<String> VARIANTS = new ListKey<>();
	public static final ListKey<SpellSchool> SPELL_SCHOOL = new ListKey<>();
	public static final ListKey<String> SPELL_SUBSCHOOL = new ListKey<>();
	public static final ListKey<String> SPELL_DESCRIPTOR = new ListKey<>();
	public static final ListKey<String> SPELL_CLASSLEVEL = new ListKey<>();
	public static final ListKey<String> SPELL_DOMAINLEVEL = new ListKey<>();
	public static final ListKey<Type> PROHIBITED_ITEM = new ListKey<>();
	public static final ListKey<Type> ITEM = new ListKey<>();
	public static final ListKey<Integer> HITDICE_ADVANCEMENT = new ListKey<>();
	public static final ListKey<Type> ITEM_TYPES = new ListKey<>();
	public static final ListKey<CDOMSingleRef<EquipmentModifier>> REPLACED_KEYS = new ListKey<>();
	public static final ListKey<SpecialProperty> SPECIAL_PROPERTIES = new ListKey<>();
	public static final ListKey<ChangeArmorType> ARMORTYPE = new ListKey<>();
	public static final ListKey<Formula> SPECIALTYKNOWN = new ListKey<>();
	public static final ListKey<Formula> KNOWN = new ListKey<>();
	public static final ListKey<Formula> CAST = new ListKey<>();
	public static final ListKey<QualifiedObject<CDOMSingleRef<Domain>>> DOMAIN = new ListKey<>();
	public static final ListKey<CDOMReference<Deity>> DEITY = new ListKey<>();
	public static final ListKey<KnownSpellIdentifier> KNOWN_SPELLS = new ListKey<>();
	public static final ListKey<SpellProhibitor> SPELL_PROHIBITOR = new ListKey<>();
	public static final ListKey<Description> BENEFIT = new ListKey<>();
	public static final ListKey<PCTemplate> LEVEL_TEMPLATES = new ListKey<>();
	public static final ListKey<PCTemplate> REPEATLEVEL_TEMPLATES = new ListKey<>();
	public static final ListKey<PCTemplate> HD_TEMPLATES = new ListKey<>();
	public static final ListKey<CDOMReference<PCTemplate>> TEMPLATE_CHOOSE = new ListKey<>();
	public static final ListKey<CDOMReference<PCTemplate>> TEMPLATE_ADDCHOICE = new ListKey<>();
	public static final ListKey<CDOMReference<PCTemplate>> TEMPLATE = new ListKey<>();
	public static final ListKey<CDOMReference<PCTemplate>> REMOVE_TEMPLATES = new ListKey<>();
	public static final ListKey<Vision> VISION_CACHE = new ListKey<>();
	public static final ListKey<PersistentTransitionChoice<?>> ADD = new ListKey<>();
	public static final ListKey<CDOMReference<? extends PCClass>> FAVORED_CLASS = new ListKey<>();
	public static final ListKey<Qualifier> QUALIFY = new ListKey<>();
	public static final ListKey<DamageReduction> DAMAGE_REDUCTION = new ListKey<>();
	public static final ListKey<CDOMSingleRef<PCStat>> UNLOCKED_STATS = new ListKey<>();
	public static final ListKey<CDOMSingleRef<PCStat>> NONSTAT_STATS = new ListKey<>();
	public static final ListKey<CDOMSingleRef<PCStat>> NONSTAT_TO_STAT_STATS = new ListKey<>();
	public static final ListKey<StatLock> STAT_LOCKS = new ListKey<>();
	public static final ListKey<StatLock> STAT_MINVALUE = new ListKey<>();
	public static final ListKey<StatLock> STAT_MAXVALUE = new ListKey<>();
	public static final ListKey<TransitionChoice<Kit>> KIT_CHOICE = new ListKey<>();
	public static final ListKey<SimpleMovement> SIMPLEMOVEMENT = new ListKey<>();
	public static final ListKey<MoveClone> MOVEMENTCLONE = new ListKey<>();
	public static final ListKey<SimpleMovement> BASE_MOVEMENT = new ListKey<>();
	public static final ListKey<FollowerOption> COMPANIONLIST = new ListKey<>();
	public static final ListKey<FollowerLimit> FOLLOWERS = new ListKey<>();
	public static final ListKey<Description> DESCRIPTION = new ListKey<>();
	public static final ListKey<ChangeProf> CHANGEPROF = new ListKey<>();
	public static final ListKey<Equipment> NATURAL_WEAPON = new ListKey<>();
	public static final ListKey<SpecialAbility> SAB = new ListKey<>();
	public static final ListKey<SubClass> SUB_CLASS = new ListKey<>();
	public static final ListKey<SubstitutionClass> SUBSTITUTION_CLASS = new ListKey<>();
	public static final ListKey<DeferredLine> SUB_CLASS_LEVEL = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> SERVES_AS_SKILL = new ListKey<>();
	public static final ListKey<CDOMReference<Race>> SERVES_AS_RACE = new ListKey<>();
	public static final ListKey<CDOMReference<PCClass>> SERVES_AS_CLASS = new ListKey<>();
	public static final ListKey<CDOMReference<Ability>> SERVES_AS_ABILITY = new ListKey<>();
	public static final ListKey<ChooseSelectionActor<?>> DF_CHOOSE_ACTOR = new ListKey<>();
	public static final ListKey<ChooseSelectionActor<?>> GVF_CHOOSE_ACTOR = new ListKey<>();
	public static final ListKey<WeaponProfProvider> WEAPONPROF = new ListKey<>();
	public static final ListKey<CDOMSingleRef<WeaponProf>> IMPLIED_WEAPONPROF = new ListKey<>();
	public static final ListKey<QualifiedObject<CDOMReference<Equipment>>> EQUIPMENT = new ListKey<>();
	public static final ListKey<ArmorProfProvider> AUTO_ARMORPROF = new ListKey<>();
	public static final ListKey<ShieldProfProvider> AUTO_SHIELDPROF = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> CCSKILL = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> CSKILL = new ListKey<>();
	public static final ListKey<BonusObj> BONUS = new ListKey<>();
	public static final ListKey<String> UNARMED_DAMAGE = new ListKey<>();
	public static final ListKey<Capacity> CAPACITY = new ListKey<>();
	public static final ListKey<SpellProhibitor> PROHIBITED_SPELLS = new ListKey<>();
	public static final ListKey<String> COMMENT = new ListKey<>();
	public static final ListKey<PersistentTransitionChoice<?>> REMOVE = new ListKey<>();
	public static final ListKey<Type> TYPE = new ListKey<>();
	public static final ListKey<BaseKit> KIT_TASKS = new ListKey<>();
	public static final ListKey<EquipmentModifier> EQMOD = new ListKey<>();
	public static final ListKey<CDOMSingleRef<Race>> APPLIED_RACE = new ListKey<>();
	public static final ListKey<EqModRef> EQMOD_INFO = new ListKey<>();
	public static final ListKey<CampaignURL> CAMPAIGN_URL = new ListKey<>();
	public static final ListKey<Qualifier> FORWARDREF = new ListKey<>();
	public static final ListKey<Campaign> CAMPAIGN = new ListKey<>();

	public static final ListKey<Class<?>> DUPES_ALLOWED = new ListKey<>();
	public static final ListKey<ObjectKey<?>> REMOVED_OBJECTKEY = new ListKey<>();
	public static final ListKey<StringKey> REMOVED_STRINGKEY = new ListKey<>();
	public static final ListKey<IntegerKey> REMOVED_INTEGERKEY = new ListKey<>();
	public static final ListKey<CDOMReference<Ability>> FEAT_TOKEN_LIST = new ListKey<>();
	public static final ListKey<ChooseSelectionActor<?>> NEW_CHOOSE_ACTOR = new ListKey<>();
	public static final ListKey<KitStat> STAT_LIST = new ListKey<>();
	public static final ListKey<ListKey<ChooseSelectionActor<?>>> GA_CAKEYS = new ListKey<>();
	public static final ListKey<String> MONSTER_ROLES = new ListKey<>();

	//These are case sensitive, please do not change them to upper case
	public static final ListKey<String> HIDDEN_Equipment = new ListKey<>();
	public static final ListKey<String> HIDDEN_Ability = new ListKey<>();
	public static final ListKey<String> HIDDEN_Skill = new ListKey<>();
	public static final ListKey<QualifiedObject<CDOMReference<Language>>> AUTO_LANGUAGES = new ListKey<>();
	public static final ListKey<QualifiedObject<CDOMReference<Language>>> AUTO_LANGUAGE = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> CLASS_SKILL = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> LOCALCSKILL = new ListKey<>();
	public static final ListKey<CDOMReference<Skill>> LOCALCCSKILL = new ListKey<>();
	public static final ListKey<BonusObj> BONUS_ANYPC = new ListKey<>();
	public static final ListKey<BonusObj> BONUS_PC = new ListKey<>();
	public static final ListKey<EquipBonus> BONUS_EQUIP = new ListKey<>();
	public static final ListKey<CDOMReference<WeaponProf>> WEAPONBONUS = new ListKey<>();
	public static final ListKey<String> SITUATION = new ListKey<>();
	public static final ListKey<FactKey<?>> REMOVED_FACTKEY = new ListKey<>();
	public static final ListKey<FactSetKey<?>> REMOVED_FACTSETKEY = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_VARIABLE = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_DATACTRL = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_SAVE = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_STAT = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_SIZE = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_ALIGNMENT = new ListKey<>();
	public static final ListKey<String> GROUP = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_GLOBALMOD = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_DYNAMIC = new ListKey<>();
	public static final ListKey<CDOMReference<Dynamic>> GRANTED = new ListKey<>();
	public static final ListKey<CampaignSourceEntry> FILE_DATATABLE = new ListKey<>();
	public static final ListKey<InfoBoolean> ENABLE = new ListKey<>();
	public static final ListKey<InfoBoolean> ALLOW = new ListKey<>();
	public static final ListKey<String> GRANTEDVARS = new ListKey<>();

	private static CaseInsensitiveMap<ListKey<?>> map = null;

	static
	{
		buildMap();
	}

	/** Private constructor to prevent instantiation of this class */
	private ListKey()
	{
		//Only allow instantiation here
	}

	@SuppressWarnings("unchecked")
	public T cast(Object obj)
	{
		return (T) obj;
	}

	public static <OT> ListKey<OT> getKeyFor(Class<OT> keyClass, String name)
	{
		/*
		 * CONSIDER This is actually not type safe, there is a case of asking
		 * for a String a second time with a different Class that ObjectKey
		 * currently does not handle. Two solutions: One, store this in a
		 * Two-Key map and allow a String to map to more than one ObjectKey
		 * given different output types (considered confusing) or Two, store the
		 * Class and validate that with a an error message if a different class
		 * is requested.
		 */
		ListKey<OT> key = (ListKey<OT>) map.get(name);
		if (key == null)
		{
			key = new ListKey<>();
			map.put(name, key);
		}
		return key;
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<>();
		Field[] fields = ListKey.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (java.lang.reflect.Modifier.isStatic(mod) && java.lang.reflect.Modifier.isFinal(mod)
                    && java.lang.reflect.Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof ListKey)
                    {
                        map.put(field.getName(), (ListKey<?>) obj);
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
		for (Map.Entry<?, ListKey<?>> me : map.entrySet())
		{
			if (me.getValue() == this)
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static Collection<ListKey<?>> getAllConstants()
	{
		return new HashSet<>(map.values());
	}
}
