package com.aptmgt.customer.services;


import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

public interface ILocationService {

    ResponseEntity<JSONObject> getLocationByGeocodes(String latitude, String longitude) throws URISyntaxException;
}
