/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedEvent;
import pcgen.cdom.reference.UnconstructedListener;
import pcgen.util.Logging;

public final class TrackingReferenceContext extends RuntimeReferenceContext implements UnconstructedListener
{

    private final DoubleKeyMapToList<CDOMReference<?>, URI, String> track =
            new DoubleKeyMapToList<>(WeakHashMap.class, HashMap.class);

    private final Set<ReferenceManufacturer<?>> listening = new HashSet<>();

    private TrackingReferenceContext()
    {
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(Class<T> cl)
    {
        ReferenceManufacturer<T> mfg = super.getManufacturer(cl);
        if (mfg instanceof TrackingManufacturer)
        {
            return mfg;
        }
        if (!listening.contains(mfg))
        {
            mfg.addUnconstructedListener(this);
            listening.add(mfg);
        }
        return new TrackingManufacturer<>(this, mfg);
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerFac(ManufacturableFactory<T> factory)
    {
        ReferenceManufacturer<T> mfg = super.getManufacturerFac(factory);
        if (mfg instanceof TrackingManufacturer)
        {
            return mfg;
        }
        if (!listening.contains(mfg))
        {
            mfg.addUnconstructedListener(this);
            listening.add(mfg);
        }
        return new TrackingManufacturer<>(this, mfg);
    }

    @Override
    public void unconstructedReferenceFound(UnconstructedEvent e)
    {
        CDOMReference<?> ref = e.getReference();
        Set<URI> uris = track.getSecondaryKeySet(ref);
        if (uris == null)
        {
            // Shouldn't happen, but this is reporting, not critical, so be safe
            return;
        }
        for (URI uri : uris)
        {
            List<String> tokens = track.getListFor(ref, uri);
            Set<String> tokenNames = new TreeSet<>();
            for (String tok : tokens)
            {
                if (tok != null)
                {
                    tokenNames.add(tok);
                }
            }
            Logging.errorPrint("  Was used in " + uri + " in tokens: " + tokenNames);
        }
    }

    private String getSource()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String source = null;
        for (StackTraceElement stackTraceElement : stackTrace)
        {
            String className = stackTraceElement.getClassName();
            if (className.startsWith("plugin.lsttokens"))
            {
                source = className;
                break;
            }
        }
        return source;
    }

    protected <T> void trackReference(CDOMReference<T> ref)
    {
        String src = getSource();
        if (src == null)
        {
            src = "?";
        }
        track.addToListFor(ref, getSourceURI(), src);
    }

    /**
     * Return a new TrackingReferenceContext. This ReferenceContext is initialized as per
     * the rules of AbstractReferenceContext.
     *
     * @return A new TrackingReferenceContext
     */
    public static TrackingReferenceContext createTrackingReferenceContext()
    {
        TrackingReferenceContext context = new TrackingReferenceContext();
        context.initialize();
        return context;
    }
}
