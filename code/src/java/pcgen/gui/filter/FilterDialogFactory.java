/*
 * FilterDialogFactory.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 23, 2002, 5:30 PM
 */
package pcgen.gui.filter;

import pcgen.cdom.base.Constants;
import pcgen.core.*;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.PToolBar;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * <code>FilterDialogFactory</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class FilterDialogFactory
{
	private static final String in_selectFilter = LanguageBundle.getString("in_selectFilter");
	private static final String in_curSelFil = LanguageBundle.getString("in_curSelFil");
	private static final String in_curNoSelFil = LanguageBundle.getString("in_curNoSelFil");
	private static final String in_curActTab = LanguageBundle.getString("in_curActTab");
	private static final String in_matchAllOf = LanguageBundle.getString("in_matchAllOf");
	private static final String in_matchNotAllOf = LanguageBundle.getString("in_matchNotAllOf");
	private static final String in_matchAnyOf = LanguageBundle.getString("in_matchAnyOf");
	private static final String in_matchNotAnyOf = LanguageBundle.getString("in_matchNotAnyOf");
	private static FilterSelectDialog filterSelectDialog = null;
	private static FilterCustomDialog filterCustomDialog = null;
	private static FilterEditorDialog filterEditorDialog = null;

	/**
	 * convenience method
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 * @return tool tip text
	 */
	public static String getSelectedFiltersToolTipText()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html>");
		buffer.append("<font face=\"Dialog\" size=\"2\">");
		buffer.append(in_selectFilter);

		Filterable f = PCGen_Frame1.getCurrentFilterable();

		if (f != null)
		{
			if (f.getSelectedFilters().size() > 0)
			{
				buffer.append("<br>").append(in_curSelFil);

				int mode = f.getFilterMode();

				if (mode == FilterConstants.MATCH_ALL)
				{
					buffer.append("<br>").append(in_matchAllOf);
				}
				else if (mode == FilterConstants.MATCH_ALL_NEGATE)
				{
					buffer.append("<br>").append(in_matchNotAllOf);
				}
				else if (mode == FilterConstants.MATCH_ANY)
				{
					buffer.append("<br>").append(in_matchAnyOf);
				}
				else if (mode == FilterConstants.MATCH_ANY_NEGATE)
				{
					buffer.append("<br>").append(in_matchNotAnyOf);
				}

				buffer.append("<ul>");

				for (Iterator it = f.getSelectedFilters().iterator(); it.hasNext();)
				{
					buffer.append("<li>");
					buffer.append(it.next().toString());
					buffer.append("</li>");
				}

				buffer.append("</ul>");
			}
			else
			{
				buffer.append("<br>").append(in_curNoSelFil);
			}
		}
		else
		{
			buffer.append("<br>").append(in_curActTab);
		}

		buffer.append("</font>");
		buffer.append("</html>");

		return buffer.toString();
	}

	/**
	 * convenience method<br>
	 * clears the selected (and therefore active) filters
	 * for the selected (and therefore active) Filterable (for example, tab)
	 * 
	 * <br>author: Thomas Behr 09-02-02
	 */
	public static void clearSelectedFiltersForSelectedFilterable()
	{
		Filterable f = PCGen_Frame1.getCurrentFilterable();

		if (f != null)
		{
			f.getAvailableFilters().addAll(f.getSelectedFilters());
			f.getSelectedFilters().clear();
			PToolBar.getCurrentInstance().setFilterInactive();
			f.refreshFiltering();

			// if FilterSelectDialog is showing, we want it updated accordingly
			FilterSelectDialog fsd = getCurrentSelectDialogInstance();

			if (fsd.isShowing())
			{
				fsd.clearSelectedFilters();
			}
		}
	}

	/**
	 * convenience method
	 * <p/>
	 * <br>author: Thomas Behr
	 */
	public static void showHideFilterCustomDialog()
	{
		if (filterCustomDialog == null)
		{
			filterCustomDialog = new FilterCustomDialog();
		}

		if (!filterCustomDialog.isShowing())
		{
			filterCustomDialog.setVisible(true);
		}
		else
		{
			filterCustomDialog.setVisible(false);
		}
	}

	/**
	 * convenience method
	 * <p/>
	 * <br>author: Thomas Behr
	 */
	public static void showHideFilterEditorDialog()
	{
		if (filterEditorDialog == null)
		{
			filterEditorDialog = new FilterEditorDialog();
		}

		if (!filterEditorDialog.isShowing())
		{
			filterEditorDialog.setVisible(true);
		}
		else
		{
			filterEditorDialog.setVisible(false);
		}
	}

	/**
	 * convenience method
	 * <p/>
	 * <br>author: Thomas Behr
	 */
	public static void showHideFilterSelectDialog()
	{
		if (filterSelectDialog == null)
		{
			filterSelectDialog = new FilterSelectDialog();
		}

		if (!filterSelectDialog.isShowing())
		{
			filterSelectDialog.setVisible(true);
		}
		else
		{
			filterSelectDialog.setVisible(false);
		}
	}

	/**
	 * this allows us to statically access the last created
	 * instance of FilterSelectDialog and set its filters
	 * <p/>
	 * <br>author: Thomas Behr 23-02-02
	 *
	 * @param availableFilters a List of strings holding names of available filters;<br>
	 *                         if availableFilters is null, then there will be
	 *                         no items in the available list
	 * @param selectedFilters  a List of strings holding names of selected filters;<br>
	 *                         if selectedFilters is null, then there will be
	 *                         no items in the selected list
	 */
	static void setFilters(List availableFilters, List selectedFilters)
	{
		getCurrentSelectDialogInstance().setFilters(availableFilters, selectedFilters);
	}

	/**
	 * this allows us to statically access the last created instance of FilterSelectDialog
	 * <p/>
	 * <br>author: Thomas Behr 12-02-02
	 *
	 * @return the current, i.e. the one that was created last,
	 *         instance of FilterSelectDialog
	 */
	private static FilterSelectDialog getCurrentSelectDialogInstance()
	{
		// this will probably never happen
		if (filterSelectDialog == null)
		{
			filterSelectDialog = new FilterSelectDialog();
		}

		return filterSelectDialog;
	}
}

/**
 * <code>FilterSelectDialog</code>
 *
 * @author Thomas Behr
 */
final class FilterSelectDialog extends JDialog implements ActionListener
{
	static final long serialVersionUID = -7786319324648194024L;
	private static final PObjectFilter displayOnlyFilter = new PObjectFilter()
	{
		public String getCategory()
		{
			return LanguageBundle.getString("in_demo");
		}

		public String getDescription()
		{
			return getName();
		}

		public String getDescription(PlayerCharacter aPC)
		{
			return getName(aPC);
		}

		public String getName()
		{
			return LanguageBundle.getString("in_actTabNot");
		}

		public String getName(PlayerCharacter aPC)
		{
			return LanguageBundle.getString("in_actTabNot");
		}

		@Override
		public String toString()
		{
			return getName();
		}

		public boolean accept(PlayerCharacter aPC, PObject pObject)
		{
			return false;
		}
	};

	private FilterList availableList;
	private FilterList selectedList;
	private final FilterNameDialog filterNameDialog = new FilterNameDialog();
	private Filterable filterable;
	private JButton addButton;
	private JButton applyButton;
	private JButton cancelButton;
	private JButton removeButton;
	private JButton saveButton;
	private JCheckBox negateBox;
	private JRadioButton matchAllRadio;
	private JRadioButton matchAnyRadio;
	private SortedListModel availableModel;
	private SortedListModel selectedModel;
	private int maxSelected;

	FilterSelectDialog()
	{
		super(Globals.getRootFrame(), LanguageBundle.getString("in_filOpt"));
		init();
	}

	/**
	 * set the possible filternames
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List of strings holding names of available filters;<br>
	 *                         if availableFilters is null, then there will be
	 *                         no items in the available list
	 * @param selectedFilters  a List of strings holding names of selected filters;<br>
	 *                         if selectedFilters is null, then there will be
	 *                         no items in the selected list
	 */
	public void setFilters(List availableFilters, List selectedFilters)
	{
		availableModel.clear();
		selectedModel.clear();

		if (availableFilters != null)
		{
			for (Iterator it = availableFilters.iterator(); it.hasNext();)
			{
				availableModel.addElement(it.next());
			}
		}

		if (selectedFilters != null)
		{
			for (Iterator it = selectedFilters.iterator(); it.hasNext();)
			{
				selectedModel.addElement(it.next());
			}
		}
	}

