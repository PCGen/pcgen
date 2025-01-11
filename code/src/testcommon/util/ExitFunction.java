package util;

/**
 * A functional interface that can be used to exit the JVM.
 */
@FunctionalInterface
public interface ExitFunction
{
	/**
	 * Is executed when the JVM should exit.
	 *
	 * @param status The status code to exit with
	 */
	void exit(int status);
}
