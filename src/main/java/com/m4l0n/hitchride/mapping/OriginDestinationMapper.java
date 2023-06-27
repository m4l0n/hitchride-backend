package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.OriginDestinationDTO;
import com.m4l0n.hitchride.pojos.LocationData;
import com.m4l0n.hitchride.pojos.OriginDestination;
import com.m4l0n.hitchride.utility.GoogleMapsApiClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OriginDestinationMapper implements BaseMapper<OriginDestination, OriginDestinationDTO> {

    private final GoogleMapsApiClient googleMapsApiClient;

    public OriginDestinationMapper(GoogleMapsApiClient googleMapsApiClient) {
        this.googleMapsApiClient = googleMapsApiClient;
    }

    @Override
    public OriginDestinationDTO mapPojoToDto(OriginDestination pojo) {
        try {
            Map<String, String> originAddress = googleMapsApiClient.getAddressFromCoordinates(pojo.getOrigin());
            LocationData locationData = new LocationData(originAddress.get("name"), originAddress.get("address"), pojo.getOrigin());
            Map<String, String> destinationAddress = googleMapsApiClient.getAddressFromCoordinates(pojo.getDestination());
            LocationData destinationData = new LocationData(destinationAddress.get("name"), destinationAddress.get("address"), pojo.getDestination());
            return new OriginDestinationDTO(locationData, destinationData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OriginDestination mapDtoToPojo(OriginDestinationDTO dto) {
        return new OriginDestination(dto.origin()
                .getAddressCoordinates(), dto.destination()
                .getAddressCoordinates());
    }

}
