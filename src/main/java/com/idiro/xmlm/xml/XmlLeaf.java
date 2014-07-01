package com.idiro.xmlm.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses a leaf in the xml.
 * @author etienne
 *
 * @param <T> type of the leaf, can be
 * BOOLEAN
 * INTEGER
 * DOUBLE
 * STRING
 * 
 * When you create a leaf it is advised to specify
 * a validator, to check if what you have parsed
 * correspond to your needs
 */
public class XmlLeaf<T> extends XmlWord{

	private T value;

	@Deprecated
	public XmlLeaf(){}

	public XmlLeaf(String name, LeafValidator<T> validator, T defaultValue){
		super(name,validator);
		setWordDefaultValue(defaultValue);
	}

	public XmlLeaf(String name,T defaultValue){
		super(name);
		setWordDefaultValue(defaultValue);
	}

	@Override
	public String getDescription() {
		StringBuffer builder = new StringBuffer();
		if(getWordDefaultValue() instanceof Boolean ){
			builder.append("BOOLEAN - ");
		}else if(getWordDefaultValue() instanceof Integer ){
			builder.append("INTEGER - ");
		}else if(getWordDefaultValue() instanceof Double ){
			builder.append("DOUBLE - ");
		}else if(getWordDefaultValue() instanceof String ){
			builder.append("STRING - ");
		}else{
			logger.error("Error in value type has to be Boolean, Integer, Double or String");
			builder.append("");
		}
		builder.append("'"+getWordDefaultValue()+"' - ");
		return builder.append(getValidator().description()).toString();
	}

	@Override
	protected boolean initParser() {
		return true;
	}

	@Override
	public boolean checkParsing(){
		return true;
	}

	/**
	 * parses the arguments from a leaf of the xml file
	 *
	 * @param n the xml file leaf
	 * @return true if it has been parsed correctly
	 */
	@SuppressWarnings("unchecked")
	public boolean parse(final Node n){

		boolean ok = true;
		if (!n.getNodeName().equals(getNameXml())) {
			logger.error("Node "+getNameXml()+" expected and not "+n.getNodeName()+".");
			return false;
		}

		//Check if it is a leaf or not, a leaf can be commented
		NodeList children = n.getChildNodes();
		int countText = 0;
		for(int j = 0; j < children.getLength(); ++j){
			if( children.item(j).getNodeName().equals("#text")){
				++countText;
			}
		}
		if(countText > 1){
			ok = false;
			logger.error("In '"+getNameXml()+"', the field count several areas of text probably because of comments");
			logger.error("Please place the comments on either the left or the right border of the field");
		}else{
			logger.debug("Parse a leaf: '"+getNameXml()+"' in "+getClass());
			String valStr = n.getTextContent().trim();
			if(getWordDefaultValue() instanceof Boolean ){
				if(valStr.toLowerCase().equals("true")){
					value = (T) Boolean.TRUE;
				}else{
					value = (T) Boolean.FALSE;
					if(!valStr.toLowerCase().equals("false")){
						ok = false;
						logger.error("In "+getNameXml()+" Boolean value has to be 'true' or 'false'");
					}
				}
			}else if(getWordDefaultValue() instanceof Integer ){
				value = (T) new Integer(Integer.parseInt(valStr));
			}else if(getWordDefaultValue() instanceof Double ){
				value = (T) new Double(Double.parseDouble(valStr));
			}else if(getWordDefaultValue() instanceof String ){
				value = (T) valStr;
			}else{
				logger.error("Error in value type has to be Boolean, Integer, Double or String");
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Generate a help for the word
	 * 
	 * @param space the number of spaces to generate
	 * @return a help
	 */
	@Override
	public String help(int space, int spacePerLevel){
		StringBuffer builder = new StringBuffer();
		builder.append(getSpaces(space)+"<"+getNameXml()+">"+
				"<!-- "+getDescription()+" -->\n"+
				getSpaces(space)+"</"+getNameXml()+">\n\n");

		return builder.toString();
	}


	/**
	 * @return the value
	 */
	@Override
	public T getWordValue() {
		return value;
	}

	/**
	 * @return the defaultValue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getWordDefaultValue() {
		return (T)super.getWordDefaultValue();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}


}
