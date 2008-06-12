/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 *
 */
public class OutputnameLst implements CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "OUTPUTNAME";
	}


	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		context.getObjectContext().put(obj, StringKey.OUTPUT_NAME, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String oname =
				context.getObjectContext()
					.getString(obj, StringKey.OUTPUT_NAME);
		if (oname == null)
		{
			return null;
		}
		return new String[]{oname};
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
