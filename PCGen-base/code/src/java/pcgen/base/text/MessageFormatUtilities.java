/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.text;

import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;

/**
 * MessageFormatUtilities contains utility methods related to
 * java.lang.MessageFormat.
 */
public final class MessageFormatUtilities
{
	/**
	 * Private Constructor for Utility Class.
	 */
	private MessageFormatUtilities()
	{
	}

	/**
	 * Returns the number of required arguments for the given MessageFormat.
	 * This will analyze all paths of any choices embedded in the format to
	 * ensure that any arguments they may call are also considered.
	 * 
	 * @param msgFormat
	 *            The MessageFormat to be analyzed
	 * @return the number of required arguments for the given MessageFormat
	 */
	public static int getRequriedArgumentCount(MessageFormat msgFormat)
	{
		Format[] formats = msgFormat.getFormatsByArgumentIndex();
		int required = formats.length;
		MessageFormat mf = new MessageFormat("");
		for (Format fmt : formats)
		{
			if (fmt instanceof ChoiceFormat)
			{
				mf.applyPattern(((ChoiceFormat) fmt).toPattern());
				required = Math.max(required, getRequriedArgumentCount(mf));
			}
		}
		return required;
	}

}
