package com.megabares.api.data;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Objects;

/**
 *
 * @author Francisco
 */
@Entity
@Table(name = "direccion")
public class Direccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "calle", nullable = true, length = 50)
    private String calle;

    @Column(name = "numero", nullable = true)
    private Short numero;

    @Column(name = "localidad", nullable = true, length = 40)
    private String localidad;

    @Column(name = "provincia", nullable = true, length = 25)
    private String provincia;

    @Column(name = "cod_pos", nullable = true)
    private Short codigoPostal;

    @Column(name = "piso", nullable = true)
    private Byte piso;

    @Column(name = "dpto", nullable = true, length = 2)
    private String departamento;

    public Direccion() {
    }

    public Direccion(String calle, Short numero, String localidad, String provincia, Short codigoPostal, Byte piso, String departamento) {
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
        this.piso = piso;
        this.departamento = departamento;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Short getNumero() {
        return numero;
    }

    public void setNumero(Short numero) {
        this.numero = numero;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Short getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Short codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Byte getPiso() {
        return piso;
    }

    public void setPiso(Byte piso) {
        this.piso = piso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.calle);
        hash = 89 * hash + Objects.hashCode(this.numero);
        hash = 89 * hash + Objects.hashCode(this.localidad);
        hash = 89 * hash + Objects.hashCode(this.provincia);
        hash = 89 * hash + Objects.hashCode(this.codigoPostal);
        hash = 89 * hash + Objects.hashCode(this.piso);
        hash = 89 * hash + Objects.hashCode(this.departamento);
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
        final Direccion other = (Direccion) obj;
        if (!Objects.equals(this.calle, other.calle)) {
            return false;
        }
        if (!Objects.equals(this.localidad, other.localidad)) {
            return false;
        }
        if (!Objects.equals(this.provincia, other.provincia)) {
            return false;
        }
        if (!Objects.equals(this.departamento, other.departamento)) {
            return false;
        }
        if (!Objects.equals(this.numero, other.numero)) {
            return false;
        }
        if (!Objects.equals(this.codigoPostal, other.codigoPostal)) {
            return false;
        }
        if (!Objects.equals(this.piso, other.piso)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Direccion{calle=").append(calle);
        sb.append(", numero=").append(numero);
        sb.append(", localidad=").append(localidad);
        sb.append(", provincia=").append(provincia);
        sb.append(", codigoPostal=").append(codigoPostal);
        sb.append(", piso=").append(piso);
        sb.append(", dpto=").append(departamento);
        sb.append('}');
        return sb.toString();
    }

}
