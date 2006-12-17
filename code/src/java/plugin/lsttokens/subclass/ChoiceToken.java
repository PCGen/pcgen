package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(SubClass subclass, String value)
	{
		subclass.setChoice(value);
		return true;
	}
}
