/*
 * CachedVariable.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 14-Mar-2004
 *
 * Current Ver: $Revision: 1.4 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/05 13:37:57 $
 *
 */
package pcgen.core.character;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class CachedVariable {
	private int serial;
	private Object value;
	/**
	 * @return Returns the serial.
	 */
	public final int getSerial() {
		return serial;
	}

	/**
	 * @param serial The serial to set.
	 */
	public final void setSerial(final int serial) {
		this.serial = serial;
	}

	/**
	 * @return Returns the value.
	 */
	public final Object getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public final void setValue(final Object value) {
		this.value = value;
	}

}
