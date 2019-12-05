/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.PCAlignment;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DomainFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.filter.FilteredListFacadeTableModel;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer.Handler;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.JDynamicTable;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.gui2.util.table.TableUtils;
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
 * This component handles deity and domain selection for a character.
 */
@SuppressWarnings("serial")
public class DomainInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler
{

    private final FilteredTreeViewTable<Object, Deity> deityTable;
    private final JDynamicTable domainTable;
    private final JTable domainRowHeaderTable;
    private final JLabel selectedDeity;
    private final JButton selectDeity;
    private final JLabel selectedDomain;
    private final InfoPane deityInfo;
    private final InfoPane domainInfo;
    private DisplayableFilter<CharacterFacade, DomainFacade> domainFilter;
    private final FilterButton<Object, Deity> qDeityButton;
    private final FilterButton<Object, DomainFacade> qDomainButton;
    private final QualifiedTreeCellRenderer qualifiedRenderer;

    public DomainInfoTab()
    {
        super();
        this.deityTable = new FilteredTreeViewTable<>();
        this.domainTable = new JDynamicTable();
        this.domainRowHeaderTable = TableUtils.createDefaultTable();
        this.selectedDeity = new JLabel();
        this.selectDeity = new JButton();
        this.selectedDomain = new JLabel();
        this.deityInfo = new InfoPane("in_deityInfo"); //$NON-NLS-1$
        this.domainInfo = new InfoPane("in_domainInfo"); //$NON-NLS-1$
        this.qDeityButton = new FilterButton<>("DeityQualified");
        this.qDomainButton = new FilterButton<>("DomainQualified");
        this.qualifiedRenderer = new QualifiedTreeCellRenderer();
        initComponents();
    }

    private void initComponents()
    {
        setOrientation(VERTICAL_SPLIT);

        deityTable.setTreeCellRenderer(qualifiedRenderer);
        JPanel panel = new JPanel(new BorderLayout());
        FilterBar<Object, Deity> bar = new FilterBar<>();
        bar.addDisplayableFilter(new SearchFilterPanel());
        qDeityButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
        bar.addDisplayableFilter(qDeityButton);
        deityTable.setDisplayableFilter(bar);
        panel.add(bar, BorderLayout.NORTH);

        ListSelectionModel selectionModel = deityTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(deityTable), BorderLayout.CENTER);

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(new JLabel(LanguageBundle.getString("in_domDeityLabel"))); //$NON-NLS-1$
        box.add(Box.createHorizontalStrut(5));
        box.add(selectedDeity);
        box.add(Box.createHorizontalStrut(5));
        box.add(selectDeity);
        box.add(Box.createHorizontalGlue());
        panel.add(box, BorderLayout.SOUTH);

        FlippingSplitPane splitPane = new FlippingSplitPane();
        splitPane.setLeftComponent(panel);

        panel = new JPanel(new BorderLayout());
        FilterBar<CharacterFacade, DomainFacade> dbar = new FilterBar<>();
        dbar.addDisplayableFilter(new SearchFilterPanel());
        qDomainButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
        dbar.addDisplayableFilter(qDomainButton);
        domainFilter = dbar;
        panel.add(dbar, BorderLayout.NORTH);

        selectionModel = domainTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        domainTable.setAutoCreateColumnsFromModel(false);
        domainTable.setColumnModel(createDomainColumnModel());

        JScrollPane scrollPane = TableUtils.createCheckBoxSelectionPane(domainTable, domainRowHeaderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(new JLabel(LanguageBundle.getString("in_domRemainDomLabel"))); //$NON-NLS-1$
        box.add(Box.createHorizontalStrut(5));
        box.add(selectedDomain);
        box.add(Box.createHorizontalGlue());

        panel.add(box, BorderLayout.SOUTH);

        splitPane.setRightComponent(panel);
        setTopComponent(splitPane);
        splitPane = new FlippingSplitPane();
        splitPane.setLeftComponent(deityInfo);
        splitPane.setRightComponent(domainInfo);
        setBottomComponent(splitPane);
        setResizeWeight(0.65);
    }

