/*
 * SwingChooser.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui.utils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.chooser.ChooserTableModel;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * This dialog type accepts a list of available items, a choice
 * limit, and some additional flags and switches. The user can
 * select and remove values until the required number of
 * choices have been made. The dialog is always modal, so a
 * call to show() will block program execution.
 *
 * @author    Matt Woodard
 * @version $Revision$
 */
public final class SwingChooser extends JDialog implements ChooserInterface
{
	static final long serialVersionUID = -2148735105737308335L;
	private static final String in_available;
	private static final String in_selected;
	private static final String in_completeMess;
	private static final String in_chooser;
	private static final String in_selTotal;
	private static final String in_selPerUnit;
	private static final String in_selEffective;
	private static final String in_validItem;
	private static final String in_deselectOne;
	private static final String in_noRemain;
	private static final String in_addOne;
	private static final String in_selectPartA;
	private static final String in_selectPartB;
	private static final String in_alreadySelected;
	private static final String in_closeChooserTip;
	private static final String in_pressToAdd;
	private static final String in_pressToRemove;
	private static final String in_removeOne;

	/**
	 * Resource bundles
	 */
	static
	{
		in_available = PropertyFactory.getString("in_available");
		in_selected = PropertyFactory.getString("in_selected");
		in_completeMess = PropertyFactory.getString("in_completeMess");
		in_chooser = PropertyFactory.getString("in_chooser");
		in_selTotal = PropertyFactory.getString("in_selTotal");
		in_selPerUnit = PropertyFactory.getString("in_selPerUnit");
		in_selEffective = PropertyFactory.getString("in_selEffective");
		in_validItem = PropertyFactory.getString("in_validItem");
		in_deselectOne = PropertyFactory.getString("in_deselectOne");
		in_noRemain = PropertyFactory.getString("in_noRemain");
		in_addOne = PropertyFactory.getString("in_addOne");
		in_selectPartA = PropertyFactory.getString("in_selectPartA");
		in_selectPartB = PropertyFactory.getString("in_selectPartB");
		in_alreadySelected = PropertyFactory.getString("in_alreadySelected");
		in_closeChooserTip = PropertyFactory.getString("in_closeChooserTip");
		in_pressToAdd = PropertyFactory.getString("in_pressToAdd");
		in_pressToRemove = PropertyFactory.getString("in_pressToRemove");
		in_removeOne = PropertyFactory.getString("in_removeOne");
	}

	/** The default available list column array */
	private static final List<String> AVAILABLE_COLUMN_NAMES =
			Arrays.asList(new String[]{in_available});

	/** The default selected list column array */
	private static final List<String> SELECTED_COLUMN_NAMES =
			Arrays.asList(new String[]{in_selected});

	/** The model table for the available item table */
	private ChooserTableModel mAvailableModel = new ChooserTableModel();

	/** The model table for the selected item table */
	private ChooserTableModel mSelectedModel = new ChooserTableModel();

	/** The JButton for adding available items to the selected list */
	private JButton mAddButton;

	/** The JButton for closing the dialog */
	private JButton mCloseButton;

	/** The JButton for removing selected items */
	private JButton mRemoveButton;

	/** The JLabel showing the remaining pool */
	private JLabel mTotalText;

	/** The JLabel showing the remaining pool */
	private JLabel mSelectedText;

	/** The JLabel showing the remaining pool */
	private JLabel mEffectiveText;

	/** The JLabel showing messages */
	private JLabelPane mMessageText;

	/** The JTableEx holding available items */
	private JTableEx mAvailableTable;

	/** The JTableEx holding selected items */
	private JTableEx mSelectedTable;

	/** The available table column names */
	private List<String> mAvailableColumnNames;

	/** The list of available items */
	private List mAvailableList = new ArrayList();

	/** The selected table column names */
	private List<String> mSelectedColumnNames;

	/** The list of selected items */
	private List mSelectedList = new ArrayList();

	/** The list of unique items */
	private List mUniqueList = new ArrayList();
	private String mSelectedTerminator = "";

	/** Will this chooser allow more choices than in the pool? */
	private boolean canGoNegative = false;

	/** Whether or not to allow duplicate choices */
	private boolean mAllowDuplicates = false;

	/** Whether or not to force mPool=0 when closing */
	private boolean mPoolFlag = true;

	/** The column containing the cost for an item */
	private int mCostColumnNumber = -1;

	private int selectionsPerUnitCost = 1;
	
	private int totalSelectionsAvailable = 1;
	
