/*
 * SwingWorker.java
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

package pcgen.gui2.util;

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker<T>
{
	private T value; // see getValue(), setValue()

	/** 
	 * Class to maintain reference to current worker thread
	 * under separate synchronization control.
	 */
	private static class ThreadVar
	{
		private Thread thread;

		ThreadVar(Thread t)
		{
			thread = t;
		}

		synchronized Thread get()
		{
			return thread;
		}

		synchronized void clear()
		{
			// TODO check for memory leak here - maybe need to dispose thread resources??
			thread = null;
		}
	}

	private ThreadVar threadVar;

	/** 
	 * Get the value produced by the worker thread, or null if it 
	 * hasn't been constructed yet
	 * @return value
	 */
	protected synchronized T getValue()
	{
		return value;
	}

	/** 
	 * Set the value produced by worker thread 
	 * @param x
	 */
	private synchronized void setValue(T x)
	{
		value = x;
	}

	/** 
	 * Compute the value to be returned by the {@code get} method.
	 * @return Object
	 */
	public abstract T construct();

	/**
	 * Called on the AWT event dispatching thread (not on the worker thread)
	 * after the {@code construct} method has returned.
	 */
	public void finished()
	{
		// Do Nothing
	}

	/**
	 * A new method that interrupts the worker thread.  Call this method
	 * to force the worker to stop what it's doing.
	 */
	public void interrupt()
	{
		Thread t = threadVar.get();
		if (t != null)
		{
			t.interrupt();
		}
		threadVar.clear();
	}

	/**
	 * Blocks until the worker thread is finished.
	 * Then returns the value created by the {@code construct} method.
	 * 
	 * Returns null if either the constructing thread or the current
	 * thread was interrupted before a value was produced
	 * 
	 * @return the value created by the {@code construct} method
	 */
	public T get()
	{
		while (true)
		{
			Thread t = threadVar.get();
			if (t == null)
			{
				return getValue();
			}
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt(); // propagate
				return null;
			}
		}
	}

	/**
	 * Start a thread that will call the {@code construct} method
	 * and then exit.
	 */
	public SwingWorker()
	{
		final Runnable doFinished = this::finished;

		Runnable doConstruct = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					setValue(construct());
				}
				finally
				{
					threadVar.clear();
				}

				SwingUtilities.invokeLater(doFinished);
			}
		};

		Thread t = new Thread(doConstruct);
		threadVar = new ThreadVar(t);
	}

	/**
	 * Start the worker thread, which will execute the implementor's 
	 * construct() method to compute the value to be returned by the
	 * get() method.
	 */
	public void start()
	{
		Thread t = threadVar.get();
		if (t != null)
		{
			t.start();
		}
	}
}
