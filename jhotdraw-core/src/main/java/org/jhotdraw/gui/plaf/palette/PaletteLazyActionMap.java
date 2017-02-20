/**
 * @(#)QuaquaLazyActionMap.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui.plaf.palette;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * QuaquaLazyActionMap.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaletteLazyActionMap extends ActionMapUIResource {
    private static final long serialVersionUID = 1L;
    /**
     * Object to invoke <code>loadActionMap</code> on. This may be
     * a Class object.
     */
    @Nullable private transient Object loader;

    /**
     * Installs an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p>
     * This should be used if the ActionMap can be shared.
     *
     * @param c JComponent to install the ActionMap on.
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    static void installLazyActionMap(JComponent c, Class<?> loaderClass,
                                     String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new PaletteLazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }

    /**
     * Returns an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p>
     * This should be used if the ActionMap can be shared.
     *
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    static ActionMap getActionMap(Class<?> loaderClass,
                                  String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new PaletteLazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        return map;
    }


    private PaletteLazyActionMap(Class<?> loader) {
        this.loader = loader;
    }

    public void put(Action action) {
        put(action.getValue(Action.NAME), action);
    }

    @Override
    public void put(Object key, Action action) {
        loadIfNecessary();
        super.put(key, action);
    }

    @Override
    public Action get(Object key) {
        loadIfNecessary();
        return super.get(key);
    }

    @Override
    public void remove(Object key) {
        loadIfNecessary();
        super.remove(key);
    }

    @Override
    public void clear() {
        loadIfNecessary();
        super.clear();
    }

    @Override
    public Object[] keys() {
        loadIfNecessary();
        return super.keys();
    }

    @Override
    public int size() {
        loadIfNecessary();
        return super.size();
    }

    @Override
    public Object[] allKeys() {
        loadIfNecessary();
        return super.allKeys();
    }

    @Override
    public void setParent(ActionMap map) {
        loadIfNecessary();
        super.setParent(map);
    }

    @SuppressWarnings("unchecked")
    private void loadIfNecessary() {
        if (loader != null) {
            Object ldr = loader;

            loader = null;
            Class<?> klass = (Class)ldr;
            try {
                Method method = klass.getDeclaredMethod("loadActionMap",
                                      new Class<?>[] { PaletteLazyActionMap.class  });
                method.invoke(klass, new Object[] { this });
            } catch (NoSuchMethodException nsme) {
                assert false : "LazyActionMap unable to load actions " +
                        klass;
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException iae) {
                assert false : "LazyActionMap unable to load actions " +
                        iae;
            }
        }
    }
}
