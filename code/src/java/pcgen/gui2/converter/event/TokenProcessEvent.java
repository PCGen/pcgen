/*
 * Copyright (c) 2006, 2009.
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
 *
 */
package pcgen.gui2.converter.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.rules.context.EditorLoadContext;

public class TokenProcessEvent extends EventObject
{

    private final EditorLoadContext context;
    private final String key;
    private final String value;
    private final String objectName;
    private final CDOMObject obj;
    private final StringBuilder result = new StringBuilder();
    private boolean consumed = false;
    private List<CDOMObject> injected;
    private final ConversionDecider decider;

    public TokenProcessEvent(EditorLoadContext lc, ConversionDecider cd, String tokenName, String tokenValue,
            String name, CDOMObject object)
    {
        super(object);
        key = tokenName;
        value = tokenValue;
        obj = object;
        context = lc;
        objectName = name;
        decider = cd;
    }

    public void consume()
    {
        consumed = true;
    }

    public boolean isConsumed()
    {
        return consumed;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void append(CharSequence s)
    {
        result.append(s);
    }

    public void append(char c)
    {
        result.append(c);
    }

    public CDOMObject getPrimary()
    {
        return obj;
    }

    public String getResult()
    {
        return result.toString();
    }

    public EditorLoadContext getContext()
    {
        return context;
    }

    public ConversionDecider getDecider()
    {
        return decider;
    }

    public void inject(CDOMObject cdo)
    {
        if (injected == null)
        {
            injected = new ArrayList<>();
        }
        injected.add(cdo);
    }

    public List<CDOMObject> getInjected()
    {
        return injected == null ? null : new ArrayList<>(injected);
    }
}
