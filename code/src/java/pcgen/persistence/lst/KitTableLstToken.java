package pcgen.persistence.lst;

import pcgen.core.Kit;

/**
 * Interface to deal with Kit Table LST tokens
 */
public interface KitTableLstToken extends LstToken
{
	/**
	 * parse token
	 * @param kit
	 * @param tableName
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(Kit kit, final String tableName,String value);
}

