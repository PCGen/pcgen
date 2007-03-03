/*
 * AddLstToken
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 * Created on February 23, 2007
 *
 * Current Ver: $Revision: 197 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 17:59:43 -0500 (Tue, 14 Mar 2006) $
 *
 */
package pcgen.persistence.lst;

import pcgen.core.PObject;

/**
 * <code>AutoLstToken</code>
 *
 * @author  Thomas Parker <thpr@users.sourceforge.net>
 */
public interface AddLstToken extends LstToken
{
	public abstract boolean parse(PObject target, String value, int level);
}
