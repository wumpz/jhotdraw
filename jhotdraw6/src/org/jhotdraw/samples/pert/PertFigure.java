/*
 * @(#)PertFigure.java 5.1
 *
 */

package CH.ifa.draw.samples.pert;

import java.awt.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;


public class PertFigure extends CompositeFigure {
    private static final int BORDER = 3;
    private Rectangle fDisplayBox;
    private Vector fPreTasks;
    private Vector fPostTasks;

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -7877776240236946511L;
    private int pertFigureSerializedDataVersion = 1;

    public PertFigure() {
        initialize();
    }

    public int start() {
        int start = 0;
        Enumeration i = fPreTasks.elements();
        while (i.hasMoreElements()) {
            PertFigure f = (PertFigure) i.nextElement();
            start = Math.max(start, f.end());
        }
        return start;
    }

    public int end() {
        return asInt(2);
    }

    public int duration() {
        return asInt(1);
    }

    public void setEnd(int value) {
        setInt(2, value);
    }

    public void addPreTask(PertFigure figure) {
        if (!fPreTasks.contains(figure)) {
            fPreTasks.addElement(figure);
        }
    }

    public void addPostTask(PertFigure figure) {
        if (!fPostTasks.contains(figure)) {
            fPostTasks.addElement(figure);
        }
    }

    public void removePreTask(PertFigure figure) {
        fPreTasks.removeElement(figure);
    }

    public void removePostTask(PertFigure figure) {
        fPostTasks.removeElement(figure);
    }

    private int asInt(int figureIndex) {
        NumberTextFigure t = (NumberTextFigure)figureAt(figureIndex);
        return t.getValue();
    }

    private String taskName() {
        TextFigure t = (TextFigure)figureAt(0);
        return t.getText();
    }

    private void setInt(int figureIndex, int value) {
        NumberTextFigure t = (NumberTextFigure)figureAt(figureIndex);
        t.setValue(value);
    }

    protected void basicMoveBy(int x, int y) {
	    fDisplayBox.translate(x, y);
	    super.basicMoveBy(x, y);
	}

    public Rectangle displayBox() {
        return new Rectangle(
            fDisplayBox.x,
            fDisplayBox.y,
            fDisplayBox.width,
            fDisplayBox.height);
    }

    public void basicDisplayBox(Point origin, Point corner) {
        fDisplayBox = new Rectangle(origin);
        fDisplayBox.add(corner);
        layout();
    }

    private void drawBorder(Graphics g) {
        super.draw(g);

        Rectangle r = displayBox();

        Figure f = figureAt(0);
        Rectangle rf = f.displayBox();
        g.setColor(Color.gray);
        g.drawLine(r.x, r.y+rf.height+2, r.x+r.width, r.y + rf.height+2);
        g.setColor(Color.white);
        g.drawLine(r.x, r.y+rf.height+3, r.x+r.width, r.y + rf.height+3);

        g.setColor(Color.white);
        g.drawLine(r.x, r.y, r.x, r.y + r.height);
        g.drawLine(r.x, r.y, r.x + r.width, r.y);
        g.setColor(Color.gray);
        g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
        g.drawLine(r.x , r.y + r.height, r.x + r.width, r.y + r.height);
    }

    public void draw(Graphics g) {
        drawBorder(g);
        super.draw(g);
    }

