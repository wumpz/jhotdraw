/*
 * @(#)DefaultDrawingViewTransferHandler.java  1.1.2  2008-03-20
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.draw;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.gui.datatransfer.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.ReversedList;

/**
 * Default TransferHandler for DrawingView objects.
 *
 * @author Werner Randelshofer
 * @version 1.1.2 2008-03-20 After import, only select imported figures in 
 * drawing view. 
 * <br>1.1.1 2008-03-10 In method importData, figures are added to drawing
 * by the InputFormat. 
 * <br>1.1 2007-12-16 Adapted to changes in InputFormat and OutputFormat.
 * <br>1.0 April 13, 2007 Created.
 */
public class DefaultDrawingViewTransferHandler extends TransferHandler {

    private final static boolean DEBUG = false;

    /** Creates a new instance. */
    public DefaultDrawingViewTransferHandler() {
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        if (DEBUG) {
            System.out.println(this + ".importData");
        }
        boolean retValue;
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
            final Drawing drawing = view.getDrawing();

            if (drawing.getInputFormats() == null ||
                    drawing.getInputFormats().size() == 0) {
                if (DEBUG) {
                    System.out.println(this + ".importData failed - drawing has no import formats");
                }
                retValue = false;
            } else {
                retValue = false;
                try {
                    // Search for a suitable input format
                    SearchLoop:
                    for (InputFormat format : drawing.getInputFormats()) {
                        for (DataFlavor flavor : t.getTransferDataFlavors()) {
                            if (DEBUG) {
                                System.out.println(this + ".importData trying to match " + format + " to flavor " + flavor);
                            }
                            if (format.isDataFlavorSupported(flavor)) {
                                if (DEBUG) {
                                    System.out.println(this + ".importData importing flavor " + flavor);
                                }
                                 LinkedList<Figure> existingFigures = new LinkedList<Figure>(drawing.getChildren());
                                format.read(t, drawing);
                                final LinkedList<Figure> importedFigures = new LinkedList<Figure>(drawing.getChildren());
                                importedFigures.removeAll(existingFigures);
                                view.clearSelection();
                                view.addToSelection(importedFigures);
                                drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {

                                    public String getPresentationName() {
                                        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                                        return labels.getString("editPaste");
                                    }

                                    public void undo() throws CannotUndoException {
                                        super.undo();
                                        drawing.removeAll(importedFigures);
                                    }

                                    public void redo() throws CannotRedoException {
                                        super.redo();
                                        drawing.addAll(importedFigures);
                                    }
                                });
                                retValue = true;
                                break SearchLoop;
                            }
                        }
                    }
                    // No input format found? Lets see if we got files - we
                    // can handle these
                    if (retValue == false && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        retValue = true;

                        // FIXME - We should perform the following code in a
                        // worker thread.
                                 LinkedList<Figure> existingFigures = new LinkedList<Figure>(drawing.getChildren());
                        for (File file : files) {
                            FileFormatLoop:
                            for (InputFormat format : drawing.getInputFormats()) {
                                if (file.isFile() &&
                                        format.getFileFilter().accept(file)) {
                                    if (DEBUG) {
                                        System.out.println(this + ".importData importing file " + file);
                                    }
                                    format.read(file, drawing);
                                }
                            }
                        }
                        final LinkedList<Figure> importedFigures = new LinkedList<Figure>(drawing.getChildren());
                        importedFigures.removeAll(existingFigures);
                        importedFigures.removeAll(existingFigures);
                        if (importedFigures.size() > 0) {
                            view.clearSelection();
                            view.addToSelection(importedFigures);

                            drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {

                                public String getPresentationName() {
                                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                                    return labels.getString("editPaste");
                                }

                                public void undo() throws CannotUndoException {
                                    super.undo();
                                    drawing.removeAll(importedFigures);
                                }

                                public void redo() throws CannotRedoException {
                                    super.redo();
                                    drawing.addAll(importedFigures);
                                }
                            });
                        }
                    }
                } catch (Throwable e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            retValue = super.importData(comp, t);
        }
        return retValue;
    }

    @Override
    public int getSourceActions(JComponent c) {
        int retValue;
        if (c instanceof DrawingView) {
            DrawingView view = (DrawingView) c;
            if (DEBUG) {
                System.out.println(this + ".getSourceActions outputFormats.size=" + view.getDrawing().getOutputFormats().size());
            }
            retValue = (view.getDrawing().getOutputFormats().size() > 0 &&
                    view.getSelectionCount() > 0) ? COPY | MOVE : NONE;
        } else {
            retValue = super.getSourceActions(c);
        }
        if (DEBUG) {
            System.out.println(this + ".getSourceActions:" + retValue);
        }
        return retValue;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (DEBUG) {
            System.out.println(this + ".createTransferable");
        }
        Transferable retValue;
        if (c instanceof DrawingView) {
            DrawingView view = (DrawingView) c;
            Drawing drawing = view.getDrawing();

            if (drawing.getOutputFormats() == null ||
                    drawing.getOutputFormats().size() == 0) {
                retValue = null;
            } else {
                java.util.List<Figure> toBeCopied = drawing.sort(view.getSelectedFigures());
                if (toBeCopied.size() > 0) {
                    try {
                        CompositeTransferable transfer = new CompositeTransferable();
                        for (OutputFormat format : drawing.getOutputFormats()) {
                            Transferable t = format.createTransferable(
                                    drawing,
                                    toBeCopied,
                                    view.getScaleFactor());
                            if (!transfer.isDataFlavorSupported(t.getTransferDataFlavors()[0])) {
                                transfer.add(t);
                            }
                        }
                        retValue = transfer;
                    } catch (IOException e) {
                        if (DEBUG) {
                            e.printStackTrace();
                        }
                        retValue = null;
                    }
                } else {
                    retValue = null;
                }
            }
        } else {
            retValue = super.createTransferable(c);
        }

        return retValue;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (DEBUG) {
            System.out.println(this + ".exportDone " + action + " move=" + MOVE);
        }
        if (source instanceof DrawingView) {
            final DrawingView view = (DrawingView) source;
            final Drawing drawing = view.getDrawing();
            if (action == MOVE) {
                final LinkedList<CompositeFigureEvent> deletionEvents = new LinkedList<CompositeFigureEvent>();
                final LinkedList<Figure> selectedFigures = new LinkedList<Figure>(view.getSelectedFigures());

                // Abort, if not all of the selected figures may be removed from the
                // drawing
                for (Figure f : selectedFigures) {
                    if (!f.isRemovable()) {
                        source.getToolkit().beep();
                        return;
                    }
                }

                view.clearSelection();
                CompositeFigureListener removeListener = new CompositeFigureListener() {

                    public void areaInvalidated(CompositeFigureEvent e) {
                    }

                    public void figureAdded(CompositeFigureEvent e) {
                    }

                    public void figureRemoved(CompositeFigureEvent evt) {
                        deletionEvents.addFirst(evt);
                    }
                };
                drawing.addCompositeFigureListener(removeListener);
                drawing.removeAll(selectedFigures);
                drawing.removeCompositeFigureListener(removeListener);
                drawing.removeAll(selectedFigures);
                drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {

                    public String getPresentationName() {
                        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                        return labels.getString("delete");
                    }

                    public void undo() throws CannotUndoException {
                        super.undo();
                        view.clearSelection();
                        for (CompositeFigureEvent evt : deletionEvents) {
                            drawing.add(evt.getIndex(), evt.getChildFigure());
                        }
                        view.addToSelection(selectedFigures);
                    }

                    public void redo() throws CannotRedoException {
                        super.redo();
                        for (CompositeFigureEvent evt : new ReversedList<CompositeFigureEvent>(deletionEvents)) {
                            drawing.remove(evt.getChildFigure());
                        }
                    }
                });
            }
        } else {
            super.exportDone(source, data, action);
        }
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        if (DEBUG) {
            System.out.println(this + ".exportAsDrag");
        }
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
        // XXX - What kind of drag gesture can we support for this??
        } else {
            super.exportAsDrag(comp, e, action);
        }
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        if (DEBUG) {
            System.out.println(this + ".getVisualRepresentation");
        }
        Image image = null;
        try {
            image = (Image) t.getTransferData(DataFlavor.imageFlavor);
        } catch (IOException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
        } catch (UnsupportedFlavorException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
        }

        return (image == null) ? null : new ImageIcon(image);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (DEBUG) {
            System.out.println(this + ".canImport " + Arrays.asList(transferFlavors));
        }
        boolean retValue;
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
            Drawing drawing = view.getDrawing();

            // Search for a suitable input format
            retValue = false;
            SearchLoop:
            for (InputFormat format : drawing.getInputFormats()) {
                for (DataFlavor flavor : transferFlavors) {
                    if (flavor.isFlavorJavaFileListType() ||
                            format.isDataFlavorSupported(flavor)) {
                        retValue = true;
                        break SearchLoop;
                    }
                }
            }
        } else {
            retValue = super.canImport(comp, transferFlavors);
        }
        return retValue;
    }

    private void getDrawing() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
