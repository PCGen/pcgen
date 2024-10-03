package util;

import java.security.Permission;

/**
 * Intercepts calls to System.exit and throws an exception instead. Otherwise, these calls would terminate the Unit Test runner.
 * That exception can be tested for inside unit tests.
 * <p>
 * <b>Note:</b> A replacement API to intercept System.exit is in discussion in <a href="https://bugs.openjdk.org/browse/JDK-8199704">JDK-8199704</a>.
 * The SecurityManager is unlikely to be removed from Java before such an alternative exists.
 */
public class SystemExitInterceptor
{

	/**
	 * Temporarily replaces the security manager in order to intercept calls to System.exit.
	 *
	 * @return A runnable that needs to be called in order to revert the change.
	 */
	@SuppressWarnings("removal")
	public static Runnable startInterceptor()
	{
		SecurityManager previousSecurityManager = System.getSecurityManager();
		System.setSecurityManager(new SecurityManager()
		{
			@Override
			public void checkExit(int status)
			{
				throw new SystemExitCalledException(status);
			}

			@Override
			public void checkPermission(Permission perm)
			{
				// Allow other activities by default
			}
		});

		return () -> System.setSecurityManager(previousSecurityManager);
	}

	public static class SystemExitCalledException extends SecurityException
	{
		private final int statusCode;

		public SystemExitCalledException(int statusCode)
		{
			super("System.exit called with status code " + statusCode);
			this.statusCode = statusCode;
		}

		public int getStatusCode()
		{
			return statusCode;
		}
	}
}
