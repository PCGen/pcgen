/*
 * Copyright 2026 (C) Gryxx
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import javafx.scene.control.ButtonType;

class ExportDialogControllerTest
{
	@Test
	void shouldOpenFileOnlyWhenOkIsPressed()
	{
		assertTrue(ExportDialogController.shouldOpenFile(Optional.of(ButtonType.OK)));
	}

	@Test
	void shouldNotOpenFileWhenCancelIsPressed()
	{
		assertFalse(ExportDialogController.shouldOpenFile(Optional.of(ButtonType.CANCEL)));
	}

	@Test
	void shouldNotOpenFileWhenDialogIsDismissed()
	{
		assertFalse(ExportDialogController.shouldOpenFile(Optional.empty()));
	}

	@Test
	void openFileInBackgroundOpensOnADifferentThreadThanTheCaller() throws IOException, InterruptedException
	{
		CountDownLatch opened = new CountDownLatch(1);
		AtomicLong callerThreadId = new AtomicLong();
		AtomicLong openerThreadId = new AtomicLong();

		callerThreadId.set(Thread.currentThread().getId());
		File file = new File("placeholder.pdf");
		ExportDialogController.openFileInBackground(file, toOpen -> {
			openerThreadId.set(Thread.currentThread().getId());
			opened.countDown();
		});

		assertTrue(opened.await(5, TimeUnit.SECONDS), "the opener should have been invoked");
		assertNotEquals(callerThreadId.get(), openerThreadId.get(),
				"the opener must not run on the calling (JavaFX Application) thread");
	}

	@Test
	void openFileInBackgroundReturnsBeforeTheOpenerCompletes() throws IOException, InterruptedException
	{
		CountDownLatch openerStarted = new CountDownLatch(1);
		CountDownLatch openerFinished = new CountDownLatch(1);
		File file = new File("placeholder.pdf");
		long start = System.nanoTime();

		ExportDialogController.openFileInBackground(file, toOpen -> {
			openerStarted.countDown();
			try
			{
				// Simulate a long-running / blocking native open.
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			openerFinished.countDown();
		});

		// The call must return while the opener is still running.
		assertTrue(openerStarted.await(5, TimeUnit.SECONDS), "the opener should have started");
		long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		assertTrue(elapsedMillis < 400,
				"openFileInBackground should not block the caller; elapsed=" + elapsedMillis + "ms");
		assertFalse(openerFinished.getCount() == 0, "the opener should still be running");
	}
}
