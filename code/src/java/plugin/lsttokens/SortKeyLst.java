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
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractStringToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

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
public class SortKeyLst extends AbstractStringToken<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, DeferredToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "SORTKEY";
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	protected StringKey stringKey()
	{
		return StringKey.SORT_KEY;
	}

	@Override
	public boolean process(LoadContext context, CDOMObject obj)
	{
		if ((obj instanceof SortKeyRequired) && (obj.get(stringKey()) == null))
		{
			Logging.deprecationPrint("Objects of type "
				+ obj.getClass().getName() + " will require a SORTKEY "
				+ "in the next version of PCGen.  "
				+ "Use without a SORTKEY is deprecated", context);
			return false;
		}
		return true;
	}

	@Override
	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}
}
