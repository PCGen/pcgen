/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.system.application;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import pcgen.util.Logging;

/**
 * The Class {@code DeadlockDetectorTask} reports any deadlocks detected by Java itself.
 * It doesn't directly handle the deadlock but alerts a handler to do so.
 */
public class DeadlockDetectorTask
{

	private static final ThreadFactory THREAD_FACTORY = r ->
	{
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		thread.setName("deadlock-detector");
		return thread;
	};

	private final DeadlockHandler deadlockHandler;

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, THREAD_FACTORY);

	public DeadlockDetectorTask(final DeadlockHandler deadlockHandler)
	{
		this.deadlockHandler = Objects.requireNonNull(deadlockHandler);
	}

	public void initialize()
	{
		Logging.debugPrint("starting deadlock detector");
		executor.scheduleAtFixedRate(this::runDeadlockCheck, 1, 1,
			TimeUnit.MINUTES);
	}
	
	private void runDeadlockCheck()
	{
		long[] deadlockedThreadIds = ManagementFactory.getThreadMXBean().findDeadlockedThreads();

		if (deadlockedThreadIds != null)
		{
			ThreadInfo[] threadInfos =
					ManagementFactory.getThreadMXBean().getThreadInfo(deadlockedThreadIds);

			DeadlockDetectorTask.this.deadlockHandler.handleDeadlock(threadInfos);
		}
	}
}
