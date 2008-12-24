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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.WeaponProf;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	@Override
	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				CDOMReference<WeaponProf> ref = context.ref
						.getCDOMAllReference(WEAPONPROF_CLASS);
				context.getListContext().addToList(getTokenName(), pcc,
						WeaponProf.STARTING_LIST, ref);
			}
			else
			{
				foundOther = true;
				CDOMReference<WeaponProf> ref = TokenUtilities
						.getTypeOrPrimitive(context, WEAPONPROF_CLASS, tokText);
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				context.getListContext().addToList(getTokenName(), pcc,
						WeaponProf.STARTING_LIST, ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		AssociatedChanges<CDOMReference<WeaponProf>> changes = context
				.getListContext().getChangesInList(getTokenName(), pcc,
						WeaponProf.STARTING_LIST);
		Collection<CDOMReference<WeaponProf>> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no add
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(added,
				Constants.PIPE) };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
