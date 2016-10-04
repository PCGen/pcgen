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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

class NetworkClient
{
	private String user = "Client";
	private final NetworkModel model;
	private Socket sock;
	private BufferedReader is;
	private PrintStream os;

	NetworkClient(NetworkModel model)
	{
		this.model = model;
		user =
				SettingsHandler.getGMGenOption(NetworkPlugin.LOG_NAME
					+ ".username", "Player");
	}

	void startClient()
	{
		String host = model.getView().getServerAddressTextField().getText();
		int port =
				SettingsHandler.getGMGenOption(
					NetworkPlugin.LOG_NAME + ".port", 80);
		model.getView().setConnectionText("Client Status",
			"Attempting to connect to " + host + ':' + port);

		try
		{
			sock = new Socket(host, port);
			is =
					new BufferedReader(new InputStreamReader(sock
						.getInputStream(), "UTF-8"));
			os =
					new PrintStream(new BufferedOutputStream(sock
						.getOutputStream()), true, "UTF-8");
			new Handler(is).start();
			sendUserMessage(user);
			model.getView().setConnectionText("Client Status",
				"Connected to " + host + ':' + port);
			SettingsHandler.setGMGenOption(NetworkPlugin.LOG_NAME
				+ ".ipAddress", host);
			model.refresh();
		}
		catch (Exception e)
		{
			model.getView().setConnectionText("Server Error", e.getMessage());
			model.resetClient();
		}
	}

	public String getUser()
	{
		return user;
	}

	void sendIM(String target, String text)
	{
		sendMessage("IM", target + '|' + text);
	}

	void sendBroadcast(String message)
	{
		sendMessage("Broadcast", message);
	}

	private void sendUserMessage(String aUser)
	{
		sendMessage("User", aUser);
	}

	void sendLogMessage(String owner, String message)
	{
		sendMessage("Log", owner + '|' + message);
	}

	void sendExitMessage()
	{
		sendMessage("Exit", "");
	}

	void sendPcgMessage(String uid, String message)
	{
		sendMessage("Pcg", uid + ':' + message);
	}

	private synchronized void sendMessage(String type, String message)
	{
		os.print(type + ": " + message + "\r\n");
		os.flush();
	}

	private void handleRemoveUserMessage(String aUser)
	{
		model.removeUser(aUser);
	}

	private void handleAddUserMessage(String aUser)
	{
		if (!aUser.equals(this.user))
		{
			model.addUser(aUser);
		}
	}

	private void handlePcgMessage(String message, Socket socket)
	{
		int num = message.indexOf(':');
		String uid = message.substring(0, num);
		String messagetext = message.substring(num + 1);
		model.handleServerPcgMessage(uid, messagetext, socket);
	}

	private void handleLogMessage(String aUser, String message)
	{
		String owner = "";
		String log = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			owner = st.nextToken();
		}
		if (st.hasMoreTokens())
		{
			log = st.nextToken();
		}
		model.log(aUser, owner, log);
	}

	private void handleIMMessage(String message)
	{
		String aUser = "";
		String log = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			aUser = st.nextToken();
		}
		if (st.hasMoreTokens())
		{
			log = st.nextToken();
		}
		model.log(aUser, log);
	}

	private void handleBroadcastMessage(String message)
	{
		String aUser = "";
		String log = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			aUser = st.nextToken();
		}
		if (st.hasMoreTokens())
		{
			log = st.nextToken();
		}
		model.log(aUser, "BROADCAST", log);
	}

	private String handleMessage(String message, Socket socket)
	{
		String retValue = "";
		if (message.startsWith("Pcg:"))
		{
			handlePcgMessage(message.substring(5), socket);
		}
		else if (message.startsWith("RemoveUser:"))
		{
			handleRemoveUserMessage(message.substring(12));
		}
		else if (message.startsWith("AddUser:"))
		{
			handleAddUserMessage(message.substring(9));
		}
		else if (message.startsWith("Log:"))
		{
			handleLogMessage("Server", message.substring(5));
		}
		else if (message.startsWith("IM:"))
		{
			handleIMMessage(message.substring(4));
		}
		else if (message.startsWith("Broadcast:"))
		{
			handleBroadcastMessage(message.substring(11));
		}
		else if (message.startsWith("Exit:"))
		{
			sendExitMessage();
		}
		else
		{
			retValue = "Return: " + message;
		}
		return retValue;
	}

	protected class Handler extends Thread
	{
		private final BufferedReader inputStream;

		public Handler(BufferedReader is)
		{
			this.inputStream = is;
		}

        @Override
		public void run()
		{
			try
			{
				String line;
				while ((line = inputStream.readLine()) != null)
				{
					String retString = "";
					Logging.debugPrint("Network message from Server: " + line);
					try
					{
						retString = handleMessage(line, sock);

						if (!retString.isEmpty())
						{
							os.print(retString + "\r\n");
							os.flush();
						}
					}
					catch (Exception e)
					{
						if (!e.getMessage().isEmpty())
						{
							os.print("Error: " + e.getMessage());
							os.flush();
						}
						break;
					}
				}
			}
			catch (Exception e)
			{
				return;
			}
			model.resetClient();
		}
	}
}
