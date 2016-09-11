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
 package plugin.network.gui;

import javax.swing.JPanel;

import pcgen.system.LanguageBundle;

/**
 *
 * @author  ddjone3
 */
// TODO remove the error panel from the toolbar, and put it in a message box at the bottom of the window.
public class NetworkView extends JPanel
{
	private static final long serialVersionUID = 4984238484753403840L;
	
	private static final String DEFAULT_IP = "0.0.0.0"; //$NON-NLS-1$

	/** Creates new form NetworkView */
	public NetworkView()
	{
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		buttonGroup1 = new javax.swing.ButtonGroup();
		toolbar = new javax.swing.JToolBar();
		rbServer = new javax.swing.JRadioButton();
		rbClient = new javax.swing.JRadioButton();
		clientPanel = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		serverAddress = new javax.swing.JTextField(15); // 255.255.255.255 = 15 chars
		connectButton = new javax.swing.JButton();
		connectionPanel = new javax.swing.JPanel();
		noteTitle = new javax.swing.JLabel();
		noteText = new javax.swing.JLabel();
		logPane = new javax.swing.JTabbedPane();
		messagePanel = new javax.swing.JPanel();
		messageBox = new javax.swing.JTextField();
		messageButton = new javax.swing.JButton();
		connectedUsersPanel = new javax.swing.JPanel();
		userList = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();

		setLayout(new java.awt.BorderLayout());

		rbServer.setText(LanguageBundle.getString("in_plugin_network_server")); //$NON-NLS-1$
		buttonGroup1.add(rbServer);
		toolbar.add(rbServer);

		rbClient.setText(LanguageBundle.getString("in_plugin_network_client")); //$NON-NLS-1$
		buttonGroup1.add(rbClient);
		toolbar.add(rbClient);
		toolbar.addSeparator();

		clientPanel.setLayout(new java.awt.FlowLayout(
			java.awt.FlowLayout.CENTER, 5, 3));

		jLabel2.setText(LanguageBundle.getString("in_plugin_network_serverAddr")); //$NON-NLS-1$
		toolbar.add(jLabel2);

		serverAddress.setText(DEFAULT_IP);
		clientPanel.add(serverAddress);

		connectButton.setText(LanguageBundle.getString("in_plugin_network_connect")); //$NON-NLS-1$
		connectButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
		clientPanel.add(connectButton);

		toolbar.add(clientPanel);
		
		toolbar.addSeparator();

		connectionPanel.setLayout(new java.awt.FlowLayout(
			java.awt.FlowLayout.CENTER, 5, 6));

		connectionPanel.add(noteTitle);

		connectionPanel.add(noteText);

		toolbar.add(connectionPanel);

		add(toolbar, java.awt.BorderLayout.NORTH);

		add(logPane, java.awt.BorderLayout.CENTER);

		messagePanel.setLayout(new java.awt.BorderLayout());

		messagePanel.add(messageBox, java.awt.BorderLayout.CENTER);

		messageButton.setText(LanguageBundle.getString("in_plugin_network_sendMsg")); //$NON-NLS-1$
		messagePanel.add(messageButton, java.awt.BorderLayout.EAST);

		add(messagePanel, java.awt.BorderLayout.SOUTH);

		connectedUsersPanel.setLayout(new java.awt.BorderLayout());

		connectedUsersPanel.setBorder(new javax.swing.border.EtchedBorder());
		userList.setBorder(new javax.swing.border.EtchedBorder());
		userList.setModel(new javax.swing.AbstractListModel()
		{
			private static final long serialVersionUID = -3023217575762600450L;
			// TODO i18n Broadcast (and change in other part of plugin)
			String[] strings = {"Broadcast"};

			@Override
			public int getSize()
			{
				return strings.length;
			}

			@Override
			public Object getElementAt(int i)
			{
				return strings[i];
			}
		});
		userList
			.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		connectedUsersPanel.add(userList, java.awt.BorderLayout.CENTER);

		jLabel1.setText(LanguageBundle.getString("in_plugin_network_spaced")); //$NON-NLS-1$
		connectedUsersPanel.add(jLabel1, java.awt.BorderLayout.NORTH);

		add(connectedUsersPanel, java.awt.BorderLayout.EAST);

	}//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JPanel clientPanel;
	private javax.swing.JButton connectButton;
	private javax.swing.JPanel connectedUsersPanel;
	private javax.swing.JPanel connectionPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTabbedPane logPane;
	private javax.swing.JTextField messageBox;
	private javax.swing.JButton messageButton;
	private javax.swing.JPanel messagePanel;
	private javax.swing.JLabel noteText;
	private javax.swing.JLabel noteTitle;
	private javax.swing.JRadioButton rbClient;
	private javax.swing.JRadioButton rbServer;
	private javax.swing.JTextField serverAddress;
	private javax.swing.JToolBar toolbar;
	private javax.swing.JList userList;

	// End of variables declaration//GEN-END:variables

	public javax.swing.JTabbedPane getLogPane()
	{
		return logPane;
	}

	public javax.swing.JTextField getServerAddressTextField()
	{
		return serverAddress;
	}

	public javax.swing.JButton getConnectButton()
	{
		return connectButton;
	}

	public javax.swing.JRadioButton getServerRadioButton()
	{
		return rbServer;
	}

	public javax.swing.JRadioButton getClientRadioButton()
	{
		return rbClient;
	}

	public javax.swing.JButton getMessageButton()
	{
		return messageButton;
	}

	public javax.swing.JTextField getMessageTextField()
	{
		return messageBox;
	}

	public javax.swing.JList getUserList()
	{
		return userList;
	}

	public void setLocalAddressText(String address)
	{
		jLabel2.setText(LanguageBundle.getString("in_plugin_network_localAddr") + address); //$NON-NLS-1$
	}

	public void setConnectionText(String title, String text)
	{
		noteTitle.setText(LanguageBundle.getFormattedString("in_plugin_network_connectiontext", title)); //$NON-NLS-1$
		noteText.setText(text);
	}

	public void hideClientPanel()
	{
		clientPanel.setVisible(false);
	}

	public void showClientPanel()
	{
		jLabel2.setText(LanguageBundle.getString("in_plugin_network_serverAddr")); //$NON-NLS-1$
		clientPanel.setVisible(true);
	}

	public void showConnectionPanel()
	{
		connectionPanel.setVisible(true);
	}
}
