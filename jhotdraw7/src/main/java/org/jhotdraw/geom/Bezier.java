/*
 * @(#)Bezier.java 2.0.1  2006-06-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.geom;

import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
/**
 * Provides algorithms for fitting Bezier curves to a set of digitized points.
 * <p>
 * Source:<br>
 * An Algorithm for Automatically Fitting Digitized Curves
 * by Philip J. Schneider.<br>
 * from "Graphics Gems", Academic Press, 1990
 *
 * @version 2.0.1 2006-06-14 Fit bezier curve must preserve closed state of
 * fitted BezierPath object.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 March 14, 2004.
 * @author Werner Randelshofer
 */
public class Bezier {
    /**
     * The most points you can have.
     */
    private final static int MAXPOINTS = 1000;
    
    /** Prevent instance creation. */
    private Bezier() {
    }
    
    
    /**
     * Example of how to use the curve-fitting code.  Given an array
     * of points and a tolerance (squared error between points and
     * fitted curve), the algorithm will generate a piecewise
     * cubic Bezier representation that approximates the points.
     * When a cubic is generated, the routine "DrawBezierCurve"
     * is called, which outputs the Bezier curve just created
     * (arguments are the degree and the control points, respectively).
     * Users will have to implement this function themselves
     *  ascii output, etc.
     *
     */
    public static void main(String[] args) {
        
        Point2D.Double[] d = {	/*  Digitized points */
            new Point2D.Double(0.0, 0.0),
            new Point2D.Double(0.0, 0.5),
            new Point2D.Double(1.1, 1.4),
            new Point2D.Double(2.1, 1.6),
            new Point2D.Double(3.2, 1.1),
            new Point2D.Double(4.0, 0.2),
            new Point2D.Double(4.0, 0.0),
        };
        double	error = 4.0;		/*  Squared error */
        GeneralPath path = fitCurve(d, error);	/*  Fit the Bezier curves */
        System.out.println(path);
    }
    /**
     * Fit a Bezier curve to a set of digitized points.
     *
     * @param p  Polygon with a set of digitized points.
     * @param error User-defined error squared.
     * @return Returns a GeneralPath containing the bezier curves.
     */
    public static GeneralPath fitCurve(Polygon p, double error) {
        Point2D.Double[] d = new Point2D.Double[p.npoints];
        for (int i=0; i < d.length; i++) {
            d[i] = new Point2D.Double(p.xpoints[i], p.ypoints[i]);
        }
        return fitCurve(d, error);
    }
    /**
     * Fit a Bezier curve to a set of digitized points.
     *
     * @param d  Array of digitized points.
     * @param error User-defined error squared.
     * @return Returns a GeneralPath containing the bezier curves.
     */
    public static GeneralPath fitCurve(Point2D.Double[] d, double error) {
        Point2D.Double tHat1 = new Point2D.Double();
        Point2D.Double tHat2 = new Point2D.Double(); /*  Unit tangent vectors at endpoints */
        GeneralPath bezierPath = new GeneralPath();
        bezierPath.moveTo((float) d[0].x, (float) d[0].y);
        
        tHat1 = computeLeftTangent(d, 0);
        tHat2 = computeRightTangent(d, d.length - 1);
        
        fitCubic(d, 0, d.length - 1, tHat1, tHat2, error, bezierPath);
        return bezierPath;
    }
    /**
     * Fit a Bezier curve to a set of digitized points.
     *
     * @param path  The path onto which to fit a bezier curve.
     * @param error User-defined error squared.
     * @return Returns a BezierPath containing the bezier curves.
     */
    public static BezierPath fitBezierCurve(BezierPath path, double error) {
        Point2D.Double[] d = path.toPolygonArray(); 
        Point2D.Double tHat1 = new Point2D.Double();
        Point2D.Double tHat2 = new Point2D.Double(); /*  Unit tangent vectors at endpoints */
        BezierPath bezierPath = new BezierPath();
        bezierPath.add(new BezierPath.Node(d[0]));
        
        tHat1 = computeLeftTangent(d, 0);
        tHat2 = computeRightTangent(d, d.length - 1);
        
        fitCubic(d, 0, d.length - 1, tHat1, tHat2, error, bezierPath);
        bezierPath.setClosed(path.isClosed());
        return bezierPath;
    }
    
    
    
