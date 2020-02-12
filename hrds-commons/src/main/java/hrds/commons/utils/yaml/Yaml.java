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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Iterator;

public class Yaml {
	
    public static YamlConfig config = YamlConfig.getDefaultConfig();

    public static void dump(Object obj, File file) throws FileNotFoundException{
    	config.dump(obj, file);
    }

    public static void dump(Object obj, File file, boolean minimalOutput) throws FileNotFoundException{
    	config.dump(obj, file, minimalOutput);
    }
    
    public static void dumpStream(Iterator iterator, File file, boolean minimalOutput) throws FileNotFoundException{
    	config.dumpStream(iterator, file, minimalOutput);
    }
    
    public static void dumpStream(Iterator iterator, File file) throws FileNotFoundException{
    	config.dumpStream(iterator, file);
    }

    public static String dump(Object obj){
    	return config.dump(obj);
    }
    
    public static String dump(Object obj, boolean minimalOutput){
    	return config.dump(obj, minimalOutput);
    }
    	
    public static String dumpStream(Iterator iterator){
    	return config.dumpStream(iterator);
    }
    
    public static String dumpStream(Iterator iterator, boolean minimalOutput){
    	return config.dumpStream(iterator, minimalOutput);
    }
    
    public static void dump(Object obj, OutputStream out) {
        config.dump(obj, out);
    }

    public static void dump(Object obj, OutputStream out, boolean minimalOutput) {
        config.dump(obj, out, minimalOutput);
    }
}