    public DynamicTableColumnModel createDomainColumnModel()
    {
        DefaultDynamicTableColumnModel model = new DefaultDynamicTableColumnModel(1);
        TableColumn column = new TableColumn(0);
        column.setHeaderValue(LanguageBundle.getString("in_domains")); //$NON-NLS-1$
        column.setPreferredWidth(150);
        model.addColumn(column);
        model.setVisible(column, true);
        column = new TableColumn(1);
        column.setHeaderValue(LanguageBundle.getString("in_descrip")); //$NON-NLS-1$
        column.setPreferredWidth(150);
        model.setVisible(column, false);
        column = new TableColumn(2);
        column.setHeaderValue(LanguageBundle.getString("in_source")); //$NON-NLS-1$
        column.setPreferredWidth(150);
        model.setVisible(column, false);
        return model;
    }

    @Override
    public ModelMap createModels(CharacterFacade character)
    {
        ModelMap models = new ModelMap();
        models.put(DeityTreeViewModel.class, new DeityTreeViewModel(character));
        models.put(DomainTableHandler.class, new DomainTableHandler(character));
        models.put(SelectDeityAction.class, new SelectDeityAction(character));
        models.put(DeityLabelHandler.class, new DeityLabelHandler(character, selectedDeity));
        models.put(DomainLabelHandler.class, new DomainLabelHandler(character, selectedDomain));
        models.put(DeityInfoHandler.class, new DeityInfoHandler(character));
        models.put(DomainInfoHandler.class, new DomainInfoHandler(character));
        models.put(DomainRenderer.class, new DomainRenderer(character));
        models.put(Handler.class, qualifiedRenderer.createHandler(character));
        models.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
        return models;
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(DomainLabelHandler.class).install();
        models.get(DeityLabelHandler.class).install();
        models.get(QualifiedFilterHandler.class).install();
        models.get(DomainTableHandler.class).install();
        models.get(DomainInfoHandler.class).install();
        models.get(DeityInfoHandler.class).install();
        models.get(DomainRenderer.class).install();
        models.get(SelectDeityAction.class).install();
        models.get(Handler.class).install();
        deityTable.setTreeViewModel(models.get(DeityTreeViewModel.class));
        selectDeity.setAction(models.get(SelectDeityAction.class));
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(DomainLabelHandler.class).uninstall();
        models.get(DeityLabelHandler.class).uninstall();
        models.get(DomainTableHandler.class).uninstall();
        models.get(DomainInfoHandler.class).uninstall();
        models.get(DeityInfoHandler.class).uninstall();
        models.get(SelectDeityAction.class).uninstall();
        models.get(Handler.class).uninstall();
    }

    @Override
    public TabTitle getTabTitle()
    {
        return new TabTitle(Tab.DOMAINS);
    }

    @Override
    public void adviseTodo(String fieldName)
    {
        if ("Domains".equals(fieldName)) //$NON-NLS-1$
        {
            if (domainTable.getRowCount() > 0)
            {
                domainTable.requestFocusInWindow();
                domainTable.getSelectionModel().setSelectionInterval(0, 0);
                deityTable.getSelectionModel().clearSelection();
            } else if (deityTable.getRowCount() > 0)
            {
                deityTable.requestFocusInWindow();
                deityTable.getSelectionModel().setSelectionInterval(0, 0);
            }
        }
    }

    private class DomainRenderer extends DefaultTableCellRenderer
    {

        private final CharacterFacade character;

        public DomainRenderer(CharacterFacade character)
        {
            this.character = character;
        }

        public void install()
        {
            domainTable.setDefaultRenderer(Object.class, this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof DomainFacade && !character.isQualifiedFor((DomainFacade) value))
            {
                setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
            } else if (!isSelected)
            {
                setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getQualifiedColor()));
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