    /**
     * Fit a Bezier curve to a (sub)set of digitized points.
     *
     * @param d  Array of digitized points.
     * @param first Indice of first point in d.
     * @param last Indice of last point in d.
     * @param tHat1 Unit tangent vectors at start point.
     * @param tHat2 Unit tanget vector at end point.
     * @param error User-defined error squared.
     * @param bezierPath Path to which the bezier curve segments are added.
     */
    private static void fitCubic(Point2D.Double[] d, int first, int last, 
            Point2D.Double tHat1, Point2D.Double tHat2, double error, GeneralPath bezierPath) {
        Point2D.Double[] bezCurve; /*Control points of fitted Bezier curve*/
        double[] u;		/*  Parameter values for point  */
        double[] uPrime;	/*  Improved parameter values */
        double	maxError;	/*  Maximum fitting error	 */
        int[]	splitPoint = new int[1]; /*  Point to split point set at.
         This is an array of size one, because we need it as an input/output parameter.
         */
        int	nPts;		/*  Number of points in subset  */
        double	iterationError; /*Error below which you try iterating  */
        int	maxIterations = 4; /*  Max times to try iterating  */
        Point2D.Double	tHatCenter = new Point2D.Double(); /* Unit tangent vector at splitPoint */
        int	i;
        
        iterationError = error * error;
        nPts = last - first + 1;
        
        /*  Use heuristic if region only has two points in it */
        if (nPts == 2) {
            double dist = v2DistanceBetween2Points(d[last], d[first]) / 3.0;
            
            bezCurve = new Point2D.Double[4];
            for (i=0; i < bezCurve.length; i++) {
                bezCurve[i] = new Point2D.Double();
            }
            bezCurve[0] = d[first];
            bezCurve[3] = d[last];
            v2Add(bezCurve[0], v2Scale(tHat1, dist), bezCurve[1]);
            v2Add(bezCurve[3], v2Scale(tHat2, dist), bezCurve[2]);
            bezierPath.curveTo(
            (float) bezCurve[1].x, (float) bezCurve[1].y,
            (float) bezCurve[2].x, (float) bezCurve[2].y,
            (float) bezCurve[3].x, (float) bezCurve[3].y
            );
            return;
        }
        
        /*  Parameterize points, and attempt to fit curve */
        u = chordLengthParameterize(d, first, last);
        bezCurve = generateBezier(d, first, last, u, tHat1, tHat2);
        
        /*  Find max deviation of points to fitted curve */
        maxError = computeMaxError(d, first, last, bezCurve, u, splitPoint);
        if (maxError < error) {
            bezierPath.curveTo(
            (float) bezCurve[1].x, (float) bezCurve[1].y,
            (float) bezCurve[2].x, (float) bezCurve[2].y,
            (float) bezCurve[3].x, (float) bezCurve[3].y
            );
            return;
        }
        
        
        /*  If error not too large, try some reparameterization  */
        /*  and iteration */
        if (maxError < iterationError) {
            for (i = 0; i < maxIterations; i++) {
                uPrime = reparameterize(d, first, last, u, bezCurve);
                bezCurve = generateBezier(d, first, last, uPrime, tHat1, tHat2);
                maxError = computeMaxError(d, first, last, bezCurve, uPrime, splitPoint);
                if (maxError < error) {
                    bezierPath.curveTo(
                    (float) bezCurve[1].x, (float) bezCurve[1].y,
                    (float) bezCurve[2].x, (float) bezCurve[2].y,
                    (float) bezCurve[3].x, (float) bezCurve[3].y
                    );
                    return;
                }
                u = uPrime;
            }
        }
        
        /* Fitting failed -- split at max error point and fit recursively */
        tHatCenter = computeCenterTangent(d, splitPoint[0]);
        fitCubic(d, first, splitPoint[0], tHat1, tHatCenter, error, bezierPath);
        v2Negate(tHatCenter);
        fitCubic(d, splitPoint[0], last, tHatCenter, tHat2, error, bezierPath);
    }
    
