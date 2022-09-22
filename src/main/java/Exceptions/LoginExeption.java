package Exceptions;


public class LoginExeption extends  Exception{

    public LoginExeption(){
        super();
    }

    public LoginExeption(String message){
        super(message);
    }
}
