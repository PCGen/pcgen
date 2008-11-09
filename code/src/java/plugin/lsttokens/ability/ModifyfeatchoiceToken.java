package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ModifyChoiceDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

/**
 * Deals with the MODIFYFEATCHOICE token
 */
public class ModifyfeatchoiceToken extends AbstractToken implements
		CDOMPrimaryToken<Ability>, ChoiceActor<Ability>
{

	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "MODIFYFEATCHOICE";
	}

	public boolean parse(LoadContext context, Ability obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		AbilityCategory category = AbilityCategory.FEAT;
		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();

			CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(
					context, ABILITY_CLASS, category, token);
			if (ability == null)
			{
				return false;
			}

			refs.add(ability);
		}

		ReferenceChoiceSet<Ability> rcs = new ReferenceChoiceSet<Ability>(refs);
		ModifyChoiceDecorator gfd = new ModifyChoiceDecorator(rcs);
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>(getTokenName(), gfd);
		TransitionChoice<Ability> tc = new TransitionChoice<Ability>(cs,
				Formula.ONE);
		tc.setTitle("Select a "
				+ SettingsHandler.getGame().getSingularTabName(Tab.ABILITIES)
				+ " to modify");
		tc.setRequired(false);
		context.getObjectContext().put(obj, ObjectKey.MODIFY_CHOICE, tc);
		tc.setChoiceActor(this);

		return true;
	}

	public String[] unparse(LoadContext context, Ability obj)
	{
		TransitionChoice<Ability> mc = context.getObjectContext().getObject(
				obj, ObjectKey.MODIFY_CHOICE);
		if (mc == null)
		{
			// Zero indicates no Token
			return null;
		}
		return new String[] { mc.getChoices().getLSTformat() };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

	public void applyChoice(CDOMObject owner, Ability choice, PlayerCharacter pc)
	{
		final List<String> abilityList = new ArrayList<String>();
		final List<String> selectedList = new ArrayList<String>();

		// build a list of available choices and choices already made.
		ChooserUtilities.modChoices(choice, abilityList, selectedList, false,
				pc, true, SettingsHandler.getGame().getAbilityCategory(
						choice.getCategory()));

		final int currentSelections = selectedList.size();

		//
		// If nothing to choose, or nothing selected, then leave
		//
		if ((abilityList.size() == 0) || (currentSelections == 0))
		{
			return;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(true); // user is required to use all available
		// pool points
		chooser.setTotalChoicesAvail(selectedList.size()); // need to remove 1
		// to add another

		chooser.setTitle("Modify selections for " + choice);
		Globals.sortChooserLists(abilityList, selectedList);
		chooser.setAvailableList(abilityList);
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		final int selectedSize = chooser.getSelectedList().size();

		if (selectedSize != currentSelections)
		{
			// need to have the same number of selections when finished
			return;
		}

		// replace old selection(s) with new and update bonuses
		pc.removeAllAssociations(choice);

		for (int i = 0; i < selectedSize; ++i)
		{
			pc
					.addAssociation(choice, (String) chooser.getSelectedList()
							.get(i));
		}
	}

	public boolean allow(Ability choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}
}
