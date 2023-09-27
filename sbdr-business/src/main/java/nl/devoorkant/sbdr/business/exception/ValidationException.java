package nl.devoorkant.sbdr.business.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation Exception
 *
 * @author <a href="mailto:jmarsman@devoorkant.nl">Jeroen Marsman</a>
 */
public class ValidationException extends Exception implements EdoException {
    private static final long serialVersionUID = 1L;
    
	private List<Node> errors = new ArrayList<Node>(0);

    public ValidationException() {
        super();
    }

    public ValidationException(List<Node> errors) {
        super();
        this.errors = errors;
    }

    public void addError(String value) {
        Node n = new Node();
        n.setValue(value);
        errors.add(n);
    }

    public void addError(String key, String value) {
        errors.add(new Node(key, value));
    }

    public List<Node> getErrors() {
        return errors;
    }

    public void setErrors(List<Node> errors) {
        this.errors = errors;
    }

    /**
     * Key Value Node
     */
    public static class Node {
        private String key;
        private String value;

        public Node() {

        }

        public Node(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
    @Override
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	for (Node errorNode: getErrors()) {
    		buf.append(errorNode.getKey()).append(": ").append(errorNode.getValue()).append("\n");
    	}
    	return buf.toString();
    }
}
