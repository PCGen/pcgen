package pcgen.persistence.lst;

import pcgen.core.kit.KitLevelAbility;

/**
 * Interface to deal with Kit Level Ability Lst Tokens
 */
public interface KitLevelAbilityLstToken extends LstToken
{
	/**
	 * parse
	 * @param kitLA
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitLevelAbility kitLA, String value);
}
