package pcgen.gui.converter;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public abstract class AbstractTokenLoader extends AbstractLoader
{
	/** The String that separates fields in the file. */
	public static final String FIELD_SEPARATOR = "\t"; //$NON-NLS-1$

	public AbstractTokenLoader(LoadContext lc)
	{
		super(lc);
	}

	public <T extends CDOMObject> void processTokens(Class<T> cl,
			StringBuilder result, int line, String lineString)
			throws PersistenceLayerException
	{
		String[] tokens = lineString.split(FIELD_SEPARATOR);
		result.append(tokens[0]);
		for (int tok = 1; tok < tokens.length; tok++)
		{
			String token = tokens[tok];
			if (token.length() == 0)
			{
				result.append(FIELD_SEPARATOR);
				continue;
			}

			T obj = getContext().ref.constructCDOMObject(cl, line + "Test"
					+ tok);
			processToken(result, obj, null, token);
		}
	}

	protected void processToken(StringBuilder result, CDOMObject obj,
			CDOMObject alt, String token) throws PersistenceLayerException
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: "
					+ token);
			return;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: " + token);
			return;
		}

		String key = token.substring(0, colonLoc);
		if (key.equals("CATEGORY") || key.equals("ABB") || key.equals("KEY"))
		{
			// Just process over these magical tokens for now
			result.append(FIELD_SEPARATOR);
			result.append(token);
			return;
		}
		String value = (colonLoc == token.length() - 1) ? null : token
				.substring(colonLoc + 1);
		if (getContext().processToken(obj, key, value))
		{
			getContext().commit();
		}
		else
		{
			Logging.replayParsedMessages();
		}
		Logging.clearParseMessages();
		Collection<String> output = getContext().unparse(obj);
		if (output == null || output.isEmpty())
		{
			if (alt != null)
			{
				output = getContext().unparse(alt);
			}
			if (output == null || output.isEmpty())
			{
				// Uh Oh
				Logging.errorPrint("Unable to unparse: " + token);
				result.append(FIELD_SEPARATOR);
				result.append(token);
				return;
			}
		}
		for (String s : output)
		{
			result.append(FIELD_SEPARATOR);
			result.append(s);
		}
	}

}
