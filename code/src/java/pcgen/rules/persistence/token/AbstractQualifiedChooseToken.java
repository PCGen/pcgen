package pcgen.rules.persistence.token;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public abstract class AbstractQualifiedChooseToken<T extends CDOMObject> extends
		AbstractToken implements CDOMSecondaryToken<CDOMObject>,
		PersistentChoiceActor<T>
{
	public String getParentToken()
	{
		return "CHOOSE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		int pipeLoc = value.indexOf('|');
		String activeValue;
		String title;
		if (pipeLoc == -1)
		{
			activeValue = value;
			title = getDefaultTitle();
		}
		else
		{
			String titleString = value.substring(pipeLoc + 1);
			if (titleString.startsWith("TITLE="))
			{
				title = titleString.substring(6);
				if (title.startsWith("\""))
				{
					title = title.substring(1, title.length() - 1);
				}
				activeValue = value.substring(0, pipeLoc);
			}
			else
			{
				activeValue = value;
				title = getDefaultTitle();
			}
		}

		PrimitiveChoiceSet<T> pcs = context.getChoiceSet(getChooseClass(),
				activeValue);
		if (pcs == null)
		{
			return false;
		}
		if (!pcs.getGroupingState().isValid())
		{
			Logging.errorPrint("Invalid combination of objects was used in: "
					+ activeValue);
			Logging.errorPrint("  Check that ALL is not combined");
			Logging.errorPrint("  Check that a key is not joined with AND (,)");
			return false;
		}
		ChoiceSet<T> cs = new ChoiceSet<T>(getTokenName(), pcs);
		cs.setTitle(title);
		/*
		 * TODO Null Formula here is a problem - eventually need to pull from
		 * SELECT
		 */
		PersistentTransitionChoice<T> tc = new PersistentTransitionChoice<T>(
				cs, null);
		/*
		 * TODO All CHOOSE based tokens (regardless of whether they use
		 * AbstractChooseToken) MUST have a validation to ensure they have a
		 * ChoiceActor - not negotiable...
		 */
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		PersistentTransitionChoice<?> tc = context.getObjectContext()
				.getObject(cdo, ObjectKey.CHOOSE_INFO);
		if (tc == null)
		{
			return null;
		}
		ChoiceSet<?> choices = tc.getChoices();
		if (!choices.getName().equals(getTokenName()))
		{
			// Don't unparse anything that isn't owned by this SecondaryToken
			/*
			 * TODO Either this really needs to be a check against the subtoken
			 * (which thus needs to be stored in the ChooseInfo) or there needs
			 * to be a loadtime check that no more than once CHOOSE subtoken
			 * uses the same AssociationListKey... :P
			 */
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(choices.getLSTformat());
		String title = choices.getTitle();
		if (!title.equals(getDefaultTitle()))
		{
			sb.append("|TITLE=");
			sb.append(title);
		}
		return new String[] { sb.toString() };
	}

	public void applyChoice(CDOMObject owner, T st, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, st);
		List<PersistentChoiceActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			/*
			 * TODO There needs to be some verification at some point that the
			 * ChooseActors are the same type as the CHOOSE that is on the
			 * object...
			 */
			for (PersistentChoiceActor ca : actors)
			{
				ca.applyChoice(owner, st, pc);
			}
		}
		pc.addAssociation(owner, encodeChoice(st));
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner, T choice)
	{
		pc.removeAssoc(owner, getListKey(), choice);
		List<PersistentChoiceActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			/*
			 * TODO There needs to be some verification at some point that the
			 * ChooseActors are the same type as the CHOOSE that is on the
			 * object...
			 */
			for (PersistentChoiceActor ca : actors)
			{
				ca.removeChoice(pc, owner, choice);
			}
		}
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, T choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
	}

	public List<T> getCurrentlySelected(CDOMObject owner, PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	public boolean allow(T choice, PlayerCharacter pc, boolean allowStack)
	{
		/*
		 * This is universally true, as any filter for qualify, etc. was dealt
		 * with by the ChoiceSet built during parse
		 */
		return true;
	}

	protected abstract String getDefaultTitle();

	protected abstract Class<T> getChooseClass();

	protected abstract AssociationListKey<T> getListKey();

}
