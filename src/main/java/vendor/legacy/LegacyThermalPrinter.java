package vendor.legacy;

public final class LegacyThermalPrinter {
    public void legacyPrint(byte[] payload) {
        // imagine ESC/POS over serial here
        System.out.println("[Legacy] printing bytes: " + payload.length);
    }
}
