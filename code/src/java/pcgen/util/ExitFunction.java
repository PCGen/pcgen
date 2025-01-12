package pcgen.util;

/**
 * An interface that can be used to exit the JVM.
 */
public interface ExitFunction
{
	/**
	 * The method must be used to exit from JVM in GracefulExit class.
	 * @see pcgen.util.GracefulExit
	 *
	 * @param status The status code to exit with
	 */
	void exit(int status);
}