    /**
     * Fit a Bezier curve to a (sub)set of digitized points.
     *
     * @param d  Array of digitized points.
     * @param first Indice of first point in d.
     * @param last Indice of last point in d.
     * @param tHat1 Unit tangent vectors at start point.
     * @param tHat2 Unit tanget vector at end point.
     * @param error User-defined error squared.
     * @param bezierPath Path to which the bezier curve segments are added.
     */
    private static void fitCubic(Point2D.Double[] d, int first, int last, Point2D.Double tHat1, 
            Point2D.Double tHat2, double error, BezierPath bezierPath) {
        Point2D.Double[] bezCurve; /*Control points of fitted Bezier curve*/
        double[] u;		/*  Parameter values for point  */
        double[] uPrime;	/*  Improved parameter values */
        double	maxError;	/*  Maximum fitting error	 */
        int[]	splitPoint = new int[1]; /*  Point to split point set at.
         This is an array of size one, because we need it as an input/output parameter.
         */
        int	nPts;		/*  Number of points in subset  */
        double	iterationError; /*Error below which you try iterating  */
        int	maxIterations = 4; /*  Max times to try iterating  */
        Point2D.Double	tHatCenter = new Point2D.Double(); /* Unit tangent vector at splitPoint */
        int	i;
        
        iterationError = error * error;
        nPts = last - first + 1;
        
        /*  Use heuristic if region only has two points in it */
        if (nPts == 2) {
            double dist = v2DistanceBetween2Points(d[last], d[first]) / 3.0;
            
            bezCurve = new Point2D.Double[4];
            for (i=0; i < bezCurve.length; i++) {
                bezCurve[i] = new Point2D.Double();
            }
            bezCurve[0] = d[first];
            bezCurve[3] = d[last];
            v2Add(bezCurve[0], v2Scale(tHat1, dist), bezCurve[1]);
            v2Add(bezCurve[3], v2Scale(tHat2, dist), bezCurve[2]);
            bezierPath.curveTo(
             bezCurve[1].x, bezCurve[1].y,
             bezCurve[2].x, bezCurve[2].y,
             bezCurve[3].x, bezCurve[3].y
            );
            return;
        }
        
        /*  Parameterize points, and attempt to fit curve */
        u = chordLengthParameterize(d, first, last);
        bezCurve = generateBezier(d, first, last, u, tHat1, tHat2);
        
        /*  Find max deviation of points to fitted curve */
        maxError = computeMaxError(d, first, last, bezCurve, u, splitPoint);
        if (maxError < error) {
            bezierPath.curveTo(
             bezCurve[1].x,  bezCurve[1].y,
             bezCurve[2].x, bezCurve[2].y,
            bezCurve[3].x, bezCurve[3].y
            );
            return;
        }
        
        
        /*  If error not too large, try some reparameterization  */
        /*  and iteration */
        if (maxError < iterationError) {
            for (i = 0; i < maxIterations; i++) {
                uPrime = reparameterize(d, first, last, u, bezCurve);
                bezCurve = generateBezier(d, first, last, uPrime, tHat1, tHat2);
                maxError = computeMaxError(d, first, last, bezCurve, uPrime, splitPoint);
                if (maxError < error) {
                    bezierPath.curveTo(
                     bezCurve[1].x,  bezCurve[1].y,
                    bezCurve[2].x, bezCurve[2].y,
                    bezCurve[3].x,  bezCurve[3].y
                    );
                    return;
                }
                u = uPrime;
            }
        }
        
        /* Fitting failed -- split at max error point and fit recursively */
        tHatCenter = computeCenterTangent(d, splitPoint[0]);
        fitCubic(d, first, splitPoint[0], tHat1, tHatCenter, error, bezierPath);
        v2Negate(tHatCenter);
        fitCubic(d, splitPoint[0], last, tHatCenter, tHat2, error, bezierPath);
    }
    
