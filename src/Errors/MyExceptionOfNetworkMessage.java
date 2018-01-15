package Errors;

public class MyExceptionOfNetworkMessage extends Exception {

    private int number;
    private String message;


    @Override
    public String getMessage() {
        return message;
    }

    public int getNumber(){return number;}

    public MyExceptionOfNetworkMessage(String message, int num){

        super(message);
        this.number=num;
        this.message = message;

    }
}
