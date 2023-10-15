package api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"h2"})
@SpringBootTest
class ApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
