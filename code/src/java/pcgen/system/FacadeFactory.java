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
 *
 */
package pcgen.system;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.GameModeDisplay;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.facade.core.CampaignInfoFactory;
import pcgen.facade.core.GameModeDisplayFacade;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.gui2.facade.Gui2CampaignInfoFactory;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;

public final class FacadeFactory
{

	private static final PropertyContext SOURCES_CONTEXT =
			PCGenSettings.getInstance().createChildContext("customSources");
	private static DefaultListFacade<SourceSelectionFacade> quickSources = null;
	private static DefaultListFacade<Campaign> campaigns = null;
	private static DefaultListFacade<GameMode> gamemodes = null;
	private static DefaultListFacade<GameModeDisplayFacade> gamemodedisplays = null;
	private static DefaultListFacade<SourceSelectionFacade> displayedSources = null;
	private static DefaultListFacade<SourceSelectionFacade> customSources;
	private static Map<String, Campaign> campaignMap;
	private static Map<GameMode, DefaultListFacade<Campaign>> campaignListMap = null;
	private static final CampaignInfoFactory CAMP_INFO_FACTORY = new Gui2CampaignInfoFactory();

	private FacadeFactory()
	{
	}

	static void initialize()
	{
		List<GameMode> modes = SystemCollections.getUnmodifiableGameModeList();
		List<GameModeDisplay> modeDisplays = SystemCollections.getUnmodifiableGameModeDisplayList();
		List<Campaign> camps = Globals.getCampaignList();
		gamemodes = new DefaultListFacade<>(modes);
		gamemodedisplays = new DefaultListFacade<>(modeDisplays);
		campaigns = new DefaultListFacade<>(camps);
		quickSources = new DefaultListFacade<>();
		displayedSources = new DefaultListFacade<>();
		customSources = new DefaultListFacade<>();
		campaignMap = new HashMap<>();
		campaignListMap = new HashMap<>();
		initCampaigns();
		initGameModes(modes);
		initCustomSourceSelections();
		initDisplayedSources();
	}

	private static void initCampaigns()
	{
		for (final Campaign campaign : campaigns)
		{
			campaignMap.put(campaign.getKeyName(), campaign);
			ListFacade<GameMode> gameModeList = campaign.getGameModes();
			for (GameMode gameMode : gameModeList)
			{
				if (!campaignListMap.containsKey(gameMode))
				{
					campaignListMap.put(gameMode, new DefaultListFacade<>());
				}
				DefaultListFacade<Campaign> campaignList = campaignListMap.get(gameMode);
				if (campaignList.containsElement(campaign))
				{
					String sourceUri = ((CDOMObject) campaign).getSourceURI().toString();
					Logging
						.errorPrint("Campaign " + sourceUri + " lists GAMEMODE:" + gameMode + " multiple times.");
				}
				else
				{
					campaignList.addElement(campaign);
				}
			}
			if (campaign.getSafe(ObjectKey.SHOW_IN_MENU) && !gameModeList.isEmpty())
			{
				GameMode game = gameModeList.getElementAt(0);
				ListFacade<Campaign> list = new DefaultListFacade<>(Collections.singleton(campaign));
				quickSources.addElement(new BasicSourceSelectionFacade(campaign.getKeyName(), list, game));
			}
		}
	}

	private static void initGameModes(List<GameMode> modes)
	{
		for (GameMode mode : modes)
		{
			String title = mode.getDefaultSourceTitle();
			if (SettingsHandler.getGameAsProperty().get().equals(mode) && title == null && !mode.getDefaultDataSetList().isEmpty())
			{
				title = mode.getName();
			}
			if (title != null && !"".equals(title))
			{
				DefaultListFacade<Campaign> qcamps = new DefaultListFacade<>();
				List<String> sources = mode.getDefaultDataSetList();
				for (String string : sources)
				{
					Campaign camp = Globals.getCampaignKeyed(string);
					if (camp != null)
					{
						qcamps.addElement(camp);
					}
					else
					{
						Logging.log(Logging.WARNING, "Unable to find source " + string + " used in default source "
							+ title + " for game mode " + mode + ". " + title + " might not work correctly.");
					}
				}
				if (qcamps.isEmpty())
				{
					Logging.log(Logging.WARNING,
						"Unable to load default source '" + title + "'. All of its sources are missing.");
					continue;
				}
				quickSources.addElement(new BasicSourceSelectionFacade(mode.getDefaultSourceTitle(), qcamps, mode));
			}
		}
	}

