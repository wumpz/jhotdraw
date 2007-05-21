/*
 * @(#)DefaultDrawingViewTransferHandler.java  1.0  April 13, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
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
 * @version 1.0 April 13, 2007 Created.
 */
public class DefaultDrawingViewTransferHandler extends TransferHandler {
    private final static boolean DEBUG = false;
    
    /** Creates a new instance. */
    public DefaultDrawingViewTransferHandler() {
    }
    
    @Override public boolean importData(JComponent comp, Transferable t) {
        if (DEBUG) System.out.println(this+".importData");
        boolean retValue;
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
            final Drawing drawing = view.getDrawing();
            
            if (drawing.getInputFormats() == null ||
                    drawing.getInputFormats().size() == 0) {
                retValue = false;
            } else {
                retValue = false;
                try {
                    // Search for a suitable input format
                    SearchLoop: for (InputFormat format : drawing.getInputFormats()) {
                        for (DataFlavor flavor : t.getTransferDataFlavors()) {
                            if (format.isDataFlavorSupported(flavor)) {
                                final java.util.List<Figure> importedFigures = format.readFigures(t);
                                view.clearSelection();
                                drawing.addAll(importedFigures);
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
                        
                        final java.util.List<Figure> storedFigures = new LinkedList<Figure>(drawing.getFigures());
                        
                        // FIXME - We should perform the following code in a
                        // worker thread.
                        for (File file : files) {
                            FileFormatLoop: for (InputFormat format : drawing.getInputFormats()) {
                                if (file.isFile() &&
                                        format.getFileFilter().accept(file)) {
                                    format.read(file, drawing);
                                }
                            }
                        }
                        
                        final LinkedList<Figure> importedFigures = new LinkedList<Figure>(drawing.getFigures());
                        importedFigures.removeAll(storedFigures);
                        if (importedFigures.size() > 0) {
                            /*
                            Rectangle2D.Double invalidatedArea = null;
                            for (Figure f : importedFigures) {
                                if (invalidatedArea == null) {
                                    invalidatedArea = f.getDrawingArea();
                                } else {
                                    invalidatedArea.add(f.getDrawingArea());
                                }
                            }*/
                            view.clearSelection();
                            view.addToSelection(importedFigures);
                            //view.getComponent().repaint(view.drawingToView(invalidatedArea));
                            
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
                    if (DEBUG) e.printStackTrace();
                }
            }
        } else {
            retValue = super.importData(comp, t);
        }
        return retValue;
    }
    
    @Override public int getSourceActions(JComponent c) {
        if (DEBUG) System.out.println(this+".getSourceActions");
        int retValue;
        if (c instanceof DrawingView) {
            DrawingView view = (DrawingView) c;
            retValue = (view.getDrawing().getOutputFormats().size() > 0 &&
                    view.getSelectionCount() > 0) ?
                        COPY | MOVE :
                        NONE;
        } else {
            retValue = super.getSourceActions(c);
        }
        return retValue;
    }
    
    @Override protected Transferable createTransferable(JComponent c) {
        if (DEBUG) System.out.println(this+".createTransferable");
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
                                    toBeCopied,
                                    view.getScaleFactor()
                                    );
                            if (! transfer.isDataFlavorSupported(t.getTransferDataFlavors()[0])) {
                                transfer.add(t);
                            }
                        }
                        retValue = transfer;
                    } catch (IOException e) {
                        if (DEBUG) e.printStackTrace();
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
    
    @Override protected void exportDone(JComponent source, Transferable data, int action) {
        if (DEBUG) System.out.println(this+".exportDone "+action+" move="+MOVE);
        if (source instanceof DrawingView) {
            final DrawingView view = (DrawingView) source;
            final Drawing drawing = view.getDrawing();
            if (action == MOVE) {
                final LinkedList<DrawingEvent> deletionEvents = new LinkedList<DrawingEvent>();
                final LinkedList<Figure> selectedFigures = new LinkedList<Figure>(view.getSelectedFigures());
                view.clearSelection();
                DrawingListener removeListener = new DrawingListener() {
                    public void areaInvalidated(DrawingEvent e) {
                    }
                    
                    public void figureAdded(DrawingEvent e) {
                    }
                    
                    public void figureRemoved(DrawingEvent evt) {
                        deletionEvents.addFirst(evt);
                    }
                    
                };
                drawing.addDrawingListener(removeListener);
                drawing.removeAll(selectedFigures);
                drawing.removeDrawingListener(removeListener);
                drawing.removeAll(selectedFigures);
                drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
                    public String getPresentationName() {
                        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                        return labels.getString("delete");
                    }
                    public void undo() throws CannotUndoException {
                        super.undo();
                        view.clearSelection();
                        for (DrawingEvent evt : deletionEvents) {
                            drawing.add(evt.getIndex(), evt.getFigure());
                        }
                        view.addToSelection(selectedFigures);
                    }
                    public void redo() throws CannotRedoException {
                        super.redo();
                        for (DrawingEvent evt : new ReversedList<DrawingEvent>(deletionEvents)) {
                            drawing.remove(evt.getFigure());
                        }
                    }
                });
            }
        } else {
            super.exportDone(source, data, action);
        }
    }
    
    @Override public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        if (DEBUG) System.out.println(this+".exportAsDrag");
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
            // XXX - What kind of drag gesture can we support for this??
        } else {
            super.exportAsDrag(comp, e, action);
        }
    }
    
    @Override public Icon getVisualRepresentation(Transferable t) {
        if (DEBUG) System.out.println(this+".getVisualRepresentation");
        Image image = null;
        try {
            image = (Image) t.getTransferData(DataFlavor.imageFlavor);
        } catch (IOException ex) {
            if (DEBUG) ex.printStackTrace();
        } catch (UnsupportedFlavorException ex) {
            if (DEBUG) ex.printStackTrace();
        }
        
        return (image == null) ? null : new ImageIcon(image);
    }
    
    @Override public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (DEBUG) System.out.println(this+".canImport "+Arrays.asList(transferFlavors));
        boolean retValue;
        if (comp instanceof DrawingView) {
            DrawingView view = (DrawingView) comp;
            Drawing drawing = view.getDrawing();
            
            // Search for a suitable input format
            retValue = false;
            SearchLoop: for (InputFormat format : drawing.getInputFormats()) {
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
