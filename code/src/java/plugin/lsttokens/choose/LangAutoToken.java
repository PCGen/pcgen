package plugin.lsttokens.choose;

import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.util.Logging;

public class LangAutoToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
			return false;
		}
		if (po instanceof PCTemplate)
		{
			((PCTemplate) po).setChooseLanguageAutos(value);
			return true;
		}
		else if (po instanceof Race)
		{
			((Race) po).setChooseLanguageAutos(value);
			return true;
		}
		Logging.errorPrint("CHOOSE:" + getTokenName() + " is not supported in "
				+ po.getClass().getName() + " files");
		return false;
	}

	public String getTokenName()
	{
		return "LANGAUTO";
	}
}
