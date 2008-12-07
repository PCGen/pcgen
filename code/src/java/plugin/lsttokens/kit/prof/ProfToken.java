/*
 * ProfToken.java
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.prof;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.WeaponProf;
import pcgen.core.kit.KitProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * PROF Token part of Kit Prof Lst Token
 */
public class ProfToken extends AbstractToken implements
		CDOMSecondaryToken<KitProf>
{
	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "PROF";
	}

	public Class<KitProf> getTokenClass()
	{
		return KitProf.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitProf obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMSingleRef<WeaponProf> ref =
					context.ref.getCDOMReference(WEAPONPROF_CLASS, tokText);
			if (ref == null)
			{
				Logging
					.errorPrint("  Error was encountered while parsing KitProf.  "
						+ tokText + " is not a valid WeaponProf");
				continue;
			}
			obj.addProficiency(ref);
		}
		return false;
	}

	public String[] unparse(LoadContext context, KitProf obj)
	{
		Collection<CDOMSingleRef<WeaponProf>> ref = obj.getProficiencies();
		if (ref == null)
		{
			return null;
		}
		return new String[]{ReferenceUtilities.joinLstFormat(ref,
			Constants.PIPE)};
	}
}
