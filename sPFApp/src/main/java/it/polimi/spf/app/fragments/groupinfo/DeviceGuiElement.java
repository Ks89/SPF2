/*
 * Copyright 2015 Stefano Cappa
 *
 * This file is part of SPF.
 *
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.polimi.spf.app.fragments.groupinfo;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 21/10/15.
 */
public class DeviceGuiElement {
    @Getter
    private String name;
    @Getter
    private String address;
    @Getter
    private String identifier;

    public DeviceGuiElement() {
    }

    public DeviceGuiElement(String name, String address, String identifier) {
        this.name = name;
        this.address = address;
        this.identifier = identifier;
    }
}
