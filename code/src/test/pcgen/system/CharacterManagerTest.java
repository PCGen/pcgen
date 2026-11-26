/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.facade.PartyFacadeImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests the order in which open characters are detached from list listeners and closed. */
class CharacterManagerTest
{
	private final PartyFacadeImpl party = (PartyFacadeImpl) CharacterManager.getCharacters();
	private final List<String> closed = new ArrayList<>();
	private ListListener<CharacterFacade> listener;

	@AfterEach
	void tearDown()
	{
		if (listener != null)
		{
			party.removeListListener(listener);
		}
		party.clearContents();
	}

	/**
	 * The character tabs detach their UI models when the party list changes, so no
	 * character may be closed before listeners have seen it leave the list.
	 */
	@Test
	void removeAllCharactersNotifiesListenersBeforeClosingCharacters()
	{
		party.addElement(fakeCharacter("first"));
		party.addElement(fakeCharacter("second"));
		List<String> closedWhenNotified = new ArrayList<>();
		listener = new ListListener<>()
		{
			@Override
			public void elementAdded(ListEvent<CharacterFacade> e)
			{
			}

			@Override
			public void elementRemoved(ListEvent<CharacterFacade> e)
			{
				closedWhenNotified.addAll(closed);
			}

			@Override
			public void elementsChanged(ListEvent<CharacterFacade> e)
			{
				closedWhenNotified.addAll(closed);
			}

			@Override
			public void elementModified(ListEvent<CharacterFacade> e)
			{
			}
		};
		party.addListListener(listener);

		CharacterManager.removeAllCharacters();

		assertEquals(Collections.emptyList(), closedWhenNotified);
		assertEquals(List.of("first", "second"), closed);
		assertEquals(0, party.getSize());
	}

	/**
	 * Creates a character without a file that records its name when closed.
	 *
	 * @param name the name recorded by {@code closeCharacter}
	 * @return a minimal character facade for party-list operations
	 */
	private CharacterFacade fakeCharacter(String name)
	{
		DefaultReferenceFacade<File> fileRef = new DefaultReferenceFacade<>();
		return (CharacterFacade) Proxy.newProxyInstance(CharacterFacade.class.getClassLoader(),
			new Class<?>[]{CharacterFacade.class}, (proxy, method, args) -> switch (method.getName())
			{
				case "closeCharacter" ->
				{
					closed.add(name);
					yield null;
				}
				case "getFileRef" -> fileRef;
				case "equals" -> proxy == args[0];
				case "hashCode" -> System.identityHashCode(proxy);
				case "toString" -> name;
				default -> throw new UnsupportedOperationException(method.getName());
			});
	}
}
