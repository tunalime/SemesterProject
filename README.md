# Birinci El Araç Satış Bilgi Sistemi

## Proje Hakkında
Bu proje, birinci el araç satış firmalarına yönelik geliştirilmiş bir bilgi sistemidir. Sistem, araçların stoğa girişinden müşteriye teslimatına kadar olan iş akışlarını yönetmek için tasarlanmıştır.

## Teknik Bilgiler
- Spring Boot 3.4.5
- Java 21
- H2 Database (file-based)
- Thymeleaf, Bootstrap
- JPA / Hibernate

## Veritabanı
Bu proje H2 veritabanı kullanmaktadır. Veritabanı dosyaları proje dizinindeki `data` klasöründe saklanır.

### H2 Konsol Erişimi
Veritabanına erişmek için:
1. Uygulamayı başlatın
2. Tarayıcıda `http://localhost:8080/h2-console` adresine gidin
3. JDBC URL: `jdbc:h2:file:./data/vehicle_sales_db`
4. Kullanıcı adı: `sa`
5. Şifre: `password`

## Çalıştırma
Projeyi çalıştırmak için:

```bash
mvn spring-boot:run
```

veya JAR dosyasını oluşturup doğrudan çalıştırabilirsiniz:

```bash
mvn clean package
java -jar target/SemesterProject-0.0.1-SNAPSHOT.jar
```

## Özellikler
- Araç stok yönetimi
- Müşteri kaydı ve takibi
- Test sürüş planlama ve takibi
- Satış işlemleri yönetimi
- Detaylı raporlama ve analizler
- Satış tahminleme (Hareketli Ortalama Yöntemi)
