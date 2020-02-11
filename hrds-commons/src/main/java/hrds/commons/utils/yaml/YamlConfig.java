/*
 * Copyright (c) 2005, Yu Cheung Ho
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hrds.commons.utils.yaml;

import hrds.commons.utils.yaml.wrapper.ObjectWrapper;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import hrds.commons.utils.yaml.util.BiDirectionalMap;
import hrds.commons.utils.yaml.util.DateTimeParser;
import hrds.commons.utils.yaml.exception.YamlException;
import hrds.commons.utils.yaml.wrapper.ArrayWrapper;
import hrds.commons.utils.yaml.wrapper.ClassWrapper;
import hrds.commons.utils.yaml.wrapper.ColorWrapper;
import hrds.commons.utils.yaml.wrapper.DateWrapper;
import hrds.commons.utils.yaml.wrapper.DefaultBeanWrapper;
import hrds.commons.utils.yaml.wrapper.DefaultCollectionWrapper;
import hrds.commons.utils.yaml.wrapper.DefaultMapWrapper;
import hrds.commons.utils.yaml.wrapper.DefaultSimpleTypeWrapper;
import hrds.commons.utils.yaml.wrapper.DimensionWrapper;
import hrds.commons.utils.yaml.wrapper.EnumWrapper;
import hrds.commons.utils.yaml.wrapper.OneArgConstructorTypeWrapper;
import hrds.commons.utils.yaml.wrapper.PointWrapper;
import hrds.commons.utils.yaml.wrapper.SimpleObjectWrapper;
import hrds.commons.utils.yaml.wrapper.WrapperFactory;

/**
 * YamlConfig represents a Jyaml configuration and contains all methods in {@link YamlOperations}
 * and is used as the entry point for Yaml operations when multiple Jyaml configurations are used in
 * the same application. See {@link YamlOperations} for documentation on the Yaml entry point
 * methods.
 */
public class YamlConfig implements Cloneable {

  private static YamlConfig defaultConfig;

  static {
    try {
      defaultConfig = fromResource("jyaml.yml");
    } catch (Exception e) {
      try {
        defaultConfig = fromFile("jyaml.yml");
      } catch (Exception e1) {
        defaultConfig = new YamlConfig();
      }
    }
  }

  public static final String CONSTRUCTOR_SCOPE = "constructor";
  public static final String FIELD_SCOPE = "field";
  public static final String PROPERTY_SCOPE = "property";

  public static final String PRIVATE = "private";
  public static final String DEFAULT = "default";
  public static final String PROTECTED = "protected";
  public static final String PUBLIC = "public";

  public YamlConfig() {
    installDefaultHandlers();
    installDefaultAccessScopes();
  }

  /**
   * The default Jyaml configuration
   *
   * @return the default Jyaml configuration
   */
  public static YamlConfig getDefaultConfig() {
    return defaultConfig == null ? null : (YamlConfig) defaultConfig.clone();
  }

  String indentAmount = "  ";

  boolean minimalOutput = false;

  boolean suppressWarnings = false;

  BiDirectionalMap<String, String> transfers = null;

  private String dateFormat = null;
  private DateFormat dateFormatter = null;

  Map<String, String> decodingAccessScope = new HashMap<String, String>();

  Map<String, String> encodingAccessScope = new HashMap<String, String>();

  Map<String, Object> handlers = new HashMap<String, Object>();

  String encoding = "UTF-8";

  /**
   * Returns the charset (or encoding) to use for both encoding and decoding. The default is UTF-8.
   * See http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html for more info.
   *
   * @return
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * sets the charset (or encoding) to use for both encoding and decoding. See
   * http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html
   *
   * @param charset
   */
  public void setEncoding(String charset) {
    this.encoding = charset;
  }

  /**
   * Returns the indentation amount used for one indentation level.
   *
   * @return the amount of indentation used for one indentation level.
   */
  public String getIndentAmount() {
    return indentAmount;
  }

  /**
   * Sets indentation amount.
   *
   * @param indentAmount must be a string consisting only of spaces.
   */
  public void setIndentAmount(String indentAmount) {
    this.indentAmount = indentAmount;
  }

  /**
   * Returns whether the minimal output option is set.
   *
   * @return whether the minimal output option is set.
   */
  public boolean isMinimalOutput() {
    return minimalOutput;
  }

