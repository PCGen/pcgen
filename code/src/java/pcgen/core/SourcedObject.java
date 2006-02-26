/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created July 23, 2005
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2005/07/24 00:22:23 $
 */
package pcgen.core;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 */
public interface SourcedObject
{

	String getSourceWithKey(String key);

	Campaign getSourceCampaign();

}
