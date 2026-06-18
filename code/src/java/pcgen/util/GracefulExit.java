package pcgen.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Intercepts JVM exit calls so registered components can veto or react to shutdown.
 * <p>
 * Production code should call {@link #exit(int)} instead of {@link System#exit(int)}.
 * All registered {@link ExitInterceptor}s are invoked in registration order; if any
 * one returns {@code false} (or throws), the exit is cancelled. The underlying
 * {@link ExitFunction} is only invoked when no interceptor vetoes.
 * <p>
 * Thread-safe. Once shutdown has begun, registration calls throw
 * {@link IllegalStateException}.
 */
public final class GracefulExit
{
	private static final Logger LOG = Logger.getLogger(GracefulExit.class.getName());

	private static final ReentrantLock lock = new ReentrantLock();

	private static final List<ExitInterceptor> interceptors = new CopyOnWriteArrayList<>();

	private static volatile boolean isShuttingDown;

	private static ExitFunction exitFunction = System::exit;

	private GracefulExit()
	{
	}

	/**
	 * Exits the JVM with {@code status}, after giving every registered interceptor
	 * a chance to veto. All interceptors are always invoked; if any returns
	 * {@code false} or throws, the exit is cancelled.
	 *
	 * @param status the status code to exit with
	 */
	public static void exit(int status)
	{
		lock.lock();
		try
		{
			LOG.log(Level.FINEST, () -> MessageFormat.format(
					"Started exiting with the status {0}. There are {1} interceptors.", status,
					interceptors.size()));
			isShuttingDown = true;

			boolean shouldExit = true;
			for (ExitInterceptor interceptor : interceptors)
			{
				boolean intercepted;
				try
				{
					intercepted = interceptor.intercept(status);
				} catch (RuntimeException e)
				{
					LOG.log(Level.WARNING, e,
							() -> MessageFormat.format(
									"Interceptor {0} threw; treating as veto.",
									interceptor.getClass().getName()));
					intercepted = false;
				}
				final boolean result = intercepted;
				LOG.log(Level.FINEST, () -> MessageFormat.format(
						"Interceptor {0} returned {1}",
						interceptor.getClass().getName(), result));
				if (!intercepted)
				{
					shouldExit = false;
				}
			}

			if (shouldExit)
			{
				LOG.info(() -> MessageFormat.format("Exiting gracefully with status {0}", status));
				exitFunction.exit(status);
			}
		} finally
		{
			lock.unlock();
		}
	}

	/**
	 * Registers an interceptor. Must be called before shutdown begins; throws
	 * {@link IllegalStateException} otherwise. Returns a {@link Registration}
	 * the caller can close to deregister the interceptor.
	 *
	 * @param interceptor the interceptor to add
	 * @return a registration that removes the interceptor when closed
	 */
	public static Registration addExitInterceptor(ExitInterceptor interceptor)
	{
		lock.lock();
		try
		{
			ensureNotShuttingDown("Cannot add an interceptor after shutdown has started");
			interceptors.add(interceptor);
		} finally
		{
			lock.unlock();
		}
		return () -> removeExitInterceptor(interceptor);
	}

	/**
	 * Clears all registered interceptors. Must be called before shutdown begins;
	 * throws {@link IllegalStateException} otherwise.
	 */
	public static void clearExitInterceptors()
	{
		lock.lock();
		try
		{
			ensureNotShuttingDown("Cannot clear interceptors after shutdown has started");
			interceptors.clear();
		} finally
		{
			lock.unlock();
		}
	}

	/**
	 * Replaces the underlying {@link ExitFunction} (default {@link System#exit}).
	 * Must be called before shutdown begins; throws {@link IllegalStateException}
	 * otherwise.
	 *
	 * @param exitFunction the exit function to use
	 */
	public static void registerExitFunction(ExitFunction exitFunction)
	{
		lock.lock();
		try
		{
			ensureNotShuttingDown("Cannot register an exit function after shutdown has started");
			GracefulExit.exitFunction = exitFunction;
		} finally
		{
			lock.unlock();
		}
	}

	/**
	 * Resets state for tests. Not for production use.
	 */
	static void resetForTests()
	{
		lock.lock();
		try
		{
			interceptors.clear();
			exitFunction = System::exit;
			isShuttingDown = false;
		} finally
		{
			lock.unlock();
		}
	}

	private static void removeExitInterceptor(ExitInterceptor interceptor)
	{
		lock.lock();
		try
		{
			interceptors.remove(interceptor);
		} finally
		{
			lock.unlock();
		}
	}

	private static void ensureNotShuttingDown(String message)
	{
		if (isShuttingDown)
		{
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Token returned by {@link #addExitInterceptor(ExitInterceptor)}. Closing it
	 * deregisters the interceptor; closing twice is a no-op.
	 */
	@FunctionalInterface
	public interface Registration extends AutoCloseable
	{
		@Override
		void close();
	}
}
