/*
 * MappedList.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 * Created on Aug 27, 2016, 1:22:00 PM
 */
package pcgen.gui3.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class MappedList<E, F> extends TransformationList<E, F> {

    private final Function<F, E> mapper;

    public MappedList(ObservableList<? extends F> source, Function<F, E> mapper) {
        super(source);
        this.mapper = mapper;
    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }

    @Override
    public E get(int index) {
        return mapper.apply(getSource().get(index));
    }

    @Override
    public int size() {
        return getSource().size();
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends F> c) {
        fireChange(new ListChangeListener.Change<E>(this) {

            @Override
            public boolean wasAdded() {
                return c.wasAdded();
            }

            @Override
            public boolean wasRemoved() {
                return c.wasRemoved();
            }

            @Override
            public boolean wasReplaced() {
                return c.wasReplaced();
            }

            @Override
            public boolean wasUpdated() {
                return c.wasUpdated();
            }

            @Override
            public boolean wasPermutated() {
                return c.wasPermutated();
            }

            @Override
            public int getPermutation(int i) {
                return c.getPermutation(i);
            }

            @Override
            protected int[] getPermutation() {
                // This method is only called by the superclass methods
                // wasPermutated() and getPermutation(int), which are
                // both overriden by this class. There is no other way
                // this method can be called.
                throw new AssertionError("Unreachable code");
            }

            @Override
            public List<E> getRemoved() {
                ArrayList<E> res = new ArrayList<>(c.getRemovedSize());
                for (F e : c.getRemoved()) {
                    res.add(mapper.apply(e));
                }
                return res;
            }

            @Override
            public int getFrom() {
                return c.getFrom();
            }

            @Override
            public int getTo() {
                return c.getTo();
            }

            @Override
            public boolean next() {
                return c.next();
            }

            @Override
            public void reset() {
                c.reset();
            }
        });
    }

}
