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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Guards the build wiring (processResources copies LICENSE next to the
 * controller) that the About dialog's "Licensing terms" tab depends on.
 */
class AboutDialogLicenseResourceTest
{
	@Test
	void licenseResourceIsBundledOnClasspath() throws IOException
	{
		try (InputStream in = AboutDialogController.class.getResourceAsStream("LICENSE"))
		{
			assertNotNull(in, "LICENSE resource must sit next to AboutDialogController on the classpath");
			String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			assertTrue(text.contains("GNU LESSER GENERAL PUBLIC LICENSE"),
					"bundled LICENSE must be the LGPL text");
		}
	}
}
