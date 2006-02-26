/*
 * PCAlignment.java
 * Copyright 2002 (C) Greg Bingleman (byngl@hotmail.com)
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
 * Created on October 14, 2002 10:01PM
 */
package pcgen.core;


/**
 * <code>PCAlignment</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.5 $
 */
public final class PCAlignment extends PObject
{
	private boolean validForDeity = true;
	private boolean validForFollower = true;

	public void setValidForDeity(final boolean argValid)
	{
		validForDeity = argValid;
	}

	public boolean isValidForDeity()
	{
		return validForDeity;
	}

	public void setValidForFollower(final boolean argValid)
	{
		validForFollower = argValid;
	}

	public boolean isValidForFollower()
	{
		return validForFollower;
	}
}
