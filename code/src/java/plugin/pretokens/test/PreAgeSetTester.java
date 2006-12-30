/*
 * PreAgeSetTester.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 30, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 05:36:01 +0100 (Sun, 17 Dec 2006) $
 *
 */
package plugin.pretokens.test;

import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author perchrh
 *
 */
public class PreAgeSetTester extends AbstractPrerequisiteTest implements
                PrerequisiteTest
{

        /* (non-Javadoc)
         * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
         */
        @Override
        public int passes(final Prerequisite prereq, final PlayerCharacter character)
        {
                //TODO IMPLEMENT THIS
        		//compare PC object ageset with prereq's ageset
        		//pc.ageset == prereq ageset first, 
        		//should support >= and the likes too
        	
                return 1;
        }

        /* (non-Javadoc)
         * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
         */
        public String kindHandled()
        {
                return "AGESET"; //$NON-NLS-1$
        }

}
