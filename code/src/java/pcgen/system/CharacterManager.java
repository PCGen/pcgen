/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterStubFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.gui2.facade.CharacterFacadeImpl;
import pcgen.gui2.facade.PartyFacadeImpl;
import pcgen.io.PCGFile;
import pcgen.io.PCGIOHandler;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.PluginManager;
import pcgen.pluginmgr.messages.PlayerCharacterWasLoadedMessage;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * This class stores the characters that are currently opened by
 * PCGen. It also handles creating new characters and opening
 * characters from files. The getCharacters method returns
 * a listenable list that allows users of this class to not
 * only see what characters are open but to easily track any
 * changes to the list of available characters.
 */
public final class CharacterManager
{

	private static final PartyFacadeImpl CHARACTERS;
	private static final RecentFileList RECENT_CHARACTERS;
	private static final RecentFileList RECENT_PARTIES;
	private static final PCGenMessageHandler MESSAGE_HANDLER;

	static
	{
		CHARACTERS = new PartyFacadeImpl();
		RECENT_CHARACTERS = new RecentFileList(PCGenSettings.RECENT_CHARACTERS);
		RECENT_PARTIES = new RecentFileList(PCGenSettings.RECENT_PARTIES);
		MESSAGE_HANDLER = PluginManager.getInstance().getPostbox();
	}

	private CharacterManager()
	{
	}

	/**
	 * Create a new character using the supplied data sets.
	 * @param delegate the UIDelegate that this character will use.
	 * @param dataset the dataset that this will be loaded with.
	 * @return The character that was created.
	 */
	public static CharacterFacade createNewCharacter(UIDelegate delegate, DataSetFacade dataset)
	{
		@SuppressWarnings("rawtypes")
		List campaigns = ListFacades.wrap(dataset.getCampaigns());
		try
		{
			@SuppressWarnings("unchecked")
			PlayerCharacter pc = new PlayerCharacter(campaigns);
			Globals.getPCList().add(pc);
			CharacterFacade character = new CharacterFacadeImpl(pc, delegate, dataset);
			String name = createNewCharacterName();
			character.setName(name);
			CHARACTERS.addElement(character);
			Logging.log(Logging.INFO, "Created new character " + name + '.'); //$NON-NLS-1$ 
			MESSAGE_HANDLER.handleMessage(new PlayerCharacterWasLoadedMessage(delegate, pc));
			return character;
		}
		catch (final Exception e)
		{
			Logging.errorPrint("Unable to create character with data " //$NON-NLS-1$
				+ dataset, e);
			delegate.showErrorMessage(LanguageBundle.getString("in_cmCreateErrorTitle"), //$NON-NLS-1$
				LanguageBundle.getFormattedString("in_cmCreateErrorMessage", //$NON-NLS-1$
					e.getMessage()));
			return null;
		}

	}

	public static ListFacade<File> getRecentCharacters()
	{
		return RECENT_CHARACTERS;
	}

	public static ListFacade<File> getRecentParties()
	{
		return RECENT_PARTIES;
	}

	/**
	 * This opens an existing character from a file and adds it to the
	 * list of open characters. If there is a character already open
	 * that uses this file, then this method does nothing.
	 * @param file the file to load this character from
	 * @param delegate the UIDelegate that this character will use
	 * @param dataset the dataset that this will be loaded with
	 * @return The character that was opened.
	 */
	public static CharacterFacade openCharacter(File file, UIDelegate delegate, DataSetFacade dataset)
	{
		final PlayerCharacter newPC = openPcInternal(file, delegate, dataset, false);

		if (newPC == null)
		{
			return null;
		}

		return createChracterFacade(delegate, dataset, newPC);
	}

	/**
	 * This opens an existing character from a file and adds it to the
	 * list of open characters. If there is a character already open
	 * that uses this file, then this method does nothing.
	 * @param file the file to load this character from
	 * @param delegate the UIDelegate that this character will use
	 * @param dataset the dataset that this will be loaded with
	 * @param blockLoadedMessage Should we stop the character loaded message being sent out to listeners.
	 * @return The character that was opened.
	 */
	public static PlayerCharacter openPlayerCharacter(File file, UIDelegate delegate, DataSetFacade dataset,
		boolean blockLoadedMessage)
	{
		final PlayerCharacter newPC = openPcInternal(file, delegate, dataset, blockLoadedMessage);

		if (newPC == null)
		{
			return null;
		}

		createChracterFacade(delegate, dataset, newPC);
		return newPC;
	}

