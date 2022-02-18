package xyz.wordtr41n.api.exception;

public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException() { }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }    
}