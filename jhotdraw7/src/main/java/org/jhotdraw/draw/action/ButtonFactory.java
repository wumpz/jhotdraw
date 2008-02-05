/*
 * @(#)ButtonFactory.java  2.0.1  2007-12-17
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw.action;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.jhotdraw.app.action.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.draw.*;

/**
 * ButtonFactory.
 *
 * @author Werner Randelshofer
 * @version 2.0.1 2007-12-17 Fixed createToggleGridButton method. 
 * <br>2.0 2007-03-31 Renamed from ToolBarButtonFactory to ButtonFactory.
 * Replaced most add...ButtonTo methods by create...Button methods.
 * <br>1.3 2006-12-29 Split methods even more up. Added additional buttons.
 * <br>1.2 2006-07-16 Split some methods up for better reuse.
 * <br>1.1 2006-03-27 Font exclusion list updated.
 * <br>1.0 13. Februar 2006 Created.
 */
public class ButtonFactory {
    /**
     * Mac OS X 'Apple Color Palette'. 
     * This palette has 8 columns.
     */
    public final static java.util.List<ColorIcon> DEFAULT_COLORS;
    static {
        LinkedList<ColorIcon> m = new LinkedList<ColorIcon>();
        m.add(new ColorIcon(0x800000,"Cayenne"));
        m.add(new ColorIcon(0x808000,"Asparagus"));
        m.add(new ColorIcon(0x008000,"Clover"));
        m.add(new ColorIcon(0x008080,"Teal"));
        m.add(new ColorIcon(0x000080,"Midnight"));
        m.add(new ColorIcon(0x800080,"Plum"));
        m.add(new ColorIcon(0x7f7f7f,"Tin"));
        m.add(new ColorIcon(0x808080,"Nickel"));
        m.add(new ColorIcon(0xff0000,"Maraschino"));
        m.add(new ColorIcon(0xffff00,"Lemon"));
        m.add(new ColorIcon(0x00ff00,"Spring"));
        m.add(new ColorIcon(0x00ffff,"Turquoise"));
        m.add(new ColorIcon(0x0000ff,"Blueberry"));
        m.add(new ColorIcon(0xff00ff,"Magenta"));
        m.add(new ColorIcon(0x666666,"Steel"));
        m.add(new ColorIcon(0x999999,"Aluminium"));
        m.add(new ColorIcon(0xff6666,"Salmon"));
        m.add(new ColorIcon(0xffff66,"Banana"));
        m.add(new ColorIcon(0x66ff66,"Flora"));
        m.add(new ColorIcon(0x66ffff,"Ice"));
        m.add(new ColorIcon(0x6666ff,"Orchid"));
        m.add(new ColorIcon(0xff66ff,"Bubblegum"));
        m.add(new ColorIcon(0x4c4c4c,"Iron"));
        m.add(new ColorIcon(0xb3b3b3,"Magnesium"));
        m.add(new ColorIcon(0x804000,"Mocha"));
        m.add(new ColorIcon(0x408000,"Fern"));
        m.add(new ColorIcon(0x008040,"Moss"));
        m.add(new ColorIcon(0x004080,"Ocean"));
        m.add(new ColorIcon(0x400080,"Eggplant"));
        m.add(new ColorIcon(0x800040,"Maroon"));
        m.add(new ColorIcon(0x333333,"Tungsten"));
        m.add(new ColorIcon(0xcccccc,"Silver"));
        m.add(new ColorIcon(0xff8000,"Tangerine"));
        m.add(new ColorIcon(0x80ff00,"Lime"));
        m.add(new ColorIcon(0x00ff80,"Sea Foam"));
        m.add(new ColorIcon(0x0080ff,"Aqua"));
        m.add(new ColorIcon(0x8000ff,"Grape"));
        m.add(new ColorIcon(0xff0080,"Strawberry"));
        m.add(new ColorIcon(0x191919,"Lead"));
        m.add(new ColorIcon(0xe6e6e6,"Mercury"));
        m.add(new ColorIcon(0xffcc66,"Cantaloupe"));
        m.add(new ColorIcon(0xccff66,"Honeydew"));
        m.add(new ColorIcon(0x66ffcc,"Spindrift"));
        m.add(new ColorIcon(0x66ccff,"Sky"));
        m.add(new ColorIcon(0xcc66ff,"Lavender"));
        m.add(new ColorIcon(0xff6fcf,"Carnation"));
        m.add(new ColorIcon(0x000000,"Licorice"));
        m.add(new ColorIcon(0xffffff,"Snow"));
        DEFAULT_COLORS = Collections.unmodifiableList(m);
    }
    public final static int DEFAULT_COLORS_COLUMN_COUNT = 8;
    /**
     * Websave color palette as used by Macromedia Fireworks. 
     * This palette has 19 columns.
     * The leftmost column contains a redundant set of color
     * icons to make selection of gray scales and of the 
     * primary colors easier.
     */
    public final static java.util.List<ColorIcon> WEBSAVE_COLORS;
    static {
        LinkedList<ColorIcon> m = new LinkedList<ColorIcon>();
        for (int b=0; b <= 0xff; b += 0x33) {
            int rgb = (b << 16) | (b << 8) | b;
            m.add(new ColorIcon(rgb));
            for (int r=0; r <= 0x66; r += 0x33) {
                for (int g=0; g <= 0xff; g += 0x33) {
                    rgb = (r << 16) | (g << 8) | b;
                    m.add(new ColorIcon(rgb));
                }
            }
        }
        int[] firstColumn = {
            0xff0000, 
            0x00ff00, 
            0x0000ff, 
            0xff00ff, 
            0x00ffff, 
            0xffff00, 
        };
        for (int b=0x0, i=0; b <= 0xff; b += 0x33, i++) {
            int rgb = (b << 16) | (b << 8) | b;
             m.add(new ColorIcon(firstColumn[i]));
             for (int r=0x99; r <= 0xff; r += 0x33) {
                for (int g=0; g <= 0xff; g += 0x33) {
                    rgb = 0xff000000 | (r << 16) | (g << 8) | b;
                    m.add(new ColorIcon(rgb));
                }
            }
        }
        WEBSAVE_COLORS = Collections.unmodifiableList(m);
    }
    public final static int WEBSAVE_COLORS_COLUMN_COUNT = 19;
    
    
    private static class ToolButtonListener implements ItemListener {
        private Tool tool;
        private DrawingEditor editor;
        public ToolButtonListener(Tool t, DrawingEditor editor) {
            this.tool = t;
            this.editor = editor;
        }
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                editor.setTool(tool);
            }
        }
    }
    
    /** Prevent instance creation. */
    private ButtonFactory() {
    }
    
    public static Collection<Action> createDrawingActions(DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new CutAction());
        a.add(new CopyAction());
        a.add(new PasteAction());
        a.add(new SelectAllAction());
        a.add(new SelectSameAction(editor));
        
        return a;
    }
    public static Collection<Action> createSelectionActions(DrawingEditor editor) {
        LinkedList<Action> a = new LinkedList<Action>();
        a.add(new DuplicateAction());
        
        a.add(null); // separator
        a.add(new GroupAction(editor));
        a.add(new UngroupAction(editor));
        
        a.add(null); // separator
        a.add(new MoveToFrontAction(editor));
        a.add(new MoveToBackAction(editor));
        
        return a;
    }
    
    public static JToggleButton addSelectionToolTo(JToolBar tb, final DrawingEditor editor) {
        return addSelectionToolTo(tb, editor, createDrawingActions(editor), createSelectionActions(editor));
    }
    public static JToggleButton addSelectionToolTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JToggleButton t;
        Tool tool;
        HashMap<String,Object> attributes;
        
        ButtonGroup group;
        if (tb.getClientProperty("toolButtonGroup") instanceof ButtonGroup) {
            group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        } else {
            group = new ButtonGroup();
            tb.putClientProperty("toolButtonGroup", group);
        }
        
        // Selection tool
        Tool selectionTool = new DelegationSelectionTool(
                drawingActions, selectionActions
                );
        editor.setTool(selectionTool);
        t = new JToggleButton();
        final JToggleButton defaultToolButton = t;
        
        ToolListener toolHandler;
        if (tb.getClientProperty("toolHandler") instanceof ToolListener) {
            toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
        } else {
            toolHandler = new ToolListener() {
                public void toolStarted(ToolEvent event) {
                }
                
                public void toolDone(ToolEvent event) {
                    defaultToolButton.setSelected(true);
                }
                
                public void areaInvalidated(ToolEvent e) {
                }
            };
            tb.putClientProperty("toolHandler", toolHandler);
        }
        
        labels.configureToolBarButton(t, "selectionTool");
        t.setSelected(true);
        t.addItemListener(
                new ToolButtonListener(selectionTool, editor)
                );
        t.setFocusable(false);
        group.add(t);
        tb.add(t);
        
        return t;
    }
    
    /**
     * Method addSelectionToolTo must have been invoked prior to this on the
     * JToolBar.
     *
     */
    public static JToggleButton addToolTo(JToolBar tb, DrawingEditor editor,
            Tool tool, String labelKey,
            ResourceBundleUtil labels) {
        
        ButtonGroup group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
        ToolListener toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
        
        JToggleButton t = new JToggleButton();
        labels.configureToolBarButton(t, labelKey);
        t.addItemListener(new ToolButtonListener(tool, editor));
        t.setFocusable(false);
        tool.addToolListener(toolHandler);
        group.add(t);
        tb.add(t);
        
        return t;
    }
    
    
    
    public static void addZoomButtonsTo(JToolBar bar, final DrawingEditor editor) {
        bar.add(createZoomButton(editor));
    }
    public static AbstractButton createZoomButton(final DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        final JPopupButton zoomPopupButton = new JPopupButton();
        
        labels.configureToolBarButton(zoomPopupButton, "viewZoom");
        zoomPopupButton.setFocusable(false);
        if (editor.getDrawingViews().size() == 0) {
            zoomPopupButton.setText("100 %");
        } else {
            zoomPopupButton.setText((int) (editor.getDrawingViews().iterator().next().getScaleFactor() * 100) + " %");
        }
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // String constants are interned
                if (evt.getPropertyName() == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                    if (evt.getNewValue() == null) {
                        zoomPopupButton.setText("100 %");
                    } else {
                        zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
                    }
                }
            }
        });
        
        double[] factors = {16, 8, 5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
        for (int i=0; i < factors.length; i++) {
            zoomPopupButton.add(
                    new ZoomEditorAction(editor, factors[i], zoomPopupButton) {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    super.actionPerformed(e);
                    zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
                }
            });
        }
        //zoomPopupButton.setPreferredSize(new Dimension(16,16));
        zoomPopupButton.setFocusable(false);
        return zoomPopupButton;
    }
    public static AbstractButton createZoomButton(DrawingView view) {
        return createZoomButton(view, new double[] {
            5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10
        });
    }
    public static AbstractButton createZoomButton(final DrawingView view, double[] factors) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        final JPopupButton zoomPopupButton = new JPopupButton();
        
        labels.configureToolBarButton(zoomPopupButton, "viewZoom");
        zoomPopupButton.setFocusable(false);
        zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
        
        view.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // String constants are interned
                if (evt.getPropertyName() == "scaleFactor") {
                    zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
                }
            }
        });
        
        for (int i=0; i < factors.length; i++) {
            zoomPopupButton.add(
                    new ZoomAction(view, factors[i], zoomPopupButton) {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    super.actionPerformed(e);
                    zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
                }
            });
        }
        //zoomPopupButton.setPreferredSize(new Dimension(16,16));
        zoomPopupButton.setFocusable(false);
        return zoomPopupButton;
    }
    /**
     * Creates toolbar buttons and adds them to the specified JToolBar
     */
    public static void addAttributesButtonsTo(JToolBar bar, DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        JButton b;
        
        b = bar.add(new PickAttributesAction(editor));
        b.setFocusable(false);
        b = bar.add(new ApplyAttributesAction(editor));
        b.setFocusable(false);
        bar.addSeparator();
        
        addColorButtonsTo(bar, editor);
        bar.addSeparator();
        addStrokeButtonsTo(bar, editor);
        bar.addSeparator();
        addFontButtonsTo(bar, editor);
    }
    public static void addColorButtonsTo(JToolBar bar, DrawingEditor editor) {
        addColorButtonsTo(bar, editor, DEFAULT_COLORS, DEFAULT_COLORS_COLUMN_COUNT);
        }
    public static void addColorButtonsTo(JToolBar bar, DrawingEditor editor,
            java.util.List<ColorIcon> colors, int columnCount) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");

        bar.add(createEditorColorButton(editor, STROKE_COLOR, colors, columnCount, "attribute.strokeColor", labels, new HashMap<AttributeKey,Object>()));
        bar.add(createEditorColorButton(editor, FILL_COLOR, colors, columnCount, "attribute.fillColor", labels, new HashMap<AttributeKey,Object>()));
        bar.add(createEditorColorButton(editor, TEXT_COLOR, colors, columnCount, "attribute.textColor", labels, new HashMap<AttributeKey,Object>()));
    }
    /**
     * Creates a color button, with an action region and a popup menu. The
     * button works like the color button in Microsoft Office:
     * <ul>
     * <li>When the user clicks on the action region, the default color of the
     * DrawingEditor is applied to the selected figures.</li>
     * <li>When the user opens the popup menu, a color palette is displayed.
     * Choosing a color from the palette changes the default color of the
     * editor and also changes the color of the selected figures.</li>
     * <li>A rectangle on the color button displays the current default color of
     * the DrawingEditor. The rectangle has the dimensions 1, 17, 20, 4 (x, y,
     * width, height).</li>
     * </ul>
     *
     * @param editor The DrawingEditor.
     * @param attributeKey The AttributeKey of the default color.
     * @param swatches A list with labeled colors containing the color palette
     * of the popup menu. The actual labels are retrieved from the supplied
     * resource bundle. This is usually a LinkedMap, so that the colors have
     * a predictable order.
     * @param columnCount The number of columns of the color palette.
     * @param labelKey The resource bundle key used for retrieving the icon and
     * the tooltip of the button.
     * @param labels The resource bundle.
     */
    public static JPopupButton createEditorColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            java.util.List<ColorIcon> swatches, int columnCount,
            String labelKey, ResourceBundleUtil labels) {
        return createEditorColorButton(
                editor, attributeKey,
                swatches, columnCount,
                labelKey, labels,
                null
                );
    }
    /**
     * Creates a color button, with an action region and a popup menu. The
     * button works like the color button in Microsoft Office:
     * <ul>
     * <li>When the user clicks on the action region, the default color of the
     * DrawingEditor is applied to the selected figures.</li>
     * <li>When the user opens the popup menu, a color palette is displayed.
     * Choosing a color from the palette changes the default color of the
     * editor and also changes the color of the selected figures.</li>
     * <li>A rectangle on the color button displays the current default color of
     * the DrawingEditor. The rectangle has the dimensions 1, 17, 20, 4 (x, y,
     * width, height).</li>
     * </ul>
     *
     * @param editor The DrawingEditor.
     * @param attributeKey The AttributeKey of the default color.
     * @param swatches A list with labeled colors containing the color palette
     * of the popup menu. The actual labels are retrieved from the supplied
     * resource bundle. This is usually a LinkedMap, so that the colors have
     * a predictable order.
     * @param columnCount The number of columns of the color palette.
     * @param labelKey The resource bundle key used for retrieving the icon and
     * the tooltip of the button.
     * @param labels The resource bundle.
     * @param defaultAttributes A set of attributes which are also applied to
     * the selected figures, when a color is selected. This can be used, to
     * set attributes that otherwise prevent the color from being shown. For
     * example, when the color attribute is set, we wan't the gradient attribute
     * of the Figure to be cleared.
     */
    public static JPopupButton createEditorColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            java.util.List<ColorIcon> swatches, int columnCount,
            String labelKey, ResourceBundleUtil labels,
            Map<AttributeKey,Object> defaultAttributes) {
        return createEditorColorButton(editor, attributeKey,
                swatches, columnCount, labelKey, labels, defaultAttributes,
                new Rectangle(1, 17, 20, 4)
                );
    }
    
    /**
     * Creates a color button, with an action region and a popup menu. The
     * button works like the color button in Microsoft Office:
     * <ul>
     * <li>When the user clicks on the action region, the default color of the
     * DrawingEditor is applied to the selected figures.</li>
     * <li>When the user opens the popup menu, a color palette is displayed.
     * Choosing a color from the palette changes the default color of the
     * editor and also changes the color of the selected figures.</li>
     * <li>A shape on the color button displays the current default color of the
     * DrawingEditor.</li>
     * </ul>
     *
     * @param editor The DrawingEditor.
     * @param attributeKey The AttributeKey of the default color.
     * @param swatches A list with labeled colors containing the color palette
     * of the popup menu. The actual labels are retrieved from the supplied
     * resource bundle. This is usually a LinkedHashMap, so that the colors have
     * a predictable order.
     * @param columnCount The number of columns of the color palette.
     * @param labelKey The resource bundle key used for retrieving the icon and
     * the tooltip of the button.
     * @param labels The resource bundle.
     * @param defaultAttributes A set of attributes which are also applied to
     * the selected figures, when a color is selected. This can be used, to
     * set attributes that otherwise prevent the color from being shown. For
     * example, when the color attribute is set, we wan't the gradient attribute
     * of the Figure to be cleared.
     * @param colorShape This shape is superimposed on the icon of the button.
     * The shape is drawn with the default color of the DrawingEditor.
     */
    public static JPopupButton createEditorColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            java.util.List<ColorIcon> swatches, int columnCount,
            String labelKey, ResourceBundleUtil labels,
            Map<AttributeKey,Object> defaultAttributes,
            Shape colorShape) {
        final JPopupButton popupButton = new JPopupButton();
        if (defaultAttributes == null) {
            defaultAttributes = new HashMap<AttributeKey,Object>();
        }
        
        popupButton.setAction(
                new DefaultAttributeAction(editor, attributeKey, defaultAttributes),
                new Rectangle(0, 0, 22, 22)
                );
        popupButton.setColumnCount(columnCount, false);
        for (ColorIcon swatch : swatches) {
            AttributeAction a;
            HashMap<AttributeKey,Object> attributes = new HashMap<AttributeKey,Object>(defaultAttributes);
            attributes.put(attributeKey, swatch.getColor());
            popupButton.add(a=
                    new AttributeAction(
                    editor,
                    attributes,
                    labels.getString(labelKey),
                    swatch
                    )
                    );
            a.putValue(Action.SHORT_DESCRIPTION, swatch.getName());
        }
        
        // No color
            AttributeAction a;
            HashMap<AttributeKey,Object> attributes = new HashMap<AttributeKey,Object>(defaultAttributes);
            attributes.put(attributeKey, null);
            popupButton.add(a=
                    new AttributeAction(
                    editor,
                    attributes,
                   labels.getString("noColor"),
                    new ColorIcon(null, "---", swatches.get(0).getIconWidth(), swatches.get(0).getIconHeight())
                    )
                    );
            a.putValue(Action.SHORT_DESCRIPTION, "---");
            
            // Color chooser
        ImageIcon chooserIcon = new ImageIcon(
                ButtonFactory.class.getResource("/org/jhotdraw/draw/action/images/showColorChooser.png")
                );
        
        popupButton.add(
                new EditorColorChooserAction(
                editor,
                attributeKey,
                "color",
                chooserIcon,
                defaultAttributes
                )
                );
        labels.configureToolBarButton(popupButton,labelKey);
        Icon icon = new EditorColorIcon(editor,
                attributeKey,
                labels.getImageIcon(labelKey, ButtonFactory.class).getImage(),
                colorShape
                );
        popupButton.setIcon(icon);
        popupButton.setDisabledIcon(icon);
        popupButton.setFocusable(false);
        
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                popupButton.repaint();
            }
        });
        
        return popupButton;
    }
    public static JPopupButton createSelectionColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            Map<String,Color> colorMap, int columnCount,
            String labelKey, ResourceBundleUtil labels) {
        return createSelectionColorButton(
                editor, attributeKey,
                colorMap, columnCount,
                labelKey, labels,
                null
                );
    }
    public static JPopupButton createSelectionColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            Map<String,Color> colorMap, int columnCount,
            String labelKey, ResourceBundleUtil labels,
            Map<AttributeKey,Object> defaultAttributes) {
        return createSelectionColorButton(editor, attributeKey,
                colorMap, columnCount, labelKey, labels, defaultAttributes,
                new Rectangle(1, 17, 20, 4)
                );
    }
    /**
     * Creates a color button, with an action region and a popup menu. The
     * button works like the color button in Adobe Fireworks:
     * <ul>
     * <li>When the user clicks at the button a popup menu with a color palette
     * is displayed.
     * Choosing a color from the palette changes the default color of the
     * editor and also changes the color of the selected figures.</li>
     * <li>A shape on the color button displays the color of the selected
     * figures. If no figures are selected, the default color of the
     * DrawingEditor is displayed.</li>
     * </ul>
     *
     * @param editor The DrawingEditor.
     * @param attributeKey The AttributeKey of the default color.
     * @param colorMap A map with labeled colors containing the color palette
     * of the popup menu. The actual labels are retrieved from the supplied
     * resource bundle. This is usually a LinkedHashMap, so that the colors have
     * a predictable order.
     * @param columnCount The number of columns of the color palette.
     * @param labelKey The resource bundle key used for retrieving the icon and
     * the tooltip of the button.
     * @param labels The resource bundle.
     * @param defaultAttributes A set of attributes which are also applied to
     * the selected figures, when a color is selected. This can be used, to
     * set attributes that otherwise prevent the color from being shown. For
     * example, when the color attribute is set, we wan't the gradient attribute
     * of the Figure to be cleared.
     * @param colorShape This shape is superimposed on the icon of the button.
     * The shape is drawn with the default color of the DrawingEditor.
     */
    public static JPopupButton createSelectionColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            Map<String,Color> colorMap, int columnCount,
            String labelKey, ResourceBundleUtil labels,
            Map<AttributeKey,Object> defaultAttributes,
            Shape colorShape) {
        final JPopupButton popupButton = new JPopupButton();
        if (defaultAttributes == null) {
            defaultAttributes = new HashMap<AttributeKey,Object>();
        }
        
        popupButton.setColumnCount(columnCount, false);
        for (Map.Entry<String,Color> entry : colorMap.entrySet()) {
            AttributeAction a;
            HashMap<AttributeKey,Object> attributes = new HashMap<AttributeKey,Object>(defaultAttributes);
            attributes.put(attributeKey, entry.getValue());
            popupButton.add(a=
                    new AttributeAction(
                    editor,
                    attributes,
                    labels.getString(labelKey),
                    new ColorIcon(entry.getValue())
                    )
                    );
            a.putValue(Action.SHORT_DESCRIPTION, entry.getKey());
        }
        
        // No color
            AttributeAction a;
            HashMap<AttributeKey,Object> attributes = new HashMap<AttributeKey,Object>(defaultAttributes);
            attributes.put(attributeKey, null);
            popupButton.add(a=
                    new AttributeAction(
                    editor,
                    attributes,
                   labels.getString("noColor"),
                    new ColorIcon(null, "---")
                    )
                    );
            a.putValue(Action.SHORT_DESCRIPTION, "---");
        
        // Color chooser
        ImageIcon chooserIcon = new ImageIcon(
                ButtonFactory.class.getResource("/org/jhotdraw/draw/action/images/showColorChooser.png")
                );
        
        popupButton.add(
                new SelectionColorChooserAction(
                editor,
                attributeKey,
                "color",
                chooserIcon,
                defaultAttributes
                )
                );
        labels.configureToolBarButton(popupButton,labelKey);
        Icon icon = new SelectionColorIcon(editor,
                attributeKey,
                labels.getImageIcon(labelKey, ButtonFactory.class).getImage(),
                colorShape
                );
        popupButton.setIcon(icon);
        popupButton.setDisabledIcon(icon);
        popupButton.setFocusable(false);
        
        final FigureSelectionListener selectionHandler = new FigureSelectionListener() {
            public void selectionChanged(FigureSelectionEvent evt) {
                popupButton.repaint();
            }
        };
        
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                    if (evt.getOldValue() != null) {
                        ((DrawingView) evt.getOldValue()).removeFigureSelectionListener(selectionHandler);
                    }
                    if (evt.getNewValue() != null) {
                        ((DrawingView) evt.getNewValue()).addFigureSelectionListener(selectionHandler);
                    }
                    popupButton.repaint();
                } else {
                    popupButton.repaint();
                    
                }
            }
        });
        
        return popupButton;
    }
    public static void addStrokeButtonsTo(JToolBar bar, DrawingEditor editor) {
        bar.add(createStrokeDecorationButton(editor));
        bar.add(createStrokeWidthButton(editor));
        bar.add(createStrokeDashesButton(editor));
        bar.add(createStrokeTypeButton(editor));
        bar.add(createStrokePlacementButton(editor));
        bar.add(createStrokeCapButton(editor));
        bar.add(createStrokeJoinButton(editor));
    }
    public static JPopupButton createStrokeWidthButton(DrawingEditor editor) {
        return createStrokeWidthButton(
                editor,
                new double[] {0.5d, 1d, 2d, 3d, 5d, 9d, 13d},
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeWidthButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        return createStrokeWidthButton(
                editor,
                new double[] {0.5d, 1d, 2d, 3d, 5d, 9d, 13d},
                labels
                );
    }
    public static JPopupButton createStrokeWidthButton(DrawingEditor editor,
            double[] widths) {
        return createStrokeWidthButton(
                editor, new double[] {0.5d, 1d, 2d, 3d, 5d, 9d, 13d},
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeWidthButton(
            DrawingEditor editor, double[] widths, ResourceBundleUtil labels) {
        JPopupButton strokeWidthPopupButton = new JPopupButton();
        
        labels.configureToolBarButton(strokeWidthPopupButton,"attribute.strokeWidth");
        strokeWidthPopupButton.setFocusable(false);
        
        NumberFormat formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).setMaximumFractionDigits(1);
            ((DecimalFormat) formatter).setMinimumFractionDigits(0);
        }
        for (int i=0; i < widths.length; i++) {
            String label = Double.toString(widths[i]);
            Icon icon = new StrokeIcon(new BasicStroke((float) widths[i], BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            AttributeAction a = new AttributeAction(
                    editor,
                    STROKE_WIDTH,
                    new Double(widths[i]),
                    label,
                    icon
                    );
            a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.strokeWidth"));
            AbstractButton btn = strokeWidthPopupButton.add(a);
            btn.setDisabledIcon(icon);
        }
        return strokeWidthPopupButton;
    }
    
    public static JPopupButton createStrokeDecorationButton(DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPopupButton strokeDecorationPopupButton = new JPopupButton();
        labels.configureToolBarButton(strokeDecorationPopupButton,"attribute.strokeDecoration");
        strokeDecorationPopupButton.setFocusable(false);
        strokeDecorationPopupButton.setColumnCount(2, false);
        LineDecoration[] decorations = {
            // Arrow
            new ArrowTip(0.35, 12, 11.3),
            
            // Arrow
            new ArrowTip(0.35, 13, 7),
            
            // Generalization triangle
            new ArrowTip(Math.PI / 5, 12, 9.8, true, true, false),
            
            // Dependency arrow
            new ArrowTip(Math.PI / 6, 12, 0, false, true, false),
            
            // Link arrow
            new ArrowTip(Math.PI / 11, 13, 0, false, true, true),
            
            // Aggregation diamond
            new ArrowTip(Math.PI / 6, 10, 18, false, true, false),
            
            // Composition diamond
            new ArrowTip(Math.PI / 6, 10, 18, true, true, true),
            null
        };
        for (int i=0; i < decorations.length; i++) {
            strokeDecorationPopupButton.add(
                    new AttributeAction(
                    editor,
                    START_DECORATION,
                    decorations[i],
                    null,
                    new LineDecorationIcon(decorations[i], true)
                    )
                    );
            strokeDecorationPopupButton.add(
                    new AttributeAction(
                    editor,
                    END_DECORATION,
                    decorations[i],
                    null,
                    new LineDecorationIcon(decorations[i], false)
                    )
                    );
        }
        
        return strokeDecorationPopupButton;
    }
    public static JPopupButton createStrokeDashesButton(DrawingEditor editor) {
        return createStrokeDashesButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeDashesButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        return createStrokeDashesButton(editor, new double[][] {
            null,
            {4d, 4d},
            {2d, 2d},
            {4d, 2d},
            {2d, 4d},
            {8d, 2d},
            {6d, 2d, 2d, 2d},
        },
                labels
                );
    }
    public static JPopupButton createStrokeDashesButton(DrawingEditor editor,
            double[][] dashes) {
        return createStrokeDashesButton(editor, dashes,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeDashesButton(DrawingEditor editor,
            double[][] dashes,
            ResourceBundleUtil labels) {
        JPopupButton strokeDashesPopupButton = new JPopupButton();
        labels.configureToolBarButton(strokeDashesPopupButton,"attribute.strokeDashes");
        strokeDashesPopupButton.setFocusable(false);
        //strokeDashesPopupButton.setColumnCount(2, false);
        for (int i=0; i < dashes.length; i++) {
            
            float[] fdashes;
            if (dashes[i] == null) {
                fdashes = null;
            } else {
                fdashes = new float[dashes[i].length];
                for (int j = 0; j < dashes[i].length; j++) {
                    fdashes[j] = (float) dashes[i][j];
                }
            }
            
            Icon icon = new StrokeIcon(
                    new BasicStroke(2f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 10f, fdashes, 0));
            
            
            AbstractButton btn = strokeDashesPopupButton.add(
                    new AttributeAction(
                    editor,
                    STROKE_DASHES,
                    dashes[i],
                    null,
                    icon
                    )
                    );
            btn.setDisabledIcon(icon);
        }
        return strokeDashesPopupButton;
    }
    public static JPopupButton createStrokeTypeButton(DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPopupButton strokeTypePopupButton = new JPopupButton();
        labels.configureToolBarButton(strokeTypePopupButton,"attribute.strokeType");
        strokeTypePopupButton.setFocusable(false);
        
        strokeTypePopupButton.add(
                new AttributeAction(
                editor,
                STROKE_TYPE,
                AttributeKeys.StrokeType.BASIC,
                labels.getString("attribute.strokeType.basic"),
                new StrokeIcon(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL))
                )
                );
        HashMap<AttributeKey,Object> attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
        attr.put(STROKE_INNER_WIDTH_FACTOR, 2d);
        strokeTypePopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeType.double"),
                new StrokeIcon(new DoubleStroke(2, 1))
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
        attr.put(STROKE_INNER_WIDTH_FACTOR, 3d);
        strokeTypePopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeType.double"),
                new StrokeIcon(new DoubleStroke(3, 1))
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
        attr.put(STROKE_INNER_WIDTH_FACTOR, 4d);
        strokeTypePopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeType.double"),
                new StrokeIcon(new DoubleStroke(4, 1))
                )
                );
        
        
        return strokeTypePopupButton;
    }
    public static JPopupButton createStrokePlacementButton(DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPopupButton strokePlacementPopupButton = new JPopupButton();
        labels.configureToolBarButton(strokePlacementPopupButton,"attribute.strokePlacement");
        strokePlacementPopupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.center"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.inside"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.outside"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.centerFilled"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.insideFilled"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.outsideFilled"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.centerUnfilled"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.insideUnfilled"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokePlacement.outsideUnfilled"),
                null
                )
                );
        
        return strokePlacementPopupButton;
    }
    
    public static void addFontButtonsTo(JToolBar bar, DrawingEditor editor) {
        bar.add(createFontButton(editor));
        bar.add(createFontStyleBoldButton(editor));
        bar.add(createFontStyleItalicButton(editor));
        bar.add(createFontStyleUnderlineButton(editor));
    }
    public static JPopupButton createFontButton(DrawingEditor editor) {
        return createFontButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createFontButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        
        JPopupButton fontPopupButton;
        
        fontPopupButton = new JPopupButton();
        
        labels.configureToolBarButton(fontPopupButton, "attribute.font");
        fontPopupButton.setFocusable(false);
        
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        HashSet<String> fontExclusionList = new HashSet<String>(Arrays.asList(new String[] {
            // Mac OS X 10.3 Font Exclusion List
            "#GungSeo", "#HeadLineA", "#PCMyungjo", "#PilGi", "Al Bayan", "Apple LiGothic",
            "Apple LiSung", "AppleMyungjo", "Arial Hebrew", "Ayuthaya", "Baghdad",
            "BiauKai", "Charcoal CY", "Corsiva Hebrew", "DecoType Naskh",
            "Devanagari MT", "Fang Song", "GB18030 Bitmap", "Geeza Pro",
            "Geezah", "Geneva CY", "Gujarati MT", "Gurmukhi MT", "Hei",
            "Helvetica CY", "Hiragino Kaku Gothic Std", "Hiragino Maru Gothic Pro",
            "Hiragino Mincho Pro", "Hiragino Kaku Gothic Pro",
            "InaiMathi",
            "Kai",
            "Krungthep", "KufiStandardGK", "LiHei Pro", "LiSong Pro",
            "Mshtakan",
            "Monaco CY",
            "Nadeem",
            "New Peninim MT", "Osaka",
            "Plantagenet Cherokee",
            "Raanana", "STFangsong", "STHeiti",
            "STKaiti", "STSong", "Sathu", "Silom",
            "Thonburi", "Times CY",
            
            // Windows XP Professional Font Exclusion List
            "Arial Unicode MS", "Batang", "Estrangelo Edessa", "Gautami",
            "Kartika", "Latha", "Lucida Sans Unicode", "Mangal", "Marlett",
            "MS Mincho", "MS Outlook", "MV Boli", "OCR-B-10 BT",
            "Raavi", "Shruti", "SimSun", "Sylfaen", "Symbol", "Tunga",
            "Vrinda", "Wingdings", "Wingdings 2", "Wingdings 3",
            "ZWAdobeF"
        }));
        LinkedList<Font> fontList = new LinkedList<Font>();
        for (int i=0; i < allFonts.length; i++) {
            if (! fontExclusionList.contains(allFonts[i].getFamily())) {
                fontList.add(allFonts[i]);
            }
        }
        allFonts = new Font[fontList.size()];
        allFonts = (Font[]) fontList.toArray(allFonts);
        Arrays.sort(allFonts, new Comparator<Font>() {
            public int compare(Font f1, Font f2) {
                int result = f1.getFamily().compareTo(f2.getFamily());
                if (result == 0) {
                    result = f1.getFontName().compareTo(f2.getFontName());
                }
                return result;
            }
        });
        LinkedList<Font> fontFamilies = new LinkedList<Font>();
        JMenu submenu = null;
        for (int i=0; i < allFonts.length; i++) {
            if (submenu != null) {
                if (! allFonts[i].getFamily().equals(allFonts[i - 1].getFamily())) {
                    submenu = null;
                }
            }
            if (submenu == null) {
                if (i < allFonts.length - 2
                        && allFonts[i].getFamily().equals(allFonts[i + 1].getFamily())) {
                    fontFamilies.add(allFonts[i]);
                    submenu = new JMenu(allFonts[i].getFamily());
                    //submenu.setFont(JPopupButton.ITEM_FONT);
                    fontPopupButton.add(submenu);
                    
                }
            }
            Action action = new AttributeAction(
                    editor,
                    FONT_FACE,
                    allFonts[i],
                    (submenu == null) ? allFonts[i].getFamily() : allFonts[i].getFontName(),
                    null,
                    new StyledEditorKit.FontFamilyAction(allFonts[i].getFontName(),allFonts[i].getFamily())
                    );
            
            if (submenu == null) {
                fontFamilies.add(allFonts[i]);
                fontPopupButton.add(action);
            } else {
                JMenuItem item = submenu.add(action);
                //item.setFont(JPopupButton.itemFont);
            }
        }
        fontPopupButton.setColumnCount( Math.max(1, fontFamilies.size()/32), true);
        fontPopupButton.setFocusable(false);
        
        return fontPopupButton;
    }
    public static JButton createFontStyleBoldButton(DrawingEditor editor) {
        return createFontStyleBoldButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JButton createFontStyleBoldButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        JButton btn;
        btn = new JButton();
        labels.configureToolBarButton(btn, "attribute.fontStyle.bold");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_BOLD, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.fontStyle.bold"));
        btn.addActionListener(a);
        return btn;
    }
    public static JButton createFontStyleItalicButton(DrawingEditor editor) {
        return createFontStyleItalicButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JButton createFontStyleItalicButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        JButton btn;
        btn = new JButton();
        labels.configureToolBarButton(btn, "attribute.fontStyle.italic");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_ITALIC, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.fontStyle.italic"));
        btn.addActionListener(a);
        return btn;
    }
    public static JButton createFontStyleUnderlineButton(DrawingEditor editor) {
        return createFontStyleUnderlineButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JButton createFontStyleUnderlineButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        JButton btn;
        btn = new JButton();
        labels.configureToolBarButton(btn, "attribute.fontStyle.underline");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_UNDERLINE, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.fontStyle.underline"));
        btn.addActionListener(a);
        return btn;
    }
    /**
     * Creates toolbar buttons and adds them to the specified JToolBar
     */
    public static void addAlignmentButtonsTo(JToolBar bar, final DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        bar.add(new AlignAction.West(editor)).setFocusable(false);
        bar.add(new AlignAction.East(editor)).setFocusable(false);
        bar.add(new AlignAction.Horizontal(editor)).setFocusable(false);
        bar.add(new AlignAction.North(editor)).setFocusable(false);
        bar.add(new AlignAction.South(editor)).setFocusable(false);
        bar.add(new AlignAction.Vertical(editor)).setFocusable(false);
        bar.addSeparator();
        bar.add(new MoveAction.West(editor)).setFocusable(false);
        bar.add(new MoveAction.East(editor)).setFocusable(false);
        bar.add(new MoveAction.North(editor)).setFocusable(false);
        bar.add(new MoveAction.South(editor)).setFocusable(false);
        bar.addSeparator();
        bar.add(new MoveToFrontAction(editor)).setFocusable(false);
        bar.add(new MoveToBackAction(editor)).setFocusable(false);
        
    }
    /**
     * Creates a button which toggles between two GridConstrainer for
     * a DrawingView.
     */
    public static AbstractButton createToggleGridButton(final DrawingView view) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        final JToggleButton toggleButton;
        
        toggleButton = new JToggleButton();
        labels.configureToolBarButton(toggleButton, "alignGrid");
        toggleButton.setFocusable(false);
        toggleButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                view.setConstrainerVisible(toggleButton.isSelected());
                //view.getComponent().repaint();
            }
        });
        view.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // String constants are interned
                if (evt.getPropertyName() == DrawingView.CONSTRAINER_VISIBLE_PROPERTY) {
                    toggleButton.setSelected(view.isConstrainerVisible());
                }
            }
        });
        
        return toggleButton;
    }
    
    public static JPopupButton createStrokeCapButton(DrawingEditor editor) {
        return createStrokeCapButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeCapButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        
        JPopupButton popupButton = new JPopupButton();
        labels.configureToolBarButton(popupButton,"attribute.strokeCap");
        popupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_BUTT);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeCap.butt"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_ROUND);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeCap.round"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_SQUARE);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeCap.square"),
                null
                )
                );
        return popupButton;
    }
    
    public static JPopupButton createStrokeJoinButton(DrawingEditor editor) {
        return createStrokeJoinButton(editor,
                ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels")
                );
    }
    public static JPopupButton createStrokeJoinButton(DrawingEditor editor,
            ResourceBundleUtil labels) {
        
        JPopupButton popupButton = new JPopupButton();
        labels.configureToolBarButton(popupButton,"attribute.strokeJoin");
        popupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_BEVEL);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeJoin.bevel"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_ROUND);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeJoin.round"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_MITER);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attribute.strokeJoin.miter"),
                null
                )
                );
        return popupButton;
    }
    public static JButton createPickAttributesButton(DrawingEditor editor) {
        JButton btn;
        btn = new JButton(new PickAttributesAction(editor));
        if (btn.getIcon() !=null) {
            btn.putClientProperty("hideActionText", Boolean.TRUE);
        }
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setVerticalTextPosition(JButton.BOTTOM);
        btn.setText(null);
        btn.setFocusable(false);
        return btn;
    }
    
    /**
     * Creates a button that applies the default attributes of the editor to
     * the current selection.
     */
    public static JButton createApplyAttributesButton(DrawingEditor editor) {
        JButton btn;
        btn = new JButton(new ApplyAttributesAction(editor));
        if (btn.getIcon() !=null) {
            btn.putClientProperty("hideActionText", Boolean.TRUE);
        }
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setVerticalTextPosition(JButton.BOTTOM);
        btn.setText(null);
        btn.setFocusable(false);
        return btn;
    }
    
}