	private static void initDisplayedSources()
	{
		IntStream.range(0, quickSources.getSize())
		         .mapToObj(quickSources::getElementAt)
		         .forEach(displayedSources::addElement);
	}

	private static void initCustomSourceSelections()
	{
		String[] keys = SOURCES_CONTEXT.getStringArray("selectionNames");
		if (keys == null)
		{
			return;
		}
		for (String name : keys)
		{
			PropertyContext context = SOURCES_CONTEXT.createChildContext(name);
			String modeName = context.getProperty("gamemode");
			GameMode mode = SystemCollections.getGameModeNamed(modeName);
			if (mode == null)
			{
				Logging
					.errorPrint("Unable to load quick source '" + name + "'. Game mode '" + modeName + "' is missing");
				continue;
			}
			String[] selectionArray = context.getStringArray("campaigns");
			List<Campaign> sources = new ArrayList<>();
			boolean error = false;
			for (String campaign : selectionArray)
			{
				Campaign c = campaignMap.get(campaign);
				if (c != null)
				{
					sources.add(c);
				}
				else
				{
					error = true;
					Logging.log(Logging.WARNING, '\'' + campaign + '\'' + " campaign not found, custom quick source '"
						+ name + "' might not work correctly.");
				}
			}
			if (sources.isEmpty())
			{
				Logging.errorPrint("Unable to load quick source '" + name + "'. All of its sources are missing");
				continue;
			}
			CustomSourceSelectionFacade selection = new CustomSourceSelectionFacade(name);
			selection.setGameMode(mode);
			selection.setCampaigns(sources);
			if (error)
			{
				selection.setLoadingState(LoadingState.LOADED_WITH_ERRORS);
				selection.setErrorMessage("Some campaigns are missing");
			}
			customSources.addElement(selection);
			quickSources.addElement(selection);
		}
	}

	public static SourceSelectionFacade createCustomSourceSelection(String name)
	{
		SourceSelectionFacade selection = new CustomSourceSelectionFacade(name);
		customSources.addElement(selection);
		quickSources.addElement(selection);
		displayedSources.addElement(selection);
		setCustomSourceSelectionArray();
		return selection;
	}

	public static void deleteCustomSourceSelection(SourceSelectionFacade source)
	{
		if (!(source instanceof CustomSourceSelectionFacade))
		{
			throw new IllegalArgumentException();
		}
		customSources.removeElement(source);
		quickSources.removeElement(source);
		displayedSources.removeElement(source);
		PropertyContext context = SOURCES_CONTEXT.createChildContext(source.toString());
		context.removeProperty("gamemode");
		context.removeProperty("campaigns");
		setCustomSourceSelectionArray();
	}

	private static void setCustomSourceSelectionArray()
	{
		List<String> sources = new ArrayList<>();
		for (SourceSelectionFacade csel : customSources)
		{
			sources.add(csel.toString());
		}
		SOURCES_CONTEXT.setStringArray("selectionNames", sources);
	}

	public static SourceSelectionFacade createSourceSelection(GameMode gameMode,
		List<? extends Campaign> campaignList)
	{
		return new BasicSourceSelectionFacade(null, new DefaultListFacade(campaignList), gameMode);
	}

	public static SourceSelectionFacade createSourceSelection(GameMode gameMode,
		List<? extends Campaign> campaignList, String sourceTitle)
	{
		return new BasicSourceSelectionFacade(sourceTitle, new DefaultListFacade(campaignList), gameMode);
	}

	/**
	 * returns a list of all SourceSelections that have not been hidden.
	 * @return a ListFacade containing SourceSelections
	 */
	public static ListFacade<SourceSelectionFacade> getDisplayedSourceSelections()
	{
		return displayedSources;
	}

	public static ListFacade<SourceSelectionFacade> getSourceSelections()
	{
		return quickSources;
	}

	public static ListFacade<SourceSelectionFacade> getCustomSourceSelections()
	{
		return customSources;
	}

	public static ListFacade<Campaign> getCampaigns()
	{
		return campaigns;
	}

	public static ListFacade<GameMode> getGameModes()
	{
		return gamemodes;
	}

