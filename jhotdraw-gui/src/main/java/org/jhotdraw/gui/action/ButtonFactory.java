/*
 * @(#)ButtonFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.action;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FILL_UNDER_STROKE;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.FONT_FACE;
import static org.jhotdraw.draw.AttributeKeys.FONT_ITALIC;
import static org.jhotdraw.draw.AttributeKeys.FONT_UNDERLINE;
import static org.jhotdraw.draw.AttributeKeys.START_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.STROKE_CAP;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;
import static org.jhotdraw.draw.AttributeKeys.STROKE_INNER_WIDTH_FACTOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_JOIN;
import static org.jhotdraw.draw.AttributeKeys.STROKE_PLACEMENT;
import static org.jhotdraw.draw.AttributeKeys.STROKE_TYPE;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.TEXT_COLOR;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.text.StyledEditorKit;
import org.jhotdraw.action.edit.CopyAction;
import org.jhotdraw.action.edit.CutAction;
import org.jhotdraw.action.edit.DuplicateAction;
import org.jhotdraw.action.edit.PasteAction;
import org.jhotdraw.api.app.Disposable;
import org.jhotdraw.color.HSBColorSpace;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.action.AlignAction;
import org.jhotdraw.draw.action.ApplyAttributesAction;
import org.jhotdraw.draw.action.AttributeAction;
import org.jhotdraw.draw.action.AttributeToggler;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.ColorIcon;
import org.jhotdraw.draw.action.DefaultAttributeAction;
import org.jhotdraw.draw.action.DrawingAttributeAction;
import org.jhotdraw.draw.action.DrawingColorChooserAction;
import org.jhotdraw.draw.action.DrawingColorChooserHandler;
import org.jhotdraw.draw.action.DrawingColorIcon;
import org.jhotdraw.draw.action.EditorColorChooserAction;
import org.jhotdraw.draw.action.EditorColorIcon;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.action.LineDecorationIcon;
import org.jhotdraw.draw.action.MoveAction;
import org.jhotdraw.draw.action.PickAttributesAction;
import org.jhotdraw.draw.action.SelectSameAction;
import org.jhotdraw.draw.action.SelectionColorChooserAction;
import org.jhotdraw.draw.action.SelectionColorChooserHandler;
import org.jhotdraw.draw.action.SelectionColorIcon;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.draw.action.StrokeIcon;
import org.jhotdraw.draw.action.UngroupAction;
import org.jhotdraw.draw.action.ZoomAction;
import org.jhotdraw.draw.action.ZoomEditorAction;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.decoration.LineDecoration;
import org.jhotdraw.draw.event.SelectionComponentRepainter;
import org.jhotdraw.draw.event.ToolAdapter;
import org.jhotdraw.draw.event.ToolEvent;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.DoubleStroke;
import org.jhotdraw.gui.JComponentPopup;
import org.jhotdraw.gui.JFontChooser;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.util.ActionUtil;
import org.jhotdraw.util.Images;
import org.jhotdraw.util.Methods;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * ButtonFactory.
 *
 * <p>
 *
 * <p>Design pattern:<br>
 * Name: Abstract Factory.<br>
 * Role: Abstract Factory.<br>
 * Partners: org.jhotdraw.samples.draw.DrawApplicationModel as Client,
 * org.jhotdraw.samples.draw.DrawView as Client, org.jhotdraw.samples.draw.DrawingPanel as Client.
 *
 * <p>
 *
 * <p>FIXME - All buttons created using the ButtonFactory must automatically become
 * disabled/enabled, when the DrawingEditor is disabled/enabled.
 */
public class ButtonFactory {

  /** Mac OS X 'Apple Color Palette'. This palette has 8 columns. */
  public static final java.util.List<ColorIcon> DEFAULT_COLORS;

  static {
    DEFAULT_COLORS = List.of(
        new ColorIcon(0x800000, "Cayenne"),
        new ColorIcon(0x808000, "Asparagus"),
        new ColorIcon(0x008000, "Clover"),
        new ColorIcon(0x008080, "Teal"),
        new ColorIcon(0x000080, "Midnight"),
        new ColorIcon(0x800080, "Plum"),
        new ColorIcon(0x7f7f7f, "Tin"),
        new ColorIcon(0x808080, "Nickel"),
        new ColorIcon(0xff0000, "Maraschino"),
        new ColorIcon(0xffff00, "Lemon"),
        new ColorIcon(0x00ff00, "Spring"),
        new ColorIcon(0x00ffff, "Turquoise"),
        new ColorIcon(0x0000ff, "Blueberry"),
        new ColorIcon(0xff00ff, "Magenta"),
        new ColorIcon(0x666666, "Steel"),
        new ColorIcon(0x999999, "Aluminium"),
        new ColorIcon(0xff6666, "Salmon"),
        new ColorIcon(0xffff66, "Banana"),
        new ColorIcon(0x66ff66, "Flora"),
        new ColorIcon(0x66ffff, "Ice"),
        new ColorIcon(0x6666ff, "Orchid"),
        new ColorIcon(0xff66ff, "Bubblegum"),
        new ColorIcon(0x4c4c4c, "Iron"),
        new ColorIcon(0xb3b3b3, "Magnesium"),
        new ColorIcon(0x804000, "Mocha"),
        new ColorIcon(0x408000, "Fern"),
        new ColorIcon(0x008040, "Moss"),
        new ColorIcon(0x004080, "Ocean"),
        new ColorIcon(0x400080, "Eggplant"),
        new ColorIcon(0x800040, "Maroon"),
        new ColorIcon(0x333333, "Tungsten"),
        new ColorIcon(0xcccccc, "Silver"),
        new ColorIcon(0xff8000, "Tangerine"),
        new ColorIcon(0x80ff00, "Lime"),
        new ColorIcon(0x00ff80, "Sea Foam"),
        new ColorIcon(0x0080ff, "Aqua"),
        new ColorIcon(0x8000ff, "Grape"),
        new ColorIcon(0xff0080, "Strawberry"),
        new ColorIcon(0x191919, "Lead"),
        new ColorIcon(0xe6e6e6, "Mercury"),
        new ColorIcon(0xffcc66, "Cantaloupe"),
        new ColorIcon(0xccff66, "Honeydew"),
        new ColorIcon(0x66ffcc, "Spindrift"),
        new ColorIcon(0x66ccff, "Sky"),
        new ColorIcon(0xcc66ff, "Lavender"),
        new ColorIcon(0xff6fcf, "Carnation"),
        new ColorIcon(0x000000, "Licorice"),
        new ColorIcon(0xffffff, "Snow"));
  }

  public static final int DEFAULT_COLORS_COLUMN_COUNT = 8;

  /**
   * Websave color palette as used by Macromedia Fireworks. This palette has 19 columns. The
   * leftmost column contains a redundant set of color icons to make selection of gray scales and of
   * the primary colors easier.
   */
  public static final java.util.List<ColorIcon> WEBSAVE_COLORS;

