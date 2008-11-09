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

package pcgen.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.Utility;
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
public class SourceSelectionDialog extends javax.swing.JDialog implements
		ActionListener
{

	private static final String ACTION_CANCEL = "cancel";
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_REMOVE = "remove";
	private static final String ACTION_ADD = "add";
	
	private JList sourceList;
	private Map<String, List<String>> nameToSourceMap = new HashMap<String, List<String>>();
	private Map<String, String> nameToGameModeMap = new HashMap<String, String>();

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
		sourceList.setModel(new javax.swing.AbstractListModel()
		{
			String[] strings = getSourceNames();

			//{ "SRD 3.0\nfor Players", "SRD 3.0 for Game Masters (includes Monsters)<br>111222333 111222333", "SRD 3.5 for Players", "SRD 3.5 for Game Masters (includes Monsters)", "MSRD" };

			public int getSize()
			{
				return strings.length;
			}

			public Object getElementAt(int i)
			{
				return strings[i];
			}
		});
		sourceList
			.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		sourceList.setLayoutOrientation(JList.VERTICAL_WRAP);
		sourceList.setVisibleRowCount(2);
		sourceList.setCellRenderer(new SourceListCellRenderer());
		JScrollPane listScrollPane = new JScrollPane(sourceList);
		listScrollPane.setPreferredSize(new Dimension(480, 240));

		Utility.buildRelativeConstraints(gbc, 1, 2, 100, 100,
			GridBagConstraints.BOTH, GridBagConstraints.WEST);
		getContentPane().add(listScrollPane, gbc);

		JButton addButton = new JButton(PropertyFactory.getString("in_add"));
		addButton.setActionCommand(ACTION_ADD);
		addButton.setEnabled(false);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		getContentPane().add(addButton, gbc);

		JButton removeButton =
				new JButton(PropertyFactory.getString("in_remove"));
		removeButton.setActionCommand(ACTION_REMOVE);
		removeButton.setEnabled(false);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
		getContentPane().add(removeButton, gbc);

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
		removeButton.addActionListener(this);
		loadButton.addActionListener(this);
		cancelButton.addActionListener(this);

		pack();
	}

	/**
	 * Build the list of source names to be displayed. Note this 
	 * also rebuilds the maps nameToSourceMap and nameToGameModeMap
	 * as these are driven by the source names.
	 * 
	 * @return the source names
	 */
	private String[] getSourceNames()
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
			if (title != null && !"".equals(title))
			{
				names.add(title);
				nameToGameModeMap.put(title, mode.getName());
				nameToSourceMap.put(title, mode.getDefaultDataSetList());
			}
		}
		
		Collections.sort(names);
		return names.toArray(new String[]{});
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
			//TODO
		}
		else if (ACTION_REMOVE.equals(e.getActionCommand()))
		{
			//TODO
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

	/**
	 * Overrides the default setVisible method to position the window. 
	 *
	 * @param visible true to show the dialog, false to hide it.
	 */
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			Window owner = getOwner();
			Rectangle ownerBounds = owner.getBounds();
			Rectangle bounds = getBounds();
	
			int width = (int) bounds.getWidth();
			int height = (int) bounds.getHeight();
	
			setBounds(
				(int) (owner.getX() + ((ownerBounds.getWidth() - width) / 2)),
				(int) (owner.getY() + ((ownerBounds.getHeight() - height) / 2)),
				width, height);
		}
		
		super.setVisible(visible);
	}
	/**
	 * Testing main entry point
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{

			public void run()
			{
				SourceSelectionDialog dialog =
						new SourceSelectionDialog(new javax.swing.JFrame(),
							true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter()
				{

					public void windowClosing(java.awt.event.WindowEvent e)
					{
						System.exit(0);
					}

				});
				dialog.setVisible(true);
			}

		});
	}

	/**
	 * Display of a single source cell in the sources list.
	 */
	private class SourceListCellRenderer extends JToggleButton implements
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
}
