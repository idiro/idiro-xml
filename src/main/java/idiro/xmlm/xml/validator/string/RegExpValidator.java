package idiro.xmlm.xml.validator.string;

import idiro.xmlm.xml.LeafValidator;
import idiro.xmlm.xml.XmlLeaf;

public class RegExpValidator extends LeafValidator<String>{
	
	private String regExp = "";

	/**
	 * @param regExp
	 */
	public RegExpValidator(String regExp) {
		super();
		this.regExp = regExp;
	}
	

	@Override
	public boolean validate(XmlLeaf<String> leaf) {
		return leaf.getWordValue().matches(regExp);
	}

	@Override
	public String description() {
		return "has to verify the regular expression "+regExp;
	}

	/**
	 * @return the regExp
	 */
	public String getRegExp() {
		return regExp;
	}

	
	

}