  static {
    List<ColorIcon> m = new ArrayList<>();
    for (int b = 0; b <= 0xff; b += 0x33) {
      int rgb = (b << 16) | (b << 8) | b;
      m.add(new ColorIcon(rgb));
      for (int r = 0; r <= 0x66; r += 0x33) {
        for (int g = 0; g <= 0xff; g += 0x33) {
          rgb = (r << 16) | (g << 8) | b;
          m.add(new ColorIcon(rgb));
        }
      }
    }
    int[] firstColumn = {0xff0000, 0x00ff00, 0x0000ff, 0xff00ff, 0x00ffff, 0xffff00};
    for (int b = 0x0, i = 0; b <= 0xff; b += 0x33, i++) {
      int rgb = (b << 16) | (b << 8) | b;
      m.add(new ColorIcon(firstColumn[i]));
      for (int r = 0x99; r <= 0xff; r += 0x33) {
        for (int g = 0; g <= 0xff; g += 0x33) {
          rgb = 0xff000000 | (r << 16) | (g << 8) | b;
          m.add(new ColorIcon(rgb, "#" + Integer.toHexString(rgb).substring(2)));
        }
      }
    }
    WEBSAVE_COLORS = Collections.unmodifiableList(m);
  }

  public static final int WEBSAVE_COLORS_COLUMN_COUNT = 19;

  /**
   * HSB color palette with a set of colors chosen based on a physical criteria.
   *
   * <p>
   *
   * <p>This is a 'human friendly' color palette which arranges the color in a way that makes it
   * easy for humans to select the desired color. The colors are ordered in a way which minimizes
   * the color contrast effect in the human visual system.
   *
   * <p>
   *
   * <p>This palette has 12 columns and 10 rows.
   *
   * <p>
   *
   * <p>The topmost row contains a null-color and a gray scale from white to black in 10 percent
   * steps.
   *
   * <p>
   *
   * <p>The remaining rows contain colors taken from the outer hull of the HSB color model:
   *
   * <p>
   *
   * <p>The columns are ordered by hue starting with red - the lowest wavelength - and ending with
   * purple - the highest wavelength. There are 12 different hues, so that all primary colors with
   * their additive complements can be selected.
   *
   * <p>
   *
   * <p>The rows are orderd by brightness with the brightest color at the top (sky) and the darkest
   * color at the bottom (earth). The first 5 rows contain colors with maximal brightness and a
   * saturation ranging form 20% up to 100%. The remaining 4 rows contain colors with maximal
   * saturation and a brightness ranging from 90% to 20% (this also makes for a range from 100% to
   * 20% if the 5th row is taken into account).
   */
  public static final java.util.List<ColorIcon> HSB_COLORS;

  public static final int HSB_COLORS_COLUMN_COUNT = 12;

  /**
   * This is the same palette as HSB_COLORS, but all color values are specified in the sRGB color
   * space.
   */
  public static final java.util.List<ColorIcon> HSB_COLORS_AS_RGB;

  public static final int HSB_COLORS_AS_RGB_COLUMN_COUNT = 12;

  static {
    ColorSpace grayCS = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    HSBColorSpace hsbCS = HSBColorSpace.getInstance();
    List<ColorIcon> m = new ArrayList<>();
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    m.add(new ColorIcon(
        new Color(0, true), labels.getToolTipTextProperty("attribute.color.noColor")));
    for (int b = 10; b >= 0; b--) {
      Color c = new Color(grayCS, new float[] {b / 10f}, 1f);
      m.add(new ColorIcon(
          c, labels.getFormatted("attribute.color.grayComponents.toolTipText", b * 10)));
    }
    for (int s = 2; s <= 8; s += 2) {
      for (int h = 0; h < 12; h++) {
        Color c = new Color(hsbCS, new float[] {(h) / 12f, s * 0.1f, 1f}, 1f);
        m.add(new ColorIcon(
            c,
            labels.getFormatted(
                "attribute.color.hsbComponents.toolTipText", h * 360 / 12, s * 10, 100)));
      }
    }
    for (int b = 10; b >= 2; b -= 2) {
      for (int h = 0; h < 12; h++) {
        Color c = new Color(hsbCS, new float[] {(h) / 12f, 1f, b * 0.1f}, 1f);
        m.add(new ColorIcon(
            new Color(hsbCS, new float[] {(h) / 12f, 1f, b * 0.1f}, 1f),
            labels.getFormatted(
                "attribute.color.hsbComponents.toolTipText", h * 360 / 12, 100, b * 10)));
      }
    }
    HSB_COLORS = Collections.unmodifiableList(m);
    m = new ArrayList<>();
    for (ColorIcon ci : HSB_COLORS) {
      if (ci.getColor() == null) {
        m.add(new ColorIcon(
            new Color(0, true), labels.getToolTipTextProperty("attribute.color.noColor")));
      } else {
        Color c = ci.getColor();
        c = c.getColorSpace() == grayCS
            ? new Color(
                c.getGreen(),
                c.getGreen(),
                c.getGreen(),
                c.getAlpha()) // workaround for rounding error
            : new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        m.add(new ColorIcon(
            c,
            labels.getFormatted(
                "attribute.color.rgbComponents.toolTipText",
                c.getRed(),
                c.getGreen(),
                c.getBlue())));
      }
    }
    HSB_COLORS_AS_RGB = Collections.unmodifiableList(m);
  }

  private static class ToolButtonListener implements ItemListener {

    private Tool tool;
    private DrawingEditor editor;

