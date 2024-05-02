/*
 * @(#)JavaPrimitivesDOMFactory.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.xml;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * {@code JavaPrimitivesDOMFactory} can be used to serialize Java primitive objects and {@link
 * DOMStorable} objects.
 *
 * <p>The following Java primitive types are supported. Object wrappers are automatically unwrapped
 * into their primitive types.
 *
 * <ul>
 *   <li>null
 *   <li>boolean
 *   <li>byte
 *   <li>short
 *   <li>char
 *   <li>int
 *   <li>long
 *   <li>float
 *   <li>double
 *   <li>string
 *   <li>enum
 *   <li>color (will be removed in a future revision of this class!)
 *   <li>font (will be removed in a future revision of this class!)
 * </ul>
 *
 * Arrays of primitive types are supported, by appending the word "Array" to a primitive type name.
 *
 * <p>You can add support for additional primitive types by overriding the methods {@code read} and
 * {@code write}.
 *
 * <p>In addition to the primitive types, this factory can store and read {@link DOMStorable}
 * objects. No mapping for {@link DOMStorable} class names is performed. For example, if a {@link
 * DOMStorable} object has the class name {@code com.example.MyClass}, then the DOM element has the
 * same name, that is: {@code &lt;com.example.MyClass&gt;}.
 *
 * <p>Since no mapping between DOM element names and {@link DOMStorable} class names is performed,
 * DOM's generated with JavaPrimitivesDOMFactory are not suited for long-term storage of objects.
 * This is because a DOM element can not be read back into an object, if the class name of the
 * object has changed.
 *
 * <p>You can implement a mapping by overriding the methods {@code getName}, {@code create}, {@code
 * getEnumName} and {@code getEnumValue}.
 */
public class JavaPrimitivesDOMFactory implements DOMFactory {

  private String escape(String name) {
    // Escape dollar characters by two full-stop characters
    name = name.replaceAll("\\$", "..");
    return name;
  }

  private String unescape(String name) {
    // Unescape dollar characters from two full-stop characters
    name = name.replaceAll("\\.\\.", Matcher.quoteReplacement("$"));
    return name;
  }

  @Override
  public String getName(Object o) {
    if (o == null) {
      return "null";
    } else if (o instanceof Boolean) {
      return "boolean";
    } else if (o instanceof Byte) {
      return "byte";
    } else if (o instanceof Character) {
      return "char";
    } else if (o instanceof Short) {
      return "short";
    } else if (o instanceof Integer) {
      return "int";
    } else if (o instanceof Long) {
      return "long";
    } else if (o instanceof Float) {
      return "float";
    } else if (o instanceof Double) {
      return "double";
    } else if (o instanceof Color) {
      return "color";
    } else if (o instanceof Font) {
      return "font";
    } else if (o instanceof byte[]) {
      return "byteArray";
    } else if (o instanceof char[]) {
      return "charArray";
    } else if (o instanceof short[]) {
      return "shortArray";
    } else if (o instanceof int[]) {
      return "intArray";
    } else if (o instanceof long[]) {
      return "longArray";
    } else if (o instanceof float[]) {
      return "floatArray";
    } else if (o instanceof double[]) {
      return "doubleArray";
    } else if (o instanceof String) {
      return "string";
    } else if (o instanceof Enum) {
      return "enum";
    } else if (o instanceof Color) {
      return "color";
    } else if (o instanceof Font) {
      return "font";
    }
    return escape(o.getClass().getName());
  }

  @Override
  public Object createPrototype(String tagName) {
    String name = unescape(tagName);
    try {
      return Class.forName(name).getConstructor().newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException("unable to instatiate instance from class " + name, ex);
    }
  }

  protected String getEnumName(Enum<?> o) {
    return escape(o.getClass().getName());
  }

  protected String getEnumValue(Enum<?> o) {
    return o.name();
  }

  @SuppressWarnings("unchecked")
  protected <E extends Enum<E>> Enum<E> createEnum(String name, String value) {
    name = unescape(name);
    Class<E> enumClass;
    try {
      enumClass = (Class<E>) Class.forName(name);
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException("Class not found for Enum with name:" + name);
    }
    if (enumClass == null) {
      throw new IllegalArgumentException("Enum name not known to factory:" + name);
    }
    return Enum.valueOf(enumClass, value);
  }

