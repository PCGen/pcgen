/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.net.URI;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 *
 */
public class SourcelongLst extends AbstractStringToken<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, InstallLstToken
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

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String title = context.getObjectContext().getString(obj,
				StringKey.SOURCE_LONG);
		if (title == null)
		{
			return null;
		}
		return new String[] { title };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public boolean parse(Campaign campaign, String value, URI sourceURI)
	{
		campaign.put(StringKey.SOURCE_LONG, value);
		return true;
	}
}
