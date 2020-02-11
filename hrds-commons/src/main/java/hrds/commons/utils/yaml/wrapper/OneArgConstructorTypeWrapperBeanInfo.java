/*
 * Copyright (c) 2007, Yu Cheung Ho
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
package hrds.commons.utils.yaml.wrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class OneArgConstructorTypeWrapperBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
          PropertyDescriptor typePD =
            new PropertyDescriptor("type", OneArgConstructorTypeWrapper.class);
          PropertyDescriptor argTypePD =
              new PropertyDescriptor("argType", OneArgConstructorTypeWrapper.class);
          PropertyDescriptor rv[] = {typePD, argTypePD};
          return rv;
        } catch (IntrospectionException e) {
          throw new Error(e.toString());
        }
      }
    
}
