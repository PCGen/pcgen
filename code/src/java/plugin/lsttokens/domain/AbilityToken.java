package plugin.lsttokens.domain;

import pcgen.core.Domain;
import pcgen.persistence.lst.DomainLstToken;

/**
 * Deals with ABILITY token 
 */
public class AbilityToken implements DomainLstToken
{

	/**
	 * get token name
	 * @return token name 
	 */
	public String getTokenName()
	{
		return "ABILITY";
	}

	/**
	 * Parse ABILITY token for domain
	 * 
	 * @param domain 
	 * @param value 
	 * @return true 
	 */
	public boolean parse(Domain domain, String value)
	{
		domain.addAbility(value);
		return true;
	}
}
