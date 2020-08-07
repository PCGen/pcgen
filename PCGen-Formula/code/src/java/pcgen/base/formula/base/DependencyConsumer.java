/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.function.Consumer;

/**
 * DependencyConsumer is a FunctionalInterface that can consume a VariableID and provide
 * items dependent on that VariableID to a given Consumer.
 */
@FunctionalInterface
public interface DependencyConsumer
{

	/**
	 * Returns a String indicating the identification of this object.
	 * 
	 * @return A String indicating the identification of this object
	 */
	public void processForDependents(VariableID<?> startingID,
		Consumer<VariableID<?>> consumer);

}
