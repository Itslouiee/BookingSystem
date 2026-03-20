# Date Selector Implementation - Code Reference

## Quick Reference: What Was Added

---

## 1. Instance Variables (Added to Dashboard.java)

```java
// Date selector for tables page
private JComboBox<LocalDate> tableDateSelector;
private LocalDate selectedTableDate;
private JLabel dateDisplayLabel;
```

**Location:** Class variables section
**Purpose:** Store the date selector component and current selected date

---

## 2. Enhanced Header Creation

```java
private void createHeader(){
    // ... existing code ...
    
    // Make date label dynamic (was static before)
    dateDisplayLabel = new JLabel(LocalDate.now().format(format));
    
    // NEW: Date selector for March (1-31)
    tableDateSelector = new JComboBox<>();
    selectedTableDate = LocalDate.now();
    
    int currentYear = LocalDate.now().getYear();
    for(int day = 1; day <= 31; day++) {
        try {
            LocalDate marchDate = LocalDate.of(currentYear, 3, day);
            tableDateSelector.addItem(marchDate);
        } catch(Exception e) {
            // Invalid date, skip
        }
    }
    
    // Set default to today if in March, else March 1st
    LocalDate today = LocalDate.now();
    if(today.getMonthValue() == 3 && today.getYear() == currentYear) {
        tableDateSelector.setSelectedItem(today);
        selectedTableDate = today;
    } else {
        tableDateSelector.setSelectedItem(LocalDate.of(currentYear, 3, 1));
        selectedTableDate = LocalDate.of(currentYear, 3, 1);
    }
    
    // Styling
    tableDateSelector.setFont(new Font("Arial",Font.PLAIN,12));
    tableDateSelector.setBackground(Color.WHITE);
    tableDateSelector.setForeground(new Color(102,0,32));
    tableDateSelector.setBounds(300,18,150,35);
    
    // Custom renderer to format dates as "Mar 15"
    tableDateSelector.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer() {
        public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value instanceof LocalDate) {
                value = ((LocalDate)value).format(DateTimeFormatter.ofPattern("MMM dd"));
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    });
    
    // EVENT LISTENER - This is the key trigger!
    tableDateSelector.addActionListener(e -> {
        if(tableDateSelector.getSelectedItem() instanceof LocalDate) {
            selectedTableDate = (LocalDate) tableDateSelector.getSelectedItem();
            loadTablesByDate(selectedTableDate);  // <-- Main method called here
        }
    });
    
    // ... rest of header setup ...
}
```

---

## 3. Main Method: Load Tables by Date

```java
/**
 * Main method to load and update tables based on selected date
 * Fetches all reservations for the given date and updates table colors accordingly
 */
private void loadTablesByDate(LocalDate selectedDate) {
    try {
        // Step 1: Get reservation data for this date
        java.util.Map<String, String> tableReservations = filterReservationsByDate(selectedDate);
        
        // Step 2: Get walk-in data for this date
        java.util.Map<String, String> tableWalkIns = getWalkInsByDate(selectedDate);
        
        // Step 3: Update table colors based on combined data
        for(int i = 0; i < tables.size(); i++) {
            String tableNumber = "T" + (i + 1);
            Color newColor = getTableStatusForDate(tableNumber, selectedDate, tableReservations, tableWalkIns);
            JPanel table = tables.get(i);
            table.setBackground(newColor);
            tableStatus.set(i, detectStatus(newColor));
        }
        
        // Step 4: Update counter labels
        updateCounters();
        
        // Step 5: Redraw the floor panel
        floorPanel.repaint();
        
    } catch(Exception ex) {
        ex.printStackTrace();
    }
}
```

**Key Points:**
- Gets data from BOTH reservations and walk-ins
- Iterates through all 14 tables
- Updates color via `setBackground()`
- Calls existing methods: `detectStatus()`, `updateCounters()`, `repaint()`

---

