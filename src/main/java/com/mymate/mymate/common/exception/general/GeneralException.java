package com.mymate.mymate.common.exception.general;


import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralException extends RuntimeException {

    private ErrorResponse status;

    public GeneralException(ErrorResponse response) {
        super(response.getCode());
        this.status = response;
    }
}
