package plugin.lsttokens.substitutionlevel;

import pcgen.core.SubstitutionClass;
import pcgen.persistence.lst.SubstitutionClassLstToken;

/**
 * Class deals with SUBSTITUTIONCLASS Token
 */
public class SubstitutionclassToken implements SubstitutionClassLstToken
{

	public String getTokenName()
	{
		return "SUBSTITUTIONCLASS";
	}

	public boolean parse(SubstitutionClass substitutionclass, String value)
	{
		substitutionclass.setName(value);
		return true;
	}
}
