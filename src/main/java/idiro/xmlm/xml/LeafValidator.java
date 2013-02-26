package idiro.xmlm.xml;

/**
 * Validate a XmlLeaf after parsing it
 * 
 * @author etienne
 *
 * @param <T>
 */
public abstract class LeafValidator<T> extends XmlValidator{

	@SuppressWarnings("unchecked")
	public boolean validate(XmlWord word){
		return validate((XmlLeaf<T>)word);
	}
	
	public abstract boolean validate(XmlLeaf<T> leaf);
}
