/*
 * Copyright 2012 Vincent Lhote
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
 */
package plugin.overland.model;

import java.util.EventListener;
import java.util.EventObject;

/**
 * To be implemented by listener of {@link TravelMethod} model change
 */
public interface TravelMethodListener extends EventListener
{

	void multUpdated(TravelSpeedEvent e);

	void unmodifiedSpeedUpdated(EventObject e);

	void speedUpdated(EventObject e);

	void useDaysChanged(TravelSpeedEvent e);

	/**
	 * 
	 * @param e the changed property is the new comment
	 */
	void commentChanged(TravelSpeedEvent e);

}
