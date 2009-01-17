/*
 * CampaignPanel.java
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
 * Created on 17/01/2009 10:59:55 PM
 *
 * $Id: $
 */
package pcgen.gui.converter.panel;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.gui.converter.event.ProgressEvent;

/**
 * The Class <code>CampaignPanel</code> displays a panel allowing 
 * the user to select the campaigns to be converted.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class CampaignPanel extends ConvertSubPanel
{

	private SpringLayout layout = new SpringLayout();

	private List<Campaign> gameModeCampaigns;
	private String folderName;

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#autoAdvance(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#performAnalysis(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		GameMode game = pc.get(ObjectKey.GAME_MODE);
		List<String> gameModeList = new ArrayList<String>();
		gameModeList.add(game.getName());
		
		File sourceFolder = pc.get(ObjectKey.DIRECTORY);
		folderName = sourceFolder.toURI().toString();

		// Only add those campaigns in the user's chosen folder and game mode
		List<Campaign> allCampaigns = Globals.getCampaignList();
		gameModeCampaigns = new ArrayList<Campaign>();
		for (Campaign campaign : allCampaigns)
		{
			if (campaign.isGameMode(gameModeList))
			{
				if (campaign.getSourceURI().toString().startsWith(folderName))
				{
					gameModeCampaigns.add(campaign);
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#setupDisplay(javax.swing.JPanel, pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(layout);
		JLabel introLabel =
				new JLabel("Please select the Campaign(s) to Convert:");
		panel.add(introLabel);
		layout.putConstraint(SpringLayout.NORTH, introLabel, 20,
			SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, introLabel, 25,
			SpringLayout.WEST, panel);
		
		final CampaignTableModel model = new CampaignTableModel(gameModeCampaigns, folderName);
		final JTable table = new JTable(model){    
		    //Implement table cell tool tips.
		    public String getToolTipText(MouseEvent e) {
		        java.awt.Point p = e.getPoint();
		        int rowIndex = rowAtPoint(p);
		        int colIndex = columnAtPoint(p);
	            String tip = String.valueOf(getValueAt(rowIndex, colIndex));
		        return tip;
		    }
		};
		table.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent event)
				{
					ListSelectionModel lsm =
							(ListSelectionModel) event.getSource();

					//int viewRow = table.getSelectedRow();
					pc.removeListFor(ListKey.CAMPAIGN);
					if (lsm.isSelectionEmpty())
					{
						fireProgressEvent(ProgressEvent.NOT_ALLOWED);
					}
					else
					{
						// Find out which indexes are selected.
						int minIndex = lsm.getMinSelectionIndex();
						int maxIndex = lsm.getMaxSelectionIndex();
						for (int i = minIndex; i <= maxIndex; i++)
						{
							if (lsm.isSelectedIndex(i))
							{
								int modelRow = table.convertRowIndexToModel(i);
								Campaign selCampaign =
										(Campaign) model
											.getValueAt(modelRow, 0);
								pc.addToListFor(ListKey.CAMPAIGN, selCampaign);
							}
						}
						fireProgressEvent(ProgressEvent.ALLOWED);
					}
				}
			}
		);

		JScrollPane listScroller = new JScrollPane(table);
		panel.add(listScroller);
		layout.putConstraint(SpringLayout.NORTH, listScroller, 20,
			SpringLayout.SOUTH, introLabel);
		layout.putConstraint(SpringLayout.WEST, listScroller, 25,
			SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.SOUTH, listScroller, -50,
			SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.EAST, listScroller, -25,
			SpringLayout.EAST, panel);
		
	}

	/**
	 * The model of the campaign table.
	 */
	@SuppressWarnings("serial")
	public class CampaignTableModel extends AbstractTableModel
	{
		
		/** The column names. */
		private String[] columnNames = {"Campaign", "Location"};
		
		/** The row data. */
		private Object[][] rowData;

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
				rowData[i++] =
						new Object[]{
							campaign,
							campaign.getSourceURI().toString().substring(
								prefix.length())};
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int col)
		{
			return columnNames[col].toString();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount()
		{
			return rowData.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount()
		{
			return columnNames.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col)
		{
			return rowData[row][col];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int col)
		{
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int row, int col)
		{
			// read only 
		}
	}

}
