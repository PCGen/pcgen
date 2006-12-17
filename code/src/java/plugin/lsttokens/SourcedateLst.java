/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.text.ParseException;
import java.util.Map;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.SourceLoader;
import pcgen.persistence.lst.SourceLstToken;
import pcgen.util.Logging;

/**
 * @author zaister
 *
 */
public class SourcedateLst implements GlobalLstToken, SourceLstToken
{

	public String getTokenName()
	{
		return "SOURCEDATE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		try
		{
			obj.getSourceEntry().getSourceBook().setDate(value);
		}
		catch (ParseException e)
		{
			Logging.errorPrint("Error parsing date", e);
			return false;
		}
		return true;
	}

	public boolean parse(Map<String, String> sourceMap, String value)
	{
		sourceMap.putAll(SourceLoader.parseSource("SOURCEDATE:" + value));
		return true;
	}
}
