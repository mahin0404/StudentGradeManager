import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * STUDENT GRADE MANAGER
 * Concepts: Event Handling, Exception Handling,
 *           Code Refactoring, Unit Testing, Git & GitHub
 */
public class StudentGradeManager extends JFrame {

    // ─── Data storage ───────────────────────────────────────
    ArrayList<String> names    = new ArrayList<>();
    ArrayList<Double> averages = new ArrayList<>();
    ArrayList<String> grades   = new ArrayList<>();

    // ─── Input fields ───────────────────────────────────────
    JTextField nameField   = new JTextField(15);
    JTextField gradesField = new JTextField(15);

    // ─── Table to show students ─────────────────────────────
    DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Name", "Average", "Grade"}, 0);
    JTable table = new JTable(tableModel);

    // ─── Status message at bottom ───────────────────────────
    JLabel status = new JLabel("Welcome! Add a student to begin.");


    //  UNIT TESTS  (run before GUI opens, results in console)
    static void runTests() {
        System.out.println("--- UNIT TESTS ---");

        // Test 1: average calculation
        double avg = calcAverage("80, 90, 70");
        pass("Average of 80,90,70 = 80.0", avg == 80.0);

        // Test 2: letter grade
        pass("80 avg => B", getLetterGrade(80).equals("B"));
        pass("95 avg => A", getLetterGrade(95).equals("A"));
        pass("55 avg => F", getLetterGrade(55).equals("F"));

        // Test 3: invalid input caught
        try {
            validateName("");           // should throw
            pass("Empty name throws", false);
        } catch (Exception e) {
            pass("Empty name throws", true);
        }

        // Test 4: grade out of range caught
        try {
            validateGrades("150");      // should throw
            pass("Grade 150 throws", false);
        } catch (Exception e) {
            pass("Grade 150 throws", true);
        }

        System.out.println("------------------\n");
    }

    static void pass(String name, boolean ok) {
        System.out.println((ok ? "PASS" : "FAIL") + ": " + name);
    }

    //  VALIDATION  (Exception Handling)
    static String validateName(String input) throws Exception {
        if (input == null || input.trim().isEmpty())
            throw new Exception("Name cannot be empty.");
        if (!input.trim().matches("[a-zA-Z ]+"))
            throw new Exception("Name must contain letters only.");
        return input.trim();
    }

    static double[] validateGrades(String input) throws Exception {
        if (input == null || input.trim().isEmpty())
            throw new Exception("Please enter at least one grade.");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            double val;
            try {
                val = Double.parseDouble(parts[i].trim());
            } catch (NumberFormatException e) {
                throw new Exception("Invalid grade \"" + parts[i].trim() + "\" — only numbers are allowed, no letters.");
            }
            if (val < 0 || val > 100)
                throw new Exception("Grade must be between 0 and 100.");
            result[i] = val;
        }
        return result;
    }
    //  HELPER METHODS  (Code Refactoring: no repeated code)
    static double calcAverage(String input) {
        try {
            double[] g = validateGrades(input);
            double sum = 0;
            for (double v : g) sum += v;
            return sum / g.length;
        } catch (Exception e) { return 0; }
    }

    static String getLetterGrade(double avg) {
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < names.size(); i++)
            tableModel.addRow(new Object[]{ names.get(i),
                    String.format("%.1f", averages.get(i)), grades.get(i) });
    }
    //  EVENT HANDLERS
    void onAdd() {
        try {
            // Exception Handling: validate inputs, show error if invalid
            String name    = validateName(nameField.getText());
            double[] g     = validateGrades(gradesField.getText());
            double avg     = calcAverage(gradesField.getText());
            String  letter = getLetterGrade(avg);

            names.add(name);
            averages.add(avg);
            grades.add(letter);

            refreshTable();
            nameField.setText("");
            gradesField.setText("");
            status.setText("Added: " + name + "  |  Avg: " + String.format("%.1f", avg) + "  Grade: " + letter);

        } catch (Exception e) {
            // Show error dialog on bad input
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            status.setText("Error: " + e.getMessage());
        }
    }

    void onRemove() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "To remove Please select a student first.");
            return;
        }
        String removed = names.get(row);
        names.remove(row);
        averages.remove(row);
        grades.remove(row);
        refreshTable();
        status.setText("Removed: " + removed);
    }

    //  GUI SETUP
    StudentGradeManager() {
        super("Student Grade Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // --- Input area (top) ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Student"));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Grades (e.g. 80, 90, 75):"));
        inputPanel.add(gradesField);

        JButton btnAdd    = new JButton("Add Student");
        JButton btnRemove = new JButton("Remove Student");

        // EVENT HANDLING: buttons trigger actions
        btnAdd.addActionListener(e -> onAdd());
        btnRemove.addActionListener(e -> onRemove());
        // EVENT HANDLING: pressing Enter in grades field also adds
        gradesField.addActionListener(e -> onAdd());

        inputPanel.add(btnAdd);
        inputPanel.add(btnRemove);

        // --- Table (middle) ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Students"));
        tablePanel.add(new JScrollPane(table));

        // --- Status bar (bottom) ---
        status.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        add(inputPanel,BorderLayout.NORTH);
        add(tablePanel,BorderLayout.CENTER);
        add(status,BorderLayout.SOUTH);

        setVisible(true);
    }
    //  MAIN — runs tests then opens the window
    public static void main(String[] args) {
        runTests(); // prints test results to console
        SwingUtilities.invokeLater(StudentGradeManager::new);
    }
}