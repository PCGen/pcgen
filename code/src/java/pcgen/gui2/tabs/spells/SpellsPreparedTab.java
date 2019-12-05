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
package pcgen.gui2.tabs.spells;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.facade.core.SpellSupportFacade.SuperNode;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterUtilities;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.SortableTableRowSorter;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class SpellsPreparedTab extends FlippingSplitPane implements CharacterInfoTab
{

    private final TabTitle tabTitle = new TabTitle(Tab.PREPARED_SPELLS);
    private final FilteredTreeViewTable<CharacterFacade, SuperNode> availableTable;
    private final JTreeViewTable<SuperNode> selectedTable;
    private final QualifiedSpellTreeCellRenderer spellRenderer;
    private final JButton addMMSpellButton;
    private final JButton addSpellButton;
    private final JButton removeSpellButton;
    private final FilterButton<CharacterFacade, SuperNode> qFilterButton;
    private final JButton addSpellListButton;
    private final JButton removeSpellListButton;
    private final JCheckBox slotsBox;
    private final JTextField spellListField;
    private final InfoPane spellsPane;
    private final InfoPane classPane;

    public SpellsPreparedTab()
    {
        this.availableTable = new FilteredTreeViewTable<>();
        this.selectedTable = new JTreeViewTable<>()
        {

            @Override
            public void setTreeViewModel(TreeViewModel<SuperNode> viewModel)
            {
                super.setTreeViewModel(viewModel);
                sortModel();
            }

        };
        this.spellRenderer = new QualifiedSpellTreeCellRenderer();
        this.addMMSpellButton = new JButton();
        this.addSpellButton = new JButton();
        this.removeSpellButton = new JButton();
        this.qFilterButton = new FilterButton<>("SpellPreparedQualified");
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
        availableTable.setTreeCellRenderer(spellRenderer);
        selectedTable.setTreeCellRenderer(spellRenderer);
        selectedTable.setRowSorter(new SortableTableRowSorter()
        {

            @Override
            public SortableTableModel getModel()
            {
                return (SortableTableModel) selectedTable.getModel();
            }

        });
        selectedTable.getRowSorter().toggleSortOrder(0);
        FilterBar<CharacterFacade, SuperNode> filterBar = new FilterBar<>();
        filterBar.addDisplayableFilter(new SearchFilterPanel());
        qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
        filterBar.addDisplayableFilter(qFilterButton);

        FlippingSplitPane upperPane = new FlippingSplitPane();
        JPanel availPanel = FilterUtilities.configureFilteredTreeViewPane(availableTable, filterBar);
        Box box = Box.createVerticalBox();
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
        availPanel.add(box, BorderLayout.SOUTH);
        upperPane.setLeftComponent(availPanel);

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

        FlippingSplitPane bottomPane = new FlippingSplitPane();
        bottomPane.setLeftComponent(spellsPane);
        bottomPane.setRightComponent(classPane);
        setBottomComponent(bottomPane);
        setOrientation(VERTICAL_SPLIT);
    }

    @Override
    public ModelMap createModels(CharacterFacade character)
    {
        ModelMap models = new ModelMap();
        models.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
        models.put(AddMMSpellAction.class, new AddMMSpellAction(character));
        models.put(AddSpellAction.class, new AddSpellAction(character));
        models.put(RemoveSpellAction.class, new RemoveSpellAction(character));
        models.put(AddSpellListAction.class, new AddSpellListAction(character));
        models.put(RemoveSpellListAction.class, new RemoveSpellListAction(character));
        models.put(UseHigherSlotsAction.class, new UseHigherSlotsAction(character));
        models.put(SpellInfoHandler.class, new SpellInfoHandler(character, availableTable, selectedTable, spellsPane));
        models.put(ClassInfoHandler.class, new ClassInfoHandler(character, availableTable, selectedTable, classPane));
        models.put(SpellFilterHandler.class, new SpellFilterHandler(character));
        return models;
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(SpellFilterHandler.class).install();
        models.get(TreeViewModelHandler.class).install();
        models.get(SpellInfoHandler.class).install();
        models.get(ClassInfoHandler.class).install();
        models.get(AddSpellAction.class).install();
        models.get(RemoveSpellAction.class).install();
        addMMSpellButton.setAction(models.get(AddMMSpellAction.class));
        addSpellListButton.setAction(models.get(AddSpellListAction.class));
        removeSpellListButton.setAction(models.get(RemoveSpellListAction.class));
        models.get(UseHigherSlotsAction.class).install();
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(SpellInfoHandler.class).uninstall();
        models.get(ClassInfoHandler.class).uninstall();
        models.get(AddSpellAction.class).uninstall();
        models.get(RemoveSpellAction.class).uninstall();
        models.get(TreeViewModelHandler.class).uninstall();
    }

    @Override
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
                spellList = ((SpellNode) selectedObject).getRootNode().toString();
            } else
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
            String label = character.getDataSet().getGameMode().getAddWithMetamagicMessage();
            if (StringUtils.isEmpty(label))
            {
                label = LanguageBundle.getString("InfoSpells.add.with.metamagic");
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
                    character.getSpellSupport().addPreparedSpell((SpellNode) object, spellList, true);
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
                    character.getSpellSupport().addPreparedSpell((SpellNode) object, spellList, false);
                }
            }
        }

        public void install()
        {
            availableTable.addActionListener(this);
            addSpellButton.setAction(this);
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
                    character.getSpellSupport().removePreparedSpell(spellNode, spellNode.getRootNode().toString());
                }
            }
        }

        public void install()
        {
            selectedTable.addActionListener(this);
            removeSpellButton.setAction(this);
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
            slotsBox.setAction(this);
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
        private CharacterFacade character;

        public TreeViewModelHandler(CharacterFacade character)
        {
            this.character = character;
            availableModel = new SpellTreeViewModel(character.getSpellSupport().getKnownSpellNodes(), false,
                    "SpellsPrepAva", character.getInfoFactory());
            selectedModel = new SpellTreeViewModel(character.getSpellSupport().getPreparedSpellNodes(), true,
                    "SpellsPrepSel", character.getInfoFactory());
        }

        public void install()
        {
            spellRenderer.setCharacter(character);
            availableTable.setTreeViewModel(availableModel);
            selectedTable.setTreeViewModel(selectedModel);
        }

        public void uninstall()
        {
            spellRenderer.setCharacter(null);
        }

    }

    private class SpellFilterHandler implements Filter<CharacterFacade, SuperNode>
    {

        private final CharacterFacade character;

        public SpellFilterHandler(CharacterFacade character)
        {
            this.character = character;
        }

        public void install()
        {
            qFilterButton.setFilter(this);
        }

        @Override
        public boolean accept(CharacterFacade context, SuperNode element)
        {
            if (element instanceof SpellNode)
            {
                SpellNode spellNode = (SpellNode) element;
                SpellFacade spell = spellNode.getSpell();
                PCClass pcClass = spellNode.getSpellcastingClass();
                return character.isQualifiedFor(spell, pcClass);
            }
            return true;
        }

    }

}
