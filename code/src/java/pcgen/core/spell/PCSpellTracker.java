/*
 * PCSpellTracker.java
 * Copyright TODO
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
 * Created on Dec 8, 2004
 *
 * $Id$
 */
package pcgen.core.spell;

import pcgen.core.CharacterDomain;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.DoubleKeyMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import pcgen.core.Domain;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>PCSpellTracker</code> has a map that stores each key and it's corresponding
 * spell info map.  It has a second map that stores the current serial for
 * each map when it was generated.  When it get's a key that needs a
 * spellInfoMap, it checks to see if it already has one, and if the serial
 * related to it is up to date.  If it is, then it just hands back the old
 * one.  if it is not, it generates it and caches it. This means the spell
 * maps only need to be refreshed when the character changes instead of
 * every time it is called for.
 *
 * @author djones4
 * @version $Revision$
 */

public class PCSpellTracker {

	protected PlayerCharacter pc;
	protected DoubleKeyMap<String, String, Integer> spellSerialMap = new DoubleKeyMap<String, String, Integer>();
	protected DoubleKeyMap<String, String, Map<String, Integer>> spellMap = new DoubleKeyMap<String, String, Map<String, Integer>>();

	protected Map<String, Integer> spellLevelMap = new HashMap<String, Integer>();
	protected int spellLevelMapSerial = 0;

	public PCSpellTracker(PlayerCharacter pc) {
		this.pc = pc;
	}

	public Map<String, Integer> getSpellInfoMap(final String key1, final String key2) {
		Integer i = spellSerialMap.get(key1, key2);
		if (i != null) {
			if (i.intValue() >= pc.getSerial()) {
				return spellMap.get(key1, key2);
			}
		}
		Map<String, Integer> newMap = buildSpellInfoMap(key1, key2);
		spellMap.put(key1, key2, newMap);
		spellSerialMap.put(key1, key2, Integer.valueOf(pc.getSerial()));
		return newMap;
	}

	public Map<String, Integer> buildSpellInfoMap(final String key1, final String key2) {
		Iterator<? extends PObject> e;
		Map<String, Integer> spellInfoMap = new HashMap<String, Integer>();

		if (!pc.getClassList().isEmpty()) {
			e = pc.getClassList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (!pc.getCompanionModList().isEmpty()) {
			e = pc.getCompanionModList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (!pc.getEquipmentList().isEmpty()) {
			e = pc.getEquipmentList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (!pc.aggregateFeatList().isEmpty()) {
			e = pc.aggregateFeatList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (!pc.getTemplateList().isEmpty()) {
			e = pc.getTemplateList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (pc.hasCharacterDomainList()) {
			List<Domain> domains = new ArrayList<Domain>();
			for ( CharacterDomain cd : pc.getCharacterDomainList() )
			{
				domains.add(cd.getDomain());
			}
			buildSpellInfoMap(spellInfoMap, key1, key2, domains.iterator());
		}

		if (!pc.getSkillList().isEmpty()) {
			e = pc.getSkillList().iterator();
			buildSpellInfoMap(spellInfoMap, key1, key2, e);
		}

		if (pc.getRace() != null) {
			spellInfoMap.putAll(getSpellInfoMapPassesPrereqs(pc.getRace(), key1, key2));
		}

		if (pc.getDeity() != null) {
			spellInfoMap.putAll(getSpellInfoMapPassesPrereqs(pc.getDeity(), key1, key2));
		}
		return spellInfoMap;
	}

	private void buildSpellInfoMap(final Map<String, Integer> spellInfoMap, final String key1, final String key2, final Iterator<? extends PObject> e) {
		while (e.hasNext()) {
			final PObject pObj = e.next();

			if (pObj == null) {
				continue;
			}

			spellInfoMap.putAll(getSpellInfoMapPassesPrereqs(pObj, key1, key2));
		}
	}

	public Map<String, Integer> getSpellInfoMapPassesPrereqs(final PObject obj, final String key1, final String key2) {
		if (obj.getSpellSupport() == null) {
			return new HashMap<String, Integer>();
		}
		return obj.getSpellSupport().getSpellInfoMapPassesPrereqs(key1, key2, pc);
	}

	public boolean isSpellLevelforKey(final String key, final int levelMatch) {
		if (spellLevelMapSerial < pc.getSerial()) {
			buildSpellLevelMap(levelMatch);
			spellLevelMapSerial = pc.getSerial();
		}

		if (!spellLevelMap.containsKey(key)) {
			return false;
		}

		final int levelInt;

		levelInt = spellLevelMap.get(key);

		if (levelMatch == levelInt) {
			return true;
		}

		if (levelMatch==-1 && levelInt>=0) {
			return true;
		}
		return false;
	}

	public int getSpellLevelforKey(final String key, final int levelMatch) {
		// rebuild the spell level map if the character has changed
		if (spellLevelMapSerial < pc.getSerial()) {
			buildSpellLevelMap(levelMatch);
			spellLevelMapSerial = pc.getSerial();
		}

		if (!spellLevelMap.containsKey(key)) {
			// This spell is not on any spell list for this character
			return -1;
		}

		return spellLevelMap.get(key);
	}

	public void buildSpellLevelMap(final int levelMatch) {
		Iterator<?> e;
		spellLevelMap.clear();

		if (!pc.getClassList().isEmpty()) {
			e = pc.getClassList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (!pc.getCompanionModList().isEmpty()) {
			e = pc.getCompanionModList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (!pc.getEquipmentList().isEmpty()) {
			e = pc.getEquipmentList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (!pc.aggregateFeatList().isEmpty()) {
			e = pc.aggregateFeatList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (!pc.getTemplateList().isEmpty()) {
			e = pc.getTemplateList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (pc.hasCharacterDomainList()) {
			e = pc.getCharacterDomainList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (!pc.getSkillList().isEmpty()) {
			e = pc.getSkillList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}

		if (pc.getRace() != null) {
			spellLevelMap.putAll(getSpellMapPassesPrereqs(pc.getRace(), levelMatch));
		}

		if (pc.getDeity() != null) {
			spellLevelMap.putAll(getSpellMapPassesPrereqs(pc.getDeity(), levelMatch));
		}
	}

	private void buildSpellLevelMap(final int levelMatch, final Iterator<?> e) {
		while (e.hasNext()) {
			Object obj = e.next();

			if (obj instanceof CharacterDomain) {
				obj = ((CharacterDomain) obj).getDomain();
			}

			if (!(obj instanceof PObject)) {
				continue;
			}

			final PObject pObj = (PObject) obj;
			spellLevelMap.putAll(getSpellMapPassesPrereqs(pObj, levelMatch));
		}
	}

	/**
	 * returns all the spells of levelMatch or lower
	 * that pass all the PreReqs
	 * @param obj
	 * @param levelMatch
	 * @return Map
	 */
	public Map<String, Integer> getSpellMapPassesPrereqs(final PObject obj, final int levelMatch) {
		if (obj.getSpellSupport() == null) {
			return new HashMap<String, Integer>();
		}

		return obj.getSpellSupport().getSpellMapPassesPrereqs(levelMatch, pc);
	}
}
