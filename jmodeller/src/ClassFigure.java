/**
 * JModeller
 *
 * @version 1.0     15.01.2001
 * @author Wolfram Kaiser (©2001)
 */

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.jhotdraw.contrib.GraphicalCompositeFigure;
import org.jhotdraw.figures.RectangleFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.FigureChangeEvent;
import org.jhotdraw.framework.HandleEnumeration;
import org.jhotdraw.standard.HandleEnumerator;
import org.jhotdraw.standard.NullHandle;
import org.jhotdraw.standard.RelativeLocator;
import org.jhotdraw.util.CollectionsFactory;
import org.jhotdraw.util.StorableInput;
import org.jhotdraw.util.StorableOutput;

/**
 * A ClassFigure is a graphical representation for a class
 * in a class diagramm. A ClassFigure separates the graphical
 * representation from the data model. A class has a class
 * name, attributes and methods. Accordingly, a ClassFigure 
 * consists of other parts to edit the class names, attributes
 * and methods respectively.
 *
 * @author Wolfram Kaiser
 */
public class ClassFigure extends GraphicalCompositeFigure {

    /**
     * Class in the model which is represented by this figure graphically
     */
    private JModellerClass  myClass;

    /**
     * Font used for attribute names
     */
    private Font            attributeFont;

    /**
     * Font used for method names
     */
    private Font            methodFont;

    /**
     * Direct reference to a composite figure which stores text figures for attribute names.
     * This figure is also part of this composite container.
     */
    private GraphicalCompositeFigure    myAttributesFigure;

    /**
     * Direct reference to a composite figure which stores text figures for method names.
     * This figure is also part of this composite container.
     */
    private GraphicalCompositeFigure    myMethodsFigure;

    /**
     * TextFigure for editing the class name
     */
    private TextFigure myClassNameFigure;

  /**
   * Default Font for classname.
   */
  private final static Font CLASSNAME_FONT = new Font( "Helvetica", Font.BOLD, 12 );

  /**
   * Default Font for attribut and operations.
   */
  private final static Font DEFAULT_FONT   = new Font( "Helvetica", Font.PLAIN, 12 );

  /**
   * Some insets.
   */
  private final static Insets INSETS0400 = new Insets( 0, 4, 0, 0 );

  private final static Insets INSETS4440 = new Insets( 4, 4, 4, 0 );


    static final long serialVersionUID = 6098176631854387694L;

    /**
     * Create a new instance of ClassFigure with a RectangleFigure as presentation figure
     */    
    public ClassFigure() {
        this(new RectangleFigure());
    }

    /**
     * Create a new instance of ClassFigure with a given presentation figure
     *
     * @param newPresentationFigure presentation figure
     */    
    public ClassFigure(Figure newPresentationFigure) {
        super(newPresentationFigure);
    }

