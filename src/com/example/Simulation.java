// src/com/example/Simulation.java
package com.example;

public class Simulation {
    private static final int NUM_READERS = 5;
    private static final int NUM_WRITERS = 2;

    public static void main(String[] args) {
        System.out.println("Yazıcı/Okuyucu Problemi Simülasyonu Başlatılıyor...");
        System.out.println("-------------------------------------------------");
        System.out.println(NUM_READERS + " Okuyucu ve " + NUM_WRITERS + " Yazıcı oluşturuluyor.");

        SharedResource sharedResource = new SharedResource();

        // Yazıcı thread'lerini oluştur ve başlat
        for (int i = 0; i < NUM_WRITERS; i++) {
            Thread writerThread = new Thread(new Writer(sharedResource), "Yazıcı-" + (i + 1));
            writerThread.start();
        }

        // Okuyucu thread'lerini oluştur ve başlat
        for (int i = 0; i < NUM_READERS; i++) {
            Thread readerThread = new Thread(new Reader(sharedResource), "Okuyucu-" + (i + 1));
            readerThread.start();
        }
    }
}