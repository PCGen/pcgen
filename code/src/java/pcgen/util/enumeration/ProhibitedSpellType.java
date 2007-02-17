package pcgen.util.enumeration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.core.spell.Spell;

public enum ProhibitedSpellType
{

	ALIGNMENT("Alignment") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getDescriptorList();
		}
		@Override
		public int getRequiredCount(List<String> l)
		{
			return l.size();
		}
	},

	DESCRIPTOR("Descriptor") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getDescriptorList();
		}
		@Override
		public int getRequiredCount(List<String> l)
		{
			return l.size();
		}
	},

	SCHOOL("School") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSchools();
		}
		@Override
		public int getRequiredCount(List<String> l)
		{
			return l.size();
		}
	},

	SUBSCHOOL("SubSchool") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSubschools();
		}
		@Override
		public int getRequiredCount(List<String> l)
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
		public int getRequiredCount(List<String> l)
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

	public abstract int getRequiredCount(List<String> l);

	@Override
	public String toString()
	{
		return text;
	}
}
