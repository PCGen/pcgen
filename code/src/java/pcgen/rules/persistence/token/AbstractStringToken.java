package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;

/**
 * Parses a token of the form: <Token Name>:<string>
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

	/*
	 * This is the next step but won't be done until everythings converted or the round robin tests wont
	 * detect refactoring problems!
	public String[] unparse(LoadContext context, T obj)
	{
		String title =
				context.getObjectContext().getString(obj, stringKey());
		if (title == null)
		{
			return null;
		}
		return new String[]{title};
	}
	*/
}
