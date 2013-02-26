package idiro.xmlm.xml.validator;

import idiro.xmlm.xml.LeafValidator;
import idiro.xmlm.xml.XmlLeaf;

public class WithinRangeValidator<T extends Comparable<T>> extends LeafValidator<T>{

	T min;
	T max;
	
	public WithinRangeValidator(T value1, T value2){
		
		int res = value1.compareTo(value2);
		if(res < 0){
			min = value1;
			max = value2;
		}else{
			min = value2;
			max = value1;
		}
	}

	@Override
	public String description() {
		return "value between "+min+" and "+max+" (included)";
	}

	@Override
	public boolean validate(XmlLeaf<T> leaf) {
		return min.compareTo(leaf.getWordValue()) <= 0 && max.compareTo(leaf.getWordValue()) >=0 ;
	}
	
}
