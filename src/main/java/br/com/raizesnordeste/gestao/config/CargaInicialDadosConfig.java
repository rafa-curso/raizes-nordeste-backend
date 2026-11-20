package br.com.raizesnordeste.gestao.config;

import br.com.raizesnordeste.gestao.domain.model.*;
import br.com.raizesnordeste.gestao.domain.repository.InventarioEstoqueRepository;
import br.com.raizesnordeste.gestao.domain.repository.ProdutoCardapioRepository;
import br.com.raizesnordeste.gestao.domain.repository.UnidadeFranquiaRepository;
import br.com.raizesnordeste.gestao.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class CargaInicialDadosConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicialDadosConfig.class);

    private final UsuarioRepository usuarioRepository;
    private final UnidadeFranquiaRepository unidadeRepository;
    private final ProdutoCardapioRepository produtoRepository;
    private final InventarioEstoqueRepository estoqueRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicialDadosConfig(UsuarioRepository usuarioRepository,
                                  UnidadeFranquiaRepository unidadeRepository,
                                  ProdutoCardapioRepository produtoRepository,
                                  InventarioEstoqueRepository estoqueRepository,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando carga de dados de demonstração para a Rede Raízes do Nordeste...");
            inicializarUsuarios();
            inicializarUnidades();
            inicializarProdutosECardapio();
            inicializarEstoque();
            log.info("Carga inicial de dados finalizada com sucesso. Autor: Rafael Inácio da Silva (RU: 4736098).");
        }
    }

    private void inicializarUsuarios() {
        Usuario admin = new Usuario(
            "Rafael Inácio - Administrador Matriz",
            "admin@raizesnordeste.com",
            passwordEncoder.encode("admin123"),
            "(81) 98888-0001",
            PerfilAcesso.ROLE_ADMIN_MATRIZ,
            true
        );

        Usuario gerente = new Usuario(
            "Maria Clara Gerente Recife",
            "gerente@raizesnordeste.com",
            passwordEncoder.encode("gerente123"),
            "(81) 98888-0002",
            PerfilAcesso.ROLE_GERENTE_LOJA,
            true
        );

        Usuario cliente = new Usuario(
            "Lucas Pereira Cliente",
            "cliente@raizesnordeste.com",
            passwordEncoder.encode("cliente123"),
            "(81) 99999-1234",
            PerfilAcesso.ROLE_CLIENTE,
            true
        );
        cliente.setPontosFidelidade(50); // Cliente com pontos acumulados

        usuarioRepository.saveAll(List.of(admin, gerente, cliente));
    }

    private void inicializarUnidades() {
        UnidadeFranquia recife = new UnidadeFranquia(
            "REC-01",
            "Raízes do Nordeste - Recife Antigo",
            "Recife",
            "PE",
            "Rua do Bom Jesus, 150 - Bairro do Recife",
            "(81) 3224-1000",
            FormatoOperacao.TRADICIONAL_COMPLETA
        );

        UnidadeFranquia caruaru = new UnidadeFranquia(
            "CAR-01",
            "Raízes do Nordeste - Caruaru Capital do Forró",
            "Caruaru",
            "PE",
            "Av. Agamenon Magalhães, 820 - Maurício de Nassau",
            "(81) 3721-2000",
            FormatoOperacao.TRADICIONAL_COMPLETA
        );

        UnidadeFranquia fortaleza = new UnidadeFranquia(
            "FOR-01",
            "Raízes do Nordeste - Fortaleza Beira Mar Express",
            "Fortaleza",
            "CE",
            "Av. Beira Mar, 2100 - Meireles",
            "(85) 3261-3000",
            FormatoOperacao.EXPRESS_REDUZIDA
        );

        unidadeRepository.saveAll(List.of(recife, caruaru, fortaleza));
    }

    private void inicializarProdutosECardapio() {
        List<ProdutoCardapio> produtos = List.of(
            new ProdutoCardapio("CUSC-001", "Cuscuz com Carne de Sol e Queijo Coalho", "Cuscuz de milho vaporizado na hora, recheado com carne de sol desfiada e queijo coalho tostado.", new BigDecimal("22.90"), CategoriaCardapio.CUSCUZ_RECHEADOS, false),
            new ProdutoCardapio("CUSC-002", "Cuscuz Sertanejo com Queijo e Manteiga", "Cuscuz fofinho tradicional regado com manteiga de garrafa e queijo coalho.", new BigDecimal("16.50"), CategoriaCardapio.CUSCUZ_RECHEADOS, false),
            new ProdutoCardapio("TAP-001", "Tapioca Tradicional de Queijo Coalho", "Massa fresca de goma de mandioca tostada na chapa com queijo coalho derretido.", new BigDecimal("15.00"), CategoriaCardapio.TAPIOCAS_ESPECIAIS, false),
            new ProdutoCardapio("TAP-002", "Tapioca de Carne Seca com Catupiry", "Tapioca recheada com carne de sol acebolada e queijo cremoso.", new BigDecimal("19.90"), CategoriaCardapio.TAPIOCAS_ESPECIAIS, false),
            new ProdutoCardapio("BOL-001", "Bolo de Macaxeira com Coco Artesanal", "Fatia generosa de bolo cremoso de macaxeira com lascas de coco fresco.", new BigDecimal("12.50"), CategoriaCardapio.DOCES_E_SOBREMESAS, false),
            new ProdutoCardapio("BOL-002", "Bolo Souza Leão Sazonal Junino", "Receita secular pernambucana cremosa à base de massa puba e leite de coco.", new BigDecimal("15.90"), CategoriaCardapio.DOCES_E_SOBREMESAS, true),
            new ProdutoCardapio("BEB-001", "Suco Natural de Graviola 500ml", "Polpa natural de graviola batida com água mineral ou leite gelado.", new BigDecimal("11.00"), CategoriaCardapio.BEBIDAS_REGIONAIS, false),
            new ProdutoCardapio("BEB-002", "Suco Regional de Caju 500ml", "Suco de caju fresco refrescante e rico em vitamina C.", new BigDecimal("9.50"), CategoriaCardapio.BEBIDAS_REGIONAIS, false),
            new ProdutoCardapio("BEB-003", "Café Coado na Hora com Manteiga de Garrafa", "Café especial do agreste passado no pano com gotas de manteiga de garrafa.", new BigDecimal("6.50"), CategoriaCardapio.BEBIDAS_REGIONAIS, false),
            new ProdutoCardapio("COM-001", "Combo Raízes Matinal Completo", "1 Cuscuz com carne de sol + 1 Suco de Caju 500ml + 1 Café Coado.", new BigDecimal("32.90"), CategoriaCardapio.COMBOS_NORDESTINOS, false)
        );

        produtoRepository.saveAll(produtos);
    }

    private void inicializarEstoque() {
        List<UnidadeFranquia> unidades = unidadeRepository.findAll();
        List<ProdutoCardapio> produtos = produtoRepository.findAll();

        for (UnidadeFranquia unidade : unidades) {
            for (ProdutoCardapio produto : produtos) {
                // Unidade express não possui certos itens complexos ou menor estoque
                int quantidade = unidade.getFormatoOperacao() == FormatoOperacao.EXPRESS_REDUZIDA ? 25 : 50;
                InventarioEstoque estoque = new InventarioEstoque(unidade, produto, quantidade, 8);
                estoqueRepository.save(estoque);
            }
        }
    }
}
