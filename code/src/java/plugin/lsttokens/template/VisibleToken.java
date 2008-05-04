package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCTemplateLstToken
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate,
	 *      java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (value.equals("DISPLAY"))
		{
			template.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (value.equals("EXPORT"))
		{
		}
		else if (value.equals("NO"))
		{
			template.setVisibility(Visibility.HIDDEN);
		}
		else
		{
			if (!value.equals("ALWAYS") && !value.equals("YES"))
			{
				StringBuffer buff = new StringBuffer();
				buff.append("In template ");
				buff.append(template.getDisplayName());
				buff.append(", token ");
				buff.append(getTokenName());
				buff.append(", use of '");
				buff.append(value);
				buff.append("' is not valid, please use DISPLAY, EXPORT, NO, YES or ALWAYS (exact String, upper case)");
				Logging.errorPrint(buff.toString());
				return false;
			}
			template.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
