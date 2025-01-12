package pcgen.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides a mechanism to intercept calls to System.exit and perform additional actions before the JVM
 * exits.
 * <p>
 * This class is thread-safe.
 */
public final class GracefulExit
{
	private static final Logger LOG = Logger.getLogger(GracefulExit.class.getName());

	/* This lock protects the preceding static fields */
	private static final ReentrantLock lock = new ReentrantLock();

	private static final List<ExitInterceptor> interceptors = new ArrayList<>();

	private static boolean isShuttingDown;

	private static ExitFunction exitFunction = System::exit;

	private GracefulExit()
	{
	}

	/**
	 * Exits the JVM with the given status code. This method will call all registered interceptors before exiting.
	 * <p>
	 * If any interceptor returns false, the exit will be canceled.
	 *
	 * @param status The status code to exit with
	 */
	public static void exit(int status)
	{
		try
		{
			lock.lock();
			LOG.log(Level.FINEST, () -> MessageFormat.format(
					"Started exiting with the status {0}. There are {1} interceptors.", status,
					interceptors.size()));
			isShuttingDown = true;

			boolean shouldExit = interceptors.stream()
					.map((ExitInterceptor interceptor) -> {
						boolean intercepted = interceptor.intercept(status);
						LOG.log(Level.FINEST,
								MessageFormat.format("Intercepted exit: {0} from registered interceptor: {1}",
										intercepted, interceptor.getClass().getName()));
						return intercepted;
					})
					.filter(Predicate.isEqual(Boolean.FALSE))
					.findAny()
					.orElse(Boolean.TRUE);

			if (shouldExit)
			{
				LOG.info(() -> MessageFormat.format("Exiting gracefully with status {0}", status));
				exitFunction.exit(status);
			}
		} finally
		{
			isShuttingDown = false;
			lock.unlock();
		}
	}

	/**
	 * Adds an interceptor to the list of interceptors. This method can be called before or after shutdown has started,
	 * otherwise an exception will be thrown.
	 *
	 * @param interceptor The interceptor to add
	 */
	public static void addExitInterceptor(ExitInterceptor interceptor)
	{
		try
		{
			lock.lock();
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot add an interceptor after shutdown has started");
			}
			if (interceptors.contains(interceptor))
			{
				throw new IllegalArgumentException("Interceptor already registered");
			}
			interceptors.add(interceptor);
		} finally
		{
			lock.unlock();
		}
	}

	/**
	 * Clears all registered interceptors. This method can be called before or after shutdown has started, otherwise an
	 * exception will be thrown.
	 */
	public static void clearExitInterceptors()
	{
		try
		{
			lock.lock();
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot clear interceptors after shutdown has started");
			}
			interceptors.clear();
		} finally
		{
			lock.unlock();
		}
	}

	/**
	 * Registers an exit function to be called when the JVM exits. This method can be called before or after shutdown has
	 * started, otherwise an exception will be thrown.
	 *
	 * @param exitFunction The exit function to register
	 */
	public static void registerExitFunction(ExitFunction exitFunction)
	{
		try
		{
			lock.lock();
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot register an exit function after shutdown has started");
			}
			GracefulExit.exitFunction = exitFunction;
		} finally
		{
			lock.unlock();
		}
	}
}
