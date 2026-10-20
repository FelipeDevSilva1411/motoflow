package com.motoflow.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.motoflow.model.*;
import com.motoflow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/os")
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private MecanicoRepository mecanicoRepository;

    // 1. Listar Todas
    @GetMapping
    public List<OrdemServico> listarTodas() {
        return repository.findAll();
    }

    // 2. Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscarPorId(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 3. Criar Nova Ordem (Com Inteligência de Normalização)
    @Transactional
    @PostMapping
    public ResponseEntity<OrdemServico> criarOS(@RequestBody OrdemServico osRecebida) {
        
        // Inteligência Cliente: Procura por WhatsApp ou Cria novo
        String telefone = osRecebida.getCliente().getTelefoneWhatsapp();
        Cliente clienteFinal = clienteRepository.findByTelefoneWhatsapp(telefone)
            .orElseGet(() -> {
                Cliente novoCliente = new Cliente();
                novoCliente.setNome(osRecebida.getCliente().getNome());
                novoCliente.setTelefoneWhatsapp(telefone);
                return clienteRepository.save(novoCliente);
            });
        
        clienteFinal.setNome(osRecebida.getCliente().getNome());
        osRecebida.setCliente(clienteFinal);

        // Inteligência Moto: Procura por Placa ou Cria nova
        String placa = osRecebida.getMoto().getPlaca();
        Moto motoFinal = motoRepository.findByPlacaIgnoreCase(placa)
            .orElseGet(() -> {
                Moto novaMoto = new Moto();
                novaMoto.setPlaca(placa);
                novaMoto.setModelo(osRecebida.getMoto().getModelo());
                novaMoto.setClienteProprietario(clienteFinal);
                return motoRepository.save(novaMoto);
            });
        
        osRecebida.setMoto(motoFinal);

        // Regras de Negócio Iniciais da OS
        osRecebida.setCodigoRastreio(gerarCodigoRastreio());
        osRecebida.setStatus(StatusOS.ORCAMENTO);
        osRecebida.setDataEntrada(LocalDateTime.now());
        
        OrdemServico osSalva = repository.save(osRecebida);
        return ResponseEntity.ok(osSalva);
    }

    // 4. Alterar Status (Kanban)
    @Transactional
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemServico> atualizarStatus(@PathVariable Long id, @RequestParam StatusOS novoStatus) {
        return repository.findById(id).map(os -> {
            os.setStatus(novoStatus);
            if (novoStatus == StatusOS.FINALIZADA || novoStatus == StatusOS.PRONTA) {
                os.setDataEntrega(LocalDateTime.now());
            }
            return ResponseEntity.ok(repository.save(os));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Adicionar Peças/Serviços
    @Transactional
    @PostMapping("/{id}/itens")
    public ResponseEntity<OrdemServico> adicionarItem(@PathVariable Long id, @RequestBody ItemOS novoItem) {
        return repository.findById(id).map(os -> {
            os.getItens().add(novoItem);
            os.recalcularTotal();
            return ResponseEntity.ok(repository.save(os));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DTO auxiliar para fechamento
    public record FechamentoDTO(FormaPagamento formaPagamento, BigDecimal desconto, BigDecimal valorRecebido) {}

    // 6. Fechar Caixa / Liquidação
    @Transactional
    @PostMapping("/{id}/fechamento")
    public ResponseEntity<OrdemServico> fecharOS(@PathVariable Long id, @RequestBody FechamentoDTO dados) {
        return repository.findById(id).map(os -> {
            os.recalcularTotal();
            BigDecimal totalBruto = os.getValorTotal() != null ? os.getValorTotal() : BigDecimal.ZERO;
            BigDecimal desc = dados.desconto() != null ? dados.desconto() : BigDecimal.ZERO;
            
            BigDecimal totalComDesconto = totalBruto.subtract(desc);
            if (totalComDesconto.compareTo(BigDecimal.ZERO) < 0) totalComDesconto = BigDecimal.ZERO;

            os.setDesconto(desc);
            os.setValorFinal(totalComDesconto);
            os.setFormaPagamento(dados.formaPagamento());

            BigDecimal recebido = dados.valorRecebido() != null ? dados.valorRecebido() : totalComDesconto;
            os.setValorPago(recebido);

            if (recebido.compareTo(totalComDesconto) > 0) {
                os.setTroco(recebido.subtract(totalComDesconto));
            } else {
                os.setTroco(BigDecimal.ZERO);
            }

            os.setPago(true);
            os.setDataPagamento(LocalDateTime.now());
            
            return ResponseEntity.ok(repository.save(os));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 7. Histórico por Placa
    @GetMapping("/historico/{placa}")
    public ResponseEntity<List<OrdemServico>> obterHistoricoPorPlaca(@PathVariable String placa) {
        List<OrdemServico> historico = repository.findByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(placa.trim());
        return ResponseEntity.ok(historico);
    }

    // 8. Rastreio (Código ou Placa)
    @GetMapping("/rastreio/{termo}")
    public ResponseEntity<OrdemServico> rastrear(@PathVariable String termo) {
        String limpo = termo.trim().replace("-", "").toUpperCase();
        
        var porCodigo = repository.findByCodigoRastreioIgnoreCase(termo.trim().toUpperCase());
        if (porCodigo.isPresent()) return ResponseEntity.ok(porCodigo.get());

        var porPlaca = repository.findFirstByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(limpo);
        return porPlaca.map(ResponseEntity::ok)
                       .orElseGet(() -> repository.findFirstByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(termo.trim().toUpperCase())
                                                  .map(ResponseEntity::ok)
                                                  .orElse(ResponseEntity.notFound().build()));
    }

    // 8b. Endpoint Público para Rastreio na Web
    @GetMapping("/public/rastreio")
    public ResponseEntity<List<OrdemServico>> rastrearPublico(
            @RequestParam(required = false) String placa, 
            @RequestParam(required = false) String codigo) {
        if (placa != null && !placa.isBlank()) {
            String limpa = placa.trim().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            List<OrdemServico> lista = repository.findByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(limpa);
            if (lista.isEmpty()) {
                lista = repository.findByMotoPlacaIgnoreCaseOrderByDataEntradaDesc(placa.trim().toUpperCase());
            }
            return ResponseEntity.ok(lista);
        }
        if (codigo != null && !codigo.isBlank()) {
            return repository.findByCodigoRastreioIgnoreCase(codigo.trim().toUpperCase())
                    .map(os -> ResponseEntity.ok(List.of(os)))
                    .orElse(ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(List.of());
    }

    // 9. Excluir OS por ID (Solução para o erro 405 Method Not Allowed)
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOS(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 10. Associar mecânico responsável à OS com suporte a comissão customizada
    @Transactional
    @PatchMapping("/{id}/mecanico")
    public ResponseEntity<OrdemServico> atribuirMecanico(
            @PathVariable Long id, 
            @RequestParam Long mecanicoId,
            @RequestParam(required = false) BigDecimal comissao) {
        return repository.findById(id).flatMap(os -> 
            mecanicoRepository.findById(mecanicoId).map(mec -> {
                os.setMecanicoResponsavel(mec);
                if (comissao != null) {
                    os.setPercentualComissaoMecanico(comissao);
                }
                return ResponseEntity.ok(repository.save(os));
            })
        ).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint Paginado de Alta Performance
    @GetMapping("/pagina")
    public ResponseEntity<Page<OrdemServico>> listarPaginado(
            @PageableDefault(page = 0, size = 50, sort = "dataEntrada", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrdemServico> paginaDeOrdens = repository.findAll(pageable);
        return ResponseEntity.ok(paginaDeOrdens);
    }

    // Utilitário Privado
    private String gerarCodigoRastreio() {
        return "MF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}