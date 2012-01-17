package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
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
		LISTS: for (CDOMReference<? extends CDOMList> ref : masterLists
				.getActiveLists())
		{
			Collection<AssociatedPrereqObject> assoc = null;
			for (CDOMList list : ref.getContainedObjects())
			{
				if (list instanceof ClassSpellList
						|| list instanceof DomainSpellList)
				{
					if (assoc == null)
					{
						CDOMReference r = ref;
						assoc = masterLists.getAssociations(r, sp);
						if (assoc == null)
						{
							continue LISTS;
						}
					}
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
}
