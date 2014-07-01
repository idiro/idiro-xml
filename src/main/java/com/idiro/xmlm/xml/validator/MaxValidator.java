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
