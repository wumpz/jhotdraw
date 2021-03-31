/*
 * @(#)JLifeFormattedTextFieldBeanInfo.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui;

import java.beans.*;

import org.jhotdraw.draw.gui.JAttributeSliderBeanInfo;

/**
 * BeanInfo for JLifeFormattedTextField.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class JLifeFormattedTextFieldBeanInfo extends SimpleBeanInfo {
    // Bean descriptor information will be obtained from introspection.//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = null;
    private static BeanDescriptor getBdescriptor(){
//GEN-HEADEREND:BeanDescriptor
        // Here you can add code for customizing the BeanDescriptor.
        if (beanDescriptor == null) {
            beanDescriptor = new BeanDescriptor(JLifeFormattedTextFieldBeanInfo.class);
        }
        beanDescriptor.setValue("isContainer", Boolean.FALSE);
        beanDescriptor.setDisplayName("JLifeFormattedTextField");
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    // Properties information will be obtained from introspection.//GEN-FIRST:Properties
    private static PropertyDescriptor[] properties = null;
    private static PropertyDescriptor[] getPdescriptor(){ //GEN-HEADEREND:Properties
        // Here you can add code for customizing the properties array.
        return properties;     }//GEN-LAST:Properties
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){ //GEN-HEADEREND:Events
        // Here you can add code for customizing the event sets array.
        return eventSets;     }//GEN-LAST:Events
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){ //GEN-HEADEREND:Methods
        // Here you can add code for customizing the methods array.
        return methods;     }//GEN-LAST:Methods
    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
    private static String iconNameC16 = null; //GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null; //GEN-END:Icons
    private static int defaultPropertyIndex = -1; //GEN-BEGIN:Idx
    private static int defaultEventIndex = -1; //GEN-END:Idx
//GEN-FIRST:Superclass
    // Here you can add code for customizing the Superclass BeanInfo.
//GEN-LAST:Superclass
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean. May return null if the
     * information should be obtained by automatic analysis.
     */
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean. May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return An array of EventSetDescriptors describing the kinds of
     * events fired by this bean. May return null if the information
     * should be obtained by automatic analysis.
     */
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return An array of MethodDescriptors describing the methods
     * implemented by this bean. May return null if the information
     * should be obtained by automatic analysis.
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     *
     * @return Index of default property in the PropertyDescriptor array
     * returned by getPropertyDescriptors.
     * <P>
     * Returns -1 if there is no default property.
     */
    @Override
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     *
     * @return Index of default event in the EventSetDescriptor array
     * returned by getEventSetDescriptors.
     * <P>
     * Returns -1 if there is no default event.
     */
    @Override
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    private java.awt.Image genIconAux(String iconName,java.awt.Image iconColor){
    	if (iconName == null) return null;
    	iconColor = (iconColor == null)?loadImage(iconName) :iconColor;
    	return iconColor;
        }
    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc. Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono). If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param iconKind The kind of icon requested. This should be
     * one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
     * ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return An image object representing the requested icon. May
     * return null if no suitable icon is available.
     */
    @Override
    public java.awt.Image getIcon(int iconKind) {
   	 switch (iconKind) {
        case ICON_COLOR_16x16:
       	 return genIconAux(iconNameC16,iconColor16);
        case ICON_COLOR_32x32:
            return genIconAux(iconNameC32,iconColor32);
        case ICON_MONO_16x16:
            return genIconAux(iconNameM16,iconMono16);
        case ICON_MONO_32x32:
            return genIconAux(iconNameM32,iconMono32);
        default:
            return null;
    }
}
}
