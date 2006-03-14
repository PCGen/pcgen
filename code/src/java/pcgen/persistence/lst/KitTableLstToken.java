package pcgen.persistence.lst;

import pcgen.core.Kit;

public interface KitTableLstToken extends LstToken
{
	public abstract boolean parse(Kit kit, final String tableName,String value);
}

