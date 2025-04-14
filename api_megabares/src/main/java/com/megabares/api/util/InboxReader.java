package com.megabares.api.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;

import javax.mail.BodyPart;

import javax.mail.Flags;

import javax.mail.Folder;

import javax.mail.Message;

import javax.mail.MessagingException;

import javax.mail.Multipart;

import javax.mail.NoSuchProviderException;

import javax.mail.Part;

import javax.mail.Session;

import javax.mail.Store;

import javax.mail.internet.MimeMultipart;

import javax.mail.search.FlagTerm;
import lombok.extern.log4j.Log4j2;

@SuppressWarnings("all")
@Log4j2
public class InboxReader {

    private static String contenidoDelMail;
    private static String USUARIO = "sistemamegabares@gmail.com";
    private static String PASS = "mega.bares";
    private static String MERCADOPAGO = "mercadopago.com";
    private static String MERCADOLIBRE = "mercadolibre.com";
    private static String PROTOCOL = "mail.store.protocol";
    private static String IMAPS = "imaps";
    private static String HOST = "imap.gmail.com";
    //
    private static final String ACTUALIZACION = "[MEGA-ACT]";
    private static final String ERROR = "[MEGA-ERR]";
    private static final String DINERO = "Recibiste dinero";
    private static final String SUSCRIPTOR = "Tienes un nuevo suscriptor";
    private static final String PAGO = "Recibiste un pago";
    private static final String VENTA = "Vendiste";
    private static final String MEGABARES = "Megabares";
    private static final String NA = "NO_APLICA";
            

    public static void main(String args[]) {

        for (;;) {
            try {
                Thread.sleep(3000L);
                log.info("Comprobando mails");
                procesarEmails(USUARIO, PASS, "Inbox");

            } catch (InterruptedException ex) {
                Logger.getLogger(InboxReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * analiza los correos, y los discrimina por los tipos posibles actualemente
     * 1 - pagos registrados 2 - nueva suscripcion 3 - Errores 4 -
     * actualizaciones
     *
     * @param usuario
     * @param contrasenia
     * @param carpeta
     */
    private static void procesarEmails(String usuario, String contrasenia, String carpeta) {
        Properties props = System.getProperties();
        props.setProperty(PROTOCOL, IMAPS);

// descomentar si hay un proxy para acceder a intener
        /* props.setProperty("http.proxyHost", "192.168.0.1");
            props.setProperty("http.proxyPort", "8080"); */
        try {
            Session session = Session.getDefaultInstance(props, null);
//            session.setDebug(true);
            Store store = session.getStore(IMAPS);
            store.connect(HOST, usuario, contrasenia);
            Folder inbox = store.getFolder(carpeta);
            inbox.open(Folder.READ_WRITE);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message messages[] = inbox.search(ft);

            for (Message message : messages) {    
                String tipo = getTipo(message);
                
                switch (tipo){
                    case ACTUALIZACION:
                        log.info("Actualizacion");
                        break;
                    case ERROR:
                        log.info("error");
                        break;
                    case DINERO:
                        log.info("dinero");
                        break;
                    case SUSCRIPTOR:
                        log.info("suscriptor");
                        break;
                    case PAGO:
                        log.info("pago");
                        break;
                    case VENTA:
                        log.info("Venta licencia megabares");
                        break;
                    default:
                        log.info("ningun caso");
                }                        

// message.setFlag(Flags.Flag.ANSWERED, true);
                contenidoDelMail = null;
                try {
                    setContenido(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (null != contenidoDelMail) {

                    message.setFlag(Flags.Flag.SEEN, true);
                    String subject = message.getSubject();

//                String content = message.getContentType();
                    MimeMultipart part = (MimeMultipart) message.getContent();

//                BodyPart bodyPart = part.getBodyPart(0);
                    part.getContentType();

                    part.getCount();

                    part.getPreamble();

                    Flags flags = message.getFlags();

                    Address[] form = message.getFrom();

                    log.info("detalle mail: " + subject);
                    log.info("FROM: ");
                    for (Address a : form) {
                        log.info(a.toString());
                    }
//                    log.info("flags:");
//                    log.info(flags.toString());

                    log.info("contenido: ");
                    int indexTotal = contenidoDelMail.indexOf("Total acreditado en tu cuenta");

                    if (indexTotal >= 0) {
                        int indexNroEnvio = contenidoDelMail.indexOf("Envío de dinero N.°:");
                        int indexMailCliente = contenidoDelMail.indexOf("target=\"_blank\">");
                        indexMailCliente = indexMailCliente + "target=\"_blank\">".length();
                        int indexNroEnvioSolo = indexNroEnvio + "Envío de dinero N.°:".length();

                        int indexPago = contenidoDelMail.indexOf("Pago: $ ");

                        String totalAcreditado = contenidoDelMail.substring(indexTotal, contenidoDelMail.indexOf("</p>", indexTotal));

                        totalAcreditado = totalAcreditado.replaceFirst("<strong>", "");
                        totalAcreditado = totalAcreditado.replaceFirst("</strong>", "");
                        log.info(totalAcreditado);
                        log.info(contenidoDelMail.substring(indexNroEnvio, contenidoDelMail.indexOf("</p>", indexNroEnvio)));
                        log.info(contenidoDelMail.substring(indexPago, contenidoDelMail.indexOf("</p>", indexPago)).trim());
                        log.info("numero envio: "
                                + contenidoDelMail.substring(indexNroEnvioSolo, contenidoDelMail.indexOf("</p>", indexNroEnvioSolo)).trim()
                                + "--");
                        log.info(contenidoDelMail.substring(indexMailCliente, contenidoDelMail.indexOf("</a>", indexMailCliente)));
                        log.info("-----------------------------------");
                    } else {
                        log.info(contenidoDelMail);
                    }
                }
            }
//            inbox.close();
            store.close();

        } catch (NoSuchProviderException e) {

            e.printStackTrace();

            System.exit(1);

        } catch (MessagingException e) {

            e.printStackTrace();

            System.exit(2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setContenido(Part p) throws Exception {

        Object o = p.getContent();

        if (o instanceof String) {
            String contenido = (String) o;
//            if (contenido.contains(MERCADOPAGO)
//                    || contenido.contains(MERCADOLIBRE)
//                    || contenido.contains(SISTEMAMEGABARES)) {
////                log.info((String) o);
            contenidoDelMail = contenido;
//            }

        } else if (o instanceof Multipart) {

//            log.info("This is a Multipart");
            Multipart mp = (Multipart) o;

            int count = mp.getCount();

            for (int i = 0; i < count; i++) {
                setContenido(mp.getBodyPart(i));
            }

        } else if (o instanceof InputStream) {
//            log.info("This is just an input stream");
            InputStream is = (InputStream) o;

            int c;

// while ((c = is.read()) != -1)
// System.out.write(c);
        }

    }

    private static String getTipo(Message m) {
        if (null == m) {
            return NA;
        }

        try {
            if (m.getSubject().contains(ACTUALIZACION)) {
                return ACTUALIZACION;

            } else if (m.getSubject().contains(ERROR)) {
                return ERROR;

            } else if (m.getSubject().contains(DINERO)) {
                return DINERO;
            } else if (m.getSubject().contains(SUSCRIPTOR)) {
                return SUSCRIPTOR;
            } else if (m.getSubject().contains(PAGO)) {
                return PAGO;
                
            } else if (m.getSubject().contains(VENTA)
                    && m.getSubject().contains(MEGABARES)){                
                return VENTA;
            }
            
        } catch (MessagingException ex) {
            Logger.getLogger(InboxReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return NA;
    }
}
