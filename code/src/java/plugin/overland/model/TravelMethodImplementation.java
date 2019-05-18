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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import plugin.overland.util.Localized;

/**
 * Stores travel methods and provides model for use in a GUI. Implementation. Visible only in same package.
 */
class TravelMethodImplementation implements TravelMethod
{
	// ### Constants ###

	private static final String UNKNOWN_WAY_0_PLEASE_FIX_1_XML = "in_plugin_overland_error_noWay"; //$NON-NLS-1$

	// ### Fields ###

	/** Name of this method */
	private final Localized name;
	private final Map<String, Map<String, Combo>> multByRoadByTerrains;
	private final List<Method> methods;
	private final Map<String, Map<Localized, String>> terrainsId;
	private final Map<String, Map<Localized, String>> routesId;

	private final MethodModel methodModel = new MethodModel();
	private final ListByWayModel routesModel;
	private final ListByWayModel terrainsModel;
	private final PaceModel paceModel = new PaceModel();
	private final ChoiceModel choiceModel = new ChoiceModel();

	private Method selectedMethod;

	// ### Constructors ###

	public TravelMethodImplementation(Localized name, Map<String, Map<String, Combo>> multByRoadByTerrains,
		Map<String, List<Localized>> terrains, Map<String, Map<Localized, String>> terrainsById,
		Map<String, List<Localized>> routes, Map<String, Map<Localized, String>> routesById, List<Method> methods)
	{
		this.name = name;
		this.multByRoadByTerrains = multByRoadByTerrains;
		this.terrainsId = terrainsById;
		this.routesId = routesById;
		this.methods = methods;
		this.routesModel = new ListByWayModel(routes);
		this.terrainsModel = new ListByWayModel(terrains);
	}

	@Override
	public ComboBoxModel getRoutesModel()
	{
		return routesModel;
	}

	@Override
	public ComboBoxModel getTerrainsModel()
	{
		return terrainsModel;
	}

	@Override
	public ComboBoxModel getMethodsModel()
	{
		return methodModel;
	}

	@Override
	public ComboBoxModel getPaceModel()
	{
		return paceModel;
	}

	@Override
	public ComboBoxModel getChoiceModel()
	{
		return choiceModel;
	}

	@Override
	public String toString()
	{
		return name.toString();
	}

	/**
	 * Returns the mult associated with current selection. Returns null if no mult value is available,
	 * no terrains/routes selected or no mult with the terrains/routes selection.
	 * @return usually a double
	 */
	private Number getMult()
	{
		Combo c = getSelectedCombo();
		if (c == null)
		{
			return null;
		}
		return c.getMult();
	}

	private Combo getSelectedCombo()
	{
		if (terrainsModel.getSelectedItem() == null || routesModel.getSelectedItem() == null)
		{
			return null;
		}
		String way = selectedMethod.getWay();
		Map<Localized, String> map = terrainsId.get(way);
		if (map == null)
		{
			Logging.errorPrintLocalised(UNKNOWN_WAY_0_PLEASE_FIX_1_XML, way, name);
			return null;
		}
		String tId = map.get(terrainsModel.getSelectedItem());
		if (!multByRoadByTerrains.containsKey(tId))
		{
			return null;
		}
		String rId = routesId.get(selectedMethod.getWay()).get(routesModel.getSelectedItem());
		if (!multByRoadByTerrains.get(tId).containsKey(rId))
		{
			return null;
		}
		return multByRoadByTerrains.get(tId).get(rId);
	}

	private String getMultString()
	{
		Combo c = getSelectedCombo();
		Number n2 = c.getMult();
		if (n2 == null)
		{
			return null;
		}
		StringBuilder n = new StringBuilder();
		n.append(LanguageBundle.getPrettyMultiplier(n2.doubleValue()));
		if (c.getAddMph().doubleValue() != 0)
		{
			n.append('\n').append(MessageFormat.format(LanguageBundle.getString("in_plusMph"),
				c.getAddMph())); //$NON-NLS-1$ 
		}
		if (c.getAddMph().doubleValue() != 0)
		{
			n.append('\n').append(MessageFormat.format(LanguageBundle.getString("in_plusKmh"),
				c.getAddKmh())); //$NON-NLS-1$ 
		}
		return n.toString();
	}

