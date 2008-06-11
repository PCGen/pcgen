package pcgen.base.lang;

import org.junit.Test;

import junit.framework.TestCase;

public class UnreachableErrorTest extends TestCase {

	@Test
	public void testEmptyConstructor()
	{
		UnreachableError unreachableError = new UnreachableError();
		assertNotNull(unreachableError);
	}
	
	@Test
	public void testMessageConstructor()
	{
		String expectedResult = "Foobar";
		UnreachableError unreachableError = new UnreachableError("Foobar");
		String result = unreachableError.getMessage();
		assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testCauseConstructor()
	{
		UnreachableError unreachableError = new UnreachableError(new NullPointerException());
		Throwable result = unreachableError.getCause();
		assertTrue(result instanceof NullPointerException);
	}

	@Test
	public void testMessageAndCauseConstructor()
	{
		String expectedResult = "Foobar";
		UnreachableError unreachableError = new UnreachableError("Foobar", new NullPointerException());
		String result = unreachableError.getMessage();
		Throwable result2 = unreachableError.getCause();
		assertTrue(result.equals(expectedResult));
		assertTrue(result2 instanceof NullPointerException);
	}

}

