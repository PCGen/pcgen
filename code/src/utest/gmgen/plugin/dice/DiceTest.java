package gmgen.plugin.dice;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import org.junit.jupiter.api.Test;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
class DiceTest
{
    @Test
    public void nonRandomDiceAssertions()
    {
        Die oneDie = new Dice(1, 1);
        assertThat("one sided die returns 1", oneDie.roll(), is(1));
        Die manyDie = new Dice(10, 1);
        assertThat("many rolls of a single die returns number of rolls", manyDie.roll(), is(10));
    }

    @Test
    public void randomDiceAssertions()
    {
        Die die = new Dice(1, 10);
        assertThat("single die returns value as expected", die.roll(),
                allOf(lessThanOrEqualTo(10), greaterThanOrEqualTo(1))
        );
    }


}
