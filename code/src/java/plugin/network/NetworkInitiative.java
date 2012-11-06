package plugin.network;

import gmgen.plugin.SystemInitiative;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class NetworkInitiative extends SystemInitiative
{
	protected String uid;
	protected Socket sock;

	public NetworkInitiative(String uid, Socket sock)
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

	public void recieveNetMessage(String message)
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
