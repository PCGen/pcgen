/*
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  NetworkCombatant.java
 *
 *  Created on January 24, 2002, 11:15 AM
 */
package plugin.network;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import gmgen.plugin.Combatant;
import gmgen.plugin.State;
import gmgen.plugin.SystemAttribute;
import gmgen.plugin.SystemHP;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A combatant that can be interacted with over the network.
 */
@SuppressWarnings("WeakerAccess")
public class NetworkCombatant extends Combatant
{
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkCombatant.class);

	private String name = "";
	private final String uid;
	private String player = "";
	private String htmlString = "";
	private float cr = 0;
	private int xp = 0;
	private final Socket sock;

	/**
	 *  Creates new Combatant
	 * @param uid human readable identifier
	 * @param sock socket to chat with user
	 */
	public NetworkCombatant(String uid, Socket sock)
	{
		this.uid = uid;
		this.sock = sock;
		this.init = new NetworkInitiative(uid, sock);
		this.hitPoints =
				new SystemHP(new SystemAttribute("Constitution", 10), 1, 1);
	}

    @Override
	public void setCR(float cr)
	{
		this.cr = cr;
		sendNetMessage("CR|" + cr);
	}

    @Override
	public float getCR()
	{
		return cr;
	}

	@Override
	public String getName()
	{
		return name;
	}

	/**
	 *  Sets the Combatant Type.
	 * @param comType valid COMTYPE tag
	 *
	 */
    @Override
	public void setCombatantType(String comType)
	{
		super.setCombatantType(comType);
		sendNetMessage("COMTYPE|" + comType);
	}

    @Override
	public void setDuration(int duration)
	{
		super.setDuration(duration);
		sendNetMessage("DURATION|" + duration);
	}

    @Override
	public void setName(String name)
	{
		this.name = name;
		sendNetMessage("NAME|" + name);
	}

	/**
	 *  Sets the number attribute of the Combatant object
	 *
	 *@param  number  The new number value
	 */
    @Override
	public void setNumber(int number)
	{
		super.setNumber(number);
		sendNetMessage("NUMBER|" + number);
	}

	/**
	 *  Creates a Vector intended for use as a row in a JTable
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
    @Override
	public Vector<Object> getRowVector(List<String> columnOrder)
	{
		Vector<Object> rowVector = new Vector<>();

		//Iterate through all the columns, and create the vector in that order
		columnOrder.forEach(columnName ->
		{
			if (columnName.equals("Name"))
			{ // Character's Name
				rowVector.add(name);
			}
			else if (columnName.equals("Player"))
			{ // Player's Name
				rowVector.add("Net: " + player);
			}
			else if (columnName.equals("Status"))
			{ // Status of Combatant
				rowVector.add(getStatus());
			}
			else if (columnName.equals("+"))
			{ // Initiative bonus
				rowVector.add(String.valueOf(init.getModifier()));
			}
			else if (columnName.equals("Init"))
			{ // Initiative #
				rowVector.add(String.valueOf(init.getCurrentInitiative()));
			}
			else if (columnName.equals("Dur"))
			{ // Duration
				if (duration == 0)
				{
					rowVector.add("");
				}
				else
				{
					rowVector.add(String.valueOf(getDuration()));
				}
			}
			else if (columnName.equals("#"))
			{ // Number (for tokens)
				rowVector.add(String.valueOf(number));
			}
			else if (columnName.equals("HP"))
			{ // Current Hit Points
				int hp = hitPoints.getCurrent();
				int sub = hitPoints.getSubdual();

				if (sub == 0)
				{
					rowVector.add(String.valueOf(hp));
				}
				else if (sub > 0)
				{
					rowVector.add(hp + "/" + sub + "s");
				}
			}
			else if (columnName.equals("HP Max"))
			{ // Max Hit Points
				rowVector.add(String.valueOf(hitPoints.getMax()));
			}
			else if (columnName.equals("Type"))
			{ //PC, Enemy, Ally, Non-Com
				rowVector.add(comType);
			}
		});

		return rowVector;
	}

    @Override
	public void editRow(List<String> columnOrder, int colNumber, Object data)
	{
		String columnName = columnOrder.get(colNumber);
		String strData = String.valueOf(data);

		//Determine which row was edited
		if (columnName.equals("Name"))
		{ // Character's Name
			setName(strData);
		}
		else if (columnName.equals("Player"))
		{ // Player's Name
			setPlayer(strData);
		}
		else if (columnName.equals("Status"))
		{ // XML Combatant's Status
			setStatus((State) data);
		}
		else if (columnName.equals("+"))
		{ // Initiative bonus
			init.setBonus(Integer.parseInt(strData));
		}
		else if (columnName.equals("Init"))
		{ // Initiative
			init.setCurrentInitiative(Integer.parseInt(strData));
		}
		else if (columnName.equals("#"))
		{ // Number (for tokens)
			setNumber(Integer.parseInt(strData));
		}
		else if (columnName.equals("HP"))
		{ // Current Hit Points
			hitPoints.setCurrent(Integer.parseInt(strData));
			sendNetMessage("HP|" + hitPoints.getCurrent());
			sendNetMessage("HPSTATE|" + hitPoints.getState());
			sendNetMessage("STATUS|" + status);
		}
		else if (columnName.equals("HP Max"))
		{ // Maximum Hit Points
			hitPoints.setMax(Integer.parseInt(strData));
			sendNetMessage("HPMAX|" + hitPoints.getMax());
			sendNetMessage("HP|" + hitPoints.getCurrent());
			sendNetMessage("HPSTATE|" + hitPoints.getState());
			sendNetMessage("STATUS|" + status);
		}
		else if (columnName.equals("Dur"))
		{ // Duration
			setDuration(Integer.parseInt(strData));
		}
		else if (columnName.equals("Type"))
		{
			// Type
			setCombatantType(strData);
		}
	}

	/**
	 *  Sets the status of the Combatant
	 *
	 *@param  status  The new status value
	 */
    @Override
	public void setStatus(State status)
	{
		super.setStatus(status);
		sendNetMessage("STATUS|" + status.name());
	}

    @Override
	public void setXP(int xp)
	{
		this.xp = xp;
		sendNetMessage("XP|" + xp);
	}

    @Override
	public int getXP()
	{
		return xp;
	}

	/**  Causes the Combatant to bleed for 1 point of damage */
    @Override
	public void bleed()
	{
		super.bleed();
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

	/**
	 *  Does damage to the Combatant
	 *
	 *@param  damage  number of points of damage to do
	 */
    @Override
	public void damage(int damage)
	{
		super.damage(damage);
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

	/**
	 *  Decrements the duration
	 *
	 *@return    new duration
	 */
    @Override
	public int decDuration()
	{
		super.decDuration();
		sendNetMessage("DURATION|" + duration);
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
		return duration;
	}

    @Override
	public void endRound()
	{
		super.endRound();
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

	/**
	 *  Heals the Combatant
	 *
	 *@param  heal  amount of healing to do
	 */
    @Override
	public void heal(int heal)
	{
		super.heal(heal);
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

    @Override
	public void kill()
	{
		super.kill();
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

    @Override
	public void nonLethalDamage(boolean type)
	{
		super.nonLethalDamage(type);
		sendNetMessage("DURATION|" + duration);
		sendNetMessage("STATUS|" + status);
	}

	/**  Raises a dead Combatant */
    @Override
	public void raise()
	{
		super.raise();
		sendNetMessage("HP|" + hitPoints.getCurrent());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

	/**  Stabilizes the Combatant */
    @Override
	public void stabilize()
	{
		super.stabilize();
		sendNetMessage("STATUS|" + status);
	}

	/**
	 *  Does subdual damage to the Combatant
	 *
	 *@param  damage  number of points of damage to do
	 */
    @Override
	public void subdualDamage(int damage)
	{
		super.subdualDamage(damage);
		sendNetMessage("HPSUB|" + hitPoints.getSubdual());
		sendNetMessage("HPSTATE|" + hitPoints.getState());
		sendNetMessage("STATUS|" + status);
	}

	@Override
	public String getPlayer()
	{
		return player;
	}

	private void setPlayer(String player)
	{
		this.player = player;
		sendNetMessage("PLAYER|" + player);
	}

    @Override
	public Element getSaveElement()
	{
		return new Element("NetworkCombatant");
	}

	private void sendNetMessage(String message)
	{
		NetworkCombatant.LOGGER.trace("sending network message to {}", uid);
		try
		{
			try (PrintStream os
				     = new PrintStream(
				     	new BufferedOutputStream(sock.getOutputStream()), true, "UTF-8"))
			{
				os.print("Pcg: " + uid + ":" + message + "\r\n");
				os.flush();
			}
		}
		catch (final Exception e)
		{
			// TODO Handle this?
		}
	}

	void recieveNetMessage(String message)
	{
		String type = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			type = st.nextToken();
		}
		String value = "";
		if (st.hasMoreTokens())
		{
			value = st.nextToken();
		}

		try
		{
			if ((type != "") && (value != ""))
			{
				if (type.equals("COMTYPE"))
				{
					super.setCombatantType(value);
				}
				else if (type.equals("CR"))
				{
					this.cr = Float.parseFloat(value);
				}
				else if (type.equals("DURATION"))
				{
					super.setDuration(Integer.parseInt(value));
				}
				else if (type.equals("HP"))
				{
					hitPoints.setCurrent(Integer.parseInt(value));
				}
				else if (type.equals("HPMAX"))
				{
					hitPoints.setMax(Integer.parseInt(value));
				}
				else if (type.equals("HPSUB"))
				{
					hitPoints.setSubdual(Integer.parseInt(value));
				}
				else if (type.equals("HPSTATE"))
				{
					hitPoints.setState(State.getState(value));
				}
				else if (type.equals("NAME"))
				{
					this.name = value;
				}
				else if (type.equals("NUMBER"))
				{
					super.setNumber(Integer.parseInt(value));
				}
				else if (type.equals("PLAYER"))
				{
					this.player = value;
				}
				else if (type.equals("STATUS"))
				{
					super.setStatus(State.getState(value));
				}
				else if (type.equals("XP"))
				{
					this.xp = Integer.parseInt(value);
				}
				else if (type.startsWith("HTMLSTRING"))
				{
					this.htmlString = value;
				}
				else if (type.startsWith("INIT"))
				{
					((NetworkInitiative) init).recieveNetMessage(message);
				}
			}
		}
		catch (final Exception e)
		{
			// TODO Handle this?
		}
	}

	static void recieveServerMessage(String message, Combatant cbt)
	{
		String type = "";
		StringTokenizer st = new StringTokenizer(message, "|");
		if (st.hasMoreTokens())
		{
			type = st.nextToken();
		}
		String value = "";
		if (st.hasMoreTokens())
		{
			value = st.nextToken();
		}

		try
		{
			if ((type != "") && (value != ""))
			{
				if (type.equals("COMTYPE"))
				{
					cbt.setCombatantType(value);
				}
				else if (type.equals("CR"))
				{
					cbt.setCR(Float.parseFloat(value));
				}
				else if (type.equals("DURATION"))
				{
					cbt.setDuration(Integer.parseInt(value));
				}
				else if (type.equals("HPMAX"))
				{
					cbt.getHP().setMax(Integer.parseInt(value));
				}
				else if (type.equals("HP"))
				{
					cbt.getHP().setCurrent(Integer.parseInt(value));
				}
				else if (type.equals("HPSUB"))
				{
					cbt.getHP().setSubdual(Integer.parseInt(value));
				}
				else if (type.equals("HPSTATE"))
				{
					cbt.getHP().setState(State.getState(value));
				}
				else if (type.equals("NAME"))
				{
					cbt.setName(value);
				}
				else if (type.equals("NUMBER"))
				{
					cbt.setNumber(Integer.parseInt(value));
				}
				else if (type.equals("STATUS"))
				{
					cbt.setStatus(State.getState(value));
				}
				else if (type.equals("XP"))
				{
					cbt.setXP(Integer.parseInt(value));
				}
				else if (type.startsWith("INITBONUS"))
				{
					cbt.init.setBonus(Integer.parseInt(value));
				}
				else if (type.startsWith("INIT"))
				{
					cbt.init.setCurrentInitiative(Integer.parseInt(value));
				}
			}
		}
		catch (final Exception e)
		{
			// TODO Handle This?
		}
	}

	static String getCombatantUid(Combatant cbt, String user)
	{
		return cbt.getName() + "-" + cbt.getPlayer() + "-" + user;
	}

	static void sendCombatant(Combatant cbt, NetworkClient client)
	{
		String uid = getCombatantUid(cbt, client.getUser());
		client.sendPcgMessage(uid, "COMTYPE|" + cbt.getCombatantType());
		client.sendPcgMessage(uid, "CR|" + cbt.getCR());
		client.sendPcgMessage(uid, "DURATION|" + cbt.getDuration());
		client.sendPcgMessage(uid, "HPMAX|" + cbt.getHP().getMax());
		client.sendPcgMessage(uid, "HP|" + cbt.getHP().getCurrent());
		client.sendPcgMessage(uid, "HPSUB|" + cbt.getHP().getSubdual());
		client.sendPcgMessage(uid, "HPSTATE|" + cbt.getHP().getState());
		client.sendPcgMessage(uid, "NAME|" + cbt.getName());
		client.sendPcgMessage(uid, "NUMBER|" + cbt.getNumber());
		client.sendPcgMessage(uid, "PLAYER|" + cbt.getPlayer());
		client.sendPcgMessage(uid, "STATUS|" + cbt.getStatus());
		client.sendPcgMessage(uid, "XP|" + cbt.getXP());
		client.sendPcgMessage(uid, "INITBONUS|" + cbt.init.getModifier());
		client.sendPcgMessage(uid, "INIT|" + cbt.init.getCurrentInitiative());
		client.sendPcgMessage(uid, "HTMLSTRING|" + cbt.toHtmlString());
	}

    @Override
	public String toHtmlString()
	{
		return htmlString;
	}

	@Override
	public String toString()
	{
		return "NetworkCombatant{"
			       + "name='"
			       + name
			       + '\''
			       + ", uid='"
			       + uid
			       + '\''
			       + ", player='"
			       + player
			       + '\''
			       + ", htmlString='"
			       + htmlString
			       + '\''
			       + ", cr="
			       + cr
			       + ", xp="
			       + xp
			       + ", sock="
			       + sock
			       + '}';
	}
}
