-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 18, 2021 at 03:43 PM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.4.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hi_food`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `age` varchar(255) DEFAULT NULL,
  `aimg` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `menu_id` int(11) NOT NULL,
  `cat_img` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `id` int(11) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `cimg` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `customer_meal_booking`
--

CREATE TABLE `customer_meal_booking` (
  `id` int(11) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `restaurant_id` varchar(255) NOT NULL,
  `meal_id` int(11) NOT NULL,
  `data_time_booking` varchar(255) DEFAULT NULL,
  `data_time_delivary` varchar(255) DEFAULT NULL,
  `is_in_door` int(11) DEFAULT NULL,
  `restaurent_tables_id` int(11) DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `customer_table_booking`
--

CREATE TABLE `customer_table_booking` (
  `id` int(11) NOT NULL,
  `restaurant_id` varchar(255) NOT NULL,
  `restaurent_tables_id` int(11) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `reservation_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`reservation_info`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `meal`
--

CREATE TABLE `meal` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `calories` varchar(255) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `img1` varchar(255) DEFAULT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `menu`
--

CREATE TABLE `menu` (
  `id` int(11) NOT NULL,
  `restaurant_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `rate`
--

CREATE TABLE `rate` (
  `id` int(11) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `restaurant_id` int(11) NOT NULL,
  `customer_rate` double NOT NULL,
  `feed_back` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `restaurant`
--

CREATE TABLE `restaurant` (
  `id` int(11) NOT NULL,
  `restaurant_name` varchar(255) DEFAULT NULL,
  `restaurant_address` varchar(255) DEFAULT NULL,
  `restaurant_phone_no` varchar(255) DEFAULT NULL,
  `manager_name` varchar(255) DEFAULT NULL,
  `manager_email` varchar(255) DEFAULT NULL,
  `manager_password` varchar(255) DEFAULT NULL,
  `managerimg` varchar(255) DEFAULT NULL,
  `admin_id` varchar(255) NOT NULL,
  `is_accepted` int(11) DEFAULT NULL,
  `restaurent_img` varchar(255) DEFAULT NULL,
  `open_at` varchar(255) DEFAULT NULL,
  `close_at` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `restaurent_tables`
--

CREATE TABLE `restaurent_tables` (
  `id` int(11) NOT NULL,
  `restaurant_id` varchar(255) NOT NULL,
  `table_number` int(11) DEFAULT NULL,
  `table_status` varchar(255) DEFAULT NULL,
  `table_number_seats` int(11) DEFAULT NULL,
  `table_location` varchar(255) DEFAULT NULL,
  `table_img` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD KEY `menu_id` (`menu_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `customer_meal_booking`
--
ALTER TABLE `customer_meal_booking`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_idv3` (`customer_id`),
  ADD KEY `meal_id` (`meal_id`),
  ADD KEY `restaurent_id` (`restaurant_id`),
  ADD KEY `table_id` (`restaurent_tables_id`);

--
-- Indexes for table `customer_table_booking`
--
ALTER TABLE `customer_table_booking`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `restaurent_idv3` (`restaurant_id`),
  ADD KEY `restaurent_table_id` (`restaurent_tables_id`);

--
-- Indexes for table `meal`
--
ALTER TABLE `meal`
  ADD PRIMARY KEY (`id`),
  ADD KEY `meal_category_fk` (`category_id`);

--
-- Indexes for table `menu`
--
ALTER TABLE `menu`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `restaurant_id_2` (`restaurant_id`),
  ADD KEY `restaurant_id` (`restaurant_id`);

--
-- Indexes for table `rate`
--
ALTER TABLE `rate`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_idv2` (`customer_id`),
  ADD KEY `restaurant_idv2` (`restaurant_id`);

--
-- Indexes for table `restaurant`
--
ALTER TABLE `restaurant`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `manager_email` (`manager_email`),
  ADD KEY `admin_id` (`admin_id`);

--
-- Indexes for table `restaurent_tables`
--
ALTER TABLE `restaurent_tables`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `restaurant_id` (`restaurant_id`,`table_number`),
  ADD KEY `restaurent_idv2` (`restaurant_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `customer_meal_booking`
--
ALTER TABLE `customer_meal_booking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `customer_table_booking`
--
ALTER TABLE `customer_table_booking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `meal`
--
ALTER TABLE `meal`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `menu`
--
ALTER TABLE `menu`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rate`
--
ALTER TABLE `rate`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `restaurant`
--
ALTER TABLE `restaurant`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `restaurent_tables`
--
ALTER TABLE `restaurent_tables`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `category`
--
ALTER TABLE `category`
  ADD CONSTRAINT `menu_id` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `customer_meal_booking`
--
ALTER TABLE `customer_meal_booking`
  ADD CONSTRAINT `customer_idCon` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `meal_id` FOREIGN KEY (`meal_id`) REFERENCES `meal` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `restaurant_idCon` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`manager_email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `table_id` FOREIGN KEY (`restaurent_tables_id`) REFERENCES `restaurent_tables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `customer_table_booking`
--
ALTER TABLE `customer_table_booking`
  ADD CONSTRAINT `customer_idConst` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `restIdConst` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`manager_email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `restaurent_table_id` FOREIGN KEY (`restaurent_tables_id`) REFERENCES `restaurent_tables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `meal`
--
ALTER TABLE `meal`
  ADD CONSTRAINT `meal_category_fk` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `menu`
--
ALTER TABLE `menu`
  ADD CONSTRAINT `restMenu_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`manager_email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `rate`
--
ALTER TABLE `rate`
  ADD CONSTRAINT `rate_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rate_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `restaurant`
--
ALTER TABLE `restaurant`
  ADD CONSTRAINT `adminId_con` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `restaurent_tables`
--
ALTER TABLE `restaurent_tables`
  ADD CONSTRAINT `rest_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`manager_email`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
