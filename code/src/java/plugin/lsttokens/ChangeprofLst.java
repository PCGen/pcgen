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
public class ChangeprofLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "CHANGEPROF";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		//value should be of the format:
		//Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
		//
		//e.g.: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial
		//
		final StringTokenizer tok = new StringTokenizer(value, "|");

		while (tok.hasMoreTokens())
		{
			String entry = tok.nextToken();
			String newProf;
			final int indx = entry.indexOf('=');
			if (indx > 1)
			{
				newProf = entry.substring(indx + 1);
				entry = entry.substring(0, indx);

				final StringTokenizer bTok = new StringTokenizer(entry, ",");
				while (bTok.hasMoreTokens())
				{
					final String eqString = bTok.nextToken();
					obj.addChangeProf(eqString, newProf);
				}
			}
			else
			{
				return false;
			}
		}
		return true;
	}
}