	@SuppressWarnings("unchecked")
	private static PlayerCharacter openPcInternal(File file, UIDelegate delegate, DataSetFacade dataset,
		boolean blockLoadedMessage)
	{
		@SuppressWarnings("rawtypes")
		List campaigns = ListFacades.wrap(dataset.getCampaigns());
		try
		{
			final PlayerCharacter newPC = new PlayerCharacter(campaigns);
			newPC.setFileName(file.getAbsolutePath());
			final PCGIOHandler ioHandler = new PCGIOHandler();
			ioHandler.read(newPC, file.getAbsolutePath());
			// Ensure any custom equipment held by the character is added to the dataset's list
			dataset.refreshEquipment();
			newPC.calcActiveBonuses();

			if (!showLoadNotices(true, ioHandler.getErrors(), file.getName(), delegate))
			{
				// if we've had errors, then abort trying to add the new PC, it's most likely "broken"
				return null;
			}
			if (!showLoadNotices(false, ioHandler.getWarnings(), file.getName(), delegate))
			{
				return null;
			}
			Logging.log(Logging.INFO, "Loaded character " + newPC.getName() //$NON-NLS-1$
				+ " - " + file.getAbsolutePath()); //$NON-NLS-1$

			// if it's not broken, then only warnings should have been generated, and we won't count those
			// Register the character so that future checks to see if file already loaded will work
			Globals.getPCList().add(newPC);
			if (!blockLoadedMessage)
			{
				MESSAGE_HANDLER.handleMessage(new PlayerCharacterWasLoadedMessage(delegate, newPC));
			}
			return newPC;

		}
		catch (final Exception e)
		{
			Logging.errorPrint("Unable to load character " + file, e); //$NON-NLS-1$
			delegate.showErrorMessage(LanguageBundle.getString("in_cmLoadErrorTitle"), //$NON-NLS-1$
				LanguageBundle.getFormattedString("in_cmLoadErrorMessage", //$NON-NLS-1$
					file, e.getMessage()));
			return null;
		}
	}

	private static CharacterFacade createChracterFacade(UIDelegate delegate, DataSetFacade dataset,
		final PlayerCharacter newPC)
	{
		CharacterFacade character = new CharacterFacadeImpl(newPC, delegate, dataset);
		CHARACTERS.addElement(character);
		return character;
	}

	/**
	 * Show the user any warnings or errors from the character load and get 
	 * their approval to continue.
	 * 
	 * @param errors Is this a list of errors?  
	 * @param warnings The warnings generated on load.
	 * @param fileName The name of the file being loaded.
	 * @param delegate The UIDelegate to use for notifications.
	 * @return true if the character should be loaded, false if not.
	 */
	private static boolean showLoadNotices(boolean errors, List<String> warnings, String fileName, UIDelegate delegate)
	{
		if (warnings.isEmpty())
		{
			return true;
		}

		StringBuilder warningMsg = new StringBuilder();
		Level lvl;
		if (errors)
		{
			warningMsg.append(LanguageBundle.getString("in_cmErrorStart"));
			Logging.errorPrint("The following errors were encountered while loading " + fileName);
			lvl = Logging.ERROR;
		}
		else
		{
			warningMsg.append(LanguageBundle.getString("in_cmWarnStart"));
			Logging.log(Logging.WARNING, "The following warnings were encountered while loading " + fileName);
			lvl = Logging.WARNING;
		}
		warningMsg.append('\n');
		for (final String string : warnings)
		{
			warningMsg.append(string);
			warningMsg.append('\n');
			Logging.log(lvl, "* " + string); //$NON-NLS-1$
		}

		if (errors)
		{
			warningMsg.append(LanguageBundle.getString("in_cmErrorEnd"));
			delegate.showErrorMessage(fileName, warningMsg.toString());
			return false;
		}

		warningMsg.append(LanguageBundle.getString("in_cmWarnEnd"));
		return delegate.showWarningConfirm(fileName, warningMsg.toString());
	}

