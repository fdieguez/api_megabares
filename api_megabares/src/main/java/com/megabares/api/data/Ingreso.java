package com.megabares.api.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.*;

/**
 *
 * @author Francisco
 */
@Entity
@Table(name = "ingreso")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Ingreso implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
    docu referencia => https://www.adictosaltrabajo.com/2007/06/27/hib-inheritance/
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "nro_mp", nullable = true)
    private Long nroMercadoPago;

    @Column(name = "numero", unique = true, nullable = false)
    private long numero;

    @Column(name = "concepto", nullable = false, length = 50)
    private String concepto; // o producto

    @JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private Cliente cliente;

    @OneToOne(mappedBy = "ingreso")
    private Email email;

    public Ingreso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getNroMercadoPago() {
        return nroMercadoPago;
    }

    public void setNroMercadoPago(Long nroMercadoPago) {
        this.nroMercadoPago = nroMercadoPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.fecha);
        hash = 89 * hash + (int) (this.numero ^ (this.numero >>> 32));
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
        final Ingreso other = (Ingreso) obj;
        if (this.numero != other.numero) {
            return false;
        }
        if (!Objects.equals(this.fecha, other.fecha)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ingreso{monto=").append(monto);
        sb.append(", fecha=").append(fecha);
        sb.append(", nroMercadoPago=").append(nroMercadoPago);
        sb.append(", numero=").append(numero);
        sb.append(", concepto=").append(concepto);
        sb.append('}');
        return sb.toString();
    }
}
