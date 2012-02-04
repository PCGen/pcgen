package pcgen.gui.tabs.resources;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.list.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.Movement;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.core.display.VisionDisplay;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.HTMLUtils;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.SourcedFollower;
import pcgen.gui.utils.TreeTableModel;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

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
public final class AvailableFollowerModel extends AbstractTreeTableModel
		implements TableColumnManagerModel
{
	private final class RaceTypeComparator implements
			Comparator<FollowerOption>
	{
		public int compare(final FollowerOption anO1,
			final FollowerOption anO2)
		{
			final Race r1 = anO1.getRace();
			final Race r2 = anO2.getRace();
			if (r1 == null)
			{
				return 1;
			}
			if (r2 == null)
			{
				return -1;
			}
			final Collator col = Collator.getInstance();
			RaceType rt1 = r1.get(ObjectKey.RACETYPE);
			RaceType rt2 = r2.get(ObjectKey.RACETYPE);
			String rt1Name = rt1 == null ? "ZZZZZ" : rt1.toString();
			String rt2Name = rt2 == null ? "ZZZZZ" : rt2.toString();
			final int diff = col.compare(rt1Name, rt2Name);
			if (diff == 0)
			{
				return anO1.compareTo(anO2);
			}
			return diff;
		}
	}

	private final class AdjustmentComparator implements
			Comparator<FollowerOption>
	{
		public int compare(final FollowerOption anO1,
			final FollowerOption anO2)
		{
			final int diff =
					anO2.getAdjustment()
						- anO1.getAdjustment();
			if (diff == 0)
			{
				return anO1.compareTo(anO2);
			}
			return diff;
		}
	}

	// column positions for Famliar tables
	// if you change these, you also have to change
	// the case statement in the FollowerModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_ADJUSTMENT = 7;
	private static final int COL_SIZE = 1;
	private static final int COL_MOVE = 2;
	private static final int COL_VISION = 3;
	private static final int COL_PRE = 4;
	private static final int COL_TYPE = 5;
	private static final int COL_SOURCE = 6;

	// view modes for Famliar tables
	private static final int VIEW_ADJUSTMENT = 1;
	private static final int VIEW_NAME = 0;
	private static final int VIEW_RACETYPE = 2;

	private List<Boolean> displayList = null;

	private PObjectNode avaRoot;

	// list of columns names
	private String[] avaNameList =
			new String[]{LanguageBundle.getString("in_typeName"), //$NON-NLS-1$
				LanguageBundle.getString("in_size"), //$NON-NLS-1$
				LanguageBundle.getString("in_speed"), //$NON-NLS-1$
				LanguageBundle.getString("in_vision"), //$NON-NLS-1$
				LanguageBundle.getString("in_alignLabel"), //$NON-NLS-1$
				LanguageBundle.getString("in_typeRace"), //$NON-NLS-1$
				LanguageBundle.getString("in_source"), //$NON-NLS-1$
				LanguageBundle.getString("in_adjustment") //$NON-NLS-1$
			};

	private final int[] avaDefaultWidth =
			{200, 100, 100, 100, 100, 100, 100, 100};

	private PlayerCharacter pc;

	/**
	 * Creates a FollowerModel
	 *
	 * @param aPC The Player Character
	 * @param viewMode The mode
	 */
	public AvailableFollowerModel(final PlayerCharacter aPC, final int viewMode)
	{
		super(null);

		setCharacter(aPC);

		//
		// if you change/add/remove entries to nameList
		// you also need to change the static COL_XXX defines
		// at the begining of this file
		//
		resetModel(viewMode);

		int i = 1;
		displayList = new ArrayList<Boolean>();
		displayList.add(Boolean.TRUE);
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
		displayList.add(Boolean.valueOf(getColumnViewOption(avaNameList[i++],
			true)));
	}

	/**
	 * Returns boolean if can edit a cell.
	 * @param node
	 * @param column
	 * @return true if cell editable
	 **/
	@Override
	public boolean isCellEditable(Object node, int column)
	{
		return (column == COL_NAME);
	}

	/**
	 * Returns Class for the column
	 * @param column
	 * @return Class
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
		return avaNameList.length;
	}

	/**
	 * Returns String name of a column
	 * @param column
	 * @return column name
	 **/
	public String getColumnName(int column)
	{
		return avaNameList[column];
	}

	/**
	 * return the root node
	 * @return root
	 */
	@Override
	public Object getRoot()
	{
		return super.getRoot();
	}

	/**
	 * Set pc
	 * @param aPC The PlayerCharacter this model is associated with
	 */
	public void setCharacter(final PlayerCharacter aPC)
	{
		this.pc = aPC;
	}

	/**
	 * return the value of a column
	 * @param node
	 * @param aColumn
	 * @return value
	 **/
	public Object getValueAt(Object node, int aColumn)
	{
		final PObjectNode fn = (PObjectNode) node;
		Race race = null;
		String sRet = Constants.EMPTY_STRING;

		if (fn == null)
		{
			Logging
				.errorPrintLocalised(
					"Errors.TreeTableModel.NoActiveNode", this.getClass().toString()); //$NON-NLS-1$

			return null;
		}

		FollowerOption option = null;
		if (fn.getItem() instanceof SourcedFollower)
		{
			SourcedFollower sf = (SourcedFollower) fn.getItem();
			option = sf.option;
			race = option.getRace();
		}
		else if (fn.getItem() instanceof Race)
		{
			race = (Race) fn.getItem();
		}
		else if (fn.getItem() instanceof Follower)
		{
			fn.getItem();
		}

		final int column = adjustColumnConst(aColumn);

		switch (column)
		{
			case COL_NAME:

				if (race != null)
				{
					return race.getDisplayName();
				}
				return fn.toString();

			case COL_SIZE:

				if (race != null)
				{
					Formula sz = race.get(FormulaKey.SIZE);
					sRet = sz == null ? "" : sz.toString();
				}

				break;

			case COL_MOVE:

				if (race == null)
				{
					return null;
				}

				/*
				 * CONSIDER This is weird... if movement null, return "", if
				 * race null, return null That's inconsistent and should
				 * probably be cleaned up...
				 */
				List<Movement> movements = race.getListFor(ListKey.MOVEMENT);
				if (movements != null && !movements.isEmpty())
				{
					sRet = movements.get(0).toString();
				}

				break;

			case COL_VISION:

				if (race != null)
				{
					sRet = VisionDisplay.getVision(this.pc, race);
				}

				break;

			case COL_PRE:

				if (race != null)
				{
					sRet = PrerequisiteUtilities.preReqHTMLStringsForList(this.pc, null,
					race.getPrerequisiteList(), true);
				}

				break;

			case COL_TYPE:

				if (race != null)
				{
					RaceType rt = race.get(ObjectKey.RACETYPE);
					sRet = rt == null ? "" : rt.toString();
				}

				break;

			case COL_SOURCE:

				if (race != null)
				{
					sRet = SourceFormat.getFormattedString(race,
					Globals.getSourceDisplay(), true);
				}

				break;

			case COL_ADJUSTMENT:
				if (option != null)
				{
					return String.valueOf(option.getAdjustment());
				}
				break;

			default:
				Logging.errorPrintLocalised("Errors.FollowerModel", column, //$NON-NLS-1$
					this.getClass().toString());

				break;
		}

		return sRet;
	}

	/**
	 * There must be a root node, but we keep it hidden
	 * @param aNode
	 **/
	private void setRoot(PObjectNode aNode)
	{
		super.setRoot(aNode);
	}

	/**
	 * changes the column order sequence and/or number of
	 * columns based on modelType (0=available, 1=selected)
	 * @param column
	 * @return int
	 **/
	private int adjustColumnConst(int column)
	{
		return column;
	}

	/**
	 * This assumes the FollowerModel exists but
	 * needs branches and nodes to be repopulated
	 *
	 * @param viewMode
	 */
	public void resetModel(final int viewMode)
	{
		avaRoot = new PObjectNode();
		setRoot(avaRoot);
		final String qFilter = this.getQFilter();

		for (CompanionList compList : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			// Check if we have a number set for this type
			final int maxVal = pc.getMaxFollowers(compList);
			if (maxVal != 0)
			{
				final PObjectNode node = new PObjectNode();
				String compType = compList.getKeyName();
				node
					.setDisplayName(CoreUtility.capitalizeFirstLetter(compType));
				avaRoot.addChild(node);

				final Map<FollowerOption, CDOMObject> followers;
				switch (viewMode)
				{
					case VIEW_NAME:
					default: {
						followers = pc.getAvailableFollowers(compType, null);
						break;
					}
					case VIEW_ADJUSTMENT: {
						followers = pc.getAvailableFollowers(compType, 
							new AdjustmentComparator());
						break;
					}
					case VIEW_RACETYPE: {
						followers = pc.getAvailableFollowers(compType, 
							new RaceTypeComparator());
						break;
					}
				}
				for (final Map.Entry<FollowerOption, CDOMObject> me : followers.entrySet())
				{
					FollowerOption follower = me.getKey();
					if (qFilter == null
						|| follower.getRace().getDisplayName().toLowerCase().indexOf(qFilter) >= 0)
					{
						final PObjectNode fol = new PObjectNode(new SourcedFollower(follower, me.getValue()));
						final StringBuffer buf = new StringBuffer();
						final boolean qual = follower.qualifies(pc, me.getValue());
						if (!qual)
						{
							buf.append(HTMLUtils.HTML);
							buf.append(SettingsHandler
								.getPrereqFailColorAsHtmlStart());
						}
						buf.append(follower.getRace().getDisplayName());
						if (!qual)
						{
							buf.append(SettingsHandler
								.getPrereqFailColorAsHtmlEnd());
							buf.append(HTMLUtils.END_HTML);
						}
						fol.setDisplayName(buf.toString());
						node.addChild(fol);
					}
				}
			}
		}

		PObjectNode rootAsPObjectNode = (PObjectNode) getRoot();
		if (rootAsPObjectNode.getChildCount() > 0)
		{
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
	}

	/**
	 * Gets the list of column names.
	 * @return List of column names
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnList()
	 */
	public List<String> getMColumnList()
	{
		List<String> retList = new ArrayList<String>();
		for (int i = 1; i < avaNameList.length; i++)
		{
			retList.add(avaNameList[i]);
		}
		return retList;
	}

	/**
	 * Checks if the specified column is visible.
	 * @param col The column to test visibility for
	 * @return true if the column is visible
	 * @see pcgen.gui.TableColumnManagerModel#isMColumnDisplayed(int)
	 */
	public boolean isMColumnDisplayed(int col)
	{
		return displayList.get(col).booleanValue();
	}

	/**
	 * Turns on or off display of the specified column.  
	 * @param col The column to change
	 * @param disp true if the column should be displayed.
	 * @see pcgen.gui.TableColumnManagerModel#setMColumnDisplayed(int, boolean)
	 */
	public void setMColumnDisplayed(int col, boolean disp)
	{
		setColumnViewOption(avaNameList[col], disp);
		displayList.set(col, Boolean.valueOf(disp));
	}

	/**
	 * Returns the offset of the first configurable column.  Returns the second
	 * column in this case.
	 * @return The second column in the table
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnOffset()
	 */
	public int getMColumnOffset()
	{
		return 1;
	}

	/**
	 * Gets the starting width of the specified column.
	 * @param col Column to get width for
	 * @return The width of the specified column.
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnDefaultWidth(int)
	 */
	public int getMColumnDefaultWidth(int col)
	{
		return SettingsHandler
			.getPCGenOption(
				"InfoResources.AFollowerModel.sizecol." + avaNameList[col], avaDefaultWidth[col]); //$NON-NLS-1$
	}

	/**
	 * Sets a starting (before user modification) witdth for the column 
	 * specified.
	 * @param col The column to set width for
	 * @param width The width to set
	 * @see pcgen.gui.TableColumnManagerModel#setMColumnDefaultWidth(int, int)
	 */
	public void setMColumnDefaultWidth(int col, int width)
	{
		SettingsHandler.setPCGenOption(
			"InfoResources.AFollowerModel.sizecol." + avaNameList[col], width); //$NON-NLS-1$
	}

	private boolean getColumnViewOption(String colName, boolean defaultVal)
	{
		return SettingsHandler.getPCGenOption(
			"InfoResources.AFollowerModel.viewcol." + colName, defaultVal); //$NON-NLS-1$
	}

	private void setColumnViewOption(String colName, boolean val)
	{
		SettingsHandler.setPCGenOption(
			"InfoResources.AFollowerModel.viewcol." + colName, val); //$NON-NLS-1$
	}

	/**
	 * @param col
	 * @param column
	 * @see pcgen.gui.TableColumnManagerModel#resetMColumn(int, javax.swing.table.TableColumn)
	 */
	public void resetMColumn(@SuppressWarnings("unused")
	int col, @SuppressWarnings("unused")
	TableColumn column)
	{
		// Nothing to do here.
	}
}
