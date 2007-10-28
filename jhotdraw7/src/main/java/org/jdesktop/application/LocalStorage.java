
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;


/**
 * Access to per application, per user, local file storage.
 * 
 * @see ApplicationContext#getLocalStorage
 * @see SessionStorage
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class LocalStorage extends AbstractBean {
    private static Logger logger = Logger.getLogger(LocalStorage.class.getName());
    private final ApplicationContext context;    
    private long storageLimit = -1L;
    private LocalIO localIO = null;
    private final File unspecifiedFile = new File("unspecified");
    private File directory = unspecifiedFile;

    protected LocalStorage(ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        this.context = context;
    }

    // FIXME - documentation
    protected final ApplicationContext getContext() {
        return context;
    }

    private void checkFileName(String fileName) {
	if (fileName == null) {
	    throw new IllegalArgumentException("null fileName");
	}
    }

    public InputStream openInputFile(String fileName) throws IOException {
	checkFileName(fileName);
	return getLocalIO().openInputFile(fileName);
    }

    public OutputStream openOutputFile(String fileName) throws IOException {
	checkFileName(fileName);
	return getLocalIO().openOutputFile(fileName);
    }

    public boolean deleteFile(String fileName) throws IOException {
	checkFileName(fileName);
	return getLocalIO().deleteFile(fileName);
    }

    /* If an exception occurs in the XMLEncoder/Decoder, we want
     * to throw an IOException.  The exceptionThrow listener method
     * doesn't throw a checked exception so we just set a flag
     * here and check it when the encode/decode operation finishes
     */
    private static class AbortExceptionListener implements ExceptionListener {
	public Exception exception = null;
	public void exceptionThrown(Exception e) {
	    if (exception == null) { exception = e; }
	}
    }

    public void save(Object bean, final String fileName) throws IOException {
	AbortExceptionListener el = new AbortExceptionListener();
	XMLEncoder e = null;
	/* Buffer the XMLEncoder's output so that decoding errors don't
	 * cause us to trash the current version of the specified file.
	 */
	ByteArrayOutputStream bst = new ByteArrayOutputStream();
	try {
	    e = new XMLEncoder(bst);
	    e.setExceptionListener(el);
	    e.setPersistenceDelegate(Rectangle.class, new RectanglePD());
	    e.writeObject(bean);
	}
	finally {
	    if (e != null) { e.close(); }
	}
	if (el.exception != null) {
	    throw new LSException("save failed \"" + fileName + "\"", el.exception);
	}
	OutputStream ost = null;
	try {
	    ost = openOutputFile(fileName);
	    ost.write(bst.toByteArray());
	}
	finally {
	    if (ost != null) { ost.close(); }
	}
    }

    public Object load(String fileName) throws IOException {
	InputStream ist = null; 
	try {
	    ist = openInputFile(fileName);
	}
	catch(IOException e) {
	    return null;
	}
	AbortExceptionListener el = new AbortExceptionListener();
	XMLDecoder d = null;	
	try {
	    d = new XMLDecoder(ist);
	    d.setExceptionListener(el);
	    Object bean = d.readObject();
	    if (el.exception != null) {
		throw new LSException("load failed \"" + fileName + "\"", el.exception);
	    }
	    return bean;
	}
	finally {
	    if (d != null) { d.close(); }
	}
    }

    private void closeStream(Closeable st, String fileName) throws IOException {
	if (st != null) { 
	    try { st.close(); } 
	    catch (java.io.IOException e) { 
		throw new LSException("close failed \"" + fileName + "\"", e);
	    }
	}
    }

    public long getStorageLimit() {
	return storageLimit;
    }

    public void setStorageLimit(long storageLimit) {
	if (storageLimit < -1L) {
	    throw new IllegalArgumentException("invalid storageLimit");
	}
	long oldValue = this.storageLimit;
	this.storageLimit = storageLimit;
	firePropertyChange("storageLimit", oldValue, this.storageLimit);
    }

    private String getId(String key, String def) {
	ResourceMap appResourceMap = getContext().getResourceMap();
	String id = appResourceMap.getString(key);
	if (id == null) {
	    logger.log(Level.WARNING, "unspecified resource "+key+" using "+def);
	    id = def;
	}
	else if (id.trim().length() == 0) {
	    logger.log(Level.WARNING, "empty resource "+key+" using "+def);	
	    id = def;
	}
	return id;
    }
    private String getApplicationId() { 
	return getId("Application.id", getContext().getApplicationClass().getSimpleName()); 
    }
    private String getVendorId() { 
	return getId("Application.vendorId", "UnknownApplicationVendor"); 
    }

    /* The following enum and method only exist to distinguish 
     * Windows and OSX for the sake of getDirectory().
     */
    private enum OSId { WINDOWS, OSX, UNIX }
    private OSId getOSId() {
	PrivilegedAction<String> doGetOSName = new PrivilegedAction<String>() {
	    public String run() {
		return System.getProperty("os.name");
	    }
	};
	OSId id = OSId.UNIX;
	String osName = AccessController.doPrivileged(doGetOSName);
	if (osName != null) {
	    if (osName.toLowerCase().startsWith("mac os x")) {
		id = OSId.OSX;
	    }
	    else if (osName.contains("Windows")) {
		id = OSId.WINDOWS;
	    }
	}
	return id;
    }

    public File getDirectory() {
	if (directory == unspecifiedFile) {
	    directory = null;
	    String userHome = null;
	    try { 
		userHome = System.getProperty("user.home");   
	    }
	    catch(SecurityException ignore) { 
	    }
	    if (userHome != null) {
		String applicationId = getApplicationId();
		OSId osId = getOSId();
		if (osId == OSId.WINDOWS) {
		    File appDataDir = null;
		    try {
			String appDataEV = System.getenv("APPDATA");
			if ((appDataEV != null) && (appDataEV.length() > 0)) {
			    appDataDir = new File(appDataEV);
			}
		    }
		    catch(SecurityException ignore) { 
		    }			
		    String vendorId = getVendorId();
		    if ((appDataDir != null) && appDataDir.isDirectory()) {
			// ${APPDATA}\{vendorId}\${applicationId}
			String path = vendorId + "\\" + applicationId + "\\";
			directory = new File(appDataDir, path);
		    }
		    else {
			// ${userHome}\Application Data\${vendorId}\${applicationId}
			String path = "Application Data\\" + vendorId + "\\" + applicationId + "\\";
			directory = new File(userHome, path);
		    }
		}
		else if (osId == OSId.OSX) {
		    // ${userHome}/Library/Application Support/${applicationId}
		    String path = "Library/Application Support/"+applicationId+"/";
		    directory = new File(userHome, path);
		}
		else {
		    // ${userHome}/.${applicationId}/
		    String path = "."+applicationId+"/";
		    directory = new File(userHome, path);
		}
	    }
	}
	return directory;
    }

    public void setDirectory(File directory) {
	File oldValue = this.directory;
	this.directory = directory;
	firePropertyChange("directory", oldValue, this.directory);
    }

    /* Papers over the fact that the String,Throwable IOException 
     * constructor was only introduced in Java 6.
     */
    private static class LSException extends IOException {
	public LSException(String s, Throwable e) {
	    super(s);
	    initCause(e);
	}
	public LSException(String s) {
	    super(s);
	}
    }

    /* There are some (old) Java classes that aren't proper beans.  Rectangle
     * is one of these.  When running within the secure sandbox, writing a 
     * Rectangle with XMLEncoder causes a security exception because 
     * DefaultPersistenceDelegate calls Field.setAccessible(true) to gain
     * access to private fields.  This is a workaround for that problem.
     * A bug has been filed, see JDK bug ID 4741757  
     */
    private static class RectanglePD extends DefaultPersistenceDelegate {
	public RectanglePD() {
	    super(new String[]{"x", "y", "width", "height"});
	}
	protected Expression instantiate(Object oldInstance, Encoder out) {
	    Rectangle oldR = (Rectangle)oldInstance;
	    Object[] constructorArgs = new Object[]{
		oldR.x, oldR.y, oldR.width, oldR.height
	    };
	    return new Expression(oldInstance, oldInstance.getClass(), "new", constructorArgs);
	}
    }

    private synchronized LocalIO getLocalIO() {
	if (localIO == null) {
	    localIO = getPersistenceServiceIO();
	    if (localIO == null) {
		localIO = new LocalFileIO();
	    }
	}
	return localIO;
    }

    private abstract class LocalIO {
	public abstract InputStream openInputFile(String fileName) throws IOException;
	public abstract OutputStream openOutputFile(String fileName) throws IOException;
	public abstract boolean deleteFile(String fileName) throws IOException;
    }

    private class LocalFileIO extends LocalIO {
	public InputStream openInputFile(String fileName) throws IOException {
	    File path = new File(getDirectory(), fileName);
	    try {
		return new BufferedInputStream(new FileInputStream(path));
	    }
	    catch (IOException e) {
		throw new LSException("couldn't open input file \"" + fileName + "\"", e);
	    }
	}
	public OutputStream openOutputFile(String fileName) throws IOException {
	    File dir = getDirectory();
	    if (!dir.isDirectory()) {
		if (!dir.mkdirs()) {
		    throw new LSException("couldn't create directory " + dir);
		}
	    }
	    File path = new File(dir, fileName);
	    try {
		return new BufferedOutputStream(new FileOutputStream(path));
	    }
	    catch (IOException e) {
		throw new LSException("couldn't open output file \"" + fileName + "\"", e);
	    }
	}
	public boolean deleteFile(String fileName) throws IOException {
	    File path = new File(getDirectory(), fileName);
	    return path.delete();
	}
    }

    /* Determine if we're a web started application and the
     * JNLP PersistenceService is available without forcing
     * the JNLP API to be class-loaded.  We don't want to 
     * require apps that aren't web started to bundle javaws.jar
     */
    private LocalIO getPersistenceServiceIO() {
	try {
	    Class smClass = Class.forName("javax.jnlp.ServiceManager");
	    Method getServiceNamesMethod = smClass.getMethod("getServiceNames");
	    String[] serviceNames = (String[])getServiceNamesMethod.invoke(null);
	    boolean psFound = false;
	    boolean bsFound = false;
	    for(String serviceName : serviceNames) {
		if (serviceName.equals("javax.jnlp.BasicService")) {
		    bsFound = true;
		}
		else if (serviceName.equals("javax.jnlp.PersistenceService")) {
		    psFound = true;
		}
	    }
	    if (bsFound && psFound) {
		return new PersistenceServiceIO();
	    }
	}
	catch (Exception ignore) {
	    // either the classes or the services can't be found
	}
	return null;
    }

    private class PersistenceServiceIO extends LocalIO {
	private BasicService bs;
	private PersistenceService ps;

	private String initFailedMessage(String s) {
	    return getClass().getName() + " initialization failed: " + s;
	}

	PersistenceServiceIO() {
	    try {
		bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
		ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
	    }
	    catch (UnavailableServiceException e) {
		logger.log(Level.SEVERE, initFailedMessage("ServiceManager.lookup"), e);
		bs = null; ps = null;
	    }
	}

	private void checkBasics(String s) throws IOException {
	    if ((bs == null) || (ps == null)) {
		throw new IOException(initFailedMessage(s));
	    }
	}

	private URL fileNameToURL(String name) throws IOException {
	    try {
		return new URL(bs.getCodeBase(), name);
	    }
	    catch (MalformedURLException e) {
		throw new LSException("invalid filename \"" + name + "\"", e);
	    }
	}

	public InputStream openInputFile(String fileName) throws IOException {
	    checkBasics("openInputFile");
	    URL fileURL = fileNameToURL(fileName);
	    try {
		return new BufferedInputStream(ps.get(fileURL).getInputStream());
	    } 
	    catch(Exception e) {
		throw new LSException("openInputFile \"" + fileName + "\" failed", e);
	    }
	}

	public OutputStream openOutputFile(String fileName) throws IOException {
	    checkBasics("openOutputFile");
	    URL fileURL = fileNameToURL(fileName);
	    try {
		FileContents fc = null;
		try {
		    fc = ps.get(fileURL);  
		}
		catch (FileNotFoundException e) {
		    /* Verify that the max size for new PersistenceService 
		     * files is >= 100K (2^17) before opening one.
		     */
		    long maxSizeRequest = 131072L;
		    long maxSize = ps.create(fileURL, maxSizeRequest);
		    if (maxSize >= maxSizeRequest) {
			fc = ps.get(fileURL);
		    }
		}
		if ((fc != null) && (fc.canWrite())) {
		    return new BufferedOutputStream(fc.getOutputStream(true));
		}
		else {
		    throw new IOException("unable to create FileContents object");
		}
	    } 
	    catch(Exception e) {
		throw new LSException("openOutputFile \"" + fileName + "\" failed", e);
	    }
	}

	public boolean deleteFile(String fileName) throws IOException {
	    checkBasics("deleteFile");
	    URL fileURL = fileNameToURL(fileName);
	    try {
		ps.delete(fileURL);
                return true;
	    } 
	    catch(Exception e) {
		throw new LSException("openInputFile \"" + fileName + "\" failed", e);
	    }
	}
    }
}
