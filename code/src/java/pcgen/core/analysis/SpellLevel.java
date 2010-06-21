package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;

public class SpellLevel
{

	public static boolean levelForKeyContains(Spell sp,
			List<? extends CDOMList<Spell>> lists, int levelMatch,
			PlayerCharacter aPC)
	{
		if (lists == null || aPC == null)
		{
			return false;
		}
		Set<Integer> resultList = new TreeSet<Integer>();
		HashMapToList<CDOMList<Spell>, Integer> levelInfo = aPC.getMasterLevelInfo(sp);
		HashMapToList<CDOMList<Spell>, Integer> pcli = aPC.getPCBasedLevelInfo(sp);
		for (CDOMList<Spell> spellList : lists)
		{
			List<Integer> levels = levelInfo.getListFor(spellList);
			if (levels != null)
			{
				resultList.addAll(levels);
			}
			if (resultList.isEmpty())
			{
				levels = pcli.getListFor(spellList);
				if (levels != null)
				{
					resultList.addAll(levels);
				}
			}
		}
		return levelMatch == -1 && !resultList.isEmpty() || levelMatch >= 0
				&& resultList.contains(levelMatch);
	}

	public static Integer[] levelForKey(Spell sp,
			List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		List<Integer> list = new ArrayList<Integer>();

		if (lists != null)
		{
			for (CDOMList<Spell> spellList : lists)
			{
				list.add(getFirstLvlForKey(sp, spellList, aPC));
			}
		}

		return list.toArray(new Integer[list.size()]);
	}

