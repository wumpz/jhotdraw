/*
 * @(#)ColorSliderUI.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.util.Images;

/**
 * A UI delegate for color sliders. The track of the slider visualizes how changing the value of the
 * slider affects the color.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorSliderUI extends BasicSliderUI {

    private static final Color FOREGROUND = new Color(0x949494);
    private static final Color TRACK_BACKGROUND = new Color(0xffffff);
    private ColorTrackImageProducer colorTrackImageProducer;
    private Image colorTrackImage;
    private static final Dimension PREFERRED_HORIZONTAL_SIZE = new Dimension(160, 4);
    private static final Dimension PREFERRED_VERTICAL_SIZE = new Dimension(4, 160);
    private static final Dimension MINIMUM_HORIZONTAL_SIZE = new Dimension(16, 4);
    private static final Dimension MINIMUM_VERTICAL_SIZE = new Dimension(4, 16);

    /**
     * Creates a new instance.
     */
    public ColorSliderUI(JSlider b) {
        super(b);
    }
    /**
     * 
     * @param pos image position (north,west....)
     */
    public static void checkUIManeger(String pos ) {
    	String concatPos = "Slider."+pos+"Thumb.small";
    	if (null == UIManager.getIcon(concatPos)) 
            UIManager.put(concatPos,
                    new PaletteSliderThumbIcon(Images.createImage(
                            ColorSliderUI.class, "/org/jhotdraw/color/images/"+concatPos+".png"), 6, true));
    }
    /**
     * @param b JComponent
     * @return ComponentUI
     */
    public static ComponentUI createUI(JComponent b) {
    	checkUIManeger("north");
    	checkUIManeger("west");
        return new ColorSliderUI((JSlider) b);
    }

    private void checkOrientation() {
    	int x=1;
    	if (slider.getOrientation() == JSlider.HORIZONTAL) x=0;
    	slider.setBorder(new EmptyBorder(0, x, -x, 1));	
    }
    @Override
    /**
     * allows to set slider's defaults
     * @param slider to handle  
     */
    protected void installDefaults(JSlider slider) {
        super.installDefaults(slider);
        focusInsets = new Insets(0, 0, 0, 0);
        slider.setOpaque(false);
        checkOrientation();
        slider.setRequestFocusEnabled(true);
    }

    @Override
    /**
     * @return dimension of the thumb
     */
    protected Dimension getThumbSize() {
        Icon thumb = getThumbIcon();
        return new Dimension(thumb.getIconWidth(), thumb.getIconHeight());
    }

    @Override
    /**
     * @param c JComponent
     * @return preferred dimension of c
     */
    public Dimension getPreferredSize(JComponent c) {
        recalculateIfInsetsChanged();
        Dimension d;
        if (slider.getOrientation() == JSlider.VERTICAL) {
            d = new Dimension(getPreferredVerticalSize());
            d.width += insetCache.left + insetCache.right;
            d.width += focusInsets.left + focusInsets.right;
            d.width += trackRect.width + tickRect.width + labelRect.width;
        } else {
            d = new Dimension(getPreferredHorizontalSize());
            d.height += insetCache.top + insetCache.bottom;
            d.height += focusInsets.top + focusInsets.bottom;
            d.height += trackRect.height + tickRect.height + labelRect.height;
        }
        return d;
    }

    @Override
    /**
     * @return preferred horizontal dimension 
     */
    public Dimension getPreferredHorizontalSize() {
        return PREFERRED_HORIZONTAL_SIZE;
    }

    @Override
    /**
     * @return preferred vertical dimension 
     */
    public Dimension getPreferredVerticalSize() {
        return PREFERRED_VERTICAL_SIZE;
    }

    @Override
    /**
     * @return minimum horizontal dimension 
     */
    public Dimension getMinimumHorizontalSize() {
        return MINIMUM_HORIZONTAL_SIZE;
    }

    @Override
    /**
     * @return minimum vertical dimension 
     */
    public Dimension getMinimumVerticalSize() {
        return MINIMUM_VERTICAL_SIZE;
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        if (slider.getOrientation() == JSlider.HORIZONTAL)
            thumbRect.y -= 3;
        else  thumbRect.x -= 3;
        
    }
    /**
     * @param pos image pos (north,west,...)
     * @return an Icon with specific pos image 
     */
    private Icon createStringByPos(String pos) {
    	String concatPos = "Slider."+pos+"Thumb.small";
    	return UIManager.getIcon(concatPos);
    	
    	
    }
    /**
     * @return thumb icon 
     */
    protected Icon getThumbIcon() {
        if (slider.getOrientation() == JSlider.HORIZONTAL)
        	return createStringByPos("north");
        else return createStringByPos("west");
        
    }

    @Override
    /**
     * allows to paint thumb in the slider 
     */
    public void paintThumb(Graphics g) {
        Rectangle knobBounds = thumbRect;
        getThumbIcon().paintIcon(slider, g, knobBounds.x, knobBounds.y);
    }

    @Override
    /**
     * allows to paint the hole track
     */
    public void paintTrack(Graphics g) {
        int cx, cy, cw, ch,pad;
        Rectangle trackBounds = trackRect;
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            pad = trackBuffer; 
            cx = trackBounds.x - pad + 1;
            cy = trackBounds.y;
            cw = trackBounds.width + pad * 2 - 2;
            ch = trackBounds.height - 1;
        } else {
            pad = trackBuffer;
            cx = trackBounds.x;
            cy = contentRect.y + 2;
            cw = trackBounds.width - 1;
            ch = trackBounds.height + pad * 2 - 5;
        }
        g.setColor(TRACK_BACKGROUND);
        g.fillRect(cx, cy, cw, ch);
        g.setColor(FOREGROUND);
        g.drawRect(cx, cy, cw - 1, ch - 1);
        paintColorTrack(g, cx + 2, cy + 2, cw - 4, ch - 4, trackBuffer);
    }
    /**
     * handle minor tick value in paint process
     */
    private int paintTicksMin(int value, Rectangle tickBounds, int xPos, Graphics g) {
    	while (value <= slider.getMaximum()) {
            xPos = xPositionForValue(value);
            paintMinorTickForHorizSlider(g, tickBounds, xPos);
            value += slider.getMinorTickSpacing();
        }
    	return value;
    }
    /**
     * handle major tick value in paint process
     */
    private int paintTicksMaj(int value, Rectangle tickBounds, int xPos, Graphics g) {
    	 while (value <= slider.getMaximum()) {
             xPos = xPositionForValue(value);
             paintMajorTickForHorizSlider(g, tickBounds, xPos);
             value += slider.getMajorTickSpacing();
         }
    	return value;
    }
    /**
     * handle horizontal spacing in paint process
     */
    private void handleTickesHorizontal(int value, Rectangle tickBounds, Graphics g, int xPos) {
            g.translate(0, tickBounds.y);
            if (slider.getMinorTickSpacing() > 0) 
            	paintTicksMin(value,tickBounds,xPos,g);
            if (slider.getMajorTickSpacing() > 0) {
                value = slider.getMinimum();
                paintTicksMaj(value,tickBounds,xPos,g);
            }
            g.translate(0, -tickBounds.y);
    }
    /**
     * handle minor tick spacing value in paint process
     */
    private void tickSpacingMin(Rectangle tickBounds, int value, int yPos, Graphics g) {
        int offset = 0;
        if (!slider.getComponentOrientation().isLeftToRight()) {
            offset = tickBounds.width - tickBounds.width / 2;
            g.translate(offset, 0);
        }
        paintTicksMin(value,tickBounds,yPos,g);
        if (!slider.getComponentOrientation().isLeftToRight()) {
            g.translate(-offset, 0);
        }
    }
    
    @Override
    /**
     * allows to paint ticks 
     */
    public void paintTicks(Graphics g) {
        Rectangle tickBounds = tickRect;
        int value = slider.getMinimum();
        int xPos = 0;
        g.setColor(FOREGROUND);
        if (slider.getOrientation() == JSlider.HORIZONTAL) 
        	handleTickesHorizontal(value,tickBounds,g,xPos);
         else {
            g.translate(tickBounds.x, 0);
            int yPos=0;
			if (slider.getMinorTickSpacing() > 0) 
				tickSpacingMin(tickBounds,value,yPos,g);
            if (slider.getMajorTickSpacing() > 0) {
                if (!slider.getComponentOrientation().isLeftToRight()) {
                    g.translate(2, 0);
                }
                paintTicksMaj(value,tickBounds,yPos,g);
                if (!slider.getComponentOrientation().isLeftToRight()) {
                    g.translate(-2, 0);
                }
            }
            g.translate(-tickBounds.x, 0);
        }
    }

    @Override
    /**
     * allows to paint Major Tick For the Horizontal Slider 
     */
    protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        g.drawLine(x, 0, x, tickBounds.height - 1);
    }

    @Override
    /**
     * allows to paint minor Tick For the Horizontal Slider 
     */
    protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        g.drawLine(x, 0, x, tickBounds.height - 1);
    }

    @Override
    /**
     * allows to paint minor Tick For the vertical Slider 
     */
    protected void paintMinorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) {
        g.drawLine(tickBounds.width / 2, y, tickBounds.width / 2 - 1, y);
    }

    @Override
    /**
     * allows to paint major Tick For the vertical Slider 
     */
    protected void paintMajorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) {
        g.drawLine(0, y, tickBounds.width - 1, y);
    }
    /**
     * allows to paint the color track
     * @param g graphics 
     * @param x x value
     * @param y y value 
     * @param width track width
     * @param height track height
     */
    public void paintColorTrack(Graphics g, int x, int y, int width, int height, int buffer) {
        if (colorTrackImageProducer == null
                || colorTrackImageProducer.getWidth() != width
                || colorTrackImageProducer.getHeight() != height) {
            if (colorTrackImage != null) {
                colorTrackImage.flush();
            }
            colorTrackImageProducer = new ColorTrackImageProducer(width, height, buffer + 2, slider.getOrientation() == JSlider.HORIZONTAL);
            if (slider.getClientProperty("colorSliderModel") != null) {
                colorTrackImageProducer.setColorSliderModel((ColorSliderModel) slider.getClientProperty("colorSliderModel"));
                colorTrackImageProducer.setColorComponentIndex(((Integer) slider.getClientProperty("colorComponentIndex")));
            }
            colorTrackImageProducer.generateColorTrack();
            colorTrackImage = slider.createImage(colorTrackImageProducer);
        } else {
            if (colorTrackImageProducer.needsGeneration()) {
                // To keep the UI responsive, we only perform the time consuming
                // regeneration of the color track if we don't already have
                // a latency of more than a 10th of a second on the most recent event.
                long latency = System.currentTimeMillis() - EventQueue.getMostRecentEventTime();
                if (latency > 100) {
                    slider.repaint();
                } else {
                    colorTrackImageProducer.regenerateColorTrack();
                }
            }
        }
        if (colorTrackImage != null) {
            g.drawImage(colorTrackImage, x, y, slider);
        }
    }

    @Override
    /**
     * allows to calculate the track of the rectangle
     */
    protected void calculateTrackRect() {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            trackRect.x = contentRect.x + trackBuffer + 1;
            trackRect.height = 14;
            trackRect.y = contentRect.y + contentRect.height - trackRect.height;
            trackRect.width = contentRect.width - (trackBuffer * 2) - 1;
        } else {
            trackRect.width = 14;
            trackRect.x = contentRect.x + (contentRect.width - trackRect.width) / 2;
            trackRect.y = contentRect.y + trackBuffer;
            trackRect.height = contentRect.height - (trackBuffer * 2) + 1;
        }
    }

    @Override
    /**
     * allows to calculate the tick of the rectangle
     */
    protected void calculateTickRect() {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            tickRect.x = trackRect.x;
            tickRect.y = trackRect.y - getTickLength();
            tickRect.width = trackRect.width;
            tickRect.height = getTickLength();
            if (!slider.getPaintTicks()) {
                --tickRect.y;
                tickRect.height = 0;
            }
        } else {
            tickRect.width = getTickLength();
            tickRect.x = contentRect.x; 
            tickRect.y = trackRect.y;
            tickRect.height = trackRect.height;
            if (!slider.getPaintTicks()) {
                --tickRect.x;
                tickRect.width = 0;
            }
        }
    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width of the tick area for
     * vertical sliders. BasicSliderUI uses the returned value to determine the tick area rectangle.
     * If you want to give your ticks some room, make this larger than you need and paint your ticks
     * away from the sides in paintTicks().
     */
    @Override
    /**
     * @return tick length
     */
    protected int getTickLength() {
        return 4;
    }

    @Override
    /**
     * generate a property change listener
     * @return PropertyChangeListener
     */
    protected PropertyChangeListener createPropertyChangeListener(JSlider slider) {
        return new CSUIPropertyChangeHandler();
    }

    public class CSUIPropertyChangeHandler extends BasicSliderUI.PropertyChangeHandler {

    	/**
    	 * check if the color track image needs regeneration to repaint the slider
    	 */
    	private void checkImgProducerNeedsRegen() {
    		if (colorTrackImageProducer.needsGeneration())
                slider.repaint();
    	}
    	/**
    	 * checks PropertyChangeEvent possible value 
    	 * @param val possible value 
    	 * @param e PropertyChangeEvent
    	 * @return true if PropertyChangeEvent's property name is equals val 
    	 */
    	private boolean checkPropertyNameEq(String val,PropertyChangeEvent e) {
    		String propertyName = e.getPropertyName();
    		return val.equals(propertyName);
    	}
    	/**
    	 * checks if color track img is null
    	 * @param e PropertyChangeEvent
    	 */
    	private void checkTrackImgProdVal(PropertyChangeEvent e) {
    		 if (colorTrackImageProducer != null) {
                 colorTrackImageProducer.setColorSliderModel(((ColorSliderModel) e.getNewValue()));
                 checkImgProducerNeedsRegen();
             }
    	}
    	/**
    	 * checks PropertyChangeEvent's new value and the color image track value 
    	 * @param e PropertyChangeEvent
    	 */
    	private void checkPropertyEventNewValAndColorTrack(PropertyChangeEvent e) {
            Integer value = (Integer) e.getNewValue();
            if (value != null && colorTrackImageProducer != null) {
                colorTrackImageProducer.componentChanged(value);
                checkImgProducerNeedsRegen();
            }
    	}
    	/**
    	 * checks the colorComponentChange property and the color track image producer
    	 */
    	private void checkColorComponentChange() {
    		 Integer value = (Integer) slider.getClientProperty("colorComponentChange");
             if (value != null && colorTrackImageProducer != null) {
                 colorTrackImageProducer.componentChanged(value);
                 checkImgProducerNeedsRegen();
             }
    	}
    	
        @Override
        /**
         * adapts to different possible values Property Change Event
         */
        public void propertyChange(PropertyChangeEvent e) {
            if (checkPropertyNameEq("Frame.active",e))
                slider.repaint();
            else if (checkPropertyNameEq("colorSliderModel",e))
            	checkTrackImgProdVal(e);
            else if (checkPropertyNameEq("snapToTicks",e)) {
                if (colorTrackImageProducer != null) {
                    colorTrackImageProducer.markAsDirty();
                    slider.repaint();
                }
            } else if (checkPropertyNameEq("colorComponentIndex",e)) {
                if (colorTrackImageProducer != null && e.getNewValue() != null) {
                    colorTrackImageProducer.setColorComponentIndex(((Integer) e.getNewValue()));
                    checkImgProducerNeedsRegen();
                }
            } else if (checkPropertyNameEq("colorComponentChange",e))
            	checkPropertyEventNewValAndColorTrack(e);
             else if (checkPropertyNameEq("colorComponentValue",e)) {
            	 checkColorComponentChange();
            } else if (checkPropertyNameEq("orientation",e)) {
                if (slider.getOrientation() == JSlider.HORIZONTAL) 
                    slider.setBorder(new EmptyBorder(0, 1, -1, 1));
                 else slider.setBorder(new EmptyBorder(0, 0, 0, 1));
                
            }
            super.propertyChange(e);
        }
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new TrackListener();
    }

    /**
     * Track mouse movements.
     *
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be
     * treated as a &quot;protected&quot; inner class. Instantiate it only within subclasses of
     * <Foo>.
     */
    {
    }

    public class TrackListener extends BasicSliderUI.TrackListener {

        /**
         * If the mouse is pressed above the "thumb" component then reduce the scrollbars value by
         * one page ("page up"), otherwise increase it by one page. If there is no thumb then page
         * up if the mouse is in the upper half of the track.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled())
                return;
            currentMouseX = e.getX();
            currentMouseY = e.getY();
            if (slider.isRequestFocusEnabled())
                slider.requestFocus();
            // Clicked inside the Thumb area?
            if (thumbRect.contains(currentMouseX, currentMouseY))
                super.mousePressed(e);
            else {
                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        slider.setValue(valueForYPosition(currentMouseY));
                        break;
                    case JSlider.HORIZONTAL:
                        slider.setValue(valueForXPosition(currentMouseX));
                        break;
                    default: break;
                }
                // FIXME:
                // We should set isDragging to false here. Unfortunately,
                // we can not access this variable in class BasicSliderUI.
            }
        }
    }
}
