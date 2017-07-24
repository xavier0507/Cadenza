package xy.hippocampus.cadenza.model.bean;

import xy.hippocampus.cadenza.model.bean.base.BaseBeanWithSerializable;

/**
 * Created by Xavier Yin on 2017/7/20.
 */

public class ErrorMessage extends BaseBeanWithSerializable {
    private String errorMessage;
    private String errorDetails;
    private int errorCode;
    private Exception toException;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Exception getToException() {
        return toException;
    }

    public void setToException(Exception toException) {
        this.toException = toException;
    }
}
