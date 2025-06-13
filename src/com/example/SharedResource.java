// src/com/example/SharedResource.java
package com.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource {

    private String sharedData;
    private final ReentrantLock lock;
    private final Condition okToRead;
    private final Condition okToWrite;

    // Durum Değişkenleri (State Variables)
    private int activeReaders; // AR: Aktif okuyucu sayısı
    private int activeWriters; // AW: Aktif yazıcı sayısı
    private int waitingReaders; // WR: Bekleyen okuyucu sayısı
    private int waitingWriters; // WW: Bekleyen yazıcı sayısı

    public SharedResource() {
        this.sharedData = "Veri1"; // Başlangıç verisi
        this.lock = new ReentrantLock(true); // Fairness policy: true (bekleyen thread'lere öncelik verir)
        this.okToRead = lock.newCondition();
        this.okToWrite = lock.newCondition();
        this.activeReaders = 0;
        this.activeWriters = 0;
        this.waitingReaders = 0;
        this.waitingWriters = 0;
    }

    // Okuyucunun veritabanına erişim izni istemesi
    public void startRead() throws InterruptedException {
        lock.lock();
        try {
            // Algoritmadaki kontrol: Aktif veya bekleyen bir yazıcı varsa bekle.
            // Önemli Not: Mesa monitör yapısı nedeniyle 'if' yerine 'while' kullanıyoruz.
            // Çünkü thread uyandığında koşulun hala geçerli olduğunu garanti edemeyiz.
            while ((activeWriters + waitingWriters) > 0) {
                waitingReaders++;
                System.out.println(Thread.currentThread().getName() + " bir yazıcı olduğu için bekliyor.");
                okToRead.await(); // okToRead koşulu sağlanana kadar bekle
                waitingReaders--;
            }
            activeReaders++;
        } finally {
            lock.unlock();
        }
    }

    // Okuyucunun veritabanı ile işini bitirmesi
    public void endRead() {
        lock.lock();
        try {
            activeReaders--;
            // Eğer bu son okuyucu ise ve bekleyen bir yazıcı varsa, onu uyandır.
            if (activeReaders == 0 && waitingWriters > 0) {
                okToWrite.signal(); // Sadece bir yazıcıyı uyandırır.
            }
        } finally {
            lock.unlock();
        }
    }

    // Yazıcının veritabanına erişim izni istemesi
    public void startWrite() throws InterruptedException {
        lock.lock();
        try {
            // Algoritmadaki kontrol: Aktif bir okuyucu veya yazıcı varsa bekle.
            while ((activeReaders + activeWriters) > 0) {
                waitingWriters++;
                System.out.println(Thread.currentThread().getName() + " başka bir thread aktif olduğu için bekliyor.");
                okToWrite.await(); // okToWrite koşulu sağlanana kadar bekle
                waitingWriters--;
            }
            activeWriters++;
        } finally {
            lock.unlock();
        }
    }

   // src/com/example/SharedResource.java

// ...

    public void endWrite() {
        lock.lock();
        try {
            activeWriters--;
            // Algoritmadaki önceliklendirme:
            // 1. Önce bekleyen başka bir yazıcı var mı diye kontrol et. Varsa ona öncelik ver.
            if (waitingWriters > 0) {
                okToWrite.signal(); // Bir sonraki yazıcıyı uyandır
            } 
            // 2. Bekleyen yazıcı yoksa ve bekleyen okuyucular varsa, hepsini uyandır.
            else if (waitingReaders > 0) {
                okToRead.signalAll(); // TÜM bekleyen okuyucuları uyandır. <-- DÜZELTİLDİ
            }
        } finally {
            lock.unlock();
        }
    }

// ...

    // Gerçek okuma işlemi
    public String read() {
        return this.sharedData;
    }

    // Gerçek yazma işlemi
    public void write(String newData) {
        this.sharedData = newData;
    }
}