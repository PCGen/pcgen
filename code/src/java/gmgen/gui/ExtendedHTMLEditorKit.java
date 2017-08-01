/*
 GNU Lesser General Public License
 ExtendedHTMLEditorKit
 Copyright (C) 2001-2002  Frits Jalvingh & Howard Kistler
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gmgen.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;

import org.jetbrains.annotations.NotNull;

/**
 * This class extends HTMLEditorKit so that it can provide other renderer
 * classes instead of the defaults. Most important is the part which renders
 * relative image paths.
 */
public class ExtendedHTMLEditorKit extends HTMLEditorKit {

    /**
     * Get the HTML tag
     * @param e
     * @return HTML.Tag
     */
    private static HTML.Tag getHTMLTag(Element e) {
        //Set List of tags
        Map<String, HTML.Tag> tags = new HashMap<>();
        HTML.Tag[] tagList = HTML.getAllTags();

        for (final HTML.Tag aTagList : tagList)
        {
            tags.put(aTagList.toString(), aTagList);
        }

        //Get Tag
        if (tags.containsKey(e.getName())) {
            return tags.get(e.getName());
        }
        return null;
    }

    /**
     * Get the parent of the list item
     * @param eleSearch
     * @return the parent of the list item
     */
    public static Element getListItemParent(Element eleSearch) {
        String listItemTag = HTML.Tag.LI.toString();

        Element workingElement = eleSearch;
        do {
            if (listItemTag.equals(workingElement.getName())) {
                return workingElement;
            }

            workingElement = workingElement.getParentElement();
        } while (!workingElement.getName().equals(HTML.Tag.HTML.toString()));

        return null;
    }

    /**
     * Get the unique string
     * @param source
     * @return the unique string
     */
    @NotNull
    private static String[] getUniqueString(String source) {
        String[] result = new String[2];

        for (int i = 0; i < 2; i++) {
            boolean hit;
            String idString;
            int counter = 0;

            do {
                hit = false;
                idString = "diesisteineidzumsuchen" + counter + '#' + i;

                if (source.contains(idString)) {
                    counter++;
                    hit = true;

                    if (counter > 10000) { return null; }
                }
            } while (hit);

            result[i] = idString;
        }

        return result;
    }

    /**
     * Method for returning a ViewFactory which handles the image rendering.
     * @return ViewFactory
     */
    @Override
    public ViewFactory getViewFactory() {
        return new HTMLFactoryExtended();
    }

    /**
     * Check the parents tag
     * @param e
     * @param tag
     * @return true if the tag equals the element
     */
    public static boolean checkParentsTag(Element e, HTML.Tag tag) {
        if (e.getName().equalsIgnoreCase(tag.toString()))
        {
            return true;
        }

        Element workingElement = e;
        do {
            workingElement = workingElement.getParentElement();
            if (workingElement.getName().equalsIgnoreCase(tag.toString()))
            {
                return true;
            }
        } while (!workingElement.getName().equalsIgnoreCase("html"));

        return false;
    }

    @Override
    public Document createDefaultDocument() {
        StyleSheet styles = getStyleSheet();
        StyleSheet ss = new StyleSheet();
        ss.addStyleSheet(styles);

        HTMLDocument doc = new ExtendedHTMLDocument(ss);
        doc.setParser(getParser());
        doc.setAsynchronousLoadPriority(4);
        doc.setTokenThreshold(100);

        return doc;
    }

    /**
     * Delete
     * @param pane
     * @throws BadLocationException
     */
    public static void delete(JTextPane pane) throws BadLocationException {
        Document htmlDoc = (ExtendedHTMLDocument) pane.getStyledDocument();
        int selStart = pane.getSelectionStart();
        int selEnd = pane.getSelectionEnd();
        String[] posStrings = getUniqueString(pane.getText());

        htmlDoc.insertString(selStart, posStrings[0], null);
        htmlDoc.insertString(selEnd + posStrings[0].length(), posStrings[1], null);

        int start = pane.getText().indexOf(posStrings[0]);
        int end = pane.getText().indexOf(posStrings[1]);

        if ((start == -1) || (end == -1)) { return; }

        String htmlString = pane.getText().substring(0, start);
        htmlString += pane.getText().substring(start + posStrings[0].length(), end);
        htmlString += pane.getText().substring(end + posStrings[1].length(), pane.getText().length());

        String source = htmlString;
        end -= posStrings[0].length();
        htmlString = source.substring(0, start);
        htmlString += getAllTableTags(source.substring(start, end));
        htmlString += source.substring(end, source.length());
        pane.setText(htmlString);
    }

