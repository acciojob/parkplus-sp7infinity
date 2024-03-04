package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot newParkingLot = new ParkingLot(name, address);
        return parkingLotRepository1.save(newParkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        SpotType spotType;

        if(numberOfWheels <= 2) spotType = SpotType.TWO_WHEELER;
        else if(numberOfWheels <= 4) spotType = SpotType.FOUR_WHEELER;
        else spotType = SpotType.OTHERS;

        Spot newSpot = new Spot();
        newSpot.setSpotType(spotType);
        newSpot.setPricePerHour(pricePerHour);
        newSpot.setParkingLot(parkingLot);

        parkingLot.getSpotList().add(newSpot);
        parkingLotRepository1.save(parkingLot);

        return newSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        Optional<Spot> optionalSpot = spotRepository1.findById(spotId);
        if(optionalSpot.isEmpty())
            return;
        Spot spot = optionalSpot.get();
        ParkingLot parkingLot = spot.getParkingLot();
        parkingLot.removeSpotById(spotId);
        parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        Spot updatedSpot = parkingLot.getSpotList().stream()
                .filter(spot -> (spot.getId() == spotId))
                .findFirst()
                .orElse(null);
        updatedSpot.setPricePerHour(pricePerHour);
        spotRepository1.save(updatedSpot);

        return updatedSpot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
