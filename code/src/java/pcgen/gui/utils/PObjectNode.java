/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 29, 2001, 7:15 PM
 * Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ListIterator;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.util.Logging;
import pcgen.util.ResetableListIterator;

/**
 * <code>PObjectNode</code> -- No, binkley isn't really the author, I
 * it is Bryan, but <em>somebody</em> has got to take code ownership and
 * clean this up.  Options include replacing with
 * <code>DefaultMutableTreeNode</code> or
 * <code>JTree.DynamicUtilTreeNode</code> (radical change); or cleaning
 * up memory allocation and fixing all the spots in Info*.java which make
 * assumptions about getChildren().length, <i>etc.</i>.
 *
 * TODO: This class overrides equals(Object), but does not override
 * hashCode(), and inherits the implementation of hashCode() from
 * java.lang.Object (which returns the identity hash code, an arbitrary
 * value assigned to the object by the VM). Therefore, the class is very
 * likely to violate the invariant that equal objects must have equal
 * hashcodes.
 *
 * TODO: This class implements the java.util.Iterator interface. However,
 * its next() method is not capable of throwing
 * java.util.NoSuchElementException. The next() method should be changed
 * so it throws NoSuchElementException if is called when there are no more
 * elements to return.
 *
 * @author B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision$
 */

// Consider replacing ResetableListIterator with ResetableListIterator, if any
// classes look like they want to be bidirectional.
public class PObjectNode implements Cloneable, ResetableListIterator
{
	private static PlayerCharacter aPC = null;
	public static final int NOT_A_FEAT = 0;
	public static final int CAN_GAIN_FEAT = 1;
	public static final int CAN_USE_FEAT = 2;
	private ArrayList<PObjectNode> children = null;
	private Object item = null; // could be a String, could be a Feat (or anything subclassed from PObject)
	private PObjectNode parent = null;
	private int checkFeatState = NOT_A_FEAT; // feat tab
	private String displayName = null;

	private int theColor = -1;

	// All this is for free when we get rid of PObjectNode[] for a
	// Collection.  XXX
	private int mark = 0;

	/** Constructor for PObjectNode */
	public PObjectNode()
	{
		// Empty Constructor
	}

	/** Constructor for PObjectNode with an item
	 * @param item
	 */
	public PObjectNode(Object item)
	{
		setItem(item);
	}

	// All PObjects have 2 states (qualified or not qualified),
	// Feats have 2 extra states which are only checked for the
	// "Selected Table". This boolean controls whether we are
	// interested in these extra feats or not
	public void setCheckFeatState(int state, PlayerCharacter currentPC)
	{
		checkFeatState = state;

		if ((state != NOT_A_FEAT) && (aPC == null))
		{
			aPC = currentPC;
		}
	}

	/**
	 * Get the child of an arbitrary node
	 * @param parent
	 * @param i
	 * @return child
	 */
	public static PObjectNode getChild(Object parent, int i)
	{
		final PObjectNode parentAsPObjectNode = (PObjectNode) parent;

		return parentAsPObjectNode.getChild(i);
	}

	/**
	 * Get the child
	 * @param i
	 * @return child
	 */
	public PObjectNode getChild(int i)
	{
		return children.get(i);
	}

	/**
	 * Gets the number of children
	 * @param parent
	 * @return child count
	 */
	public static int getChildCount(Object parent)
	{
		final PObjectNode parentAsPObjectNode = (PObjectNode) parent;

		return parentAsPObjectNode.getChildCount();
	}

	/**
	 * Gets the number of children
	 * @return child count
	 */
	public int getChildCount()
	{
		return (children == null) ? 0 : children.size();
	}

	/**
	 * Sets the children of the receiver, updates the total size,
	 * and if generateEvent is true a tree structure changed event
	 * is created.
	 * @param newChildren
	 */
	public void setChildren(ArrayList<PObjectNode> newChildren)
	{
		if (newChildren == null)
		{
			children = null;
			reset();

			return;
		}

		children = newChildren;

		for (PObjectNode n : children)
		{
			n.setParent(this);
		}

		reset();
	}

	/**
	 * Sets the children of the receiver, updates the total size,
	 * and if generateEvent is true a tree structure changed event
	 * is created.
	 * @param newChildren
	 */
	public void setChildren(PObjectNode[] newChildren)
	{
		if (newChildren == null)
		{
			children = null;
			reset();

			return;
		}

		if (children == null)
		{
			children = new ArrayList<PObjectNode>(newChildren.length);
		}
		else
		{
			children.clear();
			children.ensureCapacity(newChildren.length);
		}

		for (int i = 0; i < newChildren.length; ++i)
		{
			newChildren[i].setParent(this);
			children.add(newChildren[i]);
		}

		reset();
	}