  @Override
  public void write(DOMOutput out, Object o) throws IOException {
    if (o == null) {
      // nothing to do
    } else if (o instanceof DOMStorable) {
      // ((DOMStorable) o).write(out);
      LOG.warning("direct call of DOMOutput from JavaPrimitivesDOMFactory removed");
    } else if (o instanceof String) {
      out.addText((String) o);
    } else if (o instanceof Integer) {
      out.addText(o.toString());
    } else if (o instanceof Long) {
      out.addText(o.toString());
    } else if (o instanceof Double) {
      out.addText(o.toString());
    } else if (o instanceof Float) {
      out.addText(o.toString());
    } else if (o instanceof Boolean) {
      out.addText(o.toString());
    } else if (o instanceof Color) {
      Color c = (Color) o;
      out.addAttribute("rgba", "#" + Integer.toHexString(c.getRGB()));
    } else if (o instanceof byte[]) {
      byte[] a = (byte[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("byte");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof boolean[]) {
      boolean[] a = (boolean[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("boolean");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof char[]) {
      char[] a = (char[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("char");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof short[]) {
      short[] a = (short[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("short");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof int[]) {
      int[] a = (int[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("int");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof long[]) {
      long[] a = (long[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("long");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof float[]) {
      float[] a = (float[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("float");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof double[]) {
      double[] a = (double[]) o;
      for (int i = 0; i < a.length; i++) {
        out.openElement("double");
        write(out, a[i]);
        out.closeElement();
      }
    } else if (o instanceof Font) {
      Font f = (Font) o;
      out.addAttribute("name", f.getName());
      out.addAttribute("style", f.getStyle());
      out.addAttribute("size", f.getSize());
    } else if (o instanceof Enum) {
      Enum<?> e = (Enum<?>) o;
      out.addAttribute("type", getEnumName(e));
      out.addText(getEnumValue(e));
    } else {
      throw new IllegalArgumentException("Unsupported object type:" + o);
    }
  }

  private static final Logger LOG = Logger.getLogger(JavaPrimitivesDOMFactory.class.getName());

  @Override
  public Object read(DOMInput in) throws IOException {
    Object o;
    String tagName = in.getTagName();
    if ("null".equals(tagName)) {
      o = null;
    } else if ("boolean".equals(tagName)) {
      o = Boolean.valueOf(in.getText());
    } else if ("byte".equals(tagName)) {
      o = Byte.decode(in.getText());
    } else if ("short".equals(tagName)) {
      o = Short.decode(in.getText());
    } else if ("int".equals(tagName)) {
      o = Integer.decode(in.getText());
    } else if ("long".equals(tagName)) {
      o = Long.decode(in.getText());
    } else if ("float".equals(tagName)) {
      o = Float.parseFloat(in.getText());
    } else if ("double".equals(tagName)) {
      o = Double.parseDouble(in.getText());
    } else if ("string".equals(tagName)) {
      o = in.getText();
    } else if ("enum".equals(tagName)) {
      o = createEnum(in.getAttribute("type", (String) null), in.getText());
    } else if ("color".equals(tagName)) {
      o = new Color(in.getAttribute("rgba", 0xff));
    } else if ("font".equals(tagName)) {
      o = new Font(
          in.getAttribute("name", "Dialog"),
          in.getAttribute("style", 0),
          in.getAttribute("size", 0));
    } else if ("byteArray".equals(tagName)) {
      byte[] a = new byte[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Byte) in.readObject(i)).byteValue();
      }
      o = a;
    } else if ("shortArray".equals(tagName)) {
      short[] a = new short[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Short) in.readObject(i)).shortValue();
      }
      o = a;
    } else if ("intArray".equals(tagName)) {
      int[] a = new int[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Integer) in.readObject(i)).intValue();
      }
      o = a;
    } else if ("longArray".equals(tagName)) {
      long[] a = new long[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Long) in.readObject(i)).longValue();
      }
      o = a;
    } else if ("floatArray".equals(tagName)) {
      float[] a = new float[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Float) in.readObject(i)).floatValue();
      }
      o = a;
    } else if ("doubleArray".equals(tagName)) {
      double[] a = new double[in.getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = ((Double) in.readObject(i)).doubleValue();
      }
      o = a;
    } else {
      throw new IllegalArgumentException("unkown tagname " + tagName + " --> is not registered");
    }
    return o;
  }
}
