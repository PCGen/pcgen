package util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GracefulExit
{
	private static final Logger LOG = Logger.getLogger(GracefulExit.class.getName());

	/* This lock protects the preceding static fields */
	private static class Lock
	{
	}

	private static final Object lock = new GracefulExit.Lock();

	private static final List<ExitInterceptor> interceptors = new ArrayList<>();

	private static boolean isShuttingDown = false;

	private static ExitFunction exitFunction = System::exit;

	static void exit(int status)
	{
		synchronized (lock)
		{
			LOG.log(Level.FINEST, MessageFormat.format(
					"Started exiting with the status {0}. There are {1} interceptors.", status,
					interceptors.size()));
			isShuttingDown = true;

			boolean shouldExit = interceptors.stream()
					.map(interceptor -> interceptor.intercept(status))
					.peek(intercepted -> LOG.log(Level.FINEST,
							MessageFormat.format("Intercepted exit: {0} from registered interceptor: {1}",
									intercepted, intercepted.getClass().getName())))
					.filter(Predicate.isEqual(Boolean.FALSE))
					.findAny()
					.orElse(Boolean.TRUE);

			try
			{
				if (shouldExit)
				{
					LOG.info("Exiting gracefully with status " + status);
					exitFunction.exit(status);
				}
			} finally
			{
				isShuttingDown = false;
			}
		}
	}

	public static void addExitInterceptor(ExitInterceptor interceptor)
	{
		synchronized (lock)
		{
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot add an interceptor after shutdown has started");
			}
			if (interceptors.contains(interceptor))
			{
				throw new IllegalArgumentException("Interceptor already registered");
			}
			interceptors.add(interceptor);
		}
	}

	public static void clearExitInterceptors()
	{
		synchronized (lock)
		{
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot clear interceptors after shutdown has started");
			}
			interceptors.clear();
		}
	}

	public static void registerExitFunction(ExitFunction exitFunction)
	{
		synchronized (lock)
		{
			if (isShuttingDown)
			{
				throw new IllegalStateException("Cannot register an exit function after shutdown has started");
			}
			GracefulExit.exitFunction = exitFunction;
		}
	}
}
