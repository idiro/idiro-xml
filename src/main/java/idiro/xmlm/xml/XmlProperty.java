package idiro.xmlm.xml;

import org.apache.log4j.Logger;

/**
 * Property of a xml word.
 * 
 * It associates to each word a comment
 * for describing the role of this word
 * within his parent.
 * 
 * Also it contains a minimum and maximum
 * number of time that the element can
 * occur in its parent (it is include born)
 * 
 * The name of the class associate to this
 * word is also contained here with the
 * validator associate to the word
 * 
 * @author etienne
 *
 */
public class XmlProperty {

	private Logger logger = Logger.getLogger(getClass());
	
	
	/**
	 * Name of the xml java Object (getClass().getCanonicalName)
	 */
	private String canonicalName;
	
	/** 
	 * Interval of value accepted (include)
	 */
	private int min;
	private int max;
	
	/**
	 * Associate a child with a comment
	 */
	private String comment;

	
	/**
	 * Validator associate to the word
	 */
	private XmlValidator validator;
	
	private Object defaultValue;
	
	/**
	 * @param canonicalName
	 * @param min
	 * @param max
	 * @param comment
	 * @param validator
	 */
	public XmlProperty(String canonicalName, int value1, int value2, String comment, XmlValidator validator,
			Object defaultValue) {
		super();
		if(value1 < value2){
			this.min = value1;
			this.max = value2;
		}else{
			this.min = value2;
			this.max = value1;
		}
		if(min < 0){
			logger.warn("The value min : "+min+" cannot be bellow 0, set to 0");
			min = 0;
		}
		if(max <= 0){
			logger.warn("The value max : "+min+" cannot be bellow 1, set to 1");
			max = 1;
		}else if( max >= 1000000){
			//Just to be safe, it is more beautiful for the xml generator also
			max = 1000000;
		}
		
		
		this.comment = comment;
		this.canonicalName = canonicalName;
		this.validator = validator;
		this.defaultValue = defaultValue;
	}
	
	public boolean isIn(int number){
		return min <=number && max >= number;
	}
	
	public boolean isBellow(int number){
		return max >= number;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the canonicalName
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * @return the validator
	 */
	public XmlValidator getValidator() {
		return validator;
	}

	/**
	 * @return the defaultValue
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
}