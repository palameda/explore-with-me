package ru.practicum.service;

import ru.practicum.api.event.*;
import ru.practicum.utility.crud.DefaultGetService;
import ru.practicum.utility.crud.event.AdminActionsEventService;
import ru.practicum.utility.crud.user.UserRequestCreateService;
import ru.practicum.utility.crud.user.UserRequestGetService;
import ru.practicum.utility.crud.user.UserRequestUpdateService;

public interface EventService extends
        DefaultGetService<Long, EventFullDto>,
        UserRequestCreateService<EventFullDto, Long, NewEventDto>,
        UserRequestGetService<EventShortDto, Long, Long>,
        UserRequestUpdateService<EventFullDto, Long, Long, UpdateEventUserRequest>,
        AdminActionsEventService<EventFullDto, Long, Long, Long, UpdateEventAdminRequest> {
}
