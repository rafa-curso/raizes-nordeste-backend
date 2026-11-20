# Rede Raízes do Nordeste — API Back-end de Gestão Multicanal e Operações

> **Projeto Multidisciplinar — Trilha Back-End**  
> **Instituição:** Centro Universitário Internacional UNINTER  
> **Aluno:** Rafael Inácio da Silva  
> **RU:** 4736098  
> **Ano:** 2026  

---

## 1. Visão Geral da Solução

A **API Raízes do Nordeste** é uma solução corporativa completa para centralização, governança e integração operacional da rede de lanchonetes de culinária nordestina "Raízes do Nordeste".

O sistema unifica a jornada do cliente através de múltiplos canais de atendimento (**Aplicativo Mobile, Totens de Autoatendimento, Balcão Presencial, Retirada Rápida Pickup e Portal Web**), garantindo consistência no cardápio regional, controle descentralizado de estoque por franquia, integração desacoplada com gateways de pagamento externos e total conformidade com a **Lei Geral de Proteção de Dados (LGPD)**.

---

## 2. Diferenciais Arquiteturais e Inovações do Projeto

- **Controle Resiliente de Estoque com Rollback Automático:** Ao criar um pedido, os itens são reservados. Caso o pagamento simulado no gateway externo seja recusado ou o pedido seja cancelado, o estoque é **automaticamente restituído** com emissão de evento de compensação no log de auditoria.
- **Governança LGPD por Padrão (Privacy by Design):** Cadastro de usuários com registro explícito de consentimento (`consentimentoLgpd`), registro de data/hora do aceite e anonimização de senhas via hash BCrypt.
- **Programa de Fidelização Integrado:** Regra de negócio que concede 1 ponto para cada R$ 10,00 consumidos em pedidos pagos, além de permitir o resgate progressivo de descontos no fechamento da compra.
- **Integração de Pagamento Desacoplada:** Arquitetura que isola o processamento financeiro do núcleo do sistema, suportando webhooks e retornos assíncronos via serviço dedicado `GatewayPagamentoExternoService`.
- **Trilha de Auditoria Auditável:** Monitoramento de eventos críticos (`RegistroAuditoria`) registrando usuário responsável, IP/canal, entidade afetada e payload resumido.
- **Tratamento de Exceções Padronizado (RFC 7807):** Respostas de erro no formato *Problem Details* com status HTTP semânticos (400, 401, 403, 404, 409).
- **Cobertura de Testes Automatizados:** Testes de integração desenvolvidos com Spring Boot Test e JUnit 5 cobrindo cenários positivos e negativos.

---

## 3. Tecnologias Utilizadas

- **Linguagem:** Java 17 LTS (utilizando Modern Java Records, Pattern Matching e imutabilidade)
- **Framework Principal:** Spring Boot 3.2.5
- **Segurança e Autenticação:** Spring Security 6 & JSON Web Tokens (JJWT 0.12.5)
- **Persistência de Dados:** Spring Data JPA & Hibernate
- **Banco de Dados:** H2 Database Engine (modo persistente em arquivo local)
- **Validação de Dados:** Jakarta Bean Validation (Hibernate Validator)
- **Documentação Interativa:** OpenAPI 3 / Swagger UI (`springdoc-openapi-starter-webmvc-ui 2.5.0`)
- **Gerenciador de Dependências:** Apache Maven com Maven Wrapper incluído
- **Testes Automatizados:** JUnit 5, AssertJ, Spring Test Framework

---

## 4. Como Executar a Aplicação

### Pré-requisitos
- **Java JDK 17** instalado e configurado no PATH do sistema operacional.
- O Maven Wrapper (`mvnw` / `mvnw.cmd`) já está incluído na raiz do projeto, dispensando instalações adicionais de Maven ou servidores externos de banco de dados.

### Execução dos Testes Automatizados
No terminal, dentro da pasta do projeto backend:
```powershell
.\mvnw.cmd clean test
```

### Inicialização do Servidor
Para iniciar a API em modo de desenvolvimento:
```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível no endereço:
```text
http://localhost:8080
```

---

## 5. Acesso ao Banco de Dados (Console H2)

O banco de dados H2 funciona em modo de arquivo persistente em `./data/raizes_nordeste_db`.

- **URL da Console Web:** `http://localhost:8080/h2-console`
- **JDBC Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:file:./data/raizes_nordeste_db;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE`
- **Usuário:** `sa`
- **Senha:** *(deixar em branco)*

---

## 6. Documentação Interativa da API (Swagger / OpenAPI)

Com a aplicação em execução, acesse a documentação interativa com suporte para testes imediatos:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec:** `http://localhost:8080/v3/api-docs`

> **Como testar endpoints protegidos no Swagger:**
> 1. Execute o endpoint `POST /api/v1/auth/login` informando as credenciais de Admin ou Cliente.
> 2. Copie o token retornado no campo `tokenAcesso`.
> 3. No topo direito do Swagger UI, clique no botão **Authorize**, digite o token no campo de texto e confirme.

---

## 7. Credenciais Iniciais de Demonstração (Seed Data)

