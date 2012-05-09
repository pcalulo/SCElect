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

/**
 *
 * @author Calulo
 */
public class ResultsStruct {
    private final String[] positions;
    private final String[][] candidates;
    private final int[][] votes;

    public ResultsStruct(String[] positions, String[][] candidates, int[][] votes) {
        this.positions = positions;
        this.candidates = candidates;
        this.votes = votes;
    }

    public String[] getPositions() {
        return positions;
    }

    public String[][] getCandidates() {
        return candidates;
    }

    public int[][] getVoteData() {
        return votes;
    }
}
