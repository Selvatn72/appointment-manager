package com.aptmgt.customer.services;

import com.aptmgt.commons.exceptions.ErrorHandler;
import com.aptmgt.commons.utils.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class LocationServiceImpl implements ILocationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${here.api.url}")
    private String hereApiUrl;

    @Value("${here.api.key}")
    private String hereApiKey;

    @Override
    public ResponseEntity<JSONObject> getLocationByGeocodes(String latitude, String longitude) throws URISyntaxException {
        restTemplate.setErrorHandler(new ErrorHandler());

        URI uri = new URI(hereApiUrl + latitude + Constants.COMMA_URL + longitude + Constants.HERE_API_PARAMS + hereApiKey);

        // send GET request
        ResponseEntity<JSONObject> response = restTemplate.getForEntity(uri, JSONObject.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
