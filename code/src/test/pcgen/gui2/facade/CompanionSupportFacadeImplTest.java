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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Campaign;
import pcgen.core.DataSet;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompanionSupportFacadeImplTest extends AbstractCharacterTestCase
{

	private MockUIDelegate uiDelegate;
	private DataSetFacade dataSetFacade;
	private Race masterRace;
	private Race companionRace;
	private CompanionList companionList;
	private TodoManager todoManager;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		companionList = Globals.getContext().getReferenceContext().constructNowIfNecessary(CompanionList.class,
				"Familiar");
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
		ListFacade<Campaign> campaigns = new DefaultListFacade<>();
		dataSetFacade = new DataSet(Globals.getContext(), SettingsHandler.getGameAsProperty().get(), campaigns);
		masterRace = TestHelper.makeRace("Wood Elf");
		companionRace = TestHelper.makeRace("Weasel");

		CDOMReference<Race> race  = new CDOMDirectSingleRef<>(companionRace);
		CDOMSingleRef<CompanionList> ref = new CDOMSimpleSingleRef<>(
			BasicClassIdentity.getIdentity(CompanionList.class),
			companionList.getKeyName());
		FollowerOption option = new FollowerOption(race, ref);
		masterRace.addToListFor(ListKey.COMPANIONLIST, option);
		finishLoad();
	}

	/**
	 * Test method for {@link pcgen.gui2.facade.CompanionSupportFacadeImpl#addCompanion(CharacterFacade, String)}
	 */
	@Test
	public void testAddCompanion()
	{
		PlayerCharacter master = getCharacter();
		master.setRace(masterRace);
		master.setFileName("Master.pcg");
		master.setName("Master1");
		CompanionSupportFacadeImpl masterCsfi =
				new CompanionSupportFacadeImpl(master, todoManager,
						new DefaultReferenceFacade<>(),
						new DefaultReferenceFacade<>(),
					new CharacterFacadeImpl(master, uiDelegate, dataSetFacade));
		
		PlayerCharacter companion = new PlayerCharacter();
		companion.setRace(companionRace);
		companion.setFileName("Companion.pcg");
		companion.setName("Companion1");
		CharacterFacadeImpl compFacade = new CharacterFacadeImpl(companion, uiDelegate, dataSetFacade);

		assertNull("No companion type should be set yet.", compFacade.getCompanionType());
		assertTrue("Master should have no companions", master.getFollowerList().isEmpty());
		
		masterCsfi.addCompanion(compFacade, "Familiar");
		Follower follower = master.getFollowerList().iterator().next();
		assertEquals("Companion should be the first follower", companion.getName(), follower.getName());
		assertEquals("Master should have one companion", 1, master.getFollowerList().size());
		
		assertNotNull("Companion should have a master now", companion.getDisplay().getMaster());
		assertEquals("Companion's master", master.getName(), companion.getDisplay().getMaster().getName());
	}

	@Override
	protected void defaultSetupEnd()
	{
		//Nothing, we will trigger ourselves
	}
}
