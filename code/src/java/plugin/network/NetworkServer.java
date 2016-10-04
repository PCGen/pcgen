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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

import plugin.network.gui.NetworkView;

class NetworkServer extends Thread
{
	private final NetworkModel model;
	private static final List<String> clients = new ArrayList<>();
	private boolean run = true;
	private ServerSocket sock;

	NetworkServer(NetworkModel model)
	{
		this.model = model;
	}

    @Override
	public void run()
	{
		try
		{
			startServer();
		}
		catch (final UnknownHostException uhe)
		{
			model.getView().setConnectionText("Server Error",
				"Cannot determine local address");
		}
		catch (final Exception e)
		{
			model.getView().setConnectionText("Server Error", e.getMessage());
		}
	}

	private void startServer() throws IOException
	{
		NetworkView view = model.getView();
		int port =
				SettingsHandler.getGMGenOption(
					NetworkPlugin.LOG_NAME + ".port", 80);
		view.setConnectionText("Server Status", "Starting");
		InetAddress inetadr = InetAddress.getLocalHost();
		view.setLocalAddressText(inetadr.getHostAddress() + ":" + port);
		run = true;

		sock = new ServerSocket(port);
		view.setConnectionText("Server Status", "Started");
		while (run)
		{
			try(Socket clientSocket = sock.accept())
			{
				new Handler(clientSocket).start();
			}
			catch (final IOException e)
			{
				view.setConnectionText("Server Status", "Stopped");
			}
		}
		sock.close();
	}

	public void setRun(boolean run)
	{
		if (sock != null)
		{
			try
			{
				sock.close();
				this.run = run;
			}
			catch (final IOException e)
			{
				// TODO Handle this?
			}
		}
	}

