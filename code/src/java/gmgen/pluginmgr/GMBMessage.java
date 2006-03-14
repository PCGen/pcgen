/*
 *  GMBMessage.java - A GMBus message
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;


/**
 *  The base class of all GMBus messages.
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public abstract class GMBMessage
{
	// private members
	private GMBComponent source;
	private boolean vetoed;

	/**
	 *  Creates a new message.
	 *
	 *@param  source  The message source component
	 *@since        GMGen 3.3
	 */
	public GMBMessage(GMBComponent source)
	{
		this.source = source;
	}

	/**
	 *  Returns the sender of this message.
	 *
	 *@return    the source component
	 *@since        GMGen 3.3
	 */
	public GMBComponent getSource()
	{
		return source;
	}

	/**
	 *  Returns if this message has been vetoed by another bus component.
	 *
	 *@return    The vetoed value
	 *@since        GMGen 3.3
	 */
	public boolean isVetoed()
	{
		return vetoed;
	}

	/**
	 *  Returns a string representation of this message's parameters.
	 *
	 *@return    Description of the Return Value
	 *@since        GMGen 3.3
	 */
	public String paramString()
	{
		return "source=" + source;
	}

	/**
	 *  Returns a string representation of this message.
	 *
	 *@return    Description of the Return Value
	 *@since        GMGen 3.3
	 */
	public String toString()
	{
		return getClass().getName() + "[" + paramString() + "]";
	}

	/**
	 *  Vetoes this message. It will not be passed on further on the bus, and
	 *  instead will be returned directly to the sender with the vetoed flag on.
	 *@since        GMGen 3.3
	 */
	public void veto()
	{
		vetoed = true;
	}

	/**
	 *  A message implementation that cannot be vetoed.
	 *
	 *@author     Soulcatcher
	 *@since        GMGen 3.3
	 */
	public abstract static class NonVetoable extends GMBMessage
	{
		/**
		 *  Creates a new non-vetoable message.
		 *
		 *@param  source  The message source component
		 *@since        GMGen 3.3
		 */
		public NonVetoable(GMBComponent source)
		{
			super(source);
		}

		/**
		 *  Disallows this message from being vetoed.
		 *@since        GMGen 3.3
		 */
		public void veto()
		{
			throw new InternalError("Can't veto this message");
		}
	}
}
