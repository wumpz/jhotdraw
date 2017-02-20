/*
 * Copyright (C) 2015 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author tw
 */
public class AbstractFigureNGTest {
    
    public AbstractFigureNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testChangedWithoutWillChange() {
        new AbstractFigureImpl().changed();
    }
    
    @Test
    public void testWillChangeChangedEvents() {
        AbstractFigure figure = new AbstractFigureImpl();
        assertEquals(figure.getChangingDepth(),0);
        figure.willChange();
        assertEquals(figure.getChangingDepth(),1);
        figure.willChange();
        assertEquals(figure.getChangingDepth(),2);
        figure.changed();
        assertEquals(figure.getChangingDepth(),1);
        figure.changed();
        assertEquals(figure.getChangingDepth(),0);
    }
    

    public class AbstractFigureImpl extends AbstractFigure {

        @Override
        public void draw(Graphics2D g) {
        }

        @Override
        public Rectangle2D.Double getBounds() {
            return null;
        }

        @Override
        public Rectangle2D.Double getDrawingArea() {
            return null;
        }

        @Override
        public boolean contains(Point2D.Double p) {
            return true;
        }

        @Override
        public Object getTransformRestoreData() {
            return null;
        }

        @Override
        public void restoreTransformTo(Object restoreData) {
        }

        @Override
        public void transform(AffineTransform tx) {
        }

        @Override
        public <T> void set(AttributeKey<T> key, T value) {
        }

        @Override
        public <T> T get(AttributeKey<T> key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Map<AttributeKey<?>, Object> getAttributes() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object getAttributesRestoreData() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restoreAttributesTo(Object restoreData) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Rectangle2D.Double getDrawingArea(double factor) {
            return null;
        }
    }
    
}