    private class DeityInfoHandler implements ListSelectionListener
    {

        private final CharacterFacade character;
        private String text;

        public DeityInfoHandler(CharacterFacade character)
        {
            this.character = character;
            this.text = ""; //$NON-NLS-1$
        }

        public void install()
        {
            deityTable.getSelectionModel().addListSelectionListener(this);
            deityInfo.setText(text);
        }

        public void uninstall()
        {
            deityTable.getSelectionModel().removeListSelectionListener(this);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            if (!e.getValueIsAdjusting())
            {
                int selectedRow = deityTable.getSelectedRow();
                if (selectedRow != -1)
                {
                    Object obj = deityTable.getModel().getValueAt(selectedRow, 0);
                    if (obj instanceof Deity)
                    {
                        text = character.getInfoFactory().getHTMLInfo((Deity) obj);
                        deityInfo.setText(text);
                    }
                }
            }
        }

    }

    private class DomainInfoHandler implements ListSelectionListener
    {

        private final CharacterFacade character;
        private String text;

        public DomainInfoHandler(CharacterFacade character)
        {
            this.character = character;
            this.text = ""; //$NON-NLS-1$
        }

        public void install()
        {
            domainTable.getSelectionModel().addListSelectionListener(this);
            domainInfo.setText(text);
        }

        public void uninstall()
        {
            domainTable.getSelectionModel().removeListSelectionListener(this);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            if (!e.getValueIsAdjusting())
            {
                if (domainRowHeaderTable.isEditing())
                {
                    domainRowHeaderTable.getCellEditor().cancelCellEditing();
                }
                int selectedRow = domainTable.getSelectedRow();
                DomainFacade domain = null;
                if (selectedRow != -1)
                {
                    domain = (DomainFacade) domainTable.getModel().getValueAt(selectedRow, 0);
                }
                if (domain != null)
                {
                    text = character.getInfoFactory().getHTMLInfo(domain);
                    domainInfo.setText(text);
                }
            }
        }

    }

    private class SelectDeityAction extends AbstractAction
    {

        private final CharacterFacade character;

        public SelectDeityAction(CharacterFacade character)
        {
            super(LanguageBundle.getString("in_select")); //$NON-NLS-1$
            this.character = character;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int selectedRow = deityTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Object rowObj = deityTable.getModel().getValueAt(selectedRow, 0);
                if (rowObj instanceof Deity)
                {
                    Deity deity = (Deity) rowObj;
                    character.setDeity(deity);
                }
            }
        }

        public void install()
        {
            deityTable.addActionListener(this);
        }

        public void uninstall()
        {
            deityTable.removeActionListener(this);
        }

    }

    private class QualifiedFilterHandler
    {

        private final Filter<Object, DomainFacade> domainFilter = new Filter<>()
        {
            @Override
            public boolean accept(Object context, DomainFacade element)
            {
                return character.isQualifiedFor(element);
            }

        };
        private final Filter<Object, Deity> deityFilter = new Filter<>()
        {
            @Override
            public boolean accept(Object context, Deity element)
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
            qDomainButton.setFilter(domainFilter);
            qDeityButton.setFilter(deityFilter);
        }

    }

    private class DomainTableHandler implements FilterHandler
    {

        private final DomainTableModel tableModel;

        public DomainTableHandler(CharacterFacade character)
        {
            tableModel = new DomainTableModel(character);
        }

        public void install()
        {
            domainFilter.setFilterHandler(this);
            tableModel.setFilter(domainFilter);
            domainTable.setModel(tableModel);
            domainRowHeaderTable.setModel(tableModel);
        }

        public void uninstall()
        {
            tableModel.setFilter(null);
        }

        @Override
        public void refilter()
        {
            tableModel.refilter();
        }

        @Override
        public void scrollToTop()
        {
            // do nothing
        }

        @Override
        public void setSearchEnabled(boolean enable)
        {
        }

    }

