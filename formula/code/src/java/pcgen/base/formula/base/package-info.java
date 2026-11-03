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
/**
 * pcgen.base.formula.base is a package that represents widely-shared items
 * across pcgen.base.formula.
 * 
 * In general, items here should have very few external dependencies, and with
 * few exceptions, dependencies should be exclusive to items within the JDK or
 * outside of pcgen.base.formula.
 * 
 * It is intended that this is a "foundation" upon which many other things in
 * pcgen.base.formula.* are built upon. Therefore, it is expected that items in
 * this package will have few dependencies, but will have MANY things dependent
 * upon them. (Yes, this implies that many of the items in this package are or
 * should be interfaces).
 */
package pcgen.base.formula.base;
