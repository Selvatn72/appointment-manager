package com.aptmgt.customer.controllers;

import com.aptmgt.customer.services.ILocationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private ILocationService iLocationService;

    @GetMapping("/get_location")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<JSONObject> getLocationByGeocodes(@RequestParam(name = "lat") String latitude, @RequestParam(name = "lng") String longitude) throws URISyntaxException {
        return iLocationService.getLocationByGeocodes(latitude, longitude);
    }

}
