package com.cafepos.demo;

import com.cafepos.checkout.ReceiptPrinter;
import com.cafepos.common.Money;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.NoDiscount;
import com.cafepos.pricing.PricingService;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;

public final class Week8Demo_Adapter {
    public static void main(String[] args) {
        var pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(7.80));
        var receiptText = new ReceiptPrinter().format("LAT+L", 2, pr, 10);

        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        printer.print(receiptText);

        System.out.println("[Demo] Sent receipt via adapter.");
    }
}
