/*
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;


import java.net.URI;

/**
 *
 */
public class SourcelinkLst extends AbstractStringToken<CDOMObject> implements
        CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "SOURCELINK";
	}

	@Override
	protected StringKey stringKey()
	{
		return StringKey.SOURCE_LINK;
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public boolean parse(Campaign campaign, String value, URI sourceURI)
	{
		campaign.put(StringKey.SOURCE_LINK, value);
		return true;
	}
}