    /**
     * Insert a list element
     * @param pane
     * @param content
     */
    public static void insertListElement(JTextPane pane, String content) {
        int pos = pane.getCaretPosition();
        StyledDocument htmlDoc = (ExtendedHTMLDocument) pane.getStyledDocument();
        String source = pane.getText();
        boolean hit;
        String idString;
        int counter = 0;

        do {
            hit = false;
            idString = "diesisteineidzumsuchenimsource" + counter;

            if (source.contains(idString)) {
                counter++;
                hit = true;

                if (counter > 10000) { return; }
            }
        } while (hit);

        Element element = getListItemParent(htmlDoc.getCharacterElement(pane.getCaretPosition()));

        if (element == null) { return; }

        SimpleAttributeSet sa = new SimpleAttributeSet(element.getAttributes());
        sa.addAttribute("id", idString);
        ((ExtendedHTMLDocument) pane.getStyledDocument()).replaceAttributes(element, sa, HTML.Tag.LI);
        source = pane.getText();

        StringBuilder newHtmlString = new StringBuilder();
        int[] positions = getPositions(element, source, true, idString);
        newHtmlString.append(source.substring(0, positions[3]));
        newHtmlString.append("<li>");
        newHtmlString.append(content);
        newHtmlString.append("</li>");
        newHtmlString.append(source.substring(positions[3] + 1, source.length()));
        pane.setText(newHtmlString.toString());
        pane.setCaretPosition(pos - 1);
        element = getListItemParent(htmlDoc.getCharacterElement(pane.getCaretPosition()));
        sa = new SimpleAttributeSet(element.getAttributes());
        sa = removeAttributeByKey(sa);
        ((ExtendedHTMLDocument) pane.getStyledDocument()).replaceAttributes(element, sa, HTML.Tag.LI);
    }

    /**
     * Remove arttribute
     * @param sourceAS
     * @param removeAS
     * @return the attribute set
     */
    private static SimpleAttributeSet removeAttribute(AttributeSet sourceAS, AttributeSet removeAS) {
        try {
            String[] sourceKeys = new String[sourceAS.getAttributeCount()];
            String[] sourceValues = new String[sourceAS.getAttributeCount()];
            Enumeration<?> sourceEn = sourceAS.getAttributeNames();
            int i = 0;

            while (sourceEn.hasMoreElements()) {
                Object temp;
                temp = sourceEn.nextElement();
                sourceKeys[i] = temp.toString();
                sourceValues[i] = "";
                sourceValues[i] = sourceAS.getAttribute(temp).toString();
                i++;
            }

            String[] removeKeys = new String[removeAS.getAttributeCount()];
            String[] removeValues = new String[removeAS.getAttributeCount()];
            Enumeration<?> removeEn = removeAS.getAttributeNames();
            int j = 0;

            while (removeEn.hasMoreElements()) {
                removeKeys[j] = removeEn.nextElement().toString();
                removeValues[j] = removeAS.getAttribute(removeKeys[j]).toString();
                j++;
            }

            SimpleAttributeSet result = new SimpleAttributeSet();

            for (int countSource = 0; countSource < sourceKeys.length; countSource++) {
                boolean hit = false;

                if ("name".equals(sourceKeys[countSource]) || "resolver".equals(sourceKeys[countSource])) {
                    hit = true;
                } else {
                    for (int countRemove = 0; countRemove < removeKeys.length; countRemove++) {
                        if (!"NULL".equals(removeKeys[countRemove])) {
                            if (sourceKeys[countSource].equals(removeKeys[countRemove])) {
                                if (!"NULL".equals(removeValues[countRemove])) {
                                    if (sourceValues[countSource].equals(removeValues[countRemove])) {
                                        hit = true;
                                    }
                                } else if ("NULL".equals(removeValues[countRemove])) {
                                    hit = true;
                                }
                            }
                        } else if ("NULL".equals(removeKeys[countRemove]) && sourceValues[countSource].equals(removeValues[countRemove])) {
                            hit = true;
                        }
                    }
                }

                if (!hit) {
                    result.addAttribute(sourceKeys[countSource], sourceValues[countSource]);
                }
            }

            return result;
        } catch (ClassCastException cce) {
            return null;
        }
    }

