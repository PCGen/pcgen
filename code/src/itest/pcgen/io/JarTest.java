/*
 * Copyright (c) 2018 Javier Ortiz <javydreamercsw@users.sourceforge.net>
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
package pcgen.io;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test that the jar is properly built.
 *
 * It depends on task installToRoot to have placed things in it's place. This is
 * called on Travis.
 */
public class JarTest {

    private final static File jar = new File("pcgen.jar");
    private final static File libs = new File("libs");

    @BeforeClass
    public static void setup() {
        // Make sure the jar is in root.
        assertTrue(jar.exists());

        // Make sure libs are there
        assertTrue(libs.exists());
        assertTrue(libs.isDirectory());
        assertNotEquals(0, libs.listFiles().length);
    }

    @Test
    public void testJar() throws IOException {
        // Command to create an external process
        String command = "java -jar pcgen.jar";

        // Running the above command 
        Runtime run = Runtime.getRuntime();
        Process process = run.exec(command);
        final ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<?> future = service.submit(() -> {
                try {
                    BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    process.waitFor();

                    String line;
                    while ((line = bri.readLine()) != null) {
                        System.out.println(line);
                    }

                    boolean error = false;
                    while ((line = bre.readLine()) != null) {
                        System.out.println(line);
                        error = true;
                    }
                    if (error) {
                        fail();
                    }
                } catch (InterruptedException | IOException e) {
                    fail(e.getLocalizedMessage());
                }
            });
            future.get(5, TimeUnit.SECONDS);
        } catch (final TimeoutException e) {
            // The process may have crashed
            process.destroy();
        } catch (final Exception e) {
            e.printStackTrace(System.out);
            fail(e.getLocalizedMessage());
        } finally {
            service.shutdown();
        }
    }
}
