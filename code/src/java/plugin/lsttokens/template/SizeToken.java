package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setTemplateSize(value);
		return true;
	}
}
