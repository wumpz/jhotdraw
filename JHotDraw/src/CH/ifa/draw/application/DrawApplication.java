/*
 * @(#)DrawApplication.java 5.1
 *
 */

package CH.ifa.draw.application;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;

/**
 * DrawApplication defines a standard presentation for
 * standalone drawing editors. The presentation is
 * customized in subclasses.
 * The application is started as follows:
 * <pre>
 * public static void main(String[] args) {
 *     MayDrawApp window = new MyDrawApp();
 *     window.open();
 * }
 * </pre>
 */

public  class DrawApplication
        extends Frame
        implements DrawingEditor, PaletteListener {

    private Drawing             fDrawing;
    private Tool                fTool;
    private Iconkit             fIconkit;

    private TextField           fStatusLine;
    private StandardDrawingView fView;
    private ToolButton          fDefaultToolButton;
    private ToolButton          fSelectedToolButton;

    private String              fDrawingFilename;
    static String               fgUntitled = "untitled";

    // the image resource path
    private static final String fgDrawPath = "/CH/ifa/draw/";
    public static final String IMAGES = fgDrawPath+"images/";

    /**
     * The index of the file menu in the menu bar.
     */
    public static final int    FILE_MENU = 0;
    /**
     * The index of the edit menu in the menu bar.
     */
    public static final int    EDIT_MENU = 1;
    /**
     * The index of the alignment menu in the menu bar.
     */
    public static final int    ALIGNMENT_MENU = 2;
    /**
     * The index of the attributes menu in the menu bar.
     */
    public static final int    ATTRIBUTES_MENU = 3;


    /**
     * Constructs a drawing window with a default title.
     */
    public DrawApplication() {
        super("JHotDraw");
    }

    /**
     * Constructs a drawing window with the given title.
     */
    public DrawApplication(String title) {
        super(title);
    }

    /**
     * Opens the window and initializes its contents.
     * Clients usually only call but don't override it.
     */

    public void open() {
        fIconkit = new Iconkit(this);
		setLayout(new BorderLayout());

        fView = createDrawingView();
        Component contents = createContents(fView);
        add("Center", contents);
        //add("Center", fView);

        Panel tools = createToolPalette();
        createTools(tools);
        add("West", tools);

        fStatusLine = createStatusLine();
        add("South", fStatusLine);

		MenuBar mb = new MenuBar();
		createMenus(mb);
		setMenuBar(mb);

        initDrawing();
        Dimension d = defaultSize();
		setSize(d.width, d.height);

        addListeners();

        show();
    }

    /**
     * Registers the listeners for this window
     */
	protected void addListeners() {
	    addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    exit();
                }
            }
        );
    }

    private void initDrawing() {
        fDrawing = createDrawing();
        fDrawingFilename = fgUntitled;
        fView.setDrawing(fDrawing);
        toolDone();
    }

    /**
     * Creates the standard menus. Clients override this
     * method to add additional menus.
     */
    protected void createMenus(MenuBar mb) {
		mb.add(createFileMenu());
		mb.add(createEditMenu());
		mb.add(createAlignmentMenu());
		mb.add(createAttributesMenu());
		mb.add(createDebugMenu());
    }

    /**
     * Creates the file menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createFileMenu() {
		Menu menu = new Menu("File");
		MenuItem mi = new MenuItem("New", new MenuShortcut('n'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptNew();
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Open...", new MenuShortcut('o'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptOpen();
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Save As...", new MenuShortcut('s'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptSaveAs();
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Save As Serialized...");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptSaveAsSerialized();
		        }
		    }
		);
		menu.add(mi);
		menu.addSeparator();
		mi = new MenuItem("Print...", new MenuShortcut('p'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            print();
		        }
		    }
		);
		menu.add(mi);
		menu.addSeparator();
		mi = new MenuItem("Exit");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            exit();
		        }
		    }
		);
		menu.add(mi);
		return menu;
	}

    /**
     * Creates the edit menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createEditMenu() {
		CommandMenu menu = new CommandMenu("Edit");
		menu.add(new CutCommand("Cut", fView), new MenuShortcut('x'));
		menu.add(new CopyCommand("Copy", fView), new MenuShortcut('c'));
		menu.add(new PasteCommand("Paste", fView), new MenuShortcut('v'));
		menu.addSeparator();
		menu.add(new DuplicateCommand("Duplicate", fView), new MenuShortcut('d'));
		menu.add(new DeleteCommand("Delete", fView));
		menu.addSeparator();
		menu.add(new GroupCommand("Group", fView));
		menu.add(new UngroupCommand("Ungroup", fView));
		menu.addSeparator();
		menu.add(new SendToBackCommand("Send to Back", fView));
		menu.add(new BringToFrontCommand("Bring to Front", fView));
		return menu;
	}

    /**
     * Creates the alignment menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createAlignmentMenu() {
		CommandMenu menu = new CommandMenu("Align");
		menu.add(new ToggleGridCommand("Toggle Snap to Grid", fView, new Point(4,4)));
		menu.addSeparator();
		menu.add(new AlignCommand("Lefts", fView, AlignCommand.LEFTS));
		menu.add(new AlignCommand("Centers", fView, AlignCommand.CENTERS));
		menu.add(new AlignCommand("Rights", fView, AlignCommand.RIGHTS));
		menu.addSeparator();
		menu.add(new AlignCommand("Tops", fView, AlignCommand.TOPS));
		menu.add(new AlignCommand("Middles", fView, AlignCommand.MIDDLES));
		menu.add(new AlignCommand("Bottoms", fView, AlignCommand.BOTTOMS));
		return menu;
	}

    /**
     * Creates the debug menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createDebugMenu() {
		Menu menu = new Menu("Debug");

		MenuItem mi = new MenuItem("Simple Update");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            fView.setDisplayUpdate(new SimpleUpdateStrategy());
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Buffered Update");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            fView.setDisplayUpdate(new BufferedUpdateStrategy());
		        }
		    }
		);
		menu.add(mi);
		return menu;
	}

    /**
     * Creates the attributes menu and its submenus. Clients override this
     * method to add additional menu items.
     */
    protected Menu createAttributesMenu() {
        Menu menu = new Menu("Attributes");
        menu.add(createColorMenu("Fill Color", "FillColor"));
        menu.add(createColorMenu("Pen Color", "FrameColor"));
        menu.add(createArrowMenu());
		menu.addSeparator();
        menu.add(createFontMenu());
        menu.add(createFontSizeMenu());
        menu.add(createFontStyleMenu());
        menu.add(createColorMenu("Text Color", "TextColor"));
        return menu;
    }

    /**
     * Creates the color menu.
     */
    protected Menu createColorMenu(String title, String attribute) {
        CommandMenu menu = new CommandMenu(title);
        for (int i=0; i<ColorMap.size(); i++)
            menu.add(
                new ChangeAttributeCommand(
                    ColorMap.name(i),
                    attribute,
                    ColorMap.color(i),
                    fView
                )
            );
        return menu;
    }

    /**
     * Creates the arrows menu.
     */
    protected Menu createArrowMenu() {
        CommandMenu menu = new CommandMenu("Arrow");
        menu.add(new ChangeAttributeCommand("none",     "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_NONE),  fView));
        menu.add(new ChangeAttributeCommand("at Start", "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_START), fView));
        menu.add(new ChangeAttributeCommand("at End",   "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_END),   fView));
        menu.add(new ChangeAttributeCommand("at Both",  "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_BOTH),  fView));
        return menu;
    }

    /**
     * Creates the fonts menus. It installs all available fonts
     * supported by the toolkit implementation.
     */
    protected Menu createFontMenu() {
        CommandMenu menu = new CommandMenu("Font");
        String fonts[] = Toolkit.getDefaultToolkit().getFontList();
        for (int i = 0; i < fonts.length; i++)
            menu.add(new ChangeAttributeCommand(fonts[i], "FontName", fonts[i],  fView));
        return menu;
    }

    /**
     * Creates the font style menu with entries (Plain, Italic, Bold).
     */
    protected Menu createFontStyleMenu() {
        CommandMenu menu = new CommandMenu("Font Style");
        menu.add(new ChangeAttributeCommand("Plain", "FontStyle", new Integer(Font.PLAIN), fView));
        menu.add(new ChangeAttributeCommand("Italic","FontStyle", new Integer(Font.ITALIC),fView));
        menu.add(new ChangeAttributeCommand("Bold",  "FontStyle", new Integer(Font.BOLD),  fView));
        return menu;
    }

    /**
     * Creates the font size menu.
     */
    protected Menu createFontSizeMenu() {
        CommandMenu menu = new CommandMenu("Font Size");
        int sizes[] = { 9, 10, 12, 14, 18, 24, 36, 48, 72 };
        for (int i = 0; i < sizes.length; i++) {
            menu.add(
                new ChangeAttributeCommand(
                    Integer.toString(sizes[i]),
                    "FontSize",
                    new Integer(sizes[i]),  fView)
                );
        }
        return menu;
    }

    /**
     * Creates the tool palette.
     */
    protected Panel createToolPalette() {
        Panel palette = new Panel();
        palette.setBackground(Color.lightGray);
        palette.setLayout(new PaletteLayout(2,new Point(2,2)));
        return palette;
    }

    /**
     * Creates the tools. By default only the selection tool is added.
     * Override this method to add additional tools.
     * Call the inherited method to include the selection tool.
     * @param palette the palette where the tools are added.
     */
    protected void createTools(Panel palette) {
        Tool tool = createSelectionTool();

        fDefaultToolButton = createToolButton(IMAGES+"SEL", "Selection Tool", tool);
        palette.add(fDefaultToolButton);
    }

    /**
     * Creates the selection tool used in this editor. Override to use
     * a custom selection tool.
     */
    protected Tool createSelectionTool() {
        return new SelectionTool(view());
    }

    /**
     * Creates a tool button with the given image, tool, and text
     */
    protected ToolButton createToolButton(String iconName, String toolName, Tool tool) {
        return new ToolButton(this, iconName, toolName, tool);
    }

    /**
     * Creates the drawing view used in this application.
     * You need to override this method to use a DrawingView
     * subclass in your application. By default a standard
     * DrawingView is returned.
     */
    protected StandardDrawingView createDrawingView() {
        Dimension d = getDrawingViewSize();
        return new StandardDrawingView(this, d.width, d.height);
    }

    /**
     * Override to define the dimensions of the drawing view.
     */
    protected Dimension getDrawingViewSize() {
        return new Dimension(400, 600);
    }

    /**
     * Creates the drawing used in this application.
     * You need to override this method to use a Drawing
     * subclass in your application. By default a standard
     * Drawing is returned.
     */
    protected Drawing createDrawing() {
        return new StandardDrawing();
    }

    /**
     * Creates the contents component of the application
     * frame. By default the DrawingView is returned in
     * a ScrollPane.
     */
    protected Component createContents(StandardDrawingView view) {
        ScrollPane sp = new ScrollPane();
        Adjustable vadjust = sp.getVAdjustable();
        Adjustable hadjust = sp.getHAdjustable();
        hadjust.setUnitIncrement(16);
        vadjust.setUnitIncrement(16);

        sp.add(view);
        return sp;
    }

    /**
     * Sets the drawing to be edited.
     */
    public void setDrawing(Drawing drawing) {
        fView.setDrawing(drawing);
        fDrawing = drawing;
    }

    /**
     * Gets the default size of the window.
     */
    protected Dimension defaultSize() {
        return new Dimension(430,406);
    }

    /**
     * Creates the status line.
     */
    protected TextField createStatusLine() {
        TextField field = new TextField("No Tool", 40);
        field.setEditable(false);
        return field;
    }

    /**
     * Handles a user selection in the palette.
     * @see PaletteListener
     */
    public void paletteUserSelected(PaletteButton button) {
        ToolButton toolButton = (ToolButton) button;
        setTool(toolButton.tool(), toolButton.name());
        setSelected(toolButton);
    }

    /**
     * Handles when the mouse enters or leaves a palette button.
     * @see PaletteListener
     */
    public void paletteUserOver(PaletteButton button, boolean inside) {
        ToolButton toolButton = (ToolButton) button;
        if (inside)
            showStatus(toolButton.name());
        else
            showStatus(fSelectedToolButton.name());
    }

    /**
     * Gets the current drawing.
     * @see DrawingEditor
     */
    public Drawing drawing() {
        return fDrawing;
    }

    /**
     * Gets the current tool.
     * @see DrawingEditor
     */
    public Tool tool() {
        return fTool;
    }

    /**
     * Gets the current drawing view.
     * @see DrawingEditor
     */
    public DrawingView view() {
        return fView;
    }

    /**
     * Sets the default tool of the editor.
     * @see DrawingEditor
     */
    public void toolDone() {
        if (fDefaultToolButton != null) {
            setTool(fDefaultToolButton.tool(), fDefaultToolButton.name());
            setSelected(fDefaultToolButton);
        }
    }

    /**
     * Handles a change of the current selection. Updates all
     * menu items that are selection sensitive.
     * @see DrawingEditor
     */
    public void selectionChanged(DrawingView view) {
        MenuBar mb = getMenuBar();
        CommandMenu editMenu = (CommandMenu)mb.getMenu(EDIT_MENU);
        editMenu.checkEnabled();
        CommandMenu alignmentMenu = (CommandMenu)mb.getMenu(ALIGNMENT_MENU);
        alignmentMenu.checkEnabled();
    }

    /**
     * Shows a status message.
     * @see DrawingEditor
     */
    public void showStatus(String string) {
        fStatusLine.setText(string);
    }

    private void setTool(Tool t, String name) {
        if (fTool != null)
            fTool.deactivate();
        fTool = t;
        if (fTool != null) {
            fStatusLine.setText(name);
            fTool.activate();
        }
    }

    private void setSelected(ToolButton button) {
        if (fSelectedToolButton != null)
            fSelectedToolButton.reset();
        fSelectedToolButton = button;
        if (fSelectedToolButton != null)
            fSelectedToolButton.select();
    }

    /**
     * Exits the application. You should never override this method
     */
    public void exit() {
        destroy();
        setVisible(false);      // hide the Frame
        dispose();   // tell windowing system to free resources
		System.exit(0);
    }

    /**
     * Handles additional clean up operations. Override to destroy
     * or release drawing editor resources.
     */
    protected void destroy() {
    }

    /**
     * Resets the drawing to a new empty drawing.
     */
    public void promptNew() {
        initDrawing();
    }

    /**
     * Shows a file dialog and opens a drawing.
     */
    public void promptOpen() {
        FileDialog dialog = new FileDialog(this, "Open File...", FileDialog.LOAD);
        dialog.show();
        String filename = dialog.getFile();
        if (filename != null) {
            filename = stripTrailingAsterisks(filename);
            String dirname = dialog.getDirectory();
            loadDrawing(dirname + filename);
        }
        dialog.dispose();
    }

    /**
     * Shows a file dialog and saves drawing.
     */
    public void promptSaveAs() {
        toolDone();
        String path = getSavePath("Save File...");
        if (path != null) {
            if (!path.endsWith(".draw"))
                path += ".draw";
            saveAsStorableOutput(path);
        }
    }

    /**
     * Shows a file dialog and saves drawing.
     */
    public void promptSaveAsSerialized() {
        toolDone();
        String path = getSavePath("Save File...");
        if (path != null) {
            if (!path.endsWith(".ser"))
                path += ".ser";
            saveAsObjectOutput(path);
        }
    }

    /**
     * Prints the drawing.
     */
    public void print() {
        fTool.deactivate();
        PrintJob printJob = getToolkit().getPrintJob(this, "Print Drawing", null);

        if (printJob != null) {
            Graphics pg = printJob.getGraphics();

            if (pg != null) {
                fView.printAll(pg);
                pg.dispose(); // flush page
            }
            printJob.end();
        }
        fTool.activate();
    }

    private String getSavePath(String title) {
        String path = null;
        FileDialog dialog = new FileDialog(this, title, FileDialog.SAVE);
        dialog.show();
        String filename = dialog.getFile();
        if (filename != null) {
            filename = stripTrailingAsterisks(filename);
            String dirname = dialog.getDirectory();
            path = dirname + filename;
        }
        dialog.dispose();
        return path;
    }

    private String stripTrailingAsterisks(String filename) {
        // workaround for bug on NT
        if (filename.endsWith("*.*"))
            return filename.substring(0, filename.length() - 4);
        else
            return filename;
    }

    private void saveAsStorableOutput(String file) {
        // TBD: should write a MIME header
        try {
            FileOutputStream stream = new FileOutputStream(file);
            StorableOutput output = new StorableOutput(stream);
            output.writeStorable(fDrawing);
            output.close();
        } catch (IOException e) {
            showStatus(e.toString());
        }
    }

    private void saveAsObjectOutput(String file) {
        // TBD: should write a MIME header
        try {
            FileOutputStream stream = new FileOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(stream);
            output.writeObject(fDrawing);
            output.close();
        } catch (IOException e) {
            showStatus(e.toString());
        }
    }

    private void loadDrawing(String file) {
        toolDone();
        String type = guessType(file);
        if (type.equals("storable"))
            readFromStorableInput(file);
        else if (type.equals("serialized"))
            readFromObjectInput(file);
        else
            showStatus("Unknown file type");
    }

    private void readFromStorableInput(String file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            StorableInput input = new StorableInput(stream);
            fDrawing.release();
            fDrawing = (Drawing)input.readStorable();
            fView.setDrawing(fDrawing);
        } catch (IOException e) {
            initDrawing();
            showStatus("Error: " + e);
        }
    }

    private void readFromObjectInput(String file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            ObjectInput input = new ObjectInputStream(stream);
            fDrawing.release();
            fDrawing = (Drawing)input.readObject();
            fView.setDrawing(fDrawing);
        } catch (IOException e) {
            initDrawing();
            showStatus("Error: " + e);
        } catch (ClassNotFoundException e) {
            initDrawing();
            showStatus("Class not found: " + e);
        }
    }

    private String guessType(String file) {
        if (file.endsWith(".draw"))
            return "storable";
        if (file.endsWith(".ser"))
            return "serialized";
        return "unknown";
    }
}