	/**
	 * implementation of ActionListener interface
	 * <p/>
	 * <br>author: Thomas Behr
	 *
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if (src.equals(addButton))
		{
			final int[] indices = availableList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				// take into account that the list shrinks by one
				// with each iteration;
				// therefore we subtract from the position index
				// how many elements (i) we've removed already
				selectedModel.addElement(availableModel.get(indices[i] - i));
				availableModel.removeElementAt(indices[i] - i);
			}

			if (selectedModel.size() == maxSelected)
			{
				addButton.setEnabled(false);
			}
		}
		else if (src.equals(removeButton))
		{
			final int[] indices = selectedList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				// take into account that the list shrinks by one
				// with each iteration;
				// therefore we subtract from the position index
				// how many elements (i) we've removed already
				availableModel.addElement(selectedModel.get(indices[i] - i));
				selectedModel.removeElementAt(indices[i] - i);
			}

			if (selectedModel.size() < maxSelected)
			{
				addButton.setEnabled(true);
			}
		}
		else if (src.equals(applyButton))
		{
			if (filterable != null)
			{
				storeFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters());

				if (selectedModel.size() > 0)
				{
					PToolBar.getCurrentInstance().setFilterActive();
				}
				else
				{
					PToolBar.getCurrentInstance().setFilterInactive();
				}

				int mode = FilterConstants.MATCH_ALL;

				if (matchAnyRadio.isSelected())
				{
					mode += FilterConstants.MATCH_ANY;
				}

				if (negateBox.isSelected())
				{
					mode++;
				}

				filterable.setFilterMode(mode);
				filterable.refreshFiltering();
				filterable = null;
			}

			setVisible(false);
		}
		else if (src.equals(cancelButton))
		{
			filterable = null;
			setVisible(false);
		}
		else if (src.equals(saveButton))
		{
			PObjectFilter filter;
			final String operand = (matchAllRadio.isSelected()) ? FilterConstants.AND : FilterConstants.OR;

			if (selectedModel.size() < 1)
			{
				// show warning
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_filterErP1")
						+ Constants.LINE_SEPARATOR
						+ LanguageBundle.getString("in_filterErP2"),
						LanguageBundle.getString("in_filterErWarn"),
					MessageType.ERROR);

				return;
			}
			else if (selectedModel.size() == 1)
			{
				filter = (PObjectFilter) selectedModel.get(0);
			}
			else
			{
				filter = FilterFactory.createCompoundFilter((PObjectFilter) selectedModel.get(0),
					(PObjectFilter) selectedModel.get(1), operand);

				for (int i = 2; i < selectedModel.size(); i++)
				{
					filter = FilterFactory.createCompoundFilter(filter, (PObjectFilter) selectedModel.get(i), operand);
				}
			}

			if (negateBox.isSelected())
			{
				filter = FilterFactory.createInverseFilter(filter);
			}

			// possible naming
			filterNameDialog.setIllegalNames(createIllegalNamesList());
			filterNameDialog.setVisible(true);

			if ((filterNameDialog.getName() + filterNameDialog.getDescription()).length() > 0)
			{
				filter = FilterFactory.createNamedFilter(filter, filterNameDialog.getName(),
					filterNameDialog.getDescription());
			}

			clearSelectedFilters();
			selectedModel.addElement(filter);
		}
	}

	/**
	 *
	 */
	public void clearSelectedFilters()
	{
		for (int i = 0; i < selectedModel.size(); i++)
		{
			availableModel.addElement(selectedModel.get(i));
		}

		selectedModel.clear();
	}

	/**
	 * @param b
	 *
	 */
	@Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			settings(PCGen_Frame1.getCurrentFilterable());
		}
		super.setVisible(b);
	}

	/**
	 * set the selection mode:
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param mode an int specifying the selection mode:
	 *             SINGLE_SINGLE_MODE
	 *             SINGLE_MULTI_MODE
	 *             MULTI_MULTI_MODE
	 */
	private void setMode(int mode)
	{
		switch (mode)
		{
			case FilterConstants.SINGLE_SINGLE_MODE:
				activateSingleSingleMode();

				break;

			case FilterConstants.SINGLE_MULTI_MODE:
				activateSingleMultiMode();

				break;

			case FilterConstants.MULTI_MULTI_MODE:
				activateMultiMultiMode();

				break;

			default:
				activateDisplayOnlyMode();
		}
	}

	private void _init()
	{
		GBLPanel leftPanel = new GBLPanel();
		GBLPanel middlePanel = new GBLPanel();
		GBLPanel rightPanel = new GBLPanel();

		final ListMouseHandler lml = new ListMouseHandler();

//                  final Dimension buttonDimension = new Dimension(95, 27);
		// left stuff
		leftPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		leftPanel.gbc.weighty = 0;
		leftPanel.add(new JLabel(LanguageBundle.getString("in_availFils")), 0, 0, 1, 1);

		// Available List
//  		availableList = new JList(availableModel = new DefaultListModel());
//  		availableList = new JList(availableModel = new SortedListModel());
		availableList = new FilterList(availableModel = new SortedListModel());
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableList.addMouseListener(lml);

		JScrollPane availableScroll = new JScrollPane(availableList);
		availableScroll.setMinimumSize(new Dimension(200, 295));

//  		availableScroll.setPreferredSize(new Dimension(200, 295));
		JPanel leftListPanel = new JPanel(new GridLayout(1, 1));
		leftListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		leftListPanel.add(availableScroll);

		leftPanel.gbc.weightx = 10;
		leftPanel.gbc.weighty = 10;
		leftPanel.gbc.fill = GridBagConstraints.BOTH;
		leftPanel.gbc.anchor = GridBagConstraints.CENTER;
		leftPanel.add(leftListPanel, 1, 0, 1, 1);

		// middle stuff
		middlePanel.gbc.weightx = 0;
		middlePanel.gbc.weighty = 0;
		middlePanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		middlePanel.add(new JLabel("   "), 0, 0, 1, 1);

		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		addButton.addActionListener(this);
		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		removeButton.addActionListener(this);

		JPanel addButtonPanel = new JPanel(new GridLayout(1, 1));
		addButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		addButtonPanel.add(addButton);

		JPanel removeButtonPanel = new JPanel(new GridLayout(1, 1));
		removeButtonPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		removeButtonPanel.add(removeButton);

		JPanel middleButtonPanel = new JPanel(new GridLayout(2, 1));
		middleButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		middleButtonPanel.add(addButtonPanel);
		middleButtonPanel.add(removeButtonPanel);

		middlePanel.gbc.weighty = 10;
		middlePanel.gbc.anchor = GridBagConstraints.NORTH;
		middlePanel.add(middleButtonPanel, 1, 0, 1, 1);

		// right stuff
		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		rightPanel.add(new JLabel(LanguageBundle.getString("in_selectedFilter")), 0, 0, 1, 1);

		// Selected List
//  		selectedList = new JList(selectedModel = new DefaultListModel());
//  		selectedList = new JList(selectedModel = new SortedListModel());
		selectedList = new FilterList(selectedModel = new SortedListModel());
		selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedList.addMouseListener(lml);

		JScrollPane selectedScroll = new JScrollPane(selectedList);
		selectedScroll.setMinimumSize(new Dimension(200, 130));
		selectedScroll.setPreferredSize(new Dimension(200, 130));

		// Save button
		saveButton = new JButton(LanguageBundle.getString("in_save"));

//                  saveButton.setMinimumSize( buttonDimension );
//                  saveButton.setPreferredSize( buttonDimension );
		saveButton.addActionListener(this);

		JPanel saveButtonPanel = new JPanel(new GridLayout(1, 1));
		saveButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		saveButtonPanel.add(saveButton);

		// Option buttons
		matchAllRadio = new JRadioButton(LanguageBundle.getString("in_matchAll"));
		matchAllRadio.setEnabled(true);
		matchAnyRadio = new JRadioButton(LanguageBundle.getString("in_matchAny"));
		matchAnyRadio.setEnabled(false);
		negateBox = new JCheckBox(LanguageBundle.getString("in_negRev"));
		negateBox.setSelected(false);
		negateBox.setEnabled(false);

		ButtonGroup bg = new ButtonGroup();
		bg.add(matchAllRadio);
		bg.add(matchAnyRadio);
		matchAllRadio.setSelected(true);

		JPanel optionPanel = new JPanel(new GridLayout(3, 1));
		optionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
			BorderFactory.createRaisedBevelBorder()), BorderFactory.createEmptyBorder(0, 5, 2, 5)),
			LanguageBundle.getString("in_options"), TitledBorder.LEFT, TitledBorder.TOP,
			UIManager.getFont("Label.font")));
		optionPanel.add(matchAllRadio);
		optionPanel.add(matchAnyRadio);
		optionPanel.add(negateBox);

