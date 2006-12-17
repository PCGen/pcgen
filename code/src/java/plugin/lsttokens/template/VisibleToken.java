package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		if (value.startsWith("DISPLAY"))
		{
			template.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (value.startsWith("EXPORT"))
		{
			template.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (value.startsWith("NO"))
		{
			template.setVisibility(Visibility.HIDDEN);
		}
		else
		{
			template.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
