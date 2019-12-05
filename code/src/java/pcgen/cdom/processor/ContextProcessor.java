/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.processor;

import java.util.Objects;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.Processor;
import pcgen.util.StringPClassUtil;

/**
 * A ContextProcessor is a Processor that has the ability to wrap another Processor
 * in order to conditionally apply the underlying Processor only in a given
 * context.
 *
 * @param <T> The class of object this ContextProcessor acts upon.
 * @param <R> The class of objects which provide the context in which this
 *            ContextProcessor acts
 */
public class ContextProcessor<T, R extends PrereqObject> implements Processor<T>
{
    /**
     * The underlying Processor that this ContextModifer will apply when the
     * given context is matched.
     */
    private final Processor<T> processor;

    /**
     * A Reference which contains the objects in the context in which the
     * underlying Processor should be applied.
     */
    private final CDOMReference<R> contextItems;

    /**
     * Constructs a new ContextProcessor that will conditionally apply the given
     * Processor only when objects in the given CDOMReference are provided as the
     * context of the modification.
     *
     * @param mod        The underlying Processor that this ContextModifer will apply
     *                   when the given context is matched.
     * @param contextRef The CDOMReference which contains the objects for which the
     *                   underlying Processor should be applied.
     * @throws IllegalArgumentException if the given Processor or the given CDOMReference is null
     */
    public ContextProcessor(Processor<T> mod, CDOMReference<R> contextRef)
    {
        Objects.requireNonNull(mod, "Processor in ContextProcessor cannot be null");
        Objects.requireNonNull(contextRef, "Context in ContextProcessor cannot be null");
        processor = mod;
        contextItems = contextRef;
    }

    /**
     * Conditionally applies the underlying Processor to the given input. Will
     * only be applied if the object given as the context object is contained
     * within the CDOMReference provided during construction of this
     * ContextProcessor.
     * <p>
     * Note this method may return the object passed in as the input object. The
     * behavior of ContextProcessor will depend on the behavior of the underlying
     * Processor. Therefore, if the input object is mutable, the caller of the
     * applyProcessor method should be aware of that behavior, and should treat
     * the returned object appropriately.
     *
     * @param obj     The input object this ContextProcessor will act upon
     * @param context The context of this ContextProcessor, to establish whether this
     *                Processor should act upon the input object
     * @return The modified object, of the same class as the input object.
     */
    @Override
    public T applyProcessor(T obj, Object context)
    {
        @SuppressWarnings("unchecked")
        boolean contained = context instanceof PrereqObject && contextItems.contains((R) context);
        if (contained)
        {
            return processor.applyProcessor(obj, context);
        } else
        {
            return obj;
        }
    }

    /**
     * Returns a representation of this ContextProcessor, suitable for storing in
     * an LST file.
     *
     * @return A representation of this ContextProcessor, suitable for storing in
     * an LST file.
     */
    @Override
    public String getLSTformat()
    {
        String contextString = contextItems.getLSTformat(false);
        StringBuilder sb = new StringBuilder();
        sb.append(processor.getLSTformat()).append('|');
        sb.append(StringPClassUtil.getStringFor(contextItems.getReferenceClass()));
        sb.append(contextString.indexOf('=') == -1 ? '=' : '.');
        sb.append(contextString);
        return sb.toString();
    }

    /**
     * The class of object this ContextProcessor acts upon (matches the modified
     * class of the underlying Processor).
     *
     * @return The class of object this ContextProcessor acts upon
     */
    @Override
    public Class<T> getModifiedClass()
    {
        return processor.getModifiedClass();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ContextProcessor)
        {
            ContextProcessor<?, ?> other = (ContextProcessor<?, ?>) obj;
            return processor.equals(other.processor) && contextItems.equals(other.contextItems);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return processor.hashCode() * 31 - contextItems.hashCode();
    }

}