//  		optionPanel.setSize(new Dimension(200, 130));
		optionPanel.setPreferredSize(new Dimension(200, 130));
		optionPanel.setMinimumSize(new Dimension(200, 130));

//  		JPanel rightListPanel = new JPanel(new GridLayout(2, 1));
		GBLPanel rightListPanel = new GBLPanel();
		rightListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		rightListPanel.gbc.weightx = 0;
		rightListPanel.gbc.weighty = 10;
		rightListPanel.gbc.fill = GridBagConstraints.VERTICAL;
		rightListPanel.add(selectedScroll, 0, 0, 1, 1);
		rightListPanel.gbc.weighty = 0;
		rightListPanel.gbc.fill = GridBagConstraints.HORIZONTAL;
		rightListPanel.add(saveButtonPanel, 1, 0, 1, 1);
		rightListPanel.gbc.fill = GridBagConstraints.NONE;
		rightListPanel.add(optionPanel, 2, 0, 1, 1);

		rightPanel.gbc.weightx = 0;
		rightPanel.gbc.weighty = 10;

//  		rightPanel.gbc.anchor = GridBagConstraints.CENTER;
		rightPanel.gbc.anchor = GridBagConstraints.NORTH;
		rightPanel.gbc.fill = GridBagConstraints.VERTICAL;
		rightPanel.add(rightListPanel, 1, 0, 1, 1);

		applyButton = new JButton(LanguageBundle.getString("in_apply"));

//                  applyButton.setMinimumSize( buttonDimension );
//                  applyButton.setPreferredSize( buttonDimension );
		applyButton.addActionListener(this);

		cancelButton = new JButton(LanguageBundle.getString("in_cancel"));

//                  cancelButton.setMinimumSize( buttonDimension );
//                  cancelButton.setPreferredSize( buttonDimension );
		cancelButton.addActionListener(this);

		JPanel applyButtonPanel = new JPanel(new GridLayout(1, 1));
		applyButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
		applyButtonPanel.add(applyButton);

		JPanel cancelButtonPanel = new JPanel(new GridLayout(1, 1));
		cancelButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 2));
		cancelButtonPanel.add(cancelButton);

		JPanel rightButtonPanel = new JPanel(new GridLayout(1, 2));
		rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		rightButtonPanel.add(applyButtonPanel);
		rightButtonPanel.add(cancelButtonPanel);

		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.anchor = GridBagConstraints.NORTH;
		rightPanel.gbc.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(rightButtonPanel, 2, 0, 1, 1);

		GBLPanel mainPanel = new GBLPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mainPanel.gbc.fill = GridBagConstraints.BOTH;
		mainPanel.gbc.anchor = GridBagConstraints.NORTH;
		mainPanel.gbc.weighty = 10;
		mainPanel.gbc.weightx = 10;
		mainPanel.add(leftPanel, 0, 0, 1, 1);
		mainPanel.gbc.weightx = 0;
		mainPanel.add(middlePanel, 0, 1, 1, 1);
		mainPanel.add(rightPanel, 0, 2, 1, 1);

//  		mainPanel.gbc.fill = GridBagConstraints.NONE;
//  		mainPanel.gbc.anchor = GridBagConstraints.EAST;
//  		mainPanel.add(rightButtonPanel, 1, 2, 1, 1);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPanel.add(mainPanel, BorderLayout.CENTER);

		setContentPane(contentPanel);
	}

	private void activateDisplayOnlyMode()
	{
		availableModel.clear();
		selectedModel.clear();

		availableModel.addElement(displayOnlyFilter);
		selectedModel.addElement(displayOnlyFilter);

		availableList.setEnabled(false);
		selectedList.setEnabled(false);
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		applyButton.setEnabled(false);
		matchAllRadio.setSelected(true);
		matchAllRadio.setEnabled(false);
		matchAnyRadio.setEnabled(false);
		negateBox.setSelected(false);
		negateBox.setEnabled(false);
	}

	private void activateMultiMultiMode()
	{
		availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		maxSelected = Integer.MAX_VALUE;
	}

	private void activateSingleMultiMode()
	{
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		maxSelected = Integer.MAX_VALUE;
	}

	private void activateSingleSingleMode()
	{
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		maxSelected = 1;
	}

	/**
	 * @return a list of illegal filter names
	 */
	private List<String> createIllegalNamesList()
	{
		List<String> illegalNames = new ArrayList<String>();

		PObjectFilter filter;

		for (int i = 0; i < availableModel.size(); i++)
		{
			filter = (PObjectFilter) availableModel.get(i);

			if (filter instanceof NamedFilter)
			{
				illegalNames.add(filter.getName());
			}
		}

		for (int i = 0; i < selectedModel.size(); i++)
		{
			filter = (PObjectFilter) selectedModel.get(i);

			if (filter instanceof NamedFilter)
			{
				illegalNames.add(filter.getName());
			}
		}

		for (Iterator it = filterable.getRemovedFilters().iterator(); it.hasNext();)
		{
			filter = (PObjectFilter) it.next();

			if (filter instanceof NamedFilter)
			{
				illegalNames.add(filter.getName());
			}
		}

		return illegalNames;
	}

	private void init()
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension screenSize = Utility.getScreenSize(Toolkit.getDefaultToolkit());
		setLocation(screenSize.width / 4, screenSize.height / 4);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(false);
		_init();
		pack();
		setResizable(true);

//                  setMode(SINGLE_SINGLE_MODE);
		setMode(FilterConstants.SINGLE_MULTI_MODE);

