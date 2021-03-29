/*
 * @(#)PaletteColorChooserUI.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.security.*;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import org.jhotdraw.gui.plaf.palette.colorchooser.PaletteColorChooserMainPanel;
import org.jhotdraw.gui.plaf.palette.colorchooser.PaletteColorChooserPreviewPanel;

/**
 * PaletteColorChooserUI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaletteColorChooserUI extends ColorChooserUI {

    protected PaletteColorChooserMainPanel mainPanel;
    protected JColorChooser chooser;
    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;
    protected AbstractColorChooserPanel[] defaultChoosers;
    protected JComponent previewPanel;
    private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
    private MouseListener previewMouseListener;

    public static ComponentUI createUI(JComponent c) {
        return new PaletteColorChooserUI();
    }

    @Override
    public void installUI(JComponent c) {
        chooser = (JColorChooser) c;
        AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
        installDefaults();
        chooser.setLayout(new BorderLayout());
        mainPanel = new PaletteColorChooserMainPanel();
        chooser.add(mainPanel);
        defaultChoosers = createDefaultChoosers();
        chooser.setChooserPanels(defaultChoosers);
        installPreviewPanel();
        AbstractColorChooserPanel[] newPanels = chooser.getChooserPanels();
        updateColorChooserPanels(oldPanels, newPanels);
        // Note: install listeners only after we have fully installed
        //       all chooser panels. If we do it earlier, we send property
        //       events too early.
        installListeners();
        chooser.applyComponentOrientation(c.getComponentOrientation());
    }

    private void display (String defaultChooserName) {
    	Logger.getLogger("PaletteColorChooserUI warning: unable to instantiate " + defaultChooserName);
    }
    
    /**
     * creates the editor panel default chooser value 
     * @return an array of AbstractColorChooserPanel  
     */
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        String[] defaultChooserNames = (String[]) PaletteLookAndFeel.getInstance().get("ColorChooser.defaultChoosers");
        ArrayList<AbstractColorChooserPanel> panels = new ArrayList<>(defaultChooserNames.length);
        for (String defaultChooserName : defaultChooserNames) {
            try {
                panels.add((AbstractColorChooserPanel) Class.forName(defaultChooserName).getDeclaredConstructor().newInstance());
            } catch (AccessControlException e) {
            	display(defaultChooserName);
                e.printStackTrace();
            } catch (Exception e) {
                display(defaultChooserName);
                e.printStackTrace();
            }
        }
        return panels.toArray(new AbstractColorChooserPanel[panels.size()]);
    }

    @Override
    /**
     * uninstall the editor interface 
     * @param c JComponent 
     */
    public void uninstallUI(JComponent c) {
        chooser.remove(mainPanel);
        uninstallListeners();
        uninstallDefaultChoosers();
        uninstallDefaults();
        mainPanel.setPreviewPanel(null);
        if (previewPanel instanceof UIResource) {
            chooser.setPreviewPanel(null);
        }
        mainPanel = null;
        previewPanel = null;
        defaultChoosers = null;
        chooser = null;
    }
    /**
     * resets the editor interface to default values 
     */
    protected void installDefaults() {
        PaletteLookAndFeel.installColorsAndFont(chooser, "ColorChooser.background",
                "ColorChooser.foreground",
                "ColorChooser.font");
        TransferHandler th = chooser.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            chooser.setTransferHandler(defaultTransferHandler);
        }
    }
    
    protected void uninstallDefaults() {
        if (chooser.getTransferHandler() instanceof UIResource) {
            chooser.setTransferHandler(null);
        }
    }
    /**
     * adds listeners to the editor 
     */
    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        chooser.addPropertyChangeListener(propertyChangeListener);
        previewListener = new PreviewListener();
        chooser.getSelectionModel().addChangeListener(previewListener);
        previewMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (chooser.getDragEnabled()) {
                    TransferHandler th = chooser.getTransferHandler();
                    th.exportAsDrag(chooser, e, TransferHandler.COPY);
                }
            }
        };
    }
    /**
     * remove editor's listeners 
     */
    protected void uninstallListeners() {
        chooser.removePropertyChangeListener(propertyChangeListener);
        chooser.getSelectionModel().removeChangeListener(previewListener);
        previewPanel.removeMouseListener(previewMouseListener);
    }
    /**
     * 
     * @return PropertyChangeListener a listener to property 
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }
    /**
     * adds default preview panel to the editor 
     */
    protected void installPreviewPanel() {
        if (previewPanel != null) {
            previewPanel.removeMouseListener(previewMouseListener);
            mainPanel.setPreviewPanel(null);
        }
        previewPanel = chooser.getPreviewPanel();
        if ((previewPanel != null) && (mainPanel != null)
                && (previewPanel.getSize().getHeight() + previewPanel.getSize().getWidth() == 0)) {
            mainPanel.setPreviewPanel(null);
            return;
        }
        if (previewPanel == null || previewPanel instanceof UIResource) {
            previewPanel = new PaletteColorChooserPreviewPanel();
            chooser.setPreviewPanel(previewPanel);
        }
        previewPanel.setForeground(chooser.getColor());
        mainPanel.setPreviewPanel(previewPanel);
        previewPanel.addMouseListener(previewMouseListener);
    }

    class PreviewListener implements ChangeListener {

        @Override
        /**
         * informs listeners about a state change
         * @param e a change event 
         */
        public void stateChanged(ChangeEvent e) {
            ColorSelectionModel model = (ColorSelectionModel) e.getSource();
            if (previewPanel != null) {
                previewPanel.setForeground(model.getSelectedColor());
                previewPanel.repaint();
            }
        }
    }
    /**
     * allows to remove default chooser from panel
     */
    protected void uninstallDefaultChoosers() {
        for (AbstractColorChooserPanel defaultChooser : defaultChoosers) {
            chooser.removeChooserPanel(defaultChooser);
        }
    }
    /**
     * removes given old panels from main panel 
     * @param oldPanels an array of old panels 
     */
    private void removeOldPanel(AbstractColorChooserPanel[] oldPanels) {
    	for (AbstractColorChooserPanel oldPanel : oldPanels) {
            // remove old panels
            Container wrapper = oldPanel.getParent();
            if (wrapper != null) {
                Container parent = wrapper.getParent();
                if (parent != null) {
                    parent.remove(wrapper);  // remove from hierarchy
                }
                oldPanel.uninstallChooserPanel(chooser); // uninstall
            }
        }
    }
    /**
     * replace all old panel colors with the new one 
     * @param oldPanels array of color chooser panel
     * @param newPanels array of color chooser panel
     */
    private void updateColorChooserPanels(
            AbstractColorChooserPanel[] oldPanels,
            AbstractColorChooserPanel[] newPanels) {
        removeOldPanel(oldPanels);
        mainPanel.removeAllColorChooserPanels();
        installToNewPanel(newPanels);
    }
    /**
     * iterates over given panels adding them to the main panel 
     * @param newPanels an array of color chooser panel
     */
    private void installToNewPanel(AbstractColorChooserPanel[] newPanels) {
    	for (AbstractColorChooserPanel newPanel : newPanels) {
            if (newPanel != null) {
                mainPanel.addColorChooserPanel(newPanel);
                newPanel.installChooserPanel(chooser);
            }
        }
    }

    public class PropertyHandler implements PropertyChangeListener {

        @Override
        /**
         * allows to notify listeners about an event
         * @param e PropertyChangeEvent
         */
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals(JColorChooser.CHOOSER_PANELS_PROPERTY)) {
                AbstractColorChooserPanel[] oldPanels = (AbstractColorChooserPanel[]) e.getOldValue();
                AbstractColorChooserPanel[] newPanels = (AbstractColorChooserPanel[]) e.getNewValue();
                removeOldPanel(oldPanels);
                mainPanel.removeAllColorChooserPanels();
                installToNewPanel(newPanels);
            }
            if (name.equals(JColorChooser.PREVIEW_PANEL_PROPERTY) &&e.getNewValue() != previewPanel) 
            	installPreviewPanel();
            if ("componentOrientation".equals(name)) {
                ComponentOrientation o = (ComponentOrientation) e.getNewValue();
                JColorChooser cc = (JColorChooser) e.getSource();
                if (o != (ComponentOrientation) e.getOldValue()) {
                    cc.applyComponentOrientation(o);
                    cc.updateUI();
                }
            }
        }
    }

    static class ColorTransferHandler extends TransferHandler implements UIResource {

        private static final long serialVersionUID = 1L;

        ColorTransferHandler() {
            super("color");
        }
    }
}
