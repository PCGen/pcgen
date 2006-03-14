package pcgen.gui.tabs.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.tree.TreePath;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.character.Follower;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.util.Logging;

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
{
	// column positions for Famliar tables
	// if you change these, you also have to change
	// the case statement in the FollowerModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_SIZE = 1;
	private static final int COL_MOVE = 2;
	private static final int COL_VISION = 3;
	private static final int COL_PRE = 4;
	private static final int COL_TYPE = 5;
	private static final int COL_SOURCE = 6;

	// view modes for Famliar tables
	private static final int VIEW_TYPE = 0;
	private static final int VIEW_NAME = 1;
	private static final int VIEW_RACETYPE = 2;

	// there are two roots. One for available equipment
	// and one for selected equipment profiles
	private PObjectNode avaRoot;

	// list of columns names
	private String[] avaNameList = { "" };

	private PlayerCharacter pc;

	// view modes for Item tables
	//private static final int ITEM_VIEW_TYPE = 0;
	//private static final int ITEM_VIEW_NAME = 1;
	// view modes for Vehicle tables
	//private static final int VEHICLE_VIEW_TYPE = 0;
	//private static final int VEHICLE_VIEW_NAME = 1;

	/**
	 * Creates a FollowerModel
	 *
	 * @param pc The Player Character
	 * @param viewMode The mode
	 */
	public AvailableFollowerModel(PlayerCharacter pc, int viewMode)
	{
		super(null);

		setCharacter(pc);

		//
		// if you change/add/remove entries to nameList
		// you also need to change the static COL_XXX defines
		// at the begining of this file
		//
		avaNameList = new String[]{ "Type/Name", "Size", "Speed", "Vision", "Alignment", "Type/Race", "Source" };
		resetModel(viewMode);
	}

	/**
	 * Returns boolean if can edit a cell.
	 * @param node
	 * @param column
	 * @return true if cell editable
	 **/
	public boolean isCellEditable(Object node, int column)
	{
		return (column == COL_NAME);
	}

	/**
	 * Returns Class for the column
	 * @param column
	 * @return Class
	 **/
	public Class getColumnClass(int column)
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
	 **/
	public Object getRoot()
	{
		return (PObjectNode) super.getRoot();
	}

	/**
	 * Set pc
	 * @param pc
	 */
	public void setCharacter(PlayerCharacter pc) {
		this.pc = pc;
	}

	/**
	 * return the value of a column
	 * @param node
	 * @param column
	 * @return value
	 **/
	public Object getValueAt(Object node, int column)
	{
		final PObjectNode fn = (PObjectNode) node;
		Race race = null;
		String sRet = "";

		if (fn == null)
		{
			Logging.errorPrint("No active node when doing getValueAt in InfoRace");

			return null;
		}

		if (fn.getItem() instanceof Race)
		{
			race = (Race) fn.getItem();
		}
		else if (fn.getItem() instanceof Follower)
		{
			fn.getItem();
		}

		column = adjustColumnConst(column);

		switch (column)
		{
			case COL_NAME:

				if (race != null)
				{
					return race.getName();
				}
				return fn.toString();

			case COL_SIZE:

				if (race != null)
				{
					sRet = race.getSize();
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
				if (race.getMovement() != null) {
					sRet = race.getMovement().toString();
				}

				break;

			case COL_VISION:

				if (race != null)
				{
					sRet = race.getDisplayVision(this.pc);
				}

				break;

			case COL_PRE:

				if (race != null)
				{
					sRet = race.preReqHTMLStrings(this.pc);
				}

				break;

			case COL_TYPE:

				if (race != null)
				{
					if ("None".equals(race.getDisplayName()))
					{
						sRet = race.getType();
					}
					else
					{
						sRet = race.getDisplayName();
					}
				}

				break;

			case COL_SOURCE:

				if (race != null)
				{
					sRet = race.getSource();
				}

				break;

			default:
				Logging.errorPrint("In InfoResources.FollowerModel.getValueAt the column " + column
					+ " is not handled.");

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
	public void resetModel(int viewMode)
	{
		avaRoot = new PObjectNode();
		setRoot(avaRoot);

		switch (viewMode)
		{
			case VIEW_NAME:
			{
				// iterate through the names
				// and fill out the tree
				Collection raceList = Globals.getRaceMap().values();
				ArrayList rn = new ArrayList(raceList.size());

				for (Iterator iRace = raceList.iterator(); iRace.hasNext(); )
				{
					final Race aRace = (Race) iRace.next();

					if (aRace != null)
					{
						PObjectNode node = new PObjectNode();
						node.setItem(aRace);
						node.setParent(avaRoot);
						rn.add(node);
					}
				}

				// now add to the root node
				avaRoot.setChildren(rn);

				break;
			}

			case VIEW_TYPE:
			{
				// build the list of races and types
				ArrayList raceList = new ArrayList();
				ArrayList typeList = new ArrayList();

				for (Iterator iRace = Globals.getRaceMap().values().iterator();
					 iRace.hasNext(); )
				{
					final Race aRace = (Race) iRace.next();

					if (!raceList.contains(aRace))
					{
						raceList.add(aRace);
					}

					if (!typeList.contains(aRace.getType()))
					{
						typeList.add(aRace.getType());
					}
				}

				Collections.sort(typeList);

				//build the TYPE root nodes
				PObjectNode[] rt = new PObjectNode[typeList.size()];

				// iterate through the types
				// and fill out the tree
				for (int iType = 0; iType < typeList.size(); iType++)
				{
					final String aType = (String) typeList.get(iType);
					rt[iType] = new PObjectNode();
					rt[iType].setItem(aType);

					for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext();)
					{
						final Race aRace = (Race) fI.next();

						if (aRace == null)
						{
							continue;
						}

						if (!aRace.getType().equals(aType))
						{
							continue;
						}

						PObjectNode aFN = new PObjectNode(aRace);
						rt[iType].addChild(aFN);
					}

					// if it's not empty, add it
					if (!rt[iType].isLeaf())
					{
						rt[iType].setParent(avaRoot);
					}
				}

				// now add to the root node
				avaRoot.setChildren(rt);
				break;
			}

			case VIEW_RACETYPE:
			{
				// build the list of races and types
				ArrayList raceList = new ArrayList();
				ArrayList typeList = new ArrayList();

				for (Iterator iRace = Globals.getRaceMap().values().iterator();
					 iRace.hasNext(); )
				{
					final Race aRace = (Race) iRace.next();

					if (!raceList.contains(aRace))
					{
						raceList.add(aRace);
					}

					if (!typeList.contains(aRace.getRaceType()))
					{
						typeList.add(aRace.getRaceType());
					}
				}

				Collections.sort(typeList);

				//build the TYPE root nodes
				PObjectNode[] rt = new PObjectNode[typeList.size()];

				// iterate through the types
				// and fill out the tree
				for (int iType = 0; iType < typeList.size(); iType++)
				{
					final String aType = (String) typeList.get(iType);
					rt[iType] = new PObjectNode();
					rt[iType].setItem(aType);

					for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext();)
					{
						final Race aRace = (Race) fI.next();

						if (aRace == null)
						{
							continue;
						}

						if (!aRace.getRaceType().equals(aType))
						{
							continue;
						}

						PObjectNode aFN = new PObjectNode(aRace);
						rt[iType].addChild(aFN);
					}

					// if it's not empty, add it
					if (!rt[iType].isLeaf())
					{
						rt[iType].setParent(avaRoot);
					}
				}

				// now add to the root node
				avaRoot.setChildren(rt);
				break;
			}
		}

		PObjectNode rootAsPObjectNode = (PObjectNode) getRoot();
		if (rootAsPObjectNode.getChildCount() > 0)
		{
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
	}
}
