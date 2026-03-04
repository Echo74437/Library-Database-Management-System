CREATE DATABASE  IF NOT EXISTS WarehouseManagement;
USE WarehouseManagement;

CREATE TABLE Beneficiaries(
    BeneficiaryID INT(6) PRIMARY KEY CHECK (BeneficiaryID BETWEEN 100000 AND 999999),
    BeneficiaryName VARCHAR(100) NOT NULL CHECK (BeneficiaryName REGEXP '^[A-Za-z\\-\\s]+$'),
    Address VARCHAR(255) NOT NULL CHECK (Address REGEXP '^[^,]+,[^,]+,[^,]+$'),
    City VARCHAR(100) NOT NULL,
    ContactNumber CHAR(11) UNIQUE NOT NULL CHECK (ContactNumber REGEXP '^[0-9]{11}$'),
    Status ENUM('Active', 'Inactive') NOT NULL
);

CREATE TABLE Warehouse(
    WarehouseID INT(4) NOT NULL UNIQUE PRIMARY KEY,
    WarehouseName VARCHAR(100) NOT NULL UNIQUE,
    Address VARCHAR(255) CHECK (Address LIKE '%,%,%'),
    City VARCHAR(100) NOT NULL
);

CREATE TABLE ProductCategory(
    CategoryID INT(6) NOT NULL PRIMARY KEY,
    CategoryName VARCHAR(45) UNIQUE NOT NULL
);

CREATE TABLE Product(
    ProductID INT(6) NOT NULL PRIMARY KEY,
    CategoryID INT(6) NOT NULL,
    WarehouseID INT(4) NOT NULL,
    ProductName VARCHAR(45) NOT NULL,
    ProductDescription VARCHAR(100) NOT NULL,
    Quantity INT(6) NOT NULL,
    DateOfExpiry DATE,
    Is_Expired TINYINT(1) DEFAULT 0,
    FOREIGN KEY(CategoryID) REFERENCES ProductCategory(CategoryID),
    FOREIGN KEY(WarehouseID) REFERENCES Warehouse(WarehouseID)
);

CREATE TABLE BeneficiaryRequests (
    RequestID INT AUTO_INCREMENT PRIMARY KEY,
    BeneficiaryID INT(6) NOT NULL,
    WarehouseID INT NOT NULL,
    ProductID INT(6) NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    RequestDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    FOREIGN KEY (BeneficiaryID) REFERENCES Beneficiaries(BeneficiaryID),
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
) AUTO_INCREMENT = 10000001;

CREATE TABLE Allocation (
    AllocationID INT(8) NOT NULL,
    RequestID INT NOT NULL,
    PRIMARY KEY (AllocationID),
    FOREIGN KEY (RequestID) REFERENCES BeneficiaryRequests(RequestID)
);

CREATE TABLE Courier(
    Cour_ID INT(5) NOT NULL AUTO_INCREMENT,
    LastName VARCHAR(128) NOT NULL,
    FirstName VARCHAR(128) NOT NULL,
    ContactNumber VARCHAR(11),
    City VARCHAR(100),
    VehicleType ENUM('Motorcycle', 'Van', '4-Wheeler Truck'),
    Status ENUM('Available', 'Unavailable'),
    PRIMARY KEY(Cour_ID)
);

CREATE TABLE Delivery(
	DeliveryID INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    BeneficiaryID INT(6),
    WarehouseID INT(4),
    CourierID INT(5),
    ProductID INT(3),
    Quantity INT(5),
	AllocationID INT(8),
    DeliveryDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (BeneficiaryID) REFERENCES Beneficiaries (BeneficiaryID),
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse (WarehouseID),
    FOREIGN KEY (CourierID) REFERENCES Courier (Cour_ID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID),
	FOREIGN KEY (AllocationID) REFERENCES Allocation (AllocationID)
);

CREATE TABLE Monitor(
    MonitorID INT(7) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    WarehouseID INT(4),
    ProductID INT(6),
    MonitorDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse (WarehouseID),
    FOREIGN KEY (ProductID) REFERENCES Product (ProductID)
);

-- Warehouse Sample
INSERT INTO Warehouse (WarehouseID, WarehouseName, Address, City) VALUES
(1001, "ManilaWare", "1, Central Street, Maharlika", "Manila"),
(1020, "Northernshire", "23, Bonifacio Street, Kataastaasan", "Quezon City"),
(1141, "Iyam Storage", "100, Gem Street, Ilaya", "Lucena"),
(2552, "CebuanaStore", "58, History Street, Gitna-na-isla", "Cebu City"),
(3498, "Southpoint", "31, Oriental Street, Minamahal", "Davao City"),
(1400, "Commonholdings", "15, Life Street, Poblacion", "Antipolo"),
(1210, "Seaport Warehouse", "18, Malvar Street, Mataas-na-kahoy", "Lipa"),
(2754, "Kraftwares", "41, Lower Street, Nasyonal", "Cainta"),
(1771, "Bay Area Warehouse", "12, Techno Street, Tabing-dagat", "Santa Rosa"),
(1986, "Hillside Storage", "4, Mountain Street, Malamig", "Baguio");

