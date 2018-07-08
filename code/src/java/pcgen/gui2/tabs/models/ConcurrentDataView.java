/*
 * Copyright 2013 (C) Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingUtilities;

import pcgen.gui2.util.treeview.DataView;
import pcgen.util.Logging;

public abstract class ConcurrentDataView<E> implements DataView<E>
{

	private static ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory()
	{

		@Override
		public Thread newThread(Runnable r)
		{
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY);
			thread.setName("concurrent-dataview-thread"); //$NON-NLS-1$
			return thread;
		}

	});
	private final Runnable refreshRunnable = this::refreshTableData;
	private final Map<E, List<?>> dataMap;
	private boolean installed = false;

	public ConcurrentDataView()
	{
		this.dataMap = Collections.synchronizedMap(new WeakHashMap<E, List<?>>());
	}

	//	@Override
	public final List<?> getData(final E obj)
	{
		Future<List<?>> future = executor.submit(new Callable<List<?>>()
		{

			@Override
			public List<?> call() throws Exception
			{
				List<?> list = getDataList(obj);
				if (!list.equals(dataMap.get(obj)) && dataMap.put(obj, list) != null && installed)
				{
					SwingUtilities.invokeLater(refreshRunnable);
				}
				return list;
			}

		});
		if (!dataMap.containsKey(obj))
		{
			try
			{
				return future.get();
			}
			catch (InterruptedException | ExecutionException ex)
			{
				Logging.errorPrint(null, ex);
			}
			return Collections.emptyList();
		}
		else
		{
			return dataMap.get(obj);
		}
	}

	public void install()
	{
		installed = true;
	}

	public void uninstall()
	{
		installed = false;
	}

	protected abstract List<?> getDataList(E obj);

	protected abstract void refreshTableData();

}