	private int effectiveUsed = 0;
	
	private boolean pickAll = false;
	
	/**
	 * Chooser constructor.
	 *
	 * author   Matt Woodard
	 */
	public SwingChooser()
	{
		super(Globals.getCurrentFrame());
		initComponents();
		setChoicesPerUnit(1);
		setTotalChoicesAvail(1);
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 * author       Matt Woodard
	 */
	public void setAllowsDups(boolean aBool)
	{
		mAllowDuplicates = aBool;
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 * author                  Matt Woodard
	 */
	public void setCostColumnNumber(final int costColumnNumber)
	{
		mCostColumnNumber = costColumnNumber;
	}

	/**
	 * Sets the message text.
	 *
	 * @param argMessageText  java.lang.String
	 * author             Matt Woodard
	 */
	public void setMessageText(String argMessageText)
	{
		String messageText;

		if ((argMessageText == null) || (argMessageText.trim().length() == 0))
		{
			messageText = "<html>&nbsp;</html>";
		}
		else
		{
			messageText = argMessageText;
		}

		mMessageText.setText(messageText);
	}

	public void setNegativeAllowed(final boolean argFlag)
	{
		canGoNegative = argFlag;
	}

	/**
	 * Sets the mPool attribute of the Chooser object.
	 *
	 * @param anInt  The new mPool value
	 * author       Matt Woodard
	 */
	public void setPool(final int anInt)
	{
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * author Dmitry Jemerov
	 * @return mPool
	 */
	public int getPool()
	{
		return getEffectivePool();
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 * author          Matt Woodard
	 */
	public void setPoolFlag(boolean poolFlag)
	{
		mPoolFlag = poolFlag;
	}

	/**
	 * Returns the selected item list
	 *
	 * @return   java.util.ArrayList
	 * author   Matt Woodard
	 */
	public List getSelectedList()
	{
		return new ArrayList(mSelectedList);
	}

	public void setSelectedListTerminator(String aString)
	{
		mSelectedTerminator = aString;
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 * author            Matt Woodard
	 */
	public void setUniqueList(List uniqueList)
	{
		mUniqueList = uniqueList;
	}

	/**
	 * Overrides the default setVisible method to ensure controls
	 * are updated before showing the dialog.
	 *
	 * author   Matt Woodard
	 * @param b
	 */
	public void setVisible(boolean b)
	{
		updateAvailableTable();
		updateSelectedTable();
		updateButtonStates();

		//
		// Only do this if 1 entry and can add...
		//
		if ((mAvailableList != null) && (mAvailableList.size() == 1) && b)
		{
			final int method = SettingsHandler.getSingleChoicePreference();

			if (method != Constants.CHOOSER_SINGLECHOICEMETHOD_NONE)
			{
				mAvailableTable.changeSelection(0, 0, false, false);
				updateButtonStates();

				if (mAddButton.isEnabled())
				{
					selectAvailable();

					if ((method == Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT)
						&& close())
					{
						return;
					}
				}
			}
		}

		Window owner = getOwner();
		Rectangle ownerBounds = owner.getBounds();
		Rectangle bounds = getBounds();

		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		setBounds(
			(int) (owner.getX() + ((ownerBounds.getWidth() - width) / 2)),
			(int) (owner.getY() + ((ownerBounds.getHeight() - height) / 2)),
			width, height);

		super.setVisible(b);
	}

	private int getAdjustment(JTableEx tbl)
	{
		final TableModel tableModel = tbl.getModel();
		final int selectedRow = tbl.getSelectedRow();

		int adjustment = 1;

		if ((mCostColumnNumber >= 0)
			&& (mCostColumnNumber < tbl.getColumnCount()))
		{
			try
			{
				adjustment =
						Integer.parseInt(tableModel.getValueAt(selectedRow,
							mCostColumnNumber).toString());
			}
			catch (NumberFormatException exc)
			{
				//TODO Should this really be ignored?
			}
		}

		return adjustment;
	}

	/**
	 * Sets the available column name list
	 *
	 * @param availableColumnNames  The new AvailableColumnNames value
	 * author                      Matt Woodard
	 */
	public void setAvailableColumnNames(List<String> availableColumnNames)
	{
		mAvailableColumnNames = availableColumnNames;

		mAvailableModel.setColumnsNames(availableColumnNames == null
			? Globals.EMPTY_STRING_ARRAY : (String[]) availableColumnNames
				.toArray(new String[availableColumnNames.size()]));
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 * author               Matt Woodard
	 */
	public void setAvailableList(List availableList)
	{
		mAvailableList = availableList;
	}

	/**
	 * Sets the selected column name list
	 * @param selectedColumnNames  java.util.List
	 * author                     Matt Woodard
	 */
	public void setSelectedColumnNames(List<String> selectedColumnNames)
	{
		mSelectedColumnNames = selectedColumnNames;

		mSelectedModel.setColumnsNames(selectedColumnNames == null
			? Globals.EMPTY_STRING_ARRAY : (String[]) selectedColumnNames
				.toArray(new String[selectedColumnNames.size()]));
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 * @param columnNames   The new SelectedList value
	 * author              Matt Woodard
	 */
	public void setSelectedList(List selectedList)
	{
		mSelectedList = selectedList;
		setEffectiveUsed();
	}

	private void windowCloseEvent()
	{
		if (!mCloseButton.isEnabled())
		{
			if (JOptionPane
				.showConfirmDialog(
					this,
					"You still have choices remaining. Are you sure you want to close the dialog?",
					Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
			{
				return;
			}
		}
		this.setVisible(false);
	}

	/**
	 * Closes the dialog if the pool is satisfied
	 *
	 * author   Matt Woodard
	 * @return true or false
	 */
	private boolean close()
	{
		if ((getEffectivePool() <= 0) || !mPoolFlag)
		{
			this.setVisible(false);

			return true;
		}

		setMessageText(in_completeMess);

		return false;
	}

	/**
	 * Initializes the components of the dialog
	 *
	 * author   Matt Woodard
	 */
	private void initComponents()
	{
		// WindowConstants.DO_NOTHING_ON_CLOSE is equivalent to
		// JDialog.DO_NOTHING_ON_CLOSE but more 'correct' in a
		// Java coding context (it is a static reference)
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent we)
			{
				windowCloseEvent();
			}
		});

		// Initialize basic dialog settings
		setModal(true);
		setSize(new Dimension(640, 400));
		setTitle(in_chooser);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// Create tables
		TableSorter sorter =
				new TableSorter(mAvailableModel = new ChooserTableModel());
		final JScrollPane availableScrollPane =
				new JScrollPane(mAvailableTable = new JTableEx(sorter));
		availableScrollPane
			.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		availableScrollPane
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sorter.addMouseListenerToHeaderInTable(mAvailableTable);

		sorter = new TableSorter(mSelectedModel = new ChooserTableModel());

		final JScrollPane selectedScrollPane =
				new JScrollPane(mSelectedTable = new JTableEx(sorter));
		selectedScrollPane
			.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedScrollPane
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sorter.addMouseListenerToHeaderInTable(mSelectedTable);

		// Initialize selection types & events
		final ListSelectionModel availableSelectionModel =
				mAvailableTable.getSelectionModel();

		// Initialize selection types & events
		final ListSelectionModel selectedSelectionModel =
				mSelectedTable.getSelectionModel();

		availableSelectionModel
			.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedSelectionModel
			.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final ListSelectionListener listSelectionListener =
				new ListSelectionListener()
				{
					/**
					 * Description of the Method
					 *
					 * @param evt  Description of Parameter
					 * author     mwoodard
					 */
					public void valueChanged(ListSelectionEvent evt)
					{
						if (!evt.getValueIsAdjusting())
						{
							updateButtonStates();
						}
					}
				};

		availableSelectionModel.addListSelectionListener(listSelectionListener);
		selectedSelectionModel.addListSelectionListener(listSelectionListener);

		// Initialize the mouse events
		mAvailableTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					selectAvailable();
				}
			}
		});

		mSelectedTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					removeSelected();
				}
			}
		});

		// Create labels
		final JLabel totalLabel = new JLabel(in_selTotal + ": ");
		final JLabel selectedLabel = new JLabel("     " + in_selPerUnit + ": ");
		final JLabel effectiveRemainingLabel = new JLabel(in_selEffective + ": ");

		// Create these labels with " " to force them to layout correctly
		mMessageText = new JLabelPane();
		mMessageText.setBackground(contentPane.getBackground());
		setMessageText(null);

		mTotalText = new JLabel(" ");
		mSelectedText = new JLabel(" ");
		mEffectiveText = new JLabel(" ");

		// Create buttons
		mAddButton = new JButton(PropertyFactory.getString("in_add"));
		mAddButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_add"));
		mCloseButton = new JButton(PropertyFactory.getString("in_close"));
		mCloseButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_close"));
		mRemoveButton = new JButton(PropertyFactory.getString("in_remove"));
		mRemoveButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_remove"));

		final ActionListener eventListener = new ActionListener()
		{
			/**
			 * Description of the Method
			 *
			 * @param evt  Description of Parameter
			 * author     Matt Woodard
			 */
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() == mAddButton)
				{
					selectAvailable();
				}
				else if (evt.getSource() == mRemoveButton)
				{
					removeSelected();
				}
				else if (evt.getSource() == mCloseButton)
				{
					close();
				}
			}
		};

		mAddButton.addActionListener(eventListener);
		mRemoveButton.addActionListener(eventListener);
		mCloseButton.addActionListener(eventListener);

		// Add controls to content pane
		GridBagConstraints constraints;

		// Add available list
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPane.add(availableScrollPane, constraints);

		// Add 'add' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 4;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mAddButton, constraints);

		// Add selected list
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(selectedScrollPane, constraints);

		// Add 'remove' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 4;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mRemoveButton, constraints);

		// Add message text
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mMessageText, constraints);

		// Add selection remaining label
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(totalLabel, constraints);

		// Add selection remaining field
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(mTotalText, constraints);

		// Add selection remaining label
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(selectedLabel, constraints);

		// Add selection remaining field
		constraints = new GridBagConstraints();
		constraints.gridx = 3;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(mSelectedText, constraints);

		// Add selection remaining label
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(effectiveRemainingLabel, constraints);

		// Add selection remaining field
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(mEffectiveText, constraints);

		// Add 'close' button
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 6;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mCloseButton, constraints);
		this.getRootPane().setDefaultButton(mCloseButton);
	}

	/**
	 * Removes a selected item - invoked when the remove button is pressed
	 *
	 * author   Matt Woodard
	 */
	private void removeSelected()
	{
		setMessageText(null);

		if (mSelectedTable.getSelectedRowCount() == 0)
		{
			setMessageText(in_validItem);

			return;
		}

		if (mSelectedTable.getSelectedRowCount() > 1)
		{
			setMessageText(in_deselectOne);

			return;
		}

		final int selectedRow = mSelectedTable.getSelectedRow();

		adjustPool(-getAdjustment(mSelectedTable));

		mSelectedList.remove(selectedRow);

		updateSelectedTable();
		updateButtonStates();
	}

	/**
	 * Selects an available item - invoked when the add button is pressed
	 *
	 * author   Matt Woodard
	 */
	private void selectAvailable()
	{
		setMessageText(null);

		if (getEffectivePool() <= 0)
		{
			setMessageText(in_noRemain);

			return;
		}

		final int selectedRow = mAvailableTable.getSelectedRow();

		if (selectedRow < 0)
		{
			setMessageText(in_validItem);

			return;
		}

		if (mAvailableTable.getSelectedRowCount() > 1)
		{
			setMessageText(in_addOne);

			return;
		}

		final TableModel availableModel = mAvailableTable.getModel();

		final Object selectedObj = availableModel.getValueAt(selectedRow, 0);

		if (mUniqueList.contains(selectedObj))
		{
			// TODO Don't compose messages by concat I18N
			setMessageText(in_selectPartA + " " + selectedObj.toString() + " "
				+ in_selectPartB);

			return;
		}

		final TableModel selectedModel = mSelectedTable.getModel();

		for (int i = 0, count = selectedModel.getRowCount(); i < count; i++)
		{
			Object obj = selectedModel.getValueAt(i, 0);

			if (selectedObj.equals(obj) && !mAllowDuplicates)
			{
				// TODO Don't compose messages by concat I18N
				setMessageText(selectedObj + " " + in_alreadySelected);

				return;
			}
		}

		//
		// Make sure there are enough points remaining...
		//
		final int adjustment = getAdjustment(mAvailableTable);

		if ((getEffectivePool() - adjustment) < 0)
		{
			if (!canGoNegative)
			{
				setMessageText(in_noRemain);

				return;
			}
		}
		
		final int selectedColumns = selectedModel.getColumnCount();

		if (selectedColumns > 1 && selectedRow >= 0)
		{
			// Find the available row this corrisponds to
			// We will be optimistic and try the selected row first
			List columns = (List) mAvailableList.get(selectedRow);
			Object selCol1 = availableModel.getValueAt(selectedRow, 0);
			if (columns.get(0).equals(selCol1))
			{
				mSelectedList.add(columns);
			}
			else
			{
				// Well we will have to search for it
				for (Iterator i = mAvailableList.iterator(); i.hasNext();)
				{
					columns = (List) i.next();
					if (columns.get(0).equals(selCol1))
					{
						mSelectedList.add(columns);
						break;
					}
				}
			}
		}
		else
		{
			mSelectedList.add(selectedObj);
		}

		updateSelectedTable();

		adjustPool(adjustment);

		updateButtonStates();
	}

	/**
	 * Makes a number of checks to determine when to enable buttons
	 *
	 * author   Matt Woodard
	 */
	private void updateButtonStates()
	{
		boolean addEnabled = false;
		boolean removeEnabled = false;
		boolean closeEnabled = false;

		String addToolTip;
		String removeToolTip;
		String closeToolTip;

		if (getEffectivePool() > 0)
		{
			if (!mPoolFlag)
			{
				closeEnabled = true;
				closeToolTip = in_closeChooserTip;
			}
			else
			{
				closeToolTip = in_completeMess;
			}

			int count = mAvailableTable.getSelectedRowCount();

			if (count == 1)
			{
				int availableRow = mAvailableTable.getSelectedRow();

				if ((availableRow >= 0)
					&& (availableRow < mAvailableTable.getRowCount()))
				{
					TableModel availTableModel = mAvailableTable.getModel();

					final Object availObj =
							availTableModel.getValueAt(availableRow, 0);

					if (!mUniqueList.contains(availObj))
					{
						addEnabled = true;
						// TODO Don't compose messages by concat I18N
						addToolTip = in_pressToAdd + " " + availObj.toString();

						for (int i = 0, length = mSelectedTable.getRowCount(); i < length; i++)
						{
							final Object selectedObj =
									mSelectedModel.getValueAt(i, 0);

							if (availObj.equals(selectedObj)
								&& !mAllowDuplicates)
							{
								addEnabled = false;
								// TODO Don't compose messages by concat I18N
								addToolTip =
										availObj + " " + in_alreadySelected;
							}
						}
					}
					else
					{
						// TODO Don't compose messages by concat I18N
						addToolTip =
								in_selectPartA + " " + availObj + " "
									+ in_selectPartB;
					}
				}
				else
				{
					addToolTip = in_validItem;
				}
			}
			else if (count == 0)
			{
				addToolTip = in_validItem;
			}
			else
			{
				addToolTip = in_addOne;
			}
		}
		else
		{
			addToolTip = in_noRemain;
			closeToolTip = in_closeChooserTip;
			closeEnabled = true;
		}

		int count = mSelectedTable.getSelectedRowCount();

		if (count == 1)
		{
			int selectedRow = mSelectedTable.getSelectedRow();

			if ((selectedRow >= 0)
				&& (selectedRow < mSelectedTable.getRowCount()))
			{
				removeEnabled = true;
				// TODO Don't compose messages by concat I18N
				removeToolTip =
						in_pressToRemove
							+ " "
							+ mSelectedTable.getModel().getValueAt(selectedRow,
								0) + ".";
			}
			else
			{
				removeToolTip = in_validItem;
			}
		}
		else if (count == 0)
		{
			removeToolTip = in_validItem;
		}
		else
		{
			removeToolTip = in_removeOne;
		}

		mAddButton.setEnabled(addEnabled);
		mCloseButton.setEnabled(closeEnabled);
		mRemoveButton.setEnabled(removeEnabled);

		Utility.setDescription(mAddButton, addToolTip);
		Utility.setDescription(mCloseButton, closeToolTip);
		Utility.setDescription(mRemoveButton, removeToolTip);
	}

	/**
	 * Updates the available table entries.
	 * <p/>
	 * author   Matt Woodard
	 */
	private void updateAvailableTable()
	{
		// If the columns haven't been initialized, do so now using the default
		if (mAvailableColumnNames == null)
		{
			setAvailableColumnNames(AVAILABLE_COLUMN_NAMES);
		}

		if (mAvailableList.size() > 0 && mAvailableList.get(0) instanceof Comparable)
		{
			Collections.sort(mAvailableList);
		}
		updateTable(mAvailableTable, mAvailableModel, mAvailableList, "");
		//		mAvailableData = updateTable(mAvailableTable, mAvailableModel, mAvailableData,
		//				mAvailableList, "");
	}

	/**
	 * Updates the selected table.
	 * <p/>
	 * author   Matt Woodard
	 */
	private void updateSelectedTable()
	{
		// If the columns haven't been initialized, set the names to the default
		if (mSelectedColumnNames == null)
		{
			// If the available columns aren't default columns, use the same here
			setSelectedColumnNames(mAvailableColumnNames == AVAILABLE_COLUMN_NAMES
				? SELECTED_COLUMN_NAMES : mAvailableColumnNames);
		}

		updateTable(mSelectedTable, mSelectedModel, mSelectedList,
			mSelectedTerminator);
		//		mSelectedData = updateTable(mSelectedTable, mSelectedModel,
		//				mSelectedData, mSelectedList, mSelectedTerminator);
	}

	private static void updateTable(final JTableEx aTable,
		final ChooserTableModel aTableModel, final List anInputList,
		final String aLineTerminator)
	{
		if (anInputList.size() <= 0)
		{
			aTableModel.setData(null, aLineTerminator);
			return;
		}

		Object selectedValue = null;

		// Find the previous selected value.
		int selectedInd = aTable.getSelectedRow();

		if (selectedInd >= 0 && selectedInd < anInputList.size())
		{
			selectedValue = anInputList.get(selectedInd);
		}

		// Clear the previous selected value.
		final ListSelectionModel selectionModel = aTable.getSelectionModel();
		selectionModel.clearSelection();

		// Update the table data from the input list.
		final Object[][] newTableData = new Object[anInputList.size()][];

		int row = 0;

		for (Iterator it = anInputList.iterator(); it.hasNext();)
		{
			final Object rowData = it.next();
			if (rowData instanceof String)
			{
				newTableData[row++] = parseString(rowData.toString());
			}
			else if (rowData instanceof Collection)
			{
				Collection columns = (Collection) rowData;
				final int numColumns = columns.size();
				newTableData[row] = new Object[numColumns];
				int curCol = 0;
				final Iterator colIter = columns.iterator();
				while (colIter.hasNext())
				{
					newTableData[row][curCol++] = colIter.next();
				}
				row++;
			}
			else
			{
				newTableData[row] = new Object[1];
				newTableData[row++][0] = rowData;
			}
		}

		aTableModel.setData(newTableData, aLineTerminator);

		// Restore the previous selected value if possible.
		if (selectedValue != null)
		{
			for (int i = 0, length = aTableModel.getRowCount(); i < length; i++)
			{
				final Object val = aTableModel.getValueAt(i, 0);

				if (selectedValue.equals(val))
				{
					selectionModel.setSelectionInterval(i, i);

					break;
				}
			}
		}
	}

	/**
	 * Parses a tab-delimited string into an array of Strings.
	 *
	 * @param string the delimited string
	 *
	 * @return the embedded strings author Matthew Woodard
	 */
	private static String[] parseString(String string)
	{
		final StringTokenizer tokenizer = new StringTokenizer(string, "\t");

		final String[] results = new String[tokenizer.countTokens()];

		for (int s = 0; tokenizer.hasMoreTokens(); s++)
		{
			results[s] = tokenizer.nextToken();
		}

		return results;
	}

	public void setChoicesPerUnit(int cost)
	{
		selectionsPerUnitCost = cost;
		mSelectedText.setText(Integer.toString(cost));
		setEffectiveUsed();
	}

	public void setTotalChoicesAvail(int avail)
	{
		totalSelectionsAvailable = avail;
		mTotalText.setText(Integer.toString(avail));
		mEffectiveText.setText(Integer.toString(getEffectivePool()));
	}

	public void setPickAll(boolean b)
	{
		pickAll = b;
	}
	
	public boolean pickAll()
	{
		return pickAll;
	}
	
	public int getEffectivePool()
	{
		return selectionsPerUnitCost * totalSelectionsAvailable
				- effectiveUsed;
	}
	
	private void adjustPool(int adjustment)
	{
		effectiveUsed += adjustment;
		mEffectiveText.setText(Integer.toString(getEffectivePool()));
	}
	
	public void setEffectiveUsed()
	{
		if (mCostColumnNumber >= 0)
		{
			effectiveUsed = 0;
			for (Object item : mSelectedList)
			{
				for (int i = mAvailableModel.getRowCount() - 1; i >= 0 ; i--)
				{
					if (item.equals(mAvailableModel.getValueAt(i, 0)))
					{
						Object o = mAvailableModel.getValueAt(i, mCostColumnNumber);
						effectiveUsed += Integer.parseInt(o.toString());
					}
				}
			}
		}
		else
		{
			effectiveUsed = mSelectedList.size();
		}
		mEffectiveText.setText(Integer.toString(getEffectivePool()));
	}
}