## 4. Filtering Method: Get Reservations by Date

```java
/**
 * Fetches all reservations for a specific date and returns a map of table_no -> status
 * 
 * SQL: SELECT table_no, status FROM reservations 
 *      WHERE DATE(reservation_date) = ? AND status != 'Cancelled'
 */
private java.util.Map<String, String> filterReservationsByDate(LocalDate selectedDate) {
    java.util.Map<String, String> tableStatusMap = new java.util.HashMap<>();
    
    try {
        Connection conn = Dbconnection.getConnection();
        String sql = "SELECT table_no, status FROM reservations WHERE DATE(reservation_date) = ? AND status != 'Cancelled'";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setDate(1, java.sql.Date.valueOf(selectedDate));
        ResultSet rs = pst.executeQuery();
        
        while(rs.next()) {
            String tableNo = rs.getString("table_no");
            String status = rs.getString("status");
            
            // Only update if we don't already have a higher priority status
            if(!tableStatusMap.containsKey(tableNo)) {
                tableStatusMap.put(tableNo, status);
            }
        }
        
        rs.close();
        pst.close();
        conn.close();
        
    } catch(Exception ex) {
        ex.printStackTrace();
    }
    
    return tableStatusMap;  // {T1: "Reserved", T3: "Seated"}
}
```

**Key Points:**
- Returns `Map<String, String>`: TableNo → Status
- Filters by DATE only (ignores time)
- Excludes cancelled reservations
- Safe resource closing

---

## 5. Filtering Method: Get Walk-ins by Date

```java
/**
 * Fetches all walk-ins for a specific date and returns a map of table_no -> status
 */
private java.util.Map<String, String> getWalkInsByDate(LocalDate selectedDate) {
    java.util.Map<String, String> tableStatusMap = new java.util.HashMap<>();
    
    try {
        Connection conn = Dbconnection.getConnection();
        String sql = "SELECT table_no, status FROM walkin WHERE DATE(walk_in_date) = ? AND status != 'Done'";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setDate(1, java.sql.Date.valueOf(selectedDate));
        ResultSet rs = pst.executeQuery();
        
        while(rs.next()) {
            String tableNo = rs.getString("table_no");
            String status = rs.getString("status");
            tableStatusMap.put(tableNo, status);
        }
        
        rs.close();
        pst.close();
        conn.close();
        
    } catch(Exception ex) {
        ex.printStackTrace();
    }
    
    return tableStatusMap;  // {T2: "Waiting", T5: "Seated"}
}
```

---

## 6. Status Mapping Method: Get Table Color

```java
/**
 * Determines the color of a table based on its reservation/walk-in status for a specific date
 * 
 * Priority: Walk-ins > Reservations > Empty
 */
private Color getTableStatusForDate(String tableNo, LocalDate selectedDate, 
                                    java.util.Map<String, String> reservations, 
                                    java.util.Map<String, String> walkIns) {
    
    // PRIORITY 1: Check walk-ins first (they take precedence)
    if(walkIns.containsKey(tableNo)) {
        String status = walkIns.get(tableNo);
        if(status.equalsIgnoreCase("Seated")) {
            return new Color(102, 0, 0);        // Dark red for seated
        } else if(status.equalsIgnoreCase("Waiting")) {
            return new Color(212, 175, 55);     // Gold for waiting
        }
    }
    
    // PRIORITY 2: Check reservations
    if(reservations.containsKey(tableNo)) {
        String status = reservations.get(tableNo);
        if(status.equalsIgnoreCase("Reserved")) {
            return new Color(0, 153, 0);        // Green for reserved
        } else if(status.equalsIgnoreCase("Seated")) {
            return new Color(102, 0, 0);        // Dark red for seated
        } else if(status.equalsIgnoreCase("Waiting")) {
            return new Color(212, 175, 55);     // Gold for waiting
        }
    }
    
    // DEFAULT: Empty table
    return new Color(211, 211, 211);           // Light gray for empty
}
```

