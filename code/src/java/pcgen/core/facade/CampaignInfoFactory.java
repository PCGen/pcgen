/*
 * CampaignInfoFactory.java
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
 * Created on 08/03/2011 7:18:51 PM
 *
 * $Id$
 */
package pcgen.core.facade;

/**
 * The Class <code>CampaignInfoFactory</code> is ...
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface CampaignInfoFactory
{
	/**
	 * Produce a html formatted information string for the supplied campaign.
	 * @param campaign The campaign to be described
	 * @return The information on the campaign.
	 */
	public String getHTMLInfo(CampaignFacade campaign);

}