//                  setMode(MULTI_MULTI_MODE);
	}

	/**
	 * extract all filter data to setup this filter dialog
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param f the Filterable whose data is extracted
	 */
	private void settings(Filterable f)
	{
		if (f == null)
		{
			activateDisplayOnlyMode();
		}
		else
		{
			filterable = f;

			setMode(f.getSelectionMode());

			if (f.getSelectionMode() < FilterConstants.SINGLE_SINGLE_MODE)
			{
				return;
			}

			availableList.setEnabled(true);
			selectedList.setEnabled(true);

			setFilters(f.getAvailableFilters(), f.getSelectedFilters());

			addButton.setEnabled(selectedModel.size() < maxSelected);
			removeButton.setEnabled(true);
			applyButton.setEnabled(true);

			matchAllRadio.setEnabled(true);

			if (f.isMatchAnyEnabled())
			{
				matchAnyRadio.setEnabled(true);

				// MATCH_ALL and MATCH_ALL_NEGATE
				// both are smaller than MATCH_ANY
				if (f.getFilterMode() < FilterConstants.MATCH_ANY)
				{
					matchAllRadio.setSelected(true);
				}

				// MATCH_ANY and MATCH_ANY_NEGATE
				// both are greater than MATCH_ALL_NEGATE
				else
				{
					matchAnyRadio.setSelected(true);
				}
			}
			else
			{
				matchAllRadio.setSelected(true);
				matchAnyRadio.setEnabled(false);
			}

			if (f.isNegateEnabled())
			{
				negateBox.setEnabled(true);

				// MATCH_ALL_NEGATE and MATCH_ANY_NEGATE
				// both are odd numbers
				negateBox.setSelected((f.getFilterMode() % 2) == 1);
			}
			else
			{
				negateBox.setSelected(false);
				negateBox.setEnabled(false);
			}
		}
	}

	/**
	 * store the available and selected filters
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List in which the available filter(name)s are to be stored;<br>
	 *                         previously stored data in this List will be lost
	 * @param selectedFilters  a List in which the selected filter(name)s are to be stored;<br>
	 *                         previously stored data in this List will be lost
	 */
	private void storeFilters(List availableFilters, List selectedFilters)
	{
		if (availableFilters != null)
		{
			availableFilters.clear();

			for (Enumeration filters = availableModel.elements(); filters.hasMoreElements();)
			{
				availableFilters.add(filters.nextElement());
			}
		}

		if (selectedFilters != null)
		{
			selectedFilters.clear();

			for (Enumeration filters = selectedModel.elements(); filters.hasMoreElements();)
			{
				selectedFilters.add(filters.nextElement());
			}
		}
	}

	/**
	 *
	 */
	private class ListMouseHandler extends MouseAdapter
	{
		/**
		 * @param e
		 *
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			Object src = e.getSource();

			if (e.getClickCount() == 2)
			{
				if (src.equals(availableList) && (selectedModel.size() < maxSelected))
				{
					final int index = availableList.getSelectedIndex();

					if (index > -1)
					{
						selectedModel.addElement(availableModel.get(index));
						availableModel.removeElementAt(index);
					}

					if (selectedModel.size() == maxSelected)
					{
						addButton.setEnabled(false);
					}
				}
				else if (src.equals(selectedList))
				{
					final int index = selectedList.getSelectedIndex();

					if (index > -1)
					{
						availableModel.addElement(selectedModel.get(index));
						selectedModel.removeElementAt(index);
					}

					if (selectedModel.size() < maxSelected)
					{
						addButton.setEnabled(true);
					}
				}
			}
		}
	}
}

/**
 * <code>GBLPanel</code>
 *
 * @author Thomas Behr
 */
final class GBLPanel extends JPanel
{
	GridBagConstraints gbc;
	private GridBagLayout gbl;

	/**
	 *
	 */
	GBLPanel()
	{
		super();

		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		GBLPanel.this.setLayout(gbl);
	}

	/**
	 * @param comp
	 * @param row
	 * @param col
	 * @param argWidth
	 * @param argHeight
	 *
	 */
	void add(JComponent comp, int row, int col, int argWidth, int argHeight)
	{
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = argWidth;
		gbc.gridheight = argHeight;

		gbl.setConstraints(comp, gbc);
		super.add(comp);
	}
}

/**
 * <code>SortedListModel</code>
 * <br>
 * list model for a sorted list<br>
 * the object will be sorted lexicographical according to their toString() value
 * <br>
 * TODO THIS IS A HACK
 * <br>
 * To do this properly, you would use a sorted List which takes a Comparator
 * to determine the order of the objects<br>
 * We do not need to go to such an effort, after all we only want to display our
 * POBjectFilters
 *
 * @author Thomas Behr
 */
final class SortedListModel extends DefaultListModel
{
	/**
	 * Overwrite this method to do sorted insertion
	 *
	 * @param o
	 */
	@Override
	public void addElement(Object o)
	{
		insertSorted(o);
	}

	private int binarySearch(Object key)
	{
		String keyString = key.toString();

		int low = 0;
		int high = this.size() - 1;

		while (low <= high)
		{
			int mid = (low + high) / 2;
			String midVal = get(mid).toString();

			int cmp = midVal.compareToIgnoreCase(keyString);

			if (cmp < 0)
			{
				low = mid + 1;
			}
			else if (cmp > 0)
			{
				high = mid - 1;
			}
			else
			{
				return mid; // key found
			}
		}

		return -(low + 1); // key not found
	}

	/**
	 * Insert objects into the vector, sorted in ascending
	 * order according to the <i>Comparator</i>.
	 * The binary search algorithm is used to determine
	 * the position, where the object should be inserted.
	 *
	 * @param o
	 * @return TRUE, iff the object was successfully inserted.
	 *         FALSE, if the object could not be inserted, e.g.
	 *         if the vector was already holding an <i>equal</i>
	 *         object according to the <i>Comparator</i>
	 */
	private boolean insertSorted(Object o)
	{
		int index = binarySearch(o);

		// element not yet contained in vector
		if (index < 0)
		{
			index = -(index + 1);
			this.add(index, o);

			return true;
		}

		// element already contained in vector
		return false;
	}
}

/**
 * <code>FilterList</code>
 * <br>a Jlist which displays tool tips
 *
 * @author Thomas Behr
 */
final class FilterList extends JList
{
	static final long serialVersionUID = 6872311299112043236L;
	private static final Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

	FilterList(ListModel model)
	{
		super(model);
		ToolTipManager.sharedInstance().registerComponent(this);
		setCellRenderer(new FilterListCellRenderer());
	}

	@Override
	public String getToolTipText(MouseEvent e)
	{
		int index = locationToIndex(e.getPoint());

		if (index > -1)
		{
			PObjectFilter filter = (PObjectFilter) getModel().getElementAt(index);

			return (filter.getDescription().length() > 0) ? filter.getDescription() : filter.getName();
		}

		return getToolTipText();
	}

	private static class FilterListCellRenderer extends JLabel implements ListCellRenderer
	{
		private FilterListCellRenderer()
		{
			super();
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
													  boolean cellHasFocus)
		{
			this.setComponentOrientation(list.getComponentOrientation());

			if (isSelected)
			{
				this.setBackground(list.getSelectionBackground());
				this.setForeground(list.getSelectionForeground());
			}
			else
			{
				this.setBackground(list.getBackground());
				this.setForeground(list.getForeground());
			}

			PObjectFilter filter = (PObjectFilter) value;
			this.setText(filter.getCategory() + PObjectFilter.SEPARATOR + filter.getName());

			this.setEnabled(list.isEnabled());
			this.setFont(list.getFont());
			this.setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

			return this;
		}
	}
}

/**
 * <code>FilterCustomDialog</code>
 *
 * @author Thomas Behr
 */
final class FilterCustomDialog extends JDialog implements ActionListener
{
	static final long serialVersionUID = -6836959436921618384L;
	private FilterList availableList;
	private FilterList removedList;
	private Filterable filterable;
	private JButton addButton;
	private JButton applyButton;
	private JButton cancelButton;
	private JButton removeButton;
	private SortedListModel availableModel;
	private SortedListModel removedModel;
	private int maxSelected;

	/**
	 * Constructor
	 */
	FilterCustomDialog()
	{
		super(Globals.getRootFrame(), LanguageBundle.getString("in_filterCustom"));
		this.init();
	}

	/**
	 * implementation of ActionListener interface
	 * <p/>
	 * <br>author: Thomas Behr
	 *
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if (src.equals(addButton))
		{
			final int[] indices = availableList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				// take into account that the list shrinks by one
				// with each iteration;
				// therefore we subtract from the position index
				// how many elements (i) we've removed already
				removedModel.addElement(availableModel.get(indices[i] - i));
				availableModel.removeElementAt(indices[i] - i);
			}

			if (removedModel.size() == maxSelected)
			{
				addButton.setEnabled(false);
			}
		}
		else if (src.equals(removeButton))
		{
			final int[] indices = removedList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				// take into account that the list shrinks by one
				// with each iteration;
				// therefore we subtract from the position index
				// how many elements (i) we've removed already
				availableModel.addElement(removedModel.get(indices[i] - i));
				removedModel.removeElementAt(indices[i] - i);
			}

			if (removedModel.size() < maxSelected)
			{
				addButton.setEnabled(true);
			}
		}
		else if (src.equals(applyButton))
		{
			if (filterable != null)
			{
				storeFilters(filterable.getAvailableFilters(), filterable.getRemovedFilters());

				FilterDialogFactory.setFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters());

				SettingsHandler.storeFilterSettings(filterable);
				PCGen_Frame1.restoreFilterSettings(filterable.getName());

				filterable = null;
			}

			setVisible(false);
		}
		else if (src.equals(cancelButton))
		{
			filterable = null;
			setVisible(false);
		}
	}

	/**
	 * @param b
	 *
	 */
	@Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			settings(PCGen_Frame1.getCurrentFilterable());
		}
		super.setVisible(b);
	}

	/**
	 * set the possible filternames
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List of strings holding names of available filters;<br>
	 *                         if availableFilters is null, then there will be
	 *                         no items in the available list
	 * @param removedFilters   a List of strings holding names of removed filters;<br>
	 *                         if removedFilters is null, then there will be
	 *                         no items in the removed list
	 */
	private void setFilters(List availableFilters, List removedFilters)
	{
		availableModel.clear();
		removedModel.clear();

		if (availableFilters != null)
		{
			for (Iterator it = availableFilters.iterator(); it.hasNext();)
			{
				availableModel.addElement(it.next());
			}
		}

		if (removedFilters != null)
		{
			for (Iterator it = removedFilters.iterator(); it.hasNext();)
			{
				removedModel.addElement(it.next());
			}
		}
	}

	/**
	 * initialize gui components
	 */
	private void _init()
	{
		GBLPanel leftPanel = new GBLPanel();
		GBLPanel middlePanel = new GBLPanel();
		GBLPanel rightPanel = new GBLPanel();

		final ListMouseHandler lml = new ListMouseHandler();

// left stuff
		leftPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		leftPanel.gbc.weighty = 0;
		leftPanel.add(new JLabel(LanguageBundle.getString("in_availFils")), 0, 0, 1, 1);

		final Dimension scrollPaneDimension = new Dimension(200, 295);

// Available List
		availableList = new FilterList(availableModel = new SortedListModel());
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableList.addMouseListener(lml);

		JScrollPane availableScroll = new JScrollPane(availableList);
		availableScroll.setMinimumSize(scrollPaneDimension);
		availableScroll.setPreferredSize(scrollPaneDimension);

		JPanel leftListPanel = new JPanel(new GridLayout(1, 1));
		leftListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		leftListPanel.add(availableScroll);

		leftPanel.gbc.weightx = 10;
		leftPanel.gbc.weighty = 10;
		leftPanel.gbc.fill = GridBagConstraints.BOTH;
		leftPanel.gbc.anchor = GridBagConstraints.CENTER;
		leftPanel.add(leftListPanel, 1, 0, 1, 1);

// middle stuff
		middlePanel.gbc.weightx = 0;
		middlePanel.gbc.weighty = 0;
		middlePanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		middlePanel.add(new JLabel("   "), 0, 0, 1, 1);

		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		addButton.addActionListener(this);
		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		removeButton.addActionListener(this);

		JPanel addButtonPanel = new JPanel(new GridLayout(1, 1));
		addButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		addButtonPanel.add(addButton);

		JPanel removeButtonPanel = new JPanel(new GridLayout(1, 1));
		removeButtonPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		removeButtonPanel.add(removeButton);

		JPanel middleButtonPanel = new JPanel(new GridLayout(2, 1));
		middleButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		middleButtonPanel.add(addButtonPanel);
		middleButtonPanel.add(removeButtonPanel);

		middlePanel.gbc.weighty = 10;
		middlePanel.gbc.anchor = GridBagConstraints.NORTH;
		middlePanel.add(middleButtonPanel, 1, 0, 1, 1);

// right stuff
		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		rightPanel.add(new JLabel(LanguageBundle.getString("in_removeFils")), 0, 0, 1, 1);

// Removed List
		removedList = new FilterList(removedModel = new SortedListModel());
		removedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		removedList.addMouseListener(lml);

		JScrollPane removedScroll = new JScrollPane(removedList);
		removedScroll.setMinimumSize(scrollPaneDimension);
		removedScroll.setPreferredSize(scrollPaneDimension);

		JPanel rightListPanel = new JPanel(new GridLayout(1, 1));
		rightListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		rightListPanel.add(removedScroll);

		rightPanel.gbc.weightx = 10;
		rightPanel.gbc.weighty = 10;
		rightPanel.gbc.fill = GridBagConstraints.BOTH;
		rightPanel.gbc.anchor = GridBagConstraints.CENTER;
		rightPanel.add(rightListPanel, 1, 0, 1, 1);

		// control buttons
		final Dimension buttonDimension = new Dimension(95, 27);

		applyButton = new JButton(LanguageBundle.getString("in_apply"));
		applyButton.setPreferredSize(buttonDimension);
		applyButton.addActionListener(this);

		cancelButton = new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setPreferredSize(buttonDimension);
		cancelButton.addActionListener(this);

		JPanel applyButtonPanel = new JPanel(new GridLayout(1, 1));
		applyButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
		applyButtonPanel.add(applyButton);

		JPanel cancelButtonPanel = new JPanel(new GridLayout(1, 1));
		cancelButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 2));
		cancelButtonPanel.add(cancelButton);

		GBLPanel rightButtonPanel = new GBLPanel();
		rightButtonPanel.gbc.weightx = 0;
		rightButtonPanel.gbc.weighty = 0;
		rightButtonPanel.gbc.fill = GridBagConstraints.NONE;
		rightButtonPanel.gbc.anchor = GridBagConstraints.EAST;
		rightButtonPanel.add(applyButtonPanel, 0, 0, 1, 1);
		rightButtonPanel.add(cancelButtonPanel, 0, 1, 1, 1);

		GBLPanel mainPanel = new GBLPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.gbc.fill = GridBagConstraints.BOTH;
		mainPanel.gbc.anchor = GridBagConstraints.NORTH;
		mainPanel.gbc.weighty = 10;
		mainPanel.add(leftPanel, 0, 0, 1, 1);
		mainPanel.gbc.weightx = 0;
		mainPanel.add(middlePanel, 0, 1, 1, 1);
		mainPanel.gbc.weightx = 10;
		mainPanel.add(rightPanel, 0, 2, 1, 1);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPanel.add(mainPanel, BorderLayout.CENTER);
		contentPanel.add(rightButtonPanel, BorderLayout.SOUTH);

		setContentPane(contentPanel);
	}

	private void activateDisplayOnlyMode()
	{
		availableModel.clear();
		removedModel.clear();

//  		availableModel.addElement(displayOnlyFilter);
//  		removedModel.addElement(displayOnlyFilter);
		availableList.setEnabled(false);
		removedList.setEnabled(false);
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		applyButton.setEnabled(false);
	}

	private void activateMultiMultiMode()
	{
		availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		removedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		maxSelected = Integer.MAX_VALUE;
	}

	/**
	 * initialize
	 */
	private void init()
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack. This only works for xinerama displays
		// with two equally sized 4:3 resoltion displays.
		// final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension screenSize = Utility.getScreenSize(Toolkit.getDefaultToolkit());
		setLocation(screenSize.width / 4, screenSize.height / 4);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		_init();
		pack();
		setResizable(false);
		activateMultiMultiMode();
	}

	/**
	 * extract all filter data to setup this filter dialog
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param f the Filterable whose data is extracted
	 */
	private void settings(Filterable f)
	{
		if (f == null)
		{
			activateDisplayOnlyMode();
		}
		else
		{
			filterable = f;

			if (f.getSelectionMode() < FilterConstants.SINGLE_SINGLE_MODE)
			{
				activateDisplayOnlyMode();

				return;
			}

			availableList.setEnabled(true);
			removedList.setEnabled(true);

			setFilters(f.getAvailableFilters(), f.getRemovedFilters());

			addButton.setEnabled(true);
			removeButton.setEnabled(true);
			applyButton.setEnabled(true);
		}
	}

	/**
	 * store the available and removed filters
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List in which the available filter(name)s are to be stored;<br>
	 *                         previously stored data in this List will be lost
	 * @param removedFilters   a List in which the removed filter(name)s are to be stored;<br>
	 *                         previously stored data in this List will be lost
	 */
	private void storeFilters(List availableFilters, List removedFilters)
	{
		if (availableFilters != null)
		{
			availableFilters.clear();

			for (Enumeration filters = availableModel.elements(); filters.hasMoreElements();)
			{
				availableFilters.add(filters.nextElement());
			}
		}

		if (removedFilters != null)
		{
			removedFilters.clear();

			for (Enumeration filters = removedModel.elements(); filters.hasMoreElements();)
			{
				removedFilters.add(filters.nextElement());
			}
		}
	}

	/**
	 *
	 */
	private class ListMouseHandler extends MouseAdapter
	{
		/**
		 * @param e
		 *
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			Object src = e.getSource();

			if (e.getClickCount() == 2)
			{
				if (src.equals(availableList) && (removedModel.size() < maxSelected))
				{
					final int index = availableList.getSelectedIndex();

					if (index > -1)
					{
						removedModel.addElement(availableModel.get(index));
						availableModel.removeElementAt(index);
					}

					if (removedModel.size() == maxSelected)
					{
						addButton.setEnabled(false);
					}
				}
				else if (src.equals(removedList))
				{
					final int index = removedList.getSelectedIndex();

					if (index > -1)
					{
						availableModel.addElement(removedModel.get(index));
						removedModel.removeElementAt(index);
					}

					if (removedModel.size() < maxSelected)
					{
						addButton.setEnabled(true);
					}
				}
			}
		}
	}
}

/**
 * <code>FilterNameDialog</code>
 *
 * @author Thomas Behr
 */
final class FilterNameDialog extends JDialog implements ActionListener
{
	private JButton applyButton;
	private JTextArea descriptionArea;
	private JTextField nameField;
	private final List<String> illegalNamesList = new ArrayList<String>();

	/**
	 * Constructor
	 */
	public FilterNameDialog()
	{
		super(Globals.getRootFrame(), LanguageBundle.getString("in_filterCustom"));
		this.init();
	}

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return the filter description
	 */
	public String getDescription()
	{
		return descriptionArea.getText().trim();
	}

	/**
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param newIllegalNamesList a list of names, which are illegal for filters
	 */
	public void setIllegalNames(List<String> newIllegalNamesList)
	{
		illegalNamesList.clear();
		illegalNamesList.addAll(newIllegalNamesList);
	}

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return the filter name
	 */
	@Override
	public String getName()
	{
		return nameField.getText().trim();
	}

	/**
	 * implementation of ActionListener interface
	 * <p/>
	 * <br>author: Thomas Behr
	 *
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		String name = nameField.getText();

		if (isLegalName(name))
		{
			setVisible(false);
		}

		// show warnings
		else
		{
			if (illegalNamesList.contains(name.trim()))
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_filterEr2P1",name), LanguageBundle.getString("in_filterErWarn"),
					MessageType.ERROR);
			}
			else
			{
				int index = indexOfIllegalChar(name);
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getFormattedString("in_filterEr3P1",String.valueOf(name.charAt(index))),
					LanguageBundle.getString("in_filterErWarn"), MessageType.ERROR);
				nameField.requestFocus();
				nameField.setCaretPosition(index);
			}
		}
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			nameField.setText("");
			descriptionArea.setText("");
		}
		super.setVisible(b);
	}


	/**
	 * checks if the specified string is a legal filter name
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param s any string
	 * @return <code>true</code>,if the specified string is a legal filter name;<br>
	 *         <code>false</code>,otherwise.
	 */
	private boolean isLegalName(String s)
	{
		return !illegalNamesList.contains(s.trim()) && (indexOfIllegalChar(s.trim()) == -1);
	}

	/**
	 * initialize gui components
	 */
	private void _init()
	{
		nameField = new JTextField();

		JPanel nameFieldPanel = new JPanel(new GridLayout(1, 1));
		nameFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		nameFieldPanel.add(nameField);

		descriptionArea = new JTextArea();

		JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
		descriptionScroll.setMinimumSize(new Dimension(300, 300));
		descriptionScroll.setPreferredSize(new Dimension(300, 300));

		JPanel descriptionAreaPanel = new JPanel(new GridLayout(1, 1));
		descriptionAreaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		descriptionAreaPanel.add(descriptionScroll);

		applyButton = new JButton(LanguageBundle.getString("in_apply"));
		applyButton.setMinimumSize(new Dimension(95, 27));
		applyButton.setPreferredSize(new Dimension(95, 27));
		applyButton.addActionListener(this);

		JPanel applyButtonPanel = new JPanel(new GridLayout(1, 1));
		applyButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		applyButtonPanel.add(applyButton);

		GBLPanel mainPanel = new GBLPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mainPanel.gbc.anchor = GridBagConstraints.NORTH;
		mainPanel.gbc.weightx = 10;
		mainPanel.gbc.weighty = 0;
		mainPanel.gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(new JLabel(LanguageBundle.getString("in_nameLabel") + ":"), 0, 0, 1, 1);
		mainPanel.add(nameFieldPanel, 1, 0, 1, 1);
		mainPanel.add(new JLabel(LanguageBundle.getString("in_descrip") + ":"), 2, 0, 1, 1);
		mainPanel.gbc.weighty = 10;
		mainPanel.gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(descriptionAreaPanel, 3, 0, 1, 1);
		mainPanel.gbc.weightx = 0;
		mainPanel.gbc.weighty = 0;
		mainPanel.gbc.fill = GridBagConstraints.NONE;
		mainPanel.gbc.anchor = GridBagConstraints.EAST;
		mainPanel.add(applyButtonPanel, 4, 0, 1, 1);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPanel.add(mainPanel, BorderLayout.CENTER);

		setContentPane(contentPanel);
	}

	/**
	 * returns the first occurance of an illegal character in the specified string
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param s any string
	 * @return the index of the first illegal character;<br>
	 *         if the specified string does not contain illegal characters,
	 *         -1 is returned.
	 */
	private static int indexOfIllegalChar(String s)
	{
		char[] chars = s.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if (!Character.isJavaIdentifierStart(chars[i]) && !Character.isWhitespace(chars[i])
				&& !((chars[i] == '(') || (chars[i] == ')')))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * initialize
	 */
	private void init()
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension screenSize = Utility.getScreenSize(Toolkit.getDefaultToolkit());
		setLocation(screenSize.width / 4, screenSize.height / 4);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		_init();
		pack();
		setResizable(false);
	}
}

/**
 * <code>FilterEditorDialog</code>
 *
 * @author Thomas Behr
 */
final class FilterEditorDialog extends JDialog implements ActionListener
{
	static final long serialVersionUID = 6455344467032929848L;

	/*
	 * replaced single-line properties
	 * with one multi-line property
	 *
	 * author: Thomas Behr 02-09-20
	 */
	private static final String HELP_MESSAGE = LanguageBundle.getString("in_filterHelpMessage");
	private FilterList customList;
	private FilterList standardList;
	private final FilterNameDialog filterNameDialog = new FilterNameDialog();
	private Filterable filterable;
	private JButton addCustomButton;
	private JButton addStandardButton;
	private JButton applyButton;
	private JButton cancelButton;
	private JButton clearButton;
	private JButton createButton;
	private JButton deleteButton;
	private JTextArea editorArea;
	private List customFilters;
	private Map<String, String> customFiltersOrigin;
	private SortedListModel customModel;
	private SortedListModel standardModel;

	/**
	 * Constructor
	 */
	public FilterEditorDialog()
	{
		super(Globals.getRootFrame(), LanguageBundle.getString("in_filterCustom"));
		this.customFilters = new ArrayList();
		this.customFiltersOrigin = new HashMap<String, String>();
		this.init();
	}

	/**
	 * implementation of ActionListener interface
	 * <p/>
	 * <br>author: Thomas Behr
	 *
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src.equals(addStandardButton))
		{
			int[] indices = standardList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				PObjectFilter filter = (PObjectFilter) standardModel.get(indices[i]);
				StringBuffer buf = new StringBuffer();
				buf.append(Constants.LINE_SEPARATOR).append("[")
					.append(filter.getCategory()).append(PObjectFilter.SEPARATOR)
					.append(filter.getName()).append("]");
				editorArea.append(buf.toString());
			}
		}
		else if (src.equals(addCustomButton))
		{
			int[] indices = customList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				PObjectFilter filter = (PObjectFilter) customModel.get(indices[i]);
				StringBuffer buf = new StringBuffer();
				buf.append(Constants.LINE_SEPARATOR)
					.append("[").append(filter.getCategory())
					.append(PObjectFilter.SEPARATOR)
					.append(filter.getName()).append("]");
				editorArea.append(buf.toString());
			}
		}
		else if (src.equals(applyButton))
		{
			if (filterable != null)
			{
				storeFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters(),
					filterable.getRemovedFilters());

				FilterDialogFactory.setFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters());

				SettingsHandler.storeFilterSettings(filterable);
				PCGen_Frame1.restoreFilterSettings(filterable.getName());

				filterable = null;
			}

			setVisible(false);
		}
		else if (src.equals(cancelButton))
		{
			if (filterable != null)
			{
				unStoreFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters(),
					filterable.getRemovedFilters());
			}

			filterable = null;
			setVisible(false);
		}
		else if (src.equals(createButton))
		{
			PObjectFilter filter;

			try
			{
				filter = (new FilterParser(new List[]
				{
					filterable.getAvailableFilters(), filterable.getSelectedFilters(),
					filterable.getRemovedFilters(), customFilters
				})).parse(editorArea.getText());
			}
			catch (FilterParseException ex)
			{
				filter = null;
				Logging.errorPrint("Error in FilterDialogFactory::actionPerformed", ex);
				ShowMessageDelegate.showMessageDialog(ex.getMessage(), LanguageBundle.getString("in_error"), MessageType.ERROR);
			}

			if (filter != null)
			{
				// possible naming
				filterNameDialog.setIllegalNames(createIllegalNamesList());
				filterNameDialog.setVisible(true);

				if ((filterNameDialog.getName() + filterNameDialog.getDescription()).length() > 0)
				{
					filter = FilterFactory.createNamedFilter(filter, filterNameDialog.getName(),
						filterNameDialog.getDescription());
				}

				customModel.addElement(filter);
				customFilters.add(filter);
			}
		}
		else if (src.equals(clearButton))
		{
			editorArea.setText("");
		}
		else if (src.equals(deleteButton))
		{
			int[] indices = customList.getSelectedIndices();

			for (int i = 0; i < indices.length; i++)
			{
				// take into account that the list shrinks by one
				// with each iteration;
				// therefore we subtract from the position index
				// how many elements (i) we've removed already
				customModel.removeElementAt(indices[i] - i);
			}
		}
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			editorArea.setText(HELP_MESSAGE);
			settings(PCGen_Frame1.getCurrentFilterable());
		}
		super.setVisible(b);
	}


	/**
	 * set the possible filternames
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List of strings holding names of available filters
	 * @param selectedFilters  a List of strings holding names of selected filters
	 * @param removedFilters   a List of strings holding names of removed filters
	 */
	private void setFilters(List availableFilters, List selectedFilters, List removedFilters)
	{
		standardModel.clear();
		customModel.clear();

		customFilters.clear();
		customFiltersOrigin.clear();

		Object filter;

		for (Iterator it = availableFilters.iterator(); it.hasNext();)
		{
			filter = it.next();

			if (filter instanceof CustomFilter)
			{
				customFiltersOrigin.put(filter.getClass().getName(), "available");
				customFilters.add(filter);
				customModel.addElement(filter);
				it.remove();
			}
			else
			{
				standardModel.addElement(filter);
			}
		}

		for (Iterator it = selectedFilters.iterator(); it.hasNext();)
		{
			filter = it.next();

			if (filter instanceof CustomFilter)
			{
				customFiltersOrigin.put(filter.getClass().getName(), "selected");
				customFilters.add(filter);
				customModel.addElement(filter);
				it.remove();
			}
			else
			{
				standardModel.addElement(filter);
			}
		}

		for (Iterator it = removedFilters.iterator(); it.hasNext();)
		{
			filter = it.next();

			if (filter instanceof CustomFilter)
			{
				customFiltersOrigin.put(filter.getClass().getName(), "removed");
				customFilters.add(filter);
				customModel.addElement(filter);
				it.remove();
			}
			else
			{
				standardModel.addElement(filter);
			}
		}
	}

	/**
	 * initialize gui components
	 */
	private void _init()
	{
		GBLPanel leftPanel = new GBLPanel();
		GBLPanel middlePanel = new GBLPanel();
		GBLPanel rightPanel = new GBLPanel();

		final ListMouseHandler lml = new ListMouseHandler();

		// control buttons
		final Dimension buttonDimension = new Dimension(95, 27);

		applyButton = new JButton(LanguageBundle.getString("in_apply"));
		applyButton.setPreferredSize(buttonDimension);
		applyButton.addActionListener(this);

		cancelButton = new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setPreferredSize(buttonDimension);
		cancelButton.addActionListener(this);

		JPanel applyButtonPanel = new JPanel(new GridLayout(1, 1));
		applyButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
		applyButtonPanel.add(applyButton);

		JPanel cancelButtonPanel = new JPanel(new GridLayout(1, 1));
		cancelButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 2));
		cancelButtonPanel.add(cancelButton);

		GBLPanel rightButtonPanel = new GBLPanel();
		rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		rightButtonPanel.gbc.weightx = 0;
		rightButtonPanel.gbc.weighty = 0;
		rightButtonPanel.gbc.fill = GridBagConstraints.NONE;
		rightButtonPanel.add(applyButtonPanel, 0, 0, 1, 1);
		rightButtonPanel.add(cancelButtonPanel, 0, 1, 1, 1);

		createButton = new JButton(LanguageBundle.getString("in_create"));
		createButton.setPreferredSize(buttonDimension);
		createButton.addActionListener(this);

		clearButton = new JButton(LanguageBundle.getString("in_clear"));
		clearButton.setPreferredSize(buttonDimension);
		clearButton.addActionListener(this);

		JPanel createButtonPanel = new JPanel(new GridLayout(1, 1));
		createButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
		createButtonPanel.add(createButton);

		JPanel clearButtonPanel = new JPanel(new GridLayout(1, 1));
		clearButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 2));
		clearButtonPanel.add(clearButton);

		GBLPanel leftButtonPanel = new GBLPanel();
		leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		leftButtonPanel.gbc.weightx = 0;
		leftButtonPanel.gbc.weighty = 0;
		leftButtonPanel.gbc.fill = GridBagConstraints.NONE;
		leftButtonPanel.add(createButtonPanel, 0, 0, 1, 1);
		leftButtonPanel.add(clearButtonPanel, 0, 1, 1, 1);

		deleteButton = new JButton(LanguageBundle.getString("in_delete"));
		deleteButton.setPreferredSize(buttonDimension);
		deleteButton.addActionListener(this);

		JPanel deleteButtonPanel = new JPanel(new GridLayout(1, 1));
		deleteButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		deleteButtonPanel.add(deleteButton);