-- Beneficiaries Sample
INSERT INTO Beneficiaries (BeneficiaryID, BeneficiaryName, Address, City, ContactNumber, Status) VALUES 
(100001, 'Maria Santos', '123 Rizal Street, Barangay San Jose, Metro Manila', 'Manila', '09123456789', 'Active'),
(100002, 'Juan Dela Cruz', '456 Bonifacio Ave, Barangay Centro, Quezon', 'Quezon City', '09234567890', 'Active'),
(100003, 'Ana Rodriguez', '789 Mabini Street, Barangay Poblacion, Metro Manila', 'Makati', '09345678901', 'Active'),
(100004, 'Pedro Martinez', '321 Lapu-Lapu Road, Barangay Lahug, Cebu', 'Cebu City', '09456789012', 'Active'),
(100005, 'Rosa Garcia', '654 Davao Street, Barangay Poblacion, Davao del Sur', 'Davao City', '09567890123', 'Active'),
(100006, 'Miguel Fernandez', '987 Taft Avenue, Barangay Malate, Metro Manila', 'Manila', '09678901234', 'Active'),
(100007, 'Carmen Lopez', '147 Commonwealth Ave, Barangay Fairview, Quezon', 'Quezon City', '09789012345', 'Active'),
(100008, 'Roberto Villanueva', '258 Ayala Avenue, Barangay Bel-Air, Metro Manila', 'Makati', '09890123456', 'Active'),
(100009, 'Elena Reyes', '369 Colon Street, Barangay Centro, Cebu', 'Cebu City', '09901234567', 'Active'),
(100010, 'Carlos Mendoza', '741 Roxas Avenue, Barangay San Pedro, Davao del Sur', 'Davao City', '09012345678', 'Inactive');

-- Product Categories Sample
INSERT INTO ProductCategory (CategoryID, CategoryName) VALUES
(201, "Canned Goods"),
(202, "Rice & Grains"),
(203, "Dairy"),
(204, "Fresh Produce"),
(205, "Beverages"),
(206, "Toiletries"),
(207, "Medical Supplies"),
(208, "Baby Supplies"),
(209, "Dry Goods"),
(210, "Snacks");

-- Products Sample
INSERT INTO Product (ProductID, CategoryID, WarehouseID, ProductName, ProductDescription, Quantity, DateOfExpiry) VALUES
(300001, 201, 1001, "Corned Beef", "330g canned corned beef", 120, "2023-04-30"),
(300002, 201, 1020, "Sardines", "155g canned sardines in tomato sauce", 200, "2026-01-15"),
(300003, 202, 1141, "Long Grain Rice 10kg", "10kg bag of long grain rice", 80, NULL),
(300004, 203, 2552, "Evaporated Milk 410g", "Canned evaporated milk", 150, "2025-11-01"),
(300005, 204, 3498, "Bananas (bunch)", "Fresh Cavendish bananas, per bunch", 60, NULL),
(300006, 205, 1400, "Bottled Water 1L", "Mineral water bottles, 1L", 500, NULL),
(300007, 206, 1210, "Bar Soap (3pcs)", "Pack of 3 bath soaps", 240, NULL),
(300008, 207, 2754, "First Aid Kit (small)", "Basic first aid kit with bandages and antiseptic", 40, NULL),
(300009, 208, 2754, "Infant Formula 400g", "Powdered infant formula", 35, "2024-11-30"),
(300010, 209, 1986, "Pasta 500g", "Spaghetti pasta 500g", 220, NULL),
(300011, 202, 1001, "Brown Rice 5kg", "5kg bag of brown rice", 90, NULL),
(300012, 205, 1020, "Bulk Coffee 250g", "Roasted ground coffee", 130, "2026-06-01"),
(300013, 210, 1141, "Crackers 200g", "Salted crackers", 300, "2020-03-18"),
(300014, 201, 2552, "Baked Beans 400g", "Canned baked beans in sauce", 180, "2021-03-15"),
(300015, 207, 3498, "Paracetamol 500mg (20 tabs)", "Analgesic tablets, 20-count blister", 120, "2027-05-01");


-- Courier Sample
INSERT INTO Courier (LastName, FirstName, ContactNumber, City, VehicleType, Status) VALUES
('Perez', 'Rey', '09171234567', 'Manila', 'Motorcycle', 'Available'),
('Reyes', 'Liza', '09202345678', 'Quezon City', 'Van', 'Available'),
('Torres', 'Mark', '09393456789', 'Lucena', 'Motorcycle', 'Unavailable'),
('Lim', 'Jenny', '09454567890', 'Cebu City', '4-Wheeler Truck', 'Available'),
('Cruz', 'Alex', '09565678901', 'Davao City', 'Van', 'Unavailable'),
('Diaz', 'Sarah', '09666789012', 'Antipolo', 'Motorcycle', 'Available'),
('Garcia', 'Robert', '09777890123', 'Lipa', '4-Wheeler Truck', 'Available'),
('Gomez', 'Elena', '09888901234', 'Cainta', 'Motorcycle', 'Available'),
('Ramos', 'Paolo', '09000123456', 'Santa Rosa', 'Van', 'Available'),
('De Guzman', 'Michelle', '09181122334', 'Baguio', 'Motorcycle', 'Unavailable');
