[![Build Status](https://travis-ci.org/Ks89/SPF2.svg?branch=master)](https://travis-ci.org/Ks89/SPF2)


# SPF2

Based on https://github.com/deib-polimi/SPF

Work in progress...please be patient.

Attention to build you must start Sonatype Nexus with all libs published. I'll post istructions and other stuff in the future.



# Changelog (in ita. In the future i'll translate everything)
1. Android studio, progetti spezzati, uno con librerie tutte assieme e la sua app spfapp. Le altre sono in progetti completamente separati.
2. Android studio + dipendenze online in un server maven in locale (per ora), cioè sonatype nexus. Questo mi permette di scrivere già i file build.gradle con le dipendenze come se fossero online, anche se in realtà sono sul pc.
3. Per passare alla versione da rilasciare, basta uplodare i file di dipendenze .aar su JFrog Bintray con un account gratuito, insieme ai sorgenti e una sorta di documentazione (rispettando le loro regole degli account gratuiti che devono avere solo cose open source), in modo da essere disponibile direttamente in Maven Central ed importabile in Gradle con una riga sola di codice, rendendo la creazione di app basate su SPF molto più facile e rapido.
4. Risolto bug odioso e grave che creava crash random (anche all’avvio dell’app, richiedendo la disinstallazione dell’app stessa per svuotare la propria cache), soprattutto su Lollipop. Era legato alla gestione immagine profilo. Ora ho riscritto tutto usando la combinazione di due librerie, una di soundcloud e una creata da uno su github.
5. Fatto pulsante per attivare prossimità nella notifica in un layout personalizzato nel Notification Drawer
6. App Coupon Client e anche la Provider ora sono tutte in appcompatv7 in material design, con ancora qualche problema sulla selezione multipla per quanto riguardano i dettagli grafici.
7. Bug3 RISOLTO: Messo fix upload foto anche nelle altre app, come quella dei coupon perché su lollipop non si potevano utilizzare
8. Bug4 RISOLTO: I codici coupon vengono ricevuti in continuazione e replicati all’infinito nelle liste


# External libraries
com.google.code.gson:gson:2.3
de.hdodenhof:circleimageview:1.3.0
com.soundcloud.android:android-crop:1.0.0
com.astuetz:pagerslidingtabstrip:1.0.1
org.projectlombok:lombok:1.16.4


# License
