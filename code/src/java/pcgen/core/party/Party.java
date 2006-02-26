/*
 * Campaign.java
 * Copyright 2003 (C) ??? (jkwatson or Bryan I assume?)
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
 * Current Ver: $Revision: 1.27 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/22 18:33:22 $
 */
package pcgen.core.party;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.PCGIOHandler;
import pcgen.util.FileHelper;
import pcgen.util.Logging;

import java.io.*;
import java.util.*;

/**
 * Class to encapsulate the functionality of loading and saving parties of characters.  Also used
 * to load a single character.  This is my first stab at decoupling some of the party loading and
 * saving logic from the gui.  It's not complete and still a little messy, but at least it gets us
 * on the right track with the decoupling.
 *
 * @author jkwatson
 * @version $Revision: 1.27 $
 *
 */
public class Party
{
	private File partyFile;
	private List characterFiles = new ArrayList();

	private Party()
	{
	    // Empty Constructor
	}

	private Party(final File partyFile)
	{
		this.partyFile = partyFile;
	}

	/**
	 * Create a Party which will use the file as the storage location.  This can then be loaded with the load method.
	 * You can also save to the file.
	 * @param partyFile The filename of the pcp file.
	 * @return Party
	 */
	public static Party makePartyFromFile(final File partyFile)
	{
		return new Party(partyFile);
	}

	/**
	 * Create a new Party with a single character in it.  The character can be loaded with the load method.
	 * @param characterFile A file containing a pcgen character.
	 * @return Party
	 */
	public static Party makeSingleCharacterParty(final File characterFile)
	{
		final Party party = new Party();
		party.characterFiles.add(characterFile);

		return party;
	}

	/**
	 * Build the display name of the party by removing the extension on the file name.
	 * @return the display name of the party.
	 */
	public String getDisplayName()
	{
		String displayName = partyFile.getName();
		final int lastDot = displayName.lastIndexOf('.');

		if (lastDot >= 0)
		{
			displayName = displayName.substring(0, lastDot);
		}

		return displayName;
	}

	/**
	 * Go through all open characters and add them to the party.
	 */
	public void addAllOpenCharacters()
	{
		final Iterator characters = Globals.getPCList().iterator();

		//save PC filenames
		while (characters.hasNext())
		{
			final PlayerCharacter character = (PlayerCharacter) characters.next();
			characterFiles.add(new File(character.getFileName()));
		}
	}

	/**
	 * todo:figure out how to decouple this from the gui.  I don't like having the gui intruding here!
	 * @param mainFrame The PCGen gui.  It needs to add tabs and such when characters are loaded.
	 * @return The last PC successfully loaded. Null if no PCs could be loaded.
	 */
	public PlayerCharacter load(final PCLoader mainFrame)
	{

		PlayerCharacter lastPC = null;

		try
		{
			if (partyFile == null)
			{
				return loadCharacterFiles();
			}

			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(partyFile), "UTF-8"));

			//load version info
			br.readLine(); //Read and throw away version info. May change to actually use later

			//load character filename data
			final String charFiles = br.readLine();
			br.close();

			//we no longer load campaign/source infor from the party partyFile
			// in this space we could check the VERSION tag of versionInfo for whatever we wanted
			// if the String didn't start with VERSION: then we know it's a really old PCP partyFile
			//parse PC data and load the listed PC's
			final StringTokenizer fileNames = new StringTokenizer(charFiles, ",");

			while (fileNames.hasMoreTokens())
			{
				final String fileName = fileNames.nextToken();
				File characterFile = buildCharacterFile(fileName);
				PlayerCharacter currPC = null;

				if (!characterFile.exists())
				{
					// try using the global pcg path
					characterFile = new File(SettingsHandler.getPcgPath(), fileName);
				}

				if (characterFile.exists())
				{
					characterFiles.add(characterFile);

					// if called from the GUI, then use the GUI's PC loader so that we get the PC tabs built
					if (mainFrame != null)
					{
						currPC = mainFrame.loadPCFromFile(characterFile);
					}
					else
					{
						// otherwise, do it the quick-n-dirty way
						currPC = loadPCFromFile(characterFile);
					}
				}
				else
				{
					Logging.errorPrint("Character file does not exist: " + fileName);
					currPC = null;
				}
				if (currPC != null)
				{
					lastPC = currPC;
				}
			}

