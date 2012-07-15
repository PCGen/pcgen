package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.ChooserFacade.ChooserTreeViewType;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

public class CDOMChoiceManager<T> implements ChoiceManagerList<T>
{

	private final CDOMObject owner;
	private final Integer numberOfChoices;
	private final int choicesPerUnitCost;
	private ChooseController<T> controller = new ChooseController<T>();
	private final ChooseInformation<T> info;

	private transient int preChooserChoices;

	public CDOMChoiceManager(CDOMObject cdo,
		ChooseInformation<T> chooseType, Integer numChoices,
			int cost)
	{
		numberOfChoices = numChoices;
		owner = cdo;
		info = chooseType;
		choicesPerUnitCost = cost;
	}

	public void getChoices(PlayerCharacter pc, List<T> availableList,
			List<T> selectedList)
	{
		availableList.addAll(info.getSet(pc));
		List<? extends T> selected = info.getChoiceActor()
				.getCurrentlySelected(owner, pc);
		if (selected != null)
		{
			selectedList.addAll(selected);
		}
		preChooserChoices = selectedList.size();
	}

	public String typeHandled()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Conditionally add the selected item
	 * 
	 * @param pc
	 * @param item
	 */
	public boolean conditionallyApply(PlayerCharacter pc, T item)
	{
		List<? extends T> oldSelections = info.getChoiceActor().getCurrentlySelected(
				owner, pc);
		boolean applied = false;
		if (oldSelections == null || !oldSelections.contains(item))
		{
			if (info.getSet(pc).contains(item))
			{
				info.getChoiceActor().applyChoice(owner, item, pc);
				applied = true;
			}
		}
		adjustPool(info.getChoiceActor().getCurrentlySelected(owner, pc));
		return applied;
	}

	/**
	 * Add the selected Feat proficiencies
	 * 
	 * @param pc
	 * @param selected
	 */
	public boolean applyChoices(PlayerCharacter pc, List<T> selected)
	{
		List<? extends T> oldSelections = info.getChoiceActor().getCurrentlySelected(
				owner, pc);
		List<T> toAdd = new ArrayList<T>();
		for (T obj : selected)
		{
			if (oldSelections == null || !oldSelections.remove(obj))
			{
				toAdd.add(obj);
			}
		}
		int oldSize = 0;
		if (oldSelections != null)
		{
			oldSize = oldSelections.size();
			for (T obj : oldSelections)
			{
				info.getChoiceActor().removeChoice(pc, owner, obj);
			}
		}
		for (T obj : toAdd)
		{
			info.getChoiceActor().applyChoice(owner, obj, pc);
		}
		adjustPool(selected);
		return oldSize != selected.size();
	}

	/**
	 * Display a chooser to the user.
	 * 
	 * @param aPc The character the choice is for.
	 * @param availableList The list of possible choices.
	 * @param selectedList The list of existing selections.
	 * @return list The list of the new selections made by the user (unchanged if the dialog was cancelled)
	 */
	public List<T> doChooser(PlayerCharacter aPc, final List<T> availableList,
			final List<T> selectedList, final List<String> reservedList)
	{
		int effectiveChoices = getNumEffectiveChoices(selectedList, reservedList);

		final ChooserInterface chooser = getChooserInstance();
		boolean dupsAllowed = controller.isMultYes() && controller.isStackYes();
		chooser.setAllowsDups(dupsAllowed);
		
		/*
		 * TODO This is temporarily commented out until the correct behavior of
		 * the "available" list is established. This is done to make
		 * CDOMChoiceManager not remove items when selected, which is consistent
		 * with the (buggy?) old Choose system
		 */
		// if (!dupsAllowed)
		// {
		// availableList.removeAll(reservedList);
		//		}

		Globals.sortChooserLists(availableList, selectedList);
//		chooser.setAvailableList(availableList);
//		chooser.setSelectedList(selectedList);
//
//		chooser.setChoicesPerUnit(choicesPerUnitCost);
//		chooser.setTotalChoicesAvail(effectiveChoices);
//		chooser.setPoolFlag(false); // Allow cancel as clicking the x will
//		// cancel anyway
//
//		chooser.setVisible(true);
//
//		return chooser.getSelectedList();
		CDOMChooserFacadeImpl<T> chooserFacade =
				new CDOMChooserFacadeImpl<T>(
					LanguageBundle.getString("in_chooser"), availableList, //$NON-NLS-1$
					selectedList, effectiveChoices);
		chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
		ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
		
		return chooserFacade.getFinalSelected();
		
	}

	/**
	 * Calculate the number of effective choices the user can make.
	 *  
	 * @param selectedList The list of already selected items.
	 * @param reservedList 
	 * @return The number of choices that may be made 
	 */
	public int getNumEffectiveChoices(final List<T> selectedList,
		final List<String> reservedList)
	{
		int selectedPoolValue = (selectedList.size() + (choicesPerUnitCost - 1))
				/ choicesPerUnitCost;
		int reservedPoolValue = (reservedList.size() + (choicesPerUnitCost - 1))
				/ choicesPerUnitCost;
		int effectiveTotalChoices;
		if (numberOfChoices == null)
		{
			effectiveTotalChoices = controller.getTotalChoices();
		}
		else
		{
			effectiveTotalChoices = (numberOfChoices - reservedPoolValue + selectedPoolValue);
		}
		int effectiveChoices = Math
				.min(controller.getPool() + selectedPoolValue,
						effectiveTotalChoices / choicesPerUnitCost);
		effectiveChoices *= choicesPerUnitCost;
		effectiveChoices -=  selectedList.size();
		return effectiveChoices;
	}

	/**
	 * Retrieve the appropriate chooser to use and set its title.
	 * 
	 * @return The chooser to be displayed to the user.
	 */
	protected ChooserInterface getChooserInstance()
	{
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		String title = info.getTitle();
		if (title != null && title.length() > 0)
		{
			chooser.setTitle(getTitle());
		}
		return chooser;
	}

	/**
	 * 
	 * @param pc
	 * @param availableList
	 * @param selectedList
	 */
	public void doChooserRemove(PlayerCharacter pc, List<T> availableList,
			List<T> selectedList, List<String> reservedList)
	{
		final List<T> newSelections =
				doChooser(pc, availableList, selectedList, reservedList);

		applyChoices(pc, newSelections);
	}

	protected void adjustPool(List<? extends T> selected)
	{
		controller.adjustPool(selected);
	}

	public void setController(ChooseController<T> cc)
	{
		controller = cc;
	}

	public int getChoicesPerUnitCost()
	{
		return choicesPerUnitCost;
	}

	public int getPreChooserChoices()
	{
		return preChooserChoices;
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, String choice)
	{
		if (choice.length() > 0)
		{
			T ch = info.decodeChoice(choice);
			if (ch == null)
			{
				Logging.errorPrint("Error finding "
					+ info.getClassIdentity().getName() + " " + choice
					+ ": Not Found");
			}
			else
			{
				info.restoreChoice(pc, owner, ch);
			}
		}
	}

	protected String getTitle()
	{
		return new StringBuilder(50).append(info.getTitle()).append(" (")
				.append(owner.getDisplayName()).append(')').toString();
	}

}
