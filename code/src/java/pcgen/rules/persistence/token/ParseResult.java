/*
 * Copyright (c) 2009 Mark Jeffries <motorviper@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import java.net.URI;
import java.util.logging.Level;

import pcgen.util.Logging;

/**
 * Interface to provide feedback on parsing operations.
 */
public interface ParseResult
{
    /**
     * Object to be returned from parsing operations that succeeded with no messages.
     */
    Pass SUCCESS = new Pass();

    /*
     * Temporary object for reporting errors that should be investigated further.
     * See plugin.lsttokens.race.FeatToken.
     */
    Fail INTERNAL_ERROR = new Fail("Internal error.");

    /**
     * State of the parse operation.
     *
     * @return True if the parse was successful.
     */
    boolean passed();

    /**
     * Log any messages associated with the operation.
     */
    void printMessages(URI uri);

    /*
     * Temporary method for aiding conversion to use of ParseResult.
     * See pcgen.rules.persistence.token.ErrorParsingWrapper for use.
     */
    void addMessagesToLog(URI uri);

    /**
     * Class representing a message from the parser.
     */
    class QueuedMessage
    {
        public final Level level;
        public final String message;
        public final StackTraceElement[] stackTrace;

        public QueuedMessage(Level lvl, String msg)
        {
            level = lvl;
            message = msg;
            stackTrace = Thread.currentThread().getStackTrace();
        }
    }

    /**
     * This is the class of the SUCCESS object.
     * Under normal use it should only be used for constructing this object.
     */
    class Pass implements ParseResult
    {
        @Override
        public boolean passed()
        {
            return true;
        }

        @Override
        public void addMessagesToLog(URI uri)
        {
            //No messages because we passed
        }

        @Override
        public void printMessages(URI uri)
        {
            //No messages because we passed
        }
    }

    /**
     * Simple class to handle feedback from parse operations that fail.
     */
    class Fail implements ParseResult
    {
        private final QueuedMessage error;

        public Fail(String error)
        {
            this.error = new QueuedMessage(Logging.LST_ERROR, error);
        }

        @Override
        public boolean passed()
        {
            return false;
        }

        public QueuedMessage getError()
        {
            return error;
        }

        @Override
        public void addMessagesToLog(URI uri)
        {
            Logging.addParseMessage(error.level, generateText(error, uri), error.stackTrace);
        }

        @Override
        public void printMessages(URI uri)
        {
            Logging.log(error.level, generateText(error, uri), error.stackTrace);
        }

        @Override
        public String toString()
        {
            return error.message;
        }
    }

    /**
     * Generate the text for a given QueuedMessage and URI, which is the text of the
     * QueuedMessed indicating the URI as the source of the message.
     *
     * @param message The QueuedMessage to be processed
     * @param uri     The URI indicating the source of the message
     * @return The text of the QueuedMessed indicating the URI as the source of the
     * message
     */
    static String generateText(QueuedMessage message, URI uri)
    {
        return message.message + " (Source: " + uri + " )";
    }

}
