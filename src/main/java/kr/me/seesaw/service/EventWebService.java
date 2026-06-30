package kr.me.seesaw.service;

import kr.me.seesaw.dto.request.CreateEventRequest;
import kr.me.seesaw.dto.request.UpdateEventRequest;
import kr.me.seesaw.dto.response.VEventResponse;
import kr.me.seesaw.dto.request.SearchEventsRequest;

import java.io.IOException;
import java.util.List;

public interface EventWebService {

    List<VEventResponse> findAll(SearchEventsRequest request);

    VEventResponse find(String id);

    VEventResponse create(CreateEventRequest command) throws IOException;

    VEventResponse update(String id, UpdateEventRequest command) throws IOException;

    void delete(String id);

}
