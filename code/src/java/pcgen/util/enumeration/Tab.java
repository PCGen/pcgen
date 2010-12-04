package pcgen.util.enumeration;

import java.util.Collections;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;

public enum Tab
{

	SABILITIES("Abilities", "in_specialabilities"),
	SOURCES("Campaigns", "Source Materials"),
	CLASSES("Class", "in_class" ),
	DESCRIPTION("Description", "in_descrip"),
	DOMAINS("Domains", "in_domains"),
	ABILITIES("Feats", "in_abilities"),
	INVENTORY("Inventory", "in_inventory"),
	RACE_MASTER("Race", "in_race"),
	SKILLS("Skills", "in_skills"),
	SPELLS("Spells",  "in_spells"),
	SUMMARY("Summary","in_summary"),
	GEAR("Gear", "", 0),
	EQUIPPING("Equipping", "", 1),
	RESOURCES("Resources", "", 2),
	TEMPBONUS("TempMod", "", 3),
	NATWEAPONS("NaturalWeapons", "", 4),
	KNOWN_SPELLS("Known", "in_known_spells"),
	PREPARED_SPELLS("Prepared", "in_prepared_spells"),
	SPELLBOOKS("Spellbooks", "in_spellbooks"),
	RACES("Races", "in_races"),
	TEMPLATES("Templates", "in_templates"),
	CHARACTERSHEET("Character Sheet", "in_character_sheet"),
	INFO("SourceInfo", "in_source_info");

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
