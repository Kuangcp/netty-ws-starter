package com.github.kuangcp.ws;


import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Kuangcp
 * 2024-07-30 15:31
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = App.class)
@AutoConfigureMockMvc
public class AppTest {

    @Test
    public void testMain() throws Exception {
        App.main(new String[]{"args"});
        Thread.currentThread().join();
    }
}
