/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megabares.api.service;

import com.megabares.api.util.EmailProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Francisco
 */
@Service
@Log4j2
public class MailProcess {

    private EmailProcess emailProcess;

    public MailProcess(EmailProcess emailProcess) {
        this.emailProcess = emailProcess;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void tarea3() {        
        emailProcess.monitor();
    }    
}
