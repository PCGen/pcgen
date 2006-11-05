package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken implements PCClassLstToken {

	public String getTokenName() {
		return "DOMAIN";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			String domainKey;
			String prereq = null; //Do not initialize, null is significant!
			
			//Note: May contain PRExxx
			if (aString.indexOf("[") == -1)
			{
				domainKey = aString;
			}
			else
			{
				int openBracketLoc = aString.indexOf("[");
				domainKey = aString.substring(0, openBracketLoc);
				if (!aString.endsWith("]"))
				{
					Logging.errorPrint("Unresolved Prerequisite on Domain "
							+ aString + " in " + getTokenName());
				}
				prereq = aString.substring(openBracketLoc + 1,
						aString.length() - openBracketLoc - 2);
			}
			
			Domain thisDomain = Globals.getDomainKeyed(domainKey);
			
			if (thisDomain == null) {
				Logging.errorPrint("Unresolved Domain " + domainKey + " in "
						+ getTokenName());
			} else {
				Domain clonedDomain = thisDomain.clone();
				if (prereq != null)
				{
					try {
						clonedDomain.addPreReq(PreParserFactory.getInstance().parse(prereq));
					} catch (PersistenceLayerException e) {
						Logging.errorPrint("Error generating Prerequisite "
								+ prereq + " in " + getTokenName());
					}
				}
				pcclass.addDomain(level, clonedDomain);
			}
		}
		
		return true;
	}
}