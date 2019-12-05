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
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.PCTemplate;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer.Handler;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.treeview.CachedDataView;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * This component allows the user to manage a character's templates.
 */
public class TemplateInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

    private final TabTitle tabTitle = new TabTitle(Tab.TEMPLATES);
    private final FilteredTreeViewTable<CharacterFacade, PCTemplate> availableTable;
    private final FilteredTreeViewTable<CharacterFacade, PCTemplate> selectedTable;
    private final JButton addButton;
    private final JButton removeButton;
    private final InfoPane infoPane;
    private final FilterButton<CharacterFacade, PCTemplate> qFilterButton;
    private final QualifiedTreeCellRenderer qualifiedRenderer;

    public TemplateInfoTab()
    {
        this.availableTable = new FilteredTreeViewTable<>();
        this.selectedTable = new FilteredTreeViewTable<>();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        this.infoPane = new InfoPane("in_irTemplateInfo"); //$NON-NLS-1$
        this.qFilterButton = new FilterButton<>("TemplateQualified");
        this.qualifiedRenderer = new QualifiedTreeCellRenderer();
        initComponents();
    }

    private void initComponents()
    {
        FlippingSplitPane topPane = new FlippingSplitPane();
        setTopComponent(topPane);
        setOrientation(VERTICAL_SPLIT);

        JPanel availPanel = new JPanel(new BorderLayout());
        FilterBar<CharacterFacade, PCTemplate> bar = new FilterBar<>();
        bar.addDisplayableFilter(new SearchFilterPanel());
        qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
        bar.addDisplayableFilter(qFilterButton);
        availPanel.add(bar, BorderLayout.NORTH);

        availableTable.setDisplayableFilter(bar);
        availableTable.setTreeCellRenderer(qualifiedRenderer);
        availPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        addButton.setHorizontalTextPosition(SwingConstants.LEADING);
        box.add(addButton);
        box.add(Box.createHorizontalStrut(5));
        box.setBorder(new EmptyBorder(0, 0, 5, 0));
        availPanel.add(box, BorderLayout.SOUTH);

        topPane.setLeftComponent(availPanel);

        JPanel selPanel = new JPanel(new BorderLayout());
        FilterBar<CharacterFacade, PCTemplate> filterBar = new FilterBar<>();
        filterBar.addDisplayableFilter(new SearchFilterPanel());

        selectedTable.setDisplayableFilter(filterBar);
        selectedTable.setTreeCellRenderer(qualifiedRenderer);
        selPanel.add(new JScrollPane(selectedTable), BorderLayout.CENTER);

        box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(5));
        box.add(removeButton);
        box.add(Box.createHorizontalGlue());
        box.setBorder(new EmptyBorder(0, 0, 5, 0));
        selPanel.add(box, BorderLayout.SOUTH);

        topPane.setRightComponent(selPanel);
        setBottomComponent(infoPane);
        setResizeWeight(0.75);
    }

    @Override
    public ModelMap createModels(CharacterFacade character)
    {
        ModelMap models = new ModelMap();
        models.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
        models.put(InfoHandler.class, new InfoHandler(character));
        models.put(AddAction.class, new AddAction(character));
        models.put(RemoveAction.class, new RemoveAction(character));
        models.put(Handler.class, qualifiedRenderer.createHandler(character));
        models.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
        return models;
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(QualifiedFilterHandler.class).install();
        models.get(Handler.class).install();
        models.get(TreeViewModelHandler.class).install();
        models.get(InfoHandler.class).install();
        models.get(AddAction.class).install();
        models.get(RemoveAction.class).install();
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(TreeViewModelHandler.class).uninstall();
        models.get(InfoHandler.class).uninstall();
        models.get(AddAction.class).uninstall();
        models.get(RemoveAction.class).uninstall();
        models.get(Handler.class).uninstall();
    }

    @Override
    public TabTitle getTabTitle()
    {
        return tabTitle;
    }

    private class InfoHandler implements ListSelectionListener
    {

        private final CharacterFacade character;
        private String text;

        public InfoHandler(CharacterFacade character)
        {
            this.character = character;
            this.text = ""; //$NON-NLS-1$
        }

        public void install()
        {
            availableTable.getSelectionModel().addListSelectionListener(this);
            selectedTable.getSelectionModel().addListSelectionListener(this);
            infoPane.setText(text);
        }

        public void uninstall()
        {
            availableTable.getSelectionModel().removeListSelectionListener(this);
            selectedTable.getSelectionModel().removeListSelectionListener(this);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            if (!e.getValueIsAdjusting())
            {
                Object obj = null;
                if (e.getSource() == availableTable.getSelectionModel())
                {
                    int selectedRow = availableTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        obj = availableTable.getModel().getValueAt(selectedRow, 0);
                    }
                } else
                {
                    int selectedRow = selectedTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        obj = selectedTable.getModel().getValueAt(selectedRow, 0);
                    }
                }
                if (obj instanceof PCTemplate)
                {
                    text = character.getInfoFactory().getHTMLInfo((PCTemplate) obj);
                    infoPane.setText(text);
                }
            }
        }

    }

    private class AddAction extends AbstractAction
    {

        private final CharacterFacade character;

        public AddAction(CharacterFacade character)
        {
            super(LanguageBundle.getString("in_irAddTemplate")); //$NON-NLS-1$
            this.character = character;
            putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            List<Object> data = availableTable.getSelectedData();
            for (Object object : data)
            {
                if (object instanceof PCTemplate)
                {
                    character.addTemplate((PCTemplate) object);
                    return;
                }
            }
        }

        public void install()
        {
            availableTable.addActionListener(this);
            addButton.setAction(this);
        }

        public void uninstall()
        {
            availableTable.removeActionListener(this);
        }

    }

    private class RemoveAction extends AbstractAction
    {

        private final CharacterFacade character;

        public RemoveAction(CharacterFacade character)
        {
            super(LanguageBundle.getString("in_irRemoveTemplate")); //$NON-NLS-1$
            this.character = character;
            putValue(SMALL_ICON, Icons.Back16.getImageIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            List<Object> data = selectedTable.getSelectedData();
            for (Object object : data)
            {
                if (object instanceof PCTemplate)
                {
                    character.removeTemplate((PCTemplate) object);
                    return;
                }
            }
        }

        public void install()
        {
            selectedTable.addActionListener(this);
            removeButton.setAction(this);
        }

        public void uninstall()
        {
            selectedTable.removeActionListener(this);
        }

    }

    private class QualifiedFilterHandler
    {

        private final Filter<CharacterFacade, PCTemplate> qFilter = new Filter<>()
        {
            @Override
            public boolean accept(CharacterFacade context, PCTemplate element)
            {
                return character.isQualifiedFor(element);
            }

        };
        private final CharacterFacade character;

        public QualifiedFilterHandler(CharacterFacade character)
        {
            this.character = character;
        }

        public void install()
        {
            qFilterButton.setFilter(qFilter);
        }

    }

    private class TreeViewModelHandler
    {

        private final TemplateDataView availDataView;
        private final TemplateDataView selDataView;
        private final TemplateTreeViewModel availTreeView;
        private final TemplateTreeViewModel selTreeView;

        public TreeViewModelHandler(CharacterFacade character)
        {
            availDataView = new TemplateDataView(character, true);
            selDataView = new TemplateDataView(character, false);
            availTreeView = new TemplateTreeViewModel(character, true, availDataView);
            selTreeView = new TemplateTreeViewModel(character, false, selDataView);
        }

        public void install()
        {
            availableTable.setTreeViewModel(availTreeView);
            selectedTable.setTreeViewModel(selTreeView);
        }

        public void uninstall()
        {
        }
    }

    private static class TemplateDataView extends CachedDataView<PCTemplate>
    {

        private final List<DefaultDataViewColumn> columns;
        private final InfoFactory infoFactory;
        private final boolean isAvailModel;

        public TemplateDataView(CharacterFacade character, boolean isAvailModel)
        {
            this.infoFactory = character.getInfoFactory();
            this.isAvailModel = isAvailModel;
            if (isAvailModel)
            {
                columns = Arrays.asList(new DefaultDataViewColumn("in_lvlAdj", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_modifier", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_preReqs", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
            } else
            {
                columns = Arrays.asList(new DefaultDataViewColumn("in_lvlAdj", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_modifier", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_preReqs", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
            }
        }

        @Override
        public List<? extends DataViewColumn> getDataColumns()
        {
            return columns;
        }

        @Override
        public String getPrefsKey()
        {
            return isAvailModel ? "TemplateTreeAvail" : "TemplateTreeSelected"; //$NON-NLS-1$//$NON-NLS-2$
        }

        @Override
        public Object getDataInternal(PCTemplate obj, int column)
        {
            switch (column)
            {
                case 0:
                    return infoFactory.getLevelAdjustment(obj);
                case 1:
                    return infoFactory.getModifier(obj);
                case 2:
                    return infoFactory.getPreReqHTML(obj);
                case 3:
                    return infoFactory.getDescription(obj);
                case 4:
                    return obj.getSource();
                default:
                    return null;
            }
        }

    }

    private static class TemplateTreeViewModel implements TreeViewModel<PCTemplate>,
            Filter<CharacterFacade, PCTemplate>, ListListener<PCTemplate>
    {

        private static final DefaultListFacade<? extends TreeView<PCTemplate>> TREE_VIEWS =
                new DefaultListFacade<>(Arrays.asList(TemplateTreeView.values()));
        private final CharacterFacade character;
        private final boolean isAvailModel;
        private final TemplateDataView dataView;
        private final FilteredListFacade<CharacterFacade, PCTemplate> templates;

        public TemplateTreeViewModel(CharacterFacade character, boolean isAvailModel, TemplateDataView dataView)
        {
            this.character = character;
            this.isAvailModel = isAvailModel;
            this.dataView = dataView;
            if (isAvailModel)
            {
                templates = new FilteredListFacade<>();
                templates.setContext(character);
                templates.setFilter(this);
                templates.setDelegate(character.getDataSet().getTemplates());
                character.getTemplates().addListListener(this);
            } else
            {
                templates = null;
            }
        }

        @Override
        public ListFacade<? extends TreeView<PCTemplate>> getTreeViews()
        {
            return TREE_VIEWS;
        }

        @Override
        public int getDefaultTreeViewIndex()
        {
            return 0;
        }

        @Override
        public DataView<PCTemplate> getDataView()
        {
            return dataView;
        }

        @Override
        public ListFacade<PCTemplate> getDataModel()
        {
            if (isAvailModel)
            {
                return templates;
            } else
            {
                return character.getTemplates();
            }
        }

        @Override
        public void elementAdded(ListEvent<PCTemplate> e)
        {
            templates.refilter();
        }

        @Override
        public void elementRemoved(ListEvent<PCTemplate> e)
        {
            templates.refilter();
        }

        @Override
        public void elementsChanged(ListEvent<PCTemplate> e)
        {
            templates.refilter();
        }

        @Override
        public void elementModified(ListEvent<PCTemplate> e)
        {
            templates.refilter();
        }

        @Override
        public boolean accept(CharacterFacade context, PCTemplate element)
        {
            return !context.getTemplates().containsElement(element);
        }

    }

    private enum TemplateTreeView implements TreeView<PCTemplate>
    {

        NAME("in_nameLabel"), //$NON-NLS-1$
        TYPE_NAME("in_typeName"), //$NON-NLS-1$
        SOURCE_NAME("in_sourceName"); //$NON-NLS-1$
        private final String name;

        private TemplateTreeView(String name)
        {
            this.name = LanguageBundle.getString(name);
        }

        @Override
        public String getViewName()
        {
            return name;
        }

        @Override
        public List<TreeViewPath<PCTemplate>> getPaths(PCTemplate pobj)
        {
            switch (this)
            {
                case NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj));
                case TYPE_NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getType()));
                case SOURCE_NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
                default:
                    throw new InternalError();
            }
        }

    }

}
