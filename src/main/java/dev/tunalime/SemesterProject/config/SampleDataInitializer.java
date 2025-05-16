package dev.tunalime.SemesterProject.config;

import dev.tunalime.SemesterProject.entity.*;
import dev.tunalime.SemesterProject.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service to initialize sample data for development and testing purposes
 */
@Service
public class SampleDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final Random random = new Random();
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private StockItemRepository stockItemRepository;
    
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private Environment environment;

    /**
     * Initializes the database with sample data
     * @return Summary of created data
     */
    public Map<String, Integer> initializeData() {
        Map<String, Integer> result = new HashMap<>();
        
        // Only run in dev or test environments, not in production
        if (isTestOrDevEnvironment()) {
            if (customerRepository.count() == 0 && vehicleRepository.count() == 0) {
                logger.info("Initializing sample data...");
                
                // Create and save stock items (vehicle types)
                List<StockItem> stockItems = createStockItems();
                stockItemRepository.saveAll(stockItems);
                logger.info("Saved {} stock items", stockItems.size());
                result.put("stockItems", stockItems.size());
                
                // Create and save vehicles based on stock items
                List<Vehicle> vehicles = createVehicles(stockItems);
                vehicleRepository.saveAll(vehicles);
                logger.info("Saved {} vehicles", vehicles.size());
                result.put("vehicles", vehicles.size());
                
                // Create and save customers
                List<Customer> customers = createCustomers();
                customerRepository.saveAll(customers);
                logger.info("Saved {} customers", customers.size());
                result.put("customers", customers.size());
                
                // Create and save sales data
                List<Sale> sales = createSales(customers, vehicles);
                saleRepository.saveAll(sales);
                logger.info("Saved {} sales", sales.size());
                result.put("sales", sales.size());
                
                logger.info("Sample data initialization complete");
            } else {
                logger.info("Database already contains data, skipping initialization");
                result.put("message", 0);
            }
        } else {
            logger.info("Not in development or test environment, skipping sample data initialization");
            result.put("skipped", 1);
        }
        
        return result;
    }
    
    private boolean isTestOrDevEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length == 0 || // Default profile (no active profiles set)
               Arrays.asList(activeProfiles).contains("dev") ||
               Arrays.asList(activeProfiles).contains("test");
    }
    
    private List<Customer> createCustomers() {
        List<Customer> customers = new ArrayList<>();
        
        // Turkish first names and last names for sample data
        String[] firstNames = {
            "Ahmet", "Mehmet", "Ali", "Mustafa", "Hüseyin", "Hasan", "İbrahim", "Murat", "Ömer", "Yusuf",
            "Ayşe", "Fatma", "Emine", "Hatice", "Zeynep", "Elif", "Meryem", "Özlem", "Zehra", "Esra",
            "Can", "Emre", "Kemal", "Serkan", "Burak", "Cem", "Deniz", "Gökhan", "Tolga", "Volkan",
            "Selin", "Gamze", "Derya", "Pınar", "Ebru", "Gizem", "Merve", "Tuğba", "Burcu", "Canan"
        };
        
        String[] lastNames = {
            "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Yıldız", "Yıldırım", "Öztürk", "Aydın", "Özdemir",
            "Arslan", "Doğan", "Kılıç", "Aslan", "Çetin", "Koç", "Kurt", "Özkan", "Şimşek", "Tekin",
            "Kara", "Acar", "Altun", "Bulut", "Taş", "Aksoy", "Kaplan", "Yalçın", "Polat", "Şen",
            "Genç", "Aktaş", "Ateş", "Korkmaz", "Alp", "Tunç", "Yıldız", "Öz", "Bakır", "Çalışkan"
        };
        
        String[] cities = {
            "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya", "Gaziantep", "Şanlıurfa", "Kocaeli",
            "Mersin", "Diyarbakır", "Hatay", "Manisa", "Kayseri", "Samsun", "Balıkesir", "Kahramanmaraş", "Van", "Aydın"
        };
        
        String[] emailDomains = {
            "gmail.com", "hotmail.com", "yahoo.com", "outlook.com", "icloud.com"
        };
        
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        
        // Set containing email addresses to ensure uniqueness
        Set<String> usedEmails = new HashSet<>();
        
        // Create at least 50 customers (increased from 25)
        for (int i = 0; i < 50; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String city = cities[random.nextInt(cities.length)];
            String emailDomain = emailDomains[random.nextInt(emailDomains.length)];
            
            // Generate a unique email address for each customer
            String baseEmail = firstName.toLowerCase() + "." + lastName.toLowerCase();
            String email = baseEmail + "@" + emailDomain;
            
            // If the email is already used, add a unique identifier
            int uniqueSuffix = 1;
            while (usedEmails.contains(email)) {
                email = baseEmail + uniqueSuffix + "@" + emailDomain;
                uniqueSuffix++;
            }
            
            // Add the email to the used set
            usedEmails.add(email);
            
            // Turkish phone number format
            String phone = String.format("+90%d%d%d%d%d%d%d%d%d%d",
                    5, // Turkish mobile prefix
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10),
                    random.nextInt(10));
            
            // Random registration date between start and end dates
            LocalDate registrationDate = startDate.plusDays(random.nextInt((int) daysBetween));
            
            // Random customer status
            CustomerStatus status = CustomerStatus.values()[random.nextInt(CustomerStatus.values().length)];
            
            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(city + ", Türkiye");
            customer.setRegistrationDate(registrationDate);
            customer.setStatus(status);
            
            customers.add(customer);
        }
        
        return customers;
    }
    
    private List<StockItem> createStockItems() {
        List<StockItem> stockItems = new ArrayList<>();
        
        // Brand - Model pairs for popular vehicles in Turkey
        Map<String, List<String>> brandModels = new HashMap<>();
        brandModels.put("Renault", Arrays.asList("Clio", "Megane", "Symbol", "Captur", "Kadjar"));
        brandModels.put("Fiat", Arrays.asList("Egea", "Doblo", "Fiorino", "500", "Tipo"));
        brandModels.put("Ford", Arrays.asList("Focus", "Fiesta", "Kuga", "Mondeo", "Puma"));
        brandModels.put("Volkswagen", Arrays.asList("Polo", "Golf", "Passat", "Tiguan", "T-Roc"));
        brandModels.put("Toyota", Arrays.asList("Corolla", "Yaris", "C-HR", "RAV4", "Auris"));
        brandModels.put("Hyundai", Arrays.asList("i20", "i10", "Tucson", "Accent", "Elantra"));
        brandModels.put("Honda", Arrays.asList("Civic", "CR-V", "Jazz", "HR-V", "Accord"));
        brandModels.put("Peugeot", Arrays.asList("208", "308", "2008", "3008", "508"));
        
        // Package types
        String[] packageTypes = {"Basic", "Comfort", "Premium", "Sport", "Luxury", "Urban", "Adventure"};
        
        // Model years
        Integer[] years = {2018, 2019, 2020, 2021, 2022, 2023};
        
        for (Map.Entry<String, List<String>> entry : brandModels.entrySet()) {
            String brand = entry.getKey();
            List<String> models = entry.getValue();
            
            for (String model : models) {
                for (int i = 0; i < 2 + random.nextInt(3); i++) { // 2-4 package types per model (increased from 1-2)
                    String packageType = packageTypes[random.nextInt(packageTypes.length)];
                    Integer year = years[random.nextInt(years.length)];
                    
                    // Yıla ve markaya göre daha gerçekçi fiyat belirle (Türk Lirası cinsinden)
                    BigDecimal basePrice;
                    
                    // Luxury markaları daha yüksek fiyatla
                    boolean isLuxuryBrand = brand.equals("Volkswagen") || brand.equals("Toyota") || brand.equals("Honda");
                    
                    if (isLuxuryBrand) {
                        // Lüks markalar için fiyat aralığı (1,000,000 - 3,000,000 TL)
                        basePrice = BigDecimal.valueOf(1000000 + random.nextInt(2000000));
                    } else {
                        // Standart markalar için fiyat aralığı (500,000 - 1,500,000 TL)
                        basePrice = BigDecimal.valueOf(500000 + random.nextInt(1000000));
                    }
                    
                    // Yıla göre fiyat düzeltmesi
                    // Daha eski modeller için indirim
                    int currentYear = LocalDate.now().getYear();
                    int yearDiff = currentYear - year;
                    if (yearDiff > 0) {
                        // Her yıl için %8 değer kaybı
                        double depreciationFactor = Math.pow(0.92, yearDiff);
                        basePrice = basePrice.multiply(BigDecimal.valueOf(depreciationFactor));
                    }
                    
                    // Paket tipine göre fiyat ayarlaması
                    if (packageType.equals("Premium") || packageType.equals("Luxury")) {
                        basePrice = basePrice.multiply(BigDecimal.valueOf(1.15)); // %15 premium
                    } else if (packageType.equals("Sport")) {
                        basePrice = basePrice.multiply(BigDecimal.valueOf(1.10)); // %10 premium
                    }
                    
                    // Total quantity of this model in stock (will create this many vehicles)
                    int totalQuantity = 4 + random.nextInt(6); // 4-9 vehicles per stock item type (increased from 2-6)
                    
                    StockItem stockItem = new StockItem();
                    stockItem.setBrand(brand);
                    stockItem.setModel(model);
                    stockItem.setYear(year);
                    stockItem.setPackageType(packageType);
                    stockItem.setBasePrice(basePrice);
                    stockItem.setTotalQuantity(totalQuantity);
                    stockItem.setSpecifications("Engine: " + (1.0 + random.nextInt(20) / 10.0) + "L, " +
                            "Transmission: " + (random.nextBoolean() ? "Automatic" : "Manual") + ", " +
                            "Fuel: " + (random.nextBoolean() ? "Gasoline" : "Diesel"));
                    
                    stockItems.add(stockItem);
                }
            }
        }
        
        return stockItems;
    }
    
    private List<Vehicle> createVehicles(List<StockItem> stockItems) {
        List<Vehicle> vehicles = new ArrayList<>();
        
        // Color options
        String[] colors = {"White", "Black", "Silver", "Gray", "Red", "Blue", "Green", "Yellow", "Brown", "Orange"};
        
        // Create vehicles from each stock item
        for (StockItem stockItem : stockItems) {
            for (int i = 0; i < stockItem.getTotalQuantity(); i++) {
                String color = colors[random.nextInt(colors.length)];
                
                // VIN format (simple random for sample data)
                String vin = String.format("%s%s%d%s",
                        stockItem.getBrand().substring(0, 1).toUpperCase(),
                        stockItem.getModel().substring(0, 1).toUpperCase(),
                        stockItem.getYear(),
                        UUID.randomUUID().toString().substring(0, 10).toUpperCase());
                
                // Calculate vehicle specific price (may have small variance from base price)
                BigDecimal price = stockItem.getBasePrice().add(
                        BigDecimal.valueOf(random.nextInt(10000) - 5000)); // +/- 5000 TL
                
                // Stock entry date (when the vehicle arrived at the dealership)
                LocalDate stockEntryDate = LocalDate.now().minusMonths(random.nextInt(12));
                
                // Vehicle status distribution:
                // 45% IN_SHOWROOM
                // 35% IN_STOCK
                // 10% RESERVED
                // 5% UNDER_MAINTENANCE
                // 5% already SOLD (some historical vehicles)
                int statusRandom = random.nextInt(100);
                VehicleStatus status;
                if (statusRandom < 45) {
                    status = VehicleStatus.IN_SHOWROOM; // 45% in showroom
                } else if (statusRandom < 80) {
                    status = VehicleStatus.IN_STOCK; // 35% in stock
                } else if (statusRandom < 90) {
                    status = VehicleStatus.RESERVED; // 10% reserved
                } else if (statusRandom < 95) {
                    status = VehicleStatus.UNDER_MAINTENANCE; // 5% in maintenance
                } else {
                    status = VehicleStatus.SOLD; // 5% already sold
                }
                
                // Description
                String description = stockItem.getBrand() + " " + stockItem.getModel() + " " + 
                        stockItem.getYear() + " " + stockItem.getPackageType() + " package. " +
                        "Color: " + color + ". " + stockItem.getSpecifications();
                
                // Test drive availability (only in-showroom vehicles are available for test drive)
                boolean isAvailableForTestDrive = status == VehicleStatus.IN_SHOWROOM && random.nextBoolean();
                
                Vehicle vehicle = new Vehicle();
                vehicle.setBrand(stockItem.getBrand());
                vehicle.setModel(stockItem.getModel());
                vehicle.setYear(stockItem.getYear());
                vehicle.setPackageType(stockItem.getPackageType());
                vehicle.setVin(vin);
                vehicle.setPrice(price);
                vehicle.setColor(color);
                vehicle.setStockEntryDate(stockEntryDate);
                vehicle.setStatus(status);
                vehicle.setDescription(description);
                vehicle.setAvailableForTestDrive(isAvailableForTestDrive);
                vehicle.setStockItem(stockItem);
                
                vehicles.add(vehicle);
            }
        }
        
        return vehicles;
    }
    
    /**
     * Create sample sales data with appropriate date distribution for forecasting
     */
    private List<Sale> createSales(List<Customer> customers, List<Vehicle> vehicles) {
        List<Sale> sales = new ArrayList<>();
        
        // Get vehicles that can be sold (not already marked as sold)
        List<Vehicle> availableVehicles = vehicles.stream()
                .filter(v -> v.getStatus() != VehicleStatus.SOLD)
                .toList();
        
        // Payment methods
        PaymentMethod[] paymentMethods = PaymentMethod.values();
        
        // Sales employee names
        String[] salesEmployeeNames = {
            "Mehmet Yılmaz", "Ayşe Demir", "Ali Çelik", "Zeynep Kaya", 
            "Mustafa Şahin", "Elif Öztürk", "Hüseyin Aydın", "Fatma Özdemir"
        };
        
        // Create historical sales for the last 24 months for forecasting
        // We'll distribute them to create patterns in the data
        
        LocalDate today = LocalDate.now();
        LocalDate twoYearsAgo = today.minusYears(2);
        
        // Create a distribution pattern - more sales in summer months and December
        // and increasing trend over time
        
        // Limit the number of sales to create to around 50% of available vehicles (increased from 30%)
        // This ensures we don't convert too many vehicles to SOLD status
        int maxSales = (int)(availableVehicles.size() * 0.5);
        int totalSalesToCreate = Math.min(maxSales, 250); // Cap at 250 or 50% of available (increased from 100)
        
        logger.info("Creating {} sales out of {} available vehicles", totalSalesToCreate, availableVehicles.size());
        
        for (int i = 0; i < totalSalesToCreate; i++) {
            // Select a customer randomly
            Customer customer = customers.get(random.nextInt(customers.size()));
            
            // Select a vehicle randomly from available
            Vehicle vehicle = availableVehicles.get(i % availableVehicles.size());
            
            // Calculate a sale date between two years ago and today
            // Use distribution to create patterns:
            // 1. More recent dates have more sales (upward trend)
            // 2. Summer months (6-8) and December (12) have more sales
            
            int monthOffset = random.nextInt(24); // 0-23 months ago
            LocalDate baseDate = today.minusMonths(monthOffset);
            
            // Adjust for seasonal patterns
            Month month = baseDate.getMonth();
            int dayAdjustment;
            
            if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST || month == Month.DECEMBER) {
                // Higher probability for summer and December
                dayAdjustment = random.nextInt(20) - 10; // More sales in these months
            } else {
                dayAdjustment = random.nextInt(40) - 20; // Fewer sales in other months
            }
            
            // Adjust final date with the seasonal pattern and clamping to valid range
            LocalDate saleDate = baseDate.plusDays(dayAdjustment);
            if (saleDate.isAfter(today)) {
                saleDate = today;
            }
            if (saleDate.isBefore(twoYearsAgo)) {
                saleDate = twoYearsAgo;
            }
            
            // Create the sale with slightly randomized price from the vehicle's price
            BigDecimal salePrice = vehicle.getPrice();
            BigDecimal discount = BigDecimal.ZERO;
            
            // Indirim stratejisi:
            // 1. Araba yaşı: Daha yaşlı araçlarda indirim olasılığı daha yüksek
            // 2. Mevsimsellik: Yılın belirli dönemlerinde kampanyalar
            // 3. Rastgele faktör: Bazı müşteriler pazarlık yapabilir
            
            boolean shouldApplyDiscount = false;
            double discountPercentage = 0;
            
            // 1. Araba yaşı bazlı indirim
            int vehicleAge = LocalDate.now().getYear() - vehicle.getYear();
            if (vehicleAge > 0) {
                // Her yıl için indirim olasılığı artıyor
                shouldApplyDiscount = shouldApplyDiscount || random.nextDouble() < (0.2 * vehicleAge);
                discountPercentage += vehicleAge * 0.5; // Her yıl için %0.5 ek indirim
            }
            
            // 2. Mevsimsel indirim
            if (month == Month.JANUARY || month == Month.FEBRUARY || // Kış aylarında satışları artırmak için
                month == Month.AUGUST) { // Ağustos sonu sezon sonu
                shouldApplyDiscount = shouldApplyDiscount || random.nextDouble() < 0.4;
                discountPercentage += 2; // Ek %2 indirim
            }
            
            // 3. Rastgele müşteri pazarlık faktörü
            if (random.nextDouble() < 0.3) { // %30 olasılıkla
                shouldApplyDiscount = true;
                discountPercentage += random.nextDouble() * 3; // %0-3 arası ek indirim
            }
            
            // Toplam indirim yüzdesini sınırla (maksimum %15)
            discountPercentage = Math.min(15, discountPercentage);
            
            if (shouldApplyDiscount && discountPercentage > 0) {
                discount = salePrice.multiply(BigDecimal.valueOf(discountPercentage / 100));
                salePrice = salePrice.subtract(discount);
            }
            
            // Select payment method
            PaymentMethod paymentMethod = paymentMethods[random.nextInt(paymentMethods.length)];
            
            // Select sales employee
            String salesEmployee = salesEmployeeNames[random.nextInt(salesEmployeeNames.length)];
            
            // Create and add the sale
            Sale sale = new Sale();
            sale.setCustomer(customer);
            sale.setVehicle(vehicle);
            sale.setSaleDate(saleDate);
            sale.setSalePrice(salePrice);
            sale.setDiscount(discount);
            sale.setStatus(SaleStatus.COMPLETED);
            sale.setSalesEmployeeName(salesEmployee);
            sale.setPaymentMethod(paymentMethod);
            
            sales.add(sale);
            
            // Update vehicle status to SOLD
            vehicle.setStatus(VehicleStatus.SOLD);
        }
        
        return sales;
    }
} 