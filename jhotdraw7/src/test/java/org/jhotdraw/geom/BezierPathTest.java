/*
 * BezierPathTest.java
 * JUnit based test
 *
 * Created on January 5, 2007, 2:59 PM
 */

package org.jhotdraw.geom;

import junit.framework.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 *
 * @author werni
 */
public class BezierPathTest extends TestCase {
    
    public BezierPathTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static class NodeTest extends TestCase {

        public NodeTest(String testName) {
            super(testName);
        }

        protected void setUp() throws Exception {
        }

        protected void tearDown() throws Exception {
        }

        /**
         * Test of setTo method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testSetTo() {
            System.out.println("setTo");
            
            BezierPath.Node that = null;
            BezierPath.Node instance = new BezierPath.Node();
            
            instance.setTo(that);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of getMask method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testGetMask() {
            System.out.println("getMask");
            
            BezierPath.Node instance = new BezierPath.Node();
            
            int expResult = 0;
            int result = instance.getMask();
            assertEquals(expResult, result);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of setMask method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testSetMask() {
            System.out.println("setMask");
            
            int newValue = 0;
            BezierPath.Node instance = new BezierPath.Node();
            
            instance.setMask(newValue);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of setControlPoint method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testSetControlPoint() {
            System.out.println("setControlPoint");
            
            int index = 0;
            Point2D.Double p = null;
            BezierPath.Node instance = new BezierPath.Node();
            
            instance.setControlPoint(index, p);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of getControlPoint method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testGetControlPoint() {
            System.out.println("getControlPoint");
            
            int index = 0;
            BezierPath.Node instance = new BezierPath.Node();
            
            Point2D.Double expResult = null;
            Point2D.Double result = instance.getControlPoint(index);
            assertEquals(expResult, result);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of moveTo method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testMoveTo() {
            System.out.println("moveTo");
            
            Point2D.Double p = null;
            BezierPath.Node instance = new BezierPath.Node();
            
            instance.moveTo(p);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of moveBy method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testMoveBy() {
            System.out.println("moveBy");
            
            double dx = 0.0;
            double dy = 0.0;
            BezierPath.Node instance = new BezierPath.Node();
            
            instance.moveBy(dx, dy);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of clone method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testClone() {
            System.out.println("clone");
            
            BezierPath.Node instance = new BezierPath.Node();
            
            Object expResult = null;
            Object result = instance.clone();
            assertEquals(expResult, result);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }

        /**
         * Test of toString method, of class org.jhotdraw.geom.BezierPath.Node.
         */
        public void testToString() {
            System.out.println("toString");
            
            BezierPath.Node instance = new BezierPath.Node();
            
            String expResult = "";
            String result = instance.toString();
            assertEquals(expResult, result);
            
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        }
    }


