/**
 * JModeller
 *
 * @version 1.1     25.02.2002
 * @author Wolfram Kaiser (©2002)
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.jhotdraw.contrib.MDI_DrawApplication;
import org.jhotdraw.figures.ConnectedTextTool;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.DrawingView;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.Tool;
import org.jhotdraw.standard.ConnectionTool;
import org.jhotdraw.standard.CreationTool;
import org.jhotdraw.standard.ToggleGridCommand;
import org.jhotdraw.util.CommandMenu;
import org.jhotdraw.util.UndoableTool;

/**
 * This is the main class to start the JModeller application. The actual main() method
 * is already defined in CH.ifa.draw.application.DrawApplication
 *
 * @see CH.ifa.draw.application.DrawApplication
 */
public class JModellerApplication extends MDI_DrawApplication {

    /**
     * Path where to find the pictures (relatively to where the class files are stored)
     */
    public static final String DIAGRAM_IMAGES = "/images/";

    /**
     * Create a new instance of JModellerApplication
     */
    public JModellerApplication() {
        super("JModeller - Class Diagram Editor");
    }

    /**
     * Create the tools for the toolbar. The tools are
     * a selection tool, a tool to create a new class and
     * two tools to create association and inheritance
     * relationships between classes.
     *
     * @param   palette toolbar to which the tools should be added
     */
    protected void createTools(JToolBar palette) {
        super.createTools(palette);

        Tool tool = new UndoableTool(new ConnectedTextTool(this, new TextFigure()));
        palette.add(createToolButton(IMAGES+"ATEXT", "Label", tool));

        tool = new UndoableTool(new CreationTool(this, new ClassFigure()) {
             public void mouseDrag(MouseEvent e, int x, int y) {
                 // don't track drag events during creation: figure size is
                 //  calculated and independent of current size
             }
        });
        palette.add(createToolButton(DIAGRAM_IMAGES+"CLASS", "New Class", tool));

        tool = new UndoableTool(new ConnectionTool(this, new AssociationLineConnection()));
        palette.add(createToolButton(IMAGES+"LINE", "Association Tool", tool));

        tool = new UndoableTool(new ConnectionTool(this, new DependencyLineConnection()));
        palette.add(createToolButton(DIAGRAM_IMAGES+"DEPENDENCY", "Dependency Tool", tool));

        tool = new UndoableTool(new ConnectionTool(this, new InheritanceLineConnection()));
        palette.add(createToolButton(DIAGRAM_IMAGES+"INHERITANCE", "Inheritance Tool", tool));
    }

    /**
     * Create a special selection tool which reacts on the right mouse button
     * to show a popup menu.
     *
     * @return  selection tool with special behaviour for the right mouse button
     */
    protected Tool createSelectionTool() {
        return new DelegationSelectionTool(this);
    }

    /**
     * Create the menues for a given menu bar.
     *
     * @param   mb  menu bar to which the menus should be added
     */
    protected void createMenus(JMenuBar mb) {
        mb.add(createFileMenu());
        mb.add(createEditMenu());
        mb.add(createAlignmentMenu());
        mb.add(createAttributesMenu());
        mb.add(createLookAndFeelMenu());
    }

    /**
     * Create an attribute menu hiding some special menu entries from
     * the superclass. The attribute menu contains actions which can be
     * performed if a figure is selected in a drawing. For this figure
     * some attributes can be set such as fill and pen colour.
     *
     *  @return newly create attribute menu
     */
    protected JMenu createAttributesMenu() {
        JMenu menu = new JMenu("Attributes");
        menu.add(createColorMenu("Fill Color", FigureAttributeConstant.FILL_COLOR));
        menu.add(createColorMenu("Pen Color", FigureAttributeConstant.FRAME_COLOR));
        return menu;
    }

    /**
     * Create an alignment menu hiding some special menu entries from
     * the superclass. The alignment menu contains actions for aligning
     * figures within the drawing in this case to arrange them in a grid
     * or not.
     *
     * @return  newly created alignment menu
     */
    protected JMenu createAlignmentMenu() {
        CommandMenu menu = new CommandMenu("Align");
        menu.add(new ToggleGridCommand("Toggle Snap to Grid", this, new Point(4,4)));
        return menu;
    }

    /**
     * Create an internal window menu, so several drawing cans be manipulated
     * at the same time.
     *
     * @return  newly created alignment menu
     */
    protected JMenu createFileMenu() {
        JMenu menu = super.createFileMenu();
        menu.insert(
            new AbstractAction("New Window") {
                public void actionPerformed(ActionEvent event) {
                    newWindow(createDrawing());
                }
            }, 5);

		menu.insertSeparator( 6 );

        return menu;
    }

    /**
     * Creates the drawing view used in this application.
     * You need to override this method to use a DrawingView
     * subclass in your application. By default a standard
     * DrawingView is returned.
     */
    protected DrawingView createDrawingView() {
        DrawingView newView = super.createDrawingView();
        newView.setBackground(Color.white);
        return newView;
    }

    /**
     * Start the application by creating an instance and open
     * the editor window.
     */
    public static void main(String[] args) {
        JModellerApplication window = new JModellerApplication();
        window.open();
    }
}