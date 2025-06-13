// src/com/example/Writer.java
package com.example;

import java.util.Random;

public class Writer implements Runnable {
    private final SharedResource resource;
    private int writeCounter = 2; // "Veri2", "Veri3" ... oluşturmak için

    public Writer(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            while (true) {
                resource.startWrite(); // Yazma izni iste

                // --- Kritik Bölge Başlangıcı ---
                String newData = "Veri" + writeCounter++;
                System.out.println(Thread.currentThread().getName() + " yazıyor -> Yeni Veri: " + newData);
                resource.write(newData);
                Thread.sleep(random.nextInt(1500) + 1000); // Yazma işlemini simüle et
                // --- Kritik Bölge Sonu ---

                System.out.println(Thread.currentThread().getName() + " yazmayı bitirdi.");
                resource.endWrite(); // Yazma bittiğini bildir

                Thread.sleep(random.nextInt(3000) + 2000); // Tekrar yazmadan önce bekle
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Yazıcı thread kesintiye uğradı.");
        }
    }
}
