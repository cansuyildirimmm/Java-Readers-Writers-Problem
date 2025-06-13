// src/com/example/Reader.java
package com.example;

import java.util.Random;

public class Reader implements Runnable {
    private final SharedResource resource;

    public Reader(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            while (true) {
                resource.startRead(); // Okuma izni iste

                // --- Kritik Bölge Başlangıcı ---
                System.out.println(Thread.currentThread().getName() + " okuyor -> Veri: " + resource.read());
                Thread.sleep(random.nextInt(1000) + 500); // Okuma işlemini simüle et
                // --- Kritik Bölge Sonu ---

                System.out.println(Thread.currentThread().getName() + " okumayı bitirdi.");
                resource.endRead(); // Okuma bittiğini bildir

                Thread.sleep(random.nextInt(2000) + 1000); // Tekrar okumadan önce bekle
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Okuyucu thread kesintiye uğradı.");
        }
    }
}