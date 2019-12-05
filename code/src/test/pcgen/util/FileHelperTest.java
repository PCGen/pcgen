/*
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

/**
 * FileHelperTest
 */
class FileHelperTest
{

    private static final String BACK_ONE = ".." + File.separator;

    /**
     * Test the relative paths.
     */
    @Test
    public void testRelativePaths()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File sameDir = new File("/one/two/three/four/bar.txt");
        final String path = FileHelper.findRelativePath(base, sameDir);
        assertEquals("bar.txt", path, "wrong when same directory");
    }

    /**
     * Test relative paths but back one (../)
     */
    @Test
    public void testRelativePathBack1()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File backOneDir = new File("/one/two/three/bar.txt");
        final String path = FileHelper.findRelativePath(base, backOneDir);
        assertEquals(BACK_ONE
                + "bar.txt", path, "wrong when back one directory");
    }

    /**
     * Test relative path back two (../../)
     */
    @Test
    public void testRelativePathBack2()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File backTwoDirs = new File("/one/two/bar.txt");
        final String path = FileHelper.findRelativePath(base, backTwoDirs);
        assertEquals(BACK_ONE + BACK_ONE + "bar.txt", path, "wrong when back two directories");
    }

    /**
     * Test relative path one ahead (./foobar)
     */
    @Test
    public void testRelativePathAhead1()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File aheadOneDir = new File("/one/two/three/four/five/bar.txt");
        final String path = FileHelper.findRelativePath(base, aheadOneDir);
        assertEquals("five" + File.separator
                + "bar.txt", path, "wrong when ahead one directory");
    }

    /**
     * Test relative path one ahead (./foobar/foobar)
     */
    @Test
    public void testRelativePathAhead2()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File aheadTwoDirs =
                new File("/one/two/three/four/five/six/bar.txt");
        final String path = FileHelper.findRelativePath(base, aheadTwoDirs);
        assertEquals(
                String.join(File.separator, "five", "six", "bar.txt"), path, "wrong when ahead two directories");
    }

    /**
     * Test relative path different branch.
     */
    @Test
    public void testRelativePathDifferentBranch()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File onADifferentBranch = new File("/one/two/buckle/my/shoe.txt");
        final String path =
                FileHelper.findRelativePath(base, onADifferentBranch);
        assertEquals(BACK_ONE + BACK_ONE + "buckle"
                + File.separator + "my" + File.separator + "shoe.txt", path, "wrong when on a different branch");
    }

    /**
     * Test relative path, unrelated.
     */
    @Test
    public void testRelativePathUnrelated()
    {
        final File base = new File("/one/two/three/four/foo.txt");

        final File completelyUnrelated =
                new File("/and/now/for/something/completely/different.txt");
        final String path =
                FileHelper.findRelativePath(base, completelyUnrelated);
        assertEquals(BACK_ONE + BACK_ONE + BACK_ONE + BACK_ONE + "and"
                + File.separator + "now" + File.separator + "for" + File.separator
                + "something" + File.separator + "completely" + File.separator
                + "different.txt", path, "wrong when completely different");
    }

    /**
     * Validate windows only relative paths on the same drive.
     * Note the tests only run on Windows machines.
     */
    @Test
    public void testWindowsDriveSame()
    {
        final File base = new File("C:\\Temp\\foo.txt");
        final File sameDir = new File("C:\\Temp\\bar\\baz.txt");
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        final String path = FileHelper.findRelativePath(base, sameDir);
        assertEquals(
                "bar\\baz.txt", path,
                "Incorrect relative path for same windows drive"
        );
    }

    /**
     * Validate windows only relative paths on different drives.
     * Note the tests only run on Windows machines.
     */
    @Test
    public void testWindowsDriveDifferent()
    {
        final File base = new File("C:\\Temp\\foo.txt");
        final File sameDir = new File("D:\\Temp\\bar.txt");
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        final String path = FileHelper.findRelativePath(base, sameDir);
        assertEquals(
                "D:\\Temp\\bar.txt", path,
                "Incorrect relative path for different windows drive"
        );
    }
}
