package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HASSPELLFORMULA Token
 */
public class HasspellformulaToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "HASSPELLFORMULA";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		Logging.deprecationPrint("Ignoring HASSPELLFORMULA: "
				+ "No longer required in PCGen 5.15+ ");
		return true;
	}
}
