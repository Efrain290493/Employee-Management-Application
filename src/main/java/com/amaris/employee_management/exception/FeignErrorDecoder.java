package com.amaris.employee_management.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400: return new BadRequestException("Bad Request");
            case 404: return new NotFoundException("Not Found");
            case 500: return new InternalServerErrorException("Internal Server Error");
            default: return new Exception("Generic error");
        }
    }
}
