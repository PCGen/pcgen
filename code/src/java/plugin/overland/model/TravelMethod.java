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

import javax.swing.ComboBoxModel;

/**
 * Stores travel methods and provides model for use in a GUI.
 */
public interface TravelMethod
{

	public abstract ComboBoxModel getRoutesModel();

	public abstract ComboBoxModel getTerrainsModel();

	public abstract ComboBoxModel getMethodsModel();

	public abstract ComboBoxModel getPaceModel();

	public abstract ComboBoxModel getChoiceModel();

	public abstract void addTravelMethodListener(TravelMethodListener l);

	public abstract void removeTravelMethodListener(TravelMethodListener l);

	public abstract String getUnmodifiedImperialSpeedString();

	public abstract String getUnmodifiedMetricSpeedString();

	public abstract String getImperialSpeedString();

	public abstract String getMetricSpeedString();

	/**
	 * Returns the number of miles done in the specified time,
	 * according to the selected pace, etc. selected by this model.
	 * 
	 * @param time time in days or hours (unit is based on model's selected items)
	 * @return null if lacking selection, a double? in other cases
	 * @see TravelMethodListener#useDaysChanged(TravelSpeedEvent)
	 */
	Number convertToMiles(double time);

	/**
	 * Returns the number of kilometers done in the specified time,
	 * according to the selected pace, etc. selected by this model.
	 * 
	 * @param time time in days or hours (unit is based on model's selected items)
	 * @return null if lacking selection, a double? in other cases
	 * @see TravelMethodListener#useDaysChanged(TravelSpeedEvent)
	 */
	Number convertToKm(double time);

	Number convertToTimeFromImperial(double distance);

	Number convertToTimeFromMetric(double distance);
}
