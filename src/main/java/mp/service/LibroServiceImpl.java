package mp.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mp.domain.Genero;
import mp.domain.Libro;
import mp.exception.EntityNotFoundException;
import mp.repository.GeneroRepository;
import mp.repository.LibroRepository;

@Service
@AllArgsConstructor
@Slf4j
public class LibroServiceImpl implements LibroService {
	private LibroRepository libroRepository;
	private GeneroRepository generoRepository;
	private GeneroService generoService;

	@Override
	public List<Libro> buscarTodos() {
		List<Libro> result = libroRepository.findAll();
		result.forEach(libro -> libro.reducirGeneros());
		return result;
	}

	@Override
	public Libro buscarPorId(int id) {
		Optional<Libro> result = libroRepository.findById(id);
		if (result.isEmpty()) {
			throw new EntityNotFoundException("No se encontro el libro con el id: " + id);
		}
		result.get().reducirGeneros();
		return result.get();
	}

	@Override
	public Libro buscarPorNombre(String nombre) {
		Optional<Libro> result = libroRepository.findByNombre(nombre);
		if (result.isEmpty()) {
			throw new EntityNotFoundException("No se encontro el libro con el nombre: " + nombre);
		}
		result.get().reducirGeneros();
		return result.get();
	}

	@Override
	public void crear(Libro libro) {
		// Asociar generos que ya existen en la BD a un libro no creado generaba
		// problemas a la hora de guardarse
		log.info("Ahora vamos a crear el libro: " + libro.getNombre() + " sin generos asociados.");
		Set<Genero> generosSet = libro.getGeneros();
		libro.setGenerosANull();
		libro = libroRepository.save(libro);

		if (!generosSet.isEmpty()) {
			log.info("Asociamos los generos al libro y actualizamos en la BD.");
			libro.setGeneros(generosSet);
			libroRepository.save(libro);
		}
	}

	@Override
	public void actualizar(Libro libro) {
		if (!libroRepository.existsById(libro.getId())) {
			throw new EntityNotFoundException("El libro a actualizar no se ha encontrado en los registros.");
		}
		libroRepository.save(libro);		
	}

	@Override
	public void eliminar(int id) {
		if (!libroRepository.existsById(id)) {
			throw new EntityNotFoundException("El libro a eliminar no se ha encontrado en los registros.");
		}
		libroRepository.deleteById(id);
	}

}
