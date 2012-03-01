/*
 * ChooserDialog.java
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
 *
 * Created on 06/01/2012 9:23:01 AM
 *
 * $Id$
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

import pcgen.core.facade.ChooserFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.DelegatingListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.FacadeListModel;
import pcgen.gui2.util.JListEx;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;

/**
 * The Class <code>ChooserDialog</code> provides a general dialog to allow the 
 * user to select from a number of predefined choices. A ChooserFacade instance 
 * must be supplied, this defines the choices available, the text to be displayed 
 * on screen and the actions to be taken when the user confirms their choices. The 
 * chooser is generally displayed via a call to UIDelgate.showGeneralChooser.
 * <p>
 * This class is based heavily on Connor Petty's LanguageChooserDialog class.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class ChooserDialog extends JDialog implements ActionListener, ReferenceListener<Integer>
{

	private final ChooserFacade chooser;
	private final JTreeViewTable<InfoFacade> availTable;
	private final JLabel remainingLabel;
	private final GeneralTreeViewModel treeViewModel;
	private final FacadeListModel<InfoFacade> listModel;
	private final JListEx list;
	private boolean committed;

	/**
	 * Create a new instance of ChooserDialog for selecting from the data supplied in the chooserFacade. 
	 * @param frame The window we are opening relative to.
	 * @param chooser The definition of what should be displayed.
	 */
	public ChooserDialog(Frame frame, ChooserFacade chooser)
	{
		super(frame, true);
		this.chooser = chooser;
		this.availTable = new JTreeViewTable<InfoFacade>();
		this.remainingLabel = new JLabel();
		this.treeViewModel = new GeneralTreeViewModel();
		this.list = new JListEx();
		this.listModel = new FacadeListModel<InfoFacade>();

		treeViewModel.setDelegate(chooser.getAvailableList());
		listModel.setListFacade(chooser.getSelectedList());
		chooser.getRemainingSelections().addReferenceListener(this);
		initComponents();
		pack();
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
				chooser.getRemainingSelections().removeReferenceListener(ChooserDialog.this);
			}

		});
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		JSplitPane split = new JSplitPane();
		JPanel leftPane = new JPanel(new BorderLayout());
		//leftPane.add(new JLabel("Available Languages"), BorderLayout.NORTH);
		availTable.setTreeViewModel(treeViewModel);
		availTable.addActionListener(this);
		leftPane.add(new JScrollPane(availTable), BorderLayout.CENTER);

		JPanel buttonPane1 = new JPanel(new FlowLayout());
		JButton addButton = new JButton(chooser.getAddButtonName());
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
		labelPane.add(new JLabel(chooser.getSelectionCountName()), new GridBagConstraints());
		remainingLabel.setText(chooser.getRemainingSelections().getReference().toString());
		labelPane.add(remainingLabel, gbc);
		labelPane.add(new JLabel(chooser.getSelectedTableTitle()), gbc);
		rightPane.add(labelPane, BorderLayout.NORTH);

		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addActionListener(this);
		rightPane.add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel buttonPane2 = new JPanel(new FlowLayout());
		buttonPane2.add(new JLabel(Icons.Back16.getImageIcon()));
		JButton removeButton = new JButton(chooser.getRemoveButtonName());
		removeButton.setActionCommand("REMOVE");
		removeButton.addActionListener(this);
		buttonPane2.add(removeButton);
		rightPane.add(buttonPane2, BorderLayout.SOUTH);

		split.setRightComponent(rightPane);
		pane.add(split, BorderLayout.CENTER);
		JPanel bottomPane = new JPanel(new FlowLayout());
		JButton button = new JButton("Ok");
		button.setActionCommand("OK");
		button.addActionListener(this);
		bottomPane.add(button);
		button = new JButton("Cancel");
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
			if (!data.isEmpty() && data.get(0) instanceof InfoFacade)
			{
				chooser.addSelected((InfoFacade) data.get(0));
			}
			return;
		}
		if (e.getActionCommand().equals("REMOVE") || e.getSource() == list)
		{
			Object value = list.getSelectedValue();
			if (value != null && value instanceof InfoFacade)
			{
				chooser.removeSelected((InfoFacade) value);
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
		committed = e.getActionCommand().equals("OK");
		dispose();
	}

	/**
	 * Returns the means by which the dialog was closed.   
	 * @return the committed status, false for cancelled, true for OKed. 
	 */
	public boolean isCommitted()
	{
		return committed;
	}

	private class GeneralTreeViewModel extends DelegatingListFacade<InfoFacade> implements TreeViewModel<InfoFacade>,
			DataView<InfoFacade>, TreeView<InfoFacade>
	{

		@Override
		public ListFacade<? extends TreeView<InfoFacade>> getTreeViews()
		{
			return new DefaultListFacade<TreeView<InfoFacade>>(Collections.singletonList(this));
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<InfoFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<InfoFacade> getDataModel()
		{
			return this;
		}

		@Override
		public List<?> getData(InfoFacade obj)
		{
			return Collections.emptyList();
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return Collections.emptyList();
		}

		@Override
		public String getViewName()
		{
			return chooser.getAvailableTableTitle();
		}

		@Override
		public List<TreeViewPath<InfoFacade>> getPaths(InfoFacade pobj)
		{
			List<TreeViewPath<InfoFacade>> paths = new ArrayList<TreeViewPath<InfoFacade>>();
			for(String type : chooser.getBranchNames(pobj))
			{
				paths.add(new TreeViewPath<InfoFacade>(pobj, type));
			}
			if (paths.isEmpty())
			{
				paths.add(new TreeViewPath<InfoFacade>(pobj));
			}
			return paths;
		}

	}

}
