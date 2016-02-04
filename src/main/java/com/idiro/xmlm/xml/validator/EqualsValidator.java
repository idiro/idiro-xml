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


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;

public class EqualsValidator<T extends Comparable<T>> extends LeafValidator<T> {

	List<T> list = new LinkedList<T>();
	
	public EqualsValidator(List<T> values){
		list = values;
	}
	
	public EqualsValidator(T[] values){
		for(int i= 0; i < values.length;++i)
			list.add(values[i]);
	}
	
	@Override
	public String description() {
		return "be equals to one of the values: "+list;
	}

	@Override
	public boolean validate(XmlLeaf<T> leaf) {
		Iterator<T> it = list.iterator();
		boolean found = false;
		while(it.hasNext() && !found){
			found = it.next().compareTo(leaf.getWordValue()) == 0;
		}
		return found;
	}

}
