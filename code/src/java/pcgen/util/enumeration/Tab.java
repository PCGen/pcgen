/*
 * Copyright 2014 (C) Stefan Radermacher
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
package pcgen.util.enumeration;

import java.util.Collections;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;

public enum Tab
{
	SUMMARY("Summary", "in_summary"), RACE("Race", "in_race"), TEMPLATES("Templates", "in_Templates"), CLASSES("Class",
			"in_clClass"), SKILLS("Skills", "in_skills"), ABILITIES("Feats", "in_featsAbilities"), DOMAINS("Domains",
					"in_domains"), SPELLS("Spells", "in_spells"), KNOWN_SPELLS("Known", "in_InfoKnown",
							0), PREPARED_SPELLS("Prepared", "in_InfoPrepared", 1), SPELLBOOKS("Spellbooks",
									"in_InfoSpellbooks",
									2), INVENTORY("Inventory", "in_inventory"), PURCHASE("Purchase", "in_purchase",
											0), EQUIPPING("Equipping", "in_equipping", 1), DESCRIPTION("Description",
													"in_descrip"), TEMPBONUS("TempMod", "in_InfoTempMod"), COMPANIONS(
															"Companions", "in_companions"), CHARACTERSHEET(
																	"Character Sheet", "in_character_sheet");

	private static final Map<Object, Tab> BY_TEXT;

	static
	{
		CaseInsensitiveMap<Tab> map = new CaseInsensitiveMap<>();
		for (Tab t : values())
		{
			Tab previous = map.put(t.toString(), t);
			if (previous != null)
			{
				throw new InternalError("Two Tab objects must not have same 'text' field: " + t.toString());
			}
		}
		BY_TEXT = Collections.unmodifiableMap(map);
	}
	private final String text;
	private final String label;
	private final int index;

	Tab(String t, String l)
	{
		text = t;
		index = 0;

		if ("".equals(l))
		{
			label = t;
		}
		else
		{
			label = l;
		}
	}

	Tab(String t, String l, int i)
	{
		text = t;
		index = i;

		if ("".equals(l))
		{
			label = t;
		}
		else
		{
			label = l;
		}
	}

	@Override
	public String toString()
	{
		return text;
	}

	public String label()
	{
		return label;
	}

	public int index()
	{
		return index;
	}

	public static boolean exists(String id)
	{
		return BY_TEXT.containsKey(id);
	}

	public static Tab getTab(String name)
	{
		return BY_TEXT.get(name);
	}
}
