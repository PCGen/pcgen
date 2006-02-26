/*
 * PrePointBuyMethodParser.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 16, 2005
 *
 * $Id: PrePointBuyMethodParser.java,v 1.1 2005/09/19 18:09:53 byngl Exp $
 */
package pcgen.persistence.lst.prereq;


/**
 * <code>PrePointBuyMethodParser</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public class PrePointBuyMethodParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"POINTBUYMETHOD"};
	}

}
