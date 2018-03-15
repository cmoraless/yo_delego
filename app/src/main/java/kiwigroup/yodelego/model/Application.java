package kiwigroup.yodelego.model;

import java.util.Date;

/**
 * Created by cristian on 1/21/18.
 */

public class Application extends Offer {

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public enum ApplicationStatus {
        REJECTED,
        CANCELED,
        REVISION,
        ACCEPTED;

        public static ApplicationStatus fromInteger(int number) {
            switch(number) {
                case -2:
                    return REJECTED;
                case -1:
                    return CANCELED;
                case 0:
                    return REVISION;
                case 1:
                    return ACCEPTED;
            }
            return null;
        }
    }

    private ApplicationStatus status;

    public Application(){
        super();
    }

    public ApplicationStatus getApplicationStatus() {
        return status;
    }

}