    /**
     * Use least-squares method to find Bezier control points for region.
     *
     * @param d  Array of digitized points.
     * @param first Indice of first point in d.
     * @param last Indice of last point in d.
     * @param uPrime Parameter values for region .
     * @param tHat1 Unit tangent vectors at start point.
     * @param tHat2 Unit tanget vector at end point.
     */
    private static Point2D.Double[] generateBezier(Point2D.Double[] d, int first, int last, double[] uPrime, Point2D.Double tHat1, Point2D.Double tHat2) {
        int 	i;
        Point2D.Double[][] A = new Point2D.Double[MAXPOINTS][2]; /* Precomputed rhs for eqn	*/
        int 	nPts;			/* Number of pts in sub-curve */
        double[][] C = new double[2][2];/* Matrix C		*/
        double[] X = new double[2];	/* Matrix X			*/
        double 	det_C0_C1,		/* Determinants of matrices	*/
        det_C0_X,
        det_X_C1;
        double 	alpha_l,		/* Alpha values, left and right	*/
        alpha_r;
        Point2D.Double 	tmp = new Point2D.Double(); /* Utility variable		*/
        Point2D.Double[] bezCurve;	/* RETURN bezier curve ctl pts	*/
        
        bezCurve = new Point2D.Double[4];
        for (i=0; i < bezCurve.length; i++) {
            bezCurve[i] = new Point2D.Double();
        }
        
        nPts = last - first + 1;
        
        
        /* Compute the A's	*/
        for (i = 0; i < nPts; i++) {
            Point2D.Double v1, v2;
            v1 = (Point2D.Double) tHat1.clone();
            v2 = (Point2D.Double) tHat2.clone();
            v2Scale(v1, b1(uPrime[i]));
            v2Scale(v2, b2(uPrime[i]));
            A[i][0] = v1;
            A[i][1] = v2;
        }
        
        /* Create the C and X matrices	*/
        C[0][0] = 0.0;
        C[0][1] = 0.0;
        C[1][0] = 0.0;
        C[1][1] = 0.0;
        X[0]    = 0.0;
        X[1]    = 0.0;
        
        for (i = 0; i < nPts; i++) {
            C[0][0] += v2Dot(A[i][0], A[i][0]);
            C[0][1] += v2Dot(A[i][0], A[i][1]);
            /*					C[1][0] += V2Dot(&A[i][0], &A[i][1]);*/
            C[1][0] = C[0][1];
            C[1][1] += v2Dot(A[i][1], A[i][1]);
            
            tmp = v2SubII(d[first + i],
            v2AddII(
            v2ScaleIII(d[first], b0(uPrime[i])),
            v2AddII(
            v2ScaleIII(d[first], b1(uPrime[i])),
            v2AddII(
            v2ScaleIII(d[last], b2(uPrime[i])),
            v2ScaleIII(d[last], b3(uPrime[i]))))));
            
            
            X[0] += v2Dot(A[i][0], tmp);
            X[1] += v2Dot(A[i][1], tmp);
        }
        
        /* Compute the determinants of C and X	*/
        det_C0_C1 = C[0][0] * C[1][1] - C[1][0] * C[0][1];
        det_C0_X  = C[0][0] * X[1]    - C[0][1] * X[0];
        det_X_C1  = X[0]    * C[1][1] - X[1]    * C[0][1];
        
        /* Finally, derive alpha values	*/
        if (det_C0_C1 == 0.0) {
            det_C0_C1 = (C[0][0] * C[1][1]) * 10e-12;
        }
        alpha_l = det_X_C1 / det_C0_C1;
        alpha_r = det_C0_X / det_C0_C1;
        
        
        /*  If alpha negative, use the Wu/Barsky heuristic (see text) */
        /* (if alpha is 0, you get coincident control points that lead to
         * divide by zero in any subsequent NewtonRaphsonRootFind() call. */
        if (alpha_l < 1.0e-6 || alpha_r < 1.0e-6) {
            double dist = v2DistanceBetween2Points(d[last], d[first]) / 3.0;
            
            bezCurve[0] = d[first];
            bezCurve[3] = d[last];
            v2Add(bezCurve[0], v2Scale(tHat1, dist), bezCurve[1]);
            v2Add(bezCurve[3], v2Scale(tHat2, dist), bezCurve[2]);
            return (bezCurve);
        }
        
        /*  First and last control points of the Bezier curve are */
        /*  positioned exactly at the first and last data points */
        /*  Control points 1 and 2 are positioned an alpha distance out */
        /*  on the tangent vectors, left and right, respectively */
        bezCurve[0] = d[first];
        bezCurve[3] = d[last];
        v2Add(bezCurve[0], v2Scale(tHat1, alpha_l), bezCurve[1]);
        v2Add(bezCurve[3], v2Scale(tHat2, alpha_r), bezCurve[2]);
        return (bezCurve);
    }
    
    
    /**
     * Given set of points and their parameterization, try to find
     * a better parameterization.
     *
     * @param d  Array of digitized points.
     * @param first Indice of first point of region in d.
     * @param last Indice of last point of region in d.
     * @param u Current parameter values.
     * @param bezCurve Current fitted curve.
     */
    private static double[] reparameterize(Point2D.Double[] d, int first, int last, double[] u, Point2D.Double[] bezCurve) {
        int 	nPts = last-first+1;
        int 	i;
        double[] uPrime; /*  New parameter values	*/
        
        uPrime = new double[nPts];
        for (i = first; i <= last; i++) {
            uPrime[i-first] = newtonRaphsonRootFind(bezCurve, d[i], u[i-first]);
        }
        return (uPrime);
    }
    
    
    
