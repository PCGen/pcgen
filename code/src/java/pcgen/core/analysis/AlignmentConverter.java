/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.analysis;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.util.Logging;

public class AlignmentConverter
{
	private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;

	public static PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		PCAlignment desiredAlign;
		try
		{
			final int align = Integer.parseInt(desiredAlignIdentifier);
			List<PCAlignment> alignments = Globals.getContext().ref
					.getOrderSortedCDOMObjects(ALIGNMENT_CLASS);
			desiredAlign = alignments.get(align);
		}
		catch (NumberFormatException e)
		{
			// If it isn't a number, we expect the exception
			desiredAlign = Globals.getContext().ref.getAbbreviatedObject(
					ALIGNMENT_CLASS, desiredAlignIdentifier);
		}
		if (desiredAlign == null)
		{
			Logging.errorPrint("Unable to find alignment that matches: "
					+ desiredAlignIdentifier);
		}
		return desiredAlign;
	}

	public static PCAlignment getNoAlignment()
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				ALIGNMENT_CLASS, Constants.NONE);
	}
}
