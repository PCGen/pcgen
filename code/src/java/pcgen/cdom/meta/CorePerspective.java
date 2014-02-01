/*
 * Copyright (c) Thomas Parker, 2013-14.
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
package pcgen.cdom.meta;

public class CorePerspective
{

	private String name;

	private CorePerspective(String name)
	{
		this.name = name;
	}
	public static final CorePerspective LANGUAGE = new CorePerspective("Granted Languages");
	public static final CorePerspective ARMORPROF = new CorePerspective("Armor Proficiencies");

	@Override
	public String toString()
	{
		return name;
	}
}