	/**
	 * This opens an existing party from a file and adds all characters to the
	 * list of open characters.
	 *  
	 * @param file the file to load this party from
	 * @param delegate the UIDelegate that these characters will use
	 * @param dataset the dataset that this will be loaded with
	 * @return The party that was opened.
	 */
	public static PartyFacade openParty(File file, final UIDelegate delegate, final DataSetFacade dataset)
	{
		Logging.log(Logging.INFO, "Loading party " + file.getAbsolutePath()); //$NON-NLS-1$
		PCGIOHandler.readCharacterFileList(file).forEach(charFile -> openCharacter(charFile, delegate, dataset));
		CHARACTERS.setFile(file);
		return CHARACTERS;
	}

	public static SourceSelectionFacade getRequiredSourcesForParty(File pcpFile, UIDelegate delegate)
	{
		List<File> files = PCGIOHandler.readCharacterFileList(pcpFile);
		if ((files == null) || files.isEmpty())
		{
			return null;
		}
		GameMode gameMode = null;
		HashSet<Campaign> campaignSet = new HashSet<>();
		for (final File file : files)
		{
			SourceSelectionFacade selection = getRequiredSourcesForCharacter(file, delegate);
			if (selection == null)
			{
				Logging.errorPrint("Failed to find sources in: " + file.getAbsolutePath());
				continue;
			}
			GameMode game = selection.getGameMode().get();
			if (gameMode == null)
			{
				gameMode = game;
			}
			else if (gameMode != game)
			{
				Logging.errorPrint("Characters in " + pcpFile.getAbsolutePath() + " do not share the same game mode");
				return null;
			}

			for (final Campaign campaign : selection.getCampaigns())
			{
				campaignSet.add(campaign);
			}
		}
		//TODO: check to make sure that the campaigns are compatable

		return FacadeFactory.createSourceSelection(gameMode, new ArrayList<>(campaignSet));
	}

	/**
	 * 
	 * @param pcgFile a character file
	 * @param delegate  The UIDelegate used to display message to the user
	 * @return a SourceSelectionFacade or null if no sources could be found
	 */
	public static SourceSelectionFacade getRequiredSourcesForCharacter(File pcgFile, UIDelegate delegate)
	{
		if (!PCGFile.isPCGenCharacterFile(pcgFile))
		{
			throw new IllegalArgumentException("invalid file: " + pcgFile);
		}

		final PCGIOHandler ioHandler = new PCGIOHandler();
		SourceSelectionFacade selection = ioHandler.readSources(pcgFile);
		if (!ioHandler.getErrors().isEmpty())
		{
			ioHandler.getErrors().forEach(msg -> {
				delegate.showErrorMessage(Constants.APPLICATION_NAME, msg);
				Logging.errorPrint(msg);
			});
			return null;
		}
		return selection;
	}

	/**
	 * Check if the character's filename is ready to be saved to.    
	 * @param character The character to be checked.
	 * @return true if the file can be written to, false otherwise.
	 */
	public static boolean characterFilenameValid(CharacterFacade character)
	{
		if (character.getFileRef().get() == null || StringUtils.isEmpty(character.getFileRef().get().getName()))
		{
			return false;
		}

		File file = character.getFileRef().get();
		if (StringUtils.isBlank(file.getName()))
		{
			return false;
		}
		return !file.isDirectory() && (!file.exists() || file.canWrite());
	}

