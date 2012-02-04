
/*
 * SwingChooserUserInput.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 2 Mar 2007
 *
 * $$Id$$
 */
package pcgen.gui.utils;

import java.awt.BorderLayout;
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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.gui.utils.chooser.ChooserTableModel;
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserInterface;

/**
 * This dialog type accepts text entry of items, a choice
 * limit, and some additional flags and switches. The user can
 * enter and remove values until the required number of
 * choices have been made. The dialog is always modal, so a
 * call to show() will block program execution.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author    James Dempsey
 * @version $Revision$
 */
public final class SwingChooserUserInput extends JDialog implements ChooserInterface
{
	static final long serialVersionUID = -2148735105737308335L;
	private static final String in_uichooser_value;
	private static final String in_selected;
	private static final String in_completeMess;
	private static final String in_chooser;
	private static final String in_selRemain;
	private static final String in_validItem;
	private static final String in_deselectOne;
	private static final String in_noRemain;
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
		in_uichooser_value = LanguageBundle.getString("in_uichooser_value");
		in_selected = LanguageBundle.getString("in_selected");
		in_completeMess = LanguageBundle.getString("in_completeMess");
		in_chooser = LanguageBundle.getString("in_chooser");
		in_selRemain = LanguageBundle.getString("in_selRemain");
		in_validItem = LanguageBundle.getString("in_validItem");
		in_deselectOne = LanguageBundle.getString("in_deselectOne");
		in_noRemain = LanguageBundle.getString("in_noRemain");
		in_selectPartA = LanguageBundle.getString("in_selectPartA");
		in_selectPartB = LanguageBundle.getString("in_selectPartB");
		in_alreadySelected = LanguageBundle.getString("in_alreadySelected");
		in_closeChooserTip = LanguageBundle.getString("in_closeChooserTip");
		in_pressToAdd = LanguageBundle.getString("in_pressToAdd");
		in_pressToRemove = LanguageBundle.getString("in_pressToRemove");
		in_removeOne = LanguageBundle.getString("in_removeOne");
	}

	/** The default selected list column array */
	private static final List<String> SELECTED_COLUMN_NAMES =
			Arrays.asList(new String[]{in_selected});

	/** The model table for the selected item table */
	private ChooserTableModel mSelectedModel = new ChooserTableModel();

	/** The JButton for adding available items to the selected list */
	private JButton mAddButton;

	/** The JButton for closing the dialog */
	private JButton mCloseButton;

	/** The JButton for removing selected items */
	private JButton mRemoveButton;

	/** The JLabel showing the remaining pool */
	private JLabel mPoolText;

	/** The JLabel showing messages */
	private JLabelPane mMessageText;

	/** The label for the available text entry */
	private JLabel mAvailLabel;
	
	/** The JTextField for the available text entry */
	private JTextField mAvailableText;

	/** The JTableEx holding selected items */
	private JTableEx mSelectedTable;

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

	private int selectionsPerUnitCost = 1;
	
	private int totalSelectionsAvailable = 1;
	
	/**
	 * Chooser constructor.
	 */
	public SwingChooserUserInput()
	{
		super(Globals.getCurrentFrame());
		initComponents();
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 */
	public void setAllowsDups(boolean aBool)
	{
		mAllowDuplicates = aBool;
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 */
	public void setCostColumnNumber(final int costColumnNumber)
	{
		// Ignored
	}

	/**
	 * Sets the message text. HTML text is supported.
	 *
	 * @param argMessageText  The message to be displayed on the chooser.
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
		//TODO Need to update the UI!
		//mPoolText.setText(Integer.toString(mPool));
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
	 * are updated before showing the dialog. Note that the user input 
	 * chooser has no default input as it requires a user to input text. 
	 *
	 * @param visible true to show the chooser, false to hide it.
	 */
	public void setVisible(boolean visible)
	{
		updateSelectedTable();
		updateButtonStates();

		Window owner = getOwner();
		Rectangle ownerBounds = owner.getBounds();
		Rectangle bounds = getBounds();

		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		setBounds(
			(int) (owner.getX() + ((ownerBounds.getWidth() - width) / 2)),
			(int) (owner.getY() + ((ownerBounds.getHeight() - height) / 2)),
			width, height);

		super.setVisible(visible);
	}

	private int getAdjustment(String textToAdd)
	{
		int adjustment = 1;
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
		// ignored
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object. This is 
	 * ignored for the user input chooser as there is no available list. 
	 *
	 * @param availableList  The new AvailableList value
	 */
	public void setAvailableList(List availableList)
	{
		// Ignored as there is no list of available entries
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
		setSize(new Dimension(640, 300));
		setTitle(in_chooser);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		// Create tables
		TableSorter sorter = new TableSorter(mSelectedModel = new ChooserTableModel());

		final JScrollPane selectedScrollPane =
				new JScrollPane(mSelectedTable = new JTableEx(sorter));
		selectedScrollPane
			.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedScrollPane
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sorter.addMouseListenerToHeaderInTable(mSelectedTable);

		// Initialize user input entry fields
		JPanel availPanel = new JPanel();
		//availPanel.setLayout(new BorderLayout());
		mAvailLabel = new JLabel(in_uichooser_value);
		availPanel.add(mAvailLabel, BorderLayout.WEST);
		mAvailableText = new JTextField(20);
		availPanel.add(mAvailableText, BorderLayout.EAST);

		// Initialize selection types & events
		final CaretListener caretListener = new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				updateButtonStates();
			}
		};
		mAvailableText.addCaretListener(caretListener);
		final ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectAvailable();
			}
		};
		mAvailableText.addActionListener(actionListener);

		final ListSelectionModel selectedSelectionModel =
				mSelectedTable.getSelectionModel();

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

		selectedSelectionModel.addListSelectionListener(listSelectionListener);

		// Initialize the mouse events
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
		final JLabel selectionRemainingLabel = new JLabel(in_selRemain + ": ");

		// Create these labels with " " to force them to layout correctly
		mMessageText = new JLabelPane();
		mMessageText.setBackground(contentPane.getBackground());
		setMessageText(null);

		mPoolText = new JLabel(" ");

		// Create buttons
		mAddButton = new JButton(LanguageBundle.getString("in_add"));
		mAddButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_add"));
		mCloseButton = new JButton(LanguageBundle.getString("in_close"));
		mCloseButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_close"));
		mRemoveButton = new JButton(LanguageBundle.getString("in_remove"));
		mRemoveButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_remove"));

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
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPane.add(availPanel, constraints);

		// Add 'add' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mAddButton, constraints);

		// Add selected list
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(selectedScrollPane, constraints);

		// Add 'remove' button
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mRemoveButton, constraints);

		// Add message text
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
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
		contentPane.add(selectionRemainingLabel, constraints);

		// Add selection remaining field
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.weightx = 1.0;
		constraints.weighty = 0.01;
		constraints.insets = new Insets(0, 4, 4, 0);
		contentPane.add(mPoolText, constraints);

		// Add 'close' button
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 5;
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

		final String availText = mAvailableText.getText();

		if (mUniqueList.contains(availText))
		{
			// TODO Don't compose messages by concat I18N
			setMessageText(in_selectPartA + " " + availText.toString() + " "
				+ in_selectPartB);

			return;
		}

		final TableModel selectedModel = mSelectedTable.getModel();

		for (int i = 0, count = selectedModel.getRowCount(); i < count; i++)
		{
			Object obj = selectedModel.getValueAt(i, 0);

			if (availText.equals(obj) && !mAllowDuplicates)
			{
				// TODO Don't compose messages by concat I18N
				setMessageText(availText + " " + in_alreadySelected);

				return;
			}
		}

		//
		// Make sure there are enough points remaining...
		//
		final int adjustment = getAdjustment(availText);

		if ((getEffectivePool() - adjustment) < 0)
		{
			if (!canGoNegative)
			{
				setMessageText(in_noRemain);

				return;
			}
		}

		mSelectedList.add(availText);
		updateSelectedTable();
		mAvailableText.setText(Constants.EMPTY_STRING);		
		setPool(getEffectivePool() - adjustment);

		updateButtonStates();
	}

	/**
	 * Makes a number of checks to determine when to enable buttons
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

			if (mAvailableText.getText().length() > 0)
			{
				final String availText = mAvailableText.getText();
				if (!mUniqueList.contains(availText))
				{
					addEnabled = true;
					// TODO Don't compose messages by concat I18N
					addToolTip = in_pressToAdd + " " + availText;

					for (int i = 0, length = mSelectedTable.getRowCount(); i < length; i++)
					{
						final Object selectedObj =
								mSelectedModel.getValueAt(i, 0);

						if (availText.equals(selectedObj) && !mAllowDuplicates)
						{
							addEnabled = false;
							// TODO Don't compose messages by concat I18N
							addToolTip = availText + " " + in_alreadySelected;
						}
					}
				}
				else
				{
					// TODO Don't compose messages by concat I18N
					addToolTip =
							in_selectPartA + " " + availText + " "
								+ in_selectPartB;
				}
			}
			else
			{
				addToolTip = in_noRemain;
				closeToolTip = in_closeChooserTip;
				closeEnabled = true;
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
			setSelectedColumnNames(SELECTED_COLUMN_NAMES);
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
	}

	public void setTotalChoicesAvail(int avail)
	{
		totalSelectionsAvailable = avail;
	}

	public int getEffectivePool()
	{
		return selectionsPerUnitCost * totalSelectionsAvailable
				- mSelectedList.size();
	}

	public boolean pickAll()
	{
		return false;
	}

	public void setPickAll(boolean b)
	{
		throw new UnsupportedOperationException();
	}
}