	/**
	 * Loads the children, caching the results in the children
	 * instance variable.
	 * @return children
	 */
	public ArrayList<PObjectNode> getChildren()
	{
		return children;
	}

	/**
	 * Remove empty children
	 **/
	public void pruneEmpty()
	{
		if (getChildCount() == 0)
		{
			return;
		}

		for (ListIterator<PObjectNode> it = children.listIterator(); it
			.hasNext();)
		{
			PObjectNode node = it.next();
			if (node.isLeaf())
			{
				//				if (!node.getItem().toString().equals(""))
				if (node.getItem() instanceof String)
				{
					it.remove();
				}
			}
			else if (node.getChildCount() > 0)
			{
				node.pruneEmpty();
			}
		}
	}

	// Returns a String with the (currently only for Feats) choices in a
	// comma-delimited list.  Otherwise returns an empty string.
	public String getChoices()
	{
		final StringBuffer aString = new StringBuffer();

		if ((item != null) && (item instanceof Ability))
		{
			final Ability aFeat = (Ability) item;

			if (aFeat.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				//
				// If 1st selection has no length, then in is from CHOOSE:NOCHOICE
				//
				final int subCount = aPC.getDetailedAssociationCount(aFeat);
				ChooseInformation<?> chooseInfo =
						aFeat.get(ObjectKey.CHOOSE_INFO);

				if (chooseInfo != null)
				{
					aString.append(chooseInfo.getDisplay(aPC, aFeat));
				}
				else
				{
					aString.append(StringUtil.joinToStringBuffer(aPC
							.getExpandedAssociations(aFeat), ","));
				}
			}
		}

		return aString.toString();
	}

	// Sets the object (Could be a String or something subclassed from PObject)
	public void setItem(Object anItem)
	{
		item = anItem;
	}

	// Gets the object (Could be a String or something subclassed from PObject)
	public Object getItem()
	{
		return item;
	}

	/**
	 * Returns true if the receiver represents a leaf, that is it is
	 * isn't a directory.
	 * @return TRUE if it's a leaf node
	 */
	public boolean isLeaf()
	{
		if (children == null)
		{
			return true;
		}

		return children.size() == 0;
	}

	/**
	 * Retrieve the name of the node. This can be either the output name or
	 * the normal name of a PObject (depending on preferences), or for any
	 * other object it is the string representation of the object.
	 *
	 * @return String The name of the node. Null if it has no name.
	 */
	public String getNodeName()
	{
		String name = null;

		if (displayName != null)
		{
			name = displayName;
		}
		else if (item instanceof PObject)
		{
			if (pcgen.core.SettingsHandler.guiUsesOutputNameEquipment())
			{
				name = OutputNameFormatting.getOutputName(((PObject) item));
			}
			else
			{
				name = ((PObject) item).getDisplayName();
			}
		}
		else if (item != null)
		{
			name = item.toString();
		}

		return name;
	}

	/**
	 *  XXX -- ugh, needs to become protected!
	 * @param aNode
	 */
	public void setParent(PObjectNode aNode)
	{
		parent = aNode;
	}

	/**
	 * Returns the parent of the receiver.
	 * @return parent
	 */
	public PObjectNode getParent()
	{
		return parent;
	}

	// Gets the object's Source if it's a PObject, otherwise returns an empty string
	public String getSource()
	{
		if (item instanceof PObject)
		{
			return SourceFormat.getFormattedString(((PObject) item),
			Globals.getSourceDisplay(), true);
		}

		return "";
	}

	// XXX	Fix after switching to ArrayList
	public void add(Object obj)
	{
		throw new UnsupportedOperationException();
	}

	/**  This adds a child, if there are existing children, the new child
	 *  gets pointed to the same parent.  The first child will have to
	 *  specifically set its own parent.
	 * @param aChild
	 */
	public void addChild(PObjectNode aChild)
	{
		aChild.setParent(this);

		if (children == null)
		{
			children = new ArrayList<PObjectNode>();
		}

		children.add(aChild);
	}

	public boolean addChild(PObjectNode aChild, boolean sort)
	{
		boolean added = true;

		if (!sort || (children == null) || (aChild.item == null))
		{
			addChild(aChild);
		}

		else
		{
			final String itemName = aChild.item.toString();
			int x = 0;

			for (; x < children.size(); ++x)
			{
				final Object childItem = (children.get(x)).getItem();
				int comp = 1;

				if (childItem != null)
				{
					comp = itemName.compareToIgnoreCase(childItem.toString());
				}

				if (comp == 0)
				{
					added = false;

					break;
				}
				else if (comp < 0)
				{
					addChild(aChild, x);

					break;
				}
			}

			if (x >= children.size())
			{
				addChild(aChild);
			}
		}

		return added;
	}

