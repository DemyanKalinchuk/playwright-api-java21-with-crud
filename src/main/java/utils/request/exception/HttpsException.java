package utils.request.exception;

public class HttpsException extends RuntimeException {
    public HttpsException(String message) {
        super(message);
    }
    public int getStatusCode(){
        return Integer.parseInt(getMessage().split("actual status_code = ")[1].split("\nError message:\n")[0]);
    }
    public String getErrorMessage(){
        return getMessage().split("Error message:\n")[1];
    }
}
