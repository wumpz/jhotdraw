/*
 * @(#)FigureSelection.java 5.2
 *
 */

package CH.ifa.draw.framework;

/**
 * FigureSelection enables to transfer the selected figures
 * to a clipboard.<p>
 * Will soon be converted to the JDK 1.1 Transferable interface.
 *
 * @see Clipboard
 */

public interface FigureSelection {

    /**
     * Gets the type of the selection.
     */
    public String getType();

    /**
     * Gets the data of the selection. The result is returned
     * as a Vector of Figures.
     *
     * @return a copy of the figure selection.
     */
    public Object getData(String type);
}