// left stuff
		leftPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		leftPanel.gbc.weighty = 0;
		leftPanel.add(new JLabel(LanguageBundle.getString("in_standardFils") + ": "), 0, 0, 1, 1);

		final Dimension scrollPaneDimension = new Dimension(200, 295);

// Standard List
		standardList = new FilterList(standardModel = new SortedListModel());
		standardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		standardList.addMouseListener(lml);

		JScrollPane standardScroll = new JScrollPane(standardList);
		standardScroll.setMinimumSize(scrollPaneDimension);
		standardScroll.setPreferredSize(scrollPaneDimension);

		JPanel leftListPanel = new JPanel(new GridLayout(1, 1));
		leftListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		leftListPanel.add(standardScroll);

		addStandardButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		addStandardButton.addActionListener(this);

		JPanel addStandardButtonPanel = new JPanel(new GridLayout(1, 1));
		addStandardButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		addStandardButtonPanel.add(addStandardButton);

		leftPanel.gbc.weightx = 0;
		leftPanel.gbc.weighty = 10;
		leftPanel.gbc.fill = GridBagConstraints.BOTH;
		leftPanel.gbc.anchor = GridBagConstraints.CENTER;
		leftPanel.add(leftListPanel, 1, 0, 1, 1);
		leftPanel.gbc.weighty = 0;
		leftPanel.gbc.fill = GridBagConstraints.NONE;
		leftPanel.gbc.anchor = GridBagConstraints.NORTH;
		leftPanel.add(addStandardButtonPanel, 1, 1, 1, 1);

