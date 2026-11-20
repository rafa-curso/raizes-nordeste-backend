package br.com.raizesnordeste.gestao.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Raízes do Nordeste - API de Gestão Multicanal e Operações")
                        .version("1.0.0")
                        .description("""
                                API REST da Rede de Lanchonetes **Raízes do Nordeste**.
                                Projeto Multidisciplinar desenvolvido para o Centro Universitário Internacional UNINTER.
                                
                                **Autor:** Rafael Inácio da Silva
                                **RU:** 4736098
                                **Ano:** 2026
                                
                                ### Módulos e Recursos:
                                - **Autenticação e Governança:** JWT, BCrypt, RBAC e consentimento explícito LGPD.
                                - **Multicanalidade:** Suporte aos canais Mobile, Totem, Balcão, Pickup e Web.
                                - **Cardápio e Diferenciação Regional:** Produtos sazonais juninos e formatos de operação (Completa vs Reduzida).
                                - **Controle de Estoque e Resiliência:** Reserva, baixa e restituição automática em caso de recusa ou cancelamento.
                                - **Gateway de Pagamento Desacoplado:** Simulação assíncrona de processamento com registro transacional auditável.
                                """)
                        .contact(new Contact()
                                .name("Rafael Inácio da Silva (RU: 4736098)")
                                .email("aluno.rafael.inacio@uninter.com"))
                        .license(new License().name("Uso Acadêmico - UNINTER").url("https://www.uninter.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Informe o token JWT gerado no endpoint de login para autorizar as requisições protegidas.")));
    }
}
