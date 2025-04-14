/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.megabares.api.data;

import java.math.BigDecimal;
import jakarta.persistence.*;

/**
 *
 * @author Francisco
 */
@Entity
@Table(name = "venta")
public class Venta extends Ingreso {

    @Column(name = "envio", nullable = true, length = 30)
    private String envio;

    @Column(name = "cantidad", nullable = true)
    private Short cantidad;

    @Column(name = "comision", nullable = true)
    private BigDecimal comision;

    public Venta() {
    }

    public String getEnvio() {
        return envio;
    }

    public void setEnvio(String envio) {
        this.envio = envio;
    }

    public Short getCantidad() {
        return cantidad;
    }

    public void setCantidad(Short cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

}
