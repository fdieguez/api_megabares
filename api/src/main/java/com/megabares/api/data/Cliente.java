package com.megabares.api.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Francisco
 */
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "nombre", nullable = true)
    private String nombre;

    @Column(name = "apellido", nullable = true)
    private String apellido;

    @OneToOne
    @JoinColumn(name = "id_direccion", nullable = true)
    private Direccion direccion;

    @Column(name = "email", unique = true, nullable = true, length = 50)
    private String email;

    @Column(name = "emails_secundario", unique = false, nullable = true, length = 100)
    private String emailsSecundarios;

    @Column(name = "telefono", nullable = true, length = 25)
    private String telefono;

    @Column(name = "razon_social", nullable = true, length = 30)
    private String razonSocial;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
    private List<Ingreso> ingresos;

    @Column(name = "borrado", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date borrado;

    @Column(name = "numero", unique = true, nullable = false)
    private Long numero;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<PeriodoPago> periodosPagos = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cliente_modulo",
            joinColumns = @JoinColumn(name = "modulo_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id"))
    private Set<Modulo> modulos;

    public Cliente(String nombre, String apellido, Direccion direccion,
                   String email, String razonSocial, List<Ingreso> ingresos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.email = email;
        this.razonSocial = razonSocial;
        this.ingresos = ingresos;
    }

    public Cliente() {
        numero = 0L;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailsSecundarios() {
        return emailsSecundarios;
    }

    public void setEmailsSecundarios(String emailsSecundarios) {
        this.emailsSecundarios = emailsSecundarios;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public List<Ingreso> getIngresos() {
        return ingresos;
    }

    public void setIngresos(List<Ingreso> ingresos) {
        this.ingresos = ingresos;
    }

    public Date getBorrado() {
        return borrado;
    }

    public void setBorrado(Date borrado) {
        this.borrado = borrado;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<PeriodoPago> getPeriodosPagos() {
        return periodosPagos;
    }

    public void setPeriodosPagos(List<PeriodoPago> periodosPagos) {
        this.periodosPagos = periodosPagos;
    }

    public Set<Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(Set<Modulo> modulos) {
        this.modulos = modulos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (this.numero ^ (this.numero >>> 32));
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
        final Cliente other = (Cliente) obj;
        if (this.numero != other.numero) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append("{");
        if (id != null) {
            toStringBuilder.append("id=" + id);
        }
        if (nombre != null) {
            toStringBuilder.append(" nombre=" + nombre);
        }

        if (apellido != null) {
            toStringBuilder.append(" apellido=" + apellido);
        }

        if (direccion != null) {
            toStringBuilder.append(" direccion=" + direccion);
        }

        if (email != null) {
            toStringBuilder.append(" email=" + email);
        }

        if (emailsSecundarios != null) {
            toStringBuilder.append(" emailsSecundarios=" + emailsSecundarios);
        }

        if (telefono != null) {
            toStringBuilder.append(" telefono=" + telefono);
        }

        if (razonSocial != null) {
            toStringBuilder.append(" razonSocial=" + razonSocial);
        }

        if (numero != null) {
            toStringBuilder.append(" numero=" + numero);
        }
        toStringBuilder.append("}");
        return toStringBuilder.toString();

    }

}
