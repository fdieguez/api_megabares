/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.megabares.api.repositories;

import com.megabares.api.data.PeriodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Francisco
 */
@Repository
public interface PeriodoRepository extends JpaRepository<PeriodoPago, Object>{
    
    @Query(value = "SELECT MAX(numero) FROM PeriodoPago")
    public Long getMaxNumero();
    
}
