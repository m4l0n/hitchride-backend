package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: RideMapper.java
// Description: Mapper for Ride DTO and POJO
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class RideMapper implements BaseMapper<Ride, RideDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final DriverJourneyMapper driverJourneyMapper;
    private final DriverJourneyService driverJourneyService;
    private final UserService userService;

    public RideMapper(OriginDestinationMapper originDestinationMapper, DriverJourneyMapper driverJourneyMapper, DriverJourneyService driverJourneyService, UserService userService) {
        this.originDestinationMapper = originDestinationMapper;
        this.driverJourneyMapper = driverJourneyMapper;
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
                driverJourneyMapper.mapPojoToDto(driverJourneyService.getDriverJourneyById(pojo.getRideDriverJourney()))
        );
    }

    @Override
    public Ride mapDtoToPojo(RideDTO dto) {
        if (dto.rideDriverJourney() == null) {
            return new Ride(
                    dto.rideId(),
                    dto.ridePassenger()
                            .getUserId(),
                    originDestinationMapper.mapDtoToPojo(dto.rideOriginDestination()),
                    null
            );
        }
        return new Ride(
                dto.rideId(),
                dto.ridePassenger()
                        .getUserId(),
                originDestinationMapper.mapDtoToPojo(dto.rideOriginDestination()),
                dto.rideDriverJourney()
                        .djId()
        );
    }

}
