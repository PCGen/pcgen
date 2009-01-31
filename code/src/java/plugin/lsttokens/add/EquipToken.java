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
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.QualifiedDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class EquipToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<Equipment>
{

	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "EQUIP";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, getFullName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		Formula count;
		String items;
		if (pipeLoc == -1)
		{
			count = FormulaFactory.ONE;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			count = FormulaFactory.getFormulaFor(countString);
			if (count.isStatic() && count.resolve(null, "").doubleValue() <= 0)
			{
				Logging.log(Logging.LST_ERROR, "Count in " + getFullName()
								+ " must be > 0");
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMReference<Equipment>> refs = new ArrayList<CDOMReference<Equipment>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Equipment> lang = TokenUtilities.getTypeOrPrimitive(
					context, EQUIPMENT_CLASS, tokText);
			if (lang == null)
			{
				Logging.log(Logging.LST_ERROR, "  Error was encountered while parsing "
						+ getFullName() + ": " + value
						+ " had an invalid reference: " + tokText);
				return false;
			}
			refs.add(lang);
		}

		ReferenceChoiceSet<Equipment> rcs = new ReferenceChoiceSet<Equipment>(
				refs);
		ChoiceSet<Equipment> cs = new ChoiceSet<Equipment>(getTokenName(),
				new QualifiedDecorator<Equipment>(rcs));
		PersistentTransitionChoice<Equipment> tc = new PersistentTransitionChoice<Equipment>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.setTitle("Equipment Choice");
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges = context
				.getObjectContext().getListChanges(obj, ListKey.ADD);
		Collection<PersistentTransitionChoice<?>> addedItems = grantChanges
				.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (TransitionChoice<?> container : addedItems)
		{
			ChoiceSet<?> cs = container.getChoices();
			if (EQUIPMENT_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				StringBuilder sb = new StringBuilder();
				if (!FormulaFactory.ONE.equals(f))
				{
					sb.append(f).append(Constants.PIPE);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void applyChoice(CDOMObject owner, Equipment choice,
			PlayerCharacter pc)
	{
		Equipment bEquipment = choice.clone();
		bEquipment.setQty(1);
		pc.addEquipment(bEquipment);
	}

	public boolean allow(Equipment choice, PlayerCharacter pc,
			boolean allowStack)
	{
		return true;
	}

	public Equipment decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				EQUIPMENT_CLASS, s);
	}

	public String encodeChoice(Object choice)
	{
		return ((Equipment) choice).getKeyName();
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			Equipment choice)
	{
		// No action required
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			Equipment choice)
	{
		pc.removeEquipment(choice);
	}
}
