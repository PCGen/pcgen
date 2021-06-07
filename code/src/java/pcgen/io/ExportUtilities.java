/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.Version;
import javafx.stage.FileChooser;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * ExportUtilities is a collection of useful tools for exporting characters.
 * 
 */
public final class ExportUtilities
{
	public static final String PDF_EXPORT_DIR_PROP = "pdfExportDir";
	public static final String HTML_EXPORT_DIR_PROP = "htmlExportDir";

	/**
	 * This class should not be constructed. 
	 */
	private ExportUtilities()
	{
	}

	/**
	 * Retrieve the extension that should be used for the output file. This is base don the template name.  
	 * @param templateFilename The filename of the export template.
	 * @param isPdf Is this an export to a PDF file?
	 * @return The output filename extension.
	 */
	public static String getOutputExtension(String templateFilename, boolean isPdf)
	{
		if (isPdf)
		{
			return "pdf";
		}

		if (templateFilename.endsWith(".ftl"))
		{
			templateFilename = templateFilename.substring(0, templateFilename.length() - 4);
		}
		String extension = StringUtils.substringAfterLast(templateFilename, ".");
		if (StringUtils.isEmpty(extension))
		{
			extension = StringUtils.substringAfterLast(templateFilename, "-");
		}

		return extension;
	}

	/**
	 * Identify if this template will result in a pdf file.
	 * @param templateFile The output template.
	 * @return true if this is a pdf template.
	 */
	public static boolean isPdfTemplate(File templateFile)
	{
		return isPdfTemplate(templateFile.getName());
	}

	/**
	 * Identify if this template will result in a pdf file.
	 * @param templateFilename The name of the output template.
	 * @return true if this is a pdf template.
	 */
	public static boolean isPdfTemplate(String templateFilename)
	{
		String extension = getOutputExtension(templateFilename, false);
		return (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("fo")
			|| extension.equalsIgnoreCase("xslt") || extension.equalsIgnoreCase("xsl"));
	}

	/**
	 * Returns an ObjectWrapper of sufficiently high version for pcgen
	 */
	public static ObjectWrapper getObjectWrapper()
	{
		DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(
				new Version("2.3.28"));
		return defaultObjectWrapperBuilder.build();
	}

	public static File templateToAbsoluteTemplate(String sheetFilterPath, URI relativeURI)
	{
		File osDir;
		String outputSheetDirectory = SettingsHandler.getGameAsProperty().get().getOutputSheetDirectory();
		if (outputSheetDirectory == null)
		{
			osDir = new File(ConfigurationSettings.getOutputSheetsDir());
		}
		else
		{
			osDir = new File(ConfigurationSettings.getOutputSheetsDir(), outputSheetDirectory);
		}
		URI osPath = new File(osDir, sheetFilterPath).toURI();
		return new File(osPath.resolve(relativeURI));
	}

