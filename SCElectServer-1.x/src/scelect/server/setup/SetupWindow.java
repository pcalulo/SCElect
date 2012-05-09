/*
 * SCElect Server: Provides services to SCElect clients on the network
 * Copyright (C) 2008-2009 Lawrence Patrick C. Calulo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scelect.server.setup;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.io.*;
import java.net.URI;

/* If these were replaced with "import java.util.*;", it will fail to compile
 * due to ambiguity between java.util.List and java.awt.List.
 */
import java.util.List;
import java.util.ArrayList;

/**
 * <code>SetupWindow</code> is a wizard that is shown when SCElect
 * Server is first started. It provides a UI that allows the user to easily
 * perform the configuration tasks required to get SCElect Server up and
 * running (e.g. create officer positions and register candidate names) and
 * gives tips to the user along the way.
 *
 * @author lugkhast
 */
public class SetupWindow extends JFrame {

    private static SetupWindow instance;
    private List<SetupPage> pages = new ArrayList<SetupPage>();

    public SetupWindow() {
        initComponents();
        instance = this;
    }

    public static SetupWindow getInstance() {
        return instance;
    }

    public static void allowNext(boolean allow) {
        instance.nextBtn.setEnabled(allow);
    }

    public static void allowBack(boolean allow) {
        instance.backBtn.setEnabled(allow);
    }

