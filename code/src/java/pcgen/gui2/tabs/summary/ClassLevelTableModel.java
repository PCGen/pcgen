/*
 * ClassLevelTableModel.java
 * Copyright 2010 (C) Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on May 11, 2010, 2:01:06 PM
 */
package pcgen.gui2.tabs.summary;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.core.facade.CharacterLevelsFacade.ClassListener;
import pcgen.core.facade.CharacterLevelsFacade.HitPointListener;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.table.TableCellUtilities;

public class ClassLevelTableModel extends AbstractTableModel
		implements ListListener, ItemListener, PropertyChangeListener, HitPointListener, ClassListener
{

	private CharacterLevelsFacade levels;
	private Map<ClassFacade, Integer> finalLevelMap;
	private CharacterFacade character;
	private Editor editor = new Editor();
	private Editor renderer = new Editor();
	private JComboBox classComboBox;

	public ClassLevelTableModel(CharacterFacade character)
	{
		super();
		this.character = character;
		this.levels = character.getCharacterLevelsFacade();
		this.finalLevelMap = new HashMap<ClassFacade, Integer>();
		resetLevelMap();
		levels.addListListener(this);
		levels.addClassListener(this);
		levels.addHitPointListener(this);
	}

	public static void initializeTable(JTable classLevelTable)
	{
		JTableHeader tableHeader = classLevelTable.getTableHeader();
		tableHeader.setResizingAllowed(false);
		tableHeader.setReorderingAllowed(false);
		TableColumnModel columnModel = new DefaultTableColumnModel();
		TableCellRenderer headerRenderer = tableHeader.getDefaultRenderer();
		columnModel.addColumn(Utilities.createTableColumn(0, "Level", headerRenderer, false));
		columnModel.addColumn(Utilities.createTableColumn(1, "HP", headerRenderer, false));
		columnModel.addColumn(Utilities.createTableColumn(2, "Class (All Levels In Class)", headerRenderer, true));
		classLevelTable.setColumnModel(columnModel);
		classLevelTable.setAutoCreateColumnsFromModel(false);
		classLevelTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		classLevelTable.setFocusable(false);
		classLevelTable.setCellSelectionEnabled(false);
		classLevelTable.setRowHeight(20);
	}

	public void install(JTable table, JComboBox classComboBox)
	{
		this.classComboBox = classComboBox;
		table.setModel(this);
		table.setDefaultRenderer(Object.class, renderer);
		table.setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		table.setDefaultEditor(Object.class, editor);
		classComboBox.addItemListener(this);
		classComboBox.addPropertyChangeListener("model", this);
	}

	public void uninstall()
	{
		classComboBox.removeItemListener(this);
		classComboBox.removePropertyChangeListener("model", this);
	}

	private void resetLevelMap()
	{
		finalLevelMap.clear();
		for (int i = levels.getSize() - 1; i >= 0; i--)
		{
			ClassFacade c = levels.getClassTaken(levels.getElementAt(i));
			if (!finalLevelMap.containsKey(c))
			{
				finalLevelMap.put(c, i);
			}
		}
	}

	public int getRowCount()
	{
		return levels.getSize() + 1;
	}

	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return rowIndex >= levels.getSize() - 1 && columnIndex == 2;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
			case 1:
				return Integer.class;
			default:
				return Object.class;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex == levels.getSize())
		{
			return null;
		}
		switch (columnIndex)
		{
			case 0:
				return rowIndex + 1;
			case 1:
				return levels.getHPGained(levels.getElementAt(rowIndex));
			case 2:
				ClassFacade c = levels.getClassTaken(levels.getElementAt(rowIndex));
				if (finalLevelMap.get(c) == rowIndex)
				{
					return c.toString() + " (" + character.getClassLevel(c) + ")";
				}
				return c.toString();
			default:
				return null;
		}
	}

	public void elementAdded(ListEvent e)
	{
		editor.cancelCellEditing();
		int i = e.getIndex();
		ClassFacade c = levels.getClassTaken(levels.getElementAt(i));
		finalLevelMap.put(c, i);
		fireTableRowsInserted(i, i);
	}

	public void elementRemoved(ListEvent e)
	{
		editor.cancelCellEditing();
		resetLevelMap();
		fireTableRowsDeleted(e.getIndex(), e.getIndex());
	}

	public void elementsChanged(ListEvent e)
	{
		editor.cancelCellEditing();
		resetLevelMap();
		fireTableDataChanged();
	}

	public void itemStateChanged(ItemEvent e)
	{
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			fireTableRowsUpdated(levels.getSize(), levels.getSize());
		}
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		fireTableRowsUpdated(levels.getSize(), levels.getSize());
	}

	public void classChanged(CharacterLevelEvent e)
	{
		levelChanged(e);
	}

	public void hitPointsChanged(CharacterLevelEvent e)
	{
		levelChanged(e);
	}

	private void levelChanged(CharacterLevelEvent e)
	{
		int firstRow = e.getBaseLevelIndex();
		int lastRow = e.affectsHigherLevels() ? levels.getSize() - 1 : firstRow;
		fireTableRowsUpdated(firstRow, lastRow);
	}

	private class Editor extends AbstractCellEditor
			implements TableCellEditor, TableCellRenderer, ActionListener
	{

		private JPanel cellPanel = new JPanel();
		private JLabel cellLabel = new JLabel();
		private JButton addLevelButton = Utilities.createSignButton(Sign.Plus);
		private JButton removeLevelButton = Utilities.createSignButton(Sign.Minus);

		public Editor()
		{
			super();
			cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.X_AXIS));
			cellPanel.setOpaque(true);
			addLevelButton.setFocusable(false);
			removeLevelButton.setFocusable(false);
			addLevelButton.addActionListener(this);
			removeLevelButton.addActionListener(this);
		}

		public Object getCellEditorValue()
		{
			return null;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			return getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			cellPanel.removeAll();
			TableCellUtilities.setToRowBackground(cellPanel, table, row);
			cellLabel.setForeground(table.getForeground());
			cellLabel.setFont(table.getFont());
			if (row == levels.getSize() - 1)
			{
				cellLabel.setText(value.toString());
				cellPanel.add(cellLabel);
				cellPanel.add(Box.createHorizontalGlue());
				cellPanel.add(removeLevelButton);
			}
			else if (row == levels.getSize())
			{
				cellLabel.setText("Add Level");
				cellPanel.add(Box.createHorizontalGlue());
				cellPanel.add(cellLabel);
				cellPanel.add(Box.createHorizontalStrut(3));
				addLevelButton.setEnabled(classComboBox.getSelectedItem() != null);
				cellPanel.add(addLevelButton);
			}
			else
			{
				cellLabel.setText(value.toString());
				cellPanel.add(cellLabel);
			}
			return cellPanel;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == addLevelButton)
			{
				ClassFacade c = (ClassFacade) classComboBox.getSelectedItem();
				if (c != null)
				{
					character.addCharacterLevels(new ClassFacade[]
							{
								c
							});
				}
			}
			else
			{
				character.removeCharacterLevels(1);
			}
			cancelCellEditing();
		}

	}

}
