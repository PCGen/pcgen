/*
 * BatchExporter.java
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
 * Created on 20/01/2012 9:33:45 AM
 *
 * $Id$
 */
package pcgen.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import pcgen.core.SettingsHandler;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.PartyFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.io.ExportHandler;
import pcgen.io.PCGFile;
import pcgen.persistence.SourceFileLoader;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;

/**
 * The Class <code>BatchExporter</code> manages character sheet output to a 
 * file. It is capable of outputting either a single character or a party 
 * to an output file based on a suitable export template.
 * <p>
 * BatchExporter is intended to be used for both batch export of characters 
 * and as a library for other code to easily provide output capability. When
 * used in batch mode an instance should be created for the the temolate and 
 * one of the export methods called. When used as a library the static methods
 * should be used and supplied with preloaded characters.  
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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
	public BatchExporter(String exportTemplateFilename, UIDelegate uiDelegate)
	{
		this.exportTemplateFilename = exportTemplateFilename;
		this.uiDelegate = uiDelegate;

		String extension =
				StringUtils.substringAfterLast(exportTemplateFilename, ".");
		isPdf =
				extension.equalsIgnoreCase("xslt")
					|| extension.equalsIgnoreCase("xsl")
					|| extension.equalsIgnoreCase("fo");
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
	public boolean exportCharacter(String characterFilename, String outputFile)
	{
		File file = new File(characterFilename);
		if (!PCGFile.isPCGenCharacterFile(file))
		{
			Logging.errorPrint("Invalid party file specified: "
				+ file.getAbsolutePath());
			return false;
		}
		String outFilename = outputFile;
		if (outFilename == null)
		{
			outFilename = generateOutputFilename(characterFilename);
		}
		Logging.log(Logging.INFO, "Started export of " + file.getAbsolutePath()
			+ " using " + exportTemplateFilename + " to " + outFilename);

		// Load data
		SourceSelectionFacade sourcesForCharacter =
				CharacterManager.getRequiredSourcesForCharacter(file,
					uiDelegate);
		Logging.log(Logging.INFO, "Loading sources " + sourcesForCharacter.getCampaigns()
			+ " using game mode " + sourcesForCharacter.getGameMode());
		SourceFileLoader loader = new SourceFileLoader(sourcesForCharacter, uiDelegate);
		loader.execute();

		// Load character
		CharacterFacade character =
				CharacterManager.openCharacter(file, uiDelegate,
					loader.getDataSetFacade());

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
	public boolean exportParty(String partyFilename, String outputFile)
	{
		File file = new File(partyFilename);
		if (!PCGFile.isPCGenPartyFile(file))
		{
			Logging.errorPrint("Invalid party file specified: "
				+ file.getAbsolutePath());
			return false;
		}
		String outFilename = outputFile;
		if (outFilename == null)
		{
			outFilename = generateOutputFilename(partyFilename);
		}
		Logging.log(Logging.INFO,
			"Started export of party " + file.getAbsolutePath() + " using "
				+ exportTemplateFilename + " to " + outFilename);

		// Load data
		SourceSelectionFacade sourcesForCharacter =
				CharacterManager.getRequiredSourcesForParty(file, uiDelegate);
		SourceFileLoader loader = new SourceFileLoader(sourcesForCharacter, uiDelegate);
		loader.execute();

		// Load party
		PartyFacade party =
				CharacterManager.openParty(file, uiDelegate,
					loader.getDataSetFacade());

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
	public static boolean exportCharacterToPDF(CharacterFacade character,
		File outFile, File templateFile)
	{
		String extension =
				StringUtils.substringAfterLast(templateFile.getName(), ".");
		FOPHandler handler = new FOPHandler();
		File tempFile = null;
		try
		{
			if ("xslt".equalsIgnoreCase(extension)
				|| "xsl".equalsIgnoreCase(extension))
			{
				tempFile = File.createTempFile("currentPC_", ".xml");
				printToXMLFile(tempFile, character);
				handler.setInputFile(tempFile, templateFile);
			}
			else
			{
				tempFile = File.createTempFile("currentPC_", ".fo");
				printToFile(tempFile, true, templateFile, character);
				handler.setInputFile(tempFile);
			}
			if (StringUtils.isNotEmpty(handler.getErrorMessage()))
			{
				return false;
			}
					
			character.setDefaultOutputSheet(true, templateFile);
			handler.setMode(FOPHandler.PDF_MODE);
			handler.setOutputFile(outFile);
			handler.run();
		}
		catch (IOException e)
		{
			Logging.errorPrint("BatchExporter.exportCharacterToPDF failed", e);
			return false;

		}
		if (tempFile != null)
		{
			tempFile.delete();
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
	public static boolean exportCharacterToNonPDF(CharacterFacade character,
		File outFile, File templateFile)
	{
		BufferedWriter bw;
		try
		{
			bw =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF-8"));
			character.export(new ExportHandler(templateFile), bw);
			character.setDefaultOutputSheet(false, templateFile);
			
			bw.close();
			return true;
		}
		catch (UnsupportedEncodingException e)
		{
			Logging.errorPrint(
				"Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
		}
		catch (IOException e)
		{
			Logging.errorPrint(
				"Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
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
	public static boolean exportPartyToPDF(PartyFacade party, File outFile,
		File templateFile)
	{
		String extension =
				StringUtils.substringAfterLast(templateFile.getName(), ".");
		FOPHandler handler = new FOPHandler();
		File tempFile = null;
		try
		{
			if ("xslt".equalsIgnoreCase(extension)
				|| "xsl".equalsIgnoreCase(extension))
			{
				tempFile = File.createTempFile("currentPC_", ".xml");
				printToXMLFile(tempFile, party);
				handler.setInputFile(tempFile, templateFile);
				//SettingsHandler.setSelectedCharacterPDFOutputSheet(template.getAbsolutePath(), Globals.getPCList().get(pcExports[loop]));
			}
			else
			{
				tempFile = File.createTempFile("currentPC_", ".fo");
				printToFile(tempFile, true, templateFile, party);
				handler.setInputFile(tempFile);
			}
			handler.setMode(FOPHandler.PDF_MODE);
			handler.setOutputFile(outFile);
			handler.run();
		}
		catch (IOException e)
		{
			Logging.errorPrint("BatchExporter.exportCharacterToPDF failed", e);
			return false;

		}
		if (tempFile != null)
		{
			tempFile.delete();
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
	public static boolean exportPartyToNonPDF(PartyFacade party, File outFile,
		File templateFile)
	{
		BufferedWriter bw;
		try
		{
			bw =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF-8"));
			party.export(new ExportHandler(templateFile), bw);
			bw.close();
			return true;
		}
		catch (UnsupportedEncodingException e)
		{
			Logging.errorPrint(
				"Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
		}
		catch (IOException e)
		{
			Logging.errorPrint(
				"Unable to create output file " + outFile.getAbsolutePath(), e);
			return false;
		}
	}

	private static void printToXMLFile(File outFile, CharacterFacade character)
		throws IOException
	{
		final BufferedWriter bw =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					outFile), "UTF-8"));
		File template = getXMLTemplate(character);
		character.export(new ExportHandler(template), bw);
		bw.close();
	}

	private static void printToXMLFile(File outFile, PartyFacade party)
		throws IOException
	{
		final BufferedWriter bw =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					outFile), "UTF-8"));
		for (CharacterFacade character : party)
		{
			File templateFile = getXMLTemplate(character);
			character.export(new ExportHandler(templateFile), bw);
		}
		bw.close();
	}

	private static void printToFile(File outFile, boolean pdf,
		File templateFile, CharacterFacade character) throws IOException
	{
		final BufferedWriter bw =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					outFile), "UTF-8"));
		character.export(new ExportHandler(templateFile), bw);
		bw.close();
	}

	private static void printToFile(File outFile, boolean pdf,
		File templateFile, PartyFacade party) throws IOException
	{
		final BufferedWriter bw =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					outFile), "UTF-8"));

		if (pdf)
		{
			SettingsHandler.setSelectedPartyPDFOutputSheet(templateFile
				.getAbsolutePath());
		}
		else
		{
			SettingsHandler.setSelectedPartyHTMLOutputSheet(templateFile
				.getAbsolutePath());
		}
		party.export(new ExportHandler(templateFile), bw);
		bw.close();
	}

	private static File getXMLTemplate(CharacterFacade character)
	{
		File template =
				FileUtils.getFile(ConfigurationSettings.getSystemsDir(),
					"gameModes",
					character.getDataSet().getGameMode().getName(), "base.xml");
		if (!template.exists())
		{
			template =
					new File(ConfigurationSettings.getOutputSheetsDir(),
						"base.xml");
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
		String extension =
				StringUtils.substringAfterLast(exportTemplateFilename, ".");
		if (isPdf)
		{
			extension = "pdf";
		}
		String outputName =
				charname.substring(0, charname.lastIndexOf('.')) + "."
					+ extension;
		return new File(charFile.getParent(), outputName).getAbsolutePath();
	}
}
