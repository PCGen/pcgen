/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.QualifiedDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class KitLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>, ChoiceActor<Kit>
{

	private static final Class<Kit> KIT_CLASS = Kit.class;

	@Override
	public String getTokenName()
	{
		return "KIT";
	}

	public boolean parse(LoadContext context, CDOMObject pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		Formula count = FormulaFactory.getFormulaFor(tok.nextToken());
		if (!count.isStatic())
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Count in "
					+ getTokenName() + " must be a number");
			return false;
		}
		if (count.resolve(null, "").intValue() <= 0)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Count in "
					+ getTokenName() + " must be > 0");
			return false;
		}
		if (!tok.hasMoreTokens())
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " must have a | separating "
					+ "count from the list of possible values: " + value);
			return false;
		}
		List<CDOMReference<Kit>> refs = new ArrayList<CDOMReference<Kit>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Kit> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(KIT_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(KIT_CLASS, token);
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Non-sensical "
					+ getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<Kit> rcs = new ReferenceChoiceSet<Kit>(refs);
		ChoiceSet<Kit> cs = new ChoiceSet<Kit>(getTokenName(),
				new QualifiedDecorator<Kit>(rcs));
		cs.setTitle("Kit Selection");
		TransitionChoice<Kit> tc = new TransitionChoice<Kit>(cs, count);
		context.obj.addToList(pcc, ListKey.KIT_CHOICE, tc);
		tc.setRequired(false);
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject pcc)
	{
		Changes<TransitionChoice<Kit>> changes = context.getObjectContext()
				.getListChanges(pcc, ListKey.KIT_CHOICE);
		if (changes == null || changes.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Collection<TransitionChoice<Kit>> added = changes.getAdded();
		Set<String> set = new TreeSet<String>();
		for (TransitionChoice<Kit> tc : added)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(tc.getCount());
			sb.append(Constants.PIPE);
			sb.append(tc.getChoices().getLSTformat().replaceAll(
					Constants.COMMA, Constants.PIPE));
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void applyChoice(CDOMObject owner, Kit choice, PlayerCharacter pc)
	{
		Kit.applyKit(choice, pc);
	}

	public boolean allow(Kit choice, PlayerCharacter pc, boolean allowStack)
	{
		for (Kit k : pc.getKitInfo())
		{
			if (k.getKeyName().equalsIgnoreCase(choice.getKeyName()))
			{
				return false;
			}
		}
		return true;
	}

}
