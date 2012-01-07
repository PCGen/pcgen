/*
 * AgePanel.java
 * Copyright 2003 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on February 8, 2003, 6:00 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.Constants;
import pcgen.core.AgeSet;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.util.PropertyFactory;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.util.*;

/**
 * <code>AgePanel</code>
 *
 * Manages the setting of age related bio-settings.
 * These are: HAIR, EYES and SKINTONE
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class AgePanel extends JPanel implements PObjectUpdater
{
	static final long serialVersionUID = -5835737108073399178L;
	private static String defaultRegionName = Constants.NONE;
	private static final int COL_MINAGE = 1;
	private AgeTableModel ageModel = null; // Model for JTable

	//private static final int COL_AGEROLL = 2;
	//private static final int COL_MAXAGE = 3;

	/**
	 * Creates a new AgePanel
	 */
	AgePanel()
	{
		super();
		initComponents();
	}

	/**
	 * UpdateData takes the GUI components and updates the
	 * PObject obj with those values
	 *
	 * @see pcgen.gui.editor.PObjectUpdater#updateData(PObject)
	 */
	@Override
	public void updateData(PObject obj)
	{
		Race race;
		String region;
		String raceName;

		if (!(obj instanceof Race))
		{
			return;
		}

		race = (Race) obj;

		String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

		if (unp == null)
		{
			region = defaultRegionName;
		}
		else
		{
			region = unp[0];
		}

		raceName = race.getKeyName();

		ageModel.saveValues(region, raceName);
	}

	/**
	 * UpdateView takes the values from PObject obj
	 * and updates the GUI components
	 *
	 * @see pcgen.gui.editor.PObjectUpdater#updateView(PObject)
	 */
	@Override
	public void updateView(PObject obj)
	{
		Race race;
		String region;
		String raceKey;

		if (!(obj instanceof Race))
		{
			return;
		}

		race = (Race) obj;

		String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

		if (unp == null)
		{
			region = defaultRegionName;
		}
		else
		{
			region = unp[0];
		}

		raceKey = race.getKeyName();

		ageModel.reset(region, raceKey);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		ageModel = new AgeTableModel("", null);

		JTable table = new JTable(ageModel);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		this.add(scrollPane);
	}

	// ------------------------------------------------------------------

	/**
	 * AgeTableModel - inner class to manage the age data backing the age table.
	 */
	static final class AgeTableModel extends AbstractTableModel
	{
		private final String[] columnNames =
		{
			PropertyFactory.getString("in_demAgeName"), PropertyFactory.getString("in_demAgeMin"),
			PropertyFactory.getString("in_demAgeRoll"), PropertyFactory.getString("in_demAgeMax")
		};
		private List data = null;

		/**
		 * Create an instance of AgeTableModel for the supplied region and race.
		 *
		 * @param region The name of the race's region
		 * @param raceName The name of the target race.
		 */
		AgeTableModel(String region, String raceName)
		{
			reset(region, raceName);
		}

		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int col)
		{
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			return (col >= COL_MINAGE);
		}

		/**
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.
		 * @param c
		 * @return Class
		 */
		@Override
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			return data.size();
		}

		/**
		 * Saves the supplied value to the table model.
		 *
		 * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
		 */
		@Override
		public void setValueAt(Object value, int row, int col)
		{
			Object[] rowData = (Object[]) data.get(row);
			rowData[col] = value;
			fireTableCellUpdated(row, col);
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int col)
		{
			Object[] rowData = (Object[]) data.get(row);

			if ((rowData == null) || (col < 0) || (col > rowData.length))
			{
				return null;
			}

			return rowData[col];
		}

		/**
		 * Populates the values in the model from the BioSet entries
		 * for the supplied race and region.
		 *
		 * @param region The name of the race's region
		 * @param raceName The name of the target race.
		 */
		void reset(String region, String raceName)
		{
			String keyValue;
			String dataValue;
			Object[] ageSet;

			if ((region == null) || (region.length() == 0))
			{
				region = defaultRegionName;
			}

			// Build the list of ages
			data = new ArrayList();

			for (AgeSet as : Globals.getBioSet().getAgeSets(region).values())
			{
				ageSet = new Object[5];
				ageSet[0] = as.getName();
				ageSet[1] = "";
				ageSet[2] = "";
				ageSet[3] = "";
				ageSet[4] = region + "." + as.getIndex();
				data.add(ageSet);
			}

			if (raceName != null)
			{
				// Populate the data
				List dataValues = Globals.getBioSet().getTagForRace(region, raceName, "BASEAGE");

				if (dataValues != null)
				{
					Iterator dI = dataValues.iterator();

					for (Iterator iter = data.iterator(); iter.hasNext() && dI.hasNext();)
					{
						String value = (String) dI.next();
						Object[] element = (Object[]) iter.next();
						element[1] = value;
					}
				}

				if (dataValues != null)
				{
					dataValues = Globals.getBioSet().getTagForRace(region, raceName, "AGEDIEROLL");

					Iterator dI = dataValues.iterator();

					for (Iterator iter = data.iterator(); iter.hasNext() && dI.hasNext();)
					{
						String value = (String) dI.next();
						Object[] element = (Object[]) iter.next();
						element[2] = value;
					}
				}

				if (dataValues != null)
				{
					dataValues = Globals.getBioSet().getTagForRace(region, raceName, "MAXAGE");

					Iterator dI = dataValues.iterator();

					for (Iterator iter = data.iterator(); iter.hasNext() && dI.hasNext();)
					{
						String value = (String) dI.next();
						Object[] element = (Object[]) iter.next();
						element[3] = value;
					}
				}
			}
		}

		/**
		 * Saves the values in the model to the BioSet entries
		 * for the supplied race and region.
		 *
		 * @param region The name of the race's region
		 * @param raceName The name of the target race.
		 */
		void saveValues(String region, String raceName)
		{
			if ((region == null) || (region.length() == 0))
			{
				region = defaultRegionName;
			}

			Globals.getBioSet().removeFromUserMap(region, raceName, "BASEAGE");
			Globals.getBioSet().removeFromUserMap(region, raceName, "AGEDIEROLL");
			Globals.getBioSet().removeFromUserMap(region, raceName, "MAXAGE");

			int currentAgeSetIndex = 0;
			for (Iterator iter = data.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if (String.valueOf(element[1]).length() > 0)
				{
					Globals.getBioSet().addToUserMap(region, raceName, "BASEAGE:" + String.valueOf(element[1]), currentAgeSetIndex);
				}
				if (String.valueOf(element[2]).length() > 0)
				{
					Globals.getBioSet().addToUserMap(region, raceName, "AGEDIEROLL:" + String.valueOf(element[2]), currentAgeSetIndex);
				}
				if (String.valueOf(element[3]).length() > 0)
				{
					Globals.getBioSet().addToUserMap(region, raceName, "MAXAGE:" + String.valueOf(element[3]), currentAgeSetIndex);
				}
				currentAgeSetIndex++;
			}
		}

//		private void printDebugData()
//		{
//			int numRows = getRowCount();
//			int numCols = getColumnCount();
//
//			for (int i = 0; i < numRows; i++)
//			{
//				Globals.debugPrint("    row " + i + ":");
//				Object[] rowData = (Object[]) data.get(i);
//				for (int j = 0; j < numCols; j++)
//				{
//					Globals.debugPrint("  " + rowData[j]);
//				}
//				Globals.debugPrint("\n");
//			}
//			Globals.debugPrint("--------------------------\n");
//		}
	}
}
