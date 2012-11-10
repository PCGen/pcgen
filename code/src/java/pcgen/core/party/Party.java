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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.party;

import java.io.*;
import java.util.*;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.PCGIOHandler;
import pcgen.persistence.PersistenceManager;
import pcgen.system.PCGenSettings;
import pcgen.util.FileHelper;
import pcgen.util.Logging;

/**
 * Class to encapsulate the functionality of loading and saving parties of characters.  Also used
 * to load a single character.  This is my first stab at decoupling some of the party loading and
 * saving logic from the gui.  It's not complete and still a little messy, but at least it gets us
 * on the right track with the decoupling.
 *
 * @author jkwatson
 * @version $Revision$
 * @deprecated 
 */
public class Party
{
	private File partyFile;
	private List<File> characterFiles = new ArrayList<File>();

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
		for ( PlayerCharacter character : Globals.getPCList() )
		{
			characterFiles.add(new File(character.getFileName()));
		}
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

		for ( File file : characterFiles )
		{
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
		final PlayerCharacter newPC = new PlayerCharacter(false, PersistenceManager.getInstance().getLoadedCampaigns());
		final PCGIOHandler ioHandler = new PCGIOHandler();
		ioHandler.read(newPC, file.getAbsolutePath());
		newPC.insertBonusLanguageAbility();

		if (Globals.getUseGUI())
		{
			for ( String error : ioHandler.getErrors() )
			{
				ShowMessageDelegate.showMessageDialog(
					"Error: " + error,  //$NON-NLS-2$
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}

			for ( String warning : ioHandler.getWarnings() )
			{
				ShowMessageDelegate.showMessageDialog(
					"Warning: " + warning,  //$NON-NLS-2$
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}
		}
		else
		{
			for ( String msg : ioHandler.getMessages() )
			{
				Logging.errorPrint(msg);
			}
		}

		// if we've had errors, then abort trying to add the new PC, it's most likely "broken"
		//  if it's not broken, then only warnings should have been generated, and we won't count those
		if (ioHandler.getErrors().size() <= 0)
		{
			// Set the filename so that future checks to see if file already loaded will work
			newPC.setFileName(file.getAbsolutePath());
			Globals.getPCList().add(newPC);
			return newPC;
		}

		return null;
	}
}
