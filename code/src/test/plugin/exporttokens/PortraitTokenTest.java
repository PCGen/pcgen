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
package plugin.exporttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pcgen.util.TestHelper.evaluateToken;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystems;

import javax.imageio.ImageIO;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;

import org.junit.jupiter.api.Test;
import pcgen.io.FileAccess;

/**
 * The Class {@code PortraitTokenTest} checks the function of PortraitToken.
 */

public class PortraitTokenTest extends AbstractCharacterTestCase
{

	private final PortraitToken portraitToken = new PortraitToken();

	/**
	 * Check the generation of a thumbnail file for valid, no scaling conditions.
	 * @throws Exception Not expected.
	 */
	@Test
	public void testThumb() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		pc.setPortraitPath("code/src/resources/pcgen/images/SplashPcgen_Alpha.png");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE));
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNotNull(thumbResult, "THUMB should not be null ");
		assertNotSame(pc.getDisplay().getPortraitPath(), thumbResult, "Thumb should not be portrait");
		File thumbFile = new File(thumbResult);
		assertTrue(thumbFile.exists(), "File should exist");
		BufferedImage image = ImageIO.read(thumbFile);
		assertNotNull(image, "THUMB image should not be null");
	}

	/**
	 * Check the generation of a thumbnail file for valid conditions with scaling required.
	 * @throws Exception Not expected.
	 */
	@Test
	public void testThumbScaling() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		pc.setPortraitPath("code/src/resources/pcgen/images/SplashPcgen_Alpha.png");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, 140, 140));
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNotNull(thumbResult, "THUMB should not be null");
		assertNotSame(pc.getDisplay().getPortraitPath(), thumbResult, "Thumb should not be portrait");
		File thumbFile = new File(thumbResult);
		assertTrue(thumbFile.exists(), "File should exist");
		BufferedImage image = ImageIO.read(thumbFile);
		assertNotNull(image, "THUMB image should not be null");
		assertEquals(Constants.THUMBNAIL_SIZE, image.getWidth(), "Incorrect scaled width");
		assertEquals(Constants.THUMBNAIL_SIZE, image.getHeight(), "Incorrect scaled height");
	}

	/**
	 * Check the generation of a thumbnail file for invalid conditions.
	 */
	@Test
	public void testThumbInvalid()
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull(thumbResult, "No image or rect should be null");

		pc.setPortraitPath("code/src/resources/pcgen/images/SplashPcgen_Alpha.png");
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull(thumbResult, "No rect should be null");

		pc.setPortraitPath("");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, 140, 140));
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull(thumbResult, "No image should be null");

		pc.setPortraitPath("foo1gghas");
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull(thumbResult, "Invalid image should be null");
	}

	/**
	 * The portrait URI shouldn't be encoded, because if the path to the file has "unsafe" characters (e.g., '&')
	 * The generated XML uses FreeMarker's url_path, and it encodes URIs correctly.
	 * See OS-538 for further details.
	 * @throws Exception Not expected.
	 */
	@Test
	public void testNonEncodedURI() throws Exception {
		var inputPortraitPath = FileSystems.getDefault()
				.getPath("code", "src", "resources", "pcgen", "D&D 3.Xe", "portrait.png")
				.toString();

		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		pc.setPortraitPath(inputPortraitPath);

		FileAccess.setCurrentOutputFilter("xml");
		var outputPortraitPath = evaluateToken("PORTRAIT", pc);
		assertEquals(inputPortraitPath, outputPortraitPath, "PORTRAIT token must not be encoded");
	}
}
