package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setLanguageBonus(value);
		return true;
	}
}