    public Vector handles() {
        Vector handles = new Vector();
        handles.addElement(new NullHandle(this, RelativeLocator.northWest()));
        handles.addElement(new NullHandle(this, RelativeLocator.northEast()));
        handles.addElement(new NullHandle(this, RelativeLocator.southWest()));
        handles.addElement(new NullHandle(this, RelativeLocator.southEast()));
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(),
                                new PertDependency())
                           );
        return handles;
    }

    private void initialize() {
        fPostTasks = new Vector();
        fPreTasks = new Vector();
        fDisplayBox = new Rectangle(0, 0, 0, 0);

        Font f = new Font("Helvetica", Font.PLAIN, 12);
        Font fb = new Font("Helvetica", Font.BOLD, 12);

        TextFigure name = new TextFigure();
        name.setFont(fb);
        name.setText("Task");
        //name.setAttribute("TextColor",Color.white);
        add(name);

        NumberTextFigure duration = new NumberTextFigure();
        duration.setValue(0);
        duration.setFont(fb);
        add(duration);

        NumberTextFigure end = new NumberTextFigure();
        end.setValue(0);
        end.setFont(f);
        end.setReadOnly(true);
        add(end);
    }

    private void layout() {
	    Point partOrigin = new Point(fDisplayBox.x, fDisplayBox.y);
	    partOrigin.translate(BORDER, BORDER);
	    Dimension extent = new Dimension(0, 0);

        FigureEnumeration k = figures();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();

		    Dimension partExtent = f.size();
		    Point corner = new Point(
		                        partOrigin.x+partExtent.width,
		                        partOrigin.y+partExtent.height);
    		f.basicDisplayBox(partOrigin, corner);

		    extent.width = Math.max(extent.width, partExtent.width);
		    extent.height += partExtent.height;
		    partOrigin.y += partExtent.height;
        }
	    fDisplayBox.width = extent.width + 2*BORDER;
	    fDisplayBox.height = extent.height + 2*BORDER;
    }

    private boolean needsLayout() {
	    Dimension extent = new Dimension(0, 0);

        FigureEnumeration k = figures();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();
		    extent.width = Math.max(extent.width, f.size().width);
        }
        int newExtent = extent.width + 2*BORDER;
        return newExtent != fDisplayBox.width;
    }

    public void update(FigureChangeEvent e) {
        if (e.getFigure() == figureAt(1))  // duration has changed
            updateDurations();
        if (needsLayout()) {
            layout();
            changed();
        }
    }

    public void figureChanged(FigureChangeEvent e) {
        update(e);
    }


    public void figureRemoved(FigureChangeEvent e) {
        update(e);
    }

    public void notifyPostTasks() {
        Enumeration i = fPostTasks.elements();
        while (i.hasMoreElements())
            ((PertFigure) i.nextElement()).updateDurations();
    }

    public void updateDurations() {
        int newEnd = start()+duration();
        if (newEnd != end()) {
            setEnd(newEnd);
            notifyPostTasks();
        }
    }

    public boolean hasCycle(Figure start) {
        if (start == this)
            return true;
        Enumeration i = fPreTasks.elements();
        while (i.hasMoreElements()) {
            if (((PertFigure) i.nextElement()).hasCycle(start))
                return true;
        }
        return false;
    }

    //-- store / load ----------------------------------------------

    public void write(StorableOutput dw) {
        super.write(dw);
        dw.writeInt(fDisplayBox.x);
        dw.writeInt(fDisplayBox.y);
        dw.writeInt(fDisplayBox.width);
        dw.writeInt(fDisplayBox.height);

        writeTasks(dw, fPreTasks);
        writeTasks(dw, fPostTasks);
    }

    public void writeTasks(StorableOutput dw, Vector v) {
        dw.writeInt(v.size());
        Enumeration i = v.elements();
        while (i.hasMoreElements())
            dw.writeStorable((Storable) i.nextElement());
    }

    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        fDisplayBox = new Rectangle(
            dr.readInt(),
            dr.readInt(),
            dr.readInt(),
            dr.readInt());
        layout();
        fPreTasks = readTasks(dr);
        fPostTasks = readTasks(dr);
    }

    public Insets connectionInsets() {
        Rectangle r = fDisplayBox;
        int cx = r.width/2;
        int cy = r.height/2;
        return new Insets(cy, cx, cy, cx);
    }

    public Vector readTasks(StorableInput dr) throws IOException {
        int size = dr.readInt();
        Vector v = new Vector(size);
        for (int i=0; i<size; i++)
            v.addElement((Figure)dr.readStorable());
        return v;
    }
}