**Color Reference:**
| Status | Color | RGB |
|--------|-------|-----|
| Reserved | Green | (0, 153, 0) |
| Waiting | Gold | (212, 175, 55) |
| Seated | Dark Red | (102, 0, 0) |
| Empty | Light Gray | (211, 211, 211) |

---

## 7. How It All Connects

```
User Event Flow:
┌─────────────────────────────────────────┐
│  User selects date in JComboBox         │
│  (e.g., March 15, 2024)                 │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  ActionListener triggered               │
│  tableDateSelector.addActionListener()  │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  loadTablesByDate(LocalDate)            │
│  └─ Main orchestrator method            │
└──────────────┬──────────────────────────┘
               │
         ┌─────┴──────┐
         │            │
         ▼            ▼
    ┌─────────────┐  ┌──────────────────┐
    │ Get Res Data│  │ Get Walkin Data  │
    │ FROM SQL    │  │ FROM SQL         │
    └─────────────┘  └──────────────────┘
         │            │
         └─────┬──────┘
               │
               ▼
    ┌─────────────────────────┐
    │ For each table (T1-T14) │
    │ getTableStatusForDate() │
    │ ├─ Check walk-in        │
    │ ├─ Check reservation    │
    │ └─ Return Color         │
    └─────────────────────────┘
         │
         ▼
    ┌─────────────────────────┐
    │ Update UI               │
    │ ├─ Set table color      │
    │ ├─ Update counters      │
    │ └─ Repaint floor panel  │
    └─────────────────────────┘
```

---

## 8. Integration Points

### Where date selector connects to existing code:

1. **Constructor (existing)**
   - Already calls `createHeader()`
   - Now includes date selector initialization

2. **Event Listener (new)**
   - Calls `loadTablesByDate()` when date changes

3. **Existing Methods Used**
   - `Dbconnection.getConnection()` - DB connection
   - `detectStatus(Color)` - Maps color to status code
   - `updateCounters()` - Updates counter labels
   - `floorPanel.repaint()` - Redraws tables

4. **No Changes Needed In**
   - Database schema
   - `addTable()` method
   - `createTablesPage()` method
   - Sidebar or pages

---

## 9. Example Data Flow

**Scenario:** User selects March 15, 2024

**Database Query Results:**
```
Reservations:
┌────────┬──────────┐
│ table_no│ status   │
├────────┼──────────┤
│ T1     │ Reserved │
│ T2     │ Reserved │
│ T7     │ Seated   │
└────────┴──────────┘

Walk-ins:
┌────────┬──────────┐
│ table_no│ status   │
├────────┼──────────┤
│ T3     │ Waiting  │
│ T4     │ Seated   │
└────────┴──────────┘
```

**After Processing:**
```
tableReservations = {T1: "Reserved", T2: "Reserved", T7: "Seated"}
tableWalkIns = {T3: "Waiting", T4: "Seated"}

Table Colors Updated:
T1 → Green (from reservation)
T2 → Green (from reservation)
T3 → Gold (from walk-in)
T4 → Dark Red (from walk-in)
T5-T6 → Light Gray (empty)
T7 → Dark Red (from reservation)
T8-T14 → Light Gray (empty)

Counters Updated:
Reserved: 2
Waiting: 1
Seated: 2
Empty: 9
```

---

## 10. Debugging Tips

### To see what's happening in console:
```java
// Add this to loadTablesByDate() for debugging:
System.out.println("Selected Date: " + selectedDate);
System.out.println("Reservations: " + tableReservations);
System.out.println("Walk-ins: " + tableWalkIns);
```

### Check Java Map contents:
```java
// Print all entries
tableReservations.forEach((table, status) -> 
    System.out.println(table + " -> " + status)
);
```

### Verify colors are applied:
```java
System.out.println("Table color: " + table.getBackground());
```

