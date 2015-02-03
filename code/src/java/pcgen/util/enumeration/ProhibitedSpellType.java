package pcgen.util.enumeration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.spell.Spell;

public enum ProhibitedSpellType
{

	ALIGNMENT("Alignment") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSafeListFor(ListKey.SPELL_DESCRIPTOR);
		}
		@Override
		public int getRequiredCount(Collection<String> l)
		{
			return l.size();
		}
	},

	DESCRIPTOR("Descriptor") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSafeListFor(ListKey.SPELL_DESCRIPTOR);
		}
		@Override
		public int getRequiredCount(Collection<String> l)
		{
			return l.size();
		}
	},

	SCHOOL("School") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			/*
			 * Long method for now
			 * TODO Clean up
			 */
			List<String> list = new ArrayList<String>();
			for (SpellSchool ss : s.getSafeListFor(ListKey.SPELL_SCHOOL))
			{
				list.add(ss.toString());
			}
			return list;
		}
		@Override
		public int getRequiredCount(Collection<String> l)
		{
			return l.size();
		}
	},

	SUBSCHOOL("SubSchool") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSafeListFor(ListKey.SPELL_SUBSCHOOL);
		}
		@Override
		public int getRequiredCount(Collection<String> l)
		{
			return l.size();
		}
	},

	SPELL("Spell") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return Collections.singletonList(s.getKeyName());
		}
		@Override
		public int getRequiredCount(Collection<String> l)
		{
			return 1;
		}
	};

	private final String text;

	ProhibitedSpellType(String s)
	{
		text = s;
	}

	public abstract Collection<String> getCheckList(Spell s);

	public abstract int getRequiredCount(Collection<String> l);

	@Override
	public String toString()
	{
		return text;
	}
}
