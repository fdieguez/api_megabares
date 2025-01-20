package com.megabares.api.data;

import java.math.BigDecimal;
import jakarta.persistence.*;

/**
 *
 * @author Francisco
 */
@Entity
public class Cobro extends Ingreso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCobro")
    private Integer id;

    @Column(name = "tipo_pago", nullable = true)
    private String tipoPago;

    @Column(name = "periodo", nullable = true)
    private Byte periodoEnMeses; // ej: 1 (un mes)

    @Column(name = "comision", nullable = true)
    private BigDecimal comision;

    @Column(name = "total_acred", nullable = true)
    private BigDecimal totalAcreditado;

    @Column(name = "mensaje", nullable = true)
    private String mensaje;

    public Cobro() {
    }

    public Cobro(String tipoPago, Byte periodoEnMeses, BigDecimal comision
            , BigDecimal totalAcreditado, String mensaje) {
        this.tipoPago = tipoPago;
        this.periodoEnMeses = periodoEnMeses;
        this.comision = comision;
        this.totalAcreditado = totalAcreditado;
        this.mensaje = mensaje;
    }
    
    

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Byte getPeriodoEnMeses() {
        return periodoEnMeses;
    }

    public void setPeriodoEnMeses(Byte periodoEnMeses) {
        this.periodoEnMeses = periodoEnMeses;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    public BigDecimal getTotalAcreditado() {
        return totalAcreditado;
    }

    public void setTotalAcreditado(BigDecimal totalAcreditado) {
        this.totalAcreditado = totalAcreditado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cobro{tipoPago=").append(tipoPago);
        sb.append(", periodoEnMeses=").append(periodoEnMeses);
        sb.append(", comision=").append(comision);
        sb.append(", totalAcreditado=").append(totalAcreditado);
        sb.append(", mensaje=").append(mensaje);
        sb.append(", fecha=").append(getFecha());
        sb.append(", monto=").append(getMonto());
        sb.append(", numero=").append(getNroMercadoPago());
        sb.append(", concepto=").append(getConcepto());
        sb.append(", cliente=").append(getCliente().toString());
        sb.append('}');
        return sb.toString();
    }

    
    

}