// middle stuff
		middlePanel.gbc.weightx = 0;
		middlePanel.gbc.weighty = 0;
		middlePanel.gbc.fill = GridBagConstraints.NONE;
		middlePanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		middlePanel.add(new JLabel(LanguageBundle.getString("in_editor") + ": "), 0, 0, 1, 1);

		final Dimension editorDimension = new Dimension(scrollPaneDimension.width * 2, scrollPaneDimension.height);

		editorArea = new JTextArea();

		JScrollPane editorScroll = new JScrollPane(editorArea);
		editorScroll.setMinimumSize(editorDimension);
		editorScroll.setPreferredSize(editorDimension);

		JPanel editorPanel = new JPanel(new GridLayout(1, 1));
		editorPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		editorPanel.add(editorScroll);

		middlePanel.gbc.weightx = 10;
		middlePanel.gbc.weighty = 10;
		middlePanel.gbc.fill = GridBagConstraints.BOTH;
		middlePanel.gbc.anchor = GridBagConstraints.NORTH;
		middlePanel.add(editorPanel, 1, 0, 1, 1);
		middlePanel.gbc.weighty = 0;
		middlePanel.gbc.fill = GridBagConstraints.HORIZONTAL;
		middlePanel.gbc.anchor = GridBagConstraints.NORTH;
		middlePanel.add(leftButtonPanel, 2, 0, 1, 1);

