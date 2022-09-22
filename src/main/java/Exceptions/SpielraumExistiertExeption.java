package Exceptions;



public class SpielraumExistiertExeption extends Exception{

    public SpielraumExistiertExeption(){
        super();
    }

    public SpielraumExistiertExeption(String message){
        super(message);
    }
}
