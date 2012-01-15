/**
 * MainHP.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * @author  Greg Bingleman <byngl@hotmail.com>
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 **/
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Title:        MainHP.java
 * Description:  New GUI implementation for modifying PC hitpoints
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Greg Bingleman
 * @version $Revision$
 */
final class MainHP extends JPanel
{
	static final long serialVersionUID = 8762071234494775757L;
	private JButton okButton = new JButton();
	private JScrollPane currentHpPane;
	private JTableEx currentHpTable;
	private PCHitPointsTableModel currentHpTableModel = new PCHitPointsTableModel();
	private RendererEditor plusMinusRenderer = new RendererEditor();
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 */
	public MainHP()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	/**
	 * Set the PC for this component
	 * @param aPC
	 */
	public void setCharacter(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * Set the preferred size of the HP Pane to 1 more
	 * row than it contains (including header), maximum of 20 rows.
	 */
	public void setPSize()
	{
		Dimension preferredSize = currentHpPane.getPreferredSize();
		preferredSize.height = (Math.min(currentHpTable.getRowCount(), 20) + 2) * (currentHpTable.getRowHeight()
			+ currentHpTable.getRowMargin());
		currentHpPane.setPreferredSize(preferredSize);
	}

	private void hpTableMouseClicked(MouseEvent evt)
	{
		int iRow = currentHpTable.getSelectedRow();

		switch (currentHpTable.columnAtPoint(evt.getPoint()))
		{
			case 5: // "+"
				setForRow(iRow, getCurrentHP(iRow) + 1);
				break;

			case 6: // "-"
				setForRow(iRow, getCurrentHP(iRow) - 1);
				break;

			case 7:
				setForRow(iRow, Math.abs(Globals.getRandomInt(getHitDieSize(iRow))) + 1);
				break;
			default:
				return;
		}
	}

	private int getHitDieSize(int row)
	{
		if (row >= (currentHpTableModel.getRowCount() - 2))
		{
			return 0;
		}

		return ((Integer) currentHpTableModel.getValueAt(row, 1)).intValue();
	}

	private int getCurrentHP(int row)
	{
		return ((Integer) currentHpTableModel.getValueAt(row, 2)).intValue();
	}

	private void setForRow(int iRow, int value)
	{
		int iMax;
		int iRoll;

		// Make sure we are trying to set a real row
		if (iRow >= (currentHpTableModel.getRowCount() - 2))
		{
			return;
		}

		iMax = (Integer) currentHpTableModel.getValueAt(iRow, 1); // # of sides on die
		iRoll = value;

		if (iRoll > iMax)
		{
			iRoll = iMax;
			ShowMessageDelegate.showMessageDialog(
				"Setting roll to maximum (" + iMax + ')',
				Constants.APPLICATION_NAME, MessageType.ERROR);
		}

		if (iRoll < 1)
		{
			if (iRoll < 0)
			{
				iRoll = 0;
			}
			if (iMax > 0)
			{
				ShowMessageDelegate.showMessageDialog(
					"Roll must be at least the minimum (1)",
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}
		}
//		else if (iRoll > iMax)
//		{
//			ShowMessageDelegate.showMessageDialog("Roll cannot exceed the maximum (" + iMax + ')', Constants.APPLICATION_NAME, MessageType.ERROR);
//		}
//		else
		{
			PCClass aClass = null;

			if (aPC != null)
			{
				if ((iRow >= 0) && (iRow < aPC.getLevelInfoSize()))
				{
					aClass = aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(iRow));

					if (aClass != null)
					{
						final int lvl = aPC.getLevelInfoClassLevel(iRow) - 1;
						PCClassLevel classLevel = aPC.getActiveClassLevel(aClass, lvl);
						aPC.setAssoc(classLevel, AssociationKey.HIT_POINTS,
								Integer.valueOf(iRoll));
					}
				}

				aPC.setDirty(true);
				currentHpTableModel.fireTableDataChanged();
			}
		}
	}

	private void jbInit() throws Exception
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
					BorderFactory.createEmptyBorder()));
		getInsets().left = 5;
		getInsets().bottom = 5;
		getInsets().top = 5;
		getInsets().right = 5;

		currentHpPane = new JScrollPane();
		currentHpTable = new JTableEx();

		//
		// Hit points per level
		//
		currentHpTable.setModel(currentHpTableModel);
		currentHpTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentHpTable.setDoubleBuffered(false);
		currentHpPane.setViewportView(currentHpTable);

		this.add(currentHpPane, BorderLayout.CENTER);

		TableColumn col;
		col = currentHpTable.getColumnModel().getColumn(5);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setWidth(30);
		col.setMinWidth(30);

		col = currentHpTable.getColumnModel().getColumn(6);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setWidth(30);
		col.setMinWidth(30);

		col = currentHpTable.getColumnModel().getColumn(7);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(50);
		col.setWidth(50);
		col.setMinWidth(50);
		currentHpTable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					hpTableMouseClicked(evt);
				}
			});

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.PAGE_AXIS));
		buttonPane.add(Box.createVerticalStrut(5));
		okButton.setText(PropertyFactory.getString("in_ok"));
		okButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_ok"));
		okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PCGen_Frame1.getInst().hpTotal_Changed();

					JFrame parentFrame = (JFrame) Utility.getParentNamed(getParent(), HPFrame.class.getName());

					if (parentFrame != null)
					{
						parentFrame.dispose();
					}
				}
			});

		buttonPane.add(okButton);
		buttonPane.add(Box.createVerticalStrut(10));
		JButton rerollButton = new JButton(PropertyFactory.getString("in_reroll"));
		rerollButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_reroll"));
		rerollButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int iRows = currentHpTable.getRowCount() - 1;
				int startRow = 0;
				if (SettingsHandler.isHPMaxAtFirstLevel())
				{
					startRow++;
				}
				for (int i = startRow; i < iRows - 1; ++i)
				{
					final int dieSize = getHitDieSize(i);
					if (dieSize > 0)
					{
						setForRow(i, Math.abs(Globals.getRandomInt(dieSize)) + 1);
					}
					else
					{
						setForRow(i, 0);
					}
				}
			}
		});
		buttonPane.add(rerollButton);
		this.add(buttonPane, BorderLayout.EAST);
	}

	/** UNUSED
	   private class PCListModel extends AbstractListModel
	   {
	   public Object getElementAt(int index)
	   {
	   if (index < Globals.getPCList().size())
	   {
	   final PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(index);
	   return aPC.getDisplayName();
	   }
	   else
	   {
	   return null;
	   }
	   }
	   public int getSize()
	   {
	   return Globals.getPCList().size();
	   }
	   }
	 */
	/**
	 *
	 */
	final class PCHitPointsTableModel extends AbstractTableModel
	{
		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return (colIndex == 2);
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0: // Name
				case 5: // +
				case 6: // -
				case 7: // Reroll line
					return String.class;

				case 1: // Sides
				case 2: // Roll
				case 3: // Con Adj
				case 4: // Total
					return Integer.class;

				default:
					break;
			}

			return null;
		}

		public int getColumnCount()
		{
			return 8;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Class";

				case 1:
					return "Sides";

				case 2:
					return "Roll";

				case 3:
					return "Stat Adj";

				case 4:
					return "Total";

				case 5:
					return "+";

				case 6:
					return "-";

				case 7:
					return "Reroll";
				default:
					break;
			}

			return "Out Of Bounds";
		}

		public int getRowCount()
		{
			int iRows = 2;

			if (aPC != null)
			{
				for (PCClass aClass : aPC.getClassSet())
				{
					iRows += aPC.getLevel(aClass);
				}
			}

			return iRows;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex == 2)
			{
				setForRow(rowIndex, Integer.parseInt(aValue.toString()));
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			final int askedRowIndex = rowIndex;
			int iHp = 0;
			int iSides = 0;

			if (aPC != null)
			{
				final Race aRace = aPC.getRace();

				PCClass aClass = null;

				// Get templates - give the PC the biggest HD
				// sk4p 11 Dec 2002

				if ((rowIndex >= 0) && (rowIndex < aPC.getLevelInfoSize()))
				{
					final String classKeyName = aPC.getLevelInfoClassKeyName(rowIndex);
					aClass = aPC.getClassKeyed(classKeyName);

					if (aClass != null)
					{
						final int lvl = aPC.getLevelInfoClassLevel(rowIndex);
						final int baseSides = aPC.getLevelHitDie(aClass, lvl).getDie();
						PCClassLevel pcl = aPC.getActiveClassLevel(aClass, lvl - 1);
						Integer hp = aPC.getAssoc(pcl, AssociationKey.HIT_POINTS);
						iHp = hp == null ? 0 : hp;
						iSides = baseSides + (int) aClass.getBonusTo("HD", "MAX", lvl, aPC);

						//
						// Sanity check
						//
						if (iHp > iSides)
						{
							PCClassLevel classLevel = aPC
									.getActiveClassLevel(aClass, lvl - 1);
							aPC.setAssoc(classLevel, AssociationKey.HIT_POINTS,
									Integer.valueOf(iSides));
							iHp = iSides;
						}
					}
				}

				rowIndex -= aPC.getLevelInfoSize();

				//
				// Done all levels from all classes, then show HP from Feats (Toughness)
				//
				if (rowIndex == 0)
				{
					switch (columnIndex)
					{
						case 0:
							return "Feats";

						case 2:
						case 4:

							Integer iBonus = Integer.valueOf((int) aPC.getTotalBonusTo("HP", "CURRENTMAX"));

							return iBonus;

						default:
							break;
					}

					return null;
				}
				else if (rowIndex == 1)
				{
					switch (columnIndex)
					{
						case 0:
							return "Total";

						case 4:

							int iRows = getRowCount() - 1;
							iHp = 0;

							for (int i = 0; i < iRows; ++i)
							{
								//
								// Just in case the list is really messed up, make sure we don't
								// wind up in an infinite loop...
								//
								if (i == askedRowIndex)
								{
									break;
								}

								iHp += ((Integer) getValueAt(i, 4)).intValue();
							}

							return Integer.valueOf(iHp);

						default:
							break;
					}

					return null;
				}

				switch (columnIndex)
				{
					case 0: // Name

						if (aClass == null)
						{
							return (aRace == null) ? Constants.NONESELECTED : aRace.getKeyName();
						}
						return aClass.getDisplayName();

					case 1: // Sides
						return Integer.valueOf(iSides);

					case 2: // Roll
						return Integer.valueOf(iHp);

					case 3: // Con
						int iConMod = 0;
						if (iSides > 0)
						{
							iConMod = (int) aPC.getStatBonusTo("HP", "BONUS");
						}
						return Integer.valueOf(iConMod);

					case 4: // Total
						if (iSides > 0)
						{
							iHp += (int) aPC.getStatBonusTo("HP", "BONUS");

							if (iHp < 1)
							{
								iHp = 1;
							}
						}

						return Integer.valueOf(iHp);

					case 5:
						return "+";

					case 6:
						return "-";

					case 7:
						return "roll";

					default:
						break;
				}
			}

			return null;
		}
	}

	class RendererEditor implements TableCellRenderer
	{
		DefaultTableCellRenderer def = new DefaultTableCellRenderer();
		JButton minusButton = new JButton("-");
		JButton plusButton = new JButton("+");

		/**
		 * Constructor
		 */
		public RendererEditor()
		{
			def.setBackground(MainHP.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
		{
			switch (column)
			{
				case 5:
					def.setText("+");

					return def;

				case 6:
					def.setText("-");

					return def;

				case 7:
					def.setText("roll");
					return def;

				default:
					break;
			}

			return null;
		}
	}
}
