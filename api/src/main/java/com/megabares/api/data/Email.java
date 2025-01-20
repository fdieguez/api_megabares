package com.megabares.api.data;

import com.megabares.api.util.Compresor;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @author Francisco
 */
@Entity
@Table(name = "email")
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Version
    @Column(name = "version", nullable = false)
    private int version;
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "origen", nullable = false, length = 30)
    private String origen;
    @Column(name = "contenido", nullable = false)
    private byte[] contenido;
    @Column(name = "numero", unique = true, nullable = false)
    private long numero;
    @Column(name = "asunto", nullable = false, length = 80)
    private String asunto;
    @Column(name = "receptores", nullable = false, length = 80)
    private String receptores;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 6)
    private TipoEmail tipo;
    @JoinColumn(name = "id_ingreso", referencedColumnName = "id", nullable = true)
    @OneToOne(optional = true, cascade = CascadeType.REFRESH)
    private Ingreso ingreso;

    public Email() {
    }

    public Email(Date fecha, String origen, String contenido, String asunto,
                 String receptores, TipoEmail tipo, Ingreso ingreso) {
        this.fecha = fecha;
        this.origen = origen;
        this.contenido = Compresor.comprimir(contenido);
        this.asunto = asunto;
        this.receptores = receptores;
        this.tipo = tipo;
        this.ingreso = ingreso;
    }

    public Email(Date fecha, String origen, String contenido, String asunto,
                 String receptores, TipoEmail tipo) {
        this.fecha = fecha;
        this.origen = origen;
        this.contenido = Compresor.comprimir(contenido);
        this.asunto = asunto;
        this.receptores = receptores;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getContenido() {
        return Compresor.descomprimir(contenido);
    }

    public void setContenido(String contenido) {
        this.contenido = Compresor.comprimir(contenido);
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getReceptores() {
        return receptores;
    }

    public void setReceptores(String receptores) {
        this.receptores = receptores;
    }

    public TipoEmail getTipo() {
        return tipo;
    }

    public void setTipo(TipoEmail tipo) {
        this.tipo = tipo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Ingreso getIngreso() {
        return ingreso;
    }

    public void setIngreso(Ingreso ingreso) {
        this.ingreso = ingreso;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.fecha);
        hash = 37 * hash + Arrays.hashCode(this.contenido);
        hash = 37 * hash + Objects.hashCode(this.asunto);
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
        final Email other = (Email) obj;
        if (!Objects.equals(this.asunto, other.asunto)) {
            return false;
        }
        if (!Objects.equals(this.fecha, other.fecha)) {
            return false;
        }
        if (!Arrays.equals(this.contenido, other.contenido)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Email{fecha=").append(fecha);
        sb.append(", origen=").append(origen);
        sb.append(", numero=").append(numero);
        sb.append(", asunto=").append(asunto);
        sb.append(", receptores=").append(receptores);
        sb.append(", tipo=").append(tipo);
        sb.append('}');
        return sb.toString();
    }

}
