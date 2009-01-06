/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public abstract class AbstractLoader implements Loader
{
	private final LoadContext context;

	Set<URI> written = new HashSet<URI>();
	
	public AbstractLoader(LoadContext lc)
	{
		context = lc;
	}

	/**
	 * This method loads a single LST formatted file.
	 * 
	 * @param sourceEntry
	 *            CampaignSourceEntry containing the absolute file path or the
	 *            URL from which to read LST formatted data.
	 * @throws PersistenceLayerException
	 */
	public String loadLstFile(CampaignSourceEntry sourceEntry)
			throws PersistenceLayerException
	{
		StringBuilder dataBuffer;

		URI uri = sourceEntry.getURI();
		context.setSourceURI(uri);
		context.setExtractURI(uri);
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
			return "";
		}

		final String aString = dataBuffer.toString();

		String[] fileLines = aString.replaceAll("\r\n", "\r").split(
				LstFileLoader.LINE_SEPARATOR_REGEXP);

		StringBuilder result = new StringBuilder(dataBuffer.length());

		for (int line = 0; line < fileLines.length; line++)
		{
			String lineString = fileLines[line];
			if ((lineString.length() == 0)
					|| (lineString.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR)
					|| lineString.startsWith("SOURCE"))
			{
				result.append(lineString);
			}
			else
			{
				process(result, line, lineString, sourceEntry);
			}
			result.append("\n");
		}
		return result.toString();
	}

	public void load(File root, String outDir, Campaign c)
			throws PersistenceLayerException
	{
		load(root, outDir, getFiles(c));
	}

	protected abstract List<CampaignSourceEntry> getFiles(Campaign c);

	public void load(File root, String outDir, List<CampaignSourceEntry> cses)
			throws PersistenceLayerException
	{
		for (CampaignSourceEntry cse : cses)
		{
			URI uri = cse.getURI();
			if (written.contains(uri))
			{
				continue;
			}
			File in = new File(uri.getPath().substring(1));
			File base = findSubRoot(root, in);
			String relative = in.toString().substring(
					base.toString().length() + 1);
			File outFile = new File(root, File.separator + outDir
					+ File.separator + relative);
			if (outFile.exists())
			{
				System.err.println("Won't overwrite: " + outFile);
			}
			written.add(uri);
			ensureParents(outFile.getParentFile());
			String file = loadLstFile(cse);
			try
			{
				FileWriter fis = new FileWriter(outFile);
				fis.write(file);
				fis.close();
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public abstract void process(StringBuilder result, int line,
			String lineString, CampaignSourceEntry source)
			throws PersistenceLayerException;

	protected LoadContext getContext()
	{
		return context;
	}

	private File findSubRoot(File root, File in)
	{
		if (in.getParentFile() == null)
		{
			return null;
		}
		if (in.getParentFile().getAbsolutePath().equals(root.getAbsolutePath()))
		{
			return in;
		}
		return findSubRoot(root, in.getParentFile());
	}

	private void ensureParents(File parentFile)
	{
		if (!parentFile.exists())
		{
			ensureParents(parentFile.getParentFile());
			parentFile.mkdir();
		}
	}
}
