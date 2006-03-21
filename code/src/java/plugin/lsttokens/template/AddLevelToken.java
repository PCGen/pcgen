package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

public class AddLevelToken implements PCTemplateLstToken
{

	public boolean parse(PCTemplate template, String value)
	{
		template.addLevelMod("ADD|"+value);
		return true;
	}

	public String getTokenName()
	{
		return "ADDLEVEL";
	}

}
