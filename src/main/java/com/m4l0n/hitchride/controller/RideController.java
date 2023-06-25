package com.m4l0n.hitchride.controller;

import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.Ride;
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
            List<Ride> rides = rideService.getRecentRides();

            return ResponseAPI.positiveResponse(rides);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/acceptRide")
    public Response acceptRide(@RequestBody Ride ride) {
        try {
            Ride newRide = rideService.acceptRide(ride);

            if (newRide == null) {
                throw new HitchrideException("Ride not accepted");
            }

            return ResponseAPI.positiveResponse(newRide);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

}
