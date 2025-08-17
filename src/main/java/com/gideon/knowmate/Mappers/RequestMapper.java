package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.RequestDto;
import com.gideon.knowmate.Entity.Request;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RequestMapper implements Function<Request, RequestDto> {
    @Override
    public RequestDto apply(Request request) {
        return new RequestDto(
                request.getId(),
                request.getMessage()
        );
    }
}