	public boolean hasNext()
	{
		return mark < getChildCount();
	}

	public boolean hasPrevious()
	{
		return mark > 0;
	}

	public Object next()
	{
		return getChild(mark++);
	}

	public int nextIndex()
	{
		return mark;
	}

	public Object previous()
	{
		return getChild(--mark);
	}

	public int previousIndex()
	{
		return mark - 1;
	}

	// XXX	Fix after switching to ArrayList
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public void removeItemFromNodes(Object e)
	{
		// if no children, remove myself and update parent
		if (isChildless() && getItem().equals(e))
		{
			getParent().removeChild(this);
		}
		else
		{
			reset();

			while (hasNext())
			{
				((PObjectNode) next()).removeItemFromNodes(e);
			}
		}
	}

	public void reset()
	{
		mark = 0;
	}

	public static void resetPC(PlayerCharacter currentPC)
	{
		aPC = currentPC;
	}

	@Override
	public PObjectNode clone()
	{
		PObjectNode retVal = null;

		try
		{
			retVal = (PObjectNode) super.clone();

			ArrayList<PObjectNode> d = null;

			if (children != null)
			{
				d = new ArrayList<PObjectNode>(children.size());

				for (PObjectNode n : children)
				{
					PObjectNode node = (n.clone());
					d.add(node);
				}
			}

			retVal.setChildren(d);
		}
		catch (CloneNotSupportedException exc)
		{
			Logging.errorPrint("ERROR:", exc);
		}

		return retVal;
	}

	// TODO	Fix after switching to ArrayList
	public void set(Object obj)
	{
		throw new UnsupportedOperationException();
	}

	public void setColor(final int aColor)
	{
		theColor = aColor;
	}