    /**
     * Use Newton-Raphson iteration to find better root.
     *
     * @param Q  Current fitted bezier curve.
     * @param P  Digitized point.
     * @param u  Parameter value vor P.
     */
    private static double newtonRaphsonRootFind(Point2D.Double[] Q, Point2D.Double P, double u) {
        double 		numerator, denominator;
        Point2D.Double[] Q1 = new Point2D.Double[3], Q2 = new Point2D.Double[2];	/*  Q' and Q''			*/
        Point2D.Double	Q_u = new Point2D.Double(), Q1_u = new Point2D.Double(), Q2_u = new Point2D.Double(); /*u evaluated at Q, Q', & Q''	*/
        double 		uPrime;		/*  Improved u	*/
        int 		i;
        
        /* Compute Q(u)	*/
        Q_u = bezierII(3, Q, u);
        
        /* Generate control vertices for Q'	*/
        for (i = 0; i <= 2; i++) {
            Q1[i] = new Point2D.Double(
            (Q[i+1].x - Q[i].x) * 3.0,
            (Q[i+1].y - Q[i].y) * 3.0
            );
        }
        
        /* Generate control vertices for Q'' */
        for (i = 0; i <= 1; i++) {
            Q2[i] = new Point2D.Double(
            (Q1[i+1].x - Q1[i].x) * 2.0,
            (Q1[i+1].y - Q1[i].y) * 2.0
            );
        }
        
        /* Compute Q'(u) and Q''(u)	*/
        Q1_u = bezierII(2, Q1, u);
        Q2_u = bezierII(1, Q2, u);
        
        /* Compute f(u)/f'(u) */
        numerator = (Q_u.x - P.x) * (Q1_u.x) + (Q_u.y - P.y) * (Q1_u.y);
        denominator = (Q1_u.x) * (Q1_u.x) + (Q1_u.y) * (Q1_u.y) +
        (Q_u.x - P.x) * (Q2_u.x) + (Q_u.y - P.y) * (Q2_u.y);
        
        /* u = u - f(u)/f'(u) */
        uPrime = u - (numerator/denominator);
        return (uPrime);
    }
    
    
    
