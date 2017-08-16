/*
 *  Copyright (C) 2003 Devon Jones, Emily Smirle
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package gmgen.gui;

import java.util.Enumeration;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.swing.undo.UndoableEdit;

/**
 * {@code ExtendedHTMLDocument} is used by Swing for improved HTML
 * rendering over the standard {@code HTMLDocument}.  Hence, it contains
 * methods <em>never called by PCGen</em>, so code analysis tools will flag
 * methods as unused.  This is fine.  Do not remove or deprecate them.
 */
@SuppressWarnings("unused")
public class ExtendedHTMLDocument extends HTMLDocument {
	private static final Element[] EMPTY_ELEMENT_ARRAY = new Element[0];

	/**
	 * Constructs a new, default {@code ExtendedHTMLDocument}.  Used by
	 * Swing.
	 *
	 * @see HTMLDocument#HTMLDocument()
	 */
	public ExtendedHTMLDocument() {
		// Constructor
	}

	/**
	 * Constructs a new {@code ExtendedHTMLDocument} with the given
	 * <var>content</var> and <var>style</var>.  Used by Swing.
	 *
	 * @param content the document contents
	 * @param styles the stylesheet
	 *
	 * @see HTMLDocument#HTMLDocument(Content, StyleSheet)
	 */
	public ExtendedHTMLDocument(Content content, StyleSheet styles) {
		super(content, styles);
	}

	/**
	 * Constructs a new {@code ExtendedHTMLDocument} with the given
	 * <var>styles</var>.  Used by Swing.
	 *
	 * @param styles the stylesheet
	 *
	 * @see HTMLDocument#HTMLDocument(StyleSheet)
	 */
	public ExtendedHTMLDocument(StyleSheet styles) {
		super(styles);
	}

	/**
	 * Removes elements.  Used by Swing.
	 *
	 * @param e the element to remove
	 * @param index the element position
	 * @param count how many to remove
	 *
	 * @throws BadLocationException if there are not elements enough
	 *
	 * @see Content#remove(int, int)
	 */
	public void removeElements(Element e, int index, int count)
			throws BadLocationException {
		writeLock();

		int start = e.getElement(index).getStartOffset();
		int end = e.getElement((index + count) - 1).getEndOffset();

		try {
			Element[] removed = new Element[count];
			Element[] added = EMPTY_ELEMENT_ARRAY;

			for (int counter = 0; counter < count; counter++) {
				removed[counter] = e.getElement(counter + index);
			}

			DefaultDocumentEvent dde= new DefaultDocumentEvent(
					start, end - start, EventType.REMOVE);
			((AbstractDocument.BranchElement) e).replace(
					index, removed.length, added);
			dde.addEdit(new ElementEdit(e, index, removed, added));

			UndoableEdit u = getContent().remove(start, end - start);

			if (u != null) {
				dde.addEdit(u);
			}

			postRemoveUpdate(dde);
			dde.end();
			fireRemoveUpdate(dde);

			if (u != null) {
				fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
			}
		} finally {
			writeUnlock();
		}
	}

	/**
	 * Replaces attributes on a tag.  Used by Swing.
	 *
	 * @param e the element to edit
	 * @param a the attributes to change
	 * @param tag the tag to edit
	 */
	public void replaceAttributes(Element e, AttributeSet a, Tag tag) {
		writeLock();
		if ((e != null) && (a != null)) {
			try {
				int start = e.getStartOffset();
				DefaultDocumentEvent changes = new DefaultDocumentEvent(
						start, e.getEndOffset() - start, EventType.CHANGE);
				AttributeSet sCopy = a.copyAttributes();
				changes.addEdit(new AttributeUndoableEdit(e, sCopy, false));

				MutableAttributeSet attr
						= (MutableAttributeSet) e.getAttributes();
				Enumeration<?> aNames = attr.getAttributeNames();

				while (aNames.hasMoreElements()) {
					Object aName = aNames.nextElement();
					Object value = attr.getAttribute(aName);

					if ((value != null) && !value.toString()
							.equalsIgnoreCase(tag.toString())) {
						attr.removeAttribute(aName);
					}
				}

				attr.addAttributes(a);
				changes.end();
				fireChangedUpdate(changes);
				fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
			} finally {
				writeUnlock();
			}
		}
	}
}
