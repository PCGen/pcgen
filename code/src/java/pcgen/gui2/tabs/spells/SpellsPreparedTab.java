/*
 * SpellsPreparedTab.java
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
 * Created on Oct 1, 2011, 10:09:27 PM
 */
package pcgen.gui2.tabs.spells;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.core.facade.SpellSupportFacade.SuperNode;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
@SuppressWarnings("serial")
public class SpellsPreparedTab extends FlippingSplitPane
{

	private final TabTitle tabTitle = new TabTitle("in_InfoPrepared"); //$NON-NLS-1$
	private final JTreeViewTable<SuperNode> availableTable;
	private final JTreeViewTable<SuperNode> selectedTable;
	private final JButton addMMSpellButton;
	private final JButton addSpellButton;
	private final JButton removeSpellButton;
	private final JButton addSpellListButton;
	private final JButton removeSpellListButton;
	private final JCheckBox slotsBox;
	private final JTextField spellListField;
	private final InfoPane spellsPane;
	private final InfoPane classPane;

	public SpellsPreparedTab()
	{
		super("SpellsPrepared");
		this.availableTable = new JTreeViewTable<SuperNode>();
		this.selectedTable = new JTreeViewTable<SuperNode>();
		this.addMMSpellButton = new JButton();
		this.addSpellButton = new JButton();
		this.removeSpellButton = new JButton();
		this.addSpellListButton = new JButton();
		this.removeSpellListButton = new JButton();
		this.slotsBox = new JCheckBox();
		this.spellListField = new JTextField();
		this.spellsPane = new InfoPane(LanguageBundle.getString("InfoSpells.spell.info"));
		this.classPane = new InfoPane(LanguageBundle.getString("InfoSpells.class.info"));
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane upperPane = new FlippingSplitPane("SpellsPreparedTop");
		Box box = Box.createVerticalBox();
		JScrollPane pane = new JScrollPane(availableTable);
		pane.setPreferredSize(new Dimension(250, 300));
		box.add(pane);
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			addMMSpellButton.setHorizontalTextPosition(SwingConstants.LEADING);
			hbox.add(addMMSpellButton);
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(2));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(slotsBox);
			hbox.add(Box.createHorizontalGlue());
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(addSpellButton);
			hbox.add(Box.createHorizontalStrut(5));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setLeftComponent(box);

