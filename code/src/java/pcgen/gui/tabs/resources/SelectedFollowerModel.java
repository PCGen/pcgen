package pcgen.gui.tabs.resources;

import pcgen.cdom.list.CompanionList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 *  The TreeTableModel has a single <code>root</code> node
 *  This root node has a null <code>parent</code>.
 *  All other nodes have a parent which points to a non-null node.
 *  Parent nodes contain a list of  <code>children</code>, which
 *  are all the nodes that point to it as their parent.
 *  <code>nodes</code> which have 0 children are leafs (the end of
 *  that linked list).  nodes which have at least 1 child are not leafs
 *  Leafs are like files and non-leafs are like directories.
 *  The leafs contain an Object that we want to know about (Equipment)
 **/
public final class SelectedFollowerModel extends AbstractTreeTableModel
		implements TableColumnManagerModel
{
	// column positions for Famliar tables
	// if you change these, you also have to change
	// the case statement in the FollowerModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_FILE = 2;

	private List<Boolean> displayList = null;

	// there are two roots. One for available equipment
	// and one for selected equipment profiles
	private PObjectNode selRoot;

	// list of columns names
	private static String[] selNameList = new String[3];

	private final int[] selDefaultWidth = {200, 100, 100};

	private PlayerCharacter pc;

	// view modes for Item tables
	//private static final int ITEM_VIEW_TYPE = 0;
	//private static final int ITEM_VIEW_NAME = 1;
	// view modes for Vehicle tables
	//private static final int VEHICLE_VIEW_TYPE = 0;
	//private static final int VEHICLE_VIEW_NAME = 1;

	static
	{
		selNameList[0] = LanguageBundle.getString("in_typeName"); //$NON-NLS-1$
		selNameList[1] = LanguageBundle.getString("in_typeRace"); //$NON-NLS-1$
		selNameList[2] = LanguageBundle.getString("in_fileName"); //$NON-NLS-1$
	}

	/**
	 * Creates a FollowerModel
	 * @param aPC the PlayerCharacter to build the model for
	 **/
	public SelectedFollowerModel(PlayerCharacter aPC)
	{
		super(null);

		setCharacter(aPC);

		resetModel();

		int i = 1;
		displayList = new ArrayList<Boolean>();
		displayList.add(Boolean.TRUE);
		displayList.add(Boolean.valueOf(getColumnViewOption(selNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(selNameList[i++],
			true)));
	}

	/**
	 * Returns boolean if can edit a cell.
	 * @param node Not used
	 * @param column Column to check
	 * @return true if cell editable
	 **/
	@Override
	public boolean isCellEditable(@SuppressWarnings("unused")
	Object node, int column)
	{
		return (column == COL_NAME);
	}

	/**
	 * Returns Class for the column
	 * @param column Column to check
	 * @return Class Class of object stored in the column
	 **/
	@Override
	public Class<?> getColumnClass(int column)
	{
		return (column == COL_NAME) ? TreeTableModel.class : String.class;
	}

	/* The JTreeTableNode interface. */

	/**
	 * Returns int number of columns
	 * @return column count
	 **/
	public int getColumnCount()
	{
		return selNameList.length;
	}

	/**
	 * Returns String name of a column
	 * @param column Column to check
	 * @return column name
	 **/
	public String getColumnName(int column)
	{
		return selNameList[column];
	}

	/**
	 * return the root node
	 * @return root
	 **/
	@Override
	public Object getRoot()
	{
		return super.getRoot();
	}

	/**
	 * Set the pc
	 * @param aPC The PlayerCharacter to associate with this model
	 */
	public void setCharacter(PlayerCharacter aPC)
	{
		this.pc = aPC;
	}

	/**
	 * return the value of a column
	 * @param node Node to retrieve value for
	 * @param column Column to retrieve value for
	 * @return value at the Node (row) Column position
	 **/
	public Object getValueAt(Object node, int column)
	{
		final PObjectNode fn = (PObjectNode) node;
		Race race = null;
		Follower fObj = null;
		String sRet = ""; //$NON-NLS-1$

		if (fn == null)
		{
			Logging
				.errorPrint("No active node when doing getValueAt in InfoRace");

			return null;
		}

		if (fn.getItem() instanceof Race)
		{
			race = (Race) fn.getItem();
		}
		else if (fn.getItem() instanceof Follower)
		{
			fObj = (Follower) fn.getItem();
		}

		column = adjustColumnConst(column);

		switch (column)
		{
			case COL_NAME:

				if (race != null)
				{
					return race.getKeyName();
				}
				return fn.toString();

			case COL_TYPE:

				if (fObj != null)
				{
					sRet = fObj.getType().getKeyName();
				}

				break;

			case COL_FILE:

				if (fObj != null)
				{
					sRet = fObj.getFileName();
				}

				break;

			default:
				Logging
					.errorPrint("In InfoResources.FollowerModel.getValueAt the column "
						+ column + " is not handled.");

				break;
		}

		return sRet;
	}

	/**
	 * There must be a root node, but we keep it hidden
	 * @param aNode Node to use as a root
	 **/
	private void setRoot(PObjectNode aNode)
	{
		super.setRoot(aNode);
	}

	/**
	 * changes the column order sequence and/or number of
	 * columns based on modelType (0=available, 1=selected)
	 * @param column Column to set
	 * @return returns the parameter passed in
	 **/
	private int adjustColumnConst(int column)
	{
		return column;
	}

	/**
	 * This assumes the FollowerModel exists but
	 * needs branches and nodes to be repopulated
	 **/
	public void resetModel()
	{
		List<FollowerType> selectedList = buildCurrentCompanionsList();
		buildSelectedResources(selectedList);

		PObjectNode rootAsPObjectNode = (PObjectNode) getRoot();
		if (rootAsPObjectNode.getChildCount() > 0)
		{
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
	}

	/**
	 * @param selectedList The list of follower types
	 */
	private void buildSelectedResources(final List<FollowerType> selectedList)
	{
		// this is the root node
		selRoot = new PObjectNode();
		setRoot(selRoot);

		PObjectNode[] sl = new PObjectNode[selectedList.size()];

		for (int iSel = 0; iSel < selectedList.size(); iSel++)
		{
			final FollowerType followerType = selectedList.get(iSel);
			sl[iSel] = new PObjectNode();
			sl[iSel].setItem(followerType);
			sl[iSel].setParent(selRoot);

			for (Follower aF : pc.getFollowerList())
			{
				if (!followerType.getType().equals(aF.getType()))
				{
					continue;
				}

				PObjectNode aFN = new PObjectNode();
				aFN.setItem(aF);
				aFN.setParent(sl[iSel]);
				sl[iSel].addChild(aFN);
				followerType.incrementFollowerCount(1);
			}
		}

		// new add to the root node
		selRoot.setChildren(sl);
	}

	/**
	 =	 *  A wrapper class to associate a follower type with the
	 *  number of followers of that type.
	 */
	public class FollowerType
	{
		private CompanionList theType;
		private int theMaxNumber = -1;
		private int theSelectedNumber = 0;
		private boolean theDisplayNumberFlag = true;

		public FollowerType(final CompanionList compType, final int aMax)
		{
			theType = compType;
			theMaxNumber = aMax;
		}

		public void setDisplayNumber(final boolean yesNo)
		{
			theDisplayNumberFlag = yesNo;
		}

		public CompanionList getType()
		{
			return theType;
		}

		public void incrementFollowerCount(final int aCount)
		{
			theSelectedNumber += aCount;
		}

		public int getNumRemaining()
		{
			if (theMaxNumber < 0)
			{
				return theMaxNumber;
			}
			return theMaxNumber - theSelectedNumber;
		}

		/**
		 * Returns a String version of this object.  Uses the display
		 * flag to know if the max number should be included.
		 * @return string representation
		 */
		@Override
		public String toString()
		{
			final StringBuffer buf = new StringBuffer();
			buf.append(CoreUtility.capitalizeFirstLetter(theType.getDisplayName()));
			if (theDisplayNumberFlag)
			{
				buf.append(" ("); //$NON-NLS-1$
				buf.append(theSelectedNumber);
				buf.append("/"); //$NON-NLS-1$
				if (theMaxNumber > 0)
				{
					buf.append(theMaxNumber);
				}
				else
				{
					buf.append("*"); //$NON-NLS-1$
				}
				buf.append(")"); //$NON-NLS-1$
			}
			return buf.toString();
		}
	}

	/**
	 * @return current companion types list
	 */
	private List<FollowerType> buildCurrentCompanionsList()
	{
		ArrayList<FollowerType> selectedList = new ArrayList<FollowerType>();

		for (CompanionList compType : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			// Check if we have a number set for this type
			int maxVal = pc.getMaxFollowers(compType);
			if (maxVal != 0)
			{
				selectedList.add(new FollowerType(compType, maxVal));
			}
		}

		return selectedList;
	}

	/**
	 * Returns the selected items
	 * @return the selected items
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnList()
	 */
	public List<String> getMColumnList()
	{
		List<String> retList = new ArrayList<String>();
		for (int i = 1; i < selNameList.length; i++)
		{
			retList.add(selNameList[i]);
		}
		return retList;
	}

	/**
	 * Is the specified column visible
	 * @param col The column to check
	 * @return true if the column is displayed
	 * @see pcgen.gui.TableColumnManagerModel#isMColumnDisplayed(int)
	 */
	public boolean isMColumnDisplayed(int col)
	{
		return displayList.get(col).booleanValue();
	}

	/**
	 *
	 * @param col int
	 * @param disp boolean
	 */
	public void setMColumnDisplayed(int col, boolean disp)
	{
		setColumnViewOption(selNameList[col], disp);
		displayList.set(col, Boolean.valueOf(disp));
	}

	/**
	 * 
	 * @return int
	 */
	public int getMColumnOffset()
	{
		return 1;
	}

	/**
	 *
	 * @param col int
	 * @return int
	 */
	public int getMColumnDefaultWidth(int col)
	{
		return SettingsHandler.getPCGenOption(
			"InfoResources.FollowerModel.sizecol." + selNameList[col],
			selDefaultWidth[col]);
	}

	/**
	 *
	 * @param col int
	 * @param width int
	 */
	public void setMColumnDefaultWidth(int col, int width)
	{
		SettingsHandler.setPCGenOption("InfoResources.FollowerModel.sizecol."
			+ selNameList[col], width);
	}

	private boolean getColumnViewOption(String colName, boolean defaultVal)
	{
		return SettingsHandler.getPCGenOption(
			"InfoResources.FollowerModel.viewcol." + colName, defaultVal);
	}

	private void setColumnViewOption(String colName, boolean val)
	{
		SettingsHandler.setPCGenOption("InfoResources.FollowerModel.viewcol."
			+ colName, val);
	}

	/**
	 *
	 * @param col int
	 * @param column TableColumn
	 */
	public void resetMColumn(int col, TableColumn column)
	{
		// TODO Auto-generated method stub

	}
}
