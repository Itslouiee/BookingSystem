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

    // CardLayout for pages
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel tablesPage;
    private JPanel listPage;
    private JPanel reservationPage;
    private JTable walkinTable;
    private JComboBox<String> tableBox;

    // tables system
    private JPanel floorPanel;
    private ArrayList<JPanel> tables = new ArrayList<>();
    private ArrayList<Rectangle> originalBounds = new ArrayList<>();
    private ArrayList<Integer> tableStatus = new ArrayList<>();

    private int sidebarWidth = 120;
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
        sidebar.setBackground(new Color(30,30,30));
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
        tablesBtn.setBounds(10,120,100,40);

        JButton listBtn = new JButton("List");
        listBtn.setBounds(10,180,100,40);

        JButton reserveBtn = new JButton("RSV");
        reserveBtn.setBounds(10,240,100,40);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(10,480,100,40);

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
        header.setBackground(new Color(196,164,100)); // champagne gold
        header.setBounds(sidebarWidth,0,getWidth()-sidebarWidth,headerHeight);

        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE, dd MMM");

        JLabel date =
                new JLabel(LocalDate.now().format(format));

        date.setFont(new Font("Arial",Font.BOLD,18));
        date.setBounds(30,20,250,30);

        reservedLabel = createCounter("Reserved",Color.GREEN,300);
        seatedLabel = createCounter("Seated",new Color(128,0,32),450);
        emptyLabel = createCounter("Empty",Color.LIGHT_GRAY,600);

        header.add(date);
        header.add(reservedLabel);
        header.add(seatedLabel);
        header.add(emptyLabel);

        add(header);
    }

    private JPanel createTablesPage(){

        floorPanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.setColor(new Color(214,193,146)); // warm gold floor
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };

        floorPanel.setLayout(null);
        floorPanel.addComponentListener(new ComponentAdapter(){
         public void componentResized(ComponentEvent e){
        resizeTables();
    }
});

        addFamilyTable(floorPanel,"Table 1",80,100,Color.LIGHT_GRAY);
        addFamilyTable(floorPanel,"Table 2",80,250,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 3",250,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 4",250,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 5",250,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 6",400,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 7",400,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 8",400,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 9",550,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 10",550,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 11",550,300,Color.LIGHT_GRAY);

        addRoundTable(floorPanel,"Table 12",650,100,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 13",650,200,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 14",650,300,Color.LIGHT_GRAY);

        return floorPanel;
    }

    private JPanel createListPage(){

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(214,193,146));

    JLabel title = new JLabel("Walk-in List",JLabel.CENTER);
    JButton addWalkinBtn = new JButton("+ Add Walk-in Customer");
    addWalkinBtn.addActionListener(e -> {

    JTextField nameField = new JTextField();
    JTextField paxField = new JTextField();

    JComboBox<String> tableBoxPopup = new JComboBox<>();
    loadAvailableTables(tableBoxPopup);

    String[] status = {"Waiting","Seated"};
    JComboBox<String> statusBox = new JComboBox<>(status);

    JPanel form = new JPanel(new GridLayout(0,1));

    form.add(new JLabel("Name"));
    form.add(nameField);

    form.add(new JLabel("Pax"));
    form.add(paxField);

    form.add(new JLabel("Table"));
    form.add(tableBoxPopup);

    form.add(new JLabel("Status"));
    form.add(statusBox);

    int result = JOptionPane.showConfirmDialog(
            null,
            form,
            "Add Walk-in Customer",
            JOptionPane.OK_CANCEL_OPTION
    );

    if(result == JOptionPane.OK_OPTION){

        try{

            Connection conn = Dbconnection.getConnection();

            String sql = "INSERT INTO walkin(name,pax,table_no,status) VALUES(?,?,?,?)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1,nameField.getText());
            pst.setInt(2,Integer.parseInt(paxField.getText()));
            pst.setString(3,tableBoxPopup.getSelectedItem().toString());
            pst.setString(4,statusBox.getSelectedItem().toString());

            pst.executeUpdate();

            loadWalkinData();
            if(tableBox != null){
    loadAvailableTables(tableBox);
}

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

});

    JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topBar.setBorder(BorderFactory.createEmptyBorder(10,40,10,40));
    topBar.add(addWalkinBtn);
    title.setFont(new Font("Arial",Font.BOLD,20));

    String[] columns = {"ID","Name","Pax","Table","Status","Edit"};

    DefaultTableModel model = new DefaultTableModel(columns,0);

    walkinTable = new JTable(model);
    walkinTable.getColumnModel().getColumn(0).setMinWidth(0);
    walkinTable.getColumnModel().getColumn(0).setMaxWidth(0);
    walkinTable.getColumnModel().getColumn(0).setWidth(0);

    walkinTable.setRowHeight(28);
    walkinTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    walkinTable.setFillsViewportHeight(true);

    walkinTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
    walkinTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(),walkinTable));

    walkinTable.setShowHorizontalLines(false);
    walkinTable.setGridColor(new Color(214,193,146));
    walkinTable.getTableHeader().setFont(new Font("Serif",Font.BOLD,14));

    JScrollPane scroll = new JScrollPane(walkinTable);
    scroll.setBorder(BorderFactory.createEmptyBorder(20,40,40,40));

    JPanel topPanel = new JPanel(new BorderLayout());

    panel.add(topPanel,BorderLayout.NORTH);

    JPanel centerWrapper = new JPanel(new GridBagLayout());
    centerWrapper.setBackground(new Color(214,193,146));

    JPanel content = new JPanel(new BorderLayout(10,20));
    content.setMaximumSize(new Dimension(900,500));
    content.setMinimumSize(new Dimension(600,300));

    topPanel.add(title,BorderLayout.NORTH);
    topPanel.add(topBar,BorderLayout.SOUTH);
    content.add(scroll,BorderLayout.CENTER);

    centerWrapper.add(content, new GridBagConstraints());

    panel.add(centerWrapper,BorderLayout.CENTER);



    loadWalkinData();

    return panel;
}

    private JPanel createReservationPage(){

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Reservations",JLabel.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,20));

        String[] columns = {"Name","Guests","Time","Table"};
        String[][] data = {
                {"Michael Tan","4","7:00 PM","T5"},
                {"Sofia Lim","2","6:30 PM","T2"}
        };

        JTable table = new JTable(data,columns);
        JScrollPane scroll = new JScrollPane(table);

        panel.add(title,BorderLayout.NORTH);
        panel.add(scroll,BorderLayout.CENTER);

        return panel;
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

        int empty=0,seated=0,reserved=0;

        for(int s:tableStatus){

            if(s==0) empty++;
            if(s==1) reserved++;
            if(s==2) seated++;
        }

        emptyLabel.setText("Empty "+empty);
        reservedLabel.setText("Reserved "+reserved);
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
        JMenuItem seated=new JMenuItem("Seated");
        JMenuItem empty=new JMenuItem("Empty");

        reserved.addActionListener(e->{
            tableStatus.set(index,1);
            table.setBackground(Color.GREEN);
            updateCounters();
        });

        seated.addActionListener(e->{
            tableStatus.set(index,2);
            table.setBackground(Color.RED);
            updateCounters();
        });

        empty.addActionListener(e->{
            tableStatus.set(index,0);
            table.setBackground(Color.LIGHT_GRAY);
            updateCounters();
        });

        menu.add(reserved);
        menu.add(seated);
        menu.add(empty);

        menu.show(table,x,y);
    }

    private int detectStatus(Color color){
        if(color.equals(Color.GREEN)) return 1;
        if(color.equals(Color.RED)) return 2;
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
        table.setBorder(BorderFactory.createLineBorder(new Color(196,164,100),3));
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

    String tableNo = table.getValueAt(row,3).toString();

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

        String sql = "SELECT * FROM walkin";

        PreparedStatement pst = conn.prepareStatement(sql);

        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) walkinTable.getModel();

        model.setRowCount(0);

        while(rs.next()){

            String name = rs.getString("name");
            int pax = rs.getInt("pax");
            String tableNo = rs.getString("table_no");
            String status = rs.getString("status");

            model.addRow(new Object[]{
            rs.getInt("id"),
            name,
            pax,
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

   private void updateTableFromWalkin(String tableNo,String status){

    int index = Integer.parseInt(tableNo.replace("T","")) - 1;

    if(index >=0 && index < tables.size()){

        JPanel table = tables.get(index);

        if(status.equals("Seated")){
            table.setBackground(new Color(102,0,0)); // wine red
            tableStatus.set(index,2);
        }

        if(status.equals("Waiting")){
            table.setBackground(new Color(212,175,55)); // gold
            tableStatus.set(index,1);
        } 
        

        if(status.equals("Done")){
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

    try{

        Connection conn = Dbconnection.getConnection();

        String sql = "SELECT table_no FROM walkin WHERE status!='Done'";

        PreparedStatement pst = conn.prepareStatement(sql);

        ResultSet rs = pst.executeQuery();

        ArrayList<String> occupied = new ArrayList<>();

        while(rs.next()){
            occupied.add(rs.getString("table_no"));
        }

        tableBox.removeAllItems();

        for(int i=1;i<=14;i++){

            String table = "T"+i;

            if(!occupied.contains(table)){
                tableBox.addItem(table);
            }
        }

    }catch(Exception e){
        e.printStackTrace();
    }
}

    public static void main(String[] args){
        new Dashboard();
    }

}