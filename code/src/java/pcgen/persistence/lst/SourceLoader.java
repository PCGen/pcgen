package pcgen.persistence.lst;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Loads SOURCE
 */
public class SourceLoader
{

	public static void parseLine(LoadContext context, String lstLine,
			URI sourceFile)
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);
		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken().trim();
			try
			{
				if (context.addStatefulToken(colString))
				{
					context.commit();
				}
				else
				{
					context.rollback();
					Logging.replayParsedMessages();
				}
				Logging.clearParseMessages();
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Error parsing source: " + colString
						+ " in: " + sourceFile);
			}
		}
	}
}
