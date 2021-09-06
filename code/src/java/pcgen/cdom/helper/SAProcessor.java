/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.helper;

import java.util.Collections;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.QualifiedActor;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.util.Logging;

public final class SAProcessor implements QualifiedActor<SpecialAbility, SpecialAbility>
{
	private final PlayerCharacter pc;

	public SAProcessor(PlayerCharacter pc)
	{
		this.pc = pc;
	}

	@Override
	public SpecialAbility act(SpecialAbility sa, Object source)
	{
		final String key = sa.getKeyName();
		final int idx = key.indexOf("%CHOICE");

		if (idx == -1)
		{
			return sa;
		}

		StringBuilder sb = new StringBuilder(100);
		sb.append(key.substring(0, idx));

		if (source instanceof ChooseDriver object)
		{
			if (pc.hasAssociations(object))
			{
				List<String> associationList = pc.getAssociationList(object);
				Collections.sort(associationList);
				sb.append(StringUtil.join(associationList, ", "));
			}
		}
		else
		{
			Logging.errorPrint(
				"In SpecialAbility resolution, " + "Error using object of type: " + source.getClass().getName()
					+ " because " + "%CHOICE" + " was requested but the object does not support CHOOSE");
			sb.append("<undefined>");
		}

		sb.append(key.substring(idx + 7));
		return new SpecialAbility(sb.toString(), sa.getSADesc());
	}
}
