package com.bookspot.batch.data.library;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Library {
    private LibraryCode code;
    private String address;
    private String tel;
    private double latitude;
    private double longitude;
    private String homePage;
    private String closedInfo;
    private String operatingInfo;
}
