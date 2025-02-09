import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

//is

public class Main {
    private static final Map<String, PatientDetails> patientDetailsMap = new HashMap<>();
    private static final Map<String, String> patientCredentialsMap = new HashMap<>();
    private static final String DOCTOR_USERNAME = "doctor";
    private static final String DOCTOR_PASSWORD = "password";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/medical_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "03091107";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Medical Subscription System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(4000, 3000);
        frame.setLayout(new BorderLayout());


        JPanel centerPanel = new JPanel(new GridBagLayout());
        frame.add(centerPanel, BorderLayout.CENTER);

        JLabel label = new JLabel("Are you a patient or a doctor?");
        label.setFont(new Font("Arial", Font.BOLD, 100));


        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(label, labelConstraints);

        JButton patientButton = new JButton("Patient");
        JButton doctorButton = new JButton("Doctor");
        patientButton.setFont(new Font("Arial", Font.BOLD, 20));
        doctorButton.setFont(new Font("Arial", Font.BOLD, 20));
        Dimension buttonSize = new Dimension(200, 80);

        patientButton.setPreferredSize(buttonSize);
        doctorButton.setPreferredSize(buttonSize);


        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 1;
        buttonConstraints.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(patientButton, buttonConstraints);

        buttonConstraints.gridy = 2;
        centerPanel.add(doctorButton, buttonConstraints);

