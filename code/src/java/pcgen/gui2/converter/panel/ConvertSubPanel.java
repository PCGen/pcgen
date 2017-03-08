/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.converter.panel;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.converter.event.ProgressListener;
import pcgen.gui2.converter.event.ProgressMonitor;

public abstract class ConvertSubPanel implements ProgressMonitor
{
	private final ProgressSupport support;

	public boolean isLast()
	{
		return false;
	}

	public ConvertSubPanel()
	{
		super();
		support = new ProgressSupport(this);
	}

	/**
	 * Can the user return to this panel by means of the previous button?
	 * By default this will be false. Panels which can support previous 
	 * should override this to return true.
	 * 
	 * @return true if the user can navigate back to this panel safely.
	 */
	public boolean returnAllowed()
	{
		return false;
	}

	public void addProgressListener(ProgressListener listener)
	{
		support.addProgressListener(listener);
	}

	public ProgressListener[] getProgressListeners()
	{
		return support.getProgressListeners();
	}

	public void removeProgressListener(ProgressListener listener)
	{
		support.removeProgressListener(listener);
	}

	@Override
	public void fireProgressEvent(int id)
	{
		support.fireProgressEvent(id);
	}

	public abstract boolean performAnalysis(CDOMObject pc);

	public abstract boolean autoAdvance(CDOMObject pc);

	public abstract void setupDisplay(JPanel panel, CDOMObject pc);

	public class ProgressSupport implements ProgressMonitor
	{

		private final EventListenerList listenerList;

		private final Object source;

		public ProgressSupport(Object sourceObject)
		{
			super();
			if (sourceObject == null)
			{
				throw new IllegalArgumentException(
						"Source for ProgressSupport cannot be null");
			}
			source = sourceObject;
			listenerList = new EventListenerList();
		}

		public void addProgressListener(ProgressListener listener)
		{
			listenerList.add(ProgressListener.class, listener);
		}

		public synchronized ProgressListener[] getProgressListeners()
		{
			return listenerList.getListeners(ProgressListener.class);
		}

		public void removeProgressListener(ProgressListener listener)
		{
			listenerList.remove(ProgressListener.class, listener);
		}

		@Override
		public void fireProgressEvent(int id)
		{
			Object[] listeners = listenerList.getListenerList();
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			ProgressEvent ccEvent = null;
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == ProgressListener.class)
				{
					// Lazily create event
					if (ccEvent == null)
					{
						ccEvent = new ProgressEvent(source, id);
					}
					switch (ccEvent.getID())
					{
					case ProgressEvent.ALLOWED:
					case ProgressEvent.AUTO_ADVANCE:
						((ProgressListener) listeners[i + 1])
								.progressAllowed(ccEvent);
						break;
					case ProgressEvent.NOT_ALLOWED:
						((ProgressListener) listeners[i + 1])
								.progressNotAllowed(ccEvent);
						break;
					default:
						break;
					}
				}
			}
		}
	}

}
