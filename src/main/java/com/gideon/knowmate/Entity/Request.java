package com.gideon.knowmate.Entity;

import com.gideon.knowmate.Enum.RequestStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "requests")
public class Request {
    @Id
    private String id;
    @DBRef
    private User sender;
    @DBRef
    private User receiver;
    private String message;
    private String challengeId;
    private RequestStatus status;
    @CreatedDate
    private LocalDateTime sentAt;

}
