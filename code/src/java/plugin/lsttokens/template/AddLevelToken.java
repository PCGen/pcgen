package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * New Token to support Adding Levels to say a Lycanthorpe template
 */
public class AddLevelToken implements PCTemplateLstToken
{

	public boolean parse(PCTemplate template, String value)
	{
		template.addLevelMod("ADD|" + value);
		return true;
	}

	public String getTokenName()
	{
		return "ADDLEVEL";
	}

}
