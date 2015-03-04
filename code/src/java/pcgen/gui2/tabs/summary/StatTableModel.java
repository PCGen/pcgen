/*
 * StatTableModel.java
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.PrettyIntegerFormat;
import pcgen.gui2.util.table.TableCellUtilities;

/**
 * Model used for the Ability/statistics table.
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StatTableModel extends AbstractTableModel implements ReferenceListener<Integer>
{
	private static final String EDITABLE = "EDITABLE"; //$NON-NLS-1$
	private static final String MOVEDOWN = "movedown"; //$NON-NLS-1$
	
	private static final int ABILITY_NAME = 0;
	private static final int EDITABLE_SCORE = 3;
	private static final int RACE_ADJ = 4;
	private static final int MISC_ADJ = 5;
	private static final int FINAL_ABILITY_SCORE = 1;
	private static final int ABILITY_MOD = 2;
	private CharacterFacade character;
	private ListFacade<StatFacade> stats;
	private StatRenderer renderer = new StatRenderer();
	private SpinnerEditor editor = new SpinnerEditor();
	private final JTable table;

	public StatTableModel(CharacterFacade character, JTable jtable)
	{
		this.character = character;
		this.table = jtable;
		this.stats = character.getDataSet().getStats();
		int min = Integer.MAX_VALUE;
		for (StatFacade sf : stats)
		{
			min = Math.min(sf.getMinValue(), min);
		}
		editor.setMinValue(min);

		final JTextField field = editor.getTextField();
		InputMap map = field.getInputMap(JComponent.WHEN_FOCUSED);

		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), MOVEDOWN);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), MOVEDOWN);
		Action action = new AbstractAction()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				//Logging.log(Logging.WARNING, "Got handleEnter from " + e.getSource());
				int row = table.getEditingRow();
				final int col = table.getEditingColumn();
				table.getCellEditor().stopCellEditing(); // store user input
				final int nextRow = row + 1;
				startEditingNextRow(table, col, nextRow, field);
			}

		};
		field.getActionMap().put(MOVEDOWN, action);
	}

	private void startEditingNextRow(final JTable statsTable,
									 final int col, final int nextRow, JTextField textField)
	{
		if (nextRow >= 0 && nextRow < statsTable.getModel().getRowCount() && col >= 0 && col <
				statsTable.getModel().getColumnCount())
		{
			statsTable.editCellAt(nextRow, col);
			textField.requestFocusInWindow();
		}
	}

	@Override
	public int getRowCount()
	{
		return stats.getSize();
	}

	public static void initializeTable(JTable statsTable)
	{
		JTableHeader tableHeader = statsTable.getTableHeader();
		tableHeader.setResizingAllowed(false);
		tableHeader.setReorderingAllowed(false);

		statsTable.setAutoCreateColumnsFromModel(false);
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		{
			String htmlText = "      Ability      ";
			columnModel.addColumn(Utilities.createTableColumn(ABILITY_NAME, htmlText, new FixedHeaderCellRenderer(htmlText), false));

			htmlText = "<html><div align=\"center\">Final<br>Score</div></html>";
			TableColumn column = Utilities.createTableColumn(FINAL_ABILITY_SCORE, htmlText, new FixedHeaderCellRenderer(htmlText), false);
			TableCellRenderer renderer = new ModRenderer();
			column.setCellRenderer(new ValueRenderer());
			columnModel.addColumn(column);

			htmlText = "<html><div align=\"center\">Ability<br>Mod</div></html>";
			column =
					Utilities.createTableColumn(ABILITY_MOD, htmlText,
						new FixedHeaderCellRenderer(htmlText), false);
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);

			htmlText = "<html><div align=\"center\">Editable<br>Score</div></html>";
			column = Utilities.createTableColumn(EDITABLE_SCORE, htmlText, new FixedHeaderCellRenderer(htmlText), false);
			column.setIdentifier(EDITABLE);
			columnModel.addColumn(column);

			htmlText = "<html><div align=\"center\">Race<br>Adj</div></html>";
			column = Utilities.createTableColumn(RACE_ADJ, htmlText, new FixedHeaderCellRenderer(htmlText), false);
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);

			htmlText = "<html><div align=\"center\">Misc<br>Adj</div></html>";
			column = Utilities.createTableColumn(MISC_ADJ, htmlText, new FixedHeaderCellRenderer(htmlText), false);
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
		}
		statsTable.setColumnModel(columnModel);
		statsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		statsTable.setShowVerticalLines(false);
		statsTable.setCellSelectionEnabled(false);
		statsTable.setFocusable(false);
		// XXX this should be calculated relative to font size and the size of a jspinner
		statsTable.setRowHeight(27);
		statsTable.setOpaque(false);
		tableHeader.setFont(FontManipulation.title(statsTable.getFont()));
		FontManipulation.large(statsTable);
	}

	/*
	 * This class is a hack that gives the TableHeaderUI a dummy component
	 * so that it can be used when calculating the height of the JTableHeader.
	 */
	private static class FixedHeaderCellRenderer extends JLabel implements TableCellRenderer
	{

		public FixedHeaderCellRenderer(String text)
		{
			setText(text);
			FontManipulation.title(this);
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (table == null)
			{
				return this;
			}
			return table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

	}

	private static class ModRenderer extends JLabel implements TableCellRenderer
	{

		private DecimalFormat formatter = PrettyIntegerFormat.getFormat();

		public ModRenderer()
		{
			setHorizontalAlignment(RIGHT);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Font tableFont = table.getFont();
			if (column < 3)
			{
				setFont(tableFont.deriveFont(Font.BOLD));
			}
			else
			{
				setFont(tableFont.deriveFont(Font.PLAIN));
			}
			setBackground(table.getBackground());
			setForeground(table.getForeground());
			Integer mod = (Integer) value;
			if (mod.intValue() == 0 && column > 3)
			{
				// let's use a pretty em dash instead of hyphen/minus.
				setText("\u2014");
			}
			else
			{
				setText(formatter.format(mod.longValue()));
			}
			return this;
		}

	}

	/**
	 * The Class <code>ValueRenderer</code> displays a right aligned
	 * read-only column containing a string value.
	 */
	private static class ValueRenderer extends JLabel implements TableCellRenderer
	{

		/**
		 *  Create a new ValueRenderer instance.
		 */
		public ValueRenderer()
		{
			setHorizontalAlignment(RIGHT);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			setFont(FontManipulation.title(table.getFont()));
			setBackground(table.getBackground());
			setForeground(table.getForeground());
			setText((String) value);
			return this;
		}

	}

	public void install()
	{
		table.setModel(this);
		table.setDefaultRenderer(Object.class, renderer);
		TableColumn column = table.getColumn(EDITABLE);
		column.setCellRenderer(new TableCellUtilities.SpinnerRenderer());

		column.setCellEditor(editor);
		Dimension size = table.getPreferredSize();
		size.width = table.getTableHeader().getPreferredSize().width;
		table.setPreferredScrollableViewportSize(size);

		for (StatFacade aStat : stats)
		{
			character.getScoreBaseRef(aStat).addReferenceListener(this);
		}

	}

	public void uninstall()
	{
		if (table.isEditing() && !editor.stopCellEditing())
		{
			editor.cancelCellEditing();
		}

		for (StatFacade aStat : stats)
		{
			character.getScoreBaseRef(aStat).removeReferenceListener(this);
		}
	}

	public static class SpinnerEditor extends AbstractCellEditor
			implements TableCellEditor, ChangeListener
	{

		protected final JSpinner spinner;

		public SpinnerEditor()
		{
			this.spinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		}

		/**
		 * Set a new lower bound for the spinner.
		 * @param minValue The new minimum value.
		 */
		public void setMinValue(int minValue)
		{
			SpinnerNumberModel spinnerModel =
					(SpinnerNumberModel) spinner.getModel();
			spinnerModel.setMinimum(minValue);
		}
		
		public JTextField getTextField()
		{
			return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
		}

		@Override
		public Object getCellEditorValue()
		{
			return spinner.getValue();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
													 boolean isSelected,
													 int row,
													 int column)
		{
			spinner.setValue(value);
			spinner.addChangeListener(this);
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
				spinner.removeChangeListener(this);
				spinner.commitEdit();
			}
			catch (ParseException ex)
			{
				return false;
			}
			return super.stopCellEditing();
		}

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == EDITABLE_SCORE;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if (columnIndex == ABILITY_NAME)
		{
			return Object.class;
		}
		return Integer.class;
	}

	@Override
	public int getColumnCount()
	{
		return 6;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		StatFacade stat = stats.getElementAt(rowIndex);
		switch (columnIndex)
		{
			case ABILITY_NAME:
				return stat;
			case ABILITY_MOD:
				return character.getModTotal(stat);
			case EDITABLE_SCORE:
				return character.getScoreBase(stat);
			case RACE_ADJ:
				return character.getScoreRaceBonus(stat);
			case FINAL_ABILITY_SCORE:
				return character.getScoreTotalString(stat);
			case MISC_ADJ:
				return character.getScoreOtherBonus(stat);
			default:
				return 0;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		Number number = (Number) aValue;
		character.setScoreBase(stats.getElementAt(rowIndex), number.intValue());
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	@Override
	public void referenceChanged(ReferenceEvent<Integer> e)
	{
		fireTableDataChanged();
	}

	/**
	 * Table renderer used for abilities/statistics.
	 */
	private class StatRenderer extends JPanel implements TableCellRenderer
	{

		private JLabel statLabel = new JLabel();

		public StatRenderer()
		{
			setOpaque(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(statLabel);
			add(Box.createHorizontalGlue());
			// This is to force the use of the parent component font
			statLabel.setFont(null);
		}

		@Override
		public Component getTableCellRendererComponent(JTable jTable,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
		{
			setFont(FontManipulation.title(jTable.getFont()));
			// Those two does not seem to change anything.
			setBackground(jTable.getBackground());
			setForeground(jTable.getForeground());
			StatFacade stat = (StatFacade) value;
			//TODO: this should really call stat.toString()
			statLabel.setText(stat.getName());
			return this;
		}

	}

}
