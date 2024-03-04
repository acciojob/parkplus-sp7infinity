package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if(optionalParkingLot.isEmpty())
            throw new Exception("Cannot make reservation");

        Optional<User> optionalUser = userRepository3.findById(userId);
        if(optionalUser.isEmpty())
            throw new Exception("Cannot make reservation");

        ParkingLot parkingLot = optionalParkingLot.get();
        User user = optionalUser.get();

        Spot availableSpot = parkingLot.getSpotList().stream()
                .filter(spot -> (!spot.getOccupied() && isCorrectSpotType(spot.getSpotType(), numberOfWheels)))
                .min(Comparator.comparingInt(Spot::getPricePerHour))
                .orElse(null);
        if(availableSpot == null)
            throw new Exception("Cannot make reservation");

        Reservation reservation = new Reservation(timeInHours);
        reservation.setSpot(availableSpot);
        reservation.setUser(user);
        //reservationRepository3.save(reservation);

        availableSpot.setOccupied(true);
        availableSpot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);
        spotRepository3.save(availableSpot);
        userRepository3.save(user);

        return reservation;
    }

    private static boolean isCorrectSpotType(SpotType spotType, Integer numberOfWheels) {
        switch (spotType) {
            case TWO_WHEELER:
                return numberOfWheels <= 2;
            case FOUR_WHEELER:
                return numberOfWheels <= 4;
            case OTHERS:
                return true;
            default:
                return false;
        }
    }
}
