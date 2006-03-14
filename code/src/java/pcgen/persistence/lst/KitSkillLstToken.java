package pcgen.persistence.lst;

import pcgen.core.kit.KitSkill;

public interface KitSkillLstToken extends LstToken
{
	public abstract boolean parse(KitSkill kitSkill, String value);
}