	public static URI[] getValidFiles(Collection<File> myAllTemplates, SheetFilter sheetFilter, boolean doPartyExport)
	{
		IOFileFilter prefixFilter;
		String outputSheetsDir;
		String outputSheetDirectory = SettingsHandler.getGameAsProperty().get().getOutputSheetDirectory();
		IOFileFilter ioFilter = FileFilterUtils.asFileFilter(sheetFilter);
		if (outputSheetDirectory == null)
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + '/' + sheetFilter.getPath();
		}
		else
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + '/' + outputSheetDirectory + '/'
					+ sheetFilter.getPath();
		}

		if (doPartyExport)
		{
			prefixFilter = FileFilterUtils.prefixFileFilter(Constants.PARTY_TEMPLATE_PREFIX);
		} else
		{
			prefixFilter = FileFilterUtils.prefixFileFilter(Constants.CHARACTER_TEMPLATE_PREFIX);
		}
		IOFileFilter filter = FileFilterUtils.and(prefixFilter, ioFilter);

		List<File> files = FileFilterUtils.filterList(filter, myAllTemplates);
		Collections.sort(files);
		URI osPath = new File(outputSheetsDir).toURI();
		URI[] uriList = new URI[files.size()];
		Arrays.setAll(uriList, i -> osPath.relativize(files.get(i).toURI()));
		return uriList;
	}

	public static List<File> getAllTemplates()
	{
		try
		{
			File dir;
			String outputSheetDirectory = SettingsHandler.getGameAsProperty().get().getOutputSheetDirectory();
			if (outputSheetDirectory == null)
			{
				Logging.errorPrint("OUTPUTSHEET|DIRECTORY not defined for game mode " + SettingsHandler.getGameAsProperty().get());
				dir = new File(ConfigurationSettings.getOutputSheetsDir());
			}
			else
			{
				dir = new File(ConfigurationSettings.getOutputSheetsDir(), outputSheetDirectory);
				if (!dir.isDirectory())
				{
					Logging.errorPrint(
							"Unable to find game mode outputsheets at " + dir.getCanonicalPath() + ". Trying base.");
					dir = new File(ConfigurationSettings.getOutputSheetsDir());
				}
			}
			if (!dir.isDirectory())
			{
				Logging.errorPrint("Unable to find outputsheets folder at " + dir.getCanonicalPath() + '.');
				return Collections.emptyList();
			}
			return Files.walk(dir.toPath())
			            .filter(f -> !f.endsWith(".fo"))
			            .map(Path::toFile)
			            .collect(Collectors.toList());
		}
		catch (IOException e)
		{
			Logging.errorPrint("failed to find templates", e);
			return Collections.emptyList();
		}
	}

	public static FileChooser.ExtensionFilter getExtensionFilter(boolean pdf, String extension)
	{
		if (pdf)
		{
			return new FileChooser.ExtensionFilter("PDF Documents", "*.pdf");
		}
		else if ("htm".equalsIgnoreCase(extension) || "html".equalsIgnoreCase(extension))
		{
			return new FileChooser.ExtensionFilter("HTML Documents", "*.html", "*.htm");
		}
		else if ("xml".equalsIgnoreCase(extension))
		{
			return new FileChooser.ExtensionFilter("XML Documents", "*.xml");
		}
		else
		{
			String desc = extension + " Files (*." + extension + ')';
			return new FileChooser.ExtensionFilter(desc, "*." + extension);
		}
	}

	public static URI getDefaultSheet(SheetFilter sheetFilter, CharacterFacade character)
	{
		String outputSheetsDir;
		String outputSheetDirectory = SettingsHandler.getGameAsProperty().get().getOutputSheetDirectory();
		if (outputSheetDirectory == null)
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + '/' + sheetFilter.getPath();
		}
		else
		{
			outputSheetsDir = ConfigurationSettings.getOutputSheetsDir() + '/' + outputSheetDirectory + '/'
					+ sheetFilter.getPath();
		}
		String defaultOutputSheet = character.getDefaultOutputSheet(sheetFilter == SheetFilter.PDF);
		if (StringUtils.isEmpty(defaultOutputSheet))
		{
			defaultOutputSheet = outputSheetsDir + '/'
					+ SettingsHandler.getGameAsProperty().get().getOutputSheetDefault(sheetFilter.getTag());
		}
		URI osPath = new File(outputSheetsDir).toURI();
		return osPath.relativize(new File(defaultOutputSheet).toURI());
	}

	public static File getExportDialogBaseDir(boolean pdf)
	{
		UIPropertyContext context = UIPropertyContext.createContext("ExportDialog");

		String path;
		if (pdf)
		{
			path = context.getProperty(PDF_EXPORT_DIR_PROP);
		}
		else
		{
			path = context.getProperty(HTML_EXPORT_DIR_PROP);
		}
		if (path != null)
		{
			File baseDir = new File(path);
			if (!baseDir.isDirectory())
			{
				return SystemUtils.getUserHome();
			}
			return baseDir;
		}
		return SystemUtils.getUserHome();
	}

	public enum SheetFilter implements FilenameFilter
	{

		HTMLXML("htmlxml", "Standard", "HTM"),
		PDF("pdf", "PDF", "PDF"),
		TEXT("text", "Text", "TXT");
		private final String dirFilter;
		private final String description;
		private final String tag;

		private SheetFilter(String dirFilter, String description, String tag)
		{
			this.dirFilter = dirFilter;
			this.description = description;
			this.tag = tag;
		}

		public String getPath()
		{
			return dirFilter;
		}

		@Override
		public String toString()
		{
			return description;
		}

		public String getTag()
		{
			return tag;
		}

		@Override
		public boolean accept(File dir, String name)
		{
			return dir.getName().equalsIgnoreCase(dirFilter) && !name.endsWith("~");
		}

	}
}
