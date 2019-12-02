/*
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
 */
package pcgen.gui2.tabs.summary;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import pcgen.core.Language;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.LanguageChooserFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.LanguageChooserDialog;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.LanguageBundle;

public class LanguageTableModel extends AbstractTableModel implements ListListener<Language>
{

	private ListFacade<Language> languages;
	private ListFacade<LanguageChooserFacade> choosers;
	private CharacterFacade character;
	private JTable table;
	private int dirtyRow = -1;
	private MouseListener mouseListener = new MouseListener();
	private Renderer renderer = new Renderer();
	private Editor editor = new Editor();

	public LanguageTableModel(CharacterFacade character, JTable table)
	{
		super();
		this.table = table;
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

	public void install()
	{
		table.addMouseListener(mouseListener);
		table.addMouseMotionListener(mouseListener);
		table.setModel(this);

		table.setDefaultRenderer(Object.class, renderer);
		table.setDefaultEditor(Object.class, editor);
	}

	public void uninstall()
	{
		table.removeMouseListener(mouseListener);
		table.removeMouseMotionListener(mouseListener);
		dirtyRow = -1;
	}

	@Override
	public int getRowCount()
	{
		return languages.getSize() + choosers.getSize();
	}

	@Override
	public String getColumnName(int column)
	{
		return LanguageBundle.getString("in_sumLanguages"); //$NON-NLS-1$
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
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
        return rowIndex >= languages.getSize() || character.isRemovable(languages.getElementAt(rowIndex));
    }

	@Override
	public void elementAdded(ListEvent<Language> e)
	{
		fireTableRowsInserted(e.getIndex(), e.getIndex());
		editor.cancelCellEditing();
	}

	@Override
	public void elementRemoved(ListEvent<Language> e)
	{
		fireTableRowsDeleted(e.getIndex(), e.getIndex());
		editor.cancelCellEditing();
	}

	@Override
	public void elementsChanged(ListEvent<Language> e)
	{
		fireTableDataChanged();
		editor.cancelCellEditing();
	}

	@Override
	public void elementModified(ListEvent<Language> e)
	{
		fireTableRowsUpdated(e.getIndex(), e.getIndex());
	}

	private class MouseListener extends MouseAdapter
	{

		@Override
		public void mouseExited(MouseEvent e)
		{
			table.repaint(table.getCellRect(dirtyRow, 0, true));
			dirtyRow = -1;
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			int row = table.rowAtPoint(e.getPoint());
			if (row != dirtyRow)
			{
				editor.cancelCellEditing();
				table.repaint(table.getCellRect(dirtyRow, 0, true));
				dirtyRow = row;
				table.repaint(table.getCellRect(dirtyRow, 0, true));
			}
		}

	}

	private class Editor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		private static final String ADD_ID = "Add";
		private static final String REMOVE_ID = "Remove";
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
		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row,
			int column)
		{
			TableCellUtilities.setToRowBackground(cellPanel, jTable, row);
			if (row >= languages.getSize())
			{
				addLabel.setForeground(jTable.getForeground());
				addLabel.setFont(jTable.getFont());
				addLabel.setText(((LanguageChooserFacade) value).getName());
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
				LanguageChooserFacade chooser = choosers.getElementAt(table.getEditingRow() - languages.getSize());
				LanguageChooserDialog dialog = new LanguageChooserDialog(frame, chooser);
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
			else
			{
				Language lang = (Language) getValueAt(table.getEditingRow(), 0);
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

		private static final String ADD_ID = "Add";
		private static final String REMOVE_ID = "Remove";
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
		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
		{
			TableCellUtilities.setToRowBackground(this, jTable, row);
			if (row < languages.getSize())
			{
				boolean automatic = value instanceof Language && character.isAutomatic((Language) value);
				boolean removable = value instanceof Language && character.isRemovable((Language) value);
				if (automatic)
				{
					cellLabel.setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getAutomaticColor()));
				}
				else
				{
					cellLabel.setForeground(jTable.getForeground());
				}
				cellLabel.setText(value.toString());
				cellLabel.setFont(jTable.getFont());
				removeButton.setEnabled(dirtyRow == row);
				removeButton.setVisible(removable);
				cardLayout.show(this, REMOVE_ID);
			}
			else
			{
				addLabel.setText(((LanguageChooserFacade) value).getName());
				addLabel.setFont(jTable.getFont());
				addLabel.setForeground(jTable.getForeground());
				cardLayout.show(this, ADD_ID);
			}
			return this;
		}

	}

}
