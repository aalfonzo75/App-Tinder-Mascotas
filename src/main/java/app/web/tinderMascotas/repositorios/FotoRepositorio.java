package app.web.tinderMascotas.repositorios;

import app.web.tinderMascotas.entidades.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aleidy Alfonzo
 */

@Repository
public interface FotoRepositorio extends JpaRepository<Foto, String>{

}
