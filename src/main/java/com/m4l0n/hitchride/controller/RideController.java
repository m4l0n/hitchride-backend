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

    @GetMapping("/getUpcomingRides")
    public Response getUpcomingRides() {
        try {
            List<RideDTO> rides = rideService.getUpcomingRides();

            return ResponseAPI.positiveResponse(rides);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

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

}
