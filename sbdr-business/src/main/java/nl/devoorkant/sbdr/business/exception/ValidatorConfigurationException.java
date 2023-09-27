package nl.devoorkant.sbdr.business.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidatorConfigurationException
 * <p/>
 * This exception is used in case the Drools validator configuration could not be loaded. It contains all messages that
 * where thrown during initializing the configuration files.
 *
 * @author <a href="mailto:jmarsman@devoorkant.nl">Jeroen Marsman</a>
 */
public class ValidatorConfigurationException extends Exception implements EdoException {

    List<String> messages = new ArrayList<String>();

    public ValidatorConfigurationException() {
        super();
    }

    public ValidatorConfigurationException(List<String> messages) {
        super();
        this.messages = messages;
    }

    public ValidatorConfigurationException(String message, Throwable t) {
        super(message, t);
    }

    public ValidatorConfigurationException(Throwable t) {
        super(t);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (String message : messages) {
            sb.append(message).append("\r\n");
        }

        return sb.toString();
    }
}
