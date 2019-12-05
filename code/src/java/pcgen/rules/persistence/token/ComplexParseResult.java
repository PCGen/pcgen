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

import static pcgen.rules.persistence.token.ParseResult.generateText;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import pcgen.util.Logging;

/**
 * Class that implements ParseResult for providing more complicated feedback.
 * See plugin.lsttokens.pcclass.ExchangeLevelToken.
 */
public class ComplexParseResult implements ParseResult
{
    private final List<QueuedMessage> queuedMessages = new LinkedList<>();

    public ComplexParseResult()
    {
        //Start an empty result
    }

    public ComplexParseResult(String error)
    {
        addErrorMessage(error);
    }

    public ComplexParseResult(ComplexParseResult toCopy)
    {
        addMessages(toCopy);
    }

    public void addErrorMessage(String msg)
    {
        addParseMessage(Logging.LST_ERROR, msg);
    }

    public void addWarningMessage(String msg)
    {
        addParseMessage(Logging.LST_WARNING, msg);
    }

    public void addInfoMessage(String msg)
    {
        addParseMessage(Logging.LST_INFO, msg);
    }

    protected void addParseMessage(Level lvl, String msg)
    {
        queuedMessages.add(new QueuedMessage(lvl, msg));
    }

    public void addMessages(ComplexParseResult pr)
    {
        queuedMessages.addAll(pr.queuedMessages);
    }

    @Override
    public void printMessages(URI uri)
    {
        for (QueuedMessage msg : queuedMessages)
        {
            Logging.log(msg.level, generateText(msg, uri), msg.stackTrace);
        }
    }

    @Override
    public void addMessagesToLog(URI uri)
    {
        for (QueuedMessage msg : queuedMessages)
        {
            Logging.addParseMessage(msg.level, generateText(msg, uri), msg.stackTrace);
        }
    }

    @Override
    public boolean passed()
    {
        for (QueuedMessage msg : queuedMessages)
        {
            if (msg.level == Logging.LST_ERROR)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Copy messages from another ParseResult object.
     *
     * @param pr The object to copy from.
     */
    public void copyMessages(ParseResult pr)
    {
        if (pr instanceof ComplexParseResult)
        {
            ComplexParseResult cpr = (ComplexParseResult) pr;
            queuedMessages.addAll(cpr.queuedMessages);
        } else if (pr instanceof ParseResult.Fail)
        {
            ParseResult.Fail fail = (ParseResult.Fail) pr;
            queuedMessages.add(fail.getError());
        }
    }
}
