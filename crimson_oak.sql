-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 20, 2026 at 11:58 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `crimson_oak`
--

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `guests` int(11) NOT NULL,
  `reservation_date` date NOT NULL,
  `reservation_time` varchar(20) NOT NULL,
  `table_no` varchar(10) NOT NULL,
  `status` varchar(20) DEFAULT 'Confirmed',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`id`, `customer_name`, `guests`, `reservation_date`, `reservation_time`, `table_no`, `status`, `created_at`) VALUES
(8, 'mamamo', 2, '2026-03-21', '4:00 PM - 7:00 PM', 'T14', 'Confirmed', '2026-03-17 09:47:06'),
(9, 'jho', 2, '2026-03-17', '1:00 PM - 4:00 PM', 'T12', 'Confirmed', '2026-03-17 11:00:51'),
(10, 'haha', 2, '2026-03-17', '4:00 PM - 7:00 PM', 'T12', 'Confirmed', '2026-03-17 11:06:15'),
(11, 'chan', 4, '2026-03-21', '7:00 PM - 10:00 PM', 'T3', 'Confirmed', '2026-03-17 11:10:33'),
(12, 'step', 9, '2026-03-21', '10:00 AM - 1:00 PM', 'T2', 'Confirmed', '2026-03-17 11:11:06'),
(13, 'ira', 4, '2026-03-22', '10:00 AM - 1:00 PM', 'T3', 'Confirmed', '2026-03-17 11:14:06'),
(14, 'errah', 2, '2026-03-23', '1:00 PM - 4:00 PM', 'T14', 'Confirmed', '2026-03-17 15:22:00'),
(15, 'Errah', 2, '2026-03-24', '7:00 PM - 10:00 PM', 'T13', 'Confirmed', '2026-03-18 18:22:49'),
(16, 'dhey', 9, '2026-03-19', '10:00 AM - 1:00 PM', 'T2', 'Confirmed', '2026-03-18 20:07:41'),
(17, 'tep', 9, '2026-03-31', '1:00 PM - 4:00 PM', 'T2', 'Confirmed', '2026-03-18 20:08:10'),
(18, 'eunise', 5, '2026-03-27', '4:00 PM - 7:00 PM', 'T5', 'Confirmed', '2026-03-20 09:32:12'),
(19, 'hsdjshdjsh', 2, '2026-03-20', '7:00 PM - 10:00 PM', 'T12', 'Confirmed', '2026-03-20 09:51:02');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`id`, `username`, `password`) VALUES
(1, 'admin', '1234');

-- --------------------------------------------------------

--
-- Table structure for table `tables`
--

CREATE TABLE `tables` (
  `table_no` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tables`
--

INSERT INTO `tables` (`table_no`) VALUES
('T1'),
('T10'),
('T11'),
('T12'),
('T13'),
('T14'),
('T2'),
('T3'),
('T4'),
('T5'),
('T6'),
('T7'),
('T8'),
('T9');

-- --------------------------------------------------------

--
-- Table structure for table `walkin`
--

CREATE TABLE `walkin` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `pax` int(11) DEFAULT NULL,
  `table_no` varchar(10) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `walk_in_time` time NOT NULL,
  `walk_in_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `walkin`
--

INSERT INTO `walkin` (`id`, `name`, `pax`, `table_no`, `status`, `walk_in_time`, `walk_in_date`) VALUES
(14, 'steph', 4, 'T3', 'Waiting', '10:00:00', NULL),
(15, 'steph', 5, 'T4', 'Seated', '10:00:00', '2026-03-20'),
(16, 'eunise', 5, 'T5', 'Waiting', '10:00:00', '2026-03-20'),
(17, 'rean', 3, 'T14', 'Waiting', '10:00:00', '2026-03-20'),
(18, 'jho', 7, 'T2', 'Waiting', '10:00:00', '2026-03-20'),
(19, 'jho', 9, 'T1', 'Waiting', '10:00:00', '2026-03-20'),
(20, 'we', 1, 'T13', 'Waiting', '10:00:00', '2026-03-20'),
(21, 'chaw', 4, 'T7', 'Waiting', '19:00:00', '2026-03-20'),
(22, 'ju', 5, 'T6', 'Seated', '13:00:00', '2026-03-20'),
(23, 'jul', 4, 'T8', 'Waiting', '16:00:00', '2026-03-20');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `table_no` (`table_no`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tables`
--
ALTER TABLE `tables`
  ADD PRIMARY KEY (`table_no`);

--
-- Indexes for table `walkin`
--
ALTER TABLE `walkin`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `walkin`
--
ALTER TABLE `walkin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`table_no`) REFERENCES `tables` (`table_no`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
