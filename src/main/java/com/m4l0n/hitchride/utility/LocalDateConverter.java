package com.m4l0n.hitchride.utility;

import com.google.cloud.Timestamp;

import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateConverter {
    public static Timestamp toFirestoreTimestamp(LocalDate localDate) {
        // Convert LocalDate to java.util.Date
        java.util.Date date = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Convert java.util.Date to Firestore Timestamp
        return Timestamp.of(date);
    }

    public static LocalDate fromFirestoreTimestamp(Timestamp timestamp) {
        // Convert Firestore Timestamp to java.util.Date
        java.util.Date date = timestamp.toDate();

        // Convert java.util.Date to LocalDate
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}