/*
 * @(#)DrawApplication.java 5.2
 *
 */

package CH.ifa.draw.application;

import javax.swing.*;
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
        extends JFrame
        implements DrawingEditor, PaletteListener {

    private Drawing              fDrawing;
    private Tool                 fTool;
    private Iconkit              fIconkit;

    private JTextField           fStatusLine;
    private StandardDrawingView  fView;
    private ToolButton           fDefaultToolButton;
    private ToolButton           fSelectedToolButton;

    private String               fApplicationName;
    private String               fDrawingFilename;
    private StorageFormatManager fStorageFormatManager;
    static String                fgUntitled = "untitled";

    // the image resource path
    private static final String fgDrawPath = "/CH/ifa/draw/";
    public static final String IMAGES = fgDrawPath + "images/";

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
        setApplicationName(title);
    }

    /**
     * Factory method which can be overriden by subclasses to
     * create an instance of their type.
     *
     * @return	newly created application
     */
	protected DrawApplication createApplication() {
		return new DrawApplication();
	}
	
    /**
     * Open a new view for this application containing a
     * view of the drawing of the currently activated window.
     */
    public void newView() {
		DrawApplication window = createApplication();
		window.open();
		window.setDrawing(drawing());
		window.setDrawingTitle(getDrawingTitle() + " (View)");
    }

    /**
     * Open a new window for this application containing
     * an new (empty) drawing.
     */
    public void newWindow() {
		DrawApplication window = createApplication();
		window.open();
    }
    
    /**
     * Opens the window and initializes its contents.
     * Clients usually only call but don't override it.
     */
    public void open() {
        fIconkit = new Iconkit(this);
		getContentPane().setLayout(new BorderLayout());
		// Panel in which a JToolBar can be placed using a BoxLayout
        JPanel fullPanel = new JPanel();
        fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.X_AXIS));
        fView = createDrawingView();
        JComponent contents = createContents((StandardDrawingView)view());
        contents.setAlignmentX(LEFT_ALIGNMENT);

        JToolBar tools = createToolPalette();
        createTools(tools);

        JPanel activePanel = new JPanel();
        activePanel.setAlignmentX(LEFT_ALIGNMENT);
	    activePanel.setAlignmentY(TOP_ALIGNMENT);
        activePanel.setLayout(new BorderLayout());
        activePanel.add(tools, BorderLayout.NORTH);
        activePanel.add(contents, BorderLayout.CENTER);

        fullPanel.add(activePanel);

        fStatusLine = createStatusLine();
        getContentPane().add(fullPanel, BorderLayout.CENTER);
        getContentPane().add(fStatusLine, BorderLayout.SOUTH);

		JMenuBar mb = new JMenuBar();
		createMenus(mb);
		setJMenuBar(mb);
		
        initDrawing();
        
        Dimension d = defaultSize();
        if (d.width > mb.getPreferredSize().width) {
    		setSize(d.width, d.height);
		}
		else {
            setSize(mb.getPreferredSize().width, d.height);
		}
        addListeners();
        setVisible(true);
        fStorageFormatManager = createStorageFormatManager();
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

    protected void initDrawing() {
        setDrawing(createDrawing());
        setDrawingTitle(fgUntitled);
        view().setDrawing(drawing());
        toolDone();
    }

    /**
     * Creates the standard menus. Clients override this
     * method to add additional menus.
     */
    protected void createMenus(JMenuBar mb) {
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
    protected JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("New", new MenuShortcut('n').getKey());
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptNew();
		        }
		    }
		);
		menu.add(mi);

		mi = new JMenuItem("Open...", new MenuShortcut('o').getKey());
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptOpen();
		        }
		    }
		);
		menu.add(mi);

		mi = new JMenuItem("Save As...", new MenuShortcut('s').getKey());
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptSaveAs();
		        }
		    }
		);
		menu.add(mi);

		menu.addSeparator();
		mi = new JMenuItem("Print...", new MenuShortcut('p').getKey());
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            print();
		        }
		    }
		);
		menu.add(mi);
		menu.addSeparator();
		mi = new JMenuItem("Exit");
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
    protected JMenu createEditMenu() {
		CommandMenu menu = new CommandMenu("Edit");
		menu.add(new CutCommand("Cut", view()), new MenuShortcut('x'));
		menu.add(new CopyCommand("Copy", view()), new MenuShortcut('c'));
		menu.add(new PasteCommand("Paste", view()), new MenuShortcut('v'));
		menu.addSeparator();
		menu.add(new DuplicateCommand("Duplicate", view()), new MenuShortcut('d'));
		menu.add(new DeleteCommand("Delete", view()));
		menu.addSeparator();
		menu.add(new GroupCommand("Group", view()));
		menu.add(new UngroupCommand("Ungroup", view()));
		menu.addSeparator();
		menu.add(new SendToBackCommand("Send to Back", view()));
		menu.add(new BringToFrontCommand("Bring to Front", view()));
		return menu;
	}

    /**
     * Creates the alignment menu. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createAlignmentMenu() {
		CommandMenu menu = new CommandMenu("Align");
		menu.add(new ToggleGridCommand("Toggle Snap to Grid", view(), new Point(4,4)));
		menu.addSeparator();
		menu.add(new AlignCommand("Lefts", view(), AlignCommand.LEFTS));
		menu.add(new AlignCommand("Centers", view(), AlignCommand.CENTERS));
		menu.add(new AlignCommand("Rights", view(), AlignCommand.RIGHTS));
		menu.addSeparator();
		menu.add(new AlignCommand("Tops", view(), AlignCommand.TOPS));
		menu.add(new AlignCommand("Middles", view(), AlignCommand.MIDDLES));
		menu.add(new AlignCommand("Bottoms", view(), AlignCommand.BOTTOMS));
		return menu;
	}

    /**
     * Creates the debug menu. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createDebugMenu() {
		JMenu menu = new JMenu("Debug");

		JMenuItem mi = new JMenuItem("Simple Update");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            view().setDisplayUpdate(new SimpleUpdateStrategy());
		        }
		    }
		);
		menu.add(mi);

		mi = new JMenuItem("Buffered Update");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            view().setDisplayUpdate(new BufferedUpdateStrategy());
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
    protected JMenu createAttributesMenu() {
        JMenu menu = new JMenu("Attributes");
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
    protected JMenu createColorMenu(String title, String attribute) {
        CommandMenu menu = new CommandMenu(title);
        for (int i=0; i<ColorMap.size(); i++)
            menu.add(
                new ChangeAttributeCommand(
                    ColorMap.name(i),
                    attribute,
                    ColorMap.color(i),
                    view()
                )
            );
        return menu;
    }

    /**
     * Creates the arrows menu.
     */
    protected JMenu createArrowMenu() {
        CommandMenu menu = new CommandMenu("Arrow");
        menu.add(new ChangeAttributeCommand("none",     "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_NONE),  view()));
        menu.add(new ChangeAttributeCommand("at Start", "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_START), view()));
        menu.add(new ChangeAttributeCommand("at End",   "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_END),   view()));
        menu.add(new ChangeAttributeCommand("at Both",  "ArrowMode", new Integer(PolyLineFigure.ARROW_TIP_BOTH),  view()));
        return menu;
    }

    /**
     * Creates the fonts menus. It installs all available fonts
     * supported by the toolkit implementation.
     */
    protected JMenu createFontMenu() {
        CommandMenu menu = new CommandMenu("Font");
        String fonts[] = Toolkit.getDefaultToolkit().getFontList();
        for (int i = 0; i < fonts.length; i++)
            menu.add(new ChangeAttributeCommand(fonts[i], "FontName", fonts[i],  view()));
        return menu;
    }

    /**
     * Creates the font style menu with entries (Plain, Italic, Bold).
     */
    protected JMenu createFontStyleMenu() {
        CommandMenu menu = new CommandMenu("Font Style");
        menu.add(new ChangeAttributeCommand("Plain", "FontStyle", new Integer(Font.PLAIN), view()));
        menu.add(new ChangeAttributeCommand("Italic","FontStyle", new Integer(Font.ITALIC),view()));
        menu.add(new ChangeAttributeCommand("Bold",  "FontStyle", new Integer(Font.BOLD),  view()));
        return menu;
    }

    /**
     * Creates the font size menu.
     */
    protected JMenu createFontSizeMenu() {
        CommandMenu menu = new CommandMenu("Font Size");
        int sizes[] = { 9, 10, 12, 14, 18, 24, 36, 48, 72 };
        for (int i = 0; i < sizes.length; i++) {
            menu.add(
                new ChangeAttributeCommand(
                    Integer.toString(sizes[i]),
                    "FontSize",
                    new Integer(sizes[i]),  view())
                );
        }
        return menu;
    }

	/**
	 * Create a menu which allows the user to select a different look and feel at runtime.
	 */
    public JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Look'n'Feel");

        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        JMenuItem mi = null;

		for (int i = 0; i < lafs.length; i++) {
            mi = new JMenuItem(lafs[i].getName());
            final String lnfClassName = lafs[i].getClassName(); 
            mi.addActionListener(
                new ActionListener() {
	                public void actionPerformed(ActionEvent event) {
                        newLookAndFeel(lnfClassName);
		            }
		        }
		    );
		    menu.add(mi);
		}
        return menu;
    }

    /**
     * Creates the tool palette.
     */
    protected JToolBar createToolPalette() {
        JToolBar palette = new JToolBar();
        palette.setBackground(Color.lightGray);
        // use standard FlowLayout for JToolBar
        // palette.setLayout(new PaletteLayout(2,new Point(2,2)));
        return palette;
    }

    /**
     * Creates the tools. By default only the selection tool is added.
     * Override this method to add additional tools.
     * Call the inherited method to include the selection tool.
     * @param palette the palette where the tools are added.
     */
    protected void createTools(JToolBar palette) {
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
        return new Dimension(800, 800);
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
     * a JScrollPane.
     */
    protected JComponent createContents(StandardDrawingView view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        return sp;
    }

    /**
     * Factory method to create a StorageFormatManager for supported storage formats.
     * Different applications might want to use different storage formats and can return
     * their own format manager by overriding this method.
     */
    public StorageFormatManager createStorageFormatManager() {
        StorageFormatManager storageFormatManager = new StorageFormatManager();
        storageFormatManager.setDefaultStorageFormat(new StandardStorageFormat());
        storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat());
        storageFormatManager.addStorageFormat(new SerializationStorageFormat());
        return storageFormatManager;
    }

	/**
	 * Set the StorageFormatManager. The StorageFormatManager is used when storing and
	 * restoring Drawing from the file system.
	 */
    private void setStorageFormatManager(StorageFormatManager storageFormatManager) {
        fStorageFormatManager = storageFormatManager;
    }

	/**
	 * Return the StorageFormatManager for this application.The StorageFormatManager is
	 * used when storing and restoring Drawing from the file system.
	 */        
    public StorageFormatManager getStorageFormatManager() {
        return fStorageFormatManager;
    }
    
    /**
     * Sets the drawing to be edited.
     */
    public void setDrawing(Drawing drawing) {
        view().setDrawing(drawing);
        fDrawing = drawing;
    }

    /**
     * Gets the default size of the window.
     */
    protected Dimension defaultSize() {
        return new Dimension(600,450);
    }

    /**
     * Creates the status line.
     */
    protected JTextField createStatusLine() {
        JTextField field = new JTextField("No Tool", 40);
        field.setBackground(Color.white);
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
        JMenuBar mb = getJMenuBar();
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
        if (tool() != null)
            tool().deactivate();
        fTool = t;
        if (tool() != null) {
            fStatusLine.setText(name);
            tool().activate();
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
        setVisible(false);      // hide the JFrame
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
    	toolDone();
        JFileChooser openDialog = createOpenFileChooser();
        getStorageFormatManager().registerFileFilters(openDialog);
        if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(openDialog.getFileFilter());
            if (foundFormat != null) {
                loadDrawing(foundFormat, openDialog.getSelectedFile().getAbsolutePath());
            }
            else {
                showStatus("Not a valid file format: " + openDialog.getFileFilter().getDescription());
            }
        }
    }

    /**
     * Shows a file dialog and saves drawing.
     */
    public void promptSaveAs() {
        toolDone();
        JFileChooser saveDialog = createSaveFileChooser();
        getStorageFormatManager().registerFileFilters(saveDialog);

        if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(saveDialog.getFileFilter());
            if (foundFormat != null) {
                saveDrawing(foundFormat, saveDialog.getSelectedFile().getAbsolutePath());
            }
            else {
                showStatus("Not a valid file format: " + saveDialog.getFileFilter().getDescription());
            }
        }
    }

    /**
     * Create a file chooser for the open file dialog. Subclasses may override this
     * method in order to customize the open file dialog.
     */
    protected JFileChooser createOpenFileChooser() {
		JFileChooser openDialog = new JFileChooser();
		openDialog.setDialogTitle("Open File...");
		return openDialog;
    }

    /**
     * Create a file chooser for the save file dialog. Subclasses may override this
     * method in order to customize the save file dialog.
     */	
    protected JFileChooser createSaveFileChooser() {
        JFileChooser saveDialog = new JFileChooser();
        saveDialog.setDialogTitle("Save File...");
        return saveDialog;
    }
	
    /**
     * Prints the drawing.
     */
    public void print() {
        tool().deactivate();
        PrintJob printJob = getToolkit().getPrintJob(this, "Print Drawing", null);

        if (printJob != null) {
            Graphics pg = printJob.getGraphics();

            if (pg != null) {
                ((StandardDrawingView)view()).printAll(pg);
                pg.dispose(); // flush page
            }
            printJob.end();
        }
        tool().activate();
    }

    /**
     * Save a Drawing in a file
     */
    protected void saveDrawing(StorageFormat storeFormat, String file) {
        try {
        	setDrawingTitle(storeFormat.store(file, drawing()));
        }
        catch (IOException e) {
            showStatus(e.toString());
        }
    }

    /**
     * Load a Drawing from a file 
     */
    protected void loadDrawing(StorageFormat restoreFormat, String file) {
        try {
            Drawing restoredDrawing = restoreFormat.restore(file);
            if (restoredDrawing != null) {
                newWindow();
                setDrawing(restoredDrawing);
                setDrawingTitle(file);
            }
            else {
               showStatus("Unknown file type: could not open file '" + file + "'");
            }
        } catch (IOException e) {
            showStatus("Error: " + e);
        }
    }

	/**
	 * Switch to a new Look&Feel
	 */
    private void newLookAndFeel(String landf) {
        try {
            UIManager.setLookAndFeel(landf);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Set the title of the currently selected drawing
     */
    protected void setDrawingTitle(String drawingTitle) {
	    fDrawingFilename = drawingTitle;
	    if (fgUntitled.equals(drawingTitle)) {
	    	setTitle(getApplicationName());
	    }
	    else {
	    	setTitle(getApplicationName() + " - " + drawingTitle);
	    }
    }
    
    /**
     * Return the title of the currently selected drawing
     */
    protected String getDrawingTitle() {
    	return fDrawingFilename;
    }

	/**
	 * Set the name of the application build from this skeleton application
	 */	
	public void setApplicationName(String applicationName) {
		fApplicationName = applicationName;
	}

	/**
	 * Return the name of the application build from this skeleton application
	 */	
	public String getApplicationName() {
		return fApplicationName;
	}
}