	@Override
	public String getUnmodifiedImperialSpeedString()
	{
		return formatImperialSpeed(getUnmodifiedImperialSpeed());

	}

	@Override
	public String getImperialSpeedString()
	{
		return formatImperialSpeed(getImperialSpeed());
	}

	private String formatImperialSpeed(Number imperialSpeed)
	{
		if (imperialSpeed != null)
		{
			Pace selectedPace = paceModel.getSelected();
			if (selectedPace != null)
			{
				String unit = selectedPace.isUseDays() ? LanguageBundle.getString("in_mpd") //$NON-NLS-1$
					: LanguageBundle.getString("in_mph"); //$NON-NLS-1$
				return MessageFormat.format(unit, imperialSpeed);
			}
			return null;
		}
		return null;
	}

	@Override
	public String getUnmodifiedMetricSpeedString()
	{
		return formatMetricSpeed(getUnmodifiedMetricSpeed());
	}

	@Override
	public String getMetricSpeedString()
	{
		return formatMetricSpeed(getMetricSpeed());
	}

	private String formatMetricSpeed(Number metricSpeed)
	{
		if (metricSpeed != null)
		{
			Pace selectedPace = paceModel.getSelected();
			if (selectedPace != null)
			{
				String unit = selectedPace.isUseDays() ? LanguageBundle.getString("in_kmd") //$NON-NLS-1$
					: LanguageBundle.getString("in_kmh"); //$NON-NLS-1$
				return MessageFormat.format(unit, metricSpeed);
			}
		}
		return null;
	}

	private Double getUnmodifiedMetricSpeed()
	{
		Pace selectedPace = paceModel.getSelected();
		Choice selectedChoice = choiceModel.getSelected();
		if (selectedPace != null && selectedChoice != null)
		{
			double speed = selectedPace.getMult().doubleValue() * selectedChoice.getKmh().doubleValue();
			if (selectedPace.isUseDays())
			{
				speed *= selectedChoice.getHoursInDay().doubleValue();
			}
			return speed;
		}
		return null;
	}

	private Double getUnmodifiedImperialSpeed()
	{
		Pace selectedPace = paceModel.getSelected();
		Choice selectedChoice = choiceModel.getSelected();
		if (selectedPace != null && selectedChoice != null)
		{
			double speed = selectedPace.getMult().doubleValue() * selectedChoice.getMph().doubleValue();
			if (selectedPace.isUseDays())
			{
				speed *= selectedChoice.getHoursInDay().doubleValue();
			}
			return speed;
		}
		return null;
	}

	private Double getMetricSpeed()
	{
		Double d = getUnmodifiedMetricSpeed();
		Combo c = getSelectedCombo();
		if (d != null && c != null)
		{
			return d.doubleValue() * c.getMult().doubleValue() + c.getAddKmh().doubleValue() * getHoursInDays();
		}
		return null;
	}

	private Double getImperialSpeed()
	{
		Double d = getUnmodifiedImperialSpeed();
		Combo c = getSelectedCombo();
		if (d != null && c != null)
		{
			return d.doubleValue() * c.getMult().doubleValue() + c.getAddMph().doubleValue() * getHoursInDays();
		}
		return null;
	}

	// return 1.0 not null
	private double getHoursInDays()
	{
		Pace selectedPace = paceModel.getSelected();
		Choice selectedChoice = choiceModel.getSelected();
		if (selectedChoice != null && selectedPace != null)
		{
			if (selectedPace.isUseDays())
			{
				return selectedChoice.getHoursInDay().doubleValue();
			}
		}
		return 1.0;
	}

