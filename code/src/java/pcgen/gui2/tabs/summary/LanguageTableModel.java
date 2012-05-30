/*
 * LanguageTableModel.java
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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.LanguageChooserFacade;
import pcgen.core.facade.LanguageFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.LanguageChooserDialog;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.table.TableCellUtilities;

public class LanguageTableModel extends AbstractTableModel
		implements MouseMotionListener, ListListener<LanguageFacade>
{

	private ListFacade<LanguageFacade> languages;
	private ListFacade<LanguageChooserFacade> choosers;
	private CharacterFacade character;
	private JTable table;
	private int dirtyRow = -1;
	private Renderer renderer = new Renderer();
	private Editor editor = new Editor();

	public LanguageTableModel(CharacterFacade character)
	{
		super();
		this.character = character;
		languages = character.getLanguages();
		choosers = character.getLanguageChoosers();
		languages.addListListener(this);
	}

	public static void initializeTable(JTable table)
	{
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setFocusable(false);
		table.setRowHeight(21);
		table.getTableHeader().setReorderingAllowed(false);
	}

	public void install(JTable jTable)
	{
		this.table = jTable;
		jTable.addMouseMotionListener(this);
		jTable.setModel(this);

		jTable.setDefaultRenderer(Object.class, renderer);
		jTable.setDefaultEditor(Object.class, editor);
	}

	public void uninstall()
	{
		if (table != null)
		{
			table.removeMouseMotionListener(this);
		}
	}

	@Override
	public int getRowCount()
	{
		return languages.getSize() + choosers.getSize();
	}

	@Override
	public String getColumnName(int column)
	{
		return "Languages";
	}

	@Override
	public int getColumnCount()
	{
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < languages.getSize())
		{
			return languages.getElementAt(rowIndex);
		}
		else
		{
			return choosers.getElementAt(rowIndex - languages.getSize());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		//Do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		int row = table.rowAtPoint(e.getPoint());
		if (row == dirtyRow)
		{
			return;
		}
		editor.cancelCellEditing();
		table.repaint(table.getCellRect(dirtyRow, 0, true));
		table.repaint(table.getCellRect(row, 0, true));
		dirtyRow = row;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if (rowIndex < languages.getSize()
				&& character.isAutomatic(languages.getElementAt(rowIndex)))
		{
			return false;
		}
		return true;
	}

	@Override
	public void elementAdded(ListEvent<LanguageFacade> e)
	{
		fireTableRowsInserted(e.getIndex(), e.getIndex());
		editor.cancelCellEditing();
	}

	@Override
	public void elementRemoved(ListEvent<LanguageFacade> e)
	{
		fireTableRowsDeleted(e.getIndex(), e.getIndex());
		editor.cancelCellEditing();
	}

	@Override
	public void elementsChanged(ListEvent<LanguageFacade> e)
	{
		fireTableDataChanged();
		editor.cancelCellEditing();
	}

	@Override
	public void elementModified(ListEvent<LanguageFacade> e)
	{
		fireTableRowsUpdated(e.getIndex(), e.getIndex());
	}

	private class Editor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		private final String ADD_ID = "Add";
		private final String REMOVE_ID = "Remove";
		private JPanel cellPanel = new JPanel();
		private CardLayout cardLayout = new CardLayout();
		private JLabel addLabel = new JLabel();
		private JLabel cellLabel = new JLabel();

		public Editor()
		{
			cellPanel.setLayout(cardLayout);
			cellPanel.setOpaque(true);

			JButton addButton = Utilities.createSignButton(Sign.Plus);
			JButton removeButton = Utilities.createSignButton(Sign.Minus);
			addButton.setActionCommand(ADD_ID);
			removeButton.setActionCommand(REMOVE_ID);
			addButton.setFocusable(false);
			removeButton.setFocusable(false);
			addButton.addActionListener(this);
			removeButton.addActionListener(this);
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(addLabel);
			box.add(Box.createHorizontalStrut(3));
			box.add(addButton);
			box.add(Box.createHorizontalStrut(2));
			cellPanel.add(box, ADD_ID);

			box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(3));
			box.add(cellLabel);
			box.add(Box.createHorizontalGlue());
			box.add(removeButton);
			box.add(Box.createHorizontalStrut(2));
			cellPanel.add(box, REMOVE_ID);
		}

		@Override
		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			TableCellUtilities.setToRowBackground(cellPanel, jTable, row);
			if (row >= languages.getSize())
			{
				addLabel.setForeground(jTable.getForeground());
				addLabel.setFont(jTable.getFont());
				addLabel.setText("Add " + ((LanguageChooserFacade) value).getName());
				cardLayout.show(cellPanel, ADD_ID);
			}
			else
			{
				cellLabel.setForeground(jTable.getForeground());
				cellLabel.setFont(jTable.getFont());
				cellLabel.setText(value.toString());
				cardLayout.show(cellPanel, REMOVE_ID);
			}
			return cellPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (ADD_ID.equals(e.getActionCommand()))
			{
				Frame frame = JOptionPane.getFrameForComponent(table);
				LanguageChooserFacade chooser = choosers.getElementAt(
						table.getEditingRow() - languages.getSize());
				LanguageChooserDialog dialog = new LanguageChooserDialog(frame, chooser);
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
			else
			{
				LanguageFacade lang = (LanguageFacade) getValueAt(table.getEditingRow(), 0);
				character.removeLanguage(lang);
			}
			cancelCellEditing();
		}

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

	}

	private class Renderer extends JPanel implements TableCellRenderer
	{

		private final String ADD_ID = "Add";
		private final String REMOVE_ID = "Remove";
		private CardLayout cardLayout = new CardLayout();
		//private JPanel cellPanel = new JPanel();
		private JLabel cellLabel = new JLabel();
		private JButton addButton = Utilities.createSignButton(Sign.Plus);
		private JButton removeButton = Utilities.createSignButton(Sign.Minus);
		private JLabel addLabel = new JLabel();

		public Renderer()
		{
			setLayout(cardLayout);
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(3));
			box.add(cellLabel);
			box.add(Box.createHorizontalGlue());
			box.add(removeButton);
			box.add(Box.createHorizontalStrut(2));
			add(box, REMOVE_ID);

			box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(addLabel);
			box.add(Box.createHorizontalStrut(3));
			box.add(addButton);
			box.add(Box.createHorizontalStrut(2));
			add(box, ADD_ID);
		}

		@Override
		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			TableCellUtilities.setToRowBackground(this, jTable, row);
			if (row < languages.getSize())
			{
				boolean automatic = value instanceof LanguageFacade
						&& character.isAutomatic((LanguageFacade) value);
				Point mouse = jTable.getMousePosition();

				if (automatic)
				{
					cellLabel.setForeground(UIPropertyContext.getAutomaticColor());
				}
				else
				{
					cellLabel.setForeground(jTable.getForeground());
				}
				cellLabel.setText(value.toString());
				cellLabel.setFont(jTable.getFont());
				removeButton.setVisible(mouse != null && jTable.rowAtPoint(mouse) == row
						&& !automatic);
				cardLayout.show(this, REMOVE_ID);
			}
			else
			{
				addLabel.setText("Add " + ((LanguageChooserFacade) value).getName());
				addLabel.setFont(jTable.getFont());
				addLabel.setForeground(jTable.getForeground());
				cardLayout.show(this, ADD_ID);
			}
			return this;
		}

	}

}