		box = Box.createVerticalBox();
		box.add(new JScrollPane(selectedTable));
		box.add(Box.createVerticalStrut(4));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(removeSpellButton);
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(new JLabel(LanguageBundle.getString("InfoPreparedSpells.preparedList")));
			hbox.add(Box.createHorizontalStrut(3));
			hbox.add(spellListField);
			hbox.add(Box.createHorizontalStrut(3));
			hbox.add(addSpellListButton);
			hbox.add(Box.createHorizontalStrut(3));
			hbox.add(removeSpellListButton);
			hbox.add(Box.createHorizontalStrut(5));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setRightComponent(box);
		upperPane.setResizeWeight(0);
		setTopComponent(upperPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane("SpellsPreparedBottom");
		bottomPane.setLeftComponent(spellsPane);
		bottomPane.setRightComponent(classPane);
		setBottomComponent(bottomPane);
		setOrientation(VERTICAL_SPLIT);
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
		state.put(AddMMSpellAction.class, new AddMMSpellAction(character));
		state.put(AddSpellAction.class, new AddSpellAction(character));
		state.put(RemoveSpellAction.class, new RemoveSpellAction(character));
		state.put(AddSpellListAction.class, new AddSpellListAction(character));
		state.put(RemoveSpellListAction.class, new RemoveSpellListAction(character));
		state.put(UseHigherSlotsAction.class, new UseHigherSlotsAction(character));
		state.put(SpellInfoHandler.class, new SpellInfoHandler(character, availableTable,
															   selectedTable, spellsPane));
		state.put(ClassInfoHandler.class, new ClassInfoHandler(character, availableTable,
															   selectedTable, classPane));
		state.put(QualifiedSpellTreeCellRenderer.class,
			new QualifiedSpellTreeCellRenderer(character));
		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		((TreeViewModelHandler) state.get(TreeViewModelHandler.class)).install();
		((SpellInfoHandler) state.get(SpellInfoHandler.class)).install();
		((ClassInfoHandler) state.get(ClassInfoHandler.class)).install();
		((AddSpellAction) state.get(AddSpellAction.class)).install();
		((RemoveSpellAction) state.get(RemoveSpellAction.class)).install();
		addMMSpellButton.setAction((AddMMSpellAction) state.get(AddMMSpellAction.class));
		addSpellButton.setAction((AddSpellAction) state.get(AddSpellAction.class));
		removeSpellButton.setAction((RemoveSpellAction) state.get(RemoveSpellAction.class));
		addSpellListButton.setAction((AddSpellListAction) state.get(AddSpellListAction.class));
		removeSpellListButton.setAction((RemoveSpellListAction) state.get(RemoveSpellListAction.class));
		slotsBox.setAction((UseHigherSlotsAction) state.get(UseHigherSlotsAction.class));
		((UseHigherSlotsAction) state.get(UseHigherSlotsAction.class)).install();
		availableTable
			.setTreeCellRenderer((QualifiedSpellTreeCellRenderer) state
				.get(QualifiedSpellTreeCellRenderer.class));
		selectedTable
			.setTreeCellRenderer((QualifiedSpellTreeCellRenderer) state
				.get(QualifiedSpellTreeCellRenderer.class));
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((SpellInfoHandler) state.get(SpellInfoHandler.class)).uninstall();
		((ClassInfoHandler) state.get(ClassInfoHandler.class)).uninstall();
		((AddSpellAction) state.get(AddSpellAction.class)).uninstall();
		((RemoveSpellAction) state.get(RemoveSpellAction.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * Identify the current spell list, being the spell list that spell should 
	 * be added to. If no lists exist then a default one will be created.
	 * 
	 * @param character The character qwe are checking for.
	 * @return The name of the 'current' spell list.
	 */
	String getCurrentSpellListName(CharacterFacade character)
	{
		String spellList = "";
		Object selectedObject = selectedTable.getSelectedObject();
		if (selectedObject != null)
		{
			if (selectedObject instanceof SpellNode)
			{
				spellList =
						((SpellNode) selectedObject).getRootNode()
							.toString();
			}
			else
			{
				JTree tree = selectedTable.getTree();
				TreePath path = tree.getSelectionPath();
				while (path.getParentPath() != null && (path.getParentPath().getParentPath() != null))
				{
					path = path.getParentPath();
				}
				spellList = path.getLastPathComponent().toString();
			}
		}
		if (StringUtils.isEmpty(spellList))
		{
			spellList = spellListField.getText();
		}
		if (StringUtils.isEmpty(spellList))
		{
			ListFacade<?> data = selectedTable.getTreeViewModel().getDataModel();
			if (!data.isEmpty())
			{
				Object firstElem = data.getElementAt(0);
				if (firstElem instanceof SpellNode)
				{
					spellList = ((SpellNode) firstElem).getRootNode().toString();
				}
			}
		}
		if (StringUtils.isEmpty(spellList))
		{
			// No lists exist, so create a default one!
			spellList = "Prepared Spells";
			character.getSpellSupport().addSpellList(spellList);
		}
		return spellList;
	}

	private class AddMMSpellAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddMMSpellAction(CharacterFacade character)
		{
			this.character = character;
			String label =
					character.getDataSet().getGameMode()
						.getAddWithMetamagicMessage();
			if (StringUtils.isEmpty(label))
			{
				label =
						LanguageBundle
							.getString("InfoSpells.add.with.metamagic");
			}
			putValue(NAME, label);
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					String spellList = getCurrentSpellListName(character);
					character.getSpellSupport().addPreparedSpell(
						(SpellNode) object, spellList, true);
				}
			}
		}

	}

	private class AddSpellAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			String spellList = getCurrentSpellListName(character);
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					character.getSpellSupport().addPreparedSpell(
						(SpellNode) object, spellList, false);
				}
			}
		}
		
		public void install()
		{
			availableTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			availableTable.removeActionListener(this);
		}

	}

	private class RemoveSpellAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = selectedTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					SpellNode spellNode = (SpellNode) object;
					character.getSpellSupport().removePreparedSpell(spellNode,
						spellNode.getRootNode().toString());
				}
			}
		}
		
		public void install()
		{
			selectedTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	private class UseHigherSlotsAction extends AbstractAction
	{

		private CharacterFacade character;

		public UseHigherSlotsAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("InfoPreparedSpells.canUseHigherSlots"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().setUseHigherPreppedSlots(slotsBox.isSelected());
		}

		public void install()
		{
			slotsBox.setSelected(character.getSpellSupport().isUseHigherPreppedSlots());
		}

	}

	private class AddSpellListAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddSpellListAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("InfoSpells.add"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().addSpellList(spellListField.getText());
		}

	}

	private class RemoveSpellListAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveSpellListAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("InfoSpells.delete"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().removeSpellList(spellListField.getText());
		}

	}

	private class TreeViewModelHandler
	{

		private SpellTreeViewModel availableModel;
		private SpellTreeViewModel selectedModel;

		public TreeViewModelHandler(CharacterFacade character)
		{
			availableModel = new SpellTreeViewModel(character.getSpellSupport().getKnownSpellNodes(), false, "SpellsPrepAva");
			selectedModel = new SpellTreeViewModel(character.getSpellSupport().getPreparedSpellNodes(), true, "SpellsPrepSel");
		}

		public void install()
		{
			availableTable.setTreeViewModel(availableModel);
			selectedTable.setTreeViewModel(selectedModel);
		}

	}

}