	public static ListFacade<GameModeDisplayFacade> getGameModeDisplays()
	{
		return gamemodedisplays;
	}

	public static ListFacade<Campaign> getSupportedCampaigns(GameMode gameMode)
	{
		if (!campaignListMap.containsKey(gameMode))
		{
			return ListFacades.emptyList();
		}
		return campaignListMap.get(gameMode);
	}

	/**
	 * @return the Campaign Info Factory for the gui2 package.
	 */
	public static CampaignInfoFactory getCampaignInfoFactory()
	{
		return CAMP_INFO_FACTORY;
	}

	public static boolean passesPrereqs(List<Campaign> campaigns)
	{
		PersistenceManager pman = PersistenceManager.getInstance();
		List<URI> oldList = pman.getChosenCampaignSourcefiles();
		List<URI> uris = new ArrayList<>();
		for (Campaign campaign : campaigns)
		{
			uris.add(campaign.getSourceURI());
		}
		pman.setChosenCampaignSourcefiles(uris);
		for (Campaign campaign : campaigns)
		{
			if (!campaign.qualifies(null, campaign))
			{
				pman.setChosenCampaignSourcefiles(oldList);
				return false;
			}
		}
		pman.setChosenCampaignSourcefiles(oldList);
		return true;
	}

	private static final class BasicSourceSelectionFacade implements SourceSelectionFacade
	{

		private final ListFacade<Campaign> campaignModel;
		private final DefaultReferenceFacade<GameMode> gameMode;
		private final String name;

		private BasicSourceSelectionFacade(String name, ListFacade<Campaign> campaignModel,
			GameMode gameMode)
		{
			this.name = name;
			this.campaignModel = campaignModel;
			this.gameMode = new DefaultReferenceFacade<>(gameMode);
		}

		@Override
		public void setCampaigns(List<Campaign> campaign)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setGameMode(GameMode gameMode)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isModifiable()
		{
			return false;
		}

		@Override
		public LoadingState getLoadingState()
		{
			return LoadingState.LOADED;
		}

		@Override
		public String getLoadingErrorMessage()
		{
			return null;
		}

		@Override
		public String toString()
		{
			if (name != null)
			{
				return name;
			}
			if (gameMode.get() != null)
			{
				return LanguageBundle.getFormattedString("in_source_gamemode", gameMode.get().getDisplayName());
			}
			return "";
		}

		@Override
		public ListFacade<Campaign> getCampaigns()
		{
			return campaignModel;
		}

		@Override
		public ReferenceFacade<GameMode> getGameMode()
		{
			return gameMode;
		}

	}

	private static final class CustomSourceSelectionFacade implements SourceSelectionFacade
	{

		private final PropertyContext context;
		private final String name;
		private LoadingState loadingState = LoadingState.LOADED;
		private String errorMessage = null;

		private CustomSourceSelectionFacade(String name)
		{
			this.name = name;
			this.context = FacadeFactory.SOURCES_CONTEXT.createChildContext(name);
		}

		private final WriteableReferenceFacade<GameMode> gameMode = new DefaultReferenceFacade<>();
		private final DefaultListFacade<Campaign> campaigns = new DefaultListFacade<>();

		@Override
		public boolean isModifiable()
		{
			return true;
		}

		@Override
		public void setCampaigns(List<Campaign> campaign)
		{
			campaigns.setContents(campaign);
			List<String> camps = new ArrayList<>();
			for (Campaign camp : campaign)
			{
				camps.add(camp.getKeyName());
			}
			context.setStringArray("campaigns", camps);
		}

		@Override
		public void setGameMode(GameMode gameMode)
		{
			this.gameMode.set(gameMode);
			context.setProperty("gamemode", gameMode.getName());
		}

		@Override
		public String toString()
		{
			return name;
		}

		@Override
		public LoadingState getLoadingState()
		{
			return loadingState;
		}

		@Override
		public String getLoadingErrorMessage()
		{
			return errorMessage;
		}

		private void setLoadingState(LoadingState loadingState)
		{
			this.loadingState = loadingState;
		}

		private void setErrorMessage(String errorMessage)
		{
			this.errorMessage = errorMessage;
		}

		@Override
		public ListFacade<Campaign> getCampaigns()
		{
			return campaigns;
		}

		@Override
		public ReferenceFacade<GameMode> getGameMode()
		{
			return gameMode;
		}

	}

}
