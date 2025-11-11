# ⚡ Quick Start Guide

Get Chipi Gym running in 5 minutes!

## 🚀 For Windows Users

### 1. Prerequisites

Download and install:
- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/installer/)

### 2. Setup Database

Open MySQL Workbench and run:

```sql
CREATE DATABASE chipi_gym;
USE chipi_gym;
SOURCE path/to/database_schema.sql;
SOURCE path/to/checkins_schema.sql;
```

### 3. Configure Connection

Edit `src/main/java/com/chipigym/chipi/gym/ConexionBD.java`:
```java
private static final String USER = "root";        // Your MySQL user
private static final String PASSWORD = "1234";    // Your MySQL password
```

### 4. Run Application

#### Option A: Using NetBeans
1. Open project in NetBeans
2. Right-click → **Run**

#### Option B: Command Line
```bash
mvnw.cmd clean install
mvnw.cmd exec:java
```

---

## 🐧 For Linux/Mac Users

### 1. Prerequisites

```bash
# Ubuntu/Debian
sudo apt install default-jdk mysql-server

# Mac (using Homebrew)
brew install openjdk@17 mysql
```

### 2. Setup Database

```bash
mysql -u root -p
```

```sql
CREATE DATABASE chipi_gym;
USE chipi_gym;
SOURCE database_schema.sql;
SOURCE checkins_schema.sql;
```

### 3. Configure Connection

Edit `src/main/java/com/chipigym/chipi/gym/ConexionBD.java`

### 4. Run Application

```bash
./mvnw clean install
./mvnw exec:java
```

---

## 📱 First Steps in Application

1. **Register a Client**: Go to "Alta Cliente" tab
2. **Generate QR Code**: Search client → Click "QR" button
3. **Test Check-In**: Go to "Check-In" → Start Camera → Show QR

---

## ❓ Common Issues

**"Connection refused"**
- Start MySQL server: `sudo service mysql start` (Linux) or check Services (Windows)

**"Camera not found"**
- Make sure no other app is using the camera
- Grant camera permissions

**"ClassNotFoundException: MySQL Driver"**
- Run: `mvnw clean install` to download dependencies

---

## 📚 Need More Help?

Check the full [README.md](README.md) for detailed documentation.

---

**Ready to go! 🎉**
