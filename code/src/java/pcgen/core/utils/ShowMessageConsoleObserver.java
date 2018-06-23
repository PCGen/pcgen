/*
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.utils;

import java.util.Observable;
import java.util.Observer;

import pcgen.util.Logging;

public class ShowMessageConsoleObserver implements Observer
{

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(final Observable o, final Object arg)
	{
		if (arg instanceof MessageWrapper)
		{
			showMessageDialog((MessageWrapper) arg);
		}
	}

	private void showMessageDialog(final MessageWrapper messageWrapper)
	{
		Logging.errorPrint("Message");
		Logging.errorPrint("    Title: " + messageWrapper.getTitle());
		Logging.errorPrint("    Message: " + messageWrapper.getMessage());
		Logging.errorPrint("    Message Type: " + messageWrapper.getMessageType());
	}

}