    private static class DomainLabelHandler implements ReferenceListener<Integer>
    {

        private final JLabel label;
        private final ReferenceFacade<Integer> ref;

        public DomainLabelHandler(CharacterFacade character, JLabel label)
        {
            ref = character.getRemainingDomainSelectionsRef();
            this.label = label;
        }

        public void install()
        {
            if (ref.get() != null)
            {
                label.setText(ref.get().toString());
            }
            ref.addReferenceListener(this);
        }

        public void uninstall()
        {
            ref.removeReferenceListener(this);
        }

        @Override
        public void referenceChanged(ReferenceEvent<Integer> e)
        {
            label.setText(e.getNewReference().toString());
        }

    }

    private static class DeityLabelHandler implements ReferenceListener<Deity>
    {

        private final JLabel label;
        private final ReferenceFacade<Deity> ref;

        public DeityLabelHandler(CharacterFacade character, JLabel label)
        {
            ref = character.getDeityRef();
            this.label = label;
        }

        public void install()
        {
            label.setFont(FontManipulation.plain(label.getFont()));
            if (ref.get() != null)
            {
                label.setText(ref.get().toString());
                if (ref.get().isNamePI())
                {
                    label.setFont(FontManipulation.bold_italic(label.getFont()));
                }
            } else
            {
                label.setText(""); //$NON-NLS-1$
            }
            ref.addReferenceListener(this);
        }

        public void uninstall()
        {
            ref.removeReferenceListener(this);
        }

        @Override
        public void referenceChanged(ReferenceEvent<Deity> e)
        {
            label.setText(e.getNewReference().toString());
        }

    }

    private static class DomainTableModel extends FilteredListFacadeTableModel<DomainFacade>
    {

        private final ListListener<DomainFacade> listListener = new ListListener<>()
        {
            @Override
            public void elementAdded(ListEvent<DomainFacade> e)
            {
                elementsChanged(e);
            }

            @Override
            public void elementRemoved(ListEvent<DomainFacade> e)
            {
                elementsChanged(e);
            }

            @Override
            public void elementsChanged(ListEvent<DomainFacade> e)
            {
                fireTableRowsUpdated(0, sortedList.getSize() - 1);
            }

            @Override
            public void elementModified(ListEvent<DomainFacade> e)
            {
            }

        };

        public DomainTableModel(CharacterFacade character)
        {
            super(character);
            setDelegate(character.getAvailableDomains());
            character.getDomains().addListListener(listListener);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            if (columnIndex == -1)
            {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        protected Object getValueAt(DomainFacade element, int column)
        {
            switch (column)
            {
                case -1:
                    return character.getDomains().containsElement(element);
                case 0:
                    return element;
                case 1:
                    return character.getInfoFactory().getDescription(element);
                case 2:
                    return element.getSource();
                default:
                    return null;
            }
        }

        @Override
        public int getColumnCount()
        {
            return 3;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            if (columnIndex >= 0)
            {
                return false;
            }
            if (character.getRemainingDomainSelectionsRef().get() > 0)
            {
                return true;
            }
            DomainFacade domain = sortedList.getElementAt(rowIndex);
            return character.getDomains().containsElement(domain);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            DomainFacade domain = sortedList.getElementAt(rowIndex);
            Boolean bool = (Boolean) aValue;
            if (bool)
            {
                character.addDomain(domain);
            } else
            {
                character.removeDomain(domain);
            }
        }

    }

    private static class DeityTreeViewModel implements TreeViewModel<Deity>, DataView<Deity>
    {

        private static final ListFacade<TreeView<Deity>> VIEWS =
                new DefaultListFacade<>(Arrays.asList(DeityTreeView.values()));
        private final List<DefaultDataViewColumn> columns =
                Arrays.asList(new DefaultDataViewColumn("in_alignLabel", Object.class), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_domains", String.class), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_descrip", String.class), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_pantheon", String.class), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_favoredWeapon", String.class), //$NON-NLS-1$
                        new DefaultDataViewColumn("in_sourceLabel", String.class)); //$NON-NLS-1$
        private final CharacterFacade character;
        private final InfoFactory infoFactory;

