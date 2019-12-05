/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3;

import javax.swing.SwingUtilities;

import pcgen.util.Logging;

import javafx.application.Platform;

/**
 * A set of utility functions to help ensure we're operating on the thread
 * we think we are.
 */
public final class GuiAssertions
{
    private static final class WrongThreadException extends RuntimeException
    {
        private WrongThreadException(String message)
        {
            super(message);
        }
    }

    private GuiAssertions()
    {
    }

    public static void assertIsJavaFXThread()
    {
        if (!Platform.isFxApplicationThread())
        {
            throw new WrongThreadException(
                    "expected to be on JavaFX thread - actually on: " + Thread.currentThread().getName());
        }
    }

    public static void assertIsNotJavaFXThread()
    {
        if (Platform.isFxApplicationThread())
        {
            throw new WrongThreadException(
                    "expected NOT to be on JavaFX thread - actually on: " + Thread.currentThread().getName());
        }
    }


    public static void assertIsSwingThread()
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new WrongThreadException(
                    "expected to be on swing thread - actually on: " + Thread.currentThread().getName());
        }
    }

    public static void assertIsNotSwingThread()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            throw new WrongThreadException(
                    "expected NOT to be on swing thread - actually on: " + Thread.currentThread().getName());
        }
    }


    public static void assertIsNotOnGUIThread()
    {
        if (Platform.isFxApplicationThread() || SwingUtilities.isEventDispatchThread())
        {
            throw new WrongThreadException(
                    "expected NOT to be on gui thread - actually on: " + Thread.currentThread().getName());
        }
    }

    /**
     * This should be rarely used. Instead assert which thread you're actually supposed to be on.
     */
    public static void assertIsOnGUIThread()
    {
        Logging.debugPrint("asserting unknown gui thread: actually on: " + Thread.currentThread().getName());
        if (!Platform.isFxApplicationThread() && !SwingUtilities.isEventDispatchThread())
        {
            throw new WrongThreadException(
                    "expected to be on gui thread - actually on: " + Thread.currentThread().getName());
        }
    }


}
