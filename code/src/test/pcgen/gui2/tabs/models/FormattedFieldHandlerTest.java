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
package pcgen.gui2.tabs.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;

import pcgen.facade.util.DefaultReferenceFacade;

import org.junit.jupiter.api.Test;

/** Tests which field changes the formatted field handler forwards as edits. */
class FormattedFieldHandlerTest
{
	private final JFormattedTextField field = new JFormattedTextField();
	private final List<Integer> edits = new ArrayList<>();
	private final FormattedFieldHandler handler = new FormattedFieldHandler(field, new DefaultReferenceFacade<>(5))
	{
		@Override
		protected void valueChanged(int value)
		{
			edits.add(value);
		}
	};

	/** Component lifecycle and appearance changes, such as removal on dispose, are not edits. */
	@Test
	void nonValuePropertyChangesAreNotForwarded()
	{
		handler.install();

		field.setFont(field.getFont().deriveFont(20.0f));
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setEnabled(false);

		assertEquals(Collections.emptyList(), edits);
	}

	@Test
	void committedValueIsForwarded()
	{
		handler.install();

		field.setValue(7);

		assertEquals(List.of(7), edits);
	}

	@Test
	void uninstalledHandlerIgnoresValueChanges()
	{
		handler.install();
		handler.uninstall();

		field.setValue(7);

		assertEquals(Collections.emptyList(), edits);
	}
}
