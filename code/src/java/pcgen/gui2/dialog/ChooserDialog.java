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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.core.chooser.InfoWrapper;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DelegatingListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
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
import pcgen.system.PropertyContext;

import javafx.scene.control.ButtonBar;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code ChooserDialog} provides a general dialog to allow the
 * user to select from a number of predefined choices. A ChooserFacade instance 
 * must be supplied, this defines the choices available, the text to be displayed 
 * on screen and the actions to be taken when the user confirms their choices. The 
 * chooser is generally displayed via a call to UIDelgate.showGeneralChooser.
 * <p>
 * This class is based heavily on Connor Petty's LanguageChooserDialog class.
 *
 * 
 */
public class ChooserDialog extends JDialog implements ActionListener, ReferenceListener<Integer>, ListSelectionListener
{

	private final ChooserFacade chooser;
	private final JTreeViewTable<InfoFacade> availTable;
	private final JTextField availInput;
	private final JLabel remainingLabel;
	private final GeneralTreeViewModel treeViewModel;
	private final FacadeListModel<InfoFacade> listModel;
	private final JListEx list;
	private final InfoPane infoPane;
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
		if (chooser.isUserInput())
		{
			this.availTable = null;
			this.availInput = new JTextField(20);
		}
		else
		{
			this.availTable = new JTreeViewTable<>();
			this.availInput = null;
		}
		this.remainingLabel = new JLabel();
		this.treeViewModel = new GeneralTreeViewModel();
		this.list = new JListEx();
		this.listModel = new FacadeListModel<>();
		this.infoPane = new InfoPane();

