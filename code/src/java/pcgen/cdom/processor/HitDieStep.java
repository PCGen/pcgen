/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.processor;

import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Processor;

/**
 * A HitDieStep represents a modified HitDie that changes along the path of Hit
 * Dice defined in the Game Mode. A bound may be set in order to limit movement
 * beyond a given point.
 * <p>
 * Because the number of steps that this HitDieStep will apply is provided
 * during construction, the object constructing this HitDieStep is expected to
 * understand if it is setting an upper or lower bound.
 */
public class HitDieStep implements Processor<HitDie>
{

    /**
     * The number of steps by which this HitDieStep object will modify the
     * incoming HitDie.
     */
    private final int numSteps;

    /**
     * The bound beyond which this HitDieStep will not modify the incoming
     * HitDie. This bound overrides the number of steps to be taken, if the
     * bound is reached.
     */
    private final HitDie dieLimit;

    /**
     * Constructs a new HitDieStep object with the given number of steps and
     * bound.
     * <p>
     * Because the number of steps that this HitDieStep will apply is provided
     * during construction, the object constructing this HitDieStep is expected
     * to understand if it is setting an upper or lower bound.
     * <p>
     * *NOTE* if the HitDie provided as a bound is not in the global sequence of
     * HitDie objects (defined in the Game Mode), then this method will fail to
     * stop at the bound. Matching on this bound is exact.
     *
     * @param steps  The number of steps this HitDieStep will modify the incoming
     *               HitDie provided to applyProcessor
     * @param stopAt The bound, indicating the HitDie at which the HitDitStep will
     *               not proceed. This bound overrides the number of steps to be
     *               taken, if the bound is reached. This bound may be null to
     *               indicate there is no bound.
     * @throws IllegalArgumentException if the number of steps is zero (since that is effectively a
     *                                  pass-through, no Processor is required)
     */
    public HitDieStep(int steps, HitDie stopAt)
    {
        if (steps == 0)
        {
            throw new IllegalArgumentException();
        }
        numSteps = steps;
        dieLimit = stopAt;
    }

    /**
     * Applies this Processor to the given input object, in the context of the
     * given context object.
     * <p>
     * *NOTE* if the HitDie provided is not in the global sequence of HitDie
     * objects (defined in the Game Mode), then this method will fail to step
     * that HitDie, and the original, unmodified HitDie will be returned.
     * <p>
     * Since HitDieStep is universal, the given context is ignored.
     *
     * @param origHD  The input HitDie this HitDieStep will act upon.
     * @param context The context, ignored by HitDieStep.
     * @return The modified HitDie, as limited by the bound of this HitDieStep.
     * @throws NullPointerException if the given HitDie is null
     */
    @Override
    public HitDie applyProcessor(HitDie origHD, Object context)
    {
        int steps = numSteps;
        HitDie currentDie = origHD;
        while (steps != 0)
        {
            // Order is important, dieLimit may be null
            if (currentDie.equals(dieLimit))
            {
                return currentDie;
            }
            if (steps > 0)
            {
                currentDie = currentDie.getNext();
                steps--;
            } else
            {
                assert steps < 0;
                currentDie = currentDie.getPrevious();
                steps++;
            }
        }
        return currentDie;
        /*
         * Theoretically, the die sizes here should be stored as ... what? A
         * AbstractSequencedConstant, effectively? This gives the ability to
         * look up the next one... that makes HitDie not really storing an
         * Int... so Hit Die really should be a helper, or an enumeration?
         *
         * So it looks like an enumeration is OUT because the MODs will actually
         * alter to unexpected values... like 8 * 3 = 24... therefore, this
         * really needs to be thought through to determine what is best... The
         * behavior for these cases is undefined.
         *
         * There is a (short) thread on pcgen-devel "Hit Die Locking" from Nov
         * 2006, where this issue remains unresolved.
         */
    }

    /**
     * Returns a representation of this HitDieStep, suitable for storing in an
     * LST file.
     *
     * @return A representation of this HitDieStep, suitable for storing in an
     * LST file.
     */
    @Override
    public String getLSTformat()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('%');
        if (dieLimit == null)
        {
            sb.append('H');
        }
        if (numSteps > 0)
        {
            sb.append("up");
        } else
        {
            sb.append("down");
        }
        sb.append(Math.abs(numSteps));
        return sb.toString();
    }

    /**
     * The class of object this Processor acts upon (HitDie).
     *
     * @return The class of object this Processor acts upon (HitDie.class)
     */
    @Override
    public Class<HitDie> getModifiedClass()
    {
        return HitDie.class;
    }

    @Override
    public int hashCode()
    {
        return dieLimit == null ? numSteps : numSteps + dieLimit.hashCode() * 29;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof HitDieStep)
        {
            HitDieStep other = (HitDieStep) obj;
            return other.numSteps == numSteps
                    && (dieLimit == null && other.dieLimit == null || dieLimit != null && dieLimit.equals(other.dieLimit));
        }
        return false;
    }
}
