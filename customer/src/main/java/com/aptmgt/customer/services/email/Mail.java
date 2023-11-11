package com.aptmgt.customer.services.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Mail {

    private String[] to;
    private String cc;
    private String subject;
    private String content;
    private Map<String, String> model;
}