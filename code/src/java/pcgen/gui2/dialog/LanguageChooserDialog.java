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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
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
import pcgen.system.LanguageBundle;

public class LanguageChooserDialog extends JDialog implements ActionListener, ReferenceListener<Integer>
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
		availTable.addActionListener(this);
		leftPane.add(new JScrollPane(availTable), BorderLayout.CENTER);

		JPanel buttonPane1 = new JPanel(new FlowLayout());
		JButton addButton = new JButton(LanguageBundle.getString("in_sumLangAddLanguage")); //$NON-NLS-1$
		addButton.setActionCommand("ADD");
		addButton.addActionListener(this);
		buttonPane1.add(addButton);
		buttonPane1.add(new JLabel(Icons.Forward16.getImageIcon()));
		leftPane.add(buttonPane1, BorderLayout.SOUTH);

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
		rightPane.add(labelPane, BorderLayout.NORTH);

		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addActionListener(this);
		rightPane.add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel buttonPane2 = new JPanel(new FlowLayout());
		buttonPane2.add(new JLabel(Icons.Back16.getImageIcon()));
		JButton removeButton = new JButton(LanguageBundle.getString("in_sumLangRemoveLanguage")); //$NON-NLS-1$
		removeButton.setActionCommand("REMOVE");
		removeButton.addActionListener(this);
		buttonPane2.add(removeButton);
		rightPane.add(buttonPane2, BorderLayout.SOUTH);

		split.setRightComponent(rightPane);
		pane.add(split, BorderLayout.CENTER);
		JPanel bottomPane = new JPanel(new FlowLayout());
		JButton button = new JButton(LanguageBundle.getString("in_ok")); //$NON-NLS-1$
		button.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok")); //$NON-NLS-1$
		button.setActionCommand("OK");
		button.addActionListener(this);
		bottomPane.add(button);
		button = new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		button.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel")); //$NON-NLS-1$
		button.setActionCommand("CANCEL");
		button.addActionListener(this);
		bottomPane.add(button);
		pane.add(bottomPane, BorderLayout.SOUTH);
	}

	@Override
	public void referenceChanged(ReferenceEvent<Integer> e)
	{
		remainingLabel.setText(e.getNewReference().toString());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("ADD") || e.getSource() == availTable)
		{
			List<Object> data = availTable.getSelectedData();
			if (!data.isEmpty())
			{
				for (Object object : data)
				{
					if (object instanceof Language)
					{
						chooser.addSelected((Language) object);
					}
				}
			}
			return;
		}
		if (e.getActionCommand().equals("REMOVE") || e.getSource() == list)
		{
			Object value = list.getSelectedValue();
			if (value != null)
			{
				chooser.removeSelected((Language) value);
			}
			return;
		}
		if (e.getActionCommand().equals("OK"))
		{
			chooser.commit();
		}
		else
		{
			chooser.rollback();
		}
		dispose();
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
			List<TreeViewPath<Language>> paths = new ArrayList<>();
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				case TYPE_NAME:
					for (String type : getTypes(pobj))
					{
						paths.add(new TreeViewPath<>(pobj, type));
					}
					return paths;
				default:
					throw new InternalError();
			}
		}

		private List<String> getTypes(Language pobj)
		{
			List<String> typeList = new ArrayList<>();
			for (pcgen.cdom.enumeration.Type type : pobj.getTrueTypeList(false))
			{
				typeList.add(type.toString());
			}
			return typeList;
		}

	}
}