		treeViewModel.setDelegate(chooser.getAvailableList());
		listModel.setListFacade(chooser.getSelectedList());
		chooser.getRemainingSelections().addReferenceListener(this);
		overridePrefs();
		initComponents();
		pack();
	}

	@Override
	public void setVisible(boolean b)
	{
		//
		// Only do this if 1 entry and can add...
		//
		ListFacade<InfoFacade> availableList = chooser.getAvailableList();
		if ((availableList != null) && (availableList.getSize() == 1) && (listModel.getSize() == 0) && b
			&& !chooser.isUserInput())
		{
			final int method = UIPropertyContext.getSingleChoiceAction();

			if (method != Constants.CHOOSER_SINGLE_CHOICE_METHOD_NONE)
			{
				chooser.addSelected(availableList.getElementAt(0));

				if (method == Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT)
				{
					chooser.commit();
					committed = true;
					return;
				}
			}
		}

		super.setVisible(b);
	}

	/**
	 * We don't want some things recalled in preferences (e.g. tree sorting) as they
	 * aren't the same for all choose data. Ensure we put out desired values in first.  
	 */
	private void overridePrefs()
	{
		UIPropertyContext baseContext = UIPropertyContext.createContext("tablePrefs");
		PropertyContext context = baseContext.createChildContext(treeViewModel.getDataView().getPrefsKey());
		final String VIEW_INDEX_PREFS_KEY = "viewIdx";
		context.setInt(VIEW_INDEX_PREFS_KEY, treeViewModel.getDefaultTreeViewIndex());
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
		if (availTable != null)
		{
			availTable.setAutoCreateRowSorter(true);
			availTable.setTreeViewModel(treeViewModel);
			availTable.getRowSorter().toggleSortOrder(0);
			availTable.addActionListener(this);
			leftPane.add(new JScrollPane(availTable), BorderLayout.CENTER);
		}
		else
		{
			availInput.addActionListener(this);
			Dimension maxDim = new Dimension(Integer.MAX_VALUE, availInput.getPreferredSize().height);
			availInput.setMaximumSize(maxDim);
			JPanel availPanel = new JPanel();
			availPanel.setLayout(new BoxLayout(availPanel, BoxLayout.PAGE_AXIS));
			availPanel.add(Box.createRigidArea(new Dimension(10, 30)));
			availPanel.add(Box.createVerticalGlue());
			availPanel.add(new JLabel(LanguageBundle.getString("in_uichooser_value")));
			availPanel.add(availInput);
			availPanel.add(Box.createVerticalGlue());
			leftPane.add(availPanel, BorderLayout.WEST);
		}

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
		remainingLabel.setText(chooser.getRemainingSelections().get().toString());
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

		if (chooser.isInfoAvailable())
		{
			JSplitPane infoSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			infoSplit.setTopComponent(split);
			infoSplit.setBottomComponent(infoPane);
			infoSplit.setResizeWeight(0.8);
			pane.add(infoSplit, BorderLayout.CENTER);
			if (availTable != null)
			{
				availTable.getSelectionModel().addListSelectionListener(this);
			}
		}
		else
		{
			pane.add(split, BorderLayout.CENTER);
		}

		ButtonBar okCloseButtonBar = new OKCloseButtonBar(
				this::doOK,
				this::doCancel
		);

		pane.add(GuiUtility.wrapParentAsJFXPanel(okCloseButtonBar), BorderLayout.PAGE_END);
	}

	@Override
	public void referenceChanged(ReferenceEvent<Integer> e)
	{
		remainingLabel.setText(e.getNewReference().toString());
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (availTable != null && !e.getValueIsAdjusting())
		{
			if (e.getSource() == availTable.getSelectionModel() && availTable.getSelectedObject() instanceof InfoFacade)
			{
				InfoFacade target = (InfoFacade) availTable.getSelectedObject();
				InfoFactory factory = chooser.getInfoFactory();
				if (factory != null && target != null)
				{
					infoPane.setText(factory.getHTMLInfo(target));
				}
			}
		}
	}

	private void doOK(final javafx.event.ActionEvent event)
	{
		if (chooser.isRequireCompleteSelection() && chooser.getRemainingSelections().get() > 0)
		{
			JOptionPane.showMessageDialog(this,
					LanguageBundle.getFormattedString("in_chooserRequireComplete", //$NON-NLS-1$
							chooser.getRemainingSelections().get()), chooser.getName(), JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			committed = true;
			chooser.commit();
			dispose();
		}

	}

	private void doCancel(final javafx.event.ActionEvent event)
	{
		committed = false;
		chooser.rollback();
		dispose();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (availTable != null && (e.getActionCommand().equals("ADD") || e.getSource() == availTable))
		{
			List<Object> data = availTable.getSelectedData();
			if (!data.isEmpty())
			{
				for (Object object : data)
				{
					if (object instanceof InfoFacade)
					{
						chooser.addSelected((InfoFacade) object);
					}
				}
			}
			return;
		}
		if (availInput != null && (e.getActionCommand().equals("ADD") || e.getSource() == availInput))
		{
			String data = availInput.getText();
			if (StringUtils.isNotBlank(data))
			{
				chooser.addSelected(new InfoWrapper(data));
			}
			availInput.setText("");
			return;
		}

		if (e.getActionCommand().equals("REMOVE") || e.getSource() == list)
		{
			Object value = list.getSelectedValue();
			if (value != null && value instanceof InfoFacade)
			{
				chooser.removeSelected((InfoFacade) value);
				if (availInput != null)
				{
					availInput.setText(value.toString());
				}
			}
			return;
		}
	}

	/**
	 * Returns the means by which the dialog was closed.   
	 * @return the committed status, false for cancelled, true for OKed. 
	 */
	public boolean isCommitted()
	{
		return committed;
	}

	private class GeneralTreeViewModel extends DelegatingListFacade<InfoFacade>
			implements TreeViewModel<InfoFacade>, DataView<InfoFacade>
	{

		@Override
		public ListFacade<? extends TreeView<InfoFacade>> getTreeViews()
		{
			DefaultListFacade<TreeView<InfoFacade>> views = new DefaultListFacade<>();
			views.addElement(new ChooserTreeView(ChooserTreeViewType.NAME, chooser.getAvailableTableTitle(), chooser));
			views.addElement(
                    new ChooserTreeView(ChooserTreeViewType.TYPE_NAME, chooser.getAvailableTableTypeNameTitle(), chooser));
			return views;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return chooser.getDefaultView().ordinal();
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
		public Object getData(InfoFacade element, int column)
		{
			return null;
		}

		@Override
		public void setData(Object value, InfoFacade element, int column)
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
			return chooser.getAvailableTableTypeNameTitle();
		}

	}

	private static final class ChooserTreeView implements TreeView<InfoFacade>
	{

		private final String viewName;
		private final ChooserFacade chooser;
		private final ChooserTreeViewType viewType;

		private ChooserTreeView(ChooserTreeViewType viewType, String name, ChooserFacade chooser)
		{
			this.viewType = viewType;
			this.viewName = name;
			this.chooser = chooser;
		}

		@Override
		public String getViewName()
		{
			return viewName;
		}

		@Override
		public List<TreeViewPath<InfoFacade>> getPaths(InfoFacade pobj)
		{
			switch (viewType)
			{
				case TYPE_NAME:
					List<TreeViewPath<InfoFacade>> paths = new ArrayList<>();
					for (String type : chooser.getBranchNames(pobj))
					{
						paths.add(new TreeViewPath<>(pobj, type));
					}
					if (!paths.isEmpty())
					{
						return paths;
					}
					// Otherwise treat as a name entry
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				default:
					throw new InternalError();
			}
		}
	}

}
