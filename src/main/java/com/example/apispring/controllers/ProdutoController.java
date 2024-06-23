package com.example.apispring.controllers;

import com.example.apispring.models.Produto;
import com.example.apispring.repositorys.ProdutoRepository;
import com.example.apispring.services.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // o rest indica que o retorno vai ser um json, o controller vai encontrar.
@RequestMapping("/api/produtos")
public class ProdutoController {
    private  final ProdutoRepository produtoRepository; //o final indica que o valor não pode ser mudado.
    private final ProdutoService produtoService;
    private final Validator validador;

    @Autowired //ele faz a injeção de dependência no programa. Ele não é necessário quando criamos o programa.
    public ProdutoController(ProdutoRepository produtoRepository, ProdutoService produtoService, Validator validador){
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
        this.validador = validador;
    }

//    @GetMapping("/selecionar")
//    public List<Produto> listarProdutos(){
//        return produtoRepository.findAll();
//    }// o findAll é um método list que usa a classe como parâmetro. Pode ser considerado um método padrão do pacote Java.

    @GetMapping("/selecionar")
    public List<Produto> listarProdutos(){
        return produtoService.buscarTodosProdutos();
    }

    @GetMapping("/selecionarPorNome/{nome}")
    public List<Produto> buscarPorNome(@PathVariable String nome){
        return produtoService.buscarPorNome(nome);
    }

    @PostMapping("/inserir")
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto, BindingResult resultado){
        if (resultado.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : resultado.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.toString());
        }else {
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto inserido com sucesso");
        }
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@PathVariable Long id){ //O pathVariable serve para inserir uma variável na url

        if (produtoService.buscarPorId(id) != null) {
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }

    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarProduto(@PathVariable long id,
                                                   @Valid @RequestBody Produto produtoAtualizado, BindingResult resultado){

        if (resultado.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : resultado.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.toString());
        }else {
            Produto produtoExistence = produtoService.buscarPorId(id);
            Produto produto = produtoExistence;
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        }
    }


    @PatchMapping ("/atualizarParcial/{id}")
    public ResponseEntity<String> atualizarParcial(@PathVariable Long id,
                                                   @Valid @RequestBody Map<String, Object> mudancas){

        try{
            Produto produtoExistente = produtoService.buscarPorId(id);
            Produto produto = produtoExistente;
            if (mudancas.containsKey("nome")){
                produto.setNome(String.valueOf(mudancas.get("nome")));
            }
            if (mudancas.containsKey("descricao")){
                produto.setDescricao(String.valueOf(mudancas.get("descricao")));
            }
            if (mudancas.containsKey("preco")){
                produto.setPreco(Double.valueOf(mudancas.get("preco").toString()));
            }
            if (mudancas.containsKey("quantidadeestoque")){
                produto.setQuantidadeEstoque(Integer.valueOf(mudancas.get("quantidadeestoque").toString()));
            }

            //validando os dados
            DataBinder binder = new DataBinder(produto); //vincula o DataBinder ao produto
            binder.setValidator(validador);// configura o validador
            binder.validate();//executa o validador no objeto vinculado
            BindingResult resultado = binder.getBindingResult();
            if (resultado.hasErrors()){
                Map erros = validarProduto(resultado);
                return ResponseEntity.badRequest().body(erros.toString());
            }
            Produto produtoSalvo = produtoService.salvarProduto(produto);
            return ResponseEntity.ok(String.valueOf(produtoSalvo));

        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }

    }

    public Map<String, String> validarProduto(BindingResult resultado){
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : resultado.getFieldErrors()) {
            // Coloque o nome do campo e a mensagem de erro no mapa
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        return erros;
    }
}





