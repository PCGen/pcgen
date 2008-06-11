/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class CskillLst implements GlobalLstToken
{
	/*
	 * FIXME When this token is converted to the new syntax, a change needs to
	 * take place in PCGIOHandler, which is currently calling PObjectLoader
	 */

	public String getTokenName()
	{
		return "CSKILL";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			obj.addCSkill(tok.nextToken());
		}
		return true;
	}
}
