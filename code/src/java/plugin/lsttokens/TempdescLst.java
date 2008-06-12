/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.io.EntityEncoder;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 *
 */
public class TempdescLst implements CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "TEMPDESC";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		context.getObjectContext().put(obj, StringKey.TEMP_DESCRIPTION,
			EntityEncoder.decode(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String descr =
				context.getObjectContext().getString(obj,
					StringKey.TEMP_DESCRIPTION);
		if (descr == null)
		{
			return null;
		}
		return new String[]{EntityEncoder.encode(descr)};
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
