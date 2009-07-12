/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class PCStatToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<PCStat>
{
	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

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
		PrimitiveChoiceSet<PCStat> pcs;
		if (Constants.LST_ALL.equals(activeValue))
		{
			pcs = new ReferenceChoiceSet<PCStat>(Collections
					.singletonList(context.ref
							.getCDOMAllReference(PCSTAT_CLASS)));
		}
		else
		{
			StringTokenizer st = new StringTokenizer(activeValue, ",");
			Set<PCStat> set = new HashSet<PCStat>();
			while (st.hasMoreTokens())
			{
				PCStat stat = context.ref.getAbbreviatedObject(PCSTAT_CLASS, st
						.nextToken());
				if (!set.add(stat))
				{
					// Error (second add)
				}
			}
			if (set.isEmpty())
			{
				return false;
			}
			pcs = new SimpleChoiceSet<PCStat>(set);
		}

		if (!pcs.getGroupingState().isValid())
		{
			Logging.errorPrint("Invalid combination of objects was used in: "
					+ activeValue);
			Logging.errorPrint("  Check that ALL is not combined");
			Logging.errorPrint("  Check that a key is not joined with AND (,)");
			return false;
		}
		ChoiceSet<PCStat> cs = new ChoiceSet<PCStat>(getTokenName(), pcs);
		cs.setTitle(title);
		/*
		 * TODO Null Formula here is a problem - eventually need to pull from
		 * SELECT
		 */
		PersistentTransitionChoice<PCStat> tc = new PersistentTransitionChoice<PCStat>(
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

	public void applyChoice(CDOMObject owner, PCStat st, PlayerCharacter pc)
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

	public void removeChoice(PlayerCharacter pc, CDOMObject owner, PCStat choice)
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

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			PCStat choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
	}

	public List<PCStat> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	public boolean allow(PCStat choice, PlayerCharacter pc, boolean allowStack)
	{
		/*
		 * This is universally true, as any filter for qualify, etc. was dealt
		 * with by the ChoiceSet built during parse
		 */
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "PCSTAT";
	}

	protected Class<PCStat> getChooseClass()
	{
		return PCSTAT_CLASS;
	}

	protected String getDefaultTitle()
	{
		return "Stat choice";
	}

	public PCStat decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				PCSTAT_CLASS, s);
	}

	public String encodeChoice(Object choice)
	{
		return ((PCStat) choice).getKeyName();
	}

	protected AssociationListKey<PCStat> getListKey()
	{
		return AssociationListKey.CHOOSE_PCSTAT;
	}
}
