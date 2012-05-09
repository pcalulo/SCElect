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

import javax.swing.*;

/**
 * <code>SetupPage</code> provides two methods - <code>canProceed</code> and
 * <code>canGoBack</code> that allows the wizard's pages to control whether or
 * not to allow the user to change the page being displayed.
 *
 * @see SetupWindow
 * @author lugkhast
 */
public abstract class SetupPage extends JPanel {

    public SetupPage() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * This method is used for implementing custom rules on whether going
     * forward or backward through the wizard should be allowed.
     * @return <code>true</code> if the user should be able to proceed, false
     * otherwise.
     */
    public abstract boolean canProceed();

    /**
     * This method is used for implementing custom rules on whether going
     * forward or backward through the wizard should be allowed.
     * @return <code>true</code> if the user should be able to go back one 
     * page, false otherwise.
     */
    public abstract boolean canReturn();
}
