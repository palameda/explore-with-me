package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "events")
public class Event {

    @Data
    @Embeddable
    public static class Location {
        private Double lat;
        private Double lon;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    private Category category;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "description")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    private User initiator;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "published")
    private LocalDateTime published;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(name = "title")
    private String title;
    @Column(name = "views")
    private Long views;
    @Embedded
    private Location location;
    @Enumerated(EnumType.STRING)
    private StateAction stateAction;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private List<Comment> comments;
}
