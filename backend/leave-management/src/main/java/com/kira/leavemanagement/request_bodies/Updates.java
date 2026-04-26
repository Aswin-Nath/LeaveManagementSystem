package com.kira.leavemanagement.request_bodies;

import lombok.Data;

public class Updates {
    @Data
    public static class UpdateEmployeeName{
        private String newName;
    }
}
