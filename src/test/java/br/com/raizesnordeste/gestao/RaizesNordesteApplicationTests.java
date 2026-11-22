package br.com.raizesnordeste.gestao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RaizesNordesteApplicationTests {

    @Test
    @DisplayName("Garante que o contexto da aplicação sobe corretamente com todas as configurações")
    void contextLoads() {
    }
}
