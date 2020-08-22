/**
 * Copyright 2003 (C) John Watson <john@sleazyweasel.com>
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
package pcgen.util;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

import junit.framework.TestCase;

/**
 * FileHelperTest
 */
public class FileHelperTest extends TestCase
{

	static final String BACK_ONE = ".." + File.separator;
			
	/**
	 * Constructs a new <code>FileHelperTest</code>.
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase()
	 */
	public FileHelperTest()
	{
		super();
	}

	/**
	 * Constructs a new <code>FileHelperTest</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase(String)
	 */
	public FileHelperTest(final String name)
	{
		super(name);
	}

	/**
	 * Test the relative paths.
	 */
	public void testRelativePaths()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File sameDir = new File("/one/two/three/four/bar.txt");
		final String path = FileHelper.findRelativePath(base, sameDir);
		assertEquals("wrong when same directory", "bar.txt", path);
	}

	/**
	 * Test relative paths but back one (../)
	 */
	public void testRelativePathBack1()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File backOneDir = new File("/one/two/three/bar.txt");
		final String path = FileHelper.findRelativePath(base, backOneDir);
		assertEquals("wrong when back one directory", BACK_ONE
			+ "bar.txt", path);
	}

	/**
	 * Test relative path back two (../../)
	 */
	public void testRelativePathBack2()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File backTwoDirs = new File("/one/two/bar.txt");
		final String path = FileHelper.findRelativePath(base, backTwoDirs);
		assertEquals("wrong when back two directories", BACK_ONE+BACK_ONE+"bar.txt", path);
	}

	/**
	 * Test relative path one ahead (./foobar)
	 */
	public void testRelativePathAhead1()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File aheadOneDir = new File("/one/two/three/four/five/bar.txt");
		final String path = FileHelper.findRelativePath(base, aheadOneDir);
		assertEquals("wrong when ahead one directory", "five" + File.separator
			+ "bar.txt", path);
	}

	/**
	 * Test relative path one ahead (./foobar/foobar)
	 */
	public void testRelativePathAhead2()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File aheadTwoDirs =
				new File("/one/two/three/four/five/six/bar.txt");
		final String path = FileHelper.findRelativePath(base, aheadTwoDirs);
		assertEquals("wrong when ahead two directories", "five"
			+ File.separator + "six" + File.separator + "bar.txt", path);
	}

	/**
	 * Test relative path different branch.
	 */
	public void testRelativePathDifferentBranch()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File onADifferentBranch = new File("/one/two/buckle/my/shoe.txt");
		final String path =
				FileHelper.findRelativePath(base, onADifferentBranch);
		assertEquals("wrong when on a different branch", BACK_ONE+BACK_ONE+"buckle"
			+ File.separator + "my" + File.separator + "shoe.txt", path);
	}

	/**
	 * Test relative path, unrelated.
	 */
	public void testRelativePathUnrelated()
	{
		final File base = new File("/one/two/three/four/foo.txt");

		final File completelyUnrelated =
				new File("/and/now/for/something/completely/different.txt");
		final String path =
				FileHelper.findRelativePath(base, completelyUnrelated);
		assertEquals("wrong when completely different", BACK_ONE+BACK_ONE+BACK_ONE+BACK_ONE+"and"
			+ File.separator + "now" + File.separator + "for" + File.separator
			+ "something" + File.separator + "completely" + File.separator
			+ "different.txt", path);
	}
	
	/**
	 * Validate windows only relative paths on the same drive. 
	 * Note the tests only run on Windows machines.
	 * @throws Exception In some failure conditions.
	 */
	public void testWindowsDriveSame() throws Exception
	{
		final File base = new File("C:\\Temp\\foo.txt"); 
		final File sameDir = new File("C:\\Temp\\bar\\baz.txt"); 
		if (SystemUtils.IS_OS_WINDOWS)
		{
			final String path = FileHelper.findRelativePath(base, sameDir);
			assertEquals("Incorrect relative path for same windows drive",
				"bar\\baz.txt", path);
		}
	}
	
	/**
	 * Validate windows only relative paths on different drives. 
	 * Note the tests only run on Windows machines.
	 * @throws Exception In some failure conditions.
	 */
	public void testWindowsDriveDifferent() throws Exception
	{
		final File base = new File("C:\\Temp\\foo.txt"); 
		final File sameDir = new File("D:\\Temp\\bar.txt");
		
		if (SystemUtils.IS_OS_WINDOWS)
		{
			final String path = FileHelper.findRelativePath(base, sameDir);
			assertEquals("Incorrect relative path for different windows drive",
				"D:\\Temp\\bar.txt", path);
		}
	}

	@Test
	public void testFileNameStripsBadCharacters() {
		final String invalidCharacterFilename = "Baalgor: <the>/a great?\u0000 * \"SNA|KE EYES\" White\\mane.TXT";

		final String output = FileHelper.sanitizeFilename(invalidCharacterFilename);
		assertEquals("Baalgor thea great  SNAKE EYES Whitemane.TXT", output);
	}
}
