package pcgen.persistence.lst;

import pcgen.core.kit.KitAbilities;

/**
 * Interface for Kit Ability LST tokens
 */
public interface KitAbilityLstToken extends LstToken {
	
	/**
	 * Parse the token
	 * @param kitAbility
	 * @param value
	 * @return true if parse OK
	 */
	public abstract boolean parse(KitAbilities kitAbility, String value);

}
