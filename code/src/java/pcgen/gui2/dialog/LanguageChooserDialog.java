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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import pcgen.core.Language;
import pcgen.facade.core.LanguageChooserFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DelegatingListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FacadeListModel;
import pcgen.gui2.util.JListEx;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.ImageView;

public final class LanguageChooserDialog extends JDialog implements ReferenceListener<Integer>
{

	private final LanguageChooserFacade chooser;
	private final JTreeViewTable<Language> availTable;
	private final JLabel remainingLabel;
	private final LangTreeViewModel treeViewModel;
	private final FacadeListModel<Language> listModel;
	private final JListEx<Language> list;

	public LanguageChooserDialog(Frame frame, LanguageChooserFacade chooser)
	{
		super(frame, true);
		this.chooser = chooser;
		this.availTable = new JTreeViewTable<>();
		this.remainingLabel = new JLabel();
		this.treeViewModel = new LangTreeViewModel();
		this.list = new JListEx<>();
		this.listModel = new FacadeListModel<>();

		treeViewModel.setDelegate(chooser.getAvailableList());
		listModel.setListFacade(chooser.getSelectedList());
		chooser.getRemainingSelections().addReferenceListener(this);
		initComponents();
		pack();
		Utility.installEscapeCloseOperation(this);
	}

	private void initComponents()
	{
		setTitle(chooser.getName());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosed(WindowEvent e)
			{
				//detach listeners from the chooser
				treeViewModel.setDelegate(null);
				listModel.setListFacade(null);
				chooser.getRemainingSelections().removeReferenceListener(LanguageChooserDialog.this);
			}

		});
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		JSplitPane split = new JSplitPane();
		JPanel leftPane = new JPanel(new BorderLayout());
		//leftPane.add(new JLabel("Available Languages"), BorderLayout.NORTH);
		availTable.setAutoCreateRowSorter(true);
		availTable.setTreeViewModel(treeViewModel);
		availTable.getRowSorter().toggleSortOrder(0);
		availTable.addActionListener(new DoubleClickActionListener());
		leftPane.add(new JScrollPane(availTable), BorderLayout.CENTER);

		Button addButton = new Button(LanguageBundle.getString("in_sumLangAddLanguage"));
		addButton.setOnAction(this::doAdd);
		addButton.setGraphic(new ImageView(Icons.Forward16.asJavaFX()));
		leftPane.add(GuiUtility.wrapParentAsJFXPanel(addButton), BorderLayout.PAGE_END);

		split.setLeftComponent(leftPane);

		JPanel rightPane = new JPanel(new BorderLayout());
		JPanel labelPane = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		labelPane.add(new JLabel(LanguageBundle.getString("in_sumLangRemain")), //$NON-NLS-1$
			new GridBagConstraints());
		remainingLabel.setText(chooser.getRemainingSelections().get().toString());
		labelPane.add(remainingLabel, gbc);
		labelPane.add(new JLabel(LanguageBundle.getString("in_sumSelectedLang")), gbc); //$NON-NLS-1$
		rightPane.add(labelPane, BorderLayout.PAGE_START);

		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addActionListener(new DoubleClickActionListener());
		rightPane.add(new JScrollPane(list), BorderLayout.CENTER);

		Button removeButton = new Button(LanguageBundle.getString("in_sumLangRemoveLanguage"));
		removeButton.setOnAction(this::doRemove);
		removeButton.setGraphic(new ImageView(Icons.Back16.asJavaFX()));
		rightPane.add(GuiUtility.wrapParentAsJFXPanel(removeButton), BorderLayout.PAGE_END);

		split.setRightComponent(rightPane);
		pane.add(split, BorderLayout.CENTER);
		ButtonBar buttonBar = new OKCloseButtonBar(
				this::doOK,
				this::doRollback
		);
		pane.add(GuiUtility.wrapParentAsJFXPanel(buttonBar), BorderLayout.PAGE_END);
	}

	@Override
	public void referenceChanged(ReferenceEvent<Integer> e)
	{
		remainingLabel.setText(e.getNewReference().toString());
	}

	private void doAdd(final javafx.event.ActionEvent actionEvent)
	{
		List<Object> data = availTable.getSelectedData();
		if (!data.isEmpty())
		{
			data.stream()
			    .filter(object -> object instanceof Language)
			    .map(object -> (Language) object)
			    .forEach(chooser::addSelected);
		}
	}

	private void doRemove(final javafx.event.ActionEvent actionEvent)
	{
		Object value = list.getSelectedValue();
		if (value != null)
		{
			chooser.removeSelected((Language) value);
		}
	}

	private void doOK(final javafx.event.ActionEvent actionEvent)
	{
		chooser.commit();
		dispose();
	}

	private void doRollback(final javafx.event.ActionEvent actionEvent)
	{
		chooser.rollback();
		dispose();
	}


	private class DoubleClickActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == availTable)
			{
				doAdd(null);
			}
			else if (e.getSource() == list)
			{
				doRemove(null);
			}
		}
	}

	private static class LangTreeViewModel extends DelegatingListFacade<Language>
			implements TreeViewModel<Language>, DataView<Language>//, TreeView<LanguageFacade>
	{
		private static final ListFacade<TreeView<Language>> VIEWS =
				new DefaultListFacade<>(Arrays.asList(LanguageTreeView.values()));

		@Override
		public ListFacade<? extends TreeView<Language>> getTreeViews()
		{
			return VIEWS;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<Language> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<Language> getDataModel()
		{
			return this;
		}

		@Override
		public Object getData(Language element, int column)
		{
			return null;
		}

		@Override
		public void setData(Object value, Language element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return Collections.emptyList();
		}

		@Override
		public String getPrefsKey()
		{
			return LanguageBundle.getString("in_sumLangAvailable"); //$NON-NLS-1$;
		}

	}

	private enum LanguageTreeView implements TreeView<Language>
	{
		NAME("in_nameLabel"), //$NON-NLS-1$
		TYPE_NAME("in_typeName"); //$NON-NLS-1$

		private final String name;

		private LanguageTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<Language>> getPaths(Language pobj)
		{
			return switch (this)
					{
						case NAME -> Collections.singletonList(new TreeViewPath<>(pobj));
						case TYPE_NAME -> pobj.getTrueTypeList(false)
						                      .stream()
						                      .map(pcgen.cdom.enumeration.Type::toString)
						                      .map(type -> new TreeViewPath<>(pobj, type))
						                      .collect(Collectors.toUnmodifiableList());
						default -> throw new InternalError();
					};
		}

	}
}
