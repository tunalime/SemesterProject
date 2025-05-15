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
            "Ayşe", "Fatma", "Emine", "Hatice", "Zeynep", "Elif", "Meryem", "Özlem", "Zehra", "Esra"
        };
        
        String[] lastNames = {
            "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Yıldız", "Yıldırım", "Öztürk", "Aydın", "Özdemir",
            "Arslan", "Doğan", "Kılıç", "Aslan", "Çetin", "Koç", "Kurt", "Özkan", "Şimşek", "Tekin"
        };
        
        String[] cities = {
            "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya", "Gaziantep", "Şanlıurfa", "Kocaeli"
        };
        
        String[] emailDomains = {
            "gmail.com", "hotmail.com", "yahoo.com", "outlook.com", "icloud.com"
        };
        
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        
        // Create at least 20 customers
        for (int i = 0; i < 25; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String city = cities[random.nextInt(cities.length)];
            String emailDomain = emailDomains[random.nextInt(emailDomains.length)];
            
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
            customer.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + emailDomain);
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
                for (int i = 0; i < 1 + random.nextInt(2); i++) { // 1-2 package types per model
                    String packageType = packageTypes[random.nextInt(packageTypes.length)];
                    Integer year = years[random.nextInt(years.length)];
                    
                    // Base price (in Turkish Lira)
                    BigDecimal basePrice = BigDecimal.valueOf(300000 + random.nextInt(700000));
                    
                    // Total quantity of this model in stock (will create this many vehicles)
                    int totalQuantity = 2 + random.nextInt(5); // 2-6 vehicles per stock item type
                    
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
                
                // Vehicle status (most new cars are available, some may be in different states)
                int statusRandom = random.nextInt(10);
                VehicleStatus status;
                if (statusRandom < 6) {
                    status = VehicleStatus.IN_SHOWROOM; // 60% in showroom
                } else if (statusRandom < 8) {
                    status = VehicleStatus.RESERVED; // 20% reserved
                } else if (statusRandom < 9) {
                    status = VehicleStatus.SOLD; // 10% sold
                } else {
                    status = VehicleStatus.UNDER_MAINTENANCE; // 10% in maintenance
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
        int totalSalesToCreate = Math.min(availableVehicles.size(), 200); // Cap at 200 or available vehicles
        
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
            
            // Some sales have discounts
            if (random.nextBoolean()) {
                discount = vehicle.getPrice().multiply(BigDecimal.valueOf(0.01 * (random.nextInt(10) + 1))); // 1-10% discount
                salePrice = vehicle.getPrice().subtract(discount);
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