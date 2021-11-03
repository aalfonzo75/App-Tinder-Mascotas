package app.web.tinderMascotas.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Aleidy Alfonzo
 */

@Service
public class NotificacionServicio {
    
   @Autowired
   private JavaMailSender mailSender;
   
   @Async // para que la respuesta del mail sea inmediata
   public void enviar(String cuerpo, String titulo, String mail){
       SimpleMailMessage mensaje= new SimpleMailMessage();
       mensaje.setTo(mail);
       mensaje.setFrom("noreply@tinder-mascota.com");
       mensaje.setSubject(titulo);
       mensaje.setText(cuerpo);
       mailSender.send(mensaje);
   }
           

}