	private void sendRemoveUser(String user)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (final Thread t : tl)
		{
			if (t instanceof Handler)
			{
				((Handler) t).sendRemoveUser(user);
			}
		}
	}

	private void sendAllAddUser(String user)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (final Thread t : tl)
		{
			if (t instanceof Handler)
			{
				((Handler) t).sendAddUser("GM");
				for (final String client : NetworkServer.clients)
				{
					((Handler) t).sendAddUser(client);
				}
			}
		}
	}

	void sendIM(String source, String target, String text)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (final Thread t : tl)
		{
			if (t instanceof Handler)
			{
				if (target.equals("Broadcast"))
				{
					((Handler) t).sendBroadcast(source, text);
				}
				else if (((Handler) t).getUser().equals(target))
				{
					((Handler) t).sendIM(source, text);
				}
			}
		}
	}

	private void sendBroadcast(String user, String text)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (final Thread t : tl)
		{
			if (t instanceof Handler)
			{
				((Handler) t).sendBroadcast(user, text);
			}
		}
	}

	private String handleUserMessage(String message) throws Exception
	{
		for (final String test : NetworkServer.clients)
		{
			if (test.equals(message))
			{
				throw new Exception(
					"User with the name of "
						+ message
						+ " already connected.  Go to Edit->Preferences in GMGen.  Under the network folder, set the User Name to a different value.");
			}
		}
		NetworkServer.clients.add(message);
		sendAllAddUser(message);
		model.addUser(message);
		model.log(message, "Network", "Connected");
		model.getView().setConnectionText("Server Status",
			message + " Connected.  " + NetworkServer.clients.size() + " clients connected");
		return message;
	}

	private void handleExitMessage(String user) throws Exception
	{
		model.log(user, "Network", "Disconnected");
		for (final String test : NetworkServer.clients)
		{
			if (test.equals(user))
			{
				NetworkServer.clients.remove(test);
				break;
			}
			model.removeUser(user);
			sendRemoveUser(user);
		}
		throw new Exception("");
	}

	private void handleLogMessage(String user, String message)
	{
		String owner = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			owner = st.nextToken();
		}
		String log = "";
		if (st.hasMoreTokens())
		{
			log = st.nextToken();
		}
		model.log(user, owner, log);
	}

	private void handleIMMessage(String user, String message)
	{
		String owner = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			owner = st.nextToken();
		}
		String log = "";
		if (st.hasMoreTokens())
		{
			log = st.nextToken();
		}
		if (owner.equals("GM"))
		{
			model.log(user, log);
		}
		else
		{
			model.log(user, "(to " + owner + ")", log);
			sendIM(user, owner, log);
		}
	}

	private void handleBroadcastMessage(String user, String message)
	{
		model.log(user, "BROADCAST", message);
		sendBroadcast(user, message);
	}

	private void handlePcgMessage(String message, Socket socket)
	{
		int num = message.indexOf(':');
		String uid = message.substring(0, num);
		String messagetext = message.substring(num + 1);
		model.handlePcgMessage(uid, messagetext, socket);
	}

	public String handleMessage(String message, Socket socket) throws Exception
	{
		String user = "Client";
		if (message.startsWith("User:"))
		{
			user = handleUserMessage(message.substring(6));
		}
		else
		{
			handleMessage(user, message, socket);
		}
		return user;
	}

	public String handleMessage(String user, String message, Socket socket)
		throws Exception
	{
		String retValue = "";
		if (message.startsWith("Exit:"))
		{
			handleExitMessage(user);
		}
		else if (message.startsWith("Log:"))
		{
			handleLogMessage(user, message.substring(5));
		}
		else if (message.startsWith("Pcg:"))
		{
			handlePcgMessage(message.substring(5), socket);
		}
		else if (message.startsWith("IM:"))
		{
			handleIMMessage(user, message.substring(4));
		}
		else if (message.startsWith("Broadcast:"))
		{
			handleBroadcastMessage(user, message.substring(11));
		}
		else
		{
			retValue = "Return: " + message;
		}
		return retValue;
	}

	protected class Handler extends Thread
	{
		private final Socket socket;
		private PrintStream os = null;
		private String user = "Client";

		public Handler(Socket sock)
		{
			this.socket = sock;
		}

		private synchronized void sendMessage(String type, String message)
		{
			os.print(type + ": " + message + "\r\n");
			os.flush();
		}

		private void sendIM(String source, String message)
		{
			sendMessage("IM", source + "|" + message);
		}

		private void sendBroadcast(String aUser, String message)
		{
			sendMessage("Broadcast", aUser + "|" + message);
		}

		private void sendRemoveUser(String aUser)
		{
			sendMessage("RemoveUser", aUser);
		}

		private void sendAddUser(String aUser)
		{
			sendMessage("AddUser", aUser);
		}

		void sendExitMessage()
		{
			sendMessage("Exit", "");
		}

        @Override
		public void run()
		{
			NetworkView view = model.getView();
			try
			{
				view.setConnectionText("Server Status", user + " Connected.  "
					+ NetworkServer.clients.size() + " clients connected");
				BufferedReader is =
						new BufferedReader(new InputStreamReader(socket
							.getInputStream(), "UTF-8"));
				os =
						new PrintStream(new BufferedOutputStream(socket
							.getOutputStream()), true, "UTF-8");
				String line;
				String disconnection = " Disconnected. ";
				while ((line = is.readLine()) != null)
				{
					Logging.debugPrint("Network message from Client: " + line);
					try
					{
						String retString = "";
						if (user.equals("Client"))
						{
							user = handleMessage(line, socket);
						}
						else
						{
							retString = handleMessage(user, line, socket);
						}

						if (!retString.isEmpty())
						{
							os.print(retString + "\r\n");
							os.flush();
						}
					}
					catch (final Exception e)
					{
						if (!e.getMessage().isEmpty())
						{
							disconnection =
									" Disconnected, " + e.getMessage() + ". ";
							os.print("Error: " + e.getMessage());
							os.flush();
						}
						break;
					}
				}
				os.close();
				is.close();
				socket.close();
				view.setConnectionText("Server Status", user + disconnection
					+ NetworkServer.clients.size() + " clients connected");
			}
			catch (final IOException e)
			{
				for (final String test : NetworkServer.clients)
				{
					if (test.equals(user))
					{
						NetworkServer.clients.remove(test);
						break;
					}
				}
				view.setConnectionText("Server Error", "IO Error on socket");
			}
		}

		public String getUser()
		{
			return user;
		}

		public void setUser(String user)
		{
			this.user = user;
		}
	}
}