			Globals.sortCampaigns();
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Error loading party partyFile.", ex);

			if (Globals.getUseGUI())
			{
				//todo:i18n this message
				ShowMessageDelegate.showMessageDialog("Could not load party partyFile.", "PCGen", MessageType.ERROR);
			}
		}

		return lastPC;
	}

	/**
	 * Saves the party.
	 * @throws FileNotFoundException if the file to save into is not set
	 * @throws IOException if the party file cannot be written
	 */
	public void save() throws FileNotFoundException, IOException
	{
		if (partyFile == null)
		{
			throw new FileNotFoundException("The file to save this party to is null");
		}

		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partyFile), "UTF-8"));

		// Save party partyFile data here
		// Save version info here (we no longer save campaign/source info in the party partyFile)
		final ResourceBundle properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
		writer.write("VERSION:");
		writer.write(properties.getString("VersionNumber"));
		writer.newLine();

		for (int i = 0; i < characterFiles.size(); i++)
		{
			final File file = (File) characterFiles.get(i);
			writer.write(FileHelper.findRelativePath(partyFile, file) + ",");
		}

		writer.newLine(); // don't write files without terminators.
		writer.close();
		SettingsHandler.setPcgPath(partyFile.getParentFile()); //still set this, we want .pcp and .pcg files in the same place
	}

	private File buildCharacterFile(final String fileName)
	{
		return new File(partyFile.getParentFile().getAbsolutePath() + fileName);
	}

	/**
	 * Load all the characters in the party.
	 * @return The last character loaded, or null if that load fails.
	 */
	private PlayerCharacter loadCharacterFiles()
	{
		PlayerCharacter pc = null;

		for (int i = 0; i < characterFiles.size(); i++)
		{
			final File file = (File) characterFiles.get(i);
			pc = loadPCFromFile(file);
		}

		return pc;
	}

	/**
	 * Load the pc.
	 * @param file the file to load the pc from
	 * @return The character that was loaded.
	 */
	private static PlayerCharacter loadPCFromFile(final File file)
	{
		final PlayerCharacter newPC = new PlayerCharacter();
		final PCGIOHandler ioHandler = new PCGIOHandler();
		ioHandler.read(newPC, file.getAbsolutePath());

		if (Globals.getUseGUI())
		{
			for (Iterator it = ioHandler.getErrors().iterator(); it.hasNext();)
			{
				ShowMessageDelegate.showMessageDialog("Error: " + it.next(), Constants.s_APPNAME, MessageType.ERROR);
			}

			for (Iterator it = ioHandler.getWarnings().iterator(); it.hasNext();)
			{
				ShowMessageDelegate.showMessageDialog("Warning: " + it.next(), Constants.s_APPNAME, MessageType.ERROR);
			}
		}
		else
		{
			for (Iterator it = ioHandler.getMessages().iterator(); it.hasNext();)
			{
				Logging.errorPrint((String) it.next());
			}
		}

		// if we've had errors, then abort trying to add the new PC, it's most likely "broken"
		//  if it's not broken, then only warnings should have been generated, and we won't count those
		if (ioHandler.getErrors().size() <= 0)
		{
			// Set the filename so that future checks to see if file already loaded will work
			newPC.setFileName(file.getAbsolutePath());
			Globals.getPCList().add(newPC);
			Globals.setCurrentPC(newPC);
			Globals.sortCampaigns();
			return newPC;
		}

		return null;
	}
}
