package planning.busdriver.exception;

/**
 * Created by wayne on 7/16/16.
 */
public class MissionImpossibleException extends RuntimeException {
    public MissionImpossibleException(String message){
        super(message);
    }

    public MissionImpossibleException(){
        this(null);
    }
}
