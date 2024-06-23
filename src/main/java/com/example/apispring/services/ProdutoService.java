package com.example.apispring.services;

import com.example.apispring.models.Produto;
import com.example.apispring.repositorys.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {

    private  final ProdutoRepository produtoRepository; //o final indica que o valor não pode ser mudado.

    public ProdutoService(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodosProdutos(){
        return produtoRepository.findAll();
    }// o findAll é um método list que usa a classe como parâmetro. Pode ser considerado um método padrão do pacote Java.

    @Transactional
    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto); // o save serve para salvar o objeto ou alterar
    }

    public Produto buscarPorId(long id){
        return produtoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Produto não encontrado"));
    }

    public Produto excluirProduto(long id){
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);

        return produto;
    }

    public List<Produto> buscarPorNome(String nome){
        return produtoRepository.findByNomeLikeIgnoreCase(nome);
    }

}
