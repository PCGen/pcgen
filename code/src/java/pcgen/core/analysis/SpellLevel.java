package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.CharacterDomain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class SpellLevel
{

	/**
	 * This method gets the information about the levels at which classes and
	 * domains may cast the spell.
	 * 
	 * Modified 8 Sept 2003 by Sage_Sam for bug #801469
	 * 
	 * @return Map containing the class levels and domains that may cast the
	 *         spell
	 * @param aPC
	 */
	public static Map<String, Integer> getLevelInfo(final PlayerCharacter aPC,
			Spell sp)
	{
		Map<String, Integer> wLevelInfo = new HashMap<String, Integer>();

		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference list : masterLists.getActiveLists())
		{
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(list, sp);
			if (assoc != null)
			{
				String type = ClassSpellList.class.equals(list
						.getReferenceClass()) ? "CLASS" : "DOMAIN";
				for (AssociatedPrereqObject apo : assoc)
				{
					Integer lvl = apo
							.getAssociation(AssociationKey.SPELL_LEVEL);
					wLevelInfo.put(type + "|" + list.getLSTformat(), lvl);
				}
			}
		}

		if (aPC != null)
		{
			wLevelInfo.putAll(aPC.getSpellInfoMap("CLASS", sp.getKeyName()));
			wLevelInfo.putAll(aPC.getSpellInfoMap("DOMAIN", sp.getKeyName()));
		}
		return wLevelInfo;
	}

	public static boolean levelForKeyContains(Spell sp, final String key,
			final int levelMatch, final PlayerCharacter aPC)
	{
		// should consist of CLASS|name and DOMAIN|name pairs
		final StringTokenizer aTok = new StringTokenizer(key, "|", false);
		final int[] levelInt1 = new int[aTok.countTokens() / 2];
		int i1 = 0;

		while (aTok.hasMoreTokens())
		{
			final String objectType = aTok.nextToken();
			Class<? extends CDOMObject> listClass = "CLASS".equals(objectType) ? ClassSpellList.class
					: DomainSpellList.class;
			LoadContext context = Globals.getContext();
			if (!aTok.hasMoreTokens())
			{
				Logging.errorPrint("SEVERE: Key " + key
						+ " had even number of |");
				Thread.dumpStack();
				break;
			}
			final String objectName = aTok.nextToken();
			CDOMObject spellList = context.ref
					.silentlyGetConstructedCDOMObject(listClass, objectName);
			int result = -1;
			if (spellList == null)
			{
				Logging.debugPrint("Skipping " + objectType + " " + objectName);
			}
			else
			{
				MasterListInterface masterLists = Globals.getMasterLists();
				for (CDOMReference list : masterLists.getActiveLists())
				{
					if (list.contains(spellList))
					{
						Collection<AssociatedPrereqObject> assoc = masterLists
								.getAssociations(list, sp);
						if (assoc != null)
						{
							for (AssociatedPrereqObject apo : assoc)
							{
								if (PrereqHandler.passesAll(apo
										.getPrerequisiteList(), aPC, sp))
								{
									result = apo
											.getAssociation(AssociationKey.SPELL_LEVEL);
								}
							}
						}
					}
				}

				if (aPC != null && result == -1)
				{
					HashMap<String, Integer> wLevelInfo = new HashMap<String, Integer>();
					wLevelInfo.putAll(aPC.getSpellInfoMap("CLASS", sp
							.getKeyName()));
					wLevelInfo.putAll(aPC.getSpellInfoMap("DOMAIN", sp
							.getKeyName()));
					if (wLevelInfo.size() != 0)
					{
						Integer lvl = wLevelInfo.get(objectType + "|"
								+ objectName);

						if (lvl == null)
						{
							lvl = wLevelInfo.get(objectType + "|ALL");
						}

						if ((lvl == null) && objectType.equals("CLASS"))
						{
							final PCClass aClass = Globals.getContext().ref
									.silentlyGetConstructedCDOMObject(
											PCClass.class, objectName);

							if (aClass != null)
							{
								final StringTokenizer aTok1 = new StringTokenizer(
										aClass.getType(), ".", false);

								while (aTok1.hasMoreTokens() && (lvl == null))
								{
									lvl = wLevelInfo.get(objectType + "|TYPE."
											+ aTok1.nextToken());
								}
							}
						}

						if (lvl != null)
						{
							result = lvl.intValue();
						}
					}

				}
			}

			levelInt1[i1++] = result;
		}

		final int[] levelInt = levelInt1;

		for (int i = 0; i < levelInt.length; ++i)
		{
			// always match if levelMatch==-1
			if (((levelMatch == -1) && (levelInt[i] >= 0))
					|| ((levelMatch >= 0) && (levelInt[i] == levelMatch)))
			{
				return true;
			}
		}

		// If it's not regularly on the list, check if some SPELLLEVEL tag added
		// it.
		if (aPC != null)
		{
			return (aPC.isSpellLevelforKey(key + "|" + sp.getKeyName(),
					levelMatch));
		}
		return false;
	}

	public static Integer[] levelForKey(Spell sp, final String key,
			final PlayerCharacter aPC)
	{
		List<Integer> list = new ArrayList<Integer>();

		// If it's not regularly on the list, check if some SPELLLEVEL tag added
		// it.
		if (aPC != null)
		{
			list.add(aPC.getSpellLevelforKey(key + "|" + sp.getKeyName(), -1));
		}
		else
		{
			list.add(-1);
		}

		// should consist of CLASS|name and DOMAIN|name pairs
		final StringTokenizer aTok = new StringTokenizer(key, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String objectType = aTok.nextToken();

			if (aTok.hasMoreTokens())
			{
				list.add(levelForKey(sp, objectType, aTok.nextToken(), aPC));
			}
		}

		return list.toArray(new Integer[list.size()]);
	}

	public static int levelForKey(Spell sp, final String mType,
			final String sType, final PlayerCharacter aPC)
	{
		int result = -1;
		final Map<String, Integer> wLevelInfo = getLevelInfo(aPC, sp);
		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			Integer lvl = wLevelInfo.get(mType + "|" + sType);

			if (lvl == null)
			{
				lvl = wLevelInfo.get(mType + "|ALL");
			}

			if ((lvl == null) && mType.equals("CLASS"))
			{
				final PCClass aClass = Globals.getContext().ref
						.silentlyGetConstructedCDOMObject(PCClass.class, sType);

				if (aClass != null)
				{
					final StringTokenizer aTok = new StringTokenizer(aClass
							.getType(), ".", false);

					while (aTok.hasMoreTokens() && (lvl == null))
					{
						lvl = wLevelInfo.get(mType + "|TYPE."
								+ aTok.nextToken());
					}
				}
			}

			if (lvl != null)
			{
				result = lvl.intValue();
			}
		}

		return result;
	}

	/**
	 * Assess if this spell is of the requested level for any class.
	 * 
	 * @param level
	 *            The level to be checked.
	 * @return True if the spell is the requested level.
	 */
	public static boolean isLevel(Spell sp, final int level)
	{
		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference list : masterLists.getActiveLists())
		{
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(list, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (level == apo.getAssociation(AssociationKey.SPELL_LEVEL))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * isLevel(int aLevel)
	 * 
	 * @param aLevel
	 *            level of the spell
	 * @param aPC
	 * @return true if the spell is of the given level in any spell list
	 */
	public static boolean isLevel(Spell sp, final int aLevel,
			final PlayerCharacter aPC)
	{
		Integer levelKey = Integer.valueOf(aLevel);
		MasterListInterface masterLists = Globals.getMasterLists();
		for (PCClass pcc : aPC.getClassList())
		{
			ClassSpellList csl = pcc.get(ObjectKey.CLASS_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(csl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (levelKey.equals(apo
							.getAssociation(AssociationKey.SPELL_LEVEL)))
					{
						return true;
					}
				}
			}
		}
		for (CharacterDomain domain : aPC.getCharacterDomainList())
		{
			DomainSpellList dsl = domain.getDomain().get(
					ObjectKey.DOMAIN_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(dsl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (levelKey.equals(apo
							.getAssociation(AssociationKey.SPELL_LEVEL)))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getLevelString(Spell sp)
	{
		StringBuilder sb = new StringBuilder();
		boolean needsComma = false;
		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference list : masterLists.getActiveLists())
		{
			Collection<AssociatedPrereqObject> assoc = masterLists
					.getAssociations(list, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (needsComma)
					{
						sb.append(", ");
					}
					needsComma = true;
					sb.append(list.getLSTformat());
					sb.append(apo.getAssociation(AssociationKey.SPELL_LEVEL));
				}
			}
		}
		return sb.toString();
	}

	public static int getFirstLevelForKey(Spell sp, final String key,
			final PlayerCharacter aPC)
	{
		final Integer[] levelInt = levelForKey(sp, key, aPC);
		int result = -1;

		if (levelInt.length > 0)
		{
			for (int i = 0; i < levelInt.length; i++)
				if (levelInt[i] > -1)
					return levelInt[i];
		}

		return result;
	}

}