    /**
     * Evaluate a Bezier curve at a particular parameter value.
     *
     * @param degree  The degree of the bezier curve.
     * @param V  Array of control points.
     * @param t  Parametric value to find point for.
     */
    private static Point2D.Double bezierII(int degree, Point2D.Double[] V, double t) {
        int 	i, j;
        Point2D.Double Q; /* Point on curve at parameter t	*/
        Point2D.Double[] Vtemp; /* Local copy of control points		*/
        
        /* Copy array	*/
        Vtemp = new Point2D.Double[degree+1];
        for (i = 0; i <= degree; i++) {
            Vtemp[i] = (Point2D.Double) V[i].clone();
        }
        
        /* Triangle computation	*/
        for (i = 1; i <= degree; i++) {
            for (j = 0; j <= degree-i; j++) {
                Vtemp[j].x = (1.0 - t) * Vtemp[j].x + t * Vtemp[j+1].x;
                Vtemp[j].y = (1.0 - t) * Vtemp[j].y + t * Vtemp[j+1].y;
            }
        }
        
        Q = Vtemp[0];
        return Q;
    }
    
    
    /**
     *  B0, B1, B2, B3 :
     *	Bezier multipliers
     */
    private static double b0(double u) {
        double tmp = 1.0 - u;
        return (tmp * tmp * tmp);
    }
    
    
    private static double b1(double u) {
        double tmp = 1.0 - u;
        return (3 * u * (tmp * tmp));
    }
    
    private static double b2(double u) {
        double tmp = 1.0 - u;
        return (3 * u * u * tmp);
    }
    
    private static double b3(double u) {
        return (u * u * u);
    }
    
    
    
    /**
     * Approximate unit tangents at "left" endpoint of digitized curve.
     *
     * @param d Digitized points.
     * @param end Index to "left" end of region.
     */
    private static Point2D.Double computeLeftTangent(Point2D.Double[] d, int end) {
        Point2D.Double	tHat1 = new Point2D.Double();
        tHat1 = v2SubII(d[end+1], d[end]);
        tHat1 = v2Normalize(tHat1);
        return tHat1;
    }
    
    /**
     * Approximate unit tangents at "right" endpoint of digitized curve.
     *
     * @param d Digitized points.
     * @param end Index to "right" end of region.
     */
    private static Point2D.Double computeRightTangent(Point2D.Double[] d, int end) {
        Point2D.Double tHat2 = new Point2D.Double();
        tHat2 = v2SubII(d[end-1], d[end]);
        tHat2 = v2Normalize(tHat2);
        return tHat2;
    }
    
    
    /**
     * Approximate unit tangents at "center" of digitized curve.
     *
     * @param d Digitized points.
     * @param center Index to "center" end of region.
     */
    private static Point2D.Double computeCenterTangent(Point2D.Double[] d, int center) {
        Point2D.Double V1 = new Point2D.Double(), V2 = new Point2D.Double(), tHatCenter = new Point2D.Double();
        
        V1 = v2SubII(d[center-1], d[center]);
        V2 = v2SubII(d[center], d[center+1]);
        tHatCenter.x = (V1.x + V2.x)/2.0;
        tHatCenter.y = (V1.y + V2.y)/2.0;
        tHatCenter = v2Normalize(tHatCenter);
        return tHatCenter;
    }
    
