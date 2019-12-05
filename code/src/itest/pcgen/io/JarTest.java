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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Disabled;

/**
 * Test that the jar is properly built.
 * <p>
 * It depends on task installToRoot to have placed things in it's place. This is
 * called on Travis.
 */
public class JarTest
{

    private static final String pcgenJar = "pcgen.jar";
    private final File jar = new File(pcgenJar);
    private final File libs = new File("libs");

    @Disabled
    void testJar() throws IOException, InterruptedException, ExecutionException
    {
        // Make sure the jar is in root.
        assertThat(jar.exists(), is(true));

        // Make sure libs are there
        assertThat(libs.exists(), is(true));
        assertThat(libs.isDirectory(), is(true));
        assertThat(libs.listFiles().length, is(not(0)));

        // Running the the command
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", pcgenJar);
        Process process = pb.start();
        final ExecutorService service = Executors.newSingleThreadExecutor();
        try
        {
            final Future<?> future = service.submit(() -> {
                try
                {
                    BufferedReader bri
                            = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader bre
                            = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    process.waitFor();

                    String line;
                    while ((line = bri.readLine()) != null)
                    {
                        System.out.println(line);
                    }

                    boolean error = false;
                    while ((line = bre.readLine()) != null)
                    {
                        System.out.println(line);
                        error = true;
                    }
                    if (error)
                    {
                        fail();
                    }
                } catch (InterruptedException | IOException e)
                {
                    fail(e.getLocalizedMessage());
                }
            });
            future.get(5, TimeUnit.SECONDS);
        } catch (final TimeoutException e)
        {
            // The process may have crashed
            process.destroy();
        } finally
        {
            service.shutdown();
        }
    }
}
