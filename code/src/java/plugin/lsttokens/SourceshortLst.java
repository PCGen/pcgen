
package plugin.lsttokens;

import java.net.URI;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

public class SourceshortLst extends AbstractStringToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

    @Override
    public String getTokenName()
    {
        return "SOURCESHORT";
    }

    @Override
    protected StringKey stringKey()
    {
        return StringKey.SOURCE_SHORT;
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public boolean parse(Campaign campaign, String value, URI sourceURI)
    {
        campaign.put(StringKey.SOURCE_SHORT, value);
        return true;
    }
}
