/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.testsupport;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

public class CDOMTokenLoader<T extends CDOMObject> implements CDOMLoader<T>
{

	private final List<ModEntry> copyList = new ArrayList<ModEntry>();

	private final Class<T> targetClass;

	public CDOMTokenLoader(Class<T> cl)
	{
		targetClass = cl;
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
			}
			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(obj, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
				returnValue &= false;
			}
			Logging.clearParseMessages();
		}
		return returnValue;
	}

	public void loadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> sources)
	{
		copyList.clear();

		// Track which sources have been loaded already
		Set<CampaignSourceEntry> loadedSources = new HashSet<CampaignSourceEntry>();

		// Load the files themselves as thoroughly as possible
		for (CampaignSourceEntry sourceEntry : sources)
		{
			// Check if the CSE has already been loaded before loading it
			if (!loadedSources.contains(sourceEntry))
			{
				loadLstFile(context, sourceEntry);
				loadedSources.add(sourceEntry);
			}
		}

		processCopies(context);

		// Now handle .MOD items
		// TODO processMods();

		// Finally, forget the .FORGET items
		// TODO processForgets();
	}

	private void loadLstFile(LoadContext context,
			CampaignSourceEntry sourceEntry)
	{
		URI uri = sourceEntry.getURI();
		context.setSourceURI(uri);
		StringBuilder dataBuffer;

		try
		{
			dataBuffer = LstFileLoader.readFromURI(uri);
		}
		catch (PersistenceLayerException ple)
		{
			String message = LanguageBundle.getFormattedString(
					"Errors.LstFileLoader.LoadError", //$NON-NLS-1$
					uri, ple.getMessage());
			Logging.errorPrint(message);
			return;
		}

		final String aString = dataBuffer.toString();
		String[] fileLines = aString.split(LstFileLoader.LINE_SEPARATOR_REGEXP);

		for (int i = 0; i < fileLines.length; i++)
		{
			parseFullLine(context, i, fileLines[i], sourceEntry);
		}
	}

	public void parseFullLine(LoadContext context, int i, String line,
			CampaignSourceEntry sourceEntry)
	{
		if ((line.length() == 0)
				|| (line.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR))
		{
			return;
		}
		URI uri = sourceEntry.getURI();
		int sepLoc = line.indexOf('\t');
		String firstToken;
		String restOfLine;
		if (sepLoc == -1)
		{
			firstToken = line;
			restOfLine = null;
		}
		else
		{
			firstToken = line.substring(0, sepLoc);
			restOfLine = line.substring(sepLoc + 1);
		}

		// check for copies, mods, and forgets
		// TODO - Figure out why we need to check SOURCE in this file
		if (line.startsWith("SOURCE")) //$NON-NLS-1$
		{
			// TODO sourceMap = SourceLoader.parseLine(line,
			// sourceEntry.getURI());
		}
		else if (firstToken.indexOf(".COPY") > 0)
		{
			copyList.add(new ModEntry(sourceEntry, line, i + 1));
		}
		else if (firstToken.indexOf(".MOD") > 0)
		{
			// TODO modEntryList.add(new ModEntry(sourceEntry, line, i +
			// 1));
		}
		else if (firstToken.indexOf(".FORGET") > 0)
		{
			// TODO forgetLineList.add(line);
		}
		else
		{
			try
			{
				parseLine(context, getCDOMObject(context, firstToken),
						restOfLine, uri);
			}
			catch (PersistenceLayerException ple)
			{
				String message = LanguageBundle.getFormattedString(
						"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
						uri, i + 1, ple.getMessage());
				Logging.errorPrint(message);
				Logging.debugPrint("Parse error:", ple); //$NON-NLS-1$
			}
			catch (Throwable t)
			{
				String message = LanguageBundle.getFormattedString(
						"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
						uri, i + 1, t.getMessage());
				Logging.errorPrint(message);
				Logging.errorPrint(LanguageBundle
						.getString("Errors.LstFileLoader.Ignoring")
						+ "\n" + t);
				t.printStackTrace();
			}
		}
	}

	protected T getCDOMObject(LoadContext context, String firstToken)
	{
		return context.ref.constructCDOMObject(targetClass, firstToken);
	}

	private void processCopies(LoadContext context)
	{
		for (ModEntry me : copyList)
		{
			String line = me.lstLine;
			int sepLoc = line.indexOf('\t');
			String firstToken;
			String restOfLine;
			if (sepLoc == -1)
			{
				firstToken = line;
				restOfLine = null;
			}
			else
			{
				firstToken = line.substring(0, sepLoc);
				restOfLine = line.substring(sepLoc + 1);
			}
			int copyLoc = firstToken.indexOf(".COPY=");
			String sourceName = firstToken.substring(0, copyLoc);
			String copyName = firstToken.substring(copyLoc + 6);
			T sourceObj = context.ref.silentlyGetConstructedCDOMObject(
					targetClass, sourceName);
			if (sourceObj == null)
			{
				Logging.errorPrint("Attempt to copy " + targetClass.getName()
						+ " " + sourceName + " but it was never built");
				continue;
			}
			T copy = context.cloneConstructedCDOMObject(sourceObj, copyName);
			try
			{
				parseLine(context, copy, restOfLine, me.source.getURI());
			}
			catch (PersistenceLayerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class ModEntry
	{

		public final CampaignSourceEntry source;
		public final String lstLine;
		public final int lineNumber;

		public ModEntry(CampaignSourceEntry sourceEntry, String line, int i)
		{
			source = sourceEntry;
			lstLine = line;
			lineNumber = i;
		}

	}

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> files)
	{
		for (CampaignSourceEntry cse : files)
		{
			lc.setExtractURI(cse.getURI());
			URI writeURI = cse.getWriteURI();
			File f = new File(writeURI);
			ensureCreated(f.getParentFile());
			try
			{
				PrintWriter pw = new PrintWriter(f);
				Collection<T> objects = lc.ref
						.getConstructedCDOMObjects(targetClass);
				Set<String> set = new TreeSet<String>();
				for (T obj : objects)
				{
					String s = unparseObject(lc, cse, obj);
					if (s != null)
					{
						set.add(s);
					}
				}
				for (String s : set)
				{
					pw.println(s);
				}
				pw.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String unparseObject(LoadContext lc, CampaignSourceEntry cse, T obj)
	{
		String unparse = StringUtil.join(lc.unparse(obj), "\t");
		/*
		 * TODO This isn't good enough - you can .MOD in the
		 * original file, and that needs to be remembered
		 */
		if (cse.getURI().equals(obj.getSourceURI()))
		{
			return obj.getDisplayName() + '\t' + unparse;
		}
		else if (unparse.length() != 0)
		{
			return obj.getKeyName() + ".MOD\t" + unparse;
		}
		return null;
	}

	private boolean ensureCreated(File rec)
	{
		if (!rec.exists())
		{
			if (!ensureCreated(rec.getParentFile()))
			{
				return false;
			}
			return rec.mkdir();
		}
		return true;
	}
}
