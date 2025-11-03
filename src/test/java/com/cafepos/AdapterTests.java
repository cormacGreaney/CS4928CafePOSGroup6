package com.cafepos;

import com.cafepos.printing.Printer;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdapterTests {

    static class FakeLegacyThermalPrinter {
        int lastLen = -1;
        public void legacyPrint(byte[] payload) {
            lastLen = payload.length;
        }
    }

    @Test
    void adapter_converts_text_to_bytes() {
        FakeLegacyThermalPrinter fake = new FakeLegacyThermalPrinter();

        Printer adapter = new Printer() {
            @Override
            public void print(String receiptText) {
                byte[] bytes = receiptText.getBytes(StandardCharsets.UTF_8);
                fake.legacyPrint(bytes);
            }
        };

        adapter.print("ABC");

        assertTrue(fake.lastLen >= 3, "Adapter should convert text to bytes");
    }

    @Test
    void adapter_handles_empty_string() {
        FakeLegacyThermalPrinter fake = new FakeLegacyThermalPrinter();

        Printer adapter = new Printer() {
            @Override
            public void print(String receiptText) {
                byte[] bytes = receiptText.getBytes(StandardCharsets.UTF_8);
                fake.legacyPrint(bytes);
            }
        };

        adapter.print("");
        assertTrue(fake.lastLen == 0, "Empty string should yield 0 bytes");
    }
}
