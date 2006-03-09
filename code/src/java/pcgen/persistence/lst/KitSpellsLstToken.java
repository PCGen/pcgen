package pcgen.persistence.lst;

import pcgen.core.kit.KitSpells;

public interface KitSpellsLstToken extends LstToken
{
	public abstract boolean parse(KitSpells kitSpells, String value);
}

