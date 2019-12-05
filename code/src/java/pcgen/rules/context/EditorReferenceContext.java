/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.context;

import java.net.URI;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;

final class EditorReferenceContext extends RuntimeReferenceContext
{

    private final HashMapToList<CDOMObject, CDOMObject> copyMap = new HashMapToList<>();
    private final HashMapToList<CDOMObject, CDOMObject> modMap = new HashMapToList<>();
    private final HashMapToList<URI, Loadable> forgetMap = new HashMapToList<>();

    private EditorReferenceContext()
    {
    }

    @Override
    <T extends CDOMObject> T performCopy(T object, String copyName)
    {
        try
        {
            CDOMObject copy = object.getClass().newInstance();
            copyMap.addToListFor(object, copy);
        } catch (InstantiationException e)
        {
            throw new IllegalArgumentException(
                    "Class " + object.getClass().getName() + " must possess a zero-argument constructor", e);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(
                    "Class " + object.getClass().getName() + " must possess a public zero-argument constructor", e);
        }
        return null;
    }

    @Override
    public <T extends CDOMObject> T performMod(T object)
    {
        try
        {
            CDOMObject copy = object.getClass().newInstance();
            modMap.addToListFor(object, copy);
        } catch (InstantiationException e)
        {
            throw new IllegalArgumentException(
                    "Class " + object.getClass().getName() + " must possess a zero-argument constructor", e);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(
                    "Class " + object.getClass().getName() + " must possess a public zero-argument constructor", e);
        }
        return null;
    }

    @Override
    public <T extends Loadable> boolean forget(T obj)
    {
        /*
         * Don't want to call super. here as that only deals with abbreviations
         * and would actually delete the object out of the reference context
         * (bad!)
         */
        forgetMap.addToListFor(getSourceURI(), obj);
        return true;
    }

    public void purge(CDOMObject cdo)
    {
        super.forget(cdo);
    }

    /**
     * Return a new EditorReferenceContext. This ReferenceContext is initialized as per
     * the rules of AbstractReferenceContext.
     *
     * @return A new EditorReferenceContext
     */
    public static EditorReferenceContext createEditorReferenceContext()
    {
        EditorReferenceContext context = new EditorReferenceContext();
        context.initialize();
        return context;
    }
}
