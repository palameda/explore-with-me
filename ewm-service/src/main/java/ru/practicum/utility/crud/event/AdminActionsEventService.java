package ru.practicum.utility.crud.event;

import ru.practicum.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminActionsEventService<D, ID, UID, CID, R> {
    D confirmEventByAdmin(ID eventId, R request);

    List<D> getAllEventsForAdmins(List<UID> usersIds,
                                  List<State> states,
                                  List<CID> categoryIds,
                                  LocalDateTime start,
                                  LocalDateTime end,
                                  Integer from,
                                  Integer size);
}
