/*
 * EquipmentModels.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jan 25, 2011, 3:26:08 PM
 */
package pcgen.gui2.tabs.equip;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.Constants;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.EquipmentListFacade;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.EquipmentSetFacade.EquipNode;
import pcgen.core.facade.EquipmentSetFacade.EquipNode.NodeType;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.JTableEx;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.table.TableCellUtilities;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class EquipmentModels
{

	public enum EquipView
	{

		FULL,
		UNEQUIPPED,
		EQUIPPED;

		@Override
		public String toString()
		{
			switch (this)
			{
				case FULL:
					return "Full Listing";
				case UNEQUIPPED:
					return "Unequipped";
				case EQUIPPED:
					return "Equipped";
				default:
					throw new InternalError();
			}
		}

	}

	private final CharacterFacade character;
	private final EquipmentTableModel fullModel;
	private final EquipmentTableModel unequippedModel;
	private final EquipmentTableModel equippedModel;
	private final UnequippedList unequippedList;
	private final EquipViewHandler viewHandler;
	private final EquipAction equipAction;
	private final UnequipAction unequipAction;
	private final EquipFilterHandler filterHandler;
	private EquipView selectedView;
	private EquipmentTableModel selectedModel;
	private JComboBox equipViewBox;
	private JTableEx equipmentTable;
	private JTreeTable equipmentSetTable;

	public EquipmentModels(CharacterFacade character)
	{
		this.character = character;
		this.unequippedList = new UnequippedList(character);
		this.fullModel = new EquipmentTableModel(character);
		fullModel.setEquipmentList(character.getPurchasedEquipment());
		fullModel.setEquipmentSet(character.getEquipmentSetRef().getReference());
		this.unequippedModel = new EquipmentTableModel(character);
		unequippedModel.setEquipmentList(unequippedList);
		unequippedModel.setEquipmentSet(character.getEquipmentSetRef().getReference());
		this.equippedModel = new EquippedTableModel(character);

		selectedModel = fullModel;
		selectedView = EquipView.UNEQUIPPED;

		this.viewHandler = new EquipViewHandler();
		this.equipAction = new EquipAction();
		this.unequipAction = new UnequipAction();
		this.filterHandler = new EquipFilterHandler();
	}

	public void install(JComboBox equipBox, JTableEx eqTable,
		DisplayableFilter<? super CharacterFacade, ? super EquipmentFacade> filter,
		JTreeTable eqSetTable, JButton equipButton, JButton unequipButton)
	{
		this.equipViewBox = equipBox;
		this.equipmentTable = eqTable;
		this.equipmentSetTable = eqSetTable;
		viewHandler.install();
		equipButton.setAction(equipAction);
		unequipButton.setAction(unequipAction);
		equipAction.install();
		unequipAction.install();

		filter.setFilterHandler(filterHandler);
		fullModel.setFilter(filter);
		unequippedModel.setFilter(filter);
		equippedModel.setFilter(filter);
	}

	public void uninstall()
	{
		if (equipmentTable != null)
		{
			equipAction.uninstall();
			unequipAction.uninstall();
		}
	}
	
	private class EquipFilterHandler implements FilterHandler
	{

		@Override
		public void refilter()
		{
			selectedModel.refilter();
		}

		@Override
		public void setSearchEnabled(boolean enable)
		{
			//do nothing
		}

	}

	private class EquipViewHandler extends AbstractAction
	{

		public void install()
		{
			equipViewBox.setAction(this);
			equipViewBox.setSelectedItem(selectedView);
			equipmentTable.setModel(selectedModel);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			selectedView = (EquipView) equipViewBox.getSelectedItem();
			switch (selectedView)
			{
				case FULL:
					selectedModel = fullModel;
					equipAction.setEnabled(true);
					break;
				case UNEQUIPPED:
					selectedModel = unequippedModel;
					equipAction.setEnabled(true);
					break;
				case EQUIPPED:
					selectedModel = equippedModel;
					equipAction.setEnabled(false);
					break;
			}
			equipmentTable.setModel(selectedModel);
			filterHandler.refilter();
		}

	}

	private static class EquippedTableModel extends EquipmentTableModel
			implements ReferenceListener<EquipmentSetFacade>
	{

		public EquippedTableModel(CharacterFacade character)
		{
			super(character);
			ReferenceFacade<EquipmentSetFacade> ref = character.getEquipmentSetRef();
			ref.addReferenceListener(this);
			setEquipmentList(ref.getReference().getEquippedItems());
			setEquipmentSet(ref.getReference());
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
		{
			setEquipmentList(e.getNewReference().getEquippedItems());
			setEquipmentSet(e.getNewReference());
			//fireTableDataChanged();
		}

	}

	private class UnequipAction extends AbstractAction
	{

		public UnequipAction()
		{
			super("Unequip Selected");
			this.putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().getReference();
			int[] rows = equipmentSetTable.getSelectedRows();
			List<EquipNode> paths = new ArrayList<EquipNode>();
			for (int i = 0; i < rows.length; i++)
			{
				EquipNode path = (EquipNode) equipmentSetTable.getValueAt(rows[i], 0);
				if (path.getNodeType() == NodeType.EQUIPMENT)
				{
					paths.add(path);
				}
			}
			if (!paths.isEmpty())
			{
				Object[][] data = new Object[paths.size()][3];
				for (int i = 0; i < paths.size(); i++)
				{
					EquipNode path = paths.get(i);
					data[i][0] = path.getEquipment();
					data[i][1] = 1;
				}
				Object[] columns = new Object[]
				{
					"Item",
					"Qty",
				};
				DefaultTableModel tableModel = new DefaultTableModel(data, columns)
				{

					@Override
					public Class<?> getColumnClass(int columnIndex)
					{
						if (columnIndex == 1)
						{
							return Integer.class;
						}
						return Object.class;
					}

					@Override
					public boolean isCellEditable(int row, int column)
					{
						return column != 0;
					}

				};
				JTable table = new JTable(tableModel);
				table.setFocusable(false);
				table.setCellSelectionEnabled(false);
				table.setDefaultRenderer(Integer.class, new TableCellUtilities.SpinnerRenderer());
				table.setDefaultEditor(Integer.class, new SpinnerEditor(equipSet.getEquippedItems()));
				table.setRowHeight(22);
				table.getColumnModel().getColumn(0).setPreferredWidth(140);
				table.getColumnModel().getColumn(1).setPreferredWidth(50);
				table.setPreferredScrollableViewportSize(table.getPreferredSize());
				JTableHeader header = table.getTableHeader();
				header.setReorderingAllowed(false);
				JScrollPane pane = new JScrollPane(table);
				int res = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(equipmentTable),
					  pane, Constants.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (res == JOptionPane.OK_OPTION)
				{
					for (int i = 0; i < paths.size(); i++)
					{
						equipSet.removeEquipment(paths.get(i), (Integer) tableModel.getValueAt(i, 1));
					}
				}
			}
		}
		
		public void install()
		{
			equipmentSetTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			equipmentSetTable.removeActionListener(this);
		}

	}

	public class EquipAction extends AbstractAction
	{

		public EquipAction()
		{
			super("Equip Selected");
			this.putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int[] selectedRows = equipmentTable.getSelectedRows();
			MapToList<EquipmentFacade, EquipNode> equipMap = new HashMapToList<EquipmentFacade, EquipNode>();
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().getReference();
			List<EquipmentFacade> equipment = new ArrayList<EquipmentFacade>();

			for (int i = 0; i < selectedRows.length; i++)
			{
				EquipmentFacade equipmentFacade = selectedModel.getValue(selectedRows[i]);
				for (EquipNode path : equipSet.getNodes())
				{
					if (equipSet.canEquip(path, equipmentFacade))
					{
						equipMap.addToListFor(equipmentFacade, path);
					}
				}
				if (equipMap.containsListFor(equipmentFacade))
				{
					equipment.add(equipmentFacade);
				}
			}
			if (!equipment.isEmpty())
			{
				Object[][] data = new Object[equipment.size()][3];
				for (int i = 0; i < equipment.size(); i++)
				{
					EquipmentFacade equipmentFacade = equipment.get(i);
					data[i][0] = equipmentFacade;
					data[i][1] = 1;
					data[i][2] = equipMap.getElementInList(equipmentFacade, 0);
					String preferredNodeName = equipSet.getPreferredLoc(equipmentFacade);
					for (EquipNode node : equipMap.getListFor(equipmentFacade))
					{
						if (preferredNodeName.equals(node.toString()))
						{
							data[i][2] = node;
							break;
						}
					} 
				}
				Object[] columns = new Object[]
				{
					"Item",
					"Qty",
					"Container"
				};
				DefaultTableModel tableModel = new DefaultTableModel(data, columns)
				{

					@Override
					public Class<?> getColumnClass(int columnIndex)
					{
						if (columnIndex == 1)
						{
							return Integer.class;
						}
						return Object.class;
					}

					@Override
					public boolean isCellEditable(int row, int column)
					{
						return column != 0;
					}

				};
				JTable table = new JTable(tableModel);
				table.setFocusable(false);
				table.setCellSelectionEnabled(false);
				table.setDefaultEditor(Object.class, new ComboEditor(equipMap));
				table.setDefaultRenderer(Integer.class, new TableCellUtilities.SpinnerRenderer());
				table.setDefaultEditor(Integer.class, new SpinnerEditor(unequippedList));
				table.setRowHeight(22);
				table.getColumnModel().getColumn(0).setPreferredWidth(140);
				table.getColumnModel().getColumn(1).setPreferredWidth(50);
				table.getColumnModel().getColumn(2).setPreferredWidth(120);
				table.setPreferredScrollableViewportSize(table.getPreferredSize());
				JTableHeader header = table.getTableHeader();
				header.setReorderingAllowed(false);
				JScrollPane pane = new JScrollPane(table);
				JPanel panel = new JPanel(new BorderLayout());
				JLabel help = new JLabel("Select the quantity and location for each item.");
				panel.add(help, BorderLayout.NORTH);
				panel.add(pane, BorderLayout.CENTER);
				int res = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(equipmentTable),
					panel, Constants.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (res == JOptionPane.OK_OPTION)
				{
					for (int i = 0; i < equipment.size(); i++)
					{
						EquipNode path = (EquipNode) tableModel.getValueAt(i, 2);
						equipSet.addEquipment(path, equipment.get(i), (Integer) tableModel.getValueAt(i, 1));
					}
				}
			}
		}
		
		public void install()
		{
			equipmentTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			equipmentTable.removeActionListener(this);
		}

	}

	private class SpinnerEditor extends AbstractCellEditor implements TableCellEditor, ChangeListener
	{

		private JSpinner spinner = new JSpinner();
		private final EquipmentListFacade equipmentList;

		public SpinnerEditor(EquipmentListFacade equipmentList)
		{
			this.equipmentList = equipmentList;
			spinner.addChangeListener(this);
		}

		@Override
		public Object getCellEditorValue()
		{
			return spinner.getValue();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			EquipmentFacade equipment = (EquipmentFacade) table.getValueAt(row, 0);
			int maxQuantity = equipmentList.getQuantity(equipment);
			SpinnerNumberModel model = new SpinnerNumberModel(((Integer) value).intValue(), 1, maxQuantity, 1);
			spinner.setModel(model);
			return spinner;
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			stopCellEditing();
		}

	}

	private static class ComboEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		private JComboBox comboBox = null;
		private MapToList<EquipmentFacade, EquipNode> equipMap;

		public ComboEditor(MapToList<EquipmentFacade, EquipNode> equipMap)
		{
			this.equipMap = equipMap;
		}

		@Override
		public Object getCellEditorValue()
		{
			return comboBox.getSelectedItem();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			EquipmentFacade equipment = (EquipmentFacade) table.getValueAt(row, 0);
			if (comboBox != null)
			{
				comboBox.removeActionListener(this);
			}
			comboBox = new JComboBox(equipMap.getListFor(equipment).toArray());
			comboBox.setSelectedItem(value);
			comboBox.addActionListener(this);
			return comboBox;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			stopCellEditing();
		}

	}

}
