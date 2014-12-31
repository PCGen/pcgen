/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;

/**
 * A GroupDefinition is effectively a factory able to produce ObjectContainers
 * containing groups of objects. The GroupDefinition is the framework used to
 * provide the construction and other common underlying behavior of those
 * ObjectContainers.
 * 
 * @param <T>
 *            The type of object where this GroupDefinition is able to be used
 */
public interface GroupDefinition<T>
{

	/**
	 * Returns the GroupingState for this GroupDefinition. The GroupingState
	 * indicates how this GroupDefinition can be combined with other
	 * GroupDefinitions.
	 * 
	 * @return The GroupingState for this GroupDefinition
	 */
	public GroupingState getGroupingState();

	/**
	 * The type of object where this GroupDefinition is able to be used.
	 * 
	 * @return The class of indicating the type of object where this
	 *         GroupDefinition is able to be used
	 */
	public Class<T> getReferenceClass();

	/**
	 * Returns the name of this GroupDefinition
	 * 
	 * @return the name of this GroupDefinition
	 */
	public String getPrimitiveName();

	/**
	 * The FormatManager usable to manage the given objects managed by this
	 * GroupDefinition.
	 * 
	 * @return The FormatManager usable to manage the given objects managed by
	 *         this GroupDefinition
	 */
	public FormatManager<?> getFormatManager();

	/**
	 * Returns an ObjectContainer containing the group of objects identified by
	 * the given value.
	 * 
	 * @param context
	 *            The LoadContext used to interpret the given value
	 * @param value
	 *            The value used to identify what objects should be contained in
	 *            the returned ObjectContainer
	 * @return an ObjectContainer containing the group of objects identified by
	 *         the given value
	 */
	public ObjectContainer<T> getPrimitive(LoadContext context, String value);

}
