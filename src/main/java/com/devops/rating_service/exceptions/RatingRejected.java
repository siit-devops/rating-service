package com.devops.rating_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RatingRejected extends RuntimeException{

    public RatingRejected(String msg) {
        super(msg);
    }

}
