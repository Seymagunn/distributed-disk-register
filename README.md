Distributed-Disk-Registery (gRPC + TCP)
=======================================

---


# gRPC + Protobuf + TCP Hybrid Distributed Server

Bu proje, birden fazla sunucunun dağıtık bir küme (“family”) oluşturduğu, **gRPC + Protobuf** ile kendi aralarında haberleştiği ve aynı zamanda **lider üye (cluster gateway)** üzerinden dış dünyadan gelen **TCP text mesajlarını** tüm üyelere broadcast ettiği hibrit bir mimari örneğidir.

Sistem Programlama, Dağıtık Sistemler veya gRPC uygulama taslağı olarak kullanınız.

---

##  Özellikler

### ✔ Otomatik Dağıtık Üye Keşfi

Her yeni Üye:

* 5555’ten başlayarak boş bir port bulur
* Kendinden önce gelen üyelere gRPC katılma (Join) isteği gönderir
* Aile (Family) listesine otomatik dahil olur.

### ✔ Lider Üye (Cluster Gateway)

İlk başlayan Üye (port 5555) otomatik olarak **lider** kabul edilir ve:

* TCP port **6666** üzerinden dış dünyadan text mesajı dinler
* Her mesajı Protobuf formatına dönüştürür
* Tüm diğer üyelere gRPC üzerinden gönderir

### ✔ gRPC + Protobuf İçi Mesajlaşma

Üyeler kendi aralarında sadece **protobuf message** ile haberleşir:

```proto
message ChatMessage {
  string text = 1;
  string fromHost = 2;
  int32 fromPort = 3;
  int64 timestamp = 4;
}
```

### ✔ Aile (Family) Senkronizasyonu

Her üye, düzenli olarak diğer aile üyeleri listesini ekrana basar:

```
======================================
Family at 127.0.0.1:5557 (me)
Time: 2025-11-13T21:05:00
Members:
 - 127.0.0.1:5555
 - 127.0.0.1:5556
 - 127.0.0.1:5557 (me)
======================================
```

### ✔ Üye Düşmesi (Failover)

Health-check mekanizması ile kopan (offline) üyeler aile listesinden çıkarılır.

---

## 📁 Proje Yapısı

```
distributed-disk-register/
│
├── pom.xml
├── README.md
├── src
│   └── main
│       ├── java/com/example/family/
│       │       ├── NodeMain.java
│       │       ├── NodeRegistry.java
│       │       └── FamilyServiceImpl.java
│       │
│       └── proto/
│               └── family.proto
```
## Sistem Mimarisi

Bu projede, mesajların güvenli ve hata toleranslı şekilde saklanması için
gRPC + Protobuf ve TCP birlikte kullanılmıştır.

### Düğüm Yapısı
- Sistemdeki tüm düğümler aynı programı çalıştırır.
- 5555 portunu alan düğüm lider olarak görev yapar.
- Diğer düğümler lider düğüme bağlanarak sisteme üye olur.

### İstemci – Lider Haberleşmesi
- İstemciler sadece lider düğüm ile haberleşir.
- Haberleşme TCP üzerinden ve metin tabanlıdır.
- İstemci aşağıdaki komutları gönderebilir:
  - `SET <message_id> <message>`
  - `GET <message_id>`

### Düğümler Arası Haberleşme
- Lider ve üye düğümler arasında gRPC + Protobuf kullanılır.
- Düğümler kendi aralarında metin tabanlı mesaj göndermez.

### Hata Toleransı
- Hata tolerans değeri `tolerance.conf` dosyasından okunur.
- Her mesaj, lider düğüm ve belirlenen sayıda üye düğümde saklanır.
- Lider, mesajların hangi düğümlerde saklandığını takip eder.

### Diskte Saklama
- Her düğüm, kendisine gelen mesajları kendi diskine kaydeder.

### Hata Durumları
- Üye düğümler çalışma sırasında kapanabilir.
- GET isteği geldiğinde lider, mesajı saklayan ve çalışır durumda olan
  bir düğümden mesajı alarak istemciye gönderir.

