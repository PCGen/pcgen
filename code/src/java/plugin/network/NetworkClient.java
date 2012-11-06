package plugin.network;

import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class NetworkClient
{
	private String user = "Client";
	private NetworkModel model;
	private Socket sock;
	private BufferedReader is;
	private PrintStream os;

	public NetworkClient(NetworkModel model)
	{
		this.model = model;
		user =
				SettingsHandler.getGMGenOption(NetworkPlugin.LOG_NAME
					+ ".username", "Player");
	}

	public void startClient()
	{
		String host = model.getView().getServerAddressTextField().getText();
		int port =
				SettingsHandler.getGMGenOption(
					NetworkPlugin.LOG_NAME + ".port", 80);
		model.getView().setConnectionText("Client Status",
			"Attempting to connect to " + host + ":" + port);

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
				"Connected to " + host + ":" + port);
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

	public void sendIM(String target, String text)
	{
		sendMessage("IM", target + "|" + text);
	}

	public void sendBroadcast(String message)
	{
		sendMessage("Broadcast", message);
	}

	public void sendUserMessage(String aUser)
	{
		sendMessage("User", aUser);
	}

	public void sendLogMessage(String owner, String message)
	{
		sendMessage("Log", owner + "|" + message);
	}

	public void sendExitMessage()
	{
		sendMessage("Exit", "");
	}

	public void sendPcgMessage(String uid, String message)
	{
		sendMessage("Pcg", uid + ":" + message);
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
		int num = message.indexOf(":");
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
		BufferedReader inputStream;

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
