/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

/**
 * EqModFormatCat refers to different methods used to format the extended name
 * of Equipment when Equipment Modifiers are applied to the Equipment. An
 * EqModFormatCat is placed into the EquipmentModifier to establish where in the
 * name the identification of the EquipmentModifier should occur
 * 
 * It is designed to hold formatting choices in a type-safe fashion, so that
 * they can be quickly compared and use less memory when identical
 * EqModFormatCats exist in two CDOMObjects.
 */
public enum EqModFormatCat
{

	FRONT, MIDDLE, PARENS

}
