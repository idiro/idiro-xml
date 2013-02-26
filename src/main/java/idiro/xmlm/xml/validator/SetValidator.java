package idiro.xmlm.xml.validator;

import idiro.xmlm.xml.XmlValidator;
import idiro.xmlm.xml.XmlWord;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
