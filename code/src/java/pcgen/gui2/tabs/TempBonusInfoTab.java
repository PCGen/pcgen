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
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.TempBonusFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer.Handler;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code TempBonusInfoTab} allows the user to select which
 * temporary bonus should be applied to their character.
 */
public class TempBonusInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

    /**
     * Version for serialisation.
     */
    private static final long serialVersionUID = 4521237435574462482L;

    private final TabTitle tabTitle = new TabTitle(Tab.TEMPBONUS);
    private final FilteredTreeViewTable<CharacterFacade, TempBonusFacade> availableTable;
    private final FilteredTreeViewTable<CharacterFacade, TempBonusFacade> selectedTable;
    private final JButton addButton;
    private final JButton removeButton;
    private final InfoPane infoPane;
    private final TempBonusRenderer tempBonusRenderer;

    /**
     * Create a new instance of TemporaryBonusInfoTab.
     */
    public TempBonusInfoTab()
    {
        this.availableTable = new FilteredTreeViewTable<>();
        this.selectedTable = new FilteredTreeViewTable<>();
        this.addButton = new JButton();
        this.removeButton = new JButton();
        this.infoPane = new InfoPane(LanguageBundle.getString("in_InfoTempMod")); //$NON-NLS-1$
        this.tempBonusRenderer = new TempBonusRenderer();
        initComponents();
    }

    private void initComponents()
    {
        FlippingSplitPane topPane = new FlippingSplitPane();
        setTopComponent(topPane);
        setOrientation(VERTICAL_SPLIT);

        JPanel availPanel = new JPanel(new BorderLayout());
        FilterBar<CharacterFacade, TempBonusFacade> bar = new FilterBar<>();
        bar.addDisplayableFilter(new SearchFilterPanel());
        availPanel.add(bar, BorderLayout.NORTH);

        availableTable.setDisplayableFilter(bar);
        availableTable.setTreeCellRenderer(tempBonusRenderer);
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
        FilterBar<CharacterFacade, TempBonusFacade> filterBar = new FilterBar<>();
        filterBar.addDisplayableFilter(new SearchFilterPanel());

        selectedTable.setDisplayableFilter(filterBar);
        selectedTable.setTreeCellRenderer(tempBonusRenderer);
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
        models.put(Handler.class, tempBonusRenderer.createHandler(character));
        return models;
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(Handler.class).install();
        models.get(TreeViewModelHandler.class).install();
        models.get(InfoHandler.class).install();
        models.get(AddAction.class).install();
        models.get(RemoveAction.class).install();
    }

    @Override
    public void storeModels(ModelMap models)
    {
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

    private static class TempBonusRenderer extends CharacterTreeCellRenderer
    {

        /**
         * Version for serialisation.
         */
        private static final long serialVersionUID = -9006249573217208478L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean focus)
        {
            Object obj = ((DefaultMutableTreeNode) value).getUserObject();
            if ("".equals(obj)) //$NON-NLS-1$
            {
                obj = LanguageBundle.getString("in_none"); //$NON-NLS-1$
            }
            super.getTreeCellRendererComponent(tree, obj, sel, expanded, leaf, row, focus);
            if (value instanceof TempBonusFacade && !character.isQualifiedFor((TempBonusFacade) value))
            {
                setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
            }
            if (value instanceof InfoFacade && ((InfoFacade) value).isNamePI())
            {
                setFont(FontManipulation.bold_italic(getFont()));
            } else
            {
                setFont(FontManipulation.plain(getFont()));
            }
            return this;
        }

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
                if (obj instanceof TempBonusFacade)
                {
                    text = character.getInfoFactory().getHTMLInfo((TempBonusFacade) obj);
                    infoPane.setText(text);
                }
            }
        }

    }

    private class AddAction extends AbstractAction
    {

        /**
         * Version for serialisation.
         */
        private static final long serialVersionUID = -6640460398947215666L;

        private final CharacterFacade character;

        public AddAction(CharacterFacade character)
        {
            super(LanguageBundle.getString("in_itmInitCompAppBonTitle")); //$NON-NLS-1$
            this.character = character;
            putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            List<Object> data = availableTable.getSelectedData();
            for (Object object : data)
            {
                if (object instanceof TempBonusFacade)
                {
                    character.addTempBonus((TempBonusFacade) object);
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

        /**
         * Version for serialisation.
         */
        private static final long serialVersionUID = 2922387838116495051L;

        private final CharacterFacade character;

        public RemoveAction(CharacterFacade character)
        {
            super(LanguageBundle.getString("in_itmInitCompRemoveButTitle")); //$NON-NLS-1$
            this.character = character;
            putValue(SMALL_ICON, Icons.Back16.getImageIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            List<Object> data = selectedTable.getSelectedData();
            for (Object object : data)
            {
                if (object instanceof TempBonusFacade)
                {
                    character.removeTempBonus((TempBonusFacade) object);
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

    private class TreeViewModelHandler
    {

        private final TempBonusTreeViewModel availableModel;
        private final TempBonusTreeViewModel selectedModel;

        public TreeViewModelHandler(CharacterFacade character)
        {
            availableModel = new TempBonusTreeViewModel(character, true);
            selectedModel = new TempBonusTreeViewModel(character, false);
        }

        public void install()
        {
            availableModel.install();
            availableTable.setTreeViewModel(availableModel);
            selectedModel.install();
            selectedTable.setTreeViewModel(selectedModel);
        }
    }

    private static class TempBonusTreeViewModel implements TreeViewModel<TempBonusFacade>, DataView<TempBonusFacade>,
            Filter<CharacterFacade, TempBonusFacade>, ListListener<TempBonusFacade>
    {

        private static final ListFacade<? extends TreeView<TempBonusFacade>> TREE_VIEWS =
                new DefaultListFacade<>(Arrays.asList(TempBonusTreeView.values()));
        private final List<DefaultDataViewColumn> columns;
        private final CharacterFacade character;
        private final InfoFactory infoFactory;
        private final boolean isAvailModel;
        private final FilteredListFacade<CharacterFacade, TempBonusFacade> tempBonuses;

        public TempBonusTreeViewModel(CharacterFacade character, boolean isAvailModel)
        {
            this.character = character;
            this.infoFactory = character.getInfoFactory();
            this.isAvailModel = isAvailModel;
            if (isAvailModel)
            {
                tempBonuses = new FilteredListFacade<>();
                tempBonuses.setContext(character);
                tempBonuses.setFilter(this);
                tempBonuses.setDelegate(character.getAvailableTempBonuses());
                character.getAvailableTempBonuses().addListListener(this);
                columns = Arrays.asList(new DefaultDataViewColumn("in_itmFrom", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_itmTarget", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
            } else
            {
                tempBonuses = null;
                columns = Arrays.asList(new DefaultDataViewColumn("in_itmFrom", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_itmTarget", String.class, true), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
            }
        }

        public void install()
        {
            for (TempBonusTreeView tbTreeView : TempBonusTreeView.values())
            {
                tbTreeView.setInfoFactory(infoFactory);
            }
        }

        @Override
        public ListFacade<? extends TreeView<TempBonusFacade>> getTreeViews()
        {
            return TREE_VIEWS;
        }

        @Override
        public int getDefaultTreeViewIndex()
        {
            return isAvailModel ? 1 : 0;
        }

        @Override
        public DataView<TempBonusFacade> getDataView()
        {
            return this;
        }

        @Override
        public ListFacade<TempBonusFacade> getDataModel()
        {
            if (isAvailModel)
            {
                return tempBonuses;
            } else
            {
                return character.getTempBonuses();
            }
        }

        @Override
        public Object getData(TempBonusFacade obj, int column)
        {
            switch (column)
            {
                case 0:
                    return obj.getOriginType();
                case 1:
                    return infoFactory.getTempBonusTarget(obj);
                case 2:
                    return infoFactory.getDescription(obj);
                case 3:
                    return obj.getSource();
                default:
                    return null;
            }
        }

        @Override
        public void setData(Object value, TempBonusFacade element, int column)
        {
        }

        @Override
        public List<? extends DataViewColumn> getDataColumns()
        {
            return columns;
        }

        @Override
        public void elementAdded(ListEvent<TempBonusFacade> e)
        {
            tempBonuses.refilter();
        }

        @Override
        public void elementRemoved(ListEvent<TempBonusFacade> e)
        {
            tempBonuses.refilter();
        }

        @Override
        public void elementsChanged(ListEvent<TempBonusFacade> e)
        {
            tempBonuses.refilter();
        }

        @Override
        public void elementModified(ListEvent<TempBonusFacade> e)
        {
            tempBonuses.refilter();
        }

        @Override
        public boolean accept(CharacterFacade context, TempBonusFacade element)
        {
            return !context.getTempBonuses().containsElement(element);
        }

        @Override
        public String getPrefsKey()
        {
            return isAvailModel ? "TempModsTreeAvail" : "TempModsTreeSelected"; //$NON-NLS-1$//$NON-NLS-2$
        }

    }

    private enum TempBonusTreeView implements TreeView<TempBonusFacade>
    {

        NAME("in_nameLabel"), //$NON-NLS-1$
        ORIGIN_NAME("in_itmOriginName"), //$NON-NLS-1$
        SOURCE_NAME("in_sourceName"), //$NON-NLS-1$
        TARGET_NAME("in_itmTargetName"); //$NON-NLS-1$

        private final String name;
        private InfoFactory infoFactory;

        private TempBonusTreeView(String name)
        {
            this.name = LanguageBundle.getString(name);
        }

        /**
         * @param factory The InfoFactory for the character ebing displayed.
         */
        public void setInfoFactory(InfoFactory factory)
        {
            this.infoFactory = factory;
        }

        @Override
        public String getViewName()
        {
            return name;
        }

        @Override
        public List<TreeViewPath<TempBonusFacade>> getPaths(TempBonusFacade bonus)
        {
            switch (this)
            {
                case TARGET_NAME:
                    if (infoFactory != null)
                    {
                        return Collections
                                .singletonList(new TreeViewPath<>(bonus, infoFactory.getTempBonusTarget(bonus)));
                    }
                    // No info factory? Treat as a name
                case NAME:
                    return Collections.singletonList(new TreeViewPath<>(bonus));
                case ORIGIN_NAME:
                    return Collections.singletonList(new TreeViewPath<>(bonus, bonus.getOriginType()));
                case SOURCE_NAME:
                    return Collections.singletonList(new TreeViewPath<>(bonus, bonus.getSourceForNodeDisplay()));
                default:
                    throw new InternalError();
            }
        }

    }

}
