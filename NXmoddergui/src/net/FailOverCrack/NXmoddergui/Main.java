package net.FailOverCrack.NXmoddergui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private JFrame frame;
    private JTextField directoryField;

    public Main() {
        frame = new JFrame("NXmodder Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter the desired path for downloading and unpacking:\n"
        		+ "\n"
        		+ "");
        directoryField = new JTextField(20);
        JButton downloadButton = new JButton("Download CFW");

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String localDirectory = directoryField.getText();
                String fileUrl = "https://github.com/Atmosphere-NX/Atmosphere/releases/download/1.7.0-prerelease/atmosphere-1.7.0-prerelease-35d93a7c4+hbl-2.4.4+hbmenu-3.6.0.zip";
                String fileName = "atmosphere.zip";
                Path localFilePath = Paths.get(localDirectory, fileName);

                try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(localFilePath.toString())) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    JOptionPane.showMessageDialog(frame, "succesfully downloaded: " + localFilePath);

                    // Entpacken der ZIP-Datei
                    try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(localFilePath.toString())))) {
                        ZipEntry entry;
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            String extractedFileName = Paths.get(localDirectory, entry.getName()).toString();
                            if (entry.isDirectory()) {
                                // checks if the directory exists
                                File dir = new File(extractedFileName);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                            } else {
                                // unzips the files
                                try (FileOutputStream fileOutputStream1 = new FileOutputStream(extractedFileName)) {
                                    byte[] buffer = new byte[1024];
                                    int bytesRead1;
                                    while ((bytesRead1 = zipInputStream.read(buffer)) != -1) {
                                        fileOutputStream1.write(buffer, 0, bytesRead1);
                                    }
                                    System.out.println("succesfully unziped: " + extractedFileName);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        // deletes the zip file
                        if (localFilePath.toFile().delete()) {
                            System.out.println("ZIP file succesfully deleted: " + localFilePath);
                        } else {
                            System.out.println("cant delete ZIP file: " + localFilePath);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        inputPanel.add(label);
        inputPanel.add(directoryField);
        inputPanel.add(downloadButton);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}