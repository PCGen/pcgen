/* 
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
 */

package plugin.lsttokens.kit.deity;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Deity;
import pcgen.core.kit.KitDeity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * DEITY token for KitDeity 
 */
public class DeityToken extends AbstractNonEmptyToken<KitDeity> implements CDOMPrimaryToken<KitDeity>
{

	private static final Class<Deity> DEITY_CLASS = Deity.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "DEITY";
	}

	@Override
	public Class<KitDeity> getTokenClass()
	{
		return KitDeity.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitDeity kitDeity, String value)
	{
		CDOMSingleRef<Deity> ref = context.getReferenceContext().getCDOMReference(DEITY_CLASS, value);
		kitDeity.setDeity(ref);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitDeity kitDeity)
	{
		CDOMSingleRef<Deity> deity = kitDeity.getDeityRef();
		if (deity == null)
		{
			return null;
		}
		return new String[]{deity.getLSTformat(false)};
	}
}