  /**
   * Sets the minimal output option.
   *
   * @param minimalOutput true for on; false for off.
   */
  public void setMinimalOutput(boolean minimalOutput) {
    this.minimalOutput = minimalOutput;
  }

  /**
   * returns whether the suppress warnings option is on
   *
   * @return whether the suppress warnings option is on
   */
  public boolean isSuppressWarnings() {
    return suppressWarnings;
  }

  /**
   * sets the suppress warnings option
   *
   * @param suppressWarnings true for on; false for off.
   */
  public void setSuppressWarnings(boolean suppressWarnings) {
    this.suppressWarnings = suppressWarnings;
  }

  /**
   * returns the transfer-to-classname mapping for this configuration
   *
   * @return a transfer-classname bi-directional map
   */
  public BiDirectionalMap<String, String> getTransfers() {
    return transfers;
  }

  /**
   * sets the transfer-to-classname mapping for this configuration
   *
   * @param transferDictionary a transfer-classname bi-directional map
   */
  public void setTransfers(BiDirectionalMap<String, String> transferDictionary) {
    this.transfers = transferDictionary;
  }

  public Map<String, Object> getHandlers() {
    return handlers;
  }

  public void setHandlers(Map<String, Object> handlers) {
    this.handlers = handlers;
    // add extra handlers installed by default
    installDefaultHandlers();
  }

  void installDefaultHandlers() {
    install("java.awt.Dimension", DimensionWrapper.class.getName());
    install("java.awt.Point", PointWrapper.class.getName());
    install("java.awt.Color", ColorWrapper.class.getName());
    install("java.lang.Class", ClassWrapper.class.getName());
    install(
        "java.math.BigInteger",
        new OneArgConstructorTypeWrapper(BigInteger.class, String.class.getName()));
    install(
        "java.math.BigDecimal",
        new OneArgConstructorTypeWrapper(BigDecimal.class, String.class.getName()));
    install(
        File.class.getName(), new OneArgConstructorTypeWrapper(File.class, String.class.getName()));
    install(Date.class.getName(), new DateWrapper());
  }

  void installDefaultAccessScopes() {
    decodingAccessScope.put(FIELD_SCOPE, PUBLIC);
    decodingAccessScope.put(PROPERTY_SCOPE, PUBLIC);
    decodingAccessScope.put(CONSTRUCTOR_SCOPE, PUBLIC);
    encodingAccessScope.put(FIELD_SCOPE, PUBLIC);
    encodingAccessScope.put(PROPERTY_SCOPE, PUBLIC);
  }

  void install(String classname, Object wrapperSpec) {
    if (wrapperSpec instanceof ObjectWrapper) ((ObjectWrapper) wrapperSpec).setYamlConfig(this);
    if (!handlers.containsKey(classname)) handlers.put(classname, wrapperSpec);
  }

  String transfer2classname(String transferName) {
    if (transfers != null && transfers.containsKey(transferName))
      return transfers.get(transferName);
    return transferName;
  }

  String classname2transfer(String classname) {
    if (transfers != null && transfers.getReverse().containsKey(classname))
      return transfers.getReverse().get(classname);
    return classname;
  }

  public ObjectWrapper getWrapper(Object obj) {
    ObjectWrapper wrapper = getWrapper(obj.getClass());
    wrapper.setObject(obj);
    return wrapper;
  }

  public ObjectWrapper getWrapper(Class clazz) {
    return getWrapper(ReflectionUtil.className(clazz));
  }

  ObjectWrapper initWrapper(String classname, Class type) {
    Object handler = handlers.get(classname);
    if (handler instanceof String) {
      try {
        return (ObjectWrapper)
            ReflectionUtil.classForName((String) handler)
                .getConstructor(new Class[] {Class.class})
                .newInstance(new Object[] {type});
      } catch (Exception e) {
        throw new YamlException("Error initializing Wrapper " + handler + " for type " + type, e);
      }
    } else if (handler instanceof WrapperFactory) {
      return ((WrapperFactory) handler).makeWrapper();
    } else return null;
  }