// right stuff
		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.anchor = GridBagConstraints.NORTHWEST;
		rightPanel.add(new JLabel(LanguageBundle.getString("in_customFils") + ": "), 0, 1, 1, 1);

// Custom List
		customList = new FilterList(customModel = new SortedListModel());
		customList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customList.addMouseListener(lml);

		JScrollPane customScroll = new JScrollPane(customList);
		customScroll.setMinimumSize(scrollPaneDimension);
		customScroll.setPreferredSize(scrollPaneDimension);

		JPanel rightListPanel = new JPanel(new GridLayout(1, 1));
		rightListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		rightListPanel.add(customScroll);

		addCustomButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		addCustomButton.addActionListener(this);

		JPanel addCustomButtonPanel = new JPanel(new GridLayout(1, 1));
		addCustomButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		addCustomButtonPanel.add(addCustomButton);

		rightPanel.gbc.weightx = 0;
		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.fill = GridBagConstraints.NONE;
		rightPanel.gbc.anchor = GridBagConstraints.NORTH;
		rightPanel.add(addCustomButtonPanel, 1, 0, 1, 1);
		rightPanel.gbc.weighty = 10;
		rightPanel.gbc.fill = GridBagConstraints.BOTH;
		rightPanel.gbc.anchor = GridBagConstraints.CENTER;
		rightPanel.add(rightListPanel, 1, 1, 1, 1);
		rightPanel.gbc.weighty = 0;
		rightPanel.gbc.fill = GridBagConstraints.NONE;
		rightPanel.gbc.anchor = GridBagConstraints.EAST;
		rightPanel.add(deleteButtonPanel, 2, 1, 1, 1);

		GBLPanel mainPanel = new GBLPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.gbc.fill = GridBagConstraints.BOTH;
		mainPanel.gbc.anchor = GridBagConstraints.NORTH;
		mainPanel.gbc.weighty = 10;
		mainPanel.gbc.weightx = 0;
		mainPanel.add(leftPanel, 0, 0, 1, 1);
		mainPanel.gbc.weightx = 10;
		mainPanel.add(middlePanel, 0, 1, 1, 1);
		mainPanel.gbc.weightx = 0;
		mainPanel.add(rightPanel, 0, 2, 1, 1);

