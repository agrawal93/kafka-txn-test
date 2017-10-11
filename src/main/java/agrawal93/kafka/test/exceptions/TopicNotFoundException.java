package agrawal93.kafka.test.exceptions;

public class TopicNotFoundException extends RuntimeException {

    public TopicNotFoundException(String message) {
        super(message);
    }

    public TopicNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public TopicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
