/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.format;

import pcgen.base.geom.GridPoint;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.BasicObjectContainer;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;

/**
 * A GridPointManager is a FormatManager that provides services for GridPoint
 * objects.
 */
public class GridPointManager implements FormatManager<GridPoint>
{

	/**
	 * @see pcgen.rules.types.FormatManager#convert(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public GridPoint convert(LoadContext context, String s)
	{
		return GridPoint.valueOf(s);
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertIndirect(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public Indirect<GridPoint> convertIndirect(LoadContext context, String s)
	{
		return new BasicIndirect<GridPoint>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#convertObjectContainer(LoadContext,
	 *      java.lang.String)
	 */
	@Override
	public ObjectContainer<GridPoint> convertObjectContainer(
		LoadContext context, String s)
	{
		return new BasicObjectContainer<GridPoint>(this, convert(context, s));
	}

	/**
	 * @see pcgen.rules.types.FormatManager#unconvert(java.lang.Object)
	 */
	@Override
	public String unconvert(GridPoint gp)
	{
		return gp.getPreciseX() + "," + gp.getPreciseY();
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getType()
	 */
	@Override
	public Class<GridPoint> getType()
	{
		return GridPoint.class;
	}

	/**
	 * @see pcgen.rules.types.FormatManager#getIdentifierType()
	 */
	@Override
	public String getIdentifierType()
	{
		return "GRIDPOINT";
	}

	@Override
	public int hashCode()
	{
		return 77421;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof GridPointManager;
	}
}