    /**
     * Remove attribute by key
     * @param sourceAS
     * @return attribute set
     */
    private static SimpleAttributeSet removeAttributeByKey(SimpleAttributeSet sourceAS) {
        SimpleAttributeSet temp = new SimpleAttributeSet();
        temp.addAttribute("id", "NULL");

        return removeAttribute(sourceAS, temp);
    }

    /**
     * Remove a tag
     * @param pane
     * @param element
     */
    public static void removeTag(JTextPane pane, Element element) {
        if (element == null) { return; }

        HTML.Tag tag = getHTMLTag(element);

        // Versieht den Tag mit einer einmaligen ID
        String source = pane.getText();
        boolean hit;
        String idString;
        int counter = 0;

        do {
            hit = false;
            idString = "diesisteineidzumsuchenimsource" + counter;

            if (source.contains(idString)) {
                counter++;
                hit = true;

                if (counter > 10000) { return; }
            }
        } while (hit);

        MutableAttributeSet sa = new SimpleAttributeSet(element.getAttributes());
        sa.addAttribute("id", idString);
        ((ExtendedHTMLDocument) pane.getStyledDocument()).replaceAttributes(element, sa, tag);
        source = pane.getText();

        StringBuilder newHtmlString = new StringBuilder();
        int[] position = getPositions(element, source, true, idString);

        if (position == null) { return; }

        for (final int aPosition : position)
        {
            if (aPosition < 0) { return; }
        }

        int beginStartTag = position[0];
        int endStartTag = position[1];

        //if (true) {
            int beginEndTag = position[2];
            int endEndTag = position[3];
            newHtmlString.append(source.substring(0, beginStartTag));
            newHtmlString.append(source.substring(endStartTag, beginEndTag));
            newHtmlString.append(source.substring(endEndTag, source.length()));
        //} else {
        //    newHtmlString.append(source.substring(0, beginStartTag));
        //    newHtmlString.append(source.substring(endStartTag, source.length()));
        //}

        pane.setText(newHtmlString.toString());
    }

    private static String getAllTableTags(String source) {
        StringBuilder result = new StringBuilder();
        int caret = -1;

        do {
            caret++;

            int[] tableCarets = new int[6];
            tableCarets[0] = source.indexOf("<table", caret);
            tableCarets[1] = source.indexOf("<tr", caret);
            tableCarets[2] = source.indexOf("<td", caret);
            tableCarets[3] = source.indexOf("</table", caret);
            tableCarets[4] = source.indexOf("</tr", caret);
            tableCarets[5] = source.indexOf("</td", caret);
            java.util.Arrays.sort(tableCarets);
            caret = -1;

            for (final int tableCaret : tableCarets)
            {
                if (tableCaret >= 0)
                {
                    caret = tableCaret;

                    break;
                }
            }

            if (caret != -1) {
                result.append(source.substring(caret, source.indexOf('>', caret) + 1));
            }
        } while (caret != -1);

        return result.toString();
    }

    private static int[] getPositions(Element element, String source, boolean closingTag, String idString) {
        HTML.Tag tag = getHTMLTag(element);
        int[] position = new int[4];

        for (int i = 0; i < position.length; i++) {
            position[i] = -1;
        }

        String searchString = "<" + tag;
        int caret;

        if ((caret = source.indexOf(idString)) != -1) {
            position[0] = source.lastIndexOf('<', caret);
            position[1] = source.indexOf('>', caret) + 1;
        }

        if (closingTag) {
            String searchEndTagString = "</" + tag + '>';
            int beginEndTag;
            int endEndTag;
            caret = position[1];

            boolean end;
            beginEndTag = source.indexOf(searchEndTagString, caret);
            endEndTag = beginEndTag + searchEndTagString.length();

            int interncaret = position[1];

            do {
                boolean flaghitup;
                int hitUp = 0;

                do {
                    flaghitup = false;
                    int temphitpoint = source.indexOf(searchString, interncaret);

                    if ((temphitpoint > 0) && (temphitpoint < beginEndTag)) {
                        hitUp++;
                        flaghitup = true;
                        interncaret = temphitpoint + searchString.length();
                    }
                } while (flaghitup);

                if (hitUp == 0) {
                    end = true;
                } else {
                    for (int i = 1; i <= hitUp; i++) {
                        caret = endEndTag;
                        beginEndTag = source.indexOf(searchEndTagString, caret);
                        endEndTag = beginEndTag + searchEndTagString.length();
                    }

                    end = false;
                }
            } while (!end);

            if ((beginEndTag < 0) || (endEndTag < 0)) { return null; }

            position[2] = beginEndTag;
            position[3] = endEndTag;
        }

        return position;
    }

