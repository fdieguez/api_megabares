/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.megabares.api.repositories;
import com.megabares.api.data.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *    public EmailProcess() {   

 * @author Francisco
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByEmail(String email);
    
    @Query(value = "SELECT MAX(numero) FROM Cliente")
    public Long getMaxNumero();
    
}
