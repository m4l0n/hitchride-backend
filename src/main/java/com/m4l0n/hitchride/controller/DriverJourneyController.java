package com.m4l0n.hitchride.controller;


import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.DriverJourneyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/driverJourney", produces = MediaType.APPLICATION_JSON_VALUE)
public class DriverJourneyController {

    private final DriverJourneyService driverJourneyService;

    public DriverJourneyController(DriverJourneyService driverJourneyService) {
        this.driverJourneyService = driverJourneyService;
    }

    @PostMapping("/create")
    public Response postNewDriverJourney(@RequestBody DriverJourneyDTO driverJourneyDTO) {
        try {
            DriverJourneyDTO newDriverJourney = driverJourneyService.createDriverJourney(driverJourneyDTO);

            return ResponseAPI.positiveResponse(newDriverJourney);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/searchRides")
    public Response searchRides(@RequestBody SearchRideCriteriaDTO searchRideCriteria) {
        try {
            CompletableFuture<List<DriverJourneyDTO>> futureJourneys = driverJourneyService.searchRidesFromDriverJourneys(searchRideCriteria);
            List<DriverJourneyDTO> driverJourneys = futureJourneys.get();

            return ResponseAPI.positiveResponse(driverJourneys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Response deleteDriverJourney(@RequestBody DriverJourneyDTO driverJourneyDTO) {
        try {
            DriverJourneyDTO deletedDriverJourney = driverJourneyService.deleteDriverJourney(driverJourneyDTO);

            return ResponseAPI.positiveResponse(deletedDriverJourney);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @GetMapping("/getUserDj")
    public Response getUserDriverJourneys() {
        try {
            List<DriverJourneyDTO> driverJourneys = driverJourneyService.getUserDriverJourneys();

            return ResponseAPI.positiveResponse(driverJourneys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }
}