	@Override
	public String toString()
	{
		if (item == null)
		{
			if (displayName != null)
			{
				return displayName;
			}
			return "";
		}

		if (item instanceof PObject)
		{
			String itemName = OutputNameFormatting.piString(((PObject) item), true);
			if (displayName != null)
			{
				itemName = displayName;
			}

			if (theColor != -1)
			{
				return Constants.PIPE + theColor + Constants.PIPE + itemName;

			}
			if (checkFeatState != NOT_A_FEAT)
			{
				final Ability aFeat = (Ability) item;
				Nature nature = aPC.getAbilityNature(aFeat);
				/*
				 * TODO Is this right?  Does not having an ability turn into Normal?
				 */
				nature = (nature == null) ? Nature.NORMAL : nature;
				switch (nature)
				{
					case NORMAL:
						return handleCheckFeatState(aFeat, itemName);

					case AUTOMATIC:
						return "|" + SettingsHandler.getFeatAutoColor() + "|"
							+ itemName;

					case VIRTUAL:
						return "|" + SettingsHandler.getFeatVirtualColor()
							+ "|" + itemName;

					default:
						Logging.errorPrint("Default getFeatType:"
							+ aFeat.getDisplayName());
						return "|" + SettingsHandler.getPrereqFailColor() + "|"
							+ itemName;
				}
			}

			if (item instanceof Equipment)
			{
				final Equipment e = (Equipment) item;

				if (e.isAutomatic())
				{
					// Automatic Equipment uses the same color as Automatic Feats
					return "|" + SettingsHandler.getFeatAutoColor() + "|"
						+ itemName;
				}

				if ((e.isShield() || e.isWeapon() || e.isArmor()))
				{
					/* TODO  this is very slow because it checks if the PC is
					 * proficient with the object each time the GUI requires a
					 * refresh (very frequent condition) */
					if (aPC == null || !aPC.isProficientWith(e))
					{
						// indicates to LabelTreeCellRenderer to change text color
						// to a user-preference (default is red)
						Color aColor = Color.red;

						if (SettingsHandler.getPrereqFailColor() != 0)
						{
							aColor =
									new Color(SettingsHandler
										.getPrereqFailColor());
						}

						return "|" + aColor.getRGB() + "|" + itemName;
					}
				}

				return itemName;
			}

			if (item instanceof Race)
			{
			}

			if (item instanceof Deity)
			{
				if (aPC == null || !aPC.canSelectDeity((Deity) item))
				{
					// indicates to LabelTreeCellRenderer to change text color
					// to a user-preference (default is red)
					Color aColor = Color.red;

					if (SettingsHandler.getPrereqFailColor() != 0)
					{
						aColor =
								new Color(SettingsHandler.getPrereqFailColor());
					}

					return "|" + aColor.getRGB() + "|" + itemName;

				}
			}

			if (item instanceof PCClass)
			{
				final String subClass = ((PCClass) item).getDisplayClassName(aPC);

				if (!((PCClass) item).getDisplayName().equals(subClass))
				{
					itemName = itemName + "/" + subClass;
				}
			}

			if (!((PObject) item).qualifies(aPC, (PObject) item))
			{
				// indicates to LabelTreeCellRenderer to change text color
				// to a user-preference (default is red)
				Color aColor = Color.red;

				if (SettingsHandler.getPrereqFailColor() != 0)
				{
					aColor = new Color(SettingsHandler.getPrereqFailColor());
				}

				return "|" + aColor.getRGB() + "|" + itemName;
			}

			return itemName;
		}
		else if (item instanceof SpellInfo)
		{
			final CharacterSpell spellA = ((SpellInfo) item).getOwner();
			final boolean isSpecial = spellA.isSpecialtySpell(aPC);
			final int times = ((SpellInfo) item).getTimes();

			final StringBuffer val = new StringBuffer(80);

			// first, set the name
			val.append(OutputNameFormatting.piString(spellA.getSpell(), false)); // gets name of spell

			// now tack on any extra crap such as domains, etc
			val.append((item).toString()); // appends feat list if any

			if (isSpecial && (spellA.getOwner() instanceof Domain))
			{
				//val.append(" [").append(spellA.getSpell().getDescriptor(", ")).append(']');
				val.append(" [").append(spellA.getOwner().getKeyName()).append(
					']');
			}

			// Finally add on the number of times
			if (times > 1)
			{
				val.append(" (").append(((SpellInfo) item).getTimes()).append(
					')');
			}

			// Only wrap in HTML if might contain HTML. HTML messes up the display
			// when using Java 1.3--causes the spell names to disappear
			if (val.toString().indexOf('<') >= 0)
			{
				val.insert(0, "<html>");
				val.append("</html>");
			}

			if ((spellA.getOwner() instanceof PCClass)
				&& !isSpecial
				&& SpellCountCalc.isProhibited(spellA.getSpell(), ((PCClass) spellA.getOwner()), aPC))
			{
				Color aColor = Color.red;

				if (SettingsHandler.getPrereqFailColor() != 0)
				{
					aColor = new Color(SettingsHandler.getPrereqFailColor());
				}

				return "|" + aColor.getRGB() + "|" + val.toString();
			}
			return val.toString();
		}
		else if (item instanceof PlayerCharacter)
		{
			final PlayerCharacter bPC = (PlayerCharacter) item;

			return bPC.getName();
		}

		if (displayName != null)
		{
			return displayName;
		}

		return item.toString();
	}

	/**
	 * Are there children?
	 * @return true if childless
	 */
	private boolean isChildless()
	{
		return getChildCount() == 0;
	}

	private void addChild(PObjectNode aChild, int index)
	{
		aChild.setParent(this);

		if (children == null)
		{
			children = new ArrayList<PObjectNode>();
			children.add(aChild);
		}
		else
		{
			children.add(index, aChild);
		}
	}

	private String handleCheckFeatState(final Ability aFeat, String itemName)
	{
		switch (checkFeatState)
		{
			case CAN_GAIN_FEAT:

				if (aFeat.qualifies(aPC, aFeat))
				{
					return "|" + SettingsHandler.getPrereqQualifyColor() + "|"
						+ itemName;
				}
				return "|" + SettingsHandler.getPrereqFailColor() + "|"
					+ itemName;

			case CAN_USE_FEAT:

				if (aFeat.qualifies(aPC, aFeat))
				{
					return "|" + SettingsHandler.getPrereqQualifyColor() + "|"
						+ itemName;
				}
				return "|" + SettingsHandler.getPrereqFailColor() + "|"
					+ itemName;

			default:
				Logging.errorPrint("Bad feat state: " + checkFeatState
					+ ".  Please report this as a bug.");

				return itemName;
		}
	}

	/**
	 * This removes the child
	 * @param aChild
	 * @return true or false
	 */
	private boolean removeChild(PObjectNode aChild)
	{
		if ((children == null) || (children.indexOf(aChild) == -1))
		{
			return false;
		}

		for (ListIterator<PObjectNode> it = children.listIterator(); it
			.hasNext();)
		{
			if (it.next() == aChild)
			{
				it.remove();
			}
		}
		return true;
	}

	/**
	 * @return The name to be displayed in a table or tree.
	 */
	public final String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @param displayName The name to be displayed in a table or tree.
	 */
	public final void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
}
