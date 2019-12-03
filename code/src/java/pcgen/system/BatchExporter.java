/*
 * Copyright James Dempsey, 2012
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
package pcgen.system;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.gui2.UIPropertyContext;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;
import pcgen.io.ExportUtilities;
import pcgen.io.PCGFile;
import pcgen.persistence.SourceFileLoader;
import pcgen.util.Logging;
import pcgen.util.fop.FopTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code BatchExporter} manages character sheet output to a
 * file. It is capable of outputting either a single character or a party 
 * to an output file based on a suitable export template.
 * <p>
 * BatchExporter is intended to be used for both batch export of characters 
 * and as a library for other code to easily provide output capability. When
 * used in batch mode an instance should be created for the template and 
 * one of the export methods called. When used as a library the static methods
 * should be used and supplied with preloaded characters.  
 *
 * 
 */
public class BatchExporter
{

	private final String exportTemplateFilename;
	private final UIDelegate uiDelegate;
	private final boolean isPdf;

	/**
	 * Create a new instance of BatchExporter for use with a particular export
	 * template.
	 *   
	 * @param exportTemplateFilename The path to the export template.
	 * @param uiDelegate The object through which to report any issues to the user.
	 */
	BatchExporter(String exportTemplateFilename, UIDelegate uiDelegate)
	{
		this.exportTemplateFilename = exportTemplateFilename;
		this.uiDelegate = uiDelegate;

		isPdf = ExportUtilities.isPdfTemplate(exportTemplateFilename);
	}

	/**
	 * Export a character sheet for the character to the output file using the 
	 * pre-registered template. If the output file is null then a default file 
	 * will be used based on the character file name and the type of export 
	 * template in use. If the output file exists it will be overwritten.
	 * <p>
	 * This method will load the required data for the character, load the 
	 * character and then export the character sheet.
	 * 
	 * @param characterFilename The path to the character PCG file.
	 * @param outputFile The path to the output file to be created. May be null.
	 * @return true if the export was successful, false if it failed in some way.
	 */
	boolean exportCharacter(String characterFilename, String outputFile)
	{
		File file = new File(characterFilename);
		if (!PCGFile.isPCGenCharacterFile(file))
		{
			Logging.errorPrint("Invalid character file specified: " + file.getAbsolutePath());
			return false;
		}
		String outFilename = outputFile;
		if (outFilename == null)
		{
			outFilename = generateOutputFilename(characterFilename);
		}
		Logging.log(Logging.INFO,
			"Started export of " + file.getAbsolutePath() + " using " + exportTemplateFilename + " to " + outFilename);

		// Load data
		SourceSelectionFacade sourcesForCharacter = CharacterManager.getRequiredSourcesForCharacter(file, uiDelegate);
		Logging.log(Logging.INFO, "Loading sources " + sourcesForCharacter.getCampaigns() + " using game mode "
			+ sourcesForCharacter.getGameMode());
		SourceFileLoader loader = new SourceFileLoader(sourcesForCharacter, uiDelegate);
		loader.run();

		// Load character
		CharacterFacade character = CharacterManager.openCharacter(file, uiDelegate, loader.getDataSetFacade());
		if (character == null)
		{
			return false;
		}

		// Export character
		File templateFile = new File(exportTemplateFilename);
		File outFile = new File(outFilename);
		if (isPdf)
		{
			return exportCharacterToPDF(character, outFile, templateFile);
		}
		else
		{
			return exportCharacterToNonPDF(character, outFile, templateFile);
		}
	}

