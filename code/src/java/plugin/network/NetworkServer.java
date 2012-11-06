package plugin.network;

import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import plugin.network.gui.NetworkView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class NetworkServer extends Thread
{
	private NetworkModel model;
	private static List<String> clients = new ArrayList<String>();
	boolean run = true;
	protected ServerSocket sock;

	public NetworkServer(NetworkModel model)
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
		catch (UnknownHostException uhe)
		{
			model.getView().setConnectionText("Server Error",
				"Cannot determine local address");
		}
		catch (Exception e)
		{
			model.getView().setConnectionText("Server Error", e.getMessage());
		}
	}

	public void startServer() throws Exception
	{
		Socket clientSocket;
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
			try
			{
				clientSocket = sock.accept();
				new Handler(clientSocket).start();
			}
			catch (Exception e)
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
			catch (Exception e)
			{
				// TODO Handle this?
			}
		}
	}

	public void sendRemoveUser(String user)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (Thread t : tl)
		{
			if (t instanceof NetworkServer.Handler)
			{
				((NetworkServer.Handler) t).sendRemoveUser(user);
			}
		}
	}

	public void sendAllAddUser(String user)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (Thread t : tl)
		{
			if (t instanceof NetworkServer.Handler)
			{
				((NetworkServer.Handler) t).sendAddUser("GM");
				for (String client : clients)
				{
					((NetworkServer.Handler) t).sendAddUser(client);
				}
			}
		}
	}

	public void sendIM(String source, String target, String text)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (Thread t : tl)
		{
			if (t instanceof NetworkServer.Handler)
			{
				if (target.equals("Broadcast"))
				{
					((NetworkServer.Handler) t).sendBroadcast(source, text);
				}
				else if (((NetworkServer.Handler) t).getUser().equals(target))
				{
					((NetworkServer.Handler) t).sendIM(source, text);
				}
			}
		}
	}

	public void sendBroadcast(String user, String text)
	{
		ThreadGroup tg = getThreadGroup();
		Thread[] tl = new Thread[tg.activeCount()];
		tg.enumerate(tl);
		for (Thread t : tl)
		{
			if (t instanceof NetworkServer.Handler)
			{
				((NetworkServer.Handler) t).sendBroadcast(user, text);
			}
		}
	}

	private String handleUserMessage(String message) throws Exception
	{
		for (String test : clients)
		{
			if (test.equals(message))
			{
				throw new Exception(
					"User with the name of "
						+ message
						+ " already connected.  Go to Edit->Preferences in GMGen.  Under the network folder, set the User Name to a different value.");
			}
		}
		clients.add(message);
		sendAllAddUser(message);
		model.addUser(message);
		model.log(message, "Network", "Connected");
		model.getView().setConnectionText("Server Status",
			message + " Connected.  " + clients.size() + " clients connected");
		return message;
	}

	private void handleExitMessage(String user) throws Exception
	{
		model.log(user, "Network", "Disconnected");
		for (String test : clients)
		{
			if (test.equals(user))
			{
				clients.remove(test);
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
		model.log(user, owner, log);
	}

	private void handleIMMessage(String user, String message)
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
		int num = message.indexOf(":");
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
		Socket socket;
		PrintStream os = null;
		boolean isRun = true;
		String user = "Client";

		public Handler(Socket sock)
		{
			this.socket = sock;
		}

		private synchronized void sendMessage(String type, String message)
		{
			os.print(type + ": " + message + "\r\n");
			os.flush();
		}

		public void sendIM(String source, String message)
		{
			sendMessage("IM", source + "|" + message);
		}

		public void sendBroadcast(String aUser, String message)
		{
			sendMessage("Broadcast", aUser + "|" + message);
		}

		public void sendRemoveUser(String aUser)
		{
			sendMessage("RemoveUser", aUser);
		}

		public void sendAddUser(String aUser)
		{
			sendMessage("AddUser", aUser);
		}

		public void sendExitMessage()
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
					+ clients.size() + " clients connected");
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
					String retString = "";
					Logging.debugPrint("Network message from Client: " + line);
					try
					{
						if (user.equals("Client"))
						{
							user = handleMessage(line, socket);
						}
						else
						{
							retString = handleMessage(user, line, socket);
						}

						if (!retString.equals(""))
						{
							os.print(retString + "\r\n");
							os.flush();
						}
					}
					catch (Exception e)
					{
						if (!e.getMessage().equals(""))
						{
							disconnection =
									" Disconnected, " + e.getMessage() + ". ";
							os.print("Error: " + e.getMessage());
							os.flush();
						}
						break;
					}
					if (!isRun)
					{
						break;
					}
				}
				os.close();
				is.close();
				socket.close();
				view.setConnectionText("Server Status", user + disconnection
					+ clients.size() + " clients connected");
			}
			catch (Exception e)
			{
				for (String test : clients)
				{
					if (test.equals(user))
					{
						clients.remove(test);
						break;
					}
				}
				view.setConnectionText("Server Error", "IO Error on socket");
				return;
			}
		}

		public void setRun(boolean run)
		{
			sendExitMessage();
		}

		public Socket getSocket()
		{
			return socket;
		}

		public PrintStream getOutputStream()
		{
			return os;
		}

		public String getUser()
		{
			return user;
		}

		public void setOutputStream(PrintStream os)
		{
			this.os = os;
		}

		public void setUser(String user)
		{
			this.user = user;
		}
	}
}
