package pcgen.util.enumeration;

import java.util.Collection;
import pcgen.core.spell.Spell;

public enum ProhibitedSpellType {

	ALIGNMENT("Alignment")   { public Collection<String>getCheckList(Spell s) { return s.getDescriptorList(); }},
	
	DESCRIPTOR("Descriptor") { public Collection<String>getCheckList(Spell s) { return s.getDescriptorList(); }},

	SCHOOL("Export")         { public Collection<String>getCheckList(Spell s) { return s.getSchools(); }};

	private final String text;

	ProhibitedSpellType(String s)
	{
		text = s;
	}

	public abstract Collection<String> getCheckList(Spell s);
	
	public String toString()
	{
		return text;
	}
}