	/**
	 * Export a party sheet for the party to the output file using the 
	 * pre-registered template. If the output file is null then a default file 
	 * will be used based on the party file name and the type of export 
	 * template in use. If the output file exists it will be overwritten.
	 * <p>
	 * This method will load the required data for the party, load the characters 
	 * in the party and then export the party sheet.
	 * 
	 * @param partyFilename The path to the party PCP file.
	 * @param outputFile The path to the output file to be created. May be null.
	 * @return true if the export was successful, false if it failed in some way.
	 */
	boolean exportParty(String partyFilename, String outputFile)
	{
		File file = new File(partyFilename);
		if (!PCGFile.isPCGenPartyFile(file))
		{
			Logging.errorPrint("Invalid party file specified: " + file.getAbsolutePath());
			return false;
		}
		String outFilename = outputFile;
		if (outFilename == null)
		{
			outFilename = generateOutputFilename(partyFilename);
		}
		Logging.log(Logging.INFO, "Started export of party " + file.getAbsolutePath() + " using "
			+ exportTemplateFilename + " to " + outFilename);

		// Load data
		SourceSelectionFacade sourcesForCharacter = CharacterManager.getRequiredSourcesForParty(file, uiDelegate);
		SourceFileLoader loader = new SourceFileLoader(sourcesForCharacter, uiDelegate);
		loader.run();

		// Load party
		PartyFacade party = CharacterManager.openParty(file, uiDelegate, loader.getDataSetFacade());

		// Export party
		File templateFile = new File(exportTemplateFilename);
		File outFile = new File(outFilename);
		if (isPdf)
		{
			return exportPartyToPDF(party, outFile, templateFile);
		}
		else
		{
			return exportPartyToNonPDF(party, outFile, templateFile);
		}
	}

