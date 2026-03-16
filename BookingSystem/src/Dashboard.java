import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        sidebar.setBackground(new Color(40,40,40));
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
        header.setBackground(new Color(180,150,70));
        header.setBounds(sidebarWidth,0,getWidth()-sidebarWidth,headerHeight);

        DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE, dd MMM");

        JLabel date =
                new JLabel(LocalDate.now().format(format));

        date.setFont(new Font("Arial",Font.BOLD,18));
        date.setBounds(30,20,250,30);

        reservedLabel = createCounter("Reserved",Color.GREEN,300);
        seatedLabel = createCounter("Seated",Color.RED,450);
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
                g.setColor(new Color(190,165,90));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };

        floorPanel.setLayout(null);

        addFamilyTable(floorPanel,"Table 1",80,100,Color.GREEN);
        addFamilyTable(floorPanel,"Table 2",80,250,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 3",250,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 4",250,200,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 5",250,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 6",400,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 7",400,200,Color.RED);
        addTable(floorPanel,"Table 8",400,300,Color.LIGHT_GRAY);

        addTable(floorPanel,"Table 9",550,100,Color.LIGHT_GRAY);
        addTable(floorPanel,"Table 10",550,200,Color.RED);
        addTable(floorPanel,"Table 11",550,300,Color.LIGHT_GRAY);

        addRoundTable(floorPanel,"Table 12",650,100,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 13",650,200,Color.LIGHT_GRAY);
        addRoundTable(floorPanel,"Table 14",650,300,Color.LIGHT_GRAY);

        return floorPanel;
    }

    private JPanel createListPage(){

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JLabel title = new JLabel("Walk-in List",JLabel.CENTER);
    title.setFont(new Font("Arial",Font.BOLD,20));

    String[] columns = {"Name","Pax","Table","Status","Edit"};

    Object[][] data = {
            {"Juan Dela Cruz","4","T3","Waiting","Edit"},
            {"Maria Santos","2","T1","Seated","Edit"}
    };

    JTable table = new JTable(data,columns);

    table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
    table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(),table));

    JScrollPane scroll = new JScrollPane(table);

    panel.add(title,BorderLayout.NORTH);
    panel.add(scroll,BorderLayout.CENTER);

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

    private void resizeTables(){

        double scaleX = floorPanel.getWidth()/(double)baseWidth;
        double scaleY = floorPanel.getHeight()/(double)baseHeight;

        for(int i=0;i<tables.size();i++){

            Rectangle r = originalBounds.get(i);

            int x=(int)(r.x*scaleX);
            int y=(int)(r.y*scaleY);
            int w=(int)(r.width*scaleX);
            int h=(int)(r.height*scaleY);

            tables.get(i).setBounds(x,y,w,h);
        }
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
        label.setFont(new Font("Arial",Font.BOLD,12));

        table.add(label,BorderLayout.SOUTH);

        int index=tables.size();

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                showStatusMenu(table,index,e.getX(),e.getY());
            }
        });

        panel.add(table);

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

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                showStatusMenu(table,index,e.getX(),e.getY());
            }
        });

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

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                showStatusMenu(table,index,e.getX(),e.getY());
            }
        });

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
                table.setValueAt(status,row,3);
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

    public static void main(String[] args){
        new Dashboard();
    }
}