    public ToolButtonListener(Tool t, DrawingEditor editor) {
      this.tool = t;
      this.editor = editor;
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        editor.setTool(tool);
      }
    }
  }

  /** Prevent instance creation. */
  private ButtonFactory() {}

  public static Collection<Action> createDrawingActions(DrawingEditor editor) {
    return createDrawingActions(editor, new ArrayList<>());
  }

  public static Collection<Action> createDrawingActions(
      DrawingEditor editor, java.util.List<Disposable> dsp) {
    List<Action> list = new ArrayList<>();
    AbstractSelectedAction a;
    list.add(new CutAction());
    list.add(new CopyAction());
    list.add(new PasteAction());
    list.add(a = new SelectSameAction(editor));
    dsp.add(a);
    return list;
  }

  public static Collection<Action> createSelectionActions(DrawingEditor editor) {
    List<Action> a = new ArrayList<>();
    a.add(new DuplicateAction());
    a.add(null); // separator
    a.add(new GroupAction(editor));
    a.add(new UngroupAction(editor));
    a.add(null); // separator
    a.add(new BringToFrontAction(editor));
    a.add(new SendToBackAction(editor));
    return a;
  }

  public static JToggleButton addSelectionToolTo(JToolBar tb, final DrawingEditor editor) {
    return addSelectionToolTo(
        tb, editor, createDrawingActions(editor), createSelectionActions(editor));
  }

  public static JToggleButton addSelectionToolTo(
      JToolBar tb,
      final DrawingEditor editor,
      Collection<Action> drawingActions,
      Collection<Action> selectionActions) {
    Tool selectionTool = new DelegationSelectionTool(drawingActions, selectionActions);
    return addSelectionToolTo(tb, editor, selectionTool);
  }

  public static JToggleButton addSelectionToolTo(
      JToolBar tb, final DrawingEditor editor, Tool selectionTool) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    JToggleButton t;
    Tool tool;
    HashMap<String, Object> attributes;
    ButtonGroup group;
    if (tb.getClientProperty("toolButtonGroup") instanceof ButtonGroup) {
      group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
    } else {
      group = new ButtonGroup();
      tb.putClientProperty("toolButtonGroup", group);
    }
    // Selection tool
    editor.setTool(selectionTool);
    t = new JToggleButton();
    final JToggleButton defaultToolButton = t;
    if (!(tb.getClientProperty("toolHandler") instanceof ToolListener)) {
      ToolListener toolHandler;
      toolHandler = new ToolAdapter() {
        @Override
        public void toolDone(ToolEvent event) {
          defaultToolButton.setSelected(true);
        }
      };
      tb.putClientProperty("toolHandler", toolHandler);
    }
    labels.configureToolBarButton(t, "selectionTool");
    t.setSelected(true);
    t.addItemListener(new ToolButtonListener(selectionTool, editor));
    t.setFocusable(false);
    group.add(t);
    tb.add(t);
    return t;
  }

  /** Method addSelectionToolTo must have been invoked prior to this on the JToolBar. */
  public static JToggleButton addToolTo(
      JToolBar tb, DrawingEditor editor, Tool tool, String labelKey, ResourceBundleUtil labels) {
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
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    final JPopupButton zoomPopupButton = new JPopupButton();
    labels.configureToolBarButton(zoomPopupButton, "view.zoomFactor");
    zoomPopupButton.setFocusable(false);
    if (editor.getDrawingViews().size() == 0) {
      zoomPopupButton.setText("100 %");
    } else {
      zoomPopupButton.setText(
          (int) (editor.getDrawingViews().iterator().next().getScaleFactor() * 100) + " %");
    }
    editor.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // String constants are interned
        if ((evt.getPropertyName() == null && DrawingEditor.ACTIVE_VIEW_PROPERTY == null)
            || (evt.getPropertyName() != null
                && evt.getPropertyName().equals(DrawingEditor.ACTIVE_VIEW_PROPERTY))) {
          if (evt.getNewValue() == null) {
            zoomPopupButton.setText("100 %");
          } else {
            zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
          }
        }
      }
    });
    double[] factors = {16, 8, 5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};
    for (int i = 0; i < factors.length; i++) {
      zoomPopupButton.add(new ZoomEditorAction(editor, factors[i], zoomPopupButton) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          super.actionPerformed(e);
          zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
        }
      });
    }
    // zoomPopupButton.setPreferredSize(new Dimension(16,16));
    zoomPopupButton.setFocusable(false);
    return zoomPopupButton;
  }

  public static AbstractButton createZoomButton(DrawingView view) {
    return createZoomButton(view, new double[] {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10});
  }

  public static AbstractButton createZoomButton(final DrawingView view, double[] factors) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    final JPopupButton zoomPopupButton = new JPopupButton();
    labels.configureToolBarButton(zoomPopupButton, "view.zoomFactor");
    zoomPopupButton.setFocusable(false);
    zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
    view.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // String constants are interned
        if ("scaleFactor".equals(evt.getPropertyName())) {
          zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
        }
      }
    });
    for (int i = 0; i < factors.length; i++) {
      zoomPopupButton.add(new ZoomAction(view, factors[i], zoomPopupButton) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          super.actionPerformed(e);
          zoomPopupButton.setText((int) (view.getScaleFactor() * 100) + " %");
        }
      });
    }
    // zoomPopupButton.setPreferredSize(new Dimension(16,16));
    zoomPopupButton.setFocusable(false);
    return zoomPopupButton;
  }

  /** Creates toolbar buttons and adds them to the specified JToolBar */
  public static void addAttributesButtonsTo(JToolBar bar, DrawingEditor editor) {
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

  public static void addColorButtonsTo(
      JToolBar bar, DrawingEditor editor, java.util.List<ColorIcon> colors, int columnCount) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    bar.add(createEditorColorButton(
        editor,
        STROKE_COLOR,
        colors,
        columnCount,
        "attribute.strokeColor",
        labels,
        new HashMap<>()));
    bar.add(createEditorColorButton(
        editor, FILL_COLOR, colors, columnCount, "attribute.fillColor", labels, new HashMap<>()));
    bar.add(createEditorColorButton(
        editor, TEXT_COLOR, colors, columnCount, "attribute.textColor", labels, new HashMap<>()));
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Microsoft Office:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks on the action region, the default color of the DrawingEditor is
   *       applied to the selected figures.
   *   <li>When the user opens the popup menu, a color palette is displayed. Choosing a color from
   *       the palette changes the default color of the editor and also changes the color of the
   *       selected figures.
   *   <li>A rectangle on the color button displays the current default color of the DrawingEditor.
   *       The rectangle has the dimensions 1, 17, 20, 4 (x, y, width, height).
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a LinkedMap,
   *     so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   */
  public static JPopupButton createEditorColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels) {
    return createEditorColorButton(
        editor, attributeKey, swatches, columnCount, labelKey, labels, null);
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Microsoft Office:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks on the action region, the default color of the DrawingEditor is
   *       applied to the selected figures.
   *   <li>When the user opens the popup menu, a color palette is displayed. Choosing a color from
   *       the palette changes the default color of the editor and also changes the color of the
   *       selected figures.
   *   <li>A rectangle on the color button displays the current default color of the DrawingEditor.
   *       The rectangle has the dimensions 1, 17, 20, 4 (x, y, width, height).
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a LinkedMap,
   *     so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   */
  public static JPopupButton createEditorColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes) {
    return createEditorColorButton(
        editor,
        attributeKey,
        swatches,
        columnCount,
        labelKey,
        labels,
        defaultAttributes,
        new Rectangle(1, 17, 20, 4));
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Microsoft Office:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks on the action region, the default color of the DrawingEditor is
   *       applied to the selected figures.
   *   <li>When the user opens the popup menu, a color palette is displayed. Choosing a color from
   *       the palette changes the default color of the editor and also changes the color of the
   *       selected figures.
   *   <li>A shape on the color button displays the current default color of the DrawingEditor.
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   * @param colorShape This shape is superimposed on the icon of the button. The shape is drawn with
   *     the default color of the DrawingEditor.
   */
  public static JPopupButton createEditorColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape) {
    final JPopupButton popupButton = new JPopupButton();
    popupButton.setPopupAlpha(1f);
    if (defaultAttributes == null) {
      defaultAttributes = new HashMap<>();
    }
    popupButton.setAction(
        new DefaultAttributeAction(editor, attributeKey, defaultAttributes),
        new Rectangle(0, 0, 22, 22));
    popupButton.setColumnCount(columnCount, false);
    boolean hasNullColor = false;
    for (ColorIcon swatch : swatches) {
      AttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      Color swatchColor = swatch.getColor();
      attributes.put(attributeKey, swatchColor);
      if (swatchColor == null || swatchColor.getAlpha() == 0) {
        hasNullColor = true;
      }
      popupButton.add(
          a = new AttributeAction(
              editor, attributes, labels.getToolTipTextProperty(labelKey), swatch));
      a.putValue(Action.SHORT_DESCRIPTION, swatch.getName());
      a.setUpdateEnabledState(false);
    }
    // No color
    if (!hasNullColor) {
      AttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      attributes.put(attributeKey, null);
      popupButton.add(
          a = new AttributeAction(
              editor,
              attributes,
              labels.getToolTipTextProperty("attribute.color.noColor"),
              new ColorIcon(
                  null,
                  labels.getToolTipTextProperty("attribute.color.noColor"),
                  swatches.get(0).getIconWidth(),
                  swatches.get(0).getIconHeight())));
      a.putValue(
          Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.noColor"));
      a.setUpdateEnabledState(false);
    }
    // Color chooser
    ImageIcon chooserIcon = new ImageIcon(Images.createImage(
        ButtonFactory.class, "/org/jhotdraw/draw/action/images/attribute.color.colorChooser.png"));
    Action a;
    popupButton.add(
        a = new EditorColorChooserAction(
            editor, attributeKey, "color", chooserIcon, defaultAttributes));
    labels.configureToolBarButton(popupButton, labelKey);
    a.putValue(
        Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.colorChooser"));
    Icon icon = new EditorColorIcon(
        editor,
        attributeKey,
        labels.getLargeIconProperty(labelKey, ButtonFactory.class).getImage(),
        colorShape);
    popupButton.setIcon(icon);
    popupButton.setDisabledIcon(icon);
    popupButton.setFocusable(false);
    editor.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        popupButton.repaint();
      }
    });
    return popupButton;
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Adobe Fireworks:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks at the button a popup menu with a color palette is displayed.
   *       Choosing a color from the palette changes the default color of the editor and also
   *       changes the color of the selected figures.
   *   <li>A shape on the color button displays the color of the selected figures. If no figures are
   *       selected, the default color of the DrawingEditor is displayed.
   *   <li>A rectangle on the color button displays the current default color of the DrawingEditor.
   *       The rectangle has the dimensions 1, 17, 20, 4 (x, y, width, height).
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   */
  public static JPopupButton createSelectionColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels) {
    return createSelectionColorButton(
        editor, attributeKey, swatches, columnCount, labelKey, labels, null);
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Adobe Fireworks:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks at the button a popup menu with a color palette is displayed.
   *       Choosing a color from the palette changes the default color of the editor and also
   *       changes the color of the selected figures.
   *   <li>A rectangle on the color button displays the current default color of the DrawingEditor.
   *       The rectangle has the dimensions 1, 17, 20, 4 (x, y, width, height).
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   */
  public static JPopupButton createSelectionColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes) {
    return createSelectionColorButton(
        editor,
        attributeKey,
        swatches,
        columnCount,
        labelKey,
        labels,
        defaultAttributes,
        new Rectangle(1, 17, 20, 4));
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Adobe Fireworks:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks at the button a popup menu with a color palette is displayed.
   *       Choosing a color from the palette changes the default color of the editor and also
   *       changes the color of the selected figures.
   *   <li>A shape on the color button displays the color of the selected figures. If no figures are
   *       selected, the default color of the DrawingEditor is displayed.
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   * @param colorShape This shape is superimposed on the icon of the button. The shape is drawn with
   *     the default color of the DrawingEditor.
   */
  public static JPopupButton createSelectionColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape) {
    return createSelectionColorButton(
        editor,
        attributeKey,
        swatches,
        columnCount,
        labelKey,
        labels,
        defaultAttributes,
        colorShape,
        new ArrayList<>());
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button works like the color
   * button in Adobe Fireworks:
   *
   * <p>
   *
   * <ul>
   *   <li>When the user clicks at the button a popup menu with a color palette is displayed.
   *       Choosing a color from the palette changes the default color of the editor and also
   *       changes the color of the selected figures.
   *   <li>A shape on the color button displays the color of the selected figures. If no figures are
   *       selected, the default color of the DrawingEditor is displayed.
   * </ul>
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   * @param colorShape This shape is superimposed on the icon of the button. The shape is drawn with
   *     the default color of the DrawingEditor.
   */
  public static JPopupButton createSelectionColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      java.util.List<Disposable> dsp) {
    final JPopupButton popupButton = new JPopupButton();
    popupButton.setPopupAlpha(1f);
    if (defaultAttributes == null) {
      defaultAttributes = new HashMap<>();
    }
    popupButton.setColumnCount(columnCount, false);
    boolean hasNullColor = false;
    for (ColorIcon swatch : swatches) {
      AttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      if (swatch != null) {
        Color swatchColor = swatch.getColor();
        attributes.put(attributeKey, swatchColor);
        if (swatchColor == null || swatchColor.getAlpha() == 0) {
          hasNullColor = true;
        }
        popupButton.add(
            a = new AttributeAction(
                editor, attributes, labels.getToolTipTextProperty(labelKey), swatch));
        a.putValue(Action.SHORT_DESCRIPTION, swatch.getName());
        a.setUpdateEnabledState(false);
        dsp.add(a);
      } else {
        popupButton.add(new JPanel());
      }
    }
    // No color
    if (!hasNullColor) {
      AttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      attributes.put(attributeKey, null);
      popupButton.add(
          a = new AttributeAction(
              editor,
              attributes,
              labels.getToolTipTextProperty("attribute.color.noColor"),
              new ColorIcon(null, labels.getToolTipTextProperty("attribute.color.noColor"))));
      a.putValue(
          Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.noColor"));
      a.setUpdateEnabledState(false);
      dsp.add(a);
    }
    // Color chooser
    ImageIcon chooserIcon = new ImageIcon(Images.createImage(
        ButtonFactory.class, "/org/jhotdraw/draw/action/images/attribute.color.colorChooser.png"));
    AttributeAction a;
    popupButton.add(
        a = new SelectionColorChooserAction(
            editor,
            attributeKey,
            labels.getToolTipTextProperty("attribute.color.colorChooser"),
            chooserIcon,
            defaultAttributes));
    a.putValue(
        Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.colorChooser"));
    dsp.add(a);
    labels.configureToolBarButton(popupButton, labelKey);
    Icon icon = new SelectionColorIcon(
        editor,
        attributeKey,
        labels.getLargeIconProperty(labelKey, ButtonFactory.class).getImage(),
        colorShape);
    popupButton.setIcon(icon);
    popupButton.setDisabledIcon(icon);
    popupButton.setFocusable(false);
    dsp.add(new SelectionComponentRepainter(editor, popupButton));
    return popupButton;
  }

  public static JPopupButton createSelectionColorChooserButton(
      final DrawingEditor editor,
      final AttributeKey<Color> attributeKey,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      final java.util.List<Disposable> dsp) {
    return createSelectionColorChooserButton(
        editor, attributeKey, labelKey, labels, defaultAttributes, colorShape, null, dsp);
  }

  public static JPopupButton createSelectionColorChooserButton(
      final DrawingEditor editor,
      final AttributeKey<Color> attributeKey,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      final Class<?> uiclass,
      final java.util.List<Disposable> dsp) {
    JPopupButton popupButton;
    popupButton = new JPopupButton();
    labels.configureToolBarButton(popupButton, labelKey);
    popupButton.setFocusable(true);
    popupButton.setRequestFocusEnabled(false);
    // We lazily initialize the popup menu because creating a JColorChooser
    // takes a lot of time.
    JComponentPopup popupMenu = new JComponentPopup() {
      private static final long serialVersionUID = 1L;
      private JColorChooser colorChooser;

      @Override
      public void show(Component invoker, int x, int y) {
        if (colorChooser == null) {
          initialize();
        }
        Color c;
        if (editor.getActiveView() != null && editor.getActiveView().getSelectionCount() > 0) {
          c = editor
              .getActiveView()
              .getSelectedFigures()
              .iterator()
              .next()
              .attr()
              .get(attributeKey);
        } else {
          c = editor.getDefaultAttribute(attributeKey);
        }
        colorChooser.setColor(c == null ? new Color(0, true) : c);
        super.show(invoker, x, y);
      }

      private void initialize() {
        colorChooser = new JColorChooser();
        colorChooser.setOpaque(true);
        colorChooser.setBackground(Color.WHITE);
        if (uiclass != null) {
          try {
            colorChooser.setUI((ColorChooserUI) Methods.invokeStatic(
                uiclass, "createUI", new Class<?>[] {JComponent.class}, new Object[] {colorChooser
                }));
          } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
          }
        }
        dsp.add(new SelectionColorChooserHandler(editor, attributeKey, colorChooser, this));
        add(colorChooser);
      }
    };
    popupButton.setPopupMenu(popupMenu);
    popupButton.setPopupAlpha(1.0f); // must be set after we set the popup menu
    Icon icon = new SelectionColorIcon(
        editor,
        attributeKey,
        labels.getLargeIconProperty(labelKey, ButtonFactory.class).getImage(),
        colorShape);
    popupButton.setIcon(icon);
    popupButton.setDisabledIcon(icon);
    popupButton.setFocusable(false);
    if (dsp != null) {
      dsp.add(new SelectionComponentRepainter(editor, popupButton));
    }
    return popupButton;
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button acts on attributes
   * of the Drawing object in the current DrawingView of the DrawingEditor.
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   */
  public static JPopupButton createDrawingColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels) {
    return createDrawingColorButton(
        editor, attributeKey, swatches, columnCount, labelKey, labels, null);
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button acts on attributes
   * of the Drawing object in the current DrawingView of the DrawingEditor.
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   */
  public static JPopupButton createDrawingColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes) {
    return createDrawingColorButton(
        editor,
        attributeKey,
        swatches,
        columnCount,
        labelKey,
        labels,
        defaultAttributes,
        new Rectangle(1, 17, 20, 4));
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button acts on attributes
   * of the Drawing object in the current DrawingView of the DrawingEditor.
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   * @param colorShape This shape is superimposed on the icon of the button. The shape is drawn with
   *     the default color of the DrawingEditor.
   */
  public static JPopupButton createDrawingColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape) {
    return createDrawingColorButton(
        editor,
        attributeKey,
        swatches,
        columnCount,
        labelKey,
        labels,
        defaultAttributes,
        colorShape,
        new ArrayList<>());
  }

  /**
   * Creates a color button, with an action region and a popup menu. The button acts on attributes
   * of the Drawing object in the current DrawingView of the DrawingEditor.
   *
   * @param editor The DrawingEditor.
   * @param attributeKey The AttributeKey of the color.
   * @param swatches A list with labeled colors containing the color palette of the popup menu. The
   *     actual labels are retrieved from the supplied resource bundle. This is usually a
   *     LinkedHashMap, so that the colors have a predictable order.
   * @param columnCount The number of columns of the color palette.
   * @param labelKey The resource bundle key used for retrieving the icon and the tooltip of the
   *     button.
   * @param labels The resource bundle.
   * @param defaultAttributes A set of attributes which are also applied to the selected figures,
   *     when a color is selected. This can be used, to set attributes that otherwise prevent the
   *     color from being shown. For example, when the color attribute is set, we wan't the gradient
   *     attribute of the Figure to be cleared.
   * @param colorShape This shape is superimposed on the icon of the button. The shape is drawn with
   *     the default color of the DrawingEditor.
   */
  public static JPopupButton createDrawingColorButton(
      DrawingEditor editor,
      AttributeKey<Color> attributeKey,
      java.util.List<ColorIcon> swatches,
      int columnCount,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      java.util.List<Disposable> dsp) {
    final JPopupButton popupButton = new JPopupButton();
    popupButton.setPopupAlpha(1f);
    if (defaultAttributes == null) {
      defaultAttributes = new HashMap<>();
    }
    popupButton.setColumnCount(columnCount, false);
    boolean hasNullColor = false;
    for (ColorIcon swatch : swatches) {
      DrawingAttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      if (swatch != null) {
        Color swatchColor = swatch.getColor();
        attributes.put(attributeKey, swatchColor);
        if (swatchColor == null || swatchColor.getAlpha() == 0) {
          hasNullColor = true;
        }
        popupButton.add(
            a = new DrawingAttributeAction(
                editor, attributes, labels.getToolTipTextProperty(labelKey), swatch));
        dsp.add(a);
        a.putValue(Action.SHORT_DESCRIPTION, swatch.getName());
        a.setUpdateEnabledState(false);
      } else {
        popupButton.add(new JPanel());
      }
    }
    // No color
    if (!hasNullColor) {
      DrawingAttributeAction a;
      HashMap<AttributeKey<?>, Object> attributes = new HashMap<>(defaultAttributes);
      attributes.put(attributeKey, null);
      popupButton.add(
          a = new DrawingAttributeAction(
              editor,
              attributes,
              labels.getToolTipTextProperty("attribute.color.noColor"),
              new ColorIcon(null, labels.getToolTipTextProperty("attribute.color.noColor"))));
      dsp.add(a);
      a.putValue(
          Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.noColor"));
      a.setUpdateEnabledState(false);
    }
    // Color chooser
    ImageIcon chooserIcon = new ImageIcon(Images.createImage(
        ButtonFactory.class, "/org/jhotdraw/draw/action/images/attribute.color.colorChooser.png"));
    DrawingColorChooserAction a;
    popupButton.add(
        a = new DrawingColorChooserAction(
            editor, attributeKey, "color", chooserIcon, defaultAttributes));
    dsp.add(a);
    labels.configureToolBarButton(popupButton, labelKey);
    a.putValue(
        Action.SHORT_DESCRIPTION, labels.getToolTipTextProperty("attribute.color.colorChooser"));
    Icon icon = new DrawingColorIcon(
        editor,
        attributeKey,
        labels.getLargeIconProperty(labelKey, ButtonFactory.class).getImage(),
        colorShape);
    popupButton.setIcon(icon);
    popupButton.setDisabledIcon(icon);
    popupButton.setFocusable(false);
    if (editor != null) {
      dsp.add(new SelectionComponentRepainter(editor, popupButton));
    }
    return popupButton;
  }

  public static JPopupButton createDrawingColorChooserButton(
      final DrawingEditor editor,
      final AttributeKey<Color> attributeKey,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      final java.util.List<Disposable> dsp) {
    return createSelectionColorChooserButton(
        editor, attributeKey, labelKey, labels, defaultAttributes, colorShape, null, dsp);
  }

  public static JPopupButton createDrawingColorChooserButton(
      final DrawingEditor editor,
      final AttributeKey<Color> attributeKey,
      String labelKey,
      ResourceBundleUtil labels,
      Map<AttributeKey<?>, Object> defaultAttributes,
      Shape colorShape,
      final Class<?> uiclass,
      final java.util.List<Disposable> dsp) {
    JPopupButton popupButton;
    popupButton = new JPopupButton();
    labels.configureToolBarButton(popupButton, labelKey);
    popupButton.setFocusable(true);
    popupButton.setRequestFocusEnabled(false);
    // We lazily initialize the popup menu because creating a JColorChooser
    // takes a lot of time.
    JComponentPopup popupMenu = new JComponentPopup() {
      private static final long serialVersionUID = 1L;
      private JColorChooser colorChooser;

      @Override
      public void show(Component invoker, int x, int y) {
        if (colorChooser == null) {
          initialize();
        }
        Color c;
        if (editor.getActiveView() != null) {
          c = editor.getActiveView().getDrawing().attr().get(attributeKey);
        } else {
          c = editor.getDefaultAttribute(attributeKey);
        }
        colorChooser.setColor(c == null ? new Color(0, true) : c);
        super.show(invoker, x, y);
      }

      private void initialize() {
        colorChooser = new JColorChooser();
        colorChooser.setOpaque(true);
        colorChooser.setBackground(Color.WHITE);
        if (uiclass != null) {
          try {
            colorChooser.setUI((ColorChooserUI) Methods.invokeStatic(
                uiclass, "createUI", new Class<?>[] {JComponent.class}, new Object[] {colorChooser
                }));
          } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
          }
        }
        dsp.add(new DrawingColorChooserHandler(editor, attributeKey, colorChooser, this));
        add(colorChooser);
      }
    };
    popupButton.setPopupMenu(popupMenu);
    popupButton.setPopupAlpha(1.0f); // must be set after we set the popup menu
    Icon icon = new DrawingColorIcon(
        editor,
        attributeKey,
        labels.getLargeIconProperty(labelKey, ButtonFactory.class).getImage(),
        colorShape);
    popupButton.setIcon(icon);
    popupButton.setDisabledIcon(icon);
    popupButton.setFocusable(false);
    if (dsp != null) {
      dsp.add(new SelectionComponentRepainter(editor, popupButton));
    }
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
        new double[] {0d, 0.5d, 1d, 2d, 3d, 5d, 9d, 13d},
        ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeWidthButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createStrokeWidthButton(editor, new double[] {0.5d, 1d, 2d, 3d, 5d, 9d, 13d}, labels);
  }

  public static JPopupButton createStrokeWidthButton(DrawingEditor editor, double[] widths) {
    return createStrokeWidthButton(
        editor,
        new double[] {0.5d, 1d, 2d, 3d, 5d, 9d, 13d},
        ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeWidthButton(
      DrawingEditor editor, double[] widths, ResourceBundleUtil labels) {
    JPopupButton strokeWidthPopupButton = new JPopupButton();
    labels.configureToolBarButton(strokeWidthPopupButton, "attribute.strokeWidth");
    strokeWidthPopupButton.setFocusable(false);
    NumberFormat formatter = NumberFormat.getInstance();
    if (formatter instanceof DecimalFormat) {
      ((DecimalFormat) formatter).setMaximumFractionDigits(1);
      ((DecimalFormat) formatter).setMinimumFractionDigits(0);
    }
    for (int i = 0; i < widths.length; i++) {
      String label = Double.toString(widths[i]);
      Icon icon = new StrokeIcon(
          new BasicStroke((float) widths[i], BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
      AttributeAction a = new AttributeAction(editor, STROKE_WIDTH, widths[i], label, icon);
      a.putValue(
          ActionUtil.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.strokeWidth.text"));
      AbstractButton btn = strokeWidthPopupButton.add(a);
      btn.setDisabledIcon(icon);
    }
    return strokeWidthPopupButton;
  }

  public static JPopupButton createStrokeDecorationButton(DrawingEditor editor) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    JPopupButton strokeDecorationPopupButton = new JPopupButton();
    labels.configureToolBarButton(strokeDecorationPopupButton, "attribute.strokeDecoration");
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
    for (LineDecoration decoration : decorations) {
      strokeDecorationPopupButton.add(new AttributeAction(
          editor, START_DECORATION, decoration, null, new LineDecorationIcon(decoration, true)));
      strokeDecorationPopupButton.add(new AttributeAction(
          editor, END_DECORATION, decoration, null, new LineDecorationIcon(decoration, false)));
    }
    return strokeDecorationPopupButton;
  }

  public static JPopupButton createStrokeDashesButton(DrawingEditor editor) {
    return createStrokeDashesButton(
        editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeDashesButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createStrokeDashesButton(
        editor,
        new double[][] {null, {4d, 4d}, {2d, 2d}, {4d, 2d}, {2d, 4d}, {8d, 2d}, {6d, 2d, 2d, 2d}},
        labels);
  }

  public static JPopupButton createStrokeDashesButton(DrawingEditor editor, double[][] dashes) {
    return createStrokeDashesButton(
        editor, dashes, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeDashesButton(
      DrawingEditor editor, double[][] dashes, ResourceBundleUtil labels) {
    return createStrokeDashesButton(editor, dashes, labels, new ArrayList<>());
  }

  public static JPopupButton createStrokeDashesButton(
      DrawingEditor editor,
      double[][] dashes,
      ResourceBundleUtil labels,
      java.util.List<Disposable> dsp) {
    JPopupButton strokeDashesPopupButton = new JPopupButton();
    labels.configureToolBarButton(strokeDashesPopupButton, "attribute.strokeDashes");
    strokeDashesPopupButton.setFocusable(false);
    // strokeDashesPopupButton.setColumnCount(2, false);
    for (double[] dashe : dashes) {
      float[] fdashes;
      if (dashe == null) {
        fdashes = null;
      } else {
        fdashes = new float[dashe.length];
        for (int j = 0; j < dashe.length; j++) {
          fdashes[j] = (float) dashe[j];
        }
      }
      Icon icon = new StrokeIcon(
          new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, fdashes, 0));
      AttributeAction a;
      AbstractButton btn = strokeDashesPopupButton.add(
          a = new AttributeAction(editor, STROKE_DASHES, dashe, null, icon));
      dsp.add(a);
      btn.setDisabledIcon(icon);
    }
    return strokeDashesPopupButton;
  }

  public static JPopupButton createStrokeTypeButton(DrawingEditor editor) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    JPopupButton strokeTypePopupButton = new JPopupButton();
    labels.configureToolBarButton(strokeTypePopupButton, "attribute.strokeType");
    strokeTypePopupButton.setFocusable(false);
    strokeTypePopupButton.add(new AttributeAction(
        editor,
        STROKE_TYPE,
        AttributeKeys.StrokeType.BASIC,
        labels.getString("attribute.strokeType.basic"),
        new StrokeIcon(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL))));
    HashMap<AttributeKey<?>, Object> attr = new HashMap<>();
    attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
    attr.put(STROKE_INNER_WIDTH_FACTOR, 2d);
    strokeTypePopupButton.add(new AttributeAction(
        editor,
        attr,
        labels.getString("attribute.strokeType.double"),
        new StrokeIcon(new DoubleStroke(2, 1))));
    attr = new HashMap<>();
    attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
    attr.put(STROKE_INNER_WIDTH_FACTOR, 3d);
    strokeTypePopupButton.add(new AttributeAction(
        editor,
        attr,
        labels.getString("attribute.strokeType.double"),
        new StrokeIcon(new DoubleStroke(3, 1))));
    attr = new HashMap<>();
    attr.put(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
    attr.put(STROKE_INNER_WIDTH_FACTOR, 4d);
    strokeTypePopupButton.add(new AttributeAction(
        editor,
        attr,
        labels.getString("attribute.strokeType.double"),
        new StrokeIcon(new DoubleStroke(4, 1))));
    return strokeTypePopupButton;
  }

  public static JPopupButton createStrokePlacementButton(DrawingEditor editor) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    JPopupButton strokePlacementPopupButton = new JPopupButton();
    labels.configureToolBarButton(strokePlacementPopupButton, "attribute.strokePlacement");
    strokePlacementPopupButton.setFocusable(false);
    HashMap<AttributeKey<?>, Object> attr;
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.center"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.inside"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.CENTER);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.outside"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.centerFilled"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.insideFilled"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.FULL);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.outsideFilled"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.CENTER);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.centerUnfilled"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.INSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.insideUnfilled"), null));
    attr = new HashMap<>();
    attr.put(STROKE_PLACEMENT, AttributeKeys.StrokePlacement.OUTSIDE);
    attr.put(FILL_UNDER_STROKE, AttributeKeys.Underfill.NONE);
    strokePlacementPopupButton.add(new AttributeAction(
        editor, attr, labels.getString("attribute.strokePlacement.outsideUnfilled"), null));
    return strokePlacementPopupButton;
  }

  public static void addFontButtonsTo(JToolBar bar, DrawingEditor editor) {
    bar.add(createFontButton(editor));
    bar.add(createFontStyleBoldButton(editor));
    bar.add(createFontStyleItalicButton(editor));
    bar.add(createFontStyleUnderlineButton(editor));
  }

  public static JPopupButton createFontButton(DrawingEditor editor) {
    return createFontButton(editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createFontButton(DrawingEditor editor, ResourceBundleUtil labels) {
    return createFontButton(editor, FONT_FACE, labels);
  }

  public static JPopupButton createFontButton(
      DrawingEditor editor, AttributeKey<Font> key, ResourceBundleUtil labels) {
    return createFontButton(editor, key, labels, new ArrayList<>());
  }

  public static JPopupButton createFontButton(
      DrawingEditor editor,
      AttributeKey<Font> key,
      ResourceBundleUtil labels,
      java.util.List<Disposable> dsp) {
    JPopupButton fontPopupButton;
    fontPopupButton = new JPopupButton();
    labels.configureToolBarButton(fontPopupButton, "attribute.font");
    fontPopupButton.setFocusable(false);
    JComponentPopup popupMenu = new JComponentPopup();
    JFontChooser fontChooser = new JFontChooser();
    dsp.add(new FontChooserHandler(editor, key, fontChooser, popupMenu));
    popupMenu.add(fontChooser);
    fontPopupButton.setPopupMenu(popupMenu);
    fontPopupButton.setFocusable(false);
    return fontPopupButton;
  }

  public static JButton createFontStyleBoldButton(DrawingEditor editor) {
    return createFontStyleBoldButton(
        editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JButton createFontStyleBoldButton(DrawingEditor editor, ResourceBundleUtil labels) {
    return createFontStyleBoldButton(editor, labels, new ArrayList<>());
  }

  public static JButton createFontStyleBoldButton(
      DrawingEditor editor, ResourceBundleUtil labels, java.util.List<Disposable> dsp) {
    JButton btn;
    btn = new JButton();
    labels.configureToolBarButton(btn, "attribute.fontStyle.bold");
    btn.setFocusable(false);
    AbstractAction a = new AttributeToggler<>(
        editor, FONT_BOLD, Boolean.TRUE, Boolean.FALSE, new StyledEditorKit.BoldAction());
    a.putValue(
        ActionUtil.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.fontStyle.bold.text"));
    btn.addActionListener(a);
    return btn;
  }

  public static JButton createFontStyleItalicButton(DrawingEditor editor) {
    return createFontStyleItalicButton(
        editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JButton createFontStyleItalicButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createFontStyleItalicButton(editor, labels, new ArrayList<>());
  }

  public static JButton createFontStyleItalicButton(
      DrawingEditor editor, ResourceBundleUtil labels, java.util.List<Disposable> dsp) {
    JButton btn;
    btn = new JButton();
    labels.configureToolBarButton(btn, "attribute.fontStyle.italic");
    btn.setFocusable(false);
    AbstractAction a = new AttributeToggler<>(
        editor, FONT_ITALIC, Boolean.TRUE, Boolean.FALSE, new StyledEditorKit.BoldAction());
    a.putValue(
        ActionUtil.UNDO_PRESENTATION_NAME_KEY, labels.getString("attribute.fontStyle.italic.text"));
    btn.addActionListener(a);
    return btn;
  }

  public static JButton createFontStyleUnderlineButton(DrawingEditor editor) {
    return createFontStyleUnderlineButton(
        editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JButton createFontStyleUnderlineButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createFontStyleUnderlineButton(editor, labels, new ArrayList<>());
  }

  public static JButton createFontStyleUnderlineButton(
      DrawingEditor editor, ResourceBundleUtil labels, java.util.List<Disposable> dsp) {
    JButton btn;
    btn = new JButton();
    labels.configureToolBarButton(btn, "attribute.fontStyle.underline");
    btn.setFocusable(false);
    AbstractAction a = new AttributeToggler<>(
        editor, FONT_UNDERLINE, Boolean.TRUE, Boolean.FALSE, new StyledEditorKit.BoldAction());
    a.putValue(
        ActionUtil.UNDO_PRESENTATION_NAME_KEY,
        labels.getString("attribute.fontStyle.underline.text"));
    btn.addActionListener(a);
    return btn;
  }

  /** Creates toolbar buttons and adds them to the specified JToolBar */
  public static void addAlignmentButtonsTo(JToolBar bar, final DrawingEditor editor) {
    addAlignmentButtonsTo(bar, editor, new ArrayList<>());
  }

  /** Creates toolbar buttons and adds them to the specified JToolBar. */
  public static void addAlignmentButtonsTo(
      JToolBar bar, final DrawingEditor editor, java.util.List<Disposable> dsp) {
    AbstractSelectedAction d;
    bar.add(d = new AlignAction.West(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new AlignAction.East(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new AlignAction.Horizontal(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new AlignAction.North(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new AlignAction.South(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new AlignAction.Vertical(editor)).setFocusable(false);
    dsp.add(d);
    bar.addSeparator();
    bar.add(d = new MoveAction.West(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new MoveAction.East(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new MoveAction.North(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(d = new MoveAction.South(editor)).setFocusable(false);
    dsp.add(d);
    bar.addSeparator();
    bar.add(new BringToFrontAction(editor)).setFocusable(false);
    dsp.add(d);
    bar.add(new SendToBackAction(editor)).setFocusable(false);
    dsp.add(d);
  }

  /** Creates a button which toggles between two GridConstrainer for a DrawingView. */
  public static AbstractButton createToggleGridButton(final DrawingView view) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    final JToggleButton toggleButton;
    toggleButton = new JToggleButton();
    labels.configureToolBarButton(toggleButton, "view.toggleGrid");
    toggleButton.setFocusable(false);
    toggleButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent event) {
        view.setConstrainerVisible(toggleButton.isSelected());
        // view.getComponent().repaint();
      }
    });
    view.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // String constants are interned
        if ((evt.getPropertyName() == null && DrawingView.CONSTRAINER_VISIBLE_PROPERTY == null)
            || (evt.getPropertyName() != null
                && evt.getPropertyName().equals(DrawingView.CONSTRAINER_VISIBLE_PROPERTY))) {
          toggleButton.setSelected(view.isConstrainerVisible());
        }
      }
    });
    return toggleButton;
  }

  public static JPopupButton createStrokeCapButton(DrawingEditor editor) {
    return createStrokeCapButton(editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeCapButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createStrokeCapButton(editor, labels, new ArrayList<>());
  }

  public static JPopupButton createStrokeCapButton(
      DrawingEditor editor, ResourceBundleUtil labels, java.util.List<Disposable> dsp) {
    JPopupButton popupButton = new JPopupButton();
    labels.configureToolBarButton(popupButton, "attribute.strokeCap");
    popupButton.setFocusable(false);
    HashMap<AttributeKey<?>, Object> attr;
    attr = new HashMap<>();
    attr.put(STROKE_CAP, BasicStroke.CAP_BUTT);
    AttributeAction a;
    popupButton.add(
        a = new AttributeAction(editor, attr, labels.getString("attribute.strokeCap.butt"), null));
    dsp.add(a);
    attr = new HashMap<>();
    attr.put(STROKE_CAP, BasicStroke.CAP_ROUND);
    popupButton.add(
        a = new AttributeAction(editor, attr, labels.getString("attribute.strokeCap.round"), null));
    dsp.add(a);
    attr = new HashMap<>();
    attr.put(STROKE_CAP, BasicStroke.CAP_SQUARE);
    popupButton.add(
        a = new AttributeAction(
            editor, attr, labels.getString("attribute.strokeCap.square"), null));
    dsp.add(a);
    return popupButton;
  }

  public static JPopupButton createStrokeJoinButton(DrawingEditor editor) {
    return createStrokeJoinButton(editor, ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels"));
  }

  public static JPopupButton createStrokeJoinButton(
      DrawingEditor editor, ResourceBundleUtil labels) {
    return createStrokeJoinButton(editor, labels, new ArrayList<>());
  }

  public static JPopupButton createStrokeJoinButton(
      DrawingEditor editor, ResourceBundleUtil labels, java.util.List<Disposable> dsp) {
    JPopupButton popupButton = new JPopupButton();
    labels.configureToolBarButton(popupButton, "attribute.strokeJoin");
    popupButton.setFocusable(false);
    HashMap<AttributeKey<?>, Object> attr;
    attr = new HashMap<>();
    attr.put(STROKE_JOIN, BasicStroke.JOIN_BEVEL);
    AttributeAction a;
    popupButton.add(
        a = new AttributeAction(
            editor, attr, labels.getString("attribute.strokeJoin.bevel"), null));
    dsp.add(a);
    attr = new HashMap<>();
    attr.put(STROKE_JOIN, BasicStroke.JOIN_ROUND);
    popupButton.add(
        a = new AttributeAction(
            editor, attr, labels.getString("attribute.strokeJoin.round"), null));
    dsp.add(a);
    attr = new HashMap<>();
    attr.put(STROKE_JOIN, BasicStroke.JOIN_MITER);
    popupButton.add(
        a = new AttributeAction(
            editor, attr, labels.getString("attribute.strokeJoin.miter"), null));
    dsp.add(a);
    return popupButton;
  }

  public static JButton createPickAttributesButton(DrawingEditor editor) {
    return createPickAttributesButton(editor, new ArrayList<>());
  }

  public static JButton createPickAttributesButton(
      DrawingEditor editor, java.util.List<Disposable> dsp) {
    JButton btn;
    AbstractSelectedAction d;
    btn = new JButton(d = new PickAttributesAction(editor));
    dsp.add(d);
    if (btn.getIcon() != null) {
      btn.putClientProperty("hideActionText", Boolean.TRUE);
    }
    btn.setHorizontalTextPosition(JButton.CENTER);
    btn.setVerticalTextPosition(JButton.BOTTOM);
    btn.setText(null);
    btn.setFocusable(false);
    return btn;
  }

  /**
   * Creates a button that applies the default attributes of the editor to the current selection.
   */
  public static JButton createApplyAttributesButton(DrawingEditor editor) {
    return createApplyAttributesButton(editor, new ArrayList<>());
  }

  public static JButton createApplyAttributesButton(
      DrawingEditor editor, java.util.List<Disposable> dsp) {
    JButton btn;
    AbstractSelectedAction d;
    btn = new JButton(d = new ApplyAttributesAction(editor));
    dsp.add(d);
    if (btn.getIcon() != null) {
      btn.putClientProperty("hideActionText", Boolean.TRUE);
    }
    btn.setHorizontalTextPosition(JButton.CENTER);
    btn.setVerticalTextPosition(JButton.BOTTOM);
    btn.setText(null);
    btn.setFocusable(false);
    return btn;
  }
}
