package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken implements DeityLstToken{

	public String getTokenName() {
		return "DOMAINS";
	}

	public boolean parse(Deity deity, String value) {
		if(value.length() > 0) {
			String[] domains = value.split(",");
			deity.setDomainNameList( CoreUtility.arrayToList(domains) );
			return true;
		}
		return false;
	}
}