//                  JPanel allButtonPanel = new JPanel(new GridLayout(1,2));
//                  allButtonPanel.add(leftButtonPanel);
//                  allButtonPanel.add(rightButtonPanel);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPanel.add(mainPanel, BorderLayout.CENTER);
		contentPanel.add(rightButtonPanel, BorderLayout.SOUTH);

//                  contentPanel.add(allButtonPanel, BorderLayout.SOUTH);
		setContentPane(contentPanel);
	}

	private void activateDisplayOnlyMode()
	{
		standardModel.clear();
		customModel.clear();

//  		standardModel.addElement(displayOnlyFilter);
//  		customModel.addElement(displayOnlyFilter);
		standardList.setEnabled(false);
		customList.setEnabled(false);
		addStandardButton.setEnabled(false);
		addCustomButton.setEnabled(false);
		applyButton.setEnabled(false);
		clearButton.setEnabled(false);
		createButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}

	private void activateMultiMultiMode()
	{
		standardList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		customList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	/**
	 * @return a list of illegal filter names
	 */
	private List<String> createIllegalNamesList()
	{
		List<String> illegalNames = new ArrayList<String>();

		PObjectFilter filter;

		for (Iterator it = customFilters.iterator(); it.hasNext();)
		{
			filter = (PObjectFilter) it.next();

			if (filter instanceof NamedFilter)
			{
				illegalNames.add(filter.getName());
			}
		}

		return illegalNames;
	}

	/**
	 * initialize
	 */
	private void init()
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension screenSize = Utility.getScreenSize(Toolkit.getDefaultToolkit());
		setLocation(screenSize.width / 24, screenSize.height / 4);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		_init();
		pack();

//                  setResizable(false);
		activateMultiMultiMode();
	}

	/**
	 * extract all filter data to setup this filter dialog
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param f the Filterable whose data is extracted
	 */
	private void settings(Filterable f)
	{
		if (f == null)
		{
			activateDisplayOnlyMode();
		}
		else
		{
			filterable = f;

			if (f.getSelectionMode() < FilterConstants.SINGLE_SINGLE_MODE)
			{
				activateDisplayOnlyMode();

				return;
			}

			standardList.setEnabled(true);
			customList.setEnabled(true);

			setFilters(filterable.getAvailableFilters(), filterable.getSelectedFilters(), filterable.getRemovedFilters());

			addStandardButton.setEnabled(true);
			addCustomButton.setEnabled(true);
			applyButton.setEnabled(true);
			clearButton.setEnabled(true);
			createButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
	}

	/**
	 * store the custom filters (this method handles "Apply" action)
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List in which the custom filter(name)s are to be stored
	 * @param selectedFilters  a List in which the custom filter(name)s are to be stored
	 * @param removedFilters   a List in which the custom filter(name)s are to be stored
	 */
	private void storeFilters(List availableFilters, List selectedFilters, List removedFilters)
	{
		String origin;
		Object filter;

		for (Enumeration filters = customModel.elements(); filters.hasMoreElements();)
		{
			filter = filters.nextElement();
			origin = customFiltersOrigin.get(filter.getClass().getName());

			if ((origin == null) || ("available".equals(origin)))
			{
				availableFilters.add(filter);
			}
			else if ("selected".equals(origin))
			{
				selectedFilters.add(filter);
			}
			else if ("removed".equals(origin))
			{
				removedFilters.add(filter);
			}
		}
	}

	/**
	 * store the custom filters (this method handles "Cancel" action)
	 * <p/>
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @param availableFilters a List in which the custom filter(name)s are to be stored
	 * @param selectedFilters  a List in which the custom filter(name)s are to be stored
	 * @param removedFilters   a List in which the custom filter(name)s are to be stored
	 */
	private void unStoreFilters(List availableFilters, List selectedFilters, List removedFilters)
	{
		String origin;
		Object filter;

		for (Iterator it = customFilters.iterator(); it.hasNext();)
		{
			filter = it.next();
			origin = customFiltersOrigin.get(filter.getClass().getName());

			if (origin == null)
			{
				/*
				 * this means, the current filter was newly created
				 * but since we are in "Cancel" case,
				 * we pretend that there are no newly created filters
				 */
			}
			else if ("available".equals(origin))
			{
				availableFilters.add(filter);
			}
			else if ("selected".equals(origin))
			{
				selectedFilters.add(filter);
			}
			else if ("removed".equals(origin))
			{
				removedFilters.add(filter);
			}
		}
	}

	/**
	 *
	 */
	private class ListMouseHandler extends MouseAdapter
	{
		/**
		 * @param e
		 *
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			Object src = e.getSource();
			if (e.getClickCount() == 2)
			{
				if (src.equals(standardList))
				{
					final int index = standardList.getSelectedIndex();

					if (index > -1)
					{
						PObjectFilter filter = (PObjectFilter) standardModel.get(index);
						editorArea.append(Constants.LINE_SEPARATOR);
						editorArea.append("[" + filter.getCategory()
							+ PObjectFilter.SEPARATOR + filter.getName() + "]");
					}
				}
				else if (src.equals(customList))
				{
					final int index = customList.getSelectedIndex();

					if (index > -1)
					{
						PObjectFilter filter = (PObjectFilter) customModel.get(index);
						editorArea.append(Constants.LINE_SEPARATOR);
						editorArea.append("[" + filter.getCategory()
							+ PObjectFilter.SEPARATOR + filter.getName() + "]");
					}
				}
			}
		}
	}
}
