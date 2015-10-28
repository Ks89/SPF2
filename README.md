[![License LGPL 3](https://img.shields.io/badge/license-LGPLv3-blue.svg)](http://www.gnu.org/licenses/lgpl-3.0.txt)
![](https://reposs.herokuapp.com/?path=Ks89/SPF2)
[![Build Status](https://travis-ci.org/Ks89/SPF2.svg?branch=master)](https://travis-ci.org/Ks89/SPF2)

[![Issue Stats](http://issuestats.com/github/Ks89/SPF2/badge/pr?style=flat)](http://issuestats.com/github/Ks89/SPF2)
[![Issue Stats](http://issuestats.com/github/Ks89/SPF2/badge/issue?style=flat)](http://issuestats.com/github/Ks89/SPF2)

# SPF2

![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/SPF2_header.png)

<br>

## Informations

A social smart space is a physical space where members, both humans and smart objects, can exploit
services provided by the others in a technology-mediated way.
Social Proximity Framework (SPF) is a software solution for the creation of this spaces,
where social identity and proximity is taken into account to offer personalized services and support
real-life interactions with digital ones.
SPF2 is a new major release of [THIS SOFTWARE](https://github.com/deib-polimi/SPF) that fully support Wi-Fi Direct protocol.

SPF2 requires Android 4.2 JellyBean MR1 (API 17) or higher. But I tested this new version only on 4.4.x KilKat, 5.x.x Lollipop and 6.0 Marshmallow. 
This choice is related to to the fact that in previous versions, Wi-Fi Direct was unstable and unreliable.

This repository contains SPFApp, SPFFramework and SPFWFDMiddleware as local projects.
Also, there are the two local modules SPFLib and SPFShared. To be able to create apps based on SPF2, 
you should add these two latest modules as remote dependencies into your build.gradle.

SPFShared and SPFLib are available on JCenter and Maven Central. If you need examples of how to create SPF's applications
look these 3 repositories: 
- [SPFCouponingProvider demo](https://github.com/deib-polimi/SPF2CouponingProviderDemo)
- [SPFCouponingClient demo](https://github.com/deib-polimi/SPF2CouponingClientDemo)
- [SPFChat demo](https://github.com/deib-polimi/SPF2ChatDemo)

If you want to modify SPFLib and SPFShared, I suggest to run Sonatype Nexus OSS on your local machine 
and upload the compiled AAR's into this Maven server. Also, remember to update the version of these libraries, 
because Gradle caches dependencies automatically into .gradle/caches/modules-2/files-2.1 directory.


# Releases

- *10/27/2015* - **SPF2** 2.0.0 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.2.0.0)
- *10/26/2015* - **SPF2** RC 1 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.rc1)
- *10/22/2015* - **SPF2** Beta 4- [Download](https://github.com/Ks89/SPF2/releases/tag/v.beta4)
- *10/20/2015* - **SPF2** Beta 3 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.beta3)
- *10/19/2015* - **SPF2** Beta 2 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.beta2)
- *10/14/2015* - **SPF2** Beta 1 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.beta1)
- *10/13/2015* - **SPF2** Alpha 4 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.alpha4)
- *10/08/2015* - **SPF2** Alpha 3 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.alpha3)
- *10/06/2015* - **SPF2** Alpha 2 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.alpha2)
- *10/06/2015* - **SPF2** Alpha 1 - [Download](https://github.com/Ks89/SPF2/releases/tag/v.alpha1)


# Changelog

Changelog is available [HERE](https://github.com/Ks89/SPF2/CHANGELOG.md)


## Known issues

- [ ] If discovery phase fails, create a temporized system to restart it automatically.
- [ ] When SPF enters in onConnectionInfoAvailable(), start a timer on the client. At the end of the countdown, if this device isn’t connected to the GO it should restart the discovery phase.
- [ ] Refactor MainActivity.java to reduce codelines.
- [ ] Updates the proximity switch in the GUI accordingly to the service status. For example, when Eternal Connect is working, you should update the switch. I suggest to use Broadcast receivers (not Local, but Remote declared into the AndroidManifest.xml). Use GroupInfosFragment as an example.
- [ ] Remove all ListViews and replace they with RecyclerView. Use GroupInfosFragment as an example.
- [ ] Fix Tab positioning for tablets/smartphones. You should find a way to display centered tabs that fill the entire screen width, but with text on a single line. Your solution should work also during screen rotation.
- [ ] Improve the standard mode into the Wi-Fi Direct Middleware.

If you want to do something, create a Pull Request! XD


## Images

![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/button_iconics.png)
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/drawer.png)
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/drawer_proximity.png)
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/notifications.png) <br />
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/tablet1.png) <br />
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/tablet2.png) <br />
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/tablet3.png) <br />
![alt tag](https://raw.githubusercontent.com/deib-polimi/SPF2/master/repo_images/about_fragment.png) 


## Official documentations

The official doc is composed by these files:
- (2015) SPF2 Doc [GitHub repository](https://github.com/deib-polimi/SPF2_Documentation) and [PDF](https://github.com/deib-polimi/SPF2_Documentation/releases/download/v1.0/SPF_documentation.pdf)
- (2014) Master Thesis of Jacopo Aliprandi and Dario Archetti available [HERE](http://hdl.handle.net/10589/106727)

# License
Copyright 2014 Jacopo Aliprandi, Dario Archetti<br>
Copyright 2015 Stefano Cappa

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
