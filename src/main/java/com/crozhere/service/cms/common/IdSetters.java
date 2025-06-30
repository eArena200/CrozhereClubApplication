package com.crozhere.service.cms.common;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.payment.repository.entity.Payment;

public class IdSetters {
    public static final InMemRepository.IdSetter<Payment> PAYMENT_ID_SETTER =
            (payment, id) -> payment.setId(id);

    public static final InMemRepository.IdSetter<User> USER_ID_SETTER =
            (user, id) -> user.setId(id);

    public static final InMemRepository.IdSetter<Booking> BOOKING_ID_SETTER =
            (booking, id) -> booking.setId(id);
}

