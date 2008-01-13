/*
 * WeaponProfToken
 * Copyright 2008
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
 * Created on September 2, 2002, 8:16 AM
 *
 * Current Ver: $Revision: 197 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 18:59:43 -0400 (Tue, 14 Mar 2006) $
 *
 */
package pcgen.persistence.lst;

import pcgen.core.ArmorProf;

/**
 * <code>ArmorProfToken</code>
 */
public interface ArmorProfLstToken extends LstToken
{
	/**
	 * Parses an ArmorProf object
	 * 
	 * @param prof
	 * @param value
	 * @return true if parse OK
	 */
	public abstract boolean parse(ArmorProf prof, String value);
}
