/*  MorbidMeter - Lifetime in perspective 
    Copyright (C) 2011 EP Studios, Inc.
    www.epstudiossoftware.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.epstudios.morbidmeter;

// essentially a C++ style 'struct'
public class Configuration {
	public static final int NO_SOUND = 0;
	public static final int DEFAULT_SOUND = 1;
	public static final int MM_SOUND = 2;

	public User user;
	public String timeScaleName;
	public boolean reverseTime;
	public boolean useMsec;
	public boolean showNotifications;
	public int notificationSound;

}
