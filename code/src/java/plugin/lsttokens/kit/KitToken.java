/*
 * KitToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Kit;
import pcgen.core.kit.KitKit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * Handles the KIT tag for Kits. Allows Common tags for this Kit line as well.
 */
public class KitToken extends AbstractToken implements
		CDOMSecondaryToken<KitKit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "KIT";
	}

	public Class<KitKit> getTokenClass()
	{
		return KitKit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitKit kitKit, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMSingleRef<Kit> ref =
					context.ref.getCDOMReference(Kit.class, tokText);
			kitKit.addKit(ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, KitKit kitKit)
	{
		List<CDOMSingleRef<Kit>> kits = kitKit.getKits();
		if (kits == null || kits.isEmpty())
		{
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(kits,
			Constants.PIPE)};
	}
}
