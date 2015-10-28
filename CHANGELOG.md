**2.0.0**

1. project moved from Eclipse to Android Studio.
2. SPFShared and SPFLib are remote dependencies into a maven repository
for external apps, but at the same time they are local projects
for SPF.
3. created a Gradle task to upload dependencies into a Sonatype Nexus
Server.
4. updated to Lollipop/Marshmallow Material Design using support libraries.
5. code cleanup using Butterknife, Otto and Project Lombok. Also, improved
architecture removing cyclic dependencies.
6. completely removed AllJoyn/AllSeen’s middleware. SPF2 has a pure
Wi-Fi Direct Middleware implementation.
7. notification into Notification Drawer updated with a custom layout
(using RemoteViews) adding an “X” button to stop the Proximity
Service.
8. fixed BUG ID=0: crashes related to profile image. Now I’m using
android-crop by soundcloud to pick and cut images. Also, I added
another library to create a circular image view.
21
9. added Travis CI to the GitHub repository.
10. fixed BUG ID=2: problem when I’m trying to save profile’s fields. I
replaced the listener with a TextWatcher.
11. fixed BUG ID=9: when SPFCouponingClient is closed, SPFFramework
receives and saves coupons ignoring my filter. To fix this problem, I
changed the filter to use the db, instead of the data saved in memory.
12. huge code refactoring and design improvements into Wi-Fi Direct Middleware.
13. fixed BUG ID=11: NullPointerException in GroupActor.
14. implemented a quick workaround to specify go intent and autonomous
mode from the GUI, using switches.
15. fixed BUG ID=12: “service is null” in middleware.
16. performance improvements for group creation’s procedure into the
middleware.
17. fixed Bounjour protocol logic into the middleware.
18. added Toast notification to explain the connection status.
19. fixed BUG ID=14: NullPointerException (but I don’t remember where).
20. using the unique identifier I created a quick logic to tell to clients if a
device is a GO or not. In fact, now GOs have ids that start with the
“AP” prefix.
21. fixed BUG ID=15: implemented a logic to connect only to “AP” devices.
22. fixed BUG ID=16: related to ID=15 but isolated into ServiceList class.
23. implemented Wi-Fi Direct’s Autonomous Mode for GOs.
24. implemented a new logic to manage in a better way the selection of
Socket’s ports.
25. log system updated and added code to catch all exceptions in the
middleware.
26. increased connection’s timeout on clients to reduce problems during
the connection procedure.
22
27. fixed BUG ID=17: in a specific part of the middleware, socket.close()
were called by the UI Thread causing a caught and ignored exception.
Fixed using Otto’s events to create asynchronous calls.
28. implemented a complete Event’s hierarchy used for Otto bus.
29. completely redesigned GroupActor and its subclasses. The superclass
is a Thread and all subclasses implement run() and other abstract
methods.
30. removed ServerSocketAcceptor and moved into GroupOwnerActor.
31. removed cyclic dependency between WifiDirectMiddleware and WFDBroadcastReceiver
using Otto’s events.
32. implemented Wi-Fi Direct Group’s management into Wi-Fi Direct
middleware (not really stable, but the problem is related to the Android’s
implementation of this protocol).
33. fixed BUG ID 18: Android 6.0 Marshmallow changed the permission
policy. For this reason, I implemented a workaround to guide the user
to enable a specific feature, required by external apps to interact with
the framework.
34. SPFApp updated with the new Google Design library, for example to
create Tabs.
35. fixed BUG ID=20: added a default photo profile.
36. fixed BUG ID=21: fixed small bug related to tabs.
37. implemented Eternal Connect in a singleton class. It’s an evolution of
the Eternal Discovery that I previously created for Pigeon Messenger
(https://github.com/deib-polimi/PigeonMessanger). Eternal Connect
is able to discovery and connect automatically to nearby devices.
38. added two new fragments: GO Infos and About. The first shows all
informations about services and connection status, the second one is
an about page.
39. completely rewritten the navigation drawer using MaterialDrawer and
Iconics to create a modern GUI. Also, I reused the same code to update
the Multi-pane layout for tablets.
40. added two Broadcast receivers to update the GUI with available services
and devices.
23
41. created a new app icon in Material Design.