package kr.me.seesaw.service;

import kr.me.seesaw.dto.command.CreateEventCommand;
import kr.me.seesaw.dto.command.UpdateEventCommand;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.request.SearchEventsRequest;

import java.io.IOException;
import java.util.List;

public interface EventWebService {

    List<VEventModel> findAll(SearchEventsRequest request);

    VEventModel find(String id);

    VEventModel create(CreateEventCommand command) throws IOException;

    VEventModel update(String id, UpdateEventCommand command) throws IOException;

    void delete(String id);

}
