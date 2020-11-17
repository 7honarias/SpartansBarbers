package com.cucutain.spartansbarbers.Interface;

import com.cucutain.spartansbarbers.Models.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String documentId);
    void onBookingInfoLoadFailed(String message);
}
