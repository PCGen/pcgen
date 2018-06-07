/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.grouping;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;

/**
 * A GroupingDefinition defines a parser capable of processing the persistent format of a
 * grouping of objects (e.g. "ALL" or "GROUP=x").
 * 
 * @param <T>
 *            The format of the object that this GroupingDefinition can process.
 */
public interface GroupingDefinition<T extends Loadable>
{

	/**
	 * Returns the Identification of the Grouping. Note if the Grouping uses an "=" then
	 * this will be the part before the "=".
	 * 
	 * @return The Identification of the Grouping
	 */
	public String getIdentification();

	/**
	 * Returns the Class indicating the usable location for this GroupingDefinition
	 * 
	 * @return The Class indicating the usable location for this GroupingDefinition
	 */
	public Class<?> getUsableLocation();

	/**
	 * Processes the given GroupingInfo to produce a new GroupingCollection based on the
	 * rules of this GroupingDefinition.
	 * 
	 * @param context
	 *            The LoadContext in which the GroupingInfo should be processed
	 * @param info
	 *            The GroupingInfo to be used to produce a GroupingCollection
	 * @return A new GroupingCollection based on the given GroupingInfo processed with the
	 *         rules of this GroupingDefinition
	 */
	public GroupingCollection<T> process(LoadContext context, GroupingInfo<T> info);

	/*
	 * Note: Technically, requiresDirect is probably over defensive for use with Facts,
	 * which is the special case that drove the design, but I think the theory stands that
	 * there may be things that can't "fall up".
	 */
	/**
	 * Returns true if this GroupingDefinition will only function on the exact class
	 * returned by getUsableLocation.
	 * 
	 * If this returns false, this GroupingDefinition must function on the class returned
	 * by getUsableLocation as well as any class that extends (directly or indirectly) the
	 * class returned by getUsableLocation.
	 * 
	 * @return true if this GroupingDefinition will only function on the exact class
	 *         returned by getUsableLocation; false otherwise
	 */
	public boolean requiresDirect();
}