    /**
     * Assign parameter values to digitized points
     * using relative distances between points.
     *
     * @param d Digitized points.
     * @param first Indice of first point of region in d.
     * @param last Indice of last point of region in d.
     */
    private static double[] chordLengthParameterize(Point2D.Double[] d, int first, int last) {
        int	i;
        double[] u;	/*  Parameterization		*/
        
        u = new double[last-first+1];
        
        u[0] = 0.0;
        for (i = first+1; i <= last; i++) {
            u[i-first] = u[i-first-1] +
            v2DistanceBetween2Points(d[i], d[i-1]);
        }
        
        for (i = first + 1; i <= last; i++) {
            u[i-first] = u[i-first] / u[last-first];
        }
        
        return(u);
    }
    
    
    
    
    /**
     * Find the maximum squared distance of digitized points
     * to fitted curve.
     *
     * @param d Digitized points.
     * @param first Indice of first point of region in d.
     * @param last Indice of last point of region in d.
     * @param bezCurve Fitted Bezier curve
     * @param u Parameterization of points*
     * @param splitPoint Point of maximum error (input/output parameter, must be
     * an array of 1)
     */
    private static double computeMaxError(Point2D.Double[] d, int first, int last, Point2D.Double[] bezCurve, double[] u, int[] splitPoint) {
        int		i;
        double	maxDist;		/*  Maximum error */
        double	dist;		/*  Current error */
        Point2D.Double	P = new Point2D.Double(); /*  Point on curve */
        Point2D.Double	v = new Point2D.Double(); /*  Vector from point to curve */
        
        splitPoint[0] = (last - first + 1)/2;
        maxDist = 0.0;
        for (i = first + 1; i < last; i++) {
            P = bezierII(3, bezCurve, u[i-first]);
            v = v2SubII(P, d[i]);
            dist = v2SquaredLength(v);
            if (dist >= maxDist) {
                maxDist = dist;
                splitPoint[0] = i;
            }
        }
        return (maxDist);
    }
    
    private static Point2D.Double v2AddII(Point2D.Double a, Point2D.Double b) {
        Point2D.Double c = new Point2D.Double();
        c.x = a.x + b.x;  c.y = a.y + b.y;
        return c;
    }
    private static Point2D.Double v2ScaleIII(Point2D.Double v, double s) {
        Point2D.Double result = new Point2D.Double();
        result.x = v.x * s; result.y = v.y * s;
        return result;
    }
    
    private static Point2D.Double v2SubII(Point2D.Double a, Point2D.Double b) {
        Point2D.Double c = new Point2D.Double();
        c.x = a.x - b.x; c.y = a.y - b.y;
        return (c);
    }
    
    
    /* -------------------------------------------------------------------------
     * GraphicsGems.c
     * 2d and 3d Vector C Library
     * by Andrew Glassner
     * from "Graphics Gems", Academic Press, 1990
     * -------------------------------------------------------------------------
     */
    /**
     * Return the distance between two points
     */
    private static double v2DistanceBetween2Points(Point2D.Double a, Point2D.Double b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt((dx*dx)+(dy*dy));
    }
    
    /**
     * Scales the input vector to the new length and returns it.
     */
    private static Point2D.Double v2Scale(Point2D.Double v, double newlen) {
        double len = v2Length(v);
        if (len != 0.0) { v.x *= newlen/len;   v.y *= newlen/len; }
        return v;
    }
    
    /**
     * Returns length of input vector.
     */
    private static double v2Length(Point2D.Double a) {
        return Math.sqrt(v2SquaredLength(a));
    }
    /**
     * Returns squared length of input vector.
     */
    private static double v2SquaredLength(Point2D.Double a) {
        return (a.x * a.x)+(a.y * a.y);
    }
    
    /**
     * Return vector sum c = a+b.
     */
    private static Point2D.Double v2Add(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        c.x = a.x+b.x;  c.y = a.y+b.y;
        return c;
    }
    /**
     * Negates the input vector and returns it.
     */
    private static Point2D.Double v2Negate(Point2D.Double v) {
        v.x = -v.x;  v.y = -v.y;
        return v;
    }
    /**
     * Return the dot product of vectors a and b.
     */
    private static double v2Dot(Point2D.Double a, Point2D.Double b) {
        return (a.x*b.x)+(a.y*b.y);
    }
    /**
     * Normalizes the input vector and returns it.
     */
    private static Point2D.Double v2Normalize(Point2D.Double v) {
        double len = v2Length(v);
        if (len != 0.0) { v.x /= len;  v.y /= len; }
        return v;
    }
}
