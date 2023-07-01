package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class RideMapper implements BaseMapper<Ride, RideDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final DriverJourneyService driverJourneyService;
    private final UserService userService;

    public RideMapper(OriginDestinationMapper originDestinationMapper, DriverJourneyService driverJourneyService, UserService userService) {
        this.originDestinationMapper = originDestinationMapper;
        this.driverJourneyService = driverJourneyService;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public RideDTO mapPojoToDto(Ride pojo) {
        return new RideDTO(
                pojo.getRideId(),
                userService.loadUserByUsername(pojo.getRidePassenger()),
                originDestinationMapper.mapPojoToDto(pojo.getRideOriginDestination()),
                driverJourneyService.getDriverJourneyById(pojo.getRideDriverJourney())
        );
    }

    @Override
    public Ride mapDtoToPojo(RideDTO dto) {
        return new Ride(
                dto.rideId(),
                dto.ridePassenger().getUserId(),
                originDestinationMapper.mapDtoToPojo(dto.rideOriginDestination()),
                dto.rideDriverJourney().djId()
        );
    }

}
