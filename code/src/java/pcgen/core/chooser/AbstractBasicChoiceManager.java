/*
 * AbstractSimpleChoiceManager.java
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
 * Current Version: $Revision: 1172 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006-07-06 10:55:31 -0400 (Thu, 06 Jul 2006) $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AssociatedChoice;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * A class to handle generating a suitable list of choices, selecting from those
 * choices and potentially applying the choices to a PC
 */
public abstract class AbstractBasicChoiceManager<T> implements
		ChoiceManagerList<T>
{
	private int numberOfChoices;
	private final int choicesPerUnitCost;
	private final String chooserHandled;
	private final ArrayList<String> choices = new ArrayList<String>();

	protected final PObject pobject;

	private ChooseController<T> controller = new ChooseController<T>();
	private int preChooserChoices;
	private String title = null;

	/**
	 * Creates a new ChoiceManager object.
	 * 
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 */
	public AbstractBasicChoiceManager(PObject aPObject, String theChoices,
			PlayerCharacter aPC)
	{
		pobject = aPObject;
		StringTokenizer st = new StringTokenizer(theChoices, Constants.PIPE);
		String chooserName = st.nextToken();
		String selectionsPerUnitCost = pobject.getSelectCount();
		int totalChoices = -1;

		/*
		 * We safely assume here that the chooser has choices, or it would have
		 * failed parsing!
		 */
		while (st.hasMoreTokens())
		{
			if (chooserName.startsWith("NUMCHOICES="))
			{
				// From parser, we can assume this only occurs once
				totalChoices = aPC.getVariableValue(chooserName.substring(11),
						"").intValue();
				if (totalChoices <= 0)
				{
					// Problem!
					Logging.errorPrint("Found CHOOSER with choices " + "<= 0: "
							+ theChoices + " where total choices "
							+ "from NUMCHOICES resolves to: " + totalChoices);
					totalChoices = 0;
				}
			}
			else if (chooserName.startsWith("TITLE="))
			{
				String newTitle = chooserName.substring(6);
				if (newTitle.startsWith("\""))
				{
					newTitle = newTitle.substring(1, newTitle.length() - 1);
				}
				setTitle(newTitle);
			}
			else
			{
				break;
			}
			chooserName = st.nextToken();
		}

		while (st.hasMoreTokens())
		{
			choices.add(st.nextToken());
		}
		choices.trimToSize();
		chooserHandled = chooserName;
		numberOfChoices = totalChoices;
		choicesPerUnitCost = aPC.getVariableValue(selectionsPerUnitCost, "")
				.intValue();
	}

	/**
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public abstract void getChoices(final PlayerCharacter aPc,
			final List<T> availableList, final List<T> selectedList);

	/**
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @return list
	 */
	public List<T> doChooser(PlayerCharacter aPc, final List<T> availableList,
			final List<T> selectedList, final List<AssociatedChoice> reservedList)
	{
		int selectedPoolValue = (selectedList.size() + (choicesPerUnitCost - 1))
				/ choicesPerUnitCost;
		int reservedPoolValue = (reservedList.size() + (choicesPerUnitCost - 1))
				/ choicesPerUnitCost;
		int limitedChoices = numberOfChoices - reservedPoolValue + selectedPoolValue;
		int effectiveTotalChoices = numberOfChoices == -1 ? controller
				.getTotalChoices() : limitedChoices;
		int effectiveChoices = Math
				.min(controller.getPool() + selectedPoolValue,
						effectiveTotalChoices / choicesPerUnitCost);

		final ChooserInterface chooser = getChooserInstance();
		boolean dupsAllowed = controller.isMultYes() && controller.isStackYes();
		chooser.setAllowsDups(dupsAllowed);
		if (!dupsAllowed)
		{
			for (AssociatedChoice o : reservedList)
			{
				availableList.remove(o.getDefaultChoice());
			}
		}

		Globals.sortChooserLists(availableList, selectedList);
		chooser.setAvailableList(availableList);
		chooser.setSelectedList(selectedList);

		chooser.setChoicesPerUnit(choicesPerUnitCost);
		chooser.setTotalChoicesAvail(effectiveChoices);
		chooser.setPoolFlag(false); // Allow cancel as clicking the x will
		// cancel anyway

		processUniqueItems(chooser);

		chooser.setVisible(true);

		return chooser.getSelectedList();
	}

	/**
	 * Retrieve the appropriate chooser to use and set its title.
	 * 
	 * @return The chooser to be displayed to the user.
	 */
	protected ChooserInterface getChooserInstance()
	{
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		if (title != null && title.length() > 0)
		{
			chooser.setTitle(title + " (" + pobject.getDisplayName() + ')');
		}
		return chooser;
	}

	protected void processUniqueItems(ChooserInterface chooser)
	{
		// Nothing in this implementation
	}

	/**
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void doChooserRemove(PlayerCharacter aPC, List<T> availableList,
			List<T> selectedList, List<AssociatedChoice> reservedList)
	{
		final List<T> newSelections = doChooser(aPC, availableList,
				selectedList, reservedList);

		applyChoices(aPC, newSelections);
	}

	public abstract void applyChoices(final PlayerCharacter aPC,
			final List<T> selected);

	public List<String> getChoiceList()
	{
		return choices;
	}

	/**
	 * what type of chooser does this handle
	 * 
	 * @return type of chooser
	 */
	public String typeHandled()
	{
		return chooserHandled;
	}

	public void setTitle(String titleString)
	{
		title = titleString;
	}

	protected void setPreChooserChoices(int size)
	{
		preChooserChoices = size;
	}

	protected void adjustPool(List<T> selected)
	{
		controller.adjustPool(selected);
	}

	public boolean isMultYes()
	{
		return controller.isMultYes();
	}

	public boolean isStackYes()
	{
		return controller.isStackYes();
	}

	public void setController(ChooseController<T> cc)
	{
		controller = cc;
	}

	protected static class ChooseController<T>
	{
		public ChooseController()
		{
			// Nothing to build here
		}

		public int getPool()
		{
			return 1;
		}

		public boolean isMultYes()
		{
			return false;
		}

		public boolean isStackYes()
		{
			return false;
		}

		public double getCost()
		{
			return 1.0;
		}

		public int getTotalChoices()
		{
			return 1;
		}

		public void adjustPool(List<T> selected)
		{
			// Ignore
		}
	}

	protected class AbilityChooseController extends ChooseController<Ability>
	{
		private final Ability ability;
		private final AbilityCategory ac;
		private final PlayerCharacter pc;

		public AbilityChooseController(Ability a, AbilityCategory cat,
				PlayerCharacter aPC)
		{
			if (a == null)
			{
				throw new IllegalArgumentException(
						"Ability cannot be null for AbilityChooseController");
			}
			ability = a;
			ac = cat;
			pc = aPC;
		}

		@Override
		public int getPool()
		{
			return isMultYes() ? pc.getAvailableAbilityPool(ac).intValue() : 1;
		}

		@Override
		public boolean isMultYes()
		{
			return ability.getSafe(ObjectKey.MULTIPLE_ALLOWED);
		}

		@Override
		public boolean isStackYes()
		{
			return ability.getSafe(ObjectKey.STACKS);
		}

		@Override
		public double getCost()
		{
			return ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
		}

		@Override
		public int getTotalChoices()
		{
			return isMultYes() ? Integer.MAX_VALUE : 1;
		}

		@Override
		public void adjustPool(List<Ability> selected)
		{
			if (AbilityCategory.FEAT.equals(ac))
			{
				double cost = getCost();
				if (cost > 0)
				{
					int basePriorCost = ((preChooserChoices + (choicesPerUnitCost - 1)) / choicesPerUnitCost);
					int baseTotalCost = ((selected.size() + (choicesPerUnitCost - 1)) / choicesPerUnitCost);
					pc.adjustFeats(cost * (basePriorCost - baseTotalCost));
				}
			}
		}
	}

	/*
	 * WARNING: This should only be used in VERY RARE circumstances...
	 */
	protected void setMaxChoices(int maxChoices)
	{
		numberOfChoices = maxChoices;
	}

	public int getNumberOfChoices()
	{
		return numberOfChoices;
	}
}
