package ru.practicum.service.event;

import ru.practicum.api.event.*;
import ru.practicum.model.EventSort;
import ru.practicum.utility.crud.DefaultGetService;
import ru.practicum.utility.crud.event.AdminActionsEventService;
import ru.practicum.utility.crud.user.UserRequestCreateService;
import ru.practicum.utility.crud.user.UserRequestGetAllService;
import ru.practicum.utility.crud.user.UserRequestGetService;
import ru.practicum.utility.crud.user.UserRequestUpdateService;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService extends
        DefaultGetService<Long, EventFullDto>,
        UserRequestCreateService<EventFullDto, Long, NewEventDto>,
        UserRequestGetAllService<EventShortDto, Long>,
        UserRequestGetService<EventFullDto, Long, Long>,
        UserRequestUpdateService<EventFullDto, Long, Long, UpdateEventUserRequest>,
        AdminActionsEventService<EventFullDto, Long, Long, Long, UpdateEventAdminRequest> {

    List<EventShortDto> getPublicEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime start,
                                        LocalDateTime end,
                                        Boolean isAvailable,
                                        EventSort eventSort,
                                        Integer from,
                                        Integer size);
}
