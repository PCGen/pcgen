/*
 * LevelProperty.java
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
 * PCClass Created on April 21, 2001, 2:15 PM
 *   extracted from PCClass 10/24/06 thpr@sourceforge.net
 *
 * $Id: PCClass.java 1522 2006-10-24 22:40:09Z thpr $
 */
package pcgen.core;

public class LevelProperty<T> {
	/*
	 * CONSIDER Someday, should T extend PObject?
	 */
	private int propLevel = 0;

	private T object;

	public LevelProperty(final int argLevel, final T argObject) {
		propLevel = argLevel;
		object = argObject;
	}

	public final int getLevel() {
		return propLevel;
	}

	public final T getObject() {
		return object;
	}
	
	public static <C> LevelProperty<C> getLevelProperty(int level, C property) {
		return new LevelProperty<C>(level, property);
	}
}