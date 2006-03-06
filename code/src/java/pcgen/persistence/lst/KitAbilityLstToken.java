package pcgen.persistence.lst;

import pcgen.core.kit.KitAbilities;

public interface KitAbilityLstToken extends LstToken {
	public abstract boolean parse(KitAbilities kitAbility, String value);

}
