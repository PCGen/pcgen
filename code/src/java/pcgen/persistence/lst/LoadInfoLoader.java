/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.system.LoadInfo;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class LoadInfoLoader extends SimpleLoader<LoadInfo>
{

	/** Creates a new instance of LoadInfoLoader */
	public LoadInfoLoader()
	{
		super(LoadInfo.class);
	}

	@Override
	protected LoadInfo getLoadable(LoadContext context, String firstToken, URI sourceURI)
	{
		LoadInfo loadable = context.getReferenceContext().constructNowIfNecessary(LoadInfo.class, getGameMode());
		LstUtils.processToken(context, loadable, sourceURI, firstToken);
		return loadable;
	}
}
