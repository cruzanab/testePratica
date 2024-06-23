package com.example.apispring.repositorys;
import com.example.apispring.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeLikeIgnoreCase(String nome);
}