## 👨🏻‍💻 Kodlama

Yüksek seviyeli dillerde yazılım geliştirme işlemi basit bir editörden ziyade gelişmiş bir IDE (Integrated Development Environment) ile yapılması tavsiye edilmektedir. JVM ailesi dillerinin en çok tercih edilen [IntelliJ IDEA](https://www.jetbrains.com/idea/) aracını edu' lu mail adresinizle öğrenci lisanslı olarak indirip kullanabilirsiniz. Bu projeyi diskinize klonladıktan sonra IDEA' yı açıp, üst menüden _Open_ seçeneği projenin _pom.xml_ dosyasını seçtiğinizde projeniz açılacaktır. 


---

## 🔧 Derleme

Proje dizininde (pom.xml in olduğu):

```bash
mvn clean compile
```

Bu komut:

* `family.proto` → gRPC Java sınıflarını üretir
* Tüm server kodlarını derler

---

## ▶️ Çalıştırma

Her bir terminal yeni bir üye demektir.

### **Terminal 1 – Lider Üye**

```bash
mvn exec:java -Dexec.mainClass=com.example.family.NodeMain
```

Çıktı:

```
Node started on 127.0.0.1:5555
Leader listening for text on TCP 127.0.0.1:6666
...
```

![Sistem Başlatma](https://github.com/ismailhakkituran/distributed-disk-register/blob/main/Distributed%20System%20Start-start.png)


### **Terminal 2, 3, 4… – Diğer Üyeler**

Her yeni terminal:

```bash
mvn exec:java -Dexec.mainClass=com.example.family.NodeMain
```

Üyeler 5556, 5557, 5558… portlarını otomatik bulur
ve aileye katılır.

---
![Üyelerin aileye katılması](https://github.com/ismailhakkituran/distributed-disk-register/blob/main/Distributed%20System%20Start-family.png)

## Mesaj Gönderme (TCP → Lider Üye)

Lider Üye, dış dünyadan gelen text’i 6666 portunda bekler.

Yeni bir terminal aç:

```bash
nc 127.0.0.1 6666
```

Veya:

```bash
telnet 127.0.0.1 6666
```

Mesaj yaz:

```
Merhaba distributed world!
```

![Sistem Başlatma](https://github.com/ismailhakkituran/distributed-disk-register/blob/main/Distributed%20System%20Start-telnet.png)

###  Sonuç

Bu mesaj protobuf mesajına çevrilip tüm üyelere gider.

---

### Diğer Üyelerdeki örnek çıktı:

```
💬 Incoming message:
  From: 127.0.0.1:5555
  Text: Merhaba distributed world!
  Timestamp: 1731512345678
--------------------------------------
```

---

##  Çalışma Prensibi

###  1. Dağıtık Üye Keşfi

Yeni Üye, kendinden önceki portları gRPC ile yoklar:

```
5555 → varsa Join
5556 → varsa Join
...
```

###  2. Lider Üye (Port 5555)

Lider Üye:

* TCP 6666’dan text alır,
* Protobuf `ChatMessage` nesnesine çevirir,
* Tüm kardeş üyelere gRPC RPC gönderir.

###  3. Family Senkronizasyonu

Her üye 10 saniyede bir kendi ailesini ekrana basar.

---

##  Ödev / Bundan Sonra Yapılacaklar

Öğrenciler:

* Üye düşme tespiti (heartbeat)
* Leader election
* gRPC streaming ile real-time chat
* Redis-backed cluster membership
* Broadcast queue implementasyonu
* TCP’den gelen mesajların loglanması
* Çoklu lider senaryosu & conflict resolution

gibi özellikler ekleyebilir.

---

## Lisans

MIT — Eğitim ve araştırma amaçlı serbestçe kullanılabilir.

---

##  Katkı

Pull request’e her zaman açığız!
Yeni özellik önerileri için issue açabilirsiniz.
