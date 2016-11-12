/**
 * ChronicleEntryFacade.java
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
 * Created on 10/10/2011 1:28:12 PM
 *
 * $Id$
 */
package pcgen.facade.core;

/**
 * The Class {@code ChronicleEntryFacade} is ...
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public interface ChronicleEntryFacade
{

	/**
	 * @return the outputEntry
	 */
	public boolean isOutputEntry();

	/**
	 * @param outputEntry the outputEntry to set
	 */
	public void setOutputEntry(boolean outputEntry);

	/**
	 * @return the campaign
	 */
	public String getCampaign();

	/**
	 * @param campaign the campaign to set
	 */
	public void setCampaign(String campaign);

	/**
	 * @return the adventure
	 */
	public String getAdventure();

	/**
	 * @param adventure the adventure to set
	 */
	public void setAdventure(String adventure);

	/**
	 * @return the party
	 */
	public String getParty();

	/**
	 * @param party the party to set
	 */
	public void setParty(String party);

	/**
	 * @return the date
	 */
	public String getDate();

	/**
	 * @param date the date to set
	 */
	public void setDate(String date);

	/**
	 * @return the xpField
	 */
	public int getXpField();

	/**
	 * @param xpField the xpField to set
	 */
	public void setXpField(int xpField);

	/**
	 * @return the gmField
	 */
	public String getGmField();

	/**
	 * @param gmField the gmField to set
	 */
	public void setGmField(String gmField);

	/**
	 * @return the chronicle
	 */
	public String getChronicle();

	/**
	 * @param chronicle the chronicle to set
	 */
	public void setChronicle(String chronicle);

}