    /* Inner Classes --------------------------------------------- */

    /**
     * Class that replaces the default ViewFactory and supports the proper
     * rendering of both URL-based and local images.
     */
    private static class HTMLFactoryExtended extends HTMLFactory
    {

        /**
         * Method to handle IMG tags and invoke the image loader.
         * @param elem
         * @return View
         */
        @Override
        public View create(Element elem) {
            Object obj = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);

            if (obj instanceof HTML.Tag) {
                HTML.Tag tagType = (HTML.Tag) obj;
                if (tagType == HTML.Tag.IMG) {
                    return new ImageView(elem);
                }
            }
            return super.create(elem);
        }
    }

    /**
     * InsertListAction 
     */
    public static class InsertListAction extends InsertHTMLTextAction {

        private final HTML.Tag baseTag;

        /**
         * Action to insert a list
         * @param label
         * @param listType
         */
        public InsertListAction(String label, HTML.Tag listType) {
            super(label, "", listType, HTML.Tag.LI);
            baseTag = listType;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                JEditorPane editor = getEditor(ae);
                HTMLDocument doc = (ExtendedHTMLDocument) editor.getDocument();
                String selTextBase = editor.getSelectedText();
                Element elem = doc.getParagraphElement(editor.getCaretPosition());
                int textLength = -1;

                if (selTextBase != null) {
                    textLength = selTextBase.length();
                }

                if ((selTextBase == null) || (textLength < 1)) {
                    if (!"newListPoint".equals(ae.getActionCommand())) {
                        if (checkParentsTag(elem, HTML.Tag.OL) || checkParentsTag(elem, HTML.Tag.UL)) {
                            //Can't have a multilevel list
                            return;
                        }
                    }

                    String sListType = ((baseTag == HTML.Tag.OL) ? "ol" : "ul");
                    StringBuilder sbNew = new StringBuilder();

                    if (checkParentsTag(elem, baseTag)) {
                        sbNew.append("<li></li>");
                        insertHTML(editor, doc, editor.getCaretPosition(), sbNew.toString(), 0, 0, HTML.Tag.LI);
                    } else {
                        sbNew.append('<').append(sListType).append("><li></li></").append(sListType).append("><p>&nbsp;</p>");
                        insertHTML(editor, doc, editor.getCaretPosition(), sbNew.toString(), 0, 0,
                                (sListType.equals("ol") ? HTML.Tag.OL : HTML.Tag.UL));
                    }
                } else {
                    String sListType = ((baseTag == HTML.Tag.OL) ? "ol" : "ul");
                    HTMLDocument htmlDoc = (HTMLDocument) (editor.getDocument());
                    int iStart = editor.getSelectionStart();
                    int iEnd = editor.getSelectionEnd();
                    String selText = htmlDoc.getText(iStart, iEnd - iStart);
                    StringBuilder sbNew = new StringBuilder();
                    String sToken = ((selText.contains("\r")) ? "\r" : "\n");
                    StringTokenizer stTokenizer = new StringTokenizer(selText, sToken);
                    sbNew.append('<').append(sListType).append('>');

                    while (stTokenizer.hasMoreTokens()) {
                        sbNew.append("<li>");
                        sbNew.append(stTokenizer.nextToken());
                        sbNew.append("</li>");
                    }
                    sbNew.append("</").append(sListType).append("><p>&nbsp;</p>");
                    htmlDoc.remove(iStart, iEnd - iStart);
                    insertHTML(editor, htmlDoc, iStart, sbNew.toString(), 1, 1, null);
                }

                //Refresh
            } catch (BadLocationException ble) {
                // TODO - Handle Exception
            }
        }
    }

    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), ExtendedHTMLEditorKit.defaultActions);
    }
    
    private static final Action[] defaultActions = {new PasteAction() };

    /**
     * PasteAction
     */
    private static final class PasteAction extends TextAction {

        /** Create this object with the appropriate identifier. */
        private PasteAction() {
            super(DefaultEditorKit.pasteAction);
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param e
         *          the action event
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            Clipboard clipboard = target.getToolkit().getSystemClipboard();
            clipboard.getContents(null);
            Class<? extends JTextComponent> k = target.getClass();
            try {
                BeanInfo bi = Introspector.getBeanInfo(k);
                bi.getPropertyDescriptors();
            } catch (final IntrospectionException ex) {
                // TODO Handle this?
            }
            target.paste();
        }
    }
}