Ao iniciar pela primeira vez, a aplicação alimenta o banco de dados automaticamente com dados de homologação:

| Perfil de Acesso | E-mail de Login | Senha | Descrição de Acessos |
| :--- | :--- | :--- | :--- |
| **ROLE_ADMIN_MATRIZ** | `admin@raizesnordeste.com` | `admin123` | Acesso irrestrito a cardápio, unidades, estoque e auditoria global |
| **ROLE_GERENTE_LOJA** | `gerente@raizesnordeste.com` | `gerente123` | Gestão de pedidos e controle de estoque de unidade |
| **ROLE_CLIENTE** | `cliente@raizesnordeste.com` | `cliente123` | Consulta de cardápio, criação de pedidos e pagamento via canais |

---

## 8. Principais Endpoints da API RESTful

### 8.1 Autenticação e Usuários
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Autentica e gera token JWT | Público |
| `POST` | `/api/v1/auth/cadastrar` | Cadastra novo cliente com consentimento LGPD | Público |
| `GET` | `/api/v1/auth/me` | Retorna o perfil do usuário logado | Autenticado |

### 8.2 Cardápio Regional e Produtos
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/cardapio` | Lista itens do cardápio (filtro opcional por categoria) | Público |
| `GET` | `/api/v1/cardapio/{id}` | Consulta detalhes de um item específico | Público |
| `POST` | `/api/v1/cardapio` | Cadastra novo prato típico ou item sazonal junino | ADMIN_MATRIZ |

### 8.3 Unidades da Franquia
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/unidades` | Lista as lojas ativas da rede | Público |
| `GET` | `/api/v1/unidades/{id}` | Consulta detalhes da unidade física | Público |
| `POST` | `/api/v1/unidades` | Registra nova franquia (Tradicional ou Express) | ADMIN_MATRIZ |

### 8.4 Controle de Estoque
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/estoque/unidade/{unidadeId}` | Consulta inventário local da franquia | ADMIN / GERENTE |
| `GET` | `/api/v1/estoque/alertas-minimo` | Lista produtos em nível crítico de estoque | ADMIN / GERENTE |
| `POST` | `/api/v1/estoque` | Ajusta ou repõe saldo de estoque | ADMIN / GERENTE |

### 8.5 Pedidos Multicanal e Pagamento Desacoplado
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/pedidos` | Cria pedido multicanal com reserva de estoque | CLIENTE |
| `GET` | `/api/v1/pedidos` | Lista pedidos (filtros por canal, status e unidade) | Autenticado |
| `GET` | `/api/v1/pedidos/{id}` | Consulta detalhes do pedido e itens | Autenticado |
| `POST` | `/api/v1/pedidos/{id}/pagamento` | Solicita liquidação ao Gateway Desacoplado | CLIENTE |
| `PATCH` | `/api/v1/pedidos/{id}/status` | Atualiza status operacional (Cozinha/Entrega) | ADMIN / GERENTE |
| `GET` | `/api/v1/pedidos/{id}/pagamentos` | Consulta histórico de transações financeiras | Autenticado |

### 8.6 Trilha de Auditoria
| Método | Endpoint | Descrição | Permissão |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/auditoria` | Consulta os registros de auditoria e segurança | ADMIN_MATRIZ |

---

## 9. Coleção de Testes no Postman

Na pasta `postman/` estão disponibilizados os arquivos para teste completo de ponta a ponta:
- `Raizes_Nordeste_Postman_Collection_Rafael_Inacio.json`
- `Raizes_Nordeste_Postman_Environment.json`

**Instruções de Importação:**
1. Abra o Postman e clique em **Import**.
2. Selecione a coleção e o environment da pasta `postman/`.
3. Selecione o ambiente **Raízes do Nordeste - Local**.
4. Ao disparar as requisições `01 - Login ADMIN` e `02 - Login CLIENTE`, os tokens JWT são automaticamente salvos nas variáveis de ambiente.

---

## 10. Estrutura de Diretórios do Projeto

```text
raizes-nordeste-backend/
├── docs/
│   └── diagramas/
│       ├── figura1_casos_de_uso.png
│       ├── figura2_der_completo.png
│       ├── figura3_diagrama_classes.png
│       ├── figura4_arquitetura_sistema.png
│       ├── figura5_swagger_ui.png
│       ├── figura6_evidencia_testes.png
│       ├── diagrama-casos-de-uso.puml
│       ├── diagrama-entidade-relacionamento.puml
│       ├── diagrama-classes.puml
│       └── diagrama-arquitetura.puml
├── postman/
│   ├── Raizes_Nordeste_Postman_Collection_Rafael_Inacio.json
│   └── Raizes_Nordeste_Postman_Environment.json
├── src/
│   ├── main/
│   │   ├── java/br/com/raizesnordeste/gestao/
│   │   │   ├── RaizesNordesteApplication.java
│   │   │   ├── config/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── service/
│   │   │   └── web/controller/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/br/com/raizesnordeste/gestao/
│           ├── PedidoFluxoIntegrationTest.java
│           └── RaizesNordesteApplicationTests.java
├── pom.xml
└── README.md
```
