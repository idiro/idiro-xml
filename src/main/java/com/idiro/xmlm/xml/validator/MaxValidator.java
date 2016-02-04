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

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;

public class MaxValidator<T extends Comparable<T>> extends LeafValidator<T>{
	
	T max;
	
	public MaxValidator(T max){
		this.max = max;
	}

	@Override
	public String description() {
		return "value bellow "+max+" (included)";
	}

	@Override
	public boolean validate(XmlLeaf<T> leaf) {
		return max.compareTo(leaf.getWordValue()) >=0 ;
	}

}
