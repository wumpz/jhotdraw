/*
 * @(#)ButtonFactory.java  2.0  2007-30-31
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

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
 * @version 2.0 2007-03-31 Renamed from ToolBarButtonFactory to ButtonFactory.
 * Replaced most add...ButtonTo methods by create...Button methods.
 * <br>1.3 2006-12-29 Split methods even more up. Added additional buttons.
 * <br>1.2 2006-07-16 Split some methods up for better reuse.
 * <br>1.1 2006-03-27 Font exclusion list updated.
 * <br>1.0 13. Februar 2006 Created.
 */
public class ButtonFactory {
    public final static Map<String,Color> DEFAULT_COLORS;
    static {
        LinkedHashMap<String,Color> m = new LinkedHashMap<String,Color>();
        m.put("Cayenne", new Color(128, 0, 0));
        m.put("Asparagus", new Color(128, 128, 0));
        m.put("Clover", new Color(0, 128, 0));
        m.put("Teal", new Color(0, 128, 128));
        m.put("Midnight", new Color(0, 0, 128));
        m.put("Plum", new Color(128, 0, 128));
        m.put("Tin", new Color(127, 127, 127));
        m.put("Nickel", new Color(128, 128, 128));
        
        m.put("Maraschino", new Color(255, 0, 0));
        m.put("Lemon", new Color(255, 255, 0));
        m.put("Spring", new Color(0, 255, 0));
        m.put("Turquoise", new Color(0, 255, 255));
        m.put("Blueberry", new Color(0, 0, 255));
        m.put("Magenta", new Color(255, 0, 255));
        m.put("Steel", new Color(102, 102, 102));
        m.put("Aluminium", new Color(153, 153, 153));
        
        m.put("Salmon", new Color(255, 102, 102));
        m.put("Banana", new Color(255, 255, 102));
        m.put("Flora", new Color(102, 255, 102));
        m.put("Ice", new Color(102, 255, 255));
        m.put("Orchid", new Color(102, 102, 255));
        m.put("Bubblegum", new Color(255, 102, 255));
        m.put("Iron", new Color(76, 76, 76));
        m.put("Magnesium", new Color(179, 179, 179));
        
        
        m.put("Mocha", new Color(128, 64, 0));
        m.put("Fern", new Color(64, 128, 0));
        m.put("Moss", new Color(0, 128, 64));
        m.put("Ocean", new Color(0, 64, 128));
        m.put("Eggplant", new Color(64, 0, 128));
        m.put("Maroon", new Color(128, 0, 64));
        m.put("Tungsten", new Color(51, 51, 51));
        m.put("Silver", new Color(204, 204, 204));
        
        
        m.put("Tangerine", new Color(255, 128, 0));
        m.put("Lime", new Color(128, 255, 0));
        m.put("Sea Foam", new Color(0, 255, 128));
        m.put("Aqua", new Color(0, 128, 255));
        m.put("Grape", new Color(128, 0, 255));
        m.put("Strawberry", new Color(255, 0, 128));
        
        m.put("Lead", new Color(25, 25, 25));
        m.put("Mercury", new Color(230, 230, 230));
        
        m.put("Cantaloupe", new Color(255, 204, 102));
        m.put("Honeydew", new Color(204, 255, 102));
        m.put("Spindrift", new Color(102, 255, 204));
        m.put("Sky", new Color(102, 204, 255));
        m.put("Lavender", new Color(204, 102, 255));
        m.put("Carnation", new Color(255, 111, 207));
        
        m.put("Licorice", new Color(0, 0, 0));
        m.put("Snow", new Color(255, 255, 255));
        
        m.put("Transparent", null);
        DEFAULT_COLORS = Collections.unmodifiableMap(m);
    }
    
    
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
                if (evt.getPropertyName() == DrawingEditor.PROP_ACTIVE_VIEW) {
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
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        bar.add(createEditorColorButton(editor, STROKE_COLOR, DEFAULT_COLORS, 8, "attributeStrokeColor", labels, new HashMap<AttributeKey,Object>()));
        bar.add(createEditorColorButton(editor, FILL_COLOR, DEFAULT_COLORS, 8, "attributeFillColor", labels, new HashMap<AttributeKey,Object>()));
        bar.add(createEditorColorButton(editor, TEXT_COLOR, DEFAULT_COLORS, 8, "attributeTextColor", labels, new HashMap<AttributeKey,Object>()));
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
     * @param colorMap A map with labeled colors containing the color palette
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
            Map<String,Color> colorMap, int columnCount,
            String labelKey, ResourceBundleUtil labels) {
        return createEditorColorButton(
                editor, attributeKey,
                colorMap, columnCount,
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
     * @param colorMap A map with labeled colors containing the color palette
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
            Map<String,Color> colorMap, int columnCount,
            String labelKey, ResourceBundleUtil labels,
            Map<AttributeKey,Object> defaultAttributes) {
        return createEditorColorButton(editor, attributeKey,
                colorMap, columnCount, labelKey, labels, defaultAttributes,
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
    public static JPopupButton createEditorColorButton(
            DrawingEditor editor, AttributeKey attributeKey,
            Map<String,Color> colorMap, int columnCount,
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
                if (name == DrawingEditor.PROP_ACTIVE_VIEW) {
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
        
        labels.configureToolBarButton(strokeWidthPopupButton,"attributeStrokeWidth");
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
            a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attributeStrokeWidth"));
            AbstractButton btn = strokeWidthPopupButton.add(a);
            btn.setDisabledIcon(icon);
        }
        return strokeWidthPopupButton;
    }
    
    public static JPopupButton createStrokeDecorationButton(DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPopupButton strokeDecorationPopupButton = new JPopupButton();
        labels.configureToolBarButton(strokeDecorationPopupButton,"attributeStrokeDecoration");
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
        labels.configureToolBarButton(strokeDashesPopupButton,"attributeStrokeDashes");
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
        labels.configureToolBarButton(strokeTypePopupButton,"attributeStrokeType");
        strokeTypePopupButton.setFocusable(false);
        
        strokeTypePopupButton.add(
                new AttributeAction(
                editor,
                STROKE_TYPE,
                AttributeKeys.StrokeType.BASIC,
                labels.getString("attributeStrokeTypeBasic"),
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
                labels.getString("attributeStrokeTypeDouble"),
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
                labels.getString("attributeStrokeTypeDouble"),
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
                labels.getString("attributeStrokeTypeDouble"),
                new StrokeIcon(new DoubleStroke(4, 1))
                )
                );
        
        
        return strokeTypePopupButton;
    }
    public static JPopupButton createStrokePlacementButton(DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        JPopupButton strokePlacementPopupButton = new JPopupButton();
        labels.configureToolBarButton(strokePlacementPopupButton,"attributeStrokePlacement");
        strokePlacementPopupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
        attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
        strokePlacementPopupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokePlacementCenter"),
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
                labels.getString("attributeStrokePlacementInside"),
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
                labels.getString("attributeStrokePlacementOutside"),
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
                labels.getString("attributeStrokePlacementCenterFilled"),
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
                labels.getString("attributeStrokePlacementInsideFilled"),
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
                labels.getString("attributeStrokePlacementOutsideFilled"),
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
                labels.getString("attributeStrokePlacementCenterUnfilled"),
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
                labels.getString("attributeStrokePlacementInsideUnfilled"),
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
                labels.getString("attributeStrokePlacementOutsideUnfilled"),
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
        
        labels.configureToolBarButton(fontPopupButton, "attributeFont");
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
        labels.configureToolBarButton(btn, "attributeFontBold");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_BOLD, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attributeFontBold"));
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
        labels.configureToolBarButton(btn, "attributeFontItalic");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_ITALIC, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attributeFontItalic"));
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
        labels.configureToolBarButton(btn, "attributeFontUnderline");
        btn.setFocusable(false);
        
        AbstractAction a = new AttributeToggler(editor,
                FONT_UNDERLINE, Boolean.TRUE, Boolean.FALSE,
                new StyledEditorKit.BoldAction()
                );
        a.putValue(Actions.UNDO_PRESENTATION_NAME_KEY, labels.getString("attributeFontUnderline"));
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
     * Creates toolbar buttons and adds them to the specified JToolBar
     */
    public static AbstractButton createToggleGridButton(final DrawingEditor editor) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        final JToggleButton toggleGridButton;
        
        /*
        toggleGridButton = new JToggleButton();
        labels.configureToolBarButton(toggleGridButton, "alignGrid");
        toggleGridButton.setFocusable(false);
        toggleGridButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                Constrainer c;
                if (toggleGridButton.isSelected()) {
                    c = new GridConstrainer(10,10);
                } else {
                    c = new GridConstrainer(1,1);
                }
                for (DrawingView v : editor.getDrawingViews()) {
                    v.setConstrainer(c);
                    v.getComponent().repaint();
                }
            }
        });*/
        
        toggleGridButton = new JToggleButton();
        toggleGridButton.setAction(new ToggleGridAction(editor));
        toggleGridButton.setFocusable(false);
        
        return toggleGridButton;
    }
    /**
     * Creates toolbar buttons and adds them to the specified JToolBar
     */
    public static AbstractButton createToggleGridButton(final DrawingView view) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        final JToggleButton toggleButton;
        
        toggleButton = new JToggleButton();
        labels.configureToolBarButton(toggleButton, "alignGrid");
        toggleButton.setFocusable(false);
        toggleButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                Constrainer c;
                if (toggleButton.isSelected()) {
                    c = new GridConstrainer(10,10);
                } else {
                    c = new GridConstrainer(1,1);
                }
                view.setConstrainer(c);
                view.getComponent().repaint();
            }
        });
        view.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // String constants are interned
                if (evt.getPropertyName() == "gridConstrainer") {
                    Constrainer c = (Constrainer) evt.getNewValue();
                    toggleButton.setSelected(c.isVisible());
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
        labels.configureToolBarButton(popupButton,"attributeStrokeCap");
        popupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_BUTT);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeCapButt"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_ROUND);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeCapRound"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_CAP, BasicStroke.CAP_SQUARE);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeCapSquare"),
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
        labels.configureToolBarButton(popupButton,"attributeStrokeJoin");
        popupButton.setFocusable(false);
        
        HashMap<AttributeKey,Object> attr;
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_BEVEL);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeJoinBevel"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_ROUND);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeJoinRound"),
                null
                )
                );
        attr = new HashMap<AttributeKey,Object>();
        attr.put(STROKE_JOIN, BasicStroke.JOIN_MITER);
        popupButton.add(
                new AttributeAction(
                editor,
                attr,
                labels.getString("attributeStrokeJoinMiter"),
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
