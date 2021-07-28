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
package plugin.qualifier.deity;

import java.util.Collection;
import java.util.Collections;

import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.ChannelUtilities;
import pcgen.rules.persistence.token.AbstractPCQualifierToken;

public class PCToken extends AbstractPCQualifierToken<Deity>
{

	@Override
	protected Collection<Deity> getPossessed(PlayerCharacter pc)
	{
		Deity deity = (Deity) ChannelUtilities
			.readControlledChannel(pc.getCharID(), CControl.DEITYINPUT);
		if (deity == null)
		{
			return Collections.emptyList();
		}
		else
		{
			return Collections.singletonList(deity);
		}
	}

	@Override
	public Class<? super Deity> getReferenceClass()
	{
		return Deity.class;
	}

}
