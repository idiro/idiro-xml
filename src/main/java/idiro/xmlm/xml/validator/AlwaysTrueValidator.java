package idiro.xmlm.xml.validator;

import idiro.xmlm.xml.XmlValidator;
import idiro.xmlm.xml.XmlWord;

/**
 * Validate returns true
 * @author etienne
 *
 */
public class AlwaysTrueValidator extends XmlValidator{

	@Override
	public String description() {
		return "no check";
	}

	@Override
	public boolean validate(XmlWord w) {
		return true;
	}

}