	/**
	 * Write a PDF character sheet for the character to the output file. The 
	 * character sheet will be built according to the template file. If the 
	 * output file exists it will be overwritten.
	 *    
	 * @param character The already loaded character to be output.
	 * @param outFile The file to which the character sheet is to be written. 
	 * @param templateFile The file that has the export template definition.  
	 * @return true if the export was successful, false if it failed in some way.
	 */
	public static boolean exportCharacterToPDF(CharacterFacade character, File outFile, File templateFile)
	{

		String templateExtension = FilenameUtils.getExtension(templateFile.getName());

		boolean isTransformTemplate =
				"xslt".equalsIgnoreCase(templateExtension) || "xsl".equalsIgnoreCase(templateExtension);

		boolean useTempFile =
				PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF, false);
		String outFileName = FilenameUtils.removeExtension(outFile.getAbsolutePath());
		File tempFile = new File(outFileName + (isTransformTemplate ? ".xml" : ".fo"));
		try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(outFile));
		     ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		     OutputStream exportOutput = useTempFile
					//Output to both the byte stream and to the temp file.
					? new TeeOutputStream(byteOutputStream, new FileOutputStream(tempFile)) : byteOutputStream)
		{
			FopTask task;
			if (isTransformTemplate)
			{
				exportCharacter(character, exportOutput);
				InputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, templateFile, fileStream);
			}
			else
			{
				exportCharacter(character, templateFile, exportOutput);
				InputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, null, fileStream);
			}
			character.setDefaultOutputSheet(true, templateFile);
			task.run();
			if (StringUtils.isNotBlank(task.getErrorMessages()))
			{
				Logging.errorPrint("BatchExporter.exportCharacterToPDF failed: " //$NON-NLS-1$
					+ task.getErrorMessages());
				return false;
			}
		}
		catch (final IOException | ExportException e)
		{
			Logging.errorPrint("BatchExporter.exportCharacterToPDF failed", e); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * Write a non PDF (e.g. html, text) character sheet for the character to 
	 * the output file. The character sheet will be built according to the 
	 * template file. If the output file exists it will be overwritten.
	 *    
	 * @param character The already loaded character to be output.
	 * @param outFile The file to which the character sheet is to be written. 
	 * @param templateFile The file that has the export template definition.  
	 * @return true if the export was successful, false if it failed in some way.
	 */
	public static boolean exportCharacterToNonPDF(CharacterFacade character, File outFile, File templateFile)
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8)))
		{
			character.export(ExportHandler.createExportHandler(templateFile), bw);
			character.setDefaultOutputSheet(false, templateFile);
			return true;
		} catch (final IOException e)
		{
			Logging.errorPrint("Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
		} catch (final ExportException e)
		{
			// Error will already be reported to the log
			return false;
		}
	}

	/**
	 * Get a temporary file name for outputting a character using a particular
	 * output template.
	 * @param templateFile The output template that will be used.
	 * @return The temporary file, or null if it could not be created.
	 */
	public static File getTempOutputFilename(File templateFile)
	{
		String extension =
				ExportUtilities.getOutputExtension(templateFile.getName(), ExportUtilities.isPdfTemplate(templateFile));

		try
		{
			// create a temporary file to view the character output
			return File.createTempFile(Constants.TEMPORARY_FILE_NAME, '.' + extension, SettingsHandler.getTempPath());
		}
		catch (final IOException ioe)
		{
			ShowMessageDelegate.showMessageDialog("Could not create temporary preview file.", "PCGen",
				MessageType.ERROR);
			Logging.errorPrint("Could not create temporary preview file.", ioe);
			return null;
		}

	}

	/**
	 * Write a PDF party sheet for the characters in the party to the output 
	 * file. The party sheet will be built according to the template file. If  
	 * the output file exists it will be overwritten.
	 *    
	 * @param party The already loaded party of characters to be output.
	 * @param outFile The file to which the party sheet is to be written. 
	 * @param templateFile The file that has the export template definition.  
	 * @return true if the export was successful, false if it failed in some way.
	 */
	public static boolean exportPartyToPDF(PartyFacade party, File outFile, File templateFile)
	{
		// We want the non pdf extension here for the intermediate file.
		String templateExtension = ExportUtilities.getOutputExtension(templateFile.getName(), false);
		boolean isTransformTemplate =
				"xslt".equalsIgnoreCase(templateExtension) || "xsl".equalsIgnoreCase(templateExtension);

		boolean useTempFile =
				PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF, false);
		String outFileName = FilenameUtils.removeExtension(outFile.getAbsolutePath());
		File tempFile = new File(outFileName + (isTransformTemplate ? ".xml" : ".fo"));
		try (BufferedOutputStream fileStream = new BufferedOutputStream(new FileOutputStream(outFile));
				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
				OutputStream exportOutput = useTempFile
					//Output to both the byte stream and to the temp file.
					? new TeeOutputStream(byteOutputStream, new FileOutputStream(tempFile)) : byteOutputStream)
		{
			FopTask task;
			if (isTransformTemplate)
			{
				exportParty(party, exportOutput);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, templateFile, fileStream);
			}
			else
			{
				SettingsHandler.setSelectedPartyPDFOutputSheet(templateFile.getAbsolutePath());

				exportParty(party, templateFile, exportOutput);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
				task = FopTask.newFopTask(inputStream, null, fileStream);
			}
			task.run();
		}
		catch (final IOException | ExportException e)
		{
			Logging.errorPrint("BatchExporter.exportPartyToPDF failed", e);
			return false;
		}
		return true;
	}

	/**
	 * Write a non PDF (e.g. html, text) party sheet for the characters in the 
	 * party to the output file. The party sheet will be built according to the 
	 * template file. If the output file exists it will be overwritten.
	 *    
	 * @param party The already loaded party of characters to be output.
	 * @param outFile The file to which the party sheet is to be written. 
	 * @param templateFile The file that has the export template definition.  
	 * @return true if the export was successful, false if it failed in some way.
	 */
	public static boolean exportPartyToNonPDF(PartyFacade party, File outFile, File templateFile)
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8)))
		{
			party.export(ExportHandler.createExportHandler(templateFile), bw);
			return true;
		}
		catch (final IOException e)
		{
			Logging.errorPrint("Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
		}
	}

	/**
	 * Write a party sheet for the characters in the party to the outputStream. The party sheet will
	 * be selected based on the selected game mode and pcgen preferences.
	 *
	 * @param party the party to be output
	 * @param outputStream the stream to output the party sheet to.
	 * @throws IOException
	 * @throws ExportException
	 */
	private static void exportParty(PartyFacade party, OutputStream outputStream) throws IOException, ExportException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)))
		{
			for (final CharacterFacade character : party)
			{
				File templateFile = getXMLTemplate(character);
				character.export(ExportHandler.createExportHandler(templateFile), bw);
			}
		}
	}

	/**
	 * Write a party sheet for the characters in the party to the ouputstream. The party sheet will
	 * be built according to the template file.
	 *
	 * @param party the party to be output
	 * @param templateFile The file that has the export template definition.
	 * @param outputStream the stream to output the party sheet to.
	 * @throws IOException
	 * @throws ExportException
	 */
	private static void exportParty(PartyFacade party, File templateFile, OutputStream outputStream)
		throws IOException, ExportException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)))
		{
			for (final CharacterFacade character : party)
			{
				character.export(ExportHandler.createExportHandler(templateFile), bw);
			}
		}
	}

	/**
	 * Remove any temporary xml files produced while outputting characters. 
	 */
	static void removeTemporaryFiles()
	{
		final boolean cleanUp = UIPropertyContext.getInstance().initBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, true);

		if (!cleanUp)
		{
			return;
		}

		final String aDirectory = SettingsHandler.getTempPath() + File.separator;
		new File(aDirectory).list((aFile, aString) -> {
			try
			{
				if (aString.startsWith(Constants.TEMPORARY_FILE_NAME))
				{
					final File tf = new File(aFile, aString);
					tf.delete();
				}
			}
			catch (final Exception e)
			{
				Logging.errorPrint("removeTemporaryFiles", e);
			}

			return false;
		});
	}

	/**
	 * Exports a character to an OuputStream using the default template for the character's game
	 * mode. This is more generic
	 * method than writing to a file and the same effect can be achieved by passing in a
	 * FileOutputStream.
	 *
	 * @param character the loaded CharacterFacade to export
	 * @param outputStream the OutputStream that the character will be exported to
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ExportException if there is an export exception
	 */
	public static void exportCharacter(CharacterFacade character, OutputStream outputStream)
		throws IOException, ExportException
	{
		exportCharacter(character, getXMLTemplate(character), outputStream);
	}

	/**
	 * Exports a character to an OutputStream using the given template file. The template file is
	 * used to determine the type of character sheet that will be generated. This is more generic
	 * method than writing to a file and the same effect can be achieved by passing in a
	 * FileOutputStream.
	 *
	 * @param character the loaded CharacterFacade to export
	 * @param templateFile the export template file for the ExportHandler to use
	 * @param outputStream the OutputStream that the character will be exported to
	 * @throws IOException
	 * @throws ExportException
	 */
	private static void exportCharacter(CharacterFacade character, File templateFile, OutputStream outputStream)
		throws IOException, ExportException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)))
		{
			character.export(ExportHandler.createExportHandler(templateFile), bw);
		}
	}

	private static File getXMLTemplate(CharacterFacade character)
	{
		Path path = Path.of(ConfigurationSettings.getSystemsDir(), "gameModes",
				character.getDataSet().getGameMode().getName(), "base.xml.ftl");
		File template = new File(path.toUri());
		if (!template.exists())
		{
			template = new File(ConfigurationSettings.getOutputSheetsDir(), "base.xml.ftl");
		}
		return template;
	}

	/**
	 * Create a default character sheet output file name based on the export
	 * template type and the character file name. The output file will be 
	 * in the same folder as the character file.
	 * 
	 * @param characterFilename The path to the character PCG file.
	 * @return The default output file name.
	 */
	private String generateOutputFilename(String characterFilename)
	{
		File charFile = new File(characterFilename);
		String charname = charFile.getName();
		String extension = ExportUtilities.getOutputExtension(exportTemplateFilename, isPdf);
		String outputName = charname.substring(0, charname.lastIndexOf('.')) + '.' + extension;
		return new File(charFile.getParent(), outputName).getAbsolutePath();
	}
}
