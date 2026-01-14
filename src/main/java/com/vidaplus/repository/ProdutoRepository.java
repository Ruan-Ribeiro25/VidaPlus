package com.vidaplus.repository;

import com.vidaplus.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Busca produtos com estoque baixo (para o card de alerta)
    @Query("SELECT p FROM Produto p WHERE p.quantidadeAtual <= p.estoqueMinimo")
    List<Produto> findProdutosComBaixoEstoque();

    // Conta quantos produtos estão com estoque baixo
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.quantidadeAtual <= p.estoqueMinimo")
    long countProdutosBaixoEstoque();
    
    // Soma o valor total do estoque (Se tivéssemos preço, seria aqui. 
    // Por enquanto contamos o total de itens físicos)
    @Query("SELECT COALESCE(SUM(p.quantidadeAtual), 0) FROM Produto p")
    long totalItensEmEstoque();
}