	private String selectedUseDays()
	{
		Pace selectedPace = paceModel.getSelected();

		if (selectedPace == null)
		{
			return LanguageBundle.getString("in_unitUnknown"); //$NON-NLS-1$
		}
		else if (selectedPace.isUseDays())
		{
			return LanguageBundle.getString("in_unitDays"); //$NON-NLS-1$
		}
		else
		{
			return LanguageBundle.getString("in_unitHours"); //$NON-NLS-1$
		}
	}

	private String getSelectedComment()
	{
		Pace selectedPace = paceModel.getSelected();
		if (selectedPace == null)
		{
			return ""; //$NON-NLS-1$
		}
		return selectedPace.comment.toString();
	}

	// ### Conversion methods ###
	// (based on selected elements that combine to create a speed)

	@Override
	public Number convertToMiles(double time)
	{
		Double d = getImperialSpeed();
		if (d != null)
		{
			return d.doubleValue() * time;
		}
		return null;
	}

	@Override
	public Number convertToKm(double time)
	{
		Double d = getMetricSpeed();
		if (d != null)
		{
			return d.doubleValue() * time;
		}
		return null;
	}

	@Override
	public Number convertToTimeFromImperial(double distance)
	{
		Double d = getImperialSpeed();
		if (d != null)
		{
			return distance / d.doubleValue();
		}
		return null;
	}

	@Override
	public Number convertToTimeFromMetric(double distance)
	{
		Double d = getMetricSpeed();
		if (d != null)
		{
			return distance / d.doubleValue();
		}
		return null;
	}

	// ### Event related methods ###

	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public void addTravelMethodListener(TravelMethodListener l)
	{
		listenerList.add(TravelMethodListener.class, l);
	}

	@Override
	public void removeTravelMethodListener(TravelMethodListener l)
	{
		listenerList.remove(TravelMethodListener.class, l);
	}

	public TravelMethodListener[] getMultListeners()
	{
		return listenerList.getListeners(TravelMethodListener.class);
	}

