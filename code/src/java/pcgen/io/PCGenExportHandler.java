package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class PCGenExportHandler extends ExportHandler
{
	/**
	 * Constructor.  Populates the token map (a list of possible output tokens) and
	 * sets the character sheet template we are using.
	 *
	 * @param templateFile the template to use while exporting.
	 */
	PCGenExportHandler(File templateFile)
	{
		super(templateFile);
	}

	@Override
	public void write(PlayerCharacter aPC, BufferedWriter out)
	{
		// Set an output filter based on the type of template in use.
		FileAccess.setCurrentOutputFilter(getTemplateFile().getName());

		try (FileInputStream fis = new FileInputStream(getTemplateFile());
			 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			 BufferedReader br = new BufferedReader(isr))
		{
			// A Buffer to hold the result of the preparation
			CharSequence template = prepareTemplate(br);

			// Create a tokenizer based on EOL characters
			// 03-Nov-2008 Karianna, changed to use line separator instead of /r/n
			final StringTokenizer tokenizer = new StringTokenizer(template.toString(), Constants.LINE_SEPARATOR, false);

			// Get FOR loops and IIF statements
			final FORNode root = parseFORsAndIIFs(tokenizer);

			// TODO Not sure what these lines are for
			loopVariables.put(null, "0");
			existsOnly = false;

			// Ensure that there 'are more items to process'
			noMoreItems = false;

			// Now actually process the FOR loops in the template
			// and then clear the loop variables
			loopFOR(root, 0, 0, 1, out, aPC);
			loopVariables.clear();
		} catch (IOException exc)
		{
			Logging.errorPrint("Error in ExportHandler::write", exc);
		}
	}


	/**
	 * A helper method to prepare the template for exporting
	 *
	 * Read lines from the character sheet template and store them in a buffer
	 * with empty lines replaced by a space character and || replaced by | |
	 *
	 * @param br The BufferedReader containing the template
	 * @throws IOException
	 */
	private static StringBuilder prepareTemplate(BufferedReader br) throws IOException
	{
		// A pattern to replace || with | | to stop StringTokenizer from merging them
		Pattern pat = Pattern.compile(Pattern.quote("||"));
		String rep = Matcher.quoteReplacement("| |");

		// Hold the results of the preparation
		StringBuilder inputLine = new StringBuilder();

		String aString = br.readLine();
		while (aString != null)
		{
			// Karianna 29/11/2008 - No Longer replace blank lines with spaces,
			// doesn't seem to be needed

			// If the line is blank then append a space character
			//if (aString.length() == 0)
			//{
			//inputLine.append(' ');
			//}
			//else
			//{
			// Adjacent separators get merged by StringTokenizer,
			// so we break them up here, e.g. Change || to | |
			Matcher mat = pat.matcher(aString);
			inputLine.append(mat.replaceAll(rep));
			//}

			inputLine.append(Constants.LINE_SEPARATOR);
			aString = br.readLine();
		}
		return inputLine;
	}


	/**
	 * Parse the tokens for |FOR and |IIF sections and plain text sections
	 *
	 * @param tokens
	 * @return a FORNode object
	 */
	private FORNode parseFORsAndIIFs(StringTokenizer tokens)
	{
		// A FORNode that will hold a 'tree' of all of the FOR and IIF sections found
		final FORNode root = new FORNode(null, "0", "0", "1", false);

		while (tokens.hasMoreTokens())
		{
			final String line = tokens.nextToken();

			// If we detect a |FOR then add it as a child, if it has its own children
			// then add those as well
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");

				if (newFor.countTokens() > 1)
				{
					newFor.nextToken();

					if (newFor.nextToken().startsWith("%"))
					{
						root.addChild(parseFORs(line, tokens));
					}
					else
					{
						root.addChild(line);
					}
				}
				else
				{
					root.addChild(line);
				}
			}
			// If |IIF( is found and there is no ',' character on that line
			// then add it as a child
			else if (line.startsWith("|IIF(") && (line.lastIndexOf(',') == -1))
			{
				String expr = line.substring(5, line.lastIndexOf(')'));
				root.addChild(parseIIFs(expr, tokens));
			}
			// Else it's plain text so then just add it
			else
			{
				root.addChild(line);
			}
		}

		return root;
	}

}
