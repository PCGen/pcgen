package pcgen.rules.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.core.kit.KitAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CDOMSubLineLoader<T> implements CDOMLoader<T>
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

	public void loadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> sources)
	{
		// Track which sources have been loaded already
		Set<CampaignSourceEntry> loadedSources = new HashSet<CampaignSourceEntry>();

		// Load the files themselves as thoroughly as possible
		for (CampaignSourceEntry sourceEntry : sources)
		{
			// Check if the CSE has already been loaded before loading it
			if (!loadedSources.contains(sourceEntry))
			{
				loadLstFile(context, sourceEntry.getURI());
				loadedSources.add(sourceEntry);
			}
		}
	}

	public void loadLstFile(LoadContext context, URI uri)
	{
		StringBuilder dataBuffer;
		context.setSourceURI(uri);

		try
		{
			dataBuffer = LstFileLoader.readFromURI(uri);
		}
		catch (PersistenceLayerException ple)
		{
			String message = PropertyFactory.getFormattedString(
					"Errors.LstFileLoader.LoadError", //$NON-NLS-1$
					uri, ple.getMessage());
			Logging.errorPrint(message);
			return;
		}

		final String aString = dataBuffer.toString();
		String[] fileLines = aString.split(LstFileLoader.LINE_SEPARATOR_REGEXP);

		for (int i = 0; i < fileLines.length; i++)
		{
			String line = fileLines[i];
			if ((line.length() == 0)
					|| (line.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR))
			{
				continue;
			}
			// int sepLoc = line.indexOf('\t');
			// String firstToken;
			// String restOfLine;
			// if (sepLoc == -1)
			// {
			// firstToken = line;
			// restOfLine = null;
			// }
			// else
			// {
			// firstToken = line.substring(0, sepLoc);
			// restOfLine = line.substring(sepLoc + 1);
			// }

			// check for copies, mods, and forgets
			// TODO - Figure out why we need to check SOURCE in this file
			if (line.startsWith("SOURCE")) //$NON-NLS-1$
			{
				// TODO sourceMap = SourceLoader.parseLine(line,
				// sourceEntry.getURI());
			}
			else if (line.startsWith(targetPrefixColon))
			{
				T obj = getCDOMObject(context);
				try
				{
					parseLine(context, obj, line, uri);
				}
				catch (PersistenceLayerException ple)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, ple.getMessage());
					Logging.errorPrint(message);
					Logging.debugPrint("Parse error:", ple); //$NON-NLS-1$
				}
				catch (Throwable t)
				{
					String message = PropertyFactory.getFormattedString(
							"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
							uri, i + 1, t.getMessage());
					Logging.errorPrint(message);
					Logging.errorPrint(PropertyFactory
							.getString("Errors.LstFileLoader.Ignoring"), //$NON-NLS-1$
							t);
				}
			}
			else
			{
				Logging.errorPrint("Unsure what to do with line: " + line
						+ " in file: " + uri);
			}
		}
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

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> languageFiles)
	{
		throw new UnsupportedOperationException();
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
