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

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.io.FileAccess;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystems;

import static pcgen.util.TestHelper.evaluateToken;

/**
 * The Class <code>PortraitTokenTest</code> checks the function of PortraitToken.
 *
 * <br/>
 *
 */

public class PortraitTokenTest extends AbstractCharacterTestCase
{

	private PortraitToken portraitToken = new PortraitToken();

	/**
	 * Check the generation of a thumbnail file for valid, no scaling conditions.
	 * @throws Exception Not expected.
	 */
	public void testThumb() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		pc.setPortraitPath("code/src/java/pcgen/resources/images/SplashPcgen_Alpha.png");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, Constants.THUMBNAIL_SIZE, Constants.THUMBNAIL_SIZE));
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNotNull("THUMB should not be null ", thumbResult);
		assertNotSame("Thumb should not be portrait", pc.getDisplay().getPortraitPath(), thumbResult);
		File thumbFile = new File(thumbResult);
		assertTrue("File should exist", thumbFile.exists());
		BufferedImage image = ImageIO.read(thumbFile);
		assertNotNull("THUMB image should not be null ", image);
	}

	/**
	 * Check the generation of a thumbnail file for valid conditions with scaling required.
	 * @throws Exception Not expected.
	 */
	public void testThumbScaling() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		pc.setPortraitPath("code/src/java/pcgen/resources/images/SplashPcgen_Alpha.png");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, 140, 140));
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNotNull("THUMB should not be null ", thumbResult);
		assertNotSame("Thumb should not be portrait", pc.getDisplay().getPortraitPath(), thumbResult);
		File thumbFile = new File(thumbResult);
		assertTrue("File should exist", thumbFile.exists());
		BufferedImage image = ImageIO.read(thumbFile);
		assertNotNull("THUMB image should not be null ", image);
		assertEquals("Incorrect scaled width",  Constants.THUMBNAIL_SIZE, image.getWidth());
		assertEquals("Incorrect scaled height",  Constants.THUMBNAIL_SIZE, image.getHeight());
	}

	/**
	 * Check the generation of a thumbnail file for invalid conditions.
	 * @throws Exception Not expected.
	 */
	public void testThumbInvalid() throws Exception
	{
		PlayerCharacter pc = getCharacter();
		pc.setName("PortraitTokenTest");
		String thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull("No image or rect should be null", thumbResult);

		pc.setPortraitPath("code/src/java/pcgen/resources/images/SplashPcgen_Alpha.png");
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull("No rect should be null", thumbResult);

		pc.setPortraitPath("");
		pc.setPortraitThumbnailRect(new Rectangle(160, 70, 140, 140));
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull("No image should be null", thumbResult);

		pc.setPortraitPath("foo1gghas");
		thumbResult = portraitToken.getToken("PORTRAIT.THUMB", pc, null);
		assertNull("Invalid image should be null", thumbResult);
	}

	/**
	 * The portrait URI shouldn't be encoded, because if the path to the file has "unsafe" characters (e.g., '&')
	 * The generated XML uses FreeMarker's url_path, and it encodes URIs correctly.
	 * See OS-538 for further details.
	 * @throws Exception Not expected.
	 */
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
