package pcgen.inttest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pcgen.system.Main;
import pcgen.util.GracefulExit;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PcgenFtlErrorTest
{
	@BeforeEach
	public void setUp()
	{
		GracefulExit.registerExitFunction((int status) -> {
			assertEquals(1, status,
					MessageFormat.format("The PCGen execution returned an unexpected status code: {0}.", status));
			throw new IllegalStateException("The test execution is aborted intentionally.");
		});
	}

	@AfterEach
	public void tearDown()
	{
		GracefulExit.registerExitFunction(System::exit);
	}

	@Test
	void testWrongInputParameters()
	{
		assertThrows(IllegalStateException.class, () -> Main.main("--character", "dummy.chr"));
		assertThrows(IllegalStateException.class, () -> Main.main("--exportsheet", "dummy.xml"));
		assertThrows(IllegalStateException.class, () -> Main.main("--verbose", ""));
	}
}
