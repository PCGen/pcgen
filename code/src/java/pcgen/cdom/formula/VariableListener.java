/*
 * Copyright (c) Thomas Parker, 2016.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.formula;

import java.util.EventListener;

/**
 * A VariableListener is an object that listens for VariableChangeEvents as sent
 * by a MonitorableVariableStore.
 *
 * @param <T>
 *            The Format of the underlying Variable to which the
 *            VariableListener is listening
 */
@FunctionalInterface
public interface VariableListener<T> extends EventListener
{
	/**
	 * Sends a VariableChangeEvent to the VariableListener.
	 * 
	 * @param vcEvent
	 *            The VariableChangeEvent indicating the change that took place
	 *            to a Variable
	 */
    void variableChanged(VariableChangeEvent<T> vcEvent);
}
