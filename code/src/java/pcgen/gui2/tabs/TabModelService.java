/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.tabs;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.SwingUtilities;

import pcgen.gui2.tabs.CharacterInfoTab.ModelMap;

/**
 * Restores character tabs one event at a time, with faster tabs first.
 * All state belongs to the EDT. Canceling a generation also invalidates callbacks
 * already queued on the EDT, so detached character models cannot be restored later.
 */
final class TabModelService implements Comparator<CharacterInfoTab>
{
	private final Map<CharacterInfoTab, Long> timingMap = new HashMap<>();
	private final Queue<CharacterInfoTab> storeQueue = new ArrayDeque<>();
	private long generation;

	@Override
	public int compare(CharacterInfoTab first, CharacterInfoTab second)
	{
		// New tabs get their initial timing before previously measured tabs.
		return Long.compare(timingMap.getOrDefault(first, -1L), timingMap.getOrDefault(second, -1L));
	}

	/** Restores the selected tab immediately and records that it needs detaching. */
	void restoreTab(CharacterInfoTab tab, ModelMap models)
	{
		long start = System.nanoTime();
		tab.restoreModels(models);
		timingMap.put(tab, System.nanoTime() - start);
		storeQueue.add(tab);
	}

	/** Schedules the remaining tabs after the selected tab has been restored. */
	void restoreModels(Map<CharacterInfoTab, ModelMap> states, CharacterInfoTab firstTab)
	{
		Queue<CharacterInfoTab> remaining = new PriorityQueue<>(this);
		remaining.addAll(states.keySet());
		remaining.remove(firstTab);
		restoreNext(states, remaining, generation);
	}

	/** Queues only one tab at a time so user events can interrupt restoration. */
	private void restoreNext(Map<CharacterInfoTab, ModelMap> states,
		Queue<CharacterInfoTab> remaining, long requestedGeneration)
	{
		if (remaining.isEmpty() || requestedGeneration != generation)
		{
			return;
		}
		SwingUtilities.invokeLater(() -> {
			if (requestedGeneration == generation)
			{
				CharacterInfoTab tab = remaining.remove();
				try
				{
					restoreTab(tab, states.get(tab));
				}
				finally
				{
					restoreNext(states, remaining, requestedGeneration);
				}
			}
		});
	}

	/** Cancels pending work and detaches only tabs which were actually restored. */
	void storeModels(Map<CharacterInfoTab, ModelMap> states)
	{
		cancelRestoreTasks();
		while (!storeQueue.isEmpty())
		{
			CharacterInfoTab tab = storeQueue.remove();
			tab.storeModels(states.get(tab));
		}
	}

	/** Invalidates callbacks from the previous character selection. */
	void cancelRestoreTasks()
	{
		generation++;
	}
}