	protected void fireMultChanged(Object source)
	{
		if (terrainsModel.getSelectedItem() == null || routesModel.getSelectedItem() == null)
		{
			return;
		}
		String n2 = getMultString();
		if (n2 == null)
		{
			return;
		}

		Object[] listeners = listenerList.getListenerList();
		TravelSpeedEvent e = null;
		boolean hasUnmod = getUnmodifiedMetricSpeedString() != null;

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TravelMethodListener.class)
			{
				if (e == null)
				{
					e = new TravelSpeedEvent(source, n2);
				}
				((TravelMethodListener) listeners[i + 1]).multUpdated(e);
				// if there is an unmod speed, the speed has change
				if (hasUnmod)
				{
					((TravelMethodListener) listeners[i + 1]).speedUpdated(e);
				}
			}
		}
	}

	protected void fireUnmodifiableSpeedChanged(Object source)
	{
		if (paceModel.getSelectedItem() == null || choiceModel.getSelectedItem() == null)
		{
			return;
		}
		Object[] listeners = listenerList.getListenerList();
		EventObject e = null;
		boolean hasMult = getMult() != null;

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TravelMethodListener.class)
			{
				if (e == null)
				{
					e = new EventObject(source);
				}
				((TravelMethodListener) listeners[i + 1]).unmodifiedSpeedUpdated(e);
				// the modified speed also has changed if the mult has a value
				if (hasMult)
				{
					((TravelMethodListener) listeners[i + 1]).speedUpdated(e);
				}
			}
		}
	}

	protected void fireCommentDaysChanged(Object source)
	{
		Object[] listeners = listenerList.getListenerList();
		TravelSpeedEvent e = null;
		TravelSpeedEvent e2 = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TravelMethodListener.class)
			{
				if (e == null || e2 == null)
				{
					e = new TravelSpeedEvent(source, getSelectedComment());
					e2 = new TravelSpeedEvent(source, selectedUseDays());
				}
				((TravelMethodListener) listeners[i + 1]).commentChanged(e);
				((TravelMethodListener) listeners[i + 1]).useDaysChanged(e2);
			}
		}
	}

	protected void fireAllChanged(Object source)
	{
		Object[] listeners = listenerList.getListenerList();
		TravelSpeedEvent eComment = null;
		TravelSpeedEvent eDays = null;
		TravelSpeedEvent eMult = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TravelMethodListener.class)
			{
				if (eComment == null || eDays == null || eMult == null)
				{
					eComment = new TravelSpeedEvent(source, getSelectedComment());
					eDays = new TravelSpeedEvent(source, selectedUseDays());
					eMult = new TravelSpeedEvent(source, getMultString());
				}
				((TravelMethodListener) listeners[i + 1]).commentChanged(eComment);
				((TravelMethodListener) listeners[i + 1]).useDaysChanged(eDays);
				((TravelMethodListener) listeners[i + 1]).unmodifiedSpeedUpdated(eComment);
				((TravelMethodListener) listeners[i + 1]).speedUpdated(eComment);
				((TravelMethodListener) listeners[i + 1]).multUpdated(eMult);
			}
		}
	}

	// ### Inner classes ###

	static class Method extends Named
	{
		private final List<Pace> paces;
		private final List<Choice> choices;
		private final String way;

		public Method(Localized name, String way)
		{
			super(name);
			this.way = way;
			paces = new ArrayList<>();
			choices = new ArrayList<>();
		}

		/**
		 * @param c
		 */
		public void add(Choice c)
		{
			choices.add(c);
		}

		/**
		 * @param newPace
		 */
		public void add(Pace newPace)
		{
			paces.add(newPace);
		}

		/**
		 * @return the way
		 */
		public String getWay()
		{
			return way;
		}
	}

	public static class Pace extends Named
	{
		/**
		 * @param name2
		 * @param comment2
		 * @param useDays2
		 * @param mult2
		 */
		public Pace(Localized name2, Localized comment2, boolean useDays2, Number mult2)
		{
			super(name2);
			comment = comment2;
			useDays = useDays2;
			mult = mult2;
		}

		private boolean useDays = false;
		private Number mult = 1;
		private final Localized comment;

		/**
		 * @return the useDays
		 */
		public boolean isUseDays()
		{
			return useDays;
		}

		/**
		 * @return the mult
		 */
		public Number getMult()
		{
			return mult;
		}

	}

	static class Choice extends Named
	{
		private final Number hoursInDay;
		private final Number kmh;
		private final Number mph;

		/**
		 * @param name
		 * @param mph 
		 * @param kmh 
		 * @param hoursInDay 
		 */
		public Choice(Localized name, Number hoursInDay, double kmh, double mph)
		{
			super(name);
			this.hoursInDay = hoursInDay;
			this.kmh = kmh;
			this.mph = mph;
		}

		/**
		 * @return the hoursInDay
		 */
		public Number getHoursInDay()
		{
			return hoursInDay;
		}

		/**
		 * @return the kmh
		 */
		public Number getKmh()
		{
			return kmh;
		}

		/**
		 * @return the mph
		 */
		public Number getMph()
		{
			return mph;
		}

	}

	/**
	 * Basic super class with a localized name.
	 */
	static class Named
	{
		private final Localized name;

		public Named(Localized name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name.toString();
		}
	}

	static class Combo
	{
		private final Number mult;
		private final Number addMph;
		private final Number addKmh;

		public Combo(Number mult, Number addMph, Number addKmh)
		{
			this.mult = mult;
			this.addMph = addMph;
			this.addKmh = addKmh;
		}

		/**
		 * @return the mult
		 */
		public Number getMult()
		{
			return mult;
		}

		/**
		 * @return the addMph
		 */
		public Number getAddMph()
		{
			return addMph;
		}

		/**
		 * @return the addKmh
		 */
		public Number getAddKmh()
		{
			return addKmh;
		}
	}

	class MethodModel extends AbstractListModel implements ComboBoxModel
	{
		private static final long serialVersionUID = 2804199879316856684L;

		@Override
		public int getSize()
		{
			return methods.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return methods.get(index);
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			Method previousMethod = selectedMethod;
			int indexOf = methods.indexOf(anItem);
			if (indexOf >= 0)
			{
				selectedMethod = methods.get(indexOf);
				// do as DefaultComboModel
				fireContentsChanged(this, -1, -1);
				paceModel.fireMethodChanged(this, previousMethod);
				choiceModel.fireMethodChanged(this, previousMethod);
				terrainsModel.fireMethodChanged(this, previousMethod);
				routesModel.fireMethodChanged(this, previousMethod);
				fireAllChanged(this);
			}
		}

		@Override
		public Object getSelectedItem()
		{
			return selectedMethod;
		}

	}

	/**
	 * Used for terrain and routes.
	 */
	class ListByWayModel extends AbstractListModel implements ComboBoxModel
	{
		private static final long serialVersionUID = -5596276376727073581L;

		private final Map<String, List<Localized>> listByWay;
		private Localized selected;

		public ListByWayModel(Map<String, List<Localized>> list)
		{
			this.listByWay = list;
		}

		private int getSize(Method m)
		{
			if (m == null || !listByWay.containsKey(m.getWay()))
			{
				return 0;
			}
			return listByWay.get(m.getWay()).size();
		}

		@Override
		public int getSize()
		{
			return getSize(selectedMethod);
		}

		@Override
		public Object getElementAt(int index)
		{
			if (selectedMethod == null || !listByWay.containsKey(selectedMethod.getWay()))
			{
				return null;
			}
			return listByWay.get(selectedMethod.getWay()).get(index);
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			int indexOf = listByWay.get(selectedMethod.getWay()).indexOf(anItem);
			if (indexOf >= 0)
			{
				selected = listByWay.get(selectedMethod.getWay()).get(indexOf);
				fireMultChanged(this);
				fireContentsChanged(this, -1, -1);
			}
		}

		@Override
		public Object getSelectedItem()
		{
			return selected;
		}

		// does not fire mult changed
		private void fireMethodChanged(MethodModel source, Method previousMethod)
		{
			int start = 0;
			int end = getSize();
			if (previousMethod != null)
			{
				String previousWay = previousMethod.getWay();
				String selectedWay = selectedMethod.getWay();
				if (previousWay.equals(selectedWay))
				{
					return;
				}
				// handle selection. keep same index if not too big, else selection becomes 0
				List<Localized> previousList = listByWay.get(previousWay);
				int previousIndex = previousList.indexOf(selected);
				List<Localized> selectedList = listByWay.get(selectedWay);
				if (selectedList == null)
				{
					Logging.errorPrintLocalised(UNKNOWN_WAY_0_PLEASE_FIX_1_XML, selectedWay, name);
					// XXX do something else?
					return;
				}
				if (selectedMethod != null)
				{
					if (previousIndex < end && previousIndex >= 0)
					{
						selected = selectedList.get(previousIndex);
					}
					else
					{
						selected = selectedList.get(0);
					}
				}
				// handle firing change event
				int previousSize = getSize(previousMethod);
				if (end > previousSize)
				{
					fireIntervalAdded(source, previousSize, end - 1);
				}
				if (end < previousSize)
				{
					fireIntervalRemoved(source, end, previousSize - 1);
				}
				end = Math.min(end, previousSize);
				// increment start for each identical element at the start of the paces lists
				for (int i = 0; i < end && previousList.get(i).equals(selectedList.get(i)); i++, start++)
				{
					;
				}
				// decrement end for each identical element at the end of the paces lists
				for (int i = end - 1; i > start && previousList.get(i).equals(selectedList.get(i)); i--, end--)
				{
					;
				}
				if (start != end)
				{
					fireContentsChanged(source, start, end - 1);
				}
				else if (previousIndex >= getSize())
				{
					// indicates that the selection has changed as in #setSelectedItem
					fireContentsChanged(source, -1, -1);
				}
			}
			else
			{
				fireIntervalAdded(source, 0, end - 1);
			}
		}

	}

	/**
	 * Used for Pace and Choice.
	 *
	 * @param <T> class
	 * @see PaceModel
	 * @see ChoiceModel
	 */
	@SuppressWarnings("serial")
	protected abstract class TModel<T> extends AbstractListModel implements ComboBoxModel
	{

		private T selected;

		@Override
		public int getSize()
		{
			if (selectedMethod == null)
			{
				return 0;
			}
			return getList(selectedMethod).size();
		}

		abstract List<T> getList(Method m);

		@Override
		public Object getElementAt(int index)
		{
			if (selectedMethod == null || index < 0 || getList(selectedMethod).size() <= index)
			{
				return null;
			}
			return getList(selectedMethod).get(index);
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			int indexOf = getList(selectedMethod).indexOf(anItem);
			if (indexOf >= 0)
			{
				selected = getList(selectedMethod).get(indexOf);
				fireUnmodifiableSpeedChanged(this);
				fireContentsChanged(this, -1, -1);
			}
		}

		@Override
		public Object getSelectedItem()
		{
			return selected;
		}

		/**
		 * Method called when the selected method changes.
		 * @param source
		 * @param previousMethod
		 */
		protected void fireMethodChanged(MethodModel source, Method previousMethod)
		{
			int start = 0;
			int end = getSize();
			if (previousMethod != null)
			{
				// handle selection. keep same index if not too big, else selection becomes 0
				List<T> previousList = getList(previousMethod);
				List<T> selectedList = getList(selectedMethod);
				int previousIndex = previousList.indexOf(selected);
				if (selectedMethod != null)
				{
					if (previousIndex < end && previousIndex >= 0)
					{
						selected = selectedList.get(previousIndex);
					}
					else
					{
						selected = selectedList.get(0);
					}
				}
				// handle firing change event
				int previousSize = previousList.size();
				if (end > previousSize)
				{
					fireIntervalAdded(source, previousSize, end);
				}
				if (end < previousSize)
				{
					fireIntervalRemoved(source, end, previousSize);
				}
				end = Math.min(end, previousSize);
				// increment start for each identical element at the start of the paces lists
				for (int i = 0; i < end && previousList.get(i).equals(selectedList.get(i)); i++, start++)
				{
					;
				}
				// decrement end for each identical element at the end of the paces lists
				for (int i = end - 1; i > start && previousList.get(i).equals(selectedList.get(i)); i--, end--)
				{
					;
				}
				if (start != end)
				{
					fireContentsChanged(source, start, end - 1);
				}
				else if (previousIndex >= getSize())
				{
					// indicates that the selection has changed as in #setSelectedItem
					fireContentsChanged(source, -1, -1);
				}
			}
			else
			{
				fireIntervalAdded(source, 0, end);
			}
		}

		T getSelected()
		{
			return selected;
		}
	}

	class PaceModel extends TModel<Pace>
	{
		private static final long serialVersionUID = 8980884569594225313L;

		@Override
		List<Pace> getList(Method m)
		{
			return m.paces;
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			super.setSelectedItem(anItem);
			fireCommentDaysChanged(this);
		}
	}

	class ChoiceModel extends TModel<Choice>
	{
		private static final long serialVersionUID = 3502580215371087556L;

		@Override
		List<Choice> getList(Method m)
		{
			return m.choices;
		}
	}
}