        public DeityTreeViewModel(CharacterFacade character)
        {
            this.character = character;
            this.infoFactory = character.getInfoFactory();
        }

        @Override
        public ListFacade<? extends TreeView<Deity>> getTreeViews()
        {
            return VIEWS;
        }

        @Override
        public int getDefaultTreeViewIndex()
        {
            return 0;
        }

        @Override
        public DataView<Deity> getDataView()
        {
            return this;
        }

        @Override
        public ListFacade<Deity> getDataModel()
        {
            return character.getDataSet().getDeities();
        }

        @Override
        public Object getData(Deity obj, int column)
        {
            switch (column)
            {
                case 0:
                    return getAlignment(obj);
                case 1:
                    return infoFactory.getDomains(obj);
                case 2:
                    return infoFactory.getDescription(obj);
                case 3:
                    return infoFactory.getPantheons(obj);
                case 4:
                    return infoFactory.getFavoredWeapons(obj);
                case 5:
                    return obj.getSource();
                default:
                    return null;
            }
        }

        @Override
        public void setData(Object value, Deity element, int column)
        {
        }

        @Override
        public List<? extends DataViewColumn> getDataColumns()
        {
            return columns;
        }

        @Override
        public String getPrefsKey()
        {
            return "DeityTree"; //$NON-NLS-1$
        }

    }

    private enum DeityTreeView implements TreeView<Deity>
    {

        NAME("in_deity"), //$NON-NLS-1$
        ALIGNMENT_NAME("in_alignmentDeity"), //$NON-NLS-1$
        DOMAIN_NAME("in_domainDeity"), //$NON-NLS-1$
        PANTHEON_NAME("in_pantheonDeity"), //$NON-NLS-1$
        SOURCE_NAME("in_sourceDeity"); //$NON-NLS-1$
        private final String name;

        private DeityTreeView(String name)
        {
            this.name = LanguageBundle.getString(name);
        }

        @Override
        public String getViewName()
        {
            return name;
        }

        @Override
        public List<TreeViewPath<Deity>> getPaths(Deity pobj)
        {
            List<TreeViewPath<Deity>> paths = new ArrayList<>();
            switch (this)
            {
                case NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj));
                case DOMAIN_NAME:
                    for (String domain : getDomainNames(pobj))
                    {
                        paths.add(new TreeViewPath<>(pobj, domain));
                    }
                    return paths;
                case ALIGNMENT_NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj, getAlignment(pobj)));
                case PANTHEON_NAME:
                    for (String pantheon : getPantheons(pobj))
                    {
                        paths.add(new TreeViewPath<>(pobj, pantheon));
                    }
                    return paths;
                case SOURCE_NAME:
                    return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
                default:
                    throw new InternalError();
            }

        }

        public List<String> getDomainNames(Deity pobj)
        {
            List<String> domains = new ArrayList<>();
            for (CDOMReference<Domain> ref : pobj.getSafeListMods(Deity.DOMAINLIST))
            {
                for (Domain d : ref.getContainedObjects())
                {
                    domains.add(String.valueOf(d));
                }
            }
            return domains;
        }

        private Collection<String> getPantheons(Deity pobj)
        {
            Set<String> charDeityPantheon = new TreeSet<>();
            FactSetKey<String> fk = FactSetKey.valueOf("Pantheon");
            for (Indirect<String> indirect : pobj.getSafeSetFor(fk))
            {
                charDeityPantheon.add(indirect.get());
            }
            return charDeityPantheon;
        }

    }

    private static PCAlignment getAlignment(Deity pobj)
    {
        CDOMSingleRef<PCAlignment> ref = pobj.get(ObjectKey.ALIGNMENT);
        if (ref == null)
        {
            return null;
        }
        return ref.get();
    }

}
