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
package scelect.server.core;

import java.sql.*;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.swing.SwingUtilities;

// TODO: Use SQLite wrapper? (http://www.ch-werner.de/javasqlite/)

/**
 * Provides all necessary data storage functionality to SCElectServer. All
 * database access goes through this class, including login authentication.
 *
 * @author lugkhast
 */
public class SCDB {

    static Connection dbConn;

    // TODO: Move prepared statements into their own class

    static PreparedStatement getPassword;

    static PreparedStatement getPosition;
    static PreparedStatement getPosID;

    static PreparedStatement getCandidates;
    static PreparedStatement getNumCandidates;

    static PreparedStatement addPos;
    static PreparedStatement editPos;
    static PreparedStatement removePos;
    static PreparedStatement getAllPos;

    static PreparedStatement registerVoter;
    static PreparedStatement finalizeVoter;
    static PreparedStatement searchForVoters;
    static PreparedStatement editVoter;
    static PreparedStatement removeVoter;
    static PreparedStatement getVoter;
    static PreparedStatement getCandID;
    static PreparedStatement getVoterID;
    static PreparedStatement getCandPosID;
    static PreparedStatement addVote;
    static PreparedStatement flagAsVoted;
    static PreparedStatement getResults;

    /**
     * Arguments for addCandidate are:
     * <ol>
     * <li> CANDIDATE_NAME
     * <li> POS_ID
     * <li> PARTY
     * <li> DESCRIPTION
     * </ol>
     */
    static PreparedStatement addCandidate;
    static PreparedStatement editCandidate;
    static PreparedStatement removeCandidate;
    static String url = "jdbc:derby://localhost:1527/scelect-db";

