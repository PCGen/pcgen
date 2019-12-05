/*
 * Copyright James Dempsey, 2012
 *
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
package pcgen.gui2.facade;

import pcgen.core.Race;
import pcgen.facade.core.CompanionStubFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 * The Class {@code CompanionStub} contains a definition of a possible
 * companion (i.e. animal companion, familiar, follower etc) for a character.
 */

public class CompanionStub implements CompanionStubFacade
{

    private final DefaultReferenceFacade<Race> race;
    private final String companionType;

    /**
     * Create a new instance of CompanionStub
     *
     * @param race          The race of the possible companion.
     * @param companionType The type of companion.
     */
    CompanionStub(Race race, String companionType)
    {
        this.race = new DefaultReferenceFacade<>(race);
        this.companionType = companionType;
    }

    @Override
    public ReferenceFacade<Race> getRaceRef()
    {
        return race;
    }

    @Override
    public String getCompanionType()
    {
        return companionType;
    }

    @Override
    public String toString()
    {
        return race.toString();
    }

}
