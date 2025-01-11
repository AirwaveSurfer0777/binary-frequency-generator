package frequency_generator;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame 
{
	private static final long serialVersionUID = -7504327273338145257L;
	private JTextField binaryInputField;
    private JLabel frequencyLabel;
    private JLabel binaryLabel;
    private SourceDataLine line; // Line to play sound
    private boolean isPlaying = false; // Flag to control playback

    public Main() 
    {
        initUI();
        setTitle("Frequency Generator");
        setSize(350, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Set an icon for the JFrame
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/icon.png")));

        // Input field for binary number
        binaryInputField = new JTextField(10);
        add(new JLabel("Enter Binary:"));
        add(binaryInputField);

        // Button to play sound
        JButton playButton = new JButton("Play Sound");
        playButton.setBackground(Color.BLACK); // Set button background color to black
        playButton.setForeground(Color.WHITE); // Set button text color to white
        add(playButton);

        // Button to stop sound
        JButton stopButton = new JButton("Stop Sound");
        stopButton.setBackground(Color.RED); // Set button background color to red
        stopButton.setForeground(Color.WHITE); // Set button text color to white
        add(stopButton);

        // Labels to display binary and frequency
        binaryLabel = new JLabel("Binary: ");
        frequencyLabel = new JLabel("Frequency: ");
        add(binaryLabel);
        add(frequencyLabel);

        // Action listener for the play button
        playButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                String binaryInput = binaryInputField.getText();
                int frequency = binaryToFrequency(binaryInput);
                
                // Update the labels immediately
                binaryLabel.setText("Binary: " + binaryInput);
                frequencyLabel.setText("Frequency: " + frequency + " Hz");
                
                playSound(frequency);
            }
        });

        // Action listener for the stop button
        stopButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                stopSound();
            }
        });
    }

    // Convert binary string to frequency (in Hz)
    private int binaryToFrequency(String binaryInput) 
    {
        return Integer.parseInt(binaryInput, 2);
    }

    // Play a sound at the specified frequency in a loop
    private void playSound(int frequency)
    {
        if (isPlaying) return; // Prevent multiple threads from starting

        isPlaying = true; // Set the flag to indicate sound is playing
        new Thread(() -> {
            // Audio format parameters
            float sampleRate = 44100; // Sample rate in Hz
            int duration = 2; // Duration in seconds

            // Create an audio format
            AudioFormat audioFormat = new AudioFormat(sampleRate, 8, 1, true, true);
            try {
                // Create a line to play the sound
                line = AudioSystem.getSourceDataLine(audioFormat);
                line.open(audioFormat);
                line.start();

                // Create a buffer to hold the audio data
                byte[] buffer = new byte[(int) (sampleRate * duration)];
                while (isPlaying) 
                {
                    for (int i = 0; i < buffer.length; i++)
                    {
                        // Generate a sine wave
                        buffer[i] = (byte) (Math.sin(2 * Math.PI * i / (sampleRate / frequency)) * 127);
                    }
                    // Write the audio data to the line
                    line.write(buffer, 0, buffer.length);
                }
                line.drain();
                line.stop();
                line.close();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Stop the sound
    private void stopSound() 
    {
        isPlaying = false; // Set the flag to stop playback
        if (line != null)
        {
            line.stop();
            line.close();
        }
    }

    private void initUI() 
    {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException e) {
            try {
                System.out.println("Substance theme not detected, reverting to OS Default.");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}