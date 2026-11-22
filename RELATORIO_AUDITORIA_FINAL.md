# Relatório de Auditoria Final — Rede Raízes do Nordeste

**Aluno:** Rafael Inácio da Silva

**RU:** 4736098

**Data da validação final:** 21/11/2026

**Repositório oficial:** https://github.com/rafa-curso/raizes-nordeste-backend

## A. Correções realizadas

### Cadastro e autorização

- Removido `perfil` do DTO de cadastro público.
- Cadastro público passa a atribuir obrigatoriamente `ROLE_CLIENTE` no serviço.
- Confirmada a inexistência de endpoint público para criação ou alteração de perfis administrativos.
- Adicionados testes HTTP que enviam deliberadamente `ROLE_ADMIN_MATRIZ` e comprovam que o usuário persistido continua sendo cliente.
- Adicionados testes de acesso permitido para administrador e negado para cliente na trilha de auditoria.

### JWT, CORS e H2

- Removida a chave JWT fixa da configuração principal e do perfil de desenvolvimento.
- `APP_JWT_SECRET` tornou-se obrigatória, sem valor padrão, com validação mínima de 32 caracteres durante a inicialização.
- Criado `.env.example` apenas com valores ilustrativos; `.env` e arquivos locais equivalentes estão ignorados pelo Git.
- CORS configurado por `APP_CORS_ORIGINS`, usando origens exatas; curingas são rejeitados quando credenciais estão habilitadas.
- Console H2 desabilitado por padrão, habilitado apenas no perfil `dev` e sempre com acesso remoto desabilitado.

### Dados de demonstração

- `CargaInicialDadosConfig` foi limitada aos perfis `dev` e `test`.
- Credenciais administrativas conhecidas não são mais criadas no perfil padrão ou em produção.
- O perfil `test` conserva os dados necessários à suíte automatizada.

### Fidelidade, pagamento e estoque

- Corrigido o resgate para operar em blocos completos de 10 pontos por R$ 5,00.
- O desconto respeita o teto de 50% do subtotal e consome somente os pontos correspondentes aos blocos efetivamente concedidos.
- Validado o exemplo de 50 pontos e subtotal de R$ 20,00: desconto de R$ 10,00, consumo de 20 pontos e saldo de 30 pontos.
- Garantido saldo de pontos nunca negativo e preservação de saldos inferiores a um bloco.
- A camada financeira foi identificada explicitamente como simulação acadêmica, mantendo aprovação, recusa, registros e NSU simulado.
- Testes de estoque comparam os saldos antes da reserva, após o débito, após a recusa e após uma segunda tentativa, comprovando restituição integral e ausência de duplicação.

## B. Testes e compilação

### Execução inicial

Comando: `.\mvnw.cmd clean test`

- 15 testes detectados.
- 15 erros de inicialização porque o perfil `test` ainda não estava ativo e `APP_JWT_SECRET` passou corretamente a ser obrigatória.
- A falha foi corrigida com ativação explícita do perfil isolado de testes.

### Execução final obrigatória

Comando: `.\mvnw.cmd clean test`

- **Testes executados:** 19
- **Aprovados:** 19
- **Falhas:** 0
- **Erros:** 0
- **Ignorados:** 0
- **Resultado Maven:** `BUILD SUCCESS`
- **Tempo registrado:** 35,633 s
- O ciclo `clean test` compilou 69 arquivos-fonte principais e 2 arquivos-fonte de testes antes da execução.

## C. Documentação

- `README.md` atualizado com variáveis de ambiente, perfis, controles de segurança, fidelidade, gateway simulado, testes e link oficial do repositório.
- Coleção e ambiente Postman validados como JSON; cadastro sem `perfil`, pagamentos simulados e cenário negativo de auditoria conferidos.
- Diagramas PlantUML e imagens revisados. Não houve mudança estrutural que justificasse redesenho; a arquitetura já identifica o adquirente como mock.
- Documento Word revisado preservando o original, com atualizações em segurança, testes, execução e pagamento simulado.
- Hiperlink do GitHub verificado com destino exato para `https://github.com/rafa-curso/raizes-nordeste-backend`.

## D. Arquivos de entrega

### Backend e documentação técnica

- `.gitignore`
- `.env.example`
- `README.md`
- `RELATORIO_AUDITORIA_FINAL.md`
- `src/main/resources/application.yml`
- `src/main/java/br/com/raizesnordeste/gestao/config/CargaInicialDadosConfig.java`
- `src/main/java/br/com/raizesnordeste/gestao/config/JwtTokenProvider.java`
- `src/main/java/br/com/raizesnordeste/gestao/config/SecurityConfig.java`
- `src/main/java/br/com/raizesnordeste/gestao/dto/auth/CadastroUsuarioRequest.java`
- `src/main/java/br/com/raizesnordeste/gestao/service/AutenticacaoService.java`
- `src/main/java/br/com/raizesnordeste/gestao/service/GatewayPagamentoExternoService.java`
- `src/main/java/br/com/raizesnordeste/gestao/service/PedidoService.java`
- `src/test/resources/application-test.yml`
- `src/test/java/br/com/raizesnordeste/gestao/PedidoFluxoIntegrationTest.java`
- `src/test/java/br/com/raizesnordeste/gestao/RaizesNordesteApplicationTests.java`

### Documento acadêmico

- Original preservado: `Relatorio_Academico_UNINTER_Rafael_Inacio.docx`
- Entrega revisada: `Relatorio_Academico_UNINTER_Rafael_Inacio_REVISADO.docx`

## E. Pendências e limites de escopo

- Não foi implementada integração financeira real, por depender de provedor, credenciais, contrato e autorização; a implementação permanece corretamente simulada.
- Não foi realizado teste contra infraestrutura de produção, pois ela não foi fornecida nem integra o escopo local.
- Nenhum commit ou push foi realizado.
