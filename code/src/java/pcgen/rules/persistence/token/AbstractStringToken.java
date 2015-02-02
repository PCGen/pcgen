package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;

/**
 * Parses a token of the form: <Token Name>:<string>
 * 
 * @param <T>
 *            The type of object on which this AbstractStringToken can be used
 */
public abstract class AbstractStringToken<T extends CDOMObject> extends AbstractNonEmptyToken<T>
{
	/**
	 * This must be overridden to specify the key.
	 * @return The key.
	 */
	protected abstract StringKey stringKey();

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, T obj,
		String value)
	{
		context.getObjectContext().put(obj, stringKey(), value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, T obj)
	{
		String value = context.getObjectContext().getString(obj, stringKey());
		if (value == null)
		{
			return null;
		}
		return new String[] { value };
	}
}
