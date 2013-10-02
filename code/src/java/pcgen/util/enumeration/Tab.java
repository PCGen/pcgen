package pcgen.util.enumeration;

import java.util.Collections;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;

public enum Tab
{
	SUMMARY("Summary","in_summary"),
	RACE("Race", "in_race"),
	TEMPLATES("Templates", "in_Templates"),
	CLASSES("Class", "in_clClass" ),
	SKILLS("Skills", "in_skills"),
	ABILITIES("Feats", "in_featsAbilities"),
	DOMAINS("Domains", "in_domains"),
	SPELLS("Spells",  "in_spells"),
	KNOWN_SPELLS("Known", "in_InfoKnown", 0),
	PREPARED_SPELLS("Prepared", "in_InfoPrepared", 1),
	SPELLBOOKS("Spellbooks", "in_InfoSpellbooks", 2),
	INVENTORY("Inventory", "in_inventory"),
	PURCHASE("Purchase", "in_purchase", 0),
	EQUIPPING("Equipping", "in_equipping", 1),
	DESCRIPTION("Description", "in_descrip"),
	TEMPBONUS("TempMod", "in_InfoTempMod"),
	COMPANIONS("Companions", "in_companions"),
	CHARACTERSHEET("Character Sheet", "in_character_sheet");

	private static final Map<Object, Tab> byText;

	static
	{
		CaseInsensitiveMap<Tab> map = new CaseInsensitiveMap<Tab>();
		for (Tab t : values())
		{
			Tab previous = map.put(t.toString(), t);
			if (previous != null)
			{
				throw new InternalError(
						"Two Tab objects must not have same 'text' field: "
								+ t.toString());
			}
		}
		byText = Collections.unmodifiableMap(map);
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
		return byText.containsKey(id);
	}

	public static Tab getTab(String name)
	{
		return byText.get(name);
	}
}
