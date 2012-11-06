/**
 * ChronicleEntry.java
 * Copyright James Dempsey, 2011
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
 * Created on 30/09/2011 10:17:51 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.core.facade.ChronicleEntryFacade;

/**
 * The Class <code>ChronicleEntry</code> contains a record of an 
 * event in the character's history. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class ChronicleEntry implements ChronicleEntryFacade, Cloneable
{
	private boolean outputEntry = true;
	private String campaign = "";
	private String adventure = "";
	private String party = "";
	private String date = "";
	private int xpField = 0;
	private String gmField = "";
	private String chronicle = "";
	
	/**
	 * @return the outputEntry
	 */
    @Override
	public boolean isOutputEntry()
	{
		return outputEntry;
	}
	/**
	 * @param outputEntry the outputEntry to set
	 */
    @Override
	public void setOutputEntry(boolean outputEntry)
	{
		this.outputEntry = outputEntry;
	}
	/**
	 * @return the campaign
	 */
    @Override
	public String getCampaign()
	{
		return campaign;
	}
	/**
	 * @param campaign the campaign to set
	 */
    @Override
	public void setCampaign(String campaign)
	{
		this.campaign = campaign;
	}
	/**
	 * @return the adventure
	 */
    @Override
	public String getAdventure()
	{
		return adventure;
	}
	/**
	 * @param adventure the adventure to set
	 */
    @Override
	public void setAdventure(String adventure)
	{
		this.adventure = adventure;
	}
	/**
	 * @return the party
	 */
    @Override
	public String getParty()
	{
		return party;
	}
	/**
	 * @param party the party to set
	 */
    @Override
	public void setParty(String party)
	{
		this.party = party;
	}
	/**
	 * @return the date
	 */
    @Override
	public String getDate()
	{
		return date;
	}
	/**
	 * @param date the date to set
	 */
    @Override
	public void setDate(String date)
	{
		this.date = date;
	}
	/**
	 * @return the xpField
	 */
    @Override
	public int getXpField()
	{
		return xpField;
	}
	/**
	 * @param xpField the xpField to set
	 */
    @Override
	public void setXpField(int xpField)
	{
		this.xpField = xpField;
	}
	/**
	 * @return the gmField
	 */
    @Override
	public String getGmField()
	{
		return gmField;
	}
	/**
	 * @param gmField the gmField to set
	 */
    @Override
	public void setGmField(String gmField)
	{
		this.gmField = gmField;
	}
	/**
	 * @return the chronicle
	 */
    @Override
	public String getChronicle()
	{
		return chronicle;
	}
	/**
	 * @param chronicle the chronicle to set
	 */
    @Override
	public void setChronicle(String chronicle)
	{
		this.chronicle = chronicle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChronicleEntry clone() throws CloneNotSupportedException
	{
		return (ChronicleEntry) super.clone();
	}
	

}
