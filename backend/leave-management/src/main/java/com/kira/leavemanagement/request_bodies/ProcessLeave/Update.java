package com.kira.leavemanagement.request_bodies.ProcessLeave;

import lombok.Data;

@Data
public class Update {
    private String comments;
    private String approval_status; // its either   ACCEPT OR REJECT

}