    /**
     * All user interface initialization happens in initComponents(). The code
     * for the pages, however, are inside <code>createPages()</code>, which is
     * called in this method.
     *
     * @see createPages()
     */
    void initComponents() {
        setTitle(wndTitle);
        setMinimumSize(new Dimension(675, 520));
        setLocationByPlatform(true);
        // Change this once we're done with debugging this
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cancelSetup();
            }
        });

        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        headerLabel = new JLabel(
                "<HTML>Student Council Elections " +
                "<FONT COLOR=\"#656565\">Server</FONT></HTML>");
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.PLAIN, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(headerLabel);

        // We don't need to interact much with the separators. Reusing this
        // variable will do no harm.
        JSeparator sep;

        sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        add(sep);

        mainPanel = new JPanel();
        layoutMgr = new CardLayout();
        mainPanel.setLayout(layoutMgr);
        add(mainPanel);

        // Set up the wizard's pages
        createPages();

        sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        add(sep);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        helpBtn = new JButton("Help");
        helpBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                giveHelp();
            }
        });
        bottomPanel.add(helpBtn);
        bottomPanel.add(Box.createHorizontalGlue());

        finishBtn = new JButton("Finish");
        finishBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                finishSetup();
            }
        });
        finishBtn.setVisible(false);
        bottomPanel.add(finishBtn);

        bottomPanel.add(Box.createHorizontalStrut(5));

        backBtn = new JButton("< Back");
        backBtn.setEnabled(false);
        backBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                prevPage();
            }
        });
        bottomPanel.add(backBtn);

        bottomPanel.add(Box.createHorizontalStrut(5));

        nextBtn = new JButton("Next >");
        nextBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        });
        bottomPanel.add(nextBtn);

        bottomPanel.add(Box.createHorizontalStrut(5));

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelSetup();
            }
        });
        bottomPanel.add(cancelBtn);
        add(bottomPanel);

        nextBtn.requestFocusInWindow();
        layoutMgr.first(mainPanel);
        currentPage = 1;
        setVisible(true);
    }

    /**
     * This function creates, sets up, and attaches the setup pages to the main
     * window.
     *
     * @see initComponents()
     * @see SetupWindow
     */
    void createPages() {
        // Page 1 (Welcome to SCElect Server!)
        homePage = new SetupPage() {

            @Override
            public boolean canProceed() {
                return true;
            }

            @Override
            public boolean canReturn() {
                return true;
            }
        };

        homePage.setLayout(new BoxLayout(homePage, BoxLayout.PAGE_AXIS));
        homePage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        homePage.add(new JLabel(UI_HomePageWelcomeText1));
        homePage.add(Box.createVerticalStrut(10));

        homePageButtonPanel = new JPanel();
        homePageButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
        homePageButtonPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));
        homePageLinkButton = new JButton();
        homePageLinkButton.setText("<HTML><B>Get Apache Derby</B></HTML>");
        homePageLinkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openDerbyWebsite();
            }
        });
        homePageButtonPanel.add(homePageLinkButton);

        // A little progress bar that shows up when the link button is clicked
        openBrowserProgBar = new JProgressBar();
        openBrowserProgBar.setIndeterminate(true);
        openBrowserProgBar.setStringPainted(true);
        openBrowserProgBar.setString("Opening your web browser...");
        openBrowserProgBar.setMinimumSize(new Dimension(375, 15));
        openBrowserProgBar.setPreferredSize(new Dimension(401, 20));
        openBrowserProgBar.setVisible(false);
        homePageButtonPanel.add(openBrowserProgBar);

        homePage.add(homePageButtonPanel);
        homePage.add(new JLabel(UI_HomePageWelcomeText2));
        addPage(homePage);

        // Page 2 (creating the database)
        dbCreationPage = new DBCreationPage();
        addPage(dbCreationPage);

        // Page 3 (Officer positions)
        posPage = new PositionsPage();
        addPage(posPage);

        // Page 4 (Voter registration)
        voterPage = new VoterRegPage();
        addPage(voterPage);

        // Page 5 (Congratulations!)
        finalPage = new FinalPage();
        addPage(finalPage);
    }

    /**
     * Entry point. Used for testing only.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }

                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        new SetupWindow().setVisible(true);
                    }
                });
            }
        });
    }

    void addPage(SetupPage page) {
        pages.add(page);
        mainPanel.add(page, "page" + NUM_PAGES); // We start at index 0
        NUM_PAGES++;
    }

    /**
     * This method displays the next page of the wizard. If that page is the
     * last page, the "Next" and "Cancel" buttons are disabled, and the "Finish"
     * button is shown.
     */
    void nextPage() {
        if (currentPage < NUM_PAGES) { // If we're not at the last page...
            layoutMgr.next(mainPanel);
            currentPage++;
            // If we were at the second-to-last page and are now in the last page...
            if (currentPage == NUM_PAGES) {
                nextBtn.setEnabled(false);
                finishBtn.setVisible(true);
                cancelBtn.setEnabled(false);
                finishBtn.requestFocus();
            }

            backBtn.setEnabled(true);

            // Check the now-current page's custom can-go-forward rules.
            SetupPage page = pages.get(currentPage - 1);
            if (!page.canProceed()) {
                nextBtn.setEnabled(false);
            }
        }
    }

    /**
     * This method displays the previous page of the wizard. If that page is the
     * first page, the "Back" button is disabled. This also hides the "Finish"
     * button and enables the Next button regardless of their current state.
     */
    void prevPage() {
        if (currentPage > 1) {
            layoutMgr.previous(mainPanel);
            currentPage--;
            // Disable the back button if we're now at page 1
            backBtn.setEnabled(currentPage > 1);
            nextBtn.setEnabled(true);
            finishBtn.setVisible(false);
            cancelBtn.setEnabled(true);
        }
    }

    /**
     * This method displays a dialog that asks the user to confirm if (s)he 
     * really wants to cancel setup.
     */
    void cancelSetup() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit setup?",
                wndTitle,
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    /**
     * This method executes post-setup tasks.
     */
    void finishSetup() {
        String fSep = File.separator;
        String homeStr;
        if (System.getenv("HomePath") != null) {
            // We're running on Windows!
            // C:\Documents and Settings\*username*\.scelect\   (Windows XP)
            homeStr = System.getenv("HomeDrive") + System.getenv("HomePath") + fSep + ".scelect" + fSep;
        } else if (System.getenv("HOME") != null) {
            // /home/*username*/.scelect/
            homeStr = System.getenv("HOME") + fSep + ".scelect" + fSep;
        } else {
            // What the hell are we running on?
            // /.scelect/
            homeStr = fSep + ".scelect" + fSep; // Let's save our stuff at the root of the drive...
        }
        File dir = new File(homeStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(homeStr + "configured");
        ObjectOutputStream fout;
        try {
            fout = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));
            fout.writeObject("Hello, World!");
            fout.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Setup failed to create the config state file. SCElect " +
                    "Server may not work on your platform until this issue " +
                    "is resolved.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
        
        dispose();
    }

    /**
     * This method is called when the user clicks on the Help button.
     */
    void giveHelp() {
        JOptionPane.showMessageDialog(this,
                "<HTML>" +
                "For additional information, please see the file named " +
                "<B>readme.html</B>, which is part of the SCElect Server " +
                "download.",
                wndTitle,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method opens the Downloads section of the Apache Derby website
     * in the user's default browser. If it fails to do so, a dialog is shown
     * telling the user what happened.
     *
     * @see onBrowserOpenFail()
     */
    void openDerbyWebsite() {

        // homePageLinkButton.setText("Opening your web browser...");
        homePageLinkButton.setVisible(false);
        openBrowserProgBar.setVisible(true);

        // Opening a web browser can take a noticeable amount of time. If this
        // was not done in a SwingWorker, the UI would temporarily lock up.
        SwingWorker worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Desktop.getDesktop().browse(new URI(
                            "http://db.apache.org/derby/derby_downloads.html"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void done() {
                try {
                    boolean successful = get();
                    if (successful) {
                        homePageButtonPanel.setMaximumSize(
                                new Dimension(Integer.MAX_VALUE, 60));
                        homePageLinkButton.setText(
                                "<HTML>" +
                                "<CENTER>" +
                                "<B>Get Apache Derby</B> " +
                                "<BR>" +
                                "(if your browser has not opened yet, it is " +
                                "still starting up)" +
                                "</CENTER>" +
                                "</HTML>");
                        homePageLinkButton.setVisible(true);
                        openBrowserProgBar.setVisible(false);
                    }
                } catch (InterruptedException ignore) {
                } catch (ExecutionException ex) {
                    String reason;
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        reason = cause.getLocalizedMessage();
                    } else {
                        reason = ex.getMessage();
                    }
                    onBrowserOpenFail(reason);
                }
                homePageLinkButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * When <code>openDerbyWebsite()</code> fails, this method is called.
     *
     * @param reason The cause or message given by the exception
     * @see openDerbyWebsite()
     */
    void onBrowserOpenFail(String reason) {
        homePageLinkButton.setText("<HTML><B>Get Apache Derby</B></HTML>");
        homePageLinkButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, reason, "Unable to open website",
                JOptionPane.ERROR_MESSAGE);
    }
    //
    // ===== Fields =====
    //
    public final static String wndTitle = "SCElect Server Setup";
    // Components
    JLabel headerLabel;
    JPanel mainPanel;
    JPanel bottomPanel;
    JButton helpBtn;
    JButton finishBtn;
    JButton backBtn;
    JButton nextBtn;
    JButton cancelBtn;
    JProgressBar openBrowserProgBar;
    // The wizard's pages
    SetupPage homePage; // The first card
    SetupPage dbCreationPage;
    SetupPage posPage;
    SetupPage voterPage;
    SetupPage finalPage;
    // The current card ID
    int currentPage;
    // The number of pages
    int NUM_PAGES = 0;
    // The layout manager
    CardLayout layoutMgr;
    // UI Text
    String UI_HomePageWelcomeText1 =
            "<HTML>" +
            "<H2>Welcome to SCElect Server!</H2>" +
            "This setup process will prepare the database that SCElect " +
            "Server will use to store all data relevant to your elections. " +
            "However, before we begin the setup proper, the Apache Derby " +
            "relational database management system needs to be installed on " +
            "this computer, as it is used by SCElect Server for efficient " +
            "data storage." +
            "<BR><BR>" +
            "If you haven't already done so, click the button below to go " +
            "to the Derby downloads page, download the latest version, and " +
            "configure the required environment variables." +
            "</HTML>";
    String UI_HomePageWelcomeText2 =
            "<HTML>" +
            "You will need to set the value of the <B>DERBY_HOME</B> " +
            "environment variable to the path to the directory in which " +
            "Apache Derby is located on this machine. For instructions on " +
            "how to do so, please see the file named <B>readme.html</B> " +
            "which is part of the SCElect Server download. " +
            "When you are done setting up Apache Derby, click <B>Next</B> to " +
            "continue with the setup." +
            "</HTML>";
    // The link button and the panel it's in (Required Deps page)
    JButton homePageLinkButton;
    JPanel homePageButtonPanel;
}
