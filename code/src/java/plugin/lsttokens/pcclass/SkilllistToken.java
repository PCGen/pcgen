/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{
	private static Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "SKILLLIST";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		Formula count = FormulaFactory.getFormulaFor(tok.nextToken());
		if (!count.isStatic() || count.resolve(null, "").intValue() <= 0)
		{
			Logging.errorPrint("Count in " + getTokenName() + " must be > 0");
			return false;
		}
		if (!tok.hasMoreTokens())
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " must have a | separating "
					+ "count from the list of possible values: " + value);
			return false;
		}
		List<CDOMReference<ClassSkillList>> refs = new ArrayList<CDOMReference<ClassSkillList>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<ClassSkillList> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SKILLLIST_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(SKILLLIST_CLASS, token);
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

		ReferenceChoiceSet<ClassSkillList> rcs = new ReferenceChoiceSet<ClassSkillList>(
				refs);
		ChoiceSet<ClassSkillList> cs = new ChoiceSet<ClassSkillList>(
				getTokenName(), rcs);
		TransitionChoice<ClassSkillList> tc = new TransitionChoice<ClassSkillList>(
				cs, count);
		context.getObjectContext().put(pcc, ObjectKey.SKILLLIST_CHOICE, tc);
		tc.setTitle("Select class whose class-skills this class will inherit");
		tc.setRequired(false);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		TransitionChoice<ClassSkillList> grantChanges = context
				.getObjectContext().getObject(pcc, ObjectKey.SKILLLIST_CHOICE);
		if (grantChanges == null)
		{
			// Zero indicates no Token
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(grantChanges.getCount());
		sb.append(Constants.PIPE);
		sb.append(grantChanges.getChoices().getLSTformat().replaceAll(
				Constants.COMMA, Constants.PIPE));
		return new String[] { sb.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