        patientButton.addActionListener(e -> handlePatientAction(frame));
        doctorButton.addActionListener(e -> showLoginDialog(frame));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private static void showNearbyHospitalsInMaps(PatientDetails patientDetails) {

        double patientLat = 12.824714554411358;
        double patientLng = 80.0452120243675;

        String uri = "https://www.google.com/maps/search/hospitals/@" +
                patientLat + "," + patientLng + ",15z";
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to open the browser.");
        }
    }


    private static void handlePatientAction(JFrame frame) {
        String[] options = {"Add New Patient", "Modify Existing Patient", "View Prescription"};
        int choice = JOptionPane.showOptionDialog(frame, "Choose an option:", "Patient Interface",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> addNewPatient();
            case 1 -> modifyExistingPatient();
            case 2 -> viewPrescription();
        }
    }

    private static void addNewPatient() {
        JFrame patientFrame = new JFrame("Patient Subscription");
        patientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        patientFrame.setSize(screenSize.width, screenSize.height);
        patientFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        patientFrame.setUndecorated(true);


        patientFrame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel subscriptionLabel = new JLabel("Choose a subscription plan:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        patientFrame.add(subscriptionLabel, gbc);

        JRadioButton plan1Button = new JRadioButton("Doctor Consultation");
        JRadioButton plan2Button = new JRadioButton("Doctor Consultation + Blood Tests");
        ButtonGroup group = new ButtonGroup();
        group.add(plan1Button);
        group.add(plan2Button);
        gbc.gridy++;
        patientFrame.add(plan1Button, gbc);
        gbc.gridy++;
        patientFrame.add(plan2Button, gbc);

        addLabelAndTextField(patientFrame, "Name:", gbc);
        addLabelAndTextField(patientFrame, "Age:", gbc);
        addLabelAndTextField(patientFrame, "Gender:", gbc);
        addLabelAndTextField(patientFrame, "Blood Group:", gbc);
        addLabelAndTextField(patientFrame, "Symptoms:", gbc);

        JButton submitButton = new JButton("Submit");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        patientFrame.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String patientName = getTextFieldValue(patientFrame, "Name:");
            String patientPassword = JOptionPane.showInputDialog("Create a password for the patient:");
            patientCredentialsMap.put(patientName, patientPassword);

            PatientDetails patientDetails = new PatientDetails(
                    getTextFieldValue(patientFrame, "Age:"),
                    getTextFieldValue(patientFrame, "Gender:"),
                    getTextFieldValue(patientFrame, "Blood Group:"),
                    getTextFieldValue(patientFrame, "Symptoms:"),
                    plan1Button.isSelected() ? "Doctor Consultation" : "Doctor Consultation + Blood Tests"
            );
            patientDetailsMap.put(patientName, patientDetails);
            JOptionPane.showMessageDialog(null, "Details stored successfully!");
            patientFrame.dispose();
        });

        patientFrame.setVisible(true);
    }


    private static void modifyExistingPatient() {
        if (patientDetailsMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existing patients to modify.");
        } else {
            String patientName = authenticatePatient();
            if (patientName != null) {
                PatientDetails patientDetails = patientDetailsMap.get(patientName);

                JFrame modifyFrame = new JFrame("Modify Patient Details");
                modifyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                modifyFrame.setSize(400, 400);
                modifyFrame.setLayout(new GridBagLayout());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;

                JLabel subscriptionLabel = new JLabel("Choose a subscription plan:");
                gbc.gridx = 0;
                gbc.gridy = 0;
                modifyFrame.add(subscriptionLabel, gbc);

                JRadioButton plan1Button = new JRadioButton("Doctor Consultation");
                JRadioButton plan2Button = new JRadioButton("Doctor Consultation + Blood Tests");
                ButtonGroup group = new ButtonGroup();
                group.add(plan1Button);
                group.add(plan2Button);
                gbc.gridy++;
                modifyFrame.add(plan1Button, gbc);
                gbc.gridy++;
                modifyFrame.add(plan2Button, gbc);

                setTextFieldValue(modifyFrame, "Name:", patientName, gbc);
                setTextFieldValue(modifyFrame, "Age:", patientDetails.getAge(), gbc);
                setTextFieldValue(modifyFrame, "Gender:", patientDetails.getGender(), gbc);
                setTextFieldValue(modifyFrame, "Blood Group:", patientDetails.getBloodGroup(), gbc);
                setTextFieldValue(modifyFrame, "Symptoms:", patientDetails.getSymptoms(), gbc);

                JButton submitButton = new JButton("Submit");
                gbc.gridy++;
                gbc.gridx = 0;
                gbc.gridwidth = 2;
                modifyFrame.add(submitButton, gbc);

                submitButton.addActionListener(e -> {
                    PatientDetails updatedDetails = new PatientDetails(
                            getTextFieldValue(modifyFrame, "Age:"),
                            getTextFieldValue(modifyFrame, "Gender:"),
                            getTextFieldValue(modifyFrame, "Blood Group:"),
                            getTextFieldValue(modifyFrame, "Symptoms:"),
                            plan1Button.isSelected() ? "Doctor Consultation" : "Doctor Consultation + Blood Tests"
                    );
                    patientDetailsMap.put(patientName, updatedDetails);
                    JOptionPane.showMessageDialog(null, "Details updated successfully!");
                    modifyFrame.dispose();
                });

                modifyFrame.setVisible(true);
            }
        }
    }

    private static void viewPrescription() {
        if (patientDetailsMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existing patients to view prescription.");
        } else {
            String patientName = authenticatePatient();
            if (patientName != null) {
                PatientDetails patientDetails = patientDetailsMap.get(patientName);

                JFrame prescriptionFrame = new JFrame("View Prescription");
                prescriptionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                prescriptionFrame.setSize(400, 300);
                prescriptionFrame.setLayout(new FlowLayout());
                prescriptionFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximizes the frame to full screen
                JTextArea prescriptionTextArea = new JTextArea(10, 30);
                prescriptionTextArea.setEditable(false);
                prescriptionTextArea.setText("Prescription for " + patientName + ":\n" +
                        patientDetails.getPrescription()
                );

                JButton bloodTestButton = new JButton("Book Blood Test");
                JButton callDoctorButton = new JButton("Call Doctor");
                JButton callAmbulanceButton = new JButton("Call Ambulance");


                JButton nearbyHospitalsButton = new JButton("Nearby Hospitals");
                prescriptionFrame.add(nearbyHospitalsButton);

                nearbyHospitalsButton.addActionListener(e -> showNearbyHospitalsInMaps(patientDetails));


                prescriptionFrame.add(prescriptionTextArea);
                prescriptionFrame.add(bloodTestButton);
                prescriptionFrame.add(callDoctorButton);
                prescriptionFrame.add(callAmbulanceButton);

                bloodTestButton.addActionListener(e -> {
                    String[] timeSlots = {"9:00 AM", "11:00 AM", "2:00 PM", "4:00 PM"};
                    String selectedSlot = (String) JOptionPane.showInputDialog(
                            prescriptionFrame,
                            "Select a time slot for the blood test:",
                            "Book Blood Test",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            timeSlots,
                            timeSlots.length > 0 ? timeSlots[0] : null
                    );

                    if (selectedSlot != null) {
                        JOptionPane.showMessageDialog(null, "Blood test appointment booked for " + selectedSlot + "!");
                    }
                });

                callDoctorButton.addActionListener(e ->
                        JOptionPane.showMessageDialog(null, "Call the doctor at: 123-456-7890"));

                callAmbulanceButton.addActionListener(e ->
                        JOptionPane.showMessageDialog(null, "Ambulance on the way!"));

                prescriptionFrame.setVisible(true);
            }
        }
    }

    private static String authenticatePatient() {
        String patientName = JOptionPane.showInputDialog("Enter patient name:");
        if (patientCredentialsMap.containsKey(patientName)) {
            String enteredPassword = JOptionPane.showInputDialog("Enter patient password:");
            String correctPassword = patientCredentialsMap.get(patientName);
            if (enteredPassword.equals(correctPassword)) {
                return patientName;
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password. Access denied.");
                return null;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Patient not found. Access denied.");
            return null;
        }
    }

    private static void showLoginDialog(JFrame frame) {
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        JPanel loginPanel = new JPanel();
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Doctor Login", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            if (isDoctor(username, password)) {
                openDoctorPortal(frame);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password. Access denied.");
            }

            usernameField.setText("");
            passwordField.setText("");
        }
    }

    private static boolean isDoctor(String username, String password) {
        return DOCTOR_USERNAME.equals(username) && DOCTOR_PASSWORD.equals(password);
    }

    private static void openDoctorPortal(JFrame frame) {
        if (patientDetailsMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No patients available.");
        } else {
            String[] patients = patientDetailsMap.keySet().toArray(new String[0]);
            String selectedPatient = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select a patient:",
                    "Doctor Interface",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    patients,
                    patients.length > 0 ? patients[0] : null
            );

            if (selectedPatient != null) {
                displayPatientDetails(selectedPatient);
            }
        }
    }

    private static void displayPatientDetails(String selectedPatient) {
        PatientDetails patientDetails = patientDetailsMap.get(selectedPatient);

        JFrame doctorFrame = new JFrame("Doctor Portal");
        doctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        doctorFrame.setSize(400, 300);
        doctorFrame.setLayout(new FlowLayout());
        doctorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximizes the frame to full screen
        JTextArea patientDetailsTextArea = new JTextArea(10, 30);
        patientDetailsTextArea.setEditable(false);
        patientDetailsTextArea.setText("Patient Details for " + selectedPatient + ":\n" +
                "Age: " + patientDetails.getAge() + "\n" +
                "Gender: " + patientDetails.getGender() + "\n" +
                "Blood Group: " + patientDetails.getBloodGroup() + "\n" +
                "Symptoms: " + patientDetails.getSymptoms() + "\n" +
                "Subscription Plan: " + patientDetails.getSubscriptionPlan()
        );

        JTextArea prescriptionTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(prescriptionTextArea);

        JButton prescribeButton = new JButton("Prescribe Medication");
        doctorFrame.add(patientDetailsTextArea);
        doctorFrame.add(new JLabel("Prescription:"));
        doctorFrame.add(scrollPane);
        doctorFrame.add(prescribeButton);

        prescribeButton.addActionListener(e -> {
            String prescription = JOptionPane.showInputDialog(
                    doctorFrame,
                    "Enter prescription:",
                    "Prescribe Medication",
                    JOptionPane.PLAIN_MESSAGE
            );
            patientDetails.setPrescription(prescription);
            JOptionPane.showMessageDialog(null, "Prescription stored successfully!");
        });

        doctorFrame.setVisible(true);
    }


    private void createPatientsTable() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS patients ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "age INT,"
                    + "gender VARCHAR(10),"
                    + "bloodGroup VARCHAR(10),"
                    + "symptoms TEXT,"
                    + "subscriptionPlan VARCHAR(100),"
                    + "prescription TEXT"
                    + ")";

            statement.executeUpdate(createTableQuery);

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating 'patients' table: " + ex.getMessage());
        }
    }



    private static class PatientDetails {
        private String age;
        private String gender;
        private String bloodGroup;
        private String symptoms;
        private String subscriptionPlan;
        private String prescription;

        public PatientDetails(String age, String gender, String bloodGroup, String symptoms, String subscriptionPlan) {
            this.age = age;
            this.gender = gender;
            this.bloodGroup = bloodGroup;
            this.symptoms = symptoms;
            this.subscriptionPlan = subscriptionPlan;
            this.prescription = "";
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public String getSubscriptionPlan() {
            return subscriptionPlan;
        }

        public String getPrescription() {
            return prescription;
        }

        public void setPrescription(String prescription) {
            this.prescription = prescription;
        }
    }

    // Helper methods for setting and getting text field values
    private static void addLabelAndTextField(JFrame frame, String labelText, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        frame.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(200, textField.getPreferredSize().height));

        frame.add(textField, gbc);
    }

    private static String getTextFieldValue(JFrame frame, String labelText) {
        Component[] components = frame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && ((JLabel) component).getText().equals(labelText)) {
                int index = frame.getContentPane().getComponentZOrder(component);
                JTextField textField = (JTextField) frame.getContentPane().getComponent(index + 1);
                return textField.getText();
            }
        }
        return "";
    }

    private static void setTextFieldValue(JFrame frame, String labelText, String value, GridBagConstraints gbc) {
        gbc.gridx = 1;
        Component[] components = frame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && ((JLabel) component).getText().equals(labelText)) {
                int index = frame.getContentPane().getComponentZOrder(component);
                JTextField textField = (JTextField) frame.getContentPane().getComponent(index + 1);
                textField.setText(value);
            }
        }
    }
}
