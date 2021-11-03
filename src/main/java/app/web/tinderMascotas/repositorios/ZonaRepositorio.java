package app.web.tinderMascotas.repositorios;

import app.web.tinderMascotas.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aleidy Alfonzo
 */
@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, String> {
    
   

}
