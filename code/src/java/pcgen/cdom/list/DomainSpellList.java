package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.spell.Spell;

public class DomainSpellList extends CDOMListObject<Spell>
{

	public Class<Spell> getListClass()
	{
		return Spell.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