	public static int getFirstLvlForKey(Spell sp, CDOMList<Spell> list,
			PlayerCharacter aPC)
	{
		HashMapToList<CDOMList<Spell>, Integer> wLevelInfo = aPC.getLevelInfo(sp);
		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			List<Integer> levelList = wLevelInfo.getListFor(list);
			if (levelList != null)
			{
				// We assume those calling this method know what they are doing!
				return levelList.get(0);
			}
		}
		return -1;
	}

	/**
	 * isLevel(int aLevel)
	 *
	 * @param aLevel
	 *            level of the spell
	 * @param aPC
	 * @return true if the spell is of the given level in any spell list
	 */
	public static boolean isLevel(Spell sp, int aLevel, PlayerCharacter aPC)
	{
		Integer levelKey = Integer.valueOf(aLevel);
		MasterListInterface masterLists = Globals.getMasterLists();
		for (PCClass pcc : aPC.getClassSet())
		{
			ClassSpellList csl = pcc.get(ObjectKey.CLASS_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(csl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), aPC,
							sp))
					{
						if (levelKey.equals(apo
								.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		for (Domain domain : aPC.getDomainSet())
		{
			DomainSpellList dsl = domain.get(ObjectKey.DOMAIN_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(dsl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), aPC,
							sp))
					{
						if (levelKey.equals(apo
								.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static int getFirstLevelForKey(Spell sp,
			List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		Integer[] levelInt = levelForKey(sp, lists, aPC);
		int result = -1;

		if (levelInt.length > 0)
		{
			for (int i = 0; i < levelInt.length; i++)
			{
				if (levelInt[i] > -1)
				{
					return levelInt[i];
				}
			}
		}

		return result;
	}

	public static HashMapToList<CDOMList<Spell>, Integer> getMasterLevelInfo(
			PlayerCharacter aPC, Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> levelInfo = new HashMapToList<CDOMList<Spell>, Integer>();

		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference<? extends CDOMList> ref : masterLists
				.getActiveLists())
		{
			for (CDOMList list : ref.getContainedObjects())
			{
				if (list instanceof ClassSpellList
						|| list instanceof DomainSpellList)
				{
					Collection<AssociatedPrereqObject> assoc = masterLists
							.getAssociations(list, sp);
					for (AssociatedPrereqObject apo : assoc)
					{
						// TODO This null for source is incorrect!
						if (PrereqHandler.passesAll(apo.getPrerequisiteList(),
								aPC, null))
						{
							Integer lvl = apo
									.getAssociation(AssociationKey.SPELL_LEVEL);
							levelInfo.addToListFor(list, lvl);
						}
					}
				}
			}
		}
		return levelInfo;
	}

	public static HashMapToList<CDOMList<Spell>, Integer> getPCBasedLevelInfo(PlayerCharacter pc, Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> levelInfo = new HashMapToList<CDOMList<Spell>, Integer>();
		for (CDOMObject cdo : pc.getCDOMObjectList())
		{
			Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs = cdo
					.getModifiedLists();
			for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
			{
				Collection<? extends CDOMList> lists = ref
						.getContainedObjects();
				for (CDOMList<Spell> list : lists)
				{
					if (list instanceof ClassSpellList
							|| list instanceof DomainSpellList)
					{
						CDOMReference<? extends CDOMList<Spell>> spelllistref = (CDOMReference<? extends CDOMList<Spell>>) ref;
						Collection<CDOMReference<Spell>> mods = cdo
								.getListMods(spelllistref);
						for (CDOMReference<Spell> spellref : mods)
						{
							if (spellref.contains(sp))
							{
								Collection<AssociatedPrereqObject> listAssocs = cdo
										.getListAssociations(spelllistref,
												spellref);
								for (AssociatedPrereqObject apo : listAssocs)
								{
									// TODO This null for source is
									// incorrect!
									if (PrereqHandler.passesAll(apo
											.getPrerequisiteList(), pc, null))
									{
										Integer lvl = apo
												.getAssociation(AssociationKey.SPELL_LEVEL);
										levelInfo.addToListFor(list, lvl);
									}
								}
							}
						}
					}
				}
			}

		}
		return levelInfo;
	}

	/**
	 * Retrieve a map of the additional spells that will be added to the supplied class as a result 
	 * of SPELLKNOWN tags.
	 * 
	 * @param pc The character being tested
	 * @param pcc The PC class being checked/
	 * @return A map of additional spell lists for each spell level 
	 */
	public static Map<Integer, List<Spell>> getPCBasedBonusKnownSpells(PlayerCharacter pc, ClassSpellList csl)
	{
		HashMap<Integer, List<Spell>> levelInfo = new HashMap<Integer, List<Spell>>();
		
		for (CDOMObject cdo : pc.getCDOMObjectList())
		{
			// Get the global lists that are modified by the application of this object
			Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs = cdo
					.getModifiedLists();
			for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
			{
				Collection<? extends CDOMList> lists = ref
						.getContainedObjects();
				for (CDOMList<Spell> list : lists)
				{
					if (list == csl)
					{
						CDOMReference<? extends CDOMList<Spell>> spelllistref = (CDOMReference<? extends CDOMList<Spell>>) ref;
						Collection<CDOMReference<Spell>> mods = cdo
								.getListMods(spelllistref);
						for (CDOMReference<Spell> spellref : mods)
						{
							Collection<AssociatedPrereqObject> listAssocs = cdo
									.getListAssociations(spelllistref,
											spellref);
							for (AssociatedPrereqObject apo : listAssocs)
							{
								// TODO This null for source is
								// incorrect!
								if (PrereqHandler.passesAll(apo
										.getPrerequisiteList(), pc, null))
								{
									Integer lvl = apo
											.getAssociation(AssociationKey.SPELL_LEVEL);
									Boolean known = apo.getAssociation(AssociationKey.KNOWN);
									if (known != null && known)
									{
										Collection<Spell> spells = spellref.getContainedObjects();
										List<Spell> spellList = levelInfo.get(lvl);
										if (spellList == null)
										{
											spellList = new ArrayList<Spell>();
											levelInfo.put(lvl, spellList);
										}
										spellList.addAll(spells);
									}
								}
							}
						}
					}
				}
			}

		}
		return levelInfo;
	}

	public static DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> getPCBasedLevelInfo(
			PlayerCharacter pc)
	{
		DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> levelInfo = new DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer>();
		for (CDOMObject cdo : pc.getCDOMObjectList())
		{
			Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs = cdo
					.getModifiedLists();
			for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
			{
				Collection<? extends CDOMList> lists = ref
						.getContainedObjects();
				for (CDOMList<Spell> list : lists)
				{
					if (list instanceof ClassSpellList
							|| list instanceof DomainSpellList)
					{
						CDOMReference<? extends CDOMList<Spell>> spelllistref = (CDOMReference<? extends CDOMList<Spell>>) ref;
						Collection<CDOMReference<Spell>> mods = cdo
								.getListMods(spelllistref);
						for (CDOMReference<Spell> spellref : mods)
						{
							Collection<AssociatedPrereqObject> listAssocs = cdo
									.getListAssociations(spelllistref, spellref);
							for (AssociatedPrereqObject apo : listAssocs)
							{
								// TODO This null for source is
								// incorrect!
								if (PrereqHandler.passesAll(apo
										.getPrerequisiteList(), pc, null))
								{
									Integer lvl = apo
											.getAssociation(AssociationKey.SPELL_LEVEL);
									for (Spell sp : spellref
											.getContainedObjects())
									{
										levelInfo.addToListFor(sp, list, lvl);
									}
								}
							}
						}
					}
				}
			}
		}
		return levelInfo;
	}

	/**
	 * Add to the supplied list the additional known spells for the class that
	 * are specified by SPELLKNOWN tags associated with the character. Any
	 * existing contents of the list are preserved.
	 * 
	 * @param pc
	 *            The character being tested
	 * @param aClass
	 *            The PC class being checked
	 * @param cSpells
	 *            The list to be populated with the spells
	 */
	public static void addBonusKnowSpellsToList(PlayerCharacter pc, PObject aClass,
		Collection<CharacterSpell> cSpells)
	{
		if (!(aClass instanceof PCClass))
		{
			return;
		}
		Map<Integer, List<Spell>> spellsMap = SpellLevel
				.getPCBasedBonusKnownSpells(pc, ((PCClass) aClass)
						.get(ObjectKey.CLASS_SPELLLIST));
		for (Integer spellLevel : spellsMap.keySet())
		{
			List<Spell> spells = spellsMap.get(spellLevel);
			for (Spell spell : spells)
			{
				CharacterSpell acs =
						new CharacterSpell(aClass, spell);
				acs.addInfo(spellLevel, 1, Globals.getDefaultSpellBook());
				cSpells.add(acs);
			}
		}
	}

	/**
	 * Calculate the number of bonus known spells added for the class and level  
	 * by SPELLKNOWN tags associated with the character. 
	 * 
	 * @param pc The character being tested
	 * @param aClass The PC class being checked
	 * @param spellLevel The level of the spells to check
	 */
	public static int getNumBonusKnowSpellsForLevel(PlayerCharacter pc,
		PObject aClass, int spellLevel)
	{
		if (!(aClass instanceof PCClass))
		{
			return 0;
		}
		ClassSpellList classSpellList = ((PCClass) aClass)
				.get(ObjectKey.CLASS_SPELLLIST);
		return getNumBonusKnowSpellsForLevel(pc, spellLevel, classSpellList);
	}

	public static int getNumBonusKnowSpellsForLevel(PlayerCharacter pc,
			int spellLevel, ClassSpellList classSpellList)
	{
		Map<Integer, List<Spell>> spellsMap = SpellLevel
				.getPCBasedBonusKnownSpells(pc, classSpellList);
		List<Spell> spells = spellsMap.get(spellLevel);
		return spells != null ? spells.size() : 0;
	}
	
}
