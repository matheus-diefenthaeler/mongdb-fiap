package br.com.diefenthaeler.springblogmongodb.repository;

import br.com.diefenthaeler.springblogmongodb.model.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends MongoRepository<Autor, String> {
}
