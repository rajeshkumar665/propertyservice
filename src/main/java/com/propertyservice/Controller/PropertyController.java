package com.propertyservice.Controller;

import com.propertyservice.Dto.APIResponse;
import com.propertyservice.Dto.PropertyDto;
import com.propertyservice.Entity.Property;
import com.propertyservice.Entity.RoomAvailability;
import com.propertyservice.Entity.Rooms;
import com.propertyservice.Repository.RoomAvailabilityRepository;
import com.propertyservice.Service.PropertyService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @PostMapping(
            value = "/add-property",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,  // Ensures the endpoint accepts multipart/form-data
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse> addProperty(
            @RequestParam ("property") String propertyJson,
            @RequestParam ("files")MultipartFile[] files){

        // Log the multipart parts
        logger.info("Property JSON: " + propertyJson);
        logger.info("Number of files uploaded: " + (files != null ? files.length : 0));

        ObjectMapper objectMapper = new ObjectMapper();
        PropertyDto dto = null;
        try {
            dto = objectMapper.readValue(propertyJson,PropertyDto.class);

        } catch (JsonParseException e) {
            logger.error("Error parsing property JSON", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

       Property property = propertyService.addProperty(dto,files);

        APIResponse<Property> response = new APIResponse<>();
        response.setMessage("Property added");
        response.setStatus(201);
        response.setData(property);


        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/search-property")
    public APIResponse searchProperty(
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date
            ){
       APIResponse response = propertyService.searchProperty(name,date);
       return response;

    }
    @GetMapping("/property-id")
    public APIResponse<PropertyDto> getPropertyById(@RequestParam long id){
        APIResponse<PropertyDto> response = propertyService.findPropertyById(id);
        return response;
    }
    @GetMapping("/room-available-room-id")
    public APIResponse<List<RoomAvailability>> getTotalRoomsAvailable(@RequestParam long id){
        List<RoomAvailability> totalRooms = propertyService.getTotalRoomsAvailable(id);

        APIResponse<List<RoomAvailability>> response = new APIResponse<>();
        response.setMessage("Total rooms");
        response.setStatus(200);
        response.setData(totalRooms);
        return response;
    }
    @GetMapping("/room-id")
    public APIResponse<Rooms> getRoomType(@RequestParam long id){
        Rooms room = propertyService.getRoomById(id);

        APIResponse<Rooms> response = new APIResponse<>();
        response.setMessage("Total rooms");
        response.setStatus(200);
        response.setData(room);
        return response;
    }

    @PutMapping("/decrement-availability")
    public APIResponse<String> decrementAvailability(
            @RequestParam long roomAvailabilityId,
            @RequestBody List<LocalDate> dates) {

        logger.info("Decrementing availability for roomAvailabilityId: {}", roomAvailabilityId);

        propertyService.decrementRoomAvailability(roomAvailabilityId, dates);

        APIResponse<String> response = new APIResponse<>();
        response.setMessage("Availability updated successfully");
        response.setStatus(200);
        response.setData("Success");
        return response;
    }

}
