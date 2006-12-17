/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;

import java.util.Map;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.SourceLoader;
import pcgen.persistence.lst.SourceLstToken;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.util.PropertyFactory;

/**
 * @author djones4
 * 
 * DeprecatedToken.  Seems to be used as a SOURCEPAGE tag.
 *
 */
public class SourceLst implements GlobalLstToken, SourceLstToken,
		DeprecatedToken
{

	public String getTokenName()
	{
		return "SOURCE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.getSourceEntry().getSourceBook().setLongName(value);
		return true;
	}

	public boolean parse(Map<String, String> sourceMap, String value)
	{
		sourceMap.putAll(SourceLoader.parseSource("SOURCEPAGE:" + value));
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(PObject anObj, String anValue)
	{
		return PropertyFactory.getString("DeprecatedToken.SourceLst.Message"); //$NON-NLS-1$
	}
}
