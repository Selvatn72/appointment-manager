/**
 * 
 */
package com.aptmgt.commons.exceptions;

import com.aptmgt.commons.utils.ResponseMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;


public class ErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return new DefaultResponseErrorHandler().hasError(response);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() == HttpStatus.NOT_FOUND)
			throw new RecordNotFoundException(ResponseMessages.NO_RECORD_FOUND);
	}

}
