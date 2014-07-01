package com.idiro.xmlm.xml.validator;

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;

public class MinValidator<T extends Comparable<T>> extends LeafValidator<T>{
	
	T min;
	
	public MinValidator(T min){
		this.min = min;
	}

	@Override
	public String description() {
		return "value above "+min+" (included)";
	}

	@Override
	public boolean validate(XmlLeaf<T> leaf) {
		return min.compareTo(leaf.getWordValue()) <=0 ;
	}

}