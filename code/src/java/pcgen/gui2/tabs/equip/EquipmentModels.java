/*
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
 */
package pcgen.gui2.tabs.equip;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.facade.EquipNode;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.JTableEx;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.system.LanguageBundle;

/**
 * The container for equipping data for a character. It holds references to the 
 * models for both the left and right tables of gear. It also contains the 
 * processing to manage equipping and unequipping actions.
 *
 *  
 */
public class EquipmentModels
{

	public enum EquipView
	{
		FULL, UNEQUIPPED, EQUIPPED;

		@Override
		public String toString()
		{
			return switch (this)
					{
						case FULL -> LanguageBundle.getString("in_equipListFull"); //$NON-NLS-1$
						case UNEQUIPPED -> LanguageBundle.getString("in_equipListUnequipped"); //$NON-NLS-1$
						case EQUIPPED -> LanguageBundle.getString("in_equipListEquipped"); //$NON-NLS-1$
						default -> throw new InternalError();
					};
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
	private final MoveUpAction moveUpAction;
	private final MoveDownAction moveDownAction;
	private final EquipFilterHandler filterHandler;
	private EquipView selectedView;
	private EquipmentTableModel selectedModel;
	private final JComboBox equipViewBox;
	private final JTableEx equipmentTable;
	private final JTreeTable equipmentSetTable;
	private final JButton equipButton;
	private final JButton unequipButton;
	private final JButton moveUpButton;
	private final JButton moveDownButton;
	private final DisplayableFilter<? super CharacterFacade, ? super EquipmentFacade> filter;

	public EquipmentModels(CharacterFacade character, JComboBox equipBox, JTableEx eqTable,
		DisplayableFilter<? super CharacterFacade, ? super EquipmentFacade> filter, JTreeTable eqSetTable,
		JButton equipButton, JButton unequipButton, JButton moveUpButton, JButton moveDownButton)
	{
		this.character = character;
		this.unequippedList = new UnequippedList(character);
		this.fullModel = new EquippedTableRootModel(character);
		fullModel.setEquipmentList(character.getPurchasedEquipment());
		this.unequippedModel = new EquippedTableRootModel(character);
		unequippedModel.setEquipmentList(unequippedList);
		this.equippedModel = new EquippedTableModel(character);

		selectedModel = fullModel;
		selectedView = EquipView.UNEQUIPPED;

		this.viewHandler = new EquipViewHandler();
		this.equipAction = new EquipAction();
		this.unequipAction = new UnequipAction();
		this.moveUpAction = new MoveUpAction();
		this.moveDownAction = new MoveDownAction();
		this.filterHandler = new EquipFilterHandler();

		this.equipViewBox = equipBox;
		this.equipmentTable = eqTable;
		this.equipmentSetTable = eqSetTable;
		this.filter = filter;
		this.equipButton = equipButton;
		this.unequipButton = unequipButton;
		this.moveUpButton = moveUpButton;
		this.moveDownButton = moveDownButton;
	}

	public void install()
	{
		viewHandler.install();
		equipButton.setAction(equipAction);
		unequipButton.setAction(unequipAction);
		moveUpButton.setAction(moveUpAction);
		moveDownButton.setAction(moveDownAction);
		moveDownAction.install();
		moveUpAction.install();
		equipAction.install();
		unequipAction.install();

		filter.setFilterHandler(filterHandler);
		fullModel.setFilter(filter);
		unequippedModel.setFilter(filter);
		equippedModel.setFilter(filter);
	}

	public void uninstall()
	{
		equipAction.uninstall();
		unequipAction.uninstall();
	}

	private List<EquipNode> getSelectedEquipmentSetNodes()
	{
		int[] rows = equipmentSetTable.getSelectedRows();
		List<EquipNode> paths = new ArrayList<>();
		for (int row : rows)
		{
			EquipNode path = (EquipNode) equipmentSetTable.getValueAt(row, 0);
			if (path.getNodeType() == EquipNode.NodeType.EQUIPMENT)
			{
				paths.add(path);
			}
		}
		return paths;
	}

	private void selectNodeInEquipmentSetTable(EquipNode nodeToSelect)
	{
		TableModel model = equipmentSetTable.getModel();
		for (int i = 0; i < model.getRowCount(); i++)
		{
			if (nodeToSelect == model.getValueAt(i, 0))
			{
				equipmentSetTable.getSelectionModel().setSelectionInterval(i, i);
				break;
			}
		}
	}

	private static JScrollPane prepareScrollPane(JTable table)
	{
		JScrollPane pane = new JScrollPane(table);
		Dimension size = table.getPreferredSize();
		size.height += 30; // account for the header which has not been prepared yet
		final int decorationHeight = 80;
		final int decorationWidth = 70;
		Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		if (size.height > screenBounds.height - decorationHeight)
		{
			size.height = screenBounds.height - decorationHeight;
		}
		if (size.width > screenBounds.width - decorationWidth)
		{
			size.width = screenBounds.width - decorationWidth;
		}
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setPreferredSize(size);
		return pane;
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

		@Override
		public void scrollToTop()
		{
			// do nothing
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
				case FULL -> {
					selectedModel = fullModel;
					equipAction.setEnabled(true);
				}
				case UNEQUIPPED -> {
					selectedModel = unequippedModel;
					equipAction.setEnabled(true);
				}
				case EQUIPPED -> {
					selectedModel = equippedModel;
					equipAction.setEnabled(false);
				}
				default -> {
				}
				//Case not caught, should this cause an error?
			}
			equipmentTable.setModel(selectedModel);
			filterHandler.refilter();
		}

	}

