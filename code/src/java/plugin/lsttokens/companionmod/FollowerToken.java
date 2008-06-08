package plugin.lsttokens.companionmod;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

import java.util.StringTokenizer;

/**
 * Class deals with FOLLOWER Token
 */
public class FollowerToken implements CompanionModLstToken
{

	public String getTokenName()
	{
		return "FOLLOWER";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "=");
		final String someClasses = aTok.nextToken();
		final String aLev = aTok.nextToken();
		cmpMod.setLevel(Integer.parseInt(aLev));

		final StringTokenizer bTok = new StringTokenizer(someClasses, ",");

		while (bTok.hasMoreTokens())
		{
			final String classKey = bTok.nextToken();
			final PCClass pcClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, classKey);

			if (pcClass != null)
			{
				cmpMod.getClassMap().put(classKey, aLev);
			}
			else
			{
				// Now we accept VARiable names here.
				cmpMod.getVarMap().put(classKey, aLev);
			}
		}
		return true;
	}
}
