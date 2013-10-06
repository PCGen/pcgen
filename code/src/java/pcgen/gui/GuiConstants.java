/*
 * Constants.java
 * Copyright 2002 (C) Jonas Karlsson
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
 * $Id$
 */
package pcgen.gui;


/**
 * This interface holds a few gui constants.
 * These constants were moved from assorted GUI classes in order to reduce connections between
 * the core and gui packages.
 *
 * @author     Jonas Karlsson
 * @version    $Revision$
 */
public interface GuiConstants
{

	//output orders for tables
	/** INFOSKILLS_OUTPUT_BY_NAME_ASC = 0 */
	int INFOSKILLS_OUTPUT_BY_NAME_ASC    = 0;
	/** INFOSKILLS_OUTPUT_BY_NAME_DSC = 1 */
	int INFOSKILLS_OUTPUT_BY_NAME_DSC    = 1;
	/** INFOSKILLS_OUTPUT_BY_TRAINED_ASC = 2 */
	int INFOSKILLS_OUTPUT_BY_TRAINED_ASC = 2;
	/** INFOSKILLS_OUTPUT_BY_TRAINED_DSC = 3 */
	int INFOSKILLS_OUTPUT_BY_TRAINED_DSC = 3;
	/** INFOSKILLS_OUTPUT_BY_MANUAL = 4 */
	int INFOSKILLS_OUTPUT_BY_MANUAL      = 4;
}
