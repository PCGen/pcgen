/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class DefineLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "DEFINE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		try
		{
			String varName = tok.nextToken();
			String defineFormula;
			if (varName.startsWith("UNLOCK."))
			{
				if (tok.hasMoreTokens())
				{
					Logging
						.log(Logging.LST_ERROR,
							"Cannot provide a value with DEFINE:UNLOCK. : "
								+ value);
					return false;
				}
				defineFormula = "";
			}
			else if (!tok.hasMoreTokens())
			{
				Logging.log(Logging.LST_ERROR,
					"Non UNLOCK DEFINE missing value. Fomrat should be DEFINE:var|value : "
						+ value);
				return false;
			}
			else
			{
				defineFormula = tok.nextToken();
			}
			obj.addVariable(anInt, varName, defineFormula);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
