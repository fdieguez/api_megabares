package com.megabares.api.data;

import java.util.Date;
import java.util.Objects;
import jakarta.persistence.*;

/**
 *
 * @author Francisco
 */
@Entity
@Table(name = "Suscripcion")
public class Suscripcion extends Ingreso {

    @Column(name = "hasta", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date hasta;

    @Column(name = "frecuencia", nullable = true)
    private String frecuencia; // meses

    @Column(name = "primer_deb", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date primerDebito;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    public Suscripcion() {

        activa = true;

    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Date getPrimerDebito() {
        return primerDebito;
    }

    public void setPrimerDebito(Date primerDebito) {
        this.primerDebito = primerDebito;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.hasta);
        hash = 83 * hash + Objects.hashCode(this.frecuencia);
        hash = 83 * hash + Objects.hashCode(this.primerDebito);
        hash = 83 * hash + Objects.hashCode(this.getMonto());
        hash = 83 * hash + Objects.hashCode(this.getFecha());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Suscripcion other = (Suscripcion) obj;
        if (!Objects.equals(this.frecuencia, other.frecuencia)) {
            return false;
        }
        if (!Objects.equals(this.hasta, other.hasta)) {
            return false;
        }
        if (!Objects.equals(this.getMonto(), other.getMonto())) {
            return false;
        }
        if (!Objects.equals(this.getFecha(), other.getFecha())) {
            return false;
        }
        if (!Objects.equals(this.getPrimerDebito(), other.getPrimerDebito())) {
            return false;
        }
        return Objects.equals(this.primerDebito, other.primerDebito);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Suscripcion{hasta=").append(hasta);
        sb.append(", frecuencia=").append(frecuencia);
        sb.append(", primerDebito=").append(primerDebito);
        sb.append(", monto=").append(getMonto());
        sb.append(", cliente=").append(getCliente().getEmail());
        sb.append(", fecha=").append(getFecha());
        sb.append('}');
        return sb.toString();
    }

}
