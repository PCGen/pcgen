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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedEvent;
import pcgen.cdom.reference.UnconstructedListener;
import pcgen.util.Logging;

public class TrackingReferenceContext extends RuntimeReferenceContext implements
		UnconstructedListener
{

	private final DoubleKeyMapToList<CDOMReference<?>, URI, String> track = new DoubleKeyMapToList<CDOMReference<?>, URI, String>(WeakHashMap.class, HashMap.class);

	@Override
	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMSingleRef<T> getCDOMReference(
			Class<T> c, Category<T> cat, String val)
	{
		CDOMSingleRef<T> ref = super.getCDOMReference(c, cat, val);
		trackReference(ref);
		return ref;
	}

	@Override
	public <T extends Loadable> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val)
	{
		CDOMSingleRef<T> ref = super.getCDOMReference(c, val);
		trackReference(ref);
		return ref;
	}

	@Override
	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMAllReference(
			Class<T> c, Category<T> cat)
	{
		CDOMGroupRef<T> ref = super.getCDOMAllReference(c, cat);
		trackReference(ref);
		return ref;
	}

	@Override
	public <T extends Loadable> CDOMGroupRef<T> getCDOMAllReference(Class<T> c)
	{
		CDOMGroupRef<T> ref = super.getCDOMAllReference(c);
		trackReference(ref);
		return ref;
	}

	@Override
	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, Category<T> cat, String... val)
	{
		CDOMGroupRef<T> ref = super.getCDOMTypeReference(c, cat, val);
		trackReference(ref);
		return ref;
	}

	@Override
	public <T extends Loadable> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, String... val)
	{
		CDOMGroupRef<T> ref = super.getCDOMTypeReference(c, val);
		trackReference(ref);
		return ref;
	}

	private final Set<ReferenceManufacturer<?>> listening = new HashSet<ReferenceManufacturer<?>>();

	@Override
	public <T extends Loadable & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		ReferenceManufacturer<T> mfg = super.getManufacturer(cl, cat);
		if (!listening.contains(mfg))
		{
			mfg.addUnconstructedListener(this);
			listening.add(mfg);
		}
		return new TrackingManufacturer<T>(this, mfg);
	}

	@Override
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl)
	{
		ReferenceManufacturer<T> mfg = super.getManufacturer(cl);
		if (!listening.contains(mfg))
		{
			mfg.addUnconstructedListener(this);
			listening.add(mfg);
		}
		return new TrackingManufacturer<T>(this, mfg);
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
			Set<String> tokenNames = new TreeSet<String>();
			for (String tok : tokens)
			{
				if (tok != null)
				{
					tokenNames.add(tok);
				}
			}
			Logging.errorPrint("  Was used in " + uri + " in tokens: "
					+ tokenNames);
		}
	}

	private String getSource()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		String source = null;
		for (int i = 0; i < stackTrace.length; i++)
		{
			String className = stackTrace[i].getClassName();
			if (className.startsWith("plugin.lsttokens"))
			{
				source = className;
				break;
			}
		}
		return source;
	}

	<T> void trackReference(CDOMReference<T> ref)
	{
		String src = getSource();
		if (src == null)
		{
			src = "";
		}
		track.addToListFor(ref, getSourceURI(), getSource());
	}

}
