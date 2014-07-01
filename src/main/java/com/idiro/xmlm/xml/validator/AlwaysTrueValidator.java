package com.idiro.xmlm.xml.validator;

import com.idiro.xmlm.xml.XmlValidator;
import com.idiro.xmlm.xml.XmlWord;

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
