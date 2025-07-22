package com.gideon.knowmate.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @DBRef
    private User sender;
    @DBRef
    private User receiver;
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;
}