    /**
     * Hook method called to initizialize a ClassFigure.
     * It is called from the constructors and the clone() method.
     */
    protected void initialize() {
        // start with an empty Composite
        removeAll();

        // set the fonts used to print attributes and methods
		attributeFont = DEFAULT_FONT;
		methodFont    = DEFAULT_FONT;

        // create a new Model object associated with this View figure
        setModellerClass(new JModellerClass());

        // create a TextFigure responsible for the class name
        setClassNameFigure(new TextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getModellerClass().setName(newText);
                update();
            }
        });
    
		getClassNameFigure().setFont(CLASSNAME_FONT);
        getClassNameFigure().setText(getModellerClass().getName());
        
        // add the TextFigure to the Composite
        GraphicalCompositeFigure nameFigure = new GraphicalCompositeFigure(new SeparatorFigure());
        nameFigure.add(getClassNameFigure());
		nameFigure.getLayouter().setInsets(INSETS0400);
        add(nameFigure);

        // create a figure responsible for maintaining attributes
        setAttributesFigure(new GraphicalCompositeFigure(new SeparatorFigure()));
		getAttributesFigure().getLayouter().setInsets(INSETS4440);
        // add the figure to the Composite
        add(getAttributesFigure());

        // create a figure responsible for maintaining methods
        setMethodsFigure(new GraphicalCompositeFigure(new SeparatorFigure()));
		getMethodsFigure().getLayouter().setInsets(INSETS4440);
        // add the figure to the Composite
        add(getMethodsFigure());

        setAttribute(FigureAttributeConstant.POPUP_MENU, createPopupMenu());

        super.initialize();
    }

    /**
     * Factory method to create a popup menu which allows to add attributes and methods.
     *
     * @return newly created popup menu
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("add attribute") {
                public void actionPerformed(ActionEvent event) {
                    addAttribute("attribute");
                }
            });
        popupMenu.add(new AbstractAction("add method") {
                public void actionPerformed(ActionEvent event) {
                    addMethod("method()");
                }
            });

        popupMenu.setLightWeightPopupEnabled(true);
        return popupMenu;
    }

    /**
     * Set the figure which containes all figures representing attribute names.
     *
     * @param newAttributesFigure container for other figures
     */
    protected void setAttributesFigure(GraphicalCompositeFigure newAttributesFigure) {
        myAttributesFigure = newAttributesFigure;
    }

    /**
     * Return the figure which containes all figures representing attribute names.
     *
     * @return container for other figures
     */
    public GraphicalCompositeFigure getAttributesFigure() {
        return myAttributesFigure;
    }

    /**
     * Set the figure which containes all figures representing methods names.
     *
     * @param newMethodsFigure container for other figures
     */
    protected void setMethodsFigure(GraphicalCompositeFigure newMethodsFigure) {
        myMethodsFigure = newMethodsFigure;
    }

    /**
     * Return the figure which containes all figures representing method names.
     *
     * @return container for other figures
     */
    public GraphicalCompositeFigure getMethodsFigure() {
        return myMethodsFigure;
    }

    /**
     * Set the class name text figure responsible for handling user input
     *
     * @param newClassNameFigure text figure for the class name
     */
    protected void setClassNameFigure(TextFigure newClassNameFigure) {
        myClassNameFigure = newClassNameFigure;
    }
    
    /**
     * Return the text figure for the class name
     *
     * @return text figure for the class name
     */
    public TextFigure getClassNameFigure() {
        return myClassNameFigure;
    }

    /**
     * Add a name for an attribute. The underlying class in the model is updated as well.
     * to hold the attribute name.
     *
     * @param newAttribute name of the new attribute
     */
    protected void addAttribute(String newAttribute) {
        getModellerClass().addAttribute(newAttribute);
        TextFigure classFigureAttribute = new TextFigure() {
            public void setText(String newString) {
                if (!getText().equals(newString)) {
                    getModellerClass().renameAttribute(getText(), newString);
                }
                super.setText(newString);
                updateAttributeFigure();
            }
        };
        classFigureAttribute.setText(newAttribute);
        classFigureAttribute.setFont(attributeFont);
        getAttributesFigure().add(classFigureAttribute);
        updateAttributeFigure();
    }

    /**
     * Remove an attribute with a given name. The underlying class in the model is updated
     * as well to exclude the attribute name.
     *
     * @param oldAttribute name of the attribute to be removed.
     */
    protected void removeAttribute(Figure oldAttribute) {
        getModellerClass().removeAttribute(((TextFigure)oldAttribute).getText());
        getAttributesFigure().remove(oldAttribute);
        updateAttributeFigure();
    }

    /**
     * Update the attribute figure and the ClassFigure itself as well. This causes calculating
     * the layout of contained figures.
     */
    protected void updateAttributeFigure() {
        getAttributesFigure().update();
        update();
    }

    /**
     * Add a name for a method. The underlying class in the model is updated as well
     * to hold the method name.
     *
     * @param newMethod name of the new method
     */    
    protected void addMethod(String newMethod) {
        getModellerClass().addMethod(newMethod);
        TextFigure classFigureMethod = new TextFigure() {
            public void setText(String newString) {
                if (!getText().equals(newString)) {
                    getModellerClass().renameMethod(getText(), newString);
                }
                super.setText(newString);
                updateMethodFigure();
            }
        };
        classFigureMethod.setText(newMethod);
        classFigureMethod.setFont(methodFont);
        getMethodsFigure().add(classFigureMethod);
        updateMethodFigure();
    }

    /**
     * Remove an method with a given name. The underlying class in the model is updated
     * as well to exclude the method name.
     *
     * @param oldMethod name of the method to be removed.
     */
    protected void removeMethod(Figure oldMethod) {
        getModellerClass().removeMethod(((TextFigure)oldMethod).getText());
        getMethodsFigure().remove(oldMethod);
        updateMethodFigure();
    }

    /**
     * Update the method figure and the ClassFigure itself as well. This causes calculating
     * the layout of contained figures.
     */
    protected void updateMethodFigure() {
        getMethodsFigure().update();
        update();
    }

    /**
     * Set the class in the model which should be represented by this ClassFigure
     *
     * @param newClass class in the model
     */
    protected void setModellerClass(JModellerClass newClass) {
        myClass = newClass;
    }

    /**
     * Return the class from the model which is represented by this ClassFigure
     *
     * @return class from the model
     */
    public JModellerClass getModellerClass() {
        return myClass;
    }
    
    /**
     * Propagate the removeFromDrawing request up to the container.
     * A ClassFigure should not be removed just because one of its childern
     * is removed.
     */
    public void figureRequestRemove(FigureChangeEvent e) {
        Figure removeFigure = e.getFigure();
        if (getAttributesFigure().includes(removeFigure)) {
            removeAttribute(removeFigure);
        }
        else if (getMethodsFigure().includes(removeFigure)) {
            removeMethod(removeFigure);
        }
        else {
            // remove itself
            listener().figureRequestRemove(new FigureChangeEvent(this, displayBox()));
        }
    }

    /**
     * Return default handles on all four edges for this figure.
     */
    public HandleEnumeration handles() {
        List handles = CollectionsFactory.current().createList(4);

        handles.add(new NullHandle(getPresentationFigure(), RelativeLocator.northWest()));
        handles.add(new NullHandle(getPresentationFigure(), RelativeLocator.northEast()));
        handles.add(new NullHandle(getPresentationFigure(), RelativeLocator.southWest()));
        handles.add(new NullHandle(getPresentationFigure(), RelativeLocator.southEast()));

        return new HandleEnumerator(handles);
    }
 
    /**
     * Test whether this figure has child figures.
     *
     * @return true, if there are no child figures, false otherwise
     */
    public boolean isEmpty() {
        return figureCount() == 0;
    }

    /**
     * Read the figure and its contained elements from the StorableOutput and sets
     * the presentation figure and creates the popup menu.
     */
    public void read(StorableInput dr) throws IOException {
        getClassNameFigure().setText(dr.readString());

        int attributesCount = dr.readInt();
        for (int attributeIndex = 0; attributeIndex < attributesCount; attributeIndex++) {
            addAttribute(dr.readString());
        }

        int methodsCount = dr.readInt();
        for (int methodIndex = 0; methodIndex < methodsCount; methodIndex++) {
            addMethod(dr.readString());
        }
        setPresentationFigure((Figure)dr.readStorable());
        setAttribute(FigureAttributeConstant.POPUP_MENU, createPopupMenu());
        update();
    }
    
    /**
     * Write the figure and its contained elements to the StorableOutput.
     */
    public void write(StorableOutput dw) {
        dw.writeString(getModellerClass().getName());
        dw.writeInt(getModellerClass().getNumberOfAttributes());

        Iterator attributeIterator = getModellerClass().getAttributes();
        while (attributeIterator.hasNext()) {
            dw.writeString((String)attributeIterator.next());
        }
        dw.writeInt(getModellerClass().getNumberOfMethods());

        Iterator methodIterator = getModellerClass().getMethods();
        while (methodIterator.hasNext()) {
            dw.writeString((String)methodIterator.next());
        }
        dw.writeStorable(getPresentationFigure());
    }

    /**
     * Read the serialized figure and its contained elements from the input stream and
     * creates the popup menu
     */
    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        // call superclass' private readObject() indirectly
        s.defaultReadObject();
        
        setAttribute(FigureAttributeConstant.POPUP_MENU, createPopupMenu());
    }
}
