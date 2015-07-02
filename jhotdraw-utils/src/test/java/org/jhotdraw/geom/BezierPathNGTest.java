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
package org.jhotdraw.geom;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author toben
 */
public class BezierPathNGTest {

    public BezierPathNGTest() {
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

    @Test
    public void testToGeneralPath() {
        BezierPath instance = new BezierPath();
        Point2D.Double c0 = new Point2D.Double(0.0004, 0.002);
        instance.add(c0);
        c0 = new Point2D.Double(21.0004, 56.92827);
        instance.add(c0);

        Path2D.Double gp = instance.toGeneralPath();
        PathIterator pathIterator = gp.getPathIterator(null);
        double[] coords = new double[2];
        int i=0;
        while (pathIterator.isDone() == false) {
            pathIterator.currentSegment(coords);
            for (int j = 0; j < 3; j++) {
                assertEquals(coords[0], instance.get(i).getControlPoint(j).x);
                assertEquals(coords[1], instance.get(i).getControlPoint(j).y);
            }
            i++;
            pathIterator.next();
        }
    }
    
    /**
     * Test of toPolygonArray method, of class BezierPath.
     */
    @Test
    public void testToPolygonArray() {
        BezierPath instance = new BezierPath();
        Point2D.Double c0 = new Point2D.Double(0.0004, 0.002);
        instance.add(c0);
        c0 = new Point2D.Double(21.0004, 56.92827);
        instance.add(c0);

        Point2D.Double[] toPolygonArray = instance.toPolygonArray();
        assertEquals(toPolygonArray.length, 2);
        for (int i = 0; i < toPolygonArray.length; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(toPolygonArray[i], instance.get(i).getControlPoint(j));
            }
        }
    }
    
    @Test
    public void testPathIterator() {
        BezierPath instance = new BezierPath();
        Point2D.Double c0 = new Point2D.Double(0.0004, 0.002);
        instance.add(c0);
        c0 = new Point2D.Double(21.0004, 56.92827);
        instance.add(c0);

        PathIterator pathIterator = instance.getPathIterator(null);
        double[] coords = new double[2];
        int i=0;
        while (pathIterator.isDone() == false) {
            pathIterator.currentSegment(coords);
            for (int j = 0; j < 3; j++) {
                assertEquals(coords[0], instance.get(i).getControlPoint(j).x);
                assertEquals(coords[1], instance.get(i).getControlPoint(j).y);
            }
            i++;
            pathIterator.next();
        }
    }
    
    @Test
    public void testPathIterator2() {
        BezierPath instance = new BezierPath();
        Point2D.Double c0 = new Point2D.Double(0.0004, 0.002);
        instance.add(c0);
        c0 = new Point2D.Double(21.0004, 56.92827);
        instance.add(c0);

        PathIterator pathIterator = instance.getPathIterator(null,4);
        double[] coords = new double[2];
        int i=0;
        while (pathIterator.isDone() == false) {
            pathIterator.currentSegment(coords);
            for (int j = 0; j < 3; j++) {
                assertEquals(coords[0], instance.get(i).getControlPoint(j).x);
                assertEquals(coords[1], instance.get(i).getControlPoint(j).y);
            }
            i++;
            pathIterator.next();
        }
    }
}
