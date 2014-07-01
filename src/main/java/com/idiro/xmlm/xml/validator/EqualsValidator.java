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
