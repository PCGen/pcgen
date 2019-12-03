/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.persistence.lst;

import pcgen.cdom.base.CDOMObject;

public interface DeprecatedToken
{

	/**
	 * Get the message to output.
	 * 
	 * @param obj The object being built when the deprecated token was encountered.
	 * @param value The value of the deprecated token.
	 * 
	 * @return A message to display to the user about why the token was deprecated
	 * and how they can fix it.  This message should be i18n.
	 */
    String getMessage(CDOMObject obj, String value);
}
