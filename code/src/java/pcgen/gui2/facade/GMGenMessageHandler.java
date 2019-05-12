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
 */
package pcgen.gui2.facade;

import java.io.File;
import java.util.Iterator;

import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.io.PCGFile;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.PlayerCharacterWasLoadedMessage;
import pcgen.pluginmgr.messages.RequestFileOpenedMessageForCurrentlyOpenedPCsMessage;
import pcgen.pluginmgr.messages.RequestOpenPlayerCharacterMessage;
import pcgen.pluginmgr.messages.RequestToSavePlayerCharacterMessage;
import pcgen.pluginmgr.messages.TransmitInitiativeValuesBetweenComponentsMessage;
import pcgen.system.CharacterManager;

/**
 * The Class {@code GMGenMessageHandler} processes any requests
 * to the main PCGen program from the GMGen bus.
 *
 * 
 */
public class GMGenMessageHandler implements PCGenMessageHandler
{

	private final PCGenFrame delegate;
	private final PCGenMessageHandler messageHandler;

	/**
	 * Create a new instance of GMGenMessageHandler
	 * @param delegate The PCGenFrame instance containing the UI.
	 */
	public GMGenMessageHandler(PCGenFrame delegate, PCGenMessageHandler mh)
	{
		this.delegate = delegate;
		this.messageHandler = mh;
	}

	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof RequestOpenPlayerCharacterMessage)
		{
			handleOpenPCGRequestMessage((RequestOpenPlayerCharacterMessage) message);
		}
		else if (message instanceof RequestFileOpenedMessageForCurrentlyOpenedPCsMessage)
		{
			handleFetchOpenPCGRequestMessage();
		}
		else if (message instanceof RequestToSavePlayerCharacterMessage)
		{
			handleSavePcgMessage(message);
		}

		// This should only be used until GMGen can use PCGen to generate it's
		// Random encounter beasties.
		else if (message instanceof TransmitInitiativeValuesBetweenComponentsMessage)
		{
			handleInitHolderListSendMessage((TransmitInitiativeValuesBetweenComponentsMessage) message);
		}
		else if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			handleStateChangedMessage();
		}
	}

	private void handleSavePcgMessage(PCGenMessage message)
	{
		RequestToSavePlayerCharacterMessage smessage = (RequestToSavePlayerCharacterMessage) message;
		PlayerCharacter pc = smessage.getPc();
		for (Iterator<CharacterFacade> iterator = CharacterManager.getCharacters().iterator(); iterator.hasNext();)
		{
			CharacterFacade facade = iterator.next();
			if (facade.matchesCharacter(pc))
			{
				CharacterManager.saveCharacter(facade);
				break;
			}
		}
	}

	private void handleInitHolderListSendMessage(TransmitInitiativeValuesBetweenComponentsMessage message)
	{
		InitHolderList list = message.getInitHolderList();

		for (int i = 0; i < list.size(); i++)
		{
			InitHolder iH = list.get(i);

			if (iH instanceof PcgCombatant)
			{
				//TODO: Resolve against the current PC list and add any new characters.
				PcgCombatant pcg = (PcgCombatant) iH;
				PlayerCharacter aPC = pcg.getPC();
				Globals.getPCList().add(aPC);
				aPC.setDirty(true);
				//				addPCTab(aPC);
			}
		}
	}

	private void handleOpenPCGRequestMessage(RequestOpenPlayerCharacterMessage message)
	{
		File pcFile = message.getFile();

		if (PCGFile.isPCGenCharacterFile(pcFile))
		{
			PlayerCharacter playerCharacter = CharacterManager.openPlayerCharacter(pcFile, delegate,
				delegate.getLoadedDataSetRef().get(), message.isBlockLoadedMessage());
			message.setPlayerCharacter(playerCharacter);
			message.consume();
		}
		else if (PCGFile.isPCGenPartyFile(pcFile))
		{
			CharacterManager.openParty(pcFile, delegate, delegate.getLoadedDataSetRef().get());
		}
	}

	private void handleFetchOpenPCGRequestMessage()
	{
		for (int i = 0; i < CharacterManager.getCharacters().getSize(); i++)
		{
			CharacterFacade facade = CharacterManager.getCharacters().getElementAt(i);
			if (facade instanceof CharacterFacadeImpl)
			{
				CharacterFacadeImpl cfi = (CharacterFacadeImpl) facade;
				messageHandler.handleMessage(new PlayerCharacterWasLoadedMessage(this, cfi.getTheCharacter()));
			}
		}
	}

	private void handleStateChangedMessage()
	{
	}
}
