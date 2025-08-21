package com.gideon.knowmate.Entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recent")
public class Recents {

    @Id
    private String id;

    private String userId;

    private List<RecentActivity> activities = new ArrayList<>();
}
