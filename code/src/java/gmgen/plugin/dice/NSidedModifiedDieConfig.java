/*
 *  Initiative - A role playing utility to track turns
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin.dice;

import java.text.MessageFormat;
import java.util.Random;
import java.util.stream.IntStream;

class NSidedModifiedDieConfig implements DiceConfig
{
    private final int n;
    private final int sides;
    private final int bias;

    NSidedModifiedDieConfig(final int n, final int sides, final int bias, final Random random)
    {
        this.n = n;
        this.sides = sides;
        this.bias = bias;
    }

    @Override
    public int roll()
    {
        return IntStream.generate(() -> Die.RAND.nextInt(sides) + 1).map(v -> v + bias).limit(n).sum();
    }

    @Override
    public String toFormula()
    {
        if (bias == 0)
        {
            return MessageFormat.format("{0}d{1}", n, sides);
        }
        return MessageFormat.format("{0}d{1} + {2}", n, sides, bias);
    }
}
