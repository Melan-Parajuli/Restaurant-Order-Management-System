create database restaurant_app;
use restaurant_app;

-- 1️ Admin Table (Manages users)
CREATE TABLE admin_ (
    AdminId INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    Phone VARCHAR(10) NOT NULL UNIQUE
);



-- 3️ Staff Table (Employees in the Restaurant)
CREATE TABLE staffs (
    StaffID INT PRIMARY KEY AUTO_INCREMENT,
    staff_name VARCHAR(255) NOT NULL,
    Role VARCHAR(255) NOT NULL  -- Could be 'Waiter', 'Chef', 'Manager'
);



-- 5️ Tables Table (Tracks Restaurant Seating)
CREATE TABLE tables (
    TableID INT PRIMARY KEY AUTO_INCREMENT,
    Capacity INT NOT NULL,
    Status ENUM('Available', 'Occupied') DEFAULT 'Available'
);

-- 7️ Restaurant Menu Table (Stores Menu Items)
CREATE TABLE restaurant_menu (
    MenuItems_Id INT PRIMARY KEY ,
    Item_Name VARCHAR(255) NOT NULL,
    Category ENUM('Food', 'Beverage') NOT NULL,
    Price DECIMAL(5,2) NOT NULL
);



CREATE TABLE orders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    Table_Id INT,
    StaffID INT null,  -- Staff handling the order
    menuId INT,
    Quantity INT NOT NULL,
    Status ENUM('Pending', 'Complete') DEFAULT 'Pending',
    FOREIGN KEY (Table_Id) REFERENCES tables(TableID) ON DELETE SET NULL,
    FOREIGN KEY (StaffID) REFERENCES staffs(StaffID) ON DELETE SET NULL,
    FOREIGN KEY (menuId) REFERENCES restaurant_menu(MenuItems_Id) ON DELETE SET NULL
);


-- 9️ Payment Table (Tracks Order Payments)
CREATE TABLE payment (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    TableID INT,
    Amount VARCHAR(255),
    Payment_Methods ENUM('Cash', 'Credit Card', 'UPI', 'Wallet') NOT NULL,
    Payment_Status ENUM('Pending', 'Complete') DEFAULT 'Pending',
    FOREIGN KEY (TableID) REFERENCES tables(TableID) ON DELETE CASCADE
);


insert into restaurant_menu (MenuItems_Id, Item_Name, Category, Price )
values 
(1, 'Chicken Momo', 'Food', '120'),
(2, 'Chickne Chowemin', 'Food', '100'),
(3, 'Samosa', 'Food', '30'),
(4, 'Thakali Khana', 'Food', '250'),
(5, 'Thukpa', 'Food', '120'),
(6, 'Soft Drinks', 'Beverage', '70'),
(7, 'Lassi', 'Beverage', '80'),
(8, 'Cola Juice', 'Beverage', '50'),
(9, 'Chiya', 'Beverage', '30'),
(10, 'Water', 'Beverage', '50');

insert into admin_ (AdminID, name , username, password, Phone)
values 
(1, 'Melan Parajuli', 'melan', '123melan', '9878564123');


