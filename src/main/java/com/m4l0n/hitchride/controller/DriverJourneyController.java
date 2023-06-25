package com.m4l0n.hitchride.controller;


import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.DriverJourneyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/driverJourney", produces = MediaType.APPLICATION_JSON_VALUE)
public class DriverJourneyController {

    private final DriverJourneyService driverJourneyService;

    public DriverJourneyController(DriverJourneyService driverJourneyService) {
        this.driverJourneyService = driverJourneyService;
    }

    @PostMapping("/createDriverJourney")
    public Response postNewDriverJourney(@RequestBody DriverJourney driverJourney) {

        try {
            DriverJourney newDriverJourney = driverJourneyService.createDriverJourney(driverJourney);

            return ResponseAPI.positiveResponse(newDriverJourney);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }

    }
}