	private static class EquippedTableModel extends EquipmentTableModel implements ReferenceListener<EquipmentSetFacade>
	{

		public EquippedTableModel(CharacterFacade character)
		{
			super(character);
			ReferenceFacade<EquipmentSetFacade> ref = character.getEquipmentSetRef();
			ref.addReferenceListener(this);
			setEquipmentList(ref.get().getEquippedItems());
			setEquipmentSet(ref.get());
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
		{
			setEquipmentList(e.getNewReference().getEquippedItems());
			setEquipmentSet(e.getNewReference());
		}

	}

	private static class EquippedTableRootModel extends EquipmentTableModel implements ReferenceListener<EquipmentSetFacade>
	{

		public EquippedTableRootModel(CharacterFacade character)
		{
			super(character);
			ReferenceFacade<EquipmentSetFacade> ref = character.getEquipmentSetRef();
			ref.addReferenceListener(this);
			setEquipmentSet(ref.get());
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
		{
			EquipmentSetFacade es = e.getNewReference();
			if (es.isRoot())
			{
				setEquipmentSet(e.getNewReference());
			}
		}

	}

	private class UnequipAction extends AbstractAction
	{

		public UnequipAction()
		{
			super(LanguageBundle.getString("in_equipUnequipSel")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
			List<EquipNode> paths = getSelectedEquipmentSetNodes();
			if (!paths.isEmpty())
			{
				Object[][] data = new Object[paths.size()][3];
				for (int i = 0; i < paths.size(); i++)
				{
					EquipNode path = paths.get(i);
					data[i][0] = path.getEquipment();
					data[i][1] = equipSet.getQuantity(path);
				}
				Object[] columns = {LanguageBundle.getString("in_equipItem"), //$NON-NLS-1$
					LanguageBundle.getString("in_equipQuantityAbbrev"), //$NON-NLS-1$
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
				JScrollPane pane = EquipmentModels.prepareScrollPane(table);
				JPanel panel = new JPanel(new BorderLayout());
				JLabel help = new JLabel(LanguageBundle.getString("in_equipSelectUnequipQty")); //$NON-NLS-1$
				panel.add(help, BorderLayout.NORTH);
				panel.add(pane, BorderLayout.CENTER);
				int res = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(equipmentTable), panel,
					LanguageBundle.getString("in_equipUnequipSel"), //$NON-NLS-1$
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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
			super(LanguageBundle.getString("in_equipEquipSel")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int[] selectedRows = equipmentTable.getSelectedRows();
			MapToList<EquipmentFacade, EquipNode> equipMap = new HashMapToList<>();
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
			List<EquipmentFacade> equipment = new ArrayList<>();

			for (int selectedRow : selectedRows)
			{
				EquipmentFacade equipmentFacade = selectedModel.getValue(selectedRow);
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
					data[i][1] = unequippedList.getQuantity(equipmentFacade);
					data[i][2] = getInitialNode(equipMap, equipSet, equipmentFacade);
				}
				Object[] columns = {LanguageBundle.getString("in_equipItem"), //$NON-NLS-1$
					LanguageBundle.getString("in_equipQuantityAbbrev"), //$NON-NLS-1$
					LanguageBundle.getString("in_equipContainer") //$NON-NLS-1$
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
				JScrollPane pane = EquipmentModels.prepareScrollPane(table);
				JPanel panel = new JPanel(new BorderLayout());
				JLabel help = new JLabel(LanguageBundle.getString("in_equipSelectQtyLoc")); //$NON-NLS-1$
				panel.add(help, BorderLayout.NORTH);
				panel.add(pane, BorderLayout.CENTER);
				int res = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(equipmentTable), panel,
					LanguageBundle.getString("in_equipEquipSel"), //$NON-NLS-1$
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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

		private EquipNode getInitialNode(MapToList<EquipmentFacade, EquipNode> equipMap, EquipmentSetFacade equipSet,
			EquipmentFacade equipmentFacade)
		{
			// First see if the user has selected a suitable node in the equipped tree
			List<EquipNode> possibleNodeList = equipMap.getListFor(equipmentFacade);
			int[] rows = equipmentSetTable.getSelectedRows();
			for (int row : rows)
			{
				EquipNode path = (EquipNode) equipmentSetTable.getValueAt(row, 0);
				if (possibleNodeList.contains(path))
				{
					return path;
				}
			}

			// Check if the preferred location can be found in the list
			String preferredNodeName = equipSet.getPreferredLoc(equipmentFacade);
			for (EquipNode node : possibleNodeList)
			{
				if (preferredNodeName.equals(node.toString()))
				{
					return node;
				}
			}

			// Fall back to the first item in the list
			return equipMap.getElementInList(equipmentFacade, 0);
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

	private static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor, ChangeListener
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
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column)
		{
			EquipmentFacade equipment = (EquipmentFacade) table.getValueAt(row, 0);
			int maxQuantity = equipmentList.getQuantity(equipment);
			int minQuantity = 1;
			if (maxQuantity <= 0)
			{
				minQuantity = maxQuantity = 0;
			}
			SpinnerNumberModel model =
					new SpinnerNumberModel(((Integer) value).intValue(), minQuantity, maxQuantity, 1);
			spinner.setModel(model);
			return spinner;
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			stopCellEditing();
		}

		@Override
		public boolean stopCellEditing()
		{
			try
			{
				spinner.commitEdit();
			}
			catch (ParseException ex)
			{
				// Fall through and cancel the edit.
			}
			return super.stopCellEditing();
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
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column)
		{
			EquipmentFacade equipment = (EquipmentFacade) table.getValueAt(row, 0);
			if (comboBox != null)
			{
				comboBox.removeActionListener(this);
			}
			comboBox = new JComboBox<>(equipMap.getListFor(equipment).toArray());
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

	private class MoveUpAction extends AbstractAction
	{

		public MoveUpAction()
		{
			super(LanguageBundle.getString("in_equipMoveUpMenuCommand")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, Icons.Up16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
			List<EquipNode> paths = getSelectedEquipmentSetNodes();
			if (!paths.isEmpty())
			{
				for (EquipNode node : paths)
				{
					equipSet.moveEquipment(node, -1);
				}
				selectNodeInEquipmentSetTable(paths.get(0));
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

	private class MoveDownAction extends AbstractAction
	{

		public MoveDownAction()
		{
			super(LanguageBundle.getString("in_equipMoveDownMenuCommand")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, Icons.Down16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
			List<EquipNode> paths = getSelectedEquipmentSetNodes();
			if (!paths.isEmpty())
			{
				for (EquipNode node : paths)
				{
					equipSet.moveEquipment(node, 1);
				}
				selectNodeInEquipmentSetTable(paths.get(0));
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

}