	/**
	 * Saves this character to the character's file specified
	 * by character.getFileRef().getReference()
	 * This is expected to be called before a character is to
	 * be removed from the list of open characters.
	 * @param character the character to be saved
	 * @return true if the save succeeded, false if not 
	 */
	public static boolean saveCharacter(CharacterFacade character)
	{
		File file = character.getFileRef().get();
		if (StringUtils.isBlank(file.getName()))
		{
			return false;
		}

		Logging.log(Logging.INFO, "Saving character " + character.getNameRef().get() //$NON-NLS-1$
			+ " - " + file.getAbsolutePath()); //$NON-NLS-1$

		if (character instanceof CharacterFacadeImpl)
		{
			UIDelegate delegate = character.getUIDelegate();
			try
			{
				((CharacterFacadeImpl) character).save();
			}
			catch (final NullPointerException | IOException e)
			{
				Logging.errorPrint("Could not save " + character.getNameRef().get(), e);
				delegate.showErrorMessage(Constants.APPLICATION_NAME, "Could not save " + character.getNameRef().get());
				return false;
			}
		}
		else
		{
			Logging.errorPrint("Could not save " + character.getNameRef().get()
				+ " due to unexpected class of character: " + character.getClass().getCanonicalName());
			return false;
		}

		RECENT_CHARACTERS.addRecentFile(file);
		return true;
	}

	public static boolean saveCurrentParty()
	{
		File file = CHARACTERS.getFileRef().get();
		if (file == null)
		{
			return false;
		}
		Logging.log(Logging.INFO, "Saving party " + file.getAbsolutePath()); //$NON-NLS-1$
		CHARACTERS.save();
		return true;
	}

	/**
	 * removes a character from the list of open characters.
	 * This is called at the end of a close character operation.
	 * Note: this operation does not save the character!
	 * @param character the character to be closed
	 */
	public static void removeCharacter(CharacterFacade character)
	{
		CHARACTERS.removeElement(character);
		// This advises the message handler also.
		character.closeCharacter();
		File charFile = character.getFileRef().get();
		RECENT_CHARACTERS.addRecentFile(charFile);
		if (CHARACTERS.isEmpty())
		{
			RECENT_PARTIES.addRecentFile(CHARACTERS.getFileRef().get());
			CHARACTERS.setFile(null);
		}
		Logging.log(Logging.INFO, "Closed character " + character.getNameRef().get() //$NON-NLS-1$
			+ " - " + charFile.getAbsolutePath()); //$NON-NLS-1$
	}

	public static void removeAllCharacters()
	{
		for (final CharacterFacade characterFacade : CHARACTERS)
		{
			RECENT_CHARACTERS.addRecentFile(characterFacade.getFileRef().get());
			// This advises the message handler also.
			characterFacade.closeCharacter();
		}
		CHARACTERS.clearContents();
		RECENT_PARTIES.addRecentFile(CHARACTERS.getFileRef().get());
		CHARACTERS.setFile(null);
		Logging.log(Logging.INFO, "Closed all characters"); //$NON-NLS-1$
	}

	public static PartyFacade getCharacters()
	{
		return CHARACTERS;
	}

	/**
	 * Retrieve the loaded character matching the character stub. The character 
	 * may not have been saved yet, so may not have a file name, in which case 
	 * the match is made on character name. This is often used for retrieval of 
	 * a loaded master or companion.  
	 * 
	 * @param companion The companion to be searched for.
	 * @return The character, or null if the companion is not loaded.
	 */
	public static CharacterFacade getCharacterMatching(CharacterStubFacade companion)
	{
		File compFile = companion.getFileRef().get();
		if (StringUtils.isEmpty(compFile.getName()))
		{
			String compName = companion.getNameRef().get();
			for (final CharacterFacade character : getCharacters())
			{
				String charName = character.getNameRef().get();
				if (Objects.equals(compName, charName))
				{
					return character;
				}
			}
		}
		else
		{
			for (final CharacterFacade character : getCharacters())
			{
				File charFile = character.getFileRef().get();
				if (compFile.equals(charFile))
				{
					return character;
				}
			}
		}
		return null;
	}

	private static String createNewCharacterName()
	{
		String name = "Unnamed ";
		int i = 1;
		while (isNameUsed(name + i))
		{
			i++;
		}
		return name + i;
	}

	private static boolean isNameUsed(String name)
	{
		for (final CharacterFacade character : CHARACTERS)
		{
			if (character.getNameRef().get().equals(name))
			{
				return true;
			}
		}
		return false;
	}

}
