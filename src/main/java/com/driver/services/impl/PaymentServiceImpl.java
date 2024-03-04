package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.SpotType;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        PaymentMode paymentMode = checkPaymentMode(mode, PaymentMode.class);
        if(paymentMode == null)
            throw new Exception("Payment mode not detected");

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        if(amountSent < (reservation.getSpot().getPricePerHour() * reservation.getNumberOfHours()))
            throw new Exception("Insufficient Amount");

        Payment payment = new Payment(paymentMode);
        payment.setReservation(reservation);

        reservation.setPayment(payment);
        reservationRepository2.save(reservation);

        return payment;
    }

    private static <T extends Enum<T>> T checkPaymentMode(String mode, Class<T> enumType) {
        for (T enumValue : enumType.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(mode)) {
                return enumValue;
            }
        }
        return null;
    }
}
