
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.event.KeyEvent;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.AbstractButton;
import javax.swing.JLabel;


/**
 * An internal helper class that configures the text and mnemonic 
 * properties for instances of AbstractButton, JLabel, and 
 * javax.swing.Action.  It's used like this:
 * <pre>
 * MnemonicText.configure(myButton, "Save &As")
 * </pre>
 * The configure method unconditionally sets three properties on the 
 * target object: 
 * <ul>
 * <li>the label text, "Save As" 
 * <li>the mnemonic key code, VK_A
 * <li>the index of the mnemonic character, 5
 * </ul>
 * If the mnemonic marker character isn't present, then the second
 * two properties are cleared to VK_UNDEFINED (0) and -1 respectively.
 * <p>
 */
class MnemonicText {
    private MnemonicText() { } // not used

    public static void configure(Object target, String markedText) {
        String text = markedText;
        int mnemonicIndex = -1;
        int mnemonicKey = KeyEvent.VK_UNDEFINED;
        // TBD: mnemonic marker char should be an application resource
        int markerIndex = mnemonicMarkerIndex(markedText, '&');
        if (markerIndex == -1) {
            markerIndex = mnemonicMarkerIndex(markedText, '_');
        }
        if (markerIndex != -1) {
            text = text.substring(0, markerIndex) + text.substring(markerIndex + 1);
            mnemonicIndex = markerIndex;
            CharacterIterator sci = new StringCharacterIterator(markedText, markerIndex);
            mnemonicKey = mnemonicKey(sci.next());
        }
        if (target instanceof javax.swing.Action) {
            configureAction((javax.swing.Action)target, text, mnemonicKey, mnemonicIndex);
        }
        else if (target instanceof AbstractButton) {
            configureButton((AbstractButton)target, text, mnemonicKey, mnemonicIndex);
        }
        else if (target instanceof JLabel) {
            configureLabel((JLabel)target, text, mnemonicKey, mnemonicIndex);
        }
        else {
            throw new IllegalArgumentException("unrecognized target type " + target);
        }
    }

    private static int mnemonicMarkerIndex(String s, char marker) {
        if ((s == null) || (s.length() < 2)) { return -1; }
        CharacterIterator sci = new StringCharacterIterator(s);  
        int i = 0;
        while(i != -1) {
            i = s.indexOf(marker, i);
            if (i != -1) {
                sci.setIndex(i);
                char c1 = sci.previous();
                sci.setIndex(i);
                char c2 = sci.next();
                boolean isQuote = (c1 == '\'') && (c2 == '\'');
                boolean isSpace = Character.isWhitespace(c2);
                if (!isQuote && !isSpace && (c2 != sci.DONE)) {
                    return i;
                }
            }
            if (i != -1) { i += 1; }
        }
        return -1;
    }

    /* A general purpose way to map from a char to a KeyCode is needed.  An 
     * AWT RFE has been filed: 
     * http://bt2ws.central.sun.com/CrPrint?id=6559449
     * CR 6559449 java/classes_awt Support for converting from char to KeyEvent VK_ keycode
     */
    private static int mnemonicKey(char c) {
        int vk = (int)c;
        if ((vk >= 'a') && (vk <='z')) {
            vk -= ('a' - 'A');
        }
        return vk;
    }

    /* This javax.swing.Action constants is only 
     * defined in Mustang (1.6), see 
     * http://download.java.net/jdk6/docs/api/javax/swing/Action.html
     */
    private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";

    private static void configureAction(javax.swing.Action target, String text, int key, int index) {
        target.putValue(javax.swing.Action.NAME, text);
        if (key != KeyEvent.VK_UNDEFINED) { target.putValue(javax.swing.Action.MNEMONIC_KEY, key); }
        if (index != -1) { target.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, index); }
    }

    private static void configureButton(AbstractButton target, String text, int key, int index) {
        target.setText(text);
        if (key != KeyEvent.VK_UNDEFINED) { target.setMnemonic(key); }
        if (index != -1) { target.setDisplayedMnemonicIndex(index); }
    }

    private static void configureLabel(JLabel target, String text, int key, int index) {
        target.setText(text);
        if (key != KeyEvent.VK_UNDEFINED) { target.setDisplayedMnemonic(key); }
        if (index != -1) { target.setDisplayedMnemonicIndex(index); }
    }
}
