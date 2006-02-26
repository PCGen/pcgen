/*
 * PaperInfo.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on February 25, 2002, 10:15 PM
 *
 * $Id: PaperInfo.java,v 1.12 2004/12/04 14:13:51 binkley Exp $
 */
package pcgen.core;


/**
 * <code>PaperInfo</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.12 $
 */
public final class PaperInfo
{
	private final String[] paperInfo = new String[7];

	public void setPaperInfo(final int infoType, final String info)
	{
		if (!validIndex(infoType))
		{
			throw new IndexOutOfBoundsException("invalid index: " + infoType);
		}

		paperInfo[infoType] = info;
	}

	String getName()
	{
		return getPaperInfo(Constants.PAPERINFO_NAME);
	}

	String getPaperInfo(final int infoType)
	{
		if (!validIndex(infoType))
		{
			return null;
		}

		return paperInfo[infoType];
	}

	private static boolean validIndex(final int index)
	{
		switch (index)
		{
			case Constants.PAPERINFO_NAME:
			case Constants.PAPERINFO_HEIGHT:
			case Constants.PAPERINFO_WIDTH:
			case Constants.PAPERINFO_TOPMARGIN:
			case Constants.PAPERINFO_BOTTOMMARGIN:
			case Constants.PAPERINFO_LEFTMARGIN:
			case Constants.PAPERINFO_RIGHTMARGIN:
				break;

			default:
				return false;
		}

		return true;
	}
}