    /**
     * This method initializes SCElect Server's database connection and
     * prepared statements.
     * 
     * @return Whether initialization was successful
     */
    public static boolean Initialize(String name, String password) {
        // The dbConn == null checks are there to stop certain code from
        // re-init'ing a DB connection during setup, where a connection has
        // already been created by the time this method is called.
        if (dbConn == null) {
            System.out.print("Loading Apache Derby database driver...    ");
            try {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                System.out.println("Loaded!");
            } catch (java.lang.ClassNotFoundException e) {
                System.out.println("Driver failed to load!");
                System.err.println("Exception data:");
                System.err.println(e.getMessage());
                System.err.println("=== Note: SCElectServer must be " +
                        "compiled with derbyclient.jar set as a library. ===");
                System.err.println("(In NetBeans, go to Projects, right click " +
                        "SCElectServer, click\nProperties, then click Libraries)");
                System.exit(-1);
            }
        }

        try {
            if (dbConn == null) {
                System.out.print("Connecting to Derby database...    ");
                dbConn = DriverManager.getConnection(url, name, password);
                System.out.println("Connected.");
            }
            System.out.println("Initializing...");
            if (!VerifyDBLogin()) {
                return false;
            }
            initPreparedStatements();
            return true;
        } catch (SQLException e) {
            System.err.println("Fatal error: Failed to connect to database!");
            System.err.println("Stack trace: ");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method creates the prepared statements used by <code>SCDB</code> to
     * safely and efficiently work with the database.
     *
     * @throws java.sql.SQLException
     */
    public static void initPreparedStatements() throws java.sql.SQLException {
        getPassword = dbConn.prepareStatement(
                "select VOTER_PW from VOTERS " +
                "where VOTER_NAME = ?");

        getPosition = dbConn.prepareStatement(
                "select POS_NAME from OFFICER_POSITIONS " +
                "where POS_ID = ?");

        getPosID = dbConn.prepareStatement(
                "select POS_ID from OFFICER_POSITIONS " +
                "where POS_NAME = ?");

        getCandidates = dbConn.prepareStatement(
                "select CANDIDATE_NAME, PARTY, DESCRIPTION " +
                "from CANDIDATES where POS_ID = ?");

        getNumCandidates = dbConn.prepareStatement(
                "select COUNT(CANDIDATE_NAME) from CANDIDATES where POS_ID = ?");

        addCandidate = dbConn.prepareStatement(
                "insert into CANDIDATES " +
                "(CANDIDATE_NAME, POS_ID, PARTY, DESCRIPTION)" +
                "values (?, ?, ?, ?)");

        editCandidate = dbConn.prepareStatement(
                "update CANDIDATES " +
                "set CANDIDATE_NAME = ?, PARTY = ?, DESCRIPTION = ?" +
                "where CANDIDATE_NAME = ?");

        removeCandidate = dbConn.prepareStatement(
                "delete from CANDIDATES " +
                "where CANDIDATE_NAME = ?");

        addPos = dbConn.prepareStatement(
                "insert into OFFICER_POSITIONS " +
                "(POS_NAME, POS_ORDER)" +
                "values (?, ?)");

        editPos = dbConn.prepareStatement(
                "update OFFICER_POSITIONS " +
                "set POS_NAME = ?" +
                "where POS_NAME = ?");

        removePos = dbConn.prepareStatement(
                "delete from OFFICER_POSITIONS " +
                "where POS_NAME = ?");

        getAllPos = dbConn.prepareStatement(
                "select POS_NAME from OFFICER_POSITIONS " +
                "order by POS_ORDER");

        registerVoter = dbConn.prepareStatement(
                "insert into VOTERS (VOTER_NAME, HAS_VOTED) " +
                "values (?, 0)");

        searchForVoters = dbConn.prepareStatement(
                "select VOTER_NAME from VOTERS " +
                "where VOTER_NAME like ? and VOTER_PW is null");

        finalizeVoter = dbConn.prepareStatement(
                "update VOTERS " +
                "set VOTER_PW = ? " +
                "where VOTER_NAME = ?");

        editVoter = dbConn.prepareStatement(
                "update VOTERS " +
                "set VOTER_NAME = ?, VOTER_PW = ? " +
                "where VOTER_NAME = ?");

        removeVoter = dbConn.prepareStatement(
                "delete from VOTERS " +
                "where VOTER_NAME = ?");

        getVoter = dbConn.prepareStatement(
                "select VOTER_ID from VOTERS " +
                "where VOTER_NAME = ?");

        getCandID = dbConn.prepareStatement(
                "select CANDIDATE_ID from CANDIDATES " +
                "where CANDIDATE_NAME = ?");

        getVoterID = dbConn.prepareStatement(
                "select VOTER_ID from VOTERS " +
                "where VOTER_NAME = ?");

        addVote = dbConn.prepareStatement(
                "insert into VOTES (VOTER_ID, CANDIDATE_ID, POS_ID) " +
                "values (?, ?, ?)");

        flagAsVoted = dbConn.prepareStatement(
                "update VOTERS " +
                "set HAS_VOTED = 1 " +
                "where VOTER_ID = ?");

        /*
        getResults = dbConn.prepareStatement(
                "select a.CANDIDATE_NAME, a.PARTY, b.TOTAL_VOTES " +
                "from CANDIDATES a, " +
                    "(select CANDIDATE_ID, COUNT(CANDIDATE_ID) as TOTAL_VOTES " +
                    "from VOTES " +
                    "where POS_ID = ? " +
                    "group by CANDIDATE_ID) b " +
                "where a.CANDIDATE_ID = b.CANDIDATE_ID " +
                "order by b.TOTAL_VOTES desc");
         */
        getResults = dbConn.prepareStatement(
                "select COUNT(CANDIDATE_ID) from VOTES " +
                "where CANDIDATE_ID = ?");
    }

    /**
     * A crude method to check whether login was successful.
     * @return
     */
    public static boolean VerifyDBLogin() {
        // We cannot see ANYTHING if login details are incorrect. We just get
        // an exception. Armed with that knowledge...
        try {
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select COUNT(VOTER_ID) from VOTERS");
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * This method verifies whether the information given by the client is
     * valid.
     *
     * @param voterName The voter's username
     * @param password The voter's password
     * @return <code>true</code> if the information given is valid.
     */
    public static boolean IsLoginValid(String voterName, char[] password) {
        try {
            getPassword.setString(1, voterName);
            ResultSet rs = getPassword.executeQuery();
            if (rs.next()) { // If there is such a voter in the DB
                String dbPwHash = rs.getString("VOTER_PW");
                String clientPwHash = sha(new String(password));
                if ((dbPwHash != null) && dbPwHash.equals(clientPwHash)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean HasVoted(String voterName) {
        try {
            int voterId = GetVoterID(voterName);
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select COUNT(VOTER_ID) from VOTES " +
                    "where VOTER_ID = " + voterId);
            if (rs.next()) {
                // Check if the number of votes tied to the voter matches up
                // with the number of officer positions
                int numVotes = rs.getInt(1);
                if (numVotes == GetNumOfficerPositions()) {
                    return true;
                } else {
                    // If it doesn't match up, wipe those votes and let the
                    // voter in.
                    stmt.execute(
                            "delete from VOTES " +
                            "where VOTER_ID = " + voterId);
                    return false;
                }
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return true;
        }
    }

    /**
     * This method gets the number of officer positions from the database.
     *
     * @return If successful, the number of officer positions. If not, -1.
     */
    public static int GetNumOfficerPositions() {
        try {
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("select COUNT(POS_ID) from OFFICER_POSITIONS");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int GetPosID(String posName) throws SQLException {
        getPosID.setString(1, posName);
        ResultSet rs = getPosID.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static int GetCandID(String candName) throws SQLException {
        getCandID.setString(1, candName);
        ResultSet rs = getCandID.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static int GetVoterID(String voterName) throws SQLException {
        getVoterID.setString(1, voterName);
        ResultSet rs = getVoterID.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * This method gets the number of candidates in the given officer position.
     *
     * @param posName
     * @return
     * @throws SQLException
     */
    public static int GetNumCandidatesInPosition(String posName) throws SQLException {
        return GetNumCandidatesInPosition(GetPosID(posName));
    }

    /**
     * This method gets the number of officer positions in the position
     * corresponding to the given <code>posID</code>.
     *
     * @param posID The position to count candidates for
     * @return The number of candidates in that position
     * @throws java.sql.SQLException
     */
    public static int GetNumCandidatesInPosition(int posID) throws SQLException {
        getNumCandidates.setInt(1, posID);
        ResultSet rs = getNumCandidates.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * This method sends data about the election to the client.
     *
     * @param out The output stream to the client
     */
    public static void SendCandidatesData(ObjectOutputStream out) {
        try {
            // First, we get the number of officer positions
            int numPositions = GetNumOfficerPositions();
            String[] positions = new String[numPositions];
            String[][] candidates = new String[numPositions][];
            String[][] parties = new String[numPositions][];
            String[][] descs = new String[numPositions][];

            if (numPositions == 0) {
                // No candidates have been added yet.
                // TODO: Do something more useful here.
                return;
            }

            //System.out.println("Number of positions: " + numPositions);
            // out.println(numPositions);

            Statement stmt = dbConn.createStatement();
            ResultSet rsPositions = stmt.executeQuery(
                    "select * from OFFICER_POSITIONS order by POS_ORDER");

            // Next, we send the data.
            int posIndex = 0;
            while (rsPositions.next()) {
                // Loop through positions
                String position = rsPositions.getString("POS_NAME");
                System.out.println("Pos: " + position);
                positions[posIndex] = position;
                int posID = rsPositions.getInt("POS_ID");

                // Send the position
                // out.println(position);


                int numCandidates;
                getNumCandidates.setInt(1, posID);
                ResultSet rsNumCandidates = getNumCandidates.executeQuery();
                rsNumCandidates.next();
                numCandidates = rsNumCandidates.getInt(1);

                // Create candidate data arrays
                candidates[posIndex] = new String[numCandidates];
                parties[posIndex] = new String[numCandidates];
                descs[posIndex] = new String[numCandidates];

                getCandidates.setInt(1, posID);
                ResultSet rsCandidates = getCandidates.executeQuery();

                int candIndex = 0;
                while (rsCandidates.next()) { // Loop through candidates
                    String candName = rsCandidates.getString("CANDIDATE_NAME");
                    System.out.println("Candidate: " + candName);
                    candidates[posIndex][candIndex] = candName;
                    String candParty = rsCandidates.getString("PARTY");
                    System.out.println("Party: " + candParty);
                    parties[posIndex][candIndex] = candParty;
                    String candDesc = rsCandidates.getString("DESCRIPTION");
                    System.out.println("Desc: " + candDesc);
                    descs[posIndex][candIndex] = candDesc;
                    System.out.println("----------");
                    candIndex++;
                }
                posIndex++;
            }
            out.writeObject(positions);
            out.writeObject(candidates);
            out.writeObject(parties);
            out.writeObject(descs);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method returns a Hashtable[] containing details about the candidates
     * in the specified officer position. The Hashtables themselves have the
     * details with the keys "NAME", "PARTY", and "DESC".
     *
     * @return
     */
    public static Hashtable[] GetCandidatesInPosition(String posName)
            throws SQLException {
        int posID = GetPosID(posName);
        int numCandidates = GetNumCandidatesInPosition(posID);
        Hashtable[] candidates = new Hashtable[numCandidates];

        getCandidates.setInt(1, posID);
        ResultSet rsCandidates = getCandidates.executeQuery();

        int candIndex = 0;
        while (rsCandidates.next()) {
            Hashtable ht = new Hashtable();
            ht.put("NAME", rsCandidates.getString("CANDIDATE_NAME"));
            ht.put("PARTY", rsCandidates.getString("PARTY"));
            ht.put("DESC", rsCandidates.getString("DESCRIPTION"));
            candidates[candIndex++] = ht;
        }

        return candidates;
    }

    /**
     * This method adds a candidate to the database.
     *
     * @param positionID The candidate's position
     * @param name The candidate's name
     * @param party The party that the candidate is with
     * @param desc A description of the candidate
     */
    public static void AddCandidate(String name, String position, String party,
            String desc) throws SQLException {
        addCandidate.setString(1, name);
        addCandidate.setInt(2, GetPosID(position));
        addCandidate.setString(3, party);
        addCandidate.setString(4, desc);
        addCandidate.execute();
    }

    /**
     * This method modifies information about a candidate.
     * 
     * @param oldName The candidate whose info we will modify
     * @param newName The new name for that candidate
     * @param newParty The new party for that candidate
     * @param newDesc The new description for that candidate
     * @throws SQLException
     */
    public static void EditCandidate(String oldName, String newName,
            String newParty, String newDesc) throws SQLException {
        editCandidate.setString(1, newName);
        editCandidate.setString(2, newParty);
        editCandidate.setString(3, newDesc);
        editCandidate.setString(4, oldName);
        editCandidate.execute();
    }

    /**
     * This method removes a candidate from the database.
     *
     * @param positionID The position of the candidate
     * @param name The candidate's name
     */
    public static void RemoveCandidate(String name) throws SQLException {
        removeCandidate.setString(1, name);
        removeCandidate.execute();
    }

    /**
     * <code>AddPosition</code> adds the specified position to the database.
     * 
     * @param posName
     * @throws java.sql.SQLException
     */
    public static void AddPosition(String posName) throws SQLException {
        addPos.setString(1, posName);
        addPos.setInt(2, GetNumOfficerPositions());
        addPos.execute();
    }

    /**
     * <code>EditPosition</code> renames an existing position.
     *
     * @param oldName The name of the position to rename
     * @param newName The new name for the position
     * @throws java.sql.SQLException
     */
    public static void EditPosition(String oldName, String newName)
            throws SQLException {
        editPos.setString(1, newName);
        editPos.setString(2, oldName);
        editPos.execute();
    }

    /**
     * Removes the specified officer position from the database.
     *
     * @param posName The name of the position to remove
     * @throws java.sql.SQLException
     */
    public static void RemovePosition(String posName) throws SQLException {
        removePos.setString(1, posName);
        removePos.execute();
    }


    /* VOTER-RELATED METHODS */
    /**
     * This method registers the voter with the specified name.
     * 
     * @param name The name of the voter
     * @throws SQLException
     */
    public static void RegisterVoter(String name) throws SQLException {
        registerVoter.setString(1, name);
        registerVoter.execute();
    }

    public static void FinalizeVoter(String name, char[] password)
            throws SQLException {
        // Encrypt the password and put it in the DB
        finalizeVoter.setString(1, sha(new String(password)));
        finalizeVoter.setString(2, name);
        finalizeVoter.execute();
        ServerCore.updateWindow();
    }

    /**
     * This method searches the database for voter names that match the query.
     * 
     * @param name The name or name fragment to look for
     * @return
     * @throws SQLException
     */
    public static List<String> SearchForVoter(String name) throws SQLException {
        searchForVoters.setString(1, "%" + name + "%");
        ResultSet rs = searchForVoters.executeQuery();

        List<String> results = new ArrayList<String>();
        while (rs.next()) {
            results.add(rs.getString("VOTER_NAME"));
        }
        return results;
    }

    /**
     * This method modifies the details of the specified voter.
     *
     * @param oldName The voter whose details we will modify
     * @param newName The new name for that voter
     * @param newPw The new password for that voter
     * @throws SQLException
     */
    public static void EditVoter(String oldName, String newName, String newPw) throws SQLException {
        editVoter.setString(1, newName);
        editVoter.setString(2, sha(newPw));
        editVoter.setString(3, oldName);
        editVoter.execute();
    }

    /**
     * Removes the specifified voter from the database. This also removes all
     * of that voter's votes, if the specified voter has already voted.
     *
     * @param name The name of the voter to remove
     * @throws SQLException
     */
    public static void RemoveVoter(String name) throws SQLException {
        Statement stmt = dbConn.createStatement();
        stmt.execute("delete from VOTES where VOTER_ID = " + GetVoterID(name));
        removeVoter.setString(1, name);
        removeVoter.execute();
    }

    /**
     * This method loads voter names from a text file. Each line of text is
     * considered to be a separate voter's name.
     *
     * @param file A <code>File</code> object for the file to be used
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void ImportVotersFromFile(File file)
            throws SQLException, FileNotFoundException, IOException {
        // TODO: Make this smarter?
        BufferedReader in = new BufferedReader(new FileReader(file));
        while (in.ready()) {
            String voterToRegister = in.readLine();
            System.out.println(voterToRegister);
            try {
                RegisterVoter(voterToRegister);
            } catch (SQLException ex) {
                System.out.println("Duplicate name found: " + voterToRegister);
            }
        }
    }

    /**
     * This method gets the total number of voters.
     * 
     * @return The number of voters
     * @throws SQLException
     */
    public static int GetNumVoters() throws SQLException {
        Statement stmt = dbConn.createStatement();
        ResultSet rsNumVoters = stmt.executeQuery("select COUNT(VOTER_NAME) from VOTERS");
        rsNumVoters.next();
        return rsNumVoters.getInt(1);
    }

    /**
     * This method gets the number of voters who have already voted.
     * 
     * @return The number of voters who have already voted
     * @throws SQLException
     */
    public static int GetNumVotersVoted() throws SQLException {
        Statement stmt = dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "select COUNT(VOTER_NAME) from VOTERS " +
                "where not(HAS_VOTED = 0)");
        rs.next();
        return rs.getInt(1);
    }

    /**
     * This method gets the names of all voters who have not yet voted.
     *
     * @return A <code>List&lt;String&gt;</code> containing the names of all
     * voters who have not yet voted
     * @throws SQLException
     */
    public static List<String> GetVotersNotYetVoted() throws SQLException {
        Statement stmt = dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "select VOTER_NAME from VOTERS " +
                "where HAS_VOTED = 0");
        List<String> list = new ArrayList<String>();
        while (rs.next()) {
            String name = rs.getString(1);
            list.add(name);
        }
        return list;
    }

    /**
     * This method gets the number of voters who have not yet added a password
     * to their accounts and therefore cannot vote yet.
     *
     * @return The number of voters without a password
     * @throws SQLException
     */
    public static int GetNumVotersUnregistered() throws SQLException {
        Statement stmt = dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "select COUNT(VOTER_NAME) from VOTERS " +
                "where VOTER_PW is null");
        rs.next();
        return rs.getInt(1);
    }

    /**
     * This method gets the names of all registered voters.
     *
     * @return A <code>String[]</code> containing the names of all voters
     * @throws SQLException
     */
    public static String[] GetAllVoters() throws SQLException {
        Statement stmt = dbConn.createStatement();
        int numVoters = GetNumVoters();
        String voters[] = new String[numVoters];

        ResultSet rsVoters = stmt.executeQuery("select VOTER_NAME from VOTERS");
        int voterIndex = 0;
        while (rsVoters.next()) {
            voters[voterIndex++] = rsVoters.getString(1);
        }

        return voters;
    }

    /**
     * This method swaps the orders of the two specified officer positions.
     *
     * @param startOrder The current order of the first officer position
     * @param destOrder The new order for the first officer position.
     * @throws SQLException
     */
    public static void SwapPositions(int startOrder, int destOrder) throws SQLException {
        // Change POS_ORDER of the record in the target slot to placeholder
        // Change POS_ORDER of the record being moved to destOrder
        // Change POS_ORDER of the record that was in the target slot to the
        //   POS_ORDER of the record that was moved.
        int placeholder = Integer.MAX_VALUE;
        Statement stmt = dbConn.createStatement();
        stmt.execute("update OFFICER_POSITIONS set POS_ORDER = " + placeholder +
                " where POS_ORDER = " + destOrder);
        stmt.execute("update OFFICER_POSITIONS set POS_ORDER = " + destOrder + " where POS_ORDER = " + startOrder);
        stmt.execute("update OFFICER_POSITIONS set POS_ORDER = " + startOrder + " where POS_ORDER = " + placeholder);
    }

    public static synchronized void RecordVotes(String[] chosenCandidates,
            String username) throws SQLException {
        int voterID = GetVoterID(username);
        for (String candidate : chosenCandidates) {
            Statement stmt = dbConn.createStatement();
            int candID = GetCandID(candidate);
            ResultSet rs = stmt.executeQuery(
                    "select POS_ID from CANDIDATES " +
                    "where CANDIDATE_ID = " + candID);
            rs.next();
            int posID = rs.getInt(1);

            stmt.execute(
                    "insert into VOTES (VOTER_ID, CANDIDATE_ID, POS_ID) " +
                    "values (" + voterID + ", " + candID + ", " + posID + ")");
        }
        flagAsVoted.setInt(1, voterID);
        flagAsVoted.execute();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ServerCore.updateWindow();
            }
        });
    }

    /**
     * This method returns all registered officer positions as a
     * <code>String</code> array.
     *
     * @return A <code>String[]</code> containing the names of all officer
     * positions.
     * @throws SQLException
     */
    public static String[] GetPositions() throws SQLException {
        String[] positions = new String[GetNumOfficerPositions()];
        Statement stmt = dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "select POS_NAME from OFFICER_POSITIONS order by POS_ORDER");
        int index = 0;
        while (rs.next()) {
            positions[index] = rs.getString("POS_NAME");
            index++;
        }
        return positions;
    }

    /**
     * This method gets election results. This method does not care if some or
     * all of the voters have not voted yet.
     *
     * @return A ResultsStruct containing the results for this election.
     * @throws SQLException
     */
    public static ResultsStruct GetResults() throws SQLException {
        // Get list of positions.
        // For each position, get list of candidates in that position.
        // Get number of votes for each candidate.
        String[] positions = GetPositions();
        String[][] candidates = new String[positions.length][];
        int[][] voteData = new int[positions.length][];
        for (int posIndex = 0; posIndex < positions.length; posIndex++) {
            Hashtable[] candData =
                    GetCandidatesInPosition(positions[posIndex]);
            candidates[posIndex] = new String[candData.length];
            voteData[posIndex] = new int[candData.length];
            for (int candIndex = 0; candIndex < candData.length; candIndex++) {
                Hashtable cand = candData[candIndex];
                String candName = (String) cand.get("NAME");
                candidates[posIndex][candIndex] = candName;

                getResults.setInt(1, GetCandID(candName));
                ResultSet rs = getResults.executeQuery();
                rs.next();
                voteData[posIndex][candIndex] = rs.getInt(1);
            }
        }
        return new ResultsStruct(positions, candidates, voteData);
    }

    /**
     * <code>CreateDB</code> is called in <code>SCEServerSetupWindow</code>
     * during the setup process.
     */
    public static int CreateDB(String username, String password) {
        try {
            dbConn = DriverManager.getConnection(
                    url + ";create=true",
                    username, password);
        } catch (SQLException ex) {
            // ex.printStackTrace();
            return DBCONN_FAILED;
        }
        try {
            Statement stmt = dbConn.createStatement();
            stmt.execute(
                    "create table OFFICER_POSITIONS (" +
                    "POS_ID BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "POS_NAME VARCHAR(128) UNIQUE NOT NULL," +
                    "POS_ORDER INTEGER UNIQUE NOT NULL," +
                    "PRIMARY KEY (POS_ID)" +
                    ")");
            stmt.execute(
                    "create table CANDIDATES (" +
                    "CANDIDATE_ID BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "CANDIDATE_NAME VARCHAR(128) UNIQUE NOT NULL," +
                    "POS_ID BIGINT REFERENCES OFFICER_POSITIONS(POS_ID)," +
                    "PARTY VARCHAR(128) NOT NULL," +
                    "DESCRIPTION VARCHAR(2048) NOT NULL," +
                    "PRIMARY KEY (CANDIDATE_ID)" +
                    ")");
            stmt.execute(
                    "create table VOTERS (" +
                    "VOTER_ID BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "VOTER_NAME VARCHAR(128) UNIQUE NOT NULL," +
                    "VOTER_PW VARCHAR(128)," +
                    "HAS_VOTED INTEGER," +
                    "PRIMARY KEY (VOTER_ID)" +
                    ")");
            stmt.execute(
                    "create table VOTES (" +
                    "VOTER_ID BIGINT REFERENCES VOTERS(VOTER_ID)," +
                    "CANDIDATE_ID BIGINT REFERENCES CANDIDATES(CANDIDATE_ID)," +
                    "POS_ID BIGINT REFERENCES OFFICER_POSITIONS(POS_ID)" +
                    ")");
            Initialize(null, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return SQL_FAIL;
        }
        return DBCONN_SUCCESS;
    }

    /**
     * This method computes the SHA-1 hash of the given string.
     * 
     * @param source The string whose hash we will compute
     * @return The hash of <code>source</code>
     */
    static String sha(String source) {
        if (source == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] bytes = md.digest(source.getBytes());
            String s = new String(bytes);
            return s;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(sha(""));
    }
    /**
     * This is returned by <code>CreateDB</code> when database creation is
     * successful.
     */
    public static final int DBCONN_SUCCESS = 0;
    /**
     * This is returned by <code>CreateDB</code> when it fails to connect to
     * the database. This can be caused by not having the database running at
     * the time.
     */
    public static final int DBCONN_FAILED = 1;
    /**
     * This is returned by <code>CreateDB</code> when the table creation SQL
     * code is not syntactically correct.
     */
    public static final int SQL_FAIL = 2;
}
