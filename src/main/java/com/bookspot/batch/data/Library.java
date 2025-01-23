package com.bookspot.batch.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Library {
    private String libraryCode;
    private String name;
    private String address;
    private String tel;
    private double latitude;
    private double longitude;
    private String homePage;
    private String closedInfo;
    private String operatingInfo;
}
