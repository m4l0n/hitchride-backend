package com.m4l0n.hitchride.controller;

import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.RideService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/rides", produces = MediaType.APPLICATION_JSON_VALUE)
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Get recent rides for a passenger (rides they've booked)
     * Limited to 5
     * @userType passenger
     */
    @GetMapping("/getRecentRides")
    public Response getRecentRides() {
        try {
            List<RideDTO> rides = rideService.getRecentRides();

            return ResponseAPI.positiveResponse(rides);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    /**
     * Accept a ride, make a booking
     * @userType passenger
     * @param rideDTO ride to accept
     */
    @PostMapping("/acceptRide")
    public Response acceptRide(@RequestBody RideDTO rideDTO) {
        try {
            RideDTO newRide = rideService.acceptRide(rideDTO);

            if (newRide == null) {
                throw new HitchrideException("Ride not accepted");
            }

            return ResponseAPI.positiveResponse(newRide);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    /**
     * Get recent drives for a driver (rides they've created)
     * Limited to 5
     * @userType driver
     */
    @GetMapping("/getRecentDrives")
    public Response getRecentDrives() {
        try {
            List<RideDTO> rides = rideService.getRecentDrives();

            return ResponseAPI.positiveResponse(rides);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    /**
     * Get upcoming rides
     * @userType driver
     */
    @GetMapping("/getUpcomingRides")
    public Response getUpcomingRides() {
        try {
            List<RideDTO> rides = rideService.getUpcomingRides();

            return ResponseAPI.positiveResponse(rides);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

    /**
     * Cancel a ride
     * @userType passenger
     * @param rideDTO ride to cancel
     */
    @PostMapping("/cancelRide")
    public Response cancelRide(@RequestBody RideDTO rideDTO) {
        try {
            Boolean cancelledRide = rideService.cancelRide(rideDTO);

            if (!cancelledRide) {
                throw new HitchrideException("Something went wrong. Ride not cancelled. Please try again.");
            }

            return ResponseAPI.emptyPositiveResponse();
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @GetMapping("/getRideByDriverJourney")
    public Response getRideByDriverJourney(@RequestParam String driverJourneyId) {
        try {
            RideDTO ride = rideService.getRideByDriverJourney(driverJourneyId);
            if (ride == null) {
                return ResponseAPI.positiveResponse(null);
            }
            return ResponseAPI.positiveResponse(ride);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

}
