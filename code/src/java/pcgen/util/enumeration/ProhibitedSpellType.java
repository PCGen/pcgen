package pcgen.util.enumeration;

import java.util.Collection;
import pcgen.core.spell.Spell;

public enum ProhibitedSpellType
{

	ALIGNMENT("Alignment") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getDescriptorList();
		}
	},

	DESCRIPTOR("Descriptor") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getDescriptorList();
		}
	},

	SCHOOL("School") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSchools();
		}
	},

	SUBSCHOOL("SubSchool") {
		@Override
		public Collection<String> getCheckList(Spell s)
		{
			return s.getSubschools();
		}
	};

	private final String text;

	ProhibitedSpellType(String s)
	{
		text = s;
	}

	public abstract Collection<String> getCheckList(Spell s);

	@Override
	public String toString()
	{
		return text;
	}
}