  public ObjectWrapper getWrapper(String classname) {
    ObjectWrapper ret;
    Class type = ReflectionUtil.classForName(transfer2classname(classname));
    if (type == null) return null;
    if (handlers != null && handlers.containsKey(classname)) ret = initWrapper(classname, type);
    else {
      if (Map.class.isAssignableFrom(type)) ret = new DefaultMapWrapper(type);
      else if (Collection.class.isAssignableFrom(type)) ret = new DefaultCollectionWrapper(type);
      else if (type.isArray()) ret = new ArrayWrapper(type);
      else if (ReflectionUtil.isSimpleType(type)) return new DefaultSimpleTypeWrapper(type);
      else if (type.isEnum()) ret = new EnumWrapper(type);
      else ret = new DefaultBeanWrapper(type);
    }
    ret.setYamlConfig(this);
    return ret;
  }

  public ObjectWrapper getWrapperSetContent(String classname, String content) {
    SimpleObjectWrapper wrapper;
    Class type = ReflectionUtil.classForName(transfer2classname(classname));
    if (handlers != null && handlers.containsKey(classname)) {
      wrapper = (SimpleObjectWrapper) initWrapper(classname, type);
      wrapper.setObject(Utilities.convertType(content, wrapper.expectedArgType()));
    } else if (type != null && type.isEnum()) {
      wrapper = new EnumWrapper(type);
      wrapper.setObject(Utilities.convertType(content, wrapper.expectedArgType()));
    } else {
      wrapper = new DefaultSimpleTypeWrapper(type);
      wrapper.setObject(Utilities.convertType(content, wrapper.expectedArgType()));
    }
    wrapper.setYamlConfig(this);
    return wrapper;
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }
  }

  public String getDateFormat() {
    return this.dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
    this.dateFormatter = null; // ensure dateFormatter will be rebuilt
  }

  /**
   * Returns a DateFormat object for converting dates to strings.
   *
   * <p>If no date format has been set then returns null.
   *
   * @author Steve Leach
   */
  public DateFormat getDateFormatter() {
    if (dateFormatter == null) {
      if (dateFormat != null) {
        dateFormatter = new DateTimeParser(dateFormat);
      }
    }
    return dateFormatter;
  }

  /**
   * Loads a YamlConfig from a Yaml configuration file
   *
   * @param filename the name of the file to load
   * @return a YamlConfig object
   * @throws FileNotFoundException
   * @throws EOFException
   */
  public static YamlConfig fromFile(String filename) throws FileNotFoundException, EOFException {
    YamlDecoder dec = new YamlDecoder(new FileInputStream(filename), new YamlConfig());
    YamlConfig ret = dec.readObjectOfType(YamlConfig.class);
    dec.close();
    return ret;
  }

  /**
   * Loads a YamlConfig from a resource on the classpath
   *
   * @param filename the name of the resource
   * @return a YamlConfig object
   * @throws EOFException
   */
  public static YamlConfig fromResource(String filename) throws EOFException {
    YamlDecoder dec =
        new YamlDecoder(
            YamlConfig.class.getClassLoader().getResourceAsStream(filename), new YamlConfig());
    YamlConfig ret = dec.readObjectOfType(YamlConfig.class);
    dec.close();
    return ret;
  }

  public void dump(Object obj, File file) throws FileNotFoundException {
    YamlEncoder enc = new YamlEncoder(new FileOutputStream(file), this);
    enc.writeObject(obj);
    enc.close();
  }

  public void dump(Object obj, File file, boolean minimalOutput) throws FileNotFoundException {
    YamlEncoder enc = new YamlEncoder(new FileOutputStream(file), (YamlConfig) this.clone());
    enc.setMinimalOutput(minimalOutput);
    enc.writeObject(obj);
    enc.close();
  }

  public void dumpStream(Iterator iterator, File file, boolean minimalOutput)
      throws FileNotFoundException {
    YamlEncoder enc = new YamlEncoder(new FileOutputStream(file), (YamlConfig) this.clone());
    enc.setMinimalOutput(minimalOutput);
    while (iterator.hasNext()) enc.writeObject(iterator.next());
    enc.close();
  }

  public void dumpStream(Iterator iterator, File file) throws FileNotFoundException {
    YamlEncoder enc = new YamlEncoder(new FileOutputStream(file), this);
    while (iterator.hasNext()) enc.writeObject(iterator.next());
    enc.close();
  }

  public String dump(Object obj) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YamlEncoder enc = new YamlEncoder(out, this);
    enc.writeObject(obj);
    enc.close();
    try {
      return new String(out.toByteArray(), getEncoding());
    } catch (UnsupportedEncodingException e) {
      throw new YamlException("Unsupported encoding " + getEncoding());
    }
  }

  public String dump(Object obj, boolean minimalOutput) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YamlEncoder enc = new YamlEncoder(out, (YamlConfig) this.clone());
    enc.setMinimalOutput(minimalOutput);
    enc.writeObject(obj);
    enc.close();
    try {
      return new String(out.toByteArray(), getEncoding());
    } catch (UnsupportedEncodingException e) {
      throw new YamlException("Unsupported encoding " + getEncoding());
    }
  }

  public String dumpStream(Iterator iterator) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YamlEncoder enc = new YamlEncoder(out, this);
    while (iterator.hasNext()) enc.writeObject(iterator.next());
    enc.close();
    try {
      return new String(out.toByteArray(), getEncoding());
    } catch (UnsupportedEncodingException e) {
      throw new YamlException("Unsupported encoding " + getEncoding());
    }
  }

  public String dumpStream(Iterator iterator, boolean minimalOutput) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YamlEncoder enc = new YamlEncoder(out, (YamlConfig) this.clone());
    enc.setMinimalOutput(minimalOutput);
    while (iterator.hasNext()) enc.writeObject(iterator.next());
    enc.close();
    try {
      return new String(out.toByteArray(), getEncoding());
    } catch (UnsupportedEncodingException e) {
      throw new YamlException("Unsupported encoding " + getEncoding());
    }
  }

  public Map<String, String> getDecodingAccessScope() {
    return decodingAccessScope;
  }

  public void setDecodingAccessScope(Map<String, String> decodingAccessScope) {
    this.decodingAccessScope = decodingAccessScope;
  }

  public Map<String, String> getEncodingAccessScope() {
    return encodingAccessScope;
  }

  public void setEncodingAccessScope(Map<String, String> encodingAccessScope) {
    this.encodingAccessScope = encodingAccessScope;
  }

  public boolean isFieldAccessibleForDecoding(Field field) {
    return ReflectionUtil.isMemberField(field)
        && isWithin(field.getModifiers(), decodingAccessScope.get(FIELD_SCOPE));
  }

  public boolean isFieldAccessibleForEncoding(Field field) {
    return ReflectionUtil.isMemberField(field)
        && isWithin(field.getModifiers(), encodingAccessScope.get(FIELD_SCOPE));
  }

  public boolean isConstructorAccessibleForDecoding(Class clazz) {
    try {
      Constructor constr = clazz.getDeclaredConstructor();
      return isWithin(constr.getModifiers(), decodingAccessScope.get(CONSTRUCTOR_SCOPE));
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isPropertyAccessibleForDecoding(PropertyDescriptor prop) {
    return isWithin(prop.getWriteMethod().getModifiers(), decodingAccessScope.get(PROPERTY_SCOPE));
  }

  public boolean isPropertyAccessibleForEncoding(PropertyDescriptor prop) {
    return isWithin(prop.getReadMethod().getModifiers(), encodingAccessScope.get(PROPERTY_SCOPE));
  }

  boolean isWithin(int modifiers, String scope) {
    boolean pub = Modifier.isPublic(modifiers);
    boolean pri = Modifier.isPrivate(modifiers);
    boolean pro = Modifier.isProtected(modifiers);
    boolean def = !pub & !pri & !pro;
    if (PUBLIC.equals(scope)) return pub;
    else if (PROTECTED.equals(scope)) return pub || pro;
    else if (DEFAULT.equals(scope)) return def || pub || pro;
    else // private
    return true;
  }

  public void dump(Object obj, OutputStream out, boolean minimalOutput) {
    YamlEncoder enc = new YamlEncoder(out, (YamlConfig) this.clone());
    enc.setMinimalOutput(minimalOutput);
    enc.writeObject(obj);
    enc.close();
  }

  public void dump(Object obj, OutputStream out) {
    YamlEncoder enc = new YamlEncoder(out, this);
    enc.writeObject(obj);
    enc.close();
  }
}
