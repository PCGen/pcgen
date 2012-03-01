/**
 * SortKeyLst.java
 * Copyright 2010 (C) James Dempsey
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
 * Created on 28/05/2010 10:02:14 PM
 *
 * $Id$
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The Class <code>SortKeyLst</code> implements the global SORTKEY tag, which 
 * allows items to be sorted in a custom manner.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class SortKeyLst extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SORTKEY";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		context.obj.put(obj, StringKey.SORT_KEY, value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String sortKey =
				context.getObjectContext().getString(obj, StringKey.SORT_KEY);
		if (sortKey == null)
		{
			return null;
		}
		return new String[]{sortKey};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
