
package plugin.lsttokens;

import java.net.URI;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

public class SourcelongLst extends AbstractStringToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

    @Override
    public String getTokenName()
    {
        return "SOURCELONG";
    }

    @Override
    protected StringKey stringKey()
    {
        return StringKey.SOURCE_LONG;
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public boolean parse(Campaign campaign, String value, URI sourceURI)
    {
        campaign.put(StringKey.SOURCE_LONG, value);
        return true;
    }
}
