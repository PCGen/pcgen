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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with BONUSSPELLSTAT Token
 */
public class BonusspellstatToken extends AbstractNonEmptyToken<PCClass> implements CDOMPrimaryToken<PCClass>
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	@Override
	public String getTokenName()
	{
		return "BONUSSPELLSTAT";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
	{
		if (Constants.LST_NONE.equals(value))
		{
			context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT, Boolean.FALSE);
			return ParseResult.SUCCESS;
		}
		context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT, Boolean.TRUE);
		/*
		 * TODO Does this consume DEFAULT in some way, so that it can set
		 * HAS_BONUS_SPELL_STAT to true, but not trigger the creation of
		 * BONUS_SPELL_STAT?
		 */
		CDOMSingleRef<PCStat> pcs = context.getReferenceContext().getCDOMReference(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			return new ParseResult.Fail("Invalid Stat Abbreviation in " + getTokenName() + ": " + value);
		}
		context.getObjectContext().put(pcc, ObjectKey.BONUS_SPELL_STAT, pcs);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean bss = context.getObjectContext().getObject(pcc, ObjectKey.HAS_BONUS_SPELL_STAT);
		CDOMSingleRef<PCStat> pcs = context.getObjectContext().getObject(pcc, ObjectKey.BONUS_SPELL_STAT);
		if (bss == null)
		{
			if (pcs != null)
			{
				context.addWriteMessage(
					getTokenName() + " expected HAS_BONUS_SPELL_STAT to exist " + "if BONUS_SPELL_STAT was defined");
			}
			return null;
		}
		if (bss)
		{
			if (pcs == null)
			{
				context.addWriteMessage(
					getTokenName() + " expected BONUS_SPELL_STAT to exist " + "since HAS_BONUS_SPELL_STAT was false");
				return null;
			}
			return new String[]{pcs.getLSTformat(false)};
		}
		else
		{
			return new String[]{"NONE"};
		}
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
