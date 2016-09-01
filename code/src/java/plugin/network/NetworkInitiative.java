/*
 * Copyright 2003 (C) Devon Jones
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
 * $Id$
 */
 package plugin.network;

import gmgen.plugin.SystemInitiative;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

class NetworkInitiative extends SystemInitiative
{
	private String uid;
	private Socket sock;

	NetworkInitiative(String uid, Socket sock)
	{
		super();
		this.uid = uid;
		this.sock = sock;
	}

    @Override
	public void setBonus(int bonus)
	{
		super.setBonus(bonus);
		sendNetMessage("INITBONUS|" + getModifier());
	}

    @Override
	public void setCurrentInitiative(int currentInitiative)
	{
		super.setCurrentInitiative(currentInitiative);
		sendNetMessage("INIT|" + getCurrentInitiative());
	}

	private void sendNetMessage(String message)
	{
		try
		{
			PrintStream os =
					new PrintStream(new BufferedOutputStream(sock
						.getOutputStream()), true, "UTF-8");
			os.print("Pcg: " + uid + ":" + message + "\r\n");
			os.flush();
			//os.close();
		}
		catch (Exception e)
		{
			// TODO Handle This?
		}
	}

	void recieveNetMessage(String message)
	{
		String type = "";
		String value = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			type = st.nextToken();
		}
		if (st.hasMoreTokens())
		{
			value = st.nextToken();
		}

		try
		{
			if (type != "" && value != "")
			{
				if (type.equals("INITBONUS"))
				{
					super.setBonus(Integer.parseInt(value));
				}
				else if (type.startsWith("INIT"))
				{
					super.setCurrentInitiative(Integer.parseInt(value));
				}
			}
		}
		catch (Exception e)
		{
			// TODO Handle this?
		}
	}
}
