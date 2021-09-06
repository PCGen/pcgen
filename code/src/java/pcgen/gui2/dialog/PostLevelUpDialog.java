/*
 * Copyright James Dempsey, 2012
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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.HitPointListener;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.table.TableCellUtilities.SpinnerEditor;
import pcgen.gui2.util.table.TableCellUtilities.SpinnerRenderer;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * The Class {@code PostLevelUpDialog} provides a display of the results
 * of levelling up a character. 
 *
 * 
 */
@SuppressWarnings("serial")
public final class PostLevelUpDialog extends JDialog implements ActionListener
{

	private final CharacterLevelsFacade levels;
	private final LevelTableModel tableModel;
	private final int oldLevel;
	private final int numLevels;

	private PostLevelUpDialog(Frame frame, CharacterFacade character, int oldLevel)
	{
		super(frame, true);
		this.oldLevel = oldLevel;
		this.levels = character.getCharacterLevelsFacade();
		numLevels = character.getCharacterLevelsFacade().getSize() - oldLevel;
		this.tableModel = new LevelTableModel();
		initComponents();
		pack();
	}

	/**
	 * Display the post levelling dialog for a character. This will display a 
	 * list of levels just added along with the hit points and skill points 
	 * gained. The hit points gained may be edited.
	 * 
	 * @param parent The component we should appear above.
	 * @param character The character that has been levelled up.
	 * @param oldLevel The character's level before the level up action.
	 */
	public static void showPostLevelUpDialog(Component parent, CharacterFacade character, int oldLevel)
	{
		int size = character.getCharacterLevelsFacade().getSize();
		if (size - oldLevel + 1 < 1)
		{
			return;
		}

		Frame frame = JOptionPane.getFrameForComponent(parent);
		PostLevelUpDialog dialog = new PostLevelUpDialog(frame, character, oldLevel);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private void initComponents()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		JTable table = new JTable(tableModel)
		{

			@Override
			public TableCellEditor getCellEditor(int row, int column)
			{
				if (column == LevelTableModel.COL_ROLLED_HP && row < numLevels)
				{//TODO: the max roll should be calculated in a different manner
					String hd = levels.getClassTaken(levels.getElementAt(row + oldLevel)).getHD();
					int max = NumberUtils.toInt(hd);
					return new SpinnerEditor(new SpinnerNumberModel(1, 1, max, 1));
				}
				return super.getCellEditor(row, column);
			}

			@Override
			public TableCellRenderer getCellRenderer(int row, int column)
			{
				if (column == LevelTableModel.COL_ROLLED_HP && row < numLevels)
				{
					return new SpinnerRenderer();
				}
				return super.getCellRenderer(row, column);
			}

		};
		table.setCellSelectionEnabled(false);
		table.setRowHeight(new JSpinner().getPreferredSize().height);
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(table);
		pane.add(scrollPane, BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		JButton button = new JButton(LanguageBundle.getString("in_close")); //$NON-NLS-1$
		button.setMnemonic(LanguageBundle.getMnemonic("in_mn_close")); //$NON-NLS-1$
		button.setActionCommand("Close"); //$NON-NLS-1$
		button.addActionListener(this);
		box.add(button);
		pane.add(box, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosed(WindowEvent e)
			{
				//Make sure to remove the listeners so that the garbage collector can
				//dispose of this dialog and prevent a memory leak
				levels.removeHitPointListener(tableModel);
			}

		});

		Utility.installEscapeCloseOperation(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Close the dialog
		dispose();
	}

	private class LevelTableModel extends AbstractTableModel implements HitPointListener
	{
		static final int COL_LEVEL = 0;
		static final int COL_CLASS = 1;
		static final int COL_GAINED_HP = 2;
		static final int COL_ROLLED_HP = 3;
		static final int COL_SKILL_POINTS = 4;

		private final Object[] columns;
		private final Object[][] data;
		private final Map<PCClass, MutableInt> classLevelMap;

		LevelTableModel()
		{
			columns = new Object[]{LanguageBundle.getString("in_level"), //$NON-NLS-1$
				LanguageBundle.getString("in_classString"), //$NON-NLS-1$
				LanguageBundle.getString("in_luGainedHp"), //$NON-NLS-1$
				LanguageBundle.getString("in_luRolledHp"), //$NON-NLS-1$
				LanguageBundle.getString("in_luSkillPoints") //$NON-NLS-1$
			};

			data = new Object[numLevels + 1][5];
			classLevelMap = new HashMap<>();
			int gainedTotal = 0;
			int rolledTotal = 0;
			int pointTotal = 0;
			for (int i = oldLevel; i < (numLevels + oldLevel); i++)
			{
				CharacterLevelFacade level = levels.getElementAt(i);
				Object[] dataRow = data[i - oldLevel];
				dataRow[COL_LEVEL] = i + 1;
				PCClass pcClass = levels.getClassTaken(level);
				dataRow[COL_CLASS] = pcClass;
				if (!classLevelMap.containsKey(pcClass))
				{
					classLevelMap.put(pcClass, new MutableInt(0));
				}
				classLevelMap.get(pcClass).increment();
				gainedTotal += (Integer) (dataRow[COL_GAINED_HP] = levels.getHPGained(level));
				rolledTotal += (Integer) (dataRow[COL_ROLLED_HP] = levels.getHPRolled(level));
				pointTotal += (Integer) (dataRow[COL_SKILL_POINTS] = levels.getGainedSkillPoints(level));
			}
			data[numLevels][COL_LEVEL] = LanguageBundle.getString("in_sumTotal"); //$NON-NLS-1$
			StringBuilder builder = new StringBuilder(100);
			Iterator<PCClass> classes = classLevelMap.keySet().iterator();
			while (classes.hasNext())
			{
				PCClass c = classes.next();
				builder.append(c.getAbbrev()).append(' ');
				builder.append('(').append(classLevelMap.get(c)).append(')');
				if (classes.hasNext())
				{
					builder.append(", "); //$NON-NLS-1$
				}
			}
			data[numLevels][COL_CLASS] = builder;
			data[numLevels][COL_GAINED_HP] = gainedTotal;
			data[numLevels][COL_ROLLED_HP] = rolledTotal;
			data[numLevels][COL_SKILL_POINTS] = pointTotal;

			levels.addHitPointListener(this);
		}

		@Override
		public int getRowCount()
		{
			return numLevels + 1;
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex == COL_ROLLED_HP)
			{
				return rowIndex < numLevels;
			}
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return switch (columnIndex)
					{
						case COL_GAINED_HP, COL_ROLLED_HP, COL_SKILL_POINTS -> Integer.class;
						default -> Object.class;
					};
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return data[rowIndex][columnIndex];
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			CharacterLevelFacade level = levels.getElementAt(rowIndex + oldLevel);
			levels.setHPRolled(level, (Integer) aValue);
		}

		@Override
		public void hitPointsChanged(CharacterLevelEvent e)
		{
			int gainedTotal = 0;
			int rolledTotal = 0;
			for (int i = oldLevel; i < numLevels + oldLevel; i++)
			{
				CharacterLevelFacade level = levels.getElementAt(i);
				Object[] dataRow = data[i - oldLevel];
				gainedTotal += (Integer) (dataRow[COL_GAINED_HP] = levels.getHPGained(level));
				rolledTotal += (Integer) (dataRow[COL_ROLLED_HP] = levels.getHPRolled(level));
			}
			data[numLevels][COL_GAINED_HP] = gainedTotal;
			data[numLevels][COL_ROLLED_HP] = rolledTotal;
			fireTableRowsUpdated(0, data.length);
		}

	}

}
