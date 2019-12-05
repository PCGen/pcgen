/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.util;

import java.util.Random;

import pcgen.util.Logging;

/**
 * This class provides utility functions for random number generation.
 * <p>
 * This is actually a wrapper for a java.util.Random object.
 * <p>
 * This is done in order to facilitate future implementations which may use a
 * subclass of Random that implements a different random number generation
 * algorithm.
 */
public final class RandomUtil
{

    /**
     * this is used by the random selection tools
     */
    private static Random random = new Random(System.currentTimeMillis());

    private RandomUtil()
    {
        // Can't instantiate
    }

    /**
     * Get a random integer between 0 (inclusive) and the given value
     * (exclusive).
     *
     * @param high The upper limit (exclusive) to be used to select a random
     *             value.
     * @return a Random Integer that is {@literal 0 < x < high}
     */
    public static int getRandomInt(int high)
    {
        if (high <= 0)
        {
            return 0;
        }
        int rand = random.nextInt(high);
        if (Logging.isDebugMode())
        {
            Logging.debugPrint("Generated random number between " //$NON-NLS-1$
                    + "0 and " + high + ": " + rand); //$NON-NLS-1$//$NON-NLS-2$
        }
        return rand;
    }

    /**
     * Set the object used to generate the random numbers
     *
     * @param rand The random number generator
     */
    public static void setRandomGenerator(Random rand)
    {
        random = rand;
    }

}
