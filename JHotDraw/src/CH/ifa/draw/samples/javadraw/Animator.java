/*
 * @(#)Animator.java 5.1
 *
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Animatable;


public  class Animator extends Thread {

    private DrawingView     fView;
    private Animatable      fAnimatable;

    private boolean             fIsRunning;
    private static final int    DELAY = 1000 / 16;

    public Animator(Animatable animatable, DrawingView view) {
        super("Animator");
        fView = view;
        fAnimatable = animatable;
    }

    public void start() {
        super.start();
        fIsRunning = true;
    }

    public void end() {
        fIsRunning = false;
    }

    public void run() {
        while (fIsRunning) {
            long tm = System.currentTimeMillis();
            fView.freezeView();
            fAnimatable.animationStep();
            fView.checkDamage();
            fView.unfreezeView();

            // Delay for a while
            try {
                tm += DELAY;
                Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

