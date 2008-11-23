/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 *
 */
public class KeyLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "KEY";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.ref.reassociateKey(value, obj);
		context.obj.put(obj, StringKey.KEY_NAME, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String key =
				context.getObjectContext().getString(obj, StringKey.KEY_NAME);
		if (key == null)
		{
			return null;
		}
		return new String[]{key};
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
