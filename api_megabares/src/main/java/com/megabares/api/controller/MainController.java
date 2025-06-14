/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megabares.api.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Francisco
 */
@RestController
@Log4j2
public class MainController {
    
    @Value("${version}")
    private String version;


    @GetMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> version() {
        log.info(version);
        return new ResponseEntity<>(String.format(version), HttpStatus.OK);
    }

    @GetMapping("/hola")
    public String gethHola() {

        return "hola_v2";
    }
    
    
    
    // datos de cliente que pagaron
    
    // mandar los errores de sistema
    
    // actualizaciones
    
    // mandar bkps de datos para clientes que lo soliciten   
      

}
