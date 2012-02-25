/*
 * SkillInfoTab.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 10, 2008, 8:03:21 PM
 */
package pcgen.gui2.tabs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.AbstractSpinnerModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.core.facade.CharacterLevelsFacade.SkillBonusListener;
import pcgen.core.facade.CharacterLevelsFacade.SkillPointListener;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterUtilities;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.HtmlSheetSupport;
import pcgen.gui2.tabs.skill.SkillCostTableModel;
import pcgen.gui2.tabs.skill.SkillPointTableModel;
import pcgen.gui2.tabs.skill.SkillTreeViewModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.gui2.util.table.TableCellUtilities.SpinnerEditor;
import pcgen.gui2.util.table.TableCellUtilities.SpinnerRenderer;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SkillInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler
{

	private final FilteredTreeViewTable skillTable;
	private final JTable skillcostTable;
	private final JTable skillpointTable;
	private final InfoPane infoPane;
	private final TabTitle tabTitle;
	private final FilterButton cFilterButton;
	private final FilterButton trainedFilterButton;
	private final JEditorPane htmlPane;

	public SkillInfoTab()
	{
		this.skillTable = new FilteredTreeViewTable();
		this.skillcostTable = new JTable();
		this.skillpointTable = new JTable();
		this.infoPane = new InfoPane();
		this.cFilterButton = new FilterButton();
		this.trainedFilterButton = new FilterButton();
		this.tabTitle = new TabTitle("in_skills");
		this.htmlPane = new JEditorPane();
		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);
		setResizeWeight(.70);

		JSpinner spinner = new JSpinner();
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "#0.#"));
		skillTable.setDefaultRenderer(Float.class, new SpinnerRenderer(spinner));
		skillTable.setDefaultRenderer(Integer.class,
			new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		skillTable.setDefaultRenderer(String.class,
			new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		skillTable.setRowHeight(26);
		FilterBar filterBar = new FilterBar();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		cFilterButton.setText("Class");
		cFilterButton.setEnabled(false);
		filterBar.addDisplayableFilter(cFilterButton);

		trainedFilterButton.setText(LanguageBundle.getString("in_trained"));
		trainedFilterButton.setEnabled(false);
		filterBar.addDisplayableFilter(trainedFilterButton);
		JPanel availPanel = FilterUtilities.configureFilteredTreeViewPane(skillTable, filterBar);
		availPanel.setPreferredSize(new Dimension(650, 300));
		JScrollPane tableScrollPane;
		JPanel tablePanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		constraints.fill = java.awt.GridBagConstraints.BOTH;
		constraints.weightx = 1.0;

		constraints.ipady = 0;
		constraints.weighty = 1.0;

		SkillPointTableModel.initializeTable(skillpointTable);
		tableScrollPane = new JScrollPane(skillpointTable);
		tablePanel.add(tableScrollPane, constraints);

		htmlPane.setOpaque(false);
		htmlPane.setEditable(false);
		htmlPane.setFocusable(false);
		htmlPane.setContentType("text/html");
		JScrollPane selScrollPane = new JScrollPane(htmlPane);
		selScrollPane.setPreferredSize(new Dimension(530, 300));
		
		FlippingSplitPane topPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			  true,
			  availPanel,
			  selScrollPane);
		setTopComponent(topPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		bottomPane.setLeftComponent(tablePanel);
		tablePanel.setPreferredSize(new Dimension(650, 100));
		bottomPane.setRightComponent(infoPane);
		infoPane.setPreferredSize(new Dimension(530, 100));
		setBottomComponent(bottomPane);
		
	}

	public Hashtable<Object, Object> createModels(final CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();

		ListSelectionModel listModel = new DefaultListSelectionModel();
		listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		state.put(ListSelectionModel.class, listModel);
		state.put(SkillPointTableModel.class, new SkillPointTableModel(character));
		state.put(SkillTreeViewModel.class, new SkillTreeViewModel(character, listModel));
		state.put(SkillCostTableModel.class, new SkillCostTableModel(character, listModel));
		state.put(FilterHandler.class, new FilterHandler(character, listModel));
		state.put(InfoHandler.class, new InfoHandler(character));
		state.put(LevelSelectionHandler.class, new LevelSelectionHandler(character, listModel));
		state.put(SkillRankSpinnerEditor.class, new SkillRankSpinnerEditor(character, listModel));
		state.put(SkillSheetHandler.class, new SkillSheetHandler(character));
		return state;
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((SkillTreeViewModel) state.get(SkillTreeViewModel.class)).uninstall();
		((FilterHandler) state.get(FilterHandler.class)).uninstall();
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((LevelSelectionHandler) state.get(LevelSelectionHandler.class)).uninstall();
		((SkillSheetHandler) state.get(SkillSheetHandler.class)).uninstall();
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		skillpointTable.setModel((SkillPointTableModel) state.get(SkillPointTableModel.class));
		skillpointTable.setSelectionModel((ListSelectionModel) state.get(ListSelectionModel.class));
		skillcostTable.setModel((SkillCostTableModel) state.get(SkillCostTableModel.class));
		skillTable.setDefaultEditor(Float.class, (SkillRankSpinnerEditor) state.get(SkillRankSpinnerEditor.class));
		((FilterHandler) state.get(FilterHandler.class)).install();
		((SkillTreeViewModel) state.get(SkillTreeViewModel.class)).install(skillTable);
		((InfoHandler) state.get(InfoHandler.class)).install();
		((LevelSelectionHandler) state.get(LevelSelectionHandler.class)).install();
		((SkillSheetHandler) state.get(SkillSheetHandler.class)).install();
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		skillTable.requestFocusInWindow();
	}

	private class SkillSheetHandler implements SkillBonusListener
	{

		private final HtmlSheetSupport support;

		public SkillSheetHandler(CharacterFacade character)
		{
			String sheet = character.getDataSet().getGameMode().getInfoSheetSkill();
			support = new HtmlSheetSupport(character, htmlPane, sheet);
			character.getCharacterLevelsFacade().addSkillBonusListener(this);
		}

		public void install()
		{
			support.install();
		}

		public void uninstall()
		{
			support.uninstall();
		}

		public void skillBonusChanged(CharacterLevelEvent e)
		{
			support.refresh();
		}

	}

	private class LevelSelectionHandler implements ListListener,
			SkillPointListener, Runnable, ListSelectionListener
	{

		private final CharacterLevelsFacade levels;
		private final ListSelectionModel model;

		public LevelSelectionHandler(CharacterFacade character, ListSelectionModel model)
		{
			this.levels = character.getCharacterLevelsFacade();
			this.model = model;
		}

		public void install()
		{
			levels.addSkillPointListener(this);
			levels.addListListener(this);
			model.addListSelectionListener(this);
			updateSelectedIndex();
		}

		public void uninstall()
		{
			levels.removeSkillPointListener(this);
			levels.removeListListener(this);
			model.removeListSelectionListener(this);
		}

		public void run()
		{
			for (int i = 0; i < levels.getSize(); i++)
			{
				CharacterLevelFacade level = levels.getElementAt(i);
				if (levels.getSpentSkillPoints(level) < levels.getGainedSkillPoints(level))
				{
					if (i != model.getMinSelectionIndex())
					{
						model.setSelectionInterval(i, i);
					}
					return;
				}
			}
			// Fall back for a non empty list of levels is to select the highest one.
			if (levels.getSize() > 0)
			{
				model.setSelectionInterval(levels.getSize() - 1,
					levels.getSize() - 1);
			}
		}

		private void updateSelectedIndex()
		{
			if (levels.isEmpty())
			{
				return;
			}
			//if a level is already selected, don't change it
			//unless all the skill points have been spent
			if (!model.isSelectionEmpty())
			{
				int index = model.getMinSelectionIndex();
				CharacterLevelFacade level = levels.getElementAt(index);
				if (levels.getSpentSkillPoints(level) < levels.getGainedSkillPoints(level))
				{
					return;
				}
			}
			/* Updating now would conflict with the JTable updating due to the same events.
			 * So update the selection model after JTable has had its way with it.
			 */
			SwingUtilities.invokeLater(this);
		}

		public void skillPointsChanged(CharacterLevelEvent e)
		{
			updateSelectedIndex();
		}

		public void elementAdded(ListEvent e)
		{
			updateSelectedIndex();
		}

		public void elementRemoved(ListEvent e)
		{
			updateSelectedIndex();
		}

		public void elementsChanged(ListEvent e)
		{
			updateSelectedIndex();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			skillTable.refreshModelData();
		}

	}

	private class FilterHandler implements ListSelectionListener
	{

		private final Filter<CharacterFacade, SkillFacade> cFilter = new Filter<CharacterFacade, SkillFacade>()
		{

			public boolean accept(CharacterFacade context, SkillFacade element)
			{
				CharacterLevelsFacade levels = context.getCharacterLevelsFacade();
				CharacterLevelFacade level = levels.getElementAt(model.getMinSelectionIndex());
				return levels.getSkillCost(level, element) == SkillCost.CLASS;
			}

		};

		private final Filter<CharacterFacade, SkillFacade> gainedFilter = new Filter<CharacterFacade, SkillFacade>()
		{

			public boolean accept(CharacterFacade context, SkillFacade element)
			{
				CharacterLevelsFacade levels = context.getCharacterLevelsFacade();
				return levels.getSkillRanks(null, element) > 0.0f;
			}

		};
		private final ListSelectionModel model;
		private final CharacterFacade character;
		private boolean installed = false;

		public FilterHandler(CharacterFacade character, ListSelectionModel model)
		{
			this.character = character;
			this.model = model;
			model.addListSelectionListener(this);
		}

		public void install()
		{
			installed = true;
			skillTable.setContext(null);
			cFilterButton.setFilter(cFilter);
			cFilterButton.setEnabled(model.getMinSelectionIndex() != -1);
			trainedFilterButton.setFilter(gainedFilter);
			trainedFilterButton.setEnabled(true);
			skillTable.setContext(character);
		}

		public void uninstall()
		{
			installed = false;
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (installed && !e.getValueIsAdjusting())
			{
				cFilterButton.setEnabled(model.getMinSelectionIndex() != -1);
				trainedFilterButton.setEnabled(model.getMinSelectionIndex() != -1);
			}
		}

	}

	private class SkillRankSpinnerEditor extends SpinnerEditor
	{

		private final SkillRankSpinnerModel model;
		private ListSelectionModel listModel;
		private CharacterFacade character;

		public SkillRankSpinnerEditor(CharacterFacade character, ListSelectionModel listModel)
		{
			this(new SkillRankSpinnerModel(character));
			this.listModel = listModel;
			this.character = character;
		}

		private SkillRankSpinnerEditor(SkillRankSpinnerModel model)
		{
			super(model);
			this.model = model;

			DefaultEditor editor = new DefaultEditor(spinner);
			NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#0.#"));
			formatter.setValueClass(Float.class);
			DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

			JFormattedTextField ftf = editor.getTextField();
			ftf.setEditable(true);
			ftf.setFormatterFactory(factory);
			ftf.setHorizontalAlignment(JTextField.RIGHT);

			spinner.setEditor(editor);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
													 boolean isSelected,
													 int row,
													 int column)
		{
			SkillFacade skill = (SkillFacade) table.getModel().getValueAt(row, 0);
			int index = listModel.getMinSelectionIndex();
			model.configureModel(skill, character.getCharacterLevelsFacade().getElementAt(index));
			return spinner;
		}

		@Override
		public boolean isCellEditable(EventObject e)
		{
			return listModel.getMinSelectionIndex() != -1;
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			releaseMouse(spinner);
			super.stateChanged(e);
		}

		private void releaseMouse(JSpinner spinner)
		{
			for (int i = 0; i < spinner.getComponentCount(); i++)
			{
				Component comp = (Component) spinner.getComponent(i);
				if (comp instanceof JButton)
				{
					releaseMouse(comp);
				}
			}
		}

		private void releaseMouse(Component component)
		{
			MouseListener[] listeners =
					component.getMouseListeners();
			for (int i = 0; i < listeners.length; i++)
			{
				MouseListener listener = (MouseListener) listeners[i];
				listener.mouseReleased(new MouseEvent(component, MouseEvent.MOUSE_RELEASED,
													  System.currentTimeMillis(), 0, 0, 0, 1, false));
			}
		}

	}

	private class SkillRankSpinnerModel extends AbstractSpinnerModel
	{

		private final CharacterFacade character;
		private CharacterLevelFacade level;
		private SkillFacade skill;

		public SkillRankSpinnerModel(CharacterFacade character)
		{
			this.character = character;
		}

		public Float getValue()
		{
			return character.getCharacterLevelsFacade().getSkillRanks(level, skill);
		}

		public void configureModel(SkillFacade skill, CharacterLevelFacade level)
		{
			this.skill = skill;
			this.level = level;
			fireStateChanged();
		}

		public void setValue(Object value)
		{
			if (value instanceof Float)
			{
				setValue((Float) value);
			}
		}

		public void setValue(Float value)
		{
			CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
			SkillCost cost = levels.getSkillCost(level, skill);
			if (value < 0)
			{
				value = Float.valueOf(0);
			}
			float max = levels.getMaxRanks(level, cost);
			if (value > max)
			{
				value = max;
			}

			int points = (int) ((value - getValue()) * levels.getSkillCost(level, skill).getCost());
			if (points == 0)
			{
				// No change, ignore
				return;
			}


			if (levels.investSkillPoints(level, skill, points))
			{
				fireStateChanged();
				//TODO: Remove this method when CharacterFacade's event system is created.
				//skillpointTable.repaint();
				skillTable.refreshModelData();
			}
			else
			{
				skillTable.getCellEditor().cancelCellEditing();
			}
		}

		public Float getNextValue()
		{
			float value = getValue();
			if (level == null)
			{
				return null;
			}
			CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
			SkillCost cost = levels.getSkillCost(level, skill);

			if (value == levels.getMaxRanks(level, cost))
			{
				return null;
			}
			return value + 1f / cost.getCost();
		}

		public Float getPreviousValue()
		{
			float value = getValue();
			if (level == null || value == 0)
			{
				return null;
			}
			CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
			SkillCost cost = levels.getSkillCost(level, skill);
			return value - 1f / cost.getCost();
		}

	}

	private class InfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			skillTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			skillTable.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object data = skillTable.getSelectedObject();
				if (data != null && data instanceof SkillFacade)
				{
					infoPane.setText(character.getInfoFactory().getHTMLInfo(
							(SkillFacade) data));
				}
				else
				{
					infoPane.setText("");
				}
			}
		}

	}

}
