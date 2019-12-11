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
package pcgen.gui2.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public class ObjectInjector
{

	private final TripleKeyMapToList<URI, String, String, String> campaignData = new TripleKeyMapToList<>();

	private final DoubleKeyMapToList<URI, File, String> fileData = new DoubleKeyMapToList<>();

	private final Collection<Loader> loaders;
	private final File outDir;
	private final File rootDir;

	public ObjectInjector(LoadContext context, File outputDir, File rootDirectory, LSTConverter converter)
	{
		outDir = outputDir;
		rootDir = rootDirectory;
		loaders = converter.getInjectedLoaders();
		for (Loader l : loaders)
		{
			for (URI uri : converter.getInjectedURIs(l))
			{
				for (CDOMObject o : converter.getInjectedObjects(l, uri))
				{
					Class<?> cl = o.getClass();
					String className = StringPClassUtil.getStringFor(cl);
					if ("EQMOD".equals(className))
					{
						className = "EQUIPMOD";
					}
					String fileName = className.toLowerCase() + "_516_conversion.lst";
					context.setExtractURI(uri);
					Collection<String> result = context.unparse(o);
					String line = o.getDisplayName() + "\t" + StringUtil.join(result, "\t");
					if (result != null)
					{
						fileData.addToListFor(uri, getOutputFile(uri, fileName), line);
						campaignData.addToListFor(uri, className, fileName, line);
					}
				}
			}
		}
	}

	private File getOutputFile(URI uri, String fileName)
	{
		File outFile = new File(getNewOutputName(uri).getParentFile(), fileName);
		if (outFile.exists())
		{
			System.err.println("Won't overwrite: " + outFile);
		}
		return outFile;
	}

	private File getNewOutputName(URI uri)
	{
		File in = new File(uri);
		File base = findSubRoot(rootDir, in);
		String relative = in.toString().substring(base.toString().length() + 1);
		File actualRoot = generateCommonRoot(rootDir, outDir);
		String outString = outDir.getAbsolutePath().substring(actualRoot.getAbsolutePath().length());
        return new File(actualRoot, File.separator + outString + File.separator + relative);
	}

	public void writeInjectedObjects(List<Campaign> list) throws IOException
	{
		List<URI> affectedURIs = new ArrayList<>();
		boolean first = true;
		for (Campaign campaign : list)
		{
			for (Loader l : loaders)
			{
				for (CampaignSourceEntry cse : l.getFiles(campaign))
				{
					first &= processWrite(campaign, campaignData, cse, first);
					affectedURIs.add(cse.getURI());
				}
			}
		}
		for (URI uri : affectedURIs)
		{
			Set<File> files = fileData.getSecondaryKeySet(uri);
			if (files != null)
			{
				for (File f : files)
				{
					writeFile(f, fileData.getListFor(uri, f));
				}
			}
		}
	}

	private void writeFile(File f, List<String> lines) throws IOException
	{
		FileWriter writer = new FileWriter(f, StandardCharsets.UTF_8);
		writer.write(getFileHeader());
		for (String line : lines)
		{
			writer.write(line);
			writer.write("\n");
		}
		writer.write(getFileFooter());
		writer.close();
	}

	private String getFileHeader()
	{
        return "# This file was automatically created "
                + "during dataset conversion by PCGen "
                + PCGenPropBundle.getVersionNumber()
                + " on " + LocalDateTime.now(Clock.systemUTC())
                + "\n# This file does not contain SOURCE information\n";
	}

	private String getFileFooter()
	{
		return "\n#\n#EOF\n#\n";
	}

	private boolean processWrite(Campaign campaign, TripleKeyMapToList<URI, String, String, String> toWrite,
		CampaignSourceEntry cse, boolean needHeader) throws IOException
	{
		URI uri = cse.getURI();
		Set<String> classNames = toWrite.getSecondaryKeySet(uri);
		if (classNames != null)
		{
			URI append = campaign.getSourceURI();
			File campaignFile = getNewOutputName(append);
			FileWriter writer = new FileWriter(campaignFile, true);
			if (needHeader)
			{
				writer.write(getCampaignInsertInfo());
			}
			for (String className : classNames)
			{
				for (String fileName : toWrite.getTertiaryKeySet(uri, className))
				{
					CampaignSourceEntry writecse = cse.getRelatedTarget(fileName);
					writer.write(className);
					writer.write(":");
					writer.write(writecse.getLSTformat());
					writer.write("\n");
				}
			}
			writer.close();
			return false;
		}
		return true;
	}

	private String getCampaignInsertInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n#\n# The following file(s) were automatically added");
		sb.append(" during dataset conversion by PCGen ");
		try
		{
			ResourceBundle d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			sb.append(d_properties.getString("VersionNumber"));
		}
		catch (MissingResourceException mre)
		{
			Logging.errorPrint(mre.getMessage(), mre);
		}
		sb.append(" on ").append(LocalDateTime.now(Clock.systemUTC()));
		sb.append("\n#\n");
		return sb.toString();
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

	private File generateCommonRoot(File a, File b)
	{
		/*
		 * FUTURE Think of whether this correctly works for items which may
		 * require a path resolution; is there a flag for that or should there
		 * be another method for that, or just tough luck to users requiring
		 * that?
		 */
		if (a.equals(b))
		{
			return a;
		}
		List<File> al = generateDirectoryHierarchy(a);
		List<File> bl = generateDirectoryHierarchy(b);
		File returnFile = null;
		for (File f : al)
		{
			if (bl.contains(f))
			{
				returnFile = f;
			}
			else
			{
				break;
			}
		}
		return returnFile;
	}

	private List<File> generateDirectoryHierarchy(File a)
	{
		List<File> l = new ArrayList<>();
		while (a != null)
		{
			l.add(0, a);
			a = a.getParentFile();
		}
		return l;
	}
}
