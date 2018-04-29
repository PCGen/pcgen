package pcgen.core;

import java.util.Random;
import java.util.stream.IntStream;
import junit.framework.TestCase;
import pcgen.base.util.RandomUtil;
import pcgen.core.RollingMethods;

/**
 * The Class <code>RollingMethodsTest</code> check that the Rollingmethods class is functioning 
 * correctly.
 *
 * <br/>
 * 
 */
public class RollingMethodsTest extends TestCase
{

	static final int sides = 6;
	static final int times = 4;
	static final int keep = 3;
	static final int reroll = 1;
	static final long seed = -5450594;

	public final void testRoll()
	{
		final Random random = new Random(seed);
		RandomUtil.setRandomGenerator(random);

		random.setSeed(seed);
		int [] rolls = IntStream.generate(() -> RollingMethods.roll(sides)).limit(times).sorted().toArray();

		// Make sure the raw dice roll is in the correct range
		assertTrue(IntStream.of(rolls).min().getAsInt() > 0);
		assertTrue(IntStream.of(rolls).max().getAsInt() <= sides);

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		// rest the seed so we generate the same random numbers
		random.setSeed(seed);
		final int testSum = RollingMethods.roll(times, sides);

		// verify the RollingMethods generates the correct results
		assertEquals(sum, testSum);
	}

	public final void testTopRoll()
	{
		final Random random = new Random(seed);
		RandomUtil.setRandomGenerator(random);

		final int[] keepArr = IntStream.rangeClosed(times - keep, keep).toArray();

		random.setSeed(seed);
		int [] rolls = IntStream.generate(() -> RollingMethods.roll(sides)).limit(times).sorted().toArray();

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		// drop the lowest N rolls and sum
		final int dropSum = sum-IntStream.of(rolls).limit(times-keep).sum();

		// reset the seed so we generate the same random numbers
		random.setSeed(seed);
		final int testDropSum = RollingMethods.roll(times, sides, keepArr);

		// verify the RollingMethods generates the correct results
		assertEquals(dropSum, testDropSum);

		final String topStr = "roll("+times+","+sides+",top("+keep+"))";
		
		// reset the seed so we generate the same random numbers
		random.setSeed(seed);

		// verify the RollingMethods generates the correct results
		final int strDropSum = RollingMethods.roll(topStr);
		assertEquals(dropSum, strDropSum);

	}

	public final void testRerollRoll()
	{
		final Random random = new Random(seed);
		RandomUtil.setRandomGenerator(random);

		random.setSeed(seed);
		int [] rolls = IntStream.generate(() -> RollingMethods.roll(sides-reroll)+reroll).limit(times).sorted().toArray();

		// compute the sum of all the rolls
		final int sum = IntStream.of(rolls).sum();

		final String rerollStr = "roll("+times+","+sides+",reroll("+reroll+"))";
		
		// reset the seed so we generate the same random numbers
		random.setSeed(seed);

		// verify the RollingMethods generates the correct results
		final int strSum = RollingMethods.roll(rerollStr);
		assertEquals(sum, strSum);


	}

}
