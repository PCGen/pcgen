package pcgen.core;

import java.util.Random;
import java.util.stream.IntStream;

import junit.framework.TestCase;
import pcgen.base.util.RandomUtil;

/**
 * The Class <code>RollingMethodsTest</code> check that the Rollingmethods class is functioning 
 * correctly.
 *
 * <br/>
 * 
 */
public class RollingMethodsTest extends TestCase
{

	static final int SIDES = 6;
	static final int TIMES = 4;
	static final int KEEP = 3;
	static final int REROLL = 1;
	static final long SEED = -5450594;

	public final void testRoll()
	{
		final Random random = new Random(SEED);
		RandomUtil.setRandomGenerator(random);

		random.setSeed(SEED);
		int[] rolls = IntStream.generate(() -> RollingMethods.roll(SIDES)).limit(TIMES).sorted().toArray();

		// Make sure the raw dice roll is in the correct range
		assertTrue(IntStream.of(rolls).min().getAsInt() > 0);
		assertTrue(IntStream.of(rolls).max().getAsInt() <= SIDES);

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		// rest the seed so we generate the same random numbers
		random.setSeed(SEED);
		final int testSum = RollingMethods.roll(TIMES, SIDES);

		// verify the RollingMethods generates the correct results
		assertEquals(sum, testSum);
	}

	public final void testTopRoll()
	{
		final Random random = new Random(SEED);
		RandomUtil.setRandomGenerator(random);

		final int[] keepArr = IntStream.rangeClosed(TIMES - KEEP, KEEP).toArray();

		random.setSeed(SEED);
		int[] rolls = IntStream.generate(() -> RollingMethods.roll(SIDES)).limit(TIMES).sorted().toArray();

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		// drop the lowest N rolls and sum
		final int dropSum = sum-IntStream.of(rolls).limit(TIMES-KEEP).sum();

		// reset the seed so we generate the same random numbers
		random.setSeed(SEED);
		final int testDropSum = RollingMethods.roll(TIMES, SIDES, keepArr);

		// verify the RollingMethods generates the correct results
		assertEquals(dropSum, testDropSum);

		final String topStr = "roll("+TIMES+","+SIDES+",top("+KEEP+"))";
		
		// reset the seed so we generate the same random numbers
		random.setSeed(SEED);

		// verify the RollingMethods generates the correct results
		final int strDropSum = RollingMethods.roll(topStr);
		assertEquals(dropSum, strDropSum);

	}

	public final void testRerollRoll()
	{
		final Random random = new Random(SEED);
		RandomUtil.setRandomGenerator(random);

		random.setSeed(SEED);
		int[] rolls =
				IntStream.generate(() -> RollingMethods.roll(SIDES - REROLL) + REROLL).limit(TIMES).sorted().toArray();

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		final String rerollStr = "roll("+TIMES+","+SIDES+",reroll("+REROLL+"))";
		
		// reset the seed so we generate the same random numbers
		random.setSeed(SEED);

		// verify the RollingMethods generates the correct results
		final int strSum = RollingMethods.roll(rerollStr);
		assertEquals(sum, strSum);


	}

}
