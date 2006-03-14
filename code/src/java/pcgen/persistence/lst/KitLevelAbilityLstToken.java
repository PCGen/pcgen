package pcgen.persistence.lst;

import pcgen.core.kit.KitLevelAbility;

public interface KitLevelAbilityLstToken extends LstToken
{
	public abstract boolean parse(KitLevelAbility kitLA, String value);
}