    /**
     * Test of add method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testAdd() {
        System.out.println("add");
        
        Point2D.Double c0 = null;
        BezierPath instance = new BezierPath();
        
        instance.add(c0);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPoint method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testAddPoint() {
        System.out.println("addPoint");
        
        double x = 0.0;
        double y = 0.0;
        BezierPath instance = new BezierPath();
        
        instance.addPoint(x, y);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of set method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testSet() {
        System.out.println("set");
        
        int index = 0;
        int coord = 0;
        Point2D.Double p = null;
        BezierPath instance = new BezierPath();
        
        instance.set(index, coord, p);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGet() {
        System.out.println("get");
        
        int index = 0;
        int coord = 0;
        BezierPath instance = new BezierPath();
        
        Point2D.Double expResult = null;
        Point2D.Double result = instance.get(index, coord);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of invalidatePath method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testInvalidatePath() {
        System.out.println("invalidatePath");
        
        BezierPath instance = new BezierPath();
        
        instance.invalidatePath();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validatePath method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testValidatePath() {
        System.out.println("validatePath");
        
        BezierPath instance = new BezierPath();
        
        instance.validatePath();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toGeneralPath method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testToGeneralPath() {
        System.out.println("toGeneralPath");
        
        BezierPath instance = new BezierPath();
        
        GeneralPath expResult = null;
        GeneralPath result = instance.toGeneralPath();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of contains method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testContains() {
        System.out.println("contains");
        
        Point2D p = null;
        BezierPath instance = new BezierPath();
        
        boolean expResult = true;
        boolean result = instance.contains(p);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outlineContains method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testOutlineContains() {
        System.out.println("outlineContains");
        
        Point2D.Double p = null;
        double tolerance = 0.0;
        BezierPath instance = new BezierPath();
        
        boolean expResult = true;
        boolean result = instance.outlineContains(p, tolerance);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersects method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testIntersects() {
        System.out.println("intersects");
        
        Rectangle2D r = null;
        BezierPath instance = new BezierPath();
        
        boolean expResult = true;
        boolean result = instance.intersects(r);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathIterator method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetPathIterator() {
        System.out.println("getPathIterator");
        
        AffineTransform at = null;
        BezierPath instance = new BezierPath();
        
        PathIterator expResult = null;
        PathIterator result = instance.getPathIterator(at);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBounds2D method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetBounds2D() {
        System.out.println("getBounds2D");
        
        BezierPath instance = new BezierPath();
        
        Rectangle2D expResult = null;
        Rectangle2D result = instance.getBounds2D();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBounds2DDouble method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetBounds2DDouble() {
        System.out.println("getBounds2DDouble");
        
        BezierPath instance = new BezierPath();
        
        Rectangle2D.Double expResult = null;
        Rectangle2D.Double result = instance.getBounds2D();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBounds method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetBounds() {
        System.out.println("getBounds");
        
        BezierPath instance = new BezierPath();
        
        Rectangle expResult = null;
        Rectangle result = instance.getBounds();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setClosed method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testSetClosed() {
        System.out.println("setClosed");
        
        boolean newValue = true;
        BezierPath instance = new BezierPath();
        
        instance.setClosed(newValue);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isClosed method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testIsClosed() {
        System.out.println("isClosed");
        
        BezierPath instance = new BezierPath();
        
        boolean expResult = true;
        boolean result = instance.isClosed();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clone method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testClone() {
        System.out.println("clone");
        
        BezierPath instance = new BezierPath();
        
        Object expResult = null;
        Object result = instance.clone();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transform method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testTransform() {
        System.out.println("transform");
        
        AffineTransform tx = null;
        BezierPath instance = new BezierPath();
        
        instance.transform(tx);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTo method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testSetTo() {
        System.out.println("setTo");
        
        BezierPath that = null;
        BezierPath instance = new BezierPath();
        
        instance.setTo(that);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCenter method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetCenter() {
        System.out.println("getCenter");
        
        BezierPath instance = new BezierPath();
        
        Point2D.Double expResult = null;
        Point2D.Double result = instance.getCenter();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of chop method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testChop() {
        System.out.println("chop");
        
        Point2D.Double p = null;
        BezierPath instance = new BezierPath();
        
        Point2D.Double expResult = null;
        Point2D.Double result = instance.chop(p);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of indexOfOutermostNode method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testIndexOfOutermostNode() {
        System.out.println("indexOfOutermostNode");
        
        BezierPath instance = new BezierPath();
        
        int expResult = 0;
        int result = instance.indexOfOutermostNode();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPointOnPath method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetPointOnPath() {
        System.out.println("getPointOnPath");
        
        double relative = 0.0;
        double flatness = 0.0;
        BezierPath instance = new BezierPath();
        
        Point2D.Double expResult = null;
        Point2D.Double result = instance.getPointOnPath(relative, flatness);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findSegment method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testFindSegment() {
        System.out.println("findSegment");
        
        Point2D.Double find = null;
        float tolerance = 0.0F;
        BezierPath instance = new BezierPath();
        
        int expResult = 0;
        int result = instance.findSegment(find, tolerance);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of joinSegments method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testJoinSegments() {
        System.out.println("joinSegments");
        
        Point2D.Double join = null;
        float tolerance = 0.0F;
        BezierPath instance = new BezierPath();
        
        int expResult = 0;
        int result = instance.joinSegments(join, tolerance);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitSegment method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testSplitSegment() {
        System.out.println("splitSegment");
        
        // Split bezier path at a straight segment
        Point2D.Double split = new Point2D.Double(50, 50);
        float tolerance = 0.0F;
        BezierPath instance = new BezierPath();
        instance.addPoint(0, 0);
        instance.addPoint(100, 100);
        int result = instance.splitSegment(split, tolerance);
        assertEquals(1, result);
        assertTrue(instance.size() == 3);
        assertTrue(instance.get(0).equals(new BezierPath.Node(0, 0)));
        assertTrue(instance.get(1).equals(new BezierPath.Node(50, 50)));
        assertTrue(instance.get(2).equals(new BezierPath.Node(100, 100)));
    }

    /**
     * Test of moveTo method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testMoveTo() {
        System.out.println("moveTo");
        
        double x1 = 0.0;
        double y1 = 0.0;
        BezierPath instance = new BezierPath();
        
        instance.moveTo(x1, y1);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lineTo method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testLineTo() {
        System.out.println("lineTo");
        
        double x1 = 0.0;
        double y1 = 0.0;
        BezierPath instance = new BezierPath();
        
        instance.lineTo(x1, y1);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of quadTo method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testQuadTo() {
        System.out.println("quadTo");
        
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        BezierPath instance = new BezierPath();
        
        instance.quadTo(x1, y1, x2, y2);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of curveTo method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testCurveTo() {
        System.out.println("curveTo");
        
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double x3 = 0.0;
        double y3 = 0.0;
        BezierPath instance = new BezierPath();
        
        instance.curveTo(x1, y1, x2, y2, x3, y3);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toPolygonArray method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testToPolygonArray() {
        System.out.println("toPolygonArray");
        
        BezierPath instance = new BezierPath();
        
        Point2D.Double[] expResult = null;
        Point2D.Double[] result = instance.toPolygonArray();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWindingRule method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testSetWindingRule() {
        System.out.println("setWindingRule");
        
        int newValue = 0;
        BezierPath instance = new BezierPath();
        
        instance.setWindingRule(newValue);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWindingRule method, of class org.jhotdraw.geom.BezierPath.
     */
    public void testGetWindingRule() {
        System.out.println("getWindingRule");
        
        BezierPath instance = new BezierPath();
        
        int expResult = 0;
        int result = instance.getWindingRule();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
