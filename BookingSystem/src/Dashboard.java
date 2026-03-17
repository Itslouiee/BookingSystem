import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
public class Dashboard extends JFrame {

    private JPanel sidebar;
    private JPanel header;

    private JLabel emptyLabel;
    private JLabel seatedLabel;
    private JLabel reservedLabel;
    private JLabel waitingLabel;

    // CardLayout for pages
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel tablesPage;
    private JPanel listPage;
    private JPanel reservationPage;
    private JTable walkinTable;
    private JComboBox<String> tableBox;
    private JTable reservationsTable;
    private JPanel calendarGrid;
    private JLabel monthYearLabel;
    private LocalDate currentCalendarDate;
    private LocalDate selectedReservationDate;
    private JTextField searchField;
    private JComboBox<String> dateFilterBox;
    private JButton addReservationBtn;

    // tables system
    private JPanel floorPanel;
    private ArrayList<JPanel> tables = new ArrayList<>();
    private ArrayList<Rectangle> originalBounds = new ArrayList<>();
    private ArrayList<Integer> tableStatus = new ArrayList<>();

    private int sidebarWidth = 150;
    private int headerHeight = 70;

    private int baseWidth = 780;
    private int baseHeight = 530;

    public Dashboard(){

        setTitle("Crimson Oak - Dashboard");
        setSize(1000,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        createSidebar();
        createHeader();

        // Create pages
        tablesPage = createTablesPage();
        listPage = createListPage();
        reservationPage = createReservationPage();

        // CardLayout setup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(tablesPage,"tables");
        mainPanel.add(listPage,"list");
        mainPanel.add(reservationPage,"rsv");

        mainPanel.setBounds(sidebarWidth,headerHeight,
                getWidth()-sidebarWidth,
                getHeight()-headerHeight);

        add(mainPanel);

        addComponentListener(new java.awt.event.ComponentAdapter(){
            public void componentResized(java.awt.event.ComponentEvent evt){

                sidebar.setBounds(0,0,sidebarWidth,getHeight());

                header.setBounds(sidebarWidth,0,
                        getWidth()-sidebarWidth,headerHeight);

                mainPanel.setBounds(sidebarWidth,headerHeight,
                        getWidth()-sidebarWidth,
                        getHeight()-headerHeight);

                resizeTables();
            }
        });

        updateCounters();

        setVisible(true);
    }

    private void createSidebar(){

        sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(new Color(51,51,51));
        sidebar.setBounds(0,0,sidebarWidth,getHeight());

        JLabel title = new JLabel("Crimson Oak");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial",Font.BOLD,14));
        title.setBounds(10,20,100,25);

        JLabel sub = new JLabel("Fine Dining");
        sub.setForeground(Color.LIGHT_GRAY);
        sub.setFont(new Font("Arial",Font.PLAIN,10));
        sub.setBounds(10,40,100,20);

        JButton tablesBtn = new JButton("Tables");
        tablesBtn.setBounds(10,120,130,40);

        JButton listBtn = new JButton("List");
        listBtn.setBounds(10,180,130,40);

        JButton reserveBtn = new JButton("RSV");
        reserveBtn.setBounds(10,240,130,40);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(10,480,130,40);

        // Sidebar button style
        JButton[] navButtons = {tablesBtn, listBtn, reserveBtn, logoutBtn};
        Color burgundy = new Color(128,0,32);
        Color ivory = new Color(255,255,240);
        for(JButton btn : navButtons){
            btn.setBackground(ivory);
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(burgundy, 2));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // Navigation actions
        tablesBtn.addActionListener(e -> cardLayout.show(mainPanel,"tables"));
        listBtn.addActionListener(e -> cardLayout.show(mainPanel,"list"));
        reserveBtn.addActionListener(e -> cardLayout.show(mainPanel,"rsv"));

        sidebar.add(title);
        sidebar.add(sub);
        sidebar.add(tablesBtn);
        sidebar.add(listBtn);
        sidebar.add(reserveBtn);
        sidebar.add(logoutBtn);

        add(sidebar);
    }

    private void createHeader(){

        header = new JPanel();
        header.setLayout(null);
        header.setBackground(new Color(102,0,32)); // burgundy
        header.setBounds(sidebarWidth,0,getWidth()-sidebarWidth,headerHeight);

        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE, dd MMM");

        JLabel date =
                new JLabel(LocalDate.now().format(format));

        date.setFont(new Font("Arial",Font.BOLD,18));
        date.setForeground(Color.WHITE);
        date.setBounds(30,20,250,30);

        reservedLabel = createCounter("Reserved",new Color(0,153,0),300); // green
        waitingLabel = createCounter("Waiting",new Color(212,175,55),450); // gold
        seatedLabel = createCounter("Seated",new Color(153,0,0),600); // darker red
        emptyLabel = createCounter("Empty",new Color(200,200,200),750); // light gray

        header.add(date);
        header.add(reservedLabel);
        header.add(waitingLabel);
        header.add(seatedLabel);
        header.add(emptyLabel);

        add(header);
    }

    private JPanel createTablesPage(){ 

        floorPanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                Color top = new Color(250,248,246);
                Color bottom = new Color(230,226,220);
                GradientPaint gradient = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
                g2.setPaint(gradient);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        floorPanel.setLayout(null);
        floorPanel.addComponentListener(new ComponentAdapter(){
         public void componentResized(ComponentEvent e){
        resizeTables();
    }
});

        addFamilyTable(floorPanel,"Table 1 (7-9)",80,100,Color.LIGHT_GRAY);
        addFamilyTable(floorPanel,"Table 2 (7-9)",80,250,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 3 (4-6)",250,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 4 (4-6)",250,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 5 (4-6)",250,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 6 (4-6)",400,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 7 (4-6)",400,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 8 (4-6)",400,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 9 (4-6)",550,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 10 (4-6)",550,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 11 (4-6)",550,300,Color.LIGHT_GRAY);

        addRoundTable(floorPanel,"Table 12 (1-3)",650,100,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 13 (1-3)",650,200,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 14 (1-3)",650,300,Color.LIGHT_GRAY);

        return floorPanel;
    }

    private JPanel createListPage(){

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(248,247,245));

    JLabel title = new JLabel("Walk-in Management",JLabel.CENTER);
    title.setFont(new Font("Serif",Font.BOLD,32));
    title.setForeground(new Color(102,0,32));
    title.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(248,247,245));
    topPanel.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
    topPanel.add(title, BorderLayout.NORTH);
    
    JButton addWalkinBtn = new JButton("+ Add Walk-in Customer");
    addWalkinBtn.setFont(new Font("Arial",Font.BOLD,13));
    addWalkinBtn.setBackground(new Color(102,0,32));
    addWalkinBtn.setForeground(Color.WHITE);
    addWalkinBtn.setFocusPainted(false);
    addWalkinBtn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
    addWalkinBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    addWalkinBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
            addWalkinBtn.setBackground(new Color(128,0,48));
        }
        public void mouseExited(java.awt.event.MouseEvent e) {
            addWalkinBtn.setBackground(new Color(102,0,32));
        }
    });
    
    addWalkinBtn.addActionListener(e -> {

    JTextField nameField = new JTextField();
    nameField.setFont(new Font("Arial",Font.PLAIN,13));
    nameField.setBorder(BorderFactory.createLineBorder(new Color(196,164,100),1));
    
    JTextField paxField = new JTextField();
    paxField.setFont(new Font("Arial",Font.PLAIN,13));
    paxField.setBorder(BorderFactory.createLineBorder(new Color(196,164,100),1));

    JComboBox<String> dateBox = new JComboBox<>();
    dateBox.setFont(new Font("Arial",Font.PLAIN,13));
    
    // Populate dates for next 7 days
    for(int i = 0; i < 7; i++) {
        LocalDate date = LocalDate.now().plusDays(i);
        dateBox.addItem(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
    }

    JComboBox<String> tableBoxPopup = new JComboBox<>();
    loadAvailableTables(tableBoxPopup);
    tableBoxPopup.setFont(new Font("Arial",Font.PLAIN,13));
    
    paxField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTablesWithDate(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTablesWithDate(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTablesWithDate(); }
        
        private void updateTablesWithDate() {
            try {
                int pax = Integer.parseInt(paxField.getText());
                String dateStr = (String) dateBox.getSelectedItem();
                LocalDate selectedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                loadAvailableTablesByPaxAndDate(tableBoxPopup, pax, selectedDate);
            } catch(Exception ex) {
                loadAvailableTables(tableBoxPopup);
            }
        }
    });
    
    dateBox.addActionListener(ee -> {
        try {
            int pax = Integer.parseInt(paxField.getText());
            String dateStr = (String) dateBox.getSelectedItem();
            LocalDate selectedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            loadAvailableTablesByPaxAndDate(tableBoxPopup, pax, selectedDate);
        } catch(Exception ex) {
            loadAvailableTables(tableBoxPopup);
        }
    });

    String[] status = {"Waiting","Seated"};
    JComboBox<String> statusBox = new JComboBox<>(status);
    statusBox.setFont(new Font("Arial",Font.PLAIN,13));

    JPanel form = new JPanel(new GridLayout(0,1,10,10));
    form.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
    form.setBackground(Color.WHITE);

    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setFont(new Font("Arial",Font.BOLD,12));
    form.add(nameLabel);
    form.add(nameField);

    JLabel paxLabel = new JLabel("Pax:");
    paxLabel.setFont(new Font("Arial",Font.BOLD,12));
    form.add(paxLabel);
    form.add(paxField);

    JLabel dateLabel = new JLabel("Date:");
    dateLabel.setFont(new Font("Arial",Font.BOLD,12));
    form.add(dateLabel);
    form.add(dateBox);

    JLabel tableLabel = new JLabel("Table:");
    tableLabel.setFont(new Font("Arial",Font.BOLD,12));
    form.add(tableLabel);
    form.add(tableBoxPopup);

    JLabel statusLabel = new JLabel("Status:");
    statusLabel.setFont(new Font("Arial",Font.BOLD,12));
    form.add(statusLabel);
    form.add(statusBox);

    int result = JOptionPane.showConfirmDialog(
            null,
            form,
            "Add Walk-in Customer",
            JOptionPane.OK_CANCEL_OPTION
    );

    if(result == JOptionPane.OK_OPTION){

        try{
            // Parse the selected date
            String dateStr = (String) dateBox.getSelectedItem();
            LocalDate walkInDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            
            String walkinTable = tableBoxPopup.getSelectedItem().toString();
            String walkinStatus = statusBox.getSelectedItem().toString();
            
            // Check if table is reserved on this date
            if(hasReservationForTable(walkInDate, walkinTable)) {
                JOptionPane.showMessageDialog(null, "Table " + walkinTable + " has a reservation on " + walkInDate.format(DateTimeFormatter.ofPattern("MMM dd")) + ". Please choose another table.");
                return;
            }

            Connection conn = Dbconnection.getConnection();

            String sql = "INSERT INTO walkin(name,pax,walk_in_date,table_no,status) VALUES(?,?,?,?,?)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1,nameField.getText());
            pst.setInt(2,Integer.parseInt(paxField.getText()));
            pst.setDate(3, java.sql.Date.valueOf(walkInDate));
            pst.setString(4, walkinTable);
            pst.setString(5, walkinStatus);

            pst.executeUpdate();

            loadWalkinData();
            updateTableFromWalkin(walkinTable, walkinStatus);
            if(tableBox != null){
                loadAvailableTables(tableBox);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

});

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
    searchPanel.setBackground(new Color(248,247,245));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10,30,10,10));
    
    JLabel searchLabel = new JLabel("Search:");
    searchLabel.setFont(new Font("Arial",Font.BOLD,13));
    searchLabel.setForeground(new Color(102,0,32));
    
    searchField = new JTextField(15);
    searchField.setFont(new Font("Arial",Font.PLAIN,13));
    searchField.setBorder(BorderFactory.createLineBorder(new Color(102,0,32),1));
    searchField.setPreferredSize(new Dimension(150,34));
    searchField.setBackground(new Color(255,255,255));

    JLabel dateFilterLabel = new JLabel("Date:");
    dateFilterLabel.setFont(new Font("Arial",Font.BOLD,13));
    dateFilterLabel.setForeground(new Color(102,0,32));
    
    dateFilterBox = new JComboBox<>();
    dateFilterBox.setFont(new Font("Arial",Font.PLAIN,13));
    dateFilterBox.setPreferredSize(new Dimension(200,34));
    
    // Populate dates
    for(int i = 0; i < 7; i++) {
        LocalDate date = LocalDate.now().plusDays(i);
        dateFilterBox.addItem(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd")));
    }
    
    searchPanel.add(searchLabel);
    searchPanel.add(searchField);
    searchPanel.add(dateFilterLabel);
    searchPanel.add(dateFilterBox);

    topPanel.add(searchPanel, BorderLayout.CENTER);

    JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
    topBar.setBackground(new Color(245,242,235));
    topBar.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
    topBar.add(addWalkinBtn);

    String[] columns = {"ID","Name","Pax","Date","Table","Status","Edit"};

    DefaultTableModel model = new DefaultTableModel(columns,0);

    walkinTable = new JTable(model);
    walkinTable.getColumnModel().getColumn(0).setMinWidth(0);
    walkinTable.getColumnModel().getColumn(0).setMaxWidth(0);
    walkinTable.getColumnModel().getColumn(0).setWidth(0);

    walkinTable.setRowHeight(32);
    walkinTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    walkinTable.setFillsViewportHeight(true);
    walkinTable.setFont(new Font("Arial",Font.PLAIN,12));
    walkinTable.setSelectionBackground(new Color(102,0,32));
    walkinTable.setSelectionForeground(Color.WHITE);
    walkinTable.setGridColor(new Color(230,230,230));
    walkinTable.setShowHorizontalLines(true);
    walkinTable.setShowVerticalLines(false);
    
    walkinTable.getTableHeader().setBackground(new Color(102,0,32));
    walkinTable.getTableHeader().setForeground(Color.WHITE);
    walkinTable.getTableHeader().setFont(new Font("Serif",Font.BOLD,13));
    walkinTable.getTableHeader().setPreferredSize(new Dimension(0,35));

    walkinTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
    walkinTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(),walkinTable));

    JScrollPane scroll = new JScrollPane(walkinTable);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(102,0,32),1));
    scroll.setViewportBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    
    // Add document listener for search
    searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
        public void insertUpdate(javax.swing.event.DocumentEvent e){ filterWalkinTable(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e){ filterWalkinTable(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e){ filterWalkinTable(); }
    });

    dateFilterBox.addActionListener(e -> filterWalkinTable());

        // Add the search panel to the top area
        topPanel.add(searchPanel, BorderLayout.NORTH);

    JPanel centerWrapper = new JPanel(new GridBagLayout());
    centerWrapper.setBackground(new Color(245,242,235));
    centerWrapper.setBorder(BorderFactory.createEmptyBorder(20,30,30,30));

    JPanel content = new JPanel(new BorderLayout(0,15));
    content.setBackground(Color.WHITE);
    content.setBorder(BorderFactory.createLineBorder(new Color(196,164,100),2));
    topPanel.add(topBar,BorderLayout.SOUTH);
    content.add(scroll,BorderLayout.CENTER);

    centerWrapper.add(content, new GridBagConstraints());

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(centerWrapper,BorderLayout.CENTER);

    loadWalkinData();

    return panel;
}

    private JPanel createReservationPage(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248,247,245));

        JLabel title = new JLabel("Reservation Management",JLabel.CENTER);
        title.setFont(new Font("Serif",Font.BOLD,32));
        title.setForeground(new Color(102,0,32));
        title.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(248,247,245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
        topPanel.add(title, BorderLayout.NORTH);
        
        addReservationBtn = new JButton("+ Add Reservation");
        addReservationBtn.setFont(new Font("Arial",Font.BOLD,13));
        addReservationBtn.setBackground(new Color(102,0,32));
        addReservationBtn.setForeground(Color.WHITE);
        addReservationBtn.setFocusPainted(false);
        addReservationBtn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        addReservationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addReservationBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                addReservationBtn.setBackground(new Color(128,0,48));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                addReservationBtn.setBackground(new Color(102,0,32));
            }
        });

        addReservationBtn.addActionListener(e -> showAddReservationDialog());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
        topBar.setBackground(new Color(245,242,235));
        topBar.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
        topBar.add(addReservationBtn);
        topPanel.add(topBar, BorderLayout.SOUTH);

        // Calendar Panel
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(BorderFactory.createLineBorder(new Color(102,0,32),2));

        // Month/Year selector
        JPanel monthPanel = new JPanel(new FlowLayout());
        monthPanel.setBackground(Color.WHITE);

        JButton prevMonth = new JButton("◀");
        prevMonth.setFont(new Font("Arial",Font.BOLD,16));
        prevMonth.setBackground(new Color(102,0,32));
        prevMonth.setForeground(Color.WHITE);
        prevMonth.setFocusPainted(false);
        prevMonth.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        JButton nextMonth = new JButton("▶");
        nextMonth.setFont(new Font("Arial",Font.BOLD,16));
        nextMonth.setBackground(new Color(102,0,32));
        nextMonth.setForeground(Color.WHITE);
        nextMonth.setFocusPainted(false);
        nextMonth.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        currentCalendarDate = LocalDate.now();
        monthYearLabel = new JLabel(currentCalendarDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")), JLabel.CENTER);
        monthYearLabel.setFont(new Font("Serif",Font.BOLD,18));
        monthYearLabel.setForeground(new Color(102,0,32));

        monthPanel.add(prevMonth);
        monthPanel.add(monthYearLabel);
        monthPanel.add(nextMonth);

        calendarPanel.add(monthPanel, BorderLayout.NORTH);

        // Calendar grid
        calendarGrid = new JPanel(new GridLayout(0,7));
        calendarGrid.setBackground(Color.WHITE);
        updateCalendar();

        calendarPanel.add(calendarGrid, BorderLayout.CENTER);

        // Reservations list for selected date
        String[] resColumns = {"Time","Name","Guests","Table","Status"};
        DefaultTableModel resModel = new DefaultTableModel(resColumns,0);
        reservationsTable = new JTable(resModel);
        reservationsTable.setRowHeight(30);
        reservationsTable.setFont(new Font("Arial",Font.PLAIN,12));
        reservationsTable.setSelectionBackground(new Color(102,0,32));
        reservationsTable.setSelectionForeground(Color.WHITE);
        reservationsTable.setGridColor(new Color(230,230,230));
        reservationsTable.setShowHorizontalLines(true);
        reservationsTable.setShowVerticalLines(false);

        reservationsTable.getTableHeader().setBackground(new Color(102,0,32));
        reservationsTable.getTableHeader().setForeground(Color.WHITE);
        reservationsTable.getTableHeader().setFont(new Font("Serif",Font.BOLD,13));
        reservationsTable.getTableHeader().setPreferredSize(new Dimension(0,35));

        JScrollPane resScroll = new JScrollPane(reservationsTable);
        resScroll.setBorder(BorderFactory.createLineBorder(new Color(102,0,32),1));

        JPanel reservationsPanel = new JPanel(new BorderLayout());
        reservationsPanel.setBackground(Color.WHITE);
        reservationsPanel.add(resScroll, BorderLayout.CENTER);

        availabilityLabel = new JLabel("Available tables: ");
        availabilityLabel.setFont(new Font("Arial",Font.BOLD,12));
        availabilityLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        reservationsPanel.add(availabilityLabel, BorderLayout.SOUTH);

        // Add action listeners
        prevMonth.addActionListener(e -> {
            currentCalendarDate = currentCalendarDate.minusMonths(1);
            updateCalendar();
        });

        nextMonth.addActionListener(e -> {
            currentCalendarDate = currentCalendarDate.plusMonths(1);
            updateCalendar();
        });

        // Main content
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245,242,235));
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20,30,30,30));

        JPanel content = new JPanel(new GridLayout(1,2,20,0));
        content.setBackground(new Color(245,242,235));
        content.add(calendarPanel);
        content.add(reservationsPanel);

        centerWrapper.add(content, new GridBagConstraints());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerWrapper,BorderLayout.CENTER);

        selectReservationDate(LocalDate.now());

        return panel;
    }

    private void updateCalendar() {
        calendarGrid.removeAll();
        
        monthYearLabel.setText(currentCalendarDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        // Day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for(String day : days) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setForeground(new Color(102,0,32));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
            calendarGrid.add(dayLabel);
        }
        
        // Get first day of month and last day
        LocalDate firstOfMonth = currentCalendarDate.withDayOfMonth(1);
        LocalDate lastOfMonth = currentCalendarDate.withDayOfMonth(currentCalendarDate.lengthOfMonth());
        
        // Start from Sunday of the week containing first of month
        LocalDate startDate = firstOfMonth.minusDays(firstOfMonth.getDayOfWeek().getValue() % 7);
        
        for(int i = 0; i < 42; i++) { // 6 weeks * 7 days
            LocalDate date = startDate.plusDays(i);
            JButton dayButton = new JButton(String.valueOf(date.getDayOfMonth()));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 12));
            dayButton.setFocusPainted(false);
            dayButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            
            if(date.getMonth() != currentCalendarDate.getMonth()) {
                dayButton.setForeground(Color.GRAY);
                dayButton.setEnabled(false);
            } else if(date.equals(LocalDate.now())) {
                dayButton.setBackground(new Color(102,0,32));
                dayButton.setForeground(Color.WHITE);
                LocalDate selectedDate = date;
                dayButton.addActionListener(e -> {
                    selectReservationDate(selectedDate);
                });
            } else if(date.isBefore(LocalDate.now())) {
                dayButton.setEnabled(false);
                dayButton.setForeground(Color.GRAY);
            } else {
                dayButton.setBackground(Color.WHITE);
                LocalDate selectedDate = date;
                dayButton.addActionListener(e -> {
                    selectReservationDate(selectedDate);
                });
            }
            
            calendarGrid.add(dayButton);
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private JLabel availabilityLabel;

    private void selectReservationDate(LocalDate date) {
        loadReservationsForDate(date);
        updateAvailabilityLabel(date);
    }

    private void updateAvailabilityLabel(LocalDate date) {
        selectedReservationDate = date;
        java.util.List<String> available = getAvailableTablesForDate(date);
        if(available.isEmpty()) {
            availabilityLabel.setText("Fully booked for " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            if(addReservationBtn != null) {
                addReservationBtn.setEnabled(false);
            }
        } else {
            availabilityLabel.setText("Available tables: " + String.join(", ", available));
            if(addReservationBtn != null) {
                addReservationBtn.setEnabled(true);
            }
        }
    }

    private java.util.List<String> getAvailableTablesForDate(LocalDate date) {
        java.util.List<String> available = new java.util.ArrayList<>();
        try {
            Connection conn = Dbconnection.getConnection();
            String sql = "SELECT table_no FROM reservations WHERE DATE(reservation_date)=? AND status!='Cancelled'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            java.util.Set<String> occupied = new java.util.HashSet<>();
            while(rs.next()) {
                occupied.add(rs.getString("table_no"));
            }
            for(int i = 1; i <= 14; i++) {
                String table = "T" + i;
                if(!occupied.contains(table)) {
                    available.add(table);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return available;
    }

    private void loadReservationsForDate(LocalDate date) {
        try {
            DefaultTableModel model = (DefaultTableModel) reservationsTable.getModel();
            model.setRowCount(0);
            
            Connection conn = Dbconnection.getConnection();
            String sql = "SELECT reservation_time, customer_name, guests, table_no, status FROM reservations WHERE DATE(reservation_date) = ? ORDER BY reservation_time";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("reservation_time"),
                    rs.getString("customer_name"),
                    rs.getInt("guests"),
                    rs.getString("table_no"),
                    rs.getString("status")
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddReservationDialog() {
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial",Font.PLAIN,13));
        
        JTextField guestsField = new JTextField();
        guestsField.setFont(new Font("Arial",Font.PLAIN,13));
        
        String[] timeSlots = new String[] {
            "10:00 AM - 1:00 PM",
            "1:00 PM - 4:00 PM", 
            "4:00 PM - 7:00 PM",
            "7:00 PM - 10:00 PM"};

        JComboBox<String> timeBox = new JComboBox<>(timeSlots);
        timeBox.setFont(new Font("Arial",Font.PLAIN,13));

        JComboBox<String> dateBox = new JComboBox<>();
        dateBox.setFont(new Font("Arial",Font.PLAIN,13));
        
        // Populate dates for next 7 days
        for(int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            dateBox.addItem(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        }
        
        JComboBox<String> tableBoxPopup = new JComboBox<>();
        loadAvailableTables(tableBoxPopup);
        tableBoxPopup.setFont(new Font("Arial",Font.PLAIN,13));
        
        // Update available tables based on pax/date/time
        Runnable updateTables = () -> {
            try {
                int guests = Integer.parseInt(guestsField.getText());
                String dateStr = (String) dateBox.getSelectedItem();
                LocalDate walkinDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                String time = (String) timeBox.getSelectedItem();
                loadAvailableTablesByPaxAndDateTime(tableBoxPopup, guests, walkinDate, time);
            } catch(Exception ex) {
                loadAvailableTables(tableBoxPopup);
            }
        };

        guestsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTables.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTables.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTables.run(); }
        });

        dateBox.addActionListener(e -> updateTables.run());
        timeBox.addActionListener(e -> updateTables.run());
        
        JPanel form = new JPanel(new GridLayout(0,1,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        form.setBackground(Color.WHITE);
        
        form.add(new JLabel("Customer Name:"));
        form.add(nameField);
        form.add(new JLabel("Number of Guests:"));
        form.add(guestsField);
        form.add(new JLabel("Reservation Date:"));
        form.add(dateBox);
        form.add(new JLabel("Time:"));
        form.add(timeBox);
        form.add(new JLabel("Table:"));
        form.add(tableBoxPopup);
        
        int result = JOptionPane.showConfirmDialog(
            null,
            form,
            "Add Reservation",
            JOptionPane.OK_CANCEL_OPTION
        );
        
        if(result == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Dbconnection.getConnection();
                String sql = "INSERT INTO reservations(customer_name, guests, reservation_date, reservation_time, table_no, status) VALUES(?,?,?,?,?,'Confirmed')";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setInt(2, Integer.parseInt(guestsField.getText()));
                
                // Parse the selected date
                String dateStr = (String) dateBox.getSelectedItem();
                LocalDate resDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                
                pst.setDate(3, java.sql.Date.valueOf(resDate));

                String selectedTable = tableBoxPopup.getSelectedItem().toString();
                String selectedTime = (String) timeBox.getSelectedItem();

                if(isTableBooked(resDate, selectedTime, selectedTable)) {
                    JOptionPane.showMessageDialog(null, "Table " + selectedTable + " is already booked at " + selectedTime + ". Please choose another time or table.");
                    return;
                }

                pst.setString(4, selectedTime);
                pst.setString(5, selectedTable);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Reservation added successfully!");
                loadReservationsForDate(resDate);
                updateTableFromWalkin(selectedTable, "Reserved");
                updateAvailabilityLabel(resDate);
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding reservation!\n" + e.getMessage());
            }
        }
    }

    private boolean isTableBooked(LocalDate date, String time, String tableNo) {
        try {
            Connection conn = Dbconnection.getConnection();
            String sql = "SELECT COUNT(*) FROM reservations WHERE DATE(reservation_date) = ? AND reservation_time = ? AND table_no = ? AND status != 'Cancelled'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(date));
            pst.setString(2, time);
            pst.setString(3, tableNo);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private JLabel createCounter(String name,Color color,int x){

        JLabel box = new JLabel(name+" 0",SwingConstants.CENTER);
        box.setOpaque(true);
        box.setBackground(color);
        box.setForeground(Color.WHITE);
        box.setFont(new Font("Arial",Font.BOLD,14));
        box.setBounds(x,15,120,35);

        return box;
    }

    private void updateCounters(){

        int empty=0,seated=0,reserved=0,waiting=0;

        for(int s:tableStatus){

            if(s==0) empty++;
            if(s==1) reserved++;
            if(s==2) waiting++;
            if(s==3) seated++;
        }

        emptyLabel.setText("Empty "+empty);
        reservedLabel.setText("Reserved "+reserved);
        waitingLabel.setText("Waiting "+waiting);
        seatedLabel.setText("Seated "+seated);
    }

    private void resizeTables() {

    if(floorPanel.getWidth() == 0 || floorPanel.getHeight() == 0) return;

    double scaleX = floorPanel.getWidth() / (double) baseWidth;
    double scaleY = floorPanel.getHeight() / (double) baseHeight;

    double scale = Math.min(scaleX, scaleY);

    int newWidth = (int)(baseWidth * scale);
    int newHeight = (int)(baseHeight * scale);

    int offsetX = (floorPanel.getWidth() - newWidth) / 2;
    int offsetY = (floorPanel.getHeight() - newHeight) / 2;

    for(int i = 0; i < tables.size(); i++){

        Rectangle r = originalBounds.get(i);

        int x = (int)(r.x * scale) + offsetX;
        int y = (int)(r.y * scale) + offsetY;
        int w = (int)(r.width * scale);
        int h = (int)(r.height * scale);

        tables.get(i).setBounds(x,y,w,h);
    }

    floorPanel.repaint();
}

    private void showStatusMenu(JPanel table,int index,int x,int y){

        JPopupMenu menu=new JPopupMenu();

        JMenuItem reserved=new JMenuItem("Reserved");
        JMenuItem waiting=new JMenuItem("Waiting");
        JMenuItem seated=new JMenuItem("Seated");
        JMenuItem empty=new JMenuItem("Empty");

        reserved.addActionListener(e->{
            tableStatus.set(index,1);
            table.setBackground(new Color(0,153,0));
            updateCounters();
        });

        waiting.addActionListener(e->{
            tableStatus.set(index,2);
            table.setBackground(new Color(212,175,55));
            updateCounters();
        });

        seated.addActionListener(e->{
            tableStatus.set(index,3);
            table.setBackground(Color.RED);
            updateCounters();
        });

        empty.addActionListener(e->{
            tableStatus.set(index,0);
            table.setBackground(Color.LIGHT_GRAY);
            updateCounters();
        });

        menu.add(reserved);
        menu.add(waiting);
        menu.add(seated);
        menu.add(empty);

        menu.show(table,x,y);
    }

    private int detectStatus(Color color){
        if(color.equals(new Color(0,153,0))) return 1;
        if(color.equals(new Color(212,175,55))) return 2;
        if(color.equals(new Color(102,0,0))) return 3;
        return 0;
    }

    private void addTable(JPanel panel,String name,int x,int y,Color color){

        JPanel table=new JPanel();
        table.setLayout(new BorderLayout());
        table.setBackground(color);
        table.setBounds(x,y,80,60);
        table.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));

        JLabel label=new JLabel(name,JLabel.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Serif",Font.BOLD,13));

        table.add(label,BorderLayout.SOUTH);

        int index=tables.size();

        panel.add(table);

        table.addMouseListener(new java.awt.event.MouseAdapter(){

    public void mouseEntered(java.awt.event.MouseEvent e){
        table.setBorder(BorderFactory.createLineBorder(new Color(102,0,32),3));
    }

    public void mouseExited(java.awt.event.MouseEvent e){
        table.setBorder(BorderFactory.createLineBorder(new Color(80,80,80),2));
    }

});

        tables.add(table);
        tableStatus.add(detectStatus(color));
        originalBounds.add(new Rectangle(x,y,80,60));
    }

    private void addRoundTable(JPanel panel,String name,int x,int y,Color color){

        JPanel table=new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillOval(0,0,getWidth(),getHeight());
            }
        };

        table.setLayout(new BorderLayout());
        table.setBounds(x,y,70,70);
        table.setBackground(color);
        table.setOpaque(false);

        JLabel label=new JLabel(name,JLabel.CENTER);
        label.setForeground(Color.WHITE);

        table.add(label,BorderLayout.CENTER);

        int index=tables.size();

        

        panel.add(table);

        tables.add(table);
        tableStatus.add(detectStatus(color));
        originalBounds.add(new Rectangle(x,y,70,70));
    }

    private void addFamilyTable(JPanel panel,String name,int x,int y,Color color){

        JPanel table=new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),60,60);
            }
        };

        table.setLayout(new BorderLayout());
        table.setBounds(x,y,100,60);
        table.setBackground(color);
        table.setOpaque(false);

        JLabel label=new JLabel(name,JLabel.CENTER);
        label.setForeground(Color.WHITE);

        table.add(label,BorderLayout.CENTER);

        int index=tables.size();

        panel.add(table);

        tables.add(table);
        tableStatus.add(detectStatus(color));
        originalBounds.add(new Rectangle(x,y,100,60));
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {

    public ButtonRenderer(){
        setText("Edit");
    }

    public Component getTableCellRendererComponent(
            JTable table,Object value,boolean isSelected,
            boolean hasFocus,int row,int column){

        setText("Edit");
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private JTable table;
    private int row;

    public ButtonEditor(JCheckBox checkBox,JTable table){
        super(checkBox);
        this.table=table;

        button=new JButton("Edit");

        button.addActionListener(e->{
            fireEditingStopped();
            String[] options={"Waiting","Seated","Done"};

            String status=(String)JOptionPane.showInputDialog(
                    null,
                    "Change Status",
                    "Edit Status",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if(status!=null){

    try{

        Connection conn = Dbconnection.getConnection();

        int id = Integer.parseInt(table.getValueAt(row,0).toString());

        if(status.equals("Done")){

            String sql = "DELETE FROM walkin WHERE id=?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1,id);
            pst.executeUpdate();

        }else{

            String sql = "UPDATE walkin SET status=? WHERE id=?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1,status);
            pst.setInt(2,id);
            pst.executeUpdate();

        }

    }catch(Exception ex){
        ex.printStackTrace();
    }

    String tableNo = table.getValueAt(row,4).toString();

    updateTableFromWalkin(tableNo,status);

    loadWalkinData();
    loadAvailableTables(tableBox);
}
        });
    }

    public Component getTableCellEditorComponent(
            JTable table,Object value,boolean isSelected,
            int row,int column){

        this.row=row;
        return button;
    }

    public Object getCellEditorValue(){
        return "Notes";
    }
}

    private void loadWalkinData(){

    try{

        Connection conn = Dbconnection.getConnection();

        String sql = "SELECT * FROM walkin ORDER BY walk_in_date DESC, id DESC";

        PreparedStatement pst = conn.prepareStatement(sql);

        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) walkinTable.getModel();

        model.setRowCount(0);

        while(rs.next()){

            String name = rs.getString("name");
            int pax = rs.getInt("pax");
            LocalDate walkInDate = rs.getDate("walk_in_date").toLocalDate();
            String tableNo = rs.getString("table_no");
            String status = rs.getString("status");

            model.addRow(new Object[]{
            rs.getInt("id"),
            name,
            pax,
            walkInDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            tableNo,
            status,
            "Edit"
                });

            updateTableFromWalkin(tableNo,status);
        }

    }catch(Exception e){
        e.printStackTrace();
    }
}

    private void filterWalkinTable(){
        DefaultTableModel model = (DefaultTableModel) walkinTable.getModel();
        model.setRowCount(0);
        
        loadWalkinData();
        
        String searchText = ""; // searchField.getText().toLowerCase().trim();
        if(searchField != null) {
            searchText = searchField.getText().toLowerCase().trim();
        }
        
        String selectedDate = "";
        if(dateFilterBox != null) {
            selectedDate = (String) dateFilterBox.getSelectedItem();
        }

        for(int i = model.getRowCount() - 1; i >= 0; i--){
            boolean matchesSearch = true;
            boolean matchesDate = true;
            
            if(!searchText.isEmpty()) {
                String name = model.getValueAt(i, 1).toString().toLowerCase();
                String table = model.getValueAt(i, 4).toString().toLowerCase();
                String status = model.getValueAt(i, 5).toString().toLowerCase();
                matchesSearch = name.contains(searchText) || table.contains(searchText) || status.contains(searchText);
            }
            
            if(selectedDate != null && !selectedDate.isEmpty()) {
                String date = model.getValueAt(i, 3).toString();
                matchesDate = date.contains(selectedDate.split(",")[0]); // Match month and day
            }

            if(!matchesSearch || !matchesDate){
                model.removeRow(i);
            }
        }
    }

   private void updateTableFromWalkin(String tableNo,String status){

    int index = Integer.parseInt(tableNo.replace("T","")) - 1;

    if(index >=0 && index < tables.size()){

        JPanel table = tables.get(index);

        if(status.equalsIgnoreCase("Reserved")){
            table.setBackground(new Color(0,153,0)); // green
            tableStatus.set(index,1);
        }

        if(status.equalsIgnoreCase("Waiting")){
            table.setBackground(new Color(212,175,55)); // gold
            tableStatus.set(index,2);
        }

        if(status.equalsIgnoreCase("Seated")){
            table.setBackground(new Color(102,0,0)); // wine red
            tableStatus.set(index,3);
        }

        if(status.equalsIgnoreCase("Done")){
            table.setBackground(Color.LIGHT_GRAY);
            tableStatus.set(index,0);
        }

        updateCounters();
    }
}

 private void loadAvailableTables(JComboBox<String> tableBox){

    if(tableBox == null){
        return;
    }

    loadAvailableTablesByPax(tableBox, -1);
}

private void loadAvailableTablesByPax(JComboBox<String> tableBox, int pax){
    loadAvailableTablesByPaxAndDateTime(tableBox, pax, LocalDate.now(), null);
}

private void loadAvailableTablesByPaxAndDate(JComboBox<String> tableBox, int pax, LocalDate date) {
    loadAvailableTablesByPaxAndDateTime(tableBox, pax, date, null);
}

private void loadAvailableTablesByPaxAndDateTime(JComboBox<String> tableBox, int pax, LocalDate date, String time){

    if(tableBox == null){
        return;
    }

    try{

        Connection conn = Dbconnection.getConnection();

        // Get walk-ins for the selected date
        String walkinSql = "SELECT table_no FROM walkin WHERE DATE(walk_in_date) = ? AND status != 'Done'";
        PreparedStatement walkinPst = conn.prepareStatement(walkinSql);
        walkinPst.setDate(1, java.sql.Date.valueOf(date));
        ResultSet walkinRs = walkinPst.executeQuery();

        ArrayList<String> occupied = new ArrayList<>();

        while(walkinRs.next()){
            occupied.add(walkinRs.getString("table_no"));
        }
        
        // Get reservations for the selected date (and time if provided)
        String resSql = "SELECT table_no FROM reservations WHERE DATE(reservation_date) = ? AND status != 'Cancelled'" + (time != null ? " AND reservation_time = ?" : "");
        PreparedStatement resPst = conn.prepareStatement(resSql);
        resPst.setDate(1, java.sql.Date.valueOf(date));
        if(time != null) {
            resPst.setString(2, time);
        }
        ResultSet resRs = resPst.executeQuery();

        while(resRs.next()){
            occupied.add(resRs.getString("table_no"));
        }

        tableBox.removeAllItems();

        for(int i=1;i<=14;i++){

            String table = "T"+i;
            
            // Filter tables based on pax
            boolean matchesPax = true;
            if(pax > 0) {
                if(pax >= 7 && pax <= 9 && (i < 1 || i > 2)) {
                    matchesPax = false;  // Only tables 1-2 for 7-9 pax
                } else if(pax >= 4 && pax <= 6 && (i < 3 || i > 11)) {
                    matchesPax = false;  // Only tables 3-11 for 4-6 pax
                } else if(pax >= 1 && pax <= 3 && (i < 12 || i > 14)) {
                    matchesPax = false;  // Only tables 12-14 for 1-3 pax
                } else if(pax > 9) {
                    matchesPax = false;  // No suitable table
                } else if(pax < 1) {
                    matchesPax = false;
                }
            }

            if(!occupied.contains(table) && matchesPax){
                tableBox.addItem(table);
            }
        }

    }catch(Exception e){
        e.printStackTrace();
    }
}

private void filterTable(String query) {
    filterWalkinTable();
}

    public static void main(String[] args){
        new Dashboard();
    }

}