package Errors;

public class MyExceptionOfCommandPackage extends Exception {

    private int number;
    public int getNumber(){return number;}

    public MyExceptionOfCommandPackage(String message, int num){

        super(message);
        number=num;
    }
}
