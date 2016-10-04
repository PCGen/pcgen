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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.gui.ExtendedHTMLDocument;
import gmgen.gui.ExtendedHTMLEditorKit;
import gmgen.plugin.Combatant;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.util.LogReceiver;
import gmgen.util.LogUtilities;
import pcgen.core.SettingsHandler;

import plugin.network.gui.NetworkView;

/**
 *
 * @author  soulcatcher
 */
public class NetworkModel
{
	private NetworkView view = new NetworkView();
	static List<Color> colorList = new ArrayList<>();
	static
	{
		colorList.add(Color.BLACK);
		colorList.add(Color.BLUE);
		colorList.add(Color.RED);
		colorList.add(Color.GREEN);
		colorList.add(Color.DARK_GRAY);
		colorList.add(Color.ORANGE);
		colorList.add(Color.CYAN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.LIGHT_GRAY);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GRAY);
		colorList.add(Color.PINK);
	}
	private NetworkServer server;
	private NetworkClient client;
	private InitHolderList combat;
	private HashMap<String, Combatant> sentCombatants =
			new HashMap<>();
	private HashMap<String, NetworkCombatant> recievedCombatants =
			new HashMap<>();

	public NetworkModel()
	{
		applyPrefs();
		initLogging();
		initView();
		addListeners();
	}

	public NetworkView getView()
	{
		return view;
	}

	/**
	 * This method currently does nothing
	 */
	public void closeWindow()
	{
		// Do Nothing
	}

	/**
	 * This method currently does nothing
	 */
	public void applyPrefs()
	{
		// Do Nothing
	}

	public void handleServerPcgMessage(String uid, String messagetext,
		Socket sock)
	{
		if (sentCombatants.containsKey(uid))
		{
			Combatant cbt = sentCombatants.get(uid);
			NetworkCombatant.recieveServerMessage(messagetext, cbt);
		}
	}

	public void handlePcgMessage(String uid, String messagetext, Socket sock)
	{
		if (recievedCombatants.containsKey(uid))
		{
			NetworkCombatant cbt = recievedCombatants.get(uid);
			cbt.recieveNetMessage(messagetext);
		}
		else
		{
			NetworkCombatant cbt = new NetworkCombatant(uid, sock);
			cbt.recieveNetMessage(messagetext);
			recievedCombatants.put(uid, cbt);
			combat.add(cbt);
		}
	}

	public void sendCombatant(Combatant cbt)
	{
		String user =
				SettingsHandler.getGMGenOption(NetworkPlugin.LOG_NAME
					+ ".username", "Player");
		if (client != null)
		{
			sentCombatants
				.put(NetworkCombatant.getCombatantUid(cbt, user), cbt);
			NetworkCombatant.sendCombatant(cbt, client);
		}
	}

	void combatantUpdated(Combatant cbt)
	{
		if (combat != null)
		{
			combat.stream()
			      .filter((InitHolder v) -> v == cbt)
			      .forEach((InitHolder v) -> sendCombatant(cbt));
		}
	}

	private void initView()
	{
		view.getClientRadioButton().setSelected(true);
		view.getServerAddressTextField().setText(
			SettingsHandler.getGMGenOption(NetworkPlugin.LOG_NAME
				+ ".ipAddress", "0.0.0.0"));
		initList();
	}

	private void initList()
	{
		view.getUserList().setModel(new DefaultListModel());
		addUser("Broadcast");
	}

	private void addListeners()
	{
		view.getServerRadioButton()
			.addActionListener(new RadioActionListener());
		view.getClientRadioButton()
			.addActionListener(new RadioActionListener());
		view.getConnectButton().addActionListener(
			new ConnectionButtonActionListener());
		view.getLogPane().addFocusListener(new TabFocusListener());
		view.getMessageButton().addActionListener(
			new MessageButtonActionListener());
	}

	private void displayClientToolbar()
	{
		view.showClientPanel();
		view.showConnectionPanel();
	}

	private void displayServerToolbar()
	{
		view.hideClientPanel();
	}

	private void startServer()
	{
		server = new NetworkServer(this);
		server.start();
	}

	private void stopServer()
	{
		if (server != null)
		{
			NetworkServer serverThread = null;
			ThreadGroup tg = server.getThreadGroup();
			// The Thread Group could be null if the server never started properly
			if (tg != null)
			{
				Thread[] tl = new Thread[tg.activeCount()];
				tg.enumerate(tl);
				for (Thread t : tl)
				{
					if (t instanceof NetworkServer)
					{
						serverThread = (NetworkServer) t;
					}
					else if (t instanceof NetworkServer.Handler)
					{
						((NetworkServer.Handler) t).setRun(false);
					}
				}
				if (serverThread != null)
				{
					serverThread.setRun(false);
				}
			}
		}
		server = null;
		log("Local", "Local", "Server Shut Down");
		initList();
	}

	private void startClient()
	{
		view.getConnectButton().setText("Disconnect");
		client = new NetworkClient(this);
		client.startClient();
	}

	private void stopClient()
	{
		if (client != null)
		{
			client.sendExitMessage();
		}
	}

	public void resetClient()
	{
		client = null;
		view.getConnectButton().setText("Connect");
		getView()
			.setConnectionText("Client Status", "Disconnected from Server");
		initList();
	}

	private void initLogging()
	{
		LogUtilities.inst().addReceiver(new NetworkLogReciever());

		JTabbedPane logPane = view.getLogPane();

		JTextPane pane = new JTextPane();
		pane.setEditable(false);
		ExtendedHTMLEditorKit htmlKit = new ExtendedHTMLEditorKit();
		pane.setEditorKit(htmlKit);
		ExtendedHTMLDocument extDoc =
				(ExtendedHTMLDocument) (htmlKit.createDefaultDocument());
		extDoc.putProperty("number", 0);
		logPane.add("Logs", new JScrollPane(pane));

		JTextPane pane2 = new JTextPane();
		EditorKit kit = pane2.getEditorKit();
		Document doc = kit.createDefaultDocument();
		doc.putProperty("number", 1);
	}

	public void log(String title, String message)
	{
		JTextPane logsPane = getLogPane("Logs");
		SimpleDateFormat dateFmt = new SimpleDateFormat("hh.mm.ss a z");

		ExtendedHTMLDocument logsDoc =
				(ExtendedHTMLDocument) logsPane.getDocument();
		int i = getUserNumber(title);
		Color c = getLineColor(i);
		try
		{
			logsDoc.insertAfterEnd(logsDoc.getCharacterElement(logsDoc
				.getLength()), "<br>\n<b>"
				+ dateFmt.format(Calendar.getInstance().getTime())
				+ " <font color='#" + Integer.toHexString(c.getRGB()) + "'>" + title
				+ "</b></font>: " + message);
			setPaneIcon();
		}
		catch (Exception e)
		{
			//damn
		}
	}

	public void log(String title, String owner, String message)
	{
		JTextPane logsPane = getLogPane("Logs");
		SimpleDateFormat dateFmt = new SimpleDateFormat("hh.mm.ss a z");

		ExtendedHTMLDocument logsDoc =
				(ExtendedHTMLDocument) logsPane.getDocument();
		int i = getUserNumber(title);
		Color c = getLineColor(i);
		try
		{
			logsDoc.insertAfterEnd(logsDoc.getCharacterElement(logsDoc
				.getLength()), "<br>\n<b>"
				+ dateFmt.format(Calendar.getInstance().getTime())
				+ " <font color='#" + Integer.toHexString(c.getRGB()) + "'>" + title
				+ "</b></font> " + owner + ": " + message);
			setPaneIcon();
		}
		catch (Exception e)
		{
			//damn
		}
	}

	public int getUserNumber(String user)
	{
		JList list = view.getUserList();
		DefaultListModel model = (DefaultListModel) list.getModel();
		if (model.contains(user))
		{
			return model.indexOf(user);
		}
		return 0;
	}

	public void addUser(String user)
	{
		JList list = view.getUserList();
		DefaultListModel model = (DefaultListModel) list.getModel();
		if (!model.contains(user))
		{
			model.addElement(user);
		}
	}

	public void removeUser(String user)
	{
		JList list = view.getUserList();
		DefaultListModel model = (DefaultListModel) list.getModel();
		model.removeElement(user);
	}

	public String getSelectedUser()
	{
		JList list = view.getUserList();
		return list.getSelectedValue().toString();
	}

	private Color getLineColor(int num)
	{
		return colorList.get(num >= colorList.size() ? num % colorList.size()
			: num);
	}

	private JTextPane getLogPane(String title)
	{
		JTabbedPane logPane = view.getLogPane();
		for (int i = 0; i < logPane.getTabCount(); i++)
		{
			if (logPane.getTitleAt(i).equals(title))
			{
				return (JTextPane) ((JScrollPane) logPane.getComponent(i))
					.getViewport().getView();
			}
		}

		//doesn't exist, create a new pane
		JTextPane pane = new JTextPane();
		EditorKit kit = pane.getEditorKit();
		kit.createDefaultDocument();
		logPane.add(title, new JScrollPane(pane));

		return pane;
	}

	public void setPaneIcon()
	{
		JTabbedPane logPane = view.getLogPane();
		JTabbedPane gmgenPane = GMGenSystemView.getTabPane();
		if (gmgenPane.getSelectedComponent() instanceof NetworkView
			&& GMGenSystem.inst.isFocused())
		{
			return;
		}
		logPane.setIconAt(logPane.indexOfTab("Logs"),
			new javax.swing.ImageIcon(getClass().getResource(
				"/pcgen/resource/images/NewEnvelope.gif")));

		if (!(gmgenPane.getSelectedComponent() instanceof NetworkView))
		{
			int index = gmgenPane.indexOfComponent(view);
			javax.swing.ImageIcon icon =
					new javax.swing.ImageIcon(getClass().getResource(
						"/pcgen/resource/images/NewEnvelope.gif"));
			gmgenPane.setIconAt(index, icon);
		}
	}

	public void clearIcon()
	{
		JTabbedPane logPane = view.getLogPane();
		logPane.setIconAt(logPane.getSelectedIndex(), null);
	}

	public void sendMessage()
	{
		try
		{
			if (server != null)
			{
				server.sendIM("GM", getSelectedUser(), view
					.getMessageTextField().getText());
			}
			else if (client != null)
			{
				if (getSelectedUser().equals("Broadcast"))
				{
					client.sendBroadcast(view.getMessageTextField().getText());
				}
				else
				{
					client.sendIM(getSelectedUser(), view.getMessageTextField()
						.getText());
				}
			}
		}
		finally
		{
			view.getMessageTextField().setText("");
		}
	}

	public void setCombat(InitHolderList combat)
	{
		this.combat = combat;
	}

	public InitHolderList getCombat()
	{
		return combat;
	}

	public void refresh()
	{
		if (combat != null)
		{
			combat.stream()
					.filter(v -> v instanceof Combatant)
					.forEach((InitHolder v) -> sendCombatant((Combatant) v));
		}
	}

	private class NetworkLogReciever implements LogReceiver
	{
		public NetworkLogReciever()
		{
			// Empty Constructor
		}

		/**
		 * Logs a message associated with a specific owner.
		 *
		 * @param owner the owner of the message being logged.
		 * @param message the message to log.
		 */
        @Override
		public void logMessage(String owner, String message)
		{
			log("Local", owner, message);
			if (client != null)
			{
				client.sendLogMessage(owner, message);
			}
		}

		/**
		 * Logs a message not associated with a specific owner.
		 *
		 * @param message the message to log.
		 */
        @Override
		public void logMessage(String message)
		{
			logMessage("Misc", message);
		}
	}

	private class RadioActionListener implements ActionListener
	{
        @Override
		public void actionPerformed(ActionEvent evt)
		{
			if (view.getServerRadioButton().isSelected())
			{
				displayServerToolbar();
				startServer();
			}
			else
			{
				try
				{
					stopServer();
				}
				catch (Exception e)
				{
					log("Local", "Local", "Failed to Shutdown Server.");
				}
				try
				{
					displayClientToolbar();
				}
				catch (Exception e)
				{
					log("Local", "Local",
						"Failed to Display the client tool bar.");
				}
			}
		}
	}

	private class ConnectionButtonActionListener implements ActionListener
	{
        @Override
		public void actionPerformed(ActionEvent evt)
		{
			if (client == null)
			{
				startClient();
			}
			else
			{
				stopClient();
			}
		}
	}

	private class TabFocusListener extends FocusAdapter
	{
        @Override
		public void focusGained(FocusEvent evt)
		{
			clearIcon();
		}

	}

	private class MessageButtonActionListener implements ActionListener
	{
        @Override
		public void actionPerformed(ActionEvent evt)
		{
			sendMessage();
		}
	}
}
