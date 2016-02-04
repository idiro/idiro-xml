/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Xml parser utility
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Xml parser utility IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

package com.idiro.xmlm.xml.validator;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.idiro.xmlm.xml.XmlValidator;
import com.idiro.xmlm.xml.XmlWord;

public class SetValidator extends XmlValidator {

	@Override
	public String description() {
		return "check if all the leaves element of list are unique";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(XmlWord word) {
		boolean ok = true;
		Iterator<String> it = word.getChildren().keySet().iterator();
		while(it.hasNext() && ok){
			String nameField = it.next();
			Object o = word.getChildren().get(nameField);
			if(o instanceof List<?>){
				List<?> l = (List<?>)o;
				int lSize = l.size(),
					sSize = Integer.MAX_VALUE;
				if( lSize > 1){
					o = l.get(0);
					if(o instanceof Boolean){
						sSize =  new HashSet<Boolean>((List<Boolean>)l).size();
					}else if(o instanceof Integer){
						sSize =  new HashSet<Integer>((List<Integer>)l).size();
					}else if(o instanceof Double){
						sSize =  new HashSet<Double>((List<Double>)l).size();
					}else if(o instanceof String){
						sSize =  new HashSet<String>((List<String>)l).size();
					}
					if( sSize < lSize ){
						ok = false;
						logger.error("'"+nameField+"' contains duplicate");
					}
				}
				
			}
		}
		
		return ok;
	}

}
