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

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		if (value.startsWith("DISPLAY"))
		{
			if (!value.equals("DISPLAY"))
			{
				Logging.errorPrint("Use of '" + value
					+ "' is not valid, please use DISPLAY "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (value.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.errorPrint("Use of '" + value
					+ "' is not valid, please use EXPORT "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (value.startsWith("NO"))
		{
			if (!value.equals("NO"))
			{
				Logging.errorPrint("Use of '" + value
					+ "' is not valid, please use NO "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.HIDDEN);
		}
		else
		{
			if (!value.equals("ALWAYS") && !value.equals("YES"))
			{
				Logging.errorPrint("Use of '" + value
					+ "' is not valid, please use YES or ALWAYS "
					+ "(exact String, upper case)");
			}
			template.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
