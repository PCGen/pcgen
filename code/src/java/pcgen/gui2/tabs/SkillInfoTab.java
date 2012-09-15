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

	private final FilteredTreeViewTable<CharacterFacade, SkillFacade> skillTable;
	private final JTable skillcostTable;
	private final JTable skillpointTable;
	private final InfoPane infoPane;
	private final TabTitle tabTitle;
	private final FilterButton<CharacterFacade, SkillFacade> cFilterButton;
	private final FilterButton<CharacterFacade, SkillFacade> trainedFilterButton;
	private final JEditorPane htmlPane;

	public SkillInfoTab()
	{
		super("Skill");
		this.skillTable = new FilteredTreeViewTable<CharacterFacade, SkillFacade>();
		this.skillcostTable = new JTable();
		this.skillpointTable = new JTable();
		this.infoPane = new InfoPane();
		this.cFilterButton = new FilterButton<CharacterFacade, SkillFacade>();
		this.trainedFilterButton = new FilterButton<CharacterFacade, SkillFacade>();
		this.tabTitle = new TabTitle("in_skills"); //$NON-NLS-1$
		this.htmlPane = new JEditorPane();
		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);
		setResizeWeight(.70);

		JSpinner spinner = new JSpinner();
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "#0.#")); //$NON-NLS-1$
		skillTable.setDefaultRenderer(Float.class, new SpinnerRenderer(spinner));
		skillTable.setDefaultRenderer(Integer.class,
			new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		skillTable.setDefaultRenderer(String.class,
			new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		skillTable.setRowHeight(26);
		FilterBar<CharacterFacade, SkillFacade> filterBar = new FilterBar<CharacterFacade, SkillFacade>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		cFilterButton.setText(LanguageBundle.getString("in_classString")); //$NON-NLS-1$
		cFilterButton.setEnabled(false);
		filterBar.addDisplayableFilter(cFilterButton);

		trainedFilterButton.setText(LanguageBundle.getString("in_trained")); //$NON-NLS-1$
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
		htmlPane.setContentType("text/html"); //$NON-NLS-1$
		JScrollPane selScrollPane = new JScrollPane(htmlPane);
		selScrollPane.setPreferredSize(new Dimension(530, 300));
		
		FlippingSplitPane topPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			  true,
			  availPanel,
			  selScrollPane,
			  "SkillTop");
		setTopComponent(topPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, "SkillBottom");
		bottomPane.setLeftComponent(tablePanel);
		tablePanel.setPreferredSize(new Dimension(650, 100));
		bottomPane.setRightComponent(infoPane);
		infoPane.setPreferredSize(new Dimension(530, 100));
		setBottomComponent(bottomPane);
		
	}

	@Override
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

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((SkillTreeViewModel) state.get(SkillTreeViewModel.class)).uninstall();
		((FilterHandler) state.get(FilterHandler.class)).uninstall();
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((LevelSelectionHandler) state.get(LevelSelectionHandler.class)).uninstall();
		((SkillSheetHandler) state.get(SkillSheetHandler.class)).uninstall();
	}

	@Override
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

	@Override
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

		@Override
		public void skillBonusChanged(CharacterLevelEvent e)
		{
			support.refresh();
		}

	}

	private class LevelSelectionHandler implements ListListener<CharacterLevelFacade>,
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
			updateSelectedIndex(false);
		}

		public void uninstall()
		{
			levels.removeSkillPointListener(this);
			levels.removeListListener(this);
			model.removeListSelectionListener(this);
		}

		@Override
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
						skillpointTable.scrollRectToVisible(skillpointTable
							.getCellRect(i, 0, true));
					}
					return;
				}
			}
			// Fall back for a non empty list of levels is to select the highest one.
			if (levels.getSize() > 0)
			{
				model.setSelectionInterval(levels.getSize() - 1,
					levels.getSize() - 1);
				skillpointTable.scrollRectToVisible(skillpointTable
					.getCellRect(levels.getSize() - 1, 0, true));
			}
		}

		private void updateSelectedIndex(boolean forceChange)
		{
			if (levels.isEmpty())
			{
				return;
			}
			//if a level is already selected, don't change it
			//unless all the skill points have been spent
			if (!model.isSelectionEmpty() && !forceChange)
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

		@Override
		public void skillPointsChanged(CharacterLevelEvent e)
		{
			int firstRow = e.getBaseLevelIndex();
			boolean force = firstRow < model.getMinSelectionIndex();
			updateSelectedIndex(force);
		}

		@Override
		public void elementAdded(ListEvent<CharacterLevelFacade> e)
		{
			updateSelectedIndex(false);
		}

		@Override
		public void elementRemoved(ListEvent<CharacterLevelFacade> e)
		{
			updateSelectedIndex(false);
		}

		@Override
		public void elementsChanged(ListEvent<CharacterLevelFacade> e)
		{
			updateSelectedIndex(false);
		}

		@Override
		public void elementModified(ListEvent<CharacterLevelFacade> e)
		{
			updateSelectedIndex(false);
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

			@Override
			public boolean accept(CharacterFacade context, SkillFacade element)
			{
				if (context == null)
				{
					return false;
				}
				CharacterLevelsFacade levels = context.getCharacterLevelsFacade();
				CharacterLevelFacade level = levels.getElementAt(model.getMinSelectionIndex());
				return levels.getSkillCost(level, element) == SkillCost.CLASS;
			}

		};

		private final Filter<CharacterFacade, SkillFacade> gainedFilter = new Filter<CharacterFacade, SkillFacade>()
		{

			@Override
			public boolean accept(CharacterFacade context, SkillFacade element)
			{
				if (context == null)
				{
					return false;
				}
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
			cFilterButton.setFilter(cFilter);
			trainedFilterButton.setFilter(gainedFilter);
			skillTable.setContext(character);
		}

		public void uninstall()
		{
			installed = false;
		}

		@Override
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
			NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#0.#")); //$NON-NLS-1$
			formatter.setValueClass(Float.class);
			DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

			JFormattedTextField ftf = editor.getTextField();
			ftf.setEditable(true);
			ftf.setFormatterFactory(factory);
			ftf.setHorizontalAlignment(SwingConstants.RIGHT);

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

		private void releaseMouse(JSpinner jSpinner)
		{
			for (int i = 0; i < jSpinner.getComponentCount(); i++)
			{
				Component comp = jSpinner.getComponent(i);
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
				MouseListener listener = listeners[i];
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

		@Override
		public Float getValue()
		{
			return character.getCharacterLevelsFacade().getSkillRanks(level, skill);
		}

		public void configureModel(SkillFacade sk, CharacterLevelFacade charLevel)
		{
			this.skill = sk;
			this.level = charLevel;
			fireStateChanged();
		}

		@Override
		public void setValue(Object value)
		{
			if (value instanceof Float)
			{
				setValue((Float) value);
			}
		}

		public void setValue(Float value)
		{
			if (value == null)
			{
				return;
			}

			CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
			CharacterLevelFacade targetLevel =
					levels.findNextLevelForSkill(skill, level, value);
			if (targetLevel == null)
			{
				// No level where it can be raised.
				return;
			}
			SkillCost cost = levels.getSkillCost(targetLevel, skill);
			if (value < 0)
			{
				value = Float.valueOf(0);
			}
			float max = levels.getMaxRanks(targetLevel, cost);
			if (value > max)
			{
				value = max;
			}

			int points = (int) ((value - getValue()) * levels.getSkillCost(targetLevel, skill).getCost());
			if (points == 0)
			{
				// No change, ignore
				return;
			}


			if (levels.investSkillPoints(targetLevel, skill, points))
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

		@Override
		public Float getNextValue()
		{
			float value = getValue();
			if (level == null)
			{
				return null;
			}
			CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
			CharacterLevelFacade targetLevel = levels.findNextLevelForSkill(skill, level, value);
			if (targetLevel == null)
			{
				// No level where it can be raised.
				return null;
			}
			
			SkillCost cost = levels.getSkillCost(targetLevel, skill);
			if (value == levels.getMaxRanks(levels.getElementAt(levels.getSize()-1), cost))
			{
				return null;
			}
			return value + 1f / cost.getCost();
		}

		@Override
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
		private String text;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			skillTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			skillTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object data = skillTable.getSelectedObject();
				if (data != null && data instanceof SkillFacade)
				{
					text = character.getInfoFactory().getHTMLInfo(
							(SkillFacade) data);
				}
				else
				{
					text = ""; //$NON-NLS-1$
				}
				infoPane.setText(text);
			}
		}

	}

}
