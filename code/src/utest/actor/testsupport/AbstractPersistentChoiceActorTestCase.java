/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * 
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
package actor.testsupport;

import java.net.URISyntaxException;

import pcgen.cdom.base.Persistent;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractPersistentChoiceActorTestCase<T>
{
	protected static final String ITEM_NAME = "ItemName";
	protected LoadContext context;

	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
//				new RuntimeLoadContext(new RuntimeReferenceContext(),
//					new ConsolidatedListCommitStrategy());
	}

	public abstract Persistent<T> getActor();

	@Test
	public void testEncodeChoice()
	{
		assertEquals(getExpected(), getActor().encodeChoice(getObject()));
	}

	protected String getExpected()
	{
		return ITEM_NAME;
	}

	protected abstract T getObject();

	@Test
	public void testDecodeChoice()
	{
		if (requiresConstruction())
		{
			assertNull(getActor().decodeChoice(context, getExpected()));
		}
		assertEquals(getObject(), getActor().decodeChoice(context, getExpected()));
	}

	protected boolean requiresConstruction()
	{
		return true;
	}

}
