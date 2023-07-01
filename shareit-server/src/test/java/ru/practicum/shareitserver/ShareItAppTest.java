package ru.practicum.shareitserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShareItAppTest {

    @Test
    void testMain() {
        Assertions.assertDoesNotThrow(ShareitServerApplication::new);
        Assertions.assertDoesNotThrow(() -> ShareitServerApplication.main(new String[]{}));
    }
}
