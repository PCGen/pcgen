/*
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
 */
package pcgen.gui2.converter.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.tools.Utility;
import pcgen.system.PCGenSettings;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code CampaignPanel} displays a panel allowing
 * the user to select the campaigns to be converted.
 * 
 * 
 */
public class CampaignPanel extends ConvertSubPanel
{

	private List<Campaign> gameModeCampaigns;
	private String folderName;

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public boolean returnAllowed()
	{
		return true;
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		GameMode game = pc.get(ObjectKey.GAME_MODE);
        List<String> gameModeList = new ArrayList<>(game.getAllowedModes());

		File sourceFolder = pc.get(ObjectKey.DIRECTORY);
		folderName = sourceFolder.toURI().toString();

		// Only add those campaigns in the user's chosen folder and game mode
		List<Campaign> allCampaigns = Globals.getCampaignList();
		gameModeCampaigns = new ArrayList<>();
		for (Campaign campaign : allCampaigns)
		{
			if (campaign.containsAnyInList(ListKey.GAME_MODE, gameModeList))
			{
				if (campaign.getSourceURI().toString().startsWith(folderName))
				{
					gameModeCampaigns.add(campaign);
				}
			}
		}
		return false;
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(new GridBagLayout());
		JLabel introLabel = new JLabel("Please select the Campaign(s) to Convert:");
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST);
		gbc.insets = new Insets(25, 25, 5, 25);
		panel.add(introLabel, gbc);

		final CampaignTableModel model = new CampaignTableModel(gameModeCampaigns, folderName);
		final JTable table = new JTable(model)
		{
			//Implement table cell tool tips.
			@Override
			public String getToolTipText(MouseEvent e)
			{
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				String tip = String.valueOf(getValueAt(rowIndex, colIndex));
				return tip;
			}
		};
		table.getSelectionModel().addListSelectionListener(event -> {
            pc.removeListFor(ListKey.CAMPAIGN);
            int[] selRows = table.getSelectedRows();
            if (selRows.length == 0)
            {
                saveSourceSelection(pc);
                fireProgressEvent(ProgressEvent.NOT_ALLOWED);
            }
            else
            {
                for (int row : selRows)
                {
                    Campaign selCampaign = (Campaign) model.getValueAt(row, 0);
                    pc.addToListFor(ListKey.CAMPAIGN, selCampaign);
                }
                saveSourceSelection(pc);
                fireProgressEvent(ProgressEvent.ALLOWED);
            }
        });

		JScrollPane listScroller = new JScrollPane(table);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0, 1.0);
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(listScroller, gbc);

		initSourceSelection(model, table);
	}

	private void initSourceSelection(CampaignTableModel model, JTable table)
	{
		// Select any previous selections
		PCGenSettings context = PCGenSettings.getInstance();
		String sourceString = context.initProperty(PCGenSettings.CONVERT_SOURCES, "");
		String[] sources = sourceString.split("\\|");
		for (String srcName : sources)
		{
			for (Campaign camp : gameModeCampaigns)
			{
				if (camp.toString().equals(srcName))
				{
					for (int i = 0; i < model.getRowCount(); i++)
					{
						if (camp.equals(model.getValueAt(i, 0)))
						{
							table.getSelectionModel().addSelectionInterval(i, i);
							break;
						}
					}
					break;
				}
			}
		}
	}

	private void saveSourceSelection(CDOMObject pc)
	{
		List<Campaign> selCampaigns = pc.getSafeListFor(ListKey.CAMPAIGN);
		PCGenSettings context = PCGenSettings.getInstance();
		context.setProperty(PCGenSettings.CONVERT_SOURCES, StringUtils.join(selCampaigns, "|"));
	}

	/**
	 * The model of the campaign table.
	 */
	@SuppressWarnings("serial")
	public static class CampaignTableModel extends AbstractTableModel
	{

		/** The column names. */
		private final String[] columnNames = {"Campaign", "Location"};

		/** The row data. */
		private final Object[][] rowData;

		/**
		 * Instantiates a new campaign table model.
		 * 
		 * @param campList the list of campaigns to be displayed
		 * @param prefix the prefix to be removed from each campaign location.
		 */
		public CampaignTableModel(List<Campaign> campList, String prefix)
		{
			rowData = new Object[campList.size()][2];
			int i = 0;
			for (Campaign campaign : campList)
			{
				rowData[i++] = new Object[]{campaign, campaign.getSourceURI().toString().substring(prefix.length())};
			}
		}

		@Override
		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		@Override
		public int getRowCount()
		{
			return rowData.length;
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return rowData[row][col];
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return false;
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			// read only 
		}
	}

}
