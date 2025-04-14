package com.megabares.api.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Set;


/**
 * @author Francisco representa los modulos agregables al sistema megabares
 */
@Entity
@Table(name = "modulo")
public class Modulo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version", nullable = false)
    private int version;
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;
    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;
    @Column(name = "precio", precision = 8, scale = 2)
    private BigDecimal precio;
    @Column(name = "alta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date alta;
    @Column(name = "baja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date baja;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "modulos")
    private Set<Cliente> clientes;

    public Modulo() {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Date getAlta() {
        return alta;
    }

    public void setAlta(Date alta) {
        this.alta = alta;
    }

    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.nombre);
        hash = 41 * hash + Objects.hashCode(this.descripcion);
        hash = 41 * hash + Objects.hashCode(this.precio);
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
        final Modulo other = (Modulo) obj;
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        if (!Objects.equals(this.precio, other.precio)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Modulo{nombre=").append(nombre);
        sb.append(", descripcion=").append(descripcion);
        sb.append(", precio=").append(precio);
        sb.append(", alta=").append(alta);
        sb.append(", baja=").append(baja);
        sb.append(", cliente=").append(clientes);
        sb.append('}');
        return sb.toString();
    }

}
