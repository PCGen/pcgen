package pcgen.persistence.lst;

import pcgen.core.kit.KitSpells;

/**
 * Interface to deal with Kit Spells LST token
 */
public interface KitSpellsLstToken extends LstToken
{
	/**
	 * @param kitSpells
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitSpells kitSpells, String value);
}

