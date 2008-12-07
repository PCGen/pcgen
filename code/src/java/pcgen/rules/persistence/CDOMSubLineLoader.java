package pcgen.rules.persistence;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class CDOMSubLineLoader<T>
{

	private final Class<T> targetClass;
	private final String subTokenType;
	private final String targetPrefix;
	private final String targetPrefixColon;

	// private final int prefixLength;

	public CDOMSubLineLoader(String tokenType, String prefix, Class<T> cl)
	{
		subTokenType = tokenType;
		targetPrefix = prefix;
		targetClass = cl;
		targetPrefixColon = prefix + ":";
		// prefixLength = targetPrefixColon.length();
	}

	public boolean parseLine(LoadContext context, T obj, String val, URI source)
			throws PersistenceLayerException
	{
		if (val == null)
		{
			return true;
		}
		boolean returnValue = true;
		StringTokenizer st = new StringTokenizer(val, "\t");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken().trim();
			int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				returnValue &= false;
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				returnValue &= false;
				continue;
			}
			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processSubToken(obj, subTokenType, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else
			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
				returnValue &= false;
			}
		}
		return returnValue;
	}

	public T getCDOMObject(LoadContext context)
	{
		try
		{
			T obj = targetClass.newInstance();
			return obj;
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	public String getPrefix()
	{
		return targetPrefix;
	}

	public Class<T> getLoadedClass()
	{
		return targetClass;
	}

	public void unloadObject(LoadContext lc, T object, StringBuilder sb)
	{
		String[] unparse = lc.unparse(object, subTokenType);
		StringBuilder temp = new StringBuilder();
		if (unparse != null)
		{
			for (String s : unparse)
			{
				if (s.startsWith(targetPrefixColon))
				{
					sb.append(s);
				}
				else
				{
					temp.append('\t');
					temp.append(s);
				}
			}
			sb.append(temp);
			sb.append('\n');
		}
	}
}
