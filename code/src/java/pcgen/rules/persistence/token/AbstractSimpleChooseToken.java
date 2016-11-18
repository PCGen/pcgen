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
package pcgen.rules.persistence.token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.choiceset.CollectionToChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.primitive.CompoundOrPrimitive;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;

public abstract class AbstractSimpleChooseToken<T extends Loadable> extends
		AbstractTokenWithSeparator<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, Chooser<T>
{
	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		int pipeLoc = value.lastIndexOf('|');
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
				if (title == null || title.isEmpty())
				{
					return new ParseResult.Fail(getParentToken() + ":"
						+ getTokenName() + " had TITLE= but no title: " + value, context);
				}
			}
			else
			{
				activeValue = value;
				title = getDefaultTitle();
			}
		}
		CDOMGroupRef<T> allReference =
				context.getReferenceContext().getCDOMAllReference(getChooseClass());
		PrimitiveCollection<T> prim;
		if (Constants.LST_ALL.equals(activeValue))
		{
			prim = allReference;
		}
		else
		{
			if (hasIllegalSeparator('|', activeValue))
			{
				return ParseResult.INTERNAL_ERROR;
			}
			Set<PrimitiveCollection<T>> set =
                    new HashSet<>();
			StringTokenizer st = new StringTokenizer(activeValue, "|");
			while (st.hasMoreTokens())
			{
				String tok = st.nextToken();
				PrimitiveCollection<T> ref =
						ChoiceSetLoadUtilities.getSimplePrimitive(context,
							getManufacturer(context), tok);
				if (ref == null)
				{
					return new ParseResult.Fail(
						"Error: Count not get Reference for " + tok + " in "
							+ getTokenName(), context);
				}
				if (!set.add(ref))
				{
					return new ParseResult.Fail("Error, Found item: " + ref
						+ " twice while parsing " + getTokenName(), context);
				}
			}
			if (set.isEmpty())
			{
				return new ParseResult.Fail("No items in set.", context);
			}
			prim = new CompoundOrPrimitive<>(set);
		}

		if (!prim.getGroupingState().isValid())
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addErrorMessage("Invalid combination of objects was used in: "
				+ activeValue);
			cpr.addErrorMessage("  Check that ALL is not combined with another item");
			return cpr;
		}
		PrimitiveChoiceSet<T> pcs = new CollectionToChoiceSet<>(prim);
		BasicChooseInformation<T> tc = new BasicChooseInformation<>(getTokenName(), pcs);
		tc.setTitle(title);
		tc.setChoiceActor(this);
		context.getObjectContext().put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	public SelectionCreator<T> getManufacturer(LoadContext context)
	{
		return context.getReferenceContext().getManufacturer(getChooseClass());
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> tc =
				context.getObjectContext()
					.getObject(cdo, ObjectKey.CHOOSE_INFO);
		if (tc == null)
		{
			return null;
		}
		if (!tc.getName().equals(getTokenName()))
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
		if (!tc.getGroupingState().isValid())
		{
			context.addWriteMessage("Invalid combination of objects"
				+ " was used in: " + getParentToken() + ":" + getTokenName());
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(tc.getLSTformat());
		String title = tc.getTitle();
		if (!title.equals(getDefaultTitle()))
		{
			sb.append("|TITLE=");
			sb.append(title);
		}
		return new String[]{sb.toString()};
	}

	@Override
	public void applyChoice(ChooseDriver owner, T st, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, st);
	}

	private void applyChoice(ChooseDriver owner, T st, PlayerCharacter pc,
		ChooseSelectionActor<T> ca)
	{
		ca.applyChoice(owner, st, pc);
	}

	@Override
	public void removeChoice(PlayerCharacter pc, ChooseDriver owner, T choice)
	{
		pc.removeAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors = owner.getActors();
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, choice, pc);
			}
		}
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, T choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors = owner.getActors();
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				applyChoice(owner, choice, pc, ca);
			}
		}
	}

	@Override
	public List<T> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	@Override
	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	public boolean allow(T choice, PlayerCharacter pc, boolean allowStack)
	{
		/*
		 * This is universally true, as any filter for qualify, etc. was dealt
		 * with by the ChoiceSet built during parse
		 */
		return true;
	}

	@Override
	public T decodeChoice(LoadContext context, String s)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(
			getChooseClass(), s);
	}

	@Override
	public String encodeChoice(T choice)
	{
		return choice.getKeyName();
	}

	protected abstract Class<T> getChooseClass();

	protected abstract String getDefaultTitle();

	protected abstract AssociationListKey<T> getListKey();
}
