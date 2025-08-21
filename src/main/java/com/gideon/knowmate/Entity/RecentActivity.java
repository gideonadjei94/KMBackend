package com.gideon.knowmate.Entity;


import com.gideon.knowmate.Enum.RecentItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivity {
    private RecentItemType type;
    private String itemId;
    private String title;
    private String count;
    private String creatorName;
}
