package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with GENDERLOCK Token
 */
public class GenderlockToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "GENDERLOCK";
	}

	//set and lock character gender, disabling pulldown menu in description section.
	public boolean parse(PCTemplate template, String value)
	{
		template.setGenderLock(value);
		return true;
	}
}
