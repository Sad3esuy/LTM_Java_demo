package HTCK_25_12;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ClientChungKhoan extends JFrame {

    // === UI COMPONENTS ===
    private JTable tableMarket;
    private DefaultTableModel tableModel;
    private CandlestickChart candleChart;
    private JTextField txtIp, txtPort;
    private JButton btnConnect, btnDisconnect;
    private JLabel lblStatus, lblSelectedPair, lblCurrentPrice, lblPriceChange, lblVolume;
    private AnimatedConnectionIndicator connectionIndicator;
    private JPanel pnlOrderBook, pnlTechnicalInfo;

    // === LOGIC COMPONENTS ===
    private IChungKhoan dichVuServer;
    private Timer timer;
    private boolean isConnected = false;
    private String selectedPair = "";
    private List<CandleData> candleDataList = new ArrayList<>();

    // === PREMIUM TRADING COLOR SCHEME ===
    private static final Color BG_DARK = new Color(13, 17, 23);         // GitHub dark
    private static final Color BG_CARD = new Color(22, 27, 34);         // Card bg
    private static final Color BG_LIGHTER = new Color(33, 38, 45);      // Lighter section
    private static final Color ACCENT_BLUE = new Color(88, 166, 255);   // Chart blue
    private static final Color GREEN_UP = new Color(14, 203, 129);      // Binance green
    private static final Color RED_DOWN = new Color(246, 70, 93);       // Binance red
    private static final Color YELLOW_WARN = new Color(240, 185, 11);   // Warning
    private static final Color TEXT_WHITE = new Color(240, 246, 252);   // Primary text
    private static final Color TEXT_GRAY = new Color(139, 148, 158);    // Secondary text
    private static final Color BORDER_COLOR = new Color(48, 54, 61);    // Borders
    private static final Color GRID_COLOR = new Color(30, 35, 41);      // Chart grid

    public ClientChungKhoan() {
        initializeUI();
        attachEventListeners();
        generateSampleCandleData(); // Demo data
    }

    // ═══════════════════════════════════════════════════════════════
    // UI INITIALIZATION
    // ═══════════════════════════════════════════════════════════════

    private void initializeUI() {
        setTitle("Stock Trading Terminal • Advanced Chart");
        setSize(1800, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════════════
    // TOP BAR
    // ═══════════════════════════════════════════════════════════════

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(20, 0));
        topBar.setBackground(BG_CARD);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Left: Logo + Status
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(BG_CARD);

        JLabel lblLogo = new JLabel("📈 TRADING TERMINAL");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(TEXT_WHITE);

        connectionIndicator = new AnimatedConnectionIndicator();
        lblStatus = new JLabel("Offline");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(TEXT_GRAY);

        leftPanel.add(lblLogo);
        leftPanel.add(Box.createHorizontalStrut(15));
        leftPanel.add(connectionIndicator);
        leftPanel.add(lblStatus);

        // Right: Connection controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BG_CARD);

        txtIp = createTextField("192.168.51.168", 140);
        txtPort = createTextField("1099", 70);
        btnConnect = createButton("Connect", ACCENT_BLUE);
        btnDisconnect = createButton("Disconnect", RED_DOWN);
        btnDisconnect.setEnabled(false);

        rightPanel.add(createLabel("IP:"));
        rightPanel.add(txtIp);
        rightPanel.add(createLabel("Port:"));
        rightPanel.add(txtPort);
        rightPanel.add(btnConnect);
        rightPanel.add(btnDisconnect);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    // ═══════════════════════════════════════════════════════════════
    // MAIN CONTENT - 3 Column Layout
    // ═══════════════════════════════════════════════════════════════

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_DARK);

        // Left sidebar: Market list (20%)
        JPanel leftSidebar = buildMarketListPanel();
        leftSidebar.setPreferredSize(new Dimension(350, 0));

        // Center: Chart area (60%)
        JPanel centerPanel = buildChartPanel();

        // Right sidebar: Order book + Info (20%)
        JPanel rightSidebar = buildRightSidebar();
        rightSidebar.setPreferredSize(new Dimension(350, 0));

        main.add(leftSidebar, BorderLayout.WEST);
        main.add(centerPanel, BorderLayout.CENTER);
        main.add(rightSidebar, BorderLayout.EAST);

        return main;
    }

    // ═══════════════════════════════════════════════════════════════
    // LEFT PANEL - Market List
    // ═══════════════════════════════════════════════════════════════

    private JPanel buildMarketListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Market Watch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_WHITE);

        JTextField searchField = createTextField("Search...", 200);
        searchField.setPreferredSize(new Dimension(200, 32));

        header.add(title, BorderLayout.NORTH);
        header.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        header.add(searchField, BorderLayout.SOUTH);

        // Market table
        String[] columns = {"Pair", "Price", "Change %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMarket = new JTable(tableModel);
        styleMarketTable();

        // Add click listener
        tableMarket.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableMarket.getSelectedRow();
                if (row >= 0) {
                    selectedPair = tableMarket.getValueAt(row, 0).toString();
                    updateChartForPair(selectedPair);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableMarket);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_CARD);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void styleMarketTable() {
        tableMarket.setBackground(BG_CARD);
        tableMarket.setForeground(TEXT_WHITE);
        tableMarket.setRowHeight(48);
        tableMarket.setShowGrid(false);
        tableMarket.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableMarket.setSelectionBackground(BG_LIGHTER);
        tableMarket.setSelectionForeground(TEXT_WHITE);

        JTableHeader header = tableMarket.getTableHeader();
        header.setBackground(BG_DARK);
        header.setForeground(TEXT_GRAY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Custom renderer
        MarketCellRenderer renderer = new MarketCellRenderer();
        for (int i = 0; i < tableMarket.getColumnCount(); i++) {
            tableMarket.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Column widths
        tableMarket.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableMarket.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableMarket.getColumnModel().getColumn(2).setPreferredWidth(80);
    }

    // ═══════════════════════════════════════════════════════════════
    // CENTER PANEL - Candlestick Chart
    // ═══════════════════════════════════════════════════════════════

    private JPanel buildChartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chart header with pair info
        JPanel chartHeader = buildChartHeader();

        // Candlestick chart
        candleChart = new CandlestickChart();
        candleChart.setBackground(BG_CARD);
        candleChart.setBorder(new LineBorder(BORDER_COLOR, 1));

        // Timeframe buttons
        JPanel timeframePanel = buildTimeframePanel();

        panel.add(chartHeader, BorderLayout.NORTH);
        panel.add(candleChart, BorderLayout.CENTER);
        panel.add(timeframePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildChartHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setBackground(BG_DARK);

        lblSelectedPair = new JLabel("--");
        lblSelectedPair.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSelectedPair.setForeground(TEXT_WHITE);

        lblCurrentPrice = new JLabel("0.00");
        lblCurrentPrice.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblCurrentPrice.setForeground(GREEN_UP);

        lblPriceChange = new JLabel("+0.00%");
        lblPriceChange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPriceChange.setForeground(GREEN_UP);

        lblVolume = new JLabel("Vol: 0");
        lblVolume.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVolume.setForeground(TEXT_GRAY);

        header.add(lblSelectedPair);
        header.add(Box.createHorizontalStrut(10));
        header.add(lblCurrentPrice);
        header.add(lblPriceChange);
        header.add(Box.createHorizontalStrut(20));
        header.add(lblVolume);

        return header;
    }

    private JPanel buildTimeframePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        panel.setBackground(BG_DARK);

        String[] timeframes = {"1m", "5m", "15m", "1h", "4h", "1D", "1W"};

        for (String tf : timeframes) {
            JButton btn = createTimeframeButton(tf);
            panel.add(btn);
        }

        return panel;
    }

    private JButton createTimeframeButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(50, 32));
        btn.setBackground(BG_LIGHTER);
        btn.setForeground(TEXT_GRAY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(BORDER_COLOR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BG_CARD);
                btn.setForeground(ACCENT_BLUE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_LIGHTER);
                btn.setForeground(TEXT_GRAY);
            }
        });

        return btn;
    }

    // ═══════════════════════════════════════════════════════════════
    // RIGHT PANEL - Order Book & Technical Info
    // ═══════════════════════════════════════════════════════════════

    private JPanel buildRightSidebar() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 1, 0, 0, BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        pnlOrderBook = buildOrderBookPanel();
        pnlTechnicalInfo = buildTechnicalInfoPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                pnlOrderBook, pnlTechnicalInfo);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_DARK);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildOrderBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1));

        // Header
        JLabel title = new JLabel("Order Book");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_WHITE);
        title.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Order book content
        JPanel content = new JPanel(new GridLayout(0, 1, 0, 2));
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Sample orders
        for (int i = 0; i < 10; i++) {
            double price = 50000 + (i - 5) * 100;
            int volume = (int) (Math.random() * 1000);
            boolean isBuy = i < 5;

            content.add(createOrderRow(price, volume, isBuy));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_CARD);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOrderRow(double price, int volume, boolean isBuy) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_CARD);
        row.setPreferredSize(new Dimension(0, 24));

        JLabel lblPrice = new JLabel(String.format("%.2f", price));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setForeground(isBuy ? GREEN_UP : RED_DOWN);

        JLabel lblVolume = new JLabel(String.valueOf(volume));
        lblVolume.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVolume.setForeground(TEXT_GRAY);
        lblVolume.setHorizontalAlignment(SwingConstants.RIGHT);

        // Volume bar
        JProgressBar bar = new JProgressBar(0, 1000);
        bar.setValue(volume);
        bar.setPreferredSize(new Dimension(0, 24));
        bar.setBackground(BG_CARD);
        bar.setForeground(new Color(
                isBuy ? GREEN_UP.getRed() : RED_DOWN.getRed(),
                isBuy ? GREEN_UP.getGreen() : RED_DOWN.getGreen(),
                isBuy ? GREEN_UP.getBlue() : RED_DOWN.getBlue(),
                30
        ));
        bar.setBorderPainted(false);

        JPanel overlay = new JPanel(new BorderLayout(10, 0));
        overlay.setOpaque(false);
        overlay.add(lblPrice, BorderLayout.WEST);
        overlay.add(lblVolume, BorderLayout.EAST);

        row.add(bar, BorderLayout.CENTER);
        row.add(overlay, BorderLayout.CENTER);

        return row;
    }

    private JPanel buildTechnicalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1));

        JLabel title = new JLabel("Technical Indicators");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_WHITE);
        title.setBorder(new EmptyBorder(12, 15, 12, 15));

        JPanel content = new JPanel(new GridLayout(0, 1, 0, 8));
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(10, 15, 10, 15));

        content.add(createInfoRow("RSI (14)", "65.23", TEXT_WHITE));
        content.add(createInfoRow("MACD", "+12.5", GREEN_UP));
        content.add(createInfoRow("EMA (20)", "49,850", TEXT_GRAY));
        content.add(createInfoRow("Volume (24h)", "1.2M", TEXT_WHITE));
        content.add(createInfoRow("High (24h)", "51,200", TEXT_GRAY));
        content.add(createInfoRow("Low (24h)", "48,300", TEXT_GRAY));

        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG_CARD);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(TEXT_GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValue.setForeground(valueColor);
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);

        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    // CANDLESTICK CHART COMPONENT
    // ═══════════════════════════════════════════════════════════════

    class CandlestickChart extends JPanel {
        private int padding = 50;
        private int candleWidth = 8;
        private int candleSpacing = 4;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (candleDataList.isEmpty()) {
                drawEmptyState(g2);
                return;
            }

            drawGrid(g2);
            drawPriceAxis(g2);
            drawTimeAxis(g2);
            drawCandles(g2);
            drawVolumeBars(g2);
        }

        private void drawEmptyState(Graphics2D g2) {
            String msg = "Select a pair to view chart";
            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2.drawString(msg, x, y);
        }

        private void drawGrid(Graphics2D g2) {
            g2.setColor(GRID_COLOR);
            g2.setStroke(new BasicStroke(1));

            // Horizontal lines
            for (int i = 0; i <= 5; i++) {
                int y = padding + (getHeight() - 2 * padding) * i / 5;
                g2.drawLine(padding, y, getWidth() - padding, y);
            }

            // Vertical lines
            int visibleCandles = Math.min(candleDataList.size(), 50);
            for (int i = 0; i <= visibleCandles; i += 10) {
                int x = padding + i * (candleWidth + candleSpacing);
                g2.drawLine(x, padding, x, getHeight() - padding);
            }
        }

        private void drawPriceAxis(Graphics2D g2) {
            if (candleDataList.isEmpty()) return;

            double maxPrice = candleDataList.stream().mapToDouble(c -> c.high).max().orElse(0);
            double minPrice = candleDataList.stream().mapToDouble(c -> c.low).min().orElse(0);

            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            for (int i = 0; i <= 5; i++) {
                double price = maxPrice - (maxPrice - minPrice) * i / 5;
                int y = padding + (getHeight() - 2 * padding) * i / 5;

                String priceStr = String.format("%.2f", price);
                g2.drawString(priceStr, getWidth() - padding + 5, y + 5);
            }
        }

        private void drawTimeAxis(Graphics2D g2) {
            g2.setColor(TEXT_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            int visibleCandles = Math.min(candleDataList.size(), 50);
            for (int i = 0; i < visibleCandles; i += 10) {
                if (i < candleDataList.size()) {
                    int x = padding + i * (candleWidth + candleSpacing);
                    String time = candleDataList.get(i).timestamp;
                    g2.drawString(time, x - 15, getHeight() - padding + 20);
                }
            }
        }

        private void drawCandles(Graphics2D g2) {
            if (candleDataList.isEmpty()) return;

            double maxPrice = candleDataList.stream().mapToDouble(c -> c.high).max().orElse(0);
            double minPrice = candleDataList.stream().mapToDouble(c -> c.low).min().orElse(0);
            double priceRange = maxPrice - minPrice;

            int chartHeight = getHeight() - 2 * padding - 50; // Reserve space for volume
            int visibleCandles = Math.min(candleDataList.size(), 50);

            for (int i = 0; i < visibleCandles; i++) {
                CandleData candle = candleDataList.get(candleDataList.size() - visibleCandles + i);

                int x = padding + i * (candleWidth + candleSpacing);

                int highY = (int) (padding + (maxPrice - candle.high) / priceRange * chartHeight);
                int lowY = (int) (padding + (maxPrice - candle.low) / priceRange * chartHeight);
                int openY = (int) (padding + (maxPrice - candle.open) / priceRange * chartHeight);
                int closeY = (int) (padding + (maxPrice - candle.close) / priceRange * chartHeight);

                boolean isGreen = candle.close >= candle.open;
                g2.setColor(isGreen ? GREEN_UP : RED_DOWN);

                // Wick (shadow)
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(x + candleWidth / 2, highY, x + candleWidth / 2, lowY);

                // Body
                int bodyTop = Math.min(openY, closeY);
                int bodyHeight = Math.abs(closeY - openY);
                if (bodyHeight < 1) bodyHeight = 1;

                g2.fillRect(x, bodyTop, candleWidth, bodyHeight);
            }
        }

        private void drawVolumeBars(Graphics2D g2) {
            if (candleDataList.isEmpty()) return;

            double maxVolume = candleDataList.stream().mapToDouble(c -> c.volume).max().orElse(0);
            int volumeHeight = 50;
            int volumeY = getHeight() - padding - volumeHeight;
            int visibleCandles = Math.min(candleDataList.size(), 50);

            for (int i = 0; i < visibleCandles; i++) {
                CandleData candle = candleDataList.get(candleDataList.size() - visibleCandles + i);

                int x = padding + i * (candleWidth + candleSpacing);
                int barHeight = (int) (candle.volume / maxVolume * volumeHeight);

                boolean isGreen = candle.close >= candle.open;
                g2.setColor(new Color(
                        isGreen ? GREEN_UP.getRed() : RED_DOWN.getRed(),
                        isGreen ? GREEN_UP.getGreen() : RED_DOWN.getGreen(),
                        isGreen ? GREEN_UP.getBlue() : RED_DOWN.getBlue(),
                        100
                ));

                g2.fillRect(x, volumeY + volumeHeight - barHeight, candleWidth, barHeight);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DATA MODELS
    // ═══════════════════════════════════════════════════════════════

    class CandleData {
        String timestamp;
        double open, high, low, close, volume;

        public CandleData(String timestamp, double open, double high, double low, double close, double volume) {
            this.timestamp = timestamp;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CUSTOM RENDERERS
    // ═══════════════════════════════════════════════════════════════

    class MarketCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? BG_CARD : new Color(BG_CARD.getRed() + 3, BG_CARD.getGreen() + 3, BG_CARD.getBlue() + 3));
            }

            setBorder(new EmptyBorder(5, 10, 5, 10));

            if (col == 0) {
                // Pair name
                c.setForeground(TEXT_WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(LEFT);
            } else if (col == 1) {
                // Price
                c.setForeground(TEXT_WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(RIGHT);
            } else if (col == 2) {
                // Change %
                try {
                    String text = value.toString().replace("%", "");
                    double change = Double.parseDouble(text);
                    c.setForeground(change >= 0 ? GREEN_UP : RED_DOWN);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setText((change >= 0 ? "+" : "") + String.format("%.2f%%", change));
                } catch (Exception e) {
                    c.setForeground(TEXT_GRAY);
                }
                setHorizontalAlignment(RIGHT);
            }

            return c;
        }
    }

    class AnimatedConnectionIndicator extends JPanel {
        private Color color = TEXT_GRAY;
        private boolean active = false;
        private float alpha = 1.0f;
        private Timer pulseTimer;

        public AnimatedConnectionIndicator() {
            setPreferredSize(new Dimension(10, 10));
            setOpaque(false);

            pulseTimer = new Timer(50, e -> {
                if (active) {
                    alpha -= 0.05f;
                    if (alpha <= 0.3f) alpha = 1.0f;
                    repaint();
                }
            });
            pulseTimer.start();
        }

        public void setColor(Color c) { this.color = c; repaint(); }
        public void setActive(boolean active) { this.active = active; if (!active) alpha = 1.0f; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha));
            g2.setColor(drawColor);
            g2.fillOval(0, 0, 10, 10);

            if (active) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillOval(-2, -2, 14, 14);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // EVENT HANDLERS & LOGIC
    // ═══════════════════════════════════════════════════════════════

    private void attachEventListeners() {
        btnConnect.addActionListener(e -> connectToServer());
        btnDisconnect.addActionListener(e -> disconnectFromServer());
    }

    private void connectToServer() {
        String ip = txtIp.getText().trim();
        int port;

        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid port", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnConnect.setEnabled(false);
        updateConnectionStatus("Connecting...", YELLOW_WARN, false);

        new Thread(() -> {
            try {
                if (timer != null) timer.stop();

                Registry reg = LocateRegistry.getRegistry(ip, port);
                dichVuServer = (IChungKhoan) reg.lookup("DichVuChungKhoan");

                SwingUtilities.invokeLater(() -> {
                    isConnected = true;
                    updateConnectionStatus("Connected", GREEN_UP, true);
                    btnConnect.setEnabled(false);
                    btnDisconnect.setEnabled(true);
                    txtIp.setEnabled(false);
                    txtPort.setEnabled(false);

                    timer = new Timer(1000, e -> updateMarketData());
                    timer.start();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    updateConnectionStatus("Failed", RED_DOWN, false);
                    btnConnect.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void disconnectFromServer() {
        if (timer != null) timer.stop();
        dichVuServer = null;
        isConnected = false;

        updateConnectionStatus("Offline", TEXT_GRAY, false);
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        txtIp.setEnabled(true);
        txtPort.setEnabled(true);

        tableModel.setRowCount(0);
    }

    private void updateMarketData() {
        try {
            if (dichVuServer != null) {
                String rawData = dichVuServer.layThongTinSan();
                tableModel.setRowCount(0);

                if (rawData != null && !rawData.isEmpty()) {
                    String[] rows = rawData.split("\n");

                    for (String row : rows) {
                        row = row.trim();
                        if (row.startsWith("---") || row.isEmpty()) continue; // Skip header and empty lines

                        // Parse format: "1. Hà Nội (HNX):   100.00"
                        if (row.contains(":")) {
                            String[] parts = row.split(":");
                            if (parts.length >= 2) {
                                String pair = parts[0].trim();
                                // Remove the number prefix like "1. "
                                if (pair.matches("\\d+\\..*")) {
                                    pair = pair.substring(pair.indexOf('.') + 1).trim();
                                }
                                String price = parts[1].trim();
                                double change = Math.random() * 10 - 5; // Mock change %

                                tableModel.addRow(new Object[]{
                                        pair,
                                        price,
                                        String.format("%.2f%%", change)
                                });
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            disconnectFromServer();
        }
    }

    private void updateChartForPair(String pair) {
        lblSelectedPair.setText(pair);

        // Generate new candle data for selected pair
        generateSampleCandleData();
        candleChart.repaint();

        // Update price info
        if (!candleDataList.isEmpty()) {
            CandleData latest = candleDataList.get(candleDataList.size() - 1);
            lblCurrentPrice.setText(String.format("%.2f", latest.close));

            double change = ((latest.close - latest.open) / latest.open) * 100;
            lblPriceChange.setText(String.format("%+.2f%%", change));
            lblPriceChange.setForeground(change >= 0 ? GREEN_UP : RED_DOWN);
            lblCurrentPrice.setForeground(change >= 0 ? GREEN_UP : RED_DOWN);

            lblVolume.setText(String.format("Vol: %.1fK", latest.volume / 1000));
        }
    }

    private void updateConnectionStatus(String status, Color color, boolean isActive) {
        lblStatus.setText(status);
        lblStatus.setForeground(color);
        connectionIndicator.setActive(isActive);
        connectionIndicator.setColor(color);
    }

    // ═══════════════════════════════════════════════════════════════
    // DEMO DATA GENERATOR
    // ═══════════════════════════════════════════════════════════════

    private void generateSampleCandleData() {
        candleDataList.clear();
        Random rand = new Random();
        double basePrice = 50000;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -50);

        for (int i = 0; i < 50; i++) {
            double open = basePrice + rand.nextDouble() * 1000 - 500;
            double close = open + rand.nextDouble() * 600 - 300;
            double high = Math.max(open, close) + rand.nextDouble() * 200;
            double low = Math.min(open, close) - rand.nextDouble() * 200;
            double volume = 1000 + rand.nextDouble() * 5000;

            String time = sdf.format(cal.getTime());
            candleDataList.add(new CandleData(time, open, high, low, close, volume));

            basePrice = close;
            cal.add(Calendar.HOUR, 1);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // UI FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_GRAY);
        return label;
    }

    private JTextField createTextField(String text, int width) {
        JTextField field = new JTextField(text);
        field.setPreferredSize(new Dimension(width, 32));
        field.setBackground(BG_LIGHTER);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(ACCENT_BLUE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 32));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            Color originalColor = color;
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(
                            Math.min(color.getRed() + 20, 255),
                            Math.min(color.getGreen() + 20, 255),
                            Math.min(color.getBlue() + 20, 255)
                    ));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(originalColor);
            }
        });

        return button;
    }

    // ═══════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            ClientChungKhoan app = new ClientChungKhoan();
            app.setVisible(true);
        });
    }
}