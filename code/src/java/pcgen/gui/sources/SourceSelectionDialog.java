/*
 * SourceSelectionDialog.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 9/11/2008 08:01:30
 *
 * $Id: $
 */

package pcgen.gui.sources;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.apache.commons.lang.ArrayUtils;

import pcgen.base.lang.StringUtil;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceManager;
import pcgen.util.PropertyFactory;

/**
 * The Class <code>SourceSelectionDialog</code> provides a simplified
 * interface to selecting source materials for PCGen. It is intended 
 * to assist the majority of regular users who use only a few source 
 * combinations as well as making source selection easier for first 
 * time users. 
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
@SuppressWarnings("serial")
public class SourceSelectionDialog extends JDialog implements
		ActionListener
{

	static final String ACTION_CANCEL = "cancel";
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_HIDE = "hide";
	private static final String ACTION_UNHIDE = "unhide";
	private static final String ACTION_ADD = "add";
	
	private JList sourceList;
	private DefaultListModel sourceModel;
	private Map<String, List<String>> nameToSourceMap = new HashMap<String, List<String>>();
	private Map<String, String> nameToGameModeMap = new HashMap<String, String>();
	private String lastLoadedCollection;

	/**
	 * Creates new form SourceSelectionDialog.
	 * 
	 * @param parent the parent dialog or window.
	 * @param modal Should the dialog block the program
	 */
	public SourceSelectionDialog(Frame parent, boolean modal)
	{
		super(parent, modal);
		setTitle(PropertyFactory.getString("in_qsrc_title"));
		initComponents();
		setLocationRelativeTo(parent); // centre on parent
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 4);

		JLabel jLabel1 = new JLabel(PropertyFactory.getString("in_qsrc_intro"));
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0.0, 0.0);
		getContentPane().add(jLabel1, gbc);

		sourceList = new javax.swing.JList();
		sourceModel = new DefaultListModel();
		List<String> strings = getSourceNames();
		for (String string : strings)
		{
			sourceModel.addElement(string);
		}
		sourceList.setModel(sourceModel);
		sourceList
			.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		sourceList.setLayoutOrientation(JList.VERTICAL_WRAP);
		sourceList.setVisibleRowCount(2);
		sourceList.setCellRenderer(new SourceListCellRenderer());
		JScrollPane listScrollPane = new JScrollPane(sourceList);
		listScrollPane.setPreferredSize(new Dimension(480, 240));
		if (lastLoadedCollection != null && lastLoadedCollection.length() > 0)
		{
			sourceList.setSelectedValue(lastLoadedCollection, true);
		}

		Utility.buildRelativeConstraints(gbc, 1, 3, 100, 100,
			GridBagConstraints.BOTH, GridBagConstraints.WEST);
		getContentPane().add(listScrollPane, gbc);

		JButton addButton = new JButton(PropertyFactory.getString("in_add"));
		addButton.setActionCommand(ACTION_ADD);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		getContentPane().add(addButton, gbc);

		JButton hideButton =
				new JButton(PropertyFactory.getString("in_hide"));
		hideButton.setActionCommand(ACTION_HIDE);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
		getContentPane().add(hideButton, gbc);

		JButton unhideButton =
				new JButton(PropertyFactory.getString("in_unhide"));
		unhideButton.setActionCommand(ACTION_UNHIDE);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
		getContentPane().add(unhideButton, gbc);

		JButton loadButton = new JButton(PropertyFactory.getString("in_load"));
		loadButton.setActionCommand(ACTION_LOAD);
		getRootPane().setDefaultButton(loadButton);
		Utility.buildRelativeConstraints(gbc, 1, 1, 0.0, 0.0,
			GridBagConstraints.NONE, GridBagConstraints.EAST);
		getContentPane().add(loadButton, gbc);

		JButton cancelButton =
				new JButton(PropertyFactory.getString("in_cancel"));
		cancelButton.setActionCommand(ACTION_CANCEL);
		Utility.buildRelativeConstraints(gbc, 1, 1, 0, 0);
		getContentPane().add(cancelButton, gbc);

		//Listen for actions on the buttons
		addButton.addActionListener(this);
		hideButton.addActionListener(this);
		unhideButton.addActionListener(this);
		loadButton.addActionListener(this);
		cancelButton.addActionListener(this);

		//Listen for actions on the list
		sourceList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				sourceListMouseClicked(evt);
			}
		});

		pack();
	}

	/**
	 * Build the list of source names to be displayed. Note this 
	 * also rebuilds the maps nameToSourceMap and nameToGameModeMap
	 * as these are driven by the source names.
	 * 
	 * @return the source names
	 */
	private List<String> getSourceNames()
	{
		List<String> names = new ArrayList<String>();
		nameToSourceMap.clear();
		nameToGameModeMap.clear();
		
		for (Campaign aCamp : Globals.getCampaignList())
		{
			if (aCamp.canShowInMenu())
			{
				String name = aCamp.getDisplayName();
				names.add(name);
				nameToGameModeMap.put(name, aCamp.getGameModes().get(0));
				List<String> sourceKeys = new ArrayList<String>();
				sourceKeys.add(aCamp.getKeyName());
				nameToSourceMap.put(name, sourceKeys);
			}
		}
		
		for (GameMode mode : SystemCollections.getUnmodifiableGameModeList())
		{
			String title = mode.getDefaultSourceTitle();
			if (SettingsHandler.getGame().equals(mode) && title == null
				&& !mode.getDefaultDataSetList().isEmpty())
			{
				title =
						PropertyFactory.getFormattedString(
							"in_qsrc_game_default", mode.getName());
			}
			if (title != null && !"".equals(title))
			{
				names.add(title);
				nameToGameModeMap.put(title, mode.getName());
				nameToSourceMap.put(title, mode.getDefaultDataSetList());
			}
		}
		
		// Add in the last loaded campaigns
		List<URI> chosenCampaigns =
			PersistenceManager.getInstance().getChosenCampaignSourcefiles();
		if (!chosenCampaigns.isEmpty())
		{
			String currGameModeName = SettingsHandler.getGame().getName();
			boolean found = false;
			for (String name : names)
			{
				if (isSameCollection(name, currGameModeName, chosenCampaigns))
				{
					found = true;
					lastLoadedCollection = name;
				}
			}
			if (!found)
			{
				String title =
					PropertyFactory.getFormattedString(
						"in_qsrc_last_loaded", currGameModeName);
				names.add(title);
				nameToGameModeMap.put(title, currGameModeName);
				List<String> chosenCampaignNames = convertToNames(chosenCampaigns);
				nameToSourceMap.put(title, chosenCampaignNames);
			}
		}
		
		// Hide the names of any hidden sources
		String hiddenSources = SettingsHandler.getHiddenSources();
		String[] hiddenSourceNames = hiddenSources.split("\\|"); 
		for (String name : hiddenSourceNames)
		{
			names.remove(name);
		}
		
		// Order according to the saved prefs
		List<String> finalNames = new ArrayList<String>();
		String prefs = SettingsHandler.getQuickLaunchSources();
		String[] prefNames = prefs.split("\\|"); 
		for (String sourceName : prefNames)
		{
			if (names.contains(sourceName))
			{
				names.remove(sourceName);
				finalNames.add(sourceName);
			}
		}
		
		// Add any new items
		Collections.sort(names);
		for (String sourceName : names)
		{
			finalNames.add(sourceName);
		}
		String newPrefs = StringUtil.join(finalNames, "|");
		SettingsHandler.setQuickLaunchSources(newPrefs);
		
		return finalNames;
	}


	private boolean isSameCollection(String name, String gameModeName,
		List<URI> chosenCampaigns)
	{
		if (!gameModeName.equals(nameToGameModeMap.get(name)))
		{
			return false;
		}
		List<String> nameSources = nameToSourceMap.get(name);
		if (chosenCampaigns.size() != nameSources.size())
		{
			return false;
		}
		for (URI uri : chosenCampaigns)
		{
			final Campaign aCampaign = Globals.getCampaignByURI(uri);
			if (aCampaign == null
				|| !nameSources.contains(OutputNameFormatting.piString(
					((PObject) aCampaign), true)))
			{
				return false;
			}
//			String absPath = uri.getPath();
//			String relPath = SourceSelectionUtils.convertPathToDataPath(absPath);
//			if (!nameSources.contains(relPath))
//			{
//				return false;
//			}
		}
		return true;
	}

	private List<String> convertToNames(List<URI> chosenCampaigns)
	{
		List<String> names = new ArrayList<String>();
		for (URI uri : chosenCampaigns)
		{
			String absPath = uri.getPath();
			names.add(SourceSelectionUtils.convertPathToDataPath(absPath));
		}
		return names;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (ACTION_LOAD.equals(e.getActionCommand()))
		{
			loadButtonAction();
		}
		else if (ACTION_CANCEL.equals(e.getActionCommand()))
		{
			cancelButtonAction();
		}
		else if (ACTION_ADD.equals(e.getActionCommand()))
		{
			addButtonAction();
		}
		else if (ACTION_HIDE.equals(e.getActionCommand()))
		{
			hideButtonAction();
		}
		else if (ACTION_UNHIDE.equals(e.getActionCommand()))
		{
			unhideButtonAction();
		}
	}

	private void addButtonAction()
	{
		// Found out which game mode they want to work with
		GameModeDialog gmDialog = new GameModeDialog((Frame) this.getParent(), true);
		gmDialog.setVisible(true);
		String gmName = gmDialog.getGameModeKey();
		
		// Unload all sources
		SourceSelectionUtils.unloadSources();

		// Switch game mode
		if (!Globals.isInGameMode(gmName))
		{
			SourceSelectionUtils.changeGameMode(gmName);
		}
		
		// Create the sources
		CreateSourceDialog csd = new CreateSourceDialog((Frame) this.getParent(), true);
		csd.setVisible(true);
		
		// Refresh the sources
		SourceSelectionUtils.refreshSources();
		sourceModel.clear();
		List<String> strings = getSourceNames();
		for (String string : strings)
		{
			sourceModel.addElement(string);
		}

	}

	/**
	 * User has clicked/double clicked on the source list
	 * If double click send to load
	 *
	 * @param evt The event to be processed
	 */
	private void sourceListMouseClicked(MouseEvent evt)
	{
		if (sourceList.getSelectedIndex() >= 0)
		{
			if (evt.getClickCount() == 2)
			{
				loadButtonAction();
			}
		}
	}

	/**
	 * Load the selected source. Note this will also unload any sources already 
	 * loaded and switch game modes. 
	 */
	public void loadButtonAction()
	{
		// verify a source is selected
		if (sourceList.getSelectedIndex() < 0)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_qsrc_err_none_selected"), PropertyFactory.getString("in_qsrc_title"), MessageType.ERROR);
			return;
		}
		String sourceTitle = (String) sourceList.getSelectedValue();
		String gameMode = nameToGameModeMap.get(sourceTitle);
		List<String> sources = nameToSourceMap.get(sourceTitle);
		
		// Unload all sources
		SourceSelectionUtils.unloadSources();

		// Switch game mode
		if (!Globals.isInGameMode(gameMode))
		{
			SourceSelectionUtils.changeGameMode(gameMode);
		}
		
		// Load sources
		List<Campaign> selectedCampaigns = new ArrayList<Campaign>();
		for (String key : sources)
		{
			Campaign camp = Globals.getCampaignKeyed(key);
			selectedCampaigns.add(camp);
		}
		SourceSelectionUtils.loadSources(selectedCampaigns);

		// Close window
		setVisible(false);
		this.dispose();
	}

	public void cancelButtonAction()
	{
		setVisible(false);
		this.dispose();
	}

	private void hideButtonAction()
	{
		int selIndex = sourceList.getSelectedIndex();
		// verify a source is selected
		if (selIndex < 0)
		{
			return;
		}

		String sourceTitle = (String) sourceList.getSelectedValue();
		sourceModel.remove(selIndex);
		if (selIndex < sourceModel.getSize())
		{
			sourceList.setSelectedIndex(selIndex);
		}
		String hiddenSources = SettingsHandler.getHiddenSources();
		if (hiddenSources.length() > 0)
		{
			hiddenSources += "|";
		}
		hiddenSources += sourceTitle;
		SettingsHandler.setHiddenSources(hiddenSources);
		
		rebuildQuickSourcePrefsString();
	}

	/**
	 * Rebuild the list of quick launch sources and store it to preferences.
	 */
	void rebuildQuickSourcePrefsString()
	{
		String prefsString = "";
		for (int i=0;i<sourceModel.getSize(); i++)
		{
			String value = (String) sourceModel.get(i);
			prefsString += (i > 0 ? "|" : "") + value; 
		}
		SettingsHandler.setQuickLaunchSources(prefsString);
	}

	private void unhideButtonAction()
	{
		UnhideDialog dialog = new UnhideDialog((Frame) this.getParent(), true, sourceModel);
		dialog.setVisible(true);
	}

	/**
	 * Display of a single source cell in the sources list.
	 */
	private static class SourceListCellRenderer extends JToggleButton implements
			ListCellRenderer
	{

		/**
		 * Instantiates a new source list cell renderer.
		 */
		public SourceListCellRenderer()
		{
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.TOP);
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			setText("<HTML>" + String.valueOf(value) + "</HTML>");
			setPreferredSize(new Dimension(120, 120));

			setContentAreaFilled(isSelected);
			setSelected(isSelected);
			return this;
		}

	}
	
	/**
	 * Dialog to allow hidden sources to be selected and shown again.
	 */
	private class UnhideDialog extends JDialog implements ActionListener
	{
		private static final String ACTION_OK = "OK";
		
		DefaultListModel sourcesList;
		JList hiddenList;
		
		/**
		 * Creates new form SourceSelectionDialog.
		 * 
		 * @param parent the parent dialog or window.
		 * @param modal Should the dialog block the program
		 */
		public UnhideDialog(Frame parent, boolean modal, DefaultListModel sourcesList)
		{
			super(parent, modal);
			setTitle(PropertyFactory.getString("in_qsrc_unhide_title"));
			this.sourcesList = sourcesList;
			initComponents();
			setLocationRelativeTo(parent); // centre on parent
		}

		/**
		 * Create the dialog's user interface
		 */
		private void initComponents()
		{
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(new java.awt.GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);

			hiddenList = new javax.swing.JList();
			hiddenList.setModel(new javax.swing.AbstractListModel()
			{
				String[] strings = getHiddenSourceNames();

				public int getSize()
				{
					return strings.length;
				}

				public Object getElementAt(int i)
				{
					return strings[i];
				}

				private String[] getHiddenSourceNames()
				{
					String hiddenSources = SettingsHandler.getHiddenSources();
					String[] names =  hiddenSources.split("\\|");
					Arrays.sort(names);
					return names;
				}
			});
			hiddenList
				.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			JScrollPane listScrollPane = new JScrollPane(hiddenList);

			Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
				GridBagConstraints.BOTH, GridBagConstraints.WEST);
			getContentPane().add(listScrollPane, gbc);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			JButton okButton = new JButton(PropertyFactory.getString("in_ok"));
			okButton.setActionCommand(ACTION_OK);
			getRootPane().setDefaultButton(okButton);
			buttonPanel.add(okButton);

			JButton cancelButton =
					new JButton(PropertyFactory.getString("in_cancel"));
			cancelButton.setActionCommand(ACTION_CANCEL);
			buttonPanel.add(cancelButton);

			Utility.buildRelativeConstraints(gbc, 1, 1, 0.0, 0.0,
				GridBagConstraints.NONE, GridBagConstraints.EAST);
			getContentPane().add(buttonPanel, gbc);

			//Listen for actions on the buttons
			okButton.addActionListener(this);
			cancelButton.addActionListener(this);

			pack();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (ACTION_OK.equals(e.getActionCommand()))
			{
				String hiddenSources = SettingsHandler.getHiddenSources();
				String[] hiddenSourceNames = hiddenSources.split("\\|");
				
				Object[] selectedSourceNames = hiddenList.getSelectedValues();
				for (Object name : selectedSourceNames)
				{
					hiddenSourceNames = (String[]) ArrayUtils.removeElement(hiddenSourceNames, name);
					sourceModel.addElement(name);
				}
				String newHiddenSources = StringUtil.join(hiddenSourceNames, "|");
				SettingsHandler.setHiddenSources(newHiddenSources);
				rebuildQuickSourcePrefsString();
			}
			setVisible(false);
			this.dispose();
		}

	}
	
	/**
	 * Dialog to allow a game mode to be selected.
	 */
	private static class GameModeDialog extends JDialog implements ActionListener
	{
		private static final String ACTION_OK = "OK";

		JComboBoxEx gameModeCombo;
		String gameModeKey = "";
		
		
		/**
		 * Creates new form SourceSelectionDialog.
		 * 
		 * @param parent the parent dialog or window.
		 * @param modal Should the dialog block the program
		 */
		public GameModeDialog(Frame parent, boolean modal)
		{
			super(parent, modal);
			setTitle(PropertyFactory.getString("in_cs_title"));
			initComponents();
			setLocationRelativeTo(parent); // centre on parent
		}

		/**
		 * Create the dialog's user interface
		 */
		private void initComponents()
		{
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(new java.awt.GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);

			
			JLabel introLabel = new JLabel(PropertyFactory.getString("in_qsrc_gameModeIntro"));
			Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
			getContentPane().add(introLabel, gbc);
			
			List<GameMode> games = SystemCollections.getUnmodifiableGameModeList();
			String gameModeNames[] = new String[games.size()];
			for (int i = 0; i < gameModeNames.length; i++)
			{
				gameModeNames[i] = games.get(i).getName();
			}
			gameModeCombo = new JComboBoxEx(gameModeNames);
			gameModeCombo.setSelectedItem(SettingsHandler.getGame().getName());

			Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
				GridBagConstraints.BOTH, GridBagConstraints.WEST);
			getContentPane().add(gameModeCombo, gbc);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			JButton okButton = new JButton(PropertyFactory.getString("in_ok"));
			okButton.setActionCommand(ACTION_OK);
			getRootPane().setDefaultButton(okButton);
			buttonPanel.add(okButton);

			JButton cancelButton =
					new JButton(PropertyFactory.getString("in_cancel"));
			cancelButton.setActionCommand(ACTION_CANCEL);
			buttonPanel.add(cancelButton);

			Utility.buildRelativeConstraints(gbc, 1, 1, 0.0, 0.0,
				GridBagConstraints.NONE, GridBagConstraints.EAST);
			getContentPane().add(buttonPanel, gbc);

			//Listen for actions on the buttons
			okButton.addActionListener(this);
			cancelButton.addActionListener(this);

			pack();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (ACTION_OK.equals(e.getActionCommand()))
			{
				gameModeKey = (String) gameModeCombo.getSelectedItem();
			}
			setVisible(false);
			this.dispose();
		}

		/**
		 * @return the gameModeKey
		 */
		public String getGameModeKey()
		{
			return gameModeKey;
		}

	}
}
