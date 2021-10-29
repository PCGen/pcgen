/*
 * Copyright 2015 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 *
 * Created/Reinstated on 09/07/2015
 */
package pcgen.inttest.game_modern;


import org.junit.Ignore;
import pcgen.inttest.PcgenFtlTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests a Modern Fast Hero 3/Infiltrator 3.
 * See the PCG file for details
 */
public class pcGenGUIElwoodTest extends PcgenFtlTestCase
{
	/** TODO
	 * pcGenGUIElwoodTest > testElwood() FAILED
	 *     org.opentest4j.AssertionFailedError: Expected child nodelist length '1' but was '0' - comparing <total...> at /character[1]/weapons[1]/unarmed[1]/total[1] to <total...> at /character[1]/weapons[1]/unarmed[1]/total[1] ==> expected: <false> but was: <true>
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testElwood() throws Exception
	{
		runTest("msrd_Elwood", "Modern");
	}
}
