/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megabares.api.repositories;

import com.megabares.api.data.Cobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Francisco
 */
@Repository
public interface CobroRepository extends JpaRepository<Cobro, Integer> {
    
    @Query(value = "SELECT MAX(numero) FROM Ingreso")
    public Long getMaxId();
    
    
}
