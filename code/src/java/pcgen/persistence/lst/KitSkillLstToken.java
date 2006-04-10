package pcgen.persistence.lst;

import pcgen.core.kit.KitSkill;

/**
 * Interface to deal with Kit Skill LST Tokens
 */
public interface KitSkillLstToken extends LstToken
{
	/**
	 * Parse
	 * @param kitSkill
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitSkill kitSkill, String value);
}

