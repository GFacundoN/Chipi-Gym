# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-11-11

### Added
- **QR Check-In System**
  - Real-time QR code scanning with webcam integration
  - QR code generation for each client
  - Automatic membership validation during check-in
  - Visual and audio feedback for check-in status
  - Daily check-in history table
  
- **Anti-Duplicate System**
  - 3-second cooldown between scans for same client
  - Prevents multiple accidental registrations
  
- **Optimizations**
  - Dual binarization strategy for better QR detection
  - 200ms scan interval for faster detection
  - Improved QR detection in various lighting conditions

### Changed
- Updated database schema to include `CheckIn` table
- Enhanced UI with new "Check-In" tab
- Added "QR" button to client search table

### Technical
- Added ZXing library (v3.5.3) for QR code operations
- Added Webcam Capture library (v0.3.12) for camera access
- Implemented DAO pattern for check-in data management

## [1.0.0] - 2025-10-XX

### Added
- **Client Management**
  - Register new gym members
  - Search clients by multiple criteria
  - Update client information
  - Delete clients with confirmation
  
- **Membership Management**
  - Create and renew memberships
  - Track membership status (Active/Expired)
  - View upcoming renewals
  - Automatic expiry calculations
  
- **Database Integration**
  - MySQL database connection
  - Client and Membership tables
  - DAO pattern implementation
  
- **User Interface**
  - Modern Java Swing interface
  - Tabbed navigation
  - Custom styled components
  - Date picker integration
  
- **Medical Clearance**
  - Upload fitness certificates
  - Track certificate validity

### Technical Details
- Java 17+ with Swing
- MySQL 8.0+ database
- Maven build system
- DAO pattern for data access
- MVC architecture

---

## Future Releases

### [1.2.0] - Planned
- Export reports to PDF/Excel
- Check-out functionality
- Attendance statistics dashboard
- Multi-language support
- Database backup utility

### [2.0.0] - Planned
- Web-based interface
- Mobile app integration
- Payment processing
- Email notifications
- Cloud database support

---

## Legend

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security improvements
