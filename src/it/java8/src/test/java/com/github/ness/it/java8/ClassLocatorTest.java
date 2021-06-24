package com.github.ness.it.java8;

import com.github.ness.it.java8.v1_12_R1.ServerImpl;
import com.github.ness.reflect.locator.ClassLocator;
import org.bukkit.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ClassLocatorTest {

    @Test
    public void create(@Mock Server delegateServer) {
        Server server = ServerImpl.create(delegateServer);
        assertDoesNotThrow(() -> ClassLocator.create(server));
    }

